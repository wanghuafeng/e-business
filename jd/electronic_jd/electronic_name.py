__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import random
import codecs
import urllib2
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def read_item_id_to_get_title():
    url_pattern = 'http://item.jd.com/%s.html'
    elec_filename = os.path.join(PATH, 'out', 'elec_name')
    failed_url = os.path.join(PATH, 'log', 'elec_failed_url')
    count = 0
    with codecs.open('./sys/electronic_item_id', encoding='utf-8') as item_id_f,\
    codecs.open(failed_url, mode='wb', encoding='utf-8') as failed_url_wf,\
    codecs.open(elec_filename, mode='a', encoding='utf-8') as con_to_write_wf:
        for item_id in [item.strip() for item in item_id_f.readlines()]:
            count += 1
            item_url = url_pattern%item_id
            try:
                html = urllib2.urlopen(item_url).read()
            except:
                try:
                    html = urllib2.urlopen(item_url).read()
                except:
                    try:
                        html = urllib2.urlopen(item_url).read()
                    except:
                        print 'timed out in url;%s'%item_url
                        failed_url_wf.write('timed out in url;%s\n'%item_url)
                        continue
            soup = BeautifulSoup(html)
            try:
                div_level_str = soup.find('div', id='name')
                elec_title = div_level_str.text.strip()
            except:
                print 'div do not match pattern in url;%s'%item_url
                failed_url_wf.write('div do not match pattern in url;%s\n'%item_url)
                continue
            con_to_write_wf.write(elec_title+'\n')
            print count

def read_failed_item_url():
    failed_url_filename = os.path.join(PATH, 'log', 'elec_failed_url')
    content_to_write_filename = os.path.join(PATH, 'out', 'elec_name')
    with codecs.open(failed_url_filename, encoding='utf-8') as item_id_f,\
    codecs.open(content_to_write_filename, mode='a', encoding='utf-8') as content_to_write_af:
        count = 0
        for url in [item.split(';')[1].strip() for item in item_id_f.readlines()]:
            count += 1
            try:
                html = urllib2.urlopen(url).read()
            except:
                try:
                    html = urllib2.urlopen(url).read()
                except:
                    print 'timed out in url:%s'%url
                    continue

            soup = BeautifulSoup(html)
            try:
                div_level_str = soup.find('div', id='name')
                elec_title = div_level_str.text.strip()
            except:
                print 'div do not match pattern in url;%s'%url
                continue
            content_to_write_af.write(elec_title+'\n')
            print count
# read_failed_item_url()

def extract_goods_name():
    electronic_name_str_filename = os.path.join(PATH, 'out', 'elec_name')
    content_for_write_filename =os.path.join(PATH, 'out', 'electronic_name.txt')
    pattern = re.compile(ur"([\u4E00-\u9FA5]+)", re.U)
    count = 0
    with codecs.open(electronic_name_str_filename, encoding='utf-8') as f,\
    codecs.open(content_for_write_filename, mode='a', encoding='utf-8') as electronic_name_af:
        for line in f.readlines():
            count += 1
            temp_list_for_write = []
            splited_lien = pattern.split(line)
            for param in splited_lien:
                if len(param) <= 1:
                    continue
                if pattern.match(param):
                    temp_list_for_write.append(param+'\n')
            print count
            electronic_name_af.writelines(temp_list_for_write)

def chose_len_between_1_8_param():
    content_for_write_filename =os.path.join(PATH, 'out', 'electronic_name.txt')
    with codecs.open(content_for_write_filename, encoding='utf-8') as f:
        electronic_name_list = f.readlines()
        print len(electronic_name_list)
        remove_length_one_param_list = [item for item in electronic_name_list if 1<len(item.strip())<=8]
        print len(remove_length_one_param_list)
    with codecs.open(content_for_write_filename, mode='wb', encoding='utf-8') as wf:
        wf.writelines(remove_length_one_param_list)
        print len(remove_length_one_param_list)