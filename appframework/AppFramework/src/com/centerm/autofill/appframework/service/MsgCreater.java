package com.centerm.autofill.appframework.service;

import android.util.Log;

import com.centerm.autofill.appframework.utils.StringUtil;

/**
 * ����˵ı���Э��������
 */
public class MsgCreater {
	public final static int MSGTYPE_TRANSACTION = 3;		//��������
	public final static int SUBTYPE_TRANSACTION_Add = 1;	//��ӽ���
	
	public static byte[] createMsg(int work,int type,String jsonStr) throws Exception{
		byte[] content 		= jsonStr.getBytes("utf-8");
		byte[] typeBytes 	= new byte[]{(byte) work,(byte) type};
		content 			= StringUtil.bytes2And(typeBytes, content);// type+content
		byte checker		= RedundancyCheck(content);
		content 			= StringUtil.bytes2And(content, new byte[]{checker});
		byte[] len 			= StringUtil.IntToByte(content.length);
		return StringUtil.bytes2And(len, content);
	}
	
	/**
	 * ��������У��ֵ
	 * @param data	У������
	 * @return	У��ֵ
	 */
	public static byte RedundancyCheck(byte[] data)
	{
		byte checkCode = (byte)0x00;
		for ( int i = 0; i < data.length; i++ )
		{
			checkCode = (byte)(checkCode ^ data[i]);
		}
		
		return checkCode;
	}
	
	
	
}
