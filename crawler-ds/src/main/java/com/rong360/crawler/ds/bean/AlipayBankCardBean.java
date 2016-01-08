package com.rong360.crawler.ds.bean;

import com.rong360.crawler.bean.CrawlerBean;

/**
 * Created by Administrator on 2015/10/29.
 */
public class AlipayBankCardBean extends CrawlerBean {
    /*id*/
    String id = "";

    /*支付宝账户名*/
    String alipayName = "";

    /*支付宝绑定的银行卡名称*/
    String bankName = "";

    /*银行卡类型名称*/
    String cardType = "";

    /*支付宝绑定的银行卡后四位*/
    String cardLastNum = "";

    /*支付宝绑定的银行卡快捷支付开通状态*/
    String openStatus = "";

    /*支付宝绑定的银行卡申请时间*/
    long applyTime = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardLastNum() {
        return cardLastNum;
    }

    public void setCardLastNum(String cardLastNum) {
        this.cardLastNum = cardLastNum;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }
}
