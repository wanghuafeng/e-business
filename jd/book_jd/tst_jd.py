__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import random
import codecs
import urllib2
import xici_proxy
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def urlllib2_set_debug():
    url = "http://e.jd.com/30057504.html"
    httpHanlder = urllib2.HTTPHandler(debuglevel=1)
    opener = urllib2.build_opener(httpHanlder)
    urllib2.install_opener(opener)

    html = urllib2.urlopen(url).read().decode('gbk')
    print html.getcode()
    print html
    soup = BeautifulSoup(html)
    print soup.text

def download_pic():
    ip_port = "212.144.254.124:3128"
    url = "http://media.daimler.com/Projects/c2c/channel/images/12C242_06.jpg?dlf=904279_1701576_6000_4500_12C242_06.jpg"
    # support_proxy = "http://%s"%ip_port
    http_hanlder = urllib2.HTTPHandler({'http':"http://%s"%ip_port})
    opener = urllib2.build_opener(http_hanlder)
    urllib2.install_opener(opener)
    data = urllib2.urlopen(url).read()
    filename = "benzi.jpg"
    with open(filename,mode='wb') as wf:
        wf.write(data)

def request_error_page():
    # url = "http://baike.baidu.com/view/8523491.htm"
    url = "http://movie.douban.com/celebrity/100000/"
    try:
        response = urllib2.urlopen(url)
        print response.geturl()
    except urllib2.URLError, e:#HTTPError是URLError的子类
        if hasattr(e, 'code'):
            print e.code  == 404
        elif hasattr(e, 'reason'):
            print e.reason
        return
# request_error_page()
def req_open():
    error_url = "http://baike.baidu.com/iew/82521234123491.htm"
    req = urllib2.Request(error_url)
    try:
        urllib2.urlopen(req)
    except urllib2.HTTPError, e:
        print type(e.code)
# req_open()

def urlopen(url, retrise=3):
    try:
        response = urllib2.urlopen(url)
        html = response.read()
    except:
        if retrise > 0:
            return urlopen(url, retrise-1)
        else:
            return ''
    return html

def parse_topic_url():
    book_url = "http://book.jd.com/booksort.html"
    response = urllib2.urlopen(book_url)
    html = response.read()
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='booksort')
    em_level_list = div_level_str.find_all('em')
    topic_url_list = [item.a['href'] for item in em_level_list]
    print topic_url_list[32:-2],len(set(topic_url_list))
    return topic_url_list[32:-2]
# parse_topic_url()
def gen_whole_page_url():
    topic_url_list = parse_topic_url()
    page_url_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    redirect_url_filename = os.path.join(PATH, 'log', 'redirect_page_url')
    timeout_url_filename = os.path.join(PATH, 'log', 'timeout_topic_url')

    with codecs.open(page_url_filename, mode='wb', encoding='utf-8') as wf_page_url,\
    codecs.open(redirect_url_filename, mode='wb', encoding='utf-8') as wf_redirect_url,\
    codecs.open(timeout_url_filename, mode='wb', encoding='utf-8')as wf_timeout_url:
        for topic_url in topic_url_list:
            page_url_list = []
            # page_url = 'http://list.jd.com/1713-3258-3297.html'
            try:
                response = urllib2.urlopen(topic_url, timeout=10)
                if response.geturl() != topic_url:
                    print 'redirect page hrer in url:%s'%topic_url
                    # wf_redirect_url.write(topic_url+'\n')
            except urllib2.HTTPError, e:
                if e.getcode() == 403:
                    raise ValueError('request forbidden')
            except:
                wf_timeout_url.write(topic_url+'\n')
                print 'request timeoutd item_id in url:%s'%topic_url
                continue
            html = response.read()
            soup = BeautifulSoup(html)

            max_page_str = soup.find('div', class_='pagin pagin-m')
            if not max_page_str:
                print 'max_page_str do not match regular expression in url:%s'%topic_url
                continue
            page_size_str = max_page_str.span.text.split('/')[-1]
            page_size = 200 if int(page_size_str)>=200 else int(page_size_str)
            print page_size
            wf_redirect_url.write(str(page_size)+'\n')
            end_url_pattern = "-0-0-0-0-0-0-0-1-1-%d.html"
            head_url = topic_url.replace('.html', '')
            for i in range(1, page_size+1):
                end_url = end_url_pattern%i
                url = ''.join((head_url, end_url))
                page_url_list.append("".join((url, '\n')))
            # print len(page_url_list)
            wf_page_url.writelines  (page_url_list)
            # time.sleep(3)
# gen_whole_page_url()

