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
	
	private ProgressBar barProgress;				//�������ؼ�
	private TextView txProgress;					//������Ϣ�ؼ�
	private TextView txTip;							//��ʾ��Ϣ�ؼ�
	private SystemUpdate update;					//ִ�и��²���
	private PackageDownloadTask downloadTask;		//�����߳�
	private TextView tvCountDown;//����ʱ�ؼ�
	private ImageView imgCountDown;//����ʱʱ��
	Context owner;		//��activity
	private static boolean hasRunning = false;		//��ֹϵͳ���ú��Զ����ͬʱ�����������棬��updateActivity��ѯ

	public UpdateProgressDialog(Context context, SystemUpdate update ) {
		super(context, R.style.dialog_style );//�ޱ��⣬�ޱ������߿�
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
		
		txTip.setText( "������..." );
		downloadTask = new PackageDownloadTask();
		downloadTask.execute( (Void[])null );
	}
	
	public static boolean isHasRunning() {
		return hasRunning;
	}


	/**
	 * ��������������У���������Ƿ���ȷ
	 */
	private class PackageDownloadTask extends AsyncTask< Void, Integer, Integer > implements CopyStreamListener{
		
		private long fileSize;
		private static final int CHECKMD5 = 1001;			//У���ļ�
		private static final int READYTOUPDATE = 1002;		//׼������
		private static final int ERR_DOWNLOAD = -2;			//�����ļ�ʧ��
		private static final int ERR_HASH = -3;				//У���ļ�ʧ��
		private static final int ERR_UPDATE = -4;			//��������״̬ʧ��

		@Override
		protected Integer doInBackground(Void... params) {
			fileSize = update.getUpdateInfo().size;
			if( update.downloadVersion( this )){
				
				//У��MD5��
				publishProgress( CHECKMD5 );				
				if( update.validatePackage()){
					//׼������
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
			String txt = "׼������ϵͳ����������ģʽ...";
			switch( result ){
			case ERR_DOWNLOAD:
				txt = "�����ļ�ʧ�ܣ�";
				break;
			case ERR_HASH:
				txt = "���ص��ļ����������߲���ָ���ĸ�ʽ���޷�����������";
				break;
			case ERR_UPDATE:
				txt = "��������ģʽʧ�ܣ�";
				break;
			default://OK�ɹ�
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
				txTip.setText( "����У�������ļ�..." );
				break;
			case READYTOUPDATE:
				txTip.setText( "׼������..." );
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
			//���½���
			int progress = (int)(totalBytesTransferred * 100/fileSize);
			publishProgress( progress );
		}
	}//end of class PackageDownloadTask
	
	//����ʱ����5�룬ÿ����һ�Ρ�������ɺ���е���ʱ��
	private CountDownTimer cdTimer = new CountDownTimer(10000, 1000) {

	    public void onTick( long millisUntilFinished ) {
	   	 	long g = millisUntilFinished/1000;
	   	 	tvCountDown.setText( String.valueOf( g ));
	    }
	
	    public void onFinish() {
	    	dismiss();
	    	hasRunning = false;//�Ѿ��ر�
	    }
	};

}
