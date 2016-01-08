package com.rong360.crawler.ds.query.impl;

import javax.ws.rs.FormParam;

/**
 * Created by Administrator on 2015/12/15.
 */
public class TBVerifyMsgQuery extends TaoBaoVerifyMsgQuery implements Cloneable {
    /*****【必填】用户ID，由appId_userId组成（appId由量化派提供，userId是接口网站用户的Id） *****/
    @FormParam("user_id")
    private String userId = "";

    /*****【必填】淘宝用户名*****/
    @FormParam("login_name")
    private String loginName = "";

    /*****【必填】动态口令*****/
    @FormParam("message_code")
    private String phoneCode = "";

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }



}
