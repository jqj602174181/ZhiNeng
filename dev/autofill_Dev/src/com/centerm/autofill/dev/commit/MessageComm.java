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
 * 封装服务端的报文协议通信
 */
public class MessageComm {
	//操作类型码
	public final static byte MSGTYPE_TRANSACTION = 3;		//交易
	
	//子类型码
	public final static byte SUBTYPE_TRANSACTION_Add = 1;	//添加交易
	public final static byte SUBTYPE_EXCEPTION = 102;		//发生异常
	
	public final static String TAG_LOG = "dev_commit";		//日志标志
	public final static int ERROR_OK = 0;					//成功
	public final static int ERROR_UNKNOWN = -1;				//未知错误
	public final static int ERROR_PROTOCOL = -2;			//协议错误
	public final static int ERROR_UNREGISTER = -3;			//应用未注册
	
	private byte msgType;					//操作码
	private byte subType;					//操作子类型
	private byte[] reqData;					//请求数据包内容
	private String resContent;				//接收到的数据内容
	private String serverIp;				//服务端IP
	private int port;						//通信端口号
	private String errorinfo = "";			//错误信息
	
	public MessageComm( String serverIp, int port ){
		this.serverIp = serverIp;
		this.port = port;
	}	
	
	public byte[] getReqMsg(){
		return reqData;
	}
	
	//得到响应报文
	public String getResponseText(){
		return resContent;
	}
	
	//生成报文 
	private void makePackage( String content ){
		try {
			//加密数据，先3DES再hex
			byte[] data = content.getBytes("UTF-8");
			data = DesUtil.trides_crypt(DesUtil.KEYBYTES, data );
			String dataStr = DesUtil.bytesToHexString(data);
			
			//计算各个分量
			data = dataStr.getBytes("utf-8");
			byte[] type = new byte[]{msgType,subType};
			byte tmp = calcRedundantCode( type, (byte)0x00 );
			tmp = calcRedundantCode( data, tmp );
			byte[] code = new byte[1];
			code[0] = tmp;
			
			//组装报文
			ArrayList<byte[]> list = new ArrayList<byte[]>();			
			list.add( StringUtil.IntToByte(data.length+3));//数据长度
			list.add( type  );	//加入类型码
			list.add( data );//加入内容
			list.add( code );//校验码
			
			reqData = DataFormat.ArrayList2Bytes( list );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 配置“推送交易”的报文
	 * @param serialNo	设备序列号
	 * @param business	交易码
	 * @param outlets	网点号
	 * @param transData	交易数据
	 * @return true表示成功，false表示失败
	 */
	public boolean setPushTransactionMsg( String serialNo, String business, 
			String outlets, String transData ){
		msgType = MSGTYPE_TRANSACTION;
		subType = SUBTYPE_TRANSACTION_Add;
		try{//避免activity因错误退出
			//生成报文数据			
			JSONObject objData = new JSONObject( transData );
			JSONArray arrData = new JSONArray();
			arrData.put(objData);
			JSONStringer js = new JSONStringer();
			js.object().key("serial").value(serialNo).key("business").value(business).
						key("outlets").value(outlets).key("data").value(arrData).endObject();
	//		js.object().key("data").value(arrData).endObject();
			String content = js.toString();
			
			Log.e("socket","conent is "+content);
			//生成数据包
			makePackage( content );
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//提交当前消息，并解析
	public boolean commit(){
		boolean ret = false;
		errorinfo = "与服务端之间的通信失败";
		
		//未正确配置消息类型
		if( reqData == null )
			return ret;
		
		//连接服务器，并发送报文		
		SocketClient client = new SocketClient();
		if( client.SocketConnect( serverIp, port, 5 )){
			try {
				if( ErrorUtil.ERR_SUCCESS == client.sendMessage( reqData, reqData.length )){
					
					//读取数据包长度
					byte[] dataByte = new byte[4];
					int readlen  = client.readMessage( dataByte, 4, null, 5 );//先读取头四个字节，来计算接下来要读的长度
					if( readlen == 4 ){//读取成功
						int readLen = StringUtil.bytesToIntByBigEndian(dataByte, 0);//转为int

						//接收实际的数据
						byte[] buffer = new byte[readLen];
						int msglen = client.readMessage( buffer, readLen, null, 5);
						if( msglen == readLen ){
							//判断内容是否有效
							if( buffer[0] == msgType ){
								//取出内容，并解密
								String data = new String( buffer, 2, buffer.length-3 );//舍去第2个类型字节和校验字节	
								byte[] encryptedData = StringUtil.StringToHexA(data);
								byte[] plainData = DesUtil.trides_decrypt( DesUtil.KEYBYTES, encryptedData);
								resContent = new String( plainData, 0, plainData.length);
								Log.e("socket","resContent is "+resContent);
								
								if( buffer[1] == subType ){//正确
									ret = true;
									errorinfo = "";
								}
								else if( buffer[1] == SUBTYPE_EXCEPTION  ){//异常
									JSONObject jsexception = new JSONObject(resContent);
									Log.e( TAG_LOG, "exception:" + jsexception.getString( "exception" ));
									errorinfo = jsexception.getString( "exception" );
								}
							}//else{//记录错误值
						}
					}
				}			
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				client.SocketClose();	
			}						
		}
		//测试用
		/*ret = true;
		resContent = "{'time':'2015-06-11 10:05:22', 'code':'0001'}";*/
		return ret;
	}

	/**
	 * 计算冗余校验值
	 * @param data	待校验数据
	 * @param initValue 校验值的初始值
	 * @return	校验值
	 */
	private byte calcRedundantCode( byte[] data, byte initValue )
	{
		for ( int i = 0; i < data.length; i++ )
		{
			initValue = (byte)(initValue ^ data[i]);
		}
		
		return initValue;
	}
	
	//获取错误信息
	public String getErrorInfo(){
		return errorinfo;
	}
}
