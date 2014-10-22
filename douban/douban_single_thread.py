__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
import random
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
SLEEP_INTERVAL = random.randint(5,8)
class DoubanHistory:
    def __init__(self):
        self.item_url_list = []
        self.timeout_url_list = []
        self.movie_info_list = []
        self._gen_item_url()

    def _gen_item_url(self):
        filename = os.path.join(PATH, 'sys', 'total_item_url')
        with codecs.open(filename, encoding='utf-8') as f:
            self.item_url_list.extend([item.strip() for item in f.readlines()])

    def write_content_into_file(self):
        timestamp = time.strftime("%Y_%m_%d_%H%M%S.txt")
        filename = os.path.join(PATH, 'out', timestamp)
        with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
            temp_list = ["".join((item, '\n')) for item in self.movie_info_list]
            wf.writelines(temp_list)

    def main(self):
        #解析一个页面结构
        # url = 'http://movie.douban.com/subject/20326665/'
        for url in self.item_url_list:
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                time.sleep(60)
                try:
                    html = urllib2.urlopen(url, timeout=15).read()
                except:
                    self.timeout_url_list.append(url)
                    continue
            soup = BeautifulSoup(html)

            #电影名称
            try:
                title = soup.find('span', property='v:itemreviewed').text
                # print title
                self.movie_info_list.append(title)
            except:
                self.timeout_url_list.append(url)
                continue

            #演员名称
            try:
                div_level_str = soup.find('div', id='info')
                a_level_list = div_level_str.find_all('a', text=re.compile('[^\w]+'))
                actors_list = [item.text for item in a_level_list]
                # print actors_list
                self.movie_info_list.extend(actors_list)
            except:
                pass

            #电影简介
            con_div_level_str = soup.find('div', id='link-report')
            #有电影简介
            if con_div_level_str:
                #页面没有展开信息
                further_content = con_div_level_str.select('span[class=""]')
                if further_content:
                   movie_content = further_content[0].text
                else:
                    movie_content = con_div_level_str.find('span', class_='all hidden').text
                # print movie_content
                self.movie_info_list.append(movie_content.strip())

                if len(self.movie_info_list) > 5000:
                    self.write_content_into_file()
                    self.movie_info_list[:] = []
            time.sleep(SLEEP_INTERVAL)

        if self.timeout_url_list:
            filename = os.path.join(PATH, 'log', 'timeout_item_urls')
            with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
                temp_list = ["".join((item, '\n')) for item in self.timeout_url_list]
                wf.writelines(temp_list)

douban = DoubanHistory()
douban.main()