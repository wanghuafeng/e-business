package com.rong360.crawler.ds.api;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import javax.ws.rs.ApplicationPath;

/**
 * Created by Administrator on 2015/12/8.
 */
@ApplicationPath("api/")
public class DSOuterApplication extends ResourceConfig {
    public DSOuterApplication() {
        register(RequestContextFilter.class);
        register(JDOuterApiResource.class);
        register(TBOuterApiResource.class);
    }
}
