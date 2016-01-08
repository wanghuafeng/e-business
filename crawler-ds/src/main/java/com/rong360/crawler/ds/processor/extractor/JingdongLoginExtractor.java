package com.rong360.crawler.ds.processor.extractor;

import org.apache.log4j.Logger;

import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.cookie.PostData;
import com.rong360.crawler.cookie.manager.PostDataManager;
import com.rong360.crawler.ds.query.impl.JingdongVerifyUserQuery;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.processor.AbstractProcessor;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.query.Query;


/**
 * 
 * @ClassName: JDLoginExtractor
 * @Description:京东登录首页解析器
 * @author xiongwei
 * @date 2015-3-20 上午11:34:06
 * 
 */
public class JingdongLoginExtractor extends AbstractProcessor{

	/***** 日志记录 *****/
	private static Logger log = Logger.getLogger(Fetcher.class);
	
	/*****POST提交参数管理器*****/
	private PostDataManager postDataManager;
	

	public void setPostDataManager(PostDataManager postDataManager) {
		this.postDataManager = postDataManager;
	}
	
	{
		this.job = Job.JD;
		this.task = Task.LOGIN;
	}

	
	@Override
	public void process(CrawlerPage crawlerPage) {
		
		/*****1.通过解析京东登录首页URL获取uuid*****/
		 String sourceCode = crawlerPage.getSourceData().getSourceCode();
	     String strs1[] = sourceCode.split("name=\"uuid\" value=\"");
	     String strs2[] = strs1[1].split("\"/>");
	     String uuid = strs2[0];
	     log.info("uuid:" + uuid);
	     
	     /*****2.通过解析京东登录首页URL获取随机验证值*****/
	     String str3s[] = strs1[1].split("type=\"hidden\" name=\"");
	     String strs4[] = str3s[6].split("/>");
	     String strs5[] = strs4[0].trim().split("\"");
	     String randomKey = strs5[0];
	     String randomValue = strs5[2];
	     log.info(randomKey + ":" + randomValue);
	     
	     Query query = crawlerPage.getUriData().getQuery();
	     
	     
	     /*****3.保存通过解析首页获取到的登录验证值*****/
	     if (query instanceof JingdongVerifyUserQuery) {
	    	 JingdongVerifyUserQuery jdVerifyUserQuery = (JingdongVerifyUserQuery)query;
	    	 Job job = crawlerPage.getJob();
	    	 String loginName = jdVerifyUserQuery.getLoginName();
	    	 String cookie = crawlerPage.getUriData().getCookieString();
	    	 PostData postData = new PostData(job, loginName, cookie);
	    	 postData.getParams().put("uuid", uuid);
	    	 postData.getParams().put(randomKey, randomValue);
	    	 postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
	    	 postDataManager.saveOrUpdate(postData);
	     }
	     
	}
}
