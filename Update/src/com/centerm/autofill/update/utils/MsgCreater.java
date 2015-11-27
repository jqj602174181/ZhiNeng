package com.centerm.autofill.update.utils;

import android.util.Log;



/**
 * ����˵ı���Э��������
 */
public class MsgCreater {
	public final static int MSGTYPE_TRANSACTION = 3;		//��������
	public final static int SUBTYPE_TRANSACTION_Add = 1;	//��ӽ���
	
	public static byte[] createMsg(int work,int type,String jsonStr) throws Exception{
		byte[] content 		= jsonStr.getBytes("utf-8");
		byte[] typeBytes 	= new byte[]{(byte) work,(byte) type};
		content 			= bytes2And(typeBytes, content);// type+content
		byte checker		= RedundancyCheck(content);
		content 			= bytes2And(content, new byte[]{checker});
		byte[] len 			= StringUtil.IntToByte(content.length);
		return bytes2And(len, content);
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
	
	/**
	 * �ϲ�����
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
