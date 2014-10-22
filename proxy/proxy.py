__author__ = 'huafeng'
#coding:utf-8
import urllib2
import urllib
import os
import re
import socket
import time


def VerifyIp():
    proxy = {'domain':"115.29.3.163", 'port':"80"} # invalid
    url = 'http://42.96.192.46/echo_ip'
    loc_ip = urllib.urlopen(url).read().strip()
    print "loc_ip is :%s"%loc_ip

    start_time = time.time()
    http_proxy = 'http://%s:%s/' % (proxy['domain'], proxy['port'])
    proxy_support = urllib2.ProxyHandler({'http': http_proxy})
    opener = urllib2.build_opener(proxy_support)
    urllib2.install_opener(opener)
    request = urllib2.Request(url, headers={'User-Agent': 'Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31'})
    ip = urllib2.urlopen(url).read().strip()
    if re.match(r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}', ip) and ip != loc_ip:
        print "yep, valid proxy"
    else:
        print  "sorry, invalid proxy"
    end_time = time.time()
    print end_time-start_time
VerifyIp()

# url = 'http://42.96.192.46/echo_ip'
# loc_ip = urllib.urlopen(url).read().strip()
# print loc_ip

def _19lou_proxy_request():
    path = "/home/huafeng/PycharmProjects/spider/19lou/page_url"
    filename = os.path.join(path, 'whole_page_urls')

    proxy = {'domain':"5.45.67.109", 'port':"3128"}
    http_proxy = 'http://%s:%s/' % (proxy['domain'], proxy['port'])
    proxy_support = urllib2.ProxyHandler({'http':http_proxy})
    opener = urllib2.build_opener(proxy_support)
    urllib2.install_opener(opener)

    with open(filename) as f:
        # for url in f.readlines():
        for url in  [item.strip() for item in f.readlines()]:
            req = urllib2.Request(url)#, headers={'User-Agent': 'Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31'})
            try:
                html = urllib2.urlopen(req, timeout=5).read().decode('gbk')
                print html
                # time.sleep(5)
            except Exception, e:
                continue
            print url

# _19lou_proxy_request()