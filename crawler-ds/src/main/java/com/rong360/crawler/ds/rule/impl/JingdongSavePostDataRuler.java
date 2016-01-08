package com.rong360.crawler.ds.rule.impl;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.cookie.PostData;
import com.rong360.crawler.cookie.manager.PostDataManager;
import com.rong360.crawler.ds.processor.extractor.JingdongLoginExtractor;
import com.rong360.crawler.ds.query.impl.JingdongLoginQuery;
import com.rong360.crawler.ds.query.impl.JingdongVerifyUserQuery;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.rule.AbstractRulor;
import com.rong360.crawler.util.URIUtils;

/**
 *
 * @ClassName: JingdongSavePostDataRuler
 * @Description:保存登录时获取到的提交信息
 * @author xiongwei
 * @date 2015-5-4 上午11:34:06
 *
 */
public class JingdongSavePostDataRuler extends AbstractRulor {


	/***** 日志记录 *****/
	private static Logger log = Logger.getLogger(JingdongSavePostDataRuler.class);

	/*****京东登录首页URL*****/
	private static final String LOGIN_PAGE_URL = "https://passport.jd.com/uc/login";

	/*****京东登录验证用户是否需要验证码页面URL *****/
	private static final String  SHOW_AUTH_CODE_URL = "https://passport.jd.com/uc/showAuthCode?r=0.209022136637941&version=2015";

	/*****京东登录验证用户是否已经检查验证码页面URL *****/
	private static final String  REMEMBERME_CHECK_URL = "https://passport.jd.com/uc/rememberMeCheck?r=0.4565836989786476&version=2015";


	/***** POST提交参数管理器 *****/
	private PostDataManager postDataManager;

	/*****京东登录首页解析器*****/
	private JingdongLoginExtractor jingdongLoginExtractor;

	public void setJingdongLoginExtractor(
			JingdongLoginExtractor jingdongLoginExtractor) {
		this.jingdongLoginExtractor = jingdongLoginExtractor;
	}


	public void setPostDataManager(PostDataManager postDataManager) {
		this.postDataManager = postDataManager;
	}

	{
		this.job = Job.JD;
		this.task = Task.LOGIN;
	}

	@Override
	public boolean rule(CrawlerPage crawlerPage) {

		Query query = crawlerPage.getUriData().getQuery();
		if (query instanceof JingdongVerifyUserQuery) {
			JingdongVerifyUserQuery userQuery = (JingdongVerifyUserQuery) query;
			verifyUserRule(crawlerPage, userQuery);
			return true;
		} else if (query instanceof JingdongLoginQuery) {
			JingdongLoginQuery jdLoginQuery = (JingdongLoginQuery) query;
			String key = crawlerPage.getJob() + "_" + jdLoginQuery.getLoginName();
			PostData postData = postDataManager.getPostData(key);
			if (postData != null) {
				loginRule(crawlerPage, jdLoginQuery);
		    	postDataManager.removePostData(key);
			} else {
				JingdongVerifyUserQuery userQuery = new JingdongVerifyUserQuery();
				 try {
						BeanUtils.copyProperties(userQuery, jdLoginQuery);
					} catch (Exception e) {
					 log.warn(e.getMessage());
					 e.printStackTrace();
					}
			    crawlerPage.getUriData().setQuery(userQuery);
				verifyUserRule(crawlerPage, userQuery);
				crawlerPage.getUriData().setQuery(jdLoginQuery);
				loginRule(crawlerPage, jdLoginQuery);
			}
			return true;
		}
		return true;
	}

