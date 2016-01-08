package com.rong360.crawler.ds.api;

import com.rong360.crawler.api.ApiWd;
import com.rong360.crawler.api.impl.ApiResourceImpl;
import com.rong360.crawler.bean.*;
import com.rong360.crawler.common.Param;
import com.rong360.crawler.common.Response;
import com.rong360.crawler.common.ResponseBody;
import com.rong360.crawler.ds.bean.DSApiResult;
import com.rong360.crawler.ds.check.impl.*;
import com.rong360.crawler.ds.query.impl.TBLoginQuery;
import com.rong360.crawler.ds.query.impl.TBSendMsgQuery;
import com.rong360.crawler.ds.query.impl.TBVerifyMsgQuery;
import com.rong360.crawler.ds.query.impl.TBVerifyUserQuery;
import com.rong360.crawler.ds.service.DSApiResultService;
import com.rong360.crawler.ds.service.impl.TaoBaoLoginServiceImpl;
import com.rong360.crawler.ds.util.DsResponseUtil;
import com.rong360.crawler.ds.util.ErrorUtil;
import com.rong360.crawler.ds.util.Util;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UserAgentType;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/taobao/")
public class TBOuterApiResource extends ApiResourceImpl {

    @Autowired
    @Qualifier("taobaoLoginService")
    private TaoBaoLoginServiceImpl taobaoLoginService;

    @Autowired
    private DSApiResultService dsApiResultService;

