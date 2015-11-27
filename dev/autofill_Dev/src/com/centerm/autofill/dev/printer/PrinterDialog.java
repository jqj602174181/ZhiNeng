package com.centerm.autofill.dev.printer;


import com.centerm.autofill.dev.R;
import com.centerm.util.financial.PersonInfo;
import com.centerm.util.printer.PrinterService;
import com.centerm.common.PlaySoundClass;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")

public class PrinterDialog extends DialogFragment {
	//�ص��ӿڣ��ڶ�id����ɺ���ã�������dialog��activity֮�䴫ֵ
	public interface CompleteListener {
		void onComplete(int status);
	}

	//id������ʵ��
	private static PrinterService service = PrinterService.getService();
	
	final int TYPE_COMPLETE = 0;
	final int TYPE_ERROR = 1;
	
	//����û���Ϣ
	View mView = null;
	CompleteListener mLis = null;
	PlaySoundClass mPlaySound;
	int nRepeatCount = 0;
	private String starttitle = "";//��ʾ�ı���
	private String endtitle = ""; //ҵ�����
	private String bustile = ""; //ҵ����Ϣ
	private String businfo = "";
	private ImageView imgTip = null;
	private TextView title = null;
	private int timeout = 5;
	public PrinterDialog(){
		super();
	}
	
	public PrinterDialog( String starttitle, String endtitle,
			String bustile, String businfo ){
		this();
		this.starttitle = starttitle;
		this.endtitle = endtitle;
		this.bustile = bustile;
		this.businfo = businfo;
	}
	
	public void setReadCompleteListener(CompleteListener lis)
	{
		mLis = lis;
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case TYPE_COMPLETE:// ������
				imgTip.setImageResource(R.drawable.success);
				title.setText(endtitle);
				runbale.run();
				break;
			case TYPE_ERROR:// ����֤
				mLis.onComplete(-1);
				Toast.makeText(getActivity(), "��ӡʧ�ܣ�",
						Toast.LENGTH_SHORT).show();
				mPlaySound.releaseMediaPlayer();
				PrinterDialog.this.dismiss();
				break;
			}
		}
	};
	private Runnable runbale = new Runnable()
	{
		public void run()
		{
			if(timeout == 0)
			{
				mLis.onComplete(0);
				mPlaySound.releaseMediaPlayer();
				PrinterDialog.this.dismiss();
				return;
			}
			else
				timeout--;
			mHandler.postDelayed(this, 1000);
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.printstr, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//���ñ���
		if( starttitle.length() > 0 ){
			title = (TextView)mView.findViewById( R.id.LABEL_TITLE);
			title.setText( starttitle );
			
			imgTip = (ImageView)mView.findViewById(R.id.img_idcard);
		}

		// ������ʾ���밴��ʾ��ȡ����֤��
		mPlaySound = new PlaySoundClass(getActivity());
		mPlaySound.playAudio(R.raw.id);

		try {
			this.printString(bustile, bustile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.setCancelable(false);
		return mView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ���öԻ�����ʽ
		setStyle(DialogFragment.STYLE_NORMAL, R.style.previewStyle);
	}

	public void printString(String title, String buss) throws Exception {
		final String strtitle = " \n" + title+"\n";//ǰ���һ��\n��ֹ��һ������
		final String info = buss + "\n";
		final int STATUS_BUSY = 64;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//byte[] byData = QRUtil.CreateQR(info, 200, 200);//��ǰ����ӡ��ά��
					int status = service.getPrintStatus();
					service.setMode(true, true, false);
					service.printString(strtitle, 5);
					service.setMode(false, false, false);
					service.printString(info, 10);
					
					try {
						//service.printImage( byData, 200, 200, 1, 10);
						service.printString(" \n \n \n \n \n");//��ӡ����bug: ���س����޷���ֽ����˲��ÿո�+�س�
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(TYPE_ERROR);
					}
					while(service.getPrintStatus() == STATUS_BUSY)
					{
						Thread.sleep(100);
					}
					mHandler.sendEmptyMessage(TYPE_COMPLETE);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mHandler.sendEmptyMessage(TYPE_ERROR);
				}
			}
		}).start();
	}

}
