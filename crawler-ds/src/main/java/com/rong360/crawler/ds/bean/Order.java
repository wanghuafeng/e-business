package com.rong360.crawler.ds.bean;

import com.rong360.crawler.bean.CrawlerBean;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author xiongwei
 * @ClassName: Order
 * @Description:订单信息
 * @date 2015-3-26 上午11:34:06
 */
public class Order extends CrawlerBean {
    /*****
     * 订单号
     *****/
    private String orderId;

    /*****
     * 商品名称
     *****/
    private String productNames;

    /*****
     * 收货人
     *****/
    private String receiver;

    /*****
     * 订单金额
     *****/
    private double money;

    /*****
     * 支付方式
     *****/
    private String buyway;

    /*****
     * 购买时间
     *****/
    private long buyTime;

    /*****
     * 订单状态
     *****/
    private String orderstatus;

    /*****
     * 订单来源
     *****/
    private String ordersource;

    /*****
     * 登录账户
     *****/
    private String loginName;

    /*****
     * 收货地址
     *****/
    private String receiverAddr;

    /*****
     * 邮编
     *****/
    private String receiverPost;

    /*****
     * 固定电话
     *****/
    private String receiverFixPhone;

    /*****
     * 手机号
     *****/
    private String receiverTelephone;

    /*****
     * email
     *****/
    private String receiverEmail;

    /*****
     * 数据来源
     *****/
    private String source;

    /*****
     * HbaseKey
     *****/
    private String hbaseKey;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Field[] fields = this.getClass().getDeclaredFields();
        String s = ",   ";
        for (int i = 0, len = fields.length; i < len; i++) {
            try {
                String varName = fields[i].getName();
                boolean accessFlag = fields[i].isAccessible();
                fields[i].setAccessible(true);
                Object o = fields[i].get(this);
                if (o != null && !"".equals(o.toString().trim())) {
                    sb.append(varName).append("=").append(o);
                    sb.append(s);
                }
                fields[i].setAccessible(accessFlag);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        int index = sb.toString().lastIndexOf(s);
        if (index == -1) {
            return "";
        }
        return sb.toString().substring(0, index);
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getBuyway() {
        return buyway;
    }

    public void setBuyway(String buyway) {
        this.buyway = buyway;
    }

    public long getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(long buyTime) {
        this.buyTime = buyTime;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getOrdersource() {
        return ordersource;
    }

    public void setOrdersource(String ordersource) {
        this.ordersource = ordersource;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(String receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public String getReceiverPost() {
        return receiverPost;
    }

    public void setReceiverPost(String receiverPost) {
        this.receiverPost = receiverPost;
    }

    public String getReceiverFixPhone() {
        return receiverFixPhone;
    }

    public void setReceiverFixPhone(String receiverFixPhone) {
        this.receiverFixPhone = receiverFixPhone;
    }

    public String getReceiverTelephone() {
        return receiverTelephone;
    }

    public void setReceiverTelephone(String receiverTelephone) {
        this.receiverTelephone = receiverTelephone;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getHbaseKey() {
        return hbaseKey;
    }

    public void setHbaseKey(String hbaseKey) {
        this.hbaseKey = hbaseKey;
    }

    private static Order from(Map<String, String> map) {
        Order order = new Order();
        try {
            BeanUtils.copyProperties(order, map);
            return order;
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Order from(Map<String, String> map, String hbaseKey) {
        Order order = from(map);
        if (order != null) {
            order.setHbaseKey(hbaseKey);
            return order;
        } else {
            return null;
        }
    }

}
