package com.centerm.autofill.setting.manage;

import java.lang.reflect.Method;

/**
 * ��̫��������
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
			
			Method m[] = ethMngClass.getMethods(); // ȡ��ȫ���ķ���
			  for (int i = 0; i < m.length; i++) {
			   String metName = m[i].getName(); // ȡ�÷�������
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
	
    //��ȡ��̫��״̬
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
	
	
	//������ر�
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
	
	//����Ĭ�����ã��ȿ���dhcp
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
