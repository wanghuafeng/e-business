__author__ = 'huafeng'
#coding:utf-8
import urllib2
import os
import re
import codecs
import time
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def _gen_words_list():
    filename = os.path.join(PATH, '0710_words.txt')
    with codecs.open(filename) as f:
        word_list = [item.strip() for item in f.readlines()]
        # print len(word_list), word_list[1383:]
        print len(word_list)
        return word_list
# _gen_words_list()
def re_crawl_zero_freq():
    '''从out文件中读取词频为0的词，进行二次抓取'''
    word_freq_filename = os.path.join(PATH, 'out', 'words_freq_0710.txt')
    zero_freq_list = []
    with codecs.open(word_freq_filename)as f:
        for line in f.readlines():
            splited_line = line.split("\t")
            word = splited_line[0]
            freq = splited_line[1]
            if freq.strip() == '0':
                print line.strip()
                # print line.strip()
                zero_freq_list.append(word)
    print len(zero_freq_list)
    return zero_freq_list
# re_crawl_zero_freq()
def get_word_in_src_not_in_words_freq():
    '''与源文件做对比查超出，抓取失败的词'''
    filename = os.path.join(PATH, '0710_words.txt')
    word_freq_filename = os.path.join(PATH, 'out', 'words_freq_0710.txt')
    failed_crawled_words_list = []
    with codecs.open(filename) as src_f,\
    codecs.open(word_freq_filename) as wfreq_f:
        word_in_crawled_file_set = set(item.split('\t')[0] for item in wfreq_f.readlines())
        for word in [item.strip() for item in src_f.readlines()]:
            if word not in word_in_crawled_file_set:
                print word
                failed_crawled_words_list.append(word)
        print len(failed_crawled_words_list)
        return failed_crawled_words_list
# get_word_in_src_not_in_words_freq()
def read_item_url():
    count = 0
    # word_list_to_crawl = _gen_words_list()#抓取原始语料中的词汇
    # word_list_to_crawl = re_crawl_zero_freq()#二次抓取，词频为0的词汇
    word_list_to_crawl = get_word_in_src_not_in_words_freq()#对原始语料中timeout的词汇进行二次抓取
    url_pattern = 'http://www.baidu.com/s?wd="%s"&fr=wenku'
    num_pattern = re.compile(r'\d+')
    word_freq_filename = os.path.join(PATH, 'out', 'words_freq_0710.txt')
    timeout_url_filename = os.path.join(PATH, 'log', 'timeout_url_0528')
    enable_proxy = True

    with codecs.open(word_freq_filename, mode='a')as word_freq_wf,\
    codecs.open(timeout_url_filename, mode='a') as timeout_url_wf:
        for word in word_list_to_crawl:
            count += 1
            url = url_pattern%word
            req = urllib2.Request(url)
            #第一次抓取，enable_proxy设置为False,
            ip_port = '140.206.215.5:82'
            if enable_proxy:
                req.set_proxy(ip_port, 'http')
            try:
                html = urllib2.urlopen(req, timeout=15).read()
            except:
                try:
                    html = urllib2.urlopen(req, timeout=15).read()
                except:
                    timeout_url_wf.write(url+'\n')
                    continue
            soup = BeautifulSoup(html)
            span_level_str_list = soup.find_all('span', class_='nums')
            if not span_level_str_list:
                #如果没有词频，则enable_proxy取反
                enable_proxy = not enable_proxy
                #更换代理设置（enbale_proxy）后进行第二次抓取
                if enable_proxy:
                    req.set_proxy(ip_port, 'http')
                try:
                    html = urllib2.urlopen(req, timeout=15).read()
                except:
                    try:
                        html = urllib2.urlopen(req, timeout=15).read()
                    except:
                        timeout_url_wf.write(url+'\n')
                        continue
                soup = BeautifulSoup(html)
                span_level_str_list = soup.find_all('span', class_='nums')
            #如果第二次抓取词频依旧没有词频,则将该词的词频置为0
            if span_level_str_list:
                span_level_str = span_level_str_list[0]
                num_text = span_level_str.get_text()
                num = "".join(num_pattern.findall(num_text))
                com_str = "\t".join((word, str(num))) + '\n'
            else:
                com_str = "\t".join((word, "0")) + '\n'
            print count, com_str.decode('utf-8').strip()
            word_freq_wf.write(com_str)
            # time.sleep(1)
# read_item_url()
def remove_zero_freq():
    '''删除词频为0的词'''
    word_freq_filename = os.path.join(PATH, 'out', 'words_freq_0710.txt')

    zero_freq_list = []
    with codecs.open(word_freq_filename)as f:
        for line in f.readlines():
            splited_line = line.split("\t")
            freq = splited_line[1]
            if freq.strip() != '0':
                # print line.strip()
                zero_freq_list.append(line)
        print len(zero_freq_list)
    # des_dir_filename = os.path.join(PATH, 'out', 'no_zero_words_freq_0710.txt')
    codecs.open(word_freq_filename, mode='wb').writelines(zero_freq_list)
# remove_zero_freq()
def check_if_repeat():
    '''检查文件是否有重复'''
    word_freq_filename = os.path.join(PATH, 'out', 'words_freq_0710.txt')
    with codecs.open(word_freq_filename, encoding='utf-8') as f:
        line_list = f.readlines()
        line_set = set(line_list)
        print len(line_list), len(line_set)
    # with codecs.open(word_freq_filename, mode='wb', encoding='utf-8') as wf:
    #     wf.writelines(line_set)
# check_if_repeat()
def mk_word_freq_inorder():
    '''按照词频进行排序'''
    word_freq_filename = os.path.join(PATH, 'out', 'words_freq_0710.txt')
    with codecs.open(word_freq_filename, encoding='utf-8') as f:
        sorted_list = sorted(f.readlines(), key=(lambda x:int(x.split('\t')[1])), reverse=True)
    write_filename = os.path.join(PATH, 'out', 'words_freq_0710_inorder.txt')
    codecs.open(write_filename, mode='wb', encoding='utf-8').writelines(sorted_list)
mk_word_freq_inorder()