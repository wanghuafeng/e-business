package com.rong360.crawler.ds.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;

import com.rong360.crawler.ds.bean.HtmlBean;
import com.rong360.crawler.ds.bean.Order;
import com.rong360.crawler.ds.processor.extractor.AlipayOrderExtractor;
import com.rong360.crawler.ds.processor.extractor.TaobaoOrderExtractor;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.rong360.crawler.util.HtmlUtils;
import com.rong360.crawler.util.RegExpUtil;


public class TestTaoBaoPCOrderExtractor {

	/*****TAOBAO 订单正则表达式属性*****/
	private static Map<String, String> orderPropertiesRegxMap = new HashMap<String, String>();

	
	/*****TAOBAO 订单正则表达式属性*****/
	private static Map<String, String> bakOrderPropertiesRegxMap = new HashMap<String, String>();
	
	/*****商品清单列表 *****/
	private static String pruductBlockRegx = "(?:class=\"misc-info\">|order-info\">|class=\"trade-status\">|class=\"trade-detail-prompt\">)(.*?)(?:我对购买流程有意见或建议|var p4p_ip|window.p4p_spm|window.p4p_itemid)";
	
	/*****商品名称列表 *****/
	private static String pruductNameRegx = "(?:tradearchive|trade).taobao.com/trade/detail/(?:trade_snap.htm|tradeSnap.htm)\\?trade_id=\\d+(?:'|\").*?>(.*?)(?:</a>)";
	
	
     static {

		 /*****订单号 *****/
		 orderPropertiesRegxMap.put("orderId", "(?:订单号：|class=\"trade-imfor-dd imfor-short-dd\">|<dt>订单编号:|订单编号：</span>|class=\"order-num\">|淘宝交易号：)(.*?)(?:</span>|</dd>|</div)");


		 /*****收货人 *****/
		 orderPropertiesRegxMap.put("receiver", "(?:data-show-address=\"false\">|收货地址.*?：)(.*?)(?:<td></td>|，|</dd>|</div>)");

		 /*****订单金额 *****/
		 orderPropertiesRegxMap.put("money", "(?:实付款：|订单总金额：|实付款.*?&yen;</span><em>|实际票价：|购买总价：.*?&yen;</span>)(.*?)(?:</strong>元|元|</em>.*?</strong>)");


		 /*****支付方式*****/
		 orderPropertiesRegxMap.put("buyway", "(?:<td>支付方式</td>.*?<td>|<li>支付方式：)(.*?)(?:</li>|</td>)");


		 /*****购买时间 *****/
		 orderPropertiesRegxMap.put("buyTime", "(?:成交时间|出票时间：).*?(\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}).*?");


		 /*****订单状态 *****/
		 orderPropertiesRegxMap.put("orderstatus", "(?:.*?订单状态：)(.*?)(?:</h3>|</strong>|</dd>)");


		 /*****收货地址 *****/
		 orderPropertiesRegxMap.put("receiverAddr", "(?:data-show-address=\"false\">|收货地址.*?：|收货地址：</span>)(.+?)(?:</dd>|</li>|<td class=\"label\">|<li class=\"table-list|</div>)");

		 /*****固定电话*****/
		 bakOrderPropertiesRegxMap.put("receiverFixPhone", "((?:0[0-9]{2,3}\\-)+(?:[2-9][0-9]{6,7})+(?:\\-[0-9]{1,4})?)");


		 /*****手机号 *****/
		 bakOrderPropertiesRegxMap.put("receiverTelephone", "((?:0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8})");

		 /*****邮编 *****/
		 bakOrderPropertiesRegxMap.put("receiverPost", "(\\d{6})$");
	 }



		 /**
          * @param args
          * @throws IOException 
          */
	public static void main(String[] args) throws IOException {

//		System.out.println(orderPropertiesRegxMap.get("orderId"));
		extractOrder();

	}
	
    public  static String getContent(String path) throws IOException{
    	StringBuffer buffer = new StringBuffer();
        File file=new File(path);
        if(!file.exists()||file.isDirectory())
            throw new FileNotFoundException();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        String temp=null;
        temp=br.readLine();
        while(temp!=null){            
            temp= br.readLine();
            buffer.append(temp);
        }
        return buffer.toString();
    }

    public static void extractOrder() throws IOException {

	    
	    File file = new File("C:\\Users\\Administrator\\Desktop\\zhifubao\\");
	    for (File f : file.listFiles()) {
	    	if (f.getName().indexOf(".htm") != -1) {
	    	    
	    		/*****JD 订单属性提取值*****/
	    	    Map<String, String> orderPropertiesMap = new HashMap<String, String>();
	    	    
	        	/*****按块分隔每一个订单*****/
	    		String orderDetail = getContent(f.getAbsolutePath());
				AlipayOrderExtractor alipayOrderExtractor = new AlipayOrderExtractor();
				List<Map<String, String>> list = alipayOrderExtractor.extractOrders(orderDetail, "123@qq.com", "");
				for (Map<String, String> map : list) {
					System.out.println(map);
				}

//				TaobaoOrderExtractor taobaoOrderExtractor = new TaobaoOrderExtractor();
//				Order order = taobaoOrderExtractor.extractOrderDetail(new HtmlBean(orderDetail, ""));
//				
//	    		if (StringUtils.isNotBlank(order.getOrderId())) {
//		    		System.out.println(f.getName() + ":" + JSONObject.fromObject(order.getAssemleBean()).toString(1) + "\n\n");	
//	    		}

	    	}
	    }

		
    }
}
