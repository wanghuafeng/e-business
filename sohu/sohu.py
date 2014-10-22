__author__ = 'huafeng'

from apscheduler.scheduler import Scheduler

from sohu_spider import SohuSpider

scheduler = Scheduler(daemonic = False)

@scheduler.cron_schedule(hour=23, minute='55')
def sohu_crawler():
    sohu = SohuSpider()
    sohu.main()
scheduler.start()

