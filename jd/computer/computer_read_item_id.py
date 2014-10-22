__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
import ConfigParser
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
SPIDER_IN_CONFIG = 'computer'
CONFIG_PATH = os.path.dirname(PATH)# watch item_id config directory here!

config = ConfigParser.ConfigParser()
config_filename = os.path.join(CONFIG_PATH, 'config.ini')
config.read(config_filename)
crawled_id_in_log = config.get('%s'%SPIDER_IN_CONFIG, 'crawled_id_filename')
root_url_pattern = config.get('root', 'root_url_pattern')
item_id_filename = config.get('%s'%SPIDER_IN_CONFIG, 'item_id_filename')
item_content_filename = config.get('%s'%SPIDER_IN_CONFIG, 'item_content_filename')
failed_id_in_log = config.get('%s'%SPIDER_IN_CONFIG, 'failed_id_filename')

def write_con_into_file(con_list):
    timestamp = time.strftime('%Y_%m_%d_%H_{}'.format(item_content_filename))
    filename = os.path.join(PATH, 'out', timestamp)
    with codecs.open(filename, 'a', encoding='utf-8') as wf:
        wf.writelines(con_list)

pattern = re.compile('\d+')
def get_id_from_breakpoint():
    crawled_id_filename = os.path.join(PATH, 'log', crawled_id_in_log)
    if not os.path.isfile(crawled_id_filename):
        open(crawled_id_filename,mode='w')
    with codecs.open(crawled_id_filename, encoding='utf-8') as f:
        try:
            last_crawled_id_str = f.readlines()[-1]
            last_crawled_id = pattern.search(last_crawled_id_str).group()
        except:
            last_crawled_id = '000000'
    items_id_filename = os.path.join(PATH, 'sys', item_id_filename)
    with codecs.open(items_id_filename, encoding='utf-8') as idf:
        whole_item_id_list = [item.strip() for item in idf.readlines()]
        try:
            break_point_index = whole_item_id_list.index(last_crawled_id)
        except:
            break_point_index = 0
        # print len(whole_item_id_list[break_point_index:])
        return whole_item_id_list[break_point_index:]

# get_id_from_breakpoint()

def read_item_id():
    whole_item_id_list = get_id_from_breakpoint()
    failed_id_filename = os.path.join(PATH, 'log', failed_id_in_log)
    crawled_id_filename = os.path.join(PATH, 'log', crawled_id_in_log)
    ip_port = '115.28.50.204:80'
    http_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    null_http_hanlder = urllib2.ProxyHandler({})
    enable_proxy = False
    if enable_proxy:
        proxy_hanlder = urllib2.build_opener(http_hanlder)
    else:
        proxy_hanlder = urllib2.build_opener(null_http_hanlder)
    urllib2.install_opener(proxy_hanlder)

    with codecs.open(failed_id_filename, mode='a', encoding='utf-8') as failed_id_wf, \
    codecs.open(crawled_id_filename, mode='a', encoding='utf-8') as crawled_id_wf:
        for item_id in whole_item_id_list:
            item_msg_list = []
            url = root_url_pattern%item_id
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
            try:
                soup = BeautifulSoup(html, 'html5lib')
                name_str = soup.find('div', id='name')
            except:
                continue
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
    failed_id_filename = os.path.join(PATH, 'log', failed_id_in_log)
    ip_port = '115.28.50.204:80'
    http_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    opener = urllib2.build_opener(http_hanlder)
    crawled_failed_id = os.path.join(PATH, 'log', 'crawled_failed_id')
    with codecs.open(failed_id_filename, encoding='utf-8') as failed_id_f,\
    codecs.open(crawled_failed_id, mode='a', encoding='utf-8') as crawled_id_af:
        for line in failed_id_f.readlines():
            item_msg_list = []
            url = line.split(';')[1].strip()
            html = opener.open(url).read()
            try:
                soup = BeautifulSoup(html, 'html5lib')
                name_str = soup.find('div', id='name')
            except:
                continue
            if not name_str:
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
                crawled_id_af.write('%s\n'%url)
                continue
            item_msg_list.append(item_info+'\n')
            write_con_into_file(item_msg_list)
            crawled_id_af.write('%s\n'%url)
read_failed_id()
