package com.centerm.autofill.appframework.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.text.TextUtils;

public class JsonUtil {

	/**
	 * @Title: getJsonData
	 * @Description: ��װjson���ݰ�
	 * @param SerialNo
	 *            ���ŶӺ�
	 * @param data
	 *            ��ҵ������map
	 * @return ��װ������ݰ�
	 * @throws
	 */
	public static JSONStringer getJsonData(String SerialNo,
			HashMap<String, String> data) {
		JSONObject objData = new JSONObject(data);
		JSONArray arrData = new JSONArray();
		arrData.put(objData);

		
		JSONStringer js = new JSONStringer();
		try {
			js.object().key("queryno").value(SerialNo).key("record").value(arrData).endObject();
			
			//js.object().key("serial").value("dev_1").key("business").value("1002").
			//			key("outlets").value("1001").key("data").value(arrData).endObject();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return js;
	}

	/**
	 * �ַ���ת��list<map>
	 *
	 * @param param �ַ���
	 * @return list<map>
	 */
	public static  List<Map<String, Object>> String2Json(String param) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		int start = param.indexOf('[');
		int end = param.lastIndexOf(']');
		String content = param.substring(start, end + 1);
		try {
			list = getList(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	

	/**
	 * ������json�����ַ�����������list��
	 * 
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	private static List<Map<String, Object>> getList(String jsonStr)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONArray ja = new JSONArray(jsonStr);
		for (int j = 0; j < ja.length(); j++) {
			String jm = ja.get(j) + "";
			if (jm.indexOf("{") == 0) {
				Map<String, Object> m = getJosn(jm);
				list.add(m);
			}
		}
		return list;
	}
	
	/**
	 * ������json�ַ�����������map��
	 * 
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */	
	private static Map<String, Object> getJosn(String jsonStr) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(jsonStr)) {
			JSONObject json = new JSONObject(jsonStr);
			Iterator i = json.keys();
			while (i.hasNext()) {
				String key = (String) i.next();
				String value = json.getString(key);
				if (value.indexOf("{") == 0) {
					map.put(key.trim(), getJosn(value));
				} else if (value.indexOf("[") == 0) {
					map.put(key.trim(), getList(value));
				} else {
					map.put(key.trim(), value.trim());
				}
			}
		}
		return map;
	}
	
	//���ؾ���������json�ַ���
	public static String getSortedJsonString( TreeMap<String,String> map ){
		Set<String> keyset = map.keySet();
		
		JSONStringer js = new JSONStringer();
		try {
			js.object();
			Iterator<String> it = keyset.iterator();
			while( it.hasNext() ){
				String key = it.next();
				js.key( key );
				js.value( map.get( key ));
			}
			js.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return js.toString();
	}
}
