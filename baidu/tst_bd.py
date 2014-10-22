__author__ = 'huafeng'
#encoding:utf-8

import os
import re
import time
import gevent
import codecs
import random
import urllib2
import simplejson
import gevent.monkey
from bs4 import BeautifulSoup
from math import ceil
# gevent.monkey.patch_all()

PATH = os.path.dirname(__file__)

def parse_html(url,filecount,proxy):
    content_json_dict = {}
    http_proxy = 'http://%s'%proxy
    proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)
    try:
        html = urllib2.urlopen(url).read()
    except:
        print 'request timed item_id ...in url:%s'%url
        return
    soup = BeautifulSoup(html)
    header_str = soup.find('div', class_='lemmaTitleH1')
    if not header_str:
        print 'url not match pattern:%s'%url
        return
    content_json_dict['header'] = header_str.text
    content_json_dict['url'] = url
    para_level_list = soup.find_all('div', class_='para')
    para_str_list =[para.text for para in  para_level_list]
    content_str = "".join(para_str_list)
    content_json_dict['content'] = content_str
    json_obj = simplejson.dumps(content_json_dict)
    filename = os.path.join(PATH, 'out', filecount)
    with codecs.open('%s'%filename, mode='wb', encoding='utf-8')as f:
        f.write(json_obj)
# parse_html()

def read_with_proxy(section_count):
    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with open(filename) as f:
        proxy_ip_list = f.readlines()
    proxy_ip_list = [item.strip() for item in proxy_ip_list]
    proxy_count = len(proxy_ip_list)
    start = time.time()
    threads = []
    url_pattern = "http://baike.baidu.com/view/%s.htm"

    thread_count = 200
    threads_per_proxy = int(ceil(thread_count/float(proxy_count)))

    for i in range(1+thread_count*section_count, thread_count*(section_count+1)+1):
        url = url_pattern%str(i)
        proxy_point = (i-thread_count*section_count)/threads_per_proxy
        ip_port = proxy_ip_list[proxy_point]
        threads.append(gevent.spawn(parse_html, url, str(i), ip_port))
    gevent.joinall(threads,timeout=90)
    end = time.time()
    print "elapsed time : %d" %(end-start)

# read_with_proxy()
def baike_content():
    n = 100000000/200
    for i in range(n):
        read_with_proxy(i)
    time.sleep(random.randint(5,8))
# baike_content()
def parse_page_url():
    url = "http://tieba.baidu.com/f?kw=%B5%F6%D3%E3"
    html = urllib2.urlopen(url).read().decode('gbk')
    soup = BeautifulSoup(html)
    div_level_str = soup.find('ul', id='thread_list')
    # print div_level_str
    li_level_list = div_level_str.find_all('li', class_='j_thread_list clearfix')
    # print li_level_list, len(li_level_list)
    print li_level_list[0].find('a', class_='j_th_tit')
parse_page_url()