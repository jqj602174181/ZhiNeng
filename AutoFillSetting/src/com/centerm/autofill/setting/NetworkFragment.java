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
 * ��������
 */
public class NetworkFragment extends Fragment {
	
	private static final String deviceRegisterAction="com.centerm.autofill.DeviceRegister";//�豸ע����Ҫ�õ��Ĺ㲥
	private EditText edLocalIp;//����IP�ؼ�
	private EditText edNetmask;//��������ؼ�
	private EditText edGateway;//Ĭ�����ؿؼ�
	private EditText edServerIp;//�����IP�ؼ�
	private EditText edServerPort;//����˶˿ڿؼ�
	private EditText edOrgNo;		//������
	private TextView tvTip;//��ʾ�ؼ�
	
	AutofillSettings conf = null;//����
	Activity context;//��activity
	
	private String sLocalIp;//����IP
	private String sNetmask;//��������
	private String sGateway;//Ĭ������
	private String sServerIp;//�����IP
	private String sServerPort;//����˶˿�
	private String sOrgNo;		//������
	
	private boolean isConnectRight=false;//��¼��ͨ���Ƿ�����
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.manage_fm_network, container, false );
		context = getActivity();
		
		//��ȡ�ؼ�
		edLocalIp = (EditText)view.findViewById( R.id.EDIT_LOCALIP);
		edNetmask = (EditText)view.findViewById( R.id.EDIT_NETMASK);
		edGateway = (EditText)view.findViewById( R.id.EDIT_GATEWAY);
		edServerIp = (EditText)view.findViewById( R.id.EDIT_SERVERIP);
		edServerPort = (EditText)view.findViewById( R.id.EDIT_SERVERPORT );
		edOrgNo = (EditText)view.findViewById( R.id.EDIT_ORGNO );
		tvTip = (TextView)view.findViewById( R.id.LABEL_TIP );
		
		//��ȡ���ò����½���
		conf = new AutofillSettings( context );
		edServerIp.setText( conf.getServerIp() );
		edServerPort.setText( String.valueOf(conf.getServerPort()));
		edOrgNo.setText( conf.getOrgNO() );
		
		//������������
		edLocalIp.setText( conf.getLocalIp() );
		edNetmask.setText( conf.getLocalNetmask() );
		edGateway.setText( conf.getLocalGateway() );		
		
		//����IP��ַ����
		InputFilter[] filters = new InputFilter[]{new IPInputFilter()};
		edLocalIp.setFilters( filters );
		edNetmask.setFilters( filters );
		edGateway.setFilters( filters );
		edServerIp.setFilters( filters );
		
		//����ͼ��ͨ�Ų���
		Button btn = (Button)view.findViewById( R.id.BTN_SAVE );
		btn.setOnClickListener( saveListener );
		btn = (Button)view.findViewById( R.id.BTN_CHECK );
		btn.setOnClickListener( checkNetListener );
		return view;		
	}
	
	//�������ð�ť�Ĵ���
	private OnClickListener saveListener = new OnClickListener() {			
		@Override
		public void onClick(View v) {
			sServerIp = edServerIp.getText().toString();
			sServerPort = edServerPort.getText().toString();
			sLocalIp = edLocalIp.getText().toString();
			sNetmask = edNetmask.getText().toString();
			sGateway = edGateway.getText().toString();
			sOrgNo = edOrgNo.getText().toString();
			
			//У��IP���˿�
			if( !StringUtil.isIp( sLocalIp )){
				showTip( "����IP�������" );
			}else if( !StringUtil.isIp( sNetmask )){
				showTip( "���������������" );
			}else if( !StringUtil.isIp( sGateway )){
				showTip( "Ĭ�������������" );
			}else if( !StringUtil.isIp(sServerIp )){
				showTip( "������IP�������" );
			}else if( !StringUtil.isPort( sServerPort ) ){
				showTip( "����������˿����������Ч��Χ��1~65535��" );
			}else{//OK
				conf.setServerIp( sServerIp );
				conf.setServerPort( Integer.valueOf(sServerPort));
				conf.setOrgNO( sOrgNo );
				
				//��Ҫ����android.permission.WRITE_SETTINGSȨ��
				ContentResolver cr = context.getContentResolver();
				//����conf��ִ��Settings.System.putString
				conf.setLocalIp(sLocalIp);
				conf.setLocalGateway(sGateway);
				conf.setLocalNetmask(sNetmask);				
				Settings.System.putString( cr, "ethernet_static_dns1", "8.8.8.8" );
				Settings.System.putInt(cr, "ethernet_use_static_ip", 1 );
				
				//��Ҫ����android.permission.CHANGE_NETWORK_STATE���ұ���android.uid.system�û�Ȩ��
				boolean enable = Secure.getInt(cr, "ethernet_on", 1) == 1;
				if( enable ){
					EthernetManager.setEnabled( context.getSystemService( "ethernet"),  false );
					//Secure.putInt( cr, "ethernet_on" , 0 );ϵͳ���ó����ڵĴ�����õ���Secure���������ڲ���root app���޷�����
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					//Secure.putInt(cr, "ethernet_on", 1);
					EthernetManager.setEnabled( context.getSystemService( "ethernet"), true );
				}
				
				//����ͨ�ųɹ�,�����豸ע��Ĺ㲥�������Ƿ�ɹ��������͹㲥��
				//if(isConnectRight){
					Intent intent = new Intent(deviceRegisterAction);
					getActivity().sendBroadcast(intent);
					//}
				showTip( true, "���óɹ���", false );
			}
		}
	};
	
	
	//���ͨ�Ű�ť�Ĵ���
	private OnClickListener checkNetListener = new OnClickListener() {			
		@Override
		public void onClick(View v) {
			checkConnectToServer();	
			showTip( true, "���ڼ������ͨ���Ƿ�����...", false );
		}			
	};
	
	//���¼����UI
	private final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if( msg.what == 0){
				showTip( true, "���������������", false );
				isConnectRight = true;
			}else{
				showTip( "����������쳣�����������Ƿ���ȷ��");
				isConnectRight = false;
			}
		}		
	};
	
	//��������ͨ��
	private void checkConnectToServer(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				String ip = edServerIp.getText().toString();
				String port = edServerPort.getText().toString();
				if(!StringUtil.isIp( ip )){
					showTip( "������IP�������" );
					return;
				}
				else if(!StringUtil.isPort( port )){
					showTip( "����������˿����������Ч��Χ��1~65535��" );
					return;
				}
				
				Message msg = handler.obtainMessage();
				
				//������������
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
	
	//��ʾ��������ʾ��Ϣ
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
