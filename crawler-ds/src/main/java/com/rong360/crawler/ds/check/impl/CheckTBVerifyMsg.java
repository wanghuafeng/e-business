package com.rong360.crawler.ds.check.impl;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.ds.query.impl.TBVerifyMsgQuery;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2015/12/15.
 */
public class CheckTBVerifyMsg {
    public CheckResult check(TBVerifyMsgQuery tbVerifyMsgQuery) {
        /******检查登录名为空*****/
        if (StringUtils.isEmpty(tbVerifyMsgQuery.getLoginName())) {
            return new CheckResult(ErrorCode._20001.getMsg(), ErrorCode._20001.getCode());
        }


        /******检查userId是否为空*****/
        if (StringUtils.isEmpty(tbVerifyMsgQuery.getUserId())) {
            return new CheckResult(ErrorCode._10008.getMsg(), ErrorCode._10008.getCode());
        }

        /******检查userId长度是否超过45个字符*****/
        if (tbVerifyMsgQuery.getUserId().length() > 45) {
            return new CheckResult(ErrorCode._10011.getMsg(), ErrorCode._10011.getCode());
        }
		
		/*检查短信验证码是否为空*/
        if (tbVerifyMsgQuery.getPhoneCode().isEmpty()) {
            return new CheckResult(ErrorCode._10062.getMsg(), ErrorCode._10062.getCode());
        }
        return null;
    }

}
