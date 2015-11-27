package com.centerm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//日期相关
public class DateUtil {

	//获得当前年月日，格式是yyyyMMdd，例如20150102
	public static String getSimpleDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format( new Date());
	}
	
	//获得当前的年
	public static String getYear(){
		 return String.valueOf(Calendar.getInstance().get( Calendar.YEAR ));
	}
	
	//获得当前的月
	public static String getMonth(){
		 return String.valueOf(Calendar.getInstance().get( Calendar.MONTH )+1);
	}
	
	//获得当前的日
	public static String getDayofMonth(){
		 return String.valueOf(Calendar.getInstance().get( Calendar.DAY_OF_MONTH ));
	}
}
