package com.rong360.crawler.ds.rule.impl;

import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.cookie.PostData;
import com.rong360.crawler.cookie.manager.RedisPostDataManager;
import com.rong360.crawler.ds.encrypt.TaobaoRSAKeyPair;
import com.rong360.crawler.ds.query.impl.*;
import com.rong360.crawler.ds.util.Util;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.proxy.ip.ProxyIp;
import com.rong360.crawler.proxy.ip.pool.ProxyIpPool;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.rule.AbstractRulor;
import com.rong360.crawler.util.URIUtils;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * @author xiongwei
 */
public class TaoBaoSavePostDataRuler extends AbstractRulor {


    /*****
     * 淘宝登录首页URL
     *****/
    private static final String LOGIN_PAGE_URL = "https://login.m.taobao.com/login.htm";

    private static Logger log = Logger.getLogger(TaoBaoSavePostDataRuler.class);

    /*****
     * POST提交参数管理器
     *****/
    private RedisPostDataManager postDataManager;

    public void setPostDataManager(RedisPostDataManager postDataManager) {
        this.postDataManager = postDataManager;
    }

    {
        this.job = Job.TAOBAO;
        this.task = Task.LOGIN;
    }

    @Override
    public boolean rule(CrawlerPage crawlerPage) {
        return true;
    }

