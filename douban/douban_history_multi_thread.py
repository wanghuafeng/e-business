__author__ = 'huafeng'
#coding:utf-8
import os
import re
import codecs
import urllib2
import time
import random
import logging
import gevent
import gevent.monkey
from math import ceil
from bs4 import BeautifulSoup
gevent.monkey.patch_all()

PATH = os.path.dirname(os.path.abspath(__file__))
SLEEP_INTERVAL = random.randint(5,8)
class DoubanMovie:
    def __init__(self):
        self.whole_url_list = []
        self.timeout_url_list = []
        self.proxy_list = []
        self.get_whole_url()
        self.gen_proxy()
        self._gen_logger()

    def _gen_logger(self):
        stamptime = time.strftime("crawler_douban_movie.log")
        filename = os.path.join(PATH, 'log', stamptime)
        logfilename = os.path.join(PATH, filename)
        self.logger = logging.getLogger("douban_movie")
        self.logger.setLevel(logging.DEBUG)
        fileHanlder = logging.FileHandler(logfilename)
        fileHanlder.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        fileHanlder.setFormatter(formatter)
        self.logger.addHandler(fileHanlder)

    def get_whole_url(self):
        filename = os.path.join(PATH, 'sys', 'whole_page_url')
        with codecs.open(filename, encoding='utf-8') as f:
            self.whole_url_list.extend(f.readlines())

    def gen_proxy(self):
        filename = os.path.join(PATH, 'sys', 'xici_proxy')
        with codecs.open(filename, encoding='utf-8') as f:
            self.proxy_list.extend(f.readlines())

    def write_item_url_content(self, page_content_list):
        timestamp = time.strftime('%Y_%m_%d_%H%M%S_douban_movie.txt')
        filename = os.path.join(PATH, 'out', timestamp)
        page_content_list = ["".join((item, '\n')) for item in page_content_list]
        with codecs.open(filename, mode='wb', encoding='utf-8') as f:
            f.writelines(page_content_list)

    def parse_tag_page(self, url, ip_port):
        # url = "http://movie.douban.com/tag/2011?start=20&type=T"
        http_proxy = "http://%s"%ip_port
        proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
        opener = urllib2.build_opener(proxy_hanlder)
        urllib2.install_opener(opener)
        try:
            html = urllib2.urlopen(url, timeout=30).read()
        except:
            time.sleep(60)
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                self.logger.error('request timed item_id in url:%s'%url)
                self.timeout_url_list.append(url)
                return
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', class_='article')
        if not div_level_str:
            self.logger.debug('div_level_str do not match the pattern in page_url:%s'%url)
            return
        tr_level_list = div_level_str.find_all('tr', class_='item')
        if not tr_level_list:
            return
        item_url_list = [item.find('a', class_="")['href'] for item in tr_level_list if item]

        self.crawl_item_url_with_proxy(item_url_list, ip_port)

    def crawl_item_url_with_proxy(self, item_url_list, ip_port):
        page_content_list = []
        http_proxy = 'http://%s'%ip_port
        proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
        opener = urllib2.build_opener(proxy_hanlder)
        urllib2.install_opener(opener)

        for url in item_url_list:
            try:
                html = urllib2.urlopen(url, timeout=30).read()
            except:
                time.sleep(60)
                try:
                    html = urllib2.urlopen(url, timeout=20).read()
                except:
                    self.logger.error('request timed item_id in item_url:%s'%url)
                    self.timeout_url_list.append(url)
                    continue
            soup = BeautifulSoup(html)

            #电影名称
            span_level_str = soup.find('span', property='v:itemreviewed')
            if not span_level_str:
                self.timeout_url_list.append(url)
                self.logger.debug('no title in item_url:%s'%url)
            else:
                title = span_level_str.text
                page_content_list.append(title)
                # print title

            #演员名称
            div_level_str = soup.find('div', id='info')
            if not div_level_str:
                self.logger.debug('no actors in item_url:%s'%url)
            else:
                a_level_list = div_level_str.find_all('a')
                actors_list = [item.text for item in a_level_list]
                page_content_list.extend(actors_list)

            #电影简介
            con_div_level_str = soup.find('div', id='link-report')
            if not con_div_level_str:
                self.logger.debug('no summary in item_url:%s'%url)
                continue
            #页面没有展开信息
            further_content = con_div_level_str.select('span[class=""]')
            if further_content:
               movie_content = further_content[0].text
            else:
                movie_content = con_div_level_str.find('span', class_='all hidden').text if con_div_level_str.find('span', class_='all hidden') else con_div_level_str.find('span', class_='short').text

            page_content_list.append(movie_content.strip())

            time.sleep(SLEEP_INTERVAL)

        self.write_item_url_content(page_content_list)

    def main(self):
        proxy_ip_list = [item.strip() for item in self.proxy_list]
        proxy_count = len(proxy_ip_list)

        threads = []

        thread_count = 200
        threads_per_proxy = int(ceil(thread_count/float(proxy_count)))#3

        whole_url_list = [item.strip() for item in self.whole_url_list]
        url_count = len(whole_url_list)

        #前800个page_url的处理
        url_section = url_count/thread_count#4
        for i in range(url_section):
            for url_point in range(i*thread_count, (i+1)*thread_count):
                url = whole_url_list[url_point]
                proxy_point = (url_point - thread_count*i)/threads_per_proxy
                proxy_ip_port = proxy_ip_list[proxy_point]
                threads.append(gevent.spawn(self.parse_tag_page, url, proxy_ip_port))
            gevent.joinall(threads)
            time.sleep(SLEEP_INTERVAL)

        #处理剩余page_url
        left_threads = []
        for left_page_cout in range(7):
            left_url_point = left_page_cout + 800
            url = whole_url_list[left_url_point]
            left_proxy_point = left_page_cout
            left_proxy_ip = proxy_ip_list[left_proxy_point]
            left_threads.append(gevent.spawn(self.parse_tag_page, url, left_proxy_ip))
        gevent.joinall(left_threads)

        if self.timeout_url_list:
            timeout_url_list = ["".join((item, '\n')) for item in self.timeout_url_list]
            filename = os.path.join(PATH, 'sys', 'timeout_url_list')
            with codecs.open(filename, mode='wb', encoding='utf-8') as f:
                f.writelines(timeout_url_list)

if __name__ == "__main__":
    douban = DoubanMovie()
    douban.main()
