package com.rong360.crawler.ds.rule.impl;

import com.rong360.crawler.CrawlerConfigurableProperties;
import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.bean.Task;
import com.rong360.crawler.cookie.PostData;
import com.rong360.crawler.cookie.manager.PostDataManager;
import com.rong360.crawler.ds.encrypt.TaobaoRSAPC;
import com.rong360.crawler.ds.query.impl.TaoBaoPCLoginQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoPCSendMsgQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoPCVerifyMsgQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoPCVerifyUserQuery;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.rule.AbstractRulor;
import com.rong360.crawler.util.RegExpUtil;
import com.rong360.crawler.util.URIUtils;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author xiongwei
 * @ClassName: TaobaoSavePostDataRuler
 * @Description:保存登录时获取到的提交信息
 * @date 2015-5-4 上午11:34:06
 */
public class TaoBaoPCSavePostDataRuler extends AbstractRulor {


    /*****
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(TaoBaoPCSavePostDataRuler.class);


    /*****
     * 淘宝登录首页URL
     *****/
    private static final String LOGIN_PAGE_URL = "https://login.taobao.com/member/login.jhtml";

    /*****
     * 淘宝验证验证码接口URL
     *****/
    private static final String AUTH_CODE_RUL = "https://login.taobao.com/member/request_nick_check.do?_input_charset=utf-8";

    /*****
     * POST提交参数管理器
     *****/
    private PostDataManager postDataManager;

    public void setPostDataManager(PostDataManager postDataManager) {
        this.postDataManager = postDataManager;
    }

    {
        this.job = Job.TAOBAO;
        this.task = Task.LOGIN;
    }

