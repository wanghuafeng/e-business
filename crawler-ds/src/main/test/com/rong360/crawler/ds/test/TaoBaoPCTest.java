package com.rong360.crawler.ds.test;

import net.sf.json.JSONObject;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class TaoBaoPCTest extends TestCase {

    private String domain = "localhost";	
//    private String domain = "192.168.88.128";
//    private String domain = "10.0.2.113";
    private String loginName = "drteamshmily";
    private String password = "gcipo31415";

    /**
     * 测试获取验证码
     *****/
    public void testAuthCode() {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/taobaoPC/verifyUser.json"));
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
        cp.getUriData().getParams().put("loginName", loginName);
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
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/taobaoPC/login.json"));
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
        cp.getUriData().getParams().put("loginName", loginName);
        cp.getUriData().getParams().put("userId", "0031_01112");
        cp.getUriData().getParams().put("password", password);
        cp.getUriData().getParams().put("authcode", "fwqm");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }


    /**
     * 测试获取验证码
     *****/
    public void testSendMsg() {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/taobaoPC/sendMsg.json"));
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
        cp.getUriData().getParams().put("loginName", loginName);
        cp.getUriData().getParams().put("userId", "0031_01112");
        cp.getUriData().getParams().put("phone", "188****7592");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }


    /**
     * 测试获取验证码
     *****/
    public void testVerifyMsg() {
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://" + domain + ":8080/crawler/openapi/ds/taobaoPC/verifyMsg.json"));
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
        cp.getUriData().getParams().put("loginName", loginName);
        cp.getUriData().getParams().put("userId", "0031_01112");
        cp.getUriData().getParams().put("phoneCode", "843310");
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

    public void testSaveTb() {
        String data = "{\"tbInfo\": {\"accountRemain\": 0,\"accountStatus\": \"\",\"accountType\": \"个人账户\",\"addrs\": [{\"postCode\": \"000000\",\"addr\": \"北京 北京市 昌平区 回龙观\",\"addrDetail\": \"北农路2号华北电力大学\",\"receiverName\": \"辛雅晨\",\"phone\": \"86-18*******89\"},{\"postCode\": \"100082\",\"addr\": \"北京 北京市 海淀区 北太平庄街道\",\"addrDetail\": \"文慧园北路4号院7单元403室\",\"receiverName\": \"郭聪\",\"phone\": \"86-18*******92\"},{\"postCode\": \"200060\",\"addr\": \"上海 上海市 普陀区 长寿路街道\",\"addrDetail\": \"江宁路1078号玉佛城物华阁7C\",\"receiverName\": \"李雯依\",\"phone\": \"86-15*******86\"}],\"alipayName\": \"guo****jx@gmail.com 修改\",\"assemleBean\": \"{\\\"postCode\\\":200060,\\\"residence\\\":\\\"110108\\\",\\\"lastLoginTime\\\":0,\\\"taobaoLevel\\\":\\\"29\\\",\\\"growthNumber\\\":9142,\\\"hometown\\\":\\\"360922\\\",\\\"alipayName\\\":\\\"guo****jx@gmail.com 修改\\\",\\\"taobaoCoins\\\":145,\\\"buyerPositive\\\":100,\\\"buyerLevel\\\":2,\\\"birthday\\\":0,\\\"accountType\\\":\\\"个人账户\\\",\\\"isRelated\\\":\\\"已绑定淘宝账户\\\",\\\"accountRemain\\\":0,\\\"phoneNumber\\\":\\\"86-15*******86\\\",\\\"tianmaoPoints\\\":0,\\\"tianmaoLevel\\\":3,\\\"huaBei\\\":3000,\\\"buyDays\\\":16,\\\"loginName\\\":\\\"drteamshmily\\\",\\\"tianmaoAccount\\\":\\\"drteamshmily\\\",\\\"tianmaoExperience\\\":8107,\\\"addrs\\\":[{\\\"postCode\\\":\\\"000000\\\",\\\"addr\\\":\\\"北京 北京市 昌平区 回龙观\\\",\\\"addrDetail\\\":\\\"北农路2号华北电力大学\\\",\\\"receiverName\\\":\\\"辛雅晨\\\",\\\"phone\\\":\\\"86-18*******89\\\"},{\\\"postCode\\\":\\\"100082\\\",\\\"addr\\\":\\\"北京 北京市 海淀区 北太平庄街道\\\",\\\"addrDetail\\\":\\\"文慧园北路4号院7单元403室\\\",\\\"receiverName\\\":\\\"郭聪\\\",\\\"phone\\\":\\\"86-18*******92\\\"},{\\\"postCode\\\":\\\"200060\\\",\\\"addr\\\":\\\"上海 上海市 普陀区 长寿路街道\\\",\\\"addrDetail\\\":\\\"江宁路1078号玉佛城物华阁7C\\\",\\\"receiverName\\\":\\\"李雯依\\\",\\\"phone\\\":\\\"86-15*******86\\\"}]}\",\"birthday\": 0,\"buyDays\": 16,\"buyerLevel\": 2,\"buyerPositive\": 100,\"growthNumber\": 9142,\"hometown\": \"360922\",\"huaBei\": 3000,\"isRelated\": \"已绑定淘宝账户\",\"lastLoginTime\": 0,\"loginName\": \"drteamshmily\",\"mobile\": \"\",\"phoneNumber\": \"86-15*******86\",\"postCode\": 200060,\"residence\": \"110108\",\"taobaoCoins\": 145,\"taobaoLevel\": \"29\",\"tianmaoAccount\": \"drteamshmily\",\"tianmaoExperience\": 8107,\"tianmaoLevel\": 3,\"tianmaoPoints\": 0}}";
        UriData uriData = new UriData(0, URIUtils.getHttpURL("http://wd.rong360.com/api/jsd/saveTaoBaoUser"));
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.getUriData().getParams().put("http_method", "post");
        cp.getUriData().getParams().put("uid", "01112");
        cp.getUriData().getParams().put("data", JSONObject.fromObject(data).toString());
        cp.getUriData().getParams().put("token", "8c500e23bdf643fe8cf8b644c92dd6f5");
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        Fetcher fetcher = new Fetcher();
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());
    }
    
    public void testTest() throws UnsupportedEncodingException {
        System.out.println(URLDecoder.decode("%E9%AA%8C%E8%AF%81%E5%A4%B1%E8%B4%A5", "utf-8"));
    }
}