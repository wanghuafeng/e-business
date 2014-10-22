__author__ = 'huafeng'
#coding:utf-8
import os
import re
import codecs
import time
import urllib2
from bs4 import BeautifulSoup
PATH = os.path.dirname(os.path.abspath(__file__))

def gen_whole_topic_url():
    root_url_pattern = "http://category.dangdang.com%s\n"
    url = 'http://category.dangdang.com/cp01.00.00.00.00.00.html'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html,'html5lib')
    div_level_str = soup.find('div', id='leftCate')
    span_level_list = re.findall(r'<a class="t" href="(.*?\.html)#', div_level_str.encode('utf-8'))
    topic_url_list = [root_url_pattern%item for item in span_level_list]
    print topic_url_list, len(topic_url_list)

    # topic_url_filename = os.path.join(PATH, 'page_url', 'topic_url')
    # with codecs.open(topic_url_filename, mode='wb', encoding='utf-8')as wf:
    #     wf.writelines(topic_url_list)
# gen_whole_topic_url()
def parse_topic_url_from_html():
    div_level_str = '''<span class="m" name="C2"></span>
                            <a class="t" href="/cp01.05.00.00.00.00.html#ddclick?act=clickcat&amp;pos=0_0_0_p&amp;cat=01.00.00.00.00.00&amp;key=&amp;qinfo=&amp;pinfo=4874221_1_72&amp;minfo=&amp;ninfo=&amp;custid=&amp;permid=&amp;ref=&amp;rcount=&amp;type=&amp;t=1402284097000" title="文学">'''
    print re.search(r'<a class="t" href="(.*?\.html)#', div_level_str).group(1)
# parse_topic_url_from_html()
def read_one_topic_url_to_get_root_page_url():
    root_url_pattern = 'http://category.dangdang.com%s\n'
    topic_url = 'http://category.dangdang.com/cp01.11.00.00.00.00.html'
    html = urllib2.urlopen(topic_url).read()
    soup = BeautifulSoup(html, 'html5lib')
    div_level_str = soup.find('div', id='leftCate')
    span_level_list = re.findall(r'<a href="(.*?\.html)#', div_level_str.encode('utf-8'))
    # print span_level_list, len(span_level_list)
    root_url_list = [root_url_pattern%item for item in span_level_list]
    print root_url_list, len(root_url_list)
# read_one_topic_url_to_get_root_page_url()

def write_root_url_into_file(root_url_list):
    root_url_filename = os.path.join(PATH, 'book/page_url', 'root_page_url')
    with codecs.open(root_url_filename, mode='a', encoding='utf-8') as af:
        af.writelines(root_url_list )
def read_topic_url_to_get_root_page_url():
    root_url_pattern = 'http://category.dangdang.com%s\n'
    topic_url_list_filename = os.path.join(PATH, 'book/page_url', 'topic_url')
    with codecs.open(topic_url_list_filename, encoding='utf-8')as f:
        count = 0
        for topic_url in f.readlines():
            count += 1
            html = urllib2.urlopen(topic_url).read()
            soup = BeautifulSoup(html, 'html5lib')
            div_level_str = soup.find('div', id='leftCate')
            span_level_list = re.findall(r'<a href="(.*?\.html)#', div_level_str.encode('utf-8'))
            root_url_list = [root_url_pattern%item for item in span_level_list]
            print count, len(span_level_list)
            write_root_url_into_file(root_url_list)
# read_topic_url_to_get_root_page_url()
def read_root_get_pagesize():
    url = 'http://category.dangdang.com/cp01.50.02.00.00.00.html'
    page_url_pattern = url.replace(r'com/', r'com/pg%s-')
    # print page_url_pattern
    html = urllib2.urlopen(url).read()
    page_size = re.search(r'<span class="or">.*?/(\d+)</span>', html).group(1)
    page_url_list = []
    for page_num in range(1, int(page_size)+1):
        if page_num == 1:
            page_url_list.append(url)
            continue
        page_url = page_url_pattern%page_num
        page_url_list.append(page_url)
    print page_url_list
# read_root_get_pagesize()
def write_page_url_into_file(page_url_list):
    whole_page_url_filename = os.path.join(PATH, 'book/page_url', 'whole_page_url')
    with codecs.open(whole_page_url_filename, mode='a', encoding='utf-8') as af:
        af.writelines(page_url_list)
