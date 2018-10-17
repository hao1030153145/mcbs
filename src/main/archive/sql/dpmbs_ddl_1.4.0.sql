CREATE TABLE `work_flow_node` (
  `flow_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '流程ID',
  `template_id` int(11) NOT NULL COMMENT 'templateId模板id',
  `pre_flow_id_ids` varchar(200) DEFAULT NULL COMMENT '上一个流程ids，多个英文逗号分割',
  `next_flow_id_ids` varchar(200) DEFAULT NULL COMMENT '下一个流程ids，多个英文逗号分割',
  `type_no` varchar(64) NOT NULL COMMENT '类型',
  `node_param` text COMMENT '参数',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) NOT NULL DEFAULT '',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`flow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `work_flow_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `status` int(11) DEFAULT NULL COMMENT '状态:0,待启动，1启动中，2已完成,4停止，9异常',
  `process` varchar(1024) DEFAULT NULL COMMENT '流程（生成的）',
  `flow_type` varchar(64) DEFAULT NULL COMMENT '流程类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `param` (
  `key` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '参数的key',
  `value` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '参数的值',
  `type` varchar(30) CHARACTER SET utf8mb4 NOT NULL COMMENT '参数类型',
  PRIMARY KEY (`key`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `work_flow_detail`
ADD COLUMN `work_flow_template_id`  int(11) NULL DEFAULT NULL AFTER `flow_detail_id`;

ALTER TABLE `job_type_info`
ADD COLUMN `job_classify`  int(255) NOT NULL COMMENT '节点类型：1 为流程节点，2为状态节点' AFTER `updated_time`;