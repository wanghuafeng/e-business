__author__ = 'huafeng'
#coding:utf-8
import re
import os
import time
import urllib2
import random
import codecs
import logging
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
SLEEP_INTERVAL = random.randint(5,10)

class DoubanCrawler:

    def __init__(self):
        self.crawled_url_list = []#已抓取的url
        self.timeout_url_list = []
        self.title_actors_list = []
        self.movie_summary_list = []
        self._load_crawled_url()
        self._gen_logger()


    def _gen_logger(self):
        logfile = os.path.join(PATH, 'log', 'crawler_douban.log')
        self.logger = logging.getLogger('douban')
        self.logger.setLevel(logging.DEBUG)
        log_file = logging.FileHandler(logfile)
        log_file.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        log_file.setFormatter(formatter)
        self.logger.addHandler(log_file)

    def _load_crawled_url(self):
        filename = os.path.join(PATH, 'sys', 'douban_crawled_urls')
        with codecs.open(filename, encoding='utf-8') as f:
            self.crawled_url_list = f.readlines()

    def gen_comming_movie_list(self):
        #抓取将要上映的没有被抓取过的电影url
        url = 'http://movie.douban.com/coming'
        try:
            html = urllib2.urlopen(url, timeout=20).read()
        except:
            time.sleep(120)
            try:
                html = urllib2.urlopen(url, timeout=20).read()
            except:
                return
        soup = BeautifulSoup(html)
        tbody_level_str = soup.find('tbody')
        if not tbody_level_str:
            raise ValueError('tbody_level_str do not match pattern in root url:%s'%url)
        tr_level_list = tbody_level_str.find_all('tr')
        url_list = [item.find('a')['href'] for item in tr_level_list]
        # print url_list, len(url_list)#77
        #没有抓取过的url
        no_crawled_url_list = [item for item in url_list if item not in set(self.crawled_url_list)]
        return no_crawled_url_list

        # #将url写入到文件中
        # filename = os.path.join(PATH, 'page_url', 'douban_crawled_urls')
        # url_list = ["".join((item, '\n'))  for item in url_list if item not in set(self.crawled_url_list)]
        # with codecs.open(filename, mode='a', encoding='utf-8') as wf:
        #     wf.writelines(url_list)

    def gen_nowplaying_movie_url(self):
        #抓取最近播放的没有被抓取过的电影url
        url = 'http://movie.douban.com/nowplaying/beijing/'
        try:
            html = urllib2.urlopen(url, timeout=20).read()
        except:
            time.sleep(120)
            try:
                html = urllib2.urlopen(url, timeout=20).read()
            except:
                return
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', id='nowplaying')
        if not div_level_str:
            raise ValueError('div_level_str do not match pattern in root url:%s'%url)
        li_level_list = div_level_str.find_all('li', class_='stitle')
        url_list = [item.a['href'] for item in li_level_list]
        no_crawled_url_list = [item for item in url_list if item not in set(self.crawled_url_list)]
        return no_crawled_url_list

        # print nowplaying_url_list, len(nowplaying_url_list)
        #将url写入到文件中
        # filename = os.path.join(PATH, 'page_url', 'douban_crawled_urls')
        # url_list = ["".join((item, '\n'))  for item in url_list if item not in set(self.crawled_url_list)]
        # with codecs.open(filename, mode='a', encoding='utf-8') as wf:
        #     wf.writelines(url_list)

        # self.crawl_movie_content(nowplaying_url_list)

    def write_con_into_file(self):
        timestamp = time.strftime('%Y_%m_%d.db')
        title_actor_file = os.path.join(PATH, 'out', 'title_actors_%s'%timestamp)
        title_actor_list = ["".join((item, '\n')) for item in self.title_actors_list]
        with codecs.open(title_actor_file, mode='a', encoding='utf-8') as wf:
            wf.writelines(title_actor_list)

        movie_summary_filename = os.path.join(PATH, 'out', 'movie_summary_%s'%timestamp)
        movie_summary_list = [''.join((item, '\n')) for item in self.movie_summary_list]
        with codecs.open(movie_summary_filename, mode='a', encoding='utf-8') as movief:
            movief.writelines(movie_summary_list)

    def re_write_crawled_url(self):
        filename = os.path.join(PATH, 'sys', 'douban_crawled_urls')
        with codecs.open(filename, mode='wb', encoding='utf-8') as f:
            f.writelines(self.crawled_url_list)

    def crawl_movie_content(self, url_list):
        for url in url_list:
            url = url.strip()
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                self.logger.info('request timed item_id in url:%s'%url)
                continue

            soup = BeautifulSoup(html)

            #电影名称
            try:
                title = soup.find('span', property='v:itemreviewed').text
            except:
                raise ValueError('title do not match the pattern in url:%s'%url)
            self.title_actors_list.append(title)
            # print title

            #演员名称
            div_level_str = soup.find('div', id='info')
            if not div_level_str:
                raise ValueError('div_level_str do not match pattern in url:%s'%url)
            a_level_list = div_level_str.find_all('a')
            actors_list = [item.text for item in a_level_list]
            self.title_actors_list.extend(actors_list)

            #电影简介
            con_div_level_str = soup.find('div', id='link-report')
            if not con_div_level_str:
                self.logger.error('con_div_level_str do not match pattern in url:%s'%url)
                continue
            #页面没有展开信息
            further_content = con_div_level_str.select('span[class=""]')
            if further_content:
               movie_content = further_content[0].text
            else:
                movie_content = con_div_level_str.find('span', class_='all hidden').text
            self.movie_summary_list.append(movie_content.strip())
            # print movie_content

            self.crawled_url_list.append(url)

            time.sleep(SLEEP_INTERVAL)

        #把标题、演员、简介写入到文件(title_actors_, movie_summary_)中
        self.write_con_into_file()
        self.title_actors_list[:] = []
        self.movie_summary_list[:] = []

        #把成功抓取的url写入到已抓取文件(douban_crawled_urls)中
        if len(self.crawled_url_list) > 800:
            del self.crawled_url_list[:200]
            self.re_write_crawled_url()

    def main(self):
        com_url_list = []
        nowplaying_movie_url_list = self.gen_nowplaying_movie_url()
        if nowplaying_movie_url_list:
            com_url_list.extend(nowplaying_movie_url_list)
        comming_movie_url_list = self.gen_comming_movie_list()
        if comming_movie_url_list:
            com_url_list.extend(comming_movie_url_list)
        assert com_url_list is not []
        self.crawl_movie_content(com_url_list)


