package com.rong360.crawler.ds.page.generator;

import com.rong360.crawler.CrawlerConfigurableProperties;
import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.MetaData;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.cookie.CrawlerCookie;
import com.rong360.crawler.ds.query.impl.TaoBaoLoginQuery;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.page.generator.CookieTaskGenerator;
import com.rong360.crawler.util.URIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 
 * @ClassName: TaoBaoCookieTaskGenerator
 * @Description:淘宝任务生成器(根据Cookie生成抓取订单任务)
 * @author xiongwei
 * @date 2015-6-9 上午11:46:19
 * 
 */
public class TaoBaoCookieTaskGenerator extends CookieTaskGenerator{
	
	{
		this.job = Job.TAOBAO;
	}
	/*****批量获取任务*****/
	public List<CrawlerPage> generateAllCookieTask() {
		List<CrawlerPage> crawlerPageList = new ArrayList<CrawlerPage>();
		Set<CrawlerCookie> crawlerCookieSet = cookieManager.getAllCrawlerCookie();
		for (CrawlerCookie crawlerCookie : crawlerCookieSet) {
			if (crawlerCookie.getJob() == this.job) {
				crawlerPageList.addAll(generateSingleCookieTask(crawlerCookie));
			}
	
		}
		return crawlerPageList;
	}
	
	
	/**
	 * 根据Cookie获取CrawlerPage
	 * @param crawlerCookie
	 * @return
	 */
	public List<CrawlerPage> generateSingleCookieTask(CrawlerCookie crawlerCookie) {
		boolean useProxy = (boolean) CrawlerConfigurableProperties.getProperty("tb.use.proxy");
		List<CrawlerPage> crawlerPageList = new ArrayList<>();
		
		List<String> uriList = new ArrayList<>();
		
		uriList.add("https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm");
		for (String uri : uriList) {
			UriData uriData = new UriData(0,URIUtils.getHttpURL(uri));
			uriData.setDefaultCharset("GB18030");
			CrawlerPage cp = new CrawlerPage();
			cp.setSourceData(new SourceData());
			cp.setMetaData(new MetaData());
			cp.getMetaData().setSuccedExtract(true);
			cp.setUriData(uriData);
			cp.setSourceData(new SourceData());
			cp.setJob(crawlerCookie.getJob());
			TaoBaoLoginQuery query = new TaoBaoLoginQuery();
			query.setUserId(crawlerCookie.getUserId());
			query.setLoginName(crawlerCookie.getLoginName());
			query.setMerchantId(crawlerCookie.getMerchantId());
			query.setAppName(crawlerCookie.getAppName());
			query.setAppVersion(crawlerCookie.getAppVersion());
			query.setPlatform(crawlerCookie.getPlatform());
			
			cp.getUriData().setQuery(query);
			cp.getUriData().setEscapeSetCookie(true);
			cp.setJob(Job.TAOBAO);
			cp.setTask(Task.ORDER);
			cp.getUriData().setCookieString(crawlerCookie.getCookie());
			cp.getUriData().setId(crawlerCookie.getLoginName());
			if (crawlerCookie.getProxyIp() != null) {
				cp.getProxy().setProxyIp(crawlerCookie.getProxyIp());
			}
			cp.getProxy().setUseProxy(useProxy);
			crawlerPageList.add(cp);
		}
		return crawlerPageList;
	}
}
