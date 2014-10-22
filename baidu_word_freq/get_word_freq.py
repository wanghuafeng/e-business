__author__ = 'huafeng'
#coding:utf-8
import urllib2
import os
import re
import codecs
import time
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))
class CrawlFreq:

    def __init__(self):
        self.words_list = []
        self._gen_words_list()

    def _gen_words_list(self):
        filename = os.path.join(PATH, 'sys', 'wrod_without_freq.txt')
        with codecs.open(filename) as f:
            temp_word_list = [item.strip() for item in f.readlines()]
            self.words_list.extend(temp_word_list)

    def read_item_url(self):
        timeout_url_list = []
        url_pattern = 'http://www.baidu.com/s?wd="%s"&fr=wenku'
        num_pattern = re.compile(r'\d+')
        filename = os.path.join(PATH, 'out', 'words_freq_0528')
        with codecs.open(filename, mode='a')as f:
            self.words_list = [u'\u67cf\u79c0\u6f2b\u753b', u'\u5e2e\u4ec0\u4e48\u5fd9\u6bcf\u4e2a', u'\u5305\u5c71\u8001\u5988', u'\u523a\u9752\u6709\u8138', u'\u5361\u9e7f\u519c\u5e84', u'\u8ba9\u6cea\u5316\u4f5c\u76f8\u601d\u5440', u'\u5916\u59d3\u5144\u5f1f', u'\u665a\u4e0a\u4e0d\u7761', u'\u559c\u6b22\u505a\u7684', u'\u559c\u9a6c\u62c9\u96c5\u5c71', u'\u76f8\u8c8c\u6b66\u50e7', u'\u6cbf\u6c5f\u5927\u9053', u'\u4e00\u6839\u5934\u53d1', u'\u4e00\u89c9\u7761\u9192', u'\u4e00\u8def\u6b4c\u5531', u'\u4e00\u76f4\u4e0b\u53bb', u'\u4e00\u5b57\u4e00\u952e\u7ae0\u9c7c\u8f93\u5165', u'\u610f\u601d\u4e00\u4e0b', u'\u4f18\u8c08\u5b9d\u5b9d\u4e0d\u9519\u7684', u'\u6709\u8bdd\u597d\u597d\u8bf4', u'\u53c8\u4e0d\u597d\u5403', u'\u53c8\u4e0d\u80fd\u5403', u'\u4e0e\u4e0d\u7b11\u732b\u60f3\u53bb', u'\u5728\u7ed9\u6211\u8bf4', u'\u5728\u5bb6\u5462\u554a', u'\u5728\u54ea\u91cc\u4f4f', u'\u7cdf\u8001\u5934\u5b50', u'\u600e\u4e48\u4e0d\u56de\u6211', u'\u600e\u4e48\u8fc7\u53bb', u'\u600e\u4e48\u4e00\u4e0b', u'\u627e\u5230\u6ca1\u6709', u'\u627e\u5230\u4f60\u4e86', u'\u8fd9\u4e48\u660e\u663e', u'\u81ea\u5c0a\u5b59\u5b59', u'\u6700\u6f6e\u6728\u4e43\u4f0a', u'\u6700\u8001\u60c5\u503a', u'\u6700\u5f3a\u723d\u7ea6']
            for word in self.words_list:
                url = url_pattern%word.encode('gbk')
                try:
                    html = urllib2.urlopen(url, timeout=15).read()
                except:
                    try:
                        html = urllib2.urlopen(url, timeout=15).read()
                    except:
                        timeout_url_list.append(url)
                        continue
                soup = BeautifulSoup(html)
                span_level_str_list = soup.find_all('span', class_='nums')
                if span_level_str_list:
                    span_level_str = span_level_str_list[0]
                    num_text = span_level_str.get_text()
                    num = "".join(num_pattern.findall(num_text))
                    com_str = "\t".join((word, str(num))) + '\n'
                else:
                    com_str = "\t".join((word, "0")) + '\n'
                print com_str.strip()
                # f.write(com_str.encode('gbk'))
                # time.sleep(2)
                
        # if timeout_url_list:
        #     timeout_filename = os.path.join(PATH, 'timeout_url')
        #     with codecs.open(timeout_filename, mode='wb') as wf:
        #         timeout_url_list = [item+'\n' for item in timeout_url_list]
        #         wf.writelines(timeout_url_list)

if __name__ == "__main__":
    freq_crawler = CrawlFreq()
    freq_crawler.read_item_url()

