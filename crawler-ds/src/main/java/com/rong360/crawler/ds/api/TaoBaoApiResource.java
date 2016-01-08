package com.rong360.crawler.ds.api;

import com.rong360.crawler.api.impl.ApiResourceImpl;
import com.rong360.crawler.bean.Api;
import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.ds.bean.DSApiResult;
import com.rong360.crawler.ds.check.impl.CheckTaoBaoLoginImpl;
import com.rong360.crawler.ds.check.impl.CheckTaoBaoSendMsgImpl;
import com.rong360.crawler.ds.check.impl.CheckTaoBaoUserImpl;
import com.rong360.crawler.ds.check.impl.CheckTaoBaoVerifyMsgImpl;
import com.rong360.crawler.ds.query.impl.TaoBaoLoginQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoSendMsgQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoVerifyMsgQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoVerifyUserQuery;
import com.rong360.crawler.ds.service.DSApiResultService;
import com.rong360.crawler.ds.service.impl.TaoBaoLoginServiceImpl;
import com.rong360.crawler.ds.util.Util;
import com.rong360.crawler.page.CrawlerPage;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author xiongwei
 */
@Path("/taobao/")
public class TaoBaoApiResource extends ApiResourceImpl {

    @Autowired
    @Qualifier("taobaoLoginService")
    private TaoBaoLoginServiceImpl taobaoLoginService;

    @Autowired
    private DSApiResultService dsApiResultService;

    @POST
    @Path("/verifyUser.json")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String verifyUser(@Context HttpServletRequest request, @BeanParam TaoBaoVerifyUserQuery taoBaoVerifyUserQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTaoBaoUserImpl().check(taoBaoVerifyUserQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getTbDefaultCP(taoBaoVerifyUserQuery);

                /******2.访问京东网站,判定用户登录是否需要验证码*****/
                Object object = taobaoLoginService.verifyUser(cp);
                result = (JSONObject) object;
                if (!StringUtils.isBlank(result.getString("url"))) {
                    String fileName = cp.getUriData().getAuthCodeFileName();
                    String newAuthCodeURL = getAuthCodeURL(request, cp, fileName);
                    result.put("url", newAuthCodeURL);
                }

            } else {
                result = new JSONObject();
                result.put("errorcode", checkResult.getErrorcode());
                result.put("errorMsg", checkResult.getErrorMsg());
                result.put("url", "");
            }
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_sendMsg.getApiId(), Api._taobao_sendMsg.getApiInfo());
            dsApiResult.setUid(taoBaoVerifyUserQuery.getUserId().split("_")[1]);
            dsApiResult.setLoginName(taoBaoVerifyUserQuery.getLoginName());
            dsApiResult.setReturnCode(result.getInt("errorcode"));
            if (result.containsKey("errorMsg")) {
                dsApiResult.setReturnMsg(result.getString("errorMsg"));
            }
            dsApiResultService.save(dsApiResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    @POST
    @Path("/sendMsg.json")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String sendMsg(@Context HttpServletRequest request, @BeanParam TaoBaoSendMsgQuery taoBaoSendMsgQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTaoBaoSendMsgImpl().check(taoBaoSendMsgQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getTbDefaultCP(taoBaoSendMsgQuery);

                /******2.执行向手机发送验证码操作*****/
                result = (JSONObject) taobaoLoginService.sendMsg(cp);
            } else {
                result = new JSONObject();
                result.put("errorcode", checkResult.getErrorcode());
                result.put("errorMsg", checkResult.getErrorMsg());
            }
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_sendMsg.getApiId(), Api._taobao_sendMsg.getApiInfo());
            dsApiResult.setUid(taoBaoSendMsgQuery.getUserId().split("_")[1]);
            dsApiResult.setLoginName(taoBaoSendMsgQuery.getLoginName());
            dsApiResult.setReturnCode(result.getInt("errorcode"));
            if (result.containsKey("errorMsg")) {
                dsApiResult.setReturnMsg(result.getString("errorMsg"));
            }
            dsApiResultService.save(dsApiResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    @POST
    @Path("/verifyMsg.json")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String verifyMsg(@Context HttpServletRequest request, @BeanParam TaoBaoVerifyMsgQuery taoBaoVerifyMsgQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTaoBaoVerifyMsgImpl().check(taoBaoVerifyMsgQuery);
        JSONObject result = new JSONObject();

        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getTbDefaultCP(taoBaoVerifyMsgQuery);
                cp.getUriData().setUseRedirectCookie(true);
                cp.getUriData().setUsePrevCookie(true);

                /******5.访问京东网站执行登录操作*****/
                Object object = taobaoLoginService.verifyPhoneCode(cp);
                result = (JSONObject) object;
            } else {
                result = new JSONObject();
                result.put("errorcode", checkResult.getErrorcode());
                result.put("errorMsg", checkResult.getErrorMsg());
            }
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_sendMsg.getApiId(), Api._taobao_sendMsg.getApiInfo());
            dsApiResult.setUid(taoBaoVerifyMsgQuery.getUserId().split("_")[1]);
            dsApiResult.setLoginName(taoBaoVerifyMsgQuery.getLoginName());
            dsApiResult.setReturnCode(result.getInt("errorcode"));
            if (result.containsKey("errorMsg")) {
                dsApiResult.setReturnMsg(result.getString("errorMsg"));
            }
            dsApiResultService.save(dsApiResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    @POST
    @Path("/login.json")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String login(@Context HttpServletRequest request, @BeanParam TaoBaoLoginQuery taobaoLoginQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTaoBaoLoginImpl().check(taobaoLoginQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getTbDefaultCP(taobaoLoginQuery);
                cp.getUriData().setUseRedirectCookie(true);
                cp.getUriData().setUsePrevCookie(true);

                /******2.访问淘宝网站执行登录操作*****/
                result = (JSONObject) taobaoLoginService.login(cp);
                if (!StringUtils.isBlank(result.getString("url"))) {
                    String fileName = cp.getUriData().getAuthCodeFileName();
                    String newAuthCodeURL = getAuthCodeURL(request, cp, fileName);
                    result.put("url", newAuthCodeURL);
                }
            } else {
                result = new JSONObject();
                result.put("errorcode", checkResult.getErrorcode());
                result.put("errorMsg", checkResult.getErrorMsg());
            }
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_sendMsg.getApiId(), Api._taobao_sendMsg.getApiInfo());
            dsApiResult.setUid(taobaoLoginQuery.getUserId().split("_")[1]);
            dsApiResult.setLoginName(taobaoLoginQuery.getLoginName());
            dsApiResult.setReturnCode(result.getInt("errorcode"));
            if (result.containsKey("errorMsg")) {
                dsApiResult.setReturnMsg(result.getString("errorMsg"));
            }
            dsApiResultService.save(dsApiResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
