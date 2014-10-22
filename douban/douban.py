__author__ = 'huafeng'

from apscheduler.scheduler import Scheduler

from douban_spider import DoubanCrawler

scheduler = Scheduler(daemonic = False)

@scheduler.cron_schedule(hour=22, minute='55')
def douban_crawler():
    douban = DoubanCrawler()
    douban.main()
scheduler.start()