    @Override
    public boolean rule(CrawlerPage crawlerPage) {

        /***** 用户名 *****/
        String loginName = "";
        Query query = crawlerPage.getUriData().getQuery();

        if (query instanceof TaoBaoPCVerifyUserQuery) {
            /***** 1.设置本次请求参数信息 *****/
            TaoBaoPCVerifyUserQuery taoBaoPCVerifyUserQuery = (TaoBaoPCVerifyUserQuery) query;
            verifyUserRule(crawlerPage, taoBaoPCVerifyUserQuery);
            return true;
        } else if (query instanceof TaoBaoPCSendMsgQuery) {
            TaoBaoPCSendMsgQuery taoBaoPCSendMsgQuery = (TaoBaoPCSendMsgQuery) query;
            /***** 2.设置本次发送手机短信参数信息 *****/
            String key = job.toString() + "_" + taoBaoPCSendMsgQuery.getLoginName();
            PostData postData = postDataManager.getPostData(key);
            if (postData == null) {
                log.warn("cannot get post data by key:" + key);
                return false;
            } else if (postData.getParams().isEmpty()) {
                extractSendCodePageInfo(crawlerPage, postData);
                return true;
            } else {
                if (postData.getProxyIp() != null) {
                    crawlerPage.getProxy().setProxyIp(postData.getProxyIp());
                }
                String cookie = postData.getTempCookieString();
                postData.getParams().put("target", "securityPhone");
                postData.getParams().put("safePhoneNum", taoBaoPCSendMsgQuery.getPhone());
                String sendCodeUrl = postData.getParams().get("sendCodeUrl");
                UriData uriData = crawlerPage.getUriData();
                if (StringUtils.isBlank(sendCodeUrl)) {
                    log.warn("send code url is blank");
                    return false;
                } else {
                    uriData.setUri(URIUtils.getHttpURL(sendCodeUrl));
                    crawlerPage.getUriData().getParams().put("checkType", "phone");
                    crawlerPage.getUriData().getParams().put("target", "securityPhone");
                    crawlerPage.getUriData().getParams().put("safePhoneNum", taoBaoPCSendMsgQuery.getPhone());
                    crawlerPage.getUriData().getParams().put("checkCode", "");
                    JSONArray phone = JSONArray.fromObject(postData.getParams().get("phone"));
                    for (int i = 0; i < phone.size(); i++) {
                        if (taoBaoPCSendMsgQuery.getPhone().equals(phone.getJSONObject(i).getString("name"))) {
                            String target = phone.getJSONObject(i).getString("code");
                            postData.getParams().put("target", target);
                            crawlerPage.getUriData().getParams().put("target", target);
                            crawlerPage.getUriData().getParams().put("safePhoneNum", "");
                            crawlerPage.getUriData().getParams().put("checkCode", "");
                            break;
                        }
                    }
                    if (crawlerPage.getProxy().getProxyIp() != null) {
                        postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
                    }
                    postDataManager.saveOrUpdate(postData);
                    crawlerPage.getUriData().setCookieString(cookie);
                    return true;
                }
            }
        } else if (query instanceof TaoBaoPCVerifyMsgQuery) {
            TaoBaoPCVerifyMsgQuery taoBaoPCVerifyMsgQuery = (TaoBaoPCVerifyMsgQuery) query;
            String key = job.toString() + "_" + taoBaoPCVerifyMsgQuery.getLoginName();
            PostData postData = postDataManager.getPostData(key);

            if (postData == null) {
                loginName = taoBaoPCVerifyMsgQuery.getLoginName();
                String cookie = crawlerPage.getUriData().getCookieString();
                postData = new PostData(job, loginName, cookie);
                if (crawlerPage.getProxy().getProxyIp() != null) {
                    postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
                }
                postDataManager.saveOrUpdate(postData);
                log.warn("taobao verify msg post data is null");
            } else {
                if (postData.getProxyIp() != null) {
                    crawlerPage.getProxy().setProxyIp(postData.getProxyIp());
                }
                String cookie = postData.getTempCookieString();
                String checkCodeUrl = postData.getParams().get("checkCodeUrl");
                if (StringUtils.isBlank(checkCodeUrl)) {
                    TaoBaoPCSendMsgQuery taoBaoPCSendMsgQuery = new TaoBaoPCSendMsgQuery();
                    crawlerPage.getUriData().setQuery(taoBaoPCSendMsgQuery);
                    return false;
                }
                UriData uriData = crawlerPage.getUriData();
                uriData.setUri(URIUtils.getHttpURL(checkCodeUrl));
                crawlerPage.getUriData().getParams().put("target", postData.getParams().get("target"));
                crawlerPage.getUriData().getParams().put("checkType", "phone");
                crawlerPage.getUriData().getParams().put("safePhoneNum", postData.getParams().get("safePhoneNum"));
                crawlerPage.getUriData().getParams().put("checkCode", taoBaoPCVerifyMsgQuery.getPhoneCode());
                crawlerPage.getUriData().setCookieString(cookie);
            }
            return true;
        } else if (query instanceof TaoBaoPCLoginQuery) {
            TaoBaoPCLoginQuery taoBaoPCLoginQuery = (TaoBaoPCLoginQuery) query;
            String key = job.toString() + "_" + taoBaoPCLoginQuery.getLoginName();
            PostData postData = postDataManager.getPostData(key);
            if (postData != null) {
                loginRule(crawlerPage, taoBaoPCLoginQuery);
            } else {
//				if (postData.getProxyIp() != null) {
//					crawlerPage.getProxy().setProxyIp(postData.getProxyIp());
//				}
                //重新获取验证码
                TaoBaoPCVerifyUserQuery taoBaoPCVerifyUserQuery = new TaoBaoPCVerifyUserQuery();
                try {
                    BeanUtils.copyProperties(taoBaoPCVerifyUserQuery, taoBaoPCLoginQuery);
                    crawlerPage.getUriData().setQuery(taoBaoPCVerifyUserQuery);
                    verifyUserRule(crawlerPage, taoBaoPCVerifyUserQuery);
                    //如果不需要验证码，尝试登陆
                    if (StringUtils.isBlank(crawlerPage.getUriData().getAuthCodeURL())) {
                        crawlerPage.getUriData().setQuery(taoBaoPCLoginQuery);
                        loginRule(crawlerPage, taoBaoPCLoginQuery);
                    }
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }
        return true;
    }

    /**
     * 用户名验证规则
     *
     * @param crawlerPage
     */
    private void verifyUserRule(CrawlerPage crawlerPage, TaoBaoPCVerifyUserQuery taoBaoPCVerifyUserQuery) {

        Fetcher fetcher = new Fetcher();
        /***** 1. 访问淘宝登录首页 获取此页Cookie与登录提交信息 *****/
        UriData uriData = crawlerPage.getUriData();
        uriData.setUri(URIUtils.getHttpURL(LOGIN_PAGE_URL));
        crawlerPage.setSourceData(new SourceData());

        fetcher.innerExecute(crawlerPage);
        crawlerPage.setTask(Task.LOGIN);

        /***** 用户名 *****/
        String loginName = "";
        String cookie = crawlerPage.getUriData().getCookieString();
        loginName = taoBaoPCVerifyUserQuery.getLoginName();
        PostData postData = new PostData(job, loginName, cookie);
        extractFrontPageInfo(crawlerPage, postData);
        if (crawlerPage.getProxy().getProxyIp() != null) {
            postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
        }
        postDataManager.saveOrUpdate(postData);

        getAuthCodeURL(crawlerPage, taoBaoPCVerifyUserQuery);

    }

    /**
     * 登录规则
     *
     * @param crawlerPage
     */
    private void loginRule(CrawlerPage crawlerPage, TaoBaoPCLoginQuery taoBaoPCLoginQuery) {
        String key = crawlerPage.getJob() + "_" + taoBaoPCLoginQuery.getLoginName();
        PostData postData = postDataManager.getPostData(key);
        if (postData.getProxyIp() != null) {
            crawlerPage.getProxy().setProxyIp(postData.getProxyIp());
        }

        TaobaoRSAPC taobaoRSAPC = new TaobaoRSAPC();
        String J_Exponent = "10001";
        String J_PBK = postData.getParams().get("J_PBK");
        String loginUrl = postData.getParams().get("loginUrl");
        if (StringUtils.isBlank(J_PBK) || StringUtils.isBlank(loginUrl)) {
            TaoBaoPCVerifyUserQuery taoBaoPCVerifyUserQuery = new TaoBaoPCVerifyUserQuery();
            try {
                BeanUtils.copyProperties(taoBaoPCVerifyUserQuery, taoBaoPCLoginQuery);
                crawlerPage.getUriData().setQuery(taoBaoPCVerifyUserQuery);
                verifyUserRule(crawlerPage, taoBaoPCVerifyUserQuery);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        } else {
            String TPL_password2 = taobaoRSAPC.encryptedString(J_Exponent, J_PBK, taoBaoPCLoginQuery.getPassword());
            UriData uriData = crawlerPage.getUriData();
            uriData.setUri(URIUtils.getHttpURL(loginUrl));

            /***** 2.取出所有POST参数赋予给crawlerPage *****/
            for (Map.Entry<String, String> entry : postData.getParams().entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                if (!"loginUrl".equalsIgnoreCase(name) && !"J_PBK".equalsIgnoreCase(name)) {
                    crawlerPage.getUriData().getParams().put(name, value);
                }
            }
            crawlerPage.getUriData().getParams().put("ua",
                    "008#JxEAdAASAqcAAAAAADM4ABYBAAQ3AQEBBgEABzdAAAEBlqsWAQAENwEBAQYBAAc3EAEBAZYKBgEABzd6AQEBhqIHAQAGNwABAYVdBwEABjcBAQEaewMBACg3AQGYAQEBAQEBBEQBAQJ+AQEEVwEBAgEBAQRXAQED2QEBAQEBAQEBCwEBqTe6AetpdXVxcjsuLm1uZmhvL3VgbmNgbi9ibmwubGRsY2RzLm1uZmhvL2tpdWxtPnNkZWhzZGJ1VFNNPGl1dXFyJDM0MkAkMzQzRyQzNDNHY3R4ZHN1c2BlZC91YG5jYG4vYm5sJDM0M0d1c2BlZCQzNDNHaHVkbG1ocnUkMzQzR21ocnVeY250Zml1Xmh1ZGxyL2l1bCQzNDJHcnFsJDM0MkVgMHsxOC8wMy8wODg2NDM0MTU0LzMvYnRoYFFwaXV1cXI7Li5tbmZoby91YG5jYG4vYm5sLmxkbGNkcy5tbmZudHUva2l1bG0+cnFsPGAwezE4LzMvNjQ1OTg1NTI2LzQvcjZFVjg2J2c8dW5xJ250dTx1c3RkJ3NkZWhzZGJ1VFNNPGl1dXFyJDM0MkAkMzQzRyQzNDNHY3R4ZHN1c2BlZC91YG5jYG4vYm5sJDM0M0d1c2BlZCQzNDNHaHVkbG1ocnUkMzQzR21ocnVeY250Zml1Xmh1ZGxyL2l1bCQzNDJHcnFsJDM0MkVgMHsxOC8wMy8wODg2NDM0MTU0LzMvYnRoYFFwDAEANwEwNTU3NjAwNzc4NDE4OzE1NGRiM2MxMjdkNGQxMDVnZzg0N2BjZzVkZzEzNjMyOGA3NmBgMTECAQAGNAEDADI3FwEABDcBAfwUAQAGNwMl7TykAQEACDcBAFHXvT9tEwEACDcAwwECfGhnFgEABDcBAQEWAQAENwEBAQcBAAY3AQEBm1A=");

            crawlerPage.getUriData().getParams().put("TPL_checkcode", taoBaoPCLoginQuery.getAuthcode());
            crawlerPage.getUriData().getParams().put("TPL_username", taoBaoPCLoginQuery.getLoginName());
            crawlerPage.getUriData().getParams().put("TPL_password_2", TPL_password2);
            String cookie = postData.getTempCookieString();
            crawlerPage.getUriData().setCookieString(cookie);
        }
    }

    /***
     * 提取首页页面信息
     *
     * @param crawlerPage
     */
    public void extractFrontPageInfo(CrawlerPage crawlerPage, PostData postData) {
        String sourceCode = crawlerPage.getSourceData().getSourceCode();
//		log.info("source:" + sourceCode);
        String TPL_password = "";
        String loginUrl = "";
        String TPL_checkcode = "";
        String loginsite = "";
        String newlogin = "";
        String TPL_redirect_url = "https://i.taobao.com/my_taobao.htm";
        String from = "";
        String fc = "";
        String style = "";
        String css_style = "";
        String keyLogin = "";
        String qrLogin = "";
        String newMini = "";
        String tid = "";
        String support = "";
        String CtrlVersion = "";
        String loginType = "";
        String minititle = "";
        String minipara = "";
        String umto = "";
        String pstrong = "";
        String llnick = "";
        String sign = "";
        String need_sign = "";
        String isIgnore = "";
        String full_redirect = "";
        String popid = "";
        String callback = "";
        String guf = "";
        String not_duplite_str = "";
        String need_user_id = "";
        String poy = "";
        String gvfdcname = "";
        String gvfdcre = "";
        String from_encoding = "";
        String sub = "";
        String loginASR = "";
        String loginASRSuc = "";
        String allp = "";
        String oslanguage = "zh-CN";
        String sr = "1366*768";
        String osVer = "windows|6.1";
        String naviVer = "chrome|42.02311152";
        String ua = "";
        String ncoSessionid = "";
        String ncoSig = "";
        String ncoToken = "";
        String slideCodeShow = "false";
        String useSlideCheckcode = "false";
        Source source = new Source(sourceCode);

        List<Element> elements = source.getAllElements("name", "loginsite",
                true);
        if (!elements.isEmpty()) {
            loginsite = elements.get(0).getAttributeValue("value");
        }
        log.info("loginsite=" + loginsite);

        Element element = source.getElementById("J_StaticForm");
        elements = source.getAllElements("method", "post", true);
        if (!elements.isEmpty()) {
            loginUrl = "https://login.taobao.com" + elements.get(0).getAttributeValue("action");
        }
//        loginUrl = "https://login.taobao.com" + element.getAttributeValue("action");
        log.info("loginUrl=" + loginUrl);

        elements = source.getAllElements("name", "TPL_checkcode", true);
        if (!elements.isEmpty()) {
            // TPL_checkcode = elements.get(0).getAttributeValue("value");
        }
        log.info("TPL_checkcode=" + TPL_checkcode);

        elements = source.getAllElements("name", "newlogin", true);
        if (!elements.isEmpty()) {
            newlogin = elements.get(0).getAttributeValue("value");
        }
        log.info("newlogin=" + newlogin);

        elements = source.getAllElements("name", "TPL_redirect_url", true);
        if (!elements.isEmpty()) {
            // TPL_redirect_url = elements.get(0).getAttributeValue("value");
        }
        log.info("TPL_redirect_url=" + TPL_redirect_url);

        elements = source.getAllElements("name", "from", true);
        if (!elements.isEmpty()) {
            from = elements.get(0).getAttributeValue("value");
        }
        log.info("from=" + from);

        elements = source.getAllElements("name", "fc", true);
        if (!elements.isEmpty()) {
            fc = elements.get(0).getAttributeValue("value");
        }
        log.info("fc=" + fc);

        elements = source.getAllElements("name", "style", true);
        if (!elements.isEmpty()) {
            style = elements.get(0).getAttributeValue("value");
        }
        log.info("style=" + style);

        elements = source.getAllElements("name", "css_style", true);
        if (!elements.isEmpty()) {
            css_style = elements.get(0).getAttributeValue("value");
        }
        log.info("css_style=" + css_style);

        elements = source.getAllElements("name", "keyLogin", true);
        if (!elements.isEmpty()) {
            keyLogin = elements.get(0).getAttributeValue("value");
        }
        log.info("keyLogin=" + keyLogin);

        elements = source.getAllElements("name", "qrLogin", true);
        if (!elements.isEmpty()) {
            qrLogin = elements.get(0).getAttributeValue("value");
        }
        log.info("qrLogin=" + qrLogin);

        elements = source.getAllElements("name", "newMini", true);
        if (!elements.isEmpty()) {
            newMini = elements.get(0).getAttributeValue("value");
        }
        log.info("newMini=" + newMini);

        elements = source.getAllElements("name", "tid", true);
        if (!elements.isEmpty()) {
            // tid = elements.get(0).getAttributeValue("value");
        }
        log.info("tid=" + tid);

        elements = source.getAllElements("name", "support", true);
        if (!elements.isEmpty()) {
            support = elements.get(0).getAttributeValue("value");
        }
        log.info("support=" + support);

        elements = source.getAllElements("name", "CtrlVersion", true);
        if (!elements.isEmpty()) {
            CtrlVersion = elements.get(0).getAttributeValue("value");
        }
        log.info("CtrlVersion=" + CtrlVersion);

        // 1

        elements = source.getAllElements("name", "loginType", true);
        if (!elements.isEmpty()) {
            loginType = elements.get(0).getAttributeValue("value");
        }
        log.info("loginType=" + loginType);

        elements = source.getAllElements("name", "minititle", true);
        if (!elements.isEmpty()) {
            minititle = elements.get(0).getAttributeValue("value");
        }
        log.info("minititle=" + minititle);

        elements = source.getAllElements("name", "minipara", true);
        if (!elements.isEmpty()) {
            minipara = elements.get(0).getAttributeValue("value");
        }
        log.info("minipara=" + minipara);

        elements = source.getAllElements("name", "umto", true);
        if (!elements.isEmpty()) {
            umto = elements.get(0).getAttributeValue("value");
        }
        log.info("umto=" + umto);

        elements = source.getAllElements("name", "pstrong", true);
        if (!elements.isEmpty()) {
            pstrong = elements.get(0).getAttributeValue("value");
        }
        log.info("pstrong=" + pstrong);

        elements = source.getAllElements("name", "llnick", true);
        if (!elements.isEmpty()) {
            llnick = elements.get(0).getAttributeValue("value");
        }
        log.info("llnick=" + llnick);

        elements = source.getAllElements("name", "sign", true);
        if (!elements.isEmpty()) {
            sign = elements.get(0).getAttributeValue("value");
        }
        log.info("sign=" + sign);

        elements = source.getAllElements("name", "need_sign", true);
        if (!elements.isEmpty()) {
            need_sign = elements.get(0).getAttributeValue("value");
        }
        log.info("need_sign=" + need_sign);

        elements = source.getAllElements("name", "isIgnore", true);
        if (!elements.isEmpty()) {
            isIgnore = elements.get(0).getAttributeValue("value");
        }
        log.info("isIgnore=" + isIgnore);

        // 2

        elements = source.getAllElements("name", "full_redirect", true);
        if (!elements.isEmpty()) {
            full_redirect = elements.get(0).getAttributeValue("value");
        }
        log.info("full_redirect=" + full_redirect);

        elements = source.getAllElements("name", "popid", true);
        if (!elements.isEmpty()) {
            popid = elements.get(0).getAttributeValue("value");
        }
        log.info("popid=" + popid);

        elements = source.getAllElements("name", "callback", true);
        if (!elements.isEmpty()) {
            callback = elements.get(0).getAttributeValue("value");
        }
        log.info("callback=" + callback);

        elements = source.getAllElements("name", "guf", true);
        if (!elements.isEmpty()) {
            guf = elements.get(0).getAttributeValue("value");
        }
        log.info("guf=" + guf);

        elements = source.getAllElements("name", "not_duplite_str", true);
        if (!elements.isEmpty()) {
            not_duplite_str = elements.get(0).getAttributeValue("value");
        }
        log.info("not_duplite_str=" + not_duplite_str);

        elements = source.getAllElements("name", "need_user_id", true);
        if (!elements.isEmpty()) {
            need_user_id = elements.get(0).getAttributeValue("value");
        }
        log.info("need_user_id=" + need_user_id);

        elements = source.getAllElements("name", "poy", true);
        if (!elements.isEmpty()) {
            // poy = elements.get(0).getAttributeValue("value");
        }
        log.info("poy=" + poy);

        elements = source.getAllElements("name", "gvfdcname", true);
        if (!elements.isEmpty()) {
            gvfdcname = elements.get(0).getAttributeValue("value");
        }
        log.info("gvfdcname=" + gvfdcname);

        elements = source.getAllElements("name", "gvfdcre", true);
        if (!elements.isEmpty()) {
            gvfdcre = elements.get(0).getAttributeValue("value");
        }
        log.info("gvfdcre=" + gvfdcre);

        elements = source.getAllElements("name", "from_encoding", true);
        if (!elements.isEmpty()) {
            from_encoding = elements.get(0).getAttributeValue("value");
        }
        log.info("from_encoding=" + from_encoding);

        elements = source.getAllElements("name", "sub", true);
        if (!elements.isEmpty()) {
            sub = elements.get(0).getAttributeValue("value");
        }
        log.info("sub=" + sub);

        elements = source.getAllElements("name", "loginASR", true);
        if (!elements.isEmpty()) {
            loginASR = elements.get(0).getAttributeValue("value");
        }
        log.info("loginASR=" + loginASR);

        // 3
        elements = source.getAllElements("name", "loginASRSuc", true);
        if (!elements.isEmpty()) {
            loginASRSuc = elements.get(0).getAttributeValue("value");
        }
        log.info("loginASRSuc=" + loginASRSuc);

        elements = source.getAllElements("name", "allp", true);
        if (!elements.isEmpty()) {
            allp = elements.get(0).getAttributeValue("value");
        }
        log.info("allp=" + allp);

        elements = source.getAllElements("name", "oslanguage", true);
        if (!elements.isEmpty()) {
            // oslanguage = elements.get(0).getAttributeValue("value");
        }
        log.info("oslanguage=" + oslanguage);

        elements = source.getAllElements("name", "sr", true);
        if (!elements.isEmpty()) {
            // sr = elements.get(0).getAttributeValue("value");
        }
        log.info("sr=" + sr);

        elements = source.getAllElements("name", "osVer", true);
        if (!elements.isEmpty()) {
            // osVer = elements.get(0).getAttributeValue("value");
        }
        log.info("osVer=" + osVer);

        elements = source.getAllElements("name", "naviVer", true);
        if (!elements.isEmpty()) {
            // naviVer = elements.get(0).getAttributeValue("value");
        }
        log.info("naviVer=" + naviVer);

        elements = source.getAllElements("name", "ncoToken", true);
        if (!elements.isEmpty()) {
            ncoToken = elements.get(0).getAttributeValue("value");
        }
        log.info("ncoToken=" + ncoToken);

        String J_PBK = "";
        element = source.getElementById("J_PBK");
        J_PBK = element.getAttributeValue("value");
        log.info("J_PBK=" + J_PBK);

        postData.getParams().put("TPL_password", TPL_password);
        postData.getParams().put("J_PBK", J_PBK);
        postData.getParams().put("loginUrl", loginUrl);
        postData.getParams().put("ua", ua);
        postData.getParams().put("TPL_checkcode", TPL_checkcode);
        postData.getParams().put("loginsite", loginsite);
        postData.getParams().put("newlogin", newlogin);
        postData.getParams().put("TPL_redirect_url", TPL_redirect_url);
        postData.getParams().put("from", from);
        postData.getParams().put("fc", fc);
        postData.getParams().put("style", style);
        postData.getParams().put("css_style", css_style);
        postData.getParams().put("keyLogin", keyLogin);
        postData.getParams().put("qrLogin", qrLogin);
        postData.getParams().put("newMini", newMini);
        postData.getParams().put("tid", tid);
        postData.getParams().put("support", support);
        postData.getParams().put("CtrlVersion", CtrlVersion);
        postData.getParams().put("loginType", loginType);
        postData.getParams().put("minititle", minititle);
        postData.getParams().put("minipara", minipara);
        postData.getParams().put("umto", "NaN");
        postData.getParams().put("pstrong", pstrong);
        postData.getParams().put("llnick", llnick);
        postData.getParams().put("sign", sign);
        postData.getParams().put("need_sign", need_sign);
        postData.getParams().put("isIgnore", isIgnore);
        postData.getParams().put("full_redirect", full_redirect);
        postData.getParams().put("popid", popid);
        postData.getParams().put("callback", callback);
        postData.getParams().put("guf", guf);
        postData.getParams().put("not_duplite_str", not_duplite_str);
        postData.getParams().put("need_user_id", need_user_id);
        postData.getParams().put("poy", poy);
        postData.getParams().put("gvfdcname", gvfdcname);
        postData.getParams().put("gvfdcre", gvfdcre);
        postData.getParams().put("from_encoding", from_encoding);
        postData.getParams().put("sub", sub);
        postData.getParams().put("loginASR", "1");
        postData.getParams().put("loginASRSuc", "1");
        postData.getParams().put("allp", allp);
        postData.getParams().put("sr", sr);
        postData.getParams().put("osVer", osVer);
        postData.getParams().put("oslanguage", oslanguage);
        postData.getParams().put("naviVer", naviVer);
        postData.getParams().put("ncoSessionid", ncoSessionid);
        postData.getParams().put("ncoSig", ncoSig);
        postData.getParams().put("ncoToken", ncoToken);
        postData.getParams().put("slideCodeShow", slideCodeShow);
        postData.getParams().put("useSlideCheckcode", useSlideCheckcode);
    }

    public void getAuthCodeURL(CrawlerPage crawlerPage, TaoBaoPCVerifyUserQuery taoBaoPCVerifyUserQuery) {
        /***** 1. 访问淘宝验证是否需要验证码接口 *****/
        UriData uriData = crawlerPage.getUriData();
        uriData.setUri(URIUtils.getHttpURL(AUTH_CODE_RUL));
        crawlerPage.setSourceData(new SourceData());
        crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);
        crawlerPage.getUriData().getParams().put("username",
                taoBaoPCVerifyUserQuery.getLoginName());
        crawlerPage.getUriData().getParams().put("ua",
                "008#JxEAdAASAqcAAAAAADM4ABYBAAQ3AQEBBgEABzdAAAEBlqsWAQAENwEBAQYBAAc3EAEBAZYKBgEABzd6AQEBhqIHAQAGNwABAYVdBwEABjcBAQEaewMBACg3AQGYAQEBAQEBBEQBAQJ+AQEEVwEBAgEBAQRXAQED2QEBAQEBAQEBCwEBqTe6AetpdXVxcjsuLm1uZmhvL3VgbmNgbi9ibmwubGRsY2RzLm1uZmhvL2tpdWxtPnNkZWhzZGJ1VFNNPGl1dXFyJDM0MkAkMzQzRyQzNDNHY3R4ZHN1c2BlZC91YG5jYG4vYm5sJDM0M0d1c2BlZCQzNDNHaHVkbG1ocnUkMzQzR21ocnVeY250Zml1Xmh1ZGxyL2l1bCQzNDJHcnFsJDM0MkVgMHsxOC8wMy8wODg2NDM0MTU0LzMvYnRoYFFwaXV1cXI7Li5tbmZoby91YG5jYG4vYm5sLmxkbGNkcy5tbmZudHUva2l1bG0+cnFsPGAwezE4LzMvNjQ1OTg1NTI2LzQvcjZFVjg2J2c8dW5xJ250dTx1c3RkJ3NkZWhzZGJ1VFNNPGl1dXFyJDM0MkAkMzQzRyQzNDNHY3R4ZHN1c2BlZC91YG5jYG4vYm5sJDM0M0d1c2BlZCQzNDNHaHVkbG1ocnUkMzQzR21ocnVeY250Zml1Xmh1ZGxyL2l1bCQzNDJHcnFsJDM0MkVgMHsxOC8wMy8wODg2NDM0MTU0LzMvYnRoYFFwDAEANwEwNTU3NjAwNzc4NDE4OzE1NGRiM2MxMjdkNGQxMDVnZzg0N2BjZzVkZzEzNjMyOGA3NmBgMTECAQAGNAEDADI3FwEABDcBAfwUAQAGNwMl7TykAQEACDcBAFHXvT9tEwEACDcAwwECfGhnFgEABDcBAQEWAQAENwEBAQcBAAY3AQEBm1A=");

        Fetcher fetcher = new Fetcher();
        crawlerPage.setTask(Task.LOGIN);
        fetcher.innerExecute(crawlerPage);
        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        String authCodeURL = "";

        /***** 2. 通过验证接口结果返回验证码地址 *****/
        if (StringUtils.isNotBlank(sourceCode)) {
            JSONObject jsonObject = JSONObject.fromObject(sourceCode);
            if (jsonObject.getBoolean("needcode")) {
                authCodeURL = jsonObject.getString("url");
                crawlerPage.getUriData().setAuthCodeURL(authCodeURL);
                crawlerPage.getUriData().setAuthCodeFileName(URIUtils.md5URI(authCodeURL));
            }
        }
    }

    /***
     * 提取发送动态密码页面信息
     *
     * @param crawlerPage
     */
    public void extractSendCodePageInfo(CrawlerPage crawlerPage, PostData postData) {
        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        String success = "";
        if (StringUtils.isNotBlank(sourceCode)) {
            Matcher matcher = RegExpUtil.getMatcher(sourceCode, "gotoURL:\"(.*?)\"");
            if (matcher != null && matcher.find()) {
                Fetcher fetcher = new Fetcher();
                String sendCodeUrl = matcher.group(1);
                // sendCodeUrl = "https://login.taobao.com/member/login_unusual.htm?user_num_id=898955534&is";
                UriData uriData = crawlerPage.getUriData();
                uriData.setUri(URIUtils.getHttpURL(sendCodeUrl));
                crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
                fetcher.innerExecute(crawlerPage);
                sourceCode = crawlerPage.getSourceData().getSourceCode();
                log.info("******************************************");
                log.info(crawlerPage.getUriData().getCookieString());
                log.info(sourceCode);
                matcher = RegExpUtil.getMatcher(sourceCode, "\"(https://login.taobao.com/member/login_by_safe.htm\\?.*?)\"");
                if (matcher.find()) {
                    success = matcher.group(1);
                    //success = "https://login.taobao.com/member/login_by_safe.htm?";
                } else {
                    log.warn("cannot match login_by_safe url");
                    log.warn(sourceCode);
                }
                matcher = RegExpUtil.getMatcher(sourceCode, "AQPop.*?url:'(.*?)'");
                if (matcher.find()) {
                    String validateURL = matcher.group(1);
                    //validateURL = "https://aq.taobao.com/durex/validate?param=";
                    uriData = crawlerPage.getUriData();
                    uriData.setUri(URIUtils.getHttpURL(validateURL));
                    crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
                    crawlerPage.getUriData().setReferer(sendCodeUrl);
                    crawlerPage.getUriData().setCookieString(postData.getTempCookieString());
                    fetcher.innerExecute(crawlerPage);
                    sourceCode = crawlerPage.getSourceData().getSourceCode();
                    log.info(sourceCode);
                    if (StringUtils.isNotBlank(sourceCode)) {
                        Source source = new Source(sourceCode);
                        for (Element element : source.getAllElements()) {
                            if ("input".equalsIgnoreCase(element.getName())) {
                                if (StringUtils.isNotBlank(element.getAttributeValue("value"))) {
                                    if (element.getAttributeValue("value").contains("param")) {
                                        String phoneContent = element.getAttributeValue("value");
                                        JSONObject jsonObject = JSONObject.fromObject(phoneContent);
                                        JSONArray optionPhone = jsonObject.getJSONArray("options").getJSONObject(0).getJSONArray("optionText");
                                        String sendCodeURL = "https://aq.taobao.com/durex/sendcode?param=" + jsonObject.getString("param") + "&checkType=phone";
                                        String checkCodeUrl = "https://aq.taobao.com/durex/checkcode?param=" + jsonObject.getString("param");
                                        postData.getParams().put("phone", optionPhone.toString());
                                        postData.getParams().put("sendCodeUrl", sendCodeURL);
                                        postData.getParams().put("checkCodeUrl", checkCodeUrl);
                                    }
                                }
                            }
                        }
                    } else {
                        log.warn("cannot match validateURL");
                        log.warn(sourceCode);
                    }
                } else {
                    log.warn("cannot match AQPop url");
                    log.warn(sourceCode);
                }
            } else {
                log.warn("cannot match sendCodeUrl");
                log.warn(sourceCode);
            }
            postData.getParams().put("success", success);
            if (crawlerPage.getProxy().getProxyIp() != null) {
                postData.setProxyIp(crawlerPage.getProxy().getProxyIp());
            }
            postDataManager.saveOrUpdate(postData);
        } else {
            log.warn("send code page is null");
        }
    }

    /***
     * 提取动态密码验证页面信息
     *
     * @param crawlerPage
     */
    public void extractCheckPageInfo(CrawlerPage crawlerPage, PostData postData) {

        String chockCodeURL = "";
        String action = "";
        String event_submit_do_user_safe_check = "";
        String sid = "";
        String ssottid = "";
        String TPL_username = "";
        String TPL_redirect_url = "";
        String TPL_return_url = "";
        String _umid_token = "";
        String wapCheckId = "";
        String oauth_uri = "";
        String app_key = "";
        String sign = "";
        String checkCode = "";

        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        Source source = new Source(sourceCode);
        log.info(sourceCode);

        List<Element> elements = source.getAllElements("class",
                "c-form login-form", true);
        if (!elements.isEmpty()) {
            chockCodeURL = "http:"
                    + elements.get(0).getAttributeValue("action");
        }

        elements = source.getAllElements("name", "action", true);
        if (!elements.isEmpty()) {
            action = elements.get(0).getAttributeValue("value");
        }
        log.info("action=" + action);

        elements = source.getAllElements("name",
                "event_submit_do_user_safe_check", true);
        if (!elements.isEmpty()) {
            event_submit_do_user_safe_check = elements.get(0)
                    .getAttributeValue("value");
        }
        log.info("event_submit_do_user_safe_check="
                + event_submit_do_user_safe_check);

        elements = source.getAllElements("name", "sid", true);
        if (!elements.isEmpty()) {
            sid = elements.get(0).getAttributeValue("value");
        }
        log.info("sid=" + sid);

        elements = source.getAllElements("name", "ssottid", true);
        if (!elements.isEmpty()) {
            ssottid = elements.get(0).getAttributeValue("value");
        }
        log.info("ssottid=" + ssottid);

        elements = source.getAllElements("name", "TPL_username", true);
        if (!elements.isEmpty()) {
            TPL_username = elements.get(0).getAttributeValue("value");
        }
        log.info("TPL_username=" + TPL_username);

        elements = source.getAllElements("name", "TPL_return_url", true);
        if (!elements.isEmpty()) {
            TPL_return_url = elements.get(0).getAttributeValue("value");
        }
        log.info("TPL_return_url=" + TPL_return_url);

        elements = source.getAllElements("name", "_umid_token", true);
        if (!elements.isEmpty()) {
            _umid_token = elements.get(0).getAttributeValue("value");
        }
        log.info("_umid_token=" + _umid_token);

        elements = source.getAllElements("name", "wapCheckId", true);
        if (!elements.isEmpty()) {
            wapCheckId = elements.get(0).getAttributeValue("value");
        }
        log.info("wapCheckId=" + wapCheckId);

        elements = source.getAllElements("name", "oauth_uri", true);
        if (!elements.isEmpty()) {
            oauth_uri = elements.get(0).getAttributeValue("value");
        }
        log.info("oauth_uri=" + oauth_uri);

        elements = source.getAllElements("name", "app_key", true);
        if (!elements.isEmpty()) {
            app_key = elements.get(0).getAttributeValue("value");
        }
        log.info("app_key=" + app_key);

        elements = source.getAllElements("name", "sign", true);
        if (!elements.isEmpty()) {
            sign = elements.get(0).getAttributeValue("value");
        }
        log.info("sign=" + sign);

        elements = source.getAllElements("name", "checkCode", true);
        if (!elements.isEmpty()) {
            checkCode = elements.get(0).getAttributeValue("value");
        }
        log.info("checkCode=" + checkCode);

        postData.getParams().put("url", chockCodeURL);
        postData.getParams().put("action", action);
        postData.getParams().put("event_submit_do_user_safe_check",
                event_submit_do_user_safe_check);
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

    public static void main(String[] args) {
        String referer = "redirect_url=http%3A%2F%2Flogin.m.taobao.com%2Fsend_code.htm%3Ftoken%3D5d58fa9056c76b09542b30aeea49b38c%26ssottid%3D%26t%3DIS_NEED_2_CHECK%26sid%3D1cf91b8b5d52d646262aa114cfe5e96d&amp;sid=1cf91b8b5d52d646262aa114cfe5e96d";

        referer = referer.substring(referer.indexOf("redirect_url=")
                + "redirect_url=".length(), referer.length());
        log.info(referer);

    }
}
