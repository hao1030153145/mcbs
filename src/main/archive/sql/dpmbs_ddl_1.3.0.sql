CREATE TABLE `vis_chart` (
  `id` int(11) NOT NULL,
  `vis_id` int(11) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL COMMENT '分类id',
  `path` varchar(200) DEFAULT NULL COMMENT '图例路径',
  `process_class` varchar(200) DEFAULT NULL COMMENT '处理类',
  `vis_json_param_id` int(11) DEFAULT NULL COMMENT '可视化参数id',
  `default_data_array` text CHARACTER SET utf8 COMMENT '默认图表数据',
  `default_table_array` text CHARACTER SET utf8 COMMENT '默认表格参数',
  `input_field_array` text CHARACTER SET utf8 COMMENT '输入参数',
  `position` varchar(200) DEFAULT NULL COMMENT '位置信息',
  `img` varchar(500) DEFAULT NULL COMMENT '缩略图',
  `type` varchar(200) DEFAULT NULL COMMENT '类型',
  `type_name` varchar(200) CHARACTER SET utf8 DEFAULT '',
  `name` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `vis_json_param` (
  `id` int(11) NOT NULL,
  `type` varchar(200) DEFAULT NULL,
  `position` varchar(200) DEFAULT NULL COMMENT '位置信息',
  `param_settings` text CHARACTER SET utf8,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `vis_template` (
  `id` int(11) DEFAULT NULL,
  `url` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vis_module` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL COMMENT '项目id',
  `vis_id` int(11) DEFAULT NULL COMMENT '可视化id',
  `json_param` text COMMENT '参数json',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7748 DEFAULT CHARSET=utf8;

CREATE TABLE `visualization` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL COMMENT '名称',
  `project_id` int(11) DEFAULT NULL COMMENT '项目id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `image` varchar(200) DEFAULT NULL COMMENT '图表',
  `back_setting` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8;


CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `img` varchar(500) DEFAULT NULL COMMENT '缩略图',
  `name` varchar(200) DEFAULT NULL,
  `type` varchar(200) DEFAULT NULL COMMENT '类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `lastmodify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;