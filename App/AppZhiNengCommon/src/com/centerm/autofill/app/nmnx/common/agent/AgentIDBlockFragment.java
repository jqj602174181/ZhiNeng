package com.centerm.autofill.app.nmnx.common.agent;

import java.util.HashMap;
import java.util.Map;

import com.centerm.autofill.app.nmnx.common.R;
import com.centerm.autofill.dev.IDCardMsg;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//代理人身份证fragment
public class AgentIDBlockFragment extends BaseIDBlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState, R.layout.activity_main_agent);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		tipTitle = "请刷代理人的身份证信息";
		setTransactorType(FROM_AGENT);
	}

	@Override
	protected void special() {
		super.special();

		mtvTitle.setText("代理人信息：");

		//显示代理人身份证按钮
		btnReadAgent.setVisibility(View.VISIBLE);
		btnReadAgent.setText("点击读取代理人二代证信息");

		if( tvPersonInfo != null ){
			tvPersonInfo.setVisibility(View.GONE);
		}
	}

	@Override
	protected boolean doSpecialValidate() {
		if(idCardMsg == null && isVisiable()){
			activity.showTip("代理人身份信息无效,请重新刷代理人身份证");
			return false;
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
	 * 代理人的都要填 的证件信息
	 */
	@Override
	public void setIdentityCard(IDCardMsg idCardMsg, HashMap<String, String> map) {
		if(idCardMsg!=null){
			map.put("agent_ID_type", "1");
			map.put("agent_ID_num", idCardMsg.getId_num());
			map.put("agent_nation", idCardMsg.getNation_str());
			map.put("agent_name", idCardMsg.getName());
			map.put("agent_sex", idCardMsg.getSex().equals("男")?"1":"2");
			map.put( "agent_address", idCardMsg.getAddress());
			map.put( "agent_police", idCardMsg.getSign_office());

			map.put( "agent_ID_deadline_year", idCardMsg.getUseful_e_date_year());
			map.put( "agent_ID_deadline_month", idCardMsg.getUseful_e_date_month());
			map.put( "agent_ID_deadline_day", idCardMsg.getUseful_e_date_day());

			//			//期限类型和到期日
			//			if( idCardMsg.isLongTerm()){
			//				map.put( "agent_ID_deadline_type", "2" );
			//			}else{
			//				map.put( "agent_ID_deadline_type", "1" );
			//				map.put( "agent_ID_deadline", idCardInfo.getValidEndYYYYMMDD());
			//			}
		}
	}
}
