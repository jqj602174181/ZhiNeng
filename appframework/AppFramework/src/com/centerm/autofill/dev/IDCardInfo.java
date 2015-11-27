package com.centerm.autofill.dev;
/*
 * s身份证信息结构，用于在界面直接传值
 * */
public class IDCardInfo {
	
	private String name;			//名字
	private String pinyin;			//姓名的拼音
	private String sex;				//性别
	private String nation;			//国籍
	private String birthday;		//生日
	private String address;			//地址
	private String id;				//身份证号
	private String police;			//签发机关
	private String validStart;		//有效期起始日期
	private String validEnd;		//有效期截止日期
	private String sexCode;			//性别代码
	private String nationCode;		//民族代码
	private int photosize;			//照片数据大小
	private byte[] photo;			//照片数据
	
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
	
	//有效期是否是长期
	public boolean isLongTerm(){
		return validEnd.contains("长期") || validStart.contains("长期");
	}
	
	//返回证件到期日的年月日
	public String getValidEndYear(){
		if( !isLongTerm ())
			return validEnd.substring(0, 4);//从xxxx年xx月xx日取出年
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
	//返回证件到期日的YYYYMMDD格式
	public String getValidEndYYYYMMDD(){
		return getValidEndYear() + getValidEndMonth() + getValidEndDay();
	}
}
