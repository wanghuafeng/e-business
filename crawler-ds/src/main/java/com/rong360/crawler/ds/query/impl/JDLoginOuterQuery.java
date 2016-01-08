package com.rong360.crawler.ds.query.impl;

import javax.ws.rs.FormParam;

/**
 * Created by Administrator on 2015/12/8.
 */
public class JDLoginOuterQuery extends JingdongLoginQuery implements Cloneable {

    /*****【必填】用户ID，由appId_userId组成（appId由量化派提供，userId是接口网站用户的Id） *****/
    @FormParam("user_id")
    private String userId = "-1";

    /*****【必填】京东用户名*****/
    @FormParam("login_name")
    private String loginName = "-1";

    /*****【必填】密码*****/
    @FormParam("password")
    private String password = "-1";

    /*****【非必填】验证码*****/
    @FormParam("pic_code")
    private String authcode = "-2";

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }


}
