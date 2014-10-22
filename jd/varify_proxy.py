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

def varify(ip_port):
    url = 'http://item.jd.com/1013330.html'
    proxy_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)
    try:
        start_time = time.time()
        html = urllib2.urlopen(url, timeout=10).read()
        soup = BeautifulSoup(html)
        content = soup.find('div', id='product-detail-1')
        end_time = time.time()
        time_consume = end_time - start_time
        if not content:
            print 'invalid ip_port:%s'%ip_port
            return
        elif content and time_consume < 1:
            print 'success ip_port:%s'%ip_port
            print end_time - start_time
            return True
    except:
        print 'timed item_id...in ip_port:%s'%ip_port
        return
# varify('')

def write_proxy_into_file(http_proxy_list):
    com_str_list = [item+'\n' for item in http_proxy_list if varify(item)]
    filename = os.path.join(PATH, 'xici_proxy')
    with codecs.open(filename, mode='a', encoding='utf-8') as wf:
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
        matched_ip_port_list = [item for item in ip_port_type_list if item[-1] != 'HTTPS']
        com_str_list = [":".join((item[0], item[1])) for item in matched_ip_port_list]
        http_proxy_list.extend(com_str_list)
    write_proxy_into_file(http_proxy_list)
if __name__ == "__main__":
    gen_proxy()

