package com.rong360.crawler.ds.processor.store;

import com.rong360.crawler.api.ApiWd;
import com.rong360.crawler.bean.CrawlerBean;
import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.Module;
import com.rong360.crawler.cookie.CrawlerCookie;
import com.rong360.crawler.cookie.manager.CookieManager;
import com.rong360.crawler.ds.bean.OrderSource;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.ds.bean.DSOrderCount;
import com.rong360.crawler.ds.bean.Order;
import com.rong360.crawler.ds.page.generator.KeyGenerator;
import com.rong360.crawler.ds.query.impl.JingdongLoginQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoLoginQuery;
import com.rong360.crawler.ds.service.DSOrderCountService;
import com.rong360.crawler.ds.status.CheckStatus;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.processor.AbstractProcessor;
import com.rong360.crawler.processor.store.IStore;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.util.NoticeUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author xiongwei
 * @ClassName: OrderCheckStatusStore
 * @Description:订单抓取状态存储
 * @date 2015-4-15 上午11:46:19
 */
public class OrderCheckStatusStore extends AbstractProcessor implements IStore {

    //TODO 统计订单数
    @Autowired
    private DSOrderCountService dsOrderCountService;

    @Autowired
    private CookieManager cookieManager;
    /**
     * 保存订单数
     */
    private Map<String, Integer> orderCountMap = new HashMap<String, Integer>();

    /*****
     * 抓取状态记录表
     *****/
    private CheckStatus checkStatus;


