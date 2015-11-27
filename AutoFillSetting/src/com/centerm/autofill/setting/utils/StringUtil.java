package com.centerm.autofill.setting.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class StringUtil {
	public StringUtil(){
		
	}
	
	public static String formatDate(String strDate)
	{
		if( strDate.contains("长期"))
		{
			return strDate;
		}
		String strYear = strDate.substring(0, 4);
		String strMoth = strDate.substring(4, 6);
		String strDay = strDate.substring(6, 8);
		
		return strYear+"年"+strMoth+"月"+strDay+"日";
	}
	
	public static String ByteToString(byte[] byBuf)
	{
		int len = GetVailArrayLen(byBuf);
		String strBuf = new String(byBuf, 0, len); 
		return strBuf;
	}
	
	public static String ByteToString(byte[] byBuf, String charset)
	{
		int len = GetVailArrayLen(byBuf);
		String strBuf = null;
		try {
			strBuf = new String(byBuf, 0, len, charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return strBuf;
	}
	
	public static int GetVailArrayLen(byte[] byBuf)
	{
		for(int i=0; i<byBuf.length; i++)
		{
			if(byBuf[i] == 0)
				return i;
		}
		return byBuf.length;
	}
	
	// 1->2
	public static String HexToStringA(byte[] s)
	{
		String str="";
		int len = GetVailArrayLen(s);
		for (int i=0;i<len;i++)
		{
			int ch = (int)s[i];
			String s4 = Integer.toHexString(ch);
			if(s4.length() == 1)
			{
				s4 = "0" + s4;
			}
			else if(s4.length() > 2)
			{
				s4 = s4.substring(s4.length()-2, s4.length());
			}
			
			str = str + s4;
		}
		
		return str;
	} 
	
	// 1->2
	public static String HexToStringA(byte[] s, int len)
	{
		String str="";
		for (int i=0;i<len;i++)
		{
			int ch = (int)s[i];
			String s4 = Integer.toHexString(ch);
			if(s4.length() == 1)
			{
				s4 = "0" + s4;
			}
			else if(s4.length() > 2)
			{
				s4 = s4.substring(s4.length()-2, s4.length());
			}
			
			str = str + s4;
		}
		
		return str;
	}
	
	// 2->1
	public static byte[] StringToHexA(String s)
	{
		byte[] baKeyword = new byte[s.length()/2];
		for(int i = 0; i < baKeyword.length; i++)
		{
			try
			{
				baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			s = new String(baKeyword, "utf-8");//UTF-16le:Not
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		//return s;
		return baKeyword;
	}
	
	/**
	 * 校验ip地址是否正确
	 */
	private final static String PATTERN = 
	        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	public static boolean isIp( String ip){
		Pattern patten = Pattern.compile(PATTERN);
		return patten.matcher( ip ).matches();		
	}
	
	/**
	 * 检验网络端口是否有效
	 */
	public static boolean isPort( String port ){
		String tmp = port.trim();
		if( tmp.length() == 0 )
			return false;
		
		int p = Integer.valueOf( port );
		return (p > 1 && p < 65536);
	}
	
	/**
	 * 检测电话号码是否有效
	 * @return
	 */
	public static  boolean isPhoneNo( String no ) {
		//手机是11位，电话支持8位和7位
		if (no.length() != 11 
				&& no.length() != 8
				&& no.length() != 7 ) {
			return false;
		}

		//手机11位必须以13、15、17、18开头
		if( no.length() == 11 )
		{
			String strSub2 = no.substring(0, 2);
			if (strSub2.equals("13") || strSub2.equals("15")
					|| strSub2.equals("18") || strSub2.equals("17")) {
				return true;
			}
			return false;
		}
		
		return true;
	}
	
	/**
	 * 将int转换为byte数组
	 * @param length	int数值
	 * @return	byte数组
	 */
	public static byte[] IntToByte(int length)
	{
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte)(length >>> 24);
		byteArray[1] = (byte)(length >>> 16);
		byteArray[2] = (byte)(length >>> 8);
		byteArray[3] = (byte)(length);
		return byteArray;
	}
	
	/* byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序
	* @param src byte数组  
	* @param offset 从数组的第offset位开始  
	* @return int数值  
	*/    
	public static int bytesToInt(byte[] src, int offset) {  
		int value;    
		value = (int) ((src[offset+3] & 0xFF)   
			| ((src[offset+2] & 0xFF)<<8)   
			| ((src[offset+1] & 0xFF)<<16)   
			| ((src[offset] & 0xFF)<<24));  
		return value;  
	}  
	
	
}
