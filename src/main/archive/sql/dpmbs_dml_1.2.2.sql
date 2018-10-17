DROP TABLE IF EXISTS `content_type`;
CREATE TABLE `content_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datasource_type_id` bigint(20) NOT NULL COMMENT '数据源类型id',
  `content_type` varchar(20) NOT NULL COMMENT '数据源类型下拉选项  字段名',
  `content_type_name` varchar(20) NOT NULL COMMENT '下拉选项字段名字  诠释',
  `is_default` int(1) NOT NULL DEFAULT '0' COMMENT '1表示 默认，0表示不是默认',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `datasource`;
CREATE TABLE `datasource` (
  `datasource_id` int(11) NOT NULL,
  `datasource_name` varchar(255) NOT NULL,
  PRIMARY KEY (`datasource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datasource_type`;
CREATE TABLE `datasource_type` (
  `datasource_type_id` int(11) NOT NULL,
  `datasource_id` int(11) NOT NULL,
  `datasource_type_name` varchar(255) NOT NULL,
  `storage_type_table` varchar(50) NOT NULL,
  `status` int(255) NOT NULL COMMENT '1为有效 0为无效',
  `updated_time` datetime NOT NULL,
  `updated_by` varchar(20) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建日期',
  PRIMARY KEY (`datasource_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `menu` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `pid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES ('1', '项目管理', '', '0');
INSERT INTO `menu` VALUES ('2', '项目列表', '/project/projectListPage.html', '1');
INSERT INTO `menu` VALUES ('3', '数据源配置管理', '', '0');
INSERT INTO `menu` VALUES ('4', '数据源配置列表', '/dataSource/datasourceConfListPage.html', '3');


CREATE TABLE `role` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `menu_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '菜单 ids 逗号分隔',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '用户', '1,2');
INSERT INTO `role` VALUES ('2', '管理', '3,4');


CREATE TABLE `user_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1');
INSERT INTO `user_role` VALUES ('2', '1');
INSERT INTO `user_role` VALUES ('2', '2');
