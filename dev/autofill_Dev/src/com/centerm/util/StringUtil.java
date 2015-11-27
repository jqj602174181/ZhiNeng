package com.centerm.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

public class StringUtil {
	public StringUtil(){
		
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
		int len = s.length;
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
	
   /* byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序
	* @param src byte数组  
	* @param offset 从数组的第offset位开始  
	* @return int数值  
	*/    
	public static int bytesToInt(byte[] src, int offset) {  
		int value;    
		value = (int) ((src[offset] & 0xFF)   
			| ((src[offset+1] & 0xFF)<<8)   
			| ((src[offset+2] & 0xFF)<<16)   
			| ((src[offset+3] & 0xFF)<<24));  
		return value;  
	}
	
	/* byte数组中取int数值，本方法适用于(高位在前，低位在后)的顺序
	* @param src byte数组  
	* @param offset 从数组的第offset位开始  
	* @return int数值  
	*/    
	public static int bytesToIntByBigEndian(byte[] src, int offset) {  
		int value;    
		value = (int) ((src[offset+3] & 0xFF)   
			| ((src[offset+2] & 0xFF)<<8)   
			| ((src[offset+1] & 0xFF)<<16)   
			| ((src[offset] & 0xFF)<<24));  
		return value;  
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
	
	//json格式转化为hashmap
	public static HashMap<String, String> jsonToHashMap( String json ){
		
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			JSONObject js = new JSONObject( json );
			Iterator<String> it = js.keys();
			while( it.hasNext() ){
				String key = it.next();
				String value = js.optString( key );
				map.put( key, value );
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 合并数组
	 * @param left
	 * @param right
	 * @return
	 */
	public static byte[] bytes2And(byte[] left, byte[] right) {
		byte[] result = new byte[left.length + right.length];
		for (int i = 0; i < left.length; i++) {
			result[i] = left[i];
		}

		for (int i = 0; i < right.length; i++) {
			result[i + left.length] = right[i];
		}

		return result;
	}
}
