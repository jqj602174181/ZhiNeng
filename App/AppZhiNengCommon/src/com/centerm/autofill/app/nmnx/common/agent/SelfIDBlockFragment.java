package com.centerm.autofill.app.nmnx.common.agent;

import java.util.HashMap;
import java.util.Map;

import com.centerm.autofill.app.nmnx.common.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//�������֤fragment(��������)
public class SelfIDBlockFragment extends BaseIDBlockFragment {

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
		if(tvPersonInfo!=null){
			tvPersonInfo.setVisibility(View.GONE);
		}
		if(imgViewPhoto!=null){
			imgViewPhoto.setVisibility(View.GONE);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.startReadIDCard(getString(R.string.readIDCardOfOpenAccount), null, FROM_SELF);
	}

	/*
//	 * idCardInfoΪ���֤��Ϣ
//	 * ���ڴ��һЩ���֤��������Ϣ
//	 * ������������,Ҫ������д
//	 */
	//	public void setIdentityCard(IDCardMsg idCardMsg,HashMap<String, String> map)
	//	{
	//		super.setIdentityCard(idCardMsg, map);
	//		if( !isIdcardInfoValid && isEditInfoValid){//�༭���֤��Ϣʱֻ������֤�ź�����
	//			map.put("ID_num", editId);
	//			map.put("name", editName);
	//			map.put("ID_type", "1");	//�̶����ع������֤
	//			map.put( "police", editPolice );
	//		}
	//	}

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
