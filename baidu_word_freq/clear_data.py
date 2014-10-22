__author__ = 'huafeng'
#coding:utf-8
import os
import codecs
import re

path = os.path.dirname(os.path.abspath(__file__))

def pure_freq():

    filename = os.path.join(path, 'out/baidu_word_freq')
    if not os.path.isfile(filename):
        raise ValueError('No such file:%s'%filename)
    word_freq_dic  = {}
    with codecs.open(filename,encoding="utf-8") as f:
        for line in f.readlines():
            line = line.strip()
            splited_line = line.split("\t")
            if len(splited_line) is not 2:
                print len(splited_line),splited_line
                raise ValueError("length is splited line is not 2 in file:%s"%filename)
            word = splited_line[0]
            freq_comma = splited_line[1]
            word_freq_dic[word] = freq_comma

    pure_freq_list = ["".join(re.findall("[\d]+", item)) for item in word_freq_dic.values()]

    word_freq_list = ['\t'.join(item)+'\n' for item in map(lambda x,y:(x,y), word_freq_dic.keys(),pure_freq_list)]
    # com_str_list = ['\t'.join(item)+'\n' for item in word_freq_list]
    # for word_fre in word_freq_list:
    #     print word_fre
    with codecs.open('baidu_HZOut.txt', mode='wb', encoding="utf-8")as wf:
        wf.writelines(word_freq_list)
# pure_freq()

def make_word_in_order():
    filename = os.path.join(path, 'sys/HZOut.txt')
    word_in_order_list = []
    temp_word_set = set()
    if not os.path.isfile(filename):
        raise ValueError('no such file:%s'%filename)
    with codecs.open(filename, encoding="utf-16") as f:
        for line in f.readlines():
            line = line.strip()
            if not line.startswith(';'):
                splited_line = line.split("\t")
                word = splited_line[0]
                if not word in temp_word_set:
                    word_in_order_list.append(word)
                temp_word_set.add(word)
    # print len(word_in_order_list), len(temp_word_set)#20870

    baidu_freq_file = os.path.join(path, 'sys/baidu_HZOut.txt')
    pure_word_freq_dic = {}
    with codecs.open(baidu_freq_file, encoding="utf-8") as baidu:
        for line in baidu.readlines():
            splited_line = line.split('\t')
            word = splited_line[0]
            freq = splited_line[1]
            pure_word_freq_dic[word] = freq


    com_str_list = ["\t".join((item, pure_word_freq_dic[item])) for item in word_in_order_list]
    print len(com_str_list)

    write_filename = os.path.join(path, "baidu_words_freq")
    with codecs.open(write_filename, mode="wb", encoding="utf-8") as wf:
        wf.writelines(com_str_list)

make_word_in_order()

