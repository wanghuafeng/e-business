package com.rong360.crawler.ds.check;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.ds.query.impl.TaoBaoPCSendMsgQuery;


/**
 * 
 * @ClassName: ICheckTaoBaoPCSendMsg
 * @Description:检查用户输入参数接口
 * @author xiongwei
 * @date 2015-5-4 上午11:34:06
 * 
 */
public interface ICheckTaoBaoPCSendMsg {

	
	/***
	 * 检查用户输入参数
	 * @param TaoBaoPCSendMsgQuery
	 * @return
	 */
	public CheckResult check(TaoBaoPCSendMsgQuery taobaoPCSendMsgQuery);
}
