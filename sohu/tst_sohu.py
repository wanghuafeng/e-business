__author__ = 'wanghuafeng'
#coding:utf-8
import os
import re
import codecs
import time
import random
import urllib2
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def gen_hot_topic_html():
    url = "http://club.sohu.com/"
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', class_='nav model a14')
    print div_level_str

# gen_hot_topic_urls()
def parse_div_level():
    html = """<div class="nav model a14">
<a href="http://club.news.sohu.com/minjian/threads" target="_blank">民间纪事</a>
<a href="http://yule.club.sohu.com/bagua/threads" target="_blank">八卦爆料</a>
<a href="http://club.women.sohu.com/shopping/threads" target="_blank">逛街购物</a>
<a href="http://club.cul.sohu.com/chuyushe/threads" target="_blank">杂谈酷评</a>
<a href="http://club.baobao.sohu.com/mom_daugh/threads" target="_blank">婆媳关系</a>
<a href="http://club.sports.sohu.com/allgame/threads" target="_blank">体坛风云</a>
<a href="http://club.chihe.sohu.com/food/threads" target="_blank">美食厨房</a>
<a href="http://club.news.sohu.com/zz0580/threads" target="_blank">星空杂谈</a>
<a href="http://club.travel.sohu.com/togher/threads" target="_blank">结伴旅行</a>
<a href="http://club.learning.sohu.com/szhengzhi/threads" target="_blank">大话教育</a>
<a href="http://club.stock.sohu.com/stock/threads" target="_blank">股市风云</a>
<a href="http://club.money.sohu.com/licai/threads" target="_blank">我家理财</a>
<a href="http://club.women.sohu.com/zz0894/threads" target="_blank">情感杂谈</a>
<a href="http://club.health.sohu.com/nutrition/threads" target="_blank">食疗营养</a>
</div>"""
    soup = BeautifulSoup(html)
    a_leve_list = soup.find_all('a')
    # print a_leve_list, len(a_leve_list)
    hot_topic_url_list = [item['href'] for item in a_leve_list]
    hot_topic_url_list = [url+'\n' for url in hot_topic_url_list]
    filename = os.path.join(PATH, 'sys', 'hot_topic_url')
    # with open(filename, mode="wb") as wf:
    #     wf.writelines(hot_topic_url_list)
# parse_div_level()
def get_max_page_size():
    url ="http://club.sports.sohu.com/allgame/threads"
    html = urllib2.urlopen(url).read().decode('utf-8')
    # print html
    # soup = BeautifulSoup(html)
    # div_level = soup.find('div', class_='pages')
    pattern = re.compile(r'\<div class=\\\"pages\\\".*?\<\\\/div\>')
    # m = re.findall(r'\<a href="\/allgame\/threads\?type=all".*', html)
    match_list = pattern.search(html)
    match_html =  match_list.group()
    # print re.findall(r'\<a href=')
# get_max_page_size()
def whole_page_urls():
    page_size_dic = {"minjian":4851, "bagua":2073, "shopping":902,
                     "chuyushe":1867, "mom_daugh":7460, "allgame":1683,
                    "food":1444, "zz0580":2958, "togher":1428,
                    "szhengzhi":1755, "stock":5365, "licai":2346,
                    "zz0894":7799, "nutrition":1328}
    end_url = "/p%s?type=all&order=rtime"
    url_list = []
    filename = os.path.join(PATH, 'sys', 'hot_topic_url')
    with open(filename) as f:
        for head_url in f.readlines():
            header_url = head_url.strip()
            key = header_url.split("/")[-2]
            max_page_size = page_size_dic[key]
            for i in range(1, int(max_page_size)+1):
                new_end_url = end_url%str(i)
                url = "".join((header_url, new_end_url))
                url_list.append(url)
    url_list = [url+'\n' for url in url_list]
    print len(url_list)
    write_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    with open(write_filename, mode="wb") as wf:
        wf.writelines(url_list)
# whole_page_urls()

# header_url = "http://yule.club.sohu.com/bagua/threads"
# key = header_url.split("/")[-2]
# print key

