__author__ = 'huafeng'
import  codecs
import os
PATH = os.path.dirname(os.path.abspath(__file__))
filename = os.path.join(PATH, 'item_id_bak/fruit_item_id')
with codecs.open(filename, encoding='utf-8') as f:
    item_id_list= f.readlines()
    item_id_set = set(item_id_list)
    list_length, set_length = len(item_id_list), len(item_id_set)
    print list_length, set_length
    # item_id_set = set(f.readlines())
    # print len(item_id_list), len(item_id_set)
    # item_id_set = set(f.readlines())
    # with codecs.open(filename, mode='wb', encoding='utf-8') as wf:
    #     wf.writelines(item_id_set)
