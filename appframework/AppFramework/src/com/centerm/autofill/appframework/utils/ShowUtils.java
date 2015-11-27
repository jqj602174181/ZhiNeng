package com.centerm.autofill.appframework.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

//������ʾһЩ��ʾ��
public class ShowUtils {
	/**
	 * ������ʾ����
	 * @param strΪ��ʾ������
	 */
	public static void WarningDialog(String str,Context context)
	{
		WarningDialog(str, context,Gravity.CENTER);
	}
	
	/**
	 * ������ʾ����
	 * @param strΪ��ʾ������
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