    /******
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(OrderCheckStatusStore.class);


    public void setCheckStatus(CheckStatus checkStatus) {
        this.checkStatus = checkStatus;
    }


    @Override
    public boolean accept(CrawlerPage crawlerPage) {
        boolean a = crawlerPage.getTask() == Task.ORDER;
        boolean b = (crawlerPage.getJob() == Job.JD || crawlerPage.getJob() == Job.TAOBAO);
        return a && b;
    }


    @Override
    public void process(CrawlerPage crawlerPage) {
        log.info("OrderCheckStatusStore is processing...... ");
        Query query = crawlerPage.getUriData().getQuery();
        String userId = query.getUserId();
        String key = KeyGenerator.generateKey(crawlerPage.getJob().name(), userId, query.getMerchantId());
        if (query instanceof JingdongLoginQuery) {
            JingdongLoginQuery jingdongLoginQuery = (JingdongLoginQuery) query;
            checkStatus.updateCheckStatus(crawlerPage.getJob(), userId, query.getMerchantId());
            addOrderNumber(crawlerPage);
            if (checkStatus.finished(Job.JD, userId, query.getMerchantId())) {
                CrawlerCookie crawlerCookie = cookieManager.getCookie(key);
                if (crawlerCookie.getStatusId() != 0) {
                    ApiWd.updateStatus(crawlerCookie.getStatusId(), NoticeUtils.CrawlerStatus.DONE.getStatus());
                }
                NoticeUtils.notice(Module.JD.getModuleName(), jingdongLoginQuery.getNoticeUrl(), userId,
                        NoticeUtils.CrawlerStatus.DONE.getStatus(), null);
                int orderCount = orderCountMap.get(getKey(userId, OrderSource.JD.getSource(), query.getMerchantId()));
                orderCountMap.remove(getKey(userId, OrderSource.JD.getSource(), query.getMerchantId()));
                try {
                    /*保存京东订单数量*/
                    DSOrderCount dsOrderCount = new DSOrderCount();
                    if (userId.startsWith("0031_")) {
                        dsOrderCount.setUid(userId.split("_")[1]);
                    } else {
                        dsOrderCount.setUid(userId);
                    }
                    dsOrderCount.setLoginName(jingdongLoginQuery.getLoginName());
                    dsOrderCount.setOrderSource(OrderSource.JD.getId());
                    dsOrderCount.setOrderNumber(orderCount);
                    dsOrderCount.setAppName(query.getAppName());
                    dsOrderCount.setAppVersion(query.getAppVersion());
                    if (query.getMerchantId() != null) {
                        dsOrderCount.setMerchantId(Integer.parseInt(query.getMerchantId()));
                    }
                    dsOrderCount.setPlatform(query.getPlatform());
                    dsOrderCountService.save(dsOrderCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (query instanceof TaoBaoLoginQuery) {
            TaoBaoLoginQuery taoBaoLoginQuery = (TaoBaoLoginQuery) query;
            checkStatus.updateCheckStatus(crawlerPage.getJob(), userId, query.getMerchantId());
            addOrderNumber(crawlerPage);
            if (checkStatus.finished(Job.TAOBAO, userId, query.getMerchantId())) {
                CrawlerCookie crawlerCookie = cookieManager.getCookie(key);
                if (crawlerCookie.getStatusId() != 0) {
                    ApiWd.updateStatus(crawlerCookie.getStatusId(), NoticeUtils.CrawlerStatus.DONE.getStatus());
                }
                NoticeUtils.notice(Module.TAOBAO.getModuleName(), query.getNoticeUrl(), userId,
                        NoticeUtils.CrawlerStatus.DONE.getStatus(), null);
                int tbOrderCount = orderCountMap.get(getKey(userId, OrderSource.TAOBAO.getSource(), query.getMerchantId()));
                int alipayOrderCount = orderCountMap.get(getKey(userId, OrderSource.ALIPAY.getSource(), query.getMerchantId()));
                orderCountMap.remove(getKey(userId, OrderSource.TAOBAO.getSource(), query.getMerchantId()));
                orderCountMap.remove(getKey(userId, OrderSource.ALIPAY.getSource(), query.getMerchantId()));
                try {
                    /*保存淘宝订单数量*/
                    DSOrderCount dsOrderCount = new DSOrderCount();
                    if (userId.startsWith("0031_")) {
                        dsOrderCount.setUid(userId.split("_")[1]);
                    } else {
                        dsOrderCount.setUid(userId);
                    }
                    dsOrderCount.setLoginName(taoBaoLoginQuery.getLoginName());
                    dsOrderCount.setOrderSource(OrderSource.TAOBAO.getId());
                    dsOrderCount.setOrderNumber(tbOrderCount);
                    dsOrderCount.setAppName(query.getAppName());
                    dsOrderCount.setAppVersion(query.getAppVersion());
                    if (query.getMerchantId() != null) {
                        dsOrderCount.setMerchantId(Integer.parseInt(query.getMerchantId()));
                    }
                    dsOrderCount.setPlatform(query.getPlatform());
                    dsOrderCountService.save(dsOrderCount);

                    /*保存支付宝订单数量*/
                    dsOrderCount.setOrderSource(OrderSource.ALIPAY.getId());
                    dsOrderCount.setOrderNumber(alipayOrderCount);
                    dsOrderCount.setAppName(query.getAppName());
                    dsOrderCount.setAppVersion(query.getAppVersion());
                    if (query.getMerchantId() != null) {
                        dsOrderCount.setMerchantId(Integer.parseInt(query.getMerchantId()));
                    }
                    dsOrderCount.setPlatform(query.getPlatform());
                    dsOrderCountService.save(dsOrderCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addOrderNumber(CrawlerPage crawlerPage) {
        Query query = crawlerPage.getUriData().getQuery();
        String uid = "";
        int orderCount = 0;
        if (query instanceof JingdongLoginQuery) {
            uid = query.getUserId();
            String data = assemblyJingdongOrder(crawlerPage);
            JSONObject dataJson = JSONObject.fromObject(data);
            orderCount = dataJson.getJSONArray("orderList").size();
            addOrderNumber(getKey(uid, OrderSource.JD.getSource(), query.getMerchantId()), orderCount);
        } else if (query instanceof TaoBaoLoginQuery) {
            uid = query.getUserId();
            TaobaoOrderStore taobaoOrderStore = new TaobaoOrderStore();
            JSONObject data = taobaoOrderStore.formatData(crawlerPage);
            orderCount = data.getJSONArray("orderList").size();
            addOrderNumber(getKey(uid, OrderSource.TAOBAO.getSource(), query.getMerchantId()), orderCount);
            int alipayOrderCount = data.getJSONArray("alipayList").size();
            addOrderNumber(getKey(uid, OrderSource.ALIPAY.getSource(), query.getMerchantId()), alipayOrderCount);
        }
    }

    public void addOrderNumber(String key, int count) {
        if (orderCountMap.containsKey(key)) {
            orderCountMap.put(key, count + orderCountMap.get(key));
        } else {
            orderCountMap.put(key, count);
        }
    }

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

    public String getKey(String uid, String orderSource, String merchantId) {
        return uid + "_" + orderSource + "_" + merchantId;
    }
}
