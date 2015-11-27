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
	
	//��ʼ��һЩӦ�ó�����Դ
	private void initAppResource(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				PinYin.getPinYin( "��" );
			}
		});
		thread.run();
	}
}
