package com.centerm.autofill.utils;

import com.centerm.common.EEPROM;
import com.centerm.common.SystemInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

//用于读取设置
public class AutofillSettings {
	//通信相关key
	public static final String NAME_SERVERIP = "ServerIp";			//服务端ip
	public static final String NAME_SERVERPORT = "ServerPort";		//服务器端口
	public static final String NAME_ORGNO = "OrgNo";				//服务器记录的机构号
	public static final String NAME_CHECKIDIP	= "CheckIdIp";		//联网核查服务端ip
	public static final String NAME_CHECKIDPORT	= "CheckIdPort";	//联网核查服务端端口
	public static final String NAME_CHECKIDORGNO = "CheckIdOrg";	//联网核查上送的机构号
	
	//本地网络key
	public static final String NAME_LOCALIP	= "LocalIp";			//本地以太网ip
	public static final String NAME_LOCALNETMASK = "LocalNetmask";	//本地以太网掩码
	public static final String NAME_LOCALGATEWAY = "LocalGateway";	//本地以太网网关
	
	//版本相关key
	public static final String NAME_CLIENTABBR = "ClientAbbr";	//客户名称英文缩写
	public static final String NAME_CLIENTNAME = "ClientName";	//客户中文名
	public static final String NAME_SYSVERION  = "SysVerion";	//系统版本号
	
	private static final Uri CONTENT_URI = 
			Uri.parse( "content://com.centerm.autofill.settingprovider/settings");

	//列表变量名
	private static final String KEY_NAME = "name";	//配置名称
	private static final String KEY_VALUE = "value";	//配置的值
	
	Context context;//上下文
	public AutofillSettings( Context context ){
		this.context = context;
	}
	
	//获得配置 
	private String getConf( String name ){
		ContentResolver cr = context.getContentResolver();
		
		//生成参数
		String[] results_columns = new String[]{
				KEY_NAME,
				KEY_VALUE
		};		
		String selection = KEY_NAME + "='" + name + "'";
		String[] selectionArgs = null;
		String sortOrder = null;
		
		//查询数据库
		Cursor cursor = cr.query( CONTENT_URI,
					results_columns, selection, selectionArgs, sortOrder);
		
		//得到值后返回，默认值是空
		String value = "";
		if( cursor.moveToFirst()){
			int columnIndex = cursor.getColumnIndexOrThrow( KEY_VALUE );
			value = cursor.getString( columnIndex );
		}
		cursor.close();
		return value;		
	}	
	
	//get和set集合
	//服务器IP
	public String getServerIp(){
		return getConf( NAME_SERVERIP );
	}
	
	//服务器端口
	public int getServerPort(){
		String port = getConf( NAME_SERVERPORT );
		return Integer.parseInt( port );
	}
	//机构号
	public String getOrgNO(){
		return getConf( NAME_ORGNO );
	}	
	
	//本地IP
	public String getLocalIp(){
		String ip = getConf( NAME_LOCALIP );
		return ip;
	}	
	
	//本地子网掩码
	public String getLocalNetmask(){
		String netmask = getConf( NAME_LOCALNETMASK );	
		return netmask;
	}	
	
	//本地网关
	public String getLocalGateway(){
		String gateway = getConf( NAME_LOCALGATEWAY );
		return gateway;
	}	
	
	//联网核查服务器IP、端口和机构号
	public String getCheckIDServerIp(){
		return getConf( NAME_CHECKIDIP );
	}	
	public int getCheckIDServerPort(){
		String port = getConf( NAME_CHECKIDPORT );
		return Integer.parseInt( port );
	}	
	public String getCheckIDServerOrg(){
		return getConf( NAME_CHECKIDORGNO );
	}
	
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
	
	//获取系统当前版本号
	public static String getSysVersion(){		
		return SystemInfo.getSysVersion();
	}
}
