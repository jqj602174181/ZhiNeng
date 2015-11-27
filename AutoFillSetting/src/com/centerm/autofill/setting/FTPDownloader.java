package com.centerm.autofill.setting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamListener;

/**
 * ftp�ļ�����
 *
 */
public class FTPDownloader {	

	FTPClient ftp;//�ͻ���
	
	public FTPDownloader(){
		//��ʼ��
		ftp = new FTPClient();	
		ftp.setBufferSize( 4096 );
		int timeOut = 10;
		 ftp.setDefaultTimeout(timeOut * 1000);
		 ftp.setConnectTimeout(timeOut * 1000);
		  ftp.setDataTimeout(timeOut * 1000);

	}
	
	/**
	 * ���ӷ����
	 */
	public boolean connect( String server ){
		return connect( server, 21 );
	}
	public boolean connect( String server, int port ){
		//��������Ӿ���������
		if( ftp.isConnected() ){
			try {
				ftp.disconnect();
			} catch (Exception e) {
			}				
		}
		
		//����
		try
        {
            int reply;
            if( port > 0 ){
                ftp.connect( server, port);
            }else{
                ftp.connect( server );
            }
            
            //�����Ӧ��
            reply = ftp.getReplyCode();
            if ( FTPReply.isPositiveCompletion(reply) ){//��������
            	ftp.setKeepAlive( true );
            	ftp.setControlKeepAliveTimeout( 120 );//120����
        		ftp.setConnectTimeout( 2000 );//�����2���ӣ��������������ϵĻ�����ʱ��ܾ�
        		ftp.setDataTimeout( 5000 );//5��δ�յ����ݰ���ֱ���ж�
        		ftp.setControlKeepAliveReplyTimeout( 5000 );
                return true;
            }else{//�ܾ�����
            	ftp.disconnect();
            }            
        }catch (IOException e){
            if( ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch (IOException f){
                    // do nothing
                }
            }
        }
		return false;//����ʧ��
	}
	
	/**
	 * ��¼ftp
	 */
	public boolean login( String user, String password ){
		//�����ȵ�¼
		if( !ftp.isConnected() )
			return false;
		
		try {
			//��¼�����ò���
			boolean res = ftp.login( user, password );
			ftp.setFileType( FTP.BINARY_FILE_TYPE );//�����ƴ���
			ftp.enterLocalPassiveMode();//����pasv����ģʽ
			return res;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * ��ȡԶ���ļ��Ĵ�С��Ϣ
	 * @param remotefile ���·��
	 * @return �ļ���С
	 */
	public long getFileSize( String remotefile ){
		try {
			FTPFile[] files = ftp.listFiles( remotefile );
			if( files.length > 0 )
				return files[0].getSize();
			else
				return 0;
		} catch (IOException e) {
			return 0;
		}
	}
	
	/**
	 * �����ļ�
	 * @param isResume �Ƿ�ϵ�����
	 */
	public boolean getFile( String remotefile, String localfile){
		return getFile( remotefile, localfile, false );
	}
	public boolean getFile( String remotefile, String localfile, boolean isResume ){
		
		//�ж�Զ���ļ��Ƿ����
		long filesize = getFileSize( remotefile );
		if( filesize == 0 )
			return false;
		
		//��ָ��ƫ������ʼ����
		File file = new File( localfile );
		if( isResume ){
			long offset = file.length();
			if( filesize <= offset ){//�ļ�������ɻ����ϴ�����ʧ�ܣ���Ҫ��������
				file.delete();
				offset = 0;
			}
			ftp.setRestartOffset( offset );
		}else{
			file.delete();//Ĭ��ֱ��ɾ��
		}
		
		//��ʼ����
		boolean ret = false;
		try {
			OutputStream output = new FileOutputStream( localfile, true );
			ret = ftp.retrieveFile( remotefile, output );
			output.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return ret;
	}	
	
	/**
	 * ����¼���Ͽ�����
	 */
	public void disconnect(){
		try {
			ftp.noop();
			ftp.logout();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally{
			if (ftp.isConnected()){
	            try{
	                ftp.disconnect();
	            }catch (IOException f){
	                // do nothing
	            }
	        }
		}
	}
	
	/**
	 * �����ļ����ؼ�����
	 */
	public void setCopyStreamListener( CopyStreamListener listener ){
		ftp.setCopyStreamListener(listener);
	}
}
