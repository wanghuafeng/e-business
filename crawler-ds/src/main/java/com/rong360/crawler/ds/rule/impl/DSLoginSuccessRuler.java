package com.rong360.crawler.ds.rule.impl;

import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.cookie.CrawlerCookie;
import com.rong360.crawler.cookie.manager.CookieManager;
import com.rong360.crawler.ds.page.generator.KeyGenerator;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.rule.AbstractRulor;

/**
 * @author xiongwei
 */
public class DSLoginSuccessRuler extends AbstractRulor {

    /*****
     * Cookie管理器
     *****/
    private CookieManager cookieManager;

    public void setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    {
        this.task = Task.LOGIN;
    }

    @Override
    public boolean rule(CrawlerPage crawlerPage) {
        String cookie = crawlerPage.getUriData().getCookieString();
        Job job = crawlerPage.getJob();
        Query query = crawlerPage.getUriData().getQuery();
        String userId = query.getUserId();
        String loginName = query.getLoginName();
        String key = KeyGenerator.generateKey(job.name(), userId, query.getMerchantId());
        CrawlerCookie crawlerCookie = new CrawlerCookie(key, job, loginName, cookie, userId, query.getMerchantId(),
                query.getAppName(), query.getAppVersion(), query.getPlatform());
        crawlerCookie.setStatusId(crawlerPage.getCrawlerStatusId());
        if (crawlerPage.getProxy().getProxyIp() != null) {
            crawlerCookie.setProxyIp(crawlerPage.getProxy().getProxyIp());
        }
        cookieManager.saveOrUpdateCookies(key, crawlerCookie);
        return false;
    }

}