def read_topic_page_url():
        page_url = 'http://list.jd.com/1713-6929-6930.html'
        # page_url = "http://list.jd.com/1713-3267-3456.html"
        html = urllib2.urlopen(page_url).read()
        soup = BeautifulSoup(html)
        div_level_str = soup.find('div', id='plist')
        print div_level_str
        a_level_list = div_level_str.find_all('a', href=re.compile('http://item'), class_=None, title=True, target='_blank')
        # print a_level_list, len(a_level_list)
        item_url_list = set([item['href'] for item in a_level_list])
        # print item_url_list,len(item_url_list)

        max_page_str = soup.find('div', class_='pagin pagin-m')
        if not max_page_str:
            print 'max_page_str is null'
            return
        page_size_str = max_page_str.span.text.split('/')[-1]
        page_size = 200 if int(page_size_str)>=200 else int(page_size_str)
        print page_size
# read_one_page_url()
def test_page_url():
    filename = os.path.join(PATH, 'sys', 'whole_page_url')
    with open(filename) as f:
        page_size_list =[item.strip() for item in f.readlines()]
        print len(page_size_list), len(set(page_size_list))
# test_page_url()
def read_item_page_url():
    url = 'http://item.jd.com/876229.html'
    html = urllib2.urlopen(url).read()
    soup = BeautifulSoup(html)
    head_info = soup.find('div', id='name')
    print head_info.text
    product_detail_str = soup.find('div', id='product-detail-1')
    print product_detail_str.text.strip()
    # content_level_list = product_detail_str.find_all('div', class_='sub-m m1', clstag=True)
    # content_text_list = [item.text.strip() for item in content_level_list]
    # for con in content_text_list:
    #     print con
    # print content_level_list#, len(content_list)
# read_item_page_url()
def parse_content_list_str():
    html = """<div class="sub-m m1" clstag="shangpin|keycount|bookitem|1312a">
        <div class="sub-mt">
        <h3>编辑推荐</h3>
        </div>
        <div class="sub-mc">
        <div class="con"><p><strong>　　购1000册以上的用户， 本商品提供订制服务。如需加企业LOGO、腰封，或有其他需求，请发邮件至dushuhui@jd.com。请提供您的姓名、电话、公司名称、需购买的商品链接和数量、您要送达的城市，以及具体需求。</strong><br/>　　《人性的弱点全集》的唯一目的就是帮助你解决你所面临的最大问题：如何在你的日常生活、商务活动与社会交往中与人打交道，并有效地影响他人；如何击败人类的生存之敌―― 忧虑，以创造一种幸福美好的人生。当你通过《人性的弱点全集》解决好这一问题之后，其他问题也就迎刃而解了。</p>
        <p><strong>全新改版成功学经典著作：</strong></p>
        <p><a href="http://item.jd.com/11393181.html"><span style="color: rgb(0, 0, 255);"><strong>《思考致富：拿破仑希尔成功圣经》</strong></span></a></p>
        <p><strong> 配套全英文版：</strong></p>
        <p><span style="color: rgb(0, 0, 255);"><strong><a href="http://book.jd.com/10107854.html"><span style="color: rgb(0, 0, 255);">《人性的弱点全集（英文版）》</span></a></strong></span></p></div>
        <div class="more"><a href="javascript:void(0)">查看全部</a></div>
        </div>
        </div>"""
    soup = BeautifulSoup(html)
    print soup.text.strip()
    print '*'*30
    sub_mc_str = soup.find('div', class_='sub-mc')
    print sub_mc_str.text
# parse_content_list_str()
def read_page_url():
    url = 'http://list.jd.com/1713-3260-3338-0-0-0-0-0-0-0-1-1-2.html'
    html = urllib2.urlopen(url)
    soup = BeautifulSoup(html)
    div_level_str = soup.find('div', id='plist')
    div_item_list = div_level_str.find_all('div', class_='item')
    # print len(div_item_list)
    item_id_list = [item['sku'] for item in div_item_list]
    print item_id_list
# read_page_url()

def read_proxy_file():
    proxy_list = []
    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with codecs.open(filename, encoding='utf-8')as f:
        proxy_list.extend([item.strip() for item in f.readlines()])
    return proxy_list
