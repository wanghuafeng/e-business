package com.rong360.crawler.ds.check;

import com.rong360.crawler.ds.query.impl.CheckStatusQuery;

/**
 * 
 * @ClassName: ICheckStatusService
 * @Description:爬虫抓取状态查询接口类
 * @author xiongwei
 * @date 2015-4-20 上午11:34:06
 * 
 */
public interface ICheckStatusService {

	/***
	 * 根据userid查询抓取状态
	 * @param userId
	 * @return
	 */
	public String checkStatus(CheckStatusQuery checkStatusQuery);
}
