__author__ = 'huafeng'
import os
import re
import time
import urllib2
import codecs

PATH = os.path.dirname(os.path.abspath(__file__))

def main():
    item_url_filename = os.path.join(PATH, 'sys', 'total_item_url')
    html_failed_filename = os.path.join(PATH, 'log', 'html_failed_url')
    with codecs.open(item_url_filename, encoding='utf-8') as total_item_url_f,\
    codecs.open(html_failed_filename, mode='wb', encoding='utf-8')as failed_wf:
        url_list = [item.strip() for item in set(total_item_url_f.readlines())]
        for url in url_list:
            item_id = re.search('\d+', url).group()
            try:
                html = urllib2.urlopen(url).read()
            except BaseException:
                try:
                    html = urllib2.urlopen(url).read()
                except BaseException:
                    try:
                        html = urllib2.urlopen(url).read()
                    except BaseException:
                        failed_wf.write(url+'\n')
                        continue
            html_filename = os.path.join(PATH, 'html', '%s.html'%str(item_id))
            with open(html_filename, mode='wb') as wf:
                wf.write(html)
            time.sleep(3)
main()

def read_one_item_url():
    url = 'http://movie.douban.com/subject/22265121/'
    item_id = re.search('\d+', url).group()
    filename = os.path.join(PATH, '%s.html'%item_id)
    html = urllib2.urlopen(url).read()
    with codecs.open(filename, mode='wb') as wf:
        wf.write(html)
# read_one_item_url()