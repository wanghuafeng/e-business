package com.rong360.crawler.ds.test;

import com.rong360.crawler.bean.CrawlerState;
import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.MetaData;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.util.URIUtils;
import junit.framework.TestCase;

/**
 * Created by Administrator on 2015/12/9.
 */
public class JDOuterApiResourceTest extends TestCase {
    private String domain = "localhost";

    public void testGetPicCode() throws Exception {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/api/jd/getPicCode"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("login_name", "260080991@qq.com");
        cp.getUriData().getParams().put("app_version", "1.0");
        cp.getUriData().getParams().put("app_name", "hhh");
        cp.getUriData().getParams().put("platform", "mobile");
        cp.getUriData().getParams().put("merchant_id", "123456");
        cp.getUriData().getParams().put("user_id", "2000000");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }

    public void testLogin() throws Exception {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/api/jd/login"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("login_name", "260080991@qq.com");
        cp.getUriData().getParams().put("app_version", "1.0");
        cp.getUriData().getParams().put("app_name", "hhh");
        cp.getUriData().getParams().put("platform", "mobile");
        cp.getUriData().getParams().put("merchant_id", "123456");
        cp.getUriData().getParams().put("user_id", "2000000");
        cp.getUriData().getParams().put("password", "drteammangel1234");
        cp.getUriData().getParams().put("pic_code", "6853");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }

    public void testGetStatus() throws Exception {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/api/jd/getStatus"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("status_id", "1234");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }
}