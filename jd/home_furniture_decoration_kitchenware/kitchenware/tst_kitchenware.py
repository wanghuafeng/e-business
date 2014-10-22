__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
ROOT_URL = "http://channel.jd.com/kitchenware.html"
ITEM_FILENAME = 'kitchenware_item_id'

def gen_whole_topic_url():
    html = urllib2.urlopen(ROOT_URL).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='sortlist')
    h3_level_list =  div_level_str.find_all('h3')
    topic_url_list = [item.a['href'] for item in h3_level_list]
    # print topic_url_list, len(topic_url_list)
    return topic_url_list
# gen_whole_topic_url()
def read_one_topic_url_get_root_page_url():
    url = "http://channel.jd.com/6196-6197.html"
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='sortlist')
    # print div_level_str
    li_level_list = div_level_str.find_all('li')
    root_page_url_list = [item.a['href'] for item in li_level_list]
    print root_page_url_list
# read_one_topic_url_get_root_page_url()
page_size_div_pattern = re.compile(r'''<div class='pagin pagin-m'>.*?/(?P<page_size>\d+)</span>.*?href="(?P<end_url_pattern>[\d\-]+.html)"''', re.S)
jd_list_root_url = 'http://list.jd.com/'
def read_one_page_url_to_get_pagesize():
    url = "http://list.jd.com/6196-6197-6200.html"
    html = urllib2.urlopen(url).read()
    # page_size_div_pattern = re.compile(r'''<div class='pagin pagin-m'>.*?/(?P<page_size>\d+)</span>.*?href="(?P<href>[\d\-]+.html)"''', re.S)
    match_str = page_size_div_pattern.search(html)
    page_size = match_str.group('page_size')
    end_url = match_str.group('end_url_pattern')
    print page_size, end_url
    splited_url = end_url.split('-')
    splited_url[12] = '%s'
    end_url_pattern = '-'.join(splited_url)
    for page_num in range(1, int(page_size)+1):
        url = ''.join((jd_list_root_url,end_url_pattern%str(page_num)))
        print url
    # page_size_str = page_size_div_pattern.search(html).group(1) if page_size_div_pattern.search(html) else ''
    # if not page_size_str:
    #     print "no html match page_size here in url:%s"%url
    #     return
    # print page_size_str
    # for page_count in range(1, int(page_size_str)+1):
    #     page_url = "&".join((url,'page=%s'%page_count))
    #     print page_url
# read_one_page_url_to_get_pagesize()
def parse_page_url_pattern_from_html():
    html = '''<div class='pagin pagin-m'><span class='text'><i>1</i>/44</span>
            <span class="prev-disabled">上一页<b></b></span>
            <a class="next" href="6196-6197-6200-0-0-0-0-0-0-0-1-1-2-1-1-72-4137-33.html">下一页<b></b></a>
            </div>'''
    # pattern = re.compile(r'''<div class='pagin pagin-m'>.*?/(\d+)</span>.*?href="[\d\-]+.html"''', re.S)
    # print re.search(r'''<div class='pagin pagin-m'>.*?/(\d+)</span>.*?href="[\d\-]+.html"''', html, re.S).group()
    url_pattern = '6196-6197-6200-0-0-0-0-0-0-0-1-1-2-1-1-72-4137-33.html'
    splited_url = url_pattern.split('-')
    splited_url[12] = '%s'
    print '-'.join(splited_url)
# parse_page_url_pattern_from_html()
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
        # page_size_div_pattern = re.compile(r'''<div class=\\"pagin pagin-m\\">.*/(\d+)</span>''', re.S)
        # page_size_str = page_size_div_pattern.search(html).group(1) if page_size_div_pattern.search(html) else ''

        match_str = page_size_div_pattern.search(html)
        page_size = match_str.group('page_size')
        try:
            end_url = match_str.group('end_url_pattern')
        except BaseException:
            print 'no multi page in such url:%s'%root_page_url

        print url_count,page_size

        if page_size and end_url:
            splited_url = end_url.split('-')
            splited_url[12] = '%s'
            end_url_pattern = '-'.join(splited_url)
            for page_num in range(1, int(page_size)+1):
                url = ''.join((jd_list_root_url,end_url_pattern%str(page_num)))
                whole_page_url_list.append(url)
        else:
            whole_page_url_list.append(root_page_url)

        write_whole_page_url_into_file(whole_page_url_list)
# gen_whole_page_url()
def read_one_page_url_to_get_item_id():
    url = "http://list.jd.com/6196-11143-11156-0-0-0-0-0-0-0-1-1-30-1-1-72-4137-33.html"
    html = urllib2.urlopen(url).read()
    item_id_list = re.findall(r"sku='(\d+)'><div class=", html)
    print item_id_list, len(item_id_list)
    # item_id_list = [re.search("[\d]+", item).group() for item in item_id_str_list]
    # print item_id_list, len(set(item_id_list))
    # temp_list_for_write = [item+'\n' for item in item_id_list]
# read_one_page_url_to_get_item_id()

def get_whole_item_id():
    whole_page_url_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    crawled_page_url_filename = os.path.join(PATH, 'log', 'crawled_page_url')
    failed_page_url_filename = os.path.join(PATH, 'log', 'failed_page_url')
    item_id_filename = os.path.join(PATH, 'sys', ITEM_FILENAME)
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
            item_id_list = re.findall(r"sku='(\d+)'><div class=", html)
            print item_id_list
            if not item_id_list:
                failed_page_url_wf.write('not match id_str in url;%s'%page_url)
            temp_list_for_write = [item+'\n' for item in item_id_list]
            item_id_wf.writelines(temp_list_for_write)
            crawled_page_wf.write(page_url)
get_whole_item_id()