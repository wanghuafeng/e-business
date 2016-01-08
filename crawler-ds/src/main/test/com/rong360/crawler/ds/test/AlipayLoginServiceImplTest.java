package com.rong360.crawler.ds.test;

import com.rong360.crawler.ds.service.impl.AlipayLoginServiceImpl;
import com.rong360.crawler.proxy.ip.ProxyIp;
import junit.framework.TestCase;

/**
 * Created by Administrator on 2015/10/27.
 */
public class AlipayLoginServiceImplTest extends TestCase {

    public void testLogin() throws Exception {
        AlipayLoginServiceImpl service = new AlipayLoginServiceImpl();
        service.login("yryetgfhth=%2B4cXpeDzLTAIW6kFUQRecFhmpZ1SemTToLYAFo0PptqYBAFw4GwnFfJark7a9AzHtR2tOi0z79Bhv3%2FGrJcvMBx5eBioyNzNxQxFCZnR4OolEuhsax%2B3b2O6zHC29kvuKEkBEO8wgnPUGiqeIDIiNz0HOi7eZrm79XfjyKDMnR8OvYujqrQ%2BMqqRML5VOAUL0%2FqWCCljXr%2B6LqWUGcfjwXwckNphhfMjLWJqdh2Eu8UC8Yny8Ti2T337x%2FA%3D;uc3=nk2=B1BaVwQkJRwqnpLA&id2=W8CFoIm%2FIX4g&vt3=F8dAScUcmB9q%2F67v58Q%3D&lg2=U%2BGCWk%2F75gdr5Q%3D%3D;ockeqeudmj=hmQuR4I%3D;_cc_=W5iHLLyFfA%3D%3D;_nk_=drteamshmily;uc1=cookie14=UoWyjCc9WQlh9Q%3D%3D&cookie21=U%2BGCWk%2F7pY%2FF&cookie15=UtASsssmOIJ0bQ%3D%3D;_l_g_=Ug%3D%3D;_w_al_f=1;tbcp=e=UU6hRGwe82L%2FDIKbll6T%2BA%3D%3D&f=UUjZelW6FeJrd1O28k2FVRBkohk%3D;cookie2=1c337949d81d1e6d01a168341f5ca279;_w_app_lg=18;cookie1=VW9L%2B%2BnyONzfh3IPFjJ5YQlaVzS2cI5be932weHHMYg%3D;wud=wud;tracknick=drteamshmily;ntm=1;imewweoriw=3%2FsktfM4o5aP%2BF626C9bUGrfqG5uH2zcBHn0jW2eVWI%3D;_w_tb_nick=drteamshmily;new_wud=wud69589;munb=898955534;skt=7b4f5b51f853b2c3;sg=y44;v=0;t=c15209346c51c23d1907074b57a32858;unb=898955534;lgc=drteamshmily;cookie17=W8CFoIm%2FIX4g;WAPFDFDTGFG=%2B4cMKKP%2B8PI%2BOF0Ei6nAIPuigURCfC8%3D;_tb_token_=I0ieZeCy7p;", new ProxyIp());
    }
}