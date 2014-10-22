__author__ = 'huafeng'
#coding:utf-8
import urllib2
import os
import re
import codecs
import time
import random
from bs4 import BeautifulSoup
SLEEP_INTERVAL = random.randint(5,10)

PATH = os.path.dirname(os.path.abspath(__file__))

def request_in_douban(ip_port):
    '''过滤豆瓣被封的IP'''
    url = 'http://movie.douban.com/subject/6786002/'
    http_proxy = 'http://%s'%ip_port
    proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)
    try:
        html = urllib2.urlopen(url, timeout=10)
        soup = BeautifulSoup(html)

        title = soup.find('span', property='v:itemreviewed')

        if not title:
            print 'invalid ip_port:%s'%ip_port
            return
        else:
            print 'success ip_port:%s'%ip_port
            return True
    except:
        print 'timed item_id...in ip_port:%s'%ip_port
        return

def write_proxy_into_file(http_proxy_list):
    #过滤已被豆瓣查封的IP
    com_str_list = [item+'\n' for item in http_proxy_list if request_in_douban(item)]
    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
        wf.writelines(com_str_list)

def gen_proxy():
    url_pattern = "http://www.xici.net.co/nn/%s"
    url_list = [url_pattern%str(i) for i in range(1,6)]
    http_proxy_list = []
    for url in url_list:
        try:
            html = urllib2.urlopen(url, timeout=15).read()
        except:
            time.sleep(60)
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

    write_proxy_into_file(http_proxy_list)
    # time.sleep(SLEEP_INTERVAL)

gen_proxy()

