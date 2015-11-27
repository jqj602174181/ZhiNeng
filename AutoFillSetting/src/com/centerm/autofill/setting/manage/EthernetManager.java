package com.centerm.autofill.setting.manage;

import java.lang.reflect.Method;

/**
 * 以太网管理器
 */
public class EthernetManager {
	
    public static final int ETHERNET_STATE_UNKNOWN = 0;
    public static final int ETHERNET_STATE_DISABLED = 1;
    public static final int ETHERNET_STATE_ENABLED = 2;
    
    private static Object getInstance(){
    	try
		{
			Class<?> ethMngClass = Class.forName("android.net.ethernet.EthernetManager");
			Method method = ethMngClass.getMethod("getInstance" );
			
			Method m[] = ethMngClass.getMethods(); // 取得全部的方法
			  for (int i = 0; i < m.length; i++) {
			   String metName = m[i].getName(); // 取得方法名称
			   System.out.println("metName:" + metName);
			   }
			   
			return (Object)method.invoke( null );
		}catch( Exception e )
		{
			e.printStackTrace();
		}
    	return null;
    }
    
    private static Object getSavedConfig(){
    	try
		{
			Class<?> ethMngClass = Class.forName("android.net.ethernet.EthernetManager");
			Method method = ethMngClass.getMethod("getSavedConfig" );			   
			return (Object)method.invoke( getInstance() );
		}catch( Exception e )
		{
			e.printStackTrace();
		}
    	return null;
    }
	
    //获取以太网状态
	static public int getState(){
		try
		{
			Class<?> ethMngClass = Class.forName("android.net.ethernet.EthernetManager");
			Method getStateMethod = ethMngClass.getMethod("getState" );
			Integer ret = (Integer)getStateMethod.invoke( getInstance() );
			System.out.println( "get state=" + ret );
			return ret.intValue();
		}catch( Exception e )
		{
			e.printStackTrace();
		}
		return ETHERNET_STATE_UNKNOWN;
	}
	
	
	//开启或关闭
	static public void setEnabled(Object manager, boolean enable) {
		try
		{
			Class<?> ethMngClass = Class.forName("android.net.ethernet.EthernetManager");
			Method setEnabledMethod = ethMngClass.getMethod("setEthernetEnabled", boolean.class );
			setEnabledMethod.invoke( manager, enable );
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	//设置默认配置，既开启dhcp
	static public void setDefaultConf(){
		try
		{
			Class<?> ethMngClass = Class.forName("android.net.ethernet.EthernetManager");
			Method setDefaultConfMethod = ethMngClass.getMethod("setDefaultConf" );
			setDefaultConfMethod.invoke( getInstance() );
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	static public String getIp(){
		Object mEthInfo = getSavedConfig();
		try
		{
			Class<?> cls = Class.forName("android.net.ethernet.EthernetDevInfo");
			Method method = cls.getMethod("getIpAddress" );
			return (String)method.invoke( mEthInfo );
		}catch( Exception e )
		{
			e.printStackTrace();
		}
		return "";
	}
}
