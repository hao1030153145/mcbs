
CREATE TABLE `content_type` (
  `id` int(11) NOT NULL,
  `data_source_type_id` bigint(20) NOT NULL COMMENT '数据源类型id',
  `content_type` varchar(20) NOT NULL COMMENT '数据源类型下拉选项  字段名',
  `content_type_name` varchar(20) NOT NULL COMMENT '下拉选项字段名字  诠释',
  `is_default` int(1) NOT NULL DEFAULT '0' COMMENT '1表示 默认，0表示不是默认',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增序列',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建日期',
  `lastmodify_time` datetime NOT NULL,
  `isdelete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0代表不删除，1代表删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `data_source_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '数据源类型 名字',
  `out_table` varchar(255) NOT NULL DEFAULT '' COMMENT '数据源类型存储表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;


CREATE TABLE `data_source_type_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_source_type_id` bigint(20) NOT NULL,
  `field_name` varchar(20) NOT NULL DEFAULT '' COMMENT '字段名',
  `field_annotation` varchar(50) NOT NULL DEFAULT '' COMMENT '字段诠释',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8;


CREATE TABLE `job_type_info` (
  `type_no` varchar(64) NOT NULL COMMENT 'jobno',
  `type_name` varchar(255) NOT NULL DEFAULT '' COMMENT '工作名称',
  `type_desc` varchar(2048) NOT NULL DEFAULT '' COMMENT '工作描述',
  `param_config_url` varchar(1024) NOT NULL DEFAULT '' COMMENT '工作流配置页面',
  `execute_url` varchar(1024) NOT NULL DEFAULT '' COMMENT '执行工作url',
  `progress_url` varchar(1024) CHARACTER SET utf32 NOT NULL COMMENT '进度URL',
  `result_url` varchar(200) CHARACTER SET utf32 NOT NULL COMMENT '返回结果URL',
  `type_classify` int(1) NOT NULL DEFAULT '1' COMMENT '类型：1为正常执行节点，2为循环节点',
  `type_status` int(1) NOT NULL DEFAULT '1' COMMENT '0,无效，1 有效',
  `order_number` int(11) NOT NULL DEFAULT '0' COMMENT '序号',
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) NOT NULL DEFAULT '',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`type_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `job_type_result` (
  `reuslt_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `type_no` varchar(255) NOT NULL COMMENT 'job 类型',
  `data_source_type` bigint(20) NOT NULL COMMENT '数据源类型id',
  `result_type_name` varchar(255) NOT NULL DEFAULT '' COMMENT '数据源类型名字',
  `query_url` varchar(255) NOT NULL DEFAULT '' COMMENT '查询url',
  PRIMARY KEY (`reuslt_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;


CREATE TABLE `job_type_result_field` (
  `field_id` int(11) NOT NULL AUTO_INCREMENT,
  `result_type_id` int(11) DEFAULT NULL,
  `field_name` varchar(255) DEFAULT NULL,
  `col_name` varchar(255) DEFAULT NULL,
  `col_desc` varchar(255) DEFAULT NULL,
  `field_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`field_id`)
) ENGINE=InnoDB AUTO_INCREMENT=545 DEFAULT CHARSET=utf8;


CREATE TABLE `job_type_result_query_field` (
  `query_id` int(11) NOT NULL AUTO_INCREMENT,
  `result_type_id` int(11) DEFAULT NULL,
  `field_name` varchar(255) DEFAULT NULL,
  `field_desc` varchar(255) DEFAULT NULL,
  `field_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`query_id`)
) ENGINE=InnoDB AUTO_INCREMENT=514 DEFAULT CHARSET=utf8;


CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增序列',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `describes` text COLLATE utf8mb4_unicode_ci,
  `manager` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `customer` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_time` date NOT NULL,
  `end_time` date NOT NULL,
  `type` int(2) NOT NULL COMMENT '0:月，1:周末,2:日,3:零时',
  `status` int(2) NOT NULL COMMENT '0:待配置，1:配置中,2:未启动,3:进行中,4:停止,5:已完成,9:出错',
  `create_time` datetime NOT NULL COMMENT '创建日期',
  `lastmodify_time` datetime NOT NULL,
  `isdelete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0代表不删除，1代表删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `project_result_type` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `flow_id` bigint(20) NOT NULL,
  `flow_detail_id` bigint(20) NOT NULL,
  `result_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=195 DEFAULT CHARSET=utf8;


CREATE TABLE `status` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增序列',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建日期',
  `lastmodify_time` datetime NOT NULL,
  `isdelete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0代表不删除，1代表删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'userid自增序列',
  `account` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `passwd` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建日期',
  `lastmodify_time` datetime NOT NULL,
  `isdelete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0代表不删除，1代表删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `work_flow_detail` (
  `flow_detail_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '流程id',
  `flow_id` bigint(11) NOT NULL COMMENT '工作流id',
  `project_id` bigint(11) NOT NULL COMMENT '项目id',
  `type_no` varchar(64) NOT NULL COMMENT '工作流 节点',
  `quartz_time` varchar(255) NOT NULL DEFAULT '' COMMENT 'quartz time表达式',
  `data_source_type` varchar(64) NOT NULL DEFAULT '' COMMENT '数据源类型',
  `job_begin_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '流程执行的开始时间',
  `job_end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '流程执行的结束时间',
  `job_progress` int(11) NOT NULL DEFAULT '0' COMMENT '流程执行的进度。（0-100）',
  `job_status` int(1) NOT NULL DEFAULT '0' COMMENT '0,待启动，1启动中，2已完成,4停止，9异常',
  `error_msg` text COMMENT '错误提示信息',
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) NOT NULL DEFAULT '',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`flow_detail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=750 DEFAULT CHARSET=utf8;


CREATE TABLE `work_flow_info` (
  `flow_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '流程ID',
  `type_no` varchar(64) NOT NULL COMMENT '工作流 节点',
  `first_detail_id` int(11) NOT NULL DEFAULT '0' COMMENT '第一个流程id',
  `project_id` int(11) NOT NULL COMMENT '项目ID',
  `prev_flow_ids` varchar(1024) NOT NULL DEFAULT '' COMMENT '上一个节点的ids',
  `next_flow_ids` varchar(1024) NOT NULL DEFAULT '' COMMENT '下一个节点 的 ids',
  `total_job_num` int(11) NOT NULL DEFAULT '0' COMMENT '总Job个数',
  `complate_job_num` int(11) NOT NULL DEFAULT '0' COMMENT '已完成Job个数',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '工作流状态:0,初始，3，运行中，2已完成',
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) NOT NULL DEFAULT '',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`flow_id`)
) ENGINE=InnoDB AUTO_INCREMENT=884 DEFAULT CHARSET=utf8;


CREATE TABLE `work_flow_param` (
  `param_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `flow_detail_id` bigint(11) NOT NULL COMMENT '工作流详细id',
  `flow_id` bigint(11) NOT NULL COMMENT '工作流id',
  `project_id` bigint(11) NOT NULL COMMENT '项目id',
  `type_no` varchar(64) NOT NULL,
  `json_param` text NOT NULL COMMENT 'json参数',
  `param_type` int(11) NOT NULL DEFAULT '0' COMMENT '参数类型：0为当前节点的参数，1为公用参数',
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) NOT NULL DEFAULT '',
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`param_id`)
) ENGINE=InnoDB AUTO_INCREMENT=638 DEFAULT CHARSET=utf8;
