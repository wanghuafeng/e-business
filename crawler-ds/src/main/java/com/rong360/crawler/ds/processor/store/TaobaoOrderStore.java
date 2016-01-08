package com.rong360.crawler.ds.processor.store;

import java.util.HashMap;
import java.util.Map;

import com.rong360.crawler.bean.*;
import com.rong360.crawler.ds.bean.*;
import com.rong360.crawler.ds.query.impl.TaoBaoLoginQuery;
import com.rong360.crawler.query.Query;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.AbstractProcessor;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.processor.store.IStore;
import com.rong360.crawler.util.SignatureUtils;
import com.rong360.crawler.util.URIUtils;


/**
 * @author xiongwei
 * @ClassName: TaobaoOrderStore
 * @Description:淘宝订单存储
 * @date 2015-4-15 上午11:46:19
 */
public class TaobaoOrderStore extends AbstractProcessor implements IStore {

    /*****
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(TaobaoOrderStore.class);


    /*****
     * 数据存储提交URL
     *****/
    private static final String SAVE_URL = "http://wd.rong360.com/api/jsd/saveTaoBaoUser";

    {
        this.job = Job.TAOBAO;
        this.task = Task.ORDER;
    }

    @Override
    public void process(CrawlerPage crawlerPage) {
        log.info("TaobaoOrderStore is processing...... ");
        if (crawlerPage.getMetaData().getCrawlerBeans().size() > 0) {
            saveTaobaoUserData(crawlerPage);
        }
    }


    /***
     * 保存淘宝数据
     *
     * @param crawlerPage
     */
    public void saveTaobaoUserData(CrawlerPage crawlerPage) {
        UriData uriData = new UriData(0, URIUtils.getHttpURL(SAVE_URL));
        uriData.setHttpMethod(HttpMethod.POST);
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.getProxy().setUseProxy(false);
        Query query = crawlerPage.getUriData().getQuery();

        log.info("taobao userId: " + query.getUserId());
        String uid = "123456789";
        if (query instanceof TaoBaoLoginQuery) {
            TaoBaoLoginQuery taoBaoLoginQuery = (TaoBaoLoginQuery) query;
            uid = taoBaoLoginQuery.getUserId();
            if (uid.startsWith("0031_")) {
                uid = uid.split("_")[1];
            }
        }

        JSONObject data = formatData(crawlerPage);
        Map<String, String> params = new HashMap<String, String>();
        params.put("http_method", "post");
        params.put("uid", uid);
        params.put("data", data.toString());
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

        try {
            JSONObject jsonObject = JSONObject.fromObject(sourceCode);
            errorCode = jsonObject.getJSONObject("data").getInt("errorcode");
            if (0 == errorCode) {
                log.info("uri:" + crawlerPage.getUriData().getStrUri() + ", crawlerBean:" + data.toString() + " saveTaobaoUserData successfully...... ");
                processCrawlerStatusCode(CrawlerStatusCodes.STORE_SUCCESS, crawlerPage);
            } else {
                log.info("uri:" + crawlerPage.getUriData().getStrUri() + ",crawlerBean:" + data.toString() + " saveTaobaoUserData failed...... ");
                processCrawlerStatusCode(CrawlerStatusCodes.STORE_ERROR, crawlerPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public JSONObject formatData(CrawlerPage crawlerPage) {
        JSONObject data = new JSONObject();
        JSONArray addrs = new JSONArray();
        JSONArray tbOrders = new JSONArray();
        JSONArray alipayOrders = new JSONArray();
        JSONArray alipayBankcards = new JSONArray();
        TbInfoBean tbInfoBean = new TbInfoBean();
        AlipayInfoBean alipayInfoBean = new AlipayInfoBean();
        for (CrawlerBean crawlerBean : crawlerPage.getMetaData().getCrawlerBeans()) {
            if (crawlerBean instanceof AlipayBankCardBean) {
                alipayBankcards.add(crawlerBean.getAssemleBean());
            } else if (crawlerBean instanceof AlipayInfoBean) {
                alipayInfoBean = (AlipayInfoBean) crawlerBean;
            } else if (crawlerBean instanceof AlipayOrderBean) {
                alipayOrders.add(crawlerBean.getAssemleBean());
            } else if (crawlerBean instanceof TbAddrBean) {
                addrs.add(crawlerBean.getAssemleBean());
            } else if (crawlerBean instanceof TbInfoBean) {
                tbInfoBean = (TbInfoBean) crawlerBean;
            } else if (crawlerBean instanceof Order) {
                tbOrders.add(crawlerBean.getAssemleBean());
            }
        }
        tbInfoBean.setAddrs(addrs);
        data.put("alipayList", alipayOrders);
        data.put("alipayInfo", alipayInfoBean.getAssemleBean());
        data.put("alipayBankcards", alipayBankcards);
        data.put("tbInfo", tbInfoBean);
        data.put("orderList", tbOrders);
        return data;
    }
}
