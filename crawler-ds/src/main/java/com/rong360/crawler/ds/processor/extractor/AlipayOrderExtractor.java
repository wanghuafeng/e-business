package com.rong360.crawler.ds.processor.extractor;

import com.rong360.crawler.CrawlerConfigurableProperties;
import com.rong360.crawler.bean.CrawlerBean;
import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.ds.bean.AlipayBankCardBean;
import com.rong360.crawler.ds.bean.AlipayInfoBean;
import com.rong360.crawler.ds.bean.AlipayOrderBean;
import com.rong360.crawler.ds.bean.HtmlBean;
import com.rong360.crawler.ds.service.impl.AlipayLoginServiceImpl;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.processor.AbstractProcessor;
import com.rong360.crawler.processor.extractor.IExtractor;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.processor.store.LoggerProcessor;
import com.rong360.crawler.proxy.ip.ProxyIp;
import com.rong360.crawler.util.HtmlUtils;
import com.rong360.crawler.util.RegExpUtil;
import com.rong360.crawler.util.URIUtils;
import com.rong360.crawler.util.UnicodeDecoderUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * 支付宝订单解析类
 * Created by Administrator on 2015/10/27.
 */
public class AlipayOrderExtractor extends AbstractProcessor implements IExtractor {
    public static Logger logger = Logger.getLogger(AlipayOrderExtractor.class);

    @Override
    public void process(CrawlerPage crawlerPage) {

    }

    {
        this.job = Job.TAOBAO;
        this.task = Task.ORDER;
    }

    private Map<String, String> personalInfoRegexMap = new HashMap<>();
    private Map<String, String> accountRegexMap = new HashMap<>();
    public Map<String, String> securityRegexMap = new HashMap<>();
    private Map<String, String> yueRegexMap = new HashMap<>();
    private Map<String, String> orderRegexMap = new HashMap<>();

    {
//        personalInfoRegexMap.put("loginName", "");
        personalInfoRegexMap.put("taobaoName", "<th>淘宝会员名</th>.*?<td>(.*?)</td>");
//        personalInfoRegexMap.put("major", );
        personalInfoRegexMap.put("registerDate", "<th>注册时间</th>.*?<td>(.*?)</td>");
        personalInfoRegexMap.put("isRealName", "<span class=\"text-muted\">(已认证)</span>");
        personalInfoRegexMap.put("isProtected", "<th>会员保障</th>.*?<td>(.*?)</td>");
        personalInfoRegexMap.put("isPhone", "<th>手机</th>.*?<td>.*?<span class=\"text-muted\">(.*?)</span>.*?</span>");
        personalInfoRegexMap.put("isIdcard", "<span class=\"text-muted\">(3\\*{16}9)</span>");
//        personalInfoRegexMap.put("secretLevel", "");

        securityRegexMap.put("identifyTime", "<li>证件有效期至：(.*?)</li>");
//        securityRegexMap.put("accountLevel",);

//        yueRegexMap.put("balance", "<h3>账户余额</h3>.*?<div class=\"item-main\">(.*?)</div>");
        yueRegexMap.put("balance-integer", "<h3>账户余额</h3>.*?class=\"integer\">(.*?)</span>");
        yueRegexMap.put("balance-decimal", "<h3>账户余额</h3>.*?class=\"decimal\">(.*?)</span>");

        accountRegexMap.put("income", "历史累计收益\\(元\\)</span>.*?data-account=\"(.*?)\">");
        accountRegexMap.put("amount", "总金额\\(元\\).*?data-account='(.*?)'>");

        orderRegexMap.put("time-d", "class=\"(?:time-d|time)\">(.*?)</p>");
        orderRegexMap.put("time-h", "class=\"(?:time-h ft-gray|text-muted)\">(.*?)</p>");
        orderRegexMap.put("tradeType", "class=\"consume-title\">(.*?)</p>");
        orderRegexMap.put("tradeNoType", "class=\"consume-title\">(.*?)</p>");
        orderRegexMap.put("tradeNo", "(?:data-clipboard-text=\"|交易号:|<p>流水号:)(.*?)(?:\"|<a|</p>)");
        orderRegexMap.put("receiverName", "class=\"(?:other|name p-inline ft-gray)\">(.*?)</p>");
        orderRegexMap.put("amount", "class=\"amount\">.*?<span class=\".*?\">(.*?)</span>.*?</td>");
        orderRegexMap.put("status", "class=\"status\">.*?<p(?: class=\"text-muted\")?>(.*?)</p>.*?<td");
//        orderRegexMap.put("source", "");
//        orderRegexMap.put("alipayName", "");
        orderRegexMap.put("type", "data-link=\".*?&bizType=(.*?)\"");
    }

