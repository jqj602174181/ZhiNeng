package com.centerm.autofill.dev.test;

import java.io.UnsupportedEncodingException;

import com.centerm.autofill.dev.R;
import com.centerm.util.financial.PeriDeviceService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class testPrintActivity extends Activity {

	final static int STEP_COMMIT = 1;		//提交数据
	final static int STEP_PRINT_RECEIPT = 2;//打印凭单
	final static int STEP_PRINT = 3;		//打印凭条

	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView( R.layout.activity_commit );

		//收到数据
		Intent intent = getIntent();
		String data = "";
		if(intent != null){
			data = intent.getStringExtra("data");
		}
		System.out.println( "data=" + data );
		byte[] buf = null;
		try {
			buf = data.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		printReceipt(buf);


		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1){
					testPrintActivity.this.finish();
				}
			}
		};
	}

	private void printReceipt( final byte[] data){
		Thread printThread = new Thread(){
			@Override
			public void run() {
				try {
					PeriDeviceService.getInstance().PrintInfo(5, 9600, 20, data.length, data);
					Thread.sleep(1000);
					PeriDeviceService.getInstance().PopPaper(5, 9600, 20);

					handler.sendEmptyMessageDelayed(1, 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		printThread.start();
	}

}
