package com.centerm.autofill.dev.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.json.JSONObject;

import com.centerm.autofill.dev.R;
//import com.centerm.autofill.dev.card.CardSelectDialog;
import com.centerm.autofill.dev.printer.receipt.Formater;
import com.centerm.common.HalComService;

import android.os.Bundle;
import android.app.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final int REQ_READIDCARD = 1;//读二代证
	public static final int REQ_READMSGCARD = 2;//读磁卡
	public static final int REQ_READICCARD = 3; //读IC卡
	public static final int REQ_PRINT = 4;	 //打印
	public static final int REQ_SELECT_CARD = 5;	 //打印

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btn = (Button)findViewById( R.id.BTN_READ_IDCARD);
		btn.setOnClickListener( readIDCardListener );

		btn = (Button)findViewById( R.id.BTN_READ_MAGCARD);
		btn.setOnClickListener( readMsgCardListener );

		btn =(Button)findViewById(R.id.BTN_READ_ICCARD);
		btn.setOnClickListener(readICCardListener);

		btn = (Button)findViewById( R.id.BTN_TEST_PRINTER );
		btn.setOnClickListener( testprinterListener );

		btn = (Button)findViewById(R.id.BTN_TEST_SelectCard);
		btn.setOnClickListener(selectCardListener);

	}    
	private OnClickListener selectCardListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub


			final String name = "com.centerm.autofill.dev.card.CardSelectActivity";
			final String pkg = "com.centerm.autofill.dev";		
			try {
				// 启动重启的应用程序
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "请选择刷哪种卡" );
				MainActivity.this.startActivityForResult(intent, REQ_SELECT_CARD );
			} catch (Exception e) {
				e.printStackTrace();
			}
			//CardSelectDialog cardselect =new CardSelectDialog(MainActivity.this);
			//cardselect.show();
		}
	};
	private OnClickListener readIDCardListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final String name = "com.centerm.autofill.dev.idcard.ReadIDCardActivity";
			final String pkg = "com.centerm.autofill.dev";			
			try {
				// 启动重启的应用程序
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "请刷取款人的二代证" );
				MainActivity.this.startActivityForResult(intent, REQ_READIDCARD );
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	};

	private OnClickListener readMsgCardListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final String name = "com.centerm.autofill.dev.crt.ReadCrtActivity";
			final String pkg = "com.centerm.autofill.dev";			
			try {
				// 启动重启的应用程序
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "请插卡" );
				MainActivity.this.startActivityForResult(intent, REQ_READMSGCARD );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private OnClickListener readICCardListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final String name = "com.centerm.autofill.dev.iccard.ReadICCardActivity";
			final String pkg = "com.centerm.autofill.dev";			
			try {
				// 启动重启的应用程序
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "请插IC卡" );
				MainActivity.this.startActivityForResult(intent, REQ_READICCARD );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private OnClickListener testprinterListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			testExtPrint();
		}
	};


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch( requestCode ){
		case REQ_READIDCARD://读二代证成功
			TextView tvInfo = (TextView)findViewById( R.id.tv_person_info );
			if( resultCode == 0 ){//成功
				StringBuilder sb = new StringBuilder();
				sb.append( "姓名：" );
				sb.append( data.getStringExtra("name") );
				sb.append("\n身份证号：");
				sb.append( data.getStringExtra( "cardId" ));
				sb.append("\n性别：");
				sb.append( data.getStringExtra( "sex" ));
				sb.append("\n民族：");
				sb.append( data.getStringExtra( "nation" ));
				sb.append("\n生日：");
				sb.append( data.getStringExtra( "birthday" ));
				sb.append("\n地址：");
				sb.append( data.getStringExtra( "address" ));
				sb.append("\n签发机关：");
				sb.append( data.getStringExtra( "police" ));
				sb.append("\n有效日期：");
				sb.append( data.getStringExtra( "validStart" ) + " - " + data.getStringExtra( "validEnd" ));

				tvInfo.setText( sb.toString() );    			
				ImageView imgView = (ImageView)findViewById( R.id.iv_photo );
				imgView.setImageBitmap( BitmapFactory.decodeByteArray( data.getByteArrayExtra("photo"), 
						0, data.getByteArrayExtra("photo").length));

			}else{
				tvInfo.setText( "读身份证失败" );
			}
			break;
		case REQ_READMSGCARD:
			TextView tvCardNo = (TextView)findViewById( R.id.LABEL_MSGCARD );
			if( resultCode == 0 ){//成功
				tvCardNo.setText( "卡号：" + data.getStringExtra("no"));
			}else{
				tvCardNo.setText( "读磁卡失败" );
			}
			break;

		case REQ_READICCARD:
			TextView icCardNo = (TextView)findViewById( R.id.LABEL_MSGCARD );
			if( resultCode == 0 ){//成功
				icCardNo.setText( "IC卡号：" + data.getStringExtra("no"));
			}else{
				icCardNo.setText( "读IC卡失败" );
			}
			break;
		case REQ_SELECT_CARD:


			TextView icCardNo1 = (TextView)findViewById( R.id.LABEL_MSGCARD );
			if(data==null){
				icCardNo1.setText( "读IC卡失败" );
				return;
			}
			if( resultCode == 0 ){//成功
				icCardNo1.setText( "IC卡号：" + data.getExtras().getString("no"));
			}else{
				icCardNo1.setText( "读IC卡失败" );
			}
			break;
		}
		//IDCard.dealResult(requestCode, resultCode, data);
		//String No = MagCard.dealResult(requestCode, resultCode, data);
		//Log.i("ret", No);

	}

	//测试外部打印机
	private void testExtPrint(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put( "trancode", "8080");
		map.put( "sign_date_year", "2014");
		map.put( "sign_date_month", "5");
		map.put( "sign_date_day", "12");
		map.put( "drawer", "张三");
		map.put( "payee", "李四");
		map.put( "cur_account", "6225787890987");
		map.put( "payee_account", "677899301123");
		map.put( "from_bank", "河南农信");
		map.put( "payee_bank", "农业银行");
		map.put( "money_capital", "壹万元整");
		map.put( "money", "199990000.21");
		map.put( "check_type", "普通支票");
		map.put( "check_count", "1");
		map.put( "check_no", "789876687");
		map.put( "account_type", "5");

		try {

			//测试打印
			final String name = "com.centerm.autofill.dev.commit.CommitActivity";
			final String pkg = "com.centerm.autofill.dev";			
			try {
				//启动应用程序
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				JSONObject tranData = new JSONObject( map ); 
				intent.putExtra( "trandata", tranData.toString() );
				intent.putExtra( "trancode", "8080" );
				intent.putExtra( "formName", "进帐单" );
				MainActivity.this.startActivityForResult(intent, REQ_PRINT );
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testReceiptPrint(){
		InputStream template = getResources().openRawResource( R.raw.hnnx );
		HashMap<String,String> map = new HashMap<String,String>();
		map.put( "trancode", "8080");
		map.put( "sign_date_year", "2014");
		map.put( "sign_date_month", "5");
		map.put( "sign_date_day", "12");
		map.put( "drawer", "张三");
		map.put( "payee", "李四");
		map.put( "cur_account", "6225787890987");
		map.put( "payee_account", "677899301123");
		map.put( "from_bank", "河南农信");
		map.put( "payee_bank", "农业银行");
		map.put( "money_capital", "壹万元整");
		map.put( "money", "199990000.21");
		map.put( "check_type", "普通支票");
		map.put( "check_count", "1");
		map.put( "check_no", "789876687");
		map.put( "account_type", "5");
		Formater formater = new Formater( template );
		Formater.PrintPages pages = formater.format( map );

		try {
			System.out.println( "Pages=" + pages.getPageCount() );
			File file = new File( "/mnt/sdcard/print.log");
			FileOutputStream os = new FileOutputStream(file);
			byte[] data = pages.getPage( 0 );
			os.write( data );
			os.close();	

			HalComService comService = new HalComService();
			int fd = comService.openCom( 1 );
			comService.writeCom( fd, data, data.length );
			comService.closeCom( fd );
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
