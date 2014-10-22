__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
ROOT_URL_PATTERN = 'http://item.jd.com/%s.html'
CRALWED_ID_IN_LOG = 'crawled_id_url'

def write_con_into_file(con_list):
    timestamp = time.strftime('%Y_%m_%d_%H_book_id_con')
    filename = os.path.join(PATH, 'out', timestamp)
    with codecs.open(filename, 'a', encoding='utf-8') as wf:
        wf.writelines(con_list)

pattern = re.compile('\d+')
def get_id_from_breakpoint():
    crawled_id_filename = os.path.join(PATH, 'log', CRALWED_ID_IN_LOG)
    if not os.path.isfile(crawled_id_filename):
        open(crawled_id_filename,mode='w')
    with codecs.open(crawled_id_filename, encoding='utf-8') as f:
        try:
            last_crawled_id_str = f.readlines()[-1]
            last_crawled_id = pattern.search(last_crawled_id_str).group()
        except:
            last_crawled_id = '000000'
    item_id_filename = os.path.join(PATH, 'sys', 'book_item_ids')
    with codecs.open(item_id_filename, encoding='utf-8') as idf:
        whole_item_id_list = [item.strip() for item in idf.readlines()]
        try:
            break_point_index = whole_item_id_list.index(last_crawled_id)
        except:
            break_point_index = 0
        print len(whole_item_id_list[break_point_index:])
        return whole_item_id_list[break_point_index:]

# get_id_from_breakpoint()

def read_item_id():
    whole_item_id_list = get_id_from_breakpoint()
    failed_id_filename = os.path.join(PATH, 'log', 'failed_id_url')
    crawled_id_filename = os.path.join(PATH, 'log', CRALWED_ID_IN_LOG)
    with codecs.open(failed_id_filename, mode='a', encoding='utf-8') as failed_id_wf, \
    codecs.open(crawled_id_filename, mode='a', encoding='utf-8') as crawled_id_wf:
        for item_id in whole_item_id_list:
            item_msg_list = []
            url = ROOT_URL_PATTERN%item_id
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except BaseException:
                time.sleep(30)
                try:
                    html = urllib2.urlopen(url, timeout=15).read()
                except BaseException:
                    time.sleep(30)
                    try:
                        html = urllib2.urlopen(url, timeout=15).read()
                    except BaseException:
                        failed_id_wf.write('request timed item_id in url;%s\n'%url)
                        continue
            soup = BeautifulSoup(html)
            name_str = soup.find('div', id='name')
            if not name_str:
                failed_id_wf.write('not match name_str in url;%s\n'%url)
                continue
            try:
                item_name =  name_str.text.strip()
                item_msg_list.append(item_name+'\n')
            except:
                pass
            detail_info = soup.find('div', id='product-detail-1')
            item_info = ''
            if not detail_info:
                failed_id_wf.write('not match detail-list-1 in url;%s\n'%url)
            else:
                try:
                    item_info = detail_info.text.strip()
                except:
                    write_con_into_file(item_msg_list)
                    crawled_id_wf.write(url+'\n')
                    continue

            item_msg_list.append(item_info+'\n')
            write_con_into_file(item_msg_list)
            crawled_id_wf.write(url+'\n')
# read_item_id()
def read_failed_id():
    failed_id_filename = os.path.join(PATH, 'log', 'failed_id_url')
    re_crawled_id_filename = os.path.join(PATH, 'log', 're_crawled_id')
    re_failed_id_filename = os.path.join(PATH, 'log', 're_failed_id')
    with codecs.open(failed_id_filename, encoding='utf-8') as failed_id_f,\
        codecs.open(re_crawled_id_filename, mode='wb', encoding='utf-8') as re_crawled_wf,\
        codecs.open(re_failed_id_filename, mode='wb', encoding='utf-8') as re_failed_wf:
            for line in failed_id_f.readlines():
                item_msg_list = []
                url = line.split(';')[1].strip()
                html = urllib2.urlopen(url).read()
                try:
                    soup = BeautifulSoup(html, 'html5lib')
                    name_str = soup.find('div', id='name')
                except:
                    re_failed_wf.write('timed out in url;%s\n'%url)
                    continue
                if not name_str:
                    re_failed_wf.write('not match pattern in url;%s\n'%url)
                    continue
                try:
                    item_name =  name_str.text.strip()
                    item_msg_list.append(item_name+'\n')
                except:
                    pass
                detail_info = soup.find('div', id='product-detail-1')
                try:
                    item_info = detail_info.text.strip()
                except:
                    write_con_into_file(item_msg_list)
                    re_crawled_wf.write('%s\n'%url)
                    continue
                item_msg_list.append(item_info+'\n')
                write_con_into_file(item_msg_list)
                re_crawled_wf.write('%s\n'%url)
read_failed_id()