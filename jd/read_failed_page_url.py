__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2

end_url_with_pagesize = True
ip_port = '218.207.195.206:80'
PATH = r'/mnt/data/spiders/jd/health'
ITEM_FILENAME = 'freshfood_item_id'

def get_whole_item_id():
    http_handler = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    opener = urllib2.build_opener(http_handler)
    urllib2.install_opener(opener)
    first_failed_url_filename = os.path.join(PATH, 'log', 'failed_item_id')
    crawled_page_url_filename = os.path.join(PATH, 'log', 're_crawled_page_url')
    failed_page_url_filename = os.path.join(PATH, 'log', 're_failed_page_url')
    item_id_filename = os.path.join(PATH, 'page_url', ITEM_FILENAME)
    with codecs.open(first_failed_url_filename, encoding='utf-8') as first_failed_url_f,\
    codecs.open(crawled_page_url_filename, mode='wb', encoding='utf-8') as crawled_page_wf, \
    codecs.open(failed_page_url_filename, mode='wb', encoding='utf-8') as failed_page_url_wf, \
    codecs.open(item_id_filename, mode='a', encoding='utf-8') as item_id_wf:
        for page_url in [item.split(';')[1].strip() for item in first_failed_url_f.readlines()]:
            try:
                html = urllib2.urlopen(page_url.strip(), timeout=15).read()
            except:
                try:
                    html = urllib2.urlopen(page_url.strip(), timeout=15).read()
                except:
                    failed_page_url_wf.write('timeout in url;%s'%page_url)
                    continue
            if end_url_with_pagesize:
                item_id_list = re.findall(r'''sku=\\"(\d+)\\" selfservice''', html)
            else:
                item_id_list = re.findall(r"sku='(\d+)'><div class=", html)
            if not item_id_list:
                failed_page_url_wf.write('not match id_str in url;%s'%page_url)
                continue
            temp_list_for_write = [item+'\n' for item in item_id_list]
            # item_id_wf.writelines(temp_list_for_write)
            # crawled_page_wf.write(page_url)
            print temp_list_for_write
            time.sleep(3)
get_whole_item_id()