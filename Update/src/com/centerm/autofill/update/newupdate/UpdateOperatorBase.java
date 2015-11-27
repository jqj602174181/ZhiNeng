package com.centerm.autofill.update.newupdate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.json.JSONException;
import org.json.JSONObject;

import com.centerm.autofill.update.R;
import com.centerm.autofill.update.socket.FTPDownloader;
import com.centerm.autofill.update.updateapp.AppUpdate;
import com.centerm.autofill.update.updateapp.DownloadApp;
import com.centerm.autofill.update.updateapp.SocketCorrespondence;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

 class UpdateOperatorBase implements View.OnClickListener{
	 
	 	protected final static String versionTag = "version";
		protected NewUpdateActivity activity;
		protected View mainView ;
		protected ImageView imgUpdate;//升级的图标
		protected View imgUpdateWait;//升级等待的图片
		protected TextView tvCurrentVersion;
		protected TextView tvUPdateVersion;
		protected Button   btnUpdate;
		protected String   currentVersion	="";
		protected String   lastVersion 		="";
		protected String   romName			="系统固件";
		protected String   find 			= "发现";
		protected String   newVersion 		= "最新版本";
		protected String   updateVersion    = "";
		protected String   updating 		= "";
		protected String   downloading 	    = "";
		protected String   checkError    	= "";
		protected String   updateError		="升级失败";
		protected String connectError		="";
		protected String downloadError 		= "";
		private   String strClose;
		private   String clickClose;
		protected String strCheckUpdating;
		protected final static String space = "  ";
		protected CheckUpdating checkUpdating;
		
		protected String serviceIp;
		public final static int MSG_UPDATE_PROGRESS = 900;			//开始更新进度消息
		private final static int STEP_DOWNLOAD_PERCENT = 100;		//下载文件占总进度的进度值
		protected JSONObject updateJson;
		protected Handler updateHandler;
		private String 		unknowVersion ;
		protected boolean isCanUpdate = true;//是否可以升级
		protected final static int normalColor 	  = 0xff212121;
		protected final static int errorColor 	  = 0xffcc3333;
		protected final static int versionColor   = 0xffb9b9b9;
		protected final static int successColor   = 0xffa3a3a3;
		
		protected CloseTime closeTime = null;
		public UpdateOperatorBase(NewUpdateActivity activity,View mainView,String ip,Handler handler)
		{
			this.mainView 	= mainView;
			this.activity 	= activity;
			btnUpdate 	  	= (Button)mainView.findViewById(R.id.BTN_Update);
			currentVersion	   = activity.getString(R.string.currentVersion);
			strCheckUpdating   = activity.getString(R.string.chekcUpdating);
			lastVersion		   = activity.getString(R.string.lastVersion);
			find			   = activity.getString(R.string.find);
			newVersion		   = activity.getString(R.string.newVersion);
			downloading		   = activity.getString(R.string.downloading);
			updating		   = activity.getString(R.string.updating);
			connectError	   = activity.getString(R.string.connectError);
			checkError		   = activity.getString(R.string.checkError);
			unknowVersion 	   = activity.getString(R.string.unknow);
			updateError		   = activity.getString(R.string.updateError);
			strClose		   = activity.getString(R.string.close);
			clickClose		   = activity.getString(R.string.clickClose);
			downloadError	   = activity.getString(R.string.downloadError);
			serviceIp		   = ip;
			updateHandler	   = handler;
			initView();
		
		}
		
		protected void initView()
		{
			checkUpdating      = new CheckUpdating(tvUPdateVersion,strCheckUpdating);
		}
		protected void setHandler(Message msg)
		{
			checkUpdating.stopRun();
			switch (msg.what) {
			case SocketCorrespondence.UPDATESUCCESS://升级成功

				tvUPdateVersion.setTextColor(versionColor);
				tvUPdateVersion.setText(lastVersion);
				cancelRoateAnim();
				updateSuccessOver();
				break;
			case SocketCorrespondence.UPDATEFAIL://升级失败
		
				updateFailOver();
				break;
			case SocketCorrespondence.DOWNFAIL://下载失败要退出
	
				cancelRoateAnim();
				setErrorText(romName+space+downloadError);
			//	tvUPdateVersion.setText();
			
				break;
			case SocketCorrespondence.PROTOCOL_ERROR:
				
				break;
			case MSG_UPDATE_PROGRESS://更新进度
				
				
				break;
			case SocketCorrespondence.OPERATOR_ERROR:
				setErrorText(checkError);
				break;
			case SocketCorrespondence.CONNECT_ERROR:
				setErrorText(connectError);
				break;
			case SocketCorrespondence.COMM_ERROR:
				setErrorText(checkError);
				break;
			case SocketCorrespondence.NOUPDATE://不要升级
				tvUPdateVersion.setTextColor(versionColor);
				tvUPdateVersion.setText(lastVersion);
				isCanUpdate = false;
				break;
			case SocketCorrespondence.UPDATE://升级 
				setUpdateState((String)(msg.obj));
				break;
			case AppUpdate.MSG_UPDATE_RESULT:
				break;
			default:
				
				StringBuilder builder = new StringBuilder();
				
			
				
				if(msg.what==100){
					builder.append(updating);
					builder.append(space);
					builder.append(romName);
					builder.append(space);
					builder.append(updateVersion);
					 setRoateAnim();
				}else{
					builder.append(downloading);
					builder.append(space);
					builder.append(romName);
					builder.append(space);
					builder.append(updateVersion);
					builder.append(space);
					builder.append("(");
					builder.append(msg.what);
					builder.append("%");
					builder.append(")");
				}
				btnUpdate.setText(builder.toString());
				break;
			}
		}
		
		public boolean startCheckUpdate(String version){
		
			if(version.contains(unknowVersion)){//如果版本未知,提示无法升级
				tvUPdateVersion.setText(activity.getString(R.string.canUpdate));
				isCanUpdate = false;
				return false;
			}
			tvCurrentVersion.setText(romName+space+currentVersion+space+version);
			checkUpdating.startRun();
			return true;
		}
		
	
		protected void setUpdateState(String jsonStr)
		{
			try {
				tvUPdateVersion.setTextColor(normalColor);
				updateJson = new JSONObject(jsonStr);
				tvUPdateVersion.setText(find+space+romName+space+newVersion+space+updateJson.get(versionTag));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		/**
		 * 开始升级
		 * @return
		 */
		public boolean doUpdate(Context context,String localfile){
			
			File sysFile = new File( localfile );
			try 
			{
				RecoverySystem.verifyPackage(sysFile, null, null); //校验文件是否正确
				RecoverySystem.installPackage(context, sysFile ); //启动安装系统文件
				return true;
			}catch (java.security.SignatureException e){
				
			}
			catch( Exception e )
			{
				e.printStackTrace();
			} 
			return false;
		}	
		class UpdateFtp extends Thread implements CopyStreamListener{
			long fileSize = 0;
			
			private FTPDownloader ftpDownloader;
			private JSONObject jsonObject;
			public UpdateFtp(JSONObject jsonObject) {
				// TODO Auto-generated constructor stub
				this.jsonObject = jsonObject;
			}
			@Override
			public void run() {

				try {
					setDownLoad();
				} catch (JSONException e) {
					e.printStackTrace();
					updateHandler.sendEmptyMessage(SocketCorrespondence.UPDATEFAIL);
				}
			}
			
			//开始下载数据
			private void setDownLoad() throws JSONException
			{
				//生成下载文件信息

				String ip			= jsonObject.optString( "ip", serviceIp );//若有ip元素，采用ip的值
				String port 		= jsonObject.getString("port");
				String userName 	= jsonObject.getString("username");
				String password 	= jsonObject.getString("password");
				String filepath		= jsonObject.getString("filepath");
				String md5			= jsonObject.getString("md5");
//				String code			= jsonObject.getString("code");
				updateVersion		= jsonObject.getString("version");
				String name			= jsonObject.getString("name");	
				int ftpPort			= Integer.parseInt(port);
				
				//下载文件 
				ftpDownloader 		= new FTPDownloader();
				ftpDownloader.setCopyStreamListener(this);
				if(!ftpDownloader.connect(ip, ftpPort))
				{			
					updateHandler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//文件下载失败
					return;
				}
				if(!ftpDownloader.login(userName, password)){
					ftpDownloader.disconnect1();//断开连接
					updateHandler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//文件下载失败
					return;
				}
			
				StringBuilder fileBuilder = new StringBuilder();
				fileBuilder.append(filepath);
				fileBuilder.append("/");
				fileBuilder.append(name);
				fileSize = ftpDownloader.getFileSize(fileBuilder.toString());	
				
				//创建/mnt/sdcard/autofill文件夹
				File file = new File(DownloadApp.downloadFloder);
				if(!file.exists()){
					file.mkdir();
				}
				
				String localfile = DownloadApp.downloadFloder + name;
				boolean isRet = false;
				
				if( ftpDownloader.getFile( fileBuilder.toString(), localfile, false)){//当前采用的是全新下载，如果中间中断了，会重新开始下载						
					isRet = true;
				}
			
				ftpDownloader.disconnect1();
			
				if(isRet&&validatePackage(md5,localfile)){
					boolean success =doUpdate(activity,localfile);
					if(success){
						updateHandler.sendEmptyMessage(SocketCorrespondence.UPDATESUCCESS);//升级成功
					}else{
						
						updateHandler.sendEmptyMessage(SocketCorrespondence.UPDATEFAIL);//升级失败
					}
				}else{
				
					updateHandler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//升级失败
				}
				
			}

			@Override
			public void bytesTransferred(CopyStreamEvent event) {

				bytesTransferred( event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
			}
			
		
			
			/**
			 * 校验文件的md5码是否正确
			 * @return
			 */
			public boolean validatePackage(String md5,String localfile){
				//String localfile = sdcardDirectory + versionPackage;
				try {
					BufferedInputStream  fis = new BufferedInputStream(new FileInputStream(localfile), 4096 );;
					String tempMd5 = new String(Hex.encodeHex(DigestUtils.md5( fis )));
					fis.close();
					return tempMd5.equalsIgnoreCase( md5 );
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			public void bytesTransferred( long totalBytesTransferred, int bytesTransferred, long streamSize) {

				//更新进度
				int progress = (int)(totalBytesTransferred  *STEP_DOWNLOAD_PERCENT/fileSize);
				updateHandler.sendEmptyMessage(progress);
				//publishProgress( progress );
			}
		}
		
		public void startFtpUpdate()
		{
	
			if(!isCanUpdate){
				return;
			}
			btnUpdate.setVisibility(View.VISIBLE);
			StringBuilder builder = new StringBuilder();
			builder.append(downloading);
			builder.append(space);
			builder.append(romName);
			btnUpdate.setText(builder.toString());
	
		
			UpdateFtp updateFtp = new UpdateFtp(updateJson);
			updateFtp.start();
		}
		
		protected void setRoateAnim()
		{
			Animation anim = AnimationUtils.loadAnimation(activity, R.anim.roate); 
			imgUpdateWait.setAnimation(anim);
			imgUpdateWait.setVisibility(View.VISIBLE);
		}
		
		protected void cancelRoateAnim()
		{
			imgUpdateWait.setVisibility(View.GONE);
			imgUpdateWait.setAnimation(null);
			
		}
		//升级出错，结束的提示
		protected void updateErrorOver(String errorText)
		{
			btnUpdate.setVisibility(View.VISIBLE);
			btnUpdate.setEnabled(true);
			btnUpdate.setOnClickListener(this);
			if(closeTime==null){
				closeTime = new CloseTime(errorText, errorColor);
			}
			closeTime.startRun();
		}
		//升级成功，结束的提示，这里子类可以重写
		protected void updateSuccessOver()
		{
			
		}
		//升级失败，结束的提示，这里子类可以重写
		protected void updateFailOver()
		{
			cancelRoateAnim();
			tvUPdateVersion.setTextColor(errorColor);
			tvUPdateVersion.setText(romName+space+updateError);
		}
		//升级过程中出错的提示
		protected void setErrorText(String text)
		{
			cancelRoateAnim();
			tvUPdateVersion.setTextColor(errorColor);
			tvUPdateVersion.setText(text);
			updateErrorOver(text);
		}
		
		public void destroy()
		{
			if(closeTime!=null){
				closeTime.stopRun();
			}
		}
	 class CloseTime implements Runnable{
			private Handler handler;
			private int time = 10;
			private final static int delayTime = 1000;
		
			private int textColor = normalColor;
			private boolean isCancelAnim = false;
			private String errorText;
			public CloseTime(String errorText,int textColor)
			{
				handler = new Handler();
				this.textColor = textColor;
				this.errorText = errorText;
				btnUpdate.setTextColor(textColor);
				setText(time);
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(!isCancelAnim){
					cancelRoateAnim();
					isCancelAnim = true;
				}
			
				handler.postDelayed(this, delayTime);
				setText(time);
				if(time==0){
					handler.postDelayed(this, delayTime);
					activity.finish();
					return;
				}
				time--;
				
			}
			private void setText(int time)
			{
				StringBuilder builder = new StringBuilder();
				builder.append(errorText);
				builder.append(",");
				builder.append(time);
				builder.append(strClose);
				builder.append("(");
				builder.append(clickClose);
				builder.append(")");
				
				btnUpdate.setText(builder.toString());
				
			}
			
			public void startRun()
			{
			
				//cancelRoateAnim();
				handler.postDelayed(this, delayTime);
			
			}
			
			public void stopRun()
			{
				handler.removeCallbacks(this);
			}
			
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if(id==R.id.BTN_Update){
				activity.finish();
			}
		}
}
