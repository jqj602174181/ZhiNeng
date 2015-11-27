package com.centerm.autofill.update.updateapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

//固体升级 
 public class FirmwaresUpdate extends CommunicationBase{

	public FirmwaresUpdate(UpdateData updateData, Handler handler) {
		super(updateData.ip, updateData.port, handler);
		JSONObject jObject = new JSONObject();
		work = 0x04;
		this.type = 0x04;
		try {
			jObject.put("serial", updateData.serial);
			jObject.put("code", updateData.code);
			jObject.put("version", updateData.version);
			jsonStr = jObject.toString();
	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * 对读取的数据进行一些处理
	 * 可以在子类重写
	 */
	public boolean operatorReadData(String data)
	{
		boolean is = super.operatorReadData(data);
		if(is)
			return is;
		switch (communicationStyle) {

			case SocketCorrespondence.NOUPDATE://不需升级
				handler.sendEmptyMessage(communicationStyle);
				break;
			case SocketCorrespondence.UPDATE://需要升级
				Message message = handler.obtainMessage(communicationStyle, data);
				handler.sendMessage(message);
				break;
			default://发送协议错误，否则查询界面会卡住
				handler.sendEmptyMessage( SocketCorrespondence.PROTOCOL_ERROR );
				break;
		}
		return true;
	}
}
