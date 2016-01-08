package com.rong360.crawler.ds.test;

import com.rong360.crawler.ds.processor.extractor.AlipayOrderExtractor;
import junit.framework.TestCase;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 支付宝订单解析测试类
 * Created by Administrator on 2015/10/28.
 */
public class AlipayOrderExtractorTest extends TestCase {
    public AlipayOrderExtractor alipayOrderExtractor = new AlipayOrderExtractor();

    public void testProcess() throws Exception {

    }

    public void testExtractor() throws Exception {

    }

    public void testExtractPersonalInfo() throws Exception {

    }

    public void testExtractBankCard() throws Exception {

    }

    public void testGetNewSourceCode() throws Exception {

    }

    public void testGetMatchedValues() throws Exception {
        File file = new File("C:\\Users\\Administrator\\Desktop\\zhifubao\\");
        for (File f : file.listFiles()) {
            if (f.getName().contains(".htm")) {
                String sourceCode = TestTaoBaoPCOrderExtractor.getContent(f.getAbsolutePath());
                AlipayOrderExtractor alipayOrderExtractor = new AlipayOrderExtractor();
                Map<String, String> map = alipayOrderExtractor.getMatchedValues(alipayOrderExtractor.securityRegexMap, sourceCode, "");
                System.out.println(map);
            }
        }
    }

    public void testGetValues() throws Exception {

    }

    public void testFormat() throws Exception {

    }

    public void testFormat1() throws Exception {

    }

    public void testFormat2() throws Exception {

    }

    public void testMerge() throws Exception {

    }

    public void testGetOpenStatus() throws Exception {

    }

    public void testExtractOrdersBetween() throws Exception {

    }

    public void testExtractOrdersBetween1() throws Exception {

    }

    public void testExtractOrders() throws Exception {
        File file = new File("C:\\Users\\Administrator\\Desktop\\zhifubao\\");
        for (File f : file.listFiles()) {
            if (f.getName().contains(".htm")) {
                String sourceCode = TestTaoBaoPCOrderExtractor.getContent(f.getAbsolutePath());
                List<Map<String, String>> list = alipayOrderExtractor.extractOrders(sourceCode, "", "");
                for (Map<String, String> map : list) {
                    System.out.println(map);
                }

            }
        }
    }

}