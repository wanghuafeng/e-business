__author__ = 'huafeng'

import time
import urllib2
import gevent
from bs4 import BeautifulSoup
import gevent.monkey

gevent.monkey.patch_socket()

hosts = ["http://www.baidu.com", "http://www.amazon.com","http://www.ibm.com",
         "http://www.python.org","http://www.microsoft.com"]

def read(host):
    try:
        context = urllib2.urlopen(host)
    except urllib2.URLError:
        print "load %s failure." %host
        return
    try:
        title = BeautifulSoup(context).title.string
    except:
        print "paser %s tile failure" %host
        return
    print "%s  : %s" %(host,title)

def concuyRead():
    start = time.time()
    threads = []
    for i in range(30):
        for host in hosts:
            threads.append(gevent.spawn(read,host))
    gevent.joinall(threads)
    end = time.time()
    print "Elapsed Time : %d" %(end-start)

if __name__ == '__main__':
    concuyRead()
