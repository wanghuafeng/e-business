package com.rong360.crawler.ds.processor.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.MetaData;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.ds.bean.HtmlBean;
import com.rong360.crawler.ds.bean.Order;
import com.rong360.crawler.ds.query.impl.JingdongLoginQuery;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.AbstractProcessor;
import com.rong360.crawler.processor.extractor.IExtractor;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.uri.filter.impl.BloomUriUniqFilter;
import com.rong360.crawler.util.CrawlerBeanPopulate;
import com.rong360.crawler.util.HtmlUtils;
import com.rong360.crawler.util.RegExpUtil;
import com.rong360.crawler.util.URIUtils;


/**
 * @author xiongwei
 * @ClassName: JingdongOrderExtractor
 * @Description:京东订单解析器
 * @date 2015-3-20 上午11:34:06
 */
public class JingdongOrderExtractor extends AbstractProcessor implements IExtractor {

    {
        this.job = Job.JD;
        this.task = Task.ORDER;
    }

    /*****
     * 统计登录账号订单情况
     *****/
    private Map<String, AtomicInteger> mapAccount = new HashMap<String, AtomicInteger>();


    /*****
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(JingdongOrderExtractor.class);

    /*****
     * JD 订单正则表达式属性
     *****/
    private Map<String, String> orderPropertiesRegxMap = new HashMap<String, String>();

    /*****
     * 商品清单列表
     *****/
    private String pruductBlockRegx = "(?:<dd class=\"p-list\">|<table class=\"tb-void tb-none\">)(.*?)(?:</table>)";

    /*****
     * 商品名称列表
     *****/
    private String pruductNameRegx = "(?:<a.*?href=(?:'|\")(?:http://www.360buy.com/product/\\d+.html|http://item.jd.com/\\d+.html).*?(?:'|\").*?>)(.*?)(?:</a>)";


    /*****
     * 布隆过滤器
     *****/
    @SuppressWarnings("unused")
    private BloomUriUniqFilter bloomUriUniqFilter;


    public void setBloomUriUniqFilter(BloomUriUniqFilter bloomUriUniqFilter) {
        this.bloomUriUniqFilter = bloomUriUniqFilter;
    }

    {

        /*****订单号 *****/
        orderPropertiesRegxMap.put("orderId", "(?:<strong>订单编号：|<div class=fl>订单号：|订单编号</td>.*?<td>|订单号：)(\\d+)(?:</td>|&nbsp| &nbsp;)");


        /*****收货人 *****/
        orderPropertiesRegxMap.put("receiver", "(?:<td>收货人姓名</td>.*?<td>|<li>收&nbsp;货&nbsp;人：)(.*?)(?:</td>|</li>)");

        /*****订单金额 *****/
        orderPropertiesRegxMap.put("money", "(?:<li>应付总额：.*?￥|商品总金额：</span>￥|订单总金额：<strong class=\"ftx-01\">|总商品金额：</span>￥|应付总额：.*?&yen;)(.*?)(?:</strong>|</li>|</b></span>|</li>.*?<li><span>-余额)");


        /*****支付方式*****/
        orderPropertiesRegxMap.put("buyway", "(?:<td>支付方式</td>.*?<td>|<li>支付方式：)(.*?)(?:</li>|</td>)");

        /*****购买时间 *****/
        orderPropertiesRegxMap.put("buyTime", "(\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2})");

        /*****订单状态 *****/
        orderPropertiesRegxMap.put("orderstatus", "(?:状态：<span class=\"ftx-02\">|<div class=\"mt\">.*?<h3>|状态：<span class=\"ftx14\">)(.*?)(?:订单</h3>|</span>|订单</h3>|</span></div)");


        /*****收货地址 *****/
        orderPropertiesRegxMap.put("receiverAddr", "(?:<td>地址</td>.*?<td>|<li>地&nbsp;&nbsp;&nbsp;&nbsp;址：)(.*?)(?:</li>|</td>)");

        /*****固定电话*****/
        orderPropertiesRegxMap.put("receiverFixPhone", "(?:td>固定电话</td>.*?<td>|<li>固定电话：)(.*?)(?:手机号码|</li>)");


        /*****手机号 *****/
        orderPropertiesRegxMap.put("receiverTelephone", "(?:手机号码：|<li>手机号码：)(.*?)(?:</td>|</li>)");


        /*****email *****/
        orderPropertiesRegxMap.put("receiverEmail", "(?:<td>电子邮件</td>.*?<td>|<li>电子邮件：)(.*?)(?:</td>|</li>)");
    }

