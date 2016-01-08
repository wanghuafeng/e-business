package com.rong360.crawler.ds.processor.extractor;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rong360.crawler.CrawlerConfigurableProperties;
import com.rong360.crawler.bean.*;
import com.rong360.crawler.ds.bean.HtmlBean;
import com.rong360.crawler.ds.bean.TbAddrBean;
import com.rong360.crawler.ds.bean.TbInfoBean;
import com.rong360.crawler.ds.query.impl.TaoBaoVerifyMsgQuery;
import com.rong360.crawler.processor.store.LoggerProcessor;
import net.htmlparser.jericho.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rong360.crawler.ds.bean.Order;
import com.rong360.crawler.ds.query.impl.TaoBaoLoginQuery;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.AbstractProcessor;
import com.rong360.crawler.processor.extractor.IExtractor;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.uri.filter.impl.BloomUriUniqFilter;
import com.rong360.crawler.util.HtmlUtils;
import com.rong360.crawler.util.RegExpUtil;
import com.rong360.crawler.util.URIUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiongwei
 */
public class TaobaoOrderExtractor extends AbstractProcessor implements IExtractor {

    {
        this.job = Job.TAOBAO;
        this.task = Task.ORDER;
    }

    private final static int MAX_CRAWLER_PAGE_NUM = 5;

    private List<String> visitedOrderUrls = new ArrayList<>();

    private final static int MAX_PAGE_SIZE = 15;

    @Autowired
    private AlipayOrderExtractor alipayOrderExtractor;

    /*****
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(JingdongOrderExtractor.class);

    /*****
     * TAOBAO 订单正则表达式属性
     *****/
    private Map<String, String> orderPropertiesRegexMap = new HashMap<>();


    /*****
     * TAOBAO 订单正则表达式属性
     *****/
    private Map<String, String> bakOrderPropertiesRegexMap = new HashMap<>();

    /*淘宝个人信息*/
    private Map<String, String> tbLevelRegexMap = new HashMap<>();
    private Map<String, String> accountSecurityRegexMap = new HashMap<>();
    private Map<String, String> baseInfoRegexMap = new HashMap<>();
    private Map<String, String> vipGrowthRegexMap = new HashMap<>();
    private Map<String, String> accountManagementRegexMap = new HashMap<>();
    private Map<String, String> deliverAddressRegexMap = new HashMap<>();
    private Map<String, String> tianMaoPropertiesRegexMap = new HashMap<>();
    private Map<String, String> tbCoinRegexMap = new HashMap<>();

    /*****
     * 商品清单列表
     *****/
//	private static String productBlockRegex = "(?:class=\"misc-info\">|order-info\">|class=\"trade-status\">|class=\"trade-detail-prompt\">|class=\"order-table\">|class=\"bought-listform-maincontent\")(.*?)(?:我对购买流程有意见或建议|var p4p_ip|window.p4p_spm|window.p4p_itemid|</table>)";
    private final static String productBlockRegex = "(?:class=\"misc-info\">|order-info\">|class=\"trade-status\">|class=\"order-table\">|class=\"bought-listform-maincontent\")(.*?)(?:我对购买流程有意见或建议|var p4p_ip|window.p4p_spm|window.p4p_itemid|</table>)";

