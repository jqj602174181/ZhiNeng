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

//ת�������֤fragment
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
		tipTitle = "��ˢת��ת���������֤��Ϣ";
		setTransactorType(FROM_OTHER);
	}

	@Override
	protected void special() {
		super.special();

		mtvTitle.setText("ת������Ϣ��");

		//��ʾ���������֤��ť
		btnReadAgent.setVisibility(View.VISIBLE);
		btnReadAgent.setText("�����ȡת��������֤��Ϣ");

		if( tvPersonInfo != null ){
			tvPersonInfo.setVisibility(View.GONE);
		}
	}

	@Override
	protected boolean doSpecialValidate() {
		if(idCardMsg == null && isVisiable()){
			activity.showTip("ת���������Ϣ��Ч,������ˢת�������֤");
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

		//���ذ�ť
		btnReadAgent.setVisibility(View.GONE);
		if( tvPersonInfo != null ){
			tvPersonInfo.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void operatorIdCardInfo(int requestCode, int resultCode,
			Intent data) {

		//���֤��Ϣ�����뽻���ˡ���������ͬ
		IDCardMsg idCardMsg = DevActivity.GetIdCardMsg(requestCode, resultCode, data);
		if( idCardMsg != null ){
			String apply_name = (String)activity.getsharedResource( "apply_name" );
			//			String agent_name = (String)activity.getsharedResource( "agent_name" );
			String name = idCardMsg.getName();
			if( apply_name != null && name.equals(apply_name) ){
				activity.showTip( "ת��ת�����뿪���˲�����ͬһ���ˣ���������֤�����²�������" );
				return;
			}
			//�п����Ǵ��������죬���Ӵ�����ת�ʣ������˿���
			/*if( agent_name != null && name.equals(agent_name) ){
				activity.showTip( "ת��ת����������˲�����ͬһ���ˣ���������֤�󣬰���һ����������" );
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

		//��д�ɱ༭��ѡ��
		Map<String, String> editmap = super.getRecordMap();
		if( editmap != null){
			map.putAll(editmap);
		}		
		setIdentityCard(idCardMsg, map);
		return map;
	}

	/**
	 * ������Ҫ�� ��֤����Ϣ
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
