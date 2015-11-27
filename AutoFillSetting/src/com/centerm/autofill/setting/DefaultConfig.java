package com.centerm.autofill.setting;

import java.util.HashMap;

//�����汾�Ĳ�ͬ����
public class DefaultConfig {

	//����������
	private final static String[] DEFCONFIG_COMMON = {
		AutofillSettings.NAME_SERVERIP, 	"",
		AutofillSettings.NAME_SERVERPORT, 	"3131",
		AutofillSettings.NAME_ORGNO, 		"",
		AutofillSettings.NAME_LOCALIP, 		"",
		AutofillSettings.NAME_LOCALNETMASK, "255.255.255.0",
		AutofillSettings.NAME_LOCALGATEWAY, "",
		AutofillSettings.NAME_CHECKIDIP,	"",
		AutofillSettings.NAME_CHECKIDPORT,	"80",
		AutofillSettings.NAME_CHECKIDORGNO,	"00001",	
		AutofillSettings.NAME_APPLAYOUT, "",
	};

	//�汾��Ĭ������
	public static HashMap<String, String> getDefConfig(){
		HashMap<String, String> map = new HashMap<String, String>();
		String[] defConfig = DEFCONFIG_NMGDMQ;//��ͬ�İ汾Ҫ����ͬ��ֵ

		for( int i = 0; i < defConfig.length; i += 2 ){
			map.put( defConfig[i], defConfig[i+1]);
		}

		//��д����������
		for( int i = 0; i < DEFCONFIG_COMMON.length; i += 2){
			if( !map.containsKey(DEFCONFIG_COMMON[i]) )
				map.put( DEFCONFIG_COMMON[i], DEFCONFIG_COMMON[i+1]);
		}
		return map;		
	}


	//����ũ��
	private final static String[] DEFCONFIG_HNNX = {
		AutofillSettings.NAME_CLIENTABBR, "hnnx",
		AutofillSettings.NAME_CLIENTNAME, "����ʡũ��������"
	};

	//���ɹ�ũ��
	private final static String[] DEFCONFIG_NMGNX = {
		AutofillSettings.NAME_CLIENTABBR, "nmgnx",
		AutofillSettings.NAME_CLIENTNAME, "��ͷũ������"
	};

	// �й�����
	private final static String[] DEFCONFIG_PSBC = {
		AutofillSettings.NAME_CLIENTABBR, "HuBeipsbc",
		AutofillSettings.NAME_CLIENTNAME, "�С������ʡ���"
	};

	// ���ɹŴ�ï��
	private final static String[] DEFCONFIG_NMGDMQ = {
		AutofillSettings.NAME_CLIENTABBR, "nmgdmq",
		AutofillSettings.NAME_CLIENTNAME, "��ï��ũ����"
	};
}