    /*****
     * 商品名称列表
     *****/
    private final static String productNameRegex = "(?:(?:<a.*?href=(?:'|\")//(?:tradearchive|trade).taobao.com/trade/detail/(?:trade_snap.htm|tradeSnap.htm)\\?trade_id=\\d+(?:'|\").*?>)|href=\"//waimai.taobao.com/item.htm\\?id=\\d+&amp;city=\\d+\">)(.*?)(?:</a>)";


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
        orderPropertiesRegexMap.put("orderId", "(?:订单号：|class=\"trade-imfor-dd imfor-short-dd\">|<dt>订单编号:|订单编号：</span>|class=\"order-num\">|淘宝交易号：)(.*?)(?:</span>|</dd>|</div)");
        /*****收货人 *****/
        orderPropertiesRegexMap.put("receiver", "(?:data-show-address=\"false\">|收货地址.*?：|乘客姓名：</dt>.*?class=\"name\">|买家昵称：)(.+?)(?:<td></td>|，|</dd>|</div>|</span>)");
        /*****订单金额 *****/
        orderPropertiesRegexMap.put("money", "(?:实付款：|订单总金额：|实付款.*?&yen;</span><em>|实际票价：|购买总价：.*?&yen;</span>|人均消费)(.*?)(?:</strong>元|元|</em>.*?</strong>)");
        /*****支付方式*****/
        orderPropertiesRegexMap.put("buyway", "(?:<td>支付方式</td>.*?<td>|<li>支付方式：)(.*?)(?:</li>|</td>)");
        /*****购买时间 *****/
        orderPropertiesRegexMap.put("buyTime", "(?:成交时间|出票时间：|下单时间：</span>).*?(\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}).*?");
        /*****订单状态 *****/
        orderPropertiesRegexMap.put("orderstatus", "(?:.*?订单状态：)(.*?)(?:</h3>|</strong>|</dd>|</li>|</span>)");
        /*****收货地址 *****/
        orderPropertiesRegexMap.put("receiverAddr", "(?:data-show-address=\"false\">|收货地址.*?：|收货地址：</span>)(.+?)(?:</dd>|</li>|<td class=\"label\">|<li class=\"table-list|</div>)");
        /*****固定电话*****/
//        bakOrderPropertiesRegexMap.put("receiverFixPhone", "((?:0[0-9]{2,3}\\-)+(?:[2-9][0-9]{6,7})+(?:\\-[0-9]{1,4})?)");
        /*****手机号 *****/
        bakOrderPropertiesRegexMap.put("receiverTelephone", "((?:0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8})");
        /*****邮编 *****/
        bakOrderPropertiesRegexMap.put("receiverPost", "(\\d{6})$");

        tbLevelRegexMap.put("taobaoLevel", "买家累积信用：.*?>(\\d+)</a>");
//        tbLevelRegexMap.put("buyerLevel", "class=\"vip-icon vip-icon-(\\d+)\"");
        tbLevelRegexMap.put("buyerPositive", "好评率：.*?<strong>(.*?)%</strong>");

        accountSecurityRegexMap.put("lastLoginTime", "上&nbsp;次&nbsp;登&nbsp;录：(.*?)<font");

        baseInfoRegexMap.put("birthdayYear", "生日：.*?年.*?selected=\"selected\".*?>(\\d{4})</option>");
        baseInfoRegexMap.put("birthdayMonth", "生日：.*?月.*?selected=\"selected\".*?>(\\d{1,2})</option>");
        baseInfoRegexMap.put("birthdayDay", "生日：.*?日.*?selected=\"selected\".*?>(\\d{1,2})</option>");
        baseInfoRegexMap.put("residence", "id=\"divisionCode\" value=\"(\\d{6})\"");
        baseInfoRegexMap.put("hometown", "value=\"(\\d{6})\" id=\"liveDivisionCode\"");

        vipGrowthRegexMap.put("growthNumber", "vipCount\":(\\d+),");

        accountManagementRegexMap.put("alipayName", "<span class=\"kv_label\">支付宝账户：</span>(.*?)</div>");
        accountManagementRegexMap.put("accountType", "<span class=\"kv_label\">账户类型：</span>(.*?)</div>");
        accountManagementRegexMap.put("accountStatus", "<span class=\"kv_label\">账户状态：</span>(.*?)</div>");
        accountManagementRegexMap.put("isRelated", "<span class=\"kv_label\">绑定状态：</span>(.*?)<a");
        accountManagementRegexMap.put("mobile", "<span class=\"kv_label\">手机号码：</span>(.*?)<a");
        accountManagementRegexMap.put("accountRemain", "<span class=\"kv_label\">可用余额：</span>(.*?)<a");

