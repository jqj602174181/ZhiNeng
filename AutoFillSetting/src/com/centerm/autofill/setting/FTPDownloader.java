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
 * ftp文件下载
 *
 */
public class FTPDownloader {	

	FTPClient ftp;//客户端
	
	public FTPDownloader(){
		//初始化
		ftp = new FTPClient();	
		ftp.setBufferSize( 4096 );
		int timeOut = 10;
		 ftp.setDefaultTimeout(timeOut * 1000);
		 ftp.setConnectTimeout(timeOut * 1000);
		  ftp.setDataTimeout(timeOut * 1000);

	}
	
	/**
	 * 连接服务端
	 */
	public boolean connect( String server ){
		return connect( server, 21 );
	}
	public boolean connect( String server, int port ){
		//如果已连接就重新连接
		if( ftp.isConnected() ){
			try {
				ftp.disconnect();
			} catch (Exception e) {
			}				
		}
		
		//连接
		try
        {
            int reply;
            if( port > 0 ){
                ftp.connect( server, port);
            }else{
                ftp.connect( server );
            }
            
            //检测响应码
            reply = ftp.getReplyCode();
            if ( FTPReply.isPositiveCompletion(reply) ){//连接正常
            	ftp.setKeepAlive( true );
            	ftp.setControlKeepAliveTimeout( 120 );//120秒钟
        		ftp.setConnectTimeout( 2000 );//最长连接2秒钟，否则服务端连不上的话，超时会很久
        		ftp.setDataTimeout( 5000 );//5秒未收到数据包后，直接中断
        		ftp.setControlKeepAliveReplyTimeout( 5000 );
                return true;
            }else{//拒绝连接
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
		return false;//连接失败
	}
	
	/**
	 * 登录ftp
	 */
	public boolean login( String user, String password ){
		//必须先登录
		if( !ftp.isConnected() )
			return false;
		
		try {
			//登录并设置参数
			boolean res = ftp.login( user, password );
			ftp.setFileType( FTP.BINARY_FILE_TYPE );//二进制传输
			ftp.enterLocalPassiveMode();//采用pasv被动模式
			return res;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * 获取远程文件的大小信息
	 * @param remotefile 相对路径
	 * @return 文件大小
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
	 * 下载文件
	 * @param isResume 是否断点续传
	 */
	public boolean getFile( String remotefile, String localfile){
		return getFile( remotefile, localfile, false );
	}
	public boolean getFile( String remotefile, String localfile, boolean isResume ){
		
		//判断远程文件是否存在
		long filesize = getFileSize( remotefile );
		if( filesize == 0 )
			return false;
		
		//从指定偏移量开始下载
		File file = new File( localfile );
		if( isResume ){
			long offset = file.length();
			if( filesize <= offset ){//文件下载完成或者上次上载失败，需要重新下载
				file.delete();
				offset = 0;
			}
			ftp.setRestartOffset( offset );
		}else{
			file.delete();//默认直接删除
		}
		
		//开始下载
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
	 * 反登录并断开连接
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
	 * 设置文件下载监听器
	 */
	public void setCopyStreamListener( CopyStreamListener listener ){
		ftp.setCopyStreamListener(listener);
	}
}
