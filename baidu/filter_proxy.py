__author__ = 'huafeng'
#coding:utf-8
import urllib2
import os
import re
import time
import codecs
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
VARIFY_URL = "http://42.96.192.46/echo_ip"

def Varify_proxy(ip_port):
    http_proxy = 'http://%s'%ip_port
    proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)
    try:
        ip = urllib2.urlopen(VARIFY_URL, timeout=10).read().strip()
        if ip == ip_port.split(':')[0]:
            print 'successed ip : %s'%ip
            return True
        else:
            print 'failed ip : %s'%ip
            return
    except:
        print 'timeout request...ip:%s'%ip_port
        return
# Varify_proxy('14.1.43.82:80')

def request_in_baidu(ip_port):
    url = 'http://baike.baidu.com/view/3570272.htm'
    http_proxy = 'http://%s'%ip_port
    proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)
    try:
        html = urllib2.urlopen(url, timeout=10)
        soup = BeautifulSoup(html)
        para_level_list = soup.find_all('div', class_='para')
        if not para_level_list:
            return
        else:
            return True
    except:
        print 'timed item_id...in ip_port:%s'%ip_port
        return
# print request_in_baidu('111.47.92.226:8080')
def gen_proxy_ip():

    # url = "http://www.youdaili.cn/Daili/guonei/2039.html"
    url_list = ["http://www.youdaili.cn/Daili/guonei/2039.html", "http://www.youdaili.cn/Daili/guonei/2039_2.html"
    "http://www.youdaili.cn/Daili/guonei/2039_3.html"]
    for url in url_list:
        try:
            html = urllib2.urlopen(url, timeout=10).read().decode('utf-8')
            print html
            soup = BeautifulSoup(html)
        except :
            print 'request timed item_id...'
            continue

    #     div_level = soup.find('div', 'cont_font')
    #     proxy_text = div_level.p.text
    #     # html = '''1.179.147.2:8080@HTTP#泰国
    #     #         14.18.242.147:8080@HTTP#广东省广州市 电信'''
    #
    #     ip_port_list = re.findall('\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\:[\d]+', proxy_text)
    #
    #     for ip_port in ip_port_list:
    #         if Varify_proxy(ip_port):
    #             if request_in_baidu(ip_port):
    #                 proxy_list.append(ip_port)
    #             else:
    #                 continue
    #         else:
    #             continue
    #
    # filename = os.path.join(PATH, 'page_url', 'proxy_ip_port')
    # proxy_list = [item+'\n' for item in proxy_list]
    # with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
    #     wf.writelines(proxy_list)

# gen_proxy_ip()
def xici_proxy_check():
    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with open(filename) as f:
        for port_ip in [item.strip() for  item in f.readlines()]:
            if request_in_baidu(port_ip):
                print 'success :%s'%port_ip
            else:
                print 'invalid ip_port:%s'%port_ip
xici_proxy_check()