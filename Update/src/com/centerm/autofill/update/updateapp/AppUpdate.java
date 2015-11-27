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

//检测app是否需要更新
public class AppUpdate extends CommunicationBase implements CopyStreamListener{

	long fileSize = 0;
	private FTPDownloader ftpDownloader;
	private final static String packagenameKey = "packagename";
	private final static String versionKey ="version";
	private Context context;
	
	//用于判定升级哪些应用
	public final static String autofillPackageName="com.centerm.autofill";
	//不升级的应用程序列表
	private final static String[] exceptPackages = {
		"com.centerm.autofill.update",
	};
	
	private final static String tag = "up";
	//不可卸载的应用程序列表
	private final static String[] sysAppPackages = {
		"com.centerm.autofill.setting",
		"com.centerm.autofill.update",
		"com.centerm.autofill.dev",
		"launch",//任何包含launch的程序都不能卸载
		"com.centerm.autofillformsystem"//湖北邮政老版本不能制裁
	};
	
//	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	private ArrayList<AppInfo> listApp;			//应用程序列表，可用来判断是否需要升级或卸载
//	private HashMap<String, Boolean> appHasInstallMap;//用于记录下已经升级过,没有升级的直接卸载，String为包名，Boolean若取值true表示有升级或无需升级
//	private HashMap<String, Integer> appInstallMap;//记录下已经安装的程序的包名,一个包名对应一个版本号
//	private boolean isUpdate = false;//代表有没有升级过
	private Handler updateHandler;
	public static final int MSG_UPDATE_RESULT = 998;		//app升级结果
	
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
		getAppPakeage();//先获取已经安装的内蒙古的包
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

