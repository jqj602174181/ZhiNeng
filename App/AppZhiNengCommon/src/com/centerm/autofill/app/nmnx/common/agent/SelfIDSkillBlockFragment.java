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

//�������֤fragment(������)
public class SelfIDSkillBlockFragment extends BaseIDBlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState, int layoutid) {
		View view = super.onCreateView(inflater, container, savedInstanceState, layoutid);

		return view;
	}

	@Override
	protected void special() {
		super.special();

		if(mtvTitle!=null){
			mtvTitle.setText("�ͻ�������Ϣ");
		}
		if(btnReadAgent!=null){
			btnReadAgent.setVisibility(View.GONE);
		}
		if(btnReadSelf!=null){
			btnReadSelf.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		//���δ����ָ���ı��⣬�����Ĭ�ϵı���
		if( tipTitle == null || tipTitle.length() == 0 ){
			tipTitle = getString(R.string.readIDCardOfApplicant);
		}

		if( isAutoRun && bAfterInit == false && (!isIDCardInfoValid() && !isIDCardInfoEditValid()) ){
			enableReadIDCardSkip();
			startReadIDCard( tipTitle,null, FROM_SELF );
			bAfterInit = true;
		}else{

		}
	}

	/*
	 * idCardInfoΪ���֤��Ϣ
	 * ���ڴ��һЩ���֤��������Ϣ
	 * ������������,Ҫ������д
	 */
	public void setIdentityCard(IDCardMsg idCardMsg,HashMap<String, String> map)
	{
		super.setIdentityCard(idCardMsg, map);
		if( !isIdcardInfoValid && isEditInfoValid){//�༭���֤��Ϣʱֻ������֤�ź�����
			map.put("ID_num", editId);
			map.put("name", editName);
			map.put("ID_type", "1");	//�̶����ع������֤
			map.put( "police", editPolice );
		}
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


}
