__author__ = 'huafeng'
#coding:utf-8
import re
import os
import time
import codecs
import requests
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
timestamp = time.strftime('%Y%m%d')
print timestamp
def gen_topic_url():
    root_url = 'http://news.sina.com.cn'
    response = requests.get(root_url)
    html = response.text.encode('ISO-8859-1').decode('gbk')
    soup = BeautifulSoup(html, 'html5lib')
    div_level_str = soup.find('div', id='blk_cNav2_01')
    url__str_list = div_level_str.find_all('a')
    url_list = [item['href'] for item in url__str_list]
    print url_list
# gen_topic_url()

def read_page_url_to_get_item_timestamp():
    url = 'http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=80&asc=&page=2&r=0.30903104213777677'
    res = requests.get(url)
    html = res.text.encode('ISO-8859-1').decode('gbk')
    page_url_list = re.findall(r'url : "(http://.*?\.shtml)"', html)
    print page_url_list, len(page_url_list)
    # url_list_timestamp = page_url_list[-1].split('/')[-2]
    # valid_timestamp_url_list = []
    # for url in page_url_list:
    #     url_list_timestamp = url.split('/')[-2]
    #     if url_list_timestamp == timestamp:
    #         valid_timestamp_url_list.append(url)
# read_page_url_to_get_item_timestamp()
def write_item_url_content_into_file(item_url_info_list):
    timestamp_filename = time.strftime('%Y-%m-%d-%H')
    filename = os.path.join(PATH, 'out', timestamp_filename)
    with codecs.open(filename, mode='a', encoding='utf-8') as wf:
        wf.writelines(item_url_info_list)

def read_item_url_file():
    whole_item_url_filename = os.path.join(PATH, 'sys', 'whole_item_url_%s'%timestamp)
    timestamp_filename = time.strftime('%Y-%m-%d-%H%M00-sina_news')
    output_filename = os.path.join(PATH, 'out', timestamp_filename)
    with codecs.open(whole_item_url_filename, encoding='utf-8') as f,\
    codecs.open(output_filename, mode='a', encoding='utf-8') as wf:
        url_list = [item.strip() for item in f.readlines()]
        count = 0
        for url in url_list:
            item_url_info_list = []
            count += 1
            response = requests.get(url)
            html = response.text.encode('ISO-8859-1')
            soup = BeautifulSoup(html, 'html5lib')
            print count, url
            try:
                title = soup.find('h1', id='artibodyTitle').text.strip()
                item_url_info_list.append(title+'\n')
                div_lelvel_str = soup.find('div', id='artibody')
                p_level_list = div_lelvel_str.find_all('p')
                content_list = [item.text.strip()+'\n' for item in p_level_list]
                item_url_info_list.extend(content_list)
            except BaseException:
                print 'div do not match pattern in url;%s'%url
            if item_url_info_list:
                wf.writelines(item_url_info_list)
# read_item_url_file()
def write_item_url_into_file(item_url_list):
    item_url_filename = os.path.join(PATH, 'sys', 'whole_item_url_%s'%timestamp)
    with codecs.open(item_url_filename, mode='a', encoding='utf-8') as af:
        temp_url_list_for_write = [item+'\n' for item in item_url_list]
        af.writelines(temp_url_list_for_write)
def get_realtime_news():
    url_pattern = 'http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=80&asc=&page=%s&r=0.30903104213777677'
    start_page_num = 1
    count = 0
    valid_timestamp_url_list = []
    for page_num in range(start_page_num, start_page_num+100):
        count += 1
        res = requests.get(url_pattern%page_num)
        html = res.text.encode('ISO-8859-1').decode('gbk')
        page_url_list = re.findall(r'url : "(http://.*\.shtml)"', html)
        url_list_timestamp = page_url_list[-1].split('/')[-2].replace('-', '')
        print count, len(page_url_list)
        print url_list_timestamp, timestamp
        print '*'*50
        if url_list_timestamp != timestamp:
            for url in page_url_list:
                url_list_timestamp = url.split('/')[-2].replace('-','')
                print url_list_timestamp, timestamp
                if url_list_timestamp ==  timestamp:
                    valid_timestamp_url_list.append(url)
            write_item_url_into_file(valid_timestamp_url_list)
            # read_item_url_file()
            break
        else:
            write_item_url_into_file(page_url_list)
# get_realtime_news()
def read_one_item_url():
    url = 'http://tech.sina.com.cn/i/2014-06-23/10309453260.shtml'
    # url = 'http://ent.sina.com.cn/r/i/2014-06-23/09544163240.shtml'
    response = requests.get(url)
    html = response.text.encode('ISO-8859-1')
    # print html
    soup = BeautifulSoup(html, 'html5lib')
    # print html
    with codecs.open('i+2014-06-23+10309453260.shtml', mode='wb') as wf:
        wf.write(html)
    # print soup.find('h1', id='artibodyTitle')#.text
    # title = soup.find('h1', id='artibodyTitle').text.strip()
    # div_lelvel_str = soup.find('div', id='artibody')#.p.text.strip()
    # p_level_list = div_lelvel_str.find_all('p')
    # content_list = [item.text.strip() for item in p_level_list]
    # for con in content_list:
    #     print con
    # print title
# read_one_item_url()
# url = 'http://tech.sina.com.cn/i/2014-06-23/10309453260.shtml'
# splited_url = url.split('/')
# print '+'.join(splited_url[-3:])
sys_filename = os.path.join(PATH, 'sys', 'whole_item_url_20140623')
with codecs.open(sys_filename) as f:
    f_list = f.readlines()
    f_set = set(f_list)
    print len(f_list), len(f_set)
    # print len(set(f.readlines()))