    @Override
    public void process(CrawlerPage crawlerPage) {

        log.info("JingdongOrderExtractor is processing...... " + crawlerPage.getUriData().getStrUri());

        /*****网页源代码 *****/
        String sourceCode = crawlerPage.getSourceData().getSourceCode();

        final Source source = new Source(sourceCode);

        List<StartTag> linkElements = source
                .getAllStartTags(HTMLElementName.A);

        /*****提取订单所有下一页链接*****/
        List<String> nextPageList = new ArrayList<String>();

        if (linkElements.size() > 0) {
            for (final StartTag link : linkElements) {
                String href = link.getAttributeValue("href");
                if (href != null && href.matches("(http:)?//order.jd.com/center/list.action\\?d=\\d+&s=\\d+&t=&keyword=&search=\\d+&page=\\d+")) {
                    if (!href.startsWith("http")) {
                        href = "http:" + href;
                    }
                    if (!nextPageList.contains(href)) {
                        nextPageList.add(href);
                    }
                }
            }
        }

        /*****解析第一页订单数据*****/
        extractorList(crawlerPage.getUriData().getStrUri(), crawlerPage);

        /*****解析订单所有下一页数据*****/
        for (String href : nextPageList) {
            sourceCode = getNewSourceCode(href, crawlerPage, false).getSourceCode();
            crawlerPage.getSourceData().setSourceCode(sourceCode);
            extractorList(href, crawlerPage);
        }


        /*****登录用户名 *****/
        String loginName = "";
        Query query = crawlerPage.getUriData().getQuery();
        if (query instanceof JingdongLoginQuery) {
            JingdongLoginQuery loginQuery = (JingdongLoginQuery) query;
            loginName = loginQuery.getLoginName();
        }

        if (crawlerPage.getMetaData().getCrawlerBeans().size() > 0) {
            log.info(this.getClass().getName() + ":"
                    + crawlerPage.getUriData().getStrUri() + ", loginName:" + loginName + ", "
                    + crawlerPage.getMetaData().getCrawlerBeans().size()
                    + " order has crawlered");
        } else {
            log.warn(this.getClass().getName() + ":"
                    + crawlerPage.getUriData().getStrUri() + ", "
                    + crawlerPage.getMetaData().getCrawlerBeans().size()
                    + " order has crawlered");
        }
    }

