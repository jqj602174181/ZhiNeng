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

//本人身份证fragment(可跳过)
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
			mtvTitle.setText("客户基本信息");
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

		//如果未设置指定的标题，则采用默认的标题
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
	 * idCardInfo为身份证信息
	 * 用于存放一些身份证公共的信息
	 * 如果有特殊情况,要进行重写
	 */
	public void setIdentityCard(IDCardMsg idCardMsg,HashMap<String, String> map)
	{
		super.setIdentityCard(idCardMsg, map);
		if( !isIdcardInfoValid && isEditInfoValid){//编辑身份证信息时只存放身份证号和姓名
			map.put("ID_num", editId);
			map.put("name", editName);
			map.put("ID_type", "1");	//固定返回公民身份证
			map.put( "police", editPolice );
		}
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


}
