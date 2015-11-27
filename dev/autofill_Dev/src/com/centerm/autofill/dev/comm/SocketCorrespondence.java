package com.centerm.autofill.dev.comm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.centerm.autofill.dev.R;

class SocketCorrespondence {

	protected final static String CODING 	= "utf-8";
	static final int CONNECT_ERROR 			= -1;//����ʧ��
	static final int REGISTER_ERROR 		= -2;//ע��ʧ��
	static final int ONLINE_ERROR			= -3;//����ʧ��
	static final int OUTLINE_ERROR			= -4;//����ʧ��
	static final int COMM_ERROR				= -5;//ͨ��ʧ��
	static final int OPERATOR_ERROR			= -6;//����ʧ��
	static final int  REGISTER_ALREADY		= 1;//�Ѿ�ע��
	static final int REGISTER_SUCCESS		= 2;//ע��ɹ�
	static final int ONLINE_SUCCESS			= 3;//�ɹ�����
	static final int OUTLINE_SUCCESS		= 4;//�ɹ�����
	static final byte ERROR_OPERATOP		= 0x66;
	
	static final String SuccessStr 			="{}";
	
	
	public static void handleMessage(Handler handler,Message msg,Context context)
	{
		switch (msg.what) {
			case CONNECT_ERROR:
				showTip(context, context.getResources().getString(R.string.connect_error));
				break;
		
			case ONLINE_ERROR:
				showTip(context, context.getResources().getString(R.string.onlineError));
				break;
			case OUTLINE_ERROR:
				showTip(context, context.getResources().getString(R.string.outlineError));
				break;
			case REGISTER_ALREADY://��ע�ᣬ����Ҫ�κ���ʾ��Ϣ
				break;
			case REGISTER_SUCCESS:
				showTip(context, context.getResources().getString(R.string.registerSuccess));
				break;
			case ONLINE_SUCCESS:
				showTip(context, context.getResources().getString(R.string.onlineSuccess));
				break;
			case OUTLINE_SUCCESS:
				showTip(context, context.getResources().getString(R.string.outlineSuccess));
				break;
			default:
				showTip(context,(String)msg.obj);
				break;
		}
	}
	
	public static void showTip(Context context,String tip)
	{
		Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
	}
	
}
