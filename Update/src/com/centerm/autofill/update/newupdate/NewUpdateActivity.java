package com.centerm.autofill.update.newupdate;


import com.centerm.autofill.update.AutofillSettings;
import com.centerm.autofill.update.R;
import com.centerm.autofill.update.updateapp.FirmwaresUpdate;
import com.centerm.autofill.update.updateapp.SocketCorrespondence;
import com.centerm.autofill.update.updateapp.UpdateData;
import com.centerm.util.financial.FinancialUpdate;
import com.centerm.util.printer.PrinterService;

import android.R.integer;
import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
//com.centerm.autofill.update.newupdate��NewUpdateActivity
public class NewUpdateActivity extends Activity{

	private View relUpdateSystemRom;
	private View relUpdatePrinter;
	private View relUpdateFinance;
	private View relUpdateApp;
	private Handler mainhandler;
	
	private final static int UPDATE_SYSTEM 		= 3001;//ϵͳ�̼�
	private final static int UPDATE_PRINTER 	= 3002;//��ӡ
	private final static int UPDATE_FINANCE 	= 3003;//����ģ��
	private final static int UPDATE_APP			= 3004;//����app	

	private final static String VERSION_TAG = "version";
	AutofillSettings autofillSettings;
	private int currentStyl = UPDATE_SYSTEM;	
	private CheckUpdateThread checkUpdateThread;
	
	UpdateOperatorBase updateSystem;
	UpdateOperatorBase updatePrinter;
	UpdateOperatorBase updateFinance;
	UpdateOperatorBase currentUpdate;
	UpdateOperatorBase  updateAppOperator;
	private String 		unknowVersion ;
	private View mainView;
	
	private boolean[] isUpdateList={true,true,true,true};//���ڼ�¼����Щ������������Щ�������� 
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		mainView = getLayoutInflater().inflate(R.layout.activity_new_update, null);
		setContentView(mainView);
		relUpdateApp 		= findViewById(R.id.REL_UpdateApp);
		relUpdateFinance 	= findViewById(R.id.REL_UpdateFinance);
		relUpdatePrinter	= findViewById(R.id.REL_UpdatePrinter);
		relUpdateSystemRom	= findViewById(R.id.REL_UpdateSystemRom);
		unknowVersion 		= getResources().getString(R.string.unknow);
		autofillSettings 	= new AutofillSettings(getApplicationContext());
		
