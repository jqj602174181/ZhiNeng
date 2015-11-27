package com.centerm.autofill.app.nmnx.common.agent;

import java.util.HashMap;
import java.util.Map;

import com.centerm.autofill.app.nmnx.common.R;
import com.centerm.autofill.dev.DevActivity;
import com.centerm.autofill.dev.IDCardMsg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//转出方身份证fragment
public class SpecialPersionFragment extends BaseIDBlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_main_agent);

		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		tipTitle = "请刷转帐转出方的身份证信息";
		setTransactorType(FROM_OTHER);
	}

	@Override
	protected void special() {
		super.special();

		mtvTitle.setText("转出方信息：");

		//显示代理人身份证按钮
		btnReadAgent.setVisibility(View.VISIBLE);
		btnReadAgent.setText("点击读取转出方二代证信息");

		if( tvPersonInfo != null ){
			tvPersonInfo.setVisibility(View.GONE);
		}
	}

	@Override
	protected boolean doSpecialValidate() {
		if(idCardMsg == null && isVisiable()){
			activity.showTip("转出方身份信息无效,请重新刷转出方身份证");
			return false;
		}

		if( idCardMsg != null ){
			activity.putsharedResource( "transfer_name", idCardMsg.getName());
		}

		return super.doSpecialValidate();
	}

	@Override
	protected void onOKReadIDCard() {
		super.onOKReadIDCard();

		//隐藏按钮
		btnReadAgent.setVisibility(View.GONE);
		if( tvPersonInfo != null ){
			tvPersonInfo.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void operatorIdCardInfo(int requestCode, int resultCode,
			Intent data) {

		//身份证信息不能与交易人、代理人相同
		IDCardMsg idCardMsg = DevActivity.GetIdCardMsg(requestCode, resultCode, data);
		if( idCardMsg != null ){
			String apply_name = (String)activity.getsharedResource( "apply_name" );
			//			String agent_name = (String)activity.getsharedResource( "agent_name" );
			String name = idCardMsg.getName();
			if( apply_name != null && name.equals(apply_name) ){
				activity.showTip( "转帐转出人与开户人不能是同一个人！请更换身份证后，重新操作。！" );
				return;
			}
			//有可能是代理人来办，并从代理人转帐，给本人开户
			/*if( agent_name != null && name.equals(agent_name) ){
				activity.showTip( "转帐转出人与代理人不能是同一个人！请更换身份证后，按下一步继续。！" );
				gotoLastStep();
				return;
			}*/
		}
		super.operatorIdCardInfo(requestCode, resultCode, data);
	}

	@Override
	protected HashMap<String, String> getRecordMap() {
		if(!this.isVisiable()){
			return null;
		}

		HashMap<String, String> map = new HashMap<String, String>();

		//填写可编辑的选项
		Map<String, String> editmap = super.getRecordMap();
		if( editmap != null){
			map.putAll(editmap);
		}		
		setIdentityCard(idCardMsg, map);
		return map;
	}

	/**
	 * 交易人要填 的证件信息
	 */
	@Override
	public void setIdentityCard(IDCardMsg idCardMsg, HashMap<String, String> map) {

		if(idCardMsg!=null){
			map.put( "special_name", idCardMsg.getName());
			map.put( "special_ID_type", "1");
			map.put( "special_ID_num", idCardMsg.getId_num());
			map.put( "special_police", idCardMsg.getSign_office() );
		}
	}
}
