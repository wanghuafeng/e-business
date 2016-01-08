package com.rong360.crawler.ds.util;

import com.rong360.crawler.bean.*;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UserAgentType;
import com.rong360.crawler.query.Query;
import net.sf.json.JSONObject;

/**
 * Created by Administrator on 2015/12/8.
 */
public class Util {

    public static CrawlerPage getDefaultCrawlerPage(Job job, Task task, Query query) {
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.setJob(job);
        cp.setTask(task);
        cp.getUriData().setQuery(query);
        return cp;
    }

    public static CrawlerPage getTbDefaultCP(Query query) {
        CrawlerPage crawlerPage = getDefaultCrawlerPage(Job.TAOBAO, Task.LOGIN, query);
        crawlerPage.getUriData().setUserAgent(UserAgentType.CHROME);
        return crawlerPage;
    }

    public static JSONObject getJSONObject(String key, Object value) {
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object;
    }

    /**
     * @param job
     * @param loginName
     * @return
     */
    public static String generateRedisKey(Job job, String loginName) {
        return job.toString() + "_" + loginName;
    }
}
