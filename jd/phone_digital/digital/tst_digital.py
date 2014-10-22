__author__ = 'huafeng'
import re
import os
import time
import urllib2
import codecs
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def gen_root_page_url():
    url = "http://list.jd.com/652-654-832-0-0-0-0-0-0-0-1-1-1-1-1-72-4137-0.html"
    html = urllib2.urlopen(url).read()
    li_level_list = re.findall(r'\<LI\>.*\<\/LI>', html)
    href_filter_pattern = re.compile(r"href='(http://.*\.html)'")
    topic_url_list = [href_filter_pattern.search(item).group(1) for item in li_level_list]
    # print topic_url_list, len(topic_url_list)
    print len(topic_url_list)
    return topic_url_list
# gen_root_page_url()
def write_whole_page_url(page_url_list):
    filename = os.path.join(PATH, 'sys', 'whole_page_url')
    with codecs.open(filename, mode="a", encoding='utf-8') as wf:
        temp_list = [item+'\n' for item in page_url_list]
        wf.writelines(temp_list)
def gen_whole_page_url():
    topic_url_list = gen_root_page_url()
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
# gen_whole_page_url()
def read_one_root_page_url():
    url = "http://list.jd.com/652-654-832-0-0-0-0-0-0-0-1-1-1-1-1-72-4137-0.html"
    html = urllib2.urlopen(url).read()
    page_size_div_pattern = re.compile(r"<div class='pagin pagin-m'>.*?</div>", re.S)
    page_size_str = page_size_div_pattern.search(html).group()
    soup = BeautifulSoup(page_size_str)
    page_size = soup.span.text.split('/')[1]
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
        print page_url_list, len(page_url_list)
# read_one_root_page_url()
def read_page_get_item_url_list():
    url = "http://list.jd.com/9987-653-655-0-0-0-0-0-0-0-1-1-1-1-1-72-4137-33.html"
    html = urllib2.urlopen(url).read()
    item_id_str_list = re.findall("sku='\d+'", html)
    item_id_list = [re.search("[\d]+", item).group() for item in item_id_str_list][3:]
    print item_id_list, len(item_id_list)
    temp_list_for_write = [item+'\n' for item in item_id_list]
    filename = os.path.join(PATH, 'sys', 'digital_item_id')
    with codecs.open(filename, mode='a', encoding='utf-8') as f:
        f.writelines(temp_list_for_write)
# read_page_get_item_url_list()
def read_one_page_url_to_get_item_id():
    # url = "http://list.jd.com/737-738-898-0-0-0-0-0-0-0-1-1-1-1-1-72-2799-0.html"
    whole_page_url_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    electronic_item_id_filename = os.path.join(PATH, 'sys', 'digital_item_id')
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