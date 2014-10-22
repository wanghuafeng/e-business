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
CONFIG_PATH = os.path.dirname(PATH)# the path you can read config file!


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
    filename = os.path.join(PATH, 'item_id', timestamp)
    with codecs.open(filename, 'a', encoding='utf-8') as wf:
        wf.writelines(con_list)

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
