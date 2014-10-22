__author__ = 'huafeng'
#coding:utf-8
import urllib2
import os
import re
import time
import codecs
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def Varify_proxy(ip_port):
    url = "http://42.96.192.46/echo_ip"
    http_proxy = 'http://%s'%ip_port
    proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)
    try:
        ip = urllib2.urlopen(url, timeout=10).read().strip()
        if ip == ip_port.split(':')[0]:
            print 'successed ip : %s'%ip
            return True
        else:
            print 'failed ip : %s'%ip
            return False
    except:
        print 'timeout request...ip:%s'%ip_port
        return False
# Varify_proxy('14.1.43.82:80')

def gen_proxy_ip():
    start_time = time.time()
    url = "http://www.youdaili.cn/Daili/http/2013.html"
    html = urllib2.urlopen(url).read().decode('utf-8')
    soup = BeautifulSoup(html)
    div_level = soup.find('div', 'cont_font')
    proxy_text = div_level.p.text
    print len(proxy_text)

    # html = '''1.179.147.2:8080@HTTP#泰国
    #         1.230.127.54:8080@HTTP#韩国
    #         5.101.130.95:80@HTTP#俄罗斯
    #         5.223.112.253:8080@HTTP#伊朗
    #         14.1.43.82:80@HTTP#新西兰
    #         14.18.17.166:80@HTTP#广东省广州市 上海网宿科技股份有限公司电信CDN节点
    #         14.18.242.147:8080@HTTP#广东省广州市 电信'''
    #
    # ip_port_list = re.findall('\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\:[\d]+', html)
    # # print ip_port_list, len(ip_port_list)
    #
    # for ip_port in ip_port_list:
    #     Varify_proxy(ip_port)
        # splited_ip_port = ip_port.split(':')
        # assert len(splited_ip_port) is 2
        # print splited_ip_port
        # proxy_dic['ip'] = splited_ip_port[0]
        # proxy_dic['prot'] = splited_ip_port[1]


    # filename = os.path.join(PATH, 'page_url', 'proxy_ip_port')
    # ip_port_list = [item+'\n' for item in ip_port_list]
    # with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
    #     wf.writelines(ip_port_list)
    end_time = time.time()
    print end_time - start_time
gen_proxy_ip()