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

	final static int  BIGAMOUNT = 50000;//大额度限制
	private EditText etAmount;		//金额
	private RadioGroup rgDrawType;	//支取方式
	private RadioGroup	rgCardStyle;//卡折类型

	private RadioButton  rbPassword;//密码
	private RadioButton	 rbZhengJian;//证件
	private RadioButton	 rbWuMiYing;//无密印

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

		addDataItem(new InputDataItem( R.id.RG_WithdrawType, "withdraw_type")); //支取方式
		addDataItem(new InputDataItem(R.id.RG_CardStyle,"account_media")); //帐户类型,卡/折
		addDataItem(new InputDataItem( R.id.EDIT_Money, "amount", InputDataItem.VALIDATETYPE_NOTEMPTY, getString(R.string.moneyNoNull))); 

		putFixMapData("trancode", "nmg1041");

		return view;
	}

	@Override
	protected boolean doSpecialValidate() {
		if( !isIDCardInfoValid()){//身份证信息无效			
			double amount = StringUtil.parseDouble(etAmount.getText().toString());
			if( rgDrawType.getCheckedRadioButtonId() == R.id.RB_Password ){//密码支取
				if( amount >= BIGAMOUNT ){
					activity.showTip( "大额支取必须核验身份证！请重新点击读取身份证。" );
					return false;
				}
			}else{
				activity.showTip( "非密码方式支取，必须携带个人有效身份证！请重新点击读取身份证。" );
				return false;
			}
		}
		return super.doSpecialValidate();
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

		return map;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(group.getId()==rgCardStyle.getId()){
			switch (checkedId) {
			case R.id.RB_AccountStyleCard://卡
				rbWuMiYing.setVisibility(View.GONE);
				rbZhengJian.setVisibility(View.GONE);
				rbPassword.setChecked(true);
				break;
			case R.id.RB_AccountStyleZhe://折
				rbWuMiYing.setVisibility(View.VISIBLE);
				rbZhengJian.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	}

}
