package com.rong360.crawler.ds.service;

import com.rong360.crawler.page.CrawlerPage;

/**
 * 
 * @ClassName: ILoginService
 * @Description:登录服务接口
 * @author xiongwei
 * @date 2015-3-23 上午11:46:19
 * 
 */
public interface ILoginService {
	/***
	 * 验证用户账号信息
	 * @param crawlerPage
	 * @return
	 */
	Object verifyUser(CrawlerPage crawlerPage);
	/***
	 * 用户账号登录
	 * @param crawlerPage
	 * @return
	 */
	Object login(CrawlerPage crawlerPage);
	/***
	 * 向用户手机发送动态口令
	 * @param crawlerPage
	 * @return
	 */
	Object sendMsg(CrawlerPage crawlerPage);
	
	/***
	 * 验证用户手机动态口令
	 * @param crawlerPage
	 * @return
	 */
	Object verifyPhoneCode(CrawlerPage crawlerPage);
}
