package com.centerm.autofill.dev.commit;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.json.JSONObject;

import com.centerm.autofill.dev.R;
import com.centerm.autofill.dev.printer.receipt.Formater;
import com.centerm.autofill.dev.version.SpecialVersionConfig;
import com.centerm.autofill.utils.AutofillSettings;
import com.centerm.util.StringUtil;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

//ʵ�����ݰ��ύ�����������ô�ӡ����ӡ��ƾ����ӡ
public class CommitActivity extends Activity {
					
	//��������
	final static int STEP_COMMIT = 1;		//�ύ����
	final static int STEP_PRINT_RECEIPT = 2;//��ӡƾ��
	final static int STEP_PRINT = 3;		//��ӡƾ��
	
	final static int MSG_COMMIT_OK = 1;		//�ύ������ȷ
	final static int MSG_COMMIT_ERR = -1;	//�ύ���״���
	final static int MSG_READY_PRINT = 2;	//׼����ӡ���
	
	final static int DELAYED_TIME = 500;	//�ӳ���ʾʱ��
	final static int INVALID_RES_ID = -1;	//��Ч��ID
	
	private int step = 0;					//��ǰ�Ĳ���
	private String commitTime = "";			//�ύʱ��
	private String formNo = "";				//Ԥ���
	private String tranData;				//��������
	private String tranCode;				//������
	private String formName;				//��д�ĵ��ӵ�����
	private Formater.PrintPages pages;		//��ӡ��ҳ
	private AutofillSettings conf;			//����
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȫ��
		setContentView( R.layout.activity_commit );

		//��ý�������
		Intent intent = getIntent();
		tranData = intent.getStringExtra( "trandata" );
		tranCode = intent.getStringExtra( "trancode" );//������
		formName = intent.getStringExtra( "formName" );
		if( tranData == null || tranCode == null){
			returnError( "�������ݲ��������޷��ύ��" );
			return;
		}
		conf = new AutofillSettings(this);
		
		gotoStep( STEP_COMMIT );//�ύ������ʾ
		
		//TODO:��ʱ�����
		System.out.println( "tranData=" + tranData );
		System.out.println( "tranCode=" + tranCode );
		System.out.println( "formName=" + formName );
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	//�����ĸ�ҵ����
	private void gotoStep( int step ){
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		this.step = step;
		switch( step ){
		case STEP_COMMIT:
			ft.add( R.id.BLOCK_STEP, new CommitFragment());
			commitTransaction( tranCode, tranData );//�ύ���׵�������	
			break;
		case STEP_PRINT_RECEIPT:
			ReceiptPrintFragment fragment = new ReceiptPrintFragment();
			fragment.setPrintPages( pages );
			ft.replace( R.id.BLOCK_STEP, fragment);
			break;
		case STEP_PRINT:
			if( SpecialVersionConfig.getVersion().equals( "hnnx" )){
				if( formName.equals("��(��)��ƾ֤") || formName.equals("�������и��˿ͻ���������"))
				{
					returnComplete();
					return;
				}
			}
			PrintFragment printfm = new PrintFragment();
			printfm.setPrintInfo( formName, formNo, commitTime, conf.getClientName());
			ft.replace( R.id.BLOCK_STEP, printfm );
			break;
		}		
		ft.commit();
	}
	
	//���ش���
	public void returnError( String errorInfo ){
		Intent intent = new Intent();
		intent.putExtra( "error", errorInfo );
    	setResult( -1, intent );
    	finish();
	}
	
	//���سɹ�
	public void returnComplete(){
    	setResult( 0, null );
    	finish();
	}
	
	//������һ��
	public void nextStep(){
		gotoStep( this.step + 1 );
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {			
			switch( msg.what ){
			case MSG_COMMIT_ERR://������ͨ��ʧ��
				if( msg.obj != null){
					String errorinfo = (String)msg.obj;
					returnError( errorinfo );
				}
				else
					returnError( "������ͨ��ʧ�ܣ�" );
				break;
			case MSG_COMMIT_OK://�ύ�ɹ�
				//��ʾ׼����ӡ
				TextView tvTip = (TextView)findViewById( R.id.LABEL_TIP );
				tvTip.setText( R.string.goint_to_print );
				sendEmptyMessageDelayed( MSG_READY_PRINT, DELAYED_TIME );
				break;
			case MSG_READY_PRINT://׼����ӡ�ɹ�
				//�ж��Ƿ���Ҫ���ô�ӡƾ��
				int templateId = getReceiptTemplateRawResource();
				if( templateId != INVALID_RES_ID && getReceiptPages(templateId)){
					gotoStep( STEP_PRINT_RECEIPT );
				}else{//����Ҫ���ô�ӡ
					gotoStep( STEP_PRINT );
				}
				break;
			}
		}
		
	};
	
	//�ύ����	
	private void commitTransaction( final String tranCode, final String tranData ){
		Thread commitThread = new Thread(){
			@Override
			public void run() {
				String ip = conf.getServerIp();
				int port = conf.getServerPort();
//				String devNo = conf.getDevNo();
				String devNo = "dev_a_15";
				String orgNo = conf.getOrgNO();
				Message msg = handler.obtainMessage(MSG_COMMIT_ERR);
				
				//�ύ����
				MessageComm comm = new MessageComm( ip, port );
				if( comm.setPushTransactionMsg( devNo, tranCode, orgNo, tranData )
						&& comm.commit()){//�ɹ�
					String response = comm.getResponseText();
					try{
						JSONObject obj = new JSONObject( response );
						commitTime = obj.getString( "time" );
						formNo = obj.getString( "code" );
						msg.what = MSG_COMMIT_OK;
					}catch(Exception e ){
						e.printStackTrace();
					}
					
				}
				msg.obj = comm.getErrorInfo();
				handler.sendMessageDelayed( msg, DELAYED_TIME );
				
			}
		};
		commitThread.start();
	}
	
	//��ȡƾ����ӡģ����ԴID
	private int getReceiptTemplateRawResource(){
		//TODO:���ݰ汾���ͽ����ж�
		if( SpecialVersionConfig.getVersion().equals( "hnnx" )){
			return R.raw.hnnx;
		}
		return INVALID_RES_ID;
	}
	
	//����ƾ����ӡ����
	private boolean getReceiptPages( int templateId ){
		try{
			//����ģ��
			File f = new File("/sdcard/hnnx.xml");  
		    InputStream template = new FileInputStream(f);   
		        
			//InputStream template = getResources().openRawResource( templateId );
			Formater formater = new Formater( template );
			template.close();
			
			//���ɴ�ӡ����
			HashMap<String, String> map = StringUtil.jsonToHashMap( tranData );
			pages = formater.format( map );
			if( pages.getPageCount() > 0 )
				return true;
		}catch( Exception e){
			e.printStackTrace();
		}
		return false;    	
	}

}