        deliverAddressRegexMap.put("postCode", "<tr class=\"thead-tbl-address  address-default \">.*?<td>(\\d{6})</td>");
        deliverAddressRegexMap.put("phoneNumber", "<tr class=\"thead-tbl-address  address-default \">.*?<td>.*?(\\d+-\\d+\\*+\\d+).*?</td>");

        tbCoinRegexMap.put("taobaoCoins", "href=\"/coin/userCoinDetail.htm\" target=\"_blank\" data-spm=\"\\d+\">(\\d+)</a>");

        tianMaoPropertiesRegexMap.put("tianmaoAccount", "class=\"title\" title=\"(.*?)\"");
        tianMaoPropertiesRegexMap.put("tianmaoPoints", "我的积分:.*?(\\d+)</span>");
        tianMaoPropertiesRegexMap.put("tianmaoLevel", "您是.*?T(\\d+).*?会员");
        tianMaoPropertiesRegexMap.put("tianmaoExperience", "当前经验：.*?(\\d+)</strong></li>");
        tianMaoPropertiesRegexMap.put("huaBei", "权益消费额度：(.*?)元");
        tianMaoPropertiesRegexMap.put("buyDays", "当前天数：<strong>(\\d+)</strong>天");
    }

    @Override
    public void process(CrawlerPage crawlerPage) {

        log.info("TaobaoExtractor is processing...... " + crawlerPage.getUriData().getStrUri());

        // 网页源代码
        String sourceCode = crawlerPage.getSourceData().getSourceCode();
//        log.info("proxy ip :" + crawlerPage.getProxy().getProxyIp().toString());
//		log.info(sourceCode);
        List<CrawlerBean> crawlerBeans = new ArrayList<>();

        // 提取淘宝个人信息
        TbInfoBean tbInfoBean = getTaoBaoInfo(crawlerPage);
        tbInfoBean.setLoginName(getLoginName(crawlerPage));
        crawlerBeans.add(tbInfoBean);
        System.out.println("tbInfo:");
        System.out.println(tbInfoBean.getAssemleBean());

        //提取淘宝常用收件人地址
        List<Map<String, String>> addrs = getAddrs(crawlerPage);
        System.out.println("addrs:");
        for (Map<String, String> map : addrs) {
            alipayOrderExtractor.addBean(crawlerBeans, new TbAddrBean(), map);
            System.out.println(map);
        }

        // 解析支付宝数据
        crawlerBeans.addAll(alipayOrderExtractor.extractor(crawlerPage.getUriData().getCookieString(),
                getLoginName(crawlerPage), crawlerPage.getProxy().getProxyIp()));

        //提取所有订单的url
        List<Order> orders = analyseListPage(sourceCode, crawlerPage);
        crawlerBeans.addAll(orders);
        crawlerPage.getMetaData().setCrawlerBeans(crawlerBeans);
        log.info("taobao orderlist: total " + orders.size());
    }

    /**
     * <p>解析订单列表页</p>
     * <p>获得所有订单的url</p>
     *
     * @param page 订单列表页
     * @return 订单列表
     */
    private List<Order> analyseListPage(String page, CrawlerPage crawlerPage) {
        List<Order> orders = new ArrayList<>();
        String dataRegx = "var data = (.*)";
        Pattern pattern = Pattern.compile(dataRegx);
        Matcher dataMatcher = pattern.matcher(page);
        String orderListUrl = "https://buyertrade.taobao.com/trade/itemlist/asyncBought.htm?action=itemlist/BoughtQueryAction&event_submit_do_query=1&_input_charset=utf8";
        if (dataMatcher.find()) {
            String dataStr = dataMatcher.group(1).trim();
            //从网页中提取数据
            JSONObject data = JSONObject.fromObject(dataStr);
            int totalPage = data.getJSONObject("page").getInt("totalPage");
            int totalNumber = data.getJSONObject("page").getInt("totalNumber");
            int pageSize = data.getJSONObject("page").getInt("pageSize");
            int currentPage = data.getJSONObject("page").getInt("currentPage");

            //根据MAX_PAGE_SIZE重新设定pageSize和totalPage，
            //使得一个列表页显示更多的订单，
            //以提高抓取效率和降低被淘宝识别几率
            pageSize = MAX_PAGE_SIZE;
            totalPage = (totalNumber % MAX_PAGE_SIZE == 0) ? (totalNumber / MAX_PAGE_SIZE) : (totalNumber / MAX_PAGE_SIZE + 1);

            //从列表数据中提取所有的orderUrl
            List<String> orderUrl = extractOrderUrl(data);
            orders.addAll(extractOrderDetail(orderUrl, crawlerPage));
            JSONObject query = data.getJSONObject("query");
            Map<String, String> params = getMapFrom(query);
            if (totalPage > MAX_CRAWLER_PAGE_NUM) {
                totalPage = MAX_CRAWLER_PAGE_NUM;
            }
            for (int i = 1; i <= totalPage; i++) {
                params.put("prePageNo", Integer.toString(i - 1));
                params.put("pageSize", Integer.toString(pageSize));
                params.put("pageNum", Integer.toString(i));
                crawlerPage.getUriData().setParams(params);
                HtmlBean htmlBean = getNewSourceCode(orderListUrl, crawlerPage, HttpMethod.POST);
                page = htmlBean.getSourceCode();
//                log.info("page:" + page);
                try {
                    data = JSONObject.fromObject(page);
                    if (data.isNullObject() || data.isEmpty()) {
                        continue;
                    }
                    params = getMapFrom(data.getJSONObject("query"));
                    orderUrl = extractOrderUrl(data);
                    orders.addAll(extractOrderDetail(orderUrl, crawlerPage));
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        } else {
            crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add("orderList");
        }
        return orders;
    }

    /**
     * <p>从淘宝返回的json中提取所有订单url</p>
     *
     * @param data JSONObject 订单数据
     * @return List<String> 订单url列表
     */
    private List<String> extractOrderUrl(JSONObject data) {
        List<String> orderUrl = new ArrayList<>();
        try {
            JSONArray array = data.getJSONArray("mainOrders");
            for (Object order : array) {
                JSONArray operations = ((JSONObject) order).getJSONObject("statusInfo").getJSONArray("operations");
                for (Object operation : operations) {
                    //获取订单详情的url
                    if ("订单详情".equals(((JSONObject) operation).getString("text"))) {
                        String url = ((JSONObject) operation).getString("url");
                        if (StringUtils.isNotBlank(url)) {
                            url = "https:" + url;
                            if (!visitedOrderUrls.contains(url)) {
                                orderUrl.add(url);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return orderUrl;
    }

    /**
     * 从所给的orderUrlList中提取所有的订单信息，已解析过的orderUrl将不会被访问
     *
     * @param urlList     订单链接list
     * @param crawlerPage 上下文信息
     * @return 返回所有解析到的订单
     */
    private List<Order> extractOrderDetail(List<String> urlList, CrawlerPage crawlerPage) {
        List<Order> orders = new ArrayList<>();
        for (String url : urlList) {
            //不访问空的url或已访问过的url
            if (StringUtils.isBlank(url) || visitedOrderUrls.contains(url)
                    || "https".equals(url) || "http".equals(url)) {
                continue;
            }

            //解析订单
            Order order = extractOrderDetail(url, crawlerPage);

            //将解析过的订单url加入到已访问的订单列表中
            visitedOrderUrls.add(url);
            
            /*如果订单不为空并且包含orderId，则设置登录名和orderSource，并且添加到订单列表orders中
            * 否则发出警告*/
            if (order != null && order.getOrderId() != null && !order.getOrderId().isEmpty()) {
                order.setLoginName(getLoginName(crawlerPage));
                order.setOrdersource("TAOBAO");
                orders.add(order);
            } else {
                log.warn("empty order url : " + url);
            }
        }
        return orders;
    }

    /**
     * 从所给的orderUrl中提取所有的订单信息
     *
     * @param orderUrl    订单链接
     * @param crawlerPage 上下文信息
     * @return 返回解析到的订单
     */
    private Order extractOrderDetail(String orderUrl, CrawlerPage crawlerPage) {
        HtmlBean htmlBean = getNewSourceCode(orderUrl, crawlerPage, HttpMethod.GET);
        if (orderUrl.contains("train.trip")) {
            return extractTrainOrder(htmlBean);
        } else {
            return extractOrderDetail(htmlBean);
        }
    }

    /**
     * 从给定的页面中解析订单
     *
     * @param htmlBean 页面实体
     * @return 返回解析到的订单
     */
    public Order extractOrderDetail(HtmlBean htmlBean) {
        Map<String, String> orderPropertiesMap = new HashMap<>();
        String orderDetail = htmlBean.getSourceCode();
        //如果页面内容为空，返回空订单
        if (orderDetail == null || orderDetail.isEmpty()) {
            return new Order();
        }

        //提取商品名，用";"分隔
        List<String> products = extractProductNames(orderDetail, productBlockRegex, productNameRegex);
        if (products.size() > 0) {
            orderPropertiesMap.put("productNames", join(";", products));
        }


        CrawlerPage crawlerPage = new CrawlerPage();
        //按照每一个属性提取其值
        for (Map.Entry<String, String> entry : orderPropertiesRegexMap.entrySet()) {
            String propertyName = entry.getKey();
            String propertyValue = entry.getValue();
            Matcher propertyMatcher = RegExpUtil.getMatcher(orderDetail, propertyValue);
            if (propertyMatcher.find()) {
                String value = HtmlUtils.escapeHtmlTag(propertyMatcher.group(1));
                if ("receiverAddr".equalsIgnoreCase(propertyName)) {
                    //如果是收货地址，则尝试从中提取手机号和固定电话
                    String[] addrs = value.split("，");
                    if (addrs.length >= 2) {
                        //提取淘宝手机、固定电话
                        extractInfo(orderPropertiesMap, value, bakOrderPropertiesRegexMap);
                        value = addrs[addrs.length - 2] + "," + addrs[addrs.length - 1];
                    }
                    orderPropertiesMap.put(propertyName, value);
                } else if ("buyTime".equals(propertyName)) {
                    //如果是购买时间，则将其转成时间戳，单位为ms
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    try {
                        value = Long.toString(sdf.parse(value).getTime());
                    } catch (ParseException e) {
                        log.warn(e.getMessage());
                        e.printStackTrace();
                    }
                    orderPropertiesMap.put(propertyName, value);
                } else if ("receiver".equals(propertyName)) {
                    //如果是收货人，提取出正确的收货人
                    if (StringUtils.isNotBlank(value)) {
                        String[] data = value.split(" ");
                        if (data.length == 4) {
                            value = data[2];
                        } else if (data.length == 3) {
                            value = data[0];
                        }
                    }
                    orderPropertiesMap.put(propertyName, value);
                } else {
                    orderPropertiesMap.put(propertyName, value);
                }
            } else {
                crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add(entry.getKey());
            }
        }
        return Order.from(orderPropertiesMap, htmlBean.getHbaseKey());
    }

    public Order extractTrainOrder(HtmlBean htmlBean) {
        Map<String, String> regexMap = new HashMap<>();
        regexMap.put("orderId", "订单号：</dt>(.*?)</dd>");
        regexMap.put("receiver", "<dt>姓名：</dt>(.*?)</dd>");
        regexMap.put("money", "订单总金额：</dt>.*?&yen;(.*?)</span>");
        regexMap.put("buyway", "(?:<td>支付方式</td>.*?<td>|<li>支付方式：)(.*?)(?:</li>|</td>)");
        regexMap.put("buyTime", "(?:成交时间|出票时间：|下单时间：</span>).*?(\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}).*?");
        regexMap.put("orderstatus", "订单状态：</dt>(.*?)<a");
        regexMap.put("receiverTelephone", "手机号码：</dt>(.*?)</dd>");

        String orderDetail = htmlBean.getSourceCode();
        //如果页面内容为空，返回空订单
        if (orderDetail == null || orderDetail.isEmpty()) {
            return new Order();
        }

        Map<String, String> orderPropertiesMap = new HashMap<>();
        extractInfo(orderPropertiesMap, orderDetail, regexMap);

        Map<String, String> productInfoRegexMap = new HashMap<>();
        productInfoRegexMap.put("startStation", "class=\"train-depart\">.*?class=\"station\">(.*?)</span>");
        productInfoRegexMap.put("arriveStation", "class=\"train-arrive\">.*?class=\"station\">(.*?)</span>");
        productInfoRegexMap.put("trainNum", "class=\"train-num\">(.*?)</span>");
        productInfoRegexMap.put("seatType", "座席类型</header>(.*?)</span>");
        Map<String, String> productInfoMap = new HashMap<>();
        extractInfo(productInfoMap, orderDetail, productInfoRegexMap);
        String productNames = productInfoMap.get("startStation") + " " + productInfoMap.get("arriveStation") + " " +
                productInfoMap.get("trainNum") + " " + productInfoMap.get("seatType");
        orderPropertiesMap.put("productNames", productNames);
        return Order.from(orderPropertiesMap, htmlBean.getHbaseKey());
    }

    /***
     * 获取指定url的HTML页面内容
     *
     * @param url 需要访问的url
     * @return 页面
     */
    private HtmlBean getNewSourceCode(String url, CrawlerPage crawlerPage, HttpMethod httpMethod) {
        boolean useProxy = (boolean) CrawlerConfigurableProperties.getProperty("tb.use.proxy");
        UriData uriData = new UriData(0, URIUtils.getHttpURL(url));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.getUriData().setEscapeSetCookie(true);
        cp.getUriData().setCookieString(crawlerPage.getUriData().getCookieString());
        cp.getUriData().setSave(true);
        cp.getUriData().setId(crawlerPage.getUriData().getId());
        cp.getUriData().setHttpMethod(httpMethod);
        cp.getUriData().setParams(crawlerPage.getUriData().getParams());
        cp.getProxy().setProxyIp(crawlerPage.getProxy().getProxyIp());
        cp.getProxy().setUseProxy(useProxy);
        cp.getProxy().setUseSameProxy(true);
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.setJob(Job.TAOBAO);
        cp.getUriData().setReferer("https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm");
        try {
            Thread.sleep(12000);
        } catch (Exception e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
        Fetcher fetcher = new Fetcher();
//		System.out.println(cp.getUriData().getParams().toString());
        fetcher.process(cp);
        return new HtmlBean(cp.getSourceData().getSourceCode(), cp.getUriData().getHbaseParam().getKey());
    }

    /**
     * 从给定的JSONObject中提取map
     *
     * @param object JSONObject
     * @return map
     */
    private Map<String, String> getMapFrom(JSONObject object) {
        Map<String, String> map = new HashMap<>();
        for (Object key : object.keySet()) {
            if (object.get(key) instanceof Integer) {
                map.put((String) key, Integer.toString(object.getInt((String) key)));
            } else {
                map.put((String) key, (String) object.get(key));
            }
        }
        return map;
    }

    /**
     * 根据给定crawlerPage、regexMap，提取url所对应页面的信息，保存到resultMap中
     *
     * @param resultMap   保存结果的map
     * @param crawlerPage 上下文信息
     * @param regexMap    包含正则匹配规则的map
     * @param url         待解析页面的url
     */
    private void setInfo(Map<String, String> resultMap, CrawlerPage crawlerPage, Map<String, String> regexMap, String url) {
        HtmlBean htmlBean = getNewSourceCode(url, crawlerPage, HttpMethod.GET);
        String sourceCode = htmlBean.getSourceCode();
        if (!sourceCode.isEmpty()) {
            extractInfo(resultMap, sourceCode, regexMap);
        }
//        log.info("resultMap: " + resultMap.toString());
    }

    private void extractInfo(Map<String, String> tbInfoMap, String sourceCode, Map<String, String> regexMap) {
        CrawlerPage crawlerPage = new CrawlerPage();
        for (String key : regexMap.keySet()) {
            Matcher propertyMatcher = RegExpUtil.getMatcher(sourceCode, regexMap.get(key));
            if (propertyMatcher.find()) {
                tbInfoMap.put(key, HtmlUtils.escapeHtmlTag(propertyMatcher.group(1)));
            } else {
                crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add(key);
            }
        }
        if (crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().size() > 0) {
            LoggerProcessor loggerProcessor = new LoggerProcessor();
            loggerProcessor.process(crawlerPage);
        }
    }

    private List<String> extractProductNames(String sourceCode, String pruductBlockRegx, String pruductNameRegx) {
        List<String> products = new ArrayList<>();
        Matcher productListMatcher = RegExpUtil.getMatcher(sourceCode, pruductBlockRegx);
        if (productListMatcher.find()) {
            String productList = productListMatcher.group();
            Matcher productNameMatcher = RegExpUtil.getMatcher(productList, pruductNameRegx);
            //****商品名称列表****
            while (productNameMatcher.find()) {
                String name = HtmlUtils.escapeHtmlTag(productNameMatcher.group(1));
                if (StringUtils.isNotBlank(name)) {
                    products.add(name);
                }
            }
        } else {
            CrawlerPage crawlerPage = new CrawlerPage();
            crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add("productNames");
            LoggerProcessor loggerProcessor = new LoggerProcessor();
            loggerProcessor.process(crawlerPage);
        }
        return products;
    }

    /**
     * <p>获取淘宝个人信息</p>
     *
     * @param crawlerPage 上下文
     */
    private TbInfoBean getTaoBaoInfo(CrawlerPage crawlerPage) {
        String tbLevelUrl = "https://rate.taobao.com/myRate.htm";
        String accountSecurityUrl = "https://member1.taobao.com/member/fresh/account_security.htm";
        String baseInfoUrl = "https://i.taobao.com/user/baseInfoSet.htm";
        String vipGrowthUrl = "https://vip.taobao.com/vip_growth.htm";
        String accountManagementUrl = "https://member1.taobao.com/member/fresh/account_management.htm";
        String deliverAddressUrl = "http://member1.taobao.com/member/fresh/deliver_address.htm";
        String tianMaoUrl = "http://vip.tmall.com/vip/memberExp.htm";
        String tbCoinUrl = "https://taojinbi.taobao.com/index.htm";

        TbInfoBean tbInfoBean = new TbInfoBean();
        Map<String, String> tbInfoMap = new HashMap<>();

        //设置淘宝的个人属性
        setInfo(tbInfoMap, crawlerPage, tbLevelRegexMap, tbLevelUrl);
        setInfo(tbInfoMap, crawlerPage, accountSecurityRegexMap, accountSecurityUrl);
        setInfo(tbInfoMap, crawlerPage, baseInfoRegexMap, baseInfoUrl);
        setInfo(tbInfoMap, crawlerPage, vipGrowthRegexMap, vipGrowthUrl);
        setInfo(tbInfoMap, crawlerPage, accountManagementRegexMap, accountManagementUrl);
        setInfo(tbInfoMap, crawlerPage, deliverAddressRegexMap, deliverAddressUrl);
        setInfo(tbInfoMap, crawlerPage, tianMaoPropertiesRegexMap, tianMaoUrl);
        setInfo(tbInfoMap, crawlerPage, tbCoinRegexMap, tbCoinUrl);

        //根据年月日设置日期
        if (tbInfoMap.containsKey("birthdayYear") && tbInfoMap.containsKey("birthdayMonth") && tbInfoMap.containsKey("birthdayDay")) {
            String birthday = tbInfoMap.get("birthdayYear") + "-" + tbInfoMap.get("birthdayMonth") + "-" + tbInfoMap.get("birthdayDay");
            Date date = formatDate(birthday);
            if (date != null) {
                tbInfoBean.setBirthday(date.getTime());
            }
        }

        //设置账户余额
        if (!tbInfoMap.containsKey("accountRemain") || tbInfoMap.get("accountRemain").isEmpty()) {
            tbInfoMap.put("accountRemain", "0.0");
        }

        //设置登陆名
        tbInfoBean.setLoginName(getLoginName(crawlerPage));

        //设置淘宝买家级别
        if (tbInfoMap.containsKey("taobaoLevel") && !tbInfoMap.get("taobaoLevel").isEmpty()) {
            tbInfoBean.setBuyerLevel(calculateBuyerLevel(Integer.parseInt(tbInfoMap.get("taobaoLevel"))));
        }

        try {
            BeanUtils.copyProperties(tbInfoBean, tbInfoMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
        return tbInfoBean;
    }

    /**
     * 根据淘宝买家累计信用计算淘宝买家等级，
     * 等级从0-19，共20个等级
     *
     * @param taobaoLevel 淘宝积分
     * @return 返回淘宝买家等级
     */
    private int calculateBuyerLevel(int taobaoLevel) {
        int[] limit = {3, 10, 40, 90, 150, 250, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000,
                10000000, 20000000, 50000000, 100000000};
        for (int i = 0; i < limit.length; i++) {
            if (taobaoLevel <= limit[i]) {
                return i;
            }
        }
        return limit.length;
    }

    /**
     * 格式化yyyy-MM-dd形式的字符串
     *
     * @param string 待格式化的字符串
     * @return 如果不成功则返回null
     */
    private static Date formatDate(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(string);
        } catch (ParseException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, String>> getAddrs(CrawlerPage crawlerPage) {
        String deliverAddressUrl = "http://member1.taobao.com/member/fresh/deliver_address.htm";
        HtmlBean htmlBean = getNewSourceCode(deliverAddressUrl, crawlerPage, HttpMethod.GET);
        return getAddrs(htmlBean.getSourceCode());
    }

    private List<Map<String, String>> getAddrs(String sourceCode) {
        List<Map<String, String>> list = new ArrayList<>();
        Source source = new Source(sourceCode);
        Element tbl = source.getFirstElementByClass("tbl-main");
        if (tbl != null) {
            List<Element> trs = tbl.getAllElements("tr");
            if (trs != null) {
                for (Element tr : trs) {
                    List<Element> tds = tr.getChildElements();
                    if (!"th".equals(tds.get(0).getName())) {
                        try {
                            Map<String, String> map = new HashMap<>();
                            map.put("receiverName", HtmlUtils.escapeHtmlTag(tds.get(0).getContent().toString()));
                            map.put("addr", HtmlUtils.escapeHtmlTag(tds.get(1).getContent().toString()));
                            map.put("addrDetail", HtmlUtils.escapeHtmlTag(tds.get(2).getContent().toString()));
                            map.put("postCode", HtmlUtils.escapeHtmlTag(tds.get(3).getContent().toString()));
                            map.put("phone", HtmlUtils.escapeHtmlTag(tds.get(4).getContent().toString()));
                            list.add(map);
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    }
                }
            }
        }
        return list;
    }

    public String getLoginName(CrawlerPage crawlerPage) {
        Query query = crawlerPage.getUriData().getQuery();
        if (query instanceof TaoBaoLoginQuery) {
            TaoBaoLoginQuery loginQuery = (TaoBaoLoginQuery) query;
            return loginQuery.getLoginName();
        } else if (query instanceof TaoBaoVerifyMsgQuery) {
            TaoBaoVerifyMsgQuery msgQuery = (TaoBaoVerifyMsgQuery) query;
            return msgQuery.getLoginName();
        }
        return "";
    }

    private static String join(String sep, List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list == null || list.size() == 0) {
            return "";
        }
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            stringBuilder.append(iter.next());
            if (iter.hasNext()) {
                stringBuilder.append(sep);
            }
        }
        return stringBuilder.toString();
    }

}
