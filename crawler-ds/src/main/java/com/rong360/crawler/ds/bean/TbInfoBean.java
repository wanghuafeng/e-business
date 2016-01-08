package com.rong360.crawler.ds.bean;

import com.rong360.crawler.bean.CrawlerBean;
import net.sf.json.JSONArray;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2015/10/26.
 */
public class TbInfoBean extends CrawlerBean {
    /**
     * 淘宝账户名
     */
    String loginName = "";

    /**
     * 淘宝信用度
     */
    String taobaoLevel = "";
    
    /**
     * 淘宝买家级别
     */
    int buyerLevel = 0;
    
    /**
     * 淘宝好评率
     */
    double buyerPositive = 0.0;
    
    /**
     * 淘宝上次登陆时间
     */
    long lastLoginTime = 0;
    
    /**
     * 生日
     */
    long birthday = 0;
    
    /**
     * 居住地
     */
    String residence = "";

    /**
     * 家乡
     */
    String hometown = "";
    
    /**
     * 成长值
     */
    int growthNumber = 0;
    
    /**
     * 淘宝币
     */
    int taobaoCoins = 0;
    
    /**
     * 淘宝绑定的支付宝账户名
     */
    String alipayName = "";
    
    /**
     * 淘宝绑定的支付宝账户类型
     */
    String accountType = "";
    
    /**
     * 淘宝绑定的支付宝账户状态
     */
    String accountStatus = "";
    
    /**
     * 淘宝绑定的支付状态
     */
    String isRelated = "";
    
    /**
     * 手机号码
     */
    String mobile = "";
    
    /**
     * 淘宝绑定的支付宝可用余额
     */
    BigDecimal accountRemain = new BigDecimal(0.0);
    
    /**
     * 邮编
     */
    int postCode = 0;
    
    /**
     * 固定电话
     */
    String phoneNumber = "";
    
    /**
     * 天猫账户
     */
    String tianmaoAccount = "";
    
    /**
     * 天猫积分
     */
    int tianmaoPoints = 0;
    
    /**
     * 天猫等级
     */
    int tianmaoLevel = 0;
    
    /**
     * 天猫经验
     */
    int tianmaoExperience = 0;
    
    /**
     * 权益消费额度
     */
    BigDecimal huaBei = new BigDecimal(0.0);
    
    /**
     * 天猫购买天数
     */
    int buyDays = 0;

    /**
     * 淘宝常用收获地址
     */
    JSONArray addrs = new JSONArray();

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getTaobaoLevel() {
        return taobaoLevel;
    }

    public void setTaobaoLevel(String taobaoLevel) {
        this.taobaoLevel = taobaoLevel;
    }

    public int getBuyerLevel() {
        return buyerLevel;
    }

    public void setBuyerLevel(int buyerLevel) {
        this.buyerLevel = buyerLevel;
    }

    public double getBuyerPositive() {
        return buyerPositive;
    }

    public void setBuyerPositive(double buyerPositive) {
        this.buyerPositive = buyerPositive;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public int getGrowthNumber() {
        return growthNumber;
    }

    public void setGrowthNumber(int growthNumber) {
        this.growthNumber = growthNumber;
    }

    public int getTaobaoCoins() {
        return taobaoCoins;
    }

    public void setTaobaoCoins(int taobaoCoins) {
        this.taobaoCoins = taobaoCoins;
    }

    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getIsRelated() {
        return isRelated;
    }

    public void setIsRelated(String isRelated) {
        this.isRelated = isRelated;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public BigDecimal getAccountRemain() {
        return accountRemain;
    }

    public void setAccountRemain(BigDecimal accountRemain) {
        this.accountRemain = accountRemain;
    }

    public int getPostCode() {
        return postCode;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTianmaoAccount() {
        return tianmaoAccount;
    }

    public void setTianmaoAccount(String tianmaoAccount) {
        this.tianmaoAccount = tianmaoAccount;
    }

    public int getTianmaoPoints() {
        return tianmaoPoints;
    }

    public void setTianmaoPoints(int tianmaoPoints) {
        this.tianmaoPoints = tianmaoPoints;
    }

    public int getTianmaoLevel() {
        return tianmaoLevel;
    }

    public void setTianmaoLevel(int tianmaoLevel) {
        this.tianmaoLevel = tianmaoLevel;
    }

    public int getTianmaoExperience() {
        return tianmaoExperience;
    }

    public void setTianmaoExperience(int tianmaoExperience) {
        this.tianmaoExperience = tianmaoExperience;
    }

    public BigDecimal getHuaBei() {
        return huaBei;
    }

    public void setHuaBei(BigDecimal huaBei) {
        this.huaBei = huaBei;
    }

    public int getBuyDays() {
        return buyDays;
    }

    public void setBuyDays(int buyDays) {
        this.buyDays = buyDays;
    }

    public JSONArray getAddrs() {
        return addrs;
    }

    public void setAddrs(JSONArray addrs) {
        this.addrs = addrs;
    }
}
