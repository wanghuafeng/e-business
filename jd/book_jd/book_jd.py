__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
import xici_proxy
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def read_proxy_file():
    proxy_list = []
    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with codecs.open(filename, encoding='utf-8')as f:
        proxy_list.extend([item.strip() for item in f.readlines()])
    return proxy_list
def gen_whole_item_id():
    timeout_timestamp = time.strftime('%m%d_timeout_page_url')
    crawled_timestamp = time.strftime('%m%d_crawled_page_url')
    proxy_list = read_proxy_file()
    page_url_proxy_count = 0
    if not proxy_list:
        xici_proxy.gen_proxy()
        proxy_list = read_proxy_file()
    ip_port = proxy_list.pop()
    handle_no_div_pattern = re.compile('no_(item|plist)_div:')
    whole_page_url_filename = os.path.join(PATH, 'log', '0518_timeout_page_url')
    timeout_page_url_filename = os.path.join(PATH, 'log', timeout_timestamp)
    item_id_filename = os.path.join(PATH, 'sys', 'book_item_ids')
    page_url_crawled_filename = os.path.join(PATH, 'log', crawled_timestamp)
    with codecs.open(whole_page_url_filename, encoding='utf-8')as whole_page_url_f,\
    codecs.open(item_id_filename, mode='a', encoding='utf-8')as item_id_wf,\
    codecs.open(timeout_page_url_filename, mode='wb', encoding='utf-8') as timeout_url_wf,\
    codecs.open(page_url_crawled_filename, mode='wb', encoding='utf-8')as crawled_url_wf:
        for page_url in [handle_no_div_pattern.sub('', item.strip()) for item in whole_page_url_f.readlines() if item.startswith('no_')]:
            page_url_proxy_count += 1
            try:
                if page_url_proxy_count > 2000:
                    if not proxy_list:
                        re_read_proxy_list = read_proxy_file()
                        proxy_list = xici_proxy.get_valid_proxy(re_read_proxy_list)
                        if not proxy_list:
                            xici_proxy.gen_proxy()
                            timeout_url_wf.write('get new proxy in xici network!\n')
                            proxy_list = read_proxy_file()
                        ip_port = proxy_list.pop()
                        page_url_proxy_count = 0
                http_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
                opener = urllib2.build_opener(http_hanlder)
                html = opener.open(page_url, timeout=15)
            except urllib2.HTTPError, e:
                if e.getcode() == 403:
                    timeout_url_wf.write('403 error:request forbiddon!!!\n')
                    if not proxy_list:
                        re_read_proxy_list = read_proxy_file()
                        proxy_list = xici_proxy.get_valid_proxy(re_read_proxy_list)
                        if not proxy_list:
                            xici_proxy.gen_proxy()
                            timeout_url_wf.write('get new proxy in xici network!\n')
                            proxy_list = read_proxy_file()
                        ip_port = proxy_list.pop()
                    http_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
                    opener = urllib2.build_opener(http_hanlder)
                    html = opener.open(page_url, timeout=15).read().decode('gbk')
                else:
                    continue
            except:
                timeout_info = ''.join(('request_timeout:', page_url, '\n'))
                timeout_url_wf.write(timeout_info)
                continue
            soup = BeautifulSoup(html)
            div_level_str = soup.find('div', id='plist')
            if not div_level_str:
                error_match_info = ''.join(('no_plist_div:', page_url, '\n'))
                timeout_url_wf.write(error_match_info)
                continue
            div_item_list = div_level_str.find_all('div', class_='item')
            if not div_item_list:
                error_match_info = ''.join(('no_item_div:', page_url, '\n'))
                timeout_url_wf.write(error_match_info)
                continue
            item_id_list = [item['sku']+'\n' for item in div_item_list]
            item_id_wf.writelines(item_id_list)
            crawled_url_wf.write(page_url+'\n')
            # time.sleep(3)
gen_whole_item_id()
