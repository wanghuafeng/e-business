package com.rong360.crawler.ds.bean;

import com.rong360.crawler.bean.CrawlerBean;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2015/10/29.
 */
public class AlipayInfoBean extends CrawlerBean{
    /*支付宝账户名*/
    String loginName = "";
    
    /*淘宝账户名*/
    String taobaoName = "";
    
    /*身份证有效期限*/
    String identifyTime = "";
    
    /*注册时间*/
    long registerDate = 0;
    
    /*是否实名认证*/
    String isRealName = "";
    
    /*是否会员保障*/
    String isProtected = "";
    
    /*是否绑定手机*/
    String isPhone = "";
    
    /*安全等级*/
    String secretLevel = "";
    
    /*账户余额*/
    BigDecimal balance = new BigDecimal(0);
    
    /*余额宝总收入*/
    BigDecimal income = new BigDecimal(0);
    
    
    /*是否身份认证*/
    String isIdCard = "";
    
    /*余额宝总金额*/
    BigDecimal amount = new BigDecimal(0);

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getTaobaoName() {
        return taobaoName;
    }

    public void setTaobaoName(String taobaoName) {
        this.taobaoName = taobaoName;
    }

    public String getIdentifyTime() {
        return identifyTime;
    }

    public void setIdentifyTime(String identifyTime) {
        this.identifyTime = identifyTime;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public String getIsRealName() {
        return isRealName;
    }

    public void setIsRealName(String isRealName) {
        this.isRealName = isRealName;
    }

    public String getIsProtected() {
        return isProtected;
    }

    public void setIsProtected(String isProtected) {
        this.isProtected = isProtected;
    }

    public String getIsPhone() {
        return isPhone;
    }

    public void setIsPhone(String isPhone) {
        this.isPhone = isPhone;
    }

    public String getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(String secretLevel) {
        this.secretLevel = secretLevel;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public String getIsIdCard() {
        return isIdCard;
    }

    public void setIsIdCard(String isIdCard) {
        this.isIdCard = isIdCard;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
