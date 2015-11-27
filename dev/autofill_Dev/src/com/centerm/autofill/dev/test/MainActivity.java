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
	public static final int REQ_READIDCARD = 1;//������֤
	public static final int REQ_READMSGCARD = 2;//���ſ�
	public static final int REQ_READICCARD = 3; //��IC��
	public static final int REQ_PRINT = 4;	 //��ӡ
	public static final int REQ_SELECT_CARD = 5;	 //��ӡ

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
				// ����������Ӧ�ó���
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "��ѡ��ˢ���ֿ�" );
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
				// ����������Ӧ�ó���
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "��ˢȡ���˵Ķ���֤" );
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
				// ����������Ӧ�ó���
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "��忨" );
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
				// ����������Ӧ�ó���
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "���IC��" );
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
		case REQ_READIDCARD://������֤�ɹ�
			TextView tvInfo = (TextView)findViewById( R.id.tv_person_info );
			if( resultCode == 0 ){//�ɹ�
				StringBuilder sb = new StringBuilder();
				sb.append( "������" );
				sb.append( data.getStringExtra("name") );
				sb.append("\n���֤�ţ�");
				sb.append( data.getStringExtra( "cardId" ));
				sb.append("\n�Ա�");
				sb.append( data.getStringExtra( "sex" ));
				sb.append("\n���壺");
				sb.append( data.getStringExtra( "nation" ));
				sb.append("\n���գ�");
				sb.append( data.getStringExtra( "birthday" ));
				sb.append("\n��ַ��");
				sb.append( data.getStringExtra( "address" ));
				sb.append("\nǩ�����أ�");
				sb.append( data.getStringExtra( "police" ));
				sb.append("\n��Ч���ڣ�");
				sb.append( data.getStringExtra( "validStart" ) + " - " + data.getStringExtra( "validEnd" ));

				tvInfo.setText( sb.toString() );    			
				ImageView imgView = (ImageView)findViewById( R.id.iv_photo );
				imgView.setImageBitmap( BitmapFactory.decodeByteArray( data.getByteArrayExtra("photo"), 
						0, data.getByteArrayExtra("photo").length));

			}else{
				tvInfo.setText( "�����֤ʧ��" );
			}
			break;
		case REQ_READMSGCARD:
			TextView tvCardNo = (TextView)findViewById( R.id.LABEL_MSGCARD );
			if( resultCode == 0 ){//�ɹ�
				tvCardNo.setText( "���ţ�" + data.getStringExtra("no"));
			}else{
				tvCardNo.setText( "���ſ�ʧ��" );
			}
			break;

		case REQ_READICCARD:
			TextView icCardNo = (TextView)findViewById( R.id.LABEL_MSGCARD );
			if( resultCode == 0 ){//�ɹ�
				icCardNo.setText( "IC���ţ�" + data.getStringExtra("no"));
			}else{
				icCardNo.setText( "��IC��ʧ��" );
			}
			break;
		case REQ_SELECT_CARD:


			TextView icCardNo1 = (TextView)findViewById( R.id.LABEL_MSGCARD );
			if(data==null){
				icCardNo1.setText( "��IC��ʧ��" );
				return;
			}
			if( resultCode == 0 ){//�ɹ�
				icCardNo1.setText( "IC���ţ�" + data.getExtras().getString("no"));
			}else{
				icCardNo1.setText( "��IC��ʧ��" );
			}
			break;
		}
		//IDCard.dealResult(requestCode, resultCode, data);
		//String No = MagCard.dealResult(requestCode, resultCode, data);
		//Log.i("ret", No);

	}

	//�����ⲿ��ӡ��
	private void testExtPrint(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put( "trancode", "8080");
		map.put( "sign_date_year", "2014");
		map.put( "sign_date_month", "5");
		map.put( "sign_date_day", "12");
		map.put( "drawer", "����");
		map.put( "payee", "����");
		map.put( "cur_account", "6225787890987");
		map.put( "payee_account", "677899301123");
		map.put( "from_bank", "����ũ��");
		map.put( "payee_bank", "ũҵ����");
		map.put( "money_capital", "Ҽ��Ԫ��");
		map.put( "money", "199990000.21");
		map.put( "check_type", "��֧ͨƱ");
		map.put( "check_count", "1");
		map.put( "check_no", "789876687");
		map.put( "account_type", "5");

		try {

			//���Դ�ӡ
			final String name = "com.centerm.autofill.dev.commit.CommitActivity";
			final String pkg = "com.centerm.autofill.dev";			
			try {
				//����Ӧ�ó���
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				JSONObject tranData = new JSONObject( map ); 
				intent.putExtra( "trandata", tranData.toString() );
				intent.putExtra( "trancode", "8080" );
				intent.putExtra( "formName", "���ʵ�" );
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
		map.put( "drawer", "����");
		map.put( "payee", "����");
		map.put( "cur_account", "6225787890987");
		map.put( "payee_account", "677899301123");
		map.put( "from_bank", "����ũ��");
		map.put( "payee_bank", "ũҵ����");
		map.put( "money_capital", "Ҽ��Ԫ��");
		map.put( "money", "199990000.21");
		map.put( "check_type", "��֧ͨƱ");
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
