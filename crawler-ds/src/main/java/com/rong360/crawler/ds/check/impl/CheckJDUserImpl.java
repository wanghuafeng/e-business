package com.rong360.crawler.ds.check.impl;

import org.apache.commons.lang.StringUtils;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.ds.check.ICheckJDUser;
import com.rong360.crawler.ds.query.impl.JingdongVerifyUserQuery;
import com.rong360.crawler.util.URIUtils;

/**
 * 
 * @ClassName: CheckJDUserImpl
 * @Description:检查用户输入参数实现类
 * @author xiongwei
 * @date 2015-3-23 上午11:34:06
 * 
 */
public class CheckJDUserImpl implements ICheckJDUser {

	@Override
	public CheckResult check(JingdongVerifyUserQuery jdVerifyUserQuery) {
		
		/******检查登录名为空*****/
		if (isEmpty(jdVerifyUserQuery.getLoginName())) {
			return new CheckResult(ErrorCode._20001.getMsg(), ErrorCode._20001.getCode());
		}
		
		
		/******检查userId是否为空*****/
		if (isEmpty(jdVerifyUserQuery.getUserId())) {
			return new CheckResult(ErrorCode._10008.getMsg(), ErrorCode._10008.getCode());
		}
		
		/******检查userId长度是否超过45个字符*****/
		if (jdVerifyUserQuery.getUserId().length() > 45) {
			return new CheckResult(ErrorCode._10011.getMsg(), ErrorCode._10011.getCode());
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
	
	public static void main(String[] args) {
		System.out.println(URIUtils.md5URI("timeunit=1428914231appKey=rong360150306"));
	}

}
