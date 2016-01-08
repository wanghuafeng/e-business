package com.rong360.crawler.ds.api;

import com.rong360.crawler.api.impl.ApiResourceImpl;
import com.rong360.crawler.bean.*;
import com.rong360.crawler.ds.bean.DSApiResult;
import com.rong360.crawler.ds.check.impl.CheckTaoBaoPCLoginImpl;
import com.rong360.crawler.ds.check.impl.CheckTaoBaoPCSendMsgImpl;
import com.rong360.crawler.ds.check.impl.CheckTaoBaoPCUserImpl;
import com.rong360.crawler.ds.check.impl.CheckTaoBaoPCVerifyMsgImpl;
import com.rong360.crawler.ds.query.impl.TaoBaoPCLoginQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoPCSendMsgQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoPCVerifyMsgQuery;
import com.rong360.crawler.ds.query.impl.TaoBaoPCVerifyUserQuery;
import com.rong360.crawler.ds.service.DSApiResultService;
import com.rong360.crawler.ds.service.ILoginService;
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


/**
 * @author xiongwei
 */
@Path("/taobaoPC/")
public class TaoBaoPCApiResource extends ApiResourceImpl {

    @Autowired
    @Qualifier("taobaoPCLoginService")
    private ILoginService taobaoPCLoginService;

    @Autowired
    private DSApiResultService dsApiResultService;

    @POST
    @Path("/verifyUser.json")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public String verifyUser(@Context HttpServletRequest request, @BeanParam TaoBaoPCVerifyUserQuery taoBaoPCVerifyUserQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTaoBaoPCUserImpl().check(taoBaoPCVerifyUserQuery);
        JSONObject result = null;
        try {
            if (checkResult == null) {
                CrawlerPage cp = new CrawlerPage();
                cp.setSourceData(new SourceData());
                cp.setMetaData(new MetaData());
                cp.getMetaData().setSuccedExtract(true);
                cp.setSourceData(new SourceData());
                cp.setCrawlerState(CrawlerState.UPDATED);
                cp.setJob(Job.TAOBAO);
                cp.setTask(Task.LOGIN);
                cp.getUriData().setQuery(taoBaoPCVerifyUserQuery);

                /******2.访问京东网站,判定用户登录是否需要验证码*****/
                Object object = taobaoPCLoginService.verifyUser(cp);
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
            dsApiResult.setUid(taoBaoPCVerifyUserQuery.getUserId().split("_")[1]);
            dsApiResult.setLoginName(taoBaoPCVerifyUserQuery.getLoginName());
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
    public String sendMsg(@Context HttpServletRequest request, @BeanParam TaoBaoPCSendMsgQuery taoBaoPCSendMsgQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTaoBaoPCSendMsgImpl().check(taoBaoPCSendMsgQuery);
        JSONObject result = null;
        try {
            if (checkResult == null) {
                CrawlerPage cp = new CrawlerPage();
                cp.setSourceData(new SourceData());
                cp.setMetaData(new MetaData());
                cp.getMetaData().setSuccedExtract(true);
                cp.setSourceData(new SourceData());
                cp.setCrawlerState(CrawlerState.UPDATED);
                cp.setJob(Job.TAOBAO);
                cp.setTask(Task.LOGIN);
                cp.getUriData().setUserAgent(UserAgentType.IPHONE);
                cp.getUriData().setQuery(taoBaoPCSendMsgQuery);

                /******2.执行向手机发送验证码操作*****/
                result = (JSONObject) taobaoPCLoginService.sendMsg(cp);
            } else {
                result = new JSONObject();
                result.put("errorcode", checkResult.getErrorcode());
                result.put("errorMsg", checkResult.getErrorMsg());
            }
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_sendMsg.getApiId(), Api._taobao_sendMsg.getApiInfo());
            dsApiResult.setUid(taoBaoPCSendMsgQuery.getUserId().split("_")[1]);
            dsApiResult.setLoginName(taoBaoPCSendMsgQuery.getLoginName());
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
    public String verifyMsg(@Context HttpServletRequest request, @BeanParam TaoBaoPCVerifyMsgQuery taoBaoPCVerifyMsgQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTaoBaoPCVerifyMsgImpl().check(taoBaoPCVerifyMsgQuery);
        JSONObject result = null;

        try {
            if (checkResult == null) {
                CrawlerPage cp = new CrawlerPage();
                cp.setSourceData(new SourceData());
                cp.setMetaData(new MetaData());
                cp.getMetaData().setSuccedExtract(true);
                cp.setSourceData(new SourceData());
                cp.setCrawlerState(CrawlerState.UPDATED);
                cp.setJob(Job.TAOBAO);
                cp.setTask(Task.LOGIN);
                cp.getUriData().setQuery(taoBaoPCVerifyMsgQuery);

                /******5.访问京东网站执行登录操作*****/
                Object object = taobaoPCLoginService.verifyPhoneCode(cp);
                result = (JSONObject) object;
            } else {
                result = new JSONObject();
                result.put("errorcode", checkResult.getErrorcode());
                result.put("errorMsg", checkResult.getErrorMsg());
            }
            DSApiResult dsApiResult = new DSApiResult(Api._taobao_verifyMsg.getApiId(), Api._taobao_verifyMsg.getApiInfo());
            dsApiResult.setUid(taoBaoPCVerifyMsgQuery.getUserId().split("_")[1]);
            dsApiResult.setLoginName(taoBaoPCVerifyMsgQuery.getLoginName());
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
    public String login(@Context HttpServletRequest request, @BeanParam TaoBaoPCLoginQuery taobaoPCLoginQuery) {
        /******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
        CheckResult checkResult = new CheckTaoBaoPCLoginImpl().check(taobaoPCLoginQuery);
        JSONObject result = null;
        try {
            if (checkResult == null) {
                CrawlerPage cp = new CrawlerPage();
                cp.setSourceData(new SourceData());
                cp.setMetaData(new MetaData());
                cp.getMetaData().setSuccedExtract(true);
                cp.setSourceData(new SourceData());
                cp.setCrawlerState(CrawlerState.UPDATED);
                cp.setJob(Job.TAOBAO);
                cp.setTask(Task.LOGIN);
                cp.getUriData().setQuery(taobaoPCLoginQuery);

                /******2.访问淘宝网站执行登录操作*****/
                result = (JSONObject) taobaoPCLoginService.login(cp);
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
            dsApiResult.setUid(taobaoPCLoginQuery.getUserId().split("_")[1]);
            dsApiResult.setLoginName(taobaoPCLoginQuery.getLoginName());
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
