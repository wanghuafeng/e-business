__author__ = 'huafeng'
#coding:utf-8
import urllib2
import os
import re
import codecs
import time
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
class CrawlFreq:

    def __init__(self):
        self.words_list = []
        self._gen_words_list()

    def _gen_words_list(self):
        filename = os.path.join(PATH, 'sys', 'wrod_without_freq.txt')
        with codecs.open(filename) as f:
            temp_word_list = [item.strip() for item in f.readlines()]
            self.words_list.extend(temp_word_list)

    def read_item_url(self):
        timeout_url_list = []
        url_pattern = 'http://www.baidu.com/s?wd="%s"&fr=wenku'
        num_pattern = re.compile(r'\d+')
        filename = os.path.join(PATH, 'out', 'words_freq_0507')
        with codecs.open(filename, mode='wb')as f:
            for word in self.words_list:
                url = url_pattern%word
                try:
                    html = urllib2.urlopen(url, timeout=15).read()
                except:
                    try:
                        html = urllib2.urlopen(url, timeout=15).read()
                    except:
                        timeout_url_list.append(url)
                        continue
                soup = BeautifulSoup(html)
                span_level_str_list = soup.find_all('span', class_='nums')
                if span_level_str_list:
                    span_level_str = span_level_str_list[0]
                    num_text = span_level_str.get_text()
                    num = "".join(num_pattern.findall(num_text))
                    com_str = "\t".join((word, str(num))) + '\n'
                else:
                    com_str = "\t".join((word, "0")) + '\n'
                f.write(com_str)
                time.sleep(2)

        if timeout_url_list:
            timeout_filename = os.path.join(PATH, 'timeout_url')
            with codecs.open(timeout_filename, mode='wb') as wf:
                timeout_url_list = [item+'\n' for item in timeout_url_list]
                wf.writelines(timeout_url_list)
# freq_crawler = CrawlFreq()
# freq_crawler.read_item_url()

def use_num_pattern():
    s = "百度为您找到相关结果约8,080,000个"
    num_list = re.findall(r'\d+',s)
    num = "".join(num_list)
    print num
def read_unformal_page():
    url = 'http://www.baidu.com/s?wd="一字一键章鱼输入"&fr=wenku'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    span_level_str_list = soup.find_all('span', class_='nums')
    print span_level_str_list
# read_unformal_page()
def get_omit_word():
    source_filename = os.path.join(PATH, 'sys', 'word_without_freq_0528.txt')
    with codecs.open(source_filename) as f:
        word_set = set([item.strip() for item in f.readlines()])
        # for word in word_set:
        #     print type(word)

    des_filename = os.path.join(PATH, 'out', 'words_freq_0528')
    with codecs.open(des_filename) as rf:
        word_list = set([item.split('\t')[0] for item in rf.readlines()])

    # print len(word_list), len(word_set)
    # param = word_set-word_list
    # print list(param)[0].decode('gbk')

    for word in word_set:
        if word not in word_list:
            print word.decode('gbk')
# get_omit_word()
def check_zero_num():
    filename = os.path.join(PATH, 'out', 'words_freq_0528')
    with open(filename) as f:
        zero_freq_list = []
        for line in f.readlines():
            splited_line = line.split('\t')
            word = splited_line[0]
            freq = splited_line[1].strip()
            if freq == '0':
                print line.decode('gbk')
                zero_freq_list.append(word.decode('gbk'))
        print zero_freq_list, len(zero_freq_list)
# check_zero_num()