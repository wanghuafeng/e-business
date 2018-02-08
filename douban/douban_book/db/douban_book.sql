
DROP TABLE if EXISTS douban_books;
CREATE TABLE `douban_books` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `book` varchar(32)  NOT NULL DEFAULT ''  COMMENT '图书名称',
  `book_id` varchar(16)  NOT NULL DEFAULT ''  COMMENT '图书唯一id',
  `author_name` varchar(32)  NOT NULL DEFAULT ''  COMMENT '作者名称',
  `publish_time` varchar(32)  NOT NULL DEFAULT ''  COMMENT '出版时间',
  `score` tinyint(4) NOT NULL DEFAULT 0  COMMENT '豆瓣评分',
  `comment_count` int(10) NOT NULL DEFAULT 0  COMMENT '评论人数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_s` (`score`),
     KEY `idx_ccnt` (`comment_count`),
  KEY `idx_uptime` (`update_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='豆瓣图书爬虫';