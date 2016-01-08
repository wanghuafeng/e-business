package com.rong360.crawler.ds.check.impl;

import org.apache.commons.lang.StringUtils;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.ds.check.ICheckJDLogin;
import com.rong360.crawler.ds.query.impl.JingdongLoginQuery;
import com.rong360.crawler.util.URIUtils;

/**
 * 
 * @ClassName: CheckJDLoginImpl
 * @Description:检查用户登录请求参数实现类
 * @author xiongwei
 * @date 2015-3-23 上午11:34:06
 * 
 */
public class CheckJDLoginImpl implements ICheckJDLogin {

	@Override
	public CheckResult check(JingdongLoginQuery jdLoginQuery) {
		
		/******检查登录名为空*****/
		if (isEmpty(jdLoginQuery.getLoginName())) {
			return new CheckResult(ErrorCode._20001.getMsg(), ErrorCode._20001.getCode());
		}
		
		/******检查密码是否为空*****/
		if (isEmpty(jdLoginQuery.getPassword())) {
			return new CheckResult(ErrorCode._20002.getMsg(), ErrorCode._20002.getCode());
		}
		
		/******检查userId是否为空*****/
		if (isEmpty(jdLoginQuery.getUserId())) {
			return new CheckResult(ErrorCode._20005.getMsg(), ErrorCode._20005.getCode());
		}
		return null;
	}
	
	
	/****
	 * 检查参数是否为空
	 * @param key
	 * @return
	 */
	public boolean isEmpty(String key) {
		if ("-1".equals(key) || StringUtils.isEmpty(key)) {
			return true;
		}
		return false;
	}

}