	/**
	 * 用户名验证规则
	 * @param crawlerPage
	 */
	private void verifyUserRule(CrawlerPage crawlerPage, JingdongVerifyUserQuery userQuery) {

		Fetcher fetcher = new Fetcher();
		/*****1. 访问京东登录首页 获取此页Cookie与登录提交信息*****/
		UriData uriData = crawlerPage.getUriData();
		uriData.setUri(URIUtils.getHttpURL(LOGIN_PAGE_URL));
		crawlerPage.setUriData(uriData);
		crawlerPage.setSourceData(new SourceData());

		SourceData sourceData = null;
		String authCodeURL = "";
		for (int i=0; i<3; i++) {
			fetcher.innerExecute(crawlerPage);
			crawlerPage.setTask(Task.LOGIN);
			/*****2. 获取验证码地址*****/
			sourceData = crawlerPage.getSourceData();
			Source source = new Source(sourceData.getSourceCode());
			try {
				Element verificationElement = source.getElementById("JD_Verification1");
				authCodeURL = verificationElement.getAttributeValue("src2");
				break;
			} catch (Exception e) {
				log.warn(e.getMessage());
				authCodeURL = "";
			}
		}


		/*****3. 京东登录首页解析器,提取随机验证码*****/
		jingdongLoginExtractor.innerExecute(crawlerPage);

		/*****4. 验证用户是否需要验证码 *****/
		crawlerPage.getUriData().getParams().clear();
   	    crawlerPage.getUriData().getParams().put("loginName", userQuery.getLoginName());
   	    crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);

		crawlerPage.getUriData().setUri(URIUtils.getHttpURL(REMEMBERME_CHECK_URL));
		fetcher.innerExecute(crawlerPage);
		log.info(crawlerPage.getSourceData().getSourceCode());

		crawlerPage.getUriData().setUri(URIUtils.getHttpURL(SHOW_AUTH_CODE_URL));
		fetcher.innerExecute(crawlerPage);



		/*****5. 通过步骤3验证结果返回接口数据 *****/
		sourceData = crawlerPage.getSourceData();

		/***** 用户名 *****/
		String loginName = "";
		Job job = crawlerPage.getJob();
		loginName = userQuery.getLoginName();

		String key = job.toString() + "_" + loginName;
		PostData postData  = postDataManager.getPostData(key);
		log.info(crawlerPage.getSourceData().getSourceCode());
		if (sourceData.getSourceCode().contains("true")) {
			crawlerPage.getUriData().setAuthCodeURL(authCodeURL);
			postData.setAuthCodeURL(crawlerPage.getUriData().getAuthCodeURL());
		}
		postDataManager.saveOrUpdate(postData);

	}

	/**
	 * 登录规则
	 * @param crawlerPage
	 */
	private void loginRule(CrawlerPage crawlerPage, JingdongLoginQuery jdLoginQuery) {
		String key = crawlerPage.getJob() + "_" + jdLoginQuery.getLoginName();
		PostData postData = postDataManager.getPostData(key);

		if (postData.getProxyIp() != null) {
			crawlerPage.getProxy().setProxyIp(postData.getProxyIp());
		}
		crawlerPage.getUriData().getParams().clear();
		/***** 取出所有POST参数赋予给crawlerPage *****/
		crawlerPage.getUriData().getParams().putAll(
				postData.getParams());

		/***** 取出所有临时Cookie赋予给crawlerPage *****/
		crawlerPage.getUriData().setCookieString(
				postData.getTempCookieString());

		/***** 取出用户名、密码参数赋予给crawlerPage *****/
		crawlerPage.getUriData().getParams().put("loginname",
				jdLoginQuery.getLoginName());
		crawlerPage.getUriData().getParams().put("nloginpwd",
				jdLoginQuery.getPassword());
		crawlerPage.getUriData().getParams().put("loginpwd",
				jdLoginQuery.getPassword());
		crawlerPage.getUriData().getParams().put("authcode",
				jdLoginQuery.getAuthcode());
		crawlerPage.getUriData().getParams().put("machineCpu", "");
		crawlerPage.getUriData().getParams().put("machineDisk", "");
		crawlerPage.getUriData().getParams().put("machineNet", "");
		crawlerPage.getUriData().getParams().put("chkRememberMe", "on");
	}
}
