package com.genius.xo.mongodb.daoimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;
import org.mongojack.MongoCollection;
import org.mongojack.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.genius.core.base.constant.BaseMapperDict;
import com.genius.core.base.utils.ReflectUtil;
import com.genius.xo.base.dao.BaseDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * MongoDB DAO 的基础实现类
 * @author ShangJianguo
 */
public abstract class BaseMongoDaoImpl<T> implements BaseDao<T>{

	@Autowired
	protected DB db;
	
	private JacksonDBCollection<T, String> coll = null;
	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.registerModule(new JodaModule());
	}
	
	private static String METHOD_GET = "getUid";
	
	
	/**
	 * 每个子类必须实现此方法，提供目前操作的model对象的Class实例。
	 * @return Class<T>
	 * @author ShangJianguo
	 */
	protected abstract Class<T> getTClass();
	
	/**
	 * 获取 JacksonDBCollection 实例
	 * @return
	 * @author ShangJianguo
	 */
	protected JacksonDBCollection<T, String> getColl(){
		if (coll == null) {
			Class<T> clazz = getTClass();
			MongoCollection mongoCollection = clazz.getAnnotation(MongoCollection.class);
			String collName = null;
			if (mongoCollection == null) {
				collName = clazz.getSimpleName().toLowerCase();
			}else {
				collName = mongoCollection.name();
			}
			DBCollection dbcoll = db.getCollection(collName);
			coll = JacksonDBCollection.wrap(dbcoll, getTClass(), String.class, mapper);
		}
		return coll;
	}
	
	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#get(java.lang.String)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public T get(String id) {
		return getColl().findOneById(id);
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#getOne(java.util.Map)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public T getOne(Map<String, Object> map) {
		Query query = getQuery(map);
		return getColl().findOne(query);
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#getCount(java.util.Map)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public int getCount(Map<String, Object> map) {
		Query query = getQuery(map);
		return (int)getColl().getCount(query);
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#getList(java.util.Map)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public List<T> getList(Map<String, Object> map) {
		int skip = 0;
		if (map.get(BaseMapperDict.startIndex) != null) {
			skip = Integer.parseInt(map.get(BaseMapperDict.startIndex).toString());
		}
		int limit = 10;
		if (map.get(BaseMapperDict.pageSize) != null) {
			limit = Integer.parseInt(map.get(BaseMapperDict.pageSize).toString());
		}
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.putAll(map);
		DBObject orderby = getOrderDBObject(map);
		DBCursor<T> cursor = getColl().find(getQuery(map2)).skip(skip).limit(limit).sort(orderby);
		List<T> list = new ArrayList<>();
		if (cursor != null) {
			list = cursor.toArray();
		}
		return list;
	}
	
	/*
	 * 获取排序规则
	 */
	private DBObject getOrderDBObject(Map<String, Object> map){
		BasicDBObject orderBy = new BasicDBObject();
		String orderbykey = BaseMapperDict.createtime;
		if (map.containsKey(BaseMapperDict.orderBy)) {
			orderbykey = map.get(BaseMapperDict.orderBy).toString();
		}
		if (map.containsKey(BaseMapperDict.ascDesc)) {
			String ascdesc = map.get(BaseMapperDict.ascDesc).toString();
			if (ascdesc.equals(BaseMapperDict.asc)) {
				orderBy.append(orderbykey, -1);
			}else {
				orderBy.append(orderbykey, 1);
			}
		}else {
			orderBy.append(orderbykey, 1);
		}
		return orderBy;
	}
	

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#insert(java.lang.Object)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public boolean insert(T t) {
		try {
			getColl().insert(t);
			return true;
		} catch (MongoException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#insertList(java.util.List)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public boolean insertList(List<T> list) {
		try {
			getColl().insert(list);
			return true;
		} catch (MongoException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#update(java.lang.Object)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public boolean update(T t) {
		try {
			String id = (String)ReflectUtil.invoke(t, t.getClass().getMethod(METHOD_GET, new Class[]{}), new Object[]{});
			getColl().updateById(id, t);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#updateFields(java.util.Map)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public boolean updateFields(Map<String, Object> map) {
		String id = null;
		Query query = null;
		if (map.get(BaseMapperDict.uid) != null) {
			id = map.get(BaseMapperDict.uid).toString();
			map.remove(BaseMapperDict.uid);
		}else {
			query = DBQuery.empty();
			if (map.get(BaseMapperDict.uids) != null) {
				query.in(BaseMapperDict.uid, map.get(BaseMapperDict.uids));
				map.remove(BaseMapperDict.uids);
			}else {
				query.in(BaseMapperDict.uid, map.get(BaseMapperDict.ids));
				map.remove(BaseMapperDict.ids);
			}
		}
		Builder update = new Builder();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			update.set(key, map.get(key));
		}
		try {
			if (id != null) {
				getColl().updateById(id, update);
			}else {
				getColl().update(query, update);
			}
			return true;
		} catch (MongoException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#increase(java.util.Map)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public boolean increase(Map<String, Object> map) {
		String id = (String) map.get(BaseMapperDict.uid);
		map.remove(BaseMapperDict.uid);
		Builder update = new Builder();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			update.inc(key, Integer.valueOf(map.get(key).toString()));
		}
		getColl().updateById(id, update);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#delete(java.lang.String)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public boolean delete(String id) {
		WriteResult<T, String> result = getColl().removeById(id);
		if (result.getN() == 1) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.dao.BaseDao#deleteByMap(java.util.Map)
	 * @author: ShangJianguo
	 * 2014-12-22 下午4:56:58
	 */
	@Override
	public boolean deleteByMap(Map<String, Object> map) {
		getColl().remove(getQuery(map));
		return true;
	}
	
	/**
	 * 根据传来的map组装成可以在mongodb中查询的条件，具体的前后缀请参考 {@link com.genius.xo.mongodb.dao.constants.MongoMapperDic}
	 * @param map
	 * @return
	 * @author ShangJianguo
	 */
	public Query getQuery(Map<String, Object> map){
		map = mapFilter(map);
		Query query = DBQuery.empty();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (key.endsWith(BaseMapperDict.greaterOrEqual_key_suffix)) {
				query.greaterThanEquals(getRealField(key, false, BaseMapperDict.greaterOrEqual_key_suffix), map.get(key));
				continue;
			}
			if (key.endsWith(BaseMapperDict.greater_key_suffix)) {
				query.greaterThan(getRealField(key, false, BaseMapperDict.greater_key_suffix), map.get(key));
				continue;
			}
			if (key.endsWith(BaseMapperDict.lessOrEqual_key_suffix)) {
				query.lessThanEquals(getRealField(key, false, BaseMapperDict.lessOrEqual_key_suffix), map.get(key));
				continue;
			}
			if (key.endsWith(BaseMapperDict.less_key_suffix)) {
				query.lessThan(getRealField(key, false, BaseMapperDict.less_key_suffix), map.get(key));
				continue;
			}
			if (key.endsWith(BaseMapperDict.like_key_suffix)) {
				String value = (String) map.get(key);
				query.regex(getRealField(key, false, BaseMapperDict.like_key_suffix), Pattern.compile(value));
				continue;
			}
			if (key.equals(BaseMapperDict.ids)) {
				query.in(BaseMapperDict.uid, map.get(BaseMapperDict.ids));
				continue;
			}
			query.is(key, map.get(key));
		}
		return query;
	}
	
	// 存储一些特殊的非数据库字段的值
	private static List<String> filterKeys = new ArrayList<>();
	static {
		filterKeys.add(BaseMapperDict.ascDesc);
		filterKeys.add(BaseMapperDict.orderBy);
		filterKeys.add(BaseMapperDict.asc);
		filterKeys.add(BaseMapperDict.pageSize);
		filterKeys.add(BaseMapperDict.startIndex);
	}
	
	/*
	 * 过滤一些特殊的字段
	 */
	private static Map<String, Object> mapFilter(Map<String, Object> map){
		Map<String, Object> temp = new HashMap<>();
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			if (!filterKeys.contains(key)) {
				temp.put(key, map.get(key));
			}
		}
		return temp;
	}
	
	/*
	 * 对传过来的串去掉前缀或者后缀，然后返回真实的field值
	 * @param origin 传过来的源字符串
	 * @param prefixOrSuffix 前缀或者后缀，如果为true则表示前缀，如果为false则表示后缀
	 * @param fix 前缀（或后缀）
	 * @return 去掉前缀或者后缀后的真实field值
	 * @author ShangJianguo
	 */
	private static String getRealField(String origin, boolean prefixOrSuffix, String fix){
		if (prefixOrSuffix) {// 前缀
			return origin.substring(origin.indexOf(fix) + fix.length());
		}else {
			return origin.substring(0, origin.lastIndexOf(fix));
		}
	}

}
