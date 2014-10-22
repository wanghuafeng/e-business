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

def baidu_varify(ip_port):
    '''对百度进行过滤'''
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
def read_one_page():
    url = 'http://www.youdaili.cn/Daili/guonei/list_1.html'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    ul_level_str = soup.find('ul', class_='newslist_line')
    li_level_srt = ul_level_str.find('li')
    print li_level_srt.a['href']

read_one_page()