package com.rong360.crawler.ds.test;

import com.rong360.crawler.ds.bean.HtmlBean;
import com.rong360.crawler.ds.bean.Order;
import com.rong360.crawler.ds.processor.extractor.TaobaoOrderExtractor;
import junit.framework.TestCase;

import java.io.File;

/**
 * 淘宝订单解析测试类
 * Created by Administrator on 2016/1/7.
 */
public class TaobaoOrderExtractorTest extends TestCase {

    public void testExtractTranOrder() throws Exception {
        File file = new File("C:\\Users\\Administrator\\Desktop\\taobao\\火车票1.html");
        String sourceCode = TestTaoBaoPCOrderExtractor.getContent(file.getAbsolutePath());
        TaobaoOrderExtractor extractor = new TaobaoOrderExtractor();
        Order order = extractor.extractTrainOrder(new HtmlBean(sourceCode, ""));
        System.out.println(order.toString());
    }

    public void testExtractOrderDetail() throws Exception {
        File file = new File("C:\\Users\\Administrator\\Desktop\\taobao\\");
        for (File f : file.listFiles()) {
            if (f.getName().contains(".htm")) {
                String sourceCode = TestTaoBaoPCOrderExtractor.getContent(f.getAbsolutePath());
                TaobaoOrderExtractor alipayOrderExtractor = new TaobaoOrderExtractor();
                Order order = alipayOrderExtractor.extractOrderDetail(new HtmlBean(sourceCode, ""));
                System.out.println(order.toString());
            }
        }
    }
}