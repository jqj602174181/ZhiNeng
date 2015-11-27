package com.centerm.util.financial;


public class PersonInfo implements Cloneable{

	public PersonInfo() {
	}
	
	public byte[] name = new byte[128];		//����
	public byte[] sex = new byte[8];		//�Ա�
	public byte[] nation = new byte[32];	//����
	public byte[] birthday = new byte[12];	//����
	public byte[] address = new byte[256];	//��ַ
	public byte[] cardId = new byte[20];	//���֤��
	public byte[] police = new byte[128];	//ǩ������
	public byte[] validStart = new byte[12];//��Ч����ʼ����
	public byte[] validEnd = new byte[12];	//��Ч�ڽ�ֹ����
	public byte[] sexCode = new byte[4];	//�Ա����
	public byte[] nationCode = new byte[4];	//�������
	public int photosize;					//��Ƭ���ݴ�С
	public byte[] photo = new byte[64*1024];	//��Ƭ����
	
	
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
