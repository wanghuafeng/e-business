package com.rong360.crawler.ds.util;

import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.common.Param;
import com.rong360.crawler.common.Response;
import com.rong360.crawler.common.ResponseBody;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/2.
 */
public class DsResponseUtil {
    public static JSONObject getReturnObject(int status, int errorcode, String errorMsg, String url) {
        JSONObject object = getReturnObject(status, errorcode, errorMsg);
        object.put("url", url);
        return object;
    }
    
    public static JSONObject getReturnObject(int status, int errorcode, String errorMsg) {
        JSONObject object = getReturnObject(errorcode, errorMsg);
        object.put("status", status);
        return object;
    }

    public static JSONObject getReturnObject(int errorcode, String errorMsg) {
        JSONObject object = new JSONObject();
        object.put("errorcode", errorcode);
        object.put("errorMsg", errorMsg);
        return object;
    }

    public static JSONObject getReturnObject(int errorcode, String errorMsg, String url) {
        JSONObject object = getReturnObject(errorcode, errorMsg);
        object.put("url", url);
        return object;
    }
    
    public static JSONObject getOuterResponse(ErrorCode errorCode, JSONObject next) {
        JSONObject response = new JSONObject();
        response.put("code", errorCode.getCode());
        response.put("msg", errorCode.getMsg());
        response.put("next", next);
        return response;
    }

    public static JSONObject getOuterResponse(ErrorCode errorCode, String method, JSONArray params, JSONArray hidden) {
        return getOuterResponse(errorCode, generateNext(method, params, hidden));
    }

    public static JSONObject getOuterResponse(ErrorCode errorCode) {
        return getOuterResponse(errorCode, new JSONObject());
    }

    public static JSONObject generateNext(String method, JSONArray params, JSONArray hidden) {
        JSONObject next = new JSONObject();
        next.put("method", method);
        next.put("param", params);
        next.put("hidden", hidden);
        return next;
    }

    public static Response getRetryRes(ErrorCode errorCode) {
        Response response = new Response(errorCode);
        JSONObject data = new JSONObject();
        data.put("login_name", "");
        data.put("password", "");
        response.setData(data);
        return response;
    }

    public static Response getSuccessLoginResponse(int statusId) {
        Response response = new Response();
        JSONObject object = Param.getStatusIdObject(statusId);
        response.setData(object);
        return response;
    }

    public static Response getPicCodeResponse(String loginName, String password, String url, String userId) {
        Response response = new Response();
        ResponseBody responseBody = new ResponseBody();
        Param picCode = Param.getPicCodeParam(url);
        picCode.setRefreshMethod("getPicCode");
        Param loginNameParam = new Param("login_name", loginName);
        Param userIdParam = new Param("user_id", userId);
        List<Param> refreshParams = new ArrayList<>();
        refreshParams.add(loginNameParam);
        refreshParams.add(userIdParam);
        picCode.setRefreshParam(refreshParams);
        responseBody.getParam().add(picCode);
        responseBody.getParam().add(Param.getLoginNameParam(loginName));
        responseBody.getParam().add(Param.getPasswordParam(password));
        responseBody.setMethod("login");
        response.setResponseBody(responseBody);
        return response;
    }

    public static Param getRetryPicCode(String loginName, String password, String url, String userId) {
        Param picCode = Param.getPicCodeParam(url);
        picCode.setRefreshMethod("getPicCode");
        Param loginNameParam = new Param("login_name", loginName);
        Param userIdParam = new Param("user_id", userId);
        List<Param> refreshParams = new ArrayList<>();
        refreshParams.add(loginNameParam);
        refreshParams.add(userIdParam);
        picCode.setRefreshParam(refreshParams);
        return picCode;
    }
}
