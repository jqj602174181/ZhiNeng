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

//实现数据包提交服务器、外置打印机打印和凭条打印
public class CommitActivity extends Activity {
					
	//操作步骤
	final static int STEP_COMMIT = 1;		//提交数据
	final static int STEP_PRINT_RECEIPT = 2;//打印凭单
	final static int STEP_PRINT = 3;		//打印凭条
	
	final static int MSG_COMMIT_OK = 1;		//提交交易正确
	final static int MSG_COMMIT_ERR = -1;	//提交交易错误
	final static int MSG_READY_PRINT = 2;	//准备打印完成
	
	final static int DELAYED_TIME = 500;	//延迟显示时间
	final static int INVALID_RES_ID = -1;	//无效的ID
	
	private int step = 0;					//当前的步骤
	private String commitTime = "";			//提交时间
	private String formNo = "";				//预填单号
	private String tranData;				//交易数据
	private String tranCode;				//交易码
	private String formName;				//填写的单子的名称
	private Formater.PrintPages pages;		//打印的页
	private AutofillSettings conf;			//配置
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
		setContentView( R.layout.activity_commit );

		//获得交易数据
		Intent intent = getIntent();
		tranData = intent.getStringExtra( "trandata" );
		tranCode = intent.getStringExtra( "trancode" );//交易码
		formName = intent.getStringExtra( "formName" );
		if( tranData == null || tranCode == null){
			returnError( "交易数据不完整，无法提交！" );
			return;
		}
		conf = new AutofillSettings(this);
		
		gotoStep( STEP_COMMIT );//提交交易提示
		
		//TODO:临时打调试
		System.out.println( "tranData=" + tranData );
		System.out.println( "tranCode=" + tranCode );
		System.out.println( "formName=" + formName );
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	//进入哪个业务步骤
	private void gotoStep( int step ){
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		this.step = step;
		switch( step ){
		case STEP_COMMIT:
			ft.add( R.id.BLOCK_STEP, new CommitFragment());
			commitTransaction( tranCode, tranData );//提交交易到服务器	
			break;
		case STEP_PRINT_RECEIPT:
			ReceiptPrintFragment fragment = new ReceiptPrintFragment();
			fragment.setPrintPages( pages );
			ft.replace( R.id.BLOCK_STEP, fragment);
			break;
		case STEP_PRINT:
			if( SpecialVersionConfig.getVersion().equals( "hnnx" )){
				if( formName.equals("电(信)汇凭证") || formName.equals("电子银行个人客户服务申请"))
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
	
	//返回错误
	public void returnError( String errorInfo ){
		Intent intent = new Intent();
		intent.putExtra( "error", errorInfo );
    	setResult( -1, intent );
    	finish();
	}
	
	//返回成功
	public void returnComplete(){
    	setResult( 0, null );
    	finish();
	}
	
	//进入下一步
	public void nextStep(){
		gotoStep( this.step + 1 );
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {			
			switch( msg.what ){
			case MSG_COMMIT_ERR://与服务端通信失败
				if( msg.obj != null){
					String errorinfo = (String)msg.obj;
					returnError( errorinfo );
				}
				else
					returnError( "与服务端通信失败！" );
				break;
			case MSG_COMMIT_OK://提交成功
				//显示准备打印
				TextView tvTip = (TextView)findViewById( R.id.LABEL_TIP );
				tvTip.setText( R.string.goint_to_print );
				sendEmptyMessageDelayed( MSG_READY_PRINT, DELAYED_TIME );
				break;
			case MSG_READY_PRINT://准备打印成功
				//判断是否需要外置打印凭单
				int templateId = getReceiptTemplateRawResource();
				if( templateId != INVALID_RES_ID && getReceiptPages(templateId)){
					gotoStep( STEP_PRINT_RECEIPT );
				}else{//不需要外置打印
					gotoStep( STEP_PRINT );
				}
				break;
			}
		}
		
	};
	
	//提交交易	
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
				
				//提交报文
				MessageComm comm = new MessageComm( ip, port );
				if( comm.setPushTransactionMsg( devNo, tranCode, orgNo, tranData )
						&& comm.commit()){//成功
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
	
	//获取凭单打印模板资源ID
	private int getReceiptTemplateRawResource(){
		//TODO:根据版本类型进行判断
		if( SpecialVersionConfig.getVersion().equals( "hnnx" )){
			return R.raw.hnnx;
		}
		return INVALID_RES_ID;
	}
	
	//生成凭单打印数据
	private boolean getReceiptPages( int templateId ){
		try{
			//解析模板
			File f = new File("/sdcard/hnnx.xml");  
		    InputStream template = new FileInputStream(f);   
		        
			//InputStream template = getResources().openRawResource( templateId );
			Formater formater = new Formater( template );
			template.close();
			
			//生成打印数据
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