def gen_whole_page_url():
    root_page_url_filename = os.path.join(PATH, 'book/page_url', 'root_page_url')
    with codecs.open(root_page_url_filename, encoding='utf-8') as f:
        count = 0
        for root_page_url in f.readlines():
            count += 1
            page_url_pattern = root_page_url.replace(r'com/', r'com/pg%s-')
            html = urllib2.urlopen(root_page_url).read()
            page_size = re.search(r'<span class="or">.*?/(\d+)</span>', html).group(1)
            page_url_list = []
            for page_num in range(1, int(page_size)+1):
                if page_num == 1:
                    page_url_list.append(root_page_url)
                    continue
                page_url = page_url_pattern%page_num
                page_url_list.append(page_url)
            write_page_url_into_file(page_url_list)
            print count
# gen_whole_page_url()
def read_one_page_url_to_item_id():
    item_id_url_pattern = re.compile('href="(http://product.dangdang.com/\d+\.html)#')
    url = "http://category.dangdang.com/pg6-cp01.70.01.00.00.00.html"
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html, 'html5lib')
    div_level_list = soup.find_all('div', class_='inner')
    item_url_list = [item_id_url_pattern.search(item.encode('utf-8')).group(1) for item in div_level_list]
    print item_url_list
# read_one_page_url_to_item_id()

def read_whole_page_url_to_get_item_id():
    item_id_url_pattern = re.compile('href="(http://product.dangdang.com/\d+\.html)#')
    whole_page_url_filename = os.path.join(PATH, 'book/page_url', 'whole_page_url')
    item_id_url_filename = os.path.join(PATH, 'book/page_url', 'item_id_url')
    crawled_page_url_filename = os.path.join(PATH, 'book/log', 'crawled_page_url')
    failed_page_url_filename = os.path.join(PATH, 'book/log', 'failed_page_url')
    with codecs.open(whole_page_url_filename, encoding='utf-8') as whole_page_url_f,\
    codecs.open(crawled_page_url_filename, mode='wb', encoding='utf-8') as crawled_page_url_wf,\
    codecs.open(failed_page_url_filename, mode='wb', encoding='utf-8') as failed_page_url_wf,\
    codecs.open(item_id_url_filename, mode='wb', encoding='utf-8') as item_id_url_wf:
        for page_url in whole_page_url_f.readlines():
            try:
                html = urllib2.urlopen(page_url, timeout=30).read()
            except:
                try:
                    html = urllib2.urlopen(page_url, timeout=30).read()
                except:
                    try:
                        html = urllib2.urlopen(page_url, timeout=30).read()
                    except:
                        failed_page_url_wf.write('timed item_id in url;%s\n'%page_url)
                        continue
            soup = BeautifulSoup(html, 'html5lib')
            div_level_list = soup.find_all('div', class_='inner')
            item_url_list = []
            try:
                item_url_list = [item_id_url_pattern.search(item.encode('utf-8')).group(1) for item in div_level_list]
            except:
                failed_page_url_wf.write('not match pattern in url;%s'%page_url)
            if not item_url_list:
                failed_page_url_wf.write('div do not match pattern in url;%s'%page_url)
                continue
            temp_item_url_list = [item+'\n' for item in item_url_list]
            item_id_url_wf.writelines(temp_item_url_list)
            # print temp_item_url_list
            crawled_page_url_wf.write(page_url)
# read_whole_page_url_to_get_item_id()
def read_failed_url():
    item_id_url_pattern = re.compile('href="(http://product.dangdang.com/\d+\.html)#')
    failed_page_url_filename = os.path.join(PATH, 'log', 'failed_page_url')
    crawled_failed_page_url = os.path.join(PATH, 'log', 'crawled_failed_page_url')
    item_id_url = os.path.join(PATH, 'sys', 'item_id_url')
    with codecs.open(failed_page_url_filename, encoding='utf-8') as failed_page_url_f,\
    codecs.open(crawled_failed_page_url, mode='wb', encoding='utf-8') as crawled_failed_wf,\
    codecs.open(item_id_url, mode='a', encoding='utf-8') as item_id_af:
        for url in [item.split(';')[1] for item in failed_page_url_f.readlines()]:
            html = urllib2.urlopen(url).read()
            soup = BeautifulSoup(html, 'html5lib')
            div_level_list = soup.find_all('div', class_='inner')
            item_url_list = [item_id_url_pattern.search(item.encode('utf-8')).group(1) for item in div_level_list]
            temp_item_url_list = [item+'\n' for item in item_url_list]
            item_id_af.writelines(temp_item_url_list)
            crawled_failed_wf.write(url)
            print url.strip()
read_failed_url()