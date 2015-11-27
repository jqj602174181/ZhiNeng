package com.centerm.autofill.update.updateapp;

import java.io.File;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.centerm.autofill.update.socket.FTPDownloader;
import com.centerm.autofill.update.socket.SocketClient;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
//import android.util.Log;
import android.util.Log;

//���app�Ƿ���Ҫ����
public class AppUpdate extends CommunicationBase implements CopyStreamListener{

	long fileSize = 0;
	private FTPDownloader ftpDownloader;
	private final static String packagenameKey = "packagename";
	private final static String versionKey ="version";
	private Context context;
	
	//�����ж�������ЩӦ��
	public final static String autofillPackageName="com.centerm.autofill";
	//��������Ӧ�ó����б�
	private final static String[] exceptPackages = {
		"com.centerm.autofill.update",
	};
	
	private final static String tag = "up";
	//����ж�ص�Ӧ�ó����б�
	private final static String[] sysAppPackages = {
		"com.centerm.autofill.setting",
		"com.centerm.autofill.update",
		"com.centerm.autofill.dev",
		"launch",//�κΰ���launch�ĳ��򶼲���ж��
		"com.centerm.autofillformsystem"//���������ϰ汾�����Ʋ�
	};
	
//	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	private ArrayList<AppInfo> listApp;			//Ӧ�ó����б��������ж��Ƿ���Ҫ������ж��
//	private HashMap<String, Boolean> appHasInstallMap;//���ڼ�¼���Ѿ�������,û��������ֱ��ж�أ�StringΪ������Boolean��ȡֵtrue��ʾ����������������
//	private HashMap<String, Integer> appInstallMap;//��¼���Ѿ���װ�ĳ���İ���,һ��������Ӧһ���汾��
//	private boolean isUpdate = false;//������û��������
	private Handler updateHandler;
	public static final int MSG_UPDATE_RESULT = 998;		//app�������
	
	public AppUpdate(Context context,String serial,String ip,int port,Handler handler)
	{
		super(ip, port, handler);
		this.context = context;
		work = 5;
		type = 4;
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("serial", serial);
			jsonStr = jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		File file = new File(DownloadApp.downloadFloder);
		if(!file.exists()){
			file.mkdir();
		}
//		appInstallMap = new HashMap<String, Integer>();
//		appHasInstallMap = new HashMap<String, Boolean>();
		
		listApp = new ArrayList<AppInfo>();
	}

	@Override
	public boolean operatorReadData(String data) {
		if(super.operatorReadData(data)){
			return true;
		}
		getAppPakeage();//�Ȼ�ȡ�Ѿ���װ�����ɹŵİ�
		if(checkUpdate(data)){
		
			handler.sendEmptyMessage(SocketCorrespondence.UPDATE);
			Looper.prepare();
			updateHandler = new Handler() 
			{
				public void handleMessage(Message msg) 
				{
				// process incoming messages here
					super.handleMessage(msg);
			
					if(msg.what==1){
						if( excuteUpdate()){
		
							handler.sendEmptyMessage(SocketCorrespondence.UPDATESUCCESS);
						}else{
				
							handler.sendEmptyMessage(SocketCorrespondence.UPDATEFAIL );
						}
						//myHandleMessage(communicationData);
					}
				}
			};
			Looper.loop(); 
		}else{
			
			handler.sendEmptyMessage(SocketCorrespondence.NOUPDATE);
		}

		//����ѭ��,
		
		
		return true;
	}
	
