package com.centerm.autofill.setting;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.text.TextUtils;

import com.centerm.autofill.setting.common.EEPROM;

public class AutofillSettings {
	//通信相关key
	public static final String NAME_SERVERIP = "ServerIp";			//服务端ip
	public static final String NAME_SERVERPORT = "ServerPort";		//服务器端口
	public static final String NAME_ORGNO = "OrgNo";			//服务器记录的机构号
	public static final String NAME_CHECKIDIP	= "CheckIdIp";		//联网核查服务端ip
	public static final String NAME_CHECKIDPORT	= "CheckIdPort";	//联网核查服务端端口
	public static final String NAME_CHECKIDORGNO = "CheckIdOrg";	//联网核查上送的机构号
	public static final String NAME_APPLAYOUT = "AppLayout";  //应用布局配置
	
	//本地网络key
	public static final String NAME_LOCALIP	= "LocalIp";			//本地以太网ip
	public static final String NAME_LOCALNETMASK = "LocalNetmask";	//本地以太网掩码
	public static final String NAME_LOCALGATEWAY = "LocalGateway";	//本地以太网网关
	
	//版本相关key
	public static final String NAME_CLIENTABBR = "ClientAbbr";	//客户名称英文缩写
	public static final String NAME_CLIENTNAME = "ClientName";	//客户中文名
	public static final String NAME_SYSVERION  = "SysVerion";	//系统版本号
	
	Context context;//上下文
	public AutofillSettings( Context context ){
		this.context = context;
	}
	
	//获得配置 
	private String getConf( String name ){
		ContentResolver cr = context.getContentResolver();
		
		//生成参数
		String[] results_columns = new String[]{
				SettingContentProvider.KEY_NAME,
				SettingContentProvider.KEY_VALUE
		};		
		String selection = SettingContentProvider.KEY_NAME + "='" + name + "'";
		String[] selectionArgs = null;
		String sortOrder = null;
		
		//查询数据库
		Cursor cursor = cr.query( SettingContentProvider.CONTENT_URI,
					results_columns, selection, selectionArgs, sortOrder);
		
		//得到值后返回，默认值是空
		String value = "";
		if( cursor.moveToFirst()){
			int columnIndex = cursor.getColumnIndexOrThrow( SettingContentProvider.KEY_VALUE );
			value = cursor.getString( columnIndex );
		}
		cursor.close();
		return value;		
	}
	
	//更新配置值
	private boolean setConf( String name, String value ){
		ContentResolver cr = context.getContentResolver();
		
		//生成行数据
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
	
	//get和set集合
	//服务器IP
	public String getServerIp(){
		return getConf( NAME_SERVERIP );
	}	
	public boolean setServerIp( String ip ){		
		return setConf( NAME_SERVERIP,ip );
	}
	
	//服务器端口
	public int getServerPort(){
		String port = getConf( NAME_SERVERPORT );
		return Integer.parseInt( port );
	}	
	public boolean setServerPort( int port ){
		if( port >= 1 && port <= 65535)
			return setConf( NAME_SERVERPORT, String.valueOf( port) );
		return false;
	}
	
	//机构号
	public String getOrgNO(){
		return getConf( NAME_ORGNO );
	}	
	public boolean setOrgNO( String no ){
		return setConf( NAME_ORGNO, no );
	}
	
	//本地IP
	public String getLocalIp(){
		//如果存储的值与本地配置不相同，则进行同步
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
	
	//本地子网掩码
	public String getLocalNetmask(){
		//如果存储的值与本地配置不相同，则进行同步
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
	
	//本地网关
	public String getLocalGateway(){
		//如果存储的值与本地配置不相同，则进行同步
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
	
	//联网核查服务器IP、端口和机构号
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
	
	//常量区
	//获得客户英文名
	public String getClientAbbr(){
		return getConf( NAME_CLIENTABBR );
	}
	//获得客户中文名
	public String getClientName(){
		return getConf( NAME_CLIENTNAME );
	}	
	
	//序列码
	public String getDevNo(){
		//取出前面非空字符，因此要trim
		return EEPROM.read(EEPROM.PRODUCTSN).trim();
	}
	
	//获取系统版本号
	public String getSysVersion(){
		return SystemUpdate.getSysVersion();
	}
}
