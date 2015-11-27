package com.centerm.autofill.dev.comm;


import com.centerm.autofill.dev.R;
import com.centerm.autofill.utils.AutofillSettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
//com.centerm.autofill.dev.DeviceRegisterReceiver
public class DeviceRegisterReceiver extends BroadcastReceiver{

	private String serviceIp;
	private int   port ;
	private static final String action = "com.centerm.autofill.DeviceRegister";

	private String serial;
	private String localIp;
	private String outlets;
	private String type ;
//	private String tag ="soc";
	private Handler handler;
	private Context context;
	
	private int registerFailCount = 0;//注册失败的次数，注册三次失败代表为失败
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(action)){
			this.context = context;
			 AutofillSettings conf	= new AutofillSettings(context);
			 setHandler();
			serviceIp 		= conf.getServerIp();
			port 			= conf.getServerPort();
			serial 			= conf.getDevNo();
			localIp 		= conf.getLocalIp();
			outlets			= conf.getOrgNO();
			type			= "E10-2";
			RegisterDev registerDev = new RegisterDev(serviceIp, port, handler, serial, localIp, outlets, type);
			registerDev.startTread();
	
		}
		
	}

	private void setHandler()
	{
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				SocketCorrespondence.handleMessage(handler, msg, context);
				
				if(msg.what==SocketCorrespondence.REGISTER_SUCCESS||msg.what==SocketCorrespondence.REGISTER_ALREADY){
				//	ChangeDeviceInfo changeDeviceInfo = new ChangeDeviceInfo(serviceIp, port, handler, serial, 1);//上线
				//	changeDeviceInfo.startTread();
				}else if(msg.what==SocketCorrespondence.REGISTER_ERROR)
				{
					if(registerFailCount==3){
						SocketCorrespondence.showTip(context, context.getResources().getString(R.string.registerError));
					}else{
						//重新再注册一次
						registerFailCount++;
						RegisterDev registerDev = new RegisterDev(serviceIp, port, handler, serial, localIp, outlets, type);
						registerDev.startTread();
					}
				}
			}
			
		};
	}
	
		
}
