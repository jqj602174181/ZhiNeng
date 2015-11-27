package com.centerm.autofill.app.nmgnx.withdraw;

import java.util.HashMap;
import java.util.Map;

import com.centerm.autofill.app.nmnx.common.agent.SelfIDSkillBlockFragment;
import com.centerm.autofill.appframework.utils.StringUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainFragment extends SelfIDSkillBlockFragment implements OnCheckedChangeListener{

	final static int  BIGAMOUNT = 50000;//��������
	private EditText etAmount;		//���
	private RadioGroup rgDrawType;	//֧ȡ��ʽ
	private RadioGroup	rgCardStyle;//��������

	private RadioButton  rbPassword;//����
	private RadioButton	 rbZhengJian;//֤��
	private RadioButton	 rbWuMiYing;//����ӡ

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_main);

		etAmount 	= (EditText)view.findViewById( R.id.EDIT_Money );
		rgDrawType 	= (RadioGroup)view.findViewById( R.id.RG_WithdrawType );
		rgCardStyle = (RadioGroup)view.findViewById(R.id.RG_CardStyle);
		rbPassword	= (RadioButton)view.findViewById(R.id.RB_Password);
		rbZhengJian	= (RadioButton)view.findViewById(R.id.RB_Certificate);
		rbWuMiYing	= (RadioButton)view.findViewById(R.id.RB_NoPassword);

		rgCardStyle.setOnCheckedChangeListener(this);

		addDataItem(new InputDataItem( R.id.RG_WithdrawType, "withdraw_type")); //֧ȡ��ʽ
		addDataItem(new InputDataItem(R.id.RG_CardStyle,"account_media")); //�ʻ�����,��/��
		addDataItem(new InputDataItem( R.id.EDIT_Money, "amount", InputDataItem.VALIDATETYPE_NOTEMPTY, getString(R.string.moneyNoNull))); 

		putFixMapData("trancode", "nmg1041");

		return view;
	}

	@Override
	protected boolean doSpecialValidate() {
		if( !isIDCardInfoValid()){//���֤��Ϣ��Ч			
			double amount = StringUtil.parseDouble(etAmount.getText().toString());
			if( rgDrawType.getCheckedRadioButtonId() == R.id.RB_Password ){//����֧ȡ
				if( amount >= BIGAMOUNT ){
					activity.showTip( "���֧ȡ����������֤�������µ����ȡ���֤��" );
					return false;
				}
			}else{
				activity.showTip( "�����뷽ʽ֧ȡ������Я��������Ч���֤�������µ����ȡ���֤��" );
				return false;
			}
		}
		return super.doSpecialValidate();
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

		return map;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(group.getId()==rgCardStyle.getId()){
			switch (checkedId) {
			case R.id.RB_AccountStyleCard://��
				rbWuMiYing.setVisibility(View.GONE);
				rbZhengJian.setVisibility(View.GONE);
				rbPassword.setChecked(true);
				break;
			case R.id.RB_AccountStyleZhe://��
				rbWuMiYing.setVisibility(View.VISIBLE);
				rbZhengJian.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	}

}
