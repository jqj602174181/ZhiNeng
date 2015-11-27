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

	private RadioGroup rgHasCard;	//�Ƿ������
	private EditText etAccountName;	//����
	private EditText etAccountNo;	//�ʺ�
	private EditText etAmount;		//���

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
				//����
				DevActivity.startReadMagCard(MainFragment.this);
			}
		});

		enableEditIDInfo();

		addDataItem(new InputDataItem( R.id.EDIT_AccountName, "account_name", InputDataItem.VALIDATETYPE_NOTEMPTY, "����д������"));
		addDataItem(new InputDataItem( R.id.EDIT_AccountNo, "account", InputDataItem.VALIDATETYPE_NOTEMPTY, "����д�����ʺţ�"));
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
		boolean isSelf = rg_Ransaction.getCheckedRadioButtonId()==R.id.RB_SelfTransaction;//�Ƿ��Ǳ��˰���
		boolean hascard = rgHasCard.getCheckedRadioButtonId() == R.id.RB_HASCARD;//�Ƿ���˿���

		if( !isIDCardInfoValid()){//���֤��Ϣ��Ч			

			if( hascard ){//�п�
				if( amount >= limitMoney1 ){
					activity.showTip( "�����ﵽ20�����ϱ���������֤�������µ����ȡ���֤��" );
					return false;
				}

				//5�����ϡ�20�����£�������Ҫ�༭���֤��
				if(amount >=BIGAMOUNT && amount < limitMoney1 && !isEditInfoValid){
					activity.showTip( "������5������20�����£������µ����ȡ���֤��༭���֤��Ϣ��" );
					return false;
				}
			}else{
				//������ֻҪˢ���������֤������Ҫˢ�������֤
				if( isSelf && amount >= limitMoney2 ){
					activity.showTip( "δЯ�����ۣ������ڵ���1�򣬱���������֤�������µ����ȡ���֤��" );
					return false;
				}
			}

			//�������������֤����һ��
			if( isIDCardInfoEditValid() && !editName.equals( etAccountName.getText().toString())){
				activity.showTip( "�ʺŻ����뽻����������һ�¡���ȷ�Ϻ��޸ġ�" );
				return false;
			}

		}else{
			if( !etAccountName.getText().toString().equals( idCardMsg.getName())){
				activity.showTip( "�ʺŻ����뽻����������һ�¡���ȷ�Ϻ��޸ġ�" );
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

		//��д�ɱ༭��ѡ��
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
