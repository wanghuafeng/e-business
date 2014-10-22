__author__ = 'huafeng'
#coding:utf-8
import re
import os
import time
import urllib2
import codecs
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def gen_root_page_url():
    '''解析出topic_url'''
    url = "http://list.jd.com/list.html?cat=670,671,672"
    html = urllib2.urlopen(url).read()
    li_level_list = re.findall(r'\<LI\>.*?\<\/LI>', html)
    href_filter_pattern = re.compile(r"href='(http://.*)'")
    topic_url_list = [href_filter_pattern.search(item).group(1) for item in li_level_list]
    # print topic_url_list, len(topic_url_list)
    # print len(topic_url_list)
    return topic_url_list
# gen_root_page_url()
# html = '''<LI><a href='http://list.jd.com/list.html?cat=670,671,672'>\xe7\xac\x94\xe8\xae\xb0\xe6\x9c\xac</a></LI>"'''
# print re.search(r"href='(http://.*)'", html).group(1)

def write_whole_page_url(page_url_list):
    filename = os.path.join(PATH, 'sys', 'whole_page_url')
    with codecs.open(filename, mode="a", encoding='utf-8') as wf:
        temp_list = [item+'\n' for item in page_url_list]
        wf.writelines(temp_list)
def gen_whole_page_url():
    topic_url_list = gen_root_page_url()
    print len(topic_url_list)
    topic_count = 0
    for url in topic_url_list:
        topic_count += 1
        html = urllib2.urlopen(url).read()
        page_size_div_pattern = re.compile(r'''<div class=\\"pagin pagin-m\\">.*/(\d+)</span>''', re.S)
        page_size_str = page_size_div_pattern.search(html).group(1) if page_size_div_pattern.search(html) else ''
        if not page_size_str:
            print 'regular not match in url:%s'%url
            continue
        print page_size_str, topic_count
        page_count_list = []
        for page_count in range(1,int(page_size_str)+1):
            page_count_list.append(page_count)
        page_url_list = ['&'.join((url, 'page=%s'))%item for item in page_count_list]
        # time.sleep(3)
        write_whole_page_url(page_url_list)
# gen_whole_page_url()
def read_one_root_page_url():
    url = "http://list.jd.com/list.html?cat=670,671,672"
    html = urllib2.urlopen(url).read()
    page_size_div_pattern = re.compile(r'''<div class=\\"pagin pagin-m\\">.*/(\d+)</span>''', re.S)
    page_size_str = page_size_div_pattern.search(html).group(1) if page_size_div_pattern.search(html) else ''
    if not page_size_str:
        return
    print page_size_str
    page_count_list = []
    for page_count in range(1,int(page_size_str)+1):
        page_count_list.append(page_count)
    page_url_list = ['&'.join((url, 'page=%s'))%item for item in page_count_list]
    print page_url_list
# read_one_root_page_url()
def regular_expression():
    html = r'''<div class=\"pagin pagin-m\"><span class=\"text\"><i>1</i>/25</span>
            <a href=\"?cat=670%2C671%2C672&amp;
            page=1\" class=\"prev\">上一页<b></b></a><a href=\"?
            cat=670%2C671%2C672&amp;page=2\" class=\"next\">下一页<b></b></a></div>'''
    match = re.search(r'''<div class=\\"pagin pagin-m\\">.*/(\d+)</span>''', html)
    if match:
        print match.group(1)
    else:
        print match
def read_one_page_get_item_url_list():
    url = "http://list.jd.com/list.html?cat=670%2C671%2C672&page=2"
    html = urllib2.urlopen(url).read()
    item_id_str_list = re.findall(r'''sku=\\"\d+\\" selfservice''', html)
    item_id_list = [re.search("[\d]+", item).group() for item in item_id_str_list]
    print item_id_list, len(set(item_id_list))
    temp_list_for_write = [item+'\n' for item in item_id_list]
    confirm_write_content_into_file = False
    if confirm_write_content_into_file:
        filename = os.path.join(PATH, 'sys', 'phone_item_id')
        with codecs.open(filename, mode='a', encoding='utf-8') as f:
            f.writelines(temp_list_for_write)
# read_one_page_get_item_url_list()

def read_whole_page_url_to_get_item_id():
    whole_page_url_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    item_id_filename = os.path.join(PATH, 'sys', 'computer_item_id')
    crawled_page_url_filename = os.path.join(PATH, 'log', 'crawled_page_url')
    timeout_page_url_filename = os.path.join(PATH, 'log', 'timeout_page_url')
    with codecs.open(whole_page_url_filename,encoding='utf-8') as whole_url_f, \
    codecs.open(item_id_filename, mode='wb', encoding='utf-8') as item_wf, \
    codecs.open(crawled_page_url_filename, mode='wb', encoding='utf-8') as crawled_url_wf, \
    codecs.open(timeout_page_url_filename, mode='wb', encoding='utf-8') as timeout_url_wf:
        for page_url in whole_url_f.readlines():
            try:
                html = urllib2.urlopen(page_url, timeout=10).read()
            except:
                timeout_url_wf.write('request timeout in url;%s'%page_url)
                continue
            item_id_str_list = re.findall(r'''sku=\\"\d+\\" selfservice''', html)
            if not item_id_str_list:
                timeout_url_wf.write('no sku;%s'%page_url)
                continue
            item_id_list = [re.search("[\d]+", item).group() for item in item_id_str_list][3:]
            item_id_into_file_list = [item+'\n' for item in item_id_list]
            print item_id_into_file_list
            item_wf.writelines(item_id_into_file_list)
            crawled_url_wf.write(page_url)
            # time.sleep(3)
# read_whole_page_url_to_get_item_id()
def read_item_page_url():
    url = 'http://item.jd.com/11356077.html'
    req = urllib2.Request(url)
    req.set_proxy('221.10.102.199:82', 'http')
    html = urllib2.urlopen(req).read()
    soup = BeautifulSoup(html)
    head_info = soup.find('div', id='name')
    print head_info.text
    product_detail_str = soup.find('div', id='product-detail')
    print product_detail_str.text.strip()
    # content_level_list = product_detail_str.find_all('div', class_='sub-m m1', clstag=True)
    # content_text_list = [item.text.strip() for item in content_level_list]
    # for con in content_text_list:
    #     print con
    # print content_level_list#, len(content_list)
# read_item_page_url()
