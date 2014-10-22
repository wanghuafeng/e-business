__author__ = 'huafeng'

import os
import re
import time
import codecs
import logging
import urllib2
import random
from math import ceil
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
SLEEP_INTERVAL = random.randint(2, 5)

class MovieActor:
    def __init__(self):
        self.proxy_list = []
        self._gen_proxy()
        self.actor_content_text_list = []
        self.timeout_url_list = []

    def _gen_log(self):
        logfile = os.path.join(PATH, 'log', 'douban_actor_cralwer.log')
        self.logger = logging.getLogger(__name__)
        self.logger.setLevel(logging.DEBUG)
        log_file = logging.FileHandler(logfile)
        log_file.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
        log_file.setFormatter(formatter)
        self.logger.addHandler(log_file)

    def _gen_proxy(self):
        filename = os.path.join(PATH, 'sys', 'xici_proxy')
        with codecs.open(filename, encoding='utf-8') as f:
            self.proxy_list.extend([item.strip() for item in f.readlines()])

    def parse_actor_content_url(self, url):
        try:
            html = urllib2.urlopen(url, timeout=15).read()
        except:
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                self.timeout_url_list.append(url)
                self.logger.debug('request timeout in item_url:%s'%url)
                return
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', id='content')
        if not div_level_str:
            self.logger.error('div_level do not match regular expression in url:%s'%url)
            return
        actor_summary = div_level_str.find('div', class_='bd') if not div_level_str.find('span', class_='all hidden') else div_level_str.find('span', class_='all hidden')
        if not actor_summary:
            self.logger.debug('actor_summary do not match re in item_url:%s'%url)
        actor_content_text = actor_summary.text.strip()
        if actor_content_text:
            self.actor_content_text_list.append(actor_content_text)

    def write_content_into_file(self):
        timestamp = time.strftime('%Y_%m_%d_%H%M%S.txt')
        filename = os.path.join(PATH, 'out', 'actor_con_out', timestamp)
        actor_content_text_list = ["".join((item, '\n')) for item in self.actor_content_text_list]
        with codecs.open(filename, mode='a', encoding='utf-8') as wf:
            wf.writelines(actor_content_text_list)

    def write_timeout_url(self):
        filename = os.path.join(PATH, 'log', 'actor_info_timeout_url')
        timeout_url_list = [item+'\n' for item in self.timeout_url_list]
        with codecs.open(filename, mode='a', encoding='utf-8') as f:
            f.writelines(timeout_url_list)

    def main(self):
        range_from = 1000000
        range_to = 1339959
        url_pattern = 'http://movie.douban.com/celebrity/%d/'
        url_list = [url_pattern%item for item in range(range_from, range_to+1)]
        # print len(url_list), url_list[-1]
        url_count = range_to - range_from
        con_count_write_in_file = 2000
        file_count = url_count/2000#338000
        for file_point in range(file_count):
            for url_point in range(file_point*con_count_write_in_file, (file_point+1)*con_count_write_in_file):
                url = url_list[url_point]
                self.parse_actor_content_url(url)
                time.sleep(SLEEP_INTERVAL)
            self.write_content_into_file()
            self.actor_content_text_list[:] = []
            if self.timeout_url_list:
                self.write_timeout_url()

        url_left = url_count - file_count*con_count_write_in_file#339959-338000
        for i in range(1,url_left+1):
            url = url_list[-i]
            self.parse_actor_content_url(url)
            time.sleep(SLEEP_INTERVAL)
        self.write_content_into_file()
        self.actor_content_text_list[:] = []
        if self.timeout_url_list:
            self.write_timeout_url()

if __name__ == '__main__':
    actor = MovieActor()
    actor.main()