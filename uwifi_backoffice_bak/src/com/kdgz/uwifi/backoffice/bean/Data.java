package com.kdgz.uwifi.backoffice.bean;

/**
 * ajax请求返还结构
 * 
 * @author lanbo
 * 
 */
public class Data {
	
	public Data() {
		// default constructor
	}
	
	public Data(String status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	private String status;

	private String msg;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
