package com.centerm.util.financial;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*!
 * @brief 磁卡服务类
 * @par 说明:该类主要是提供对磁卡的操作，单一实例类。
 */
public class MagneticCardService {
	public static final int MSG_READCARD = 0x1001;
	public static final int MSG_WRITECARD = 0x1002;
	
	public static final int CARD_READ_TRACK1 = 0x00;
	public static final int CARD_READ_TRACK2 = 0x01;
	public static final int CARD_READ_TRACK3 = 0x02;
	public static final int CARD_READ_TRACK12 = 0x03;
	public static final int CARD_READ_TRACK23 = 0x04;
	public static final int CARD_WRITE_TRACK1 = 0x05;
	public static final int CARD_WRITE_TRACK2 = 0x06;
	public static final int CARD_WRITE_TRACK3 = 0x07;
	public static final int CARD_WRITE_TRACK12 = 0x08;
	public static final int CARD_WRITE_TRACK23 = 0x09;
	
	public static final int CARD_READ_TRACK123 = 0x10;
	public static final int CARD_WRITE_TRACK123 = 0x11;
	
	static MagneticCardService instance;
	private int port = 0;
	private int baud = 0;
	private int timeout = 0;
	private int mode = -1;
	private boolean isWorking = false;
	private OnReadCardListener rlistener = null;
	private OnWriteCardListener wlistener = null;
	
	
	/**
	 * @author Administrator
	 *
	 */
	public interface OnReadCardListener {
		public void onComplete(byte[] byTrack1, byte[] byTrack2, byte[] byTrack3);
		public void onError(int nErrCode, String strMsg);
		public void onCancel();
	}
	
	public interface OnWriteCardListener {
		public void onWriteCardSuc();
		public void onError(int errId);
	}
	
	private MagneticCardService()
	{
	}
	
	/*!
	 * @brief 获取磁卡service实例
	 * @return service实例
	 */
	public static MagneticCardService getInstance()
	{
		if( instance == null )
		{
			instance = new MagneticCardService();
		}
		return instance;
	}
	
	static 
    {
		//加载动态库
    	System.loadLibrary( "msgcard" );
    }
	
	/*********************对外方法声明*************************/
	public void setComParams(int nPort, int nBaud)
	{
		this.port = nPort;
		this.baud = nBaud;
	}
	
	public void setOutTime(int nOutTime)
	{
		this.timeout = nOutTime;
	}
	
	public void readTrack1(OnReadCardListener rls)
	{
		readTrack(CARD_READ_TRACK1, rls);
	}
	
	public void readTrack2(OnReadCardListener rls)
	{
		readTrack(CARD_READ_TRACK2, rls);
	}
	
	public void readTrack3(OnReadCardListener rls)
	{
		readTrack(CARD_READ_TRACK3, rls);
	}
	
	public void readTrack12(OnReadCardListener rls)
	{
		readTrack(CARD_READ_TRACK12, rls);
	}
	
	public void readTrack23(OnReadCardListener rls)
	{
		readTrack(CARD_READ_TRACK23, rls);
	}
	
	public void readTrack123(OnReadCardListener rls)
	{
		readTrack(CARD_READ_TRACK123, rls);
	}
	
	public void cancelRead() {
		CancelRead();
	}
	
	public void writeTrack1(OnWriteCardListener wls, byte[] byTrack1)
	{
		writeTrack(CARD_WRITE_TRACK1, wls, byTrack1, null, null);
	}
	
	public void writeTrack2(OnWriteCardListener wls, byte[] byTrack2)
	{
		writeTrack(CARD_WRITE_TRACK2, wls, null, byTrack2, null);
	}
	
	public void writeTrack3(OnWriteCardListener wls, byte[] byTrack3)
	{
		writeTrack(CARD_WRITE_TRACK3, wls, null, null, byTrack3);
	}
	
	public void writeTrack12(OnWriteCardListener wls, byte[] byTrack1, byte[] byTrack2)
	{
		writeTrack(CARD_WRITE_TRACK12, wls, byTrack1, byTrack2, null);
	}
	
	public void writeTrack23(OnWriteCardListener wls, byte[] byTrack2, byte[] byTrack3)
	{
		writeTrack(CARD_WRITE_TRACK23, wls, null, byTrack2, byTrack3);
	}
	
