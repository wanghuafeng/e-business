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

public class TBOuterApiResourceTest extends TestCase {

    private String domain = "localhost";
    //    private String domain = "192.168.88.128";
//    private String domain = "10.0.2.113";
    private String loginName = "drteamshmily";
    private String password = "gcipo31415";

    public void testGetPicCode() throws Exception {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/api/taobao/getPicCode"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("login_name", loginName);
        cp.getUriData().getParams().put("app_version", "1.0");
        cp.getUriData().getParams().put("app_name", "hhh");
        cp.getUriData().getParams().put("platform", "mobile");
        cp.getUriData().getParams().put("merchant_id", "123456");
        cp.getUriData().getParams().put("user_id", "01112");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }

    public void testSendMsg() throws Exception {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/api/taobao/sendMsg"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("login_name", loginName);
        cp.getUriData().getParams().put("user_id", "01112");
        cp.getUriData().getParams().put("phone", "188****7592");
        cp.getUriData().getParams().put("app_version", "1.0");
        cp.getUriData().getParams().put("app_name", "hhh");
        cp.getUriData().getParams().put("platform", "mobile");
        cp.getUriData().getParams().put("merchant_id", "123456");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }

    public void testVerifyMsg() throws Exception {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/api/taobao/verifyMsg"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("app_version", "1.0");
        cp.getUriData().getParams().put("app_name", "hhh");
        cp.getUriData().getParams().put("platform", "mobile");
        cp.getUriData().getParams().put("merchant_id", "123456");
        cp.getUriData().getParams().put("login_name", loginName);
        cp.getUriData().getParams().put("user_id", "01112");
        cp.getUriData().getParams().put("message_code", "843310");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }

    public void testLogin() throws Exception {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/api/taobao/login"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("app_version", "1.0");
        cp.getUriData().getParams().put("app_name", "hhh");
        cp.getUriData().getParams().put("platform", "mobile");
        cp.getUriData().getParams().put("merchant_id", "123456");
        cp.getUriData().getParams().put("login_name", loginName);
        cp.getUriData().getParams().put("user_id", "01112");
        cp.getUriData().getParams().put("password", password);
        cp.getUriData().getParams().put("pic_code", "15cf");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }
}