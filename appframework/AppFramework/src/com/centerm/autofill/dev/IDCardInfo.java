package com.centerm.autofill.dev;
/*
 * s���֤��Ϣ�ṹ�������ڽ���ֱ�Ӵ�ֵ
 * */
public class IDCardInfo {
	
	private String name;			//����
	private String pinyin;			//������ƴ��
	private String sex;				//�Ա�
	private String nation;			//����
	private String birthday;		//����
	private String address;			//��ַ
	private String id;				//���֤��
	private String police;			//ǩ������
	private String validStart;		//��Ч����ʼ����
	private String validEnd;		//��Ч�ڽ�ֹ����
	private String sexCode;			//�Ա����
	private String nationCode;		//�������
	private int photosize;			//��Ƭ���ݴ�С
	private byte[] photo;			//��Ƭ����
	
	public String getName() {
		return name;
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
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPolice() {
		return police;
	}
	public void setPolice(String police) {
		this.police = police;
	}
	public String getValidStart() {
		return validStart;
	}
	public void setValidStart(String validStart) {
		this.validStart = validStart;
	}
	public String getValidEnd() {
		return validEnd;
	}
	public void setValidEnd(String validEnd) {
		this.validEnd = validEnd;
	}
	public String getSexCode() {
		return sexCode;
	}
	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}
	public String getNationCode() {
		return nationCode;
	}
	public void setNationCode(String nationCode) {
		this.nationCode = nationCode;
	}
	public int getPhotoSize() {
		return photosize;
	}
	public void setPhotoSize(int photosize) {
		this.photosize = photosize;
	}
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getBirthday(String YYMMDD) {
		if(this.birthday == null)
			return null;
		if(YYMMDD.equalsIgnoreCase("year") == true)
		{
			return this.birthday.substring(0, 4);
		}
		else if(YYMMDD.equalsIgnoreCase("month") == true)
		{
			return this.birthday.substring(5, 7);
		}
		else if(YYMMDD.equalsIgnoreCase("day") == true)
		{
			return this.birthday.substring(8, 10);
		}
		else
			return this.birthday;
	}
	
	//��Ч���Ƿ��ǳ���
	public boolean isLongTerm(){
		return validEnd.contains("����") || validStart.contains("����");
	}
	
	//����֤�������յ�������
	public String getValidEndYear(){
		if( !isLongTerm ())
			return validEnd.substring(0, 4);//��xxxx��xx��xx��ȡ����
		else
			return "";
	}
	public String getValidEndMonth(){
		if( !isLongTerm ())
			return validEnd.substring(5, 7);
		else
			return "";
	}
	public String getValidEndDay(){
		if( !isLongTerm ())
			return validEnd.substring(8, 10);
		else
			return "";
	}
	//����֤�������յ�YYYYMMDD��ʽ
	public String getValidEndYYYYMMDD(){
		return getValidEndYear() + getValidEndMonth() + getValidEndDay();
	}
}
