package com.centerm.autofill.update;

import android.app.Application;

public class ContextUtil extends Application {

	private static Application context;//Ӧ�ó����������
	
	/**
	 * ��ȡӦ��������ʵ��
	 * @return
	 */
	public static Application getInstance()
	{
		return context;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
	}	
	
}
