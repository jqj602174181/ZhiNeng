package com.centerm.autofill.update;

//import com.centerm.autofill.update.dialog.UpdateOperator;
import com.centerm.autofill.update.newupdate.NewUpdateActivity;
import com.centerm.autofill.update.updateapp.SocketCorrespondence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;
//com.centerm.autofill.update.UpdateAppReciver
//app升级广播
public class UpdateReceiver extends BroadcastReceiver{
	public final static String updateAppAction = "com.centerm.autofill.update.updateApp";
	private final static String bootAction ="android.intent.action.BOOT_COMPLETED";
	
	public final static int MSG_EXCUTE_UPDATE = 1;//执行升级
	static Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if( msg.what == MSG_EXCUTE_UPDATE ){
				Context context =  ContextUtil.getInstance();
				Intent intent = new Intent();
				intent.putExtra("style", SocketCorrespondence.UPDATE_ALL );
				intent.setClass(context, NewUpdateActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 if(bootAction.equals(intent.getAction())){
		//	 AppUpdateService.startService(context);
			//TestActivity.startFirmwaresUpdateActivity(context, SocketCorrespondence.UPDATE_ALL);
			 handler.sendEmptyMessageDelayed( MSG_EXCUTE_UPDATE, 10000 );//10秒后开始启动，否则网络可能未正常初始化完成
		}else if(intent.getAction().equals(updateAppAction))
		{
			//UpdateOperator.appUpdate(context, null);
		}
	}

}