    public List<CrawlerBean> extractor(String cookieString, String loginName, ProxyIp proxyIp) {
        logger.info("开始抓取支付宝信息");

        List<CrawlerBean> list = new ArrayList<>();
        
        /*登陆支付宝*/
        AlipayLoginServiceImpl alipayLoginService = new AlipayLoginServiceImpl();
        CrawlerPage crawlerPage = alipayLoginService.login(cookieString, proxyIp);
        crawlerPage.getUriData().setId(loginName);
        
        
        /*解析个人信息*/
        Map<String, String> personalInfo = extractPersonalInfo(crawlerPage);
        personalInfo.put("loginName", loginName);
        addBean(list, new AlipayInfoBean(), personalInfo);
//        System.out.println("personalInfo:");
//        System.out.println(personalInfo);
        
        /*解析关联银行卡*/
        List<Map<String, String>> cardList = extractBankCard(crawlerPage, loginName);
        logger.info("cardList:" + cardList.size());
        for (Map<String, String> map : cardList) {
            addBean(list, new AlipayBankCardBean(), map);
//            System.out.println(map);
        }
        
        /*解析支付宝订单*/
        Calendar today = Calendar.getInstance();
        today.add(Calendar.YEAR, -3);
        Date beginDate = today.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            beginDate = sdf.parse("2008-01-01");
        } catch (ParseException e) {
            logger.warn(e.getMessage());
//            e.printStackTrace();
        }
        List<Map<String, String>> orderList = new ArrayList<>();
        try {
            orderList = extractOrdersBetween(crawlerPage, beginDate, loginName);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        logger.info("orderList:" + orderList.size());
        for (Map<String, String> map : orderList) {
            addBean(list, new AlipayOrderBean(), map);
//            System.out.println(map);
        }

        return list;
    }

    private Map<String, String> extractPersonalInfo(CrawlerPage crawlerPage) {
        /*获取基本信息*/
        String baseInfoUrl = "https://custweb.alipay.com/account/index.htm";
        Map<String, String> personalInfo = getValues(crawlerPage, baseInfoUrl, personalInfoRegexMap);
        if (personalInfo.containsKey("isIdcard") && !personalInfo.get("isIdcard").isEmpty()) {
            personalInfo.put("isIdcard", "是");
        } else {
            personalInfo.put("isIdcard", "否");
        }
        if (personalInfo.containsKey("registerDate") && !personalInfo.get("registerDate").isEmpty()) {
            personalInfo.put("registerDate", format(personalInfo.get("registerDate")));
        } else {
            personalInfo.put("registerDate", "0");
        }
        if (personalInfo.containsKey("isPhone") && !personalInfo.get("isPhone").isEmpty()) {
            personalInfo.put("isPhone", "已绑定");
        } else {
            personalInfo.put("registerDate", "");
        }
        
        /*获取余额宝信息*/
        String yuebaoUrl = "https://yebprod.alipay.com/yeb/asset.htm";
        Map<String, String> account = getValues(crawlerPage, yuebaoUrl, accountRegexMap);

        /*获取账户余额*/
        String yueUrl = "https://personalweb.alipay.com/portal/newhome.htm";
        Map<String, String> yue = getValues(crawlerPage, yueUrl, yueRegexMap);
        if (yue.containsKey("balance-integer") && !yue.get("balance-integer").isEmpty()
                && yue.containsKey("balance-decimal") && !yue.get("balance-decimal").isEmpty()) {
            String integer = yue.get("balance-integer").replaceAll(",", "");
            String decimal = yue.get("balance-decimal");
            yue.put("balance", integer + decimal);
        }

        String certifyUrl = "https://certify.alipay.com/certifyInfo.htm";
        Map<String, String> certify = getValues(crawlerPage, certifyUrl, securityRegexMap);

        List<Map<String, String>> list = new ArrayList<>();
        list.add(personalInfo);
        list.add(account);
        list.add(yue);
        list.add(certify);
        return merge(list);
    }

