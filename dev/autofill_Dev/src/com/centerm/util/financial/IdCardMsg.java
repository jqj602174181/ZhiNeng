package com.centerm.util.financial;

public class IdCardMsg implements Cloneable{

	public IdCardMsg() {
	}

	public String name; //名字
	public String sex;	//性别
	public String nation_str; //国籍

	public String birth_year ; //生日
	public String birth_month ;
	public String birth_day ;
	public String address ; //地址
	public String id_num ; //身份证号
	public String sign_office; //签发机关

	public String useful_s_date_year ; //有效期起始日期
	public String useful_s_date_month ;
	public String useful_s_date_day ;

	public String useful_e_date_year ; //有效期截止日期
	public String useful_e_date_month;
	public String useful_e_date_day ;
	
	public byte[] photo; //照片数据

	@Override
	public Object clone(){
		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
