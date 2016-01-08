package com.rong360.crawler.ds.api;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import com.rong360.crawler.util.RongLoggingFilter;

import javax.ws.rs.ApplicationPath;



/**
 * 
 * @ClassName: DSApplication
 * @Description:电商API接口类
 * @author xiongwei
 * @date 2015-6-5 上午11:40:49
 *
 */
@ApplicationPath("openapi/ds/")
public class DSApplication extends ResourceConfig{
	
    public DSApplication() {
        register(RequestContextFilter.class);
        register(JDApiResource.class);
        register(ReportStatusApiResource.class);
        register(TaoBaoApiResource.class);
        register(TaoBaoPCApiResource.class);
        register(RongLoggingFilter.class);
    }

}
