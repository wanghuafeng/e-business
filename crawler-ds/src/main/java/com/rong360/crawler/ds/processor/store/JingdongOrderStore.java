package com.rong360.crawler.ds.processor.store;

import com.rong360.crawler.bean.*;
import com.rong360.crawler.ds.bean.Order;
import com.rong360.crawler.ds.query.impl.JingdongLoginQuery;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.AbstractProcessor;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.processor.store.IStore;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.util.SignatureUtils;
import com.rong360.crawler.util.URIUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author xiongwei
 * @ClassName: JingdongOrderStore
 * @Description:京东订单存储
 * @date 2015-4-15 上午11:46:19
 */
public class JingdongOrderStore extends AbstractProcessor implements IStore {

    /*****
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(JingdongOrderStore.class);


    /*****
     * 数据存储提交URL
     *****/
    private static final String SAVE_URL = "http://wd.rong360.com/api/jsd/saveJDUser";

    {
        this.job = Job.JD;
        this.task = Task.ORDER;
    }

    @Override
    public void process(CrawlerPage crawlerPage) {

        log.info("JingdongOrderStore is processing...... ");
        if (crawlerPage.getMetaData().getCrawlerBeans().size() > 0) {
            saveJingdongOrderData(crawlerPage);
        }
    }


    /***
     * 保存京东订单数据
     *
     * @param crawlerPage
     */
    public void saveJingdongOrderData(CrawlerPage crawlerPage) {
        UriData uriData = new UriData(0, URIUtils.getHttpURL(SAVE_URL));
        uriData.setHttpMethod(HttpMethod.POST);
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.getProxy().setUseProxy(false);
        Query query = crawlerPage.getUriData().getQuery();
        log.info("jd userId: " + query.getUserId());
        String userId = "123456789";
        if (query instanceof JingdongLoginQuery) {
            JingdongLoginQuery jingdongLoginQuery = (JingdongLoginQuery) query;
            userId = jingdongLoginQuery.getUserId();
            if (jingdongLoginQuery.getUserId().startsWith("0031_")) {
                String[] appid_userid = jingdongLoginQuery.getUserId().split("_");
                userId = appid_userid[1];
            }
        }
        String data = assemblyJingdongOrder(crawlerPage);
        Map<String, String> params = new HashMap<String, String>();
        params.put("http_method", "post");
        params.put("uid", userId);
        params.put("data", data);
        params.put("app_version", query.getAppVersion());
        params.put("app_name", query.getAppName());
        params.put("platform", query.getPlatform());
        params.put("merchant_id", query.getMerchantId());
        params.put("token", SignatureUtils.getSignature(params, "@rong363#wd#1215"));
        cp.getUriData().setParams(params);

        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);


        String sourceCode = cp.getSourceData().getSourceCode();


        /***** 接口调用状态码*****/
        int errorCode = 1;

        if (StringUtils.isBlank(sourceCode)) {
            log.fatal("uri:" + crawlerPage.getUriData().getStrUri() + ",data:" + data + " savejingdongOrder failed...... ");
            processCrawlerStatusCode(CrawlerStatusCodes.STORE_ERROR, crawlerPage);

        } else {
            JSONObject jsonObject = JSONObject.fromObject(sourceCode);
            try {
                log.info("sourceCode:" + sourceCode);
                errorCode = jsonObject.getJSONObject("data").getInt("errorcode");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (0 == errorCode) {
                log.info("uri:" + crawlerPage.getUriData().getStrUri() + ", data:" + data + " savejingdongOrder successfully...... ");
                processCrawlerStatusCode(CrawlerStatusCodes.STORE_SUCCESS, crawlerPage);
            } else {
                log.error("uri:" + crawlerPage.getUriData().getStrUri() + ",data:" + data + " savejingdongOrder failed...... ");
                processCrawlerStatusCode(CrawlerStatusCodes.STORE_ERROR, crawlerPage);
            }
        }


    }

    /***
     * 处理HTTP状态
     *
     * @param statusCode
     * @param crawlerPage
     */
    private void processCrawlerStatusCode(int statusCode, CrawlerPage crawlerPage) {
        crawlerPage.getFetchStatusCode().setStatusCode(crawlerPage.getFetchStatusCode().getStatusCode() + statusCode);
    }


    /***
     * 组装京东订单数据
     *
     * @param crawlerPage
     * @return
     */
    public String assemblyJingdongOrder(CrawlerPage crawlerPage) {

        List<CrawlerBean> orderList = crawlerPage.getMetaData().getCrawlerBeans();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (CrawlerBean crawlerBean : orderList) {
            if (crawlerBean instanceof Order) {
                Order order = (Order) crawlerBean;
                JSONObject object = JSONObject.fromObject(order.getAssemleBean());
                jsonArray.add(object);
            }

        }
        jsonObject.put("orderList", jsonArray);
        return jsonObject.toString();
    }

}
