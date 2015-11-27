package com.centerm.autofill.update;

import android.app.Application;

public class ContextUtil extends Application {

	private static Application context;//应用程序的上下文
	
	/**
	 * 获取应用上下文实例
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
