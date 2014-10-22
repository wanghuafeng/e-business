__author__ = 'huafeng'

import os
import re
import time
import codecs
import logging
import urllib2
import random
import gevent
import gevent.monkey
from math import ceil
from bs4 import BeautifulSoup
gevent.monkey.patch_all()

PATH = os.path.dirname(os.path.abspath(__file__))
SLEEP_INTERVAL = random.randint(2,5)

class MovieActor:
    def __init__(self):
        self.proxy_list = []
        self._gen_proxy()
        self.actor_content_text_list = []
        self.timeout_url_list = []
    
    def _gen_proxy(self):
        filename = os.path.join(PATH, 'sys', 'xici_proxy')
        with codecs.open(filename, encoding='utf-8') as f:
            self.proxy_list.extend([item.strip() for item in f.readlines()])

    def parse_actor_content_url(self, url, ip_port):
        # http_proxy = 'http://%s'%ip_port
        # proxy_hanlder = urllib2.HTTPHandler({'http':http_proxy})
        # opener = urllib2.build_opener(proxy_hanlder)
        # urllib2.install_opener(opener)

        try:
            html = urllib2.urlopen(url, timeout=15).read()
        except:
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                self.timeout_url_list.append(url)
                return
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', id='content')
        if not div_level_str:
            return
        actor_summary = div_level_str.find('div', class_='bd') if not div_level_str.find('span', class_='all hidden') else div_level_str.find('span', class_='all hidden')
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
        proxy_count = len(self.proxy_list)
        thread_count = 60
        range_from = 1000000
        range_to = 1339959
        url_range_start = range_from/thread_count
        url_range_stop = range_to/thread_count

        threads_per_proxy = int(ceil(thread_count/float(proxy_count)))
        threads = []
        url_pattern = 'http://movie.douban.com/celebrity/%s/'
        for post_time_count in range(url_range_start, url_range_stop):
            for url_point in range(post_time_count*thread_count, (post_time_count+1)*thread_count):
                url = url_pattern%str(url_point)
                proxy_point = (url_point - post_time_count*thread_count)/threads_per_proxy
                ip_port = self.proxy_list[proxy_point]
                threads.append(gevent.spawn(self.parse_actor_content_url, url, ip_port))
            gevent.joinall(threads)
            self.write_content_into_file()
            self.actor_content_text_list[:] = []
            if self.timeout_url_list:
                self.write_timeout_url()
                self.timeout_url_list[:] = []
            time.sleep(SLEEP_INTERVAL)
        left_threads = []
        url_left_count = range_to % thread_count
        for url_point in range(url_left_count):
            url = url_pattern%str(range_to-url_point)
            proxy_point = url_point/threads_per_proxy
            ip_port = self.proxy_list[proxy_point]
            left_threads.append(gevent.spawn(self.parse_actor_content_url, url, ip_port))
        gevent.joinall(left_threads)
        self.write_content_into_file()
        self.actor_content_text_list[:] = []
if __name__ == '__main__':
    actor = MovieActor()
    actor.main()