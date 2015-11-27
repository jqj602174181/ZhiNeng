package com.centerm.autofill.appframework.utils;

public class DefUtil {
	public static final int DEF_TIMEOUT = 10;		//默认超时时间，单位s
	public static final int MSG_HEAD_LEN = 7;		//报文头长度
	public static final int MSG_BODY_MAX = 4096;	//报文体最大长度
	public static final int CMD_GET_SERIALNO = 0x61;	//获取排队号
	public static final int CMD_QUERY_INFO = 0x92;		//查询信息
	public static final int CMD_COMM_BUSSINESS = 0x54;	//存储数据
}