		setHandler();
		checkUpdateThread = new CheckUpdateThread();
		checkUpdateThread.start();
		
		
	}
	
	private void setHandler()
	{
		mainhandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				boolean is = true;

				switch (msg.what) {
					case SocketCorrespondence.NOUPDATE://��Ҫ����
						isUpdateList[currentStyl-UPDATE_SYSTEM] = false;
						currentUpdate.setHandler(msg);

					
						if(currentStyl==UPDATE_APP){
							/*
							 * app���������������������
							 */
							currentStyl = UPDATE_SYSTEM;
							checkStatus();
							startUpdate();
						}else{
							nextCheckUpdate();
						}
					
					
						
						break;
					case SocketCorrespondence.UPDATE://��⵽�������� 
					
						currentUpdate.setHandler(msg);
						
						if(currentStyl==UPDATE_APP){
							
							currentStyl = UPDATE_SYSTEM;
							checkStatus();
							startUpdate();
						}else{
				
							nextCheckUpdate();
						}
						break;
					case SocketCorrespondence.UPDATESUCCESS:
					//	if(	currentStyl == -1)return;
						currentUpdate.setHandler(msg);
						if(	currentStyl == -1)return;
						setNextStatus();
						startUpdate();
						break;
					case SocketCorrespondence.UPDATEFAIL://��������
						//
						currentUpdate.setHandler(msg);
						if(	currentStyl == -1)return;
					    setNextStatus();
						startUpdate();
						break;
					case SocketCorrespondence.DOWNFAIL:
					//	if(	currentStyl == -1)return;
						currentUpdate.setHandler(msg);//���س���
					//	 setNextStatus();
					//	startUpdate();
						break;
					case UPDATE_SYSTEM:
						
						currentStyl = UPDATE_SYSTEM;
					
						updateSystem 	= new UpdateSystemRom(NewUpdateActivity.this, mainView,autofillSettings.getServerIp(),mainhandler);
						currentUpdate = updateSystem;
						is = updateSystem.startCheckUpdate((String)msg.obj);
						if(!is){
							nextCheckUpdate();
						}else{
							FirmwaresUpdate firmwaresUpdate = new FirmwaresUpdate(checkUpdateThread.updateData, mainhandler);
							Thread thread = new Thread(firmwaresUpdate);
							thread.start();
						}
						isUpdateList[0]= is;
						break;
					case UPDATE_PRINTER:
					
						currentStyl = UPDATE_PRINTER;
						updatePrinter = new UpdatePrinterRom(NewUpdateActivity.this, mainView,autofillSettings.getServerIp(),mainhandler);
						currentUpdate = updatePrinter;
					
						is = updatePrinter.startCheckUpdate((String)msg.obj);
						if(!is){
							nextCheckUpdate();
							
						}else{
							FirmwaresUpdate firmwaresUpdate = new FirmwaresUpdate(checkUpdateThread.updateData, mainhandler);
							Thread thread = new Thread(firmwaresUpdate);
							thread.start();
						}
						isUpdateList[1]= is;
						break;
					case UPDATE_FINANCE:
						currentStyl = UPDATE_FINANCE;
						updateFinance = new UpdateFinanceRom(NewUpdateActivity.this, mainView,autofillSettings.getServerIp(),mainhandler);
						currentUpdate = updateFinance;
						is = updateFinance.startCheckUpdate((String)msg.obj);
	
						if(!is){
							nextCheckUpdate();
						}else{
							FirmwaresUpdate firmwaresUpdate = new FirmwaresUpdate(checkUpdateThread.updateData, mainhandler);
							Thread thread = new Thread(firmwaresUpdate);
							thread.start();
						}
						
						isUpdateList[2]= is;
						//����ģ������������˳���Ϣѭ��
						checkUpdateThread.quitLoop();
						break;

				  default:
					  if(currentUpdate!=null){
							currentUpdate.setHandler(msg);
					  }
					  
					  //nextCheckUpdate();
					break;
				}
				
			
			}
			
		};
	}
	
	private void checkStatus()
	{
		/*
		 * �ȼ����Щ��Ҫ������
		 */	
	
		if(currentStyl==-1)return;
		while(true)
		{
			if(currentStyl==UPDATE_APP||isUpdateList[currentStyl-UPDATE_SYSTEM]){
				
				break;
			}else{

				currentStyl++;
			}
		}
	
	
	}
	
	private void setNextStatus()
	{
		if(currentStyl==UPDATE_SYSTEM){
			currentStyl = UPDATE_PRINTER;
		}else if(currentStyl==UPDATE_PRINTER){
			currentStyl = UPDATE_FINANCE;
		}else if(currentStyl==UPDATE_FINANCE){
			currentStyl = UPDATE_APP;
		}else if(currentStyl==-1){
			return;
		}
		if(currentStyl==UPDATE_APP){
			currentStyl = -1;
			updateAppOperator.startFtpUpdate();
			currentUpdate =updateAppOperator;
		}else {
			checkStatus();
		}
		
	}
	private void  startUpdate()
	{
	
	


		switch (currentStyl) {
		case UPDATE_SYSTEM:

			updateSystem.startFtpUpdate();
			currentUpdate =updateSystem;
			break;
		case UPDATE_PRINTER:
			
			updatePrinter.startFtpUpdate();
			currentUpdate =updatePrinter;
			break;
		case UPDATE_FINANCE:
			updateFinance.startFtpUpdate();
			currentUpdate =updateFinance;
			break;
		case UPDATE_APP:
			currentStyl = -1;
			updateAppOperator.startFtpUpdate();
			currentUpdate =updateAppOperator;
			break;
		default:
			break;
		}
	}
	/*
	 * �z�y��һģ��
	 */
	private void nextCheckUpdate()
	{

		switch (currentStyl) {
		case UPDATE_SYSTEM://��ǰ��������System
			//��һ���Ǵ�ӡ
			currentStyl = UPDATE_PRINTER;
			checkUpdateThread.sendHandlerMsg(UPDATE_PRINTER);
			relUpdatePrinter.setVisibility(View.VISIBLE);
			break;
		case UPDATE_PRINTER://��ǰ�������Ǵ�ӡ����һ���ǽ���ģ��
			currentStyl = UPDATE_FINANCE;
			checkUpdateThread.sendHandlerMsg(UPDATE_FINANCE);
			relUpdateFinance.setVisibility(View.VISIBLE);
			break;
		case UPDATE_FINANCE:
			currentStyl = UPDATE_APP;
			relUpdateApp.setVisibility(View.VISIBLE);
			updateAppOperator = new UpdateAppOperator(mainView, this, autofillSettings.getDevNo(), autofillSettings.getServerIp(),autofillSettings.getServerPort() , mainhandler);
			updateAppOperator.startCheckUpdate("");
			currentUpdate = updateAppOperator;
			break;
		case UPDATE_APP://�����app����,��������system
	
			//currentStyl = UPDATE_SYSTEM;
			
			break;
		default:
			break;
		}
	}
	
	//���ڼ����Ƿ��и��µ��߳�
	private class CheckUpdateThread extends Thread{
		private Handler checkHandler;
		UpdateData updateData = new UpdateData();
		public CheckUpdateThread()
		{
			updateData.serial 	= autofillSettings.getDevNo();
			updateData.ip     	= autofillSettings.getServerIp();
			updateData.port	  	= autofillSettings.getServerPort();
			
		}
		public void run()
		{
			super.run();
			Looper.prepare();
			checkHandler = new Handler(){
				
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					
					updateData.version =unknowVersion;
					switch (msg.what) {
						case UPDATE_SYSTEM:
							currentStyl = UPDATE_SYSTEM;
							updateData.code="system";
							updateData.version = AutofillSettings.getSysVersion();
							break;
						case UPDATE_PRINTER:
							currentStyl = UPDATE_PRINTER;
							updateData.code="printer";
							PrinterService pt = PrinterService.getService(); // ��ȡ��ӡ������
							try {
								updateData.version = pt.getDevVersion();
								
							} catch (Exception e) {
								e.printStackTrace();
								
								
							}
							break;
						case UPDATE_FINANCE:
							currentStyl = UPDATE_FINANCE;
							FinancialUpdate finUpdate = FinancialUpdate.getService(); // ��ȡ����ģ��������������
							updateData.code="financial";
							try {
								updateData.version = finUpdate.getDevVersion();		
							} catch (Exception e) {
								e.printStackTrace();
							
							}
							break;
					default:
						break;
					}
					
					Message message = mainhandler.obtainMessage(msg.what, updateData.version);
					mainhandler.sendMessage(message);//������Ӧ�İ汾
					
					if(updateData.version.contains(unknowVersion)){//�汾δ֪�������,��Ҫ����
						
					}else{
						
					}
					
				}
			};
			checkHandler.sendEmptyMessage(UPDATE_SYSTEM);
			Looper.loop(); 
		}
		
		public void quitLoop()
		{
			if(checkHandler!=null)
			checkHandler.getLooper().quit();
		}
		
		public void sendHandlerMsg(int msg)
		{
			if(checkHandler!=null)
			checkHandler.sendEmptyMessage(msg);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		checkUpdateThread.quitLoop();
		
		if(updateSystem!=null){
			updateSystem.destroy();
		}
		if(updateAppOperator!=null){
			updateAppOperator.destroy();
		}
		
		if(updatePrinter!=null){
			updatePrinter.destroy();
		}
		
		if(updateFinance!=null){
			updateFinance.destroy();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
	}
	
	
}
