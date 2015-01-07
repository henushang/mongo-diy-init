package com.genius.mongodb.mock.testconstants;

import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * 单元测试用到的常量
 * @author liushaomin 
 */
public class TestConst {

	public static final String name = "zhangsan";

	public static final String name1 = "zhangsan1";
	
	public static final String _123456 = "123456";

	public static final String pwd_123456 = DigestUtils.md5Hex(_123456);

	public static final String userId = "greathill";

	public static final String uid = "uid00000000000000000000000000000";

	public static final String uid1 = "uid11111111111111111111111111111";

	public static final String uid2 = "uid22222222222222222222222222222";

	public static final int oid = 1;
	
	public static final int age = 20;
	public static final int age1 = 201;
	
	public static final String DadName = "dadname";
	
	public static final String MomName = "momname";

	/**
	 * 可测试常量之间关联是否正确
	 */
	@Test
	public void testUserConstant() {
		assertTrue(true);
	}
}
