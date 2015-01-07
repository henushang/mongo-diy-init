package com.genius.xo.mongodb.dao;


import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.genius.mongodb.mock.testconstants.TestConst;
import com.genius.xo.mongodb.model.User;
import com.mongodb.DB;
import com.mongodb.DBCollection;

/**
 * 测试类
 * 
 * @author ShangJianguo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:**/applicationContext**.xml" })
public class DBTest {

	@Autowired
	DB db;
	JacksonDBCollection<User, String> coll = null;
	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.registerModule(new JodaModule());
	}
	
	@Before
	public void before(){
		DBCollection dbcoll = db.getCollection("user");
		coll = JacksonDBCollection.wrap(dbcoll, User.class, String.class, mapper);
	}
	
	@Ignore
	@Test
	public void testMongoDB() {
		System.out.println(db.getCollectionNames());
		System.out.println(db.getStats());
	}
	
	@Ignore
	@Test
	public void testInsert(){
		User user = new User();
		user.setName(TestConst.name);
		user.setAge(TestConst.age);
		user.setId(TestConst.uid1);
		user.setBirthday(DateTime.now());
		WriteResult<User, String> insertresult = coll.insert(user);
		Assert.assertEquals(user.getAge(), insertresult.getSavedObject().getAge());
		coll.removeById(TestConst.uid1);
		assertNull(coll.findOneById(TestConst.uid1));
	}
}
