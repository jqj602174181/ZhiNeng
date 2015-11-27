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

	protected BaseScrollActivity activity; // ������activity
	HashMap<String, String> map = new HashMap<String, String>();// �洢������
	HashMap<String, String> fixMapData;// �̶��洢������

	public final static int  BIGAMOUNT = 50000;//��������

	protected final int FROM_SELF = 0;	//����
	protected final int FROM_AGENT = 1;	//������
	protected final int FROM_OTHER = 2;	//����

	private long lastTimeBtnPressed = 0;// ��һ�ΰ�ť���µ�ʱ�䣬��ʱ��������ʱ����ֹ����
	final int MIN_BUTTON_PRESS_INTERVAL = 500;// ��ť������С���Ϊ500ms
	private String tipReadIDErr = "�������֤��ȡʧ�ܣ�"; // ��ȡ���֤��������ʾ��Ϣ
	protected IDCardMsg idCardMsg;// �����ȡ�õ����֤��Ϣ
	private boolean canSkipOnReadIDCard = false; // �����֤ʱ�Ƿ������
	private volatile boolean isReadingIDCard = false;// �Ƿ����ڶ�����֤
	protected int Transactor = FROM_SELF;	//��ǰˢ����֤���˵�����

	protected static final double limitMoney = 999999999.99;

	protected String magCardInfo; // �����ȡ�õĿ�����Ϣ

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (BaseScrollActivity) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState, int layoutId) {
		View view = inflater.inflate(layoutId, container, false);

		// ע�ᰴť������
		onFinishListener = (OnFinishListener) activity;

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null)
			return;
		switch (requestCode) {
		case DevActivity.REQ_READIDCARD:// ������֤
			isReadingIDCard = false;
			operatorIdCardInfo(requestCode, resultCode, data);
			break;
		case DevActivity.REQ_READMAGCARD:// ���ſ�
		case DevActivity.REQ_SELECTREADCARD://ѡ������ֿ�
			String info = data.getStringExtra("no");
			operatorMagCardInfo(info);
			break;
		default:// ͨ��ʱ��������

			break;
		}
	}

	/*
	 * �Զ�ȡ�Ķ���֤��Ϣ���д���
	 */
	protected void operatorIdCardInfo(int requestCode, int resultCode,
			Intent data) {
		idCardMsg = DevActivity.GetIdCardMsg(requestCode, resultCode, data);
		if (idCardMsg != null) {
			if (Transactor == FROM_AGENT) {
				if (activity.getUniqueMark().equals(idCardMsg.getId_num())) {
					Toast.makeText(activity, "�������뽻���˲�����ͬһ���ˣ���������֤�����²�����",Toast.LENGTH_LONG).show();
					idCardMsg = null;
				} else{
					showIdCardInfo(idCardMsg);
					onOKReadIDCard();// ��ȡ�ɹ�
				}
			} else if( Transactor == FROM_SELF ) {
				activity.setUniqueMark(idCardMsg.getId_num());
				showIdCardInfo(idCardMsg);
				onOKReadIDCard();// ��ȡ�ɹ�
			} else if( Transactor == FROM_OTHER){
				showIdCardInfo(idCardMsg);
				onOKReadIDCard();// ��ȡ�ɹ�
			}
		} else {// ����ʧ��
			if (resultCode == DevActivity.RES_CANCEL) {// �û�ȡ��
				onCancelReadIDCard();
			} else if (resultCode == DevActivity.RES_SKIP) {
				onSkipReadIDCard();
			} else {
				activity.showTip(tipReadIDErr);
				gotoLastStep();// ��ʧ�ܣ�������һ��
			}
		}
	}

	// ���������֤ʱ��������
	protected void enableReadIDCardSkip() {
		canSkipOnReadIDCard = true;
	}

	// ���ɹ���ȡ����֤ʱ
	protected void onOKReadIDCard() {
	}

	// �ص���һ��
	public void gotoLastStep() {
		onFinishListener.onFinishListener(this);
	}

	// ���û�����������֤ʱ
	protected void onSkipReadIDCard() {
		activity.showTip("�ý�����Ҫ�������֤�������޷������������ԡ�");
		//		gotoLastStep();// ��ʧ�ܣ�������һ��
	}

	// ���û�ȡ��������֤ʱ
	protected void onCancelReadIDCard() {
		activity.showTip("�ý�����Ҫ�������֤�������޷������������ԡ�");
		gotoLastStep();// ��ʧ�ܣ�������һ��
	}

	@Override
	public void onStop() {
		cancelReadIDCard();// ��ֹ����֤ҳ�滹���Ķ�
		super.onStop();
	}

	// ȡ������֤��ȡ����ֹ��ҳ��ر��ˣ�����֤�Ķ�ҳ�滹û�йر�
	protected void cancelReadIDCard() {
		if (isReadingIDCard) {
			activity.finishActivity(DevActivity.REQ_READIDCARD);
			isReadingIDCard = false;
		}
	}

	// ����Ĭ�ϵĿؼ�id������ʾ
	protected void showIdCardInfo(IDCardMsg person) {
		showIdCardInfo(R.id.tv_person_info, R.id.iv_photo, person);
	}

	// ���¶���֤��Ϣ��ʾ
	protected void showIdCardInfo(int textViewId, int imageViewId, IDCardMsg person) {
		// �ж���Ϣ�Ƿ���Ч
		String strName = person.getName();
		if (strName.length() == 0) {
			return;
		}
		// �������ݲ����
		String mstrInfo = "������" + strName + "      ����ƴ����" + person.getPinyin()
				+ "\n" + "�Ա�" + person.getSex() + "      ���壺"
				+ person.getNation_str() + "      ���գ�" + person.getBirth_year()+"-"+person.getBirth_month()+"-"+person.getBirth_day()
				+ "       �������й�\n" + "���֤�ţ�" + person.getId_num() + "     ֤���������ڣ�"
				+ person.getUseful_e_date_year()+"-"+person.getUseful_e_date_month()+"-"+person.getUseful_e_date_day() + "\n" + "��ַ��" + person.getAddress();

		View view = getView();// ������view
		TextView tvInfo = (TextView)view.findViewById(R.id.tv_person_info);
		ImageView imgView = (ImageView)view.findViewById(R.id.iv_photo);
		tvInfo.setText(mstrInfo);
		tvInfo.setTextColor(0xCC000000);
		imgView.setImageBitmap(decodeIdCardBitmap(person));  
	}

	// ���֤��Ƭ��byte[]ת��bitmap
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
	 * ��ȡ���п���Ϣ��������ݲ�ͬ���������д
	 * 
	 * @param magCardInfor
	 */
	protected void operatorMagCardInfo(String magCardInfo) {
		this.magCardInfo = magCardInfo;
	}


	private ArrayList<InputDataItem> list = new ArrayList<InputDataItem>();// ��Ҫ��������У��

	protected void addDataItem(InputDataItem item){
		//��һ���Ĳ��������
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
		//������̣�ֱ���˳�
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

	//������֤����ָ����ʲô�˵����֤
	protected void startReadIDCard(String title, String errTip, int from) {
		//������̣�ֱ���˳�
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

	//�жϰ�����Ϣ����Ƿ����
	private boolean isActionIntervalTooShort(){
		long now = System.currentTimeMillis();
		if (Math.abs(now - lastTimeBtnPressed) < MIN_BUTTON_PRESS_INTERVAL) {
			lastTimeBtnPressed = now;
			return true;
		}
		lastTimeBtnPressed = now;
		return false;
	}

	// �Ƿ����
	protected boolean isVisiable() {
		if (activity != null) {
			return activity.isVisibledFragment(this);
		}

		return false;
	}

	// ��������洢����
	private void recordData() {
		if (list != null) {
			View fmview = getView();
			if (fmview != null) {
				map.clear();// ���ԭʼ��¼
				for (int i = 0; i < list.size(); i++) {
					InputDataItem item = list.get(i);
					View subview = fmview.findViewById(item.resid);
					if (subview != null) {
						if (subview instanceof EditText) {// TextView,EditTex�̳���TextView
							if (item.validateType == InputDataItem.VALIDATEYPE_MONEY) {
								map.put(item.strid, ((TextView) subview)
										.getText().toString().trim());
							} else {
								map.put(item.strid, ((TextView) subview)
										.getText().toString().trim());
							}
						} else if (subview instanceof RadioGroup) {// RadioGroup����
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
						} else if (subview instanceof CheckBox) {// checkbox����
							CheckBox cb = (CheckBox) subview;
							map.put(item.strid, cb.isChecked() ? "1" : "0");
						}if (subview instanceof TextView) {//���Ҫ���������ΪcheckBoxҲ��TextView������
							map.put(item.strid, ((TextView) subview)
									.getText().toString().trim());
						}
					}
				}
			}
		}

		// ����̶�ֵ
		if (fixMapData != null) {
			map.putAll(fixMapData);
		}
		onRecordData(map);
	}

	// ����������أ�ʵ��һЩ����ؼ�����ֵ�Ļ�ȡ
	protected void onRecordData(HashMap<String, String> map) {

	}

	// ����Ӧ��Ҫ���ظú���������һ��map��ÿһ��Ϊ���ظ�������������
	protected HashMap<String, String> getRecordMap() {
		recordData();
		// ȥ������Ҫ��¼�Ķ���
		return map;
	}

	// �洢�����ֵ
	public void putFixMapData(String key, String value) {
		if (fixMapData == null) {
			fixMapData = new HashMap<String, String>();
		}
		fixMapData.put(key, value);
	}

	/************************* ʵ������У��ͻָ� *************************/
	public class InputDataItem {
		public int resid; // �ؼ�����Դid
		public String strid; // �ַ���ID
		public int validateType; // ����У������
		public String errTip; // ������ʾ��Ϣ

		// У������
		public static final int VALIDATETYPE_NONE = 0;// ����ҪУ��
		public static final int VALIDATETYPE_NOTEMPTY = 1;// У��ǿ�
		public static final int VALIDATETYPE_PHONE = 2;// �绰����
		public static final int VALIDATEYPE_MONEY = 3;// ���
		public static final int VALIDATEYPE_ZIPCODE = 4;// �ʱ�

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

	// У����д�������Ƿ���ȷ
	protected boolean validateForm() {
		if (list != null) {
			View fmview = getView();
			for (int i = 0; i < list.size(); i++) {
				InputDataItem item = list.get(i);
				// ��ҪУ��
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
						// �ǿ�У��
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

	// �������⴦��Ŀ�����д
	protected boolean doSpecialValidate() {
		return true;
	}

	/******************* һЩ�ڲ��Ĺ����ؼ� *************************/
	// Сд����Զ�ת��Ϊ��д���ı��ؼ�����Ӧ
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
	 * ����С���������λ��
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

			//���Ϊ�գ���д�ؼ�Ҳ�ÿ�
			if (text.length() == 0){
				if (etTarget != null)
					etTarget.setText("");
				return;
			}

			//���������������ֵ��������ɾ�����һλ
			double money = Double.parseDouble(text);
			if( money > limitMoney) {
				String origintext = text.substring( 0, text.length() - 1);
				sourceET.setText(origintext);
				sourceET.setSelection(origintext.length());
				return;
			}

			//С��������2λ���Ǻͷ�
			if (text.contains(".")) {
				if (text.length() - 1 - text.indexOf(".") > 2) {

					text = text.subSequence(0, text.indexOf(".") + 3)
							.toString();

					sourceET.setText(text);
					sourceET.setSelection(text.length());
				}
			}

			//�����.xx���Զ���ǰ�����0
			if (text.trim().substring(0).equals(".")) {
				text = "0" + text;
				sourceET.setText(text);
				sourceET.setSelection(2);
			}

			//��ǰ������ֲ�Ӧ��Ϊ0����С����
			if (text.startsWith("0") && text.trim().length() > 1) {
				if (!text.substring(1, 2).equals(".")) {
					sourceET.setText(text.subSequence(0, 1));
					sourceET.setSelection(1);
					return;
				}
			}


			//ת����д
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

	/**************** ��ť����¼����� *****************/
	// ���巭ҳ����ɽӿ�
	public interface OnFinishListener {
		public void onFinishListener(Fragment fragment);// ��һ����ť�����ʱ
	}

	private OnFinishListener onFinishListener;// ��ҳ��������ָ��BaseTabActivity
}
