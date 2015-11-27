package com.centerm.autofill.setting;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.centerm.autofill.setting.common.SocketClient;
import com.centerm.autofill.setting.manage.EthernetManager;
import com.centerm.autofill.setting.utils.IPInputFilter;
import com.centerm.autofill.setting.utils.StringUtil;


/**
 * 网络设置
 */
public class NetworkFragment extends Fragment {
	
	private static final String deviceRegisterAction="com.centerm.autofill.DeviceRegister";//设备注册所要用到的广播
	private EditText edLocalIp;//本地IP控件
	private EditText edNetmask;//子网掩码控件
	private EditText edGateway;//默认网关控件
	private EditText edServerIp;//服务端IP控件
	private EditText edServerPort;//服务端端口控件
	private EditText edOrgNo;		//机构号
	private TextView tvTip;//提示控件
	
	AutofillSettings conf = null;//配置
	Activity context;//父activity
	
	private String sLocalIp;//本地IP
	private String sNetmask;//子网掩码
	private String sGateway;//默认网关
	private String sServerIp;//服务端IP
	private String sServerPort;//服务端端口
	private String sOrgNo;		//机构号
	
	private boolean isConnectRight=false;//记录下通信是否正常
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.manage_fm_network, container, false );
		context = getActivity();
		
		//获取控件
		edLocalIp = (EditText)view.findViewById( R.id.EDIT_LOCALIP);
		edNetmask = (EditText)view.findViewById( R.id.EDIT_NETMASK);
		edGateway = (EditText)view.findViewById( R.id.EDIT_GATEWAY);
		edServerIp = (EditText)view.findViewById( R.id.EDIT_SERVERIP);
		edServerPort = (EditText)view.findViewById( R.id.EDIT_SERVERPORT );
		edOrgNo = (EditText)view.findViewById( R.id.EDIT_ORGNO );
		tvTip = (TextView)view.findViewById( R.id.LABEL_TIP );
		
		//读取配置并更新界面
		conf = new AutofillSettings( context );
		edServerIp.setText( conf.getServerIp() );
		edServerPort.setText( String.valueOf(conf.getServerPort()));
		edOrgNo.setText( conf.getOrgNO() );
		
		//更新网络连接
		edLocalIp.setText( conf.getLocalIp() );
		edNetmask.setText( conf.getLocalNetmask() );
		edGateway.setText( conf.getLocalGateway() );		
		
		//设置IP地址过滤
		InputFilter[] filters = new InputFilter[]{new IPInputFilter()};
		edLocalIp.setFilters( filters );
		edNetmask.setFilters( filters );
		edGateway.setFilters( filters );
		edServerIp.setFilters( filters );
		
		//保存和检测通信操作
		Button btn = (Button)view.findViewById( R.id.BTN_SAVE );
		btn.setOnClickListener( saveListener );
		btn = (Button)view.findViewById( R.id.BTN_CHECK );
		btn.setOnClickListener( checkNetListener );
		return view;		
	}
	
	//保存设置按钮的处理
	private OnClickListener saveListener = new OnClickListener() {			
		@Override
		public void onClick(View v) {
			sServerIp = edServerIp.getText().toString();
			sServerPort = edServerPort.getText().toString();
			sLocalIp = edLocalIp.getText().toString();
			sNetmask = edNetmask.getText().toString();
			sGateway = edGateway.getText().toString();
			sOrgNo = edOrgNo.getText().toString();
			
			//校验IP、端口
			if( !StringUtil.isIp( sLocalIp )){
				showTip( "本地IP输入错误！" );
			}else if( !StringUtil.isIp( sNetmask )){
				showTip( "子网掩码输入错误！" );
			}else if( !StringUtil.isIp( sGateway )){
				showTip( "默认网关输入错误！" );
			}else if( !StringUtil.isIp(sServerIp )){
				showTip( "服务器IP输入错误！" );
			}else if( !StringUtil.isPort( sServerPort ) ){
				showTip( "服务器服务端口输入错误！有效范围是1~65535。" );
			}else{//OK
				conf.setServerIp( sServerIp );
				conf.setServerPort( Integer.valueOf(sServerPort));
				conf.setOrgNO( sOrgNo );
				
				//需要申请android.permission.WRITE_SETTINGS权限
				ContentResolver cr = context.getContentResolver();
				//改在conf中执行Settings.System.putString
				conf.setLocalIp(sLocalIp);
				conf.setLocalGateway(sGateway);
				conf.setLocalNetmask(sNetmask);				
				Settings.System.putString( cr, "ethernet_static_dns1", "8.8.8.8" );
				Settings.System.putInt(cr, "ethernet_use_static_ip", 1 );
				
				//需要申请android.permission.CHANGE_NETWORK_STATE，且必须android.uid.system用户权限
				boolean enable = Secure.getInt(cr, "ethernet_on", 1) == 1;
				if( enable ){
					EthernetManager.setEnabled( context.getSystemService( "ethernet"),  false );
					//Secure.putInt( cr, "ethernet_on" , 0 );系统设置程序内的代码采用的是Secure，但是由于不是root app，无法调用
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					//Secure.putInt(cr, "ethernet_on", 1);
					EthernetManager.setEnabled( context.getSystemService( "ethernet"), true );
				}
				
				//网络通信成功,发送设备注册的广播。不管是否成功，都发送广播。
				//if(isConnectRight){
					Intent intent = new Intent(deviceRegisterAction);
					getActivity().sendBroadcast(intent);
					//}
				showTip( true, "设置成功。", false );
			}
		}
	};
	
	
	//检测通信按钮的处理
	private OnClickListener checkNetListener = new OnClickListener() {			
		@Override
		public void onClick(View v) {
			checkConnectToServer();	
			showTip( true, "正在检测服务端通信是否正常...", false );
		}			
	};
	
	//更新检测结果UI
	private final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if( msg.what == 0){
				showTip( true, "服务端连接正常。", false );
				isConnectRight = true;
			}else{
				showTip( "服务端连接异常！请检查配置是否正确。");
				isConnectRight = false;
			}
		}		
	};
	
	//检测服务器通信
	private void checkConnectToServer(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				String ip = edServerIp.getText().toString();
				String port = edServerPort.getText().toString();
				if(!StringUtil.isIp( ip )){
					showTip( "服务器IP输入错误！" );
					return;
				}
				else if(!StringUtil.isPort( port )){
					showTip( "服务器服务端口输入错误！有效范围是1~65535。" );
					return;
				}
				
				Message msg = handler.obtainMessage();
				
				//尝试连接网络
				SocketClient client = new SocketClient();
				if( client.SocketConnect( ip, Integer.valueOf(port), 2 )){
					client.SocketClose();
					msg.what = 0;
				}else{
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		});
		thread.start();
	}
	
	//显示或隐藏提示信息
	private void showTip( String text ){ showTip( true, text, true );}
	private void showTip( boolean shown, String text, boolean iserr ){
		if( shown ){
			if( iserr )
				tvTip.setTextColor( getResources().getColor(R.color.red_tiperr) );
			else
				tvTip.setTextColor( getResources().getColor(R.color.green_tipok) );
			tvTip.setText( text );
			tvTip.setVisibility( View.VISIBLE );
		}else{
			tvTip.setVisibility( View.INVISIBLE );
		}
	}
}
