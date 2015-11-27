package com.centerm.autofill.appframework.domain;

public class ApplyAccountRecord extends Record{

	private String longterm_flag;
	private String cus_idtype;
	private String trancode;
	private String prt_issue_addr;
	private String prt_profession;
	private String post_code;
	private String record_id_num;
	private String issue_code;
	private String record_account_num;
	private String cus_idnum;
	private String sign_date_year;
	private String cus_addr;
	private String nationality_flag;
	private String amount;
	private String cus_spell;
	private String cus_name;
	private String cus_sex_flag;
	private String sign_date_month;
	private String sign_date_day;
	private String id_duedate;
	private String prt_idtype;
	private String prt_currency;
	private String prt_nationality;
	private String mobile_num;
	private String title;
	private String content;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public ApplyAccountRecord() {
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ApplyAccountRecord[record_id_num:"+record_id_num
				+" prt_profession:"+prt_profession+"]";
	}
	
	public String getLongterm_flag() {
		return longterm_flag;
	}

	public void setLongterm_flag(String longterm_flag) {
		this.longterm_flag = longterm_flag;
	}

	public String getCus_idtype() {
		return cus_idtype;
	}

	public void setCus_idtype(String cus_idtype) {
		this.cus_idtype = cus_idtype;
	}

	public String getTrancode() {
		return trancode;
	}

	public void setTrancode(String trancode) {
		this.trancode = trancode;
	}

	public String getPrt_issue_addr() {
		return prt_issue_addr;
	}

	public void setPrt_issue_addr(String prt_issue_addr) {
		this.prt_issue_addr = prt_issue_addr;
	}

	public String getPrt_profession() {
		return prt_profession;
	}

	public void setPrt_profession(String prt_profession) {
		this.prt_profession = prt_profession;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

	public String getRecord_id_num() {
		return record_id_num;
	}

	public void setRecord_id_num(String record_id_num) {
		this.record_id_num = record_id_num;
	}

	public String getIssue_code() {
		return issue_code;
	}

	public void setIssue_code(String issue_code) {
		this.issue_code = issue_code;
	}

	public String getRecord_account_num() {
		return record_account_num;
	}

	public void setRecord_account_num(String record_account_num) {
		this.record_account_num = record_account_num;
	}

	public String getCus_idnum() {
		return cus_idnum;
	}

	public void setCus_idnum(String cus_idnum) {
		this.cus_idnum = cus_idnum;
	}

	public String getSign_date_year() {
		return sign_date_year;
	}

	public void setSign_date_year(String sign_date_year) {
		this.sign_date_year = sign_date_year;
	}

	public String getCus_addr() {
		return cus_addr;
	}

	public void setCus_addr(String cus_addr) {
		this.cus_addr = cus_addr;
	}

	public String getNationality_flag() {
		return nationality_flag;
	}

	public void setNationality_flag(String nationality_flag) {
		this.nationality_flag = nationality_flag;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCus_spell() {
		return cus_spell;
	}

	public void setCus_spell(String cus_spell) {
		this.cus_spell = cus_spell;
	}

	public String getCus_name() {
		return cus_name;
	}

	public void setCus_name(String cus_name) {
		this.cus_name = cus_name;
	}

	public String getCus_sex_flag() {
		return cus_sex_flag;
	}

	public void setCus_sex_flag(String cus_sex_flag) {
		this.cus_sex_flag = cus_sex_flag;
	}

	public String getSign_date_month() {
		return sign_date_month;
	}

	public void setSign_date_month(String sign_date_month) {
		this.sign_date_month = sign_date_month;
	}

	public String getSign_date_day() {
		return sign_date_day;
	}

	public void setSign_date_day(String sign_date_day) {
		this.sign_date_day = sign_date_day;
	}

	public String getId_duedate() {
		return id_duedate;
	}

	public void setId_duedate(String id_duedate) {
		this.id_duedate = id_duedate;
	}

	public String getPrt_idtype() {
		return prt_idtype;
	}

	public void setPrt_idtype(String prt_idtype) {
		this.prt_idtype = prt_idtype;
	}

	public String getPrt_currency() {
		return prt_currency;
	}

	public void setPrt_currency(String prt_currency) {
		this.prt_currency = prt_currency;
	}

	public String getPrt_nationality() {
		return prt_nationality;
	}

	public void setPrt_nationality(String prt_nationality) {
		this.prt_nationality = prt_nationality;
	}

	public String getMobile_num() {
		return mobile_num;
	}

	public void setMobile_num(String mobile_num) {
		this.mobile_num = mobile_num;
	}	
}