    @POST
    @Path("/getPicCode")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String getPicCode(@Context HttpServletRequest request, @BeanParam TBVerifyUserQuery tbVerifyUserQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTBPicCode().check(tbVerifyUserQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getTbDefaultCP(tbVerifyUserQuery);

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
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_verifyUser.getApiId(), Api._taobao_verifyUser.getApiInfo());
            dsApiResult.setUid(tbVerifyUserQuery.getUserId());
            dsApiResult.setLoginName(tbVerifyUserQuery.getLoginName());
            dsApiResult.setReturnCode(result.getInt("errorcode"));
            if (result.containsKey("errorMsg")) {
                dsApiResult.setReturnMsg(result.getString("errorMsg"));
            }
            dsApiResultService.save(dsApiResult);
        } catch (Exception e) {
            result.put("errorcode", ErrorCode._10007.getCode());
            result.put("errorMsg", ErrorCode._10007.getMsg());
            result.put("url", "");
            e.printStackTrace();
        }
        // 包装成符合格式的串
        Response response = new Response();
        String url = result.getString("url");
        int code = result.getInt("errorcode");
        if (code == ErrorCode._0.getCode()) {
            if (StringUtils.isNotBlank(url)) {
                JSONObject picCode = Util.getJSONObject("pic_code", url);
                response.setData(picCode);
            } else {
                response = new Response(ErrorUtil.SUCCEED_CODE);
            }
        } else {
            response = new Response(ErrorUtil.FAILED_CODE);
        }
        ApiWd.saveLog(Module.TAOBAO, "getPicCode", tbVerifyUserQuery, response);
        return response.toString();
    }


    @POST
    @Path("/getMessageCode")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String sendMsg(@Context HttpServletRequest request, @BeanParam TBSendMsgQuery tbSendMsgQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTBSendMsg().check(tbSendMsgQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getTbDefaultCP(tbSendMsgQuery);
                cp.getUriData().setUserAgent(UserAgentType.IPHONE);

                /******2.执行向手机发送验证码操作*****/
                result = (JSONObject) taobaoLoginService.sendMsg(cp);
            } else {
                result = new JSONObject();
                result.put("errorcode", checkResult.getErrorcode());
                result.put("errorMsg", checkResult.getErrorMsg());
            }
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_sendMsg.getApiId(), Api._taobao_sendMsg.getApiInfo());
            dsApiResult.setUid(tbSendMsgQuery.getUserId());
            dsApiResult.setLoginName(tbSendMsgQuery.getLoginName());
            dsApiResult.setReturnCode(result.getInt("errorcode"));
            if (result.containsKey("errorMsg")) {
                dsApiResult.setReturnMsg(result.getString("errorMsg"));
            }
            dsApiResultService.save(dsApiResult);
        } catch (Exception e) {
            result.put("errorcode", ErrorCode._10007.getCode());
            result.put("errorMsg", ErrorCode._10007.getMsg());
            result.put("url", "");
            e.printStackTrace();
        }
        // 包装成符合格式的串
        Response response;
        int code = result.getInt("errorcode");
        if (code == ErrorCode._0.getCode()) {
            response = new Response(ErrorUtil.SUCCEED_CODE);
        } else {
            response = new Response(ErrorUtil.FAILED_CODE);
        }
        ApiWd.saveLog(Module.TAOBAO, "sendMsg", tbSendMsgQuery, response);
        return response.toString();
    }


    @POST
    @Path("/submitMessageCode")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String verifyMsg(@Context HttpServletRequest request, @BeanParam TBVerifyMsgQuery tbVerifyMsgQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTBVerifyMsg().check(tbVerifyMsgQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getTbDefaultCP(tbVerifyMsgQuery);
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
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_verifyMsg.getApiId(), Api._taobao_verifyMsg.getApiInfo());
            dsApiResult.setUid(tbVerifyMsgQuery.getUserId());
            dsApiResult.setLoginName(tbVerifyMsgQuery.getLoginName());
            dsApiResult.setReturnCode(result.getInt("errorcode"));
            if (result.containsKey("errorMsg")) {
                dsApiResult.setReturnMsg(result.getString("errorMsg"));
            }
            dsApiResultService.save(dsApiResult);
        } catch (Exception e) {
            result.put("errorcode", ErrorCode._10007.getCode());
            result.put("errorMsg", ErrorCode._10007.getMsg());
            result.put("url", "");
            e.printStackTrace();
        }
        // 包装成符合格式的串
        Response response = wrapVerifyMsg(result);
        ApiWd.saveLog(Module.TAOBAO, "verifyMsg", tbVerifyMsgQuery, response);
        return response.toString();
    }


    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String login(@Context HttpServletRequest request, @BeanParam TBLoginQuery tbLoginQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTBLogin().check(tbLoginQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getTbDefaultCP(tbLoginQuery);
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
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_login.getApiId(), Api._taobao_login.getApiInfo());
            dsApiResult.setUid(tbLoginQuery.getUserId());
            dsApiResult.setLoginName(tbLoginQuery.getLoginName());
            dsApiResult.setReturnCode(result.getInt("errorcode"));
            if (result.containsKey("errorMsg")) {
                dsApiResult.setReturnMsg(result.getString("errorMsg"));
            }
            dsApiResultService.save(dsApiResult);
        } catch (Exception e) {
            result.put("errorcode", ErrorCode._10007.getCode());
            result.put("errorMsg", ErrorCode._10007.getMsg());
            result.put("url", "");
            e.printStackTrace();
        }
        // 包装成符合格式的串
        Response response = wrapTBLogin(result, tbLoginQuery);
        ApiWd.saveLog(Module.TAOBAO, "login", tbLoginQuery, response);
        return response.toString();
    }

    private static Response wrapVerifyMsg(JSONObject result) {
        Response response = new Response();
        response.setData(new JSONObject());
        ErrorCode dsErrorCode = ErrorUtil.transfer(result.getInt("errorcode"), ErrorUtil.Source.TB);
        switch (dsErrorCode.getCode()) {
            case 200: //登陆成功
                if (result.containsKey("statusId") && result.getInt("statusId") != -1) {
                    // 登陆成功
                    response = DsResponseUtil.getSuccessLoginResponse(result.getInt("statusId"));
                } else {
                    response = DsResponseUtil.getRetryRes(dsErrorCode);
                }
                return response;
            case 211101: //动态密码输入不正确或已过期
            case 201: //其他错误
            case 200100: //登录名为空
            case 505001: //帐户名密码不匹配
            default: //其他错误
                return DsResponseUtil.getRetryRes(dsErrorCode);
        }
    }

    private static Response wrapTBLogin(JSONObject result, TBLoginQuery query) {
        Response response = new Response();
        response.setData(new JSONObject());
        ErrorCode dsErrorCode = ErrorUtil.transfer(result.getInt("errorcode"), ErrorUtil.Source.TB);
        ResponseBody responseBody = new ResponseBody();
        switch (dsErrorCode.getCode()) {
            case 210101: //图片验证码输入不正确
                response = DsResponseUtil.getPicCodeResponse(query.getLoginName(), query.getPassword(),
                        result.getString("url"), query.getUserId());
                return response;
            case 504002: //页面过期，请重试
                response.setCode(dsErrorCode.getCode());
                response.setMsg(dsErrorCode.getMsg());
                response.getData().put("login_name", query.getLoginName());
                response.getData().put("password", query.getPassword());
                return response;
            case 200: //登陆成功
                if (result.containsKey("phone") && StringUtils.isNotBlank(result.getString("phone"))) {
                    // 需要发送短信验证码
                    String[] phones = result.getString("phone").split(";");
                    Param phoneCheckBox = getMsgParam(query.getLoginName(), query.getUserId(), "", phones);
                    Param loginName = new Param("login_name", query.getLoginName());
                    responseBody.getParam().add(phoneCheckBox);
                    responseBody.getHiddenParam().add(loginName);
                    responseBody.setMethod("verifyMsg");
                    response.setResponseBody(responseBody);
                } else {
                    if (result.containsKey("statusId") && result.getInt("statusId") != -1) {
                        // 登陆成功
                        response = DsResponseUtil.getSuccessLoginResponse(result.getInt("statusId"));
                    } else {
                        response = DsResponseUtil.getRetryRes(dsErrorCode);
                    }
                }
                return response;
            case 211101: //动态密码输入不正确或已过期
            case 201: //其他错误
            case 200100: //登录名为空
            case 505001: //帐户名密码不匹配
            default: //其他错误
                return DsResponseUtil.getRetryRes(dsErrorCode);
        }
    }

    private static Param getMsgParam(String loginName, String userId, String selectedPhone, String[] optionPhones) {
        Map<String, String> options = new HashMap<>();
        for (String phone : optionPhones) {
            options.put(phone, phone);
        }
        Param phoneCheckBox = Param.getCheckBoxParam("手机号", "手机号", "phone", "", options);
        List<Param> refreshParams = new ArrayList<>();
        Param loginNameParam = new Param("login_name", loginName);
        Param userIdParam = new Param("user_id", userId);
        Param phoneParam = new Param("phone", selectedPhone);
        phoneCheckBox.setRefreshMethod("sendMsg");
        refreshParams.add(loginNameParam);
        refreshParams.add(userIdParam);
        refreshParams.add(phoneParam);
        phoneCheckBox.setRefreshParam(refreshParams);
        return phoneCheckBox;
    }

}
