//先暂时注释掉，没有用到
//package com.centerm.autofill.setting;
//
//import com.centerm.autofill.app.appframework.util.ShareprefrencesUitls;
//import com.centerm.autofill.app.appframework.util.UtilGlobalData;
//import com.centerm.autofill.setting.utils.ConfigUtil;
//import com.centerm.autofill.setting.utils.IPInputFilter;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.text.InputFilter;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
////基础设置
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.TextView;
//public class BaseSettingFragment extends Fragment implements View.OnClickListener{
//
//	private EditText edClientEdit;
//	private RadioButton raIsPublicRadio;
//	private RadioButton raIsPersionalRadio;
//	private RadioButton raIsFinancialRadio;
//	private Button okBtn;
//	private Activity activity;
//	private boolean isPublic;
//	private boolean isPersional;
//	private boolean isFinancial;
//	private ShareprefrencesUitls shareprefrencesUitls;
//	private String sUserName = "";
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate( R.layout.basesetting_fragment, container, false );
//		activity = getActivity();
//		edClientEdit = (EditText)view.findViewById(R.id.BaseSetting_EditText);
//		okBtn		= (Button)view.findViewById(R.id.BaseSetting_okBtn);
//		raIsFinancialRadio = (RadioButton)view.findViewById(R.id.BaseSeting_isFinancial);
//		raIsPersionalRadio = (RadioButton)view.findViewById(R.id.BaseSeting_isPersion);
//		raIsPublicRadio    = (RadioButton)view.findViewById(R.id.BaseSeting_isPublic);
//		okBtn.setOnClickListener(this);
//		raIsFinancialRadio.setOnClickListener(this);
//		raIsPersionalRadio.setOnClickListener(this);
//		raIsPublicRadio.setOnClickListener(this);
//		init();
//		return view;		
//	}
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		int id  = v.getId();
//		switch (id) {
//		case R.id.BaseSetting_okBtn:
//			sUserName = edClientEdit.getText().toString();
//			if(sUserName.length()==0){
//				return;
//			}
//			save();
//			break;
//		case R.id.BaseSeting_isFinancial:
//			isFinancial = !isFinancial;
//			raIsFinancialRadio.setChecked(isFinancial);
//			break;
//		case R.id.BaseSeting_isPersion:
//			isPersional = !isPersional;
//			raIsPersionalRadio.setChecked(isPersional);
//			break;
//		case R.id.BaseSeting_isPublic:
//			isPublic = !isPublic;
//			raIsPublicRadio.setChecked(isPublic);
//			break;
//		default:
//			break;
//		}
//	}
//	
//	private void init()
//	{
//		shareprefrencesUitls = new ShareprefrencesUitls(getActivity());
//		isPublic = shareprefrencesUitls.getShareDataBoolean(UtilGlobalData.isSettingPublicKey,true);
//		isFinancial = shareprefrencesUitls.getShareDataBoolean(UtilGlobalData.isSettingFinancialKey,true);
//		isPersional = shareprefrencesUitls.getShareDataBoolean(UtilGlobalData.isSettingPersionalKey,true);
//		sUserName   = shareprefrencesUitls.getShareData(UtilGlobalData.sSettingClientKey);
//		edClientEdit.setText(sUserName);
//		raIsFinancialRadio.setChecked(isFinancial);
//		raIsPersionalRadio.setChecked(isPersional);
//		raIsPublicRadio.setChecked(isPublic);
//		
//	}
//	
//	private void save()
//	{
//		shareprefrencesUitls.savedata(UtilGlobalData.sSettingClientKey, sUserName);
//		shareprefrencesUitls.saveShareDataBoolean(UtilGlobalData.isSettingPublicKey, isPublic);
//		shareprefrencesUitls.saveShareDataBoolean(UtilGlobalData.isSettingFinancialKey, isFinancial);
//		shareprefrencesUitls.saveShareDataBoolean(UtilGlobalData.isSettingPersionalKey, isPersional);
//	}
//}
