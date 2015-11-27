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

	private DatePickerDialog dlg;		//����ѡ��Ի���
	private Button btnDate;				//���ڰ�ť
	private int date_year = 0;			//����
	private int date_month = 0;			//����
	private int date_day = 0;			//����
	
	public DatePickerButton( Context context, Button btn, int year, int month, int day){
		
		//��ʼ��������
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
		
		//��ť����ʱ������ѡ���
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
	
	//����ѡ�������
	private DatePickerDialog.OnDateSetListener setDateListener = new DatePickerDialog.OnDateSetListener() { //
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {			
			date_year = year;
			date_month = month + 1;
			date_day = day;
			btnDate.setText( buildDate( year, month + 1, day ));
		}
	};
	
	//�������޸ĳ� yyyy��mm��dd��
	private String buildDate( int yyyy, int mm, int dd ){
		StringBuilder sb = new StringBuilder();
		sb.append(yyyy);
		sb.append("��");
		sb.append( mm );
		sb.append("��");
		sb.append(dd);
		sb.append("��");
		return sb.toString();
	}
	
	//������ڱ�ʾyyyy��mm��dd��
	public String getDate(){
		StringBuilder sb = new StringBuilder();
		sb.append( date_year );
		sb.append("��");
		sb.append( date_month );
		sb.append("��");
		sb.append( date_day );
		sb.append("��");
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
	
	//������ʾ��Ϣ
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
