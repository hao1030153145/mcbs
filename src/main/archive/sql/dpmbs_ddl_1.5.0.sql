
/*==============================================================*/
/* Table: work_flow_input_param                                 */
/*==============================================================*/
CREATE TABLE `work_flow_input_param` (
  `id` int(11) NOT NULL,
  `type_no` varchar(255) DEFAULT NULL COMMENT '节点类型',
  `param_cn_name` varchar(50) DEFAULT NULL COMMENT '参数中文名',
  `param_en_name` varchar(50) DEFAULT NULL COMMENT '参数英文名',
  `prompt` varchar(500) DEFAULT NULL COMMENT '提示',
  `style_id` int(11) DEFAULT NULL COMMENT '样式id',
  `restrictions` text COMMENT '约束条件',
  `is_required` int(11) DEFAULT NULL COMMENT '是否必填（1为必填，0为不必填）',
  `control_prop` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `request_url` varchar(255) DEFAULT NULL COMMENT '参数请求url(有些参数是需要选择值的，则请求url)',
  `filed_mapping` varchar(255) DEFAULT NULL COMMENT '请求的数据映射（{"id":"datasourceId","value":"datasourceName"}）',
  `param_type` varchar(20) DEFAULT NULL COMMENT '参数类型（int,text,datetime,float,jsonObject,jsonArray）',
  `is_show` int(11) DEFAULT NULL COMMENT '是否展示页面(1为展示，0为不展示)',
  `next_param_id` varchar(255) DEFAULT NULL COMMENT '当前参数需要下一个参数的值',
  `pre_param_id` varchar(255) DEFAULT '' COMMENT '当前参数需要上一个参数的值',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='输入参数';


/*==============================================================*/
/* Table: work_flow_node_param                                  */
/*==============================================================*/
CREATE TABLE `work_flow_node_param` (
  `param_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `flow_detail_id` bigint(11) NOT NULL COMMENT '工作流详细id',
  `project_id` bigint(11) NOT NULL COMMENT '项目id',
  `type_no` varchar(64) NOT NULL,
  `param_type` int(11) NOT NULL DEFAULT '0' COMMENT '参数类型：0为当前节点的参数，1为公用参数',
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) NOT NULL DEFAULT '',
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `input_param_id` int(11) DEFAULT NULL COMMENT '配置参数id',
  `input_param_cn_name` varchar(50) DEFAULT NULL COMMENT '参数中文名',
  `input_param_type` varchar(20) DEFAULT NULL COMMENT '参数类型（int,text,datetime,float,jsonObject,jsonArray）',
  `input_param_value` text COMMENT '参数值',
  `config` text COMMENT '部分参数的config配置',
  PRIMARY KEY (`param_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1594 DEFAULT CHARSET=utf8;


/*==============================================================*/
/* Table: work_flow_output_filed                                */
/*==============================================================*/
CREATE TABLE `work_flow_output_filed` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `flow_detail_id` bigint(20) NOT NULL COMMENT '工作流节点id',
  `filed_id` bigint(20) NOT NULL COMMENT '存储字段id(来源base系统)',
  `filed_en_name` varchar(20) NOT NULL COMMENT '存储字段中文name(来源base系统)',
  `filed_type` text COMMENT '存储字段类型(来源base系统)',
  `is_customed` int(1) DEFAULT NULL COMMENT '是否自定义字段（1为是，0为否）',
  `filed_cn_name` varchar(20) NOT NULL COMMENT '存储字段英文name(来源base系统)',
  `storage_type_table` varchar(20) DEFAULT NULL COMMENT '存储表名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7719 DEFAULT CHARSET=utf8;


/*==============================================================*/
/* Table: work_flow_template_node_param                         */
/*==============================================================*/
CREATE TABLE `work_flow_template_node_param` (
  `param_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `template_id` int(11) NOT NULL COMMENT 'templateId模板id',
  `work_flow_node_id` bigint(11) NOT NULL COMMENT '工作流模板节点表id',
  `type_no` varchar(64) NOT NULL,
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(255) NOT NULL DEFAULT '',
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `input_param_id` int(11) DEFAULT NULL COMMENT '配置参数id',
  `input_param_cn_name` varchar(50) DEFAULT NULL COMMENT '参数中文名',
  `input_param_type` varchar(20) DEFAULT NULL COMMENT '参数类型（int,text,datetime,float,jsonObject,jsonArray）',
  `input_param_value` text COMMENT '参数值',
  `config` text COMMENT '部分参数的config配置',
  PRIMARY KEY (`param_id`)
) ENGINE=InnoDB AUTO_INCREMENT=531 DEFAULT CHARSET=utf8;


/*==============================================================*/
/* Table: style                                                 */
/*==============================================================*/
CREATE TABLE `style` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `style_code` varchar(20) DEFAULT NULL COMMENT '样式编码',
  `style_name` varchar(20) DEFAULT NULL COMMENT '样式名称',
  `style_path` varchar(500) DEFAULT NULL COMMENT '指向路径',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;


/*==============================================================*/
/* Table: work_flow_input_param_relation                        */
/*==============================================================*/
CREATE TABLE `work_flow_input_param_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_no` varchar(255) DEFAULT NULL COMMENT '节点类型',
  `input_param_id` int(11) DEFAULT NULL COMMENT '工作流配置参数表(work_flow_input_param)的id',
  `relation_type` varchar(20) DEFAULT NULL COMMENT '关系类型（rely为依赖，notNull为不能为空的字段）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;


/*==============================================================*/
/* Table: job_type_category                                     */
/*==============================================================*/
CREATE TABLE `job_type_category` (
  `id` int(11) NOT NULL,
  `name` varchar(20) DEFAULT NULL COMMENT '名字',
  `created_by` varchar(255) NOT NULL DEFAULT '',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `work_flow_template`
ADD COLUMN `img_url`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '预览图片url' AFTER `lastmodify_time`,
ADD COLUMN `is_delete`  int(11) NULL DEFAULT 0 COMMENT '模板逻辑删除' AFTER `img_url`;


ALTER TABLE `job_type_info`
ADD COLUMN `input_num`  int(2) NULL COMMENT '接收数量（-1表示不限制）' AFTER `job_classify`,
ADD COLUMN `job_type_category_id`  int(11) NULL COMMENT '分类id' AFTER `input_num`,
ADD COLUMN `query_url`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '查看url' AFTER `job_type_category_id`,
ADD COLUMN `img_url`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '节点图标' AFTER `query_url`,
ADD COLUMN `tip`  text(0) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '提示信息' AFTER `img_url`;


ALTER TABLE `work_flow_detail`
ADD COLUMN `node_info`  text NULL COMMENT '页面属性' AFTER `result_param`;
ADD COLUMN `is_save`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '验证表示 默认为未通过' AFTER `node_info`;


ALTER TABLE `work_flow_node`
ADD COLUMN `node_info`  text NULL COMMENT '页面属性' AFTER `updated_time`;
ADD COLUMN `is_save`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '验证表示 默认为未通过' AFTER `node_info`;


