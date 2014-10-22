__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import random
import codecs
import urllib2
import xici_proxy
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
def parse_topic_url():
    book_url = "http://book.jd.com/booksort.html"
    response = urllib2.urlopen(book_url)
    html = response.read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='booksort')
    em_level_list = div_level_str.find_all('em')
    topic_url_list = [item.a['href'] for item in em_level_list]
    print topic_url_list[32:-2],len(set(topic_url_list))
    return topic_url_list[32:-2]
# parse_topic_url()
def gen_whole_page_url():
    topic_url_list = parse_topic_url()
    book_page_url_filename = os.path.join(PATH, 'sys', 'book_name_whole_page_url')
    redirect_url_filename = os.path.join(PATH, 'log', 'redirect_page_url')
    timeout_url_filename = os.path.join(PATH, 'log', 'timeout_topic_url')
    with codecs.open(book_page_url_filename, mode='wb', encoding='utf-8') as wf_page_url,\
    codecs.open(redirect_url_filename, mode='wb', encoding='utf-8') as wf_redirect_url,\
    codecs.open(timeout_url_filename, mode='wb', encoding='utf-8')as wf_timeout_url:
        count = 0
        for topic_url in topic_url_list:
            count += 1
            page_url_list = []
            try:
                response = urllib2.urlopen(topic_url, timeout=10)
                if response.geturl() != topic_url:
                    print 'redirect page hrer in url:%s'%topic_url
            except:
                wf_timeout_url.write(topic_url+'\n')
                print 'timed out item_id in url:%s'%topic_url
                continue
            html = response.read()
            soup = BeautifulSoup(html)
            max_page_str = soup.find('div', class_='pagin pagin-m')
            if not max_page_str:
                print 'max_page_str do not match regular expression in url:%s'%topic_url
                continue
            page_size = max_page_str.span.text.split('/')[-1]
            print count, page_size
            end_url_pattern = '?s=15&t=1&p=%s'
            for page_num in range(1, int(page_size)+1):
                url = ''.join((topic_url,end_url_pattern%page_num))
                page_url_list.append(url+'\n')
            wf_page_url.writelines(page_url_list)
# gen_whole_page_url()
def read_topic_page_url_to_get_pagesize():
        page_url = 'http://list.jd.com/1713-3265-3429.html'
        # page_url = "http://list.jd.com/1713-3267-3456.html"
        html = urllib2.urlopen(page_url).read()
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', id='plist')
        # print div_level_str
        a_level_list = div_level_str.find_all('a', href=re.compile('http://item'), class_=None, title=True, target='_blank')
        # print a_level_list, len(a_level_list)
        item_url_list = set([item['href'] for item in a_level_list])
        # print item_url_list,len(item_url_list)
        max_page_str = soup.find('div', class_='pagin pagin-m')
        if not max_page_str:
            print 'max_page_str is null'
            return
        page_size = max_page_str.span.text.split('/')[-1]
        print page_size
        end_url_pattern = '?s=15&t=1&p=%s'
        for page_num in range(1, int(page_size)+1):
            url = ''.join((page_url,end_url_pattern%page_num))
            print url
# read_topic_page_url_to_get_pagesize()