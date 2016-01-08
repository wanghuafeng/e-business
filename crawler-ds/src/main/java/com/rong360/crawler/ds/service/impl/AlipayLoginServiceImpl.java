package com.rong360.crawler.ds.service.impl;

import com.rong360.crawler.bean.*;
import com.rong360.crawler.ds.util.HtmlUtil;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.proxy.ip.ProxyIp;
import com.rong360.crawler.util.RegExpUtil;
import com.rong360.crawler.util.URIUtils;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;

public class AlipayLoginServiceImpl {

    private Logger logger = Logger.getLogger(AlipayLoginServiceImpl.class);

    /**
     * 返回支付宝登陆后的cookie
     *
     * @param cookieString 登陆淘宝后的cookie
     * @param proxyIp      代理ip
     * @return 返回登陆支付宝后的上下文
     */
    public CrawlerPage login(String cookieString, ProxyIp proxyIp) {
        CrawlerPage cp = new CrawlerPage();
        cp.getProxy().setProxyIp(proxyIp);
        String url1 = step1_my_taobao("https://i.taobao.com/my_taobao.htm", cp, cookieString);
        String url2 = step2_user_i(url1, cp);
        String cookie1 = step3_member_login(url2, cp, cookieString);
        return step4_certCheck(cp, cookie1, cookieString);
    }

    private String step1_my_taobao(String url, CrawlerPage crawlerPage, String cookieString) {
        UriData uriData = new UriData(0, URIUtils.getHttpURL(url));
        crawlerPage.setSourceData(new SourceData());
        crawlerPage.setMetaData(new MetaData());
        crawlerPage.getMetaData().setSuccedExtract(true);
        crawlerPage.setUriData(uriData);
        crawlerPage.setSourceData(new SourceData());
        crawlerPage.setCrawlerState(CrawlerState.UPDATED);
        crawlerPage.setJob(Job.TAOBAO);
        crawlerPage.getUriData().setSave(false);
        crawlerPage.getUriData().setCookieString(cookieString);
//        crawlerPage.getUriData().setUseRedirectCookie(true);
//        crawlerPage.getUriData().setUsePrevCookie(true);
        Fetcher fetcher = new Fetcher();
        fetcher.process(crawlerPage);
        Matcher matcher = RegExpUtil.getMatcher(crawlerPage.getSourceData().getSourceCode(), "href=\"(//lab.alipay.com/user/i.htm\\?src=yy_content_jygl.*?)\" >我的支付宝</a>");
        if (matcher.find()) {
            return "http:" + matcher.group(1);
        }
        return "";
    }

    private String step2_user_i(String url, CrawlerPage crawlerPage) {
        crawlerPage.getUriData().setUri(URIUtils.getHttpURL(url));
        Fetcher fetcher = new Fetcher();
        fetcher.process(crawlerPage);
        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        Matcher matcher = RegExpUtil.getMatcher(sourceCode, "window.location.href = \"(.*?)\";");
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String step3_member_login(String url, CrawlerPage crawlerPage, String cookieString) {
        crawlerPage.getUriData().setUri(URIUtils.getHttpURL(url));
        crawlerPage.getUriData().setCookieString(cookieString);
        Fetcher fetcher = new Fetcher();
        fetcher.process(crawlerPage);
        HtmlUtil.extractParam(crawlerPage);
        return crawlerPage.getUriData().getCookieString();
    }

    private CrawlerPage step4_certCheck(CrawlerPage cp, String cookie1, String cookieString) {
        Fetcher fetcher = new Fetcher();
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        UriData uriData = cp.getUriData();
        uriData.setUri(URIUtils.getHttpURL("https://authsu18.alipay.com/login/certCheck.htm"));
        uriData.setCookieString(cookieString);
        fetcher.process(cp);
        String cookie2 = cp.getUriData().getCookieString();
        String sourceCode = cp.getSourceData().getSourceCode();
        Matcher matcher = RegExpUtil.getMatcher(sourceCode, "window.location.href = \"(.*?)\";");
        if (matcher.find()) {
//            System.out.println("***********************************");
            String newUrl = matcher.group(1);
            cp.getUriData().setUri(URIUtils.getHttpURL(newUrl));
            cp.getUriData().setCookieString(cookie1 + cookie2 + cookieString);
            cp.getUriData().setReferer("https://authsu18.alipay.com/login/certCheck.htm");
            cp.getUriData().setHttpMethod(HttpMethod.GET);
            cp.getUriData().setEscapeSetCookie(true);
            fetcher.process(cp);
            logger.info(cp.getSourceData().getSourceCode());
            logger.info(cp.getUriData().getCookieString());
        }
        return cp;
    }

    public static boolean isSuccessLogining(String sourceCode) {
        return sourceCode.contains("账户余额");
    }
}
