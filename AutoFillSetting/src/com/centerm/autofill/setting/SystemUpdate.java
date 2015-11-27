package com.centerm.autofill.setting;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.net.io.CopyStreamListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.Environment;
import android.os.RecoverySystem;

import com.centerm.autofill.setting.utils.FileUtil;
/**
 * 执行系统更新操作
 *
 */
public class SystemUpdate {

	private String serverIp;//服务器ip
	private VersionInfo updateInfo;//待升级的版本信息
	
	//static静态配置
	private static final String serverUser = "E10";//ftp用户名
	private static final String serverPsd = "4001581515";//ftp用户密码
	private static final String versionFile = "versions.xml";//版本信息文件
	private static final String versionPackage = "update.zip";	//版本文件
	private static final String sdcardDirectory = Environment.getExternalStorageDirectory().getPath() + File.separator;//SD卡目录
	
	public SystemUpdate( String ip ){
		serverIp = ip;
		updateInfo = new VersionInfo();//生成一个无效的版本，表示无可更新的版本
	}
	
	//版本信息
	public class VersionInfo{
		public String version="";		//文件的版本号
		public String targetversion="";//只能在目标版本上升级
		public String device="";		//设备类型，总是E10
		public String build="";			//编译日期
		public String file="";			//远程文件路径
		public long size;				//文件大小
		public String md5;				//远程文件的校验码
		public String detail="";		//版本信息细节
		public boolean alive = false;	//是否有过更新
		
		//校验版本信息是否合法
		public boolean isValid(){
			return (!version.isEmpty()) && (!targetversion.isEmpty()) && size > 0 && (!md5.isEmpty())
					&& (!file.isEmpty()) &&  (device.equalsIgnoreCase("E10"));
		}
	}
	
	/**
	 * 远程查询版本信息
	 */
	public boolean query(){
		boolean ret = false;
		FTPDownloader ftp = new FTPDownloader();
		if( ftp.connect( serverIp ) ){//连接
			if( ftp.login( serverUser, serverPsd )){//登录
				String localfile = sdcardDirectory + versionFile;
				if( ftp.getFile( versionFile, localfile)){
					
					//解析版本信息，并查找是否有匹配项
					ArrayList<VersionInfo> versions = parseVersionInfoFile( localfile );
					String sysVersion = getSysVersion();
					for( int i = 0; i < versions.size(); i++ ){
						if( versions.get(i).targetversion.equals( sysVersion ) ){//记录版本信息
							updateInfo = versions.get(i);
							break;
						}
					}
					updateInfo.alive = true;//标记已通过正确查询
					ret = true;
				}
			}
			ftp.disconnect();//断开连接				
		}
		return ret;			
	}
	
	//获取可升级的版本信息，只有query执行成功后才可执行
	public VersionInfo getUpdateInfo(){
		return updateInfo;
	}
	
	/**
	 * 下载版本文件
	 */
	public boolean downloadVersion( CopyStreamListener listener ){
		boolean ret = false;
		if( updateInfo.isValid() ){			
			FTPDownloader ftp = new FTPDownloader();
			if( ftp.connect( serverIp ) ){//连接
				if( ftp.login( serverUser, serverPsd )){//登录
					if( listener != null ){
						ftp.setCopyStreamListener(listener);
					}
					String localfile = sdcardDirectory + versionPackage;
					
					if( ftp.getFile( updateInfo.file, localfile, false)){//当前采用的是全新下载，如果中间中断了，会重新开始下载						
						ret = true;
					}
				}
				ftp.disconnect();//断开连接				
			}
		}
		return ret;
	}	
	
	/**
	 * 校验文件的md5码是否正确
	 * @return
	 */
	public boolean validatePackage(){
		String localfile = sdcardDirectory + versionPackage;
		try {
			BufferedInputStream  fis = new BufferedInputStream(new FileInputStream(localfile), 4096 );;
			String md5 = new String(Hex.encodeHex(DigestUtils.md5( fis )));
			fis.close();
			return md5.equalsIgnoreCase( updateInfo.md5 );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 开始升级
	 * @return
	 */
	public boolean doUpdate(Context context){
		String localfile = sdcardDirectory + versionPackage;
		File sysFile = new File( localfile );
		try 
		{
			RecoverySystem.verifyPackage(sysFile, null, null); //校验文件是否正确
			RecoverySystem.installPackage(context, sysFile ); //启动安装系统文件
			return true;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		} 
		return false;
	}	
	
	public static boolean updateFromFile(Context context, String path){
		File sysFile = new File( path );
		try 
		{
			RecoverySystem.verifyPackage(sysFile, null, null); //校验文件是否正确
			if( path.startsWith("/mnt/usb_storage/")){//如果是U盘中的文件，必须转移至SD卡
				String localfile = sdcardDirectory + versionPackage;
				if( FileUtil.copyFile( path, localfile ))//拷贝文件成功
					sysFile = new File( localfile );
			}
			RecoverySystem.installPackage(context, sysFile ); //启动安装系统文件
			return true;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return false;
	}
	
	//删除升级的缓存文件
	public void deleteCache(){
		String localfile = sdcardDirectory + versionPackage;
		File sysFile = new File( localfile );
		sysFile.delete();
	}
	
	//获取系统当前版本号
	public static String getSysVersion(){
		
		return GetBuildProproperties( "ro.product.version");
	}
	
	// 获取build.prop中的指定属性， 从E10的oqc程序获得
	private  static String GetBuildProproperties( String PropertiesName ) {
		String ProperValue = "";
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = new BufferedInputStream(new FileInputStream(new File(
					"/system/build.prop")));
			br = new BufferedReader(new InputStreamReader(is));
			String strTemp = "";
			while ((strTemp = br.readLine()) != null) {
				// 如果文件没有读完则继续
				if (strTemp.indexOf(PropertiesName) != -1) {
					ProperValue = strTemp.substring(strTemp.indexOf("=") + 1);
					break;
				}
			}
			br.close();
			is.close();
		} catch (Exception e) {
			if (e.getMessage() != null)
				System.out.println(e.getMessage());
			else
				e.printStackTrace();
		}

		return ProperValue;
	}
	
	/**
	 * 解析版本信息文件
	 */
	private ArrayList<VersionInfo> parseVersionInfoFile( String path ){
		ArrayList<VersionInfo> versions = new ArrayList<VersionInfo>();
		try {
			//解析xml文件
			FileInputStream fileStream = new FileInputStream(path);//得到文件流
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();				
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse( fileStream  );
			
			NodeList updateNL = doc.getElementsByTagName( "Update" );//得到Update结点
			Element  element = null;
			
			//遍历update结点
			for( int i = 0; i < updateNL.getLength(); i++ )
			{
				element = (Element)updateNL.item( i );
				String name = element.getNodeName();
				if( name.equals( "Update" ) )
				{
					try {
						//解析update结点内容
						VersionInfo info = new VersionInfo();
						info.version = element.getAttribute( "version" );
						info.targetversion = element.getAttribute( "targetversion" );
						info.device = element.getAttribute( "device" );
						info.build = element.getAttribute( "build" );
						info.file = element.getAttribute( "file" );
						info.size = Long.parseLong(element.getAttribute( "size" ));
						info.md5 = element.getAttribute( "md5" );
						info.detail = element.getAttribute( "detail" );
						info.alive = true;
						
						if( info.isValid()){
							versions.add( info );
						}
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			}//end of for
			fileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versions;
	}
}
