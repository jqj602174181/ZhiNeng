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

	final String DEF_TITLE = "��ˢ�������֤";		//Ĭ����ʾ�ı���
	private TextView tvCountDown;					//����ʱ	
	private String tipTitle;						//������֤�Ի���ı��� 
	private boolean bCanSkip;						//�Ƿ���ʾ������ť

	ReadCardDialog dlg;
	private Common common;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȫ��
		setContentView( R.layout.error_readidcard );
		tvCountDown = (TextView)findViewById( R.id.LABEL_TIMEMOUT );    
		//ȡ������

		Intent intent = getIntent();
		tipTitle = intent.getStringExtra( "title" );
		if( tipTitle == null )
			tipTitle = DEF_TITLE;        
		bCanSkip = intent.getBooleanExtra( "canskip", false );

		//��Ӧ���ذ�ť
		TextView tvRet = (TextView)findViewById( R.id.LABEL_RETURN );
		tvRet.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				returnError( ReadCardDialog.RES_CANCEL );
			}
		});

		//��Ӧ�����¶�ȡ����ť
		Button btnReread = (Button)findViewById( R.id.BTN_READ);
		btnReread.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showReadIDCardDialog();
				showContent( false );//����ʾ����activity��Ϣ
				cdTimer.cancel();
			}
		});

		//��ʼ���ζ�ȡ����֤
		showReadIDCardDialog();        
		showContent( false );//����ʾ����activity��Ϣ

		common = new Common();
		IntentFilter filter = new IntentFilter();//��ͼ������		
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);//USB�豸�γ�
		filter.addAction(common.ACTION_USB_PERMISSION);//�Զ����USB�豸������Ȩ
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

	//�㲥������
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(common.ACTION_USB_PERMISSION.equals(action)){//USB�豸δ��Ȩ����SDTAPI�з����Ĺ㲥
				//���µ��� ʵ����
				dlg.initUSB(ReadIDCardActivity.this);
			}
		}
	};

	//�Ƿ���ʾ������ʾ������
	private void showContent( boolean shown ){
		View view = findViewById( R.id.VIEW_CONTENT );
		view.setVisibility( shown? View.VISIBLE : View.INVISIBLE );
	}

	//���֤�Ķ�����ʱ
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
				resCode == ReadCardDialog.RES_SKIP ){//ȡ��
			returnError( resCode );
		}else{//ʧ��
			cdTimer.start();
			showContent( true );
		}
	}	

	//���֤��Ч��ת��Ϊ������
	private String formatDate( String strDate )
	{
		if( strDate.contains("����"))
		{
			return strDate;
		}
		String strYear = strDate.substring(0, 4);
		String strMoth = strDate.substring(4, 6);
		String strDay = strDate.substring(6, 8);

		return strYear+"��"+strMoth+"��"+strDay+"��";
	}

	/******************����ʱ***************/
	//����ʱ����60�룬ÿ����һ��	��Ϊ���ÿͻ��ܿ���60����˴�61��ʼ��
	private CountDownTimer cdTimer = new CountDownTimer( 61*1000, 1000 ){		

		public void onTick( long millisUntilFinished ) {
			long g = millisUntilFinished/1000;
			tvCountDown.setText( "�����ص���ʱ��" + String.valueOf( g ) + "��");
		}

		public void onFinish() {
			tvCountDown.setText( "�����ص���ʱ��0��");	    	
			returnError( ReadCardDialog.RES_ERR );//�������ش���	    	
		}
	};

	//��ʾ������֤�ĶԻ���
	private void showReadIDCardDialog(){
		dlg = new ReadCardDialog( tipTitle, bCanSkip, this);
		dlg.setReadCompleteListener(this);
		dlg.show( getFragmentManager(), "readcard" );
	}

	//���ش���
	private void returnError( int errcode ){
		Intent intent = new Intent();
		setResult( errcode, intent );
		finish();
	}

}
