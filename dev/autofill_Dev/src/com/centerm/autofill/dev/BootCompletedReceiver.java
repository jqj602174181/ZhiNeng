package com.centerm.autofill.dev;

import com.centerm.util.PinYin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if( intent.getAction().equals( Intent.ACTION_BOOT_COMPLETED)){
			initAppResource();
		}
	}
	
	//初始化一些应用程序资源
	private void initAppResource(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				PinYin.getPinYin( "中" );
			}
		});
		thread.run();
	}
}
