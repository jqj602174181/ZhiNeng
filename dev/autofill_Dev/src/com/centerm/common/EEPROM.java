package com.centerm.common;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class EEPROM {
	public static final int PRODUCTNAME = 0; //产品名称
	public static final int PRODUCTSN = 16; //产品序列号
	public static final int PRODUCDATE = 48; //产品生产日期
	public static final int PRODUCTMAC1 = 56; //产品mac2
	public static final int PRODUCTMAC2 = 62; //产品mac2
	public static final int PRODUCTRESERVED = 68; //产品预留字段
	private static final Map<Integer, Integer> productMap = new HashMap<Integer, Integer>(){
		{
			put( PRODUCTNAME, 16 );
			put( PRODUCTSN, 32 );
			put( PRODUCDATE, 8 );
			put( PRODUCTMAC1, 6 );
			put( PRODUCTMAC2, 6 );
			put( PRODUCTRESERVED, 188 );
		}
		};
	
	/*!
	 * @brief 写eeprom
	 * @param infType 写入信息的类型，详细见上面
	 * @param info 写信息的数据
	 * @return 返回操作是否成功
	 */
	public static boolean write( int infType, byte[] info )
	{
		RandomAccessFile randomAccessFile = null;
		long offset = 0;
		boolean bRet = false;
		try {
			randomAccessFile = new RandomAccessFile( "/sys/bus/i2c/devices/4-0050/eeprom", "rw");
			offset = infType;
			randomAccessFile.seek(offset);
			//初始化需要写的模块
			for( int  i = 0; i < productMap.get(infType); i++ )
			{
				randomAccessFile.write( (byte)'\0' );
			}
			randomAccessFile.seek(offset); //回滚到需要写的起始位置
			randomAccessFile.write(info); //写数据
			bRet = true;
		} catch (IOException e) {
			e.printStackTrace();
			bRet = false;
		}
		finally
		{
			if( null != randomAccessFile )
			{
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return bRet;
	}
	
	/*!
	 * @brief 读eeprom
	 * @param infType 读取信息类型，详细见上面
	 * @return 返回读取的字节
	 */
	public static String read( int infType )
	{
		RandomAccessFile randomAccessFile = null;
		long offset = 0;
		int len = productMap.get(infType);
		byte [] info = new byte[ len ]; //
		String sInfo = new String();
		try {
			randomAccessFile = new RandomAccessFile( "/sys/bus/i2c/devices/4-0050/eeprom", "r");
			offset = infType;
			randomAccessFile.seek(offset);
			randomAccessFile.read(info, 0, len ); //读数据
			if( -1 == info[0] ) //全新主板，未出化状态
			{
				sInfo = "";
			}
			else
			{
				sInfo = new String( info,"UTF-8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			if( null != randomAccessFile )
			{
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return sInfo; 
	}

}
