ALTER TABLE `project`
ADD COLUMN `project_type`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'page' COMMENT '项目类型（vis 表示可视化项目，page 表示 原来的页面配置项目）' AFTER `isdelete`;

CREATE TABLE `project_export` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL COMMENT '项目id',
  `json_param` text COMMENT 'json参数',
  `result_jsonParam` text,
  `status` int(11) DEFAULT NULL COMMENT '状态(1进行中，2已完成,，9异常)',
  `progress` int(11) DEFAULT NULL COMMENT '流程执行的进度',
  `error_message` text,
  `create_time` datetime DEFAULT NULL,
  `lastmodify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;



