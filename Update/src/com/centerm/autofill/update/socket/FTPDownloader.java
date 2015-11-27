package com.centerm.autofill.update.socket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamListener;

import android.util.Log;

//import android.util.Log;

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
	//	ConnectThread connectThread = null;
		//����
		try
        {
	//		connectThread = new ConnectThread();
	//		connectThread.start();
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
        	//	connectThread.interrupt();
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
		//	ConnectThread connectThread = new ConnectThread();
		//	connectThread.start();
		
			boolean res = ftp.login( user, password );
			ftp.setFileType( FTP.BINARY_FILE_TYPE );//�����ƴ���
			ftp.enterLocalPassiveMode();//����pasv����ģʽ
		
		//	connectThread.interrupt();
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
			return false;
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
	//ֱ�ӶϿ�
	public void  disconnect1(){
		 try {
			ftp.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * �����ļ����ؼ�����
	 */
	public void setCopyStreamListener( CopyStreamListener listener ){
		ftp.setCopyStreamListener(listener);
	}
	
	/**
	 * ���ӳ�ʱ���̣߳�5���û�������ϣ���������ʧ��
	 * @author fengzhenhai
	 *
	 */
	private class ConnectThread extends Thread{
		private int time = 5;
		private boolean isStop= false;
		public ConnectThread()
		{
			
		}
		
		public void run()
		{
			super.run();
			while (!isStop) {
				time--;
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(time==0){
					try {
						ftp.disconnect();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
				
			}
		}

		@Override
		public void interrupt() {
			// TODO Auto-generated method stub
			super.interrupt();
			isStop = true;
			
		}
		
		
	}
}
