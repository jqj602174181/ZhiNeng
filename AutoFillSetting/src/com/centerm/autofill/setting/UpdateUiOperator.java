package com.centerm.autofill.setting;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UpdateUiOperator {

	private View mainView;
	private Activity activity;
	private Handler handler;;//用于UI线程同步

	private Button btnUpdatePrinter;
	private Button btnUpdate;				//执行更新按钮
	public final static int UPDATE_FINANCIAL		= 1;//升级金融模块
	public final static int UPDATE_PRINTER			= 2;//升级打印
	public final static int UPDATE_ALL				= 3;//升级打印与金融模块
//	private UpdataOperator updataOperator;
	public UpdateUiOperator(Activity activity,View view)
	{
		this.mainView = view;
		this.activity = activity;
	
	
		btnUpdate = (Button)view.findViewById( R.id.BTN_UPDATE );	
		btnUpdate.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
			
			//	updataOperator.startUpdateFirmwares();
				startUpdataActivity(UPDATE_FINANCIAL);
			}
		});
		
		btnUpdatePrinter = (Button)view.findViewById(R.id.BTN_UPDATE_Printer);
		btnUpdatePrinter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startUpdataActivity(UPDATE_PRINTER);
			}
		});
		
		Button btnUpdateApp = (Button)view.findViewById(R.id.BTN_UPDATE_App);
		btnUpdateApp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startUpdataActivity(UPDATE_PRINTER);
			}
		});
			
	}
	

	
	private void startUpdataActivity(int style)
	{
	//	final String name = "com.centerm.autofill.update.FirmwaresUpdateActivity";
		final String name="com.centerm.autofill.update.newupdate.NewUpdateActivity";
		final String pkg = "com.centerm.autofill.update";		
		try {
			// 启动重启的应用程序
			ComponentName appComponent = new ComponentName(pkg, name);
			Intent intent = new Intent();				
			intent.setComponent(appComponent);
			intent.putExtra( "style", style );
			activity.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//查询版本信息

	public void startQuery(){

		
	//	btnUpdate.setEnabled(false);
		//UpdataOperator.checkUpdateFirmwares(activity,SocketCorrespondence.UPDATE_PRINTER,handler);
	}
	
	public void setBtnUpdate(boolean is)
	{
		btnUpdate.setEnabled(is);
	}
	
	public void showUpdate(String date)
	{
		
		btnUpdate.setEnabled(true);
	}
}
