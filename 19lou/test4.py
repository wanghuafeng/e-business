__author__ = 'huafeng'
#coding:utf-8
import urllib2
import os
import re
import time
import codecs
import random
from bs4 import BeautifulSoup

PATH = os.path.dirname(__file__)
def gen_topic_urls():

    url = "http://www.19lou.com"
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find_all('div', class_='head-bd')[0]
    nav_level_str = div_level_str.find_all('nav', class_='floor')[0]
    a_level_list = nav_level_str.select('a[href^="http"]')
    url_list = [item['href']+'\n' for item in a_level_list]
    print url_list
    print len(url_list)

    filename = os.path.join(PATH, 'sys', 'hot_topic_urls')
    # with codecs.open(filename, mode="wb", encoding="utf-8")as f:
    #     f.writelines(url_list)

    # a_level = div_level_str.find_all('a')
# gen_topic_urls()

def gen_food_nav_topic():
    url = "http://tour.19lou.com"
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level = soup.find_all('div', id='header-nav')
    div_level_str = div_level[0] if len(div_level) is 1 else None
    a_level = div_level_str.find_all('h2')
    # print a_level
    # print len(a_level)#7
    url_list = [item.a['href'] for item in a_level]
    print url_list
# gen_food_nav_topic()
def parse_a_level_str():
    a_level_str = '<h2><a href="http://www.19lou.com/forum-9-1.html" title="杭州美食 杭州特色小吃">美食俱乐部</a></h2>'
    a_level_str = '<h2><span class="nav1"> 首页 </span></h2>'
    soup = BeautifulSoup(a_level_str)
    print(soup.a)
    # print soup.a['href']
# parse_a_level_str()
def gen_nav_topic_urls():
    filename = os.path.join(PATH, 'sys', 'hot_topic_urls')
    temp_url_list = []
    with open(filename) as f:
        for url in f.readlines():
            url = url.strip()
            try:
                html = urllib2.urlopen(url, timeout=10).read()
            except:
                print  "time item_id in url:%s"%url
                continue
            soup = BeautifulSoup(html)
            div_level = soup.find_all('div', id='header-nav')
            if  len(div_level) is not 1:
                print url
            else:
                print len(div_level)
            div_level_str = div_level[0] if len(div_level) is 1 else None
            a_level = div_level_str.find_all('h2')
            url_list = [item.a['href'] for item in a_level]
            temp_url_list.extend(url_list)
            time.sleep(5)

    marry_urls_list =  ['http://www.19lou.com/forum-291-1.html', 'http://www.19lou.com/forum-464641-1.html', 'http://www.19lou.com/forum-1219-1.html', 'http://www.19lou.com/forum-2246-1.html']
    baby_list = ['http://www.19lou.com/forum-464780-1.html', 'http://www.19lou.com/forum-464779-1.html', 'http://www.19lou.com/forum-130-1.html', 'http://www.19lou.com/forum-16-1.html', 'http://www.19lou.com/forum-1994-1.html', 'http://www.19lou.com/piclist-1366-1.html', 'http://www.19lou.com/forum-1614-1.html', 'http://www.19lou.com/forum-1221-1.html']
    temp_url_list.extend(marry_urls_list)
    temp_url_list.extend(baby_list)
    temp_url_list = [item+'\n' for item in temp_url_list]
    print len(temp_url_list)
    write_file = os.path.join(PATH, 'sys', 'whole_page_urls')
    # with open(write_file, mode='wb') as wf:
    #     wf.writelines(temp_url_list)
# gen_nav_topic_urls()

