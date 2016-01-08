package com.rong360.crawler.ds.check.impl;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.ds.query.impl.JDVerifyUserQuery;
import com.rong360.crawler.ds.query.impl.JingdongVerifyUserQuery;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2015/12/10.
 */
public class CheckJDVerifyOuter {

    public CheckResult check(JDVerifyUserQuery jdVerifyUserQuery) {

        /******检查登录名为空*****/
        if (isEmpty(jdVerifyUserQuery.getLoginName())) {
            return new CheckResult(ErrorCode._20001.getMsg(), ErrorCode._20001.getCode());
        }


        /******检查userId是否为空*****/
        if (isEmpty(jdVerifyUserQuery.getUserId())) {
            return new CheckResult(ErrorCode._10008.getMsg(), ErrorCode._10008.getCode());
        }

        /******检查userId长度是否超过45个字符*****/
        if (jdVerifyUserQuery.getUserId().length() > 45) {
            return new CheckResult(ErrorCode._10011.getMsg(), ErrorCode._10011.getCode());
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
