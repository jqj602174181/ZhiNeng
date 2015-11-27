package com.centerm.autofill.dev.magcard;

import com.centerm.autofill.dev.R;
import com.centerm.util.financial.MagneticCardService;

import com.centerm.common.PlaySoundClass;

import android.annotation.SuppressLint;
import android.app.DialogFragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment") 
public class ReadMagCardDialog extends DialogFragment {

	public interface ReadMagCardListener {
		void onReadCardComplete( int resCode, String str);//结果码，0表示成功，-1表示失败
	}	
	
	//常量
	public final static int RES_OK = 0;							//读取成功
	public final static int RES_ERR = -1;						//读取失败
	
	MagneticCardService magService = MagneticCardService.getInstance();
	PlaySoundClass mPlaySound;
	ReadMagCardListener completeListener = null;//读取完成的监听器
	String title = null;						//标题
	
	public ReadMagCardDialog( String title ){
		super();
		this.title = title;
	}
	public void setReadCompleteListener( ReadMagCardListener listener)
	{
		completeListener = listener;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.readmagcard, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		TextView tvTitle = (TextView)view.findViewById( R.id.LABEL_TITLE);
		tvTitle.setText( title );

		// 播放提示音
		mPlaySound = new PlaySoundClass(getActivity());
		mPlaySound.playAudio(R.raw.card);

		magService.setComParams(2, 9600);
		magService.setOutTime(16);//保持与底层超时一致
		magService.readTrack2(new MagneticCardService.OnReadCardListener() {
			
			@Override
			public void onError(int nErrCode, String strMsg) {
				//防止程序退出时,产生空指针异常
				try{
					Toast.makeText(getActivity(), "读磁卡失败：" + nErrCode + "|" + strMsg, Toast.LENGTH_LONG).show();
					ReadMagCardDialog.this.dismiss();
					completeListener.onReadCardComplete( RES_ERR, null );
				}catch(NullPointerException e){
					
				}
			
			}
			
			@Override
			public void onComplete(byte[] byTrack1, byte[] byTrack2, byte[] byTrack3) {
				String strCardNo = new String(byTrack2);
				if( strCardNo.indexOf( '=' ) > 0 ){//必须包含等号
					String []strArray = strCardNo.split( "=" );
					completeListener.onReadCardComplete( RES_OK, strArray[0]);
				}else{
					completeListener.onReadCardComplete( RES_ERR, null );
				}
				ReadMagCardDialog.this.dismiss();
			}
			
			@Override
			public void onCancel() {
				Toast.makeText(getActivity(), "用户取消读", Toast.LENGTH_LONG).show();
				ReadMagCardDialog.this.dismiss();
				completeListener.onReadCardComplete( RES_ERR, null );
			}
		});
		this.setCancelable(false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setStyle(DialogFragment.STYLE_NORMAL, R.style.previewStyle);// 设置对话框样式
	}
	
}
