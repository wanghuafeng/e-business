package com.rong360.crawler.ds.service.impl;

import com.rong360.crawler.CrawlerConfigurableProperties;
import com.rong360.crawler.api.ApiWd;
import com.rong360.crawler.bean.CrawlerStatus;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.Module;
import com.rong360.crawler.cookie.PostData;
import com.rong360.crawler.cookie.manager.RedisPostDataManager;
import com.rong360.crawler.ds.query.impl.TaoBaoLoginQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoSendMsgQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoVerifyMsgQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoVerifyUserQuery;
import com.rong360.crawler.ds.rule.impl.DSLoginSuccessRuler;
import com.rong360.crawler.ds.rule.impl.TaoBaoSavePostDataRuler;
import com.rong360.crawler.ds.service.ILoginService;
import com.rong360.crawler.ds.util.DsResponseUtil;
import com.rong360.crawler.ds.util.Util;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.proxy.ip.ProxyIp;
import com.rong360.crawler.proxy.ip.pool.ProxyIpPool;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.util.NoticeUtils;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiongwei
 * @ClassName: TaobaoLoginServiceImpl
 * @Description:淘宝登录接口实现
 * @date 2015-5-4 上午11:46:19
 */
@Service("taoBaoLoginService")
public class TaoBaoLoginServiceImpl implements ILoginService {

    /*****
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(TaoBaoLoginServiceImpl.class);

    private boolean useProxy = (boolean) CrawlerConfigurableProperties.getProperty("tb.use.proxy");

    /*****
     * 下载器
     *****/
    private Fetcher fetcher;


    /*****
     * 淘宝登录首页URL
     *****/
    @SuppressWarnings("unused")
    private static final String LOGIN_PAGE_URL = "https://login.m.taobao.com/login.htm";

    /*****
     * 规则处理
     *****/
    private TaoBaoSavePostDataRuler savePostDataRuler;


    public void setSavePostDataRuler(TaoBaoSavePostDataRuler savePostDataRuler) {
        this.savePostDataRuler = savePostDataRuler;
    }

    /*****
     * 登录成功处理规则
     *****/
    private DSLoginSuccessRuler loginSuccessRuler;

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


    public void setLoginSuccessRuler(DSLoginSuccessRuler loginSuccessRuler) {
        this.loginSuccessRuler = loginSuccessRuler;
    }


    @Override
    public Object login(CrawlerPage crawlerPage) {
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);
        if (crawlerPage.getProxy().getProxyIp() == null) {
            ProxyIp proxyIp = ProxyIpPool.getRong360ProxyIp(crawlerPage.getUriData().getQuery().getUserId(), "qin", "");
            crawlerPage.getProxy().setProxyIp(proxyIp);
        }

        JSONObject result;
        /***** 1. 设置首页获取的Cookie,POST等参数 *****/
        savePostDataRuler.loginRule(crawlerPage);


        Query query = crawlerPage.getUriData().getQuery();

        /***** 2. 如果未访问首页,则先访问首页返回验证码 *****/
        if (query instanceof TaoBaoVerifyUserQuery) {
            crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
            fetcher.innerExecute(crawlerPage);
            result = DsResponseUtil.getReturnObject(0, ErrorCode._10012.getCode(), ErrorCode._10012.getMsg(), crawlerPage.getUriData().getAuthCodeFileName());
            return result;
        }

        String key = "";
        if (query instanceof TaoBaoLoginQuery) {
            TaoBaoLoginQuery taobaoLoginQuery = (TaoBaoLoginQuery) query;
            key = Util.generateRedisKey(crawlerPage.getJob(), taobaoLoginQuery.getLoginName());
        }


