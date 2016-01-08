package com.rong360.crawler.ds.test;

import com.rong360.crawler.bean.HttpMethod;
import com.rong360.crawler.ds.query.impl.TaoBaoLoginQuery;
import com.rong360.crawler.ds.util.Util;
import com.rong360.crawler.page.CrawlerPage;
import com.rong360.crawler.page.UriData;
import com.rong360.crawler.processor.fetcher.Fetcher;
import com.rong360.crawler.util.URIUtils;

/*
 * 文 件 名:  TestTaobao.java
 * 修 改 人:  chenchong@rong360.com
 * 修改时间:  2015年6月19日
 * 修改内容:  <修改内容>
 */

/**
 * @author chenchong@rong360.com
 */
public class TestTaobao {
    public static void main(String[] args) {

        // UriData uriData = new
        // UriData(0,URIUtils.getHttpURL("http://buyer.trade.taobao.com/trade/detail/trade_item_detail.htm?spm=a1z09.2.9.33.1yjFaK&bizOrderId=1003852244718239"));

        // UriData uriData = new
        // UriData(0,URIUtils.getHttpURL("https://api.weibo.com/2/users/show.json?screen_name=xiongweil&source=2724715451"));

        CrawlerPage cp = Util.getTbDefaultCP(new TaoBaoLoginQuery());
        cp.setUriData(new UriData(0, URIUtils.getHttpURL("https://h5.m.taobao.com/mlapp/olist.html?spm=a2141.7756461.2.6")));
        // cp.getUriData().setReferer("https://ipcrs.pbccrc.org.cn/userReg.do");

        // cp.getUriData().getParams().put("msg", "checkRegLoginnameHasUsed");
        // cp.getUriData().getParams().put("loginname", "sadfsdaf");
        // cp.getUriData().getParams().put("id_no", "431121198605044714");
        // cp.getUriData().getParams().put("ver_code", "21");
        // cp.getUriData().getParams().put("activity_code", "21");
        cp.getUriData().setHttpMethod(HttpMethod.GET);

        // cp.getUriData().setUserAgent(UserAgentType.BAIDUSPIDER);
        // cp.getUriData().setCookieString(new SinaCookie().getCookie());

        // System.setProperty("socksProxySet", "true");
        // System.setProperty("socksProxyHost", "119.254.101.42");
        // System.setProperty("socksProxyPort", Integer.toString(8001));
        for (int i = 0; i < 1; i++) {
            Fetcher fetcher = new Fetcher();
            cp.getUriData().setCookieString("yryetgfhth=%2B4cXpeDzLTAIW6kFUQRecFhmpZ1SemTToLYAFo0PptqYBAFw4GwnFfJarlc8gGiGsu8blxiSwms9Y5B%2B8k1Z3zNRntJGjJJ7ZHrr41MDnSkjUAn8Xkf7RP8PQeKsrPpHqSlr3npgkzr8io2kqSUPEx77mZh%2BUvRT7go9C2TPhtXeXA7it6xLweBcEjW%2BWd%2FKg2Lrnyhw3QLPlVClz6Lt6UNjcAbF8JgoMk15uXsiDZK9h3PTkbQ%2B8jFvY94%3D;uc3=nk2=B1BaVwQkJRwqnpLA&id2=W8CFoIm%2FIX4g&vt3=F8dAScUfryZMRhvG4ZM%3D&lg2=UIHiLt3xD8xYTw%3D%3D;ockeqeudmj=iWgPvs4%3D;_cc_=VFC%2FuZ9ajQ%3D%3D;_nk_=drteamshmily;uc1=cookie14=UoWyj%2FUck4%2BlbQ%3D%3D&cookie21=UtASsssme%2BBq&cookie15=URm48syIIVrSKA%3D%3D;_l_g_=Ug%3D%3D;_w_al_f=1;tbcp=e=UU6hRGwe82L%2FDIKbll6T%2BA%3D%3D&f=UUjZelW6FeJrd1O28k2FVRBkohk%3D;cookie2=1c8aacbdd4d4fbafccf4e228b7efc40b;_w_app_lg=18;cookie1=VW9L%2B%2BnyONzfh3IPFjJ5YQlaVzS2cI5be932weHHMYg%3D;wud=wud;tracknick=drteamshmily;ntm=0;imewweoriw=3%2FsvJIEMbkYdvix6NcNsrMccWIylqHdtnvZM%2FQKUr%2Bc%3D;_w_tb_nick=drteamshmily;new_wud=wud36027;munb=898955534;skt=8921975354ec0377;sg=y44;v=0;t=4515e3259e859437971a551746810bc5;unb=898955534;lgc=drteamshmily;cookie17=W8CFoIm%2FIX4g;WAPFDFDTGFG=%2B4cMKKP%2B8PI%2BOF0Ei6nAIPuigURCfC8%3D;_tb_token_=dWE17gvFE7p;");
            fetcher.process(cp);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(cp.getSourceData().getSourceCode());
        }
    }
}
