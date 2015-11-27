package com.centerm.autofill.dev.iccard;

import java.util.HashMap;
import java.util.Map;

import com.centerm.autofill.dev.R;
import com.centerm.autofill.dev.magcard.ReadMagCardDialog;
import com.centerm.autofill.dev.magcard.ReadMagCardDialog.ReadMagCardListener;
import com.centerm.util.StringUtil;
import com.centerm.util.financial.ICCardService;
import com.centerm.util.financial.MsgCardService;
import com.centerm.common.PlaySoundClass;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment") 
public class ReadICCardDialog extends DialogFragment {

	String title = null;						//标题
	public final static int RES_OK = 0;							//读取成功
	public final static int RES_ERR = -1;						//读取失败
	public final static int SHOW_TIME = 1;		//显示倒计时
	public final static int UPDATE_TITLE = 2;   //更新标题
	
	MsgCardService iccardService ;
	PlaySoundClass mPlaySound;
	ReadICCardListener completeListener = null;//读取完成的监听器
	
	TextView tvTime;
	TextView tvTitle ;
	int nTimeout = 20,nCount = 0;
	
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
			ReadICCardDialog.this.dismiss();
		}
	};
	
	private Handler refreshHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case  SHOW_TIME:
					int time = nTimeout-nCount;
					tvTitle.setText(title);
					tvTime.setText("( 超时倒计时 ： " + time + " ）");
				//	nCount++;
					break;
					
				case UPDATE_TITLE:
					tvTitle.setText("正在读取卡片信息 . . .");
					tvTime.setText("");
					break;
				case RES_ERR:
					tvTitle.setText(title);
					break;
			}
		}
		
	};
	
	public ReadICCardDialog( String title ){
		this.title = title;
	}
	
	public void setReadCompleteListener( ReadICCardListener listener){
		completeListener = listener;
	}
	
	public interface ReadICCardListener {
		void onReadCardComplete( int resCode, String str);//结果码，0表示成功，-1表示失败
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.readiccard, container);
		 tvTitle = (TextView)view.findViewById( R.id.LABEL_TITLE);
		tvTime = (TextView)view.findViewById( R.id.TIME_TEXT);
		tvTitle.setText( title );
		
		ReadThread readThread = new ReadThread();
		readThread.start();
		
		mPlaySound = new PlaySoundClass(getActivity());
		mPlaySound.playAudio(R.raw.read_iccard);

		this.setCancelable(false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setStyle(DialogFragment.STYLE_NORMAL, R.style.previewStyle);// 设置对话框样式
	}
	

	/*
	 * !
	 * 
	 * @brief 读卡
	 */
	private String readCard() {
		refreshHandler.sendEmptyMessage(UPDATE_TITLE);

		return iccardService.readCard();
	}
	
	
	 
	private String powerOn() {
		
		return null;
	}


	private String powerOff() {
	
		return null;
	}
	
	
	
	
	class ReadThread extends  Thread{
		

		public ReadThread()
		{
			nCount = 0;
			iccardService = new MsgCardService();
		}
		@Override
		public void run() {
			Message msg = Message.obtain();
			String result = powerOn();
			while(true){
				if(nCount>nTimeout){
					msg.what = RES_ERR;
					msg.obj = result;
					mHandler.sendMessage(msg);
					powerOff();
					break;
				}
				
				if(iccardService.isCardexist()){
					
					refreshHandler.sendEmptyMessage(UPDATE_TITLE);
					result = readCard();
					if(result==null){
						msg.what = RES_ERR;
						mHandler.sendMessage(msg);
						break;
					}else{
						msg.what = RES_OK;
						msg.obj = result;	
						mHandler.sendMessage(msg);
						powerOff();
						break;
					}
				
					
				}else{
			
				}
			
				refreshHandler.sendEmptyMessage(SHOW_TIME);
			
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				nCount++;
			}
		}
	};
	
}