def gen_marry_header_nav():
    '''24个hot_topic_url中，marry，baby不符合header-nav的pattern
    此处需要对这两个url做单独处理'''
    exception_urls = ["http://marry.19lou.com","http://baby.19lou.com"]
    # url = "http://marry.19lou.com"
    # html = urllib2.urlopen(url).read()
    html = '''<div class="nav" id="0">
<h1 id="logo">
<a href="#" title="" ttname="marry_19lou">
        我要结婚
    </a>
</h1>
<ul class="clearall">
<li class="selected">
<em>首页</em>
<span></span>
</li>
<li><a href="http://www.19lou.com/forum-291-1.html" title="杭州婚庆 杭州婚博会 杭州婚宴酒店" ttname="marry_19lou_291">结婚大本营</a><span></span></li>
<li><a href="http://www.19lou.com/forum-464641-1.html" title="新娘交流区 新娘论坛 婚礼日记" ttname="marry_19lou_464641">结婚日记</a><span></span></li>
<li><a href="http://www.19lou.com/forum-1219-1.html" title="杭州婚纱摄影 杭州婚庆策划服务" ttname="marry_19lou_1219">结婚商城</a><span></span></li>
<li><a href="http://marry.19lou.com/four" title="杭州婚庆四大金刚―婚礼摄像师_婚礼摄影师_新娘造型师_婚礼主持人-我要结婚-杭州19楼" ttname="marry_19lou_464767">四大金刚</a><span></span></li>
<li><a href="http://www.19lou.com/forum-2246-1.html" title="杭州婚礼二手用品交易 杭州二手婚纱 杭州婚宴转让" ttname="marry_19lou_2246">婚礼用品转让</a><span></span></li>
<li><a href="http://home.19lou.com/jiafang" title="杭州家纺商家集合_杭州家纺排名_家纺十大品牌" ttname="marry_19lou_464794">家纺专区</a><span></span></li>
</ul>
<a class="marry-app" href="http://newmarry.19lou.com" target="_blank" title="新版结婚首页抢先看">新版结婚首页抢先看！</a>
</div>'''
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div',id='0')
    a_level = div_level_str.find_all('a', href=re.compile('http'))
    marry_url_list = [item['href'] for item in a_level]
    print marry_url_list
    marry_urls_list =  ['http://www.19lou.com/forum-291-1.html', 'http://www.19lou.com/forum-464641-1.html', 'http://www.19lou.com/forum-1219-1.html', 'http://www.19lou.com/forum-2246-1.html']
# gen_marry_header_nav()

def gen_baby_header_nav():
    # url = "http://baby.19lou.com"
    # html = urllib2.urlopen(url).read()
    html = '''<div class="header-nav fl">
<h2>
<span class="nav1">
                       首页
                   </span>
</h2>
<!-- showPop 杭州家居二级楼导航处理  这个处理会导致该二级楼导航的板块不能随意修改
    假如要取消这个气泡效果 需要再走一次发布 update by zhangHaiJun-->
<h2>
<a class="nav2" href="http://www.19lou.com/forum-464780-1.html" title="孕前 不孕不育论坛">想要宝宝</a>
</h2>
<h2>
<a class="nav3" href="http://www.19lou.com/forum-464779-1.html" title="单独二胎 生二胎">我要生二胎</a>
</h2>
<h2>
<a class="nav4" href="http://www.19lou.com/forum-130-1.html" title="准妈妈论坛 怀孕知识 备孕日记">准妈妈论坛</a>
</h2>
<h2>
<a class="nav5" href="http://www.19lou.com/forum-16-1.html" title="亲子论坛">孩爸孩妈聊天室</a>
</h2>
<h2>
<a class="nav6" href="http://www.19lou.com/forum-1994-1.html" title="杭州幼儿园 杭州最好的幼儿园">幼儿园论坛</a>
</h2>
<h2>
<a class="nav7" href="http://www.19lou.com/piclist-1366-1.html" title="宝宝秀 漂亮妈咪">妈咪宝宝秀</a>
</h2>
<h2>
<a class="nav8" href="http://www.19lou.com/forum-1614-1.html" title="亲子采购">亲子超级采购团</a>
</h2>
<h2>
<a class="nav9" href="http://www.19lou.com/forum-1221-1.html" title="杭州保姆网 杭州月嫂保姆招聘 杭州保姆市场">家有保姆</a>
</h2>
</div>'''
    soup = BeautifulSoup(html)
    div_level = soup.find_all('div', class_='header-nav fl')
    div_level_str = div_level[0]
    a_match_level = div_level_str.find_all('a')
    # print a_match_level
    baby_url_list = [item['href'] for item in a_match_level]
    print baby_url_list
    baby_list = ['http://www.19lou.com/forum-464780-1.html', 'http://www.19lou.com/forum-464779-1.html', 'http://www.19lou.com/forum-130-1.html', 'http://www.19lou.com/forum-16-1.html', 'http://www.19lou.com/forum-1994-1.html', 'http://www.19lou.com/piclist-1366-1.html', 'http://www.19lou.com/forum-1614-1.html', 'http://www.19lou.com/forum-1221-1.html']
