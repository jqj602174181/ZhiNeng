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

//���������֤fragment
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
		tipTitle = "��ˢ�����˵����֤��Ϣ";
		setTransactorType(FROM_AGENT);
	}

	@Override
	protected void special() {
		super.special();

		mtvTitle.setText("��������Ϣ��");

		//��ʾ���������֤��ť
		btnReadAgent.setVisibility(View.VISIBLE);
		btnReadAgent.setText("�����ȡ�����˶���֤��Ϣ");

		if( tvPersonInfo != null ){
			tvPersonInfo.setVisibility(View.GONE);
		}
	}

	@Override
	protected boolean doSpecialValidate() {
		if(idCardMsg == null && isVisiable()){
			activity.showTip("�����������Ϣ��Ч,������ˢ���������֤");
			return false;
		}

		return super.doSpecialValidate();
	}

	@Override
	protected void onOKReadIDCard() {
		super.onOKReadIDCard();

		//���ذ�ť
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

		//��д�ɱ༭��ѡ��
		Map<String, String> editmap = super.getRecordMap();
		if( editmap != null){
			map.putAll(editmap);
		}		
		setIdentityCard(idCardMsg, map);
		return map;
	}

	/**
	 * �����˵Ķ�Ҫ�� ��֤����Ϣ
	 */
	@Override
	public void setIdentityCard(IDCardMsg idCardMsg, HashMap<String, String> map) {
		if(idCardMsg!=null){
			map.put("agent_ID_type", "1");
			map.put("agent_ID_num", idCardMsg.getId_num());
			map.put("agent_nation", idCardMsg.getNation_str());
			map.put("agent_name", idCardMsg.getName());
			map.put("agent_sex", idCardMsg.getSex().equals("��")?"1":"2");
			map.put( "agent_address", idCardMsg.getAddress());
			map.put( "agent_police", idCardMsg.getSign_office());

			map.put( "agent_ID_deadline_year", idCardMsg.getUseful_e_date_year());
			map.put( "agent_ID_deadline_month", idCardMsg.getUseful_e_date_month());
			map.put( "agent_ID_deadline_day", idCardMsg.getUseful_e_date_day());

			//			//�������ͺ͵�����
			//			if( idCardMsg.isLongTerm()){
			//				map.put( "agent_ID_deadline_type", "2" );
			//			}else{
			//				map.put( "agent_ID_deadline_type", "1" );
			//				map.put( "agent_ID_deadline", idCardInfo.getValidEndYYYYMMDD());
			//			}
		}
	}
}
