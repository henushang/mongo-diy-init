package com.genius.xo.mongodb.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mongojack.MongoCollection;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.genius.core.base.utils.GlobalUtil;

/**
 * MongoDB 测试类
 *
 * @author ShangJianguo
 */
@MongoCollection(name="user")
public class User {

	private String id = GlobalUtil.getUUID();
	
	private String name;
	
	private int age;
	
	private DateTime birthday;
	
	private int[] lukynumbers = {};
	
	private Parent parent;
	
	private List<Cat> cats = new ArrayList<>();
 
	@ObjectId
	@JsonProperty("_id")
	public String getId() {
		return id;
	}

	@ObjectId
	@JsonProperty("_id")
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public DateTime getBirthday() {
		return birthday;
	}

	public void setBirthday(DateTime birthday) {
		this.birthday = birthday;
	}

	public int[] getLukynumbers() {
		return lukynumbers;
	}

	public void setLukynumbers(int[] lukynumbers) {
		this.lukynumbers = lukynumbers;
	}

	public Parent getParent() {
		return parent;
	}

	public void setParent(Parent parent) {
		this.parent = parent;
	}

	public List<Cat> getCats() {
		return cats;
	}

	public void setCats(List<Cat> cats) {
		this.cats = cats;
	}
}