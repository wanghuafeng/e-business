__author__ = 'huafeng'

from apscheduler.scheduler import Scheduler

from mop_crawler import MopCrawl

scheduler = Scheduler(daemonic = False)

@scheduler.cron_schedule(hour=23,minute='55')
def mop_spider():
    mop = MopCrawl()
    mop.main()

scheduler.start()