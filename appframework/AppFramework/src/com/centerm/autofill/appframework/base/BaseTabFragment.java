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
 * ������fragment����װ�����¹���: (1)����֤��Ϣ�Ķ�����ʾ
 * 
 */
public class BaseTabFragment extends Fragment {

	protected int btnDrawableId = R.drawable.btn_selector;
	protected int textColor = 0xffffffff;
	HashMap<String, String> map = new HashMap<String, String>();// �洢�����ݣ����˳���ǰҳ����ʾʱ�������´洢
	HashMap<String, String> fixMapData;// �̶��洢�����ݣ�������Ϊҳ���л�������ݣ������ݿ���ҳ��ֱ���޸�
	protected BaseTabActivity activity; // ������activity
	protected IDCardMsg idCardMsg;// �����ȡ�õ����֤��Ϣ
	private String tipReadIDErr = "�������֤��ȡʧ�ܣ�"; // ��ȡ���֤��������ʾ��Ϣ
	protected String magCardInfo; // �����ȡ�õĿ�����Ϣ
	private long lastTimeBtnPressed = 0;// ��һ�ΰ�ť���µ�ʱ�䣬��ʱ��������ʱ����ֹ����
	final int MIN_BUTTON_PRESS_INTERVAL = 500;// ��ť������С���Ϊ500ms

	protected final int FROM_SELF = 0;	//����
	protected final int FROM_AGENT = 1;	//������
	protected final int FROM_OTHER = 2;	//����
	private int Transactor = FROM_SELF;	//��ǰˢ����֤���˵�����
	private volatile boolean isReadingIDCard = false;// �Ƿ����ڶ�����֤
	private boolean canSkipOnReadIDCard = false; // �����֤ʱ�Ƿ������

