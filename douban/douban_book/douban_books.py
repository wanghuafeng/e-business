#!-*- coding:utf-8 -*-
import re
import json
import codecs
import random
import time
import logging
from bs4 import BeautifulSoup
from result_db import db
from pyspider.libs.base_handler import *
logger = logging.getLogger("douban")


class Handler(BaseHandler):

    crawl_config = {
    }

    @every(minutes=24 * 60)
    def on_start(self):
        start_url = 'https://book.douban.com/tag/'
        self.crawl(start_url, callback=self.topic_page, force_update=True)

    @config(age=3 * 24 * 60 * 60)
    def topic_page(self, response):
        html = response.content
        soup = BeautifulSoup(html, 'lxml')
        tag_cols = soup.find_all('table', class_='tagCol')
        root_url = 'https://book.douban.com'
        for tag_col in tag_cols:
            td_list = tag_col.find_all('td')
            tag_url_list = [root_url+item.find('a').get('href') for item in td_list if item]
            for tag_url in tag_url_list:
                for index in range(50):  # 每个topic可以翻50页，每页20条数据
                    tag_page_url = '%s?start=%s&type=S' % (tag_url, index*20)
                    time.sleep(random.randint(0, 3))
                    self.crawl(tag_page_url, callback=self.tag_detail_page, force_update=True)

    @config(priority=2)
    def tag_detail_page(self, response):
        try:
            content = response.content
            soup = BeautifulSoup(content, 'lxml')
            subject_item_list = soup.find_all('li', class_='subject-item')
            for subject_item in subject_item_list:
                # subject_item = soup.find('li', class_='subject-item')
                score = subject_item.find('span', class_='rating_nums').text
                comment_count = re.search('\d+', subject_item.find('span', class_='pl').text).group()
                book_name = subject_item.find('h2', class_="").text.strip()
                book_id_url = subject_item.find('h2', class_="").a.get('href')
                book_id = re.search('\d+', book_id_url).group() if book_id_url else ''
                pub_info_list = subject_item.find('div', class_='pub').text.split('/')
                author_name, publish_time = pub_info_list[0], pub_info_list[-2]
                data = {
                    'book': book_name,
                    'book_id': book_id,
                    'author_name': author_name.strip(),
                    'score': score,
                    'comment_count':comment_count,
                    'publish_time': publish_time
                }
                return data
        except BaseException as e:
            logger.error('=--------------err_msg=%s' % e.message)

    def on_result(self, result):
        super(Handler, self).on_result(result)
        logger.info('------------------------%s' % result)
        assert self.task['taskid']
        assert self.task['project']
        assert self.task['url']
        if not result:
            return
        try:
            db._replace(**result)
        except BaseException as e:
            logger.info('exec db failed, err_msg=%s' % e)