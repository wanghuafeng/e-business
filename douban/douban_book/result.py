#!-*- coding:utf-8 -*-
import logging
from pyspider.result.result_worker import OneResultWorker
# from result_db import db

logger = logging.getLogger("ResultWorker")

class DoubanResultWorker(OneResultWorker):

    def on_result(self, task, result):
        if not result:
            return
        # db._replace(**result)
