package com.rong360.crawler.ds.query.impl;

import javax.ws.rs.FormParam;

/**
 * Created by Administrator on 2015/12/8.
 */
public class JDVerifyUserQuery extends JingdongVerifyUserQuery implements Cloneable {
    /*****【必填】用户ID *****/
    @FormParam("user_id")
    private String userId = "-1";

    /*****【必填】京东用户名*****/
    @FormParam("login_name")
    private String loginName = "-1";

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

}
