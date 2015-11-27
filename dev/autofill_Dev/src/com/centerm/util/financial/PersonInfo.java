package com.centerm.util.financial;


public class PersonInfo implements Cloneable{

	public PersonInfo() {
	}
	
	public byte[] name = new byte[128];		//名字
	public byte[] sex = new byte[8];		//性别
	public byte[] nation = new byte[32];	//国籍
	public byte[] birthday = new byte[12];	//生日
	public byte[] address = new byte[256];	//地址
	public byte[] cardId = new byte[20];	//身份证号
	public byte[] police = new byte[128];	//签发机关
	public byte[] validStart = new byte[12];//有效期起始日期
	public byte[] validEnd = new byte[12];	//有效期截止日期
	public byte[] sexCode = new byte[4];	//性别代码
	public byte[] nationCode = new byte[4];	//民族代码
	public int photosize;					//照片数据大小
	public byte[] photo = new byte[64*1024];	//照片数据
	
	
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
