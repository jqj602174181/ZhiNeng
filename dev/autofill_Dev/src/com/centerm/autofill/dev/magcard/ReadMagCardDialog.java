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
		void onReadCardComplete( int resCode, String str);//����룬0��ʾ�ɹ���-1��ʾʧ��
	}	
	
	//����
	public final static int RES_OK = 0;							//��ȡ�ɹ�
	public final static int RES_ERR = -1;						//��ȡʧ��
	
	MagneticCardService magService = MagneticCardService.getInstance();
	PlaySoundClass mPlaySound;
	ReadMagCardListener completeListener = null;//��ȡ��ɵļ�����
	String title = null;						//����
	
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

		// ������ʾ��
		mPlaySound = new PlaySoundClass(getActivity());
		mPlaySound.playAudio(R.raw.card);

		magService.setComParams(2, 9600);
		magService.setOutTime(16);//������ײ㳬ʱһ��
		magService.readTrack2(new MagneticCardService.OnReadCardListener() {
			
			@Override
			public void onError(int nErrCode, String strMsg) {
				//��ֹ�����˳�ʱ,������ָ���쳣
				try{
					Toast.makeText(getActivity(), "���ſ�ʧ�ܣ�" + nErrCode + "|" + strMsg, Toast.LENGTH_LONG).show();
					ReadMagCardDialog.this.dismiss();
					completeListener.onReadCardComplete( RES_ERR, null );
				}catch(NullPointerException e){
					
				}
			
			}
			
			@Override
			public void onComplete(byte[] byTrack1, byte[] byTrack2, byte[] byTrack3) {
				String strCardNo = new String(byTrack2);
				if( strCardNo.indexOf( '=' ) > 0 ){//��������Ⱥ�
					String []strArray = strCardNo.split( "=" );
					completeListener.onReadCardComplete( RES_OK, strArray[0]);
				}else{
					completeListener.onReadCardComplete( RES_ERR, null );
				}
				ReadMagCardDialog.this.dismiss();
			}
			
			@Override
			public void onCancel() {
				Toast.makeText(getActivity(), "�û�ȡ����", Toast.LENGTH_LONG).show();
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
		setStyle(DialogFragment.STYLE_NORMAL, R.style.previewStyle);// ���öԻ�����ʽ
	}
	
}
