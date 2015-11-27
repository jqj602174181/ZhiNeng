package com.centerm.util;

import java.util.ArrayList;

/**
 * ���ݸ�ʽת��
 */
public class DataFormat {

	/**
	 * ��byte���͵�����ת��Ϊһ��16�����ַ���������0x21��ת��Ϊ"21"
	 * @param data -Ԥת��������
	 * @param autopad -�Ƿ��Զ����룬����λΪ0���Ƿ��Զ���Ϊ0
	 * @return ת������ַ���
	 */
	public static String toHex(byte data, boolean autopad)
	{
		String hex = Integer.toHexString(data & 0xFF).toUpperCase();//ת��Ϊ16����
		if (hex.length() == 1 && autopad )
		{
			hex = '0' + hex;
		}
		return hex;
	}
	
	/**
	 * ��byte���͵�����ת��Ϊһ��16�����ַ������ұض�Ϊ2���ַ�������0x21��ת��Ϊ"21"��0x02ת��Ϊ"02"
	 * @param data -Ԥת��������
	 * @return ת������ַ���
	 */
	public static String toHex(byte data)
	{
		return toHex( data, true );
	}

	/**
	 * ��byte���͵�����ת��Ϊһ��16�����ַ���������0x2122��ת��Ϊ"2122"
	 * @param data -Ԥת������������
	 * @param autopad -�Ƿ��Զ����룬����λΪ0���Ƿ��Զ���Ϊ0
	 * @return ת������ַ���
	 */
	public static String toHex(byte[] data, boolean autopad)
	{
		StringBuffer hex = new StringBuffer();
		for( int i = 0; i < data.length; i++ )
		{
			hex.append(toHex(data[i], autopad));
		}
		return hex.toString();
	}
	
	/**
	 * ��byte���͵�����ת��Ϊһ��16�����ַ������Ҹ�λΪ0���Զ����룬����0x2122��ת��Ϊ"2122"��0x0203ת��Ϊ"0203"
	 * @param data -Ԥת������������
	 * @return ת������ַ���
	 */
	public static String toHex(byte[] data)
	{
		return toHex( data, true );
	}

