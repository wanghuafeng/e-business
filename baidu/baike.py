__author__ = 'huafeng'
#encoding:utf-8

import os
import re
import time
import gevent
import codecs
import random
import urllib2
import simplejson
import gevent.monkey
from bs4 import BeautifulSoup
from math import ceil
gevent.monkey.patch_all()

PATH = os.path.dirname(__file__)

class BaiKe(object):
    def __init__(self):

        self.timeout_url_list = []

    def write_timeout_url(self, url_list):
        filename = os.path.join(PATH, 'sys', 'timeout_urls')
        with codecs.open(filename, mode='a', encoding='utf-8') as f:
            url_list = [item+'\n' for item in url_list]
            f.writelines(url_list)

    def parse_html(self, url,filecount,proxy):

        content_json_dict = {}
        http_proxy = 'http://%s'%proxy
        proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
        opener = urllib2.build_opener(proxy_hanlder)
        urllib2.install_opener(opener)
        try:
            html = urllib2.urlopen(url).read()
        except:
            self.timeout_url_list.append(url)
            if len(self.timeout_url_list) > 500:
                self.write_timeout_url(self.timeout_url_list)
                self.timeout_url_list[:] = []
            return

        soup = BeautifulSoup(html)
        header_str = soup.find('div', class_='lemmaTitleH1')
        if not header_str:
            self.timeout_url_list.append(url)
            if len(self.timeout_url_list) > 500:
                self.write_timeout_url(self.timeout_url_list)
                self.timeout_url_list[:] = []
            # print 'url not match pattern:%s'%url
            return
        content_json_dict['header'] = header_str.text
        content_json_dict['url'] = url
        para_level_list = soup.find_all('div', class_='para')
        para_str_list =[para.text for para in  para_level_list]
        content_str = "".join(para_str_list)
        content_json_dict['content'] = content_str
        json_obj = simplejson.dumps(content_json_dict)
        filename = os.path.join(PATH, 'out', filecount)
        with codecs.open('%s'%filename, mode='wb', encoding='utf-8')as f:
            f.write(json_obj)
    # parse_html()

    def read_with_proxy(self, section_count):
        filename = os.path.join(PATH, 'sys', 'xici_proxy')
        with open(filename) as f:
            proxy_ip_list = f.readlines()
        proxy_ip_list = [item.strip() for item in proxy_ip_list]
        proxy_count = len(proxy_ip_list)
        # start = time.time()
        threads = []
        url_pattern = "http://baike.baidu.com/view/%s.htm"

        thread_count = 200
        threads_per_proxy = int(ceil(thread_count/float(proxy_count)))

        for i in range(1+thread_count*section_count, thread_count*(section_count+1)+1):
            url = url_pattern%str(i)
            proxy_point = (i-thread_count*section_count)/threads_per_proxy
            ip_port = proxy_ip_list[proxy_point]
            threads.append(gevent.spawn(self.parse_html, url, str(i), ip_port))
        gevent.joinall(threads,timeout=90)
        # end = time.time()
        # print "elapsed time : %d" %(end-start)

    def request_timeout_url(self, section_count, timeout_url_list):
        filename = os.path.join(PATH, 'sys', 'xici_proxy')
        with open(filename) as f:
            proxy_ip_list = f.readlines()
        proxy_ip_list = [item.strip() for item in proxy_ip_list]
        proxy_count = len(proxy_ip_list)
        threads = []
        # url_pattern = "http://baike.baidu.com/view/%s.htm"

        thread_count = 100
        threads_per_proxy = int(ceil(thread_count/float(proxy_count)))

        for i in range(thread_count*section_count, thread_count*(section_count+1)):
            # url = url_pattern%str(i)
            try:
                url = timeout_url_list[i]
            except:
                continue
            proxy_point = (i-thread_count*section_count)/threads_per_proxy
            ip_port = proxy_ip_list[proxy_point]
            threads.append(gevent.spawn(self.parse_html, url, str(i), ip_port))
        gevent.joinall(threads,timeout=90)

    def baike_content(self):
        url_count = 10000000
        threads_count = 200
        n = url_count/threads_count
        for i in range(n):
            self.read_with_proxy(i)
            time.sleep(random.randint(2,5))
        filename = os.path.join(PATH, 'sys', 'timeout_urls')
        for recheck_count in range(4):

            with open(filename) as f:
                timeout_url_list = f.readlines()

                temp_timeout_url = os.path.join(PATH, 'sys', 'temp_timeout_urls')
                with codecs.open(temp_timeout_url, mode='wb', encoding='utf-8') as wf:
                    wf.writelines(timeout_url_list)
                #清空timeout_urls文件
                with open(filename, mode='wb')as rf:
                    pass

                timeout_url_count = len(timeout_url_list)
                timeout_threads_count = 100
                m = timeout_url_count/timeout_threads_count
                for j in range(m):
                    self.request_timeout_url(j, timeout_url_list)
if __name__ == "__main__":
    baike = BaiKe()
    baike.baike_content()