# end_url = "/p%s?type=all&order=rtime"
# for i in range(1,4):
#     new_end_url = end_url%str(i)
#     print new_end_url
def gen_item_url():
    '''使用正则从script脚本中匹配出item的url'''
    url = "http://club.sports.sohu.com/allgame/threads/p1?type=all&order=rtime"
    # html = urllib2.urlopen(url).read().decode('utf-8')
    # item_url = re.findall(r'href=\\\"\\\/allgame\\\/thread\\\/[\w]+\\\"',html)
    # print item_url, len(item_url)

    item_url = [u'href=\\"\\/allgame\\/thread\\/2a2oy4tyrug\\"',
                u'href=\\"\\/allgame\\/thread\\/2a2nw7vzgp9\\"',
                u'href=\\"\\/allgame\\/thread\\/2a2iwri0qfo\\"',
                u'href=\\"\\/allgame\\/thread\\/29zakwoqj27\\"',
                u'href=\\"\\/allgame\\/thread\\/2a2iflobvq7\\"',
                u'href=\\"\\/allgame\\/thread\\/2a2idiykwlu\\"',
                u'href=\\"\\/allgame\\/thread\\/2a2iay9a592\\"',
                u'href=\\"\\/allgame\\/thread\\/2a2i8vwbbc9\\"',
                u'href=\\"\\/allgame\\/thread\\/2a1n16apr87\\"', u'href=\\"\\/allgame\\/thread\\/2a2hjlm8tx3\\"', u'href=\\"\\/allgame\\/thread\\/2a2fdr78axf\\"', u'href=\\"\\/allgame\\/thread\\/2a2f5wyzoeu\\"', u'href=\\"\\/allgame\\/thread\\/2a1mlw8x5u1\\"', u'href=\\"\\/allgame\\/thread\\/2a2e4mm525r\\"', u'href=\\"\\/allgame\\/thread\\/24smbav92wp\\"', u'href=\\"\\/allgame\\/thread\\/29zhwytlb51\\"', u'href=\\"\\/allgame\\/thread\\/2a2a0cf9a0j\\"', u'href=\\"\\/allgame\\/thread\\/2a29m9f6kf8\\"', u'href=\\"\\/allgame\\/thread\\/2a29la3kema\\"', u'href=\\"\\/allgame\\/thread\\/2a291ekxbgr\\"', u'href=\\"\\/allgame\\/thread\\/2a28su40o7h\\"', u'href=\\"\\/allgame\\/thread\\/2a28mssj791\\"', u'href=\\"\\/allgame\\/thread\\/2a28bg9e62q\\"', u'href=\\"\\/allgame\\/thread\\/2a099v6uz5v\\"', u'href=\\"\\/allgame\\/thread\\/2a09qa4jspn\\"', u'href=\\"\\/allgame\\/thread\\/29zgu41a5aa\\"', u'href=\\"\\/allgame\\/thread\\/2a1hexzp87f\\"', u'href=\\"\\/allgame\\/thread\\/29ziwa3matr\\"', u'href=\\"\\/allgame\\/thread\\/2a1pfyobaz3\\"', u'href=\\"\\/allgame\\/thread\\/2a1mv2xbkur\\"', u'href=\\"\\/allgame\\/thread\\/2a1mrtq6srz\\"', u'href=\\"\\/allgame\\/thread\\/24a4oapxqj7\\"', u'href=\\"\\/allgame\\/thread\\/28fv9o4vg8x\\"', u'href=\\"\\/allgame\\/thread\\/28g69ee8e19\\"', u'href=\\"\\/allgame\\/thread\\/29grs6ol56c\\"', u'href=\\"\\/allgame\\/thread\\/28id3n7o8j7\\"', u'href=\\"\\/allgame\\/thread\\/26qe4zerw76\\"', u'href=\\"\\/allgame\\/thread\\/29ttbx7bihw\\"', u'href=\\"\\/allgame\\/thread\\/29u447wwj15\\"', u'href=\\"\\/allgame\\/thread\\/29ynuf1nng2\\"', u'href=\\"\\/allgame\\/thread\\/29ykxiutgar\\"', u'href=\\"\\/allgame\\/thread\\/29ykv1a784q\\"', u'href=\\"\\/allgame\\/thread\\/29ydujzf2kr\\"', u'href=\\"\\/allgame\\/thread\\/29wemc69gn4\\"', u'href=\\"\\/allgame\\/thread\\/29weio1td31\\"', u'href=\\"\\/allgame\\/thread\\/29yk15lnxlo\\"', u'href=\\"\\/allgame\\/thread\\/29yrp3om5um\\"', u'href=\\"\\/allgame\\/thread\\/29x39ro2zvp\\"', u'href=\\"\\/allgame\\/thread\\/29z52b9m4bi\\"', u'href=\\"\\/allgame\\/thread\\/29z0584rasq\\"', u'href=\\"\\/allgame\\/thread\\/29z4xzjhgk4\\"', u'href=\\"\\/allgame\\/thread\\/29z6w0wqc31\\"', u'href=\\"\\/allgame\\/thread\\/29z79y3eqf2\\"', u'href=\\"\\/allgame\\/thread\\/291qdjgjie3\\"', u'href=\\"\\/allgame\\/thread\\/29z9q7y46zf\\"', u'href=\\"\\/allgame\\/thread\\/29z8dzgw2xy\\"', u'href=\\"\\/allgame\\/thread\\/27lkrztpw53\\"', u'href=\\"\\/allgame\\/thread\\/29yu81pga5w\\"', u'href=\\"\\/allgame\\/thread\\/26zw1nbdn6o\\"', u'href=\\"\\/allgame\\/thread\\/29jdo336k08\\"', u'href=\\"\\/allgame\\/thread\\/291qxgplmqo\\"']
    # clear_item_url = [item.replace('\\','') for item in item_url]
    root_url = "http://club.sports.sohu.com"
    clear_item_url = [re.sub('href|=|"|\\\\', '', item) for item in item_url]
    print ["".join((root_url, end_url)) for end_url in clear_item_url]

