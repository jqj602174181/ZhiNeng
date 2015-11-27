package com.centerm.autofill.appframework.common;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centerm.autofill.appframework.R;

public class PreviewDialog extends DialogFragment {

	
	Context mContext = null;
	int mImgId = 0;

	public void setImageId(int id) {
		mImgId = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//this.setCancelable(false);//�û���ȡ���������û����󣬵������û��Ч��
		return new PictureView(getActivity(), mImgId, this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���öԻ�����ʽ
		setStyle(DialogFragment.STYLE_NORMAL, R.style.previewStyle);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		getActivity().onBackPressed();//�˳���Ӧ��
	}
	
	
	
}
