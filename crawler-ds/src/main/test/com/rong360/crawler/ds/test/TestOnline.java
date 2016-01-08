package com.rong360.crawler.ds.test;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang.StringUtils;

import com.rong360.crawler.bean.CrawlerState;
import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.bean.Job;
import com.rong360.crawler.bean.MetaData;
import com.rong360.crawler.bean.SourceData;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.page.UserAgentType;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.util.URIUtils;


public class TestOnline {

    /**
     * @param args
     */
    public static void main(String[] args) {

        UriData uriData = new UriData(0, URIUtils.getHttpURL("https://login.taobao.com:443/member/login.jhtml?tpl_redirect_url=https%3A%2F%2Fauthgtj.alipay.com%3A443%2Flogin%2FtrustLoginResultDispatch.htm%3FredirectType%3D%26sign_from%3D3000%26goto%3Dhttps%253A%252F%252Flab.alipay.com%252Fuser%252Fi.htm%253Fsrc%253Dyy_content_jygl&from_alipay=1"));
//        UriData uriData = new UriData(0, URIUtils.getHttpURL("https://login.taobao.com/member/login.jhtml?tpl_redirect_url=https%3A%2F%2Fauthsu18.alipay.com%3A443%2Flogin%2FtrustLoginResultDispatch.htm%3FredirectType%3D%26sign_from%3D3000%26goto%3Dhttps%253A%252F%252Flab.alipay.com%252Fuser%252Fi.htm%253Fsrc%253Dyy_content_jygl&from_alipay=1"));
        uriData.setId("xiongwei");
        String cookie = "thw=cn; cna=EgkhDQ9xjW8CAT2Uy/qcinoI; wud=wud; linezing_session=wD1wWZNJf3Ce310QgL53KJ1H_1444789824820T6hv_1; lzstat_uv=32387864803775828616|2728987@2706017@2618224; lzstat_ss=2887253876_1_1426762672_2728987|2744487740_1_1428416078_2706017|1426898397_0_1445615758_2618224; _tb_token_=b5m4jeT47Ds12Z; CNZZDATA30050853=cnzz_eid%3D203688605-1445505779-https%253A%252F%252Fmember1.taobao.com%252F%26ntime%3D1445849155; v=0; uc3=nk2=B1BaVwQkJRwqnpLA&id2=W8CFoIm%2FIX4g&vt3=F8dASMdPjj%2B8IG4uWas%3D&lg2=W5iHLLyFOGW7aA%3D%3D; existShop=MTQ0NTkyODQ3NQ%3D%3D; lgc=drteamshmily; tracknick=drteamshmily; sg=y44; cookie2=1528e2164e552ee8e3048a5fefdb0603; cookie1=VW9L%2B%2BnyONzfh3IPFjJ5YQlaVzS2cI5be932weHHMYg%3D; unb=898955534; skt=617f65d5dbf60243; t=1f75288ed11ca06c72ca789ed0995dac; _cc_=URm48syIZQ%3D%3D; tg=0; _l_g_=Ug%3D%3D; _nk_=drteamshmily; cookie17=W8CFoIm%2FIX4g; mt=ci=0_1&cyk=0_0; x=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0%26_ato%3D0%26__ll%3D-1; whl=-1%260%260%261445929989803; uc1=cookie14=UoWzXLnMCGIgbg%3D%3D&existShop=false&cookie16=W5iHLLyFPlMGbLDwA%2BdvAGZqLg%3D%3D&cookie21=VFC%2FuZ9ainBZ&tag=2&cookie15=UIHiLt3xD8xYTw%3D%3D&pas=0; l=AioqgjzEUA-KCtKJ5FPQQgCA-p7Mka71; isg=8EF927711E927C73C378BA4ADC829A00";
        CrawlerPage cp = new CrawlerPage();
        cp.setSourceData(new SourceData());
        cp.setMetaData(new MetaData());
        cp.getMetaData().setSuccedExtract(true);
        cp.setUriData(uriData);
        cp.setSourceData(new SourceData());
        cp.setCrawlerState(CrawlerState.UPDATED);
        cp.setJob(Job.JD);
        cp.getUriData().setSave(false);
		cp.getUriData().setReferer("https://auth.alipay.com/login/trust_login.do?src=yy_content_jygl&sign_account_no=20881020226126200156&sign_from=3000&goto=https://lab.alipay.com/user/i.htm");
        cp.getUriData().setHttpMethod(HttpMethod.GET);
        Fetcher fetcher = new Fetcher();
        cp.getUriData().setCookieString(cookie);
        fetcher.process(cp);
        String cookie1 = cp.getUriData().getCookieString();
//        System.out.println(cp.getSourceData().getSourceCode());


        extractParam(cp);
        cp.getUriData().setHttpMethod(HttpMethod.POST);
        uriData = cp.getUriData();
        uriData.setUri(URIUtils.getHttpURL("https://authsu18.alipay.com/login/certCheck.htm"));
        cp.getUriData().setCookieString(cookie);
        fetcher.process(cp);
        String cookie2 = cp.getUriData().getCookieString();
//			System.out.println(cp.getSourceData().getSourceCode());

        uriData = cp.getUriData();
        uriData.setUri(URIUtils.getHttpURL("https://lab.alipay.com:443/user/navigate.htm?goto=https%3A%2F%2Flab.alipay.com%2Fuser%2Fi.htm%3Fsrc%3Dyy_content_jygl"));
        cp.getUriData().setCookieString(cookie1 + cookie2 + cookie);
        cp.getUriData().setReferer("https://authsu18.alipay.com/login/certCheck.htm");
        cp.getUriData().setHttpMethod(HttpMethod.GET);
        fetcher.process(cp);
        System.out.println(cp.getSourceData().getSourceCode());

    }

    public static void extractParam(CrawlerPage crawlerPage) {

        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        Source source = new Source(sourceCode);
        for (Element element : source.getAllElements()) {
            if ("input".equalsIgnoreCase(element.getName())) {
                if (StringUtils.isNotBlank(element.getAttributeValue("name"))) {
                    if (StringUtils.isBlank(crawlerPage.getUriData().getParams().get(element.getAttributeValue("name")))) {
                        crawlerPage.getUriData().getParams().put(element.getAttributeValue("name"), element.getAttributeValue("value"));
                    }
                }
            }
        }
    }

}
