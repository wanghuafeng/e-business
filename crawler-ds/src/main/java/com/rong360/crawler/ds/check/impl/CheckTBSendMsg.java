package com.rong360.crawler.ds.check.impl;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.bean.ErrorCode;
import com.rong360.crawler.ds.query.impl.TBSendMsgQuery;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2015/12/15.
 */
public class CheckTBSendMsg {
    public CheckResult check(TBSendMsgQuery tbSendMsgQuery) {
        /******检查登录名为空*****/
        if (StringUtils.isEmpty(tbSendMsgQuery.getLoginName())) {
            return new CheckResult(ErrorCode._20001.getMsg(), ErrorCode._20001.getCode());
        }


        /******检查手机号码是否为空*****/
        if (StringUtils.isEmpty(tbSendMsgQuery.getUserId())) {
            return new CheckResult(ErrorCode._10008.getMsg(), ErrorCode._10008.getCode());
        }

        /******检查是否为空*****/
        if (StringUtils.isEmpty(tbSendMsgQuery.getPhone())) {
            return new CheckResult(ErrorCode._20004.getMsg(), ErrorCode._20004.getCode());
        }


        /******检查userId长度是否超过45个字符*****/
        if (tbSendMsgQuery.getUserId().length() > 45) {
            return new CheckResult(ErrorCode._10011.getMsg(), ErrorCode._10011.getCode());
        }
        return null;
    }

}
