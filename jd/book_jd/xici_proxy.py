__author__ = 'huafeng'
#coding:utf-8
import os
import re
import time
import codecs
import urllib2
from bs4 import BeautifulSoup

PATH = os.path.dirname(os.path.abspath(__file__))

def Varify_proxy(ip_port):
    '''被封的IP'''
    # ip_port = "58.20.127.178:3128"
    url = 'http://list.jd.com/1713-3260-3338-0-0-0-0-0-0-0-1-1-2.html'
    proxy_hanlder = urllib2.ProxyHandler({'http':'http://%s'%ip_port})
    opener = urllib2.build_opener(proxy_hanlder)
    urllib2.install_opener(opener)
    try:
        resopnse = urllib2.urlopen(url, timeout=8).read().decode('gbk')
        # print resopnse.read().decode('gbk')
    except urllib2.URLError, e:
        if e.reason:
            print e.reason
            return
        else:
            return
    except:
        print 'timed item_id...in ip_port:%s'%ip_port
        return

    soup = BeautifulSoup(resopnse)
    div_level_str = soup.find('div', id='plist')
    # print div_level_str
    div_item_list = div_level_str.find_all('div', class_='item')
    item_id_list = [item['sku'] for item in div_item_list]

    if item_id_list:
        print 'success ip_port:%s'%ip_port
        return True
    else:
        return

def write_proxy_into_file(http_proxy_list):
    '''过滤已被查封的IP,并把有效代理写入到本地xici_proxy文件中'''
    com_str_list = [item+'\n' for item in http_proxy_list if Varify_proxy(item)]
    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
        wf.writelines(com_str_list)

def check_exists_proxy_file():
    '''过滤当前代理文件xici_proxy中有效proxy, 并写入到本地文件'''
    proxy_filename = os.path.join(PATH, 'sys', 'xici_proxy')
    new_proxy_filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with codecs.open(proxy_filename, encoding='utf-8') as proxy_filename_f:
        new_proxy_list = [item for item in proxy_filename_f.readlines() if Varify_proxy(item)]
    with codecs.open(new_proxy_filename, mode='wb', encoding='utf-8') as new_proxy_filename_wf:
        new_proxy_filename_wf.writelines(new_proxy_list)
        print len(new_proxy_list)

def get_valid_proxy(proxy_list):
    '''返回有效的代理'''
    ip_port_list = [item for item in proxy_list if Varify_proxy(item)]
    return ip_port_list

def gen_proxy():
    url_pattern = "http://www.xici.net.co/nn/%s"
    url_list = [url_pattern%str(i) for i in range(1,4)]

    filename = os.path.join(PATH, 'sys', 'xici_proxy')
    with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
        for url in url_list:
            try:
                html = urllib2.urlopen(url, timeout=15).read()
            except:
                time.sleep(60)
                try:
                    html = urllib2.urlopen(url, timeout=15).read()
                except:
                    continue
            soup = BeautifulSoup(html)
            tr_level_list = soup.find_all('tr')
            td_level_list = [item.find_all('td') for item in tr_level_list]
            ip_port_type_list = [(param[1].text, param[2].text, param[5].text) for param in td_level_list if len(param)>5]
            matched_ip_port_list = [item for item in ip_port_type_list if item[-1] != 'HTTPS']
            com_str_list = [":".join((item[0], item[1])) for item in matched_ip_port_list]
            temp_proxy_list = [item+'\n' for item in com_str_list if Varify_proxy(item)]
            wf.writelines(temp_proxy_list)

if __name__ == "__main__":
    # gen_proxy()
    check_exists_proxy_file()