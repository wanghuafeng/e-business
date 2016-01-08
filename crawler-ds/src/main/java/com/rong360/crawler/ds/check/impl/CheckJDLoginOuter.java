package com.rong360.crawler.ds.check.impl;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.ds.query.impl.JDLoginOuterQuery;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2015/12/10.
 */
public class CheckJDLoginOuter {

    public CheckResult check(JDLoginOuterQuery jdLoginQuery) {

        /******检查登录名为空*****/
        if (isEmpty(jdLoginQuery.getLoginName())) {
            return new CheckResult(ErrorCode._20001.getMsg(), ErrorCode._20001.getCode());
        }

        /******检查密码是否为空*****/
        if (isEmpty(jdLoginQuery.getPassword())) {
            return new CheckResult(ErrorCode._20002.getMsg(), ErrorCode._20002.getCode());
        }

        /******检查userId是否为空*****/
        if (isEmpty(jdLoginQuery.getUserId())) {
            return new CheckResult(ErrorCode._20005.getMsg(), ErrorCode._20005.getCode());
        }
        return null;
    }


    /****
     * 检查参数是否为空
     * @param key
     * @return
     */
    public boolean isEmpty(String key) {
        return "-1".equals(key) || StringUtils.isEmpty(key);
    }

}
