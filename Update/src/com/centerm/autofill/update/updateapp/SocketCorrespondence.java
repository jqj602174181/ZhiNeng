package com.centerm.autofill.update.updateapp;

import android.content.Context;
import android.widget.Toast;

public class SocketCorrespondence {

	protected final static String CODING 	= "utf-8";
	public static final int CONNECT_ERROR 			= -1;//����ʧ��
	public static final int COMM_ERROR				= -5;//ͨ��ʧ��
	public static final int OPERATOR_ERROR			= -6;//����ʧ��
	public static final int PROTOCOL_ERROR			= -7;//Э�����
	public static final int START_STATUS 			= 2000;
	public static final int UPDATE 					= 0X04+START_STATUS;//��Ҫ���� 
	public static final int NOUPDATE				= 0x00+START_STATUS;//��������
	public static final int UPDATEAPP				= 0x05+START_STATUS;//����app
	public final static int DOWNFAIL 				= 201;//����ʧ��
	public final static int UPDATEFAIL 				= 202;//����ʧ��
	public final static int UPDATESUCCESS			= 200;//�����ɹ�
	public final static int UDPDATEAFTER			= 203;//�Ժ�����
	
	public final static int UPDATE_FINANCIAL		= 301;//��������ģ��
	public final static int UPDATE_PRINTER			= 302;//������ӡ
	public final static int UPDATE_SYSTEM			= 303;//ϵͳ����
	public final static int UPDATE_APP				= 304;//appӦ������
	public final static int UPDATE_ALL				= 305;//������ӡ�����ģ��
	
	public static void showTip(Context context,String tip)
	{
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}
	
}
