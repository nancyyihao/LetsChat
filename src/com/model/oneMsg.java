package com.model;

public class oneMsg{
	private String uid ;
	private String date ;
	private String msg ;
	private String dir ;     //IN 表示消息进来，OUT表示消息发出去
	private String toWhom ;  
	public oneMsg(){
	}
	public oneMsg(String uid,String toWhom,String date,String msg, String dir ){
		this.uid = uid ;
		this.date = date ;
		this.msg = msg ;
		this.dir = dir ;
		this.toWhom = toWhom ;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getToWhom() {
		return toWhom;
	}
	public void setToWhom(String toWhom) {
		this.toWhom = toWhom;
	}
}
