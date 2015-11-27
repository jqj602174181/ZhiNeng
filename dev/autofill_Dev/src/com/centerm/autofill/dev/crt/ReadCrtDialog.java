package com.centerm.autofill.dev.crt;

import com.centerm.autofill.dev.R;
import com.centerm.util.financial.CrtCardService;
import com.centerm.util.financial.MsgCardService;
import com.centerm.common.PlaySoundClass;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment") 
public class ReadCrtDialog extends DialogFragment {

	String title = null;						//����
	public final static int RES_OK = 0;							//��ȡ�ɹ�
	public final static int RES_ERR = -1;						//��ȡʧ��
	public final static int SHOW_TIME = 1;		//��ʾ����ʱ
	public final static int UPDATE_TITLE = 2;   //���±���

	CrtCardService crtCardService ;
	PlaySoundClass mPlaySound;
	ReadCrtListener completeListener = null;//��ȡ��ɵļ�����
	private ReadThread readThread;
	private boolean findLoop = true;

	TextView tvTime;
	TextView tvTitle ;
	//	int nTimeout = 20,nCount = 0;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case RES_OK:
				String result = (String)msg.obj;
				completeListener.onReadCardComplete(RES_OK,result);
				break;

			case RES_ERR:
				completeListener.onReadCardComplete(RES_ERR, null);
				break;
			}
			ReadCrtDialog.this.dismiss();
			mPlaySound.releaseMediaPlayer();
			cdTimer.cancel();
		}
	};

	private Handler refreshHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case  SHOW_TIME:
				//				int time = nTimeout - nCount;
				tvTitle.setText(title);
				//				tvTime.setText("( ��ʱ����ʱ �� " + time + " ��");
				break;
			case UPDATE_TITLE:
				tvTitle.setText("���ڶ�ȡ��Ƭ��Ϣ . . .");
				tvTime.setText("");
				break;
			case RES_ERR:
				tvTitle.setText(title);
				break;
			}
		}

	};

	public interface ReadCrtListener {
		void onReadCardComplete( int resCode, String str);//����룬0��ʾ�ɹ���-1��ʾʧ��
	}	

	public ReadCrtDialog( String title ){
		this.title = title;
	}

	public void setReadCompleteListener( ReadCrtListener listener){
		completeListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.readcrtcard, container);
		tvTitle = (TextView)view.findViewById( R.id.LABEL_TITLE);
		tvTime = (TextView)view.findViewById( R.id.TIME_TEXT);
		tvTitle.setText( title );

		readThread = new ReadThread();
		readThread.start();

		mPlaySound = new PlaySoundClass(getActivity());
		mPlaySound.playAudio(R.raw.read_iccard);

		this.setCancelable(false);
		cdTimer.start();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setStyle(DialogFragment.STYLE_NORMAL, R.style.previewStyle);// ���öԻ�����ʽ
	}

	//����ʱ����20�룬ÿ����һ��	��Ϊ���ÿͻ��ܿ���20����˴�21��ʼ��
	private CountDownTimer cdTimer = new CountDownTimer( 21*1000, 1000 ){		

		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvTime.setText( "����ʱ����ʱ��" + String.valueOf( g ) + "��");
		}

		public void onFinish() {
			tvTime.setText( "����ʱ����ʱ��0��");
			stopRead();
		}
	};

	/*
	 * ����
	 */
	private String readCard() {
		return crtCardService.readCard();
	}

	private void stopRead(){
		if(readThread != null && readThread.isAlive()){
			findLoop = false;
		}
		ReadCrtDialog.this.dismiss();
		mPlaySound.releaseMediaPlayer();
		cdTimer.cancel();
	}

	class ReadThread extends Thread{

		public ReadThread()
		{
			crtCardService = CrtCardService.getInstance();
		}
		@Override
		public void run() {
			String result;
			Message msg = Message.obtain();
			while(findLoop){
				if(crtCardService.isCardexist()){
					refreshHandler.sendEmptyMessage(UPDATE_TITLE);
					result = readCard();
					if(result == null){
						msg.what = RES_ERR;
						mHandler.sendMessage(msg);
						break;
					}else{
						msg.what = RES_OK;
						msg.obj = result;	
						mHandler.sendMessage(msg);
						break;
					}
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			refreshHandler.sendEmptyMessage(SHOW_TIME);
		}
	};

}
