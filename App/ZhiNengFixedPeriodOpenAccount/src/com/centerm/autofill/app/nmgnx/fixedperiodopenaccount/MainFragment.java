package com.centerm.autofill.app.nmgnx.fixedperiodopenaccount;

import java.util.HashMap;
import java.util.Map;

import com.centerm.autofill.app.nmnx.common.agent.SelfIDBlockFragment;
import com.centerm.autofill.appframework.base.BaseScrollFragment.MoneyTextWatcherLimit;
import com.centerm.autofill.dev.DevActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainFragment extends SelfIDBlockFragment implements View.OnClickListener, OnCheckedChangeListener{

	private RadioGroup rgTransactor; // 办理人
	private RadioGroup rgTransactionType;//现金或转帐
	private EditText etAmount;		//金额

	private double amount = 0;			//金额 

	private RadioGroup	rgOpenAccountStyle;//开户类别

	private LinearLayout linearMainCard;

	private Button 	btnMainCard;
	private EditText etMainCard;
	private int openAccountStyle = 1;
	private int transactionType = 1;	//1现金，2从本人卡折转帐或从他人卡折转帐

	private String[] termTypes; //存期类型
	private int openStyle = 1; //开户类别
	private Spinner spTerm;

	private RadioGroup rgWithdrawType;
	private RadioGroup rgWithdrawTypePassword;
	private RadioGroup rgTermType;			//定期开户类型
	private RadioGroup rgRedepositType;		//转存方式

	private int widthdrawType = 1;  //1密码，2证件，3无印密
	private String[] timeList={"03","06","12","24","36","60"};//03三个月，06六个月，12是1年，24是2年，36是3年，60是5年

	private boolean isSelf = true;	//是否本人办理

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_main);

		etAmount = (EditText)view.findViewById( R.id.EDIT_StartAmount );

		etAmount.addTextChangedListener(new MoneyTextWatcherLimitEx(etAmount,null));
		//开户金额：若是现金，必须输入金额
		rgTransactionType = (RadioGroup) view.findViewById(R.id.RG_TRANSACTIONTYPE );
		rgTransactionType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup rg, int checkedId) {

				if (checkedId == R.id.RB_TRANS_CURRENT) {
					transactionType = 1;
					activity.enableFragment("转出方身份证", false);
				} else if( checkedId == R.id.RB_TRANS_TRANSFER) {
					transactionType = 2;
					activity.enableFragment("转出方身份证", false);
				} else if( checkedId == R.id.RB_TRANS_TRANSFER_OTHER) {
					transactionType = 3;
					if( amount >= 50000 ){
						activity.enableFragment("转出方身份证", true);
					}
				}
			}
		});

		//代理人
		rgTransactor = (RadioGroup) view.findViewById(R.id.RG_Ransaction);
		rgTransactor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				if (checkedId == R.id.RB_SelfTransaction) { // 本人，则隐藏代理人信息页
					activity.enableFragment("代理人身份信息", false);
					agentPhone.setVisibility(View.GONE);
					deleteDataItem(R.id.EDIT_AgentMobile);
					isSelf = true;
				} else { // 显示代理人信息页
					activity.enableFragment("代理人身份信息", true);
					agentPhone.setVisibility(View.VISIBLE);
					addDataItem(new InputDataItem( R.id.EDIT_AgentMobile, "agent_phone", InputDataItem.VALIDATETYPE_PHONE, "请填写代理人电话！"));
					isSelf = false;
				}
			}
		});

		//零存整取，只能选择不转存
		rgRedepositType = (RadioGroup)view.findViewById( R.id.RG_REDEPOSITTYPE );
		rgTermType = (RadioGroup)view.findViewById( R.id.RG_TERMTYPE );
		rgTermType.setOnCheckedChangeListener( new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				RadioButton rbRedepositType2 = (RadioButton)activity.findViewById( R.id.RB_REDEPOSIT2 );
				RadioButton rbRedepositType3 = (RadioButton)activity.findViewById( R.id.RB_REDEPOSIT3 );
				if (checkedId == R.id.RB_TERM_FIX) {
					rbRedepositType2.setVisibility( View.VISIBLE );
					rbRedepositType3.setVisibility( View.VISIBLE );
				} else if( checkedId == R.id.RB_lingChunZhengQu){
					rgRedepositType.check( R.id.RB_REDEPOSIT1 );
					rbRedepositType2.setVisibility( View.INVISIBLE );
					rbRedepositType3.setVisibility( View.INVISIBLE );
				}
			}
		});

		//存期
		spTerm = (Spinner)view.findViewById( R.id.SP_DEPOSITTERM);
		termTypes = getResources().getStringArray(R.array.depositterm);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, termTypes);
		spTerm.setAdapter( adapter );
		rgWithdrawType  = (RadioGroup)view.findViewById(R.id.RG_WithdrawType);
		rgWithdrawTypePassword = (RadioGroup)view.findViewById(R.id.RG_WithdrawTypePassword);
		rgWithdrawType.setOnCheckedChangeListener(this);

		setRgOpenAccountStyle(view);
		setOpenStyle();
		activity.putsharedResource( "transaction_type", 1 ); 

		addDataItem(new InputDataItem(R.id.EDIT_StartAmount, "amount"));
		addDataItem(new InputDataItem(R.id.EDIT_MobileNo, "telephone", InputDataItem.VALIDATETYPE_PHONE, "请输入正确的电话号码！"));
		addDataItem(new InputDataItem(R.id.RG_TERMTYPE, "account_fix_type"));
		addDataItem(new InputDataItem(R.id.RG_WithdrawType, "withdraw_type"));
		addDataItem(new InputDataItem(R.id.RG_REDEPOSITTYPE, "redeposit_type"));

		putFixMapData("trancode", "nmg1021");// 业务代码
		putFixMapData("deposit_type", "2");//定期

		return view;
	}

	/*
	 * 根据开户类别来决定支取方式
	 * 若开户类别是“卡内定期”，则默认选用“密码”支取方式；若是“定期存单”，则可选择密码、证件或无印密（即无密码支取）三种支取方式。
	 */
	private void setOpenStyle()
	{
		if(activity.getsharedResource("openStyle") == null){
			return;
		}
		openStyle = (Integer)activity.getsharedResource("openStyle");

		if(openStyle==1){//定期存单/折
			rgWithdrawType.setVisibility(View.VISIBLE);
			rgWithdrawTypePassword.setVisibility(View.GONE);
		}else if(openStyle==2){//卡内定期
			rgWithdrawType.setVisibility(View.GONE);
			rgWithdrawTypePassword.setVisibility(View.VISIBLE);
			((RadioButton)rgWithdrawType.getChildAt(0)).setChecked(true);
			rgWithdrawType.getChildAt(1).setVisibility(View.GONE);
			rgWithdrawType.getChildAt(2).setVisibility(View.GONE);
		}
	}

	@Override
	protected boolean doSpecialValidate() {
		//定期时金额至少50及以上
		String amount = etAmount.getText().toString();
		if( (amount.length() > 0 && Double.parseDouble( amount ) < 50) ){
			activity.showTip( "定期存款金额不能低于50元。" );
			return false;
		}

		if(openAccountStyle==2){
			if(etMainCard.getText().length()==0){
				activity.showTip(getString(R.string.mainCardNoNull));
				return false;
			}
		}

		//存款金额为空，直接按0计算
		if( amount.length() == 0 )
			amount = "0";

		activity.putsharedResource("amount", Double.parseDouble( amount )); //存放金额用来判断
		activity.putsharedResource("openStyle", openAccountStyle);

		activity.putsharedResource("apply_name", idCardMsg.getName());	
		return super.doSpecialValidate();
	}

	private void setRgOpenAccountStyle(View view)
	{
		etMainCard		   = (EditText)view.findViewById(R.id.EDIT_MainCard);
		btnMainCard		   = (Button)view.findViewById(R.id.BTN_MainCard);
		linearMainCard	   = (LinearLayout)view.findViewById(R.id.LINEAR_MainCard);
		rgOpenAccountStyle = (RadioGroup)view.findViewById(R.id.RG_OpenAccountStyle);
		btnMainCard.setOnClickListener(this);
		rgOpenAccountStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.RB_OpenAccount_KaiNeiDingQi:
					openAccountStyle = 2;
					linearMainCard.setVisibility(View.VISIBLE);				
					break;
				case R.id.RB_OpenAccount_DingQiChunZhe:
					openAccountStyle = 1;
					linearMainCard.setVisibility(View.GONE);
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.BTN_MainCard:
			DevActivity.startReadMagCard(MainFragment.this);
			break;
		default:
			break;
		}
	}

	/*
	 * 限制小数点输入的位数
	 */
	public class MoneyTextWatcherLimitEx extends MoneyTextWatcherLimit{
		public MoneyTextWatcherLimitEx(EditText sourceET, EditText targetET) {
			super(sourceET, targetET);
		}

		@Override
		public void afterTextChanged(Editable s) {	
			super.afterTextChanged( s );

			//记录金额
			String text = s.toString();
			if( text.length() > 0 ){
				amount = Double.parseDouble(text);
			}else{
				amount = 0;
			}
			//大于5万，且是从他人转帐
			if( amount >= 50000 && transactionType == 3 ){
				activity.enableFragment("转出方身份证", true);
			}else{
				activity.enableFragment("转出方身份证", false);
			}

		}
	}

	@Override
	protected HashMap<String, String> getRecordMap() {

		HashMap<String, String> map = new HashMap<String, String>();

		// 填写可编辑的选项
		Map<String, String> editmap = super.getRecordMap();
		if (editmap != null) {
			map.putAll(editmap);
		}

		setIdentityCard( idCardMsg, map );
		if(openAccountStyle==2){
			map.put("account", etMainCard.getText().toString());
		}

		map.put( "telephone_transactor", map.get( "telephone" ));
		map.put( "transaction_type", "" + ( transactionType == 1 ? 1 : 2 ) );

		int id= (int)spTerm.getSelectedItemPosition();
		map.put("deposit_term", timeList[id]);

		if(openStyle==1){//定期存单/折
			map.put("withdraw_type", "" + widthdrawType);
		}else if(openStyle==2){//卡内定期
			map.put("withdraw_type", "1");
		}

		//本人转帐
		if( transactionType == 2 ){
			map.put( "special_name", idCardMsg.getName());
			map.put( "special_ID_type", "1");
			map.put( "special_ID_num", idCardMsg.getId_num());
			map.put( "special_police", idCardMsg.getSign_office());
		}else if( transactionType == 3 ){
			//本人办理且从他人卡/折转帐，还必须传回本人信息作为 取款凭条背面的交易人
			if( isSelf ){
				map.put( "special_agent_name", idCardMsg.getName());
				map.put( "special_agent_ID_type", "1");
				map.put( "special_agent_ID_num", idCardMsg.getId_num());
				map.put( "special_agent_police", idCardMsg.getSign_office());
			}
		}
		return map;
	}

	@Override
	protected void operatorMagCardInfo(String magCardInfo) {
		super.operatorMagCardInfo(magCardInfo);
		etMainCard.setText(magCardInfo);
	}

	@Override
	protected void onRecordData(HashMap<String, String> map) {
		super.onRecordData(map);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(group.getId() == R.id.RG_WithdrawType){
			switch (checkedId) {
			case R.id.RB_Password:
				widthdrawType = 1;
				break;
			case R.id.RB_Certificate:
				widthdrawType = 2;
				break;
			case R.id.RB_Stamp:
				widthdrawType = 3;
				break;
			default:
				break;
			}
		}
	}
}
