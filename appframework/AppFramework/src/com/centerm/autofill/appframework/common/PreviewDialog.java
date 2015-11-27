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
		
		//this.setCancelable(false);//用户可取消，否则用户点错后，点击后退没有效果
		return new PictureView(getActivity(), mImgId, this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置对话框样式
		setStyle(DialogFragment.STYLE_NORMAL, R.style.previewStyle);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		getActivity().onBackPressed();//退出该应用
	}
	
	
	
}
