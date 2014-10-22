#coding:utf-8
import re
from bs4 import BeautifulSoup

soup = BeautifulSoup('<b class="boldest">Extremely bold</b>')
tag = soup.b
# print tag.name# b
# print tag['class']# ['boldest']返回list，此处不能执行点号操作

# html = '<li class="active_menu"><a href="/topic/list_76_78_0_0.html">单身公寓</a></li>'
# tag = BeautifulSoup(html)
# print tag.li#<li class="active_menu"><a href="/topic/list_76_78_0_0.html">单身公寓</a></li>
# print tag.find('a')#<a href="/topic/list_76_78_0_0.html">单身公寓</a>
# print tag.find('a')['href']#/topic/list_76_78_0_0.html
def com_time_str():
    import datetime
    t = "2014-04-08 09:25:38"
    d = t.split()[0]
    print(d)
    now = datetime.date.today()
    print now
    print d>str(now)

def prase_html():
    html = """<tr data-sid="9863770">
            <td class="cblack title"><span><a class="js-icon" href="#"><font class="cblack title">◆</font> </a>
            <a href="/read_9863770_1_0.html" target="_blank" title="猫扑网上如何设置签名">猫扑网上如何设置签名</a></span>
            <span class="add"></span></td>

            <td><a href="/user/434544253" style="cursor:pointer" target="_blank">凤凰涅槃之窗</a></td>

            <td>58/<b><font color="ff0000">735</font></b></td>

            <td class="time">2014-04-10 10:36:54</td>
            </tr>"""
    soup = BeautifulSoup(html)

    tr_level = soup.find_all('td', {'class':'cblack title'})
    a_level = soup.find_all(href=re.compile('read_[\d]+\_\d\_\d\.html'))
    print  a_level[0]['href']
    # url_list = [item.get_text() for item in tr_level]
    # splited_word = url_list[0].split()
    # print splited_word[1]
    # print tr_level[0]
    #时间信息参数的获取
    time_level_list = soup.find_all('td', {'class':'time'})
    time_list = [param.get_text() for param in time_level_list]
    # print time_list
    # print tr_level
# prase_html()
def hot_title():
    hot_html = '''
                <tr data-sid="14782828">
                <td class="hot title tieStyle_zhanZD1 tieStyle_yc7">
                <span><a class="js-icon" href="#">
                <font class="hot title tieStyle_zhanZD1">◆</font></a>
                 <a href="/read_14782828_1_0.html" target="_blank"
                 title="【活动：黑涩会造型大赛】出来混的！总得有个样！">【活动：黑涩会造型大赛】出来混的！总得有个样！
                 </a></span><span class="add">原创贴</span></td>

                <td><a href="/user/443625314" style="cursor:pointer" target="_blank">贴贴土豪盟主</a></td>
                <td><b><font color="ff0000">175</font></b>/<span style="display:inline-block;FILTER: glow(color=#ffff00, strength=2);color:#ff3c00;font-weight:bold">101322</span></td>
                <td class="time">2014-04-10 18:37:12</td>
                </tr>'''
    soup = BeautifulSoup(hot_html)
    a_level = soup.find_all(href=re.compile('read_[\d]+\_\d\_\d\.html'))
    # print a_level[0]['href']
    td_level_list = soup.select('td[class^="hot"]')
    print td_level_list[0].find_all('a')[1]['href'] #/read_14782828_1_0.html
# hot_title()

def list_method():
    a = [1,2,3]
    b = [4,5,6]
    print [(a[i],b[i]) for i in range(len(a))]

# url = "http://tt.mop.com/topic/list_237_238_0_0.html"
# head_url = "_".join(url.split("_")[:-1])
# for i in range(10):
#     print head_url + "_%s_1.html"%str(i)

def gen_whole_urls():
    '''把所有url地址写入whole_page_urls'''
    url_list = []
    with open('sys/hot_topic_urls') as f:
        for url in f.readlines():
            url = url.strip()
            head_url = "_".join(url.split("_")[:-1])
            for i in range(1,21):
                url = head_url + "_%s_1.html"%str(i) + "\n"
                url_list.append(url)
    print url_list
    print(len(url_list))
    # with open('whole_page_urls', mode="wb") as wf:
    #     wf.writelines(url_list)
# gen_whole_urls()
def gen_msg_urls():
    '''以发帖时间排列的帖子的url地址：*_1_2.html
    每天发表新帖子数量在6000以内'''
    msg_url_list = []
    with open('sys/hot_topic_urls') as f:
        for url in f.readlines():
            url = url.strip()
            head_url = "_".join(url.split("_")[:-1])
            for i in range(1,3):
                url = head_url + "_%s_2.html"%str(i) + "\n"
                msg_url_list.append(url)
    print msg_url_list
    print(len(msg_url_list))
    with open('sys/msg_urls', mode="wb") as wf:
        wf.writelines(msg_url_list)
