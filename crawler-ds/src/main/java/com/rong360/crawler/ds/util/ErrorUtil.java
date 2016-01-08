package com.rong360.crawler.ds.util;

import com.rong360.crawler.bean.ErrorCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/8.
 */
public class ErrorUtil {
    public enum Source {
        JD, TB
    }
    public static Map<Integer, ErrorCode> commonReflectMap = new HashMap<>();
    public static Map<Integer, ErrorCode> jdReflectMap = new HashMap<>();
    public static Map<Integer, ErrorCode> tbReflectMap = new HashMap<>();

    public static ErrorCode SUCCEED_CODE = new ErrorCode(200, "ok");
    public static ErrorCode FAILED_CODE = new ErrorCode(201, "error");
    
    static {
        commonReflectMap.put(ErrorCode._0.getCode(), SUCCEED_CODE);
        commonReflectMap.put(ErrorCode._1.getCode(), FAILED_CODE);
        commonReflectMap.put(ErrorCode._10007.getCode(), FAILED_CODE);
        commonReflectMap.put(ErrorCode._10012.getCode(), new ErrorCode(210101, ErrorCode._10012.getMsg())); //图片验证码输入不正确
        commonReflectMap.put(ErrorCode._10013.getCode(), new ErrorCode(211101, ErrorCode._10013.getMsg())); //动态密码输入不正确或已过期
        commonReflectMap.put(ErrorCode._20001.getCode(), new ErrorCode(200100, ErrorCode._20001.getMsg())); //登录名为空
        commonReflectMap.put(ErrorCode._20011.getCode(), new ErrorCode(210100, ErrorCode._20011.getMsg())); //验证码为空

        tbReflectMap.put(ErrorCode._10016.getCode(), new ErrorCode(505001, ErrorCode._10016.getMsg())); //帐户名密码不匹配

        jdReflectMap.put(ErrorCode._10010.getCode(), new ErrorCode(504002, ErrorCode._10010.getMsg())); //页面过期，请重试
        jdReflectMap.put(ErrorCode._10016.getCode(), new ErrorCode(504001, ErrorCode._10016.getMsg())); //帐户名密码不匹配
        jdReflectMap.put(ErrorCode._10063.getCode(), new ErrorCode(504003, ErrorCode._10063.getMsg())); //账户名不存在
    }

    /**
     * @param errorCode 旧版errorCode
     * @param source 来源：JD or TB
     * @return dsErrorCode
     */
    public static ErrorCode transfer(ErrorCode errorCode, Source source) {
        return transfer(errorCode.getCode(), source);
    }

    public static ErrorCode transfer(int code, Source source) {
        Map<Integer, ErrorCode> map = new HashMap<>();
        switch (source) {
            case JD:
                map = jdReflectMap;
                break;
            case TB:
                map = tbReflectMap;
                break;
            default:
                break;
        }
        // 先检查是否公用的errorCode
        if (commonReflectMap.containsKey(code)) {
            return commonReflectMap.get(code);
        } else if (map.containsKey(code)) {
            return map.get(code);
        } else {
            return FAILED_CODE;
        }
    }

    public static ErrorCode transferJD(ErrorCode errorCode) {
        return transfer(errorCode, Source.JD);
    }

    public static ErrorCode transferTB(ErrorCode errorCode) {
        return transfer(errorCode, Source.TB);
    }
}
