package com.centerm.autofill.appframework.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONStringer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.centerm.autofill.appframework.listener.QueryDataListener;
import com.centerm.autofill.appframework.utils.DefUtil;
import com.centerm.autofill.appframework.utils.DesUtil;
import com.centerm.autofill.appframework.utils.JsonUtil;
import com.centerm.autofill.appframework.utils.StringUtil;


//10.18.59.222,3131
public class CtClient {
	private static final int SUCCESS_SAVE = 1;	
	private static final int SUCCESS_QUERY = 2;
	
	private static final int ERROR_SAVE = -1;
	private static final int ERROR_QUERY = -2;

	
	private static CtClient instance;
	private QueryDataListener queryListener = null;
	
	
	private CtClient(){	
	}
	
	public static CtClient getInstance() {
		if( instance == null ) {
			instance = new CtClient();
		}
		return instance;
	}
	
	
	/**
	* @Title: SaveUserData
	* @Description: �����û����ݣ����̲߳���
	* @param host��������ip
	* @param port���������˿�
	* @param map ��ҵ������map
	* @throws
	*/ 
	public void SaveUserData(final String host, final int port, 
			final Handler handler, final HashMap<String, String> map)
	{
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				//JSONStringer js_test = JsonUtil.getJsonData("0000", map);//����ʹ��
				//String SerialNo = "0001";
				//��ȡ�ŶӺ�
				
				String SerialNo = GetSerialNumber(host, port);
				if( SerialNo == null )
				{
					handler.sendEmptyMessage(-1);
					return;
				}
				//Log.i("Deposit", "get serial num:" + SerialNo);
		        
				//����json���ݰ�
				JSONStringer js = JsonUtil.getJsonData(SerialNo, map);
				//System.out.println(js.toString()); 
				
				//����������
				if( CommBusiness(host, port, js.toString()) < 0 )
				{
					handler.sendEmptyMessage(-1);
				}
				else{
					Message msg = new Message();
					msg.what = 0;
					Bundle bundle = new Bundle();
					bundle.putString("serial", SerialNo);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	
	/**
	* @Title: QueryHistoryData
	* @Description: ��ѯ��ʷ���ݣ����̲߳���
	* @param host��������ip
	* @param port���������˿�
	* @param map ��ҵ������map
	* @throws
	*/ 
	public void QueryHistoryData(final String host, final int port, 
			final String param, QueryDataListener l)
	{
		Log.i("QueryHistoryData", "do work");
		queryListener = l;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//��ȡ�ŶӺ�
				Log.i("QueryHistoryData", "get serial");
				String SerialNo = GetSerialNumber(host, port);
				if( SerialNo == null )
				{
					mHandler.sendEmptyMessage(ERROR_QUERY);
					return;
				} 
				
				Log.i("client", "get serial num:" + SerialNo);
		        
				Log.i("client", "query param:" + param); 
				
				//����������
				String result = QueryInfo(host, port, param);
//				result = "{\"record\":[{\"title\":\"one\",\"content\":\"first\"},"
//						+ "{\"title\":\"two\",\"content\":\"second\"}]}";
//				result = "{\"record\":[{\"longterm_flag\":\"2\",\"cus_idtype\":\"10\",\"trancode\":\"020202|010101|010201|010601\",\"prt_issue_addr\":\"�����й�������Ƿ־�\",\"profession_flag\":\"8\",\"prt_profession\":\"����\",\"post_code\":\"430000\",\"record_id_num\":\"350321198807200850\",\"issue_code\":\"350321\",\"record_account_num\":\"\",\"cus_idnum\":\"350321198807200850\",\"sign_date_year\":\"2015\",\"cus_addr\":\"����ʡ�������������ʯ������念��66��\",\"nationality_flag\":\"CN\",\"amount\":\"20.22\",\"cus_spell\":\"kangzhenpeng\",\"cus_name\":\"������\",\"cus_sex_flag\":\"1\",\"sign_date_month\":\"3\",\"sign_date_day\":\"26\",\"id_duedate\":\"20160712\",\"prt_idtype\":\"1\",\"prt_currency\":\"1\",\"prt_nationality \":\"1\",\"mobile_num\":\"18950492168\"},{\"longterm_flag\":\"2\",\"cus_idtype\":\"10\",\"trancode\":\"020202|010101|010201|010601\",\"prt_issue_addr\":\"�����й�������Ƿ־�\",\"profession_flag\":\"8\",\"prt_profession\":\"���һ��ء���Ⱥ��֯����ҵ����ҵ��λ��ҵ��Ա\",\"post_code\":\"430000\",\"record_id_num\":\"350321198807200850\",\"issue_code\":\"350321\",\"record_account_num\":\"\",\"cus_idnum\":\"350321198807200850\",\"sign_date_year\":\"2015\",\"cus_addr\":\"����ʡ�������������ʯ������念��66��\",\"nationality_flag\":\"CN\",\"amount\":\"10.00\",\"cus_spell\":\"kangzhenpeng\",\"cus_name\":\"������\",\"cus_sex_flag\":\"1\",\"sign_date_month\":\"21\",\"sign_date_day\":\"26\",\"id_duedate\":\"20160712\",\"prt_idtype\":\"1\",\"prt_currency\":\"1\",\"prt_nationality \":\"1\",\"mobile_num\":\"18950492168\"}]}";
				Log.i("client ", "result:" + result);
				if(result == null || result.isEmpty())
				{
					mHandler.sendEmptyMessage(ERROR_QUERY);
				}
				else{
					List list = JsonUtil.String2Json(result);
					
					Message msg = new Message();
					msg.what = SUCCESS_QUERY;
					Bundle bundle = new Bundle();
					msg.setData(bundle);
					msg.obj = list;
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	
	/**
	* @Title: GetSerialNumber
	* @Description: ��ȡ�ŶӺ�
	* @param strIP��������IP
	* @param nPort���������˿�
	* @return �ŶӺ��ַ���
	* @throws
	*/ 
	private String GetSerialNumber(String strIP, int nPort)
	{
		byte[] szResult = new byte[DefUtil.MSG_BODY_MAX];

		int nRetLen = ParamParase.SendJsonAndRecv(strIP, nPort, DefUtil.CMD_GET_SERIALNO, null, 0, szResult);
		
		String result = null;
		if(nRetLen > 0)
		{
			result = StringUtil.ByteToString(szResult);
		}
		return result;
	}
	
	/**
	* @Title: CommBusiness
	* @Description: ����ҵ������
	* @param strIP��������IP
	* @param nPort���������˿�
	* @param strParam��ҵ���������
	* @return 
	* @throws
	*/ 
	private int CommBusiness(String strIP, int nPort, String strParam)
	{
		
		byte[] szResult = new byte[DefUtil.MSG_BODY_MAX];
		
		byte[] byParam = null;
		try {
			byParam = strParam.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int nRetLen = ParamParase.SendJsonAndRecv(strIP, nPort, DefUtil.CMD_COMM_BUSSINESS, byParam, byParam.length, szResult);
		

		return nRetLen;
	}
	
	/**
	* @Title: QueryInfo
	* @Description: ��ѯ����
	* @param strIP��������IP
	* @param nPort���������˿�
	* @param strParam����ѯ����
	* @return ��ѯ���
	* @throws
	*/ 
	private String QueryInfo(String strIP, int nPort, String strParam)
	{
		byte[] szResult = new byte[DefUtil.MSG_BODY_MAX];
		
		byte[] byParam = null;
		try {
			byParam = strParam.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int nRetLen = ParamParase.SendJsonAndRecv(strIP, nPort, DefUtil.CMD_QUERY_INFO, byParam, byParam.length, szResult);
		
		String result = null;
		if(nRetLen > 0)
		{
			result = StringUtil.ByteToString(szResult);
		}
		return result;
	}
	
	//��Ϣ����
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			switch (msg.what) {
			case SUCCESS_QUERY:
				if(queryListener != null)
				{
					List list = (List) msg.obj;
					queryListener.onQuerySuc(list);
				}
				break;
			case ERROR_QUERY:	//��ȡ�������汾
				if(queryListener != null)
				{
					queryListener.onError(-1, "��ѯʧ��");
				}
				break;			
			default:
				break;
			}
		}
	};
}
