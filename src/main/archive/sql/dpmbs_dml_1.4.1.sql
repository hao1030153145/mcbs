INSERT INTO `param` VALUES ('csv', 'csv', 'ExportFileType');
INSERT INTO `param` VALUES ('data', '数据抓取', 'crawlWay');
INSERT INTO `param` VALUES ('datetime', 'datetime', 'typeOf');
INSERT INTO `param` VALUES ('excel', 'excel', '');
INSERT INTO `param` VALUES ('excel', 'excel', 'ExportFileType');
INSERT INTO `param` VALUES ('number', 'number', 'typeOf');
INSERT INTO `param` VALUES ('orAndAn', '原始+语义', 'exportSelect');
INSERT INTO `param` VALUES ('originalData', '原始数据', 'exportSelect');
INSERT INTO `param` VALUES ('process', '流程抓取', 'crawlWay');
INSERT INTO `param` VALUES ('semanticResult', '语义结果', 'exportSelect');
INSERT INTO `param` VALUES ('statisticalResult', '统计结果', 'exportSelect');
INSERT INTO `param` VALUES ('text', 'text', 'typeOf');
INSERT INTO `param` VALUES ('不包括', 'notInclude', 'text');
INSERT INTO `param` VALUES ('介于', 'between', 'number');
INSERT INTO `param` VALUES ('包括', 'include', 'text');
INSERT INTO `param` VALUES ('大于', 'gt', 'number');
INSERT INTO `param` VALUES ('大于等于', 'gte', 'number');
INSERT INTO `param` VALUES ('小于', 'lt', 'number');
INSERT INTO `param` VALUES ('小于等于', 'lte', 'number');
INSERT INTO `param` VALUES ('指定', 'specific', 'datetime');
INSERT INTO `param` VALUES ('起止', 'fromTo', 'datetime');

INSERT INTO `param` (`key`, `value`, `type`) VALUES ('等于', 'eq', 'number');

-- 删除了 是 和 不是
delete from `param` where `key`='是' and `type`='text';
delete from `param` where `key`='不是' and `type`='text';
delete from `param` where `key`='excel' and `type`='';