	//��json���н���������Ϣ
	private boolean parseUpdateInfo( String data ){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(data);
			JSONArray jsonArray = jsonObject.getJSONArray("app");
			int size = jsonArray.length();
			String packageName="";
			int versionCode = 1;
			
			for( int i = 0; i < size; i++ ){
				JSONObject tempJsonObject = jsonArray.getJSONObject(i);
				packageName = tempJsonObject.getString(packagenameKey);
				versionCode = tempJsonObject.getInt(versionKey);
				addRemoteAppInfo( packageName, versionCode );
			}//for
			
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//�ж��Ƿ���Ҫ����
	private boolean checkUpdate( String data ){
		//����������Ӧ����Ϣ
		if( parseUpdateInfo( data )){
			for( int i = 0; i < listApp.size(); i++ ){
				AppInfo app = listApp.get( i );
				if( app.needUpdate() || app.needUninstall()){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean excuteUpdate( ){
		
		boolean retval = true;
		//��һ��װ��ж��Ӧ��
		for( int i = 0; i < listApp.size(); i++ ){
			AppInfo app = listApp.get( i );
			if( app.needUpdate() ){
				try {
					JSONObject obj = new JSONObject();
					obj.put( "packagename", app.getPackageName() );
					obj.put( "version", app.getRemoteVersionCode());
				
					boolean installres = updatApp( obj.toString() );
	
					if( !installres )
						retval = false;
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}else if( app.needUninstall()){
				boolean uninstallres =uninstallApp( app.getPackageName() );
				if( !uninstallres )
					retval = false;
			}
		}
		return retval;		
	}
	

	//����Ƿ�Ҫ����
//	private boolean checkUpdate(String data)
//	{
//	
//		JSONObject jsonObject;
//		try {
//			jsonObject = new JSONObject(data);
//			JSONArray jsonArray 	  = jsonObject.getJSONArray("app");
//			int size = jsonArray.length();
//			String packageName="";
//			int versionCode = 1;
//			boolean isNull = appInstallMap.isEmpty();
//			if(isNull)return true;
//			
//			//��������Ҫ��Ӧ����ѡ���������Ϊ��Ҫж��
//			for(int i=0;i<size;i++){//for
//				JSONObject tempJsonObject = jsonArray.getJSONObject(i);
//				packageName = tempJsonObject.getString(packagenameKey);
//				versionCode = tempJsonObject.getInt(versionKey);
//				Integer tempVersion = appInstallMap.get(packageName);
//		
//				if( tempVersion == null ||(tempVersion!=null&&tempVersion<versionCode)){//������Ҫ����					
//						
//					if(tempVersion!=null){
//						appHasInstallMap.put(packageName, true);//������Ҫ��������Ҫж��
//					}
//					isUpdate = true;
//				}else if(tempVersion!=null){
//					appHasInstallMap.put(packageName, true);//������Ҫ��������Ҫж��
//				}
//			}//for
//			
//			Iterator< Map.Entry<String,Boolean>> iter = appHasInstallMap.entrySet().iterator();
//			while (iter.hasNext()) {
//			    Map.Entry<String,Boolean> entry = iter.next();
//			    boolean is  = (Boolean)entry.getValue();
//			    String tag = (String)entry.getKey();
//			    if( !is && appCanUninstall(tag)){//����û����������Ҫж��
//			    	isUpdate = true;
//			    
//			    }
//			}
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return isUpdate;
//	}
	
	
	public void quitLoop()
	{
		if(updateHandler!=null)
			updateHandler.getLooper().quit();
	}
	
	
	
//	private void myHandleMessage(String data)
//	{
//		//getAppPakeage();//�Ȼ�ȡ�Ѿ���װ�����ɹŵİ� //�Ѿ����ù������ٵ���
//		JSONObject jsonObject;
//		try {
//			jsonObject = new JSONObject(data);
//			JSONArray jsonArray 	  = jsonObject.getJSONArray("app");
//			int size = jsonArray.length();
//			String packageName="";
//			int versionCode = 1;
////			boolean isNull = appInstallMap.isEmpty();
//			for(int i=0;i<size;i++){//for
//				JSONObject tempJsonObject = jsonArray.getJSONObject(i);
//				packageName = tempJsonObject.getString(packagenameKey);
//				versionCode = tempJsonObject.getInt(versionKey);
//				Integer tempVersion = appInstallMap.get(packageName);
//		
//				//���δ��װ���߰汾���ͣ����������
//				if( tempVersion == null ||(tempVersion!=null&&tempVersion<versionCode)){//������Ҫ����
//					updatApp(tempJsonObject.toString());
//				}else if(tempVersion!=null){//����Ѿ����µģ���¼��Ҫж�صı�־
//					appHasInstallMap.put(packageName, true);
//				}
//			}//for
//			uninstallApp();
//			if(isUpdate){
//				handler.sendEmptyMessage(SocketCorrespondence.UPDATESUCCESS);
//			}else{
//				//handler.sendEmptyMessage(SocketCorrespondence.NOUPDATE);
//			}
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
	//����app
	private boolean updatApp(String jsonStr)
	{
		work = 0x5;
		type = 0x5;
		SocketClient client = new SocketClient();
		boolean is = client.SocketConnect(serviceIp, port, 5);
	
		if(!is){
			communicationStyle = SocketCorrespondence.CONNECT_ERROR;//����ʧ��
			//operatorReadData(communicationData);

			return false;
		}
		
		try{
			is = sendData(jsonStr,client);			
		} catch (JSONException e) {
			e.printStackTrace();			
		} catch (Exception e) {
			e.printStackTrace();	
		}
		
		if(is){
			readData(client);
		}else{
			//client.SocketClose();
			//return false;
		}
		client.SocketClose();
		
		if( communicationStyle != SocketCorrespondence.OPERATOR_ERROR &&
			communicationStyle != SocketCorrespondence.COMM_ERROR ){
			try {
				setDownLoad(communicationData);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			//return false;
		}
		return true;
	}
	
	//ж��Ӧ�ó���
	private boolean uninstallApp( String pkgName ){
		boolean result = false;
		if( appCanUninstall( pkgName )){
			result = DownloadApp.uninstallAppSilent(pkgName);
		}
		
		String tip = "";
    	if( result ){
    		tip =  "ж��Ӧ��" + pkgName + "�ɹ�";
    	}else{
    		tip = "ж��Ӧ��" + pkgName + "ʧ��";
    	}    	
    	Message msg = handler.obtainMessage( MSG_UPDATE_RESULT, tip );
		handler.sendMessage( msg );
		
		return result;
	}
	
	
//	private void uninstallApp()
//	{
//		Iterator<Map.Entry<String,Boolean>> iter = appHasInstallMap.entrySet().iterator();
//		while (iter.hasNext()) {
//		    Map.Entry<String,Boolean> entry = (Map.Entry<String,Boolean>) iter.next();
//		    String packageName = (String)entry.getKey();
//		    boolean is  = (Boolean)entry.getValue();
//		    
//		    //û�����������ҿ�ж�ص���Ҫж��
//		    if( !is && appCanUninstall(packageName)){
//		    	isUpdate = true;
//		    	boolean result = DownloadApp.uninstallAppSilent(packageName);
//		    	String tip = "ж��Ӧ��" + packageName + "ʧ��";
//		    	if( result )
//		    		tip =  "ж��Ӧ��" + packageName + "�ɹ�";
//		    	
//		    	Message msg = handler.obtainMessage( MSG_UPDATE_RESULT, tip );
//				handler.sendMessage( msg );
//		    }
//		}
//	}
	
	//Ӧ�ó��������Ϣ
	class AppInfo{
		private int localVersionCode = -1;	//�����Ѱ�װ�İ汾��
		private int remoteVersionCode = -1;//Զ�̰�װ��İ汾��
		private String packageName = "";		//����
		
		AppInfo(){}
		public int getLocalVersionCode() {
			return localVersionCode;
		}
		public void setLocalVersionCode(int localVersionCode) {
			this.localVersionCode = localVersionCode;
		}
		public int getRemoteVersionCode() {
			return remoteVersionCode;
		}
		public void setRemoteVersionCode(int remoteVersionCode) {
			this.remoteVersionCode = remoteVersionCode;
		}
		public String getPackageName() {
			return packageName;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		
		public boolean isSameApp( String pkgname ){
			return pkgname.equals( packageName );
		}
		
		//�Ƿ���Ҫ����
		public boolean needUpdate(){
			if( localVersionCode == -1 //����δ��װ
					|| ( remoteVersionCode > 0 && remoteVersionCode > localVersionCode ) ){//Զ�̱ȱ��ذ汾����
				return true;
			}
			return false;
		}
		
		
		public boolean needUninstall(){
			//�ų�����ж�صĳ���
			if( appCanUninstall(packageName) && localVersionCode > 0 && remoteVersionCode == -1 )
				return true;
			
			return false;
		}
		
	}
	
	//�ѱ���Ӧ����Ϣ���뵽listApp
	private void addLocalAppInfo( String pkgname, int versioncode ){
		//�����У���ֻ������
		for( int i = 0; i < listApp.size(); i++ ){
			AppInfo app = listApp.get(i);
			if( app.isSameApp( pkgname )){
				app.setLocalVersionCode( versioncode );
				return;
			}
		}
		
		//ȫ�����
		AppInfo newapp = new AppInfo();
		newapp.setLocalVersionCode( versioncode );
		newapp.setPackageName( pkgname );
		listApp.add( newapp );
	}
	
	//��Զ��Ӧ����Ϣ���뵽listApp
	private void addRemoteAppInfo( String pkgname, int versioncode ){
		//�����У���ֻ������
		for( int i = 0; i < listApp.size(); i++ ){
			AppInfo app = listApp.get(i);
			if( app.isSameApp( pkgname )){
				app.setRemoteVersionCode( versioncode );
				return;
			}
		}
		
		//ȫ�����
		AppInfo newapp = new AppInfo();
		newapp.setRemoteVersionCode( versioncode );
		newapp.setPackageName( pkgname );
		listApp.add( newapp );
	}
	
	//�ж�Ӧ�ó����Ƿ���Խ���ж��
	private boolean appCanUninstall( String pkgname ){
		Log.e(tag,"pkgname is "+pkgname);
		for( int i = 0; i < sysAppPackages.length; i++ ){
			if( pkgname.contains( sysAppPackages[i] ))
				return false;
		}
		return true;
	}
	
	//�ж�Ӧ�ó����Ƿ���Խ���Զ������
	private boolean appCanUpdate( String pkgname ){
		//������������
		if( !pkgname.contains(autofillPackageName)){
			return false;
		}
		
		//�ų����������ĳ���
		for( int i = 0; i < exceptPackages.length; i++ ){
			if( pkgname.contains( exceptPackages[i] ))
				return false;
		}
		return true;
	}
	
	private void getAppPakeage()
	{
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
		String packageName = null;
		int versionCode = 1;
		for(int i=0;i<packages.size();i++){
			packageName = packages.get(i).packageName;
			versionCode = packages.get(i).versionCode;
			if( appCanUpdate(packageName) ){
				addLocalAppInfo( packageName, versionCode );
//				appInstallMap.put(packageName, versionCode);	
//				appHasInstallMap.put(packageName, false);
			}
		}
	}

	@Override
	public void bytesTransferred(CopyStreamEvent arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void bytesTransferred(long arg0, int arg1, long arg2) {
		// TODO Auto-generated method stub
		
	}
	
	//��ʼ��������
	private boolean setDownLoad(String jsonStr) throws JSONException
	{
		//��ȡftp������Ϣ
		JSONObject jsonObject = new JSONObject(jsonStr);
		String ip = jsonObject.optString( "ip", serviceIp);//�����ָ��ip����ʹ��ָ����ip������ʹ�����õ�ip
		String port 		= jsonObject.getString("port");
		String userName 	= jsonObject.getString("username");
		String password 	= jsonObject.getString("password");
		String filepath		= jsonObject.getString("filepath");
		String md5			= jsonObject.getString("md5");
//			String packageName	= jsonObject.getString("packagename");
//			String version		= jsonObject.getString("version");
		String name			= jsonObject.getString("name");
		int ftpPort			= Integer.parseInt(port);
		boolean retval = false;										//����ֵ
		String localfile 	= DownloadApp.downloadFloder + name;
		String tip = "����Ӧ��" + name + "ʧ��";
		
		//��ʼ�����ļ�
		ftpDownloader 		= new FTPDownloader();
		ftpDownloader.setCopyStreamListener(this);
		//boolean isSuccess = false;
		if( ftpDownloader.connect(ip, ftpPort) ){
			if(ftpDownloader.login(userName, password)){//��¼�ɹ�
			
				//�õ�Զ���ļ�·���ʹ�С
				StringBuilder fileBuilder = new StringBuilder();
				fileBuilder.append(filepath);
				fileBuilder.append(File.separator);
				fileBuilder.append(name);
				fileSize = ftpDownloader.getFileSize(fileBuilder.toString());
				
				//�����ļ�
				if( fileSize > 0 ){	
					if( ftpDownloader.getFile( fileBuilder.toString(), localfile, false)){//��ǰ���õ���ȫ�����أ�����м��ж��ˣ������¿�ʼ����						
						//��У���װ
						if( DownloadApp.validatePackage(md5,localfile)){							
							if( DownloadApp.doUpdate(localfile) ){
								tip = "�ɹ�����Ӧ��" + name ;
								retval = true;//���ز���װ�ɹ�
							}else{
								tip = "��װӦ��" + name + "ʧ��";
							}
													
						}
					}
				}
			}
			ftpDownloader.disconnect1();
		}		

		Message msg = handler.obtainMessage( MSG_UPDATE_RESULT, tip );
		handler.sendMessage( msg );	
		return retval;
	}
	
	public void startAppUpdate()
	{
		if(updateHandler!=null){
			Log.e(tag, "handeStart");
		 updateHandler.sendEmptyMessage(1);
		}
	}
	
	
}
