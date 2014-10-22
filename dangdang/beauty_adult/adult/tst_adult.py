__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
from bs4 import BeautifulSoup
PATH = os.path.dirname(os.path.abspath(__file__))

def get_root_page_url():
    url = 'http://category.dangdang.com/cid4005284.html'
    html = urllib2.urlopen(url).read()
    root_url_list = re.findall(r'<span>\s*<a href="(/cid\d+\.html)#', html)
    print root_url_list, len(root_url_list)#48
    return root_url_list
# get_root_page_url()

# root_page_url_list = ['/cid4008149.html', '/cid4008150.html', '/cid4008151.html', '/cid4008152.html', '/cid4008154.html', '/cid4008153.html', '/cid4008148.html', '/cid4006411.html', '/cid4008156.html', '/cid4008146.html', '/cid4010196.html', '/cid4008124.html', '/cid4008122.html', '/cid4008120.html', '/cid4008121.html', '/cid4008125.html', '/cid4008123.html', '/cid4008130.html', '/cid4008128.html', '/cid4010053.html', '/cid4010052.html', '/cid4002768.html', '/cid4010051.html', '/cid4008126.html', '/cid4009794.html', '/cid4008157.html', '/cid4002838.html', '/cid4008158.html', '/cid4008159.html', '/cid4008160.html', '/cid4002831.html', '/cid4003970.html', '/cid4002841.html', '/cid4002845.html', '/cid4006391.html', '/cid4008161.html', '/cid4002773.html', '/cid4002770.html', '/cid4003852.html', '/cid4006425.html', '/cid4010055.html', '/cid4002771.html', '/cid4002772.html', '/cid4002774.html']
root_url_pattern = 'http://category.dangdang.com%s'

def read_one_page_url_to_get_pagesize():
    url = 'http://category.dangdang.com/cid4002945.html'
    html = urllib2.urlopen(url).read()
    # print html
    page_size = re.search(r'<span class=.*?/(\d+)</span>', html).group(1)
    print page_size
# read_one_page_url_to_get_pagesize()
def write_whole_page_url_into_file(whole_page_url_list):
    whole_page_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    with codecs.open(whole_page_filename, mode='a', encoding='utf-8') as wf:
        temp_url_list = [item+'\n' for item in whole_page_url_list]
        wf.writelines(temp_url_list)
def gen_whole_page_url():
    root_page_url_list = get_root_page_url()
    count = 0
    for root_page_url in root_page_url_list:
        count += 1
        url = root_url_pattern%root_page_url
        html = urllib2.urlopen(url).read()
        page_size = re.search(r'<span class=.*?/(\d+)</span>', html).group(1)
        print count, page_size
        page_url_pattern = url.replace(r'.html', r'-pg%s.html')
        page_url_list = []
        for page_num in range(1, int(page_size)+1):
            page_url = page_url_pattern%page_num
            page_url_list.append(page_url)
        write_whole_page_url_into_file(page_url_list)
# gen_whole_page_url()

def read_one_page_url_get_item_id():
    ip_port = '58.20.127.106:3128'
    http_handler = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    opener = urllib2.build_opener(http_handler)
    urllib2.install_opener(opener)
    url = 'http://category.dangdang.com/cid4006001-pg1.html'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html, 'html5lib')
    div_level_str = soup.find('div', class_='shoplist')
    item_url_list = re.findall(r'<a href="(http://product.dangdang.com/\d+\.html)', div_level_str.encode('utf-8'))
    print item_url_list, len(item_url_list)
# read_one_page_url_get_item_id()

def get_item_id():
    spider_name = 'adult'
    ip_port = '58.20.127.106:3128'
    http_handler = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    opener = urllib2.build_opener(http_handler)
    urllib2.install_opener(opener)
    whole_page_url = os.path.join(PATH, 'sys', 'whole_page_url')
    crawled_page_url = os.path.join(PATH, 'log', 'crawled_page_url')
    failed_page_url = os.path.join(PATH, 'log', 'failed_page_url')
    item_id_url = os.path.join(PATH, 'sys', '%s_item_id'%spider_name)
    with codecs.open(whole_page_url, encoding='utf-8') as whole_page_url_f,\
    codecs.open(crawled_page_url, mode='wb', encoding='utf-8') as crawled_page_url_wf,\
    codecs.open(failed_page_url, mode='wb', encoding='utf-8') as failed_page_url_wf,\
    codecs.open(item_id_url, mode='wb', encoding='utf-8') as item_id_wf:
        for page_url in whole_page_url_f.readlines():
            try:
                html = urllib2.urlopen(page_url).read()
            except BaseException:
                try:
                    html = urllib2.urlopen(page_url).read()
                except:
                    try:
                        html = urllib2.urlopen(page_url).read()
                    except BaseException:
                        failed_page_url_wf.write('timed item_id in url;%s'%page_url)
                        continue
            soup = BeautifulSoup(html, 'html5lib')
            div_level_str = soup.find('div', class_='shoplist')
            try:
                item_url_list = re.findall(r'<a href="(http://product.dangdang.com/\d+\.html)', div_level_str.encode('utf-8'))
            except BaseException:
                failed_page_url_wf.write('not match pattern in url;%s'%page_url)
                continue
            temp_list_for_write = [item+'\n' for item in item_url_list]
            item_id_wf.writelines(temp_list_for_write)
            crawled_page_url_wf.write(page_url)
get_item_id()