def nowplaying_movie():
    #抓取最近播放的电影
    url = 'http://movie.douban.com/nowplaying/beijing/'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='nowplaying')
    li_level_list = div_level_str.find_all('li', class_='stitle')
    # print li_level_list, len(li_level_list)

    nowplaying_url_list = [item.a['href'] for item in li_level_list]
    # print nowplaying_url_list, len(nowplaying_url_list)

# nowplaying_movie()

def parse_li_level_str():
    html = """<li class="stitle">
    <a class="ticket-btn" data-psource="title" href="http://movie.douban.com/subject/6082518/?from=playing_poster" target="_blank" title="超凡蜘蛛侠2">
                                        超凡蜘蛛侠2
                                    </a>
    </li> """
    soup = BeautifulSoup(html)
    print soup.a['href']
# parse_li_level_str()
def crawl_one_page():
    #解析一个页面结构
    url = 'http://movie.douban.com/subject/10485647/'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)

    #电影名称
    title = soup.find('span', property='v:itemreviewed').text
    print title

    #演员名称
    div_level_str = soup.find('div', id='info')
    print div_level_str.text


    #电影简介
    # con_div_level_str = soup.find('div', id='link-report')
    # #页面没有展开信息
    # further_content = con_div_level_str.select('span[class=""]')
    # if further_content:
    #    movie_content = further_content[0].text
    # else:
    #     movie_content = con_div_level_str.find('span', class_='all hidden').text if con_div_level_str.find('span', class_='all hidden') else con_div_level_str.find('span', class_='short').text
    # print movie_content

