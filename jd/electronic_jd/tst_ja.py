__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def li_item_parse():
    page_url_str = "<LI><a href='http://list.jd.com/737-794-798.html'>平板电视</a></LI>" \
                   "<LI><a href='http://list.jd.com/737-794-870.html'>空调</a></LI>"
    li_level_list = re.findall(r'\<LI\>.*\<\/LI>', page_url_str)[0]
    print li_level_list
    soup = BeautifulSoup(li_level_list)
    print soup.find_all('LI')
    print li_level_list, len(li_level_list)
    print soup.find_all('li')
def gen_whole_page_url():
    url = "http://list.jd.com/737-794-823-0-0-0-0-0-0-0-1-1-1-1-1-72-2799-33.html"
    html = urllib2.urlopen(url).read()
    li_level_list = re.findall(r'\<LI\>.*\<\/LI>', html)
    href_filter_pattern = re.compile(r"href='(http://.*\.html)'")
    topic_url_list = [href_filter_pattern.search(item).group(1) for item in li_level_list]
    # print topic_url_list, len(topic_url_list)
    print len(topic_url_list)
    return topic_url_list
# gen_whole_page_url()
def write_whole_page_url(page_url_list):
    filename = os.path.join(PATH, 'sys', 'whole_page_url')
    with codecs.open(filename, mode="a", encoding='utf-8') as wf:
        temp_list = [item+'\n' for item in page_url_list]
        wf.writelines(temp_list)
def parse_root_page_url():
    # url = 'http://list.jd.com/737-794-798.html'
    topic_url_list = gen_whole_page_url()
    topic_count = 0
    for url in topic_url_list:
        topic_count += 1
        html = urllib2.urlopen(url).read()
        page_size_div_pattern = re.compile(r"<div class='pagin pagin-m'>.*?</div>", re.S)
        page_size_str = page_size_div_pattern.search(html).group()
        soup = BeautifulSoup(page_size_str)
        page_size = soup.span.text.split('/')[1]
        print page_size,topic_count
        end_page_url = soup.a['href'] if soup.find('a') else ''
        if end_page_url:
            page_url_list = []
            splited_page_url = end_page_url.split('-')
            splited_page_url.pop(12)
            splited_page_url.insert(12,'%s')
            page_url_pattern = '-'.join(splited_page_url)
            for i in range(1,int(page_size)+1):
                url = 'http://list.jd.com/%s'%page_url_pattern%i
                page_url_list.append(url)
            write_whole_page_url(page_url_list)
                # print url
            # time.sleep(3)
# parse_root_page_url()
def re_combine_url():
    page_size = 14
    end_page_url = "737-794-798-0-0-0-0-0-0-0-1-1-2-1-1-72-4137-33.html"
    splited_page_url = end_page_url.split('-')
    # replace_num = end_page_url.split('-')[-6]
    # print splited_page_url[12]
    splited_page_url.pop(12)
    splited_page_url.insert(12,'%s')
    page_url_pattern = '-'.join(splited_page_url)
    print 'http://%s'%page_url_pattern%page_size
# re_combine_url()
def read_one_page_url_to_get_item_id():
    # url = "http://list.jd.com/737-738-898-0-0-0-0-0-0-0-1-1-1-1-1-72-2799-0.html"
    whole_page_url_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    electronic_item_id_filename = os.path.join(PATH, 'sys', 'electornic_item_id')
    crawled_page_url_filename = os.path.join(PATH, 'log', 'crawled_page_url')
    timeout_page_url_filename = os.path.join(PATH, 'log', 'timeout_page_url')
    with codecs.open(whole_page_url_filename,encoding='utf-8') as whole_url_f, \
    codecs.open(electronic_item_id_filename, mode='wb', encoding='utf-8') as elec_item_wf, \
    codecs.open(crawled_page_url_filename, mode='wb', encoding='utf-8') as crawled_url_wf, \
    codecs.open(timeout_page_url_filename, mode='wb', encoding='utf-8') as timeout_url_wf:
        for page_url in whole_url_f.readlines():
            try:
                html = urllib2.urlopen(page_url, timeout=10).read()
            except:
                timeout_url_wf.write('request timeout in url;%s'%page_url)
                continue
            item_id_str_list = re.findall("sku='\d+'", html)
            if not item_id_str_list:
                timeout_url_wf.write('no sku;%s'%page_url)
                continue
            item_id_list = [re.search("[\d]+", item).group() for item in item_id_str_list][3:]
            # print item_id_list, len(item_id_list)
            item_id_into_file_list = [item+'\n' for item in item_id_list]
            elec_item_wf.writelines(item_id_into_file_list)
            crawled_url_wf.write(page_url)
# read_one_page_url_to_get_item_id()

def filter_item_id():
    item_str_list = ["sku='965714'", "sku='965712'", "sku='1123854192'",
                     "sku='754448'", "sku='959228'", "sku='1111578'",
                     "sku='746959'", "sku='407009'", "sku='340408'",
                     "sku='966272'", "sku='1022423'", "sku='1063646'",
                     "sku='996118'", "sku='965716'", "sku='959407'",
                     "sku='959225'", "sku='987540'", "sku='747202'",
                     "sku='807113'", "sku='827049'", "sku='1003760703'",
                     "sku='732358'", "sku='959408'", "sku='1006803372'",
                     "sku='1074647'", "sku='965712'", "sku='1063642'",
                     "sku='896321'", "sku='680494'", "sku='988165'",
                     "sku='965705'", "sku='829884'", "sku='647588'",
                     "sku='996198'", "sku='620943'", "sku='896562'",
                     "sku='965714'", "sku='1003760710'", "sku='1004226943'"]
    item_id_list = [re.search("[\d]+", item).group() for item in item_str_list]
def read_page_get_item_url_list():
    url = "http://list.jd.com/737-1276-795-0-0-0-0-0-0-0-1-1-6-1-1-72-4137-33.html"
    html = urllib2.urlopen(url).read()
    item_id_str_list = re.findall("sku='\d+'", html)
    item_id_list = [re.search("[\d]+", item).group() for item in item_id_str_list][3:]
    print item_id_list
    temp_list_for_write = [item+'\n' for item in item_id_list]
    filename = os.path.join(PATH, 'sys', 'electornic_item_id')
    with codecs.open(filename, mode='a', encoding='utf-8') as f:
        f.writelines(temp_list_for_write)
# read_page_get_item_url_list()