def regular_expression_group():
    url = "http://club.sports.sohu.com/allgame/threads/p1?type=all&order=rtime"
    m = re.search('(?P<root>http:.*com)\/(?P<topic>[a-z]+)\/', url)
    print m.group('root'), m.group('topic')

def msg_comment():
    url = "http://club.sports.sohu.com/allgame/thread/24a4oapxqj7"
    html = urllib2.urlopen(url).read()
    msg_con_list = re.findall(r'\<div class=\\\"wrap\\\".*\<\\\/div\>', html)
    # print msg_con_list, len(msg_con_list)
    msg_con = re.sub(r'\\\\','', msg_con_list[0])
    msg_con = eval('u"""%s"""'%msg_con)
    # print msg_con
    # soup =  BeautifulSoup(msg_con)
    # print soup.text
# msg_comment()

def item_msg_comment_con():
    url = "http://club.news.sohu.com/minjian/thread/2a84l9uedbb"
    html = urllib2.urlopen(url).read()
    comment_con_list = re.findall(r'\<table class=\\\"viewpost\\\".*\<\\\/table\>', html)
    # print comment_con_list
    # print comment_con_list, len(comment_con_list)
    clear_comment_str = re.sub(r'\\\\', '', comment_con_list[0])
    clear_comment_str = eval('u"""%s"""'%clear_comment_str)
    msg_comment_con = re.sub(r'\\', '', clear_comment_str)
    soup = BeautifulSoup(msg_comment_con)
    # table_level_list = soup.find_all('table')
    # print len(table_level_list)
    div_level_list = soup.find_all('div', class_='wrap')
    print len(div_level_list)#, div_level_list
    msg_comment_text = [item.text.strip() for item in div_level_list]
    for con in msg_comment_text:
        print con

    print len(msg_comment_text)

    # for msg in msg_comment_text:
    #     print msg
    # soup = BeautifulSoup(clear_comment_str)
    # page_comment_msg_text = soup.find('div', class_=re.compile(r'\\\"wrap\\\"')).text
    # page_comment_msg_text = re.sub(r'\\\\|\\n', '', page_comment_msg_text)
    # # print page_comment_msg_text
    # splited_con_list = re.split(r'\s*', page_comment_msg_text)
    # print len(splited_con_list),splited_con_list
    # sub_text_list = [re.sub(r'\\\\', '', item).encode("utf-8") for item in splited_con_list]
    # param = [item for item in splited_con_list][-10]
    # print [re.sub(ur'\\\\', u'', param)]
# item_msg_comment_con()
def convert_unicode():
    astring = "\u8944\u6a0a\u5e02"
    u = eval("u" + '"%s"'%astring)
    print type(u),u
    s = eval('u"%s"'%astring)
    print s