	/**
	 * ��һ���ַ���ת��Ϊ2�������֡�����"2122"��ת��Ϊ0x2122
	 * @param str -Ԥת�����ַ���
	 * @return ת�������ɵ�����
	 */
	public static byte[] hexToBytes(String str)
	{
		if (str == null)
		{
			return null;
		}
		else if (str.length() < 2)
		{
			return null;
		}
		else
		{
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++)
			{
				//ÿ��ȡ�������ַ���Ȼ��ϳ�16������
				String bytedata = str.substring(i * 2, i * 2 + 2);
				buffer[i] = (byte) Integer.parseInt(bytedata, 16);
			}
			return buffer;
		}
	}
	
	/**
	 * ���ݲ��룬����ʱ�ں������0x00
	 * @param data - ԭʼ����
	 * @param radix -����Ľ�����
	 * @return
	 */
	public static byte[] dataAlign( byte[] data, int radix )
	{
		//�����ݲ��뵽8�ֽڵ�������
		byte[] newdata = null;
		int len = data.length;
		if( len % radix != 0 )
		{
			len = ( len + radix - 1 ) / radix * radix;
			newdata = new byte[ len ];
			System.arraycopy( data, 0, newdata, 0, data.length );
		}
		else
		{
			newdata = data;
		}
		return newdata;		
	}
	
	/**
	 * ������ݡ���ƫ����Ϊoffset��ʼ�����ֵpaddata��data��
	 * @param data  -Ԥ���ԭʼ����
	 * @param offset -����ƫ����
	 * @param paddata -������������
	 * @return ��������Ľ��data
	 */
	public static byte[] dataPadding( byte[] data, int offset, byte[] paddata )
	{
		if( offset > data.length || offset < 0 )//offsetƫ�������Ϸ�
		{
			return data;
		}
		else
		{
			int len = data.length - offset;//��Ҫ�����ֽ���
			int step = paddata.length;
			for( int i = 0; i < len; i ++ )
			{
				data[ offset + i ] = paddata[ i % step ];
			}
			return data;
		}
	}
	
	
	/**
	 * �����ݽ��д�ȡ��������ƫ����Ϊ0���ҳ�����Դ���ݳ�����ͬ���ͷ���Դ���ݣ��������·���һ������Ϊlen�����飬Ȼ����н�ȡ������������ȫ����0x00
	 * @param data -Դ����
	 * @param offset -ƫ������С��0��ʾ��ĩβ��ʼ��ȡ
	 * @param len -��Ҫ�ضϵĳ���ֵ,��ֵ��ҪС�ڻ����data����
	 * @return ��ȡ�������
	 */
	public static byte[] dataCut( byte[] data, int offset, int len )
	{
		byte[] newdata = null;
		
		int newoffset = offset;
		if( newoffset < 0 )//���ƫ����С��0������Ҫ��β������ƫ��
		{
			newoffset = data.length + newoffset;
		}
		//TODO����Ҫ����offset����data���Ⱥ�С��data���ȵĸ�ֵ�����
		
		if( newoffset == 0 && data.length == len )//���ȹ�����������ȣ��Ͳ���Ҫ���´���
		{
			newdata = data;
		}		
		else
		{
			newdata = new byte[len];
			int copylen = newoffset + len > data.length ? data.length - newoffset : len;
			System.arraycopy( data, newoffset, newdata, 0, copylen );
		}
		return newdata;
	}
	
	/**
	 * �����ݽ��д��ϲ���
	 * @param data -Դ����
	 * @param len -��Ҫ�ضϵĳ���ֵ,��ֵ��ҪС�ڻ����data����
	 * @return �ضϺ������
	 */
	public static byte[] dataCut( byte[] data, int len )
	{
		return dataCut( data, 0, len );
	}
	
	
	
	/**
	 * ���������ݽ��������󷵻����Ľ��
	 * @param data1 -�������
	 * @param data2 -�������
	 * @return ���Ľ��
	 */
	public static byte[] xor( byte[] data1, byte[] data2 )
	{
		int minlen = data1.length;
		int maxlen = data2.length;
		if( minlen > maxlen )//���±Ƚ���󳤶Ⱥ���С����
		{
			int tmp = maxlen;
			maxlen = minlen;
			minlen = tmp;
		}//else ����Ҫ����
		byte[] data = new byte[ maxlen ];
		
		for( int i  = 0; i < minlen; i++ )//����������
		{
			data[i] = ((byte)(data1[i] ^ data2[i]));
		}
		
		if( minlen != maxlen )//�ѹ��������ݿ���������
		{
			if( maxlen == data1.length )
			{
				System.arraycopy(data1, minlen, data, minlen, maxlen - minlen );
			}
			else
			{
				System.arraycopy(data2, minlen, data, minlen, maxlen - minlen );
			}
		}
		return data;
	}
	
	/**
	 * ��BCD��ת��ΪASCII�͵��ַ������ַ���������nLen������
	 * @param srcdata -Ԥת����Դ����
	 * @param nLen -Ԥת����Դ���ݵĳ���
	 * @return ת������ַ���
	 */
	public static String BCDToASCII(byte[] srcdata, int nLen)
	{
		byte[] data = new byte[nLen*2];
		for (int i = 0; i < nLen; i++)
		{
			byte c  = 0;//��ֵ��ֽ�
			if( i < srcdata.length )//���δ����Դ���ݵĳ���
			{
				c = srcdata[i];
			}
			data[i*2] = (byte)(((c>>4) & 0x0f) + 0x30);
			data[i*2+1] = (byte)((c & 0x0f) + 0x30);
		}

		return new String(data);
	}
	
	/**
	 * ��ANSII��������ת��ΪBCD�룬 
	 * @param srcdata -Ԥת����Դ����
	 * @param nLen -Դ���ݵĳ���
	 * @return ת�����BCD��
	 */
	public static byte[] ASCIIToBCD(byte[] srcdata, int nLen)
	{
		int maxlen = (nLen + 1) / 2;			//һ����Ҫ�����ֽ����洢
		byte[] data = new byte[maxlen];			//���ص�����
		int srclen = srcdata.length; 			//Դ���ݳ���
		
		maxlen = maxlen > (srclen + 1) / 2 ? (srclen + 1) / 2 : maxlen;//����ֻ��Ҫת�����ɶ��ٸ��ֽڣ����ⳬ���ַ����ĳ���
		for (int i = 0; i < maxlen; i++ )
		{
			byte m = (byte)((srcdata[2*i] & 0x0f) << 4 );//��4λ
			byte n = 0;//��4λ
			if( i * 2 + 1 < srclen )//��δ���������ԭʼ���ȣ��Ϳ�ȡ����2���ֽ�
			{
				n = (byte)(srcdata[2*i+1] & 0x0f);
			}
			data[i] = (byte)(m|n);//�������
		}
		return data;
	}

	/**
	 * ��ANSII��������ת��ΪBCD�룬 
	 * @param strAscii -Ԥת����Դ�����ַ���
	 * @param nLen -Դ���ݵĳ���
	 * @return ת�����BCD��
	 */
	public static byte[] ASCIIToBCD(String strAscii, int nLen)
	{
		return ASCIIToBCD( strAscii.getBytes(), nLen );
	}
	
	//�Ѷ���������ת��Ϊ�ַ��������ǲ���ı����ַ���������ת���ַ�
	public static String ByteToRawString( byte[] data )
	{
		try
		{
			return new String( data, "ISO-8859-1" );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return "";
		
	}
	
	//��arraylist��ÿ��byte[]ƴ��һ��
	public static byte[] ArrayList2Bytes( ArrayList<byte[]> list ){
		//�����ܳ���
		int len = 0;
		for( int i = 0; i < list.size(); i++ ){
			len += list.get(i).length;
		}
		
		//���ɽ��
		byte[] res = new byte[len];
		int offset = 0;
		for( int k = 0; k < list.size(); k++ ){
			byte[] data = list.get( k );
			System.arraycopy( data, 0, res, offset, data.length );
			offset += data.length;
		}
		return res;
	}
}
