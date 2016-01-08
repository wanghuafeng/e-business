package com.rong360.crawler.ds.service.impl;

import com.rong360.crawler.CrawlerConfigurableProperties;
import com.rong360.crawler.api.ApiWd;
import com.rong360.crawler.bean.CrawlerStatus;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.Module;
import com.rong360.crawler.cookie.PostData;
import com.rong360.crawler.cookie.manager.RedisPostDataManager;
import com.rong360.crawler.ds.query.impl.*;
import com.rong360.crawler.ds.rule.impl.DSLoginSuccessRuler;
import com.rong360.crawler.ds.rule.impl.TaoBaoPCSavePostDataRuler;
import com.rong360.crawler.ds.service.ILoginService;
import com.rong360.crawler.ds.util.DsResponseUtil;
import com.rong360.crawler.ds.util.Util;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.proxy.ip.ProxyIp;
import com.rong360.crawler.proxy.ip.pool.ProxyIpPool;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.util.NoticeUtils;
import com.rong360.crawler.util.URIUtils;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiongwei
 */
public class TaoBaoPCLoginServiceImpl implements ILoginService {

    /*****
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(TaoBaoPCLoginServiceImpl.class);

    private boolean useProxy = (boolean) CrawlerConfigurableProperties.getProperty("tb.use.proxy");


    /*****
     * 下载器
     *****/
    private Fetcher fetcher;

    /*****
     * 规则处理
     *****/
    private TaoBaoPCSavePostDataRuler savePostDataRuler;

    public void setSavePostDataRuler(TaoBaoPCSavePostDataRuler savePostDataRuler) {
        this.savePostDataRuler = savePostDataRuler;
    }


    @Autowired
    private TaoBaoPCSavePostDataRuler ruler;

    @Autowired
    private DSLoginSuccessRuler successRuler;

    /*****
     * POST提交参数管理器
     *****/
    private RedisPostDataManager postDataManager;

    public void setPostDataManager(RedisPostDataManager postDataManager) {
        this.postDataManager = postDataManager;
    }

