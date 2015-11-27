package com.centerm.autofill.setting;

import java.util.HashMap;

//各个版本的不同配置
public class DefaultConfig {

	//公共的配置
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

	//版本的默认配置
	public static HashMap<String, String> getDefConfig(){
		HashMap<String, String> map = new HashMap<String, String>();
		String[] defConfig = DEFCONFIG_NMGDMQ;//不同的版本要附不同的值

		for( int i = 0; i < defConfig.length; i += 2 ){
			map.put( defConfig[i], defConfig[i+1]);
		}

		//填写公共的配置
		for( int i = 0; i < DEFCONFIG_COMMON.length; i += 2){
			if( !map.containsKey(DEFCONFIG_COMMON[i]) )
				map.put( DEFCONFIG_COMMON[i], DEFCONFIG_COMMON[i+1]);
		}
		return map;		
	}


	//河南农信
	private final static String[] DEFCONFIG_HNNX = {
		AutofillSettings.NAME_CLIENTABBR, "hnnx",
		AutofillSettings.NAME_CLIENTNAME, "河南省农村信用社"
	};

	//内蒙古农信
	private final static String[] DEFCONFIG_NMGNX = {
		AutofillSettings.NAME_CLIENTABBR, "nmgnx",
		AutofillSettings.NAME_CLIENTNAME, "包头农商银行"
	};

	// 中国邮政
	private final static String[] DEFCONFIG_PSBC = {
		AutofillSettings.NAME_CLIENTABBR, "HuBeipsbc",
		AutofillSettings.NAME_CLIENTNAME, "中　国　邮　政"
	};

	// 内蒙古达茂旗
	private final static String[] DEFCONFIG_NMGDMQ = {
		AutofillSettings.NAME_CLIENTABBR, "nmgdmq",
		AutofillSettings.NAME_CLIENTNAME, "达茂旗农信社"
	};
}
