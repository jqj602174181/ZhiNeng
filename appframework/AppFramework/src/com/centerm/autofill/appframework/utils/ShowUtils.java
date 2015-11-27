package com.centerm.autofill.appframework.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

//用于显示一些提示语
public class ShowUtils {
	/**
	 * 错误提示窗口
	 * @param str为提示的内容
	 */
	public static void WarningDialog(String str,Context context)
	{
		WarningDialog(str, context,Gravity.CENTER);
	}
	
	/**
	 * 错误提示窗口
	 * @param str为提示的内容
	 */
	public static void WarningDialog(String str,Context context,int gravity)
	{
		if(context!=null){
	
			Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
	 
			toast.setGravity(gravity,0,0);
			toast.show();

		}
	}
}
