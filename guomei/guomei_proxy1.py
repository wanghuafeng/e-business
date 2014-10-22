__author__ = 'huafeng'

import urllib2
import urllib
import os
import re
import time
import codecs
import simplejson
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

ip_port = '211.151.50.179:81'
http_handler = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
opener = urllib2.build_opener(http_handler)
urllib2.install_opener(opener)

def get_whole_root_page_url():
    url = 'http://www.gome.com.cn/category/cat21266797.html'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html, 'html5lib')
    div_level_str = soup.find('div', id='J_leftnav')
    a_level_list = div_level_str.find_all('a')
    root_page_url_list = [item['href'] for item in a_level_list]
    print root_page_url_list, len(root_page_url_list)
    return root_page_url_list
# get_whole_root_page_url()
def read_one_root_url_get_pagesize():
    url = 'http://www.gome.com.cn/category/cat10000062.html'
    url_pattern = 'http://www.gome.com.cn/p/json?module=async_search&paramJson={"pageNumber":%(page_num)s,"envReq":{"catId":"%(catId)s","regionId":"11011700","ip":"221.7.11.10:82","cookieId":"ec6dba97-0815-4c95-8889-0208c6b46d03","et":"","XSearch":false,"pageNumber":1,"pageSize":36,"more":0,"sale":0,"instock":1,"rewriteTag":false,"priceTag":0,"promoTag":0,"question":""}}'
    catId = re.search(r'cat\d+', url).group()
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('span', class_='num')
    page_size = re.search(r'/(\d+)', div_level_str.encode('utf-8')).group(1)
    for page_num in range(1, int(page_size)):
        page_url = url_pattern%{'page_num':page_num, 'catId':catId}
        print page_url
# read_one_root_url_get_pagesize()
whole_page_url_name = 'book_whole_page_url'
def get_whole_page_url():
    root_url_list = get_whole_root_page_url()
    page_url_filename = os.path.join(PATH, 'page_url', whole_page_url_name)
    url_pattern = 'http://www.gome.com.cn/p/json?module=async_search&paramJson={"pageNumber":%(page_num)s,"envReq":{"catId":"%(catId)s","regionId":"11011700","ip":"221.7.11.10:82","cookieId":"ec6dba97-0815-4c95-8889-0208c6b46d03","et":"","XSearch":false,"pageNumber":1,"pageSize":36,"more":0,"sale":0,"instock":1,"rewriteTag":false,"priceTag":0,"promoTag":0,"question":""}}'
    with codecs.open(page_url_filename, mode='wb', encoding='utf-8')as wf:
        count = 0
        for root_url in root_url_list:
            catId = re.search(r'cat\d+', root_url).group()
            page_url_list = []
            count += 1
            html = urllib2.urlopen(root_url).read()
            soup = BeautifulSoup(html)
            div_level_str = soup.find('span', class_='num')
            page_size = re.search(r'/(\d+)', div_level_str.encode('utf-8')).group(1)
            print count, page_size
            for page_num in range(1, int(page_size)+1):
                page_url = url_pattern%{'page_num':page_num, 'catId':catId}
                page_url_list.append(page_url+'\n')
            wf.writelines(page_url_list)
    with codecs.open(page_url_filename, encoding='utf-8') as f:
        whole_page_url_list = f.readlines()
        whole_page_url_set = set(whole_page_url_list)
        list_length, set_length = len(whole_page_url_list), len(whole_page_url_set)
        if list_length != set_length:
            print '%(topic_name)s:%(list_length)s==%(set_length)s'%{'topic_name':whole_page_url_name, 'list_length':list_length, 'set_length':set_length}
            with codecs.open(page_url_filename, mode='wb', encoding='utf-8') as wf:
                wf.writelines(whole_page_url_set)
        else:
            print list_length
