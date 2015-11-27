package com.centerm.autofill.dev.idcard;

import com.centerm.autofill.dev.R;
import com.centerm.util.financial.IDCardService;
import com.centerm.util.financial.IdCardMsg;
import com.centerm.util.financial.IDCardService.OnReadCardListener;
import com.centerm.common.PlaySoundClass;
import com.sdt.Common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")

public class ReadCardDialog extends DialogFragment {
	//�ص��ӿڣ��ڶ�id����ɺ���ã�������dialog��activity֮�䴫ֵ
	public interface ReadCardListener {
		void onReadCardComplete( int resCode, IdCardMsg person);//�����룬0��ʾ�ɹ���-1��ʾʧ��
	}

	//����
	public final static int RES_OK = 0;							//��ȡ�ɹ�
	public final static int RES_ERR = -1;						//��ȡʧ��
	public final static int RES_CANCEL = -2;					//ȡ����ȡ
	public final static int RES_SKIP = -3;						//��������

	IDCardService idService = null;
	IdCardMsg perInfo;										//���֤��Ϣ
	ReadCardListener readCardCompleteListener = null;		//��������ʱ�ص�����
	PlaySoundClass mPlaySound;
	String title = "";										//��ʾ�ı���
	private TextView tvCountDown;							//����ʱ	
	private boolean bShowSkip = false;						//��ʾ������ť	
	private int resCodeOnCancel = RES_CANCEL;				//ȡ��ʱ�ķ���ֵ

	private Activity activity;

	public ReadCardDialog(){
		super();
	}

	public ReadCardDialog( String title ){
		this();
		this.title = title;
	}

	public ReadCardDialog( String title, boolean canSkip, Activity activity){
		this();
		this.title = title;
		bShowSkip = canSkip;
		idService = IDCardService.getInstance();	//id������ʵ��
		idService.init(activity);
		this.activity = activity;
	}

	public void setReadCompleteListener(ReadCardListener lis)
	{
		readCardCompleteListener = lis;
	}

	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			readCardCompleteListener.onReadCardComplete( msg.what, perInfo);
			mPlaySound.releaseMediaPlayer();
			cdTimer.cancel();
			ReadCardDialog.this.dismiss();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.readidcard, container );
		getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
		tvCountDown = (TextView)view.findViewById( R.id.LABEL_TIMEMOUT );

		//���ñ���
		if( title.length() > 0 ){
			TextView tvTitle = (TextView)view.findViewById( R.id.LABEL_TITLE);
			tvTitle.setText( title );
		}

		//��Ӧ���ذ�ť
		TextView tvRet = (TextView)view.findViewById( R.id.LABEL_RETURN );        
		tvRet.setOnClickListener( clickListener );
		TextView tvCancelTip = (TextView)view.findViewById( R.id.LABEL_RETURN);

		//���������޷��ذ�ť
		if( bShowSkip ){
			ImageButton btnSkip = (ImageButton)view.findViewById( R.id.BTN_SKIP );
			btnSkip.setVisibility( View.VISIBLE );
			btnSkip.setOnClickListener( clickListener );
			//        btnRet.setVisibility( View.INVISIBLE );//����ʾ
			tvCancelTip.setVisibility( View.INVISIBLE );//����ʾ

			TextView tvTip = (TextView)view.findViewById( R.id.LABEL_SKIP);
			tvTip.setVisibility( View.VISIBLE );
			tvTip = (TextView)view.findViewById( R.id.LABEL_TIPRETURN);
			tvTip.setVisibility( View.VISIBLE );	        		
		}//else{//����ʾ����������ʾ����

		// ������ʾ���밴��ʾ��ȡ����֤��
		mPlaySound = new PlaySoundClass(getActivity());
		mPlaySound.playAudio(R.raw.id);

		//��ʼ������֤
		idService.readCard(readListener);

		this.setCancelable(false);//������֤ʱ������رյ�ǰ����
		cdTimer.start();
		return view;
	}
	
	public void initUSB(Activity activity){
		idService.init(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���öԻ�����ʽ
		setStyle(DialogFragment.STYLE_NORMAL, R.style.previewStyle);
	}

	private OnClickListener clickListener = new OnClickListener() {			
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if( id == R.id.LABEL_RETURN ){
				resCodeOnCancel = RES_CANCEL;
				idService.cancelRead();
			}
			else if(id == R.id.BTN_SKIP ){	
				resCodeOnCancel = RES_SKIP;
				idService.cancelRead();
			}
			//handler.sendEmptyMessage( RES_CANCEL );��onCancel�ڲ�ʵ��
		}
	};

	/******************����ʱ***************/
	//����ʱ����20�룬ÿ����һ��	��Ϊ���ÿͻ��ܿ���20����˴�21��ʼ��
	private CountDownTimer cdTimer = new CountDownTimer( 21*1000, 1000 ){		

		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvCountDown.setText( "����ʱ����ʱ��" + String.valueOf( g ) + "��");
		}

		public void onFinish() {
			tvCountDown.setText( "����ʱ����ʱ��0��");

			idService.stopRead();
		}
	};
	
	//������֤���������
	OnReadCardListener readListener = new OnReadCardListener() {

		public void onComplete(IdCardMsg idCardMsg) {			
			ReadCardDialog.this.perInfo = (IdCardMsg)idCardMsg.clone();//��¡�����ʹ��ͬ����Ķ���֤��Ϣ��ͬ������ָ��ͬһ������
			handler.sendEmptyMessage( RES_OK );
		}

		public void onError(String strMsg) {
			//�����ִ���ʱ���ɸ���Ӧ��ָ��������Ϣ
			/*Toast.makeText(getActivity(), "������֤ʧ�ܣ�" + nErrCode + "|" + strMsg,
						Toast.LENGTH_LONG ).show();*/
			handler.sendEmptyMessage( RES_ERR );
		}

		public void onCancel() {
			Toast.makeText(getActivity(), "����ȡ�����֤���Ķ�����", Toast.LENGTH_LONG ).show();
			handler.sendEmptyMessage( resCodeOnCancel );
		}
	};
}
