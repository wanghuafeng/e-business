package com.rong360.crawler.ds.bean;

import com.rong360.crawler.bean.CrawlerBean;

/**
 * Created by Administrator on 2015/10/29.
 */
public class TbAddrBean extends CrawlerBean {
    /*淘宝账户名*/
    String loginName = "";

    /*淘宝收货人*/
    String receiverName = "";

    /*所在地区*/
    String addr = "";

    /*详细地址*/
    String addrDetail = "";

    /*邮政编码*/
    String postCode = "";

    /*联系方式*/
    String phone = "";

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAddrDetail() {
        return addrDetail;
    }

    public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