get_whole_page_url()
def read_one_page_url_get_item_id():
    page_url = 'http://www.gome.com.cn/p/json?module=async_search&paramJson={"pageNumber":16,"envReq":{"catId":"cat10000062","regionId":"11011700","ip":"119.80.62.82","cookieId":"ec6dba97-0815-4c95-8889-0208c6b46d03","et":"","XSearch":false,"pageNumber":1,"pageSize":36,"more":0,"sale":0,"instock":1,"rewriteTag":false,"priceTag":0,"promoTag":0,"question":""}}'
    html = urllib2.urlopen(page_url).read()
    pid_list_set = set(re.findall(r'"pId" : "([\w]+)"', html))
    pid_list = list(pid_list_set)
    print list(pid_list), len(pid_list)
# read_one_page_url_get_item_id()

# whole_page_url_name = 'computer_whole_page_url'
item_id_name = 'digital_item_id'
def get_whole_item_id():
    whole_page_url_filename = os.path.join(PATH, 'page_url', whole_page_url_name)
    crawled_page_url_filename = os.path.join(PATH, 'log', 'crawled_page_url')
    failed_page_url_filename = os.path.join(PATH, 'log', 'failed_page_url')
    item_id_filename = os.path.join(PATH, 'item_id', item_id_name)
    count = 0
    with codecs.open(whole_page_url_filename, encoding='utf-8') as page_url_f, \
            codecs.open(crawled_page_url_filename, mode='wb', encoding='utf-8') as crawled_wf, \
            codecs.open(failed_page_url_filename, mode='wb', encoding='utf-8') as failed_wf, \
            codecs.open(item_id_filename, mode='wb', encoding='utf-8') as item_id_wf:
        for page_url in [item.strip() for item in page_url_f.readlines()]:
            try:
                html = urllib2.urlopen(page_url, timeout=20).read()
            except BaseException:
                try:
                    html = urllib2.urlopen(page_url, timeout=20).read()
                except BaseException:
                    try:
                        html = urllib2.urlopen(page_url, timeout=20).read()
                    except BaseException:
                        failed_wf.write('timed item_id in url;%s\n'%page_url)
                        continue
            count += 1
            pid_list_set = set(re.findall(r'"pId" : "([\w]+)"', html))
            if not pid_list_set:
                print 'pid is null in url;%s'%page_url
                failed_wf.write('pid is null in url;%s\n'%page_url)
                continue
            temp_for_write_list = [item+'\n' for item in pid_list_set]
            item_id_wf.writelines(temp_for_write_list)
            crawled_wf.write(page_url+'\n')
            print count, len(pid_list_set)

    with codecs.open(item_id_filename, encoding='utf-8') as f:
        item_id_list = f.readlines()
        item_id_set = set(item_id_list)
        list_length, set_length = len(item_id_list), len(item_id_set)
        if list_length != set_length:
            print '%(topic_name)s:%(list_length)s==%(set_length)s'%{'topic_name':item_id_name, 'list_length':list_length, 'set_length':set_length}
            with codecs.open(item_id_filename, mode='wb', encoding='utf-8') as wf:
                wf.writelines(item_id_set)
        else:
            print list_length
            # time.sleep(5)
# get_whole_item_id()
def read_failed_page_url():
    failed_page_url_filename = os.path.join(PATH, 'log', 'failed_page_url')
    item_id_filename = os.path.join(PATH, 'item_id', item_id_name)
    with codecs.open(failed_page_url_filename, encoding='utf-8') as f,\
    codecs.open(item_id_filename, mode='a', encoding='utf-8') as wf:
        count = 0
        for page_url in [item.split(';')[1].strip() for item in f.readlines()]:
            count += 1
            html = urllib2.urlopen(page_url, timeout=20).read()
            pid_list_set = set(re.findall(r'"pId" : "([\w]+)"', html))
            if not pid_list_set:
                print 'pid is null in url;%s'%page_url
                continue
            temp_for_write_list = [item+'\n' for item in pid_list_set]
            wf.writelines(temp_for_write_list)
            print count, len(pid_list_set)

    with codecs.open(item_id_filename, encoding='utf-8') as unique_f:
        print len(unique_f.readlines())
# read_failed_page_url()