    public boolean verifyUserRule(CrawlerPage crawlerPage) {
        Query query = crawlerPage.getUriData().getQuery();
        if (query instanceof TaoBaoVerifyUserQuery) {
            verifyUserRule(crawlerPage, (TaoBaoVerifyUserQuery) query);
            return true;
        } else if (query instanceof TaoBaoLoginQuery) {
            try {
                TaoBaoVerifyUserQuery taoBaoVerifyUserQuery = new TaoBaoVerifyUserQuery();
                TaoBaoLoginQuery taobaoLoginQuery = (TaoBaoLoginQuery) query;
                BeanUtils.copyProperties(taoBaoVerifyUserQuery, taobaoLoginQuery);
                crawlerPage.getUriData().setQuery(taoBaoVerifyUserQuery);
                verifyUserRule(crawlerPage, taoBaoVerifyUserQuery);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用户名验证规则
     *
     * @param crawlerPage           上下文
     * @param taoBaoVerifyUserQuery 淘宝获取验证码参数
     */
    private void verifyUserRule(CrawlerPage crawlerPage, TaoBaoVerifyUserQuery taoBaoVerifyUserQuery) {

        Fetcher fetcher = new Fetcher();
        /*****1. 访问宝登登录首页 获取此页Cookie与登录提交信息*****/
        UriData uriData = crawlerPage.getUriData();
        uriData.setUri(URIUtils.getHttpURL(LOGIN_PAGE_URL));
        crawlerPage.setUriData(uriData);
        crawlerPage.setSourceData(new SourceData());

        fetcher.innerExecute(crawlerPage);
        crawlerPage.setTask(Task.LOGIN);

        /***** 用户名 *****/
        String loginName;
        String cookie = crawlerPage.getUriData().getCookieString();
        loginName = taoBaoVerifyUserQuery.getLoginName();
        PostData postData = new PostData(job, loginName, cookie);
        extractFrontPageInfo(crawlerPage, postData);
        if (crawlerPage.getProxy().getProxyIp() != null) {
            postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
        }
        postDataManager.saveOrUpdate(postData);

    }

    public boolean sendMsgRule(CrawlerPage crawlerPage) {
        Query query = crawlerPage.getUriData().getQuery();
        String loginName;
        /***** 2.设置本次发送手机短信参数信息 *****/
        String key = job.toString() + "_" + query.getLoginName();
        PostData postData = postDataManager.getPostData(key);
        if (postData == null) {
            loginName = query.getLoginName();
            String cookie = crawlerPage.getUriData().getCookieString();
            postData = new PostData(job, loginName, cookie);
            if (crawlerPage.getProxy().getProxyIp() != null) {
                postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
            }
            postDataManager.saveOrUpdate(postData);
        } else if (postData.getParams().isEmpty()) {
            extractSendCodePageInfo(crawlerPage, postData);
        } else {
            if (postData.getProxyIp() != null) {
                crawlerPage.getProxy().setProxyIp(postData.getProxyIp());
            }
            String cookie = postData.getTempCookieString();
            String url = postData.getParams().get("url");
            UriData uriData = crawlerPage.getUriData();
            uriData.setUri(URIUtils.getHttpURL(url));
            copyParam(crawlerPage, postData);
            crawlerPage.getUriData().setCookieString(cookie);
        }
        return true;
    }

    public boolean verifyMsgRule(CrawlerPage crawlerPage) {
        Query query = crawlerPage.getUriData().getQuery();
        TaoBaoVerifyMsgQuery taoBaoVerifyMsgQuery = (TaoBaoVerifyMsgQuery) query;
        String key = job.toString() + "_" + taoBaoVerifyMsgQuery.getLoginName();
        PostData postData = postDataManager.getPostData(key);

        if (postData == null) {
            String loginName = taoBaoVerifyMsgQuery.getLoginName();
            String cookie = crawlerPage.getUriData().getCookieString();
            postData = new PostData(job, loginName, cookie);
            if (crawlerPage.getProxy().getProxyIp() != null) {
                postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
            }
            postDataManager.saveOrUpdate(postData);
        } else if (postData.getParams().isEmpty()) {
            extractCheckPageInfo(crawlerPage, postData);
        } else {
            if (postData.getProxyIp() != null) {
                crawlerPage.getProxy().setProxyIp(postData.getProxyIp());
            }
            String cookie = postData.getTempCookieString();
            String url = postData.getParams().get("url");
            UriData uriData = crawlerPage.getUriData();
            uriData.setUri(URIUtils.getHttpURL(url));
            copyParam(crawlerPage, postData);
            crawlerPage.getUriData().getParams().put("checkCode", taoBaoVerifyMsgQuery.getPhoneCode());
            crawlerPage.getUriData().setCookieString(cookie);
        }
        return true;
    }

    public boolean loginRule(CrawlerPage crawlerPage) {
        Query query = crawlerPage.getUriData().getQuery();
        TaoBaoLoginQuery taobaoLoginQuery = (TaoBaoLoginQuery) query;
        String key = job.toString() + "_" + taobaoLoginQuery.getLoginName();
        PostData postData = postDataManager.getPostData(key);
        if (postData != null) {
            if (!isPassedStep1(postData.getParams())) {
                verifyUserRule(crawlerPage);
            } else {
                loginRule(crawlerPage, taobaoLoginQuery);
            }
            return true;
        } else {
            verifyUserRule(crawlerPage);
            return false;
        }
    }

    /**
     * 登录规则
     *
     * @param crawlerPage      上下文
     * @param taobaoLoginQuery 淘宝登陆参数
     */
    private void loginRule(CrawlerPage crawlerPage, TaoBaoLoginQuery taobaoLoginQuery) {
        String key = crawlerPage.getJob() + "_" + taobaoLoginQuery.getLoginName();
        PostData postData = postDataManager.getPostData(key);

        TaobaoRSAKeyPair taobaoRSAKeyPair = new TaobaoRSAKeyPair();
        String J_Exponent = postData.getParams().get("J_Exponent");
        String J_Module = postData.getParams().get("J_Module");
        String loginUrl = postData.getParams().get("loginUrl");
        String TPL_password2 = taobaoRSAKeyPair.encryptedString(J_Exponent, J_Module, taobaoLoginQuery.getPassword());
        UriData uriData = crawlerPage.getUriData();
        uriData.setUri(URIUtils.getHttpURL(loginUrl));
        postData.getParams().remove("loginUrl");
        postData.getParams().remove("J_Exponent");
        postData.getParams().remove("J_Module");

        /***** 2.取出所有POST参数赋予给crawlerPage *****/
        crawlerPage.getUriData().setParams(postData.getParams());
        crawlerPage.getUriData().getParams().put("TPL_checkcode", taobaoLoginQuery.getAuthcode());
        crawlerPage.getUriData().getParams().put("TPL_username", taobaoLoginQuery.getLoginName());
        crawlerPage.getUriData().getParams().put("TPL_password2", TPL_password2);
        String cookie = postData.getTempCookieString();
        crawlerPage.getUriData().setCookieString(cookie);
        if (crawlerPage.getProxy().getProxyIp() != null) {
            postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
        }
        postDataManager.saveOrUpdate(postData);
    }


    /***
     * 提取首页页面信息
     *
     * @param crawlerPage 上下文
     * @param postData    cookie
     */
    private void extractFrontPageInfo(CrawlerPage crawlerPage, PostData postData) {


        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        Source source = new Source(sourceCode);
        Element element;
        log.info(source);
        String ua = "";
//        element = source.getElementById("UA_InputId");
//         ua = element.getAttributeValue("value");
        log.info("ua=" + ua);

        element = source.getElementById("J_StandardCode");
        String authcodeUrl = "";
        if (element != null) {
            authcodeUrl = "http:" + element.getAttributeValue("src");
        } else {
            List<Element> elements = source.getAllElements("alt", "验证码", true);
            if (!elements.isEmpty()) {
                authcodeUrl = "http:" + elements.get(0).getAttributeValue("value");
            }
        }
        log.info("authcodeUrl=" + authcodeUrl);

        element = source.getElementById("J_Login");
        String loginUrl = "";
        if (element != null) {
            loginUrl = element.getAttributeValue("action");
        } else {
            List<Element> elements = source.getAllElements("method", "post", true);
            if (!elements.isEmpty()) {
                loginUrl = elements.get(0).getAttributeValue("action");
            }
        }
        log.info("loginUrl=" + loginUrl);

        element = source.getElementById("J_Exponent");
        String J_Exponent = element.getAttributeValue("value");
        log.info("J_Exponent=" + J_Exponent);

        element = source.getElementById("J_Module");
        String J_Module = element.getAttributeValue("value");
        log.info("J_Module=" + J_Module);

        String _tb_token_ = getValueOfFirstElement(source, "_tb_token_");
        log.info("_tb_token_=" + _tb_token_);


        String TPL_checkcode = getValueOfFirstElement(source, "TPL_checkcode");
        log.info("TPL_checkcode=" + TPL_checkcode);

        String need_check_code = getValueOfFirstElement(source, "need_check_code");
        log.info("need_check_code=" + need_check_code);

        String wapCheckId = getValueOfFirstElement(source, "wapCheckId");
        log.info("wapCheckId=" + wapCheckId);

        String action = getValueOfFirstElement(source, "action");
        log.info("action=" + action);

        String event_submit_do_login = getValueOfFirstElement(source, "event_submit_do_login");
        log.info("event_submit_do_login=" + event_submit_do_login);

//        TPL_redirect_url = getValueOfFirstElement(source, "TPL_redirect_url");
        String TPL_redirect_url = "http://h5.m.taobao.com/awp/mtb/mtb.htm?#!/awp/mtb/mtb.htm";
        log.info("TPL_redirect_url=" + TPL_redirect_url);

        String sid = getValueOfFirstElement(source, "sid");
        log.info("sid=" + sid);

        String _umid_token = getValueOfFirstElement(source, "_umid_token");
        log.info("_umid_token=" + _umid_token);

        String TPL_timestamp = getValueOfFirstElement(source, "TPL_timestamp");
        log.info("_umid_token=" + TPL_timestamp);


        postData.getParams().put("ua", ua);
        postData.getParams().put("_tb_token_", _tb_token_);
        postData.getParams().put("TPL_checkcode", TPL_checkcode);
        postData.getParams().put("need_check_code", need_check_code);
        postData.getParams().put("wapCheckId", wapCheckId);
        postData.getParams().put("action", action);
        postData.getParams().put("event_submit_do_login", event_submit_do_login);
        postData.getParams().put("TPL_redirect_url", TPL_redirect_url);
        postData.getParams().put("sid", sid);
        postData.getParams().put("_umid_token", _umid_token);
        postData.getParams().put("TPL_timestamp", TPL_timestamp);
        postData.getParams().put("loginUrl", loginUrl);
        postData.getParams().put("J_Exponent", J_Exponent);
        postData.getParams().put("J_Module", J_Module);
        postData.setAuthCodeURL(authcodeUrl);
        if (crawlerPage.getProxy().getProxyIp() != null) {
            postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
        }
        postDataManager.saveOrUpdate(postData);
        if (authcodeUrl.contains("sessionid=") && authcodeUrl.contains("&&type=")) {
            crawlerPage.getUriData().setAuthCodeFileName(authcodeUrl.substring(authcodeUrl.indexOf("sessionid=") + "sessionid=".length(), authcodeUrl.indexOf("&&type=")));
        }
        crawlerPage.getUriData().setAuthCodeURL(authcodeUrl);

    }

    /***
     * 提取发送动态密码页面信息
     *
     * @param crawlerPage 上下文
     * @param postData    cookie
     */
    private void extractSendCodePageInfo(CrawlerPage crawlerPage, PostData postData) {


        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        Source source = new Source(sourceCode);
        //log.info(sourceCode);


        Element element = source.getElementById("J_footer_login");
        String referer = element.getAttributeValue("href");
        if (StringUtils.isNotBlank(referer)) {
            referer = referer.substring(referer.indexOf("redirect_url=") + "redirect_url=".length(), referer.length());
        }
        log.info("referer=" + referer);
        crawlerPage.getUriData().setReferer(referer);

        String send_code = "";
        List<Element> elements = source.getAllElements("class", "c-form login-form", true);
        if (!elements.isEmpty()) {
            send_code = "http:" + elements.get(0).getAttributeValue("action");
        }
        log.info("send_code=" + send_code);

        String action = getValueOfFirstElement(source, "action");
        log.info("action=" + action);

        String event_submit_do_gen_check_code = getValueOfFirstElement(source, "event_submit_do_gen_check_code");
        log.info("event_submit_do_gen_check_code=" + event_submit_do_gen_check_code);

        String sid = getValueOfFirstElement(source, "sid");
        log.info("sid=" + sid);

        String ssottid = getValueOfFirstElement(source, "ssottid");
        log.info("ssottid=" + ssottid);

        String TPL_username = getValueOfFirstElement(source, "TPL_username");
        log.info("TPL_username=" + TPL_username);

        String TPL_return_url = getValueOfFirstElement(source, "TPL_return_url");
        log.info("TPL_return_url=" + TPL_return_url);

        String _umid_token = getValueOfFirstElement(source, "_umid_token");
        log.info("_umid_token=" + _umid_token);

        String wapCheckId = getValueOfFirstElement(source, "wapCheckId");
        log.info("wapCheckId=" + wapCheckId);

        String oauth_uri = getValueOfFirstElement(source, "oauth_uri");
        log.info("oauth_uri=" + oauth_uri);

        String app_key = getValueOfFirstElement(source, "app_key");
        log.info("app_key=" + app_key);

        String sign = getValueOfFirstElement(source, "sign");
        log.info("sign=" + sign);

        String from = getValueOfFirstElement(source, "checkCode");
        log.info("from=" + from);

        String TPL_redirect_url = "";

        postData.getParams().clear();
        postData.getParams().put("url", send_code);
        postData.getParams().put("referer", referer);
        postData.getParams().put("action", action);
        postData.getParams().put("event_submit_do_gen_check_code", event_submit_do_gen_check_code);
        postData.getParams().put("sid", sid);
        postData.getParams().put("ssottid", ssottid);
        postData.getParams().put("TPL_username", TPL_username);
        postData.getParams().put("TPL_redirect_url", TPL_redirect_url);
        postData.getParams().put("TPL_return_url", TPL_return_url);
        postData.getParams().put("_umid_token", _umid_token);
        postData.getParams().put("wapCheckId", wapCheckId);
        postData.getParams().put("oauth_uri", oauth_uri);
        postData.getParams().put("app_key", app_key);
        postData.getParams().put("sign", sign);
        postData.getParams().put("from", from);
        if (crawlerPage.getProxy().getProxyIp() != null) {
            postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
        }
        postDataManager.saveOrUpdate(postData);
        crawlerPage.getUriData().setParams(postData.getParams());
    }


    /***
     * 提取动态密码验证页面信息
     *
     * @param crawlerPage 上下文
     * @param postData    cookie
     */
    private void extractCheckPageInfo(CrawlerPage crawlerPage, PostData postData) {


        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        Source source = new Source(sourceCode);
        log.info(sourceCode);

        String checkCodeUrl = "";
        List<Element> elements = source.getAllElements("class", "c-form login-form", true);
        if (!elements.isEmpty()) {
            checkCodeUrl = "http:" + elements.get(0).getAttributeValue("action");
        }
        log.info("checkCodeUrl=" + checkCodeUrl);

        String action = getValueOfFirstElement(source, "action");
        log.info("action=" + action);

        String event_submit_do_user_safe_check = getValueOfFirstElement(source, "event_submit_do_user_safe_check");
        log.info("event_submit_do_user_safe_check=" + event_submit_do_user_safe_check);

        String sid = getValueOfFirstElement(source, "sid");
        log.info("sid=" + sid);

        String ssottid = getValueOfFirstElement(source, "ssottid");
        log.info("ssottid=" + ssottid);

        String TPL_username = getValueOfFirstElement(source, "TPL_username");
        log.info("TPL_username=" + TPL_username);

        String TPL_return_url = getValueOfFirstElement(source, "TPL_return_url");
        log.info("TPL_return_url=" + TPL_return_url);

        String _umid_token = getValueOfFirstElement(source, "_umid_token");
        log.info("_umid_token=" + _umid_token);

        String wapCheckId = getValueOfFirstElement(source, "wapCheckId");
        log.info("wapCheckId=" + wapCheckId);

        String oauth_uri = getValueOfFirstElement(source, "oauth_uri");
        log.info("oauth_uri=" + oauth_uri);

        String app_key = getValueOfFirstElement(source, "app_key");
        log.info("app_key=" + app_key);

        String sign = getValueOfFirstElement(source, "sign");
        log.info("sign=" + sign);

        String checkCode = getValueOfFirstElement(source, "checkCode");
        log.info("checkCode=" + checkCode);

        String TPL_redirect_url = "";

        postData.getParams().put("url", checkCodeUrl);
        postData.getParams().put("action", action);
        postData.getParams().put("event_submit_do_user_safe_check", event_submit_do_user_safe_check);
        postData.getParams().put("sid", sid);
        postData.getParams().put("ssottid", ssottid);
        postData.getParams().put("TPL_username", TPL_username);
        postData.getParams().put("TPL_redirect_url", TPL_redirect_url);
        postData.getParams().put("TPL_return_url", TPL_return_url);
        postData.getParams().put("_umid_token", _umid_token);
        postData.getParams().put("wapCheckId", wapCheckId);
        postData.getParams().put("oauth_uri", oauth_uri);
        postData.getParams().put("app_key", app_key);
        postData.getParams().put("sign", sign);
        postData.getParams().put("checkCode", checkCode);
        if (crawlerPage.getProxy().getProxyIp() != null) {
            postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
        }
        postDataManager.saveOrUpdate(postData);
    }

    private void copyParam(CrawlerPage crawlerPage, PostData postData) {
        for (Map.Entry<String, String> entry : postData.getParams().entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (!"url".equals(name)) {
                crawlerPage.getUriData().getParams().put(name, value);
            }
        }
    }

    private String getValueOfFirstElement(Source source, String elementName) {
        List<Element> elements = source.getAllElements("name", elementName, true);
        if (!elements.isEmpty()) {
            return elements.get(0).getAttributeValue("value");
        } else {
            return "";
        }
    }

    public static boolean isPassedStep1(Map<String, String> params) {
        return params.containsKey("J_Exponent") && params.containsKey("J_Module") && params.containsKey("loginUrl");
    }

    public static void main(String[] args) {
        String referer = "redirect_url=http%3A%2F%2Flogin.m.taobao.com%2Fsend_code.htm%3Ftoken%3D5d58fa9056c76b09542b30aeea49b38c%26ssottid%3D%26t%3DIS_NEED_2_CHECK%26sid%3D1cf91b8b5d52d646262aa114cfe5e96d&amp;sid=1cf91b8b5d52d646262aa114cfe5e96d";

        referer = referer.substring(referer.indexOf("redirect_url=") + "redirect_url=".length(), referer.length());
        log.info(referer);

    }
}
