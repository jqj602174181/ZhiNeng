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

//������Ҫ���µ�app
public class DownloadApp extends CommunicationBase implements CopyStreamListener{

	long fileSize = 0;
	private FTPDownloader ftpDownloader;

	public static String tempFolder = "autofill"+File.separator;
	public static final String sdcardDirectory = Environment.getExternalStorageDirectory().getPath() + File.separator;//SD��Ŀ¼
	
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
	
	//��ʼ��������
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
	//		handler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//�ļ�����ʧ��
			return;
		}
		if(!ftpDownloader.login(userName, password)){
			ftpDownloader.disconnect();//��¼ʧ�ܣ��Ͽ�����
		//	handler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//�ļ�����ʧ��
			return;
		}
		
		StringBuilder fileBuilder = new StringBuilder();
		fileBuilder.append(filepath);
		fileBuilder.append(File.separator);
		fileBuilder.append(name);
		fileSize = ftpDownloader.getFileSize(fileBuilder.toString());

		
		String localfile =downloadFloder+name;
		boolean isRet = false;
		if( ftpDownloader.getFile( fileBuilder.toString(), localfile, false)){//��ǰ���õ���ȫ�����أ�����м��ж��ˣ������¿�ʼ����						
			isRet = true;
		}
		ftpDownloader.disconnect();
	
		if(isRet&&validatePackage(md5,localfile)){
			boolean success =doUpdate(localfile);
			if(handler!=null){
				if(success){
					
		     		//handler.sendEmptyMessage(SocketCorrespondence.UPDATESUCCESS);//�����ɹ�
				}else{
					//handler.sendEmptyMessage(SocketCorrespondence.UPDATEFAIL);//����ʧ��
				}
			}
			
		}else{
			if(handler!=null)
				handler.sendEmptyMessage(SocketCorrespondence.DOWNFAIL);//�ļ�����ʧ��
		}
		File file = new File(localfile);
		if(file.exists()){//�����ǰ�װ����˻���û����ɣ��������ص��ļ�ɾ����
			file.delete();
		}
		
	}

	@Override
	public void bytesTransferred(CopyStreamEvent event) {
		bytesTransferred( event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
	}
	@Override
	public void bytesTransferred( long totalBytesTransferred, int bytesTransferred, long streamSize) {
		//���½���
//		int progress = (int)(totalBytesTransferred * 100/fileSize);
		//	handler.sendEmptyMessage(progress);
		//publishProgress( progress );
	}

	/**
	 * ��ʼ����
	 * @return
	 */
	public static  boolean doUpdate(String localfile){
		return installAppSilent(localfile);
	}	
	//��Ĭ��װapp
	public static  boolean installAppSilent(String apkFile){
		String [] args = {"pm", "install", "-r", apkFile};
		String result = AppSilent(args);
		if( result == null )
			return true;//���ɳɹ�
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
	//��Ĭж��app
	public static  boolean uninstallAppSilent(String packageName){
		String [] args = {"pm", "uninstall", packageName};//�������ó��򣬰�������������ȫ��ɾ��
		if( packageName.equals("com.centerm.autofill.setting")){
			args = new String[]{"pm", "uninstall", "-k", packageName};
		}
		String result = AppSilent(args);
		if( result == null )
			return true;//���ɳɹ�
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
	 * У���ļ���md5���Ƿ���ȷ
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
