package com.centerm.autofill.update.utils;

import android.util.Log;



/**
 * 服务端的报文协议生成器
 */
public class MsgCreater {
	public final static int MSGTYPE_TRANSACTION = 3;		//交易类型
	public final static int SUBTYPE_TRANSACTION_Add = 1;	//添加交易
	
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
	 * 计算冗余校验值
	 * @param data	校验数据
	 * @return	校验值
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
