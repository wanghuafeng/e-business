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
#
# ip_port = '210.73.220.18:8088'
# http_handler = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
# opener = urllib2.build_opener(http_handler)
# urllib2.install_opener(opener)
def test_proxy_set():
    url = 'http://42.96.192.46/echo_ip'
    loc_ip = urllib2.urlopen(url).read()
    print loc_ip
# test_proxy_set()
# def get_whole_root_page_url():
#     url = 'http://www.yhd.com/ctg/s2/c22993-%E5%A5%B3%E5%8C%85/?tc=0.0.16.CatMenu_Site_100000003_7421_68.1936&tp=6.0.15.0.1621.QhBmqG'
#     html = urllib2.urlopen(url).read()
#     soup = BeautifulSoup(html, 'html5lib')
#     li_level_list = soup.find_all('li', class_=' child_third')
#     root_url_list = [item.a['href'] for item in li_level_list]
#     print root_url_list, len(root_url_list)
#     print urllib.unquote(url)
#     return root_url_list
# # get_whole_root_page_url()
def get_whole_root_page_url():
    url = 'http://www.yhd.com/ctg/s2/vc1925?tc=0.0.16.CatMenu_Site_100000003_7421_70.1938&tp=6.0.15.0.1623.QhBmqG'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html, 'html5lib')
    li_level_list = soup.find('div', class_='search_filter')
    root_url_list = re.findall(r'href="(http://www.yhd.com.*?)"', li_level_list.encode('utf-8'))
    print root_url_list, len(root_url_list)
    # root_url_list = [item.a['href'] for item in li_level_list]
    # print root_url_list, len(root_url_list)
    # print urllib.unquote(url)
    return root_url_list
# get_whole_root_page_url()
def read_one_root_url_get_pagesize():
    url = 'http://www.yhd.com/ctg/s2/c22996-0-61537/'
    html = urllib2.urlopen(url).read()
    # html = simplejson.loads(json_str)['value']
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', class_='search_select_page')
    page_size_level_str = div_level_str.find('div', class_='select_page_num')
    page_size_str = page_size_level_str.encode('utf-8')
    page_size = re.search(r'/(\d+)', page_size_str).group(1)
    print page_size
    url_pattern_str = div_level_str.find('div', class_='select_page_btn')
    url_str = url_pattern_str.find('a', class_='next')['url']
    url_pattern = re.sub(r'-p.-', '-p%s-', url_str)
    for page_num in range(1, int(page_size)+1):
        page_url = url_pattern%page_num
        print page_url
# read_one_root_url_get_pagesize()
whole_page_url_name = 'watchGlass_whole_page_url'
def get_whole_page_url():
    root_url_list = get_whole_root_page_url()
    page_url_filename = os.path.join(PATH, 'page_url', whole_page_url_name)
    with codecs.open(page_url_filename, mode='wb', encoding='utf-8')as wf:
        count = 0
        for root_url in root_url_list:
            page_url_list = []
            count += 1
            html = urllib2.urlopen(root_url).read()
            soup = BeautifulSoup(html)
            div_level_str = soup.find('div', class_='search_select_page')
            page_size_level_str = div_level_str.find('div', class_='select_page_num')
            page_size_str = page_size_level_str.encode('utf-8')
            page_size = re.search(r'/(\d+)', page_size_str).group(1)
            print count, page_size
            if int(page_size) == 1:
                page_url_list.append(root_url+'\n')
            else:
                url_pattern_str = div_level_str.find('div', class_='select_page_btn')
                url_str = url_pattern_str.find('a', class_='next')['url']
                url_pattern = re.sub(r'-p.-', '-p%s-', url_str)
                for page_num in range(1, int(page_size)+1):
                    page_url = url_pattern%page_num
                    page_url_list.append(page_url+'\n')
            wf.writelines(page_url_list)
get_whole_page_url()
def read_one_page_url_get_item_id():
    # url = 'http://www.yhd.com/ctg/s2/c22882-0-59582/'
    page_url = 'http://www.yhd.com/ctg/searchPage/c5228-0-59327/b/a-s1-v0-p11-price-d0-f0-m1-rt0-pid-mid0-k/'
    html = urllib2.urlopen(page_url).read()
    if not page_url.endswith(r'k/'):
        mid_str = re.search(r"var topProductsId = '([\d+_1,]+)'", html).group(1)
    else:
        callback_json_str = simplejson.loads(html)
        json_html = callback_json_str['value']
        mid_str = re.search(r'"extField7":"([\d+_1,]+)"', json_html).group(1)

    mid_list = re.findall(r'(\d+)_1', mid_str)
    print mid_list, len(mid_list)

# read_one_page_url_get_item_id()
item_id_name = 'beauty_item_id'
def get_whole_item_id():
    whole_page_url_filename = os.path.join(PATH, 'page_url', whole_page_url_name)
    crawled_page_url_filename = os.path.join(PATH, 'log', 'crawled_page_url')
    failed_page_url_filename = os.path.join(PATH, 'log', 'failed_page_url')
    item_id_filename = os.path.join(PATH, 'item_id', item_id_name)
    count = 0
    with codecs.open(whole_page_url_filename, encoding='utf-8') as page_url_f, \
            codecs.open(crawled_page_url_filename, mode='wb', encoding='utf-8') as crawled_wf, \
            codecs.open(failed_page_url_filename, mode='wb', encoding='utf-8') as failed_wf, \
            codecs.open(item_id_filename, mode='a', encoding='utf-8') as item_id_wf:
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
            if not page_url.endswith(r'k/'):
                try:
                    mid_str = re.search(r"var topProductsId = '([\d+_1,]+)'", html).group(1)
                except BaseException:
                    print 'do not match pattern;%s'%page_url
                    failed_wf.write('do not match pattern;%s\n'%page_url)
                    continue
            else:
                try:
                    callback_json_str = simplejson.loads(html)
                    json_html = callback_json_str['value']
                    mid_str = re.search(r'"extField7":"([\d+_1,]+)"', json_html).group(1)
                except BaseException:
                    print 'do not match pattern;%s'%page_url
                    failed_wf.write('do not match pattern;%s\n'%page_url)
                    continue
            mid_list = re.findall(r'(\d+)_1', mid_str)
            temp_item_id_list = [item+'\n' for item in mid_list]
            item_id_wf.writelines(temp_item_id_list)
            crawled_wf.write(page_url+'\n')
            print count, len(mid_list)
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
            if not page_url.endswith(r'k/'):
                try:
                    mid_str = re.search(r"var topProductsId = '([\d+_1,]+)'", html).group(1)
                except BaseException:
                    print 'do not match pattern;%s'%page_url
                    continue
            else:
                try:
                    callback_json_str = simplejson.loads(html)
                    json_html = callback_json_str['value']
                    mid_str = re.search(r'"extField7":"([\d+_1,]+)"', json_html).group(1)
                except BaseException:
                    print 'do not match pattern;%s'%page_url
                    continue
            mid_list = re.findall(r'(\d+)_1', mid_str)
            temp_item_id_list = [item+'\n' for item in mid_list]
            wf.writelines(temp_item_id_list)
            print count, len(mid_list)
# read_failed_page_url()