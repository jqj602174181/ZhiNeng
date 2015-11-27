package com.centerm.autofill.update.updateapp;

import android.content.Context;
import android.widget.Toast;

public class SocketCorrespondence {

	protected final static String CODING 	= "utf-8";
	public static final int CONNECT_ERROR 			= -1;//连接失败
	public static final int COMM_ERROR				= -5;//通信失败
	public static final int OPERATOR_ERROR			= -6;//操作失败
	public static final int PROTOCOL_ERROR			= -7;//协议错误
	public static final int START_STATUS 			= 2000;
	public static final int UPDATE 					= 0X04+START_STATUS;//需要升级 
	public static final int NOUPDATE				= 0x00+START_STATUS;//无需升级
	public static final int UPDATEAPP				= 0x05+START_STATUS;//升级app
	public final static int DOWNFAIL 				= 201;//下载失败
	public final static int UPDATEFAIL 				= 202;//升级失败
	public final static int UPDATESUCCESS			= 200;//升级成功
	public final static int UDPDATEAFTER			= 203;//稍后升级
	
	public final static int UPDATE_FINANCIAL		= 301;//升级金融模块
	public final static int UPDATE_PRINTER			= 302;//升级打印
	public final static int UPDATE_SYSTEM			= 303;//系统升级
	public final static int UPDATE_APP				= 304;//app应用升级
	public final static int UPDATE_ALL				= 305;//升级打印与金融模块
	
	public static void showTip(Context context,String tip)
	{
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}
	
}
