__author__ = 'huafeng'
#coding:utf-8
import re
import os
import time
import urllib2
import random
import codecs
import logging
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
SLEEP_INTERVAL = random.randint(5,10)

class DoubanCrawler:

    def __init__(self):
        self.crawled_url_list = []#已抓取的url
        self.timeout_url_list = []
        self.title_actors_list = []
        self.movie_summary_list = []
        self._load_crawled_url()
        self._gen_logger()


    def _gen_logger(self):
        logfile = os.path.join(PATH, 'log', 'crawler_douban.log')
        self.logger = logging.getLogger('douban')
        self.logger.setLevel(logging.DEBUG)
        log_file = logging.FileHandler(logfile)
        log_file.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        log_file.setFormatter(formatter)
        self.logger.addHandler(log_file)

    def _load_crawled_url(self):
        filename = os.path.join(PATH, 'sys', 'douban_crawled_urls')
        with codecs.open(filename, encoding='utf-8') as f:
            self.crawled_url_list.extend([item.strip() for item in f.readlines()])

    def gen_comming_movie_list(self):
        #抓取将要上映的没有被抓取过的电影url
        url = 'http://movie.douban.com/coming'
        try:
            html = urllib2.urlopen(url, timeout=20).read()
        except:
            time.sleep(120)
            try:
                html = urllib2.urlopen(url, timeout=20).read()
            except:
                return
        soup = BeautifulSoup(html)
        tbody_level_str = soup.find('tbody')
        if not tbody_level_str:
            raise ValueError('tbody_level_str do not match pattern in root url:%s'%url)
        tr_level_list = tbody_level_str.find_all('tr')
        url_list = [item.find('a')['href'] for item in tr_level_list]
        # print url_list, len(url_list)#77
        #没有抓取过的url
        no_crawled_url_list = [item for item in url_list if item.encode('utf-8') not in set(self.crawled_url_list)]
        return no_crawled_url_list

        # #将url写入到文件中
        # filename = os.path.join(PATH, 'page_url', 'douban_crawled_urls')
        # url_list = ["".join((item, '\n'))  for item in url_list if item not in set(self.crawled_url_list)]
        # with codecs.open(filename, mode='a', encoding='utf-8') as wf:
        #     wf.writelines(url_list)

    def gen_nowplaying_movie_url(self):
        url = 'http://movie.douban.com/nowplaying/beijing/'
        try:
            html = urllib2.urlopen(url, timeout=20).read()
        except:
            time.sleep(120)
            try:
                html = urllib2.urlopen(url, timeout=20).read()
            except:
                return
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', id='nowplaying')
        if not div_level_str:
            raise ValueError('div_level_str do not match pattern in root url:%s'%url)
        li_level_list = div_level_str.find_all('li', class_='stitle')
        url_list = [item.a['href'] for item in li_level_list]
        no_crawled_url_list = [item for item in url_list if item.encode('utf-8') not in set(self.crawled_url_list)]
        return no_crawled_url_list

        # print nowplaying_url_list, len(nowplaying_url_list)
        #将url写入到文件中
        # filename = os.path.join(PATH, 'page_url', 'douban_crawled_urls')
        # url_list = ["".join((item, '\n'))  for item in url_list if item not in set(self.crawled_url_list)]
        # with codecs.open(filename, mode='a', encoding='utf-8') as wf:
        #     wf.writelines(url_list)

        # self.crawl_movie_content(nowplaying_url_list)

    def write_con_into_file(self):
        filename = time.strftime('realtime_movie_info_%Y_%m_%d.txt')
        title_actor_file = os.path.join(PATH, 'out', filename)
        title_actor_list = ["".join((item, '\n')) for item in self.title_actors_list]
        with codecs.open(title_actor_file, mode='a', encoding='utf-8') as wf:
            wf.writelines(title_actor_list)

        movie_summary_filename = os.path.join(PATH, 'out', filename)
        movie_summary_list = [''.join((item, '\n')) for item in self.movie_summary_list]
        with codecs.open(movie_summary_filename, mode='a', encoding='utf-8') as movief:
            movief.writelines(movie_summary_list)

    def re_write_crawled_url(self):
        filename = os.path.join(PATH, 'sys', 'douban_crawled_urls')
        with codecs.open(filename, mode='wb', encoding='utf-8') as f:
            crawled_url_list = ["".join((item, '\n')) for item in self.crawled_url_list]
            f.writelines(crawled_url_list)

    def crawl_movie_content(self, url_list):
        for url in url_list:
            url = url.strip()
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                self.logger.info('request timed item_id in url:%s'%url)
                continue

            soup = BeautifulSoup(html)

            #电影名称
            try:
                title = soup.find('span', property='v:itemreviewed').text
            except:
                raise ValueError('title do not match the pattern in url:%s'%url)
            self.title_actors_list.append(title)
            # print title

            #演员名称
            div_level_str = soup.find('div', id='info')
            if not div_level_str:
                raise ValueError('div_level_str do not match pattern in url:%s'%url)
            a_level_list = div_level_str.find_all('a')
            actors_list = [item.text for item in a_level_list]
            self.title_actors_list.extend(actors_list)

            #电影简介
            con_div_level_str = soup.find('div', id='link-report')
            if not con_div_level_str:
                self.logger.error('con_div_level_str do not match pattern in url:%s'%url)
                continue
            #页面没有展开信息
            further_content = con_div_level_str.select('span[class=""]')
            if further_content:
               movie_content = further_content[0].text
            else:
                movie_content = con_div_level_str.find('span', class_='all hidden').text
            self.movie_summary_list.append(movie_content.strip())
            # print movie_content

            self.crawled_url_list.append(url)

            time.sleep(SLEEP_INTERVAL)

        #把标题、演员、简介写入到文件(title_actors_, movie_summary_)中
        self.write_con_into_file()
        self.title_actors_list[:] = []
        self.movie_summary_list[:] = []

        #把成功抓取的url写入到已抓取文件(douban_crawled_urls)中
        if len(self.crawled_url_list) > 800:
            del self.crawled_url_list[:200]
        self.re_write_crawled_url()

    def main(self):
        com_url_list = []
        nowplaying_movie_url_list = self.gen_nowplaying_movie_url()
        if nowplaying_movie_url_list:
            com_url_list.extend(nowplaying_movie_url_list)
        comming_movie_url_list = self.gen_comming_movie_list()
        if comming_movie_url_list:
            com_url_list.extend(comming_movie_url_list)
        assert com_url_list is not []
        self.crawl_movie_content(com_url_list)
if __name__ == "__main__":
    douban = DoubanCrawler()
    douban.main()
