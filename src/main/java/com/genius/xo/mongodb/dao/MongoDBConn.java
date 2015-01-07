package com.genius.xo.mongodb.dao;

import org.springframework.stereotype.Component;

/**
 * 数据库的连接信息
 * 
 * @author ShangJianguo
 */
@Component
public class MongoDBConn {
	/*
	 * 连接配置文件中的直接属性
	 */
	private String hostname;

	private String username;
	
	private String password;
	
	private int credential;
	
	private String usersource;
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getCredential() {
		return credential;
	}

	public void setCredential(int credential) {
		this.credential = credential;
	}

	public String getUsersource() {
		return usersource;
	}

	public void setUsersource(String usersource) {
		this.usersource = usersource;
	}
}
