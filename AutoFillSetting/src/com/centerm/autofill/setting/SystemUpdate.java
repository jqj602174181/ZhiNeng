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
 * ִ��ϵͳ���²���
 *
 */
public class SystemUpdate {

	private String serverIp;//������ip
	private VersionInfo updateInfo;//�������İ汾��Ϣ
	
	//static��̬����
	private static final String serverUser = "E10";//ftp�û���
	private static final String serverPsd = "4001581515";//ftp�û�����
	private static final String versionFile = "versions.xml";//�汾��Ϣ�ļ�
	private static final String versionPackage = "update.zip";	//�汾�ļ�
	private static final String sdcardDirectory = Environment.getExternalStorageDirectory().getPath() + File.separator;//SD��Ŀ¼
	
	public SystemUpdate( String ip ){
		serverIp = ip;
		updateInfo = new VersionInfo();//����һ����Ч�İ汾����ʾ�޿ɸ��µİ汾
	}
	
	//�汾��Ϣ
	public class VersionInfo{
		public String version="";		//�ļ��İ汾��
		public String targetversion="";//ֻ����Ŀ��汾������
		public String device="";		//�豸���ͣ�����E10
		public String build="";			//��������
		public String file="";			//Զ���ļ�·��
		public long size;				//�ļ���С
		public String md5;				//Զ���ļ���У����
		public String detail="";		//�汾��Ϣϸ��
		public boolean alive = false;	//�Ƿ��й�����
		
		//У��汾��Ϣ�Ƿ�Ϸ�
		public boolean isValid(){
			return (!version.isEmpty()) && (!targetversion.isEmpty()) && size > 0 && (!md5.isEmpty())
					&& (!file.isEmpty()) &&  (device.equalsIgnoreCase("E10"));
		}
	}
	
	/**
	 * Զ�̲�ѯ�汾��Ϣ
	 */
	public boolean query(){
		boolean ret = false;
		FTPDownloader ftp = new FTPDownloader();
		if( ftp.connect( serverIp ) ){//����
			if( ftp.login( serverUser, serverPsd )){//��¼
				String localfile = sdcardDirectory + versionFile;
				if( ftp.getFile( versionFile, localfile)){
					
					//�����汾��Ϣ���������Ƿ���ƥ����
					ArrayList<VersionInfo> versions = parseVersionInfoFile( localfile );
					String sysVersion = getSysVersion();
					for( int i = 0; i < versions.size(); i++ ){
						if( versions.get(i).targetversion.equals( sysVersion ) ){//��¼�汾��Ϣ
							updateInfo = versions.get(i);
							break;
						}
					}
					updateInfo.alive = true;//�����ͨ����ȷ��ѯ
					ret = true;
				}
			}
			ftp.disconnect();//�Ͽ�����				
		}
		return ret;			
	}
	
	//��ȡ�������İ汾��Ϣ��ֻ��queryִ�гɹ���ſ�ִ��
	public VersionInfo getUpdateInfo(){
		return updateInfo;
	}
	
	/**
	 * ���ذ汾�ļ�
	 */
	public boolean downloadVersion( CopyStreamListener listener ){
		boolean ret = false;
		if( updateInfo.isValid() ){			
			FTPDownloader ftp = new FTPDownloader();
			if( ftp.connect( serverIp ) ){//����
				if( ftp.login( serverUser, serverPsd )){//��¼
					if( listener != null ){
						ftp.setCopyStreamListener(listener);
					}
					String localfile = sdcardDirectory + versionPackage;
					
					if( ftp.getFile( updateInfo.file, localfile, false)){//��ǰ���õ���ȫ�����أ�����м��ж��ˣ������¿�ʼ����						
						ret = true;
					}
				}
				ftp.disconnect();//�Ͽ�����				
			}
		}
		return ret;
	}	
	
	/**
	 * У���ļ���md5���Ƿ���ȷ
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
	 * ��ʼ����
	 * @return
	 */
	public boolean doUpdate(Context context){
		String localfile = sdcardDirectory + versionPackage;
		File sysFile = new File( localfile );
		try 
		{
			RecoverySystem.verifyPackage(sysFile, null, null); //У���ļ��Ƿ���ȷ
			RecoverySystem.installPackage(context, sysFile ); //������װϵͳ�ļ�
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
			RecoverySystem.verifyPackage(sysFile, null, null); //У���ļ��Ƿ���ȷ
			if( path.startsWith("/mnt/usb_storage/")){//�����U���е��ļ�������ת����SD��
				String localfile = sdcardDirectory + versionPackage;
				if( FileUtil.copyFile( path, localfile ))//�����ļ��ɹ�
					sysFile = new File( localfile );
			}
			RecoverySystem.installPackage(context, sysFile ); //������װϵͳ�ļ�
			return true;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return false;
	}
	
	//ɾ�������Ļ����ļ�
	public void deleteCache(){
		String localfile = sdcardDirectory + versionPackage;
		File sysFile = new File( localfile );
		sysFile.delete();
	}
	
	//��ȡϵͳ��ǰ�汾��
	public static String getSysVersion(){
		
		return GetBuildProproperties( "ro.product.version");
	}
	
	// ��ȡbuild.prop�е�ָ�����ԣ� ��E10��oqc������
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
				// ����ļ�û�ж��������
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
	 * �����汾��Ϣ�ļ�
	 */
	private ArrayList<VersionInfo> parseVersionInfoFile( String path ){
		ArrayList<VersionInfo> versions = new ArrayList<VersionInfo>();
		try {
			//����xml�ļ�
			FileInputStream fileStream = new FileInputStream(path);//�õ��ļ���
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();				
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse( fileStream  );
			
			NodeList updateNL = doc.getElementsByTagName( "Update" );//�õ�Update���
			Element  element = null;
			
			//����update���
			for( int i = 0; i < updateNL.getLength(); i++ )
			{
				element = (Element)updateNL.item( i );
				String name = element.getNodeName();
				if( name.equals( "Update" ) )
				{
					try {
						//����update�������
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