# gen_baby_header_nav()

def bs4_multi_proper():
    data_soup = BeautifulSoup('<a class="sister" href="http://example.com/elsie" id="link1">three</a>')
    level = data_soup.find_all('a',class_='sister', href=re.compile('http'), id='link1')
    print level
# bs4_multi_proper()

def check_valid_url():
    whole_url_list = []
    filename = os.path.join(PATH, 'sys', 'nav_topic_urls')
    if not os.path.isfile(filename):
        raise ValueError('No such file:%s'%filename)
    with open(filename) as f:
        for url in f.readlines():
            url = url.strip()
            try:
                html = urllib2.urlopen(url, timeout=10).read().decode('gbk')
            except:
                print 'time item_id request in url:%s'%url
                continue
            soup = BeautifulSoup(html)
            div_level = soup.find_all('div', class_='clearall')
            print url if len(div_level) is 0 else 1
            # print div_level
            sleep_interval = random.randint(8, 13)
            time.sleep(sleep_interval)
# check_valid_url()
def gen_max_page_size():
    '''解析当前页面获取该topic的max_page_size'''
    url = "http://www.19lou.com/forum-293-1.html"
    html = urllib2.urlopen(url).read().decode('gbk')
    soup = BeautifulSoup(html)
    # div_level_str = soup.find_all('div', class_=re.compile('^clearall$') )
    # print div_level_str
    a_level_str = soup.find('a', class_='page-last')
    print a_level_str
    # href_con = a_level_str['href']#http://www.19lou.com/forum-168-342.html
    # print href
    # url = "http://www.19lou.com/forum-168-342.html"
    # m = re.search('\-[\d]+\-([\d]+)', href_con)
    # print m.group(1)
# gen_max_page_size()

def chose_valid_div_level():
    html = """<div class="auto-jh clearall">
<div class="fl auto-jh-cont">
<p class="auto-jh-text clearall" id="auto_prize_msg"></p>
<div class="auto-jh-select clearall">我的爱车：
                    <select id="brandselect2"><option value="0">-请选择品牌-</option></select>
<select id="seriesselect2"><option value="0">-请选择车系-</option></select>
<a href="javascript:void(0);" id="showPrice_but2" ttname="bbs_baojia_cheyouhui">去晒车价</a>
</div>
</div>
<div class="lunbo fr " id="auto_image_lunbo">
</div>
</div>"""
    soup = BeautifulSoup(html)
    div_level = soup.find('div', class_=lambda x:len(x) is 8 and x=='clearall')
    print div_level
# chose_valid_div_level()

