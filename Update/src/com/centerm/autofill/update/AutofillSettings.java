package com.centerm.autofill.update;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.centerm.autofill.update.utils.EEPROM;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

//���ڶ�ȡ����
public class AutofillSettings {
	//ͨ�����key
	public static final String NAME_SERVERIP = "ServerIp";			//�����ip
	public static final String NAME_SERVERPORT = "ServerPort";		//�������˿�
	public static final String NAME_ORGNO = "OrgNo";				//��������¼�Ļ�����
	public static final String NAME_CHECKIDIP	= "CheckIdIp";		//�����˲�����ip
	public static final String NAME_CHECKIDPORT	= "CheckIdPort";	//�����˲����˶˿�
	public static final String NAME_CHECKIDORGNO = "CheckIdOrg";	//�����˲����͵Ļ�����
	
	//��������key
	public static final String NAME_LOCALIP	= "LocalIp";			//������̫��ip
	public static final String NAME_LOCALNETMASK = "LocalNetmask";	//������̫������
	public static final String NAME_LOCALGATEWAY = "LocalGateway";	//������̫������
	
	//�汾���key
	public static final String NAME_CLIENTABBR = "ClientAbbr";	//�ͻ�����Ӣ����д
	public static final String NAME_CLIENTNAME = "ClientName";	//�ͻ�������
	
	private static final Uri CONTENT_URI = 
			Uri.parse( "content://com.centerm.autofill.settingprovider/settings");

	//�б������
	private static final String KEY_NAME = "name";	//��������
	private static final String KEY_VALUE = "value";	//���õ�ֵ
	
	Context context;//������
	public AutofillSettings( Context context ){
		this.context = context;
	}
	
	//������� 
	private String getConf( String name ){
		ContentResolver cr = context.getContentResolver();
		
		//���ɲ���
		String[] results_columns = new String[]{
				KEY_NAME,
				KEY_VALUE
		};		
		String selection = KEY_NAME + "='" + name + "'";
		String[] selectionArgs = null;
		String sortOrder = null;
		
		//��ѯ���ݿ�
		Cursor cursor = cr.query( CONTENT_URI,
					results_columns, selection, selectionArgs, sortOrder);
		
		//�õ�ֵ�󷵻أ�Ĭ��ֵ�ǿ�
		String value = "";
		if( cursor.moveToFirst()){
			int columnIndex = cursor.getColumnIndexOrThrow( KEY_VALUE );
			value = cursor.getString( columnIndex );
		}
		cursor.close();
		return value;		
	}	
	
	//get��set����
	//������IP
	public String getServerIp(){
		return getConf( NAME_SERVERIP );
	}
	
	//�������˿�
	public int getServerPort(){
		String port = getConf( NAME_SERVERPORT );
		return Integer.parseInt( port );
	}
	//������
	public String getOrgNO(){
		return getConf( NAME_ORGNO );
	}	
	
	//����IP
	public String getLocalIp(){
		String ip = getConf( NAME_LOCALIP );
		return ip;
	}	
	
	//������������
	public String getLocalNetmask(){
		String netmask = getConf( NAME_LOCALNETMASK );	
		return netmask;
	}	
	
	//��������
	public String getLocalGateway(){
		String gateway = getConf( NAME_LOCALGATEWAY );
		return gateway;
	}	
	
	//�����˲������IP���˿ںͻ�����
	public String getCheckIDServerIp(){
		return getConf( NAME_CHECKIDIP );
	}	
	public int getCheckIDServerPort(){
		String port = getConf( NAME_CHECKIDPORT );
		return Integer.parseInt( port );
	}	
	public String getCheckIDServerOrg(){
		return getConf( NAME_CHECKIDORGNO );
	}
	
	//��ÿͻ�Ӣ����
	public String getClientAbbr(){
		return getConf( NAME_CLIENTABBR );
	}
	//��ÿͻ�������
	public String getClientName(){
		return getConf( NAME_CLIENTNAME );
	}
	
	//������
	public String getDevNo(){
		//ȡ��ǰ��ǿ��ַ������Ҫtrim
		return EEPROM.read(EEPROM.PRODUCTSN).trim();
	}
	
	//��ȡϵͳ��ǰ�汾��
	public static String getSysVersion(){
		
		return GetBuildProproperties( "ro.product.version");
	}
	
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
}