# crawl_one_page()
def parse_div_level_str():
    #解析div
    html = '''<div id="info">
    <span><span class="pl">导演</span>: <a href="/celebrity/1322695/" rel="v:directedBy">王松</a></span><br/>
    <span><span class="pl">编剧</span>: <a href="/celebrity/1339304/">唐明智</a> / <a href="/celebrity/1339303/">韩梦泽</a> / <a href="/celebrity/1339305/">渠水清</a></span><br/>
    <span><span class="pl">主演</span>: <a href="/celebrity/1317855/" rel="v:starring">郑昊</a> / <a href="/celebrity/1337826/" rel="v:starring">小彩旗</a> / <a href="/celebrity/1315471/" rel="v:starring">阚清子</a> / <a href="/celebrity/1322696/" rel="v:starring">贾伟</a></span><br/>
    <span class="pl">类型:</span> <span property="v:genre">悬疑</span> / <span property="v:genre">惊悚</span><br/>
    <span class="pl">制片国家/地区:</span> 中国大陆<br/>
    <span class="pl">语言:</span> 汉语普通话<br/>
    <span class="pl">上映日期:</span> <span content="2014-05-07(中国大陆)" property="v:initialReleaseDate">2014-05-07(中国大陆)</span><br/>
    <span class="pl">片长:</span> <span content="88" property="v:runtime">88分钟</span><br/>
    <span class="pl">又名:</span> 诡先生 / Enchanted Doll<br/>
    </div>'''
    soup = BeautifulSoup(html)
    #标题

    # #演员名称
    # a_level_list = soup.find_all('a')
    # actors_list = [item.text for item in a_level_list]
    # for actor in actors_list:
    #     print actor
# div_level_str()

def parse_tr_level():
    html = """<tr class="">
        <td class="">
                        2016年02月08日
                    </td>
        <td>
        <a class="" href="http://movie.douban.com/subject/25827963/">西游记之三打白骨精</a>
        </td>
        <td class="">
                        剧情 / 动作 / 奇幻
                    </td>
        <td class="">
                        中国大陆 / 香港
                    </td>
        <td class="">
                        72人
                    </td>
        </tr>"""
    soup = BeautifulSoup(html)
    print soup.find('a')
# parse_tr_level()

def history_page():
    '''解析历史数据页面'''
    url = "http://movie.douban.com/tag/2011"
    t1 = time.time()
    html = urllib2.urlopen(url).read()
    t2 = time.time()
    soup = BeautifulSoup(html)
    t3 = time.time()
    # div_level_str = soup.find('div', id="wrapper")
    # print len(div_level_str)
    # tr_level_list = div_level_str.find_all('tr', class_='item')
    # print tr_level_list, len(tr_level_list)
    print "urlopen time consume is :%s"%(t2 -t1)
    print 'bs time consume is %s'%(t3-t2)

# history_page()

#******************************************************************
#******************************************************************
#历史数据抓取
def gen_whole_page_url():
    '''抓取去所有页面url，并写入到指定文件（whole_page_url）中'''
    whole_page_url_list = []
    url_pattern = 'http://movie.douban.com/tag/%s'
    page_url_pattern = "http://movie.douban.com/tag/%(age)s?start=%(start_count)s&type=T"
    # url_list = [url_pattern%str(i) for i in range(1990, 2014)]
    for age in range(1990, 2014):
        url = url_pattern%str(age)
        html = urllib2.urlopen(url).read()
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', class_='paginator')
        a_level_list = div_level_str.find_all('a', text=re.compile('\d'))
        max_page_size = a_level_list[-1].text
        for page_size in range(int(max_page_size)):
            start_count = page_size*20
            page_url = page_url_pattern%{'age':age, 'start_count':start_count}
            print page_url
            whole_page_url_list.append(page_url)
        time.sleep(SLEEP_INTERVAL)

    #将所有页面url写入到指定文件中
    # filename = os.path.join(PATH, 'page_url', 'whole_page_url')
    # whole_page_url_list = ["".join((item, '\n')) for item in whole_page_url_list]
    # with codecs.open(filename, mode='wb', encoding='utf-8')as f:
    #     f.writelines(whole_page_url_list)
