package com.rong360.crawler.ds.check;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.ds.query.impl.TaoBaoLoginQuery;


/**
 * 
 * @ClassName: ICheckJDUser
 * @Description:检查用户登录请求参数接口
 * @author xiongwei
 * @date 2015-3-23 上午11:34:06
 * 
 */
public interface ICheckTaoBaoLogin {

	/***
	 * 检查用户输入参数
	 * @param JingdongLoginQuery
	 * @return
	 */
	public CheckResult check(TaoBaoLoginQuery taobaoLoginQuery);
}
