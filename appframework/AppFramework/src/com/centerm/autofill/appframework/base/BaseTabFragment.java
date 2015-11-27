package com.centerm.autofill.appframework.base;

import java.util.HashMap;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.autofill.appframework.R;
import com.centerm.autofill.appframework.utils.DateUtil;
import com.centerm.autofill.appframework.utils.NumToChinese;
import com.centerm.autofill.appframework.utils.StringUtil;
import com.centerm.autofill.dev.DevActivity;
import com.centerm.autofill.dev.IDCardInfo;
import com.centerm.autofill.dev.IDCardMsg;

/**
 * 基础的fragment，封装了以下功能: (1)二代证信息阅读与显示
 * 
 */
public class BaseTabFragment extends Fragment {

	protected int btnDrawableId = R.drawable.btn_selector;
	protected int textColor = 0xffffffff;
	HashMap<String, String> map = new HashMap<String, String>();// 存储的数据，当退出当前页的显示时进行重新存储
	HashMap<String, String> fixMapData;// 固定存储的数据，不会因为页的切换变更内容，但内容可在页内直接修改
	protected BaseTabActivity activity; // 所属的activity
	protected IDCardMsg idCardMsg;// 保存读取好的身份证信息
	private String tipReadIDErr = "居民身份证读取失败！"; // 读取身份证错误后的提示信息
	protected String magCardInfo; // 保存读取好的卡的信息
	private long lastTimeBtnPressed = 0;// 最一次按钮按下的时间，当时间间隔过短时，禁止操作
	final int MIN_BUTTON_PRESS_INTERVAL = 500;// 按钮按下最小间隔为500ms

	protected final int FROM_SELF = 0;	//本人
	protected final int FROM_AGENT = 1;	//代理人
	protected final int FROM_OTHER = 2;	//他人
	private int Transactor = FROM_SELF;	//当前刷二代证的人的类型
	private volatile boolean isReadingIDCard = false;// 是否正在读二代证
	private boolean canSkipOnReadIDCard = false; // 读身份证时是否可跳过

