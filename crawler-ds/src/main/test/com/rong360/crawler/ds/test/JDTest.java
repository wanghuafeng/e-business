package com.rong360.crawler.ds.test;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Base64Utils;

import junit.framework.TestCase;

import com.rong360.crawler.bean.CrawlerState;
import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.MetaData;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.util.URIUtils;


public class JDTest extends TestCase{

	//private String domain = "localhost";	
	private String domain = "localhost";
		
	/**测试获取验证码*****/
	public void testAuthCode() {
		    UriData uriData = new UriData(0,URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/jd/verifyUser.json"));
			CrawlerPage cp = new CrawlerPage();
			cp.setSourceData(new SourceData());
			cp.setMetaData(new MetaData());
			cp.getMetaData().setSuccedExtract(true);
			cp.setUriData(uriData);
			cp.setSourceData(new SourceData());
			cp.setCrawlerState(CrawlerState.UPDATED);
			cp.getUriData().getParams().put("appId", "0031");
			cp.getUriData().getParams().put("timeunit", "1425621163841");
			cp.getUriData().getParams().put("token", "8f5b7decf99aba00e390679c7036b997");
			cp.getUriData().getParams().put("loginName", "260080991@qq.com");
			cp.getUriData().getParams().put("userId", "0031_2000000");
			cp.getUriData().setHttpMethod(HttpMethod.POST);
			Fetcher fetcher = new Fetcher();
			fetcher.process(cp);
			System.out.println(cp.getSourceData().getSourceCode());
    }
	
	
	/**测试登录*****/
	public void testLogin() {
		    UriData uriData = new UriData(0,URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/jd/login.json"));
			CrawlerPage cp = new CrawlerPage();
			cp.setSourceData(new SourceData());
			cp.setMetaData(new MetaData());
			cp.getMetaData().setSuccedExtract(true);
			cp.setUriData(uriData);
			cp.setSourceData(new SourceData());
			cp.setCrawlerState(CrawlerState.UPDATED);
			cp.getUriData().getParams().put("appId", "0031");
			cp.getUriData().getParams().put("timeunit", "1425621163841");
			cp.getUriData().getParams().put("token", "8f5b7decf99aba00e390679c7036b997");
			cp.getUriData().getParams().put("loginName", "260080991@qq.com");
			cp.getUriData().getParams().put("userId", "0031_2000000");
			cp.getUriData().getParams().put("password", "drteammangel1234");
			cp.getUriData().getParams().put("authcode", "");
			cp.getUriData().setHttpMethod(HttpMethod.POST);
			Fetcher fetcher = new Fetcher();
			fetcher.process(cp);
			System.out.println(cp.getSourceData().getSourceCode());
    }
	
	
	/**检查状态*****/
	public void testCheckStatus() {
		    UriData uriData = new UriData(0,URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/report/checkstatus.json"));
			CrawlerPage cp = new CrawlerPage();
			cp.setSourceData(new SourceData());
			cp.setMetaData(new MetaData());
			cp.getMetaData().setSuccedExtract(true);
			cp.setUriData(uriData);
			cp.setSourceData(new SourceData());
			cp.setCrawlerState(CrawlerState.UPDATED);
			cp.getUriData().getParams().put("appId", "0031");
			cp.getUriData().getParams().put("timeunit", "1425621163841");
			cp.getUriData().getParams().put("token", "8f5b7decf99aba00e390679c7036b997");
			cp.getUriData().getParams().put("userId", "0031_2000000");
			cp.getUriData().setHttpMethod(HttpMethod.POST);
			Fetcher fetcher = new Fetcher();
			fetcher.process(cp);
			System.out.println(cp.getSourceData().getSourceCode());
    }
	
	
	public void testBase64() {
		System.out.println(Base64Utils.encodeToString(null));
		System.out.println(StringUtils.isBlank(null));
	}

 }