	protected static final double limitMoney = 999999999.99;
	protected static final String sLimitMoney = "999999999.99";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (BaseTabActivity) activity;
	}

	// ��д������layout��ԴID����
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState, int layoutid) {
		View view = inflater.inflate(layoutid, container, false);

		initBtnResource();
		// ע�ᰴť������
		onPageSwitchListener = (OnPageSwitchListener) activity;

		// ������Щ��ť��Ҫ��ʾ������
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

	protected void initBtnResource()// ��ʼ����ť�ı�����һЩ�������ɫ
	{

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// �Զ��ָ�ָ���ؼ�������
		if (inputDataItems != null) {
			View fmview = getView();
			for (int i = 0; i < inputDataItems.length; i++) {
				InputDataItem item = inputDataItems[i];

				// ������ͼ�����ͽ������ݴ洢
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

		if (idCardMsg != null) {// ��ʾ���֤��Ϣ
			showIdCardInfo(idCardMsg);
		}
	}

	@Override
	public void onStop() {
		recordData();// �洢���ݣ�����������ٺ��޷����»������
		cancelReadIDCard();// ��ֹ����֤ҳ�滹���Ķ�
		super.onStop();
	}

	// �Ƿ����
	protected boolean isVisiable() {
		if (activity != null) {
			return activity.isVisibledTab(this);
		}

		return false;
	}

	// ��������洢����
	private void recordData() {
		if (inputDataItems != null) {
			View fmview = getView();
			if (fmview != null) {
				map.clear();// ���ԭʼ��¼
				for (int i = 0; i < inputDataItems.length; i++) {
					InputDataItem item = inputDataItems[i];
					if (!inputDataItems[i].needRecord)
						continue;
					View subview = fmview.findViewById(item.resid);

					if (subview != null) {
						if (subview instanceof EditText) {// TextView,EditTex�̳���TextView
							if (inputDataItems[i].validateType == InputDataItem.VALIDATEYPE_MONEY) {

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

						}// TODO:other

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

	/******************* ʵ������У��ͻָ� *************/
	public class InputDataItem {
		public int resid; // �ؼ�����Դid
		public String strid; // �ַ���ID
		public int validateType; // ����У������
		public String errTip; // ������ʾ��Ϣ
		public boolean needRecord; // ���ؼ�¼ʱ���Ƿ���Ҫ��¼

		// У������
		public static final int VALIDATETYPE_NONE = 0;// ����ҪУ��
		public static final int VALIDATETYPE_NOTEMPTY = 1;// У��ǿ�
		public static final int VALIDATETYPE_PHONE = 2;// �绰����
		public static final int VALIDATEYPE_MONEY = 3;// ���
		public static final int VALIDATEYPE_ZIPCODE = 4;// �ʱ�

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

	private InputDataItem[] inputDataItems;// ��Ҫ��������У��ͻָ�����

	protected void setInputDataItems(InputDataItem[] items) {
		this.inputDataItems = items;
	}

	protected void setInputDataValidateMode(String strid, int validateType,
			String errTip) {
		if (inputDataItems == null)
			return;
		// ���Ҳ����и���
		for (int i = 0; i < inputDataItems.length; i++) {
			InputDataItem item = inputDataItems[i];
			if (strid.equals(item.strid)) {
				item.validateType = validateType;
				item.errTip = errTip;
			}
		}
	}

	// ������fragmentʵ�����е�У��
	protected boolean doSpecialValidate() {
		return true;
	}

	// У����д�������Ƿ���ȷ
	private boolean validateForm() {
		if (inputDataItems != null) {
			View fmview = getView();
			for (int i = 0; i < inputDataItems.length; i++) {
				InputDataItem item = inputDataItems[i];

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

	/**************** ��ť����¼����� *****************/
	// ���巭ҳ����ɽӿ�
	public interface OnPageSwitchListener {
		public void onNextStep();// ��һ����ť�����ʱ

		public void onPreviewStep();// ��һ����ť�����ʱ

		public void onComplete();// ��ɰ�ť�����ʱ
	}

	private OnPageSwitchListener onPageSwitchListener;// ��ҳ��������ָ��BaseTabActivity

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

	// �ص���һ��
	public void gotoLastStep() {
		onPageSwitchListener.onPreviewStep();
	}


	// ���¶���֤��Ϣ��ʾ
	protected void showIdCardInfo(int textViewId, int imageViewId,
			IDCardMsg person) {
		View view = getView();// ������view
		TextView tvInfo = (TextView) view.findViewById(textViewId);// ���֤�ı���Ϣ
		ImageView imgView = (ImageView) view.findViewById(imageViewId);

		// �ж���Ϣ�Ƿ���Ч
		String strName = person.getName();
		if (strName.length() == 0) {
			return;
		}

		// �������ݲ����
//		String mstrInfo = "������" + strName + "      ����ƴ����" + person.getPinyin()
//				+ "\n" + "�Ա�" + person.getSex() + "      ���壺"
//				+ person.getNation_str() + "      ���գ�" + person.getBirthday()
//				+ "       �������й�\n" + "���֤�ţ�" + person.getId() + "     ֤���������ڣ�"
//				+ person.getValidEnd() + "\n" + "��ַ��" + person.getAddress();
//		tvInfo.setText(mstrInfo);
//		imgView.setImageBitmap(decodeIdCardBitmap(person));  //��ע����
	}

	// ����Ĭ�ϵĿؼ�id������ʾ
	protected void showIdCardInfo(IDCardMsg person) {
		showIdCardInfo(R.id.tv_person_info, R.id.iv_photo, person);
	}

	// ���֤��Ƭ��byte[]ת��bitmap
	private Bitmap decodeIdCardBitmap(IDCardInfo person) {
		byte[] temp = person.getPhoto();
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			return null;
		}
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

	/**
	 * ������ȡ����֤
	 * 
	 * @param title
	 *            ��ȡ����֤ʱ������ʾ��Ϣ
	 * @param errTip
	 *            ��ȡ����ʱ����ʾ��Ϣ
	 */
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

	// ���������֤ʱ��������
	protected void enableReadIDCardSkip() {
		canSkipOnReadIDCard = true;
	}

	// ȡ������֤��ȡ����ֹ��ҳ��ر��ˣ�����֤�Ķ�ҳ�滹û�йر�
	protected void cancelReadIDCard() {
		if (isReadingIDCard) {
			activity.finishActivity(DevActivity.REQ_READIDCARD);
			isReadingIDCard = false;
		}
	}

	// ���û�ȡ��������֤ʱ
	protected void onCancelReadIDCard() {
		activity.showTip("�ý�����Ҫ�������֤�������޷������������ԡ�");
		gotoLastStep();// ��ʧ�ܣ�������һ��
	}

	// ���û�����������֤ʱ
	protected void onSkipReadIDCard() {
		activity.showTip("�ý�����Ҫ�������֤�������޷������������ԡ�");
		gotoLastStep();// ��ʧ�ܣ�������һ��
	}

	// ���ɹ���ȡ����֤ʱ
	protected void onOKReadIDCard() {
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
			onOKReadIDCard();// ��ȡ�ɹ�
			if (Transactor == FROM_AGENT) {
				if (activity.getUniqueMark().equals(idCardMsg.getId_num())) {
					Toast.makeText(activity, "�������뽻���˲�����ͬһ���ˣ���������֤�󣬰���һ��������",
							Toast.LENGTH_LONG).show();
					idCardMsg = null;
					gotoLastStep();// TODO:δ����Ҫ�޸ĳ�ͣ���ڵ�ǰҳ�棬��ʾ�ٴζ�ȡ
				} else
					showIdCardInfo(idCardMsg);
			} else if( Transactor == FROM_SELF ) {
				activity.setUniqueMark(idCardMsg.getId_num());
				showIdCardInfo(idCardMsg);
			} else{//FROM_OTHER
				showIdCardInfo(idCardMsg);
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

	/**
	 * ��ȡ���п���Ϣ��������ݲ�ͬ���������д
	 * 
	 * @param magCardInfor
	 */
	protected void operatorMagCardInfo(String magCardInfor) {
		this.magCardInfo = magCardInfor;
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

	/*
	 * ����ѡ��������ͺ����
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
	 * ���������п�
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
	 * ��һЩ�̶�����Ϣ,������ݲ�ͬ�����������д trancodeΪҵ�����
	 */
	protected void putClientFixMapData(String trancode) {
		if (trancode != null)
			putFixMapData("trancode", trancode);// ҵ�����
		putFixMapData("sign_year", DateUtil.getYear());// ǩ�����ڵ���
		putFixMapData("sign_month", DateUtil.getMonth());// ǩ�����ڵ���
		putFixMapData("sign_day", DateUtil.getDayofMonth());// ǩ�����ڵ���

	}

	/**
	 * ���뽻�״���
	 * 
	 * @param trancode
	 */
	public void putFixMapDataTrancode(String trancode) {
		putFixMapData("trancode", trancode);// ҵ�����
	}

	protected void putFixMapDataDate() {
		putFixMapData("sign_year", DateUtil.getYear());// ǩ�����ڵ���
		putFixMapData("sign_month", DateUtil.getMonth());// ǩ�����ڵ���
		putFixMapData("sign_day", DateUtil.getDayofMonth());// ǩ�����ڵ���

	}
}
