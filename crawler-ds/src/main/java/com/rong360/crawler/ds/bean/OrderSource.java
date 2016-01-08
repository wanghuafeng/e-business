package com.rong360.crawler.ds.bean;

/**
 * Created by Administrator on 2015/9/21.
 */
public enum OrderSource {
    JD(1, "JD"),
    TAOBAO(2, "TAOBAO"),
    ALIPAY(3, "ALIPAY");

    private int id;
    private String source;

    OrderSource(int id, String source) {
        this.id = id;
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