# gen_whole_page_url()
def gen_max_page_size():
    # url = "http://movie.douban.com/tag/2008"
    # html = urllib2.urlopen(url).read()
    # soup = BeautifulSoup(html)
    # div_level_str = soup.find('div', class_='paginator')
    # print div_level_str
    html = '''<div class="paginator">
            <span class="prev">
                        &lt;前页
                    </span>
            <span class="thispage" data-total-page="42">1</span>
            <a href="http://movie.douban.com/tag/2008?start=20&amp;type=T">2</a>
            <a href="http://movie.douban.com/tag/2008?start=40&amp;type=T">3</a>
            <a href="http://movie.douban.com/tag/2008?start=60&amp;type=T">4</a>
            <a href="http://movie.douban.com/tag/2008?start=80&amp;type=T">5</a>
            <a href="http://movie.douban.com/tag/2008?start=100&amp;type=T">6</a>
            <a href="http://movie.douban.com/tag/2008?start=120&amp;type=T">7</a>
            <a href="http://movie.douban.com/tag/2008?start=140&amp;type=T">8</a>
            <a href="http://movie.douban.com/tag/2008?start=160&amp;type=T">9</a>
            <span class="break">...</span>
            <a href="http://movie.douban.com/tag/2008?start=800&amp;type=T">41</a>
            <a href="http://movie.douban.com/tag/2008?start=820&amp;type=T">42</a>
            <span class="next">
            <link href="http://movie.douban.com/tag/2008?start=20&amp;type=T" rel="next"/>
            <a href="http://movie.douban.com/tag/2008?start=20&amp;type=T">后页&gt;</a>
            </span>
            </div>'''
    soup = BeautifulSoup(html)
    a_level_list = soup.find_all('a', text=re.compile('\d'))
    print a_level_list
    print a_level_list[-1].text
# gen_max_page_size()

def parse_tagCol():

    html= '''<table class="tagCol">
    <tbody>
    <tr>
    <td><a href="./2011">2011</a><b>(900794)</b></td>
    <td><a href="./2012">2012</a><b>(853904)</b></td>
    <td><a href="./2010">2010</a><b>(791468)</b></td>
    <td><a href="./2013">2013</a><b>(754322)</b></td>
    </tr>
    </tbody>
    </table>'''
    soup = BeautifulSoup(html)
    tbody_level_str = soup.find_all('td')
    print tbody_level_str
# parse_tagCol()
def remove_blank_line():
    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    clean_url_list = []
    with codecs.open(filename, encoding='utf-8')as f:
        temp_list = [item for item in f.readlines() if item != '\n']
        clean_url_list.extend(temp_list)
    with codecs.open(filename, mode='wb', encoding='utf-8')as wf:
        wf.writelines(clean_url_list)
# remove_blank_line()

def write_item_url_content(page_content_list):
    #将所有的页面信息写入到指定文件中
    timestamp = time.strftime('%Y_%m_%d_%H%M%S_douban_movie_.txt')
    filename = os.path.join(PATH, 'out', timestamp)
    page_content_list = ["".join((item, '\n')) for item in page_content_list]
    with codecs.open(filename, mode='wb', encoding='utf-8') as f:
        f.writelines(page_content_list)
