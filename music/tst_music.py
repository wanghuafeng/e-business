__author__ = 'huafeng'
#coding:utf-8
import os
import re
import urllib2
import codecs
import time
import random
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def gen_item_url():
    day_hot_url = "http://music.baidu.com/top/dayhot"
    new_url = "http://music.baidu.com/top/new"
    url_root = 'http://music.baidu.com/'
    url_compile_filter = re.compile(r'/song/[\d]+')
    html = urllib2.urlopen(new_url).read()
    soup = BeautifulSoup(html)
    span_level_list = soup.find_all('span', class_='song-title')
    # span_level_str = '''<span class="song-title " style="width: 240px;">
    # <a href="/song/14385500" title="时间都去哪儿了">时间都去哪儿了</a>
    # <a class="mv-icon" href="/mv/14385500"
    # target="_blank" title="歌曲MV"></a></span>'''
    whole_url_list = [item.a['href'] for item in span_level_list]
    item_url_list = ["".join((url_root, url)) for url in whole_url_list if url_compile_filter.match(url)]
    print item_url_list, len(item_url_list)
# gen_item_url()
def parse_one_page():
    url = 'http://music.baidu.com/song/118422882'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='lyricCont')
    music_con = div_level_str.text
    splited_con = music_con.split('\n')
    print len(splited_con),splited_con[20]
    for i in splited_con:
        print i
# parse_one_page()
def filter_unformal_url():
    unformal_url = "http://y.baidu.com/gotoartist/song/23648?play_song=23648&pst=songList"
    formal_url = '/song/118170108'
    url_compile_filter = re.compile(r'/song/[\d]+')
    match = url_compile_filter.match(formal_url)
    if match:
        print match.group()
# filter_unformal_url()
def write_music_content_into_file():
    timestamp = time.strftime('%Y_%m_%d_%H%M%S_baidu_music.txt')
    filename = os.path.join(PATH, 'out', timestamp)
    print filename
# write_music_content_into_file()
