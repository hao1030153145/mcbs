
--work_flow_list添加
CREATE TABLE `work_flow_list` (
  `work_flow_id` bigint(10) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(10) NOT NULL COMMENT '可视化工作流项目id',
  `work_flow_template_id` int(10) DEFAULT NULL COMMENT '模板id',
  `work_flow_name` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '项目名称',
  `create_time` datetime NOT NULL,
  `finish_time` datetime DEFAULT NULL,
  `status` int(1) NOT NULL COMMENT '状态(1表示进行中，2表示已完成，3表示未启动，4表示已停止，5表示配置中，9表示出错)',
  `img` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '缩略图',
  PRIMARY KEY (`work_flow_id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8;

--work_flow_template_output_filed添加
CREATE TABLE `work_flow_template_output_filed` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `flow_id` bigint(11) NOT NULL COMMENT '模板id',
  `filed_id` bigint(11) NOT NULL COMMENT '字段id',
  `filed_en_name` varchar(20) NOT NULL COMMENT '存储字段英文name(来源base系统)',
  `filed_type` varchar(20) DEFAULT NULL COMMENT '字段类型',
  `is_customed` int(1) DEFAULT NULL,
  `filed_cn_name` varchar(20) NOT NULL COMMENT '存储字段中文name(来源base系统)',
  `storage_type_table` varchar(20) DEFAULT NULL COMMENT '存储表名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=177 DEFAULT CHARSET=utf8;


ALTER TABLE `menu`
CHANGE COLUMN `index_num`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '位置 用于前端用' AFTER `pid`;

ALTER TABLE `menu`
ADD COLUMN `index_num`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '位置 用于前端用' AFTER `pid`;


CREATE TABLE `storage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `storage_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '存储名称',
  `storage_type` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '存储类型',
  `key_id` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '密匙id',
  `password` varchar(255) NOT NULL COMMENT '密匙',
  `storage_address` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT 'bucket_name',
  `path` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '访问路径',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

ALTER TABLE `work_flow_detail`
add COLUMN `work_flow_id` bigint(11) NOT NULL DEFAULT 0 COMMENT '可视化工作流id' AFTER `project_id`;

ALTER TABLE `work_flow_node_param`
add COLUMN `work_flow_id` bigint(11) NOT NULL DEFAULT 0 COMMENT '可视化工作流id' AFTER `project_id`;

ALTER TABLE `work_flow_template`
add COLUMN `is_delete` int(1) NOT NULL DEFAULT 0 COMMENT '模板逻辑删除' AFTER `img_url`;

ALTER TABLE `work_flow_node`
add COLUMN `job_status` int(1) NOT NULL DEFAULT 0 COMMENT '5无效  0 有效' AFTER `is_save`;
