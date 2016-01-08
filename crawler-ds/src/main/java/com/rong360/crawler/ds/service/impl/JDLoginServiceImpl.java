package com.rong360.crawler.ds.service.impl;

import com.rong360.crawler.api.ApiWd;
import com.rong360.crawler.bean.*;
import com.rong360.crawler.cookie.PostData;
import com.rong360.crawler.cookie.manager.PostDataManager;
import com.rong360.crawler.ds.query.impl.JingdongLoginQuery;
import com.rong360.crawler.ds.query.impl.JingdongVerifyUserQuery;
import com.rong360.crawler.ds.service.ILoginService;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.processor.IProcessor;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.query.Query;
import com.rong360.crawler.rule.IRuler;
import com.rong360.crawler.util.NoticeUtils;
import com.rong360.crawler.util.URIUtils;
import com.rong360.crawler.util.UnicodeDecoderUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

/**
 * @author xiongwei
 * @ClassName: JDLoginServiceImpl
 * @Description:京东登录接口实现
 * @date 2015-3-20 上午11:46:19
 */
public class JDLoginServiceImpl implements ILoginService {


    /*****
     * 日志记录
     *****/
    private static Logger log = Logger.getLogger(JDLoginServiceImpl.class);

    /*****
     * 京东登录表单提交URL
     *****/
    private static final String LOGIN_URL = "https://passport.jd.com/uc/loginService";

    /*****
     * 京东登录表单提交URL
     *****/
    @SuppressWarnings("unused")
    private static final String ORDER_URL = "http://order.jd.com/center/list.action";

    /*****
     * 下载器
     *****/
    private IProcessor fetcher;

    /*****
     * POST提交参数管理器
     *****/
    private PostDataManager postDataManager;

    /*****
     * 保存登录时获取到的提交信息规则处理
     *****/
    private IRuler jingdongSavePostDataRuler;

    /*****
     * 登录成功处理规则
     *****/
    private IRuler loginSuccessRuler;


    public void setPostDataManager(PostDataManager postDataManager) {
        this.postDataManager = postDataManager;
    }

    public void setJingdongSavePostDataRuler(IRuler jingdongSavePostDataRuler) {
        this.jingdongSavePostDataRuler = jingdongSavePostDataRuler;
    }

    public void setFetcher(Fetcher fetcher) {
        this.fetcher = fetcher;
    }

    public void setLoginSuccessRuler(IRuler loginSuccessRuler) {
        this.loginSuccessRuler = loginSuccessRuler;
    }