# gen_msg_urls()
def gen_comment_urls():
    '''以评论回复时间排列的帖子的url地址：*_1_1.html
    取2页，mop中每天回复的帖子数量在6000以内'''
    comment_url_list = []
    with open('sys/hot_topic_urls') as f:
        for url in f.readlines():
            url = url.strip()
            head_url = "_".join(url.split("_")[:-1])
            for i in range(1,4):
                url = head_url + "_%s_1.html"%str(i) + "\n"
                comment_url_list.append(url)
    print comment_url_list
    print(len(comment_url_list))
    with open('sys/comment_urls', mode="wb") as wf:
        wf.writelines(comment_url_list)
# gen_comment_urls()
def gen_comment_page_size():
    endgray = '<a class="endgray" href="/read_14780013_6_0.html">尾页</a>'
    soup = BeautifulSoup(endgray)
    url = soup.find('a', class_='endgray')['href']
    splited_url = url.split('_')
    print splited_url
    page_size = int(splited_url[-2])
    # print page_size
    for count in range(2, page_size+1):
        url = "_".join((splited_url[0], splited_url[1], str(count), splited_url[-1]))
        print  url
# gen_comment_page_size()
def test_isintence():
    a = [1,2]
    if not isinstance(a, list):
        print 'yes a is list'

def get_comment_content():
    html = """<div class="box2 js-reply" data-rid="566614">
    <ul class="htC">
    <li class="htc1">
    <div class="fl">
    <span class="h_lc louceng">1F</span> <input class="1F_item_reply_id" type="hidden" value="566614"/>
    <a class="h_yh" href="/user/419191292" target="_blank">hcuhsadr</a>
    <span class="h_fs"> <a href="http://tt.mop.com/read_3911348.html" target="_blank">(蓝猫)</a></span>
    </div>
    <div class="fr" style="padding-right: 20px">
    </div>
    </li>
    <li class="htc2">
    <div class="h_nr js-reply-body">
                                            好爱学习哦
    <img data-original="http://tinyurl.com/243rzqa" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/2ejjy4v" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/2emrkpc" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/27sjg5d" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/2d92rgl" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/29ttmcj" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/2e78hll" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/2ayv8eo" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/2fyunpq" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/29eonlt" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/2db5psz" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/2597hn7" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/>
    <img data-original="http://tinyurl.com/276obz2" height="0" name="replyImgs" src="http://txt.mop.com/ttv2/images/290.jpg" width="0"/></div>
    <div class="h_lz">2010-09-04 12:34:48</div>
    </li>
    <li class="htc3">
    <div class="fl">
    <!--
                <a class="i_gz" href="#">关注</a>
                 -->
    <a class="i_ch" href="http://msg.hi.mop.com/SendMessage.do?id=419191292" target="_blank">传呼</a>
    </div>
    <div class="fr">
    <div class="operate js-operate" data-uid="419191292">
    <!--
             <a class="an_hf" href="#">回复</a>
             <a class="an_yy" onclick="Mopper.page.read.quotReply($(this))" href="#replytext">引用</a>
              -->
    </div>
    </div>
    </li>
    </ul>
    </div>"""
    soup = BeautifulSoup(html)
    comment_con_div_level = soup.find('div', class_='h_nr js-reply-body')
    comment_con = comment_con_div_level.get_text().strip()
    # print len(comment_con.strip())
    print comment_con
# get_comment_content()
def check_current_path():
    import os
    PATH = os.path.dirname(os.path.abspath(__file__))
    print PATH
import os
import time
def data_parse():
    PATH = os.path.dirname(__file__)
    timestamp = time.strftime('%Y_%m_%d')
    filename = "".join((timestamp, '_mop_spider.log'))
    logfile = os.path.join(PATH, 'log', filename)
    # print logfile

    timestamp = time.strftime('%Y_%m_%d_%H%M%S')
    confile = "".join((timestamp, '_mop.txt'))
    output_filename = os.path.join(PATH, 'out', confile)
    # print output_filename
    # print(timestamp)
    msg_comment_set = [1,2,3,4]
    temp_list = ["".join((str(item), "\n")) for item in msg_comment_set]
    print temp_list
    for var in temp_list:
        print var

    timestamp = time.strftime('%Y-%m-%d')
    print timestamp
    t = "2014-04-17 15:31:35"
    print t.split()[0]

import datetime
now_time = datetime.datetime.now()
yesterday = datetime.datetime.now() + datetime.timedelta(days=-1)
print yesterday.strftime("%Y-%m-%d")
