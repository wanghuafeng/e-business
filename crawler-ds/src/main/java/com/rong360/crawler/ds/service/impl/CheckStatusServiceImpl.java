package com.rong360.crawler.ds.service.impl;

import com.rong360.crawler.bean.Job;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.rong360.crawler.ds.check.ICheckStatusService;
import com.rong360.crawler.ds.query.impl.CheckStatusQuery;
import com.rong360.crawler.ds.status.CheckStatus;


/**
 * 
 * @ClassName: CheckStatusServiceImpl
 * @Description:爬虫抓取状态查询接口类
 * @author xiongwei
 * @date 2015-4-20 上午11:34:06
 * 
 */
public class CheckStatusServiceImpl implements ICheckStatusService {

	/*****抓取状态记录表*****/
	private CheckStatus  checkStatus;

	
	/******日志记录*****/
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(CheckStatusServiceImpl.class);
	
	
	public void setCheckStatus(CheckStatus checkStatus) {
		this.checkStatus = checkStatus;
	}

	
	@Override
	public String checkStatus(CheckStatusQuery checkStatusQuery) {
		String userId = checkStatusQuery.getUserId();
		JSONObject jsonObject = new JSONObject();
		Job job;
		switch (checkStatusQuery.getSource()) {
			case "JD":
				job = Job.JD;
				break;
			case "TAOBAO":
				job = Job.TAOBAO;
				break;
			default: 
			/*默认为京东*/
				job = Job.JD;
				break;
		}
		boolean finished = checkStatus.finished(job, userId, checkStatusQuery.getMerchantId());
		if (finished) {
			jsonObject.put("status", "1");
			jsonObject.put("userId", userId);
			jsonObject.put("errorcode", "0");	
		} else {
			jsonObject.put("status", "0");
			jsonObject.put("userId", userId);
			jsonObject.put("errorcode", "0");	
		}
		return jsonObject.toString();
	}

}
