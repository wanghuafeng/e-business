package com.rong360.crawler.ds.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.rong360.crawler.util.HtmlUtils;
import com.rong360.crawler.util.RegExpUtil;


public class TestJDOrderExtractor {

	/*****JD 订单正则表达式属性*****/
	private static Map<String, String> orderPropertiesRegxMap = new HashMap<String, String>();

	/*****商品清单列表 *****/
	private static String pruductBlockRegx = "(?:<dd class=\"p-list\">|<table class=\"tb-void tb-none\">)(.*?)(?:</table>)";
	
	/*****商品名称列表 *****/
	private  static String pruductNameRegx = "(?:<a.*?href=(?:'|\")(?:http://www.360buy.com/product/\\d+.html|http://item.jd.com/\\d+.html).*?(?:'|\").*?>)(.*?)(?:</a>)";
	
	
	static {
		{
			
			/*****订单号 *****/
			orderPropertiesRegxMap.put("orderId", "(?:<strong>订单编号：|<div class=fl>订单号：|订单编号</td>.*?<td>|订单号：)(\\d+)(?:</td>|&nbsp| &nbsp;)");

			
			/*****收货人 *****/
			orderPropertiesRegxMap.put("receiver", "(?:<td>收货人姓名</td>.*?<td>|<li>收&nbsp;货&nbsp;人：)(.*?)(?:</td>|</li>)");
			
			/*****订单金额 *****/
			orderPropertiesRegxMap.put("money", "(?:<li>应付总额：.*?￥|商品总金额：</span>￥|订单总金额：<strong class=\"ftx-01\">|总商品金额：</span>￥|应付总额：.*?&yen;)(.*?)(?:</strong>|</li>|</b></span>|</li>.*?<li><span>-余额)");
			
			
			/*****支付方式*****/
			orderPropertiesRegxMap.put("buyway", "(?:<td>支付方式</td>.*?<td>|<li>支付方式：)(.*?)(?:</li>|</td>)");
			
			/*****购买时间 *****/
			orderPropertiesRegxMap.put("buyTime", "(\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2})");
			
			/*****订单状态 *****/
			orderPropertiesRegxMap.put("orderstatus", "(?:状态：<span class=\"ftx-02\">|<div class=\"mt\">.*?<h3>|状态：<span class=\"ftx14\">)(.*?)(?:订单</h3>|</span>|订单</h3>|</span></div)");
			
			
			/*****收货地址 *****/
			orderPropertiesRegxMap.put("receiverAddr", "(?:<td>地址</td>.*?<td>|<li>地&nbsp;&nbsp;&nbsp;&nbsp;址：)(.*?)(?:</li>|</td>)");
			
			/*****固定电话*****/
			orderPropertiesRegxMap.put("receiverFixPhone", "(?:td>固定电话</td>.*?<td>|<li>固定电话：)(.*?)(?:手机号码|</li>)");

			
			/*****手机号 *****/
			orderPropertiesRegxMap.put("receiverTelephone", "(?:手机号码：|<li>手机号码：)(.*?)(?:</td>|</li>)");
			
			
			/*****email *****/
			orderPropertiesRegxMap.put("receiverEmail", "(?:<td>电子邮件</td>.*?<td>|<li>电子邮件：)(.*?)(?:</td>|</li>)");
			
		}
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		

		extractOrder();

	}
	
    public static void extractOrder() throws IOException {
    	

	    
	    File file = new File("C:\\Users\\Administrator\\Desktop\\jd\\");
	    for (File f : file.listFiles()) {
	    	if (f.getName().indexOf(".htm") != -1) {
	    	    
	    		/*****JD 订单属性提取值*****/
	    	    Map<String, String> orderPropertiesMap = new HashMap<String, String>();
	    	    
	        	/*****按块分隔每一个订单*****/
	    		String orderDetail = TestTaoBaoPCOrderExtractor.getContent(f.getAbsolutePath());
	    		
	    		/*****商品清单列表*****/
	    		Matcher productListMatcher = RegExpUtil.getMatcher(orderDetail, pruductBlockRegx);
	    		if (productListMatcher.find()) {
	    			String productList = productListMatcher.group();
	    			Matcher productNameMatcher = RegExpUtil.getMatcher(productList, pruductNameRegx);
	    			/*****商品名称列表*****/
	    			String productNames = "";
	    			while (productNameMatcher.find()) {
	    				String name = HtmlUtils.escapeHtmlTag(productNameMatcher.group(1));
	    				if (StringUtils.isNotBlank(name)) {
	    					productNames += name + ";";
	    				}
	    			}
	    			if (StringUtils.isNotBlank(productNames)) {
	    				productNames = productNames.substring(0, productNames.lastIndexOf(";"));
	    				orderPropertiesMap.put("productNames", productNames);
	    			}
	    		}
	    		
	    		/*****按照每一个属性提取其值*****/
	    		for(Map.Entry<String, String> entry : orderPropertiesRegxMap.entrySet()) {
	    			String propertyName = entry.getKey();
	    			String propertyValue = entry.getValue();
	    			Matcher propertyMatcher = RegExpUtil.getMatcher(orderDetail, propertyValue);
	    			if (propertyMatcher.find()) {
	    				String value = HtmlUtils.escapeHtmlTag(propertyMatcher.group(1));
	    				orderPropertiesMap.put(propertyName, value);
	    			}
	    		}
	    		if (orderPropertiesMap.containsKey("orderId")) {
		    		System.out.println(f.getName() + ":" + JSONObject.fromObject(orderPropertiesMap).toString(1) + "\n\n");	
	    		}

	    	}
	    }

		
    }
}