		//进入循环,
		
		
		return true;
	}
	
	//从json包中解析升级信息
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
	
	//判断是否需要升级
	private boolean checkUpdate( String data ){
		//解析服务器应用信息
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
		//逐一安装或卸载应用
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
	

	//检测是否要升级
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
//			//服务器需要的应用挑选出来，标记为不要卸载
//			for(int i=0;i<size;i++){//for
//				JSONObject tempJsonObject = jsonArray.getJSONObject(i);
//				packageName = tempJsonObject.getString(packagenameKey);
//				versionCode = tempJsonObject.getInt(versionKey);
//				Integer tempVersion = appInstallMap.get(packageName);
//		
//				if( tempVersion == null ||(tempVersion!=null&&tempVersion<versionCode)){//代表需要升级					
//						
//					if(tempVersion!=null){
//						appHasInstallMap.put(packageName, true);//代表需要升级，不要卸载
//					}
//					isUpdate = true;
//				}else if(tempVersion!=null){
//					appHasInstallMap.put(packageName, true);//代表不需要升级，不要卸载
//				}
//			}//for
//			
//			Iterator< Map.Entry<String,Boolean>> iter = appHasInstallMap.entrySet().iterator();
//			while (iter.hasNext()) {
//			    Map.Entry<String,Boolean> entry = iter.next();
//			    boolean is  = (Boolean)entry.getValue();
//			    String tag = (String)entry.getKey();
//			    if( !is && appCanUninstall(tag)){//代表没有升级过，要卸载
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
//		//getAppPakeage();//先获取已经安装的内蒙古的包 //已经调用过，不再调用
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
//				//如果未安装或者版本过低，则进行升级
//				if( tempVersion == null ||(tempVersion!=null&&tempVersion<versionCode)){//代表需要升级
//					updatApp(tempJsonObject.toString());
//				}else if(tempVersion!=null){//如果已经最新的，记录不要卸载的标志
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
	//升级app
	private boolean updatApp(String jsonStr)
	{
		work = 0x5;
		type = 0x5;
		SocketClient client = new SocketClient();
		boolean is = client.SocketConnect(serviceIp, port, 5);
	
		if(!is){
			communicationStyle = SocketCorrespondence.CONNECT_ERROR;//连接失败
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
	
	//卸载应用程序
	private boolean uninstallApp( String pkgName ){
		boolean result = false;
		if( appCanUninstall( pkgName )){
			result = DownloadApp.uninstallAppSilent(pkgName);
		}
		
		String tip = "";
    	if( result ){
    		tip =  "卸载应用" + pkgName + "成功";
    	}else{
    		tip = "卸载应用" + pkgName + "失败";
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
//		    //没有升级过，且可卸载的需要卸载
//		    if( !is && appCanUninstall(packageName)){
//		    	isUpdate = true;
//		    	boolean result = DownloadApp.uninstallAppSilent(packageName);
//		    	String tip = "卸载应用" + packageName + "失败";
//		    	if( result )
//		    		tip =  "卸载应用" + packageName + "成功";
//		    	
//		    	Message msg = handler.obtainMessage( MSG_UPDATE_RESULT, tip );
//				handler.sendMessage( msg );
//		    }
//		}
//	}
	
	//应用程序相关信息
	class AppInfo{
		private int localVersionCode = -1;	//本地已安装的版本号
		private int remoteVersionCode = -1;//远程安装后的版本号
		private String packageName = "";		//包名
		
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
		
		//是否需要升级
		public boolean needUpdate(){
			if( localVersionCode == -1 //本地未安装
					|| ( remoteVersionCode > 0 && remoteVersionCode > localVersionCode ) ){//远程比本地版本更高
				return true;
			}
			return false;
		}
		
		
		public boolean needUninstall(){
			//排除不可卸载的程序
			if( appCanUninstall(packageName) && localVersionCode > 0 && remoteVersionCode == -1 )
				return true;
			
			return false;
		}
		
	}
	
	//把本地应用信息加入到listApp
	private void addLocalAppInfo( String pkgname, int versioncode ){
		//若已有，则只做更新
		for( int i = 0; i < listApp.size(); i++ ){
			AppInfo app = listApp.get(i);
			if( app.isSameApp( pkgname )){
				app.setLocalVersionCode( versioncode );
				return;
			}
		}
		
		//全新添加
		AppInfo newapp = new AppInfo();
		newapp.setLocalVersionCode( versioncode );
		newapp.setPackageName( pkgname );
		listApp.add( newapp );
	}
	
	//把远程应用信息加入到listApp
	private void addRemoteAppInfo( String pkgname, int versioncode ){
		//若已有，则只做更新
		for( int i = 0; i < listApp.size(); i++ ){
			AppInfo app = listApp.get(i);
			if( app.isSameApp( pkgname )){
				app.setRemoteVersionCode( versioncode );
				return;
			}
		}
		
		//全新添加
		AppInfo newapp = new AppInfo();
		newapp.setRemoteVersionCode( versioncode );
		newapp.setPackageName( pkgname );
		listApp.add( newapp );
	}
	
	//判断应用程序是否可以进行卸载
	private boolean appCanUninstall( String pkgname ){
		Log.e(tag,"pkgname is "+pkgname);
		for( int i = 0; i < sysAppPackages.length; i++ ){
			if( pkgname.contains( sysAppPackages[i] ))
				return false;
		}
		return true;
	}
	
	//判断应用程序是否可以进行远程升级
	private boolean appCanUpdate( String pkgname ){
		//包含基础包名
		if( !pkgname.contains(autofillPackageName)){
			return false;
		}
		
		//排除不可升级的程序
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
	
	//开始下载数据
	private boolean setDownLoad(String jsonStr) throws JSONException
	{
		//获取ftp下载信息
		JSONObject jsonObject = new JSONObject(jsonStr);
		String ip = jsonObject.optString( "ip", serviceIp);//如果有指定ip，则使用指定的ip，否则使用配置的ip
		String port 		= jsonObject.getString("port");
		String userName 	= jsonObject.getString("username");
		String password 	= jsonObject.getString("password");
		String filepath		= jsonObject.getString("filepath");
		String md5			= jsonObject.getString("md5");
//			String packageName	= jsonObject.getString("packagename");
//			String version		= jsonObject.getString("version");
		String name			= jsonObject.getString("name");
		int ftpPort			= Integer.parseInt(port);
		boolean retval = false;										//返回值
		String localfile 	= DownloadApp.downloadFloder + name;
		String tip = "下载应用" + name + "失败";
		
		//开始下载文件
		ftpDownloader 		= new FTPDownloader();
		ftpDownloader.setCopyStreamListener(this);
		//boolean isSuccess = false;
		if( ftpDownloader.connect(ip, ftpPort) ){
			if(ftpDownloader.login(userName, password)){//登录成功
			
				//得到远程文件路径和大小
				StringBuilder fileBuilder = new StringBuilder();
				fileBuilder.append(filepath);
				fileBuilder.append(File.separator);
				fileBuilder.append(name);
				fileSize = ftpDownloader.getFileSize(fileBuilder.toString());
				
				//下载文件
				if( fileSize > 0 ){	
					if( ftpDownloader.getFile( fileBuilder.toString(), localfile, false)){//当前采用的是全新下载，如果中间中断了，会重新开始下载						
						//先校验后安装
						if( DownloadApp.validatePackage(md5,localfile)){							
							if( DownloadApp.doUpdate(localfile) ){
								tip = "成功升级应用" + name ;
								retval = true;//下载并安装成功
							}else{
								tip = "安装应用" + name + "失败";
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
