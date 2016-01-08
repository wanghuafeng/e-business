package com.rong360.crawler.ds.bean;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ds_api_result")
public class DSApiResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    // 用户id
    @Column(name = "uid")
    private String uid = "";

    // 设备号
    @Column(name = "device_id")
    private String deviceId = "";

    // 电商账户登录名
    @Column(name = "login_name")
    private String loginName = "";

    // 手机号
    @Column(name = "phone")
    private String phone = "";

    // 接口编号
    @Column(name = "api_id")
    private int apiId = 0;

    // 接口说明
    @Column(name = "api_info")
    private String apiInfo = "";

    // 返回状态码
    @Column(name = "return_code")
    private int returnCode = 0;

    // 返回信息
    @Column(name = "return_msg")
    private String returnMsg = "";

    // 记录更新时间
    @Column(name = "update_time")
    private Date updateTime = new Date(System.currentTimeMillis());

    // 记录创建时间
    @Column(name = "create_time")
    private Date createTime = new Date(System.currentTimeMillis());
    
    @Column(name = "merchant_id")
    private int merchantId = 0;

    @Column(name = "app_name")
    private String appName = "";

    @Column(name = "app_version")
    private String appVersion = "";

    @Column(name = "platform")
    private String platform = "";

    public DSApiResult() {
    }

    public DSApiResult(int apiId, String apiInfo) {
        this.apiId = apiId;
        this.apiInfo = apiInfo;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    public String getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(String apiInfo) {
        this.apiInfo = apiInfo;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    @Override
    public String toString() {
        return "DSApiResult{" +
                "id=" + id +
                ", uid=" + uid +
                ", deviceId='" + deviceId + '\'' +
                ", loginName='" + loginName + '\'' +
                ", phone='" + phone + '\'' +
                ", apiId=" + apiId +
                ", apiInfo='" + apiInfo + '\'' +
                ", returnCode=" + returnCode +
                ", returnMsg='" + returnMsg + '\'' +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }

    public void merge(DSApiResult dsApiResult) {
        this.setDeviceId(dsApiResult.getDeviceId());
        this.setLoginName(dsApiResult.getLoginName());
        this.setPhone(dsApiResult.getPhone());
        this.setReturnCode(dsApiResult.getReturnCode());
        this.setReturnMsg(dsApiResult.getReturnMsg());
        this.setUpdateTime(dsApiResult.getUpdateTime());
        this.setMerchantId(dsApiResult.getMerchantId());
        this.setPlatform(dsApiResult.getPlatform());
        this.setAppVersion(dsApiResult.getAppVersion());
        this.setAppName(dsApiResult.getAppName());
    }
}