# convert_unicode()
# print "\u9690\u9a6c\u5c14\u79d1\u592b\u6a21\u578b".decode('unicode-escape')
# print unicode("<b>\u9690\u9a6c\u5c14\u79d1\u592b\u6a21\u578b</b>", 'unicode-escape')
# print re.sub(r'\\\\', '', s)
# uni_s = u"\u52a0\u5173\u6ce8"
# print type(uni_s)
# print uni_s.encode("utf-8")

def max_page():
    url = "http://club.news.sohu.com/minjian/thread/2a16v1hjnbz"
    html = urllib2.urlopen(url).read().decode("utf-8")
    pages_size_list = re.findall(r'\<div class=\\\"pages\\\".*?\<\\\/div\>', html)
    print pages_size_list
    div_level_str = re.sub(r'\\', '', pages_size_list[0])
    # print div_level_str
    soup = BeautifulSoup(div_level_str)
    a_level_list = soup.find_all('a')
    # print a_level_list,len(a_level_list)
    url_list = [item['href'] for item in a_level_list]
    # print url_list
    max_size_str = url_list[-2]

# max_page()
def get_multi_page_size():
    max_str = "/minjian/thread/z9xt2y19xy/p12"
    splited_max_list = max_str.split('/')
    m = re.search('[\d]+', splited_max_list[-1])
    print type(m.group())
    # m = re.search('')
    # splited_max = max_str.split('?')
    # m = re.search('[\d]+', splited_max[0])
    # print m.group()
# get_multi_page_size()

class Tokenizer:
    def __init__(self, string):
        self.string = string
        self.index = 0
        self.__next()
    def __next(self):
        # print self.index, self.string
        if self.index >= len(self.string):
            self.next = None
            return
        char = self.string[self.index]
        print char[0]
        if char[0] == "\\":
            try:
                c = self.string[self.index + 1]
            except IndexError:
                raise ValueError( "bogus escape (end of line)")
            char = char + c
        self.index = self.index + len(char)
        self.next = char

# token = Tokenizer('\\')

def realtime_url():

    end_url = "/p%s?type=all&order=rtime"
    url_list = []
    filename = os.path.join(PATH, 'sys', 'hot_topic_url')

    with open(filename) as f:
        for head_url in f.readlines():
            head_url = head_url.strip()
            for i in range(1, 11):
                new_end_url = end_url%str(i)
                url = "".join((head_url, new_end_url))
                url_list.append(url)
    url_list = [url+'\n' for url in url_list]
    write_filename = os.path.join(PATH, 'sys', 'realtime_page_url')
    # with open(write_filename, mode="wb") as wf:
    #     wf.writelines(url_list)
# realtime_url()
def timestamp_msg_comment():
    url = "http://club.women.sohu.com/shopping/threads/p1?type=all&order=rtime"
    m = re.search('(?P<root_url>http:.*com)\/(?P<topic>[a-z]+)\/', url)
    topic = m.group('topic')
    root_url = m.group('root_url')
    pattern = re.compile(r'href=\\\"\\\/%s\\\/thread\\\/[\!\w]+\\\"'%topic)
    html = urllib2.urlopen(url, timeout=10).read()
    # print html
    table_html = re.search(r'\<table class=\\\"postlist\\\".*\<\\\/table\>', html).group()
    soup = BeautifulSoup(table_html)
    tr_level_list = soup.find_all('td', class_=None)
    # print len(soup.find_all('tr', class_=None))

    tr_text_list = [eval('u"%s"'%item.text) for item in tr_level_list]
    for i in tr_text_list:
        print i

    # soup_text = eval('u"""%s"""'%soup.text)
    # print soup_text, len(soup.text)

    # td_level_list = soup.find('td', class_=None)
    # print td_level_list




    # soup = BeautifulSoup(html)
    # table_level = soup.find('table')
    # print table_level






    # item_url = pattern.findall(html)
    # print len(item_url)
    #
    # end_item_url = [re.sub('href|=|"|\\\\', '', item) for item in item_url]
    # clear_item_url = ["".join((root_url, item)) for item in end_item_url]
    # print len(clear_item_url),clear_item_url
timestamp_msg_comment()