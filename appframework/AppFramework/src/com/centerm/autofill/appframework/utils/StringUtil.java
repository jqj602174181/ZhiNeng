package com.centerm.autofill.appframework.utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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
		//有效电话号码
		//7位固定电话、8位固定电话、10位固定电话（如010xxxxxxx)、
		//11位固定电话(如010xxxxxxxx)、12位固定电话(如0591xxxxxxxx)，13位固定电话：0591-xxxxxxxx
		//11位手机号：13x, 15x， 17x,18x	
		
		if( no.length() < 7 || no.length() > 13 || no.length() == 9 )
			return false;
		
		//超过9位，以0开头，说明是固定电话
		if( no.length() >= 10 && no.charAt(0) == '0' )
			return true;
		
		//超过9位，且不是手机号码，必须以0开头
		if( no.length() >= 10 && no.length() != 11 && no.charAt(0) != '0')
			return false;
		
		//手机是11位，电话支持8位和7位
//		if (no.length() != 11 
//				&& no.length() != 8
//				&& no.length() != 7 ) {
//			return false;
//		}

		//手机11位必须以13、15、17、18开头
		if( no.length() == 11 )
		{
//			String strSub2 = no.substring(0, 2);
//			if( strSub2.equals("13") || strSub2.equals("14") || strSub2.equals("15")
//					|| strSub2.equals("18") || strSub2.equals("17")) {
//				return true;
//			}
			//手机号当前有13,14,15,16,17，18，避免未来新手机段的出现，删除限制
			if( no.startsWith("1"))
				return true;
			
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
		public static int bytesToInt1(byte[] src, int offset) {  
			int value;    
			value = (int) ((src[offset+3] & 0xFF)   
				| ((src[offset+2] & 0xFF)<<8)   
				| ((src[offset+1] & 0xFF)<<16)   
				| ((src[offset] & 0xFF)<<24));  
			return value;  
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
		
		/**
		 * 将指定的字符串末尾补空格，直至达到指定长度。
		 * 注意：含有非ascii字符的字符串请谨慎调用该函数，因为一个中文可能是2个字节
		 * @param str 字符串
		 * @param length 指定长度
		 * @return 生成的字符串
		 */
		public static String padding( String str, int length){
			
			//字符串过长或参数无效
			if( str.length() >= length || length < 1 ){
				return str;
			}
			
			int n = length - str.length();
			char[] tmp = new char[n];
			for( int i = 0; i < n; i++ )
				tmp[i] = ' ';
			
			return str + new String(tmp);
		}
		
		//如果为空或非法字符，返回0
		public static int parseIntSafety( String str ){
			if( str == null || str.length() == 0 ){
				return 0;
			}
			try{
				return Integer.parseInt( str );
			}catch( NumberFormatException e ){
				return 0;
			}
		}
		
		//解析double，不采用e的格式
		public static String parseDouble( double number ){
			try {
				// 格式化为xx.xx，并去除.号
				DecimalFormat decformat = new DecimalFormat("###############0.00");
				return decformat.format( number );
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";		
			
		}
		
		//把字符串转成double型
		public static double parseDouble( String number ){
			try {
				if( number == null || number.length() == 0){
					return 0;
				}
				return Double.parseDouble(number);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;	
		}
}
