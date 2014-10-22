__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def gen_whole_topic_url():
    url = "http://channel.jd.com/furniture.html"
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='sortlist')
    h3_level_list =  div_level_str.find_all('h3')
    topic_url_list = [item.a['href'] for item in h3_level_list]
    # print topic_url_list, len(topic_url_list)
    return topic_url_list
# gen_whole_topic_url()
def read_one_topic_url_get_root_page_url():
    url = "http://list.jd.com/list.html?cat=9847,9848,9863"
    html = urllib2.urlopen(url).read()
    print html
    soup = BeautifulSoup(html)
    # div_level_str = soup.find('div', id='sortlist')
    div_level_str = soup.find('div', class_='current')
    print div_level_str
    li_level_list = div_level_str.find_all('li')
    root_page_url_list = [item.a['href'] for item in li_level_list]
    print root_page_url_list
# read_one_topic_url_get_root_page_url()
def read_one_page_url_to_get_pagesize():
    url = "http://list.jd.com/9847-9854-9896.html"
    html = urllib2.urlopen(url).read()
    page_size_div_pattern = re.compile(r'''<div class=\\"pagin pagin-m\\">.*/(\d+)</span>''', re.S)
    page_size_str = page_size_div_pattern.search(html).group(1) if page_size_div_pattern.search(html) else ''
    if not page_size_str:
        print "no html match page_size here in url:%s"%url
        return
    for page_count in range(1, int(page_size_str)+1):
        page_url = "&".join((url,'page=%s'%page_count))
        print page_url
# read_one_page_url_to_get_pagesize()
def gen_whole_root_page_url():
    topic_url_list = gen_whole_topic_url()
    whole_root_page_url_list = []
    for topic_url in topic_url_list:
        html = urllib2.urlopen(topic_url).read()
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', id='sortlist')
        li_level_list = div_level_str.find_all('li')
        root_page_url_list = [item.a['href'] for item in li_level_list]
        whole_root_page_url_list.extend(root_page_url_list)
        # print root_page_url_list
    print len(whole_root_page_url_list)#, whole_root_page_url_list
    return whole_root_page_url_list
# gen_whole_root_page_url()
def write_whole_page_url_into_file(page_url_list):
    filename = os.path.join(PATH, 'sys', 'whole_page_url')
    with codecs.open(filename, mode='a', encoding='utf-8') as af:
        temp_list_to_write = [item+'\n' for item in page_url_list]
        af.writelines(temp_list_to_write)
def gen_whole_page_url():
    whole_root_page_url = gen_whole_root_page_url()
    url_count = 0
    for root_page_url in whole_root_page_url:
        url_count += 1
        whole_page_url_list = []
        html = urllib2.urlopen(root_page_url).read()
        page_size_div_pattern = re.compile(r'''<div class=\\"pagin pagin-m\\">.*/(\d+)</span>''', re.S)
        page_size_str = page_size_div_pattern.search(html).group(1) if page_size_div_pattern.search(html) else ''
        print url_count, page_size_str
        if not page_size_str:
            print "no html match page_size here in url:%s"%root_page_url
            continue
        for page_count in range(1, int(page_size_str)+1):
            page_url = "&".join((root_page_url,'page=%s'%page_count))
            whole_page_url_list.append(page_url)
        write_whole_page_url_into_file(whole_page_url_list)
        # time.sleep(3)
# gen_whole_page_url()
def read_one_page_url_to_get_item_id():
    url = "http://list.jd.com/list.html?cat=1620%2C1621%2C1626&page=2"
    html = urllib2.urlopen(url).read()
    # print html
    item_id_str_list = re.findall(r'''sku=\\"\d+\\" selfservice''', html)
    item_id_list = [re.search("[\d]+", item).group() for item in item_id_str_list]
    print item_id_list, len(set(item_id_list))
    temp_list_for_write = [item+'\n' for item in item_id_list]
# read_one_page_url_to_get_item_id()

def get_whole_item_id():
    item_id_for = 'furniture_item_id'
    ip_port = '122.96.59.102:83'
    http_handler = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    opener = urllib2.build_opener(http_handler)
    urllib2.install_opener(opener)
    whole_page_url_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    crawled_page_url_filename = os.path.join(PATH, 'log', 'crawled_page_url')
    failed_page_url_filename = os.path.join(PATH, 'log', 'failed_page_url')
    item_id_filename = os.path.join(PATH, 'sys', item_id_for)
    with codecs.open(whole_page_url_filename, encoding='utf-8') as whole_page_url_f,\
    codecs.open(crawled_page_url_filename, mode='wb', encoding='utf-8') as crawled_page_wf, \
    codecs.open(failed_page_url_filename, mode='wb', encoding='utf-8') as failed_page_url_wf, \
    codecs.open(item_id_filename, mode='wb', encoding='utf-8') as item_id_wf:
        for page_url in whole_page_url_f.readlines():
            try:
                html = urllib2.urlopen(page_url, timeout=15).read()
            except:
                try:
                    html = urllib2.urlopen(page_url, timeout=15).read()
                except:
                    failed_page_url_wf.write('timeout in url;%s'%page_url)
                    continue
            item_id_str_list = re.findall(r'''sku=\\"\d+\\" selfservice''', html)
            if not item_id_str_list:
                failed_page_url_wf.write('not match id_str in url;%s'%page_url)
            try:
                item_id_list = [re.search("[\d]+", item).group() for item in item_id_str_list]
            except:
                failed_page_url_wf.write('not match id in url;%s')
            temp_list_for_write = [item+'\n' for item in item_id_list]
            item_id_wf.writelines(temp_list_for_write)
            crawled_page_wf.write(page_url)
            # print temp_list_for_write
            # time.sleep(5)
get_whole_item_id()