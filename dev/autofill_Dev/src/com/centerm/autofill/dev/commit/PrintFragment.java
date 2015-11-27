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

//打印排队凭条
public class PrintFragment extends Fragment implements BuildinPrinter.IPrintComplete {
	final static int MSG_PRINT_COMPLETE = 1;	//打印完成
	final static int MSG_PRINT_ERROR = -1;		//打印出错了
	final static int MSG_CANCEL_TIMER = 2;		//取消定时器

	private String tranName;		//交易名称
	private String formNo;			//填单号
	private String createdTime;		//创建时间
	private String strLogo;			//客户logo信息
	private TextView tvCountDown;			//成功时倒计时	
	private TextView tvCountDownNoPaper;	//缺纸时倒计时
	//	private BuildinPrinter printer;			//内置打印机

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

		//立即退出功能
		Button btn = (Button)view.findViewById( R.id.BTN_EXIT );
		btn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View view) {
				complete();
			}
		});

		//立即打印
		btn = (Button)view.findViewById( R.id.BTN_PRINT );
		btn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View view) {
				startPrint();
			}
		});

		//如果有纸，则进行打印，否则显示缺纸
		//		printer = BuildinPrinter.getInstance();
		//		printer.setPrintCompleteListener( this );
		if( hasPaper() ){
			//		if(true){
			//显示正在打印
			View viewPrinting = view.findViewById( R.id.BLOCK_PRINTING );
			viewPrinting.setVisibility( View.VISIBLE );
			//			printer.print(tranName, formNo, createdTime, strLogo);
			startPrint2();
		}else{//打印检测到缺纸
			View viewNoPaper = view.findViewById( R.id.BLOCK_NOPAPER );
			viewNoPaper.setVisibility( View.VISIBLE );
			tvCountDownNoPaper.setText( "（返回倒计时：60）");
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
				//取消正在打印显示
				View container = getView();
				View view = container.findViewById( R.id.BLOCK_PRINTING );
				view.setVisibility( View.GONE );

				//显示打印成功提示并倒计时
				view = container.findViewById( R.id.BLOCK_PRINTEND );
				view.setVisibility( View.VISIBLE );
				cdTimer.start();
				break;
			case MSG_PRINT_ERROR://打印出错了
				printCompleteInError();
				break;
			case MSG_CANCEL_TIMER:
				noPaperTimer.cancel();
				break;
			}
		}
	};

	//打印完成
	public void onPrintComplete( boolean isSuccess ){		
		handler.sendEmptyMessage( MSG_PRINT_COMPLETE );
	}

	//打印成功时的倒计时，共10秒，每秒钟一次	。为了让客户能看到10，因此从11开始。
	private CountDownTimer cdTimer = new CountDownTimer( 11*1000, 1000 ){		

		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvCountDown.setText( "（" + String.valueOf( g ) + "秒后将退出）");
		}

		public void onFinish() {
			tvCountDown.setText( "（正在退出...）");
			complete();
		}
	};

	//缺纸倒计时，共60秒，每秒钟一次	。为了让客户能看到60，因此从61开始。
	private CountDownTimer noPaperTimer = new CountDownTimer( 61*1000, 1000 ){	


		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvCountDownNoPaper.setText( "（返回倒计时：" + String.valueOf( g ) + "）");

			//已经有纸张了，继续打印
			if( hasPaper() ){
				handler.sendEmptyMessage( MSG_CANCEL_TIMER );//从源码看不能在CountDownTimer的onTick中取消该定时器，
				//且只能在界面线程中取消，因此采用handler实现
				View view = getView();
				View viewPrint = view.findViewById( R.id.BTN_PRINT );
				viewPrint.setVisibility( View.VISIBLE );
				tvCountDownNoPaper.setText( "装纸完成后，请继续打印" );
			}
		}

		public void onFinish() {
			tvCountDownNoPaper.setText( "（返回倒计时：0）");
			CommitActivity activity = (CommitActivity)getActivity();
			activity.returnError( "由于缺纸，未完成打印！" );
		}
	};

	//成功退出
	private void complete(){
		CommitActivity activity = (CommitActivity)getActivity();
		activity.returnComplete();
	}

	//成功退出
	private void printCompleteInError(){
		CommitActivity activity = (CommitActivity)getActivity();
		activity.returnError( "打印出错" );
	}

	private void startPrint2() {
		//生成标题和内容
		final String title = " \n     " + strLogo +"\n";//前面加一个\n防止第一行缩行
		final String tranNLable = "                您办理的业务是：\n";
		String space = "";
		for(int n = (24 - tranName.length()*2) / 2; n>0; n--){		// 没倍宽倍高时，一行打印48个英文或24个中文
			space += " ";
		}
		final String tranN = space + tranName + "\n";
		final String formN = "    预填单号: "+formNo + "\n";
		final String time = "            时间:" + createdTime +"\n";

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					printContent(title, 1);
					printContent(tranNLable, 0);
					printContent(tranN, 1);
					printContent(formN, 1);
					printContent(time, 0);
					//换行
					mSerial.write(HexUtils.hexStr2FeedLine(8));
					//切纸0全切，1半切
					mSerial.write(HexUtils.hexStr2CutPaper(0));	

					onPrintComplete( true );
				} catch (Exception e) {
					e.printStackTrace();
					onPrintComplete(false);//发生了异常也要提醒完成
				}
			}
		}).start();
	}
	
	private void printContent(String str, int lev){
		byte[] bytes = HexUtils.GetPrintData(str);

		mSerial.write(HexUtils.SetPrintModel(lev, lev));
		// 设置字体旋转
		mSerial.write(HexUtils.SetPrintRotate(0));	
		mSerial.write(bytes, bytes.length);
		//换行
		byte[] etBytes2 = HexUtils.hexStr2FeedLine(1);
		mSerial.write(etBytes2, etBytes2.length);
	}

	//打印
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
					isHasPaper = true; //正常
					break;
				case 0x70:
					isHasPaper = false; //缺衹
					break;
				default:
					isHasPaper = false; //异常
					break;
				} 
				break;
			}   
		}
		return isHasPaper;
	}
}
