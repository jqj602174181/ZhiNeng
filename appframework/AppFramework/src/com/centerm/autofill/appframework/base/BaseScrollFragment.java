package com.centerm.autofill.appframework.base;

import java.util.ArrayList;
import java.util.HashMap;

import com.centerm.autofill.appframework.R;
import com.centerm.autofill.appframework.base.BaseTabFragment.InputDataItem;
import com.centerm.autofill.appframework.utils.NumToChinese;
import com.centerm.autofill.appframework.utils.StringUtil;
import com.centerm.autofill.dev.DevActivity;
import com.centerm.autofill.dev.IDCardMsg;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BaseScrollFragment extends Fragment {

	protected BaseScrollActivity activity; // 所属的activity
	HashMap<String, String> map = new HashMap<String, String>();// 存储的数据
	HashMap<String, String> fixMapData;// 固定存储的数据

	public final static int  BIGAMOUNT = 50000;//大额度限制

	protected final int FROM_SELF = 0;	//本人
	protected final int FROM_AGENT = 1;	//代理人
	protected final int FROM_OTHER = 2;	//他人

	private long lastTimeBtnPressed = 0;// 最一次按钮按下的时间，当时间间隔过短时，禁止操作
	final int MIN_BUTTON_PRESS_INTERVAL = 500;// 按钮按下最小间隔为500ms
	private String tipReadIDErr = "居民身份证读取失败！"; // 读取身份证错误后的提示信息
	protected IDCardMsg idCardMsg;// 保存读取好的身份证信息
	private boolean canSkipOnReadIDCard = false; // 读身份证时是否可跳过
	private volatile boolean isReadingIDCard = false;// 是否正在读二代证
	protected int Transactor = FROM_SELF;	//当前刷二代证的人的类型

	protected static final double limitMoney = 999999999.99;

	protected String magCardInfo; // 保存读取好的卡的信息

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (BaseScrollActivity) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState, int layoutId) {
		View view = inflater.inflate(layoutId, container, false);

		// 注册按钮监听器
		onFinishListener = (OnFinishListener) activity;

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null)
			return;
		switch (requestCode) {
		case DevActivity.REQ_READIDCARD:// 读二代证
			isReadingIDCard = false;
			operatorIdCardInfo(requestCode, resultCode, data);
			break;
		case DevActivity.REQ_READMAGCARD:// 读磁卡
		case DevActivity.REQ_SELECTREADCARD://选择读哪种卡
			String info = data.getStringExtra("no");
			operatorMagCardInfo(info);
			break;
		default:// 通信时返回数据

			break;
		}
	}

	/*
	 * 对读取的二代证信息进行处理
	 */
	protected void operatorIdCardInfo(int requestCode, int resultCode,
			Intent data) {
		idCardMsg = DevActivity.GetIdCardMsg(requestCode, resultCode, data);
		if (idCardMsg != null) {
			if (Transactor == FROM_AGENT) {
				if (activity.getUniqueMark().equals(idCardMsg.getId_num())) {
					Toast.makeText(activity, "代理人与交易人不能是同一个人！请更换身份证后，重新操作。",Toast.LENGTH_LONG).show();
					idCardMsg = null;
				} else{
					showIdCardInfo(idCardMsg);
					onOKReadIDCard();// 读取成功
				}
			} else if( Transactor == FROM_SELF ) {
				activity.setUniqueMark(idCardMsg.getId_num());
				showIdCardInfo(idCardMsg);
				onOKReadIDCard();// 读取成功
			} else if( Transactor == FROM_OTHER){
				showIdCardInfo(idCardMsg);
				onOKReadIDCard();// 读取成功
			}
		} else {// 读卡失败
			if (resultCode == DevActivity.RES_CANCEL) {// 用户取消
				onCancelReadIDCard();
			} else if (resultCode == DevActivity.RES_SKIP) {
				onSkipReadIDCard();
			} else {
				activity.showTip(tipReadIDErr);
				gotoLastStep();// 读失败，返回上一步
			}
		}
	}

	// 允许读二代证时跳过操作
	protected void enableReadIDCardSkip() {
		canSkipOnReadIDCard = true;
	}

	// 当成功读取二代证时
	protected void onOKReadIDCard() {
	}

	// 回到上一步
	public void gotoLastStep() {
		onFinishListener.onFinishListener(this);
	}

	// 当用户跳过读二代证时
	protected void onSkipReadIDCard() {
		activity.showTip("该交易需要核验身份证，交易无法继续，请重试。");
		//		gotoLastStep();// 读失败，返回上一步
	}

	// 当用户取消读二代证时
	protected void onCancelReadIDCard() {
		activity.showTip("该交易需要核验身份证，交易无法继续，请重试。");
		gotoLastStep();// 读失败，返回上一步
	}

	@Override
	public void onStop() {
		cancelReadIDCard();// 防止二代证页面还在阅读
		super.onStop();
	}

	// 取消二代证读取，防止主页面关闭了，二代证阅读页面还没有关闭
	protected void cancelReadIDCard() {
		if (isReadingIDCard) {
			activity.finishActivity(DevActivity.REQ_READIDCARD);
			isReadingIDCard = false;
		}
	}

	// 采用默认的控件id进行显示
	protected void showIdCardInfo(IDCardMsg person) {
		showIdCardInfo(R.id.tv_person_info, R.id.iv_photo, person);
	}

	// 更新二代证信息显示
	protected void showIdCardInfo(int textViewId, int imageViewId, IDCardMsg person) {
		// 判断信息是否有效
		String strName = person.getName();
		if (strName.length() == 0) {
			return;
		}
		// 生成内容并填充
		String mstrInfo = "姓名：" + strName + "      姓名拼音：" + person.getPinyin()
				+ "\n" + "性别：" + person.getSex() + "      民族："
				+ person.getNation_str() + "      生日：" + person.getBirth_year()+"-"+person.getBirth_month()+"-"+person.getBirth_day()
				+ "       国籍：中国\n" + "身份证号：" + person.getId_num() + "     证件到期日期："
				+ person.getUseful_e_date_year()+"-"+person.getUseful_e_date_month()+"-"+person.getUseful_e_date_day() + "\n" + "地址：" + person.getAddress();

		View view = getView();// 所属的view
		TextView tvInfo = (TextView)view.findViewById(R.id.tv_person_info);
		ImageView imgView = (ImageView)view.findViewById(R.id.iv_photo);
		tvInfo.setText(mstrInfo);
		tvInfo.setTextColor(0xCC000000);
		imgView.setImageBitmap(decodeIdCardBitmap(person));  
	}

	// 身份证照片由byte[]转成bitmap
	private Bitmap decodeIdCardBitmap(IDCardMsg person) {
		byte[] temp = person.getPhoto();
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			return null;
		}
	}

	/**
	 * 读取银行卡信息，子类根据不同情况进行重写
	 * 
	 * @param magCardInfor
	 */
	protected void operatorMagCardInfo(String magCardInfo) {
		this.magCardInfo = magCardInfo;
	}


	private ArrayList<InputDataItem> list = new ArrayList<InputDataItem>();// 需要进行数据校验

	protected void addDataItem(InputDataItem item){
		//不一样的才往里面加
		for(InputDataItem mItem : list){
			if(mItem.resid == item.resid){
				return;
			}
		}
		list.add(item);
	}

	protected void deleteDataItem(int resid){
		for(InputDataItem mItem : list){
			if(mItem.resid == resid){
				list.remove(mItem);
			}
		}
	}

	protected void startReadIDCard(String title, String errTip) {
		//间隔过短，直接退出
		if( isActionIntervalTooShort())
			return;

		if (title == null || title.length() == 0)
			title = getString(R.string.readIdentityCard);

		if (errTip != null && errTip.length() > 0)
			tipReadIDErr = errTip;

		if (idCardMsg == null) {
			DevActivity.startReadIDCard(this, title, canSkipOnReadIDCard);
			isReadingIDCard = true;
		}
	}

	//读二代证，并指定是什么人的身份证
	protected void startReadIDCard(String title, String errTip, int from) {
		//间隔过短，直接退出
		if( isActionIntervalTooShort())
			return;

		if (title == null || title.length() == 0)
			title = getString(R.string.readIdentityCard);

		if (errTip != null && errTip.length() > 0)
			tipReadIDErr = errTip;

		if (idCardMsg == null) {
			DevActivity.startReadIDCard(this, title, canSkipOnReadIDCard);
			isReadingIDCard = true;
			this.Transactor = from;
		}
	}

	//判断按键消息间隔是否过短
	private boolean isActionIntervalTooShort(){
		long now = System.currentTimeMillis();
		if (Math.abs(now - lastTimeBtnPressed) < MIN_BUTTON_PRESS_INTERVAL) {
			lastTimeBtnPressed = now;
			return true;
		}
		lastTimeBtnPressed = now;
		return false;
	}

	// 是否可视
	protected boolean isVisiable() {
		if (activity != null) {
			return activity.isVisibledFragment(this);
		}

		return false;
	}

	// 把数据项存储起来
	private void recordData() {
		if (list != null) {
			View fmview = getView();
			if (fmview != null) {
				map.clear();// 清除原始记录
				for (int i = 0; i < list.size(); i++) {
					InputDataItem item = list.get(i);
					View subview = fmview.findViewById(item.resid);
					if (subview != null) {
						if (subview instanceof EditText) {// TextView,EditTex继承至TextView
							if (item.validateType == InputDataItem.VALIDATEYPE_MONEY) {
								map.put(item.strid, ((TextView) subview)
										.getText().toString().trim());
							} else {
								map.put(item.strid, ((TextView) subview)
										.getText().toString().trim());
							}
						} else if (subview instanceof RadioGroup) {// RadioGroup功能
							RadioGroup rg = (RadioGroup) subview;
							View checkedView = rg.findViewById(rg
									.getCheckedRadioButtonId());
							if (checkedView != null) {
								int n = rg.indexOfChild(checkedView) + 1;
								if (n > 0)
									map.put(item.strid, String.valueOf(n));
							}
						} else if (subview instanceof Spinner) {
							Spinner spin = (Spinner) subview;
							long n = spin.getSelectedItemId() + 1;
							map.put(item.strid, String.valueOf(n));
						} else if (subview instanceof CheckBox) {// checkbox类型
							CheckBox cb = (CheckBox) subview;
							map.put(item.strid, cb.isChecked() ? "1" : "0");
						}if (subview instanceof TextView) {//这个要放在最后，因为checkBox也是TextView的了类
							map.put(item.strid, ((TextView) subview)
									.getText().toString().trim());
						}
					}
				}
			}
		}

		// 填入固定值
		if (fixMapData != null) {
			map.putAll(fixMapData);
		}
		onRecordData(map);
	}

	// 子类可以重载，实现一些特殊控件内容值的获取
	protected void onRecordData(HashMap<String, String> map) {

	}

	// 子类应该要重载该函数，返回一个map，每一项为返回给服务器的数据
	protected HashMap<String, String> getRecordMap() {
		recordData();
		// 去除不需要记录的对象
		return map;
	}

	// 存储不变的值
	public void putFixMapData(String key, String value) {
		if (fixMapData == null) {
			fixMapData = new HashMap<String, String>();
		}
		fixMapData.put(key, value);
	}

	/************************* 实现数据校验和恢复 *************************/
	public class InputDataItem {
		public int resid; // 控件的资源id
		public String strid; // 字符串ID
		public int validateType; // 数据校验类型
		public String errTip; // 错误提示信息

		// 校验类型
		public static final int VALIDATETYPE_NONE = 0;// 不需要校验
		public static final int VALIDATETYPE_NOTEMPTY = 1;// 校验非空
		public static final int VALIDATETYPE_PHONE = 2;// 电话号码
		public static final int VALIDATEYPE_MONEY = 3;// 金额
		public static final int VALIDATEYPE_ZIPCODE = 4;// 邮编

		public InputDataItem(int resid, String strid, int validateType,
				String errTip) {
			this.resid = resid;
			this.strid = strid;
			this.validateType = validateType;
			this.errTip = errTip;
		}

		public InputDataItem(int resid, String strid) {
			this.resid = resid;
			this.strid = strid;
			this.validateType = VALIDATETYPE_NONE;
			this.errTip = "";
		}

		public InputDataItem(int resid, String strid, boolean is) {
			this.resid = resid;
			this.strid = strid;
			this.validateType = VALIDATETYPE_NONE;
			this.errTip = "";
		}

		public InputDataItem() {
			this.resid = 0;
			this.strid = "";
			this.validateType = VALIDATETYPE_NONE;
			this.errTip = "";
		}
	}

	// 校验填写的数据是否正确
	protected boolean validateForm() {
		if (list != null) {
			View fmview = getView();
			for (int i = 0; i < list.size(); i++) {
				InputDataItem item = list.get(i);
				// 需要校验
				if (item.validateType != InputDataItem.VALIDATETYPE_NONE) {
					View subview = fmview.findViewById(item.resid);
					if (subview != null) {
						String value = "";
						if (subview instanceof EditText) {
							value = ((EditText) subview).getText().toString()
									.trim();
						} else if (subview instanceof Button) {
							value = ((Button) subview).getText().toString()
									.trim();
						}
						// 非空校验
						if (((item.validateType == InputDataItem.VALIDATETYPE_NOTEMPTY))
								&& value.length() == 0) {
							activity.showTip(item.errTip);
							return false;
						} else if (((item.validateType == InputDataItem.VALIDATEYPE_MONEY))
								&& value.length() == 0) {
							activity.showTip(item.errTip);
							return false;
						} else if (((item.validateType == InputDataItem.VALIDATETYPE_PHONE))
								&& !StringUtil.isPhoneNo(value)) {
							activity.showTip(item.errTip);
							return false;
						} else if (((item.validateType == InputDataItem.VALIDATEYPE_ZIPCODE))
								&& value.length() != 6) {
							activity.showTip(item.errTip);
							return false;
						}
					}
				}
			}
		}
		return doSpecialValidate();
	}

	// 子类特殊处理的可以重写
	protected boolean doSpecialValidate() {
		return true;
	}

	/******************* 一些内部的公共控件 *************************/
	// 小写金额自动转化为大写的文本控件的响应
	public class MoneyTextWatcher implements TextWatcher {
		protected EditText etTarget;

		public MoneyTextWatcher(EditText targetET) {
			this.etTarget = targetET;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String text = s.toString();

			try {
				if (text.length() > 0) {
					double amount = Double.parseDouble(s.toString());
					etTarget.setText(NumToChinese.amountToChinese(amount));
				} else {
					etTarget.setText("");
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 限制小数点输入的位数
	 */
	public class MoneyTextWatcherLimit extends MoneyTextWatcher {

		protected EditText sourceET;

		public MoneyTextWatcherLimit(EditText sourceET, EditText targetET) {
			super(targetET);
			this.sourceET = sourceET;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			super.onTextChanged(s, start, before, count);

		}

		@Override
		public void afterTextChanged(Editable s) {
			String text = s.toString();

			//金额为空，大写控件也置空
			if (text.length() == 0){
				if (etTarget != null)
					etTarget.setText("");
				return;
			}

			//不允许超过最大上限值，超过则删除最后一位
			double money = Double.parseDouble(text);
			if( money > limitMoney) {
				String origintext = text.substring( 0, text.length() - 1);
				sourceET.setText(origintext);
				sourceET.setSelection(origintext.length());
				return;
			}

			//小数点后最多2位，角和分
			if (text.contains(".")) {
				if (text.length() - 1 - text.indexOf(".") > 2) {

					text = text.subSequence(0, text.indexOf(".") + 3)
							.toString();

					sourceET.setText(text);
					sourceET.setSelection(text.length());
				}
			}

			//如果是.xx，自动在前面加上0
			if (text.trim().substring(0).equals(".")) {
				text = "0" + text;
				sourceET.setText(text);
				sourceET.setSelection(2);
			}

			//最前面的数字不应该为0，除小数外
			if (text.startsWith("0") && text.trim().length() > 1) {
				if (!text.substring(1, 2).equals(".")) {
					sourceET.setText(text.subSequence(0, 1));
					sourceET.setSelection(1);
					return;
				}
			}


			//转化大写
			if (etTarget != null) {
				try {
					if (text.length() > 0) {
						double amount = Double.parseDouble(text);
						etTarget.setText(NumToChinese.amountToChinese(amount));
					} else {
						etTarget.setText("");
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**************** 按钮点击事件处理 *****************/
	// 定义翻页、完成接口
	public interface OnFinishListener {
		public void onFinishListener(Fragment fragment);// 上一步按钮被点击时
	}

	private OnFinishListener onFinishListener;// 换页监听器，指定BaseTabActivity
}
