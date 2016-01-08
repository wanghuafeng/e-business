package com.rong360.crawler.ds.page.generator;

import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.MetaData;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.cookie.CrawlerCookie;
import com.rong360.crawler.ds.query.impl.JingdongLoginQuery;
import com.rong360.crawler.ds.status.CheckStatus;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.page.generator.CookieTaskGenerator;
import com.rong360.crawler.util.URIUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


/**
 * @author xiongwei
 * @ClassName: JDCookieTaskGenerator
 * @Description:京东任务生成器(根据Cookie生成抓取订单任务)
 * @date 2015-3-20 上午11:46:19
 */
public class JDCookieTaskGenerator extends CookieTaskGenerator {
    @Autowired
    private CheckStatus checkStatus;

    public List<String> processingUserList = new ArrayList<String>();

    {
        this.job = Job.JD;
    }

    /*****
     * 批量获取任务
     *****/
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
     *
     * @param crawlerCookie
     * @return
     */
    public List<CrawlerPage> generateSingleCookieTask(CrawlerCookie crawlerCookie) {
        List<CrawlerPage> crawlerPageList = new ArrayList<CrawlerPage>();
        if (processingUserList.contains(crawlerCookie.getKey())) {
            return crawlerPageList;
        }
        processingUserList.add(crawlerCookie.getKey());

        List<String> uriList = new ArrayList<String>();

        /****今年以内订单******/
        uriList.add("http://order.jd.com/center/list.action?search=0&d=2&s=4096");

        /****2015年以内订单******/
        uriList.add("http://order.jd.com/center/list.action?search=0&d=2015&s=4096");

        /****2014年以内订单******/
        uriList.add("http://order.jd.com/center/list.action?search=0&d=2014&s=4096");

        /****2013年以内订单******/
        uriList.add("http://order.jd.com/center/list.action?search=0&d=2013&s=4096");

        /****2013年以前订单******/
        uriList.add("http://order.jd.com/center/list.action?search=0&d=3&s=4096");
        for (String uri : uriList) {
            UriData uriData = new UriData(0, URIUtils.getHttpURL(uri));
            uriData.setDefaultCharset("GB18030");
            CrawlerPage cp = new CrawlerPage();
            cp.setSourceData(new SourceData());
            cp.setMetaData(new MetaData());
            cp.getMetaData().setSuccedExtract(true);
            cp.setUriData(uriData);
            cp.setSourceData(new SourceData());
            cp.setJob(crawlerCookie.getJob());
            JingdongLoginQuery query = new JingdongLoginQuery();
            System.out.println("jd cookie:" + query.toString());
            query.setUserId(crawlerCookie.getUserId());
            query.setLoginName(crawlerCookie.getLoginName());
            query.setMerchantId(crawlerCookie.getMerchantId());
            query.setAppName(crawlerCookie.getAppName());
            query.setAppVersion(crawlerCookie.getAppVersion());
            query.setPlatform(crawlerCookie.getPlatform());
            cp.getUriData().setQuery(query);
            cp.getUriData().setEscapeSetCookie(true);
            cp.setTask(Task.ORDER);
            cp.getUriData().setCookieString(crawlerCookie.getCookie());
            cp.getUriData().setId(crawlerCookie.getLoginName());
            crawlerPageList.add(cp);
        }
        return crawlerPageList;
    }

    public void clearUser(String userId) {
        processingUserList.remove(userId);
    }
}
