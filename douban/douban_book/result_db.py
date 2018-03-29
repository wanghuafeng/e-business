#!-*- coding:utf-8 -*-
import os
import json
import mysql.connector
from pyspider.database.mysql.resultdb import BaseDB

cur_path = os.path.dirname(os.path.abspath(__file__))

class DoubanResultDB(BaseDB):
    __tablename__ = 'douban_books'

    def __init__(self, host='localhost', port=3306, database='resultdb',
                 user='root', passwd=None):
        self.config_jdata = self._load_config()
        host = self.config_jdata.get('host') or host
        port = self.config_jdata.get('port') or port
        database = self.config_jdata.get('database') or database
        user = self.config_jdata.get('user') or user
        passwd = self.config_jdata.get('passwd') or passwd
        self.database_name = database
        self.conn = mysql.connector.connect(user=user, password=passwd,
                                            host=host, port=port, autocommit=True,
                                            database=database)

    def _load_config(self):
        config_path = os.path.join(cur_path, 'conf/mysql.json')
        con = open(config_path).read()
        return json.loads(con)

    @property
    def dbcur(self):
        try:
            if self.conn.unread_result:
                self.conn.get_rows()
            return self.conn.cursor()
        except (mysql.connector.OperationalError, mysql.connector.InterfaceError):
            self.conn.ping(reconnect=True)
            self.conn.database = self.database_name
            return self.conn.cursor()


db = DoubanResultDB()

if __name__  == "__main__":
    data = {
        'book': 'book',
        'book_id': '12312',
        'author_name': 'huafeng',
        'score': '9.1',
        'comment_count': '13312312',
        'publish_time': '2'
    }
    db._replace(**data)
    # sql = 'REPLACE INTO `douban_books` (`book_id`) VALUES (13312312)'
    # douban_db.dbcur.execute(sql)