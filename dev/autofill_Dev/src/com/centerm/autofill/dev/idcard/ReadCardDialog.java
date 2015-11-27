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
	//回调接口，在都id卡完成后调用，可用于dialog和activity之间传值
	public interface ReadCardListener {
		void onReadCardComplete( int resCode, IdCardMsg person);//返回码，0表示成功，-1表示失败
	}

	//常量
	public final static int RES_OK = 0;							//读取成功
	public final static int RES_ERR = -1;						//读取失败
	public final static int RES_CANCEL = -2;					//取消读取
	public final static int RES_SKIP = -3;						//跳过操作

	IDCardService idService = null;
	IdCardMsg perInfo;										//身份证信息
	ReadCardListener readCardCompleteListener = null;		//读卡结束时回调函数
	PlaySoundClass mPlaySound;
	String title = "";										//提示的标题
	private TextView tvCountDown;							//倒计时	
	private boolean bShowSkip = false;						//显示跳过按钮	
	private int resCodeOnCancel = RES_CANCEL;				//取消时的返回值

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
		idService = IDCardService.getInstance();	//id卡操作实体
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

		//设置标题
		if( title.length() > 0 ){
			TextView tvTitle = (TextView)view.findViewById( R.id.LABEL_TITLE);
			tvTitle.setText( title );
		}

		//响应返回按钮
		TextView tvRet = (TextView)view.findViewById( R.id.LABEL_RETURN );        
		tvRet.setOnClickListener( clickListener );
		TextView tvCancelTip = (TextView)view.findViewById( R.id.LABEL_RETURN);

		//有跳过，无返回按钮
		if( bShowSkip ){
			ImageButton btnSkip = (ImageButton)view.findViewById( R.id.BTN_SKIP );
			btnSkip.setVisibility( View.VISIBLE );
			btnSkip.setOnClickListener( clickListener );
			//        btnRet.setVisibility( View.INVISIBLE );//不显示
			tvCancelTip.setVisibility( View.INVISIBLE );//不显示

			TextView tvTip = (TextView)view.findViewById( R.id.LABEL_SKIP);
			tvTip.setVisibility( View.VISIBLE );
			tvTip = (TextView)view.findViewById( R.id.LABEL_TIPRETURN);
			tvTip.setVisibility( View.VISIBLE );	        		
		}//else{//不显示跳过，但显示返回

		// 播放提示“请按提示读取二代证”
		mPlaySound = new PlaySoundClass(getActivity());
		mPlaySound.playAudio(R.raw.id);

		//开始读二代证
		idService.readCard(readListener);

		this.setCancelable(false);//读二代证时不允许关闭当前窗口
		cdTimer.start();
		return view;
	}
	
	public void initUSB(Activity activity){
		idService.init(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置对话框样式
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
			//handler.sendEmptyMessage( RES_CANCEL );由onCancel内部实现
		}
	};

	/******************倒计时***************/
	//倒计时，共20秒，每秒钟一次	。为了让客户能看到20，因此从21开始。
	private CountDownTimer cdTimer = new CountDownTimer( 21*1000, 1000 ){		

		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvCountDown.setText( "（超时倒计时：" + String.valueOf( g ) + "）");
		}

		public void onFinish() {
			tvCountDown.setText( "（超时倒计时：0）");

			idService.stopRead();
		}
	};
	
	//读二代证结果监听器
	OnReadCardListener readListener = new OnReadCardListener() {

		public void onComplete(IdCardMsg idCardMsg) {			
			ReadCardDialog.this.perInfo = (IdCardMsg)idCardMsg.clone();//克隆完才能使不同界面的二代证信息不同，否则都指定同一个对象
			handler.sendEmptyMessage( RES_OK );
		}

		public void onError(String strMsg) {
			//当出现错误时，由各个应用指定错误信息
			/*Toast.makeText(getActivity(), "读二代证失败：" + nErrCode + "|" + strMsg,
						Toast.LENGTH_LONG ).show();*/
			handler.sendEmptyMessage( RES_ERR );
		}

		public void onCancel() {
			Toast.makeText(getActivity(), "您已取消身份证的阅读操作", Toast.LENGTH_LONG ).show();
			handler.sendEmptyMessage( resCodeOnCancel );
		}
	};
}
