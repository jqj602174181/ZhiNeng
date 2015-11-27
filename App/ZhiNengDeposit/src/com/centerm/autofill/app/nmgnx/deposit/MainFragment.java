package com.centerm.autofill.app.nmgnx.deposit;

import java.util.HashMap;
import java.util.Map;

import com.centerm.autofill.app.nmnx.common.agent.SelfIDSkillBlockFragment;
import com.centerm.autofill.dev.DevActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainFragment extends SelfIDSkillBlockFragment {

	private RadioGroup rgHasCard;	//是否带卡折
	private EditText etAccountName;	//户名
	private EditText etAccountNo;	//帐号
	private EditText etAmount;		//金额

	private final int limitMoney1 = 200000;
	private final int limitMoney2 = 10000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_main);

		etAccountName = (EditText)view.findViewById( R.id.EDIT_AccountName );
		etAccountNo = (EditText)view.findViewById(R.id.EDIT_AccountNo);
		etAmount = (EditText)view.findViewById( R.id.EDIT_AMOUNT );
		rgHasCard = (RadioGroup)view.findViewById( R.id.RG_HAVECARD );

		Button btn = (Button)view.findViewById(R.id.BTN_ReadMagcard);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//读卡
				DevActivity.startReadMagCard(MainFragment.this);
			}
		});

		enableEditIDInfo();

		addDataItem(new InputDataItem( R.id.EDIT_AccountName, "account_name", InputDataItem.VALIDATETYPE_NOTEMPTY, "请填写户名！"));
		addDataItem(new InputDataItem( R.id.EDIT_AccountNo, "account", InputDataItem.VALIDATETYPE_NOTEMPTY, "请填写存入帐号！"));
		addDataItem(new InputDataItem( R.id.EDIT_AMOUNT, "amount", InputDataItem.VALIDATETYPE_NOTEMPTY, getString(R.string.saveMoneyNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_MobileNo, "telephone", InputDataItem.VALIDATETYPE_PHONE, getString(R.string.errtip_phone)));
		addDataItem(new InputDataItem( R.id.RG_HAVECARD, "with_account_media" ));

		putFixMapData( "deposit_type", "1" );
		putFixMapData("trancode", "nmg1031" );

		return view;
	}

	@Override
	protected boolean doSpecialValidate() {
		double amount = Double.parseDouble(etAmount.getText().toString());
		boolean isSelf = rg_Ransaction.getCheckedRadioButtonId()==R.id.RB_SelfTransaction;//是否是本人办理
		boolean hascard = rgHasCard.getCheckedRadioButtonId() == R.id.RB_HASCARD;//是否带了卡折

		if( !isIDCardInfoValid()){//身份证信息无效			

			if( hascard ){//有卡
				if( amount >= limitMoney1 ){
					activity.showTip( "存款金额达到20万及以上必须核验身份证！请重新点击读取身份证。" );
					return false;
				}

				//5万及以上、20万以下，至少需要编辑身份证。
				if(amount >=BIGAMOUNT && amount < limitMoney1 && !isEditInfoValid){
					activity.showTip( "存入金额5万以上20万以下，请重新点击读取身份证或编辑身份证信息。" );
					return false;
				}
			}else{
				//代理人只要刷代理人身份证，不需要刷本人身份证
				if( isSelf && amount >= limitMoney2 ){
					activity.showTip( "未携带卡折，金额大于等于1万，必须核验身份证！请重新点击读取身份证。" );
					return false;
				}
			}

			//户名必须与身份证名称一致
			if( isIDCardInfoEditValid() && !editName.equals( etAccountName.getText().toString())){
				activity.showTip( "帐号户名与交易人姓名不一致。请确认后修改。" );
				return false;
			}

		}else{
			if( !etAccountName.getText().toString().equals( idCardMsg.getName())){
				activity.showTip( "帐号户名与交易人姓名不一致。请确认后修改。" );
				return false;
			}
		}

		return super.doSpecialValidate();
	}

	@Override
	protected void operatorMagCardInfo(String magCardInfo) {
		super.operatorMagCardInfo(magCardInfo);
		if (magCardInfo != null){
			etAccountNo.setText(magCardInfo);
			etAccountNo.setEnabled( false );
		}
	}

	@Override
	protected void onOKReadIDCard() {
		super.onOKReadIDCard();
		if( idCardMsg != null ){
			etAccountName.setText(idCardMsg.getName());
		}
	}

	@Override
	protected void setPersioInfo() {
		super.setPersioInfo();
		etAccountName.setText( editName+"");
	}

	@Override
	protected HashMap<String, String> getRecordMap() {
		HashMap<String, String> map = new HashMap<String, String>();

		//填写可编辑的选项
		Map<String, String> editmap = super.getRecordMap();
		if( editmap != null){
			map.putAll( editmap );
		}

		setIdentityCard(idCardMsg, map);		
		map.put( "telephone_transactor", map.get( "telephone" ));
		map.put( "name", map.get("account_name"));

		return map;
	}

}
