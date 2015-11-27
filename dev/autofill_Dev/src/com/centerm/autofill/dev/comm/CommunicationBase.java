package com.centerm.autofill.dev.comm;
//通信相关的基类
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.centerm.common.MsgCreater;
import com.centerm.common.SocketClient;
import com.centerm.util.DesUtil;
import com.centerm.util.ErrorUtil;
import com.centerm.util.StringUtil;

public class CommunicationBase extends Thread{
	public static  String tag ="socket";
	public   SocketClient client; 
	public  String serviceIp = "10.18.59.222";
	public int port = 3131;
	public String jsonStr;
	public byte work,type;//信息的头两个字节
	protected final String CODING = "utf-8";

	protected Handler handler;
	
	protected int communicationStyle = 0;//用于记录通信错误的
	protected String communicationData=null;//记录通讯时读取的信息
	public CommunicationBase(String ip,int port,Handler handler)
	{
		this.handler = handler;
		serviceIp = ip;
		this.port = port;
	}
	
	
	public boolean sendData(String jsonStr) throws Exception
	{
		byte[] data = jsonStr.getBytes(CODING);
		data = DesUtil.trides_crypt(DesUtil.KEYBYTES, data);
		String dataStr =  StringUtil.HexToStringA(data);
		data = MsgCreater.createMsg(work,type,dataStr);
		
		int success = client.sendMessage(data, data.length);
		Log.e(tag,"success is "+jsonStr);
		if(success==ErrorUtil.ERR_OPEN){
			communicationStyle = SocketCorrespondence.COMM_ERROR;//通信失败
			return false;
		}
		return true;
	}
	
	public void readData()
	{
		byte[] dataByte = new byte[4];
		int len = client.readMessage(dataByte, 4, null, 5);
		Log.e(tag, "len is "+len);
		if(len!=4){
			communicationStyle = SocketCorrespondence.COMM_ERROR;//通信失败
			return;
		}
		int readLen = StringUtil.bytesToIntByBigEndian(dataByte, 0);
		
		byte[] stringBuffer = new byte[readLen];
		len = client.readMessage(stringBuffer, readLen, null, 5);
		if(len!=readLen){
			communicationStyle = SocketCorrespondence.COMM_ERROR;//通信失败
			return;
		}
		
		if(stringBuffer[1]==0x66){
			communicationStyle = SocketCorrespondence.OPERATOR_ERROR;//操作失败
		}else if(stringBuffer[1]==0x11){//已经注册
			communicationStyle = SocketCorrespondence.REGISTER_ALREADY;
		}
		String data = null;
		data = new String(stringBuffer,2,stringBuffer.length-3);

		Log.e(tag,"reavData is "+data);
		byte[] dataByte1 = StringUtil.StringToHexA(data);
		dataByte1		=DesUtil.trides_decrypt(DesUtil.KEYBYTES, dataByte1);
		communicationData = new String(dataByte1,0,dataByte1.length);
		
	}
	
	public void startTread()
	{
		SocketThread thread = new SocketThread();
		thread.start();
	}
	private class SocketThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			client = new SocketClient();
			boolean is = client.SocketConnect(serviceIp, port, 5);
			if(!is){
				communicationStyle = SocketCorrespondence.CONNECT_ERROR;//连接失败
				return;
			}
	
			try {
					is = sendData(jsonStr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(is){
				readData();
				operatorReadData(communicationData);//处理读取的信息
			}
			
			
		
			client.SocketClose();
		}
		
	}
	
	
	
	/*
	 * 对读取的数据进行一些处理
	 * 可以在子类重写
	 */
	public void operatorReadData(String data)
	{
		switch (communicationStyle) {
			case SocketCorrespondence.OPERATOR_ERROR:
				try {
					JSONObject jsonObject = new JSONObject(data);
					String exception = jsonObject.getString("exception");
					Message message = handler.obtainMessage(0, exception);
					handler.sendMessage(message);
					System.out.println( "error=" + exception );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case SocketCorrespondence.CONNECT_ERROR:
				handler.sendEmptyMessage(SocketCorrespondence.CONNECT_ERROR);
				break;
		default:
			break;
		}
		
	}
	
	
	
	
}
