package com.rong360.crawler.ds.test;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Base64Utils;

import junit.framework.TestCase;

import com.rong360.crawler.bean.CrawlerState;
import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.MetaData;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.util.URIUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class TaoBaoTest extends TestCase {

    //private String domain = "localhost";	
    private String domain = "localhost";

    /**
     * 测试获取验证码
     *****/
    public void testAuthCode() {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/taobao/verifyUser.json"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("appId", "0031");
        cp.getUriData().getParams().put("timeunit", "1425621163841");
        cp.getUriData().getParams().put("token", "8f5b7decf99aba00e390679c7036b997");
        cp.getUriData().getParams().put("loginName", "drteamshmily");
        cp.getUriData().getParams().put("userId", "0031_01112");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }

    /**
     * 测试登录
     *****/
    public void testLogin() {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/taobao/login.json"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("appId", "0031");
        cp.getUriData().getParams().put("timeunit", "1425621163841");
        cp.getUriData().getParams().put("token", "8f5b7decf99aba00e390679c7036b997");
        cp.getUriData().getParams().put("loginName", "drteamshmily");
        cp.getUriData().getParams().put("userId", "0031_01112");
        cp.getUriData().getParams().put("password", "gcipo31415");
//        cp.getUriData().getParams().put("authcode", "cjac");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }


    /**
     * 测试获取验证码
     *****/
    public void testSendMsg() {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/taobao/sendMsg.json"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("appId", "0031");
        cp.getUriData().getParams().put("timeunit", "1425621163841");
        cp.getUriData().getParams().put("token", "8f5b7decf99aba00e390679c7036b997");
        cp.getUriData().getParams().put("loginName", "drteamshmily");
        cp.getUriData().getParams().put("userId", "0031_01112");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }


    /**
     * 测试验证验证码
     *****/
    public void testVerifyMsg() {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/taobao/verifyMsg.json"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("appId", "0031");
        cp.getUriData().getParams().put("timeunit", "1425621163841");
        cp.getUriData().getParams().put("token", "8f5b7decf99aba00e390679c7036b997");
        cp.getUriData().getParams().put("loginName", "drteamshmily");
        cp.getUriData().getParams().put("userId", "0031_01112");
        cp.getUriData().getParams().put("phoneCode", "489470");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }


    /**
     * 检查状态
     *****/
    public void testCheckStatus() {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/report/checkstatus.json"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("appId", "0031");
        cp.getUriData().getParams().put("timeunit", "1425621163841");
        cp.getUriData().getParams().put("token", "8f5b7decf99aba00e390679c7036b997");
        cp.getUriData().getParams().put("userId", "0031_01112");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }


    public void testBase64() {
        System.out.println(Base64Utils.encodeToString(null));
        System.out.println(StringUtils.isBlank(null));
    }

    public static void main(String[] args) {
        String authcode = "/9j/6gAE95v/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8U\\r\\nHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwh\\r\\nMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAo\\r\\nAJYDAREAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIE\\r\\nAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJico\\r\\nKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZ\\r\\nmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6\\r\\n/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAEC\\r\\nAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNE\\r\\nRUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmq\\r\\nsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEA\\r\\nPwD3+gAoAKACgAoAKACgDi5vip4UtvEFxo1xfNDPbymF5ZExEGHBG72OR+Fe1Hh/HToRxEI3TV7d\\r\\nbehn7WN7HVWOpWOpwefYXlvdRf34JQ4/MGvKq0KtGXLVi4vzVi009i1WQwoA5Hxf8RtD8F3Vvbam\\r\\nLmSadDIqW8YYhc4yckdTn8q9fLslxOYRc6Nklpr/AMMzOdSMNzk7j4/+Hwh+yaVqcsn8IlEaA/iG\\r\\nb+VetDg/F39+cUvK7/REfWI9Eegv4o0q1e0t9RvILK+uVQraSyDzAW6DHfnjNfPLAV5qU6UXKMb6\\r\\n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\\r\\n";
        byte[] bytes = Base64Utils.decodeFromString(authcode);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("test.jpg"));
            bw.write(new String(bytes, "utf-8"));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}