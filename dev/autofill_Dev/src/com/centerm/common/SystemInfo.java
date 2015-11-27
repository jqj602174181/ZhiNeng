package com.centerm.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SystemInfo {

	// ��ȡbuild.prop�е�ָ�����ԣ� ��E10��oqc������
	private  static String GetBuildProproperties( String PropertiesName ) {
		String ProperValue = "";
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = new BufferedInputStream(new FileInputStream(new File(
					"/system/build.prop")));
			br = new BufferedReader(new InputStreamReader(is));
			String strTemp = "";
			while ((strTemp = br.readLine()) != null) {
				// ����ļ�û�ж��������
				if (strTemp.indexOf(PropertiesName) != -1) {
					ProperValue = strTemp.substring(strTemp.indexOf("=") + 1);
					break;
				}
			}
			br.close();
			is.close();
		} catch (Exception e) {
			if (e.getMessage() != null)
				System.out.println(e.getMessage());
			else
				e.printStackTrace();
		}

		return ProperValue;
	}
	
	//��ȡϵͳ��ǰ�汾��
	public static String getSysVersion(){
		
		return GetBuildProproperties( "ro.product.version");
	}
}
