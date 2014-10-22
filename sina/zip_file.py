#coding:utf-8
import zipfile
import time
import os

PATH = os.path.dirname(os.path.abspath(__file__))
date_dir = time.strftime('%Y_%m_%d')
def zip_file():
    '''压缩data_dir目录下的shtml页面'''
    src_file = os.path.join(PATH,'html', date_dir)
    zipfilename = os.path.join(PATH, 'html', '%s.zip'%date_dir)
    filelist = []
    for root, dirs, files in os.walk(src_file):
        for filename in files:
           filelist.append(os.path.join(root, filename)) 
    zf = zipfile.ZipFile(zipfilename, mode='w', compression=zipfile.ZIP_DEFLATED)
    for fullpath_name in filelist:
        arcname = fullpath_name[len(src_file):]
        zf.write(fullpath_name, arcname)
    zf.close()
# zip_file()
def remove_orginal_dir():
    '''删除date_dir目录'''
    date_dir_fullpath = os.path.join(PATH, 'html', date_dir)
    print date_dir_fullpath
    os.system('rm -r %s'%date_dir_fullpath)
remove_orginal_dir()