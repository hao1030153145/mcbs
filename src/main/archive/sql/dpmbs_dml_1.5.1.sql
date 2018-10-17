
--job_type_category添加
INSERT INTO `job_type_category` (`id`, `name`, `created_by`, `created_time`) VALUES ('4', '处理', 'summer', '2018-03-13 17:46:45');
INSERT INTO  `job_type_category` (`id`, `name`, `created_by`, `created_time`) VALUES ('5', '条件', 'summer', '2018-03-13 17:46:58');

--menu表更新
UPDATE  `menu` SET `id`='1', `name`='项目管理', `url`='', `pid`='0', `index_num`='1' WHERE (`id`='1');
UPDATE  `menu` SET `id`='2', `name`='项目列表', `url`='/project/projectListPage.html', `pid`='1', `index_num`='1-1' WHERE (`id`='2');
UPDATE  `menu` SET `id`='3', `name`='数据源配置管理', `url`='', `pid`='0', `index_num`='2' WHERE (`id`='3');
UPDATE  `menu` SET `id`='4', `name`='数据源配置列表', `url`='/dataSource/datasourceConfListPage.html', `pid`='3', `index_num`='2-1' WHERE (`id`='4');
UPDATE  `menu` SET `id`='5', `name`='抓取流程配置管理', `url`='', `pid`='0', `index_num`='3' WHERE (`id`='5');
UPDATE  `menu` SET `id`='6', `name`='抓取流程配置列表', `url`='/workFlowTemplate/toWorkFlowTemplateList.html', `pid`='5', `index_num`='3-1' WHERE (`id`='6');
UPDATE  `menu` SET `id`='7', `name`='可视化工作流', `url`='', `pid`='0', `index_num`='4' WHERE (`id`='7');
UPDATE  `menu` SET `id`='8', `name`='可视化工作流列表', `url`='/project/projectListPage.html?type=vis', `pid`='7', `index_num`='4-1' WHERE (`id`='8');
UPDATE  `menu` SET `id`='9', `name`='模板列表', `url`='/workFlowTemplate/toVisWorkFlowTemplateList.html', `pid`='7', `index_num`='4-2' WHERE (`id`='9');

