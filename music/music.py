__author__ = 'huafeng'

from apscheduler.scheduler import Scheduler

from baidu_music import BaiduMusic

scheduler = Scheduler(daemonic = False)

@scheduler.cron_schedule(hour=23,minute='20')
def music_spider():
    music = BaiduMusic()
    music.main()

scheduler.start()
