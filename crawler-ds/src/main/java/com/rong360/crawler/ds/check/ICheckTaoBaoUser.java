package com.rong360.crawler.ds.check;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.ds.query.impl.TaoBaoVerifyUserQuery;


/**
 * 
 * @ClassName: ICheckTaoBaoUser
 * @Description:检查用户输入参数接口
 * @author xiongwei
 * @date 2015-5-4 上午11:34:06
 * 
 */
public interface ICheckTaoBaoUser {

	
	/***
	 * 检查用户输入参数
	 * @param jdVerifyUserQuery
	 * @return
	 */
	public CheckResult check(TaoBaoVerifyUserQuery taoBaoVerifyUserQuery);
}
