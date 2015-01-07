package com.genius.xo.mongodb.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.genius.core.base.constant.BaseMapperDict;
import com.genius.core.base.utils.GlobalUtil;
import com.genius.mongodb.mock.testconstants.TestConst;
import com.genius.mongodb.mock.testconstants.TestViewKeyConst;
import com.genius.xo.mongodb.model.Cat;
import com.genius.xo.mongodb.model.Parent;
import com.genius.xo.mongodb.model.User;

/**
 * 测试类
 * 
 * @author ShangJianguo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:**/applicationContext**.xml" })
public class UserDaoTest {

	@Autowired
	UserDao dao;

	User entity = null;

	/**
	 * 在执行所有单元测试方法之前插入user
	 */
	@Before
	public void before() {
		User user = new User();
		user.setName(TestConst.name);
		user.setAge(TestConst.age);
		user.setBirthday(DateTime.now().minusDays(2));
		user.setId(TestConst.uid);
		assertTrue(dao.insert(user));

		entity = new User();
		entity.setName(TestConst.name1);
		entity.setAge(TestConst.age1);
		entity.setBirthday(DateTime.now().minusDays(2));
	}

	/**
	 * 在执行完所有单元测试之后删除user
	 */
	@After
	public void after() {
		dao.delete(TestConst.uid);
		assertNull(dao.get(TestConst.uid));
		entity = null;
	}

	@Test
	public void testGet() {
		assertNotNull(dao.get(TestConst.uid));
	}

	/**
	 * 插入成功
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testInsert() {
		boolean result = dao.insert(entity);
		assertTrue(result);
		assertTrue(dao.delete(entity.getId()));
	}

	/**
	 * 插入失败
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testInsertFail() {
		dao.insert(entity);
		boolean result = dao.insert(entity);
		assertFalse(result);
		assertTrue(dao.delete(entity.getId()));
	}

	/**
	 * 测试插入原生数据类型数组
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testInsertWithArray() {
		entity.setLukynumbers(new int[] { 1, 3, 1, 4 });
		dao.insert(entity);
		User user = dao.get(entity.getId());
		assertEquals(4, user.getLukynumbers().length);
		assertTrue(dao.delete(entity.getId()));
	}

	/**
	 * 测试插入引用的对象
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testInsertRefObj() {
		Parent parent = new Parent();
		parent.setDad(TestConst.DadName);
		parent.setMom(TestConst.MomName);
		entity.setParent(parent);
		boolean result = dao.insert(entity);
		assertTrue(result);
		User user = dao.get(entity.getId());
		assertEquals(TestConst.DadName, user.getParent().getDad());
		assertTrue(dao.delete(entity.getId()));
	}

	/**
	 * 测试插入有对象列表（List）的数据
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testInsertWithObjList() {
		List<Cat> list = new ArrayList<>();
		Cat cat = null;
		for (int i = 0; i < 3; i++) {
			cat = new Cat();
			cat.setName(TestViewKeyConst.cat + i);
			cat.setAge(i + 1);
			list.add(cat);
		}
		entity.setCats(list);
		assertTrue(dao.insert(entity));
		assertEquals(list.size(), dao.get(entity.getId()).getCats().size());
		assertTrue(dao.delete(entity.getId()));
	}

	@Test
	public void testGetOne() {
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.name, TestConst.name);
		assertNotNull(dao.getOne(map));
	}
	
	@Test
	public void testGetCount() {
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.name, TestConst.name);
		assertEquals(1, dao.getCount(map));
	}

	@Test
	public void testGetList() {
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.name, TestConst.name);
		List<User> list = dao.getList(map);
		assertEquals(1, list.size());
	}
	
	/**
	 * 测试大于等于条件成功
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testGetListGte(){
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.age + BaseMapperDict.greaterOrEqual_key_suffix, 20);
		List<User> list = dao.getList(map);
		assertEquals(1, list.size());
		map.put(TestViewKeyConst.age + BaseMapperDict.greaterOrEqual_key_suffix, 21);
		list = dao.getList(map);
		assertEquals(0, list.size());
	}
	
	/**
	 * 测试大于等于条件
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testGetListGt(){
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.age + TestViewKeyConst.greater_key_suffix, TestConst.age);
		List<User> list = dao.getList(map);
		assertEquals(0, list.size());
		map.put(TestViewKeyConst.age + TestViewKeyConst.greater_key_suffix, TestConst.age - 1);
		list = dao.getList(map);
		assertEquals(1, list.size());
	}

	/**
	 * 测试小于等于条件
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testGetListLte(){
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.age + BaseMapperDict.lessOrEqual_key_suffix, TestConst.age);
		List<User> list = dao.getList(map);
		assertEquals(1, list.size());
		map.put(TestViewKeyConst.age + BaseMapperDict.lessOrEqual_key_suffix, TestConst.age - 1);
		list = dao.getList(map);
		assertEquals(0, list.size());
	}

	/**
	 * 测试小于条件
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testGetListLt(){
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.birthday + TestViewKeyConst.less_key_suffix, DateTime.now());
		map.put(TestViewKeyConst.name, TestConst.name);
		map.put(TestViewKeyConst.birthday + TestViewKeyConst.less_key_suffix, DateTime.now());
		List<User> list = dao.getList(map);
		assertEquals(1, list.size());
	}
	
	/**
	 * 测试模糊匹配（like）
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testGetListLike(){
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.name + TestViewKeyConst.like_key_suffix, "zhan");
		List<User> list = dao.getList(map);
		assertEquals(1, list.size());
		
		map.put(TestViewKeyConst.name + TestViewKeyConst.like_key_suffix, "hangs");
		list = dao.getList(map);
		assertEquals(1, list.size());
		
		map.put(TestViewKeyConst.name + TestViewKeyConst.like_key_suffix, "hangsann");
		list = dao.getList(map);
		assertEquals(0, list.size());
	}
	
	/**
	 * 同时传入多个后缀的条件
	 * 
	 * @author ShangJianguo
	 */
	@Test
	public void testGetListMutilFix(){
		Map<String, Object> map = new HashMap<>();
		map.put(TestViewKeyConst.age + BaseMapperDict.greater_key_suffix, TestConst.age - 1);
		map.put(TestViewKeyConst.birthday + BaseMapperDict.lessOrEqual_key_suffix, DateTime.now());
		map.put(TestViewKeyConst.name + BaseMapperDict.like_key_suffix, "zhan");
		List<User> list = dao.getList(map);
		assertEquals(1, list.size());
	}
	
	@Test
	public void testUpdate() {
		User user = new User();
		user.setAge(1);
		dao.update(user);
//		assertEquals(1, dao.get(TestConst.uid).getAge());
	}

	@Test
	public void testUpdateFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(TestViewKeyConst.name, TestConst.name);
		dao.updateFields(map);
//		assertEquals();
	}

	@Test
	public void testDeleteByMap() {
		String name = "testname";
		for (int i = 0; i < 3; i++) {
			User entity = new User();
			entity.setName(name);
			entity.setAge(TestConst.age);
			entity.setBirthday(DateTime.now());
			entity.setId(GlobalUtil.getUUID());
			dao.insert(entity);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(TestViewKeyConst.name, name);
		assertTrue(dao.getList(map).size() == 3);
		dao.deleteByMap(map);
		assertTrue(dao.getList(map).size() == 0);
	}
	
	@Test
	public void Increase(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(BaseMapperDict.uid, TestConst.uid);
		map.put(TestViewKeyConst.age, 1);
		dao.increase(map);
	}
}
