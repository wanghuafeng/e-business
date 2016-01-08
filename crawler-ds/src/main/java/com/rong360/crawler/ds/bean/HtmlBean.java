package com.rong360.crawler.ds.bean;

import com.rong360.crawler.bean.CrawlerBean;

/**
 * Created by Administrator on 2015/10/14.
 */
public class HtmlBean extends CrawlerBean {
    //网页源代码
    private String sourceCode;
    
    //HbaseKey
    private String hbaseKey;

    public HtmlBean(String sourceCode, String hbaseKey) {
        this.sourceCode = sourceCode;
        this.hbaseKey = hbaseKey;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getHbaseKey() {
        return hbaseKey;
    }

    public void setHbaseKey(String hbaseKey) {
        this.hbaseKey = hbaseKey;
    }
}
