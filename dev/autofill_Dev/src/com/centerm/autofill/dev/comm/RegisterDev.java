package com.centerm.autofill.dev.comm;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

//设备注册
public class RegisterDev extends CommunicationBase{

	public RegisterDev(String ip, int port, Handler handler,String serial,String localIp,String outlets,String type) {
		super(ip, port, handler);
		JSONObject jsonObject = new JSONObject();
		work = 0x01;
		this.type = 0x01;
		try {
			jsonObject.put("serial", serial);
			jsonObject.put("type", type);
			jsonObject.put("outlets", outlets);
			jsonObject.put("ip", localIp);
			jsonStr = jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void operatorReadData(String data) {
		super.operatorReadData(data);
		
		switch (communicationStyle) {
			case SocketCorrespondence.COMM_ERROR:
				handler.sendEmptyMessage(SocketCorrespondence.REGISTER_ERROR);
				break;
			
			case 0:
				try {
					JSONObject jsonObject = new JSONObject(data);
					if(jsonObject.toString().equals(SocketCorrespondence.SuccessStr)){//注册成功
						handler.sendEmptyMessage(SocketCorrespondence.REGISTER_SUCCESS);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case SocketCorrespondence.REGISTER_ALREADY://已经注册
				handler.sendEmptyMessage(SocketCorrespondence.REGISTER_ALREADY);
				break;
		default:
			break;
		}
	}
	
	
	
}
