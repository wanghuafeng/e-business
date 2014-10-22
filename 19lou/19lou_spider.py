__author__ = 'huafeng'
#encoding:utf-8
import os
import re
import time
import urllib
import urllib2
import random
import codecs
from bs4 import BeautifulSoup

def gen_item_urls():
    url = "http://www.19lou.com/forum-1190-1.html"
    html = urllib2.urlopen(url, timeout=10).read().decode('gbk')
    soup = BeautifulSoup(html)
    div_level_list = soup.find_all('div', class_='subject')

    item_url_list = [item.a['href'] for item in div_level_list]
    print item_url_list
# gen_item_urls()
def msg_comment_con():
    headers = {'User-Agent':'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6'}
    req = urllib2.Request(
        url = "http://www.19lou.com/forum-1190-thread-11091370424024346-1-1.html",
        headers = headers,
    )
    html = urllib2.urlopen(req).read().decode('gbk')
    print html
    # soup = BeautifulSoup(html)
    # div_level = soup.find_all('div', class_='post-cont')
    # print div_level
# msg_comment_con()
#
# url = "http://www.19lou.com/forum-1190-thread-11091370424024346-1-1.html"
# user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
# headers = { 'User-Agent' : user_agent }
# req = urllib2.Request(url, headers=headers)
# req.add_header('Cache-Control', 'no-cache')
# req.add_header('Accept', '*/*')
# req.add_header('Connection', 'Keep-Alive')
# html = urllib2.urlopen(req).read().decode('gbk')
# print html

# import urllib2
# cookies = urllib2.HTTPCookieProcessor()
# opener = urllib2.build_opener(cookies)
# f = opener.open('http://www.19lou.com/forum-1190-thread-11091370424024346-1-1.html')
# user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
# request = urllib2.Request(
#         url     = 'http://www.19lou.com/forum-1190-thread-11091370424024346-1-1.html',
#         headers = {'user_agent' : user_agent},
#         )
# print opener.open(request).read()

# import cookielib
# import urllib2
# loginUrl = "http://www.19lou.com/forum-1190-thread-11091370424024346-1-1.html"
# cj = cookielib.CookieJar()
# opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cj))
# urllib2.install_opener(opener)
# resp = urllib2.urlopen(loginUrl)
# for index, cookie in enumerate(cj):
#     print '[',index, ']',cookie

def login():
    post_url = "http://www.19lou.com/login"
    post_data = {
        'userName':'komoxo2014',
        'userPass':'1qaz2wsx',
    }
    post_data = urllib.urlencode(post_data)
    cookies = urllib2.HTTPCookieProcessor()
    operner = urllib2.build_opener(cookies)
    req = urllib2.Request(post_url, post_data)
    result = operner.open(req)
    if result.geturl() == 'http://www.19lou.com/':
        print "login sucess..."


    headers = {
    "Host":"www.19lou.com",
    "User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0",
    "Referer":"	http://www.19lou.com/"
        }

    url = "http://www.19lou.com/forum-1190-thread-11091370424024346-1-1.html"
    get_req = urllib2.Request(url)
    html = operner.open(get_req).read().decode('gbk')
    print html
# login()