    private List<Map<String, String>> extractBankCard(CrawlerPage crawlerPage, String loginName) {
        String url = "https://zht.alipay.com/asset/bindQuery.json?_input_charset=utf-8&providerType=BANK";
        crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
        String sourceCode = getNewSourceCode(crawlerPage, url).getSourceCode();
//        System.out.println(sourceCode);
        List<Map<String, String>> list = new ArrayList<>();
        try {
            JSONObject result = JSONObject.fromObject(sourceCode);
            if ("ok".equals(result.getString("stat"))) {
                JSONArray bankCards = result.getJSONArray("results");
                for (Object bankCard : bankCards) {
                    JSONObject card = JSONObject.fromObject(bankCard);
                    if (!card.containsKey("bankProductType")) {
                        continue;
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("id", card.getString("signId"));
                    map.put("alipayName", loginName);
                    map.put("bankName", card.getString("providerName"));
                    map.put("cardType", card.getString("cardTypeName"));
                    map.put("cardLastNum", card.getString("providerUserName"));
                    map.put("openStatus", getOpenStatus(crawlerPage, card.getString("partnerId"), card.getString("encryptProviderUserId")));
                    map.put("applyTime", "0");
                    list.add(map);
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return list;
    }

    /**
     * 获得给定url页面的源码
     *
     * @param crawlerPage
     * @param url
     * @return
     */
    private HtmlBean getNewSourceCode(CrawlerPage crawlerPage, String url) {
        boolean useProxy = (boolean) CrawlerConfigurableProperties.getProperty("tb.use.proxy");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
        crawlerPage.getUriData().setEscapeSetCookie(true);
        crawlerPage.getUriData().setId(crawlerPage.getUriData().getId());
        crawlerPage.getUriData().setUri(URIUtils.getHttpURL(url));
        crawlerPage.getUriData().setSave(true);
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);
        Fetcher fetcher = new Fetcher();
        fetcher.process(crawlerPage);
        return new HtmlBean(crawlerPage.getSourceData().getSourceCode(), crawlerPage.getUriData().getHbaseParam().getKey());
    }

    /**
     * 根据regexMap匹配网页源码,返回匹配到的key-values对应的map
     *
     * @param regexMap
     * @param sourceCode
     * @return
     */
    public Map<String, String> getMatchedValues(Map<String, String> regexMap, String sourceCode, String url) {
        Map<String, String> values = new HashMap<>();
        CrawlerPage crawlerPage = new CrawlerPage();
        crawlerPage.getUriData().setStrUri(url);
        for (String key : regexMap.keySet()) {
            Matcher matcher = RegExpUtil.getMatcher(sourceCode, regexMap.get(key));
            if (matcher.find()) {
                values.put(key, HtmlUtils.escapeHtmlTag(matcher.group(1)));
            } else {
                crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add(key);
            }
        }
        if (crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().size() > 0) {
            LoggerProcessor loggerProcessor = new LoggerProcessor();
            loggerProcessor.process(crawlerPage);
        }
        return values;
    }

    private Map<String, String> getValues(CrawlerPage crawlerPage, String url, Map<String, String> regexMap) {
        String sourceCode = getNewSourceCode(crawlerPage, url).getSourceCode();
        return getMatchedValues(regexMap, sourceCode, url);
    }

    /**
     * 将yyyy年MM月dd日格式的字符串转换为时间戳并作为字符串返回
     *
     * @param string
     * @return
     */
    private String format(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            return Long.toString(sdf.parse(string).getTime());
        } catch (ParseException e) {
            logger.warn(e.getMessage());
            logger.warn("dateString " + string + "does not match 'yyyy年MM月dd日'");
        }
        return "0";
    }

    /**
     * 返回yyyy.MM.dd格式的字符串
     *
     * @param date
     * @return
     */
    private String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(date);
    }

    /**
     * 格式化指定形式的字符串并转化为时间戳
     *
     * @param source
     * @param pattern
     * @return
     */
    private String format(String source, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if (source.matches("\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}")) {
            sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");
        } else if (source.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        }
        try {
            return Long.toString(sdf.parse(source).getTime());
        } catch (ParseException e) {
            logger.warn(e.getMessage());
        }
        return "0";
    }

    private Map<String, String> merge(List<Map<String, String>> maps) {
        Map<String, String> result = new HashMap<>();
        for (Map<String, String> map : maps) {
            for (String key : map.keySet()) {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

    private String getOpenStatus(CrawlerPage crawlerPage, String partnerId, String encryptProviderUserId) {
        String url = "https://zht.alipay.com/asset/assetItemQuery.json?_input_charset=utf-8&action=bank";
        String encodedId;
        try {
            encodedId = URLEncoder.encode(encryptProviderUserId, "UTF-8");
            String sourceCode = getNewSourceCode(crawlerPage, url + "&partnerId=" + partnerId + "&encryptProviderUserId=" + encodedId).getSourceCode();
            JSONObject result = JSONObject.fromObject(sourceCode);
            if ("ok".equals(result.getString("stat"))) {
                if (result.getJSONObject("results").getJSONObject("FASTPAYSERVICE").getBoolean("validKatong")) {
                    return "已开通";
                } else {
                    return "未开通";
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage());
        }
        return "";
    }

    private List<Map<String, String>> extractOrdersBetween(CrawlerPage crawlerPage, Date beginDate, String loginName) {
        return extractOrdersBetween(crawlerPage, beginDate, new Date(System.currentTimeMillis()), loginName);
    }

    private List<Map<String, String>> extractOrdersBetween(CrawlerPage crawlerPage, Date beginDate, Date endDate, String loginName) {
        String url = "https://consumeprod.alipay.com/record/advanced.htm?beginTime=00%3A00&endTime=24%3A00" +
                "&dateRange=customDate&status=all&keyword=bizOutNo&keyValue=&dateType=createDate&minAmount=&maxAmount=" +
                "&fundFlow=all&tradeType=ALL&categoryId=&_input_charset=utf-8";
        url = url + "&beginDate=" + format(beginDate) + "&endDate=" + format(endDate);
        HtmlBean htmlBean = getNewSourceCode(crawlerPage, url);
        String sourceCode = htmlBean.getSourceCode();
        String nextPageRegex = "class=\"page-next\" href=\"(.*?)\">下一页></a>";
        String orderNumRegex = "<div class=\"page-link\">.*?共(.*?)条";
        List<Map<String, String>> list = new ArrayList<>();
        list.addAll(extractOrders(sourceCode, loginName, htmlBean.getHbaseKey()));
        Matcher matcher = RegExpUtil.getMatcher(sourceCode, nextPageRegex);
        if (matcher.find()) {
            /*高级版*/
            while (sourceCode.contains("下一页")) {
                matcher = RegExpUtil.getMatcher(sourceCode, nextPageRegex);
                if (matcher.find()) {
                    String nextPageUrl = matcher.group(1);
                    if (nextPageUrl.startsWith("https://consumeprod.alipay.com")) {
                        nextPageUrl += "&maxAmount=&minAount=&keyValue=";
                        nextPageUrl = nextPageUrl.replaceAll("&amp;", "&");
                    }
                    sourceCode = getNewSourceCode(crawlerPage, nextPageUrl).getSourceCode();
                    List<Map<String, String>> orders = extractOrders(sourceCode, loginName, htmlBean.getHbaseKey());
                    list.addAll(orders);
                } else {
                    break;
                }
            }
        } else {
            /*基础版*/
            url = "https://consumeprod.alipay.com/record/standard.htm?_input_charset=utf-8&tradeType=ALL&dateRange=customDate&status=all&fundFlow=all&&dateType=createDate";
            url = url + "&beginDate=" + format(beginDate) + "&endDate=" + format(endDate);
            htmlBean = getNewSourceCode(crawlerPage, url);
            sourceCode = htmlBean.getSourceCode();
            matcher = RegExpUtil.getMatcher(sourceCode, orderNumRegex);
            if (matcher.find()) {
                int orderNum = Integer.parseInt(HtmlUtils.escapeHtmlTag(matcher.group(1)));
                int pageNum = (orderNum % 20 == 0) ? (orderNum / 20) : (orderNum / 20 + 1);
                for (int i = 2; i <= pageNum; i++) {
                    sourceCode = getNewSourceCode(crawlerPage, url + "&pageNum=" + i).getSourceCode();
                    List<Map<String, String>> orders = extractOrders(sourceCode, loginName, htmlBean.getHbaseKey());
                    list.addAll(orders);
                }
            } else {
                crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add("orderNum");
            }
        }
        if (crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().size() > 0) {
            LoggerProcessor loggerProcessor = new LoggerProcessor();
            loggerProcessor.process(crawlerPage);
        }
        return list;
    }

    public List<Map<String, String>> extractOrders(String sourceCode, String loginName, String hbaseKey) {
        List<Map<String, String>> list = new ArrayList<>();
        String orderBlockRegex = "<tr id=\"J-item-\\d+\".*?class=\"J-item.*?\">.*?</tr>";
        Matcher matcher = RegExpUtil.getMatcher(sourceCode, orderBlockRegex);
        LoggerProcessor loggerProcessor = new LoggerProcessor();
        while (matcher.find()) {
            CrawlerPage crawlerPage = new CrawlerPage();
            Map<String, String> map = new HashMap<>();
            String orderBlock = matcher.group();
            for (Map.Entry<String, String> entry : orderRegexMap.entrySet()) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue();
                Matcher propertyMatcher = RegExpUtil.getMatcher(orderBlock, propertyValue);
                if (propertyMatcher.find()) {
                    String value = UnicodeDecoderUtils.decodeUnicode(HtmlUtils.escapeHtmlTag(propertyMatcher.group(1)));
                    if (propertyName.equals("amount")) {
                        value = value.replaceAll(" ", "");
                    }
                    map.put(propertyName, value);
                } else {
                    crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add(entry.getKey());
                }
            }
            if (crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().size() > 0) {
                loggerProcessor.process(crawlerPage);
            }
            if (map.containsKey("time-d") && !map.get("time-d").isEmpty()
                    && map.containsKey("time-h") && !map.get("time-h").isEmpty()) {
                map.put("payTime", format(map.get("time-d") + " " + map.get("time-h"), "yyyy.MM.dd hh:mm"));
            }
            if (map.containsKey("receiverName") && StringUtils.isNotBlank(map.get("receiverName"))) {
                String receiverName = map.get("receiverName");
                if (receiverName.endsWith("|")) {
                    receiverName = receiverName.substring(0, receiverName.length() - 1);
                }
                map.put("receiverName", receiverName.trim());
            }
            map.put("source", "ZHIFUBAO");
            map.put("alipayName", loginName);
            map.put("hbaseKey", hbaseKey);
            list.add(map);
        }
        if (!matcher.find(1)) {
            CrawlerPage crawlerPage = new CrawlerPage();
            crawlerPage.getMetaData().getExtractorResult().getWarnAtrrSet().add("alipayOrderBlock");
            loggerProcessor.process(crawlerPage);
        }
        return list;
    }

    public void addBean(List<CrawlerBean> list, CrawlerBean crawlerBean, Map<String, String> map) {
        try {
            BeanUtils.populate(crawlerBean, map);
            list.add(crawlerBean);
        } catch (Exception e) {
//            System.out.println(crawlerBean.getAssemleBean());
            logger.warn(e.getMessage());
        }
    }
}
