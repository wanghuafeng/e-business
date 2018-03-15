#!-*- coding:utf-8 -*-
import re
from bs4 import BeautifulSoup
from pyspider.libs.base_handler import *

class Hanlder(BaseHandler):
    crawl_config = {
    }

    @every(seconds=10)
    def on_start(self):
        start_url = 'https://book.douban.com/tag/'
        self.crawl(start_url, callback=self.topic_page)

    def topic_page(self, response):
        html = response.content
        soup = BeautifulSoup(html, 'lxml')
        tag_cols = soup.find_all('table', class_='tagCol')
        root_url = 'https://book.douban.com'
        for tag_col in tag_cols:
            td_list = tag_col.find_all('td')
            tag_url_list = [root_url+item.find('a').get('href') for item in td_list if item]
            for tag_url in tag_url_list:
                self.crawl(tag_url, callback=self.tag_detail_page)

    def tag_detail_page(self, response):
        """
            <h2 class="">
            <a href="https://book.douban.com/subject/25862578/" onclick="moreurl(this,{i:'0',query:'',subject_id:'25862578',from:'book_subject_search'})" title="解忧杂货店">
            解忧杂货店
            </a>
            </h2>
            <span class="rating_nums">8.6</span>
            <span class="pl">
            (272811人评价)
            </span>
            <div class="pub">
            [日] 东野圭吾 / 李盈春 / 南海出版公司 / 2014-5 / 39.50元
            </div>
        """
        # tag_url = 'https://book.douban.com/tag/小说'
        # content = requests.get(tag_url).content
        content = response.content
        soup = BeautifulSoup(content, 'lxml')
        subject_item = soup.find('li', class_='subject-item')
        score = subject_item.find('span', class_='rating_nums').text
        comment_count = re.search('\d+', subject_item.find('span', class_='pl').text).group()
        book_name = subject_item.find('h2', class_="").text.strip()
        book_id_url = subject_item.find('h2', class_="").a.get('href')
        book_id = re.search('\d+', book_id_url).group() if book_id_url else ''
        pub_info_list = subject_item.find('div', class_='pub').text.split('/')
        author_name, publish_time = pub_info_list[0], pub_info_list[-2]
        return {
            'book': book_name,
            'book_id': book_id,
            'author_name': author_name.strip(),
            'score': score,
            'comment_count':comment_count,
            'publish_time': publish_time
        }