def gen_whole_item_id():
    proxy_list = read_proxy_file()
    proxy_count = len(proxy_list)
    page_url_proxy_count = 0
    if not proxy_list:
        xici_proxy.gen_proxy()
        proxy_list = read_proxy_file()
    ip_port = proxy_list.pop()
    whole_page_url_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    timeout_page_url_filename = os.path.join(PATH, 'log', 'timeout_page_url')
    item_id_filename = os.path.join(PATH, 'sys', 'book_item_ids_2')
    page_url_crawled_filename = os.path.join(PATH, 'log', 'crawled_page_url')
    with codecs.open(whole_page_url_filename, encoding='utf-8')as whole_page_url_f,\
    codecs.open(item_id_filename, mode='wb', encoding='utf-8')as item_id_wf,\
    codecs.open(timeout_page_url_filename, mode='wb', encoding='utf-8') as timeout_url_wf,\
    codecs.open(page_url_crawled_filename, mode='wb', encoding='utf-8')as crawled_url_wf:
        for page_url in [item.strip() for item in whole_page_url_f.readlines()]:
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
# gen_whole_item_id()
def parse_timeout_page_url_file():
    filename = os.path.join(PATH, 'log', 'timeout_page_url')
    no_plist_div_list = []
    no_item_div_list = []
    total_line_list = []
    with codecs.open(filename, encoding='utf-8') as f:
        for line in f.readlines():
            total_line_list.append(line)
            if line.startswith('no_plist_div:'):
                no_plist_div_list.append(line)
            elif line.startswith('no_item_div:'):
                no_item_div_list.append(line)
    item_line_count = len(no_item_div_list)
    plist_list_count = len(no_plist_div_list)
    total_list_count = len(total_line_list)
    print "total line:%s ,\n item line count:%s \n plist line count is %d"%(total_list_count,item_line_count, plist_list_count)
# parse_timeout_page_url_file()
def test_proxy_pop():
    proxy_list = read_proxy_file()
    page_url_proxy_count = 0
    if not proxy_list:
        xici_proxy.gen_proxy()
        proxy_list = read_proxy_file()
    ip_port = proxy_list.pop()
    whole_page_url_filename = os.path.join(PATH, 'sys', 'whole_page_url')
    with codecs.open(whole_page_url_filename, encoding='utf-8') as f:
        for page_url in f.readlines():
            page_url_proxy_count += 1
            try:
                if page_url_proxy_count > 2:
                    if not proxy_list:
                        re_read_proxy_list = read_proxy_file()
                        proxy_list = xici_proxy.get_valid_proxy(re_read_proxy_list)
                        if not proxy_list:
                            xici_proxy.gen_proxy()
                            proxy_list = read_proxy_file()
                    ip_port = proxy_list.pop()
                    page_url_proxy_count = 0
                print page_url_proxy_count
                print ip_port
                time.sleep(0.5)
                # http_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
                # opener = urllib2.build_opener(http_hanlder)
                # html = opener.open(page_url, timeout=15)
            except urllib2.HTTPError, e:
                if e.getcode() == 403:
                    if not proxy_list:
                        re_read_proxy_list = read_proxy_file()
                        proxy_list = xici_proxy.get_valid_proxy(re_read_proxy_list)
                        if not proxy_list:
                            xici_proxy.gen_proxy()
                            proxy_list = read_proxy_file()
                        ip_port = proxy_list.pop()
                    http_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
                    opener = urllib2.build_opener(http_hanlder)
                    html = opener.open(page_url, timeout=15).read().decode('gbk')
                else:
                    continue
            except:
                timeout_info = ''.join(('request_timeout:', page_url, '\n'))
                continue
# test_proxy_pop()
def get_timeout_page_url():
    filename = os.path.join(PATH, 'log', 'timeout_page_url')
    no_plist_div_list = []
    no_item_div_list = []
    total_line_list = []
    with codecs.open(filename, encoding='utf-8') as f:
        for line in f.readlines():

            total_line_list.append(line)
            if line.startswith('no_plist_div:'):
                no_plist_div_list.append(line.replace('no_plist_div:', ''))
            elif line.startswith('no_item_div:'):
                no_item_div_list.append(line.replace('no_item_div:', ''))
    item_line_count = len(no_item_div_list)
    plist_list_count = len(no_plist_div_list)
    total_list_count = len(total_line_list)
    print "total line:%s ,\n item line count:%s \n plist line count is %d"%(total_list_count,item_line_count, plist_list_count)
    timeout_url_filename = os.path.join(PATH, 'sys', 'timeout_url')

    no_plist_div_list.extend(no_item_div_list)
    timeout_url_list = no_plist_div_list[:]
    with codecs.open(timeout_url_filename, mode='wb', encoding='utf-8')as timeout_url_wf:
        timeout_url_wf.writelines(timeout_url_list)
# get_timeout_page_url()
# line = 'no_item_div:http://list.jd.com/1713-3291-6631-0-0-0-0-0-0-0-1-1-110.html'
# if line.startswith('no_item_div:'):
#     print line.replace('no_item_div:', '')
def timestamp():
    datastamp = time.strftime('%m_%d_timeout_page_url')
    print datastamp
    url = "no_item_div:http://list.jd.com/1713-3260-3343-0-0-0-0-0-0-0-1-1-171.html"
    if url.startswith('no_'):
        # print url
        print re.sub('no_(item|plist)_div:', '', url)
def check_con_of_readlines():
    filename = os.path.join(PATH, 'sys', 'book_item_ids')
    with open(filename) as f:
        flist = f.readlines()
        print len(f.readlines())
# check_con_of_readlines()