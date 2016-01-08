package com.rong360.crawler.ds.check.impl;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.ds.query.impl.TBVerifyUserQuery;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2015/12/15.
 */
public class CheckTBPicCode {
    public CheckResult check(TBVerifyUserQuery tbVerifyUserQuery) {
        /******检查登录名为空*****/
        if (StringUtils.isEmpty(tbVerifyUserQuery.getLoginName())) {
            return new CheckResult(ErrorCode._20001.getMsg(), ErrorCode._20001.getCode());
        }


        /******检查userId是否为空*****/
        if (StringUtils.isEmpty(tbVerifyUserQuery.getUserId())) {
            return new CheckResult(ErrorCode._10008.getMsg(), ErrorCode._10008.getCode());
        }

        /******检查userId长度是否超过45个字符*****/
        if (tbVerifyUserQuery.getUserId().length() > 45) {
            return new CheckResult(ErrorCode._10011.getMsg(), ErrorCode._10011.getCode());
        }
        return null;
    }
}
