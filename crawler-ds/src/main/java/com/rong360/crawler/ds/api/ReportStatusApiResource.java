package com.rong360.crawler.ds.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.rong360.crawler.api.impl.ApiResourceImpl;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.rong360.crawler.bean.CheckResult;
import com.rong360.crawler.ds.check.ICheckStatusService;
import com.rong360.crawler.ds.check.impl.CheckStatusQueryImpl;
import com.rong360.crawler.ds.query.impl.CheckStatusQuery;


/**
 * 
 * @ClassName: ReportStatusApiResource
 * @Description:检查抓取状态api
 * @author xiongwei
 * @date 2015-6-26 上午11:46:19
 * 
 */
@Path("/report/")
public class ReportStatusApiResource extends ApiResourceImpl {
	
	@Autowired
	private ICheckStatusService checkStatusService;
	
    @POST
	@Path("/checkstatus.json")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED})  
	@Produces({MediaType.APPLICATION_JSON})
    public String checkstatus(@Context HttpServletRequest request, @BeanParam CheckStatusQuery checkStatusQuery) {
		/******1.通过判断参数输入合法性,是否需要执行下一步操作*****/
	    CheckResult checkResult = new CheckStatusQueryImpl().check(checkStatusQuery);
		JSONObject result = null;
		
		if (checkResult == null) {
			/******2.执行查询操作*****/
			result = JSONObject.fromObject(checkStatusService.checkStatus(checkStatusQuery));

		} else {
			result = new JSONObject();
			result.put("code", checkResult.getErrorcode());
			result.put("msg", checkResult.getErrorMsg());
		}
		return result.toString();
    }
     
}
