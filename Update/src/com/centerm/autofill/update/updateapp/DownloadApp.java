package com.centerm.autofill.update.updateapp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.os.Handler;
//import android.util.Log;

import com.centerm.autofill.update.socket.FTPDownloader;

//下载需要更新的app
public class DownloadApp extends CommunicationBase implements CopyStreamListener{

	long fileSize = 0;
	private FTPDownloader ftpDownloader;

	public static String tempFolder = "autofill"+File.separator;
	public static final String sdcardDirectory = Environment.getExternalStorageDirectory().getPath() + File.separator;//SD卡目录
	
	public  static final String downloadFloder = sdcardDirectory + tempFolder;
	public DownloadApp(String version,String packageName,String ip,int port,Handler handler)
	{
		super(ip, port, handler);
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("version", version.trim());
			jsonObject.put("packagename", packageName.trim());
			this.jsonStr = jsonObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		work = 0x5;
		type = 0x5;
		
	}
	
	public boolean operatorReadData(String data)
	{
		if(super.operatorReadData(data)){
			return true;
		}
		
		try {
			setDownLoad(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		return false;
	}
	
	//开始下载数据
	private void setDownLoad(String jsonStr) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(jsonStr);
		String port 		= jsonObject.getString("port");
		String userName 	= jsonObject.getString("username");
		String password 	= jsonObject.getString("password");
		String filepath		= jsonObject.getString("filepath");
		String md5			= jsonObject.getString("md5");
//		String packageName	= jsonObject.getString("packagename");
//		String version		= jsonObject.getString("version");
		String name			= jsonObject.getString("name");

		int ftpPort			= Integer.parseInt(port);
		ftpDownloader 		= new FTPDownloader();
		ftpDownloader.setCopyStreamListener(this);
		if(!ftpDownloader.connect(serviceIp, ftpPort))
		{
	//		handler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//文件下载失败
			return;
		}
		if(!ftpDownloader.login(userName, password)){
			ftpDownloader.disconnect();//登录失败，断开连接
		//	handler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//文件下载失败
			return;
		}
		
		StringBuilder fileBuilder = new StringBuilder();
		fileBuilder.append(filepath);
		fileBuilder.append(File.separator);
		fileBuilder.append(name);
		fileSize = ftpDownloader.getFileSize(fileBuilder.toString());

		
		String localfile =downloadFloder+name;
		boolean isRet = false;
		if( ftpDownloader.getFile( fileBuilder.toString(), localfile, false)){//当前采用的是全新下载，如果中间中断了，会重新开始下载						
			isRet = true;
		}
		ftpDownloader.disconnect();
	
		if(isRet&&validatePackage(md5,localfile)){
			boolean success =doUpdate(localfile);
			if(handler!=null){
				if(success){
					
		     		//handler.sendEmptyMessage(SocketCorrespondence.UPDATESUCCESS);//升级成功
				}else{
					//handler.sendEmptyMessage(SocketCorrespondence.UPDATEFAIL);//升级失败
				}
			}
			
		}else{
			if(handler!=null)
				handler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//文件下载失败
		}
		File file = new File(localfile);
		if(file.exists()){//不管是安装完成了还是没有完成，都把下载的文件删除了
			file.delete();
		}
		
	}

	@Override
	public void bytesTransferred(CopyStreamEvent event) {
		bytesTransferred( event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
	}
	@Override
	public void bytesTransferred( long totalBytesTransferred, int bytesTransferred, long streamSize) {
		//更新进度
//		int progress = (int)(totalBytesTransferred * 100/fileSize);
		//	handler.sendEmptyMessage(progress);
		//publishProgress( progress );
	}

	/**
	 * 开始升级
	 * @return
	 */
	public static  boolean doUpdate(String localfile){
		return installAppSilent(localfile);
	}	
	//静默安装app
	public static  boolean installAppSilent(String apkFile){
		String [] args = {"pm", "install", "-r", apkFile};
		String result = AppSilent(args);
		if( result == null )
			return true;//当成成功
		else{
			String lower = result.toLowerCase();
			if( lower.contains("success")){
				return true;
			}
			else{
				return false;
			}
		}
	}
	//静默卸载app
	public static  boolean uninstallAppSilent(String packageName){
		String [] args = {"pm", "uninstall", packageName};//除了设置程序，把所有已有数据全部删除
		if( packageName.equals("com.centerm.autofill.setting")){
			args = new String[]{"pm", "uninstall", "-k", packageName};
		}
		String result = AppSilent(args);
		if( result == null )
			return true;//当成成功
		else{
			String lower = result.toLowerCase();
			if( lower.contains("success")){
				return true;
			}
			else{
				return false;
			}
		}
	}
	
	public static String AppSilent(String[] args)
	{
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder (args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			int read = - 1;
			process = processBuilder.start ();
			errIs = process.getErrorStream ();
			while ((read = errIs.read ()) != - 1) {
				baos.write (read);
			}
			baos.write ('\n');
			
			inIs = process.getInputStream();
			while ((read = inIs.read ()) !=  -1) {
				baos.write (read);
			}
			
			byte [] data = baos.toByteArray ();			
			result = new String (data);
		} catch (IOException e) {
			e.printStackTrace ();
		} catch (Exception e) {
			e.printStackTrace ();
		}finally {
		try {
			if(errIs != null) {
				errIs.close ();
			}
			if (inIs != null) {
				inIs.close ();
			}
		}catch(IOException e) {
			e.printStackTrace ();
		}
		if (process != null) {
			process.destroy ();
		}
		}
		return result;
	}
	/**
	 * 校验文件的md5码是否正确
	 * @return
	 */
	public static boolean validatePackage(String md5,String localfile){
	
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
}
