package com.centerm.autofill.appframework.common;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.centerm.autofill.appframework.R;

public class WaitingDialog extends Dialog{

	private Context context;
	private View mainView;
	private TextView textView;
	private String text;
	private boolean isCanBack = false;
	/*
	 * text是要显示的提示语，isCan用来判断是否是模态窗口,true是,false不是
	 */
	public WaitingDialog(Context context,String text,boolean isCan)
	{
		super(context,R.style.custom_dialog_style);

		this.context = context;
		mainView = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_waiting, null);
		textView = (TextView)mainView.findViewById(R.id.dialog_waiting_name);
		LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(

				LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.FILL_PARENT);
	
		this.text = text;
		setContentView(mainView,fill_parent);
		isCanBack = !isCan;
		setCancelable(isCan);
		
		
		
	}
	
	
	
	public void setText(String text)
	{
		this.text = text;
		if(textView!=null)
			textView.setText(text);
	}
	
	public void showDialog()
	{
		if(!isShowing()){
			show();
		
		}
	}
	
	public void closeDialog()
	{
		if(isShowing()){
			dismiss();
			//indexThread.flag = false;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		if(isCanBack){
			closeDialog();
		}
		
	}
	
	
}