def item_url_crawl_with_proxy():
    #使用代理进行item_url的抓取
    page_content_list = []
    url = 'http://movie.douban.com/subject/1308843/'
    ip_port = '210.73.220.18:8088'
    http_proxy = 'http://%s'%ip_port
    proxy_hanlder = urllib2.ProxyHandler({'http':http_proxy})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)

    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)

    #电影名称
    title = soup.find('span', property='v:itemreviewed').text
    page_content_list.append(title)
    print title

    #演员名称
    div_level_str = soup.find('div', id='info')
    a_level_list = div_level_str.find_all('a')
    actors_list = [item.text for item in a_level_list]
    page_content_list.extend(actors_list)

    #电影简介
    con_div_level_str = soup.find('div', id='link-report')
    #页面没有展开信息
    further_content = con_div_level_str.select('span[class=""]')
    if further_content:
       movie_content = further_content[0].text
    else:
        movie_content = con_div_level_str.find('span', class_='all hidden').text if con_div_level_str.find('span', class_='all hidden') else con_div_level_str.find('span', class_='short').text
    print movie_content

    page_content_list.append(movie_content.strip())

# item_url_crawl_with_proxy()

def gen_total_item_url():
    #从页面中解析item_url列表
    sleep_interval = random.randint(5,8)
    timeout_url_list = []
    filename = os.path.join(PATH, 'sys', 'whole_page_url')
    total_item_url_list = []
    with codecs.open(filename, encoding='utf-8') as f:
        page_url_list = [item.strip() for item in f.readlines()]
        for url in page_url_list:
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                time.sleep(60)
                try:
                    html = urllib2.urlopen(url, timeout=15).read()
                except:
                    timeout_url_list.append(url)
                    continue
            soup = BeautifulSoup(html)
            div_level_str = soup.find('div', class_='article')
            if not div_level_str:
                timeout_url_list.append(url)
                continue
            tr_level_list = div_level_str.find_all('tr', class_='item')
            item_url_list = [item.find('a', class_="")['href'] for item in tr_level_list]
            total_item_url_list.extend(item_url_list)
            time.sleep(sleep_interval)
    write_filename = os.path.join(PATH, 'sys', 'total_item_url')
    total_item_url_list = ["".join((item, '\n')) for item in total_item_url_list]
    with codecs.open(write_filename, mode='wb', encoding='utf-8') as wf:
        wf.writelines(total_item_url_list)

    if timeout_url_list:
        timeout_filename = os.path.join(PATH, 'timeout_item_url')
        timeout_url_list = [''.join((item, '\n'))for item in timeout_url_list]
        with codecs.open(timeout_filename, mode='wb', encoding='utf-8') as timeoutfile:
            timeoutfile.writelines(timeout_url_list)
# gen_total_item_url()

def parse_movie_actor_page():
    url = 'http://movie.douban.com/celebrity/1005773/'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='intro')
    further_content = div_level_str.find('span', class_='all hidden')
    if further_content:
        actor_info = further_content.text
    else:
        actor_info = div_level_str.find_all('div', class_='bd')[-1].text
    print actor_info.strip()

# parse_movie_actor_page()
# print time.strftime('realtime_movie_info_%Y_%m_%d.txt')
def parse_actor_content_url():
    con_url = 'http://movie.douban.com/celebrity/1050210/'
    no_con_url = "http://movie.douban.com/celebrity/1001404/"
    further_con_url = "http://movie.douban.com/celebrity/1002783/"
    ip_port = "210.34.0.227:8080"
    http_proxy = "http://%s"%ip_port
    http_hanlder = urllib2.HTTPHandler({'http':http_proxy})
    opener = urllib2.build_opener(http_hanlder)
    urllib2.install_opener(opener)
    # request = urllib2.Request(con_url)
    # request.add_header('User-Agent', 'Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.14) Gecko/20080404 (FoxPlus) Firefox/2.0.0.14')
    # html = opener.open(request).read()
    html = urllib2.urlopen(further_con_url).read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='content')
    actor_summary = div_level_str.find('div', class_='bd') if not div_level_str.find('span', class_='all hidden') else div_level_str.find('span', class_='all hidden')
    print actor_summary.text.strip()
# parse_actor_content_url()







