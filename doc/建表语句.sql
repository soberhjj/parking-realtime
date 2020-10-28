CREATE TABLE `kafka_offset` (
  `topic` varchar(255) NOT NULL DEFAULT '' COMMENT 'topic',
  `partition` int(11) NOT NULL DEFAULT 0 COMMENT '分区',
  `consumer_id` varchar(255) NOT NULL DEFAULT '' COMMENT '消费者标识',
  `offset` bigint(20) NOT NULL DEFAULT 0 COMMENT '偏移量',
  UNIQUE KEY `uk_kafka_offset` (`topic`,`partition`,`consumer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存放kafka消费偏移量';