    /**
     * 从订单列表页提取所有订单链接
     *
     * @param url
     * @param crawlerPage
     */
    public void extractorList(String url, CrawlerPage crawlerPage) {

        /*****登录用户名 *****/
        String loginName = "";
        Query query = crawlerPage.getUriData().getQuery();
        if (query instanceof JingdongLoginQuery) {
            JingdongLoginQuery loginQuery = (JingdongLoginQuery) query;
            loginName = loginQuery.getLoginName();
        }

        /*****网页源代码 *****/
        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        for (int i = 0; i < 3; i++) {
            if (sourceCode.contains("//order.jd.com/normal/item.action?orderid=") || sourceCode.contains("//order.jd.com/center/itemPage.action?orderid=")) {
                mapAccount.remove(loginName);
                break;
            } else if (sourceCode.contains("暂无订单，这就去挑选商品") || sourceCode.contains("最近没有下过订单哦")) {
                if (!mapAccount.containsKey(loginName)) {
                    mapAccount.put(loginName, new AtomicInteger(1));
                } else {
                    mapAccount.get(loginName).addAndGet(1);
                    if (mapAccount.get(loginName).get() == 5) {
                        log.info("京东小白用户, loginName:" + loginName);
                        mapAccount.remove(loginName);
                    }
                }
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sourceCode = getNewSourceCode(url, crawlerPage, false).getSourceCode();
        }


        final Source source = new Source(sourceCode);

        List<StartTag> linkElements = source
                .getAllStartTags(HTMLElementName.A);

        /*****提取所有订单链接*****/
        List<String> orderList = new ArrayList<String>();

        if (linkElements.size() > 0) {
            for (final StartTag link : linkElements) {
                String href = link.getAttributeValue("href");
                if (href != null && href.matches("(http:)?//order.jd.com/(center|normal)/(itemPage.action|item.action)\\?orderid=\\d+&PassKey=\\w{32}")) {
                    if (!href.startsWith("http")) {
                        href = "http:" + href;
                    }
                    if (!orderList.contains(href)) {
                        orderList.add(href);
                    }
                }
            }
        }
        extractorOrder(orderList, crawlerPage);
    }

    /**
     * 从订单详情页提取订单详情
     *
     * @param orderList
     * @param crawlerPage
     */
    public void extractorOrder(List<String> orderList, CrawlerPage crawlerPage) {

        /*****登录用户名 *****/
        String loginName = "";
        Query query = crawlerPage.getUriData().getQuery();
        if (query instanceof JingdongLoginQuery) {
            JingdongLoginQuery loginQuery = (JingdongLoginQuery) query;
            loginName = loginQuery.getLoginName();
        }

        /*****备用订单链接*****/
        List<String> backOrderList = new ArrayList<String>();


        for (String orderURL : orderList) {
            for (int i = 0; i <= 2; i++) {
                /*****JD 订单属性提取值*****/
                Map<String, String> orderPropertiesMap = new HashMap<String, String>();

                /*****按块分隔每一个订单*****/
                HtmlBean htmlBean = getNewSourceCode(orderURL, crawlerPage, true);
                String orderDetail = htmlBean.getSourceCode();

                /*****商品清单列表*****/
                Matcher productListMatcher = RegExpUtil.getMatcher(orderDetail, pruductBlockRegx);
                if (productListMatcher.find()) {
                    String productList = productListMatcher.group();
                    Matcher productNameMatcher = RegExpUtil.getMatcher(productList, pruductNameRegx);
                    /*****商品名称列表*****/
                    String productNames = "";
                    while (productNameMatcher.find()) {
                        String name = HtmlUtils.escapeHtmlTag(productNameMatcher.group(1));
                        if (StringUtils.isNotBlank(name)) {
                            productNames += name + ";";
                        }
                    }
                    if (StringUtils.isNotBlank(productNames)) {
                        productNames = productNames.substring(0, productNames.lastIndexOf(";"));
                        orderPropertiesMap.put("productNames", productNames);
                    }
                }

                /*****按照每一个属性提取其值*****/
                for (Map.Entry<String, String> entry : orderPropertiesRegxMap.entrySet()) {
                    String propertyName = entry.getKey();
                    String propertyValue = entry.getValue();
                    Matcher propertyMatcher = RegExpUtil.getMatcher(orderDetail, propertyValue);
                    if (propertyMatcher.find()) {
                        String value = HtmlUtils.escapeHtmlTag(propertyMatcher.group(1));
                        orderPropertiesMap.put(propertyName, value);
                    } else {
                        crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add(propertyName);
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final Source source = new Source(orderDetail);
                List<StartTag> linkElements = source
                        .getAllStartTags(HTMLElementName.A);
                if (linkElements.size() > 0) {
                    for (final StartTag link : linkElements) {
                        String href = link.getAttributeValue("href");
                        if (href != null && href.matches("(http:)?//order.jd.com/(center|normal)/(itemPage.action|item.action)\\?orderid=\\d+&PassKey=\\w{32}")) {
                            if (!href.startsWith("http")) {
                                href = "http:" + href;
                            }
                            if (!backOrderList.contains(href)) {
                                backOrderList.add(href);
                            }
                        }
                    }
                }
                if (orderPropertiesMap.containsKey("orderId")) {
                    Order order = new Order();
                    order.setOrdersource("JD");
                    order.setLoginName(loginName);
                    CrawlerBeanPopulate.copyProperties(order, orderPropertiesMap);
                    orderPropertiesMap.clear();
                    order.setHbaseKey(htmlBean.getHbaseKey());
                    crawlerPage.getMetaData().getCrawlerBeans().add(order);
                    log.info("[orderURL:" + orderURL + "]" + order.getAssemleBean());
                    break;
                }
            }
        }

        for (String orderURL : backOrderList) {
            for (int i = 0; i <= 2; i++) {
                /*****JD 订单属性提取值*****/
                Map<String, String> orderPropertiesMap = new HashMap<String, String>();

                /*****按块分隔每一个订单*****/
                HtmlBean htmlBean = getNewSourceCode(orderURL, crawlerPage, true);
                String orderDetail = htmlBean.getSourceCode();

                /*****商品清单列表*****/
                Matcher productListMatcher = RegExpUtil.getMatcher(orderDetail, pruductBlockRegx);
                if (productListMatcher.find()) {
                    String productList = productListMatcher.group();
                    Matcher productNameMatcher = RegExpUtil.getMatcher(productList, pruductNameRegx);
                    /*****商品名称列表*****/
                    String productNames = "";
                    while (productNameMatcher.find()) {
                        String name = HtmlUtils.escapeHtmlTag(productNameMatcher.group(1));
                        if (StringUtils.isNotBlank(name)) {
                            productNames += name + ";";
                        }
                    }
                    if (StringUtils.isNotBlank(productNames)) {
                        productNames = productNames.substring(0, productNames.lastIndexOf(";"));
                        orderPropertiesMap.put("productNames", productNames);
                    }
                }

                /*****按照每一个属性提取其值*****/
                for (Map.Entry<String, String> entry : orderPropertiesRegxMap.entrySet()) {
                    String propertyName = entry.getKey();
                    String propertyValue = entry.getValue();
                    Matcher propertyMatcher = RegExpUtil.getMatcher(orderDetail, propertyValue);
                    if (propertyMatcher.find()) {
                        String value = HtmlUtils.escapeHtmlTag(propertyMatcher.group(1));
                        orderPropertiesMap.put(propertyName, value);
                    } else {
                        crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add(propertyName);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (orderPropertiesMap.containsKey("orderId")) {
                    Order order = new Order();
                    order.setOrdersource("JD");
                    order.setLoginName(loginName);
                    CrawlerBeanPopulate.copyProperties(order, orderPropertiesMap);
                    orderPropertiesMap.clear();
                    order.setHbaseKey(htmlBean.getHbaseKey());
                    crawlerPage.getMetaData().getCrawlerBeans().add(order);
                    log.info("[orderURL:" + orderURL + "]" + order.getAssemleBean());
                    break;
                }
            }
        }
    }

    /***
     * 获取订单HTML页面内容
     *
     * @param url
     * @return
     */
    public HtmlBean getNewSourceCode(String url, CrawlerPage crawlerPage, boolean isSave) {
        UriData uriData = new UriData(0, URIUtils.getHttpURL(url));
        CrawlerPage cp = new CrawlerPage();
        uriData.setDefaultCharset("GB18030");
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.getUriData().setCookieString(crawlerPage.getUriData().getCookieString());
        cp.getUriData().setSave(isSave);
        cp.getUriData().setId(crawlerPage.getUriData().getId());
        cp.setJob(Job.JD);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        return new HtmlBean(cp.getSourceData().getSourceCode(), cp.getUriData().getHbaseParam().getKey());
    }
}
