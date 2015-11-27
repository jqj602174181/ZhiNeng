package com.centerm.autofill.appframework.domain;

public class Record {

	private int id;
	private String date;
	private String name;
	private String account;
	private String money;
	
	public Record() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Record[id:"+id+" name:"+name+"]";
	}
}