--menu添加
INSERT INTO  `menu` (`id`, `name`, `url`, `pid`, `index_num`) VALUES ('10', '存储管理', '', '0', '5');
INSERT INTO  `menu` (`id`, `name`, `url`, `pid`, `index_num`) VALUES ('11', '存储列表', '/storage/toStorageList.html', '10', '5-1');
--job_type_info添加
INSERT INTO  `job_type_info` (`type_no`, `type_name`, `type_desc`, `param_config_url`, `execute_url`, `progress_url`, `result_url`, `type_classify`, `job_type`, `type_status`, `order_number`, `created_by`, `created_time`, `updated_by`, `updated_time`, `job_classify`, `input_num`, `job_type_category_id`, `query_url`, `img_url`, `tip`) VALUES ('dataFilter', '数据筛选', '', '/toWorkFlow.html?toModular=dataFilter', '/workFlow/executeWorkFlow.json', '/workFlow/getExecuteStatus.json', '/workFlow/getWorkFlowResultParams.json', '1', '2', '1', '10', '', '2018-03-13 17:54:16', '', '2018-03-13 17:54:19', '1', '1', '4', NULL, '/project/visWorkFlowIcon/dataFilter.png', '<p style=";text-align:justify;text-justify:inter-ideograph"><strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">【数据筛选】</span></span></strong></p>
<p style=";text-indent:28px;text-align:justify;text-justify:inter-ideograph"><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">用户可通过该节点设置筛选条件来进行数据筛选。</span></span></p>
<p style=";text-align:justify;text-justify:inter-ideograph"><strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">解决问题类型</span></span></strong></p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph"><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">设置筛选条件，对数据进行筛选</span></span></p>
<p style=";text-align:justify;text-justify:inter-ideograph"><strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">流程配置</span></span></strong></p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph"><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑"> 前置节点：可以连接任意可连接的节点  </span></span></p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph"><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑"> 后置节点：可以连接任意可连接的节点  </span></span></p>
<p style=";text-align:justify;text-justify:inter-ideograph"><strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">注意事项</span></span></strong></p>
<p style=";text-align:justify;text-justify:inter-ideograph"><span style="font-family: 微软雅黑;font-size: 12px">    <span style="font-family:微软雅黑"> 可按照数值、文本、时间三种类型的字段进行条件的设置  </span></span></p>
<p style=";text-align:justify;text-justify:inter-ideograph"><strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">参数说明</span></span></strong></p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph"><span style="font-family: 微软雅黑;font-size: 12px">（1）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、满足信息可选择全部条件\任意条件  </span></span></p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph"><span style="font-family: 微软雅黑;font-size: 12px">（2）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、条件的筛选根据选择的字段类型进行联动变化  </span></span></p>
<p style=";text-align:justify;text-justify:inter-ideograph"><strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">输出结果</span></span></strong></p>
<p style="text-indent:27px;text-align:justify;text-justify:inter-ideograph"><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑"> 经过条件筛选后的数据  </span></span></p>
<p><br/></p>');
INSERT INTO  `job_type_info` (`type_no`, `type_name`, `type_desc`, `param_config_url`, `execute_url`, `progress_url`, `result_url`, `type_classify`, `job_type`, `type_status`, `order_number`, `created_by`, `created_time`, `updated_by`, `updated_time`, `job_classify`, `input_num`, `job_type_category_id`, `query_url`, `img_url`, `tip`) VALUES ('condition', '条件', '', '/toWorkFlow.html?toModular=condition', '/workFlow/executeWorkFlow.json', '/workFlow/getExecuteStatus.json', '/workFlow/getWorkFlowResultParams.json', '1', '2', '1', '11', '', '2018-03-13 17:57:39', '', '2018-03-13 17:57:41', '1', '1', '5', NULL, '/project/visWorkFlowIcon/condition.png,/project/visWorkFlowIcon/subCondition.png', '<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">【条件】</span></span></strong>
</p>
<p style=";text-indent:28px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">用户可通过该节点设置不同的条件输出对接不同的节点。</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">解决问题类型</span></span></strong>
</p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">不同条件类型的数据，做不同的数据处理，可连接不同的节点</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">流程配置</span></span></strong>
</p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">前置节点：可以连接任意可连接的节点</span></span>
</p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">后置节点：可以连接任意可连接的节点</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">注意事项</span></span></strong>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">&nbsp;&nbsp;&nbsp;&nbsp;<span style="font-family:微软雅黑">1、每增加一个条件类型，工作台中会增加一个子条件</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">&nbsp;&nbsp;&nbsp;&nbsp;<span style="font-family:微软雅黑">2、否则——表示条件设置后剩余的全部数据</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">参数说明</span></span></strong>
</p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">（1）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、条件类型可选择 如果\否则</span></span>
</p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">（2）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、条件的筛选根据选择的字段类型进行联动变  化</span></span>
</p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">（3）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、可点击“+号”增加多个条件块</span></span>
</p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">（4）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、每增加一个条件块，工作台中母条件右侧增加一个子条件</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">输出结果</span></span></strong>
</p>
<p style="text-indent:27px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">经过条件设置后的数据，可对接不同的节点</span></span>
</p>
<p>
    <br/>
</p>');
INSERT INTO  `job_type_info` (`type_no`, `type_name`, `type_desc`, `param_config_url`, `execute_url`, `progress_url`, `result_url`, `type_classify`, `job_type`, `type_status`, `order_number`, `created_by`, `created_time`, `updated_by`, `updated_time`, `job_classify`, `input_num`, `job_type_category_id`, `query_url`, `img_url`, `tip`) VALUES ('pushOSS', '推送OSS', '', '/toWorkFlow.html?toModular=pussOSS', '/workFlow/executeWorkFlow.json', '/workFlow/getExecuteStatus.json', '/workFlow/getWorkFlowResultParams.json', '1', '2', '1', '8', '', '2018-03-13 18:10:05', '', '2018-03-13 18:10:07', '1', '1', '3', NULL, '/project/visWorkFlowIcon/pushOSS.png', '<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">【推送OSS】</span></span></strong>
</p>
<p style=";text-indent:28px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">用户可通过该节点实现对文件的下载以及存储到OSS。</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">解决问题类型</span></span></strong>
</p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">需要将文件推送到OSS中进行存储</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">流程配置</span></span></strong>
</p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">前置节点：可以连接任意可连接的节点</span></span>
</p>
<p style="text-indent:28px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">后置节点：该节点没有输出</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">注意事项</span></span></strong>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">&nbsp;&nbsp;&nbsp;&nbsp;<span style="font-family:微软雅黑">下载请求编码应该为网页中文本的编码方式</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">参数说明</span></span></strong>
</p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">（1）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、下载字段可增加多个，点击+按钮即可</span></span>
</p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">（2）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、下载字段可减少，但至少保留一个</span></span>
</p>
<p style="text-indent:24px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px">（3）</span><span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">、下载储存地 已在储存管理中设置</span></span>
</p>
<p style=";text-align:justify;text-justify:inter-ideograph">
    <strong><span style="font-family: 微软雅黑;font-size: 14px"><span style="font-family:微软雅黑">输出结果</span></span></strong>
</p>
<p style="text-indent:27px;text-align:justify;text-justify:inter-ideograph">
    <span style="font-family: 微软雅黑;font-size: 12px"><span style="font-family:微软雅黑">经过条件筛选后的数据</span></span>
</p>
<p>
    <br/>
</p>');

/**param表添加**/
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('GBK', 'GBK', 'downloadCoding');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('ISO', 'ISO', 'downloadCoding');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('unicode', 'unicode', 'downloadCoding');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('UTF-8', 'UTF-8', 'downloadCoding');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('不为空', 'not null', 'datetime');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('不为空', 'not null', 'number');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('不为空', 'not null', 'text');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('为空', 'null', 'datetime');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('为空', 'null', 'number');
INSERT INTO  `param` (`key`, `value`, `type`) VALUES ('为空', 'null', 'text');

UPDATE  `role` SET `id`='1', `name`='用户', `menu_ids`='1,2,5,6,7,8,9,10,11' WHERE (`id`='1');

INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('38', 'dataFilter', '节点名称', 'nodeName', NULL, '2', NULL, '1', NULL, '2018-03-14 10:54:51', '2018-03-14 10:54:53', NULL, NULL, 'text', '1', NULL, '', '41');
INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('39', 'dataFilter', '满足信息', 'meetInfo', NULL, '1', NULL, NULL, NULL, '2018-03-14 10:57:45', '2018-03-14 10:57:47', NULL, '{\"id\":\"key\",\"value\":\"value\"}', 'text', '1', '40', '', '42');
INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('40', 'dataFilter', '条件设置', 'conditionSetting', NULL, '7', NULL, NULL, NULL, '2018-03-14 11:03:25', '2018-03-14 11:03:27', '/visWorkFlow/getObjectByTypeNoAndDataSourceId.json,/projectExport/getConditionList.json', '{\"id\":\"key\",\"value\":\"value\"}', 'jsonArray', '1', NULL, '39', '43');
INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('41', 'condition', '节点名称', 'nodeName', NULL, '2', NULL, '1', NULL, '2018-03-14 11:11:15', '2018-03-14 11:11:18', NULL, NULL, 'text', '1', NULL, '', '44');
INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('42', 'condition', '条件设置', 'conditionSetting', NULL, '7', NULL, NULL, NULL, '2018-03-14 11:12:19', '2018-03-14 11:12:21', '/visWorkFlow/getObjectByTypeNoAndDataSourceId.json,/projectExport/getConditionList.json', '{\"id\":\"key\",\"value\":\"value\"}', 'jsonArray', '1', NULL, '', '45');
INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('43', 'pushOSS', '节点名称', 'nodeName', NULL, '2', NULL, '1', NULL, '2018-03-14 11:14:08', '2018-03-14 11:14:10', NULL, NULL, 'text', '1', NULL, '', '46');
INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('44', 'pushOSS', '下载字段', 'downloadField', NULL, '7', NULL, NULL, NULL, '2018-03-14 11:16:35', '2018-03-14 11:16:37', '/visWorkFlow/getObjectByTypeNoAndDataSourceId.json', '{\"id\":\"filed_en_name\",\"value\":\"filed_cn_name\"}', 'text', '1', NULL, '', '47');
INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('45', 'pushOSS', '下载请求编码', 'requestEncoding', NULL, '1', NULL, NULL, NULL, '2018-03-14 11:21:59', '2018-03-14 11:22:01', '/visWorkFlow/getDownLoadCoding.json', '{\"id\":\"key\",\"value\":\"value\"}', 'text', '1', NULL, '', '48');
INSERT INTO  `work_flow_input_param` (`id`, `type_no`, `param_cn_name`, `param_en_name`, `prompt`, `style_id`, `restrictions`, `is_required`, `control_prop`, `create_time`, `lastmodify_time`, `request_url`, `filed_mapping`, `param_type`, `is_show`, `next_param_id`, `pre_param_id`, `order_num`) VALUES ('46', 'pushOSS', '下载储存地', 'storageName', NULL, '1', NULL, NULL, NULL, '2018-03-14 11:24:03', '2018-03-14 11:24:05', '/storage/getAllStorage.json', '{\"id\":\"id\",\"value\":\"storageName\"}', 'text', '1', NULL, '', '49');

DELETE from work_flow_input_param_relation where id in (19,22,25)

INSERT INTO  `work_flow_input_param_relation` (`id`, `type_no`, `input_param_id`, `relation_type`, `create_time`, `lastmodify_time`) VALUES ('35', 'dataFilter', '40', 'rely', '2018-03-20 15:37:17', '2018-03-20 15:37:19');
INSERT INTO  `work_flow_input_param_relation` (`id`, `type_no`, `input_param_id`, `relation_type`, `create_time`, `lastmodify_time`) VALUES ('36', 'condition', '42', 'rely', '2018-03-20 15:37:52', '2018-03-20 15:37:54');
INSERT INTO `work_flow_input_param_relation` VALUES ('37', 'pushOSS', '43', 'notNull',null, null);
INSERT INTO `work_flow_input_param_relation` VALUES ('38', 'pushOSS', '44', 'rely',null, null);
INSERT INTO `work_flow_input_param_relation` VALUES ('39', 'pushOSS', '45', 'notNull',null, null);
INSERT INTO `work_flow_input_param_relation` VALUES ('40', 'pushOSS', '46', 'notNull',null, null);
UPDATE  `work_flow_input_param_relation` SET `relation_type`='rely' WHERE (`id`='34');

INSERT INTO `param` (`key`, `value`, `type`) VALUES ('不为空', 'not null', 'int');
INSERT INTO `param` (`key`, `value`, `type`) VALUES ('为空', 'null', 'int');
INSERT INTO `param` (`key`, `value`, `type`) VALUES ('介于', 'between', 'int');
INSERT INTO `param` (`key`, `value`, `type`) VALUES ('大于', 'gt', 'int');
INSERT INTO `param` (`key`, `value`, `type`) VALUES ('大于等于', 'gte', 'int');
INSERT INTO `param` (`key`, `value`, `type`) VALUES ('小于', 'lt', 'int');
INSERT INTO `param` (`key`, `value`, `type`) VALUES ('小于等于', 'lte', 'int');
INSERT INTO `dpmbs_db`.`param` (`key`, `value`, `type`) VALUES ('int', 'int', 'typeof');
INSERT INTO `param` (`key`, `value`, `type`) VALUES ('等于', 'eq', 'int');







