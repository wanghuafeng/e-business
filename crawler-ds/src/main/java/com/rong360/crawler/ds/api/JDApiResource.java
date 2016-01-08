package com.rong360.crawler.ds.api;

import com.rong360.crawler.api.impl.ApiResourceImpl;
import com.rong360.crawler.bean.*;
import com.rong360.crawler.ds.bean.DSApiResult;
import com.rong360.crawler.ds.check.impl.CheckJDLoginImpl;
import com.rong360.crawler.ds.check.impl.CheckJDUserImpl;
import com.rong360.crawler.ds.query.impl.JingdongLoginQuery;
import com.rong360.crawler.ds.query.impl.JingdongVerifyUserQuery;
import com.rong360.crawler.ds.service.DSApiResultService;
import com.rong360.crawler.ds.service.ILoginService;
import com.rong360.crawler.page.CrawlerPage;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @ClassName: JDApiResource
 * @Description:京东用户登录api
 * @date 2015-6-5 上午11:46:19
 */
@Path("/jd/")
public class JDApiResource extends ApiResourceImpl {
    public static Logger log = Logger.getLogger(JDApiResource.class);
    
    @Autowired
    private ILoginService jdLoginService;

    @Autowired
    private DSApiResultService dsApiResultService;

    @POST
    @Path("/verifyUser.json")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String verifyUser(@Context HttpServletRequest request, @BeanParam JingdongVerifyUserQuery jingdongVerifyUserQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckJDUserImpl().check(jingdongVerifyUserQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = new CrawlerPage();
                cp.setSourceData(new SourceData());
                cp.setMetaData(new MetaData());
                cp.getMetaData().setSuccedExtract(true);
                cp.setSourceData(new SourceData());
                cp.setCrawlerState(CrawlerState.UPDATED);
                cp.setJob(Job.JD);
                cp.setTask(Task.LOGIN);
                cp.getUriData().setQuery(jingdongVerifyUserQuery);

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
                if (jingdongVerifyUserQuery.getUserId().contains("_")) {
                    dsApiResult.setUid(jingdongVerifyUserQuery.getUserId().split("_")[1]);
                } else {
                    dsApiResult.setUid(jingdongVerifyUserQuery.getUserId());
                }
                dsApiResult.setLoginName(jingdongVerifyUserQuery.getLoginName());
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
            log.error(e.getMessage());
        }
        return result.toString();
    }


    @POST
    @Path("/login.json")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String login(@Context HttpServletRequest request, @BeanParam JingdongLoginQuery jingdongLoginQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckJDLoginImpl().check(jingdongLoginQuery);
        JSONObject result = new JSONObject();
        try {
            if (checkResult == null) {
                CrawlerPage cp = new CrawlerPage();
                cp.setSourceData(new SourceData());
                cp.setMetaData(new MetaData());
                cp.getMetaData().setSuccedExtract(true);
                cp.setSourceData(new SourceData());
                cp.setCrawlerState(CrawlerState.UPDATED);
                cp.setJob(Job.JD);
                cp.setTask(Task.LOGIN);
                cp.getUriData().setQuery(jingdongLoginQuery);

                /******2.访问京东网站执行登录操作*****/
                Object object = jdLoginService.login(cp);
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
            try {
                DSApiResult dsApiResult = new DSApiResult(Api._jd_login.getApiId(), Api._jd_login.getApiInfo());
                if (jingdongLoginQuery.getUserId().contains("_")) {
                    dsApiResult.setUid(jingdongLoginQuery.getUserId().split("_")[1]);
                } else {
                    dsApiResult.setUid(jingdongLoginQuery.getUserId());
                }
                dsApiResult.setLoginName(jingdongLoginQuery.getLoginName());
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
            log.error(e.getMessage());
        }
        return result.toString();
    }

}
