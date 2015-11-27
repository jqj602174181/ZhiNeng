package com.centerm.autofill.update.updateapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

//�������� 
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
	 * �Զ�ȡ�����ݽ���һЩ����
	 * ������������д
	 */
	public boolean operatorReadData(String data)
	{
		boolean is = super.operatorReadData(data);
		if(is)
			return is;
		switch (communicationStyle) {

			case SocketCorrespondence.NOUPDATE://��������
				handler.sendEmptyMessage(communicationStyle);
				break;
			case SocketCorrespondence.UPDATE://��Ҫ����
				Message message = handler.obtainMessage(communicationStyle, data);
				handler.sendMessage(message);
				break;
			default://����Э����󣬷����ѯ����Ῠס
				handler.sendEmptyMessage( SocketCorrespondence.PROTOCOL_ERROR );
				break;
		}
		return true;
	}
}
