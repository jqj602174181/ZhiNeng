package com.centerm.autofill.dev.idcard;

import com.centerm.autofill.dev.R;
import com.centerm.autofill.dev.idcard.ReadCardDialog;
import com.centerm.autofill.dev.idcard.ReadCardDialog.ReadCardListener;
import com.centerm.util.PinYin;
import com.centerm.util.financial.IdCardMsg;
import com.sdt.Common;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

public class ReadIDCardActivity extends Activity implements ReadCardListener{

	final String DEF_TITLE = "请刷居民身份证";		//默认提示的标题
	private TextView tvCountDown;					//倒计时	
	private String tipTitle;						//读二代证对话框的标题 
	private boolean bCanSkip;						//是否显示跳过按钮

	ReadCardDialog dlg;
	private Common common;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
		setContentView( R.layout.error_readidcard );
		tvCountDown = (TextView)findViewById( R.id.LABEL_TIMEMOUT );    
		//取出标题

		Intent intent = getIntent();
		tipTitle = intent.getStringExtra( "title" );
		if( tipTitle == null )
			tipTitle = DEF_TITLE;        
		bCanSkip = intent.getBooleanExtra( "canskip", false );

		//响应返回按钮
		TextView tvRet = (TextView)findViewById( R.id.LABEL_RETURN );
		tvRet.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				returnError( ReadCardDialog.RES_CANCEL );
			}
		});

		//响应“重新读取”按钮
		Button btnReread = (Button)findViewById( R.id.BTN_READ);
		btnReread.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showReadIDCardDialog();
				showContent( false );//不显示本身activity信息
				cdTimer.cancel();
			}
		});

		//开始初次读取二代证
		showReadIDCardDialog();        
		showContent( false );//不显示本身activity信息

		common = new Common();
		IntentFilter filter = new IntentFilter();//意图过滤器		
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);//USB设备拔出
		filter.addAction(common.ACTION_USB_PERMISSION);//自定义的USB设备请求授权
		registerReceiver(mUsbReceiver, filter);
	}

	@Override
	protected void onStop() {		
		super.onStop();
		cdTimer.cancel();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mUsbReceiver);
	}

	//广播接收器
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(common.ACTION_USB_PERMISSION.equals(action)){//USB设备未授权，从SDTAPI中发出的广播
				//重新调用 实例化
				dlg.initUSB(ReadIDCardActivity.this);
			}
		}
	};

	//是否显示错误提示内容区
	private void showContent( boolean shown ){
		View view = findViewById( R.id.VIEW_CONTENT );
		view.setVisibility( shown? View.VISIBLE : View.INVISIBLE );
	}

	//身份证阅读结束时
	@Override
	public void onReadCardComplete( int resCode, IdCardMsg person) {	
		
		Intent intent=new Intent();
		if( resCode == ReadCardDialog.RES_OK ){
			String name =  new String(person.name).trim();
			intent.putExtra( "name", name );
			intent.putExtra( "pinyin", PinYin.getPinYin(name));
			intent.putExtra( "sex", new String(person.sex).trim());
			intent.putExtra( "nation_str", person.nation_str); 
			intent.putExtra( "birth_year", person.birth_year); 
			intent.putExtra( "birth_month", person.birth_month); 
			intent.putExtra( "birth_day", person.birth_day); 
			intent.putExtra( "address", person.address); 
			intent.putExtra( "id_num", person.id_num); 
			intent.putExtra( "sign_office", person.sign_office); 
			intent.putExtra( "useful_s_date_year", person.useful_s_date_year);
			intent.putExtra( "useful_s_date_month", person.useful_s_date_month);
			intent.putExtra( "useful_s_date_day", person.useful_s_date_day); 
			intent.putExtra( "useful_e_date_year", person.useful_e_date_year);
			intent.putExtra( "useful_e_date_month", person.useful_e_date_month);
			intent.putExtra( "useful_e_date_day", person.useful_e_date_day); 
			intent.putExtra( "photo", person.photo );
			setResult( 0, intent );
			finish();
		}else if( resCode == ReadCardDialog.RES_CANCEL ||
				resCode == ReadCardDialog.RES_SKIP ){//取消
			returnError( resCode );
		}else{//失败
			cdTimer.start();
			showContent( true );
		}
	}	

	//身份证有效期转化为年月日
	private String formatDate( String strDate )
	{
		if( strDate.contains("长期"))
		{
			return strDate;
		}
		String strYear = strDate.substring(0, 4);
		String strMoth = strDate.substring(4, 6);
		String strDay = strDate.substring(6, 8);

		return strYear+"年"+strMoth+"月"+strDay+"日";
	}

	/******************倒计时***************/
	//倒计时，共60秒，每秒钟一次	。为了让客户能看到60，因此从61开始。
	private CountDownTimer cdTimer = new CountDownTimer( 61*1000, 1000 ){		

		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvCountDown.setText( "（返回倒计时：" + String.valueOf( g ) + "）");
		}

		public void onFinish() {
			tvCountDown.setText( "（返回倒计时：0）");	    	
			returnError( ReadCardDialog.RES_ERR );//结束返回错误	    	
		}
	};

	//显示读二代证的对话框
	private void showReadIDCardDialog(){
		dlg = new ReadCardDialog( tipTitle, bCanSkip, this);
		dlg.setReadCompleteListener(this);
		dlg.show( getFragmentManager(), "readcard" );
	}

	//返回错误
	private void returnError( int errcode ){
		Intent intent = new Intent();
		setResult( errcode, intent );
		finish();
	}

}
