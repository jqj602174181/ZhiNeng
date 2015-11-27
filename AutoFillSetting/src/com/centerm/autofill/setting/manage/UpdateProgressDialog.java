package com.centerm.autofill.setting.manage;

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.centerm.autofill.setting.R;
import com.centerm.autofill.setting.SystemUpdate;

public class UpdateProgressDialog extends Dialog {
	
	private ProgressBar barProgress;				//进度条控件
	private TextView txProgress;					//进度信息控件
	private TextView txTip;							//提示信息控件
	private SystemUpdate update;					//执行更新操作
	private PackageDownloadTask downloadTask;		//下载线程
	private TextView tvCountDown;//倒计时控件
	private ImageView imgCountDown;//倒计时时钟
	Context owner;		//父activity
	private static boolean hasRunning = false;		//防止系统设置和自动检测同时运行升级界面，供updateActivity查询

	public UpdateProgressDialog(Context context, SystemUpdate update ) {
		super(context, R.style.dialog_style );//无标题，无背景、边框
		setContentView( R.layout.manage_update_progress );
		owner = context;
		
		barProgress = (ProgressBar)findViewById( R.id.PROGRESSBAR );
		txProgress = (TextView)findViewById( R.id.LABEL_PROGRESS );
		txTip = (TextView)findViewById( R.id.LABEL_TIP );
		tvCountDown = (TextView)findViewById( R.id.LABEL_COUNTDOWN );
		imgCountDown = (ImageView)findViewById(R.id.IMG_COUNTDOWN);
		this.update = update;
		this.setCancelable( false );
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if( !hasRunning ){
			hasRunning = true;
		}
		
		txTip.setText( "下载中..." );
		downloadTask = new PackageDownloadTask();
		downloadTask.execute( (Void[])null );
	}
	
	public static boolean isHasRunning() {
		return hasRunning;
	}


	/**
	 * 解析升级包，并校验升级包是否正确
	 */
	private class PackageDownloadTask extends AsyncTask< Void, Integer, Integer > implements CopyStreamListener{
		
		private long fileSize;
		private static final int CHECKMD5 = 1001;			//校验文件
		private static final int READYTOUPDATE = 1002;		//准备升级
		private static final int ERR_DOWNLOAD = -2;			//下载文件失败
		private static final int ERR_HASH = -3;				//校验文件失败
		private static final int ERR_UPDATE = -4;			//进入升级状态失败

		@Override
		protected Integer doInBackground(Void... params) {
			fileSize = update.getUpdateInfo().size;
			if( update.downloadVersion( this )){
				
				//校验MD5码
				publishProgress( CHECKMD5 );				
				if( update.validatePackage()){
					//准备升级
					publishProgress( READYTOUPDATE );
					if( update.doUpdate(owner) )
						return 0;
					else
						return ERR_UPDATE;
				}else
					return ERR_HASH;
			}else{
				return ERR_DOWNLOAD;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			String txt = "准备重启系统，进入升级模式...";
			switch( result ){
			case ERR_DOWNLOAD:
				txt = "下载文件失败！";
				break;
			case ERR_HASH:
				txt = "下载的文件不完整或者不是指定的格式，无法进行升级！";
				break;
			case ERR_UPDATE:
				txt = "进入升级模式失败！";
				break;
			default://OK成功
				break;
			}
			txTip.setText( txt );
			
			tvCountDown.setVisibility(View.VISIBLE);
			imgCountDown.setVisibility(View.VISIBLE);
			cdTimer.start();
		}//end onPostExecute

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progress = values[0];
			switch( progress ){
			case CHECKMD5:
				txTip.setText( "正在校验升级文件..." );
				break;
			case READYTOUPDATE:
				txTip.setText( "准备升级..." );
				break;
			default:
				barProgress.setProgress( progress );
		    	txProgress.setText( progress + "%");
		    	break;
			}
			
		}		
		
		public void bytesTransferred(CopyStreamEvent event) {
			bytesTransferred( event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
		}

		public void bytesTransferred( long totalBytesTransferred, int bytesTransferred, long streamSize) {
			//更新进度
			int progress = (int)(totalBytesTransferred * 100/fileSize);
			publishProgress( progress );
		}
	}//end of class PackageDownloadTask
	
	//倒计时，共5秒，每秒钟一次。升级完成后进行倒计时。
	private CountDownTimer cdTimer = new CountDownTimer(10000, 1000) {

	    public void onTick( long millisUntilFinished ) {
	   	 	long g = millisUntilFinished/1000;
	   	 	tvCountDown.setText( String.valueOf( g ));
	    }
	
	    public void onFinish() {
	    	dismiss();
	    	hasRunning = false;//已经关闭
	    }
	};

}
