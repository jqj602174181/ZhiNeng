package com.centerm.autofill.appframework.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;

public class DatePickerButton {

	private DatePickerDialog dlg;		//日期选择对话框
	private Button btnDate;				//日期按钮
	private int date_year = 0;			//日年
	private int date_month = 0;			//日月
	private int date_day = 0;			//日日
	
	public DatePickerButton( Context context, Button btn, int year, int month, int day){
		
		//初始化年月日
		Calendar cal = Calendar.getInstance();
		if( year == 0 ){
			date_year = cal.get(Calendar.YEAR);
			date_month = cal.get(Calendar.MONTH) + 1;
			date_day = cal.get(Calendar.DAY_OF_MONTH);
		}else{
			date_year = year;
			date_month = month;
			date_day = day;
		}
		
		//按钮按下时，弹出选择框
		dlg = new DatePickerDialog( context, setDateListener, date_year, date_month -1, date_day );
		btnDate = btn;
		btnDate.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View btn) {
				dlg.show();
			}
		});
		
		if( year != 0 ){
			updateInfo();
		}
	}

	
	public DatePickerButton( Context context, Button btn ){
		this( context, btn, 0, 0, 0);
	}		
	
	//日期选择监听器
	private DatePickerDialog.OnDateSetListener setDateListener = new DatePickerDialog.OnDateSetListener() { //
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {			
			date_year = year;
			date_month = month + 1;
			date_day = day;
			btnDate.setText( buildDate( year, month + 1, day ));
		}
	};
	
	//把日期修改成 yyyy年mm月dd日
	private String buildDate( int yyyy, int mm, int dd ){
		StringBuilder sb = new StringBuilder();
		sb.append(yyyy);
		sb.append("年");
		sb.append( mm );
		sb.append("月");
		sb.append(dd);
		sb.append("日");
		return sb.toString();
	}
	
	//获得日期表示yyyy年mm月dd日
	public String getDate(){
		StringBuilder sb = new StringBuilder();
		sb.append( date_year );
		sb.append("年");
		sb.append( date_month );
		sb.append("月");
		sb.append( date_day );
		sb.append("日");
		return sb.toString();
	};
	
	public String getDateYYYYMMDD(){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance();
			cal.set( date_year,  date_month - 1, date_day);
			return format.format( cal.getTime() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	//更新显示信息
	private void updateInfo(){
		if( btnDate != null )
			btnDate.setText( buildDate( date_year, date_month, date_day ));
	}


	public int getDate_year() {
		return date_year;
	}


	public void setDate_year(int date_year) {
		this.date_year = date_year;
	}


	public int getDate_month() {
		return date_month;
	}


	public void setDate_month(int date_month) {
		this.date_month = date_month;
	}


	public int getDate_day() {
		return date_day;
	}


	public void setDate_day(int date_day) {
		this.date_day = date_day;
	}
	
	
}
