package com.centerm.autofill.app.nmgnx.crossbanktransfer;

import java.util.HashMap;

import com.centerm.autofill.app.nmnx.common.agent.SelfIDSkillBlockFragment;
import com.centerm.autofill.dev.DevActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainFragment extends SelfIDSkillBlockFragment implements View.OnClickListener{

	private View mainView;

	private Button   btnReadCard;			//读卡按钮
	private EditText edMoneyYuan;			//金额（元）
	private EditText edMoneyBig;			//金额（大写）

	private EditText edRemitAccounts;		//汇款人帐号

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_main);

		edRemitAccounts				= 	(EditText)mainView.findViewById(R.id.EDIT_RemintAccounts);

		addDataItem(new InputDataItem( R.id.EDIT_RemintFullNamne, "account_name", InputDataItem.VALIDATETYPE_NOTEMPTY, getString(R.string.RemintNameNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_RemintHuiChuBank, "account_bank", InputDataItem.VALIDATETYPE_NOTEMPTY, getString(R.string.RemintBankNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_RemintAccounts, "account",InputDataItem.VALIDATETYPE_NOTEMPTY,getString(R.string.RemintAccountNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_DxPayeeFullNamne, "credit_name", InputDataItem.VALIDATETYPE_NOTEMPTY, getString(R.string.payeeNameNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_DxPayeeAccounts, "credit_account",InputDataItem.VALIDATETYPE_NOTEMPTY,getString(R.string.payeeAccountNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_DxPayeeHuiLuBank, "credit_bank", InputDataItem.VALIDATETYPE_NOTEMPTY, getString(R.string.payeeBankNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_DxMoneyYuan, "amount",InputDataItem.VALIDATETYPE_NOTEMPTY,getString(R.string.amountLowerNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_DxMoneyBig, "amount_capital",InputDataItem.VALIDATETYPE_NOTEMPTY,getString(R.string.amountLowerNoNull)));
		addDataItem(new InputDataItem( R.id.EDIT_Information, "extra_info"));
		
		edMoneyBig					= 	(EditText)mainView.findViewById(R.id.EDIT_DxMoneyBig);
		edMoneyYuan					= 	(EditText)mainView.findViewById(R.id.EDIT_DxMoneyYuan);

		MoneyTextWatcherLimit textWatcher = new MoneyTextWatcherLimit(edMoneyYuan,edMoneyBig);
		edMoneyYuan.addTextChangedListener(textWatcher);
		btnReadCard = (Button)mainView.findViewById(R.id.BTN_RemintReadCard);
		btnReadCard.setOnClickListener(this);

		putFixMapData( "trancode", "nmg1061");

		return mainView;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.BTN_RemintReadCard){
			DevActivity.startReadMagCard(MainFragment.this);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		disableAutoRun();
		super.onAttach(activity);
	}

	@Override
	protected void operatorMagCardInfo(String magCardInfo) {
		super.operatorMagCardInfo(magCardInfo);
		edRemitAccounts.setText(magCardInfo);
	}

	@Override
	protected HashMap<String, String> getRecordMap() {

		HashMap<String, String> map = super.getRecordMap();
		RadioGroup rg = (RadioGroup) mainView.findViewById(R.id.RG_Common);
		View checkedView = rg.findViewById(rg.getCheckedRadioButtonId());
		if (checkedView != null) {
			int n = rg.indexOfChild(checkedView) + 1;
			if (n > 0)
				map.put("remit_method", String.valueOf(n));
		}

		rg = (RadioGroup) mainView.findViewById(R.id.RG_BusinessType);
		checkedView = rg.findViewById(rg.getCheckedRadioButtonId());
		if (checkedView != null) {
			int n = rg.indexOfChild(checkedView) + 1;
			if (n > 0)
				map.put("remit_type", String.valueOf(n));
		}
		return map;
	}
}
