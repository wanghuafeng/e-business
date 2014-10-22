__author__ = 'huafeng'
import os
import sys
import codecs

PATH = os.path.dirname(os.path.abspath(__file__))
walk_path = os.path.join(PATH, 'item_id_bak')
def check_length():
    for root, subdirs, files in os.walk(walk_path):
        for id_file in files:
            id_filename = os.path.join(walk_path,id_file)
            with codecs.open(id_filename) as f:
                item_id_list = f.readlines()
                item_id_set = set(item_id_list)
                list_length, set_length = len(item_id_list), len(item_id_set)
                print '%s:%s ==> %s'%(id_file, list_length, set_length)
                # with codecs.open(id_filename, mode='wb', encoding='utf-8') as wf:
                #     wf.writelines(item_id_set)
check_length()
