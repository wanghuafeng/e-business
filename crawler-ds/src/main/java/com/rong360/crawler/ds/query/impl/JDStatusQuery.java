package com.rong360.crawler.ds.query.impl;

import com.rong360.crawler.query.Query;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Administrator on 2015/12/10.
 */
@XmlRootElement(name = "jdStatusQuery")
public class JDStatusQuery extends Query implements Cloneable {
    @FormParam("status_id")
    String statusId;

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}
