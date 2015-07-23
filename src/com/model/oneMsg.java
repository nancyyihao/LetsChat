package com.model;

/**
 * 类功能描述：表示一条消息</br>
 *
 * @author 王明献
 * @version 1.0
 * </p>
 * 修改时间：</br>
 * 修改备注：</br>
 */
public class oneMsg {
	
	private String uid ;       //消息是由哪个用户发的
	private String date ;      //消息产生时间
	private String msg ;       //消息主体
	private String dir ;       //IN 表示消息进来，OUT表示消息发出去
	private String toWhom ;    //消息要发给谁
	
	public oneMsg() {
	}
	
	public oneMsg(String uid,String toWhom,
			String date,String msg, String dir ) {
		
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
