package com.rong360.crawler.ds.bean;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ds_order_count")
public class DSOrderCount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "uid")
    private String uid;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "login_name")
    private String loginName;

    @Column(name = "order_source")
    private int orderSource;

    @Column(name = "order_number")
    private int orderNumber;

    @Column(name = "create_time")
    private Date createTime = new Date(System.currentTimeMillis());

    @Column(name = "update_time")
    private Date updateTime = new Date(System.currentTimeMillis());

    @Column(name = "merchant_id")
    private int merchantId = 0;

    @Column(name = "app_name")
    private String appName = "";

    @Column(name = "app_version")
    private String appVersion = "";

    @Column(name = "platform")
    private String platform = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public int getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(int orderSource) {
        this.orderSource = orderSource;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
