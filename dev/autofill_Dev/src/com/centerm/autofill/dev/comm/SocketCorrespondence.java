package com.centerm.autofill.dev.comm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.centerm.autofill.dev.R;

class SocketCorrespondence {

	protected final static String CODING 	= "utf-8";
	static final int CONNECT_ERROR 			= -1;//连接失败
	static final int REGISTER_ERROR 		= -2;//注册失败
	static final int ONLINE_ERROR			= -3;//上线失败
	static final int OUTLINE_ERROR			= -4;//下线失败
	static final int COMM_ERROR				= -5;//通信失败
	static final int OPERATOR_ERROR			= -6;//操作失败
	static final int  REGISTER_ALREADY		= 1;//已经注册
	static final int REGISTER_SUCCESS		= 2;//注册成功
	static final int ONLINE_SUCCESS			= 3;//成功上线
	static final int OUTLINE_SUCCESS		= 4;//成功下线
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
			case REGISTER_ALREADY://已注册，不需要任何提示信息
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
