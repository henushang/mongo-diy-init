package com.genius.xo.mongodb.daoimpl;

import org.springframework.stereotype.Component;

import com.genius.xo.mongodb.dao.UserDao;
import com.genius.xo.mongodb.model.User;

/**
 *
 * @author ShangJianguo
 */
@Component
public class UserDaoImpl extends BaseMongoDaoImpl<User> implements UserDao {

	/* (non-Javadoc)
	 * @see com.genius.xo.mongodb.daoimpl.BaseMongoDaoImpl#getTClass()
	 * @author: ShangJianguo
	 * 2014-12-23 下午5:18:38
	 */
	@Override
	protected Class<User> getTClass() {
		// TODO Auto-generated method stub
		return User.class;
	}


}
