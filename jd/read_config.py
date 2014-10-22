__author__ = 'huafeng'
import re
import urllib2
import urllib
import time
import cookielib
import ConfigParser
from bs4 import BeautifulSoup

def read_config():
    config = ConfigParser.ConfigParser()
    print config.read('config.ini')

    spiders_name = config.sections()

    crawled_id_filename = config.get('computer','crawled_id_filename')
    print crawled_id_filename
# read_config()
def read_one_item_id():
    url = 'http://list.jd.com/6233-6236-6254-0-0-0-0-0-0-0-1-1-625-1-1-72-4137-33.html'
    ip_port = '218.207.195.206:80'
    enable_proxy = False
    start_time = time.time()
    http_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    null_http_hanlder = urllib2.ProxyHandler({})
    if enable_proxy:
        opener = urllib2.build_opener(http_hanlder)
    else:
        opener = urllib2.build_opener(null_http_hanlder)
    urllib2.install_opener(opener)
    html = urllib2.urlopen(url).read()
    item_id_list = re.findall(r"sku='(\d+)'><div class=", html)
    print item_id_list
    # soup = BeautifulSoup(html, 'html5lib')
    # print soup.find('div', id='name').text
    # print soup.find('div', id='product-detail-1').text
    end_time = time.time()
    print end_time - start_time
read_one_item_id()

def xiaomi():
    login_url = 'https://account.xiaomi.com/pass/serviceLoginAuth2'
    ip_port = '218.203.54.8:80'
    proxy_hanlder = urllib2.ProxyHandler({'https':'https://%s'%ip_port})
    cookiejar = cookielib.CookieJar()
    cookie_hanlder = urllib2.HTTPCookieProcessor(cookiejar)
    opener = urllib2.build_opener(cookie_hanlder, proxy_hanlder)
    post_data = {
                'user' : 'hexinwei@baiwenbao.com',
                'pwd' : 'www.komoxo.com',
                'callback' : 'https://account.xiaomi.com' ,
                'sid' : 'passport',
                'hidden' : '',
                'qs' : '%3Fsid%3Dpassport',
                '_sign' : 'KKkRvCpZoDC+gLdeyOsdMhwV0Xg='
            }
    post_data = urllib.urlencode(post_data)
    req = urllib2.Request(login_url,post_data)
    response = opener.open(req)
    print response.read()
# xiaomi()
def regular():
    s = 'not match name_str in url;http://item.jd.com/1017375.html'
    print re.search('\d+', s).group()
    print s.split(';')[1].strip()

def variable_scope():
    for i in range(10):
        if i == 0:
            var = 'a'
        if i == 2:
            var = 'b'
        if i == 5:
            var = 'c'
        print i,var
# variable_scope()