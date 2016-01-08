package com.rong360.crawler.ds.bean;

import com.rong360.crawler.bean.CrawlerBean;

/**
 * Created by Administrator on 2015/10/29.
 */
public class AlipayOrderBean extends CrawlerBean {
    /*交易号*/
    String tradeNo = "";

    /*支付时间*/
    long payTime = 0;

    /*交易类型*/
    String tradeType = "";

    /*交易账号类型*/
    String tradeNoType = "";

    /*接收人*/
    String receiverName = "";

    /*交易金额*/
    double amount = 0.0;

    /*交易状态*/
    String status = "";

    /*来源*/
    String source = "";

    /*支付宝账户名*/
    String alipayName = "";

    /*支付宝交易类型*/
    String type = "";

    /*hbaseKey*/
    String hbaseKey = "";

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public long getPayTime() {
        return payTime;
    }

    public void setPayTime(long payTime) {
        this.payTime = payTime;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeNoType() {
        return tradeNoType;
    }

    public void setTradeNoType(String tradeNoType) {
        this.tradeNoType = tradeNoType;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHbaseKey() {
        return hbaseKey;
    }

    public void setHbaseKey(String hbaseKey) {
        this.hbaseKey = hbaseKey;
    }
}
