package com.centerm.autofill.setting;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.text.TextUtils;

import com.centerm.autofill.setting.common.EEPROM;

public class AutofillSettings {
	//ͨ�����key
	public static final String NAME_SERVERIP = "ServerIp";			//�����ip
	public static final String NAME_SERVERPORT = "ServerPort";		//�������˿�
	public static final String NAME_ORGNO = "OrgNo";			//��������¼�Ļ�����
	public static final String NAME_CHECKIDIP	= "CheckIdIp";		//�����˲�����ip
	public static final String NAME_CHECKIDPORT	= "CheckIdPort";	//�����˲����˶˿�
	public static final String NAME_CHECKIDORGNO = "CheckIdOrg";	//�����˲����͵Ļ�����
	public static final String NAME_APPLAYOUT = "AppLayout";  //Ӧ�ò�������
	
	//��������key
	public static final String NAME_LOCALIP	= "LocalIp";			//������̫��ip
	public static final String NAME_LOCALNETMASK = "LocalNetmask";	//������̫������
	public static final String NAME_LOCALGATEWAY = "LocalGateway";	//������̫������
	
	//�汾���key
	public static final String NAME_CLIENTABBR = "ClientAbbr";	//�ͻ�����Ӣ����д
	public static final String NAME_CLIENTNAME = "ClientName";	//�ͻ�������
	public static final String NAME_SYSVERION  = "SysVerion";	//ϵͳ�汾��
	
	Context context;//������
	public AutofillSettings( Context context ){
		this.context = context;
	}
	
	//������� 
	private String getConf( String name ){
		ContentResolver cr = context.getContentResolver();
		
		//���ɲ���
		String[] results_columns = new String[]{
				SettingContentProvider.KEY_NAME,
				SettingContentProvider.KEY_VALUE
		};		
		String selection = SettingContentProvider.KEY_NAME + "='" + name + "'";
		String[] selectionArgs = null;
		String sortOrder = null;
		
		//��ѯ���ݿ�
		Cursor cursor = cr.query( SettingContentProvider.CONTENT_URI,
					results_columns, selection, selectionArgs, sortOrder);
		
		//�õ�ֵ�󷵻أ�Ĭ��ֵ�ǿ�
		String value = "";
		if( cursor.moveToFirst()){
			int columnIndex = cursor.getColumnIndexOrThrow( SettingContentProvider.KEY_VALUE );
			value = cursor.getString( columnIndex );
		}
		cursor.close();
		return value;		
	}
	
	//��������ֵ
	private boolean setConf( String name, String value ){
		ContentResolver cr = context.getContentResolver();
		
		//����������
		ContentValues updateValues = new ContentValues();
		updateValues.put( SettingContentProvider.KEY_VALUE, value );
		
		String where = SettingContentProvider.KEY_NAME + "='" + name + "'";;
		String[] selectionArgs = null;
		int updateRowCount = cr.update( SettingContentProvider.CONTENT_URI,
				updateValues, where, selectionArgs );
		
		if( updateRowCount > 0 ){
			return true;
		}else{
			return false;
		}
	}
	
	//get��set����
	//������IP
	public String getServerIp(){
		return getConf( NAME_SERVERIP );
	}	
	public boolean setServerIp( String ip ){		
		return setConf( NAME_SERVERIP,ip );
	}
	
	//�������˿�
	public int getServerPort(){
		String port = getConf( NAME_SERVERPORT );
		return Integer.parseInt( port );
	}	
	public boolean setServerPort( int port ){
		if( port >= 1 && port <= 65535)
			return setConf( NAME_SERVERPORT, String.valueOf( port) );
		return false;
	}
	
	//������
	public String getOrgNO(){
		return getConf( NAME_ORGNO );
	}	
	public boolean setOrgNO( String no ){
		return setConf( NAME_ORGNO, no );
	}
	
	//����IP
	public String getLocalIp(){
		//����洢��ֵ�뱾�����ò���ͬ�������ͬ��
		String ip = getConf( NAME_LOCALIP );		
		String sysip = Settings.System.getString(context.getContentResolver(), "ethernet_static_ip");
		if( !TextUtils.isEmpty( sysip) && !ip.equals( sysip )){
			setConf( NAME_LOCALIP,sysip );
			return sysip;			
		}
		return ip;
	}	
	public boolean setLocalIp( String ip ){
		ContentResolver cr = context.getContentResolver();
		Settings.System.putString( cr, "ethernet_static_ip", ip );		
		return setConf( NAME_LOCALIP,ip );
	}
	
	//������������
	public String getLocalNetmask(){
		//����洢��ֵ�뱾�����ò���ͬ�������ͬ��
		String netmask = getConf( NAME_LOCALNETMASK );		
		String sysmask = Settings.System.getString(context.getContentResolver(), "ethernet_static_netmask");
		if( !TextUtils.isEmpty( sysmask) && !netmask.equals( sysmask )){
			setConf( NAME_LOCALNETMASK, sysmask );
			return sysmask;			
		}
		return netmask;
	}	
	public boolean setLocalNetmask( String netmask ){
		ContentResolver cr = context.getContentResolver();
		Settings.System.putString( cr, "ethernet_static_netmask", netmask );
		return setConf( NAME_LOCALNETMASK,netmask );
	}
	
	//��������
	public String getLocalGateway(){
		//����洢��ֵ�뱾�����ò���ͬ�������ͬ��
		String gateway = getConf( NAME_LOCALGATEWAY );		
		String sysgateway = Settings.System.getString(context.getContentResolver(), "ethernet_static_gateway");
		if( !TextUtils.isEmpty( sysgateway) && !gateway.equals( sysgateway )){
			setConf( NAME_LOCALGATEWAY, sysgateway );
			return sysgateway;			
		}
		return gateway;
	}	
	public boolean setLocalGateway( String gateway ){
		ContentResolver cr = context.getContentResolver();
		Settings.System.putString( cr, "ethernet_static_gateway", gateway );
		return setConf( NAME_LOCALGATEWAY, gateway );
	}
	
	//�����˲������IP���˿ںͻ�����
	public String getCheckIDServerIp(){
		return getConf( NAME_CHECKIDIP );
	}	
	public boolean setCheckIDServerIp( String ip ){		
		return setConf( NAME_CHECKIDIP,ip );
	}	
	public int getCheckIDServerPort(){
		String port = getConf( NAME_CHECKIDPORT );
		return Integer.parseInt( port );
	}	
	public boolean setCheckIDServerPort( int port ){
		if( port >= 1 && port <= 65535)
			return setConf( NAME_CHECKIDPORT, String.valueOf( port) );
		return false;
	}
	public String getCheckIDServerOrg(){
		return getConf( NAME_CHECKIDORGNO );
	}	
	public boolean setCheckIDServerOrg( String org ){		
		return setConf( NAME_CHECKIDORGNO,org );
	}
	
	//������
	//��ÿͻ�Ӣ����
	public String getClientAbbr(){
		return getConf( NAME_CLIENTABBR );
	}
	//��ÿͻ�������
	public String getClientName(){
		return getConf( NAME_CLIENTNAME );
	}	
	
	//������
	public String getDevNo(){
		//ȡ��ǰ��ǿ��ַ������Ҫtrim
		return EEPROM.read(EEPROM.PRODUCTSN).trim();
	}
	
	//��ȡϵͳ�汾��
	public String getSysVersion(){
		return SystemUpdate.getSysVersion();
	}
}