	protected static final double limitMoney = 999999999.99;
	protected static final String sLimitMoney = "999999999.99";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (BaseTabActivity) activity;
	}

	// 重写，增加layout资源ID参数
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState, int layoutid) {
		View view = inflater.inflate(layoutid, container, false);

		initBtnResource();
		// 注册按钮监听器
		onPageSwitchListener = (OnPageSwitchListener) activity;

		// 决定哪些按钮需要显示或隐藏
		Button btnPre = (Button) view.findViewById(R.id.BTN_PRE);
		Button btnNext = (Button) view.findViewById(R.id.BTN_NEXT);
		Button btnComplete = (Button) view.findViewById(R.id.BTN_COMPLETE);
		if (activity.hasPreTab(this)) {
			btnPre.setVisibility(View.VISIBLE);
		} else {
			btnPre.setVisibility(View.GONE);
		}
		if (activity.hasNextTab(this)) {
			btnNext.setVisibility(View.VISIBLE);
			btnComplete.setVisibility(View.GONE);
		} else {
			btnNext.setVisibility(View.GONE);
			btnComplete.setVisibility(View.VISIBLE);
		}
		btnPre.setOnClickListener(onClickListener);
		btnNext.setOnClickListener(onClickListener);
		btnComplete.setOnClickListener(onClickListener);

		btnComplete.setTextColor(textColor);
		btnNext.setTextColor(textColor);
		btnPre.setTextColor(textColor);

		btnComplete.setBackgroundResource(btnDrawableId);
		btnNext.setBackgroundResource(btnDrawableId);
		btnPre.setBackgroundResource(btnDrawableId);

		return view;
	}

	protected void initBtnResource()// 初始化按钮的背景和一些字体的颜色
	{

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// 自动恢复指定控件的数据
		if (inputDataItems != null) {
			View fmview = getView();
			for (int i = 0; i < inputDataItems.length; i++) {
				InputDataItem item = inputDataItems[i];

				// 根据视图的类型进行数据存储
				if (map.containsKey(item.strid)) {
					View subview = fmview.findViewById(item.resid);
					String value = map.get(item.strid);
					if (subview != null) {
						if (subview instanceof EditText) {
							((EditText) subview).setText(value);
						} else if (subview instanceof TextView) {
							((TextView) subview).setText(value);
						}
					}
				}

			}
		}

		if (idCardMsg != null) {// 显示身份证信息
			showIdCardInfo(idCardMsg);
		}
	}

	@Override
	public void onStop() {
		recordData();// 存储数据，否则界面消毁后，无法重新获得数据
		cancelReadIDCard();// 防止二代证页面还在阅读
		super.onStop();
	}

	// 是否可视
	protected boolean isVisiable() {
		if (activity != null) {
			return activity.isVisibledTab(this);
		}

		return false;
	}

	// 把数据项存储起来
	private void recordData() {
		if (inputDataItems != null) {
			View fmview = getView();
			if (fmview != null) {
				map.clear();// 清除原始记录
				for (int i = 0; i < inputDataItems.length; i++) {
					InputDataItem item = inputDataItems[i];
					if (!inputDataItems[i].needRecord)
						continue;
					View subview = fmview.findViewById(item.resid);

					if (subview != null) {
						if (subview instanceof EditText) {// TextView,EditTex继承至TextView
							if (inputDataItems[i].validateType == InputDataItem.VALIDATEYPE_MONEY) {

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

						}// TODO:other

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

	/******************* 实现数据校验和恢复 *************/
	public class InputDataItem {
		public int resid; // 控件的资源id
		public String strid; // 字符串ID
		public int validateType; // 数据校验类型
		public String errTip; // 错误提示信息
		public boolean needRecord; // 返回记录时，是否需要记录

		// 校验类型
		public static final int VALIDATETYPE_NONE = 0;// 不需要校验
		public static final int VALIDATETYPE_NOTEMPTY = 1;// 校验非空
		public static final int VALIDATETYPE_PHONE = 2;// 电话号码
		public static final int VALIDATEYPE_MONEY = 3;// 金额
		public static final int VALIDATEYPE_ZIPCODE = 4;// 邮编

		public InputDataItem(int resid, String strid, int validateType,
				String errTip, boolean needRecord) {
			this.resid = resid;
			this.strid = strid;
			this.validateType = validateType;
			this.errTip = errTip;
			this.needRecord = needRecord;
		}

		public InputDataItem(int resid, String strid) {
			this.resid = resid;
			this.strid = strid;
			this.validateType = VALIDATETYPE_NONE;
			this.errTip = "";
			this.needRecord = true;
		}

		public InputDataItem(int resid, String strid, boolean is) {
			this.resid = resid;
			this.strid = strid;
			this.validateType = VALIDATETYPE_NONE;
			this.errTip = "";
			this.needRecord = is;
		}

		public InputDataItem() {
			this.resid = 0;
			this.strid = "";
			this.validateType = VALIDATETYPE_NONE;
			this.errTip = "";
			this.needRecord = true;
		}
	}

	private InputDataItem[] inputDataItems;// 需要进行数据校验和恢复和项

	protected void setInputDataItems(InputDataItem[] items) {
		this.inputDataItems = items;
	}

	protected void setInputDataValidateMode(String strid, int validateType,
			String errTip) {
		if (inputDataItems == null)
			return;
		// 查找并进行更改
		for (int i = 0; i < inputDataItems.length; i++) {
			InputDataItem item = inputDataItems[i];
			if (strid.equals(item.strid)) {
				item.validateType = validateType;
				item.errTip = errTip;
			}
		}
	}

	// 各个子fragment实现特有的校验
	protected boolean doSpecialValidate() {
		return true;
	}

	// 校验填写的数据是否正确
	private boolean validateForm() {
		if (inputDataItems != null) {
			View fmview = getView();
			for (int i = 0; i < inputDataItems.length; i++) {
				InputDataItem item = inputDataItems[i];

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

	/**************** 按钮点击事件处理 *****************/
	// 定义翻页、完成接口
	public interface OnPageSwitchListener {
		public void onNextStep();// 下一步按钮被点击时

		public void onPreviewStep();// 上一步按钮被点击时

		public void onComplete();// 完成按钮被点击时
	}

	private OnPageSwitchListener onPageSwitchListener;// 换页监听器，指定BaseTabActivity

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			if (id == R.id.BTN_NEXT) {
				if (validateForm())
					onPageSwitchListener.onNextStep();
			} else if (id == R.id.BTN_PRE) {
				onPageSwitchListener.onPreviewStep();
			} else if (id == R.id.BTN_COMPLETE) {
				if (validateForm())
					onPageSwitchListener.onComplete();
			}

		}
	};

	// 回到上一步
	public void gotoLastStep() {
		onPageSwitchListener.onPreviewStep();
	}


	// 更新二代证信息显示
	protected void showIdCardInfo(int textViewId, int imageViewId,
			IDCardMsg person) {
		View view = getView();// 所属的view
		TextView tvInfo = (TextView) view.findViewById(textViewId);// 身份证文本信息
		ImageView imgView = (ImageView) view.findViewById(imageViewId);

		// 判断信息是否有效
		String strName = person.getName();
		if (strName.length() == 0) {
			return;
		}

		// 生成内容并填充
//		String mstrInfo = "姓名：" + strName + "      姓名拼音：" + person.getPinyin()
//				+ "\n" + "性别：" + person.getSex() + "      民族："
//				+ person.getNation_str() + "      生日：" + person.getBirthday()
//				+ "       国籍：中国\n" + "身份证号：" + person.getId() + "     证件到期日期："
//				+ person.getValidEnd() + "\n" + "地址：" + person.getAddress();
//		tvInfo.setText(mstrInfo);
//		imgView.setImageBitmap(decodeIdCardBitmap(person));  //先注释下
	}

	// 采用默认的控件id进行显示
	protected void showIdCardInfo(IDCardMsg person) {
		showIdCardInfo(R.id.tv_person_info, R.id.iv_photo, person);
	}

	// 身份证照片由byte[]转成bitmap
	private Bitmap decodeIdCardBitmap(IDCardInfo person) {
		byte[] temp = person.getPhoto();
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			return null;
		}
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

	/**
	 * 启动读取二代证
	 * 
	 * @param title
	 *            读取二代证时界面提示信息
	 * @param errTip
	 *            读取错误时的提示信息
	 */
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

	// 允许读二代证时跳过操作
	protected void enableReadIDCardSkip() {
		canSkipOnReadIDCard = true;
	}

	// 取消二代证读取，防止主页面关闭了，二代证阅读页面还没有关闭
	protected void cancelReadIDCard() {
		if (isReadingIDCard) {
			activity.finishActivity(DevActivity.REQ_READIDCARD);
			isReadingIDCard = false;
		}
	}

	// 当用户取消读二代证时
	protected void onCancelReadIDCard() {
		activity.showTip("该交易需要核验身份证，交易无法继续，请重试。");
		gotoLastStep();// 读失败，返回上一步
	}

	// 当用户跳过读二代证时
	protected void onSkipReadIDCard() {
		activity.showTip("该交易需要核验身份证，交易无法继续，请重试。");
		gotoLastStep();// 读失败，返回上一步
	}

	// 当成功读取二代证时
	protected void onOKReadIDCard() {
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
			onOKReadIDCard();// 读取成功
			if (Transactor == FROM_AGENT) {
				if (activity.getUniqueMark().equals(idCardMsg.getId_num())) {
					Toast.makeText(activity, "代理人与交易人不能是同一个人！请更换身份证后，按下一步继续。",
							Toast.LENGTH_LONG).show();
					idCardMsg = null;
					gotoLastStep();// TODO:未来需要修改成停留在当前页面，提示再次读取
				} else
					showIdCardInfo(idCardMsg);
			} else if( Transactor == FROM_SELF ) {
				activity.setUniqueMark(idCardMsg.getId_num());
				showIdCardInfo(idCardMsg);
			} else{//FROM_OTHER
				showIdCardInfo(idCardMsg);
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

	/**
	 * 读取银行卡信息，子类根据不同情况进行重写
	 * 
	 * @param magCardInfor
	 */
	protected void operatorMagCardInfo(String magCardInfor) {
		this.magCardInfo = magCardInfor;
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

	/*
	 * 启动选择读卡类型后读卡
	 */
	protected void startReadCard() {
		long now = System.currentTimeMillis();
		if (Math.abs(now - lastTimeBtnPressed) < MIN_BUTTON_PRESS_INTERVAL) {
			lastTimeBtnPressed = now;
			return;
		}
		lastTimeBtnPressed = now;
		DevActivity.startSelectMagCard(getActivity());
	}

	/*
	 * 启动读银行卡
	 */
	protected void startReadMagCard() {
		long now = System.currentTimeMillis();
		if (Math.abs(now - lastTimeBtnPressed) < MIN_BUTTON_PRESS_INTERVAL) {
			lastTimeBtnPressed = now;
			return;
		}
		lastTimeBtnPressed = now;
//		DevActivity.startReadMagCard(getActivity());
	}

	/*
	 * 放一些固定的信息,子类根据不同的情况进行重写 trancode为业务代码
	 */
	protected void putClientFixMapData(String trancode) {
		if (trancode != null)
			putFixMapData("trancode", trancode);// 业务代码
		putFixMapData("sign_year", DateUtil.getYear());// 签字日期的年
		putFixMapData("sign_month", DateUtil.getMonth());// 签字日期的月
		putFixMapData("sign_day", DateUtil.getDayofMonth());// 签字日期的日

	}

	/**
	 * 放入交易代码
	 * 
	 * @param trancode
	 */
	public void putFixMapDataTrancode(String trancode) {
		putFixMapData("trancode", trancode);// 业务代码
	}

	protected void putFixMapDataDate() {
		putFixMapData("sign_year", DateUtil.getYear());// 签字日期的年
		putFixMapData("sign_month", DateUtil.getMonth());// 签字日期的月
		putFixMapData("sign_day", DateUtil.getDayofMonth());// 签字日期的日

	}
}
