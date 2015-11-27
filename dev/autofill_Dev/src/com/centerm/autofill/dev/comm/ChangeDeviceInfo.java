package com.centerm.autofill.dev.comm;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

//修改设备信息
class ChangeDeviceInfo extends CommunicationBase{

	private int statusError = 0;
	private int statusSuccess = 0;
	public ChangeDeviceInfo(String ip, int port, Handler handler,String serial,int status) {
		super(ip, port, handler);
		// TODO Auto-generated constructor stub
		JSONObject jsonObject = new JSONObject();
		work = 0x01;
		this.type = 0x02;
		try {
			jsonObject.put("serial", serial);
			jsonObject.put("status", ""+status);
			jsonStr = jsonObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(status==1){//下线
			statusError = SocketCorrespondence.OUTLINE_ERROR;
			statusSuccess = SocketCorrespondence.OUTLINE_SUCCESS;
		}else if(status==0){//上线
			statusError = SocketCorrespondence.ONLINE_ERROR;
			statusSuccess = SocketCorrespondence.ONLINE_SUCCESS;
		}
		
	}
	
	@Override
	public void operatorReadData(String data) {
		// TODO Auto-generated method stub
		super.operatorReadData(data);
		
		switch (communicationStyle) {
			case SocketCorrespondence.COMM_ERROR:
				handler.sendEmptyMessage(statusError);
				break;
			
			case 0:
				try {
					JSONObject jsonObject = new JSONObject(data);
					if(jsonObject.toString().equals(SocketCorrespondence.SuccessStr)){//注册成功
						handler.sendEmptyMessage(statusSuccess);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
		default:
			break;
		}
	}
	

}