    @Override
    public Object login(CrawlerPage crawlerPage) {
        crawlerPage.getUriData().setFormEntityCharset("GB18030");
        crawlerPage.getProxy().setUseProxy(false);
        JSONObject result = new JSONObject();
        String sourceCode = "";
        String loginName = "";
        try {
            /***** 1. 设置verifyUser阶段获取的Cookie,POST等参数 *****/
            jingdongSavePostDataRuler.innerEexcuteRule(crawlerPage);

            /***** 2. 设置京东登录表单提交URL *****/
            crawlerPage.getUriData().setUri(URIUtils.getHttpURL(LOGIN_URL));
            crawlerPage.getUriData().setHttpMethod(HttpMethod.POST);

            String authCodeURL = crawlerPage.getUriData().getAuthCodeURL();

            /***** 3. 执行HTTP POST提交登录操作 *****/
            fetcher.innerExecute(crawlerPage);
            sourceCode = crawlerPage.getSourceData().getSourceCode();

            /***** 4. 将返回的unicode字符转化为中文 *****/
            sourceCode = UnicodeDecoderUtils.decodeUnicode(sourceCode);

            /***** 5. 清除上一次用户登录失败后缓存中所有POST参数、Cookie(已经失效) *****/
            String key = "";

            Query query = crawlerPage.getUriData().getQuery();
            if (query instanceof JingdongLoginQuery) {
                JingdongLoginQuery jdLoginQuery = (JingdongLoginQuery) query;
                key = crawlerPage.getJob() + "_" + jdLoginQuery.getLoginName();
                loginName = jdLoginQuery.getLoginName();
            }
            log.info("jd trylogin " + sourceCode + ", loginName=" + loginName);

            PostData postData = postDataManager.getPostData(key);
            postDataManager.removePostData(key);


            /***** 6. 通过返回结果判断是否登录结果状态 *****/
            if (sourceCode.contains("success")) {
                int statusId = ApiWd.saveStatus(
                        new CrawlerStatus(query.getMerchantId(), Module.JD.getModuleName(), query.getUserId(),
                                NoticeUtils.CrawlerStatus.CRAWLING.getStatus()));
                result = wrapLoginRes(1, "", ErrorCode._0);
                result.put("statusId", statusId);
                log.info("jd login success loginName=" + loginName);
                /***** 7. 保存所有登录成功的Cookie值 *****/
                crawlerPage.setCrawlerStatusId(statusId);
                loginSuccessRuler.innerEexcuteRule(crawlerPage);

            } else if (sourceCode.contains("emptyAuthcode") || sourceCode.contains("verifycode")) {

                /***** 8重新请求获取所有POST参数、Cookie、验证码图片 *****/
                crawlerPage.getUriData().setHttpMethod(HttpMethod.GET);
                jingdongSavePostDataRuler.innerEexcuteRule(crawlerPage);
                postData = postDataManager.getPostData(key);
                authCodeURL = postData.getAuthCodeURL();
                crawlerPage.getUriData().setAuthCodeURL(authCodeURL);
                crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                if (!StringUtils.isBlank(authCodeURL)) {
                    crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                    setAuthCodeFileName(crawlerPage);
                    fetcher.innerExecute(crawlerPage);
                    result = wrapLoginRes(0, authCodeURL, ErrorCode._10012);
                    return result;
                } else {
                    result = wrapLoginRes(0, "", ErrorCode._10010);
                    return result;
                }

            } else if (sourceCode.contains("username") && sourceCode.contains("刷新")) {
                result = wrapLoginRes(0, "", ErrorCode._10010);
            } else if (sourceCode.contains("username") && sourceCode.contains("你的账号因安全原因被暂时封锁")) {
                result = wrapLoginRes(0, "", ErrorCode._10010.getCode(), "你的账号因安全原因被暂时封锁");
            } else if (sourceCode.contains("username") && sourceCode.contains("账户名不存在，请重新输入")) {
                result = wrapLoginRes(0, "", ErrorCode._10063.getCode(), "账户名不存在，请重新输入");
            } else if (sourceCode.contains("pwd")) {
                result = wrapLoginRes(0, "", ErrorCode._10016);
            } else if (sourceCode.contains("Cookie") || sourceCode.contains("请您再次登录")) {
                result = wrapLoginRes(0, "", ErrorCode._10007);
            } else {
                result = wrapLoginRes(0, "", ErrorCode._10007);
            }
            return result;
        } catch (Exception e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
        result = wrapLoginRes(0, "", ErrorCode._10007);
        return result;
    }

    @Override
    public Object sendMsg(CrawlerPage crawlerPage) {
        return null;
    }

    @Override
    public Object verifyPhoneCode(CrawlerPage crawlerPage) {
        return null;
    }

    @Override
    public Object verifyUser(CrawlerPage crawlerPage) {
        crawlerPage.getUriData().setFormEntityCharset("GB18030");
        crawlerPage.getProxy().setUseProxy(false);
        String authCodeURL = "";
        JSONObject result = new JSONObject();
        try {
            /***** 1. 访问京东登录首页 获取此页Cookie与登录提交信息 *****/
            crawlerPage.getUriData().setAuthCodeURL(authCodeURL);
            jingdongSavePostDataRuler.innerEexcuteRule(crawlerPage);
            String key = "";
            Query query = crawlerPage.getUriData().getQuery();
            if (query instanceof JingdongVerifyUserQuery) {
                JingdongVerifyUserQuery userQuery = (JingdongVerifyUserQuery) query;
                key = crawlerPage.getJob() + "_" + userQuery.getLoginName();
                PostData postData = postDataManager.getPostData(key);
                authCodeURL = postData.getAuthCodeURL();
            }

            /***** 2. 验证用户是否需要验证码 *****/
            if (!StringUtils.isBlank(authCodeURL)) {
                crawlerPage.getUriData().setHttpMethod(HttpMethod.PATCH);
                setAuthCodeFileName(crawlerPage);
                fetcher.innerExecute(crawlerPage);
                if (crawlerPage.getCrawlerState() != CrawlerState.UPDATED) {
                    result.put("errorcode", ErrorCode._10007.getCode());
                    result.put("url", "");
                    return result;
                }
                result.put("errorcode", 0);
                result.put("url", authCodeURL);
            } else {
                result.put("errorcode", 0);
                result.put("url", "");
            }
            return result;

        } catch (Exception e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
        result.put("errorcode", ErrorCode._10007.getCode());
        result.put("url", "");
        return result;

    }

    /**
     * 设置下载后的验证码图片地址
     *
     * @param crawlerPage
     */
    private void setAuthCodeFileName(CrawlerPage crawlerPage) {
        /***** 访问图片必须设置这个参数,否则请到到的图片不是真图 *****/
        Header[] httpHeaders = new Header[]{new BasicHeader("Referer", "https://passport.jd.com/uc/login")};
        crawlerPage.getUriData().setHttpHeaders(httpHeaders);
        int beginIndex = crawlerPage.getUriData().getAuthCodeURL().lastIndexOf("acid=") + 5;
        int endIndex = crawlerPage.getUriData().getAuthCodeURL().lastIndexOf("&uid=");
        String fileName = crawlerPage.getUriData().getAuthCodeURL().substring(beginIndex, endIndex);
        crawlerPage.getUriData().setAuthCodeFileName(fileName);
    }

    public static JSONObject wrapLoginRes(int status, String url, int errorCode, String errorMsg) {
        JSONObject res = new JSONObject();
        res.put("status", status);
        res.put("errorcode", errorCode);
        res.put("url", url);
        res.put("errorMsg", errorMsg);
        return res;
    }

    public static JSONObject wrapLoginRes(int status, String url, ErrorCode errorCode) {
        return wrapLoginRes(status, url, errorCode.getCode(), errorCode.getMsg());
    }

    public static void main(String[] args) {
        System.out.println(Module.JD.name());
    }
}
