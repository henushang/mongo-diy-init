package com.genius.xo.mongodb.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * 获取MongoDB的DB实例，该类采用singleton模式
 * 
 * @author ShangJianguo
 */
public class MongoDBFactory {

	private static MongoDBFactory instance;

	private static DB db;

	private MongoDBConn mongodbConn;
	private static String defaultUserSource = "$external";

	private static String SLASH = "/";
	private static String DOUBLESLASH = "//";
	private static String COLON = ":";
	private static String COMMA = ",";

	private MongoDBFactory() {
	}

	/**
	 * 获取 MongoDBFactory 的实例
	 * @return
	 * @author ShangJianguo
	 */
	public static synchronized MongoDBFactory getInstance() {
		if (instance == null) {
			instance = new MongoDBFactory();
		}
		return instance;
	}

	/**
	 * 获取DB实例
	 * @return DB
	 * @author ShangJianguo
	 */
	public synchronized DB getDB() {
		if (db == null) {
			List<ServerAddress> serverAddressList = getServerAddresses();
			MongoClient mongoClient = null;
			if (mongodbConn.getCredential() == 0) {// 需要使用用户名的安全验证
				if (serverAddressList.size() == 1) {
					mongoClient = new MongoClient(serverAddressList.get(0));
				} else {
					mongoClient = new MongoClient(serverAddressList);
				}
			} else {
				List<MongoCredential> credentialList = getCredentials();
				if (serverAddressList.size() == 0) {
					mongoClient = new MongoClient(serverAddressList.get(0), credentialList);
				} else {
					mongoClient = new MongoClient(serverAddressList, credentialList);
				}
			}
			String hostname = mongodbConn.getHostname();
			String dbname = hostname.substring(hostname.lastIndexOf(SLASH) + 1);
			db = mongoClient.getDB(dbname);
		}
		return db;
	}

	/**
	 * 获取 MongoCredential 列表
	 * 
	 * @return List<MongoCredential>
	 * @author ShangJianguo
	 */
	public List<MongoCredential> getCredentials() {
		List<MongoCredential> list = new ArrayList<>();
		String username = mongodbConn.getUsername();
		String password = mongodbConn.getPassword();

		String[] names = username.split(COMMA);
		String[] pwds = password.split(COMMA);
		MongoCredential credential = null;
		String source = StringUtils.isEmpty(mongodbConn.getUsersource()) ? defaultUserSource : mongodbConn.getUsersource();
		for (int i = 0; i < names.length; i++) {
			credential = MongoCredential.createMongoCRCredential(names[i], source, pwds[i].toCharArray());
			list.add(credential);
		}
		return list;
	}

	/**
	 * 获取 ServerAddress 列表
	 * 
	 * @return List<ServerAddress>
	 * @author ShangJianguo
	 */
	public List<ServerAddress> getServerAddresses() {
		String hostname = mongodbConn.getHostname();
		String realHost = hostname.substring(hostname.indexOf(DOUBLESLASH) + 2, hostname.lastIndexOf(SLASH));
		String[] hostnames = realHost.split(COMMA);
		List<ServerAddress> list = new ArrayList<>();
		ServerAddress sa = null;
		for (String item : hostnames) {
			String host = item.split(COLON)[0];
			String sport = item.split(COLON)[1];
			int port = Integer.parseInt(sport);
			try {
				sa = new ServerAddress(host, port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				break;
			}
			list.add(sa);
		}
		return list;
	}

	public MongoDBConn getMongodbConn() {
		return mongodbConn;
	}

	public void setMongodbConn(MongoDBConn mongodbConn) {
		this.mongodbConn = mongodbConn;
	}
}
