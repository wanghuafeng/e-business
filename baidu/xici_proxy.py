__author__ = 'huafeng'
#coding:utf-8
import urllib2
import os
import re
import time
import codecs
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def request_in_baidu(ip_port):
    start_time = time.time()
    url = 'http://baike.baidu.com/view/3570272.htm'
    http_proxy = 'http://%s'%ip_port
    proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)

    try:
        html = urllib2.urlopen(url, timeout=10)
        soup = BeautifulSoup(html)
        para_level_list = soup.find_all('div', class_='para')
        end_time = time.time()
        interval = end_time - start_time
        if para_level_list and interval < 5:
            print 'success ip_port:%s'%ip_port
            return True
    except:
        # print 'timed item_id...in ip_port:%s'%ip_port
        return

def gen_proxy():
    url_pattern = "http://www.xici.net.co/nn/%s"
    url_list = [url_pattern%str(i) for i in range(1,6)]
    http_proxy_list = []
    for url in url_list:
        try:
            html = urllib2.urlopen(url, timeout=15).read()
        except:
            time.sleep(120)
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                continue
        soup = BeautifulSoup(html)
        tr_level_list = soup.find_all('tr')
        td_level_list = [item.find_all('td') for item in tr_level_list]
        ip_port_type_list = [(param[1].text, param[2].text, param[5].text) for param in td_level_list if len(param)>5]
        # print ip_port_type_list, len(ip_port_type_list)
        matched_ip_port_list = [item for item in ip_port_type_list if item[-1] != 'HTTPS']
        # print matched_ip_port_list, len(matched_ip_port_list)
        com_str_list = [":".join((item[0], item[1])) for item in matched_ip_port_list]
        http_proxy_list.extend(com_str_list)

    #过滤已被百度查封的IP
    com_str_list = [item for item in http_proxy_list if request_in_baidu(item)]
    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
        com_str_list = [item+'\n' for item in com_str_list]
        wf.writelines(com_str_list)
gen_proxy()



