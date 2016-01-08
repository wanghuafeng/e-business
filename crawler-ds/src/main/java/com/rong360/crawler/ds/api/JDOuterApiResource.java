package com.rong360.crawler.ds.api;

import com.rong360.crawler.api.ApiWd;
import com.rong360.crawler.api.impl.ApiResourceImpl;
import com.rong360.crawler.bean.*;
import com.rong360.crawler.common.Param;
import com.rong360.crawler.common.Response;
import com.rong360.crawler.common.ResponseBody;
import com.rong360.crawler.ds.bean.DSApiResult;
import com.rong360.crawler.ds.check.impl.CheckJDLoginOuter;
import com.rong360.crawler.ds.check.impl.CheckJDVerifyOuter;
import com.rong360.crawler.ds.query.impl.JDLoginOuterQuery;
import com.rong360.crawler.ds.query.impl.JDStatusQuery;
import com.rong360.crawler.ds.query.impl.JDVerifyUserQuery;
import com.rong360.crawler.ds.service.DSApiResultService;
import com.rong360.crawler.ds.service.ILoginService;
import com.rong360.crawler.ds.util.DsResponseUtil;
import com.rong360.crawler.ds.util.ErrorUtil;
import com.rong360.crawler.ds.util.Util;
import com.rong360.crawler.page.CrawlerPage;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/8.
 */
@Path("/jd/")
public class JDOuterApiResource extends ApiResourceImpl {
    public static Logger log = Logger.getLogger(JDOuterApiResource.class);

    @Autowired
    private ILoginService jdLoginService;

    @Autowired
    private DSApiResultService dsApiResultService;

    @POST
    @Path("/getPicCode")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String getPicCode(@Context HttpServletRequest request, @BeanParam JDVerifyUserQuery jdVerifyUserQuery) {
        CheckResult checkResult = new CheckJDVerifyOuter().check(jdVerifyUserQuery);
        JSONObject result = new JSONObject();
        Response response = new Response();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getDefaultCrawlerPage(Job.JD, Task.LOGIN, jdVerifyUserQuery);

                /******2.访问京东网站,判定用户登录是否需要验证码*****/
                Object object = jdLoginService.verifyUser(cp);

                result = (JSONObject) object;
                if (!StringUtils.isBlank(result.getString("url"))) {
                    String fileName = cp.getUriData().getAuthCodeFileName();
                    String newAuthCodeURL = getAuthCodeURL(request, cp, fileName);
                    result.put("url", newAuthCodeURL);
                }
                result.put("errorMsg", "ok");

            } else {
                result = new JSONObject();
                result.put("errorcode", checkResult.getErrorcode());
                result.put("errorMsg", checkResult.getErrorMsg());
                result.put("url", "");
            }
            try {
                DSApiResult dsApiResult = new DSApiResult(Api._jd_verifyUser.getApiId(), Api._jd_verifyUser.getApiInfo());
                dsApiResult.setUid(jdVerifyUserQuery.getUserId());
                dsApiResult.setLoginName(jdVerifyUserQuery.getLoginName());
                dsApiResult.setReturnCode(result.getInt("errorcode"));
                if (result.containsKey("errorMsg")) {
                    dsApiResult.setReturnMsg(result.getString("errorMsg"));
                }
                dsApiResultService.save(dsApiResult);
            } catch (Exception e) {
                log.warn("save api result failed:" + e.getMessage());
            }
        } catch (Exception e) {
            result.put("errorcode", ErrorCode._10007.getCode());
            result.put("errorMsg", ErrorCode._10007.getMsg());
            result.put("url", "");
            log.error(e.getMessage());
        }
        // 包装成符合格式的串
        String url = result.getString("url");
        if (StringUtils.isNotBlank(url)) {
            JSONObject picCode = Util.getJSONObject("pic_code", url);
            response.setData(picCode);
        }
        ApiWd.saveLog(Module.JD, "getPicCode", jdVerifyUserQuery, response);
        return response.toString();
    }

    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String login(@Context HttpServletRequest request, @BeanParam JDLoginOuterQuery jdLoginQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckJDLoginOuter().check(jdLoginQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = Util.getDefaultCrawlerPage(Job.JD, Task.LOGIN, jdLoginQuery);

                /******2.访问京东网站执行登录操作*****/
                Object object = jdLoginService.login(cp);
                result = (JSONObject) object;
                if (StringUtils.isNotBlank(result.getString("url"))) {
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
            // saveRequestLog();
            try {
                DSApiResult dsApiResult = new DSApiResult(Api._jd_login.getApiId(), Api._jd_login.getApiInfo());
                dsApiResult.setUid(jdLoginQuery.getUserId());
                dsApiResult.setLoginName(jdLoginQuery.getLoginName());
                dsApiResult.setReturnCode(result.getInt("errorcode"));
                if (result.containsKey("errorMsg")) {
                    dsApiResult.setReturnMsg(result.getString("errorMsg"));
                }
                dsApiResultService.save(dsApiResult);
            } catch (Exception e) {
                log.warn("save api result failed:" + e.getMessage());
            }
        } catch (Exception e) {
            result.put("errorcode", ErrorCode._10007.getCode());
            result.put("errorMsg", ErrorCode._10007.getMsg());
            result.put("url", "");
            log.error(e.getMessage());
        }
        // 包装成符合格式的串
        Response response = wrapJDLogin(result, jdLoginQuery);
        ApiWd.saveLog(Module.JD, "login", jdLoginQuery, response);
        return response.toString();
    }

    @POST
    @Path("/getStatus")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String getStatus(@Context HttpServletRequest request, @BeanParam JDStatusQuery jdStatusQuery) {
        try {
            int res = ApiWd.queryStatus(Integer.parseInt(jdStatusQuery.getStatusId()));
            JSONObject data = new JSONObject();
            data.put("status", res);
            Response response = new Response(ErrorUtil.SUCCEED_CODE);
            response.setData(data);
            ApiWd.saveLog(Module.JD, "getStatus", jdStatusQuery, response);
            return response.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Response(ErrorUtil.FAILED_CODE).toString();
        }
    }


    public static Response wrapJDLogin(JSONObject result, JDLoginOuterQuery query) {
        Response response = new Response();
        response.setData(new JSONObject());
        ErrorCode dsErrorCode = ErrorUtil.transfer(result.getInt("errorcode"), ErrorUtil.Source.JD);
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
                if (result.containsKey("statusId") && result.getInt("statusId") != -1) {
                    int statusId = result.getInt("statusId");
                    JSONObject object = Param.getStatusIdObject(statusId);
                    response.setData(object);
                } else {
                    response = DsResponseUtil.getRetryRes(dsErrorCode);
                }
                return response;
            case 201: //其他错误
            case 200100: //登录名为空
            case 504001: //帐户名密码不匹配
            case 504003: //账户名不存在
            default: //其他错误
                return DsResponseUtil.getRetryRes(dsErrorCode);
        }
    }

    public static void main(String[] args) {
    }
}
