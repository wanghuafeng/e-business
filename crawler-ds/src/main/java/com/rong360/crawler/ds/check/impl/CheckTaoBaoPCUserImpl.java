package com.rong360.crawler.ds.check.impl;

import org.apache.commons.lang.StringUtils;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.ds.check.ICheckTaoBaoPCUser;
import com.rong360.crawler.ds.query.impl.TaoBaoPCVerifyUserQuery;
import com.rong360.crawler.util.URIUtils;

/**
 * 
 * @ClassName: CheckTaoBaoPCUserImpl
 * @Description:检查用户输入参数实现类
 * @author xiongwei
 * @date 2015-3-23 上午11:34:06
 * 
 */
public class CheckTaoBaoPCUserImpl implements ICheckTaoBaoPCUser {

	@Override
	public CheckResult check(TaoBaoPCVerifyUserQuery taoBaoPCVerifyUserQuery) {
		/******检查登录名为空*****/
		if (StringUtils.isEmpty(taoBaoPCVerifyUserQuery.getLoginName())) {
			return new CheckResult(ErrorCode._20001.getMsg(), ErrorCode._20001.getCode());
		}
		
		
		/******检查userId是否为空*****/
		if (StringUtils.isEmpty(taoBaoPCVerifyUserQuery.getUserId())) {
			return new CheckResult(ErrorCode._10008.getMsg(), ErrorCode._10008.getCode());
		}
		
		/******检查userId长度是否超过45个字符*****/
		if (taoBaoPCVerifyUserQuery.getUserId().length() > 45) {
			return new CheckResult(ErrorCode._10011.getMsg(), ErrorCode._10011.getCode());
		}
		return null;
	}
}
