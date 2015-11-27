package com.centerm.autofill.appframework.service;

import java.io.UnsupportedEncodingException;

import com.centerm.autofill.appframework.common.SocketClient;
import com.centerm.autofill.appframework.utils.DefUtil;
import com.centerm.autofill.appframework.utils.DesUtil;
import com.centerm.autofill.appframework.utils.ErrorUtil;
import com.centerm.autofill.appframework.utils.StringUtil;



public class ParamParase {

	
	/**
	* @Title: GetPackage
	* @Description: 生产数据包
	* @param[in] byType：类型码
	* @param[in] szData：数据
	* @param[in] nDataLen：数据长度
	* @param[out] szPkg：通讯包数据
	* @return >=0:通讯包数据长度 <0:错误码
	* @throws
	*/ 
	private static int GetPackage(byte byType, byte[] szData, int nDataLen, byte[] szPkg)
	{
		byte[] byDesData = null;
		byte[] byReq = null;
		int nReqLen = 0;
		byte[] byKey = { 0x11, 0x22, 0x4F, 0x58, (byte)0x88, 0x10, 0x40, 0x38, 
				  0x28, 0x25, 0x79, 0x51, (byte)0xCB, (byte)0xDD, 0x55, 0x66, 
				  0x77, 0x29, 0x74, (byte)0x98, 0x30, 0x40, 0x36, (byte)0xE2 };
		
		if(szData != null && nDataLen > 0)
		{	
			//数据不足8的倍数填充0x00
			int nTmpLen = 0;
			if(nDataLen % 8 == 0)
			{
				nTmpLen = nDataLen;
			}
			else
			{
				nTmpLen = (nDataLen/8+1)*8;
			}
			byte[] byTmp = new byte[nTmpLen];
			System.arraycopy(szData, 0, byTmp, 0, nDataLen);
			
			//加密
			byDesData = DesUtil.encrypt(byTmp, byKey);
			
			//拆分
			try {
				byReq = StringUtil.HexToStringA(byDesData, byDesData.length).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nReqLen = byReq.length;
			
			if(byReq.length + DefUtil.MSG_HEAD_LEN > szPkg.length)
			{
				return ErrorUtil.ERR_PARAM;
			}
			
			//填充报文体
			System.arraycopy(byReq, 0, szPkg, DefUtil.MSG_HEAD_LEN, byReq.length);
		}
		
		if(DefUtil.MSG_HEAD_LEN > szPkg.length)
		{
			return ErrorUtil.ERR_PARAM;
		}
		
		//报文头
		int nIndex = 0;
		szPkg[nIndex++] = (byte)0xff;
		szPkg[nIndex++] = (byte)0xff;
		szPkg[nIndex++] = byType;
		szPkg[nIndex++] = (byte)((nReqLen >> 24) & 0xff);
		szPkg[nIndex++] = (byte)((nReqLen >> 16) & 0xff);
		szPkg[nIndex++] = (byte)((nReqLen >> 8) & 0xff);
		szPkg[nIndex++] = (byte)((nReqLen >> 0) & 0xff);
		
		return nIndex + nReqLen;
	}
	
	/**
	* @Title: ReadPackage
	* @Description: 读数据包
	* @param client：客户端socket
	* @param byType：类型码
	* @param szData：通讯包数据
	* @return >=0通讯包数据长度 <0：错误码
	* @throws
	*/ 
	private static int ReadPackage(SocketClient client, byte[] byType, byte[] szData)
	{
		//接收头数据
		byte [] szHead = new byte[8];
		int nReadLen = client.readMessage(szHead, DefUtil.MSG_HEAD_LEN, null, DefUtil.DEF_TIMEOUT);
		//判断头数据长度
		if(nReadLen != DefUtil.MSG_HEAD_LEN)
		{
			System.out.println("recv msg head length error");
			return ErrorUtil.ERR_PACKAGE;
		}
		
		//判断报文头
		if (szHead[0] != (byte)0xff || szHead[1] != (byte)0xff)
		{
			System.out.println("recv msg head magic error");
			return ErrorUtil.ERR_PACKAGE;	
		}
		
		//从头部获取数据长度
		byType[0] = szHead[2];
		int nDataLen = ((szHead[3]&0xff) << 24) 
				| ((szHead[4]&0xff) << 16) 
				| ((szHead[5]&0xff) << 8) 
				| ((szHead[6]&0xff) << 0);
		System.out.println("recv msg head data length is:" + nDataLen);
		if (nDataLen <= 0)
		{
			return 0;
		}

		//接收数据部分
		byte[] szRes = new byte[nDataLen];
		nReadLen = client.readMessage(szRes, nDataLen, null, DefUtil.DEF_TIMEOUT);
		System.out.println("recv msg data length is:" + nReadLen);
		if(nReadLen <= 0)
		{
			return ErrorUtil.ERR_READ;
		}
		
		//合并数据
		byte[] szDesData = StringUtil.StringToHexA(new String(szRes));
		
		//解密数据部分
		byte[] byKey = { 0x11, 0x22, 0x4F, 0x58, (byte)0x88, 0x10, 0x40, 0x38, 
				  0x28, 0x25, 0x79, 0x51, (byte)0xCB, (byte)0xDD, 0x55, 0x66, 
				  0x77, 0x29, 0x74, (byte)0x98, 0x30, 0x40, 0x36, (byte)0xE2 };
		byte [] byTmp = DesUtil.decrypt(szDesData, byKey);
		
		if(byTmp.length > szData.length)
		{
			return ErrorUtil.ERR_PARAM;
		}
		System.arraycopy(byTmp, 0, szData, 0, byTmp.length);
		
		return byTmp.length;
	}
	
	/**
	* @Title: SendJsonAndRecv
	* @Description: 发送json并且接收返回数据
	* @param strIp：服务器IP
	* @param nPort：服务器端口
	* @param nBusType：类型码
	* @param szJs：json数据
	* @param nJsLen：json数据长度
	* @param[out] szRecv：接收数据
	* @return >=0:解析后数据长度；<0：失败
	* @throws
	*/ 
	public static int SendJsonAndRecv(String strIp, int nPort, 
			int nBusType, byte [] szJs, int nJsLen, byte[] szRecv)
	{	
		
		//连接服务器
		SocketClient client = new SocketClient();
		client.SocketConnect(strIp, nPort, DefUtil.DEF_TIMEOUT);
		
		//发送数据包
		byte[] pOutPkg = new byte[DefUtil.MSG_BODY_MAX];
		int nOutPkgLen = GetPackage((byte)nBusType, szJs, nJsLen, pOutPkg);
		//client.sendMessage(pOutPkg, nOutPkgLen);
		
		int length = client.sendMessage(szJs, nJsLen);
		pOutPkg = null;
		
		//读取数据包
		byte[] type = new byte[2];
		int nInDataLen = ReadPackage(client, type, szRecv);
		if (nInDataLen < 0)
		{
			client.SocketClose();
			return nInDataLen;
		}
		
		int nRet = (type[0] == (byte)0x80 ? nInDataLen : ErrorUtil.ERR_ERROR);
		client.SocketClose();
		
		return nRet;
	}
}