        /***** 3. 执行HTTP POST提交登录操作 *****/
        crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);
        fetcher.innerExecute(crawlerPage);

        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        log.info(sourceCode);

        /***** 4. 通过返回结果判断是否登录结果状态 *****/
        if (StringUtils.isNotBlank(sourceCode)) {
            if (sourceCode.contains("为了您的账号安全，请输入验证码。") || sourceCode.contains("验证码错误")) {
                postDataManager.removePostData(key);
                crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
                savePostDataRuler.loginRule(crawlerPage);
                crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                fetcher.innerExecute(crawlerPage);
                result = DsResponseUtil.getReturnObject(0, ErrorCode._10012.getCode(), ErrorCode._10012.getMsg(), crawlerPage.getUriData().getAuthCodeFileName());
                return result;
            } else if (sourceCode.contains("success-tip")) {
                int statusId = ApiWd.saveStatus(
                        new CrawlerStatus(query.getMerchantId(), Module.JD.getModuleName(), query.getUserId(),
                                NoticeUtils.CrawlerStatus.CRAWLING.getStatus()));
                result = DsResponseUtil.getReturnObject(1, ErrorCode._0.getCode(), ErrorCode._0.getMsg(), "");
                result.put("statusId", statusId);
                crawlerPage.setCrawlerStatusId(statusId);
                loginSuccessRuler.innerEexcuteRule(crawlerPage);
                return result;
            } else if (sourceCode.contains("floatNotify") && sourceCode.contains("页面已过期")) {
                crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
                savePostDataRuler.loginRule(crawlerPage);
                crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                fetcher.innerExecute(crawlerPage);
                result = DsResponseUtil.getReturnObject(0, ErrorCode._10010.getCode(), ErrorCode._10010.getMsg(), crawlerPage.getUriData().getAuthCodeFileName());
                return result;
            } else if (sourceCode.contains("floatNotify") && sourceCode.contains("密码和账户名不匹配")) {
                postDataManager.removePostData(key);
                crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
                savePostDataRuler.loginRule(crawlerPage);
                crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                fetcher.innerExecute(crawlerPage);
                result = DsResponseUtil.getReturnObject(0, ErrorCode._10016.getCode(), ErrorCode._10016.getMsg(), crawlerPage.getUriData().getAuthCodeFileName());
                return result;
            } else if (sourceCode.contains("sendCode.htm")) {
                result = DsResponseUtil.getReturnObject(0, ErrorCode._0.getCode(), "安全验证,您的账号价值连城，我们为您保驾护航", "");
                result.put("phone", "1**********");
                PostData postData = postDataManager.getPostData(key);
                if (postData == null) {
                    return result;
                }
                postData.getParams().clear();
                postDataManager.saveOrUpdate(postData);
                TaoBaoSendMsgQuery taobaoSendMsgQuery = new TaoBaoSendMsgQuery();
                try {
                    BeanUtils.copyProperties(taobaoSendMsgQuery, query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                crawlerPage.getUriData().setQuery(taobaoSendMsgQuery);
                savePostDataRuler.sendMsgRule(crawlerPage);
                return result;
            }
        }
        postDataManager.removePostData(key);
        result = DsResponseUtil.getReturnObject(0, ErrorCode._10007.getCode(), ErrorCode._10007.getMsg(), "");
        return result;
    }

    @Override
    public Object sendMsg(CrawlerPage crawlerPage) {
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);
        JSONObject result = new JSONObject();
        /***** 1. 设置首页获取的Cookie,POST等参数 *****/
        savePostDataRuler.sendMsgRule(crawlerPage);
        crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);

        /***** 2. 执行HTTP POST提交发送动态密码操作 *****/
        fetcher.innerExecute(crawlerPage);
        String sourceCode = crawlerPage.getSourceData().getSourceCode();


        Query query = crawlerPage.getUriData().getQuery();
        String key = "";
        if (query instanceof TaoBaoSendMsgQuery) {
            TaoBaoSendMsgQuery taoBaoSendMsgQuery = (TaoBaoSendMsgQuery) query;
            key = crawlerPage.getJob().toString() + "_" + taoBaoSendMsgQuery.getLoginName();
        }

        log.info(sourceCode);

        /***** 3. 通过返回结果判断是否登录结果状态 *****/
        if (StringUtils.isNotBlank(sourceCode)) {
            if (sourceCode.contains("floatNotify") && sourceCode.contains("页面已过期")) {
                result.put("status", 0);
                result.put("errorcode", ErrorCode._0.getCode());
                return result;
            } else if (sourceCode.contains("<span>二次验证</span>") || sourceCode.contains("auth-code-txt")) {
                result.put("status", 1);
                result.put("errorcode", ErrorCode._0.getCode());
                PostData postData = postDataManager.getPostData(key);
                if (postData == null) {
                    return result;
                }
                postData.getParams().clear();
                postDataManager.saveOrUpdate(postData);
                TaoBaoVerifyMsgQuery taoBaoVerifyMsgQuery = new TaoBaoVerifyMsgQuery();
                try {
                    BeanUtils.copyProperties(taoBaoVerifyMsgQuery, query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                crawlerPage.getUriData().setQuery(taoBaoVerifyMsgQuery);
                savePostDataRuler.verifyMsgRule(crawlerPage);
                return result;
            }
        }
        result.put("errorcode", ErrorCode._10007.getCode());
        result.put("errorMsg", ErrorCode._10007.getMsg());
        return result;
    }

    @Override
    public Object verifyPhoneCode(CrawlerPage crawlerPage) {
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);
        JSONObject result = new JSONObject();
        try {
            /***** 1. 设置首页获取的Cookie,POST等参数 *****/
            savePostDataRuler.verifyMsgRule(crawlerPage);

            /***** 2. 执行HTTP POST提交验证动态密码操作 *****/
            crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);
            fetcher.innerExecute(crawlerPage);
            String sourceCode = crawlerPage.getSourceData().getSourceCode();
            log.info(sourceCode);
            /***** 3. 通过返回结果判断是否登录结果状态 *****/
            if (StringUtils.isNotBlank(sourceCode)) {
                if (sourceCode.contains("floatNotify") && sourceCode.contains("页面已过期")) {
                    result.put("status", 0);
                    result.put("errorcode", ErrorCode._0.getCode());
                    return result;
                } else if (sourceCode.contains("<span>二次验证</span>")) {
                    result.put("status", 0);
                    result.put("errorcode", ErrorCode._0.getCode());
                    return result;
                } else if (sourceCode.contains("success-tip")) {
                    Query query = crawlerPage.getUriData().getQuery();
                    int statusId = ApiWd.saveStatus(
                            new CrawlerStatus(query.getMerchantId(), Module.JD.getModuleName(), query.getUserId(),
                                    NoticeUtils.CrawlerStatus.CRAWLING.getStatus()));
                    result = DsResponseUtil.getReturnObject(1, ErrorCode._0.getCode(), ErrorCode._0.getMsg(), "");
                    result.put("statusId", statusId);
                    result.put("status", 1);
                    result.put("errorcode", ErrorCode._0.getCode());
                    crawlerPage.setCrawlerStatusId(statusId);
                    loginSuccessRuler.innerEexcuteRule(crawlerPage);
                    return result;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("errorcode", ErrorCode._20001.getCode());
        return result;
    }

    @Override
    public Object verifyUser(CrawlerPage crawlerPage) {
        ProxyIp proxyIp = ProxyIpPool.getRong360ProxyIp(crawlerPage.getUriData().getQuery().getUserId(), "qin", "");
        crawlerPage.getProxy().setProxyIp(proxyIp);
        crawlerPage.getProxy().setUseProxy(useProxy);
        crawlerPage.getProxy().setUseSameProxy(true);
        JSONObject result = new JSONObject();
        try {
            /*****1. 访问淘宝登录首页 获取此页Cookie与登录提交信息*****/
            savePostDataRuler.verifyUserRule(crawlerPage);

            /*****2. 返回下载到地验证码图片地址 *****/
            if (StringUtils.isNotBlank(crawlerPage.getUriData().getAuthCodeURL())) {
                result.put("errorcode", 0);
                result.put("url", crawlerPage.getUriData().getAuthCodeFileName());
                crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                fetcher.innerExecute(crawlerPage);
            } else {
                result.put("errorcode", 0);
                result.put("url", "");
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("errorcode", ErrorCode._10007.getCode());
        result.put("url", "");
        return result;
    }
}
