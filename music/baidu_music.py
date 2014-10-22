__author__ = 'huafeng'
#coding:utf-8
import os
import re
import urllib2
import codecs
import time
import random
import logging
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
SLEEP_INTERVAL = random.randint(2, 5)
class BaiduMusic:
    def __init__(self):
        self.valid_item_url_list = []
        self.music_content_list = []
        self.crawled_url_list = []
        self._gen_log()
        self._load_crawled_file()

    def _load_crawled_file(self):
        filename = os.path.join(PATH, 'sys', 'crawled_item_url')
        with codecs.open(filename, encoding='utf-8') as f:
            self.crawled_url_list.extend([item.strip() for item in f.readlines()])

    def _gen_log(self):
        logfile = os.path.join(PATH, 'log', 'baidu_music_cralwer.log')
        self.logger = logging.getLogger(__name__)
        self.logger.setLevel(logging.DEBUG)
        log_file = logging.FileHandler(logfile)
        log_file.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
        log_file.setFormatter(formatter)
        self.logger.addHandler(log_file)

    def gen_item_url(self):
        url_root = 'http://music.baidu.com/'
        url_compile_filter = re.compile(r'/song/[\d]+')

        page_url_list = ["http://music.baidu.com/top/dayhot","http://music.baidu.com/top/new"]
        for page_url in page_url_list:
            try:
                html = urllib2.urlopen(page_url, timeout=15).read()
            except:
                time.sleep(40)
                try:
                    html = urllib2.urlopen(page_url, timeout=15).read()
                except:
                    self.logger.debug('request timed item_id in page_url:%s'%url)
                    continue
            time.sleep(SLEEP_INTERVAL)
            soup = BeautifulSoup(html)
            span_level_list = soup.find_all('span', class_='song-title')
            if not span_level_list:
                self.logger.error('span_level do not match regular expression in page_url:%s'%url)
            whole_url_list = [item.a['href'] for item in span_level_list]
            item_url_list = ["".join((url_root, url)) for url in whole_url_list if url_compile_filter.match(url)]
            self.valid_item_url_list.extend(item_url_list)

    def parse_item_url(self):
        valid_url_list = [url for url in self.valid_item_url_list if not url in self.crawled_url_list]
        for url in valid_url_list:
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                time.sleep(20)
                try:
                    html = urllib2.urlopen(url, timeout=15)
                except:
                    self.logger.debug('request timed item_id in item_url:%s'%url)
                    continue
            self.crawled_url_list.append(url)
            soup = BeautifulSoup(html)
            div_level_str = soup.find('div', id='lyricCont')
            if not div_level_str:
                self.logger.error('div_level_str do not match re in item_url:%s'%url)
                continue
            # print div_level_str.text
            self.music_content_list.append(div_level_str.text)
            time.sleep(SLEEP_INTERVAL)

    def write_music_content_into_file(self):
        timestamp = time.strftime('%Y_%m_%d_%H%M%S_baidu_music.txt')
        filename = os.path.join(PATH, 'out', timestamp)
        with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
            music_conten_list = ["".join((item, '\n')) for item in self.music_content_list]
            wf.writelines(music_conten_list)

    def re_write_crawled_url_file(self):
        filename = os.path.join(PATH, 'sys', 'crawled_item_url')
        crawled_url_list = ["".join((url, '\n')) for url in self.crawled_url_list]
        with codecs.open(filename, 'wb', encoding='utf-8') as wf:
            wf.writelines(crawled_url_list)

    def main(self):
        self.gen_item_url()
        self.parse_item_url()
        self.write_music_content_into_file()
        self.music_content_list[:] = []
        if len(self.crawled_url_list) > 1000:
            del self.crawled_url_list[:200]
        self.re_write_crawled_url_file()

if __name__ == "__main__":
    musicer = BaiduMusic()
    musicer.main()



