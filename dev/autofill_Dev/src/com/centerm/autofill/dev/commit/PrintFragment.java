package com.centerm.autofill.dev.commit;

import jp.ksksue.driver.serial.FTDriver;
import jp.ksksue.driver.serial.HexUtils;

import com.centerm.autofill.dev.R;
import com.centerm.autofill.dev.printer.BuildinPrinter;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//��ӡ�Ŷ�ƾ��
public class PrintFragment extends Fragment implements BuildinPrinter.IPrintComplete {
	final static int MSG_PRINT_COMPLETE = 1;	//��ӡ���
	final static int MSG_PRINT_ERROR = -1;		//��ӡ������
	final static int MSG_CANCEL_TIMER = 2;		//ȡ����ʱ��

	private String tranName;		//��������
	private String formNo;			//���
	private String createdTime;		//����ʱ��
	private String strLogo;			//�ͻ�logo��Ϣ
	private TextView tvCountDown;			//�ɹ�ʱ����ʱ	
	private TextView tvCountDownNoPaper;	//ȱֽʱ����ʱ
	//	private BuildinPrinter printer;			//���ô�ӡ��

	FTDriver mSerial;
	final int SERIAL_BAUDRATE = FTDriver.BAUD115200;

	public void setPrintInfo( String tranName, String formNo, 
			String createdTime, String strLogo){
		this.tranName = tranName;
		this.formNo = formNo;
		this.createdTime = createdTime;
		this.strLogo = strLogo;
	}	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.fragment_print, container, false );		
		tvCountDown = (TextView)view.findViewById( R.id.LABEL_EXITTIMEOUT );
		tvCountDownNoPaper = (TextView)view.findViewById( R.id.LABEL_TIMEMOUT );

		//�����˳�����
		Button btn = (Button)view.findViewById( R.id.BTN_EXIT );
		btn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View view) {
				complete();
			}
		});

		//������ӡ
		btn = (Button)view.findViewById( R.id.BTN_PRINT );
		btn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View view) {
				startPrint();
			}
		});

		//�����ֽ������д�ӡ��������ʾȱֽ
		//		printer = BuildinPrinter.getInstance();
		//		printer.setPrintCompleteListener( this );
		if( hasPaper() ){
			//		if(true){
			//��ʾ���ڴ�ӡ
			View viewPrinting = view.findViewById( R.id.BLOCK_PRINTING );
			viewPrinting.setVisibility( View.VISIBLE );
			//			printer.print(tranName, formNo, createdTime, strLogo);
			startPrint2();
		}else{//��ӡ��⵽ȱֽ
			View viewNoPaper = view.findViewById( R.id.BLOCK_NOPAPER );
			viewNoPaper.setVisibility( View.VISIBLE );
			tvCountDownNoPaper.setText( "�����ص���ʱ��60��");
			noPaperTimer.start();
		}

		mSerial = new FTDriver((UsbManager) getActivity().getSystemService(Context.USB_SERVICE), getActivity());

		PendingIntent permissionIntent = PendingIntent.getBroadcast(getActivity(), 0,
				new Intent("jp.ksksue.sample.USB_PERMISSION"), 0);
		mSerial.setPermissionIntent(permissionIntent); 
		// Broadcast listen for new devices
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		getActivity().registerReceiver(mUsbReceiver, filter); 
		
		mSerial.begin(SERIAL_BAUDRATE);

		return view;
	}	

	BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				mSerial.usbAttached(intent);
				mSerial.begin(SERIAL_BAUDRATE);
				//mainloop();
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				mSerial.usbDetached(intent);
				//mSerial.end();
				//mStop = true;
			}
		}
	};

	@Override
	public void onStop() {
		cdTimer.cancel();
		noPaperTimer.cancel();
		getActivity().unregisterReceiver(mUsbReceiver);
		super.onStop();
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch( msg.what ){
			case MSG_PRINT_COMPLETE:
				//ȡ�����ڴ�ӡ��ʾ
				View container = getView();
				View view = container.findViewById( R.id.BLOCK_PRINTING );
				view.setVisibility( View.GONE );

				//��ʾ��ӡ�ɹ���ʾ������ʱ
				view = container.findViewById( R.id.BLOCK_PRINTEND );
				view.setVisibility( View.VISIBLE );
				cdTimer.start();
				break;
			case MSG_PRINT_ERROR://��ӡ������
				printCompleteInError();
				break;
			case MSG_CANCEL_TIMER:
				noPaperTimer.cancel();
				break;
			}
		}
	};

	//��ӡ���
	public void onPrintComplete( boolean isSuccess ){		
		handler.sendEmptyMessage( MSG_PRINT_COMPLETE );
	}

	//��ӡ�ɹ�ʱ�ĵ���ʱ����10�룬ÿ����һ��	��Ϊ���ÿͻ��ܿ���10����˴�11��ʼ��
	private CountDownTimer cdTimer = new CountDownTimer( 11*1000, 1000 ){		

		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvCountDown.setText( "��" + String.valueOf( g ) + "����˳���");
		}

		public void onFinish() {
			tvCountDown.setText( "�������˳�...��");
			complete();
		}
	};

	//ȱֽ����ʱ����60�룬ÿ����һ��	��Ϊ���ÿͻ��ܿ���60����˴�61��ʼ��
	private CountDownTimer noPaperTimer = new CountDownTimer( 61*1000, 1000 ){	


		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvCountDownNoPaper.setText( "�����ص���ʱ��" + String.valueOf( g ) + "��");

			//�Ѿ���ֽ���ˣ�������ӡ
			if( hasPaper() ){
				handler.sendEmptyMessage( MSG_CANCEL_TIMER );//��Դ�뿴������CountDownTimer��onTick��ȡ���ö�ʱ����
				//��ֻ���ڽ����߳���ȡ������˲���handlerʵ��
				View view = getView();
				View viewPrint = view.findViewById( R.id.BTN_PRINT );
				viewPrint.setVisibility( View.VISIBLE );
				tvCountDownNoPaper.setText( "װֽ��ɺ��������ӡ" );
			}
		}

		public void onFinish() {
			tvCountDownNoPaper.setText( "�����ص���ʱ��0��");
			CommitActivity activity = (CommitActivity)getActivity();
			activity.returnError( "����ȱֽ��δ��ɴ�ӡ��" );
		}
	};

	//�ɹ��˳�
	private void complete(){
		CommitActivity activity = (CommitActivity)getActivity();
		activity.returnComplete();
	}

	//�ɹ��˳�
	private void printCompleteInError(){
		CommitActivity activity = (CommitActivity)getActivity();
		activity.returnError( "��ӡ����" );
	}

	private void startPrint2() {
		//���ɱ��������
		final String title = " \n     " + strLogo +"\n";//ǰ���һ��\n��ֹ��һ������
		final String tranNLable = "                �������ҵ���ǣ�\n";
		String space = "";
		for(int n = (24 - tranName.length()*2) / 2; n>0; n--){		// û������ʱ��һ�д�ӡ48��Ӣ�Ļ�24������
			space += " ";
		}
		final String tranN = space + tranName + "\n";
		final String formN = "    Ԥ���: "+formNo + "\n";
		final String time = "            ʱ��:" + createdTime +"\n";

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					printContent(title, 1);
					printContent(tranNLable, 0);
					printContent(tranN, 1);
					printContent(formN, 1);
					printContent(time, 0);
					//����
					mSerial.write(HexUtils.hexStr2FeedLine(8));
					//��ֽ0ȫ�У�1����
					mSerial.write(HexUtils.hexStr2CutPaper(0));	

					onPrintComplete( true );
				} catch (Exception e) {
					e.printStackTrace();
					onPrintComplete(false);//�������쳣ҲҪ�������
				}
			}
		}).start();
	}
	
	private void printContent(String str, int lev){
		byte[] bytes = HexUtils.GetPrintData(str);

		mSerial.write(HexUtils.SetPrintModel(lev, lev));
		// ����������ת
		mSerial.write(HexUtils.SetPrintRotate(0));	
		mSerial.write(bytes, bytes.length);
		//����
		byte[] etBytes2 = HexUtils.hexStr2FeedLine(1);
		mSerial.write(etBytes2, etBytes2.length);
	}

	//��ӡ
	private void startPrint(){
		View view = getView();
		View viewNoPaper = view.findViewById( R.id.BLOCK_NOPAPER );
		viewNoPaper.setVisibility( View.GONE );
		View viewPrinting = view.findViewById( R.id.BLOCK_PRINTING );
		viewPrinting.setVisibility( View.VISIBLE );	
		//		printer.print(tranName, formNo, createdTime, strLogo);
		startPrint2();
	}

	private boolean hasPaper(){
		boolean isHasPaper = true;
		byte[] etBytes =new byte[3];
		int iIndex=0;
		etBytes[iIndex++]=0x10;
		etBytes[iIndex++]=0x04;
		etBytes[iIndex++]=0x04;
		mSerial.write(etBytes,iIndex); 
		byte[] bRead=new byte[8]; 		
		for(iIndex=0;iIndex<10;iIndex++)
		{
			if(mSerial.read(bRead)>0)
			{
				System.out.println("bRead[0]:"+bRead[0]); 
				switch(bRead[0])
				{
				case 0x10: 
				case 0x12: 
					isHasPaper = true; //����
					break;
				case 0x70:
					isHasPaper = false; //ȱ�}
					break;
				default:
					isHasPaper = false; //�쳣
					break;
				} 
				break;
			}   
		}
		return isHasPaper;
	}
}
