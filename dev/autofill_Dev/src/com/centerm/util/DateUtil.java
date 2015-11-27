package com.centerm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//�������
public class DateUtil {

	//��õ�ǰ�����գ���ʽ��yyyyMMdd������20150102
	public static String getSimpleDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format( new Date());
	}
	
	//��õ�ǰ����
	public static String getYear(){
		 return String.valueOf(Calendar.getInstance().get( Calendar.YEAR ));
	}
	
	//��õ�ǰ����
	public static String getMonth(){
		 return String.valueOf(Calendar.getInstance().get( Calendar.MONTH )+1);
	}
	
	//��õ�ǰ����
	public static String getDayofMonth(){
		 return String.valueOf(Calendar.getInstance().get( Calendar.DAY_OF_MONTH ));
	}
}