# splited_url = url.split('-')
# print splited_url
import logging
# stampfile = time.strftime('%Y_%m_%d_%H%M.txt')
logfile = os.path.join('log/19lou.log')
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
log_file = logging.FileHandler(logfile, encoding="utf-8")
log_file.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
log_file.setFormatter(formatter)
logger.addHandler(log_file)
def get_whole_page_urls():
    whole_page_url_list = []
    filename = os.path.join(PATH, 'sys', 'nav_topic_urls')
    if not os.path.isfile(filename):
        raise ValueError('No such file:%s'%filename)
    with open(filename) as f:
        for url in f.readlines():
            url = url.strip()
            try:
                html = urllib2.urlopen(url, timeout=10).read().decode('gbk')
            except:
                print 'time item_id request in url:%s'%url
                logger.debug('time item_id request in url:%s'%url)
                continue
            soup = BeautifulSoup(html)
            # div_level_str = soup.find('div', class_='clearall')
            a_level_str = soup.find('a', class_='page-last')
            href_url = a_level_str['href']
            m = re.search('\-[\d]+\-([\d]+)', href_url)
            max_page_size = m.group(1)

            splited_url = href_url.split('-')
            for i in range(1, int(max_page_size)+1):
                # print splited_url
                splited_url[-1] = re.sub('\d+', str(i), splited_url[-1])
                page_url = '-'.join(splited_url)
                # print page_url
                # time.sleep(3)
                whole_page_url_list.append(page_url)

            sleep_interval = random.randint(8, 13)
            time.sleep(sleep_interval)
    write_file = os.path.join(PATH, 'sys', 'whole_page_urls')
    whole_page_url_list = [url+'\n' for url in whole_page_url_list]
    with open(write_file, mode="wb") as wf:
        wf.writelines(whole_page_url_list)
# get_whole_page_urls()

# url = "http://www.19lou.com/forum-168-3.html"
# m = re.search('\-[\d]+\-([\d]+)', url)
# page_size = m.group(1)
# splited_url = url.split('-')
# for i in range(int(page_size)):
#     # print splited_url
#     splited_url[-1] = re.sub('\d+', str(i), splited_url[-1])
#     page_url = '-'.join(splited_url)
#     print page_url
def handle_time_out_urls():
    url = "http://www.19lou.com/forum-40-1.html"
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    a_level_str = soup.find('a', class_='page-last')
    href_url = a_level_str['href']
    m = re.search('\-[\d]+\-([\d]+)', href_url)
    max_size = m.group(1)
    for i in range(1, int(max_size)+1):
        splited_url = url.split('-')
        splited_url[-1] = re.sub('\d+', str(i), splited_url[-1])
        url = '-'.join(splited_url)
        print url
# handle_time_out_urls()
def read_temp_url_file():
    '''删除文件中的偶数空格行'''
    temp_url_list = []
    filename = os.path.join(PATH, 'sys', 'temp_url_file')
    with open(filename) as f:
        for url in f.readlines():
            if url != '\n':
                temp_url_list.append(url)
    # temp_url_list = [url for url in temp_url_list if url != '\n']
    print len(temp_url_list)
    write_filename = os.path.join(PATH, 'sys', 'whole_page_urls')
    with open(write_filename, mode='wb') as wf:
        wf.writelines(temp_url_list)
# read_temp_url_file()
def get_item_urls():
    url = "http://www.19lou.com/forum-9-1.html"
    html = urllib2.urlopen(url).read().decode('gbk')
    soup = BeautifulSoup(html)
    table_level = soup.find('table', class_='list-data')
    # table_level
# get_item_urls()lll
def test_items_pattern():
    '''测试所有nav_topic_url是否满足同一个pattern'''
    filename = os.path.join(PATH, 'sys', 'nav_topic_urls')
    with open(filename) as f:
        for url in f.readlines():
            url = url.strip()
            try:
                html = urllib2.urlopen(url,timeout=10).read().decode('gbk')
            except:
                logger.debug('request timed item_id in url:%s'%url)
                continue
            soup = BeautifulSoup(html)
            div_level_list = soup.find_all('div', class_='subject')
            if len(div_level_list) is not 30:
                print url,len(div_level_list)
            # print len(div_level_list)
            sleep_interval = random.randint(8,13)
            time.sleep(sleep_interval)
test_items_pattern()

# url = "http://www.19lou.com/forum-1945-1.html "
# html = urllib2.urlopen(url).read().decode('gbk')
# soup = BeautifulSoup(html)
# div_level = soup.find_all('div', class_='subject')
# print len(div_level)

def add_new_urls():
    url = "http://www.19lou.com/forum-1190-%s.html"
    for i in range(1, 101):
        new_url = url%str(i)
        print new_url
# add_new_urls()