    public void setFetcher(Fetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public Object login(CrawlerPage crawlerPage) {
        /**
         * 设置代理
         */
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);
        if (crawlerPage.getProxy().getProxyIp() == null) {
            ProxyIp proxyIp = ProxyIpPool.getRong360ProxyIp(crawlerPage.getUriData().getQuery().getUserId(), "qin", "");
            crawlerPage.getProxy().setProxyIp(proxyIp);
        }

        JSONObject result;
        /***** 1. 设置首页获取的Cookie,POST等参数 *****/
        if (ruler.rule(crawlerPage)) {
            /***** 2. 如果未访问首页,则先访问首页返回验证码 *****/
            Query query = crawlerPage.getUriData().getQuery();
            if (query instanceof TaoBaoPCVerifyUserQuery) {
                crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                fetcher.innerExecute(crawlerPage);
                result = DsResponseUtil.getReturnObject(0, ErrorCode._10012.getCode(), 
                        ErrorCode._10012.getMsg(), crawlerPage.getUriData().getAuthCodeFileName());
                return result;
            } else if (query instanceof TaoBaoPCLoginQuery) {
                TaoBaoPCLoginQuery taoBaoPCLoginQuery = (TaoBaoPCLoginQuery) query;
                String key = Util.generateRedisKey(crawlerPage.getJob(), taoBaoPCLoginQuery.getLoginName());

                /***** 3. 执行HTTP POST提交登录操作 *****/
                crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);
                fetcher.innerExecute(crawlerPage);
                String sourceCode = crawlerPage.getSourceData().getSourceCode();
                log.info("login cookie:" + crawlerPage.getUriData().getCookieString());
                log.debug(sourceCode);
                /***** 4. 通过返回结果判断是否登录结果状态 *****/
                if (StringUtils.isNotBlank(sourceCode)) {
                    if (sourceCode.contains("gotoURL:\"https://i.taobao.com/my_taobao.htm")) {
                        int statusId = ApiWd.saveStatus(
                                new CrawlerStatus(query.getMerchantId(), Module.JD.getModuleName(), query.getUserId(),
                                        NoticeUtils.CrawlerStatus.CRAWLING.getStatus()));
                        result = DsResponseUtil.getReturnObject(1, ErrorCode._0.getCode(), ErrorCode._0.getMsg(), "");
                        result.put("statusId", statusId);
                        postDataManager.removePostData(key);
                        crawlerPage.setCrawlerStatusId(statusId);
                        successRuler.rule(crawlerPage);
                        return result;
                    } else if (sourceCode.contains("class=\"error\"") && sourceCode.contains("页面已过期")) {
                        crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
                        savePostDataRuler.innerEexcuteRule(crawlerPage);
                        crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                        fetcher.innerExecute(crawlerPage);
                        result = DsResponseUtil.getReturnObject(1, ErrorCode._10010.getCode(),
                                ErrorCode._10010.getMsg(), crawlerPage.getUriData().getAuthCodeFileName());
                        return result;
                    } else if (sourceCode.contains("class=\"error\"") && (sourceCode.contains("验证码错误"))) {
                        Source source = new Source(sourceCode);
                        Element element = source.getElementById("J_StandardCode_m");
                        String authCodeURL = element.getAttributeValue("data-src");
                        crawlerPage.getUriData().setAuthCodeURL(authCodeURL);
                        crawlerPage.getUriData().setAuthCodeFileName(URIUtils.md5URI(authCodeURL));
                        crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                        fetcher.innerExecute(crawlerPage);
                        result = DsResponseUtil.getReturnObject(0, ErrorCode._10012.getCode(),
                                ErrorCode._10012.getMsg(), crawlerPage.getUriData().getAuthCodeFileName());
                        return result;
                    } else if (sourceCode.contains("class=\"error\"") && sourceCode.contains("为了你的账户安全，请拖动滑块完成验证")) {
                        result = DsResponseUtil.getReturnObject(0, ErrorCode._10012.getCode(), 
                                "为了你的账户安全，请拖动滑块完成验证", "");
                        return result;
                    } else if (sourceCode.contains("class=\"error\"")
                            && (sourceCode.contains("你输入的账户名和密码不匹配") || sourceCode.contains("你输入的密码和账户名不匹配"))) {
                        postDataManager.removePostData(key);
                        result = DsResponseUtil.getReturnObject(0, ErrorCode._10016.getCode(), ErrorCode._10016.getMsg(), "");
                        result.put("url", "");
                        return result;
                    } else if (sourceCode.contains("gotoURL:\"https://login.taobao.com/member/login_unusual.htm")) {
                        PostData postData = postDataManager.getPostData(key);
                        if (postData == null) {
                            result = DsResponseUtil.getReturnObject(0, ErrorCode._10007.getCode(), ErrorCode._10007.getMsg(), "");
                            return result;
                        } else {
                            postData.getParams().clear();
                            postDataManager.saveOrUpdate(postData);
                            TaoBaoPCSendMsgQuery taoBaoPCSendMsgQuery = new TaoBaoPCSendMsgQuery();
                            try {
                                BeanUtils.copyProperties(taoBaoPCSendMsgQuery, query);
                            } catch (Exception e) {
                                log.warn(e.getMessage());
                            }
                            crawlerPage.getUriData().setQuery(taoBaoPCSendMsgQuery);
                            if (ruler.rule(crawlerPage)) {
                                postData = postDataManager.getPostData(key);
                                String phone = "";
                                if (StringUtils.isNotBlank(postData.getParams().get("phone"))) {
                                    JSONArray optionPhone = JSONArray.fromObject(postData.getParams().get("phone"));
                                    for (int i = 0; i < optionPhone.size(); i++) {
                                        String name = optionPhone.getJSONObject(i).getString("name");
                                        if (!"常用手机".equalsIgnoreCase(name)) {
                                            phone += name;
                                            if (i < optionPhone.size()) {
                                                phone += ";";
                                            }
                                        }
                                    }
                                    if (phone.endsWith(";")) {
                                        phone = phone.substring(0, phone.lastIndexOf(";"));
                                    }
                                }
                                result = DsResponseUtil.getReturnObject(0, ErrorCode._0.getCode(), "安全验证,您的账号价值连城，我们为您保驾护航", "");
                                result.put("phone", phone);
                                return result;
                            } else {
                                result = DsResponseUtil.getReturnObject(0, ErrorCode._10007.getCode(), ErrorCode._10007.getMsg(), "");
                                return result;
                            }
                        }
                    }
                }
                postDataManager.removePostData(key);
                result = DsResponseUtil.getReturnObject(0, ErrorCode._10007.getCode(), ErrorCode._10007.getMsg(), "");
                return result;
            } else {
                result = DsResponseUtil.getReturnObject(0, ErrorCode._10007.getCode(), ErrorCode._10007.getMsg(), "");
                return result;
            }
        } else {
            result = DsResponseUtil.getReturnObject(0, ErrorCode._10007.getCode(), ErrorCode._10007.getMsg(), "");
            return result;
        }
    }

    @Override
    public Object sendMsg(CrawlerPage crawlerPage) {
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);
        JSONObject result = new JSONObject();
        /***** 1. 设置首页获取的Cookie,POST等参数 *****/
        savePostDataRuler.innerEexcuteRule(crawlerPage);

        /***** 2. 执行HTTP POST提交发送动态密码操作 *****/
        crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);
        fetcher.innerExecute(crawlerPage);
        String sourceCode = crawlerPage.getSourceData().getSourceCode();

        log.info(sourceCode);
        log.debug(sourceCode);

        /***** 3. 通过返回结果判断是否登录结果状态 *****/
        if (StringUtils.isNotBlank(sourceCode)) {
            if (sourceCode.contains("isSuccess") && sourceCode.contains("true")) {
                result.put("status", 1);
                result.put("errorcode", ErrorCode._0.getCode());
                return result;
            } else {
                result.put("status", 0);
                result.put("errorcode", ErrorCode._0.getCode());
                return result;
            }
        }
        result = DsResponseUtil.getReturnObject(ErrorCode._10007.getCode(), ErrorCode._10007.getMsg());
        return result;
    }

    @Override
    public Object verifyPhoneCode(CrawlerPage crawlerPage) {
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);

        JSONObject result;
        try {
            Query query = crawlerPage.getUriData().getQuery();
            String key = "";
            if (query instanceof TaoBaoPCVerifyMsgQuery) {
                TaoBaoPCVerifyMsgQuery taoBaoPCVerifyMsgQuery = (TaoBaoPCVerifyMsgQuery) query;
                key = Util.generateRedisKey(crawlerPage.getJob(), taoBaoPCVerifyMsgQuery.getLoginName());
            }

            /***** 1. 设置首页获取的Cookie,POST等参数 *****/
            savePostDataRuler.innerEexcuteRule(crawlerPage);

            if (crawlerPage.getUriData().getQuery() instanceof TaoBaoSendMsgQuery) {
                result = DsResponseUtil.getReturnObject(0, ErrorCode._0.getCode(), "请获取短信验证码");
                return result;
            }

            /***** 2. 执行HTTP POST提交验证动态密码操作 *****/
            crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);
            fetcher.innerExecute(crawlerPage);
            String sourceCode = crawlerPage.getSourceData().getSourceCode();
            log.info(sourceCode);
            log.debug(sourceCode);
            /***** 3. 通过返回结果判断是否登录结果状态 *****/
            if (StringUtils.isNotBlank(sourceCode)) {
                if (sourceCode.contains("isSuccess") && sourceCode.contains("true")) {
                    PostData postData = postDataManager.getPostData(key);
                    if (postData != null && postData.getParams().get("success").startsWith("https://login.taobao.com/member/login_by_safe.htm")) {
                        int statusId = ApiWd.saveStatus(
                                new CrawlerStatus(query.getMerchantId(), Module.JD.getModuleName(), query.getUserId(),
                                        NoticeUtils.CrawlerStatus.CRAWLING.getStatus()));
                        result = DsResponseUtil.getReturnObject(1, ErrorCode._0.getCode(), ErrorCode._0.getMsg(), "");
                        result.put("statusId", statusId);

                        /***** 4. 访问登录成功页面获取cookie *****/
                        UriData uriData = crawlerPage.getUriData();
                        uriData.setUri(URIUtils.getHttpURL(postData.getParams().get("success")));
                        crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
                        fetcher.innerExecute(crawlerPage);
                        result = DsResponseUtil.getReturnObject(1, ErrorCode._0.getCode(), ErrorCode._0.getMsg());
                        crawlerPage.setCrawlerStatusId(statusId);
                        successRuler.rule(crawlerPage);
                        return result;
                    }
                } else {
                    result = DsResponseUtil.getReturnObject(0, ErrorCode._10013.getCode(), ErrorCode._10013.getMsg());
                    return result;
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        result = DsResponseUtil.getReturnObject(ErrorCode._20001.getCode(), "登陆失败");
        return result;
    }

    @Override
    public Object verifyUser(CrawlerPage crawlerPage) {
        ProxyIp proxyIp = ProxyIpPool.getRong360ProxyIp(crawlerPage.getUriData().getQuery().getUserId(), "qin", "");
        crawlerPage.getProxy().setProxyIp(proxyIp);
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);
        JSONObject result;
        try {
            /*****1. 访问淘宝登录首页 获取此页Cookie与登录提交信息*****/
            savePostDataRuler.innerEexcuteRule(crawlerPage);

            /*****2. 下载返回的验证码图片地址 *****/
            if (StringUtils.isNotBlank(crawlerPage.getUriData().getAuthCodeURL())) {
                result = DsResponseUtil.getReturnObject(0, "", crawlerPage.getUriData().getAuthCodeFileName());
                crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                fetcher.innerExecute(crawlerPage);
            } else {
                result = DsResponseUtil.getReturnObject(0, "", "");
            }
            return result;

        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        result = DsResponseUtil.getReturnObject(ErrorCode._10007.getCode(), ErrorCode._10007.getMsg(), "");
        return result;
    }
}
