package com.centerm.util.financial;

public class IdCardMsg implements Cloneable{

	public IdCardMsg() {
	}

	public String name; //����
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
