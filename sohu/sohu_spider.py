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
SLEEP_INTERVAL = random.randint(5, 8)

class SohuSpider(object):

    def __init__(self):
        self.msg_comment_list = []
        self._gen_log()

    def _gen_log(self):
        timestamp = time.strftime("%Y_%m_%d")
        filename = "".join((timestamp, '_sohu_spider.log'))
        logfile = os.path.join(PATH, 'log', filename)
        self.logger = logging.getLogger(__name__)
        self.logger.setLevel(logging.DEBUG)
        log_file = logging.FileHandler(logfile)
        log_file.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        log_file.setFormatter(formatter)
        self.logger.addHandler(log_file)

    def realtime_page_urls(self):
        page_url_list = []
        filename = os.path.join(PATH, 'sys', 'realtime_page_url')
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
                except :
                    self.logger.error("request timed item_id in url:%s"%url)

                comment_con_list = re.findall(r'\<table class=\\\"viewpost\\\".*\<\\\/table\>', html)
                if not comment_con_list:
                    self.logger.debug('html here do not match regular expression,url:%s'%url)
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
                                if multi_comment_page_size == 1:
                                    continue
                                elif int(multi_comment_page_size) <= 3:
                                    start_range = 2
                                    end_range = int(multi_comment_page_size)+1
                                else:
                                    start_range = int(multi_comment_page_size)-2
                                    end_range = int(multi_comment_page_size) + 1

                                for i in range(start_range,end_range):
                                    new_end_url = "/p%s"%str(i)
                                    multi_comment_url = "".join((url, new_end_url))
                                    multi_url_list.append(multi_comment_url)
                    #抓取多页评论
                    if not multi_url_list:
                        continue
                    self.multi_commet_pages(multi_url_list)

    def multi_commet_pages(self, comment_url_list):
        for url in comment_url_list:
            # url = "http://club.news.sohu.com/minjian/thread/z9xt2y19xy/p5"
            try:
                html = urllib2.urlopen(url, timeout=10).read()
            except:
                self.logger.error("request timed item_id in url:%s"%url)
                continue
            comment_con_list = re.findall(r'\<table class=\\\"viewpost\\\".*\<\\\/table\>', html)
            if not comment_con_list:
                self.logger.debug('html here do not match regular expression,url:%s'%url)
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
        confile = "".join((timestamp, '_sohu_realtime.txt'))
        output_filename = os.path.join(PATH, 'out', confile)
        with codecs.open(output_filename, mode="wb", encoding="utf-8") as wf:
            temp_list = [item+"\n" for item in msg_comment_set]
            wf.writelines(temp_list)

    def main(self):
        page_url_list = self.realtime_page_urls()
        for url in page_url_list:
            m = re.search('(?P<root_url>http:.*com)\/(?P<topic>[a-z]+)\/', url)
            topic = m.group('topic')
            root_url = m.group('root_url')
            pattern = re.compile(r'href=\\\"\\\/%s\\\/thread\\\/[\!\w]+\\\"'%topic)
            try:
                html = urllib2.urlopen(url, timeout=10).read()
            except:
                # print "crawled failure in url:%s"%url
                self.logger.error("request timed item_id in url:%s"%url)
                continue
            # print "crawled sucess in url:%s"%url
            item_url = pattern.findall(html)
            if not item_url:
                self.logger.debug('html here do not match regular expression,url:%s'%url)
                continue
            end_item_url = [re.sub('href|=|"|\\\\', '', item) for item in item_url]
            clear_item_url = ["".join((root_url, item)) for item in end_item_url]
            time.sleep(SLEEP_INTERVAL)

            msg_comment_set = set(self.msg_comment_list)
            if len(msg_comment_set) > 3000:
                self.write_msg_comment_into_file(msg_comment_set)
                self.msg_comment_list[:] = []

            self.item_url_con(clear_item_url)

if __name__ ==  "__main__":
    sohu = SohuSpider()
    sohu.main()