__author__ = 'huafeng'
#coding:utf-8
import re
import os
import time
import codecs
import requests
import datetime
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
TIMESTAMP = time.strftime('%Y%m%d')
# yesterday = (datetime.datetime.now().date()+datetime.timedelta(days=-1)).strftime('%Y_%m_%d')

def read_item_url_file():
    whole_item_url_filename = os.path.join(PATH, 'sys', 'whole_item_url_%s'%TIMESTAMP)
    timestamp_filename = time.strftime('%Y_%m_%d_%H%M00_sina_news')
    data_directory = os.path.join(PATH, 'html', time.strftime('%Y_%m_%d'))
    if not os.path.exists(data_directory):
        os.system('mkdir %s'%data_directory)
    output_filename = os.path.join(PATH, 'out', timestamp_filename)
    failed_url_filename = os.path.join(PATH, 'log','sina_news_log')
    with codecs.open(whole_item_url_filename, encoding='utf-8') as f,\
    codecs.open(output_filename, mode='a', encoding='utf-8') as wf,\
    codecs.open(failed_url_filename, mode='a', encoding='utf-8')as log_f:
        url_list = [item.strip() for item in f.readlines()]
        for url in url_list:
            item_url_info_list = []
            try:
                html = requests.get(url).text.encode('ISO-8859-1')
                splited_url = url.split('/')
                html_filename = '+'.join(splited_url[-3:])
                filename = os.path.join(PATH, data_directory, html_filename)
                with open(filename, mode='wb') as htmlwf:
                    htmlwf.write(html)
            except BaseException:
                log_f.write('timed out in item_url;%s\n'%url)
                continue
            soup = BeautifulSoup(html, 'html5lib')
            try:
                title = soup.find('h1', id='artibodyTitle').text.strip()
                item_url_info_list.append(title+'\n')
                div_lelvel_str = soup.find('div', id='artibody')
                p_level_list = div_lelvel_str.find_all('p')
                content_list = [item.text.strip()+'\n' for item in p_level_list]
                item_url_info_list.extend(content_list)
            except BaseException:
                log_f.write('div do not match pattern in item_url;%s\n'%url)
                continue
            if item_url_info_list:
                wf.writelines(item_url_info_list)
# read_item_url_file()
def write_item_url_into_file(item_url_list):
    item_url_filename = os.path.join(PATH, 'sys', 'whole_item_url_%s'%TIMESTAMP)
    with codecs.open(item_url_filename, mode='a', encoding='utf-8') as af:
        temp_url_list_for_write = [item+'\n' for item in item_url_list]
        af.writelines(temp_url_list_for_write)
def get_realtime_news():
    url_pattern = 'http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=89&spec=&type=&ch=01&k=&offset_page=0&offset_num=0&num=80&asc=&page=%s&r=0.30903104213777677'
    start_page_num = 1
    valid_timestamp_url_list = []
    failed_url_filename = os.path.join(PATH, 'log','sina_news_log')
    with codecs.open(failed_url_filename, mode='a', encoding='utf-8')as af:
        for page_num in range(start_page_num, start_page_num+100):
            url = url_pattern%page_num
            try:
                res = requests.get(url)
                html = res.text.encode('ISO-8859-1')
            except BaseException:
                try:
                    res = requests.get(url)
                    html = res.text.encode('ISO-8859-1')
                except BaseException:
                    try:
                        res = requests.get(url)
                        html = res.text.encode('ISO-8859-1')
                    except BaseException:
                        af.write('timed out in page_url;%s\n'%url)
                        continue
            try:
                page_url_list = re.findall(r'url : "(http://.*\.shtml)"', html)
                url_list_timestamp = page_url_list[-1].split('/')[-2].replace('-', '')
            except BaseException:
                af.write('div not pattern in page_url;%s\n'%url)
                continue
            if url_list_timestamp != TIMESTAMP:
                for url in page_url_list:
                    url_list_timestamp = url.split('/')[-2].replace('-','')
                    if url_list_timestamp ==  TIMESTAMP:
                        valid_timestamp_url_list.append(url)
                write_item_url_into_file(valid_timestamp_url_list)
                read_item_url_file()
                break
            else:
                write_item_url_into_file(page_url_list)

if __name__ == '__main__':
    get_realtime_news()
