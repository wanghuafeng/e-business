__author__ = 'wanghuafeng'
#coding:utf-8
import re
import os
import time
import codecs
import random
import logging
import urllib2
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
SLEEP_INTERVAL = random.randint(3, 5)


class SohuSpider(object):

    def __init__(self):
        self.msg_comment_list = []
        self._gen_log()
        self.url_history_crawled()#记录已抓取过的url

    def url_history_crawled(self):
        sohufile = time.strftime('log/sohu_url_crawled.txt')
        url_logfile = os.path.join(PATH, sohufile)
        self.url_history_logger = logging.getLogger('sohu')
        self.url_history_logger.setLevel(logging.DEBUG)
        url_log_file = logging.FileHandler(url_logfile, encoding="utf-8")
        url_log_file.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        url_log_file.setFormatter(formatter)
        self.url_history_logger.addHandler(url_log_file)

    def _gen_log(self):
        timestamp = time.strftime("%Y_%m_%d")
        filename = "".join((timestamp, '_sohu_history.log'))
        logfile = os.path.join(PATH, 'log', filename)
        self.logger = logging.getLogger(__name__)
        self.logger.setLevel(logging.DEBUG)
        log_file = logging.FileHandler(logfile)
        log_file.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        log_file.setFormatter(formatter)
        self.logger.addHandler(log_file)

    def gen_page_urls(self):
        page_url_list = []
        filename = os.path.join(PATH, 'sys', 'whole_page_url')
        if not os.path.isfile(filename):
            raise ValueError('No such file:%s'%filename)
        with open(filename) as f:
            for url in f.readlines():
                url = url.strip()
                page_url_list.append(url)
        return page_url_list

    def item_url_con(self, item_url_list):

        if item_url_list:
            for url in item_url_list:
                # url = "http://club.news.sohu.com/minjian/thread/z9xt2y19xy"
                try:
                    html = urllib2.urlopen(url, timeout=10).read()
                except Exception, e:
                    continue

                comment_con_list = re.findall(r'\<table class=\\\"viewpost\\\".*\<\\\/table\>', html)
                if not comment_con_list:
                    continue
                clear_comment_str = re.sub(r'\\\\', '', comment_con_list[0])
                clear_comment_str = eval('u"""%s"""'%clear_comment_str)
                msg_comment_con = re.sub(r'\\', '', clear_comment_str)
                soup = BeautifulSoup(msg_comment_con)
                div_level_list = soup.find_all('div', class_='wrap')
                msg_comment_text_list = [item.text.strip() for item in div_level_list]

                self.msg_comment_list.extend(msg_comment_text_list)
                time.sleep(SLEEP_INTERVAL)

                pages_size_list = re.findall(r'\<div class=\\\"pages\\\".*?\<\\\/div\>', html)
                #评论时否为多页
                if pages_size_list:
                    multi_url_list = []
                    div_level_str = re.sub(r'\\', '', pages_size_list[0])
                    soup = BeautifulSoup(div_level_str)
                    a_level_list = soup.find_all('a')
                    url_list = [item['href'] for item in a_level_list]
                    if url_list:
                        max_size_str = url_list[-2]
                        splited_max_list = max_size_str.split('/')
                        if splited_max_list:
                            match = re.search('[\d]+', splited_max_list[-1])
                            if match:
                                multi_comment_page_size = match.group()
                                for i in range(2,int(multi_comment_page_size)+1):
                                    new_end_url = "/p%s"%str(i)
                                    multi_comment_url = "".join((url, new_end_url))
                                    multi_url_list.append(multi_comment_url)
                    #抓取多页评论
                    self.multi_commet_pages(multi_url_list)

    def multi_commet_pages(self, comment_url_list):
        for url in comment_url_list:
            # url = "http://club.news.sohu.com/minjian/thread/z9xt2y19xy/p5"
            try:
                html = urllib2.urlopen(url, timeout=10).read()
            except Exception, e:
                continue
            comment_con_list = re.findall(r'\<table class=\\\"viewpost\\\".*\<\\\/table\>', html)
            if not comment_con_list:
                continue
            clear_comment_str = re.sub(r'\\\\', '', comment_con_list[0])
            clear_comment_str = eval('u"""%s"""'%clear_comment_str)
            msg_comment_con = re.sub(r'\\', '', clear_comment_str)
            soup = BeautifulSoup(msg_comment_con)
            div_level_list = soup.find_all('div', class_='wrap')
            msg_comment_text_list = [item.text.strip() for item in div_level_list]
            self.msg_comment_list.extend(msg_comment_text_list)
            time.sleep(SLEEP_INTERVAL)

    def write_msg_comment_into_file(self, msg_comment_set):
        '''满足条数限制以后将抓取信息写入到本地'''
        timestamp = time.strftime('%Y_%m_%d_%H%M%S')
        confile = "".join((timestamp, '_sohu.txt'))
        output_filename = os.path.join(PATH, 'out', confile)
        with codecs.open(output_filename, mode="wb", encoding="utf-8") as wf:
            temp_list = [item+"\n" for item in msg_comment_set]
            wf.writelines(temp_list)

    def main(self):
        page_url_list = self.gen_page_urls()
        for url in page_url_list:
            self._gen_log()
            m = re.search('(?P<root_url>http:.*com)\/(?P<topic>[a-z]+)\/', url)
            topic = m.group('topic')
            root_url = m.group('root_url')
            pattern = re.compile(r'href=\\\"\\\/%s\\\/thread\\\/[\!\w]+\\\"'%topic)
            try:
                html = urllib2.urlopen(url, timeout=10).read()
            except Exception,e:
                self.url_history_logger.debug('request timed item_id in url :%s'%url)
                continue
            self.url_history_logger.debug('url crawled success in %s'%url)
            item_url = pattern.findall(html)
            if not item_url:
                continue
            end_item_url = [re.sub('href|=|"|\\\\', '', item) for item in item_url]
            clear_item_url = ["".join((root_url, item)) for item in end_item_url]
            time.sleep(SLEEP_INTERVAL)

            msg_comment_set = set(self.msg_comment_list)
            if len(msg_comment_set) > 3000:
                self.write_msg_comment_into_file(msg_comment_set)
                self.msg_comment_list[:] = []

            self.item_url_con(clear_item_url)
sohu = SohuSpider()
sohu.main()