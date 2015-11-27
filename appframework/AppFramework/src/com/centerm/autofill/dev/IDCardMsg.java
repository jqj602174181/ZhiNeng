package com.centerm.autofill.dev;

public class IDCardMsg implements Cloneable{

	public IDCardMsg() {
	}

	public String name; //����
	public String pinyin; //ƴ��
	public String sex;	//�Ա�
	public String nation_str; //����

	public String birth_year ; //����
	public String birth_month ;
	public String birth_day ;
	public String address ; //��ַ
	public String id_num ; //���֤��
	public String sign_office; //ǩ������

	public String useful_s_date_year ; //��Ч����ʼ����
	public String useful_s_date_month ;
	public String useful_s_date_day ;

	public String useful_e_date_year ; //��Ч�ڽ�ֹ����
	public String useful_e_date_month;
	public String useful_e_date_day ;
	
	public byte[] photo; //��Ƭ����
	

	public String getName() {
		return name;
	}



	public String getPinyin() {
		return pinyin;
	}



	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getSex() {
		return sex;
	}



	public void setSex(String sex) {
		this.sex = sex;
	}



	public String getNation_str() {
		return nation_str;
	}



	public void setNation_str(String nation_str) {
		this.nation_str = nation_str;
	}



	public String getBirth_year() {
		return birth_year;
	}



	public void setBirth_year(String birth_year) {
		this.birth_year = birth_year;
	}



	public String getBirth_month() {
		return birth_month;
	}



	public void setBirth_month(String birth_month) {
		this.birth_month = birth_month;
	}



	public String getBirth_day() {
		return birth_day;
	}



	public void setBirth_day(String birth_day) {
		this.birth_day = birth_day;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getId_num() {
		return id_num;
	}



	public void setId_num(String id_num) {
		this.id_num = id_num;
	}



	public String getSign_office() {
		return sign_office;
	}



	public void setSign_office(String sign_office) {
		this.sign_office = sign_office;
	}



	public String getUseful_s_date_year() {
		return useful_s_date_year;
	}



	public void setUseful_s_date_year(String useful_s_date_year) {
		this.useful_s_date_year = useful_s_date_year;
	}



	public String getUseful_s_date_month() {
		return useful_s_date_month;
	}



	public void setUseful_s_date_month(String useful_s_date_month) {
		this.useful_s_date_month = useful_s_date_month;
	}



	public String getUseful_s_date_day() {
		return useful_s_date_day;
	}



	public void setUseful_s_date_day(String useful_s_date_day) {
		this.useful_s_date_day = useful_s_date_day;
	}



	public String getUseful_e_date_year() {
		return useful_e_date_year;
	}



	public void setUseful_e_date_year(String useful_e_date_year) {
		this.useful_e_date_year = useful_e_date_year;
	}



	public String getUseful_e_date_month() {
		return useful_e_date_month;
	}



	public void setUseful_e_date_month(String useful_e_date_month) {
		this.useful_e_date_month = useful_e_date_month;
	}



	public String getUseful_e_date_day() {
		return useful_e_date_day;
	}



	public void setUseful_e_date_day(String useful_e_date_day) {
		this.useful_e_date_day = useful_e_date_day;
	}



	public byte[] getPhoto() {
		return photo;
	}



	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}



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
