package com.centerm.autofill.dev.commit;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.util.Log;

import com.centerm.common.SocketClient;
import com.centerm.util.DataFormat;
import com.centerm.util.ErrorUtil;
import com.centerm.util.StringUtil;
import com.centerm.util.DesUtil;

/**
 * ��װ����˵ı���Э��ͨ��
 */
public class MessageComm {
	//����������
	public final static byte MSGTYPE_TRANSACTION = 3;		//����
	
	//��������
	public final static byte SUBTYPE_TRANSACTION_Add = 1;	//��ӽ���
	public final static byte SUBTYPE_EXCEPTION = 102;		//�����쳣
	
	public final static String TAG_LOG = "dev_commit";		//��־��־
	public final static int ERROR_OK = 0;					//�ɹ�
	public final static int ERROR_UNKNOWN = -1;				//δ֪����
	public final static int ERROR_PROTOCOL = -2;			//Э�����
	public final static int ERROR_UNREGISTER = -3;			//Ӧ��δע��
	
	private byte msgType;					//������
	private byte subType;					//����������
	private byte[] reqData;					//�������ݰ�����
	private String resContent;				//���յ�����������
	private String serverIp;				//�����IP
	private int port;						//ͨ�Ŷ˿ں�
	private String errorinfo = "";			//������Ϣ
	
	public MessageComm( String serverIp, int port ){
		this.serverIp = serverIp;
		this.port = port;
	}	
	
	public byte[] getReqMsg(){
		return reqData;
	}
	
	//�õ���Ӧ����
	public String getResponseText(){
		return resContent;
	}
	
	//���ɱ��� 
	private void makePackage( String content ){
		try {
			//�������ݣ���3DES��hex
			byte[] data = content.getBytes("UTF-8");
			data = DesUtil.trides_crypt(DesUtil.KEYBYTES, data );
			String dataStr = DesUtil.bytesToHexString(data);
			
			//�����������
			data = dataStr.getBytes("utf-8");
			byte[] type = new byte[]{msgType,subType};
			byte tmp = calcRedundantCode( type, (byte)0x00 );
			tmp = calcRedundantCode( data, tmp );
			byte[] code = new byte[1];
			code[0] = tmp;
			
			//��װ����
			ArrayList<byte[]> list = new ArrayList<byte[]>();			
			list.add( StringUtil.IntToByte(data.length+3));//���ݳ���
			list.add( type  );	//����������
			list.add( data );//��������
			list.add( code );//У����
			
			reqData = DataFormat.ArrayList2Bytes( list );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���á����ͽ��ס��ı���
	 * @param serialNo	�豸���к�
	 * @param business	������
	 * @param outlets	�����
	 * @param transData	��������
	 * @return true��ʾ�ɹ���false��ʾʧ��
	 */
	public boolean setPushTransactionMsg( String serialNo, String business, 
			String outlets, String transData ){
		msgType = MSGTYPE_TRANSACTION;
		subType = SUBTYPE_TRANSACTION_Add;
		try{//����activity������˳�
			//���ɱ�������			
			JSONObject objData = new JSONObject( transData );
			JSONArray arrData = new JSONArray();
			arrData.put(objData);
			JSONStringer js = new JSONStringer();
			js.object().key("serial").value(serialNo).key("business").value(business).
						key("outlets").value(outlets).key("data").value(arrData).endObject();
	//		js.object().key("data").value(arrData).endObject();
			String content = js.toString();
			
			Log.e("socket","conent is "+content);
			//�������ݰ�
			makePackage( content );
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//�ύ��ǰ��Ϣ��������
	public boolean commit(){
		boolean ret = false;
		errorinfo = "������֮���ͨ��ʧ��";
		
		//δ��ȷ������Ϣ����
		if( reqData == null )
			return ret;
		
		//���ӷ������������ͱ���		
		SocketClient client = new SocketClient();
		if( client.SocketConnect( serverIp, port, 5 )){
			try {
				if( ErrorUtil.ERR_SUCCESS == client.sendMessage( reqData, reqData.length )){
					
					//��ȡ���ݰ�����
					byte[] dataByte = new byte[4];
					int readlen  = client.readMessage( dataByte, 4, null, 5 );//�ȶ�ȡͷ�ĸ��ֽڣ������������Ҫ���ĳ���
					if( readlen == 4 ){//��ȡ�ɹ�
						int readLen = StringUtil.bytesToIntByBigEndian(dataByte, 0);//תΪint

						//����ʵ�ʵ�����
						byte[] buffer = new byte[readLen];
						int msglen = client.readMessage( buffer, readLen, null, 5);
						if( msglen == readLen ){
							//�ж������Ƿ���Ч
							if( buffer[0] == msgType ){
								//ȡ�����ݣ�������
								String data = new String( buffer, 2, buffer.length-3 );//��ȥ��2�������ֽں�У���ֽ�	
								byte[] encryptedData = StringUtil.StringToHexA(data);
								byte[] plainData = DesUtil.trides_decrypt( DesUtil.KEYBYTES, encryptedData);
								resContent = new String( plainData, 0, plainData.length);
								Log.e("socket","resContent is "+resContent);
								
								if( buffer[1] == subType ){//��ȷ
									ret = true;
									errorinfo = "";
								}
								else if( buffer[1] == SUBTYPE_EXCEPTION  ){//�쳣
									JSONObject jsexception = new JSONObject(resContent);
									Log.e( TAG_LOG, "exception:" + jsexception.getString( "exception" ));
									errorinfo = jsexception.getString( "exception" );
								}
							}//else{//��¼����ֵ
						}
					}
				}			
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				client.SocketClose();	
			}						
		}
		//������
		/*ret = true;
		resContent = "{'time':'2015-06-11 10:05:22', 'code':'0001'}";*/
		return ret;
	}

	/**
	 * ��������У��ֵ
	 * @param data	��У������
	 * @param initValue У��ֵ�ĳ�ʼֵ
	 * @return	У��ֵ
	 */
	private byte calcRedundantCode( byte[] data, byte initValue )
	{
		for ( int i = 0; i < data.length; i++ )
		{
			initValue = (byte)(initValue ^ data[i]);
		}
		
		return initValue;
	}
	
	//��ȡ������Ϣ
	public String getErrorInfo(){
		return errorinfo;
	}
}