	public void writeTrack123(OnWriteCardListener wls, byte[] byTrack1, byte[] byTrack2, byte[] byTrack3)
	{
		writeTrack(CARD_WRITE_TRACK123, wls, byTrack1, byTrack2, byTrack3);
	}
	
	/*********************内部私有方法声明*************************/
	//消息处理
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			Bundle bundle = msg.getData();
			int ret = 0;
			switch (msg.what) {
			case MSG_READCARD:
				if(rlistener == null)
				{
					Log.d("MagCardService", "rlistener is null");
					break;
				}
				byte[] byTrack1 = bundle.getByteArray("track1");
				byte[] byTrack2 = bundle.getByteArray("track2");
				byte[] byTrack3 = bundle.getByteArray("track3");
				ret = bundle.getInt("result");
				Log.d("MagCardService", "read card result:"+ret);
				if( ret == 0)//TODO:读磁卡失败时，也会返回成功
				{
					rlistener.onComplete(byTrack1, byTrack2, byTrack3);
				}
				else if( ret == -7)
				{
					rlistener.onCancel();
				}
				else
				{
					rlistener.onError(ret, getErrMsg(ret));
				}
				rlistener = null;
				break;
			case MSG_WRITECARD:
				if(wlistener == null)
				{
					Log.d("MagCardService", "wlistener is null");
					break;
				}
				ret = bundle.getInt("result");
				Log.d("MagCardService", "write card result:"+ret);
				if( ret == 0)
				{
					wlistener.onWriteCardSuc();
				}
				else
				{
					wlistener.onError(ret);
				}
				wlistener = null;
				break;
			default:
				break;
			}

			mode = -1;
			isWorking = false;
		}
	};
	
	private void readTrack(int nMode, OnReadCardListener rls)
	{
		if(isWorking || rlistener != null)
		{
			return;
		}
		this.rlistener = rls;
		this.mode = nMode;
		this.isWorking = true;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte[] byTrack1 = new byte[256];
				byte[] byTrack2 = new byte[256];
				byte[] byTrack3 = new byte[256];
				int ret = ReadCard( port, baud, (byte)'9', timeout, mode, 
						byTrack1, byTrack1.length,
						byTrack2, byTrack2.length,
						byTrack3, byTrack3.length);
				
				Bundle bundle = new Bundle();
				bundle.putByteArray("track1", byTrack1);
				bundle.putByteArray("track2", byTrack2);
				bundle.putByteArray("track3", byTrack3);
				bundle.putInt("result", ret);
				Message msg = new Message();
				msg.what = MSG_READCARD;
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
		}).start();
	}
	
	private void writeTrack(int nMode, OnWriteCardListener wls,
			final byte[] byTrack1, final byte[] byTrack2, final byte[] byTrack3)
	{
		if(isWorking || wlistener != null)
		{
			return;
		}
		this.wlistener = wls;
		this.mode = nMode;
		this.isWorking = true;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				int ret = WriteCard( port, baud, (byte)'9', timeout, mode, 
						byTrack1, byTrack2, byTrack3);
				
				Bundle bundle = new Bundle();
				bundle.putInt("result", ret);
				Message msg = new Message();
				msg.what = MSG_WRITECARD;
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	private String getErrMsg(int errId)
	{
		String errMsg = null;
		switch(errId)
		{
		case 0:
			errMsg = "成功";
			break;
		case -1:
			errMsg = "失败";
			break;	
		case -2:
			errMsg = "不合法参数";
			break;	
		case -3:
			errMsg = "超时";
			break;	
		case -4:
			errMsg = "打开设备失败";
			break;	
		case -5:
			errMsg = "写失败";
			break;	
		case -6:
			errMsg = "读失败";
			break;
		case -7:
			errMsg = "用户取消";
			break;	
		case -8:
			errMsg = "数据包错误";
			break;
		default:
			errMsg = "未知错误";
			break;
		}
		
		return errMsg;
	}
	
	
	/*********************动态库中方法声明*************************/
	private native int ReadCard(int port, int baud, byte bp, int timeout, int mode,
			byte[] byTrack1, int nTrack1Len,
			byte[] byTrack2, int nTrack2Len,
			byte[] byTrack3, int nTrack3Len);
	
	private native int WriteCard(int port, int baud, byte bp, int timeout, int mode,
			byte[] byTrack1, byte[] byTrack2, byte[] byTrack3);
	
	private native void CancelRead();
}
