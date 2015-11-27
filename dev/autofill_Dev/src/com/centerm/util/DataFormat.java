package com.centerm.util;

import java.util.ArrayList;

/**
 * 数据格式转换
 */
public class DataFormat {

	/**
	 * 将byte类型的数据转化为一个16进制字符串，例如0x21将转化为"21"
	 * @param data -预转化的数据
	 * @param autopad -是否自动补齐，若高位为0，是否自动填为0
	 * @return 转化后的字符串
	 */
	public static String toHex(byte data, boolean autopad)
	{
		String hex = Integer.toHexString(data & 0xFF).toUpperCase();//转化为16进制
		if (hex.length() == 1 && autopad )
		{
			hex = '0' + hex;
		}
		return hex;
	}
	
	/**
	 * 将byte类型的数据转化为一个16进制字符串，且必定为2个字符，例如0x21将转化为"21"，0x02转化为"02"
	 * @param data -预转化的数据
	 * @return 转化后的字符串
	 */
	public static String toHex(byte data)
	{
		return toHex( data, true );
	}

	/**
	 * 将byte类型的数据转化为一个16进制字符串，例如0x2122将转化为"2122"
	 * @param data -预转化的数据数组
	 * @param autopad -是否自动补齐，若高位为0，是否自动填为0
	 * @return 转化后的字符串
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
	 * 将byte类型的数据转化为一个16进制字符串，且高位为0会自动补齐，例如0x2122将转化为"2122"，0x0203转化为"0203"
	 * @param data -预转化的数据数组
	 * @return 转化后的字符串
	 */
	public static String toHex(byte[] data)
	{
		return toHex( data, true );
	}

	/**
	 * 将一个字符串转化为2进制数字。例如"2122"将转化为0x2122
	 * @param str -预转化的字符串
	 * @return 转化后生成的数据
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
				//每次取出两个字符，然后合成16进制数
				String bytedata = str.substring(i * 2, i * 2 + 2);
				buffer[i] = (byte) Integer.parseInt(bytedata, 16);
			}
			return buffer;
		}
	}
	
	/**
	 * 数据补齐，补齐时在后面填充0x00
	 * @param data - 原始数据
	 * @param radix -补齐的进制数
	 * @return
	 */
	public static byte[] dataAlign( byte[] data, int radix )
	{
		//将数据补齐到8字节的整数倍
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
	 * 填充数据。从偏移量为offset开始，填充值paddata到data中
	 * @param data  -预填充原始数据
	 * @param offset -填充的偏移量
	 * @param paddata -用来填充的数据
	 * @return 返回填充后的结果data
	 */
	public static byte[] dataPadding( byte[] data, int offset, byte[] paddata )
	{
		if( offset > data.length || offset < 0 )//offset偏移量不合法
		{
			return data;
		}
		else
		{
			int len = data.length - offset;//需要填充的字节数
			int step = paddata.length;
			for( int i = 0; i < len; i ++ )
			{
				data[ offset + i ] = paddata[ i % step ];
			}
			return data;
		}
	}
	
	
	/**
	 * 将数据进行戴取操作，若偏移量为0，且长度与源数据长度相同，就返回源数据；否则重新分配一个长度为len的数组，然后进行截取，过长的数据全部填0x00
	 * @param data -源数据
	 * @param offset -偏移量，小于0表示从末尾开始截取
	 * @param len -需要截断的长度值,其值需要小于或等于data长度
	 * @return 截取后的数据
	 */
	public static byte[] dataCut( byte[] data, int offset, int len )
	{
		byte[] newdata = null;
		
		int newoffset = offset;
		if( newoffset < 0 )//如果偏移量小于0，就需要从尾部进行偏移
		{
			newoffset = data.length + newoffset;
		}
		//TODO：需要处理offset超过data长度和小于data长度的负值的情况
		
		if( newoffset == 0 && data.length == len )//长度过长，或者相等，就不需要重新创建
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
	 * 将数据进行戴断操作
	 * @param data -源数据
	 * @param len -需要截断的长度值,其值需要小于或等于data长度
	 * @return 截断后的数据
	 */
	public static byte[] dataCut( byte[] data, int len )
	{
		return dataCut( data, 0, len );
	}
	
	
	
	/**
	 * 将两个数据进行异或，最后返回异或的结果
	 * @param data1 -异或数据
	 * @param data2 -异或数据
	 * @return 异或的结果
	 */
	public static byte[] xor( byte[] data1, byte[] data2 )
	{
		int minlen = data1.length;
		int maxlen = data2.length;
		if( minlen > maxlen )//重新比较最大长度和最小长度
		{
			int tmp = maxlen;
			maxlen = minlen;
			minlen = tmp;
		}//else 不需要处理
		byte[] data = new byte[ maxlen ];
		
		for( int i  = 0; i < minlen; i++ )//进行异或操作
		{
			data[i] = ((byte)(data1[i] ^ data2[i]));
		}
		
		if( minlen != maxlen )//把过长的数据拷贝到后面
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
	 * 将BCD码转化为ASCII型的字符串，字符串长度是nLen的两倍
	 * @param srcdata -预转化的源数据
	 * @param nLen -预转化的源数据的长度
	 * @return 转化后的字符串
	 */
	public static String BCDToASCII(byte[] srcdata, int nLen)
	{
		byte[] data = new byte[nLen*2];
		for (int i = 0; i < nLen; i++)
		{
			byte c  = 0;//拆分的字节
			if( i < srcdata.length )//如果未超过源数据的长度
			{
				c = srcdata[i];
			}
			data[i*2] = (byte)(((c>>4) & 0x0f) + 0x30);
			data[i*2+1] = (byte)((c & 0x0f) + 0x30);
		}

		return new String(data);
	}
	
	/**
	 * 将ANSII类型数据转化为BCD码， 
	 * @param srcdata -预转化的源数据
	 * @param nLen -源数据的长度
	 * @return 转化后的BCD码
	 */
	public static byte[] ASCIIToBCD(byte[] srcdata, int nLen)
	{
		int maxlen = (nLen + 1) / 2;			//一共需要多少字节来存储
		byte[] data = new byte[maxlen];			//返回的数据
		int srclen = srcdata.length; 			//源数据长度
		
		maxlen = maxlen > (srclen + 1) / 2 ? (srclen + 1) / 2 : maxlen;//计算只需要转化生成多少个字节，避免超过字符串的长度
		for (int i = 0; i < maxlen; i++ )
		{
			byte m = (byte)((srcdata[2*i] & 0x0f) << 4 );//高4位
			byte n = 0;//低4位
			if( i * 2 + 1 < srclen )//若未超过数组的原始长度，就可取出第2个字节
			{
				n = (byte)(srcdata[2*i+1] & 0x0f);
			}
			data[i] = (byte)(m|n);//进行组合
		}
		return data;
	}

	/**
	 * 将ANSII类型数据转化为BCD码， 
	 * @param strAscii -预转化的源数据字符串
	 * @param nLen -源数据的长度
	 * @return 转化后的BCD码
	 */
	public static byte[] ASCIIToBCD(String strAscii, int nLen)
	{
		return ASCIIToBCD( strAscii.getBytes(), nLen );
	}
	
	//把二进制数据转化为字符串，但是不会改变用字符集来进行转化字符
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
	
	//把arraylist中每个byte[]拼在一起
	public static byte[] ArrayList2Bytes( ArrayList<byte[]> list ){
		//计算总长度
		int len = 0;
		for( int i = 0; i < list.size(); i++ ){
			len += list.get(i).length;
		}
		
		//生成结果
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
