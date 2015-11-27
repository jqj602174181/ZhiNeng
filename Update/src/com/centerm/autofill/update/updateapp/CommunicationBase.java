package com.centerm.autofill.update.updateapp;
//通信相关的基类
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
//import android.util.Log;

import com.centerm.autofill.update.socket.SocketClient;
import com.centerm.autofill.update.utils.DesUtil;
import com.centerm.autofill.update.utils.ErrorUtil;
import com.centerm.autofill.update.utils.MsgCreater;
import com.centerm.autofill.update.utils.StringUtil;

public class CommunicationBase implements Runnable{
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
	
	
	public boolean sendData(String jsonStr,SocketClient socketClient) throws Exception
	{
		byte[] data = jsonStr.getBytes(CODING);
		data = DesUtil.trides_crypt(DesUtil.KEYBYTES, data);
		String dataStr =  StringUtil.HexToStringA1(data);
		data = MsgCreater.createMsg(work,type,dataStr);
		
		int success = socketClient.sendMessage(data, data.length);
		if(success==ErrorUtil.ERR_OPEN){
			communicationStyle = SocketCorrespondence.COMM_ERROR;//通信失败
			return false;
		}
		return true;
	}
	
	public void readData(SocketClient client)
	{
		byte[] dataByte = new byte[4];
		int len = client.readMessage(dataByte, 4, null, 5);
		if(len!=4){
			communicationStyle = SocketCorrespondence.COMM_ERROR;//通信失败
			return;
		}
		int readLen = StringUtil.bytesToInt(dataByte, 0);
		
		byte[] stringBuffer = new byte[readLen];
		len = client.readMessage(stringBuffer, readLen, null, 5);
		if(len!=readLen){
			communicationStyle = SocketCorrespondence.COMM_ERROR;//通信失败
			return;
		}
		
		if(stringBuffer[1]==0x66){
			communicationStyle = SocketCorrespondence.OPERATOR_ERROR;//操作失败
		}else{
			communicationStyle = stringBuffer[1]+SocketCorrespondence.START_STATUS;//加上一个数，防止与进度的数据冲突
		}
		String data = null;
		data = new String(stringBuffer,2,stringBuffer.length-3);
		
		byte[] dataByte1 = StringUtil.StringToHexA(data);
		dataByte1		=DesUtil.trides_decrypt(DesUtil.KEYBYTES, dataByte1);
		
		communicationData = new String(dataByte1,0,dataByte1.length);
		Log.e("socket","data is "+communicationData);
		
	}
	
	
	@Override
	public void run() {
		client = new SocketClient();
		boolean is = client.SocketConnect(serviceIp, port, 5);
		if(!is){
			communicationStyle = SocketCorrespondence.CONNECT_ERROR;//连接失败
			operatorReadData(communicationData);
			return;
		}

		try {
				is = sendData(jsonStr,client);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(is){
			readData(client);
			//operatorReadData(communicationData);//处理读取的信息
		}
		client.SocketClose();
		
		if(is){
			operatorReadData(communicationData);//处理读取的信息
		}
	}
	
	
	
	/*
	 * 对读取的数据进行一些处理
	 * 可以在子类重写
	 */
	public boolean operatorReadData(String data)
	{
		boolean isOperator = false;
		if(handler==null)return false;
		switch (communicationStyle) {
			case SocketCorrespondence.OPERATOR_ERROR:
				try {
					JSONObject jsonObject = new JSONObject(data);
					String exception = jsonObject.getString("exception");
					Message message = handler.obtainMessage(SocketCorrespondence.OPERATOR_ERROR, exception);
					handler.sendMessage(message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isOperator =  true;
				break;
			case SocketCorrespondence.CONNECT_ERROR:
		
				handler.sendEmptyMessage(SocketCorrespondence.CONNECT_ERROR);
				isOperator = true;
				break;
			case SocketCorrespondence.COMM_ERROR:
				handler.sendEmptyMessage(SocketCorrespondence.CONNECT_ERROR);
				isOperator = false;
				break;
		default:
			isOperator =false;
			break;
		}
		return isOperator;
		
	}
	
	
	
	
}
