__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import urllib
import codecs
import urllib2
import logging
from bs4 import BeautifulSoup

HEAD_URL = "http://tt.mop.com"

def gen_hot_topic_urls():
    url = "http://tt.mop.com/topic/list_209_43_0_1434_1.html##"
    html = urllib2.urlopen(url).read().decode("gbk")
    soup = BeautifulSoup(html)
    div_level = soup.find_all('div', {'class':'left_menu_con_center'})
    ul_level = div_level[0].find_all('ul', {'style':'display: block'})
    li_level = ul_level[0].find_all('li', {'class':'active_menu'})
    url_list = [tag.find('a')['href'] for tag in li_level]
    print len(url_list)#31
    print(url_list)
    ##write urls into file :hot_topic_urls
    with codecs.open("hot_topic_urls",mode="wb", encoding="utf-8")as wf:
        head_url = "http://tt.mop.com"
        for end_url in url_list:
            url = head_url+end_url+'\n'
            # wf.write(url)

PATH = os.path.dirname(os.path.abspath(__file__))
class MopCrawl:
    def __init__(self):
        self.whole_page_urls_list = self._load()
        self.msg_comment_set = set()
        self._gen_log()

    def _gen_log(self):
        timestamp = time.strftime("%Y_%m_%d")
        filename = "".join((timestamp, '_mop_spider.log'))
        logfile = os.path.join(PATH, 'log', filename)
        self.logger = logging.getLogger(__name__)
        self.logger.setLevel(logging.DEBUG)
        log_file = logging.FileHandler(logfile)
        log_file.setLevel(logging.DEBUG)
        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        log_file.setFormatter(formatter)
        self.logger.addHandler(log_file)

    def _load(self):
        '''加载所有url地址到内存中，保存到self.msg_comment_set中'''
        temp_url_list = []
        filename = os.path.join(PATH, 'sys/whole_page_urls')
        if not os.path.isfile(filename):
            raise ValueError("No such file :%s"%filename)
        with open(filename) as f:
            for url in f.readlines():
                url = url.strip()
                temp_url_list.append(url)
        return temp_url_list

    def get_page_comment_content(self, url_tuple):
        '''接受页面url列表，解析url页面内容，抓取msg，comment信息'''
        if not isinstance(url_tuple, tuple):
            raise TypeError("input param is not tuple")

        for url in url_tuple:
            try:
                html = urllib2.urlopen(url, timeout=10).read().decode('gbk')
                soup = BeautifulSoup(html)
            except:
                self.logger.error("request timed item_id in url: %s"%url)
                continue

            #msg 信息
            msg_div_level = soup.find_all('div', class_='tz_mainP')
            if msg_div_level:
                msg_content = msg_div_level[0].get_text().strip()
                msg_content = re.sub(r"\s*", "", msg_content)
                self.msg_comment_set.add(msg_content)
                # print(msg_content)
            else:
                self.logger.debug("no msg_content in url: %s"%url)

            #comment 信息
            comment_div_level = soup.find_all('div', class_='area htlb')
            if comment_div_level:
                comment_div_level_str = comment_div_level[0] if len(comment_div_level) is 1 else comment_div_level[1]
                comment_items_level_list = comment_div_level_str.find_all('div', class_='box2 js-reply')
                # print comment_items_level_list[0]
                # print len(comment_items_level_list)#comment count each page : 50
                comment_con_level_list = [item.find('div', class_='h_nr js-reply-body') for item in comment_items_level_list]
                # print comment_con_level_list[0]
                comment_con_list = [item.get_text().strip() for item in comment_con_level_list]
                for comment in comment_con_list:
                    self.msg_comment_set.add(comment)
                    # print comment
            else:
                self.logger.debug("no comment in url: %s"%url)

            time.sleep(5)

            check_next_page = soup.find_all('div', class_='page')#if len =1,no next page,else not next page
            if check_next_page:
                next_page_str = check_next_page[1] if len(check_next_page) is 2 else check_next_page[0]
                end_page_str = next_page_str.find('a', class_='endgray')#['href']
                if end_page_str:#如果comment多页
                    # self.logger.info("multi comment page in url%s"%url)
                    comment_page_list = []
                    end_page_url_str = end_page_str['href']#/read_14780013_6_0.html
                    splited_url = end_page_url_str.split('_')
                    page_size = int(splited_url[-2])
                    # print page_size
                    self.logger.info("multi comment page in url: %s and comment_page_size is %s"%(url, page_size))
                    for count in range(2, page_size+1):
                        back_url = "_".join((splited_url[0], splited_url[1], str(count), splited_url[-1]))
                        url = "".join((HEAD_URL, back_url))
                        # print  url
                        comment_page_list.append(url)#将所有的comment page url 写入comment_page_url

                    self.read_comment_page(comment_page_list)

    def read_comment_page(self, comment_page_list):
        '''如果comment为多页，则读取所有page'''
        if not isinstance(comment_page_list, list):
            raise TypeError("param of comment_page_list is not list")
        # comment_page_list = ["http://tt.mop.com/read_4855994_1_0.html"]
        for url in comment_page_list:
            try:
                self.logger.info("mutlti comment_page url open here is: %s"%url)
                comment_html = urllib2.urlopen(url, timeout=10).read().decode('gbk')
                soup = BeautifulSoup(comment_html)
            except:
                self.logger.error("request timed item_id in url: %s"%url)
                continue
            comment_div_level = soup.find_all('div', class_='area htlb')#comment 信息
            if not comment_div_level:
                continue
            comment_div_level_str = comment_div_level[0] if len(comment_div_level) is 1 else comment_div_level[1]
            comment_items_level = comment_div_level_str.find_all('div', class_='box2 js-reply')
            comment_con_level_list = [item.find('div', class_='h_nr js-reply-body') for item in comment_items_level]
            # print comment_con_level_list[0]
            comment_con_list = [item.get_text().strip() for item in comment_con_level_list]
            for comment in comment_con_list:
                self.msg_comment_set.add(comment)
                # print(comment)
                # print con
            time.sleep(5)

    def write_msg_comment_into_file(self):
        '''满足条数限制以后将抓取信息写入到本地'''
        timestamp = time.strftime('%Y_%m_%d_%H%M%S')
        confile = "".join((timestamp, '_mop.txt'))
        output_filename = os.path.join(PATH, 'out', confile)
        with codecs.open(output_filename, mode="wb", encoding="utf-8") as wf:
            temp_list = [item+"\n" for item in self.msg_comment_set]
            wf.writelines(temp_list)

    def main(self):
        '''主函数，遍历self.whole_page_urls_list中的url地址'''
        # url = "http://tt.mop.com/topic/list_209_210_0_2_1.html"
        with open("sys/whole_page_urls") as f:
            for url in f.readlines():
                url = url.strip()

                try:
                    html = urllib2.urlopen(url, timeout=10).read().decode("gbk")
                    soup = BeautifulSoup(html)
                except:
                    self.logger.debug("request timed item_id in url: %s"%url)
                    continue
                table_level_list = soup.find_all('table', {"class":"tiezi_table"})

                ##页面普通信息url列表:cblack title
                if not table_level_list:
                    continue
                td_level_list = table_level_list[0].find_all('td', {'class':'cblack title'}) #len: 97
                msg_url_list = ["".join((HEAD_URL, item.find_all(href=re.compile('read_[\d]+\_\d\_\d\.html'))[0]['href'])) for item in td_level_list]
                if not msg_url_list:
                    continue
                hot_title_url_tuple = tuple(msg_url_list)

                self.get_page_comment_content(hot_title_url_tuple)
                if len(self.msg_comment_set) > 2000:
                    self.write_msg_comment_into_file()
                    self.msg_comment_set.clear()#写入后将msg_comment_set清空，节省出内存空间

                time.sleep(5)

        ##页面所有信息url列表：whole mes in page
        # whole_td_level_list =["".join((HEAD_URL, item['href'])) for item in table_level_list[0].find_all(href=re.compile('read_[\d]+\_\d\_\d\.html'))]
        # print whole_td_level_list
        # print(len(whole_td_level_list))

mopspider = MopCrawl()
mopspider.main()

        ##页面热点信息url列表：hot topic 11
        # hot_td_level_list = table_level_list[0].select('td[class^=hot]')
        # hot_title_url_list = ["".join((HEAD_URL, item.find_all('a')[1]['href'])) for item in hot_td_level_list]
        # print len(hot_title_url_list)
        # hot_title_url_tuple = tuple(hot_title_url_list)
        # self.get_page_content_comment(hot_title_url_tuple)

        ##页面往日热门信息url列表：title green 4
        # green_td_level_list = table_level_list[0].select('td[class^=title]')
        # print len(green_td_level_list)
        # green_title_back_url_list = ["".join((HEAD_URL, item.find_all('a')[1]['href'])) for item in green_td_level_list]

        ##msg信息的时间参数列表
        # time_level_list = table_level_list[0].find_all('td', {'class':'time'})
        # time_list = [param.get_text() for param in time_level_list]
        # print len(time_list)
