package com.centerm.autofill.setting.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtil {

	public static final String PREFERENCE_NAME = "server";
	private static SharedPreferences mSharedPreferences;
	private static ConfigUtil mConfigUtils;
	private static SharedPreferences.Editor editor;

	private String KEY_SERVER_IP = "server_ip";
	private String KEY_SERVER_PORT = "server_port";
	
	private String KEY_CHECKID_IP = "checkid_ip";
	private String KEY_CHECKID_PORT = "checkid_port";
	private String KEY_CHECKID_ORG = "checkid_org";
	private String DEF_SERVER_IP = "172.27.35.1";
	private String DEF_CHECKID_IP = "172.27.35.2";
	private String DEF_CHECKID_ORG = "00001";
	private int DEF_SERVER_PORT = 80;

	private ConfigUtil(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public static ConfigUtil getInstance(Context cxt) {
		if (mConfigUtils == null) {
			mConfigUtils = new ConfigUtil(cxt);
		}
		editor = mSharedPreferences.edit();
		return mConfigUtils;
	}

	public void setServerIP(String strIP) {
		editor.putString(KEY_SERVER_IP, strIP);
		editor.commit();
	}
	
	public void setCheckIdOrg(String strOrg) {
		editor.putString(KEY_CHECKID_ORG, strOrg);
		editor.commit();
	}
	
	public String getCheckIdOrg()
	{
		return mSharedPreferences.getString(KEY_CHECKID_ORG, DEF_CHECKID_ORG);
	}
	
	public void setCheckIdIP(String strIP) {
		editor.putString(KEY_CHECKID_IP, strIP);
		editor.commit();
	}

	public String getServerIP() {
		return mSharedPreferences.getString(KEY_SERVER_IP, DEF_SERVER_IP);
	}

	public void setServerPort(int nPort) {
		editor.putInt(KEY_SERVER_PORT, nPort);
		editor.commit();
	}
	
	public void setCheckIdPort(int nPort) {
		editor.putInt(KEY_CHECKID_PORT, nPort);
		editor.commit();
	}
	
	public int getServerPort() {
		return mSharedPreferences.getInt(KEY_SERVER_PORT, DEF_SERVER_PORT);
	}
	
	public String getCheckIdIp()
	{
		return mSharedPreferences.getString(KEY_CHECKID_IP, DEF_CHECKID_IP);
	}
	
	public int getCheckIdPort() {
		return mSharedPreferences.getInt(KEY_CHECKID_PORT, DEF_SERVER_PORT);
	}
}
