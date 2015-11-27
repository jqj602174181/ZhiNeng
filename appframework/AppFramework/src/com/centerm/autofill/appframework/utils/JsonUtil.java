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
	 * @Description: 封装json数据包
	 * @param SerialNo
	 *            ：排队号
	 * @param data
	 *            ：业务数据map
	 * @return 封装后的数据包
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
	 * 字符串转成list<map>
	 *
	 * @param param 字符串
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
	 * 将单个json数组字符串解析放在list中
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
	 * 将单个json字符串解析放在map中
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
	
	//返回经过排序后的json字符串
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
