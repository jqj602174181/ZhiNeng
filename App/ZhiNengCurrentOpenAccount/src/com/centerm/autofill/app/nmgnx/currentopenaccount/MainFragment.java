package com.centerm.autofill.app.nmgnx.currentopenaccount;

import java.util.HashMap;
import java.util.Map;

import com.centerm.autofill.app.nmnx.common.agent.SelfIDBlockFragment;
import com.centerm.autofill.appframework.base.BaseScrollActivity;
import com.centerm.autofill.appframework.view.MultiRadioGroup;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainFragment extends SelfIDBlockFragment {

	private RadioGroup rgTransactor; // ������
	
	private MultiRadioGroup rgCardType;	//������ 
	private RadioGroup rgWidthdrawType;	//֧ȡ��ʽ
	private RadioGroup rgCardType1;		//����
	private RadioGroup rgCardType2;		//����
	private LinearLayout linearWithdrawType;
	private BaseScrollActivity activity;	//��acitivity
	private RadioGroup rgAccountStyle;
	private LinearLayout linearAccountType;

	private RadioGroup rgTransactionType; //�ֽ��ת��
	private EditText etAmount;		//���

	private int accountMediaStyle = 1; //�ʻ��������ͣ�1���ۣ�2������, 3IC��
	private int transactionType = 1;	//1�ֽ�2�ӱ��˿���ת�ʻ�����˿���ת��
	private double amount = 0;			//��� 

	final static String[] szCardType = {"1", "2", "3", "4", "5", "6", "7"};

	private Spinner	spPostal;
	private String postalList[];
	private String postalAreaList[];

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_main);

		activity = (BaseScrollActivity)getActivity();	

		postalAreaList 	= getResources().getStringArray(R.array.postal_area);
		postalList		= getResources().getStringArray(R.array.postal);
		spPostal		= (Spinner)view.findViewById(R.id.SP_Postal);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
				R.layout.spinner_item, postalAreaList );
		spPostal.setAdapter( adapter );

		//ʵ�ֶ�ѡһ
		rgCardType1 		= (RadioGroup)view.findViewById( R.id.RG_CARDTYPE);
		rgCardType2 		= (RadioGroup)view.findViewById( R.id.RG_CARDTYPE2);
		rgAccountStyle 		= (RadioGroup)view.findViewById(R.id.RG_ACCOUNTTTYPE);
		linearAccountType	= (LinearLayout)view.findViewById(R.id.LINEAR_AccountType);
		linearWithdrawType  = (LinearLayout)view.findViewById(R.id.LINEAR_WithdrawType);
		rgCardType = new MultiRadioGroup( rgCardType1, rgCardType2 );

		//������
		rgTransactor = (RadioGroup) view.findViewById(R.id.RG_Ransaction);
		rgTransactor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				if (checkedId == R.id.RB_SelfTransaction) { // ���ˣ������ش�������Ϣҳ
					activity.enableFragment("�����������Ϣ", false);
					agentPhone.setVisibility(View.GONE);
					deleteDataItem(R.id.EDIT_AgentMobile);
					activity.putsharedResource( "transactor", "self" );
				} else { // ��ʾ��������Ϣҳ
					activity.enableFragment("�����������Ϣ", true);
					agentPhone.setVisibility(View.VISIBLE);
					addDataItem(new InputDataItem( R.id.EDIT_AgentMobile, "agent_phone", InputDataItem.VALIDATETYPE_PHONE, "����д�����˵绰��"));
					activity.putsharedResource( "transactor", "agent" );
				}
			}
		});


		RadioGroup rgCardPass = (RadioGroup)view.findViewById( R.id.RG_CARDPASS );
		rgWidthdrawType = (RadioGroup)view.findViewById( R.id.RG_WithdrawType );
		rgCardPass.setOnCheckedChangeListener( new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(RadioGroup rg, int checkedId ) {
				Activity activity = getActivity();
				RadioButton rbCert = (RadioButton)activity.findViewById( R.id.RB_Certificate );
				RadioButton rbStamp = (RadioButton)activity.findViewById( R.id.RB_Stamp );
				View cardType1 = activity.findViewById(  R.id.BLOCK_CARDTYPE );
				View cardType2 = activity.findViewById(  R.id.BLOCK_CARDTYPE2 );


				switch (checkedId) {
				case R.id.RB_Bankbook://����
					/*
					 * ֧ȡ��ʽѡ�񣺿�ѡ�����롢֤������ӡ�ܣ���������֧ȡ������֧ȡ��ʽ
					 */
					//							inputFieldItems[0].needRecord = true;
					//							inputFieldItems[4].needRecord = true;
					accountMediaStyle =1;
					linearWithdrawType.setVisibility(View.VISIBLE);
					linearAccountType.setVisibility(View.VISIBLE);
					rgAccountStyle.getChildAt(1).setVisibility(View.VISIBLE);
					rgWidthdrawType.check( R.id.RB_Password);
					rbCert.setVisibility( View.VISIBLE );
					rbStamp.setVisibility( View.VISIBLE );
					cardType1.setVisibility( View.GONE );
					cardType2.setVisibility( View.GONE );
					break;

				case R.id.RB_MagneticCard://����������
					/*
					 * (1)	�ʻ�����ѡ��ֻ�ܹ�ѡ�����ʻ���
								(2)	֧ȡ��ʽѡ��ֻ��ѡ������֧ȡ��ʽ��
								(3)	����ѡ�񣺽�ţ��ͨ������ũһ��ͨ������һ��ͨ����ţ�׽𿨡���ũ��������ͨ����У԰һ��ͨ��

					 */
					//							inputFieldItems[0].needRecord = true;
					//							inputFieldItems[4].needRecord = true;
					accountMediaStyle = 2;
					linearWithdrawType.setVisibility(View.VISIBLE);
					linearAccountType.setVisibility(View.VISIBLE);
					rgAccountStyle.getChildAt(1).setVisibility(View.GONE);
					rgAccountStyle.check(R.id.RB_ACCOUNT_CURRENT);
					rgWidthdrawType.check( R.id.RB_Password);
					rbCert.setVisibility( View.GONE );
					rbStamp.setVisibility( View.GONE );
					cardType1.setVisibility( View.VISIBLE );
					cardType2.setVisibility( View.VISIBLE );
					break;
				case R.id.RB_ICCard://���IC��
					//c)	���ǿ����IC���������ʻ���Ϣҳ�档
					//							inputFieldItems[0].needRecord = false;
					//							inputFieldItems[4].needRecord = false;
					accountMediaStyle = 3;
					linearAccountType.setVisibility(View.GONE);
					linearWithdrawType.setVisibility(View.GONE);
					cardType1.setVisibility(View.GONE );
					cardType2.setVisibility(View.GONE );
					break;
				default:
					break;
				}

			}
		});
		etAmount = (EditText)view.findViewById( R.id.EDIT_StartAmount );
		etAmount.addTextChangedListener(new MoneyTextWatcherLimitEx(etAmount,null));

		rgTransactionType = (RadioGroup) view.findViewById(R.id.RG_TRANSACTIONTYPE );
		rgTransactionType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				//ת��ʱ�ʻ����Ͳ����Ǵ���
				RadioButton rb = (RadioButton)activity.findViewById( R.id.RB_ACCOUNT_FIXED );
				if( accountMediaStyle == 1 ){//�ſ�ʱ�Ž��п���
					if (checkedId == R.id.RB_TRANS_CURRENT) {
						rb.setVisibility( View.VISIBLE );
					} else if( checkedId == R.id.RB_TRANS_TRANSFER){
						rgAccountStyle.check( R.id.RB_ACCOUNT_CURRENT );
						rb.setVisibility( View.GONE );
					}
				}

				switch( checkedId ){
				case R.id.RB_TRANS_CURRENT:
					transactionType = 1;
					activity.enableFragment("ת�������֤", false);
					break;
				case R.id.RB_TRANS_TRANSFER:
					transactionType = 2;
					activity.enableFragment("ת�������֤", false);
					break;
				case R.id.RB_TRANS_TRANSFER_OTHER:
					transactionType = 3;
					if( amount >= 50000 ){
						activity.enableFragment("ת�������֤", true);
					}
				}				
			}
		});

		addDataItem(new InputDataItem(R.id.EDIT_MobileNo, "telephone", InputDataItem.VALIDATETYPE_PHONE, "��������ȷ�ĵ绰���룡"));
		addDataItem(new InputDataItem(R.id.RG_CARDPASS, "account_media"));
		addDataItem(new InputDataItem(R.id.EDIT_StartAmount, "amount"));
		addDataItem(new InputDataItem(R.id.RG_ACCOUNTTTYPE, "account_type"));
		addDataItem(new InputDataItem(R.id.RG_WithdrawType, "withdraw_type"));

		activity.putsharedResource( "transactor", "self" );

		putFixMapData("deposit_type", "1");//����
		putFixMapData("trancode", "nmg1091");

		return view;
	}

	@Override
	protected boolean doSpecialValidate() {
		activity.putsharedResource( "apply_name", idCardMsg.getName());
		activity.putsharedResource( "apply_police", idCardMsg.getSign_office());
		activity.putsharedResource( "apply_idnum", idCardMsg.getId_num());
		return super.doSpecialValidate();
	}

	/*
	 * ����С���������λ��
	 */
	public class MoneyTextWatcherLimitEx extends MoneyTextWatcherLimit{
		public MoneyTextWatcherLimitEx(EditText sourceET, EditText targetET) {
			super(sourceET, targetET);
		}

		@Override
		public void afterTextChanged(Editable s) {	
			super.afterTextChanged( s );

			//��¼���
			String text = s.toString();
			if( text.length() > 0 ){
				amount = Double.parseDouble(text);
			}else{
				amount = 0;
			}
			//����5�����Ǵ�����ת��
			if( amount >= 50000 && transactionType == 3 ){
				activity.enableFragment("ת�������֤", true);
			}else{
				activity.enableFragment("ת�������֤", false);
			}

		}
	}

	@Override
	protected HashMap<String, String> getRecordMap() {
		if( !isVisiable())
			return null;

		HashMap<String, String> map = new HashMap<String, String>();

		// ��д�ɱ༭��ѡ��
		Map<String, String> editmap = super.getRecordMap();
		if (editmap != null) {
			map.putAll(editmap);
		}

		setIdentityCard(idCardMsg, map);
		int id = spPostal.getSelectedItemPosition();
		map.put("postal", postalList[id]);
		map.put( "telephone_transactor", map.get( "telephone" ));	

		if(accountMediaStyle!=3){
			map.put( "trancode", "nmg1011");
			map.put("account_media", ""+accountMediaStyle);
		}else{
			map.put( "trancode", "nmg1012");
		}

		map.put( "transaction_type", "" + ( transactionType == 1 ? 1 : 2 ) );		

		String name = (String)activity.getsharedResource("apply_name");
		String police = (String)activity.getsharedResource("apply_police");
		String idnum = (String)activity.getsharedResource("apply_idnum");
		//����ת��
		if( transactionType == 2 ){
			map.put( "special_name", name);
			map.put( "special_ID_type", "1");
			map.put( "special_ID_num", idnum);
			map.put( "special_police", police );
		}else if( transactionType == 3 ){
			//���˰����Ҵ����˿�/��ת�ʣ������봫�ر�����Ϣ��Ϊ ȡ��ƾ������Ľ�����
			if( "self".equals((String)activity.getsharedResource("transactor")) ){
				map.put( "special_agent_name", name);
				map.put( "special_agent_ID_type", "1");
				map.put( "special_agent_ID_num", idnum);
				map.put( "special_agent_police", police);
			}
		}
		return map;
	}

	@Override
	protected void onRecordData( HashMap<String, String> map ){
		int n = rgCardType.getCheckedRadioButtonIndex();
		if(accountMediaStyle==3){
			map.put( "card_type", "8");
			map.put( "account_media", "5" );
			map.put( "withdraw_type", "1" );
		}else if(accountMediaStyle==2){
			map.put( "card_type", szCardType[n]);
		}
	}
}
