package com.transing.workflow.biz.service.impl.local;

import com.alibaba.fastjson.JSONObject;
import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.logicframework.integration.sao.hdfs.BaseSaoHDFS;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.VisWorkFlowService;
import com.transing.dpmbs.constant.RedisKey;
import com.transing.dpmbs.integration.bo.TemplateOutputFiledBO;
import com.transing.dpmbs.integration.bo.VisWorkFlowBO;
import com.transing.dpmbs.integration.bo.WorkFlowListBO;
import com.transing.dpmbs.util.Base64Util;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowTemplateService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.WorkFlowDataService;
import com.transing.workflow.integration.WorkFlowTemplateDataService;
import com.transing.workflow.integration.bo.*;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sunny
 * @version 1.0
 */
@Service("workFlowTemplateService")
public class WorkFlowTemplateServicePojo extends BaseService implements WorkFlowTemplateService
{
    @Resource
    private WorkFlowTemplateDataService workFlowTemplateDataService;
    @Resource
    private WorkFlowDataService workFlowDataService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;
    @Resource
    private BaseSaoHDFS baseSaoHDFS;
    @Resource
    private VisWorkFlowService visWorkFlowService;
    @Resource
    private JobTypeService jobTypeService;

    @Resource
    private BaseDaoRedis redisClient;

    public static final String IMG_PATH = "project/vis/workFlow/template/";
    protected String loggerName = this.getClass().getSimpleName();
    private String upload;

    @Override
    public WorkFlowTemplateBO getWorkFlowTemplateListById(int id) throws BizException {
        return workFlowTemplateDataService.getWorkFlowTemplateListById(id);
    }


    /**
     * 根据id逻辑删除模板数据
     * @param list
     * @return
     * @throws BizException
     */
    @Override
    public int logicDeleteVisWorkFlowTemplateByIds(List<Integer> list) throws BizException {
        return workFlowTemplateDataService.logicDeleteVisWorkFlowTemplateByIds(list);
    }

    /**
     * 根据查询条件获得未被逻辑删除的模板数据
     * @param param
     * @return
     * @throws BizException
     */
    @Override
    public List<WorkFlowTemplatePO> getWorkFlowTemplateListByCondition(Map<String, Object> param) throws BizException {
        return workFlowTemplateDataService.getWorkFlowTemplateListByCondition(param);
    }

    /**
     * 根据查询条件获得模板的数目
     * @param param
     * @return
     * @throws BizException
     */
    @Override
    public int getWorkFlowTemplateCountByCondition(Map<String, Object> param) throws BizException {
        return workFlowTemplateDataService.getWorkFlowTemplateCountByCondition(param);
    }

    @Override
    public List<WorkFlowTemplateBO> getWorkFlowTemplateListByParam(Integer status,String name, Integer page, Integer size) throws BizException {
        return workFlowTemplateDataService.getWorkFlowTemplateListByParam(status,name,page,size);
    }

    @Override
    public List<WorkFlowTemplateBO> getVisWorkFlowTemplateListByParam(Integer status, String name, Integer page, Integer size, String createTime, String endTime) throws BizException {
        return workFlowTemplateDataService.getVisWorkFlowTemplateListByParam(status,name,page,size,createTime,endTime);
    }

    @Override
    public Integer getVisWorkFlowTemplateCountByParam(Integer status, String name, String createTime,String endTime) throws BizException {
        return workFlowTemplateDataService.getVisWorkFlowTemplateCountByParam(status, name, createTime,endTime);
    }

    @Override
    public Integer getWorkFlowTemplateCountByParam(Integer status,String name) throws BizException {
        return workFlowTemplateDataService.getWorkFlowTemplateCountByParam(status,name);
    }

    @Override
    public Integer getStatusByDetailId(Integer detailId) throws BizException {
        return workFlowTemplateDataService.getStatusByDetailId(detailId);
    }

    @Override
    public String getTypeNoByDetailId(Integer flowDetailId) throws BizException {
        return workFlowTemplateDataService.getTypeNoByDetailId(flowDetailId);
    }

    @Override
    public int addWorkFlowTemplate(WorkFlowTemplateBO workFlowTemplateBO) throws BizException {
        return workFlowTemplateDataService.addWorkFlowTemplate(workFlowTemplateBO);
    }

    @Override
    public List<String> getVisTemplateNameList() throws BizException {
        return workFlowTemplateDataService.getVisTemplateNameList();
    }

    @Override
    public List<WorkFlowNodeBO> getWorkFlowNodeListByTemplateId(int templateId) throws BizException {
        return workFlowTemplateDataService.getWorkFlowNodeListByTemplateId(templateId);
    }

    @Override
    public int addWorkFlowNode(WorkFlowNodeBO workFlowNodeBO) throws BizException {
        return workFlowTemplateDataService.addWorkFlowNode(workFlowNodeBO);
    }

    @Override
    @Transactional
    public int saveWorkFlowTemplateDeial(WorkFlowTemplateDetailPO workFlowTemplateDetailPO) throws BizException {

        Integer id = workFlowTemplateDetailPO.getId();
        JSONArray processList = new JSONArray();

        if(null == id || id == 0){
            WorkFlowTemplateBO workFlowTemplateBO = new WorkFlowTemplateBO();

            workFlowTemplateBO.setName(workFlowTemplateDetailPO.getTemplateName());
            workFlowTemplateBO.setFlowType(WorkFlowTemplateBO.FLOWTYPE_CRAWL);
            workFlowTemplateBO.setStatus(WorkFlowTemplateBO.STATUS_VALID);

            workFlowTemplateDataService.addWorkFlowTemplate(workFlowTemplateBO);
            id = workFlowTemplateBO.getId();

            List<WorkFlowNodePO> workFlowNodePOList = workFlowTemplateDetailPO.getWorkFlowInfoList();
            Long flowId = 0L;
            for (WorkFlowNodePO workFlowNodePO:workFlowNodePOList) {
                WorkFlowNodeBO workFlowNodeBO = new WorkFlowNodeBO();

                workFlowNodeBO.setName(workFlowNodePO.getName());
                workFlowNodeBO.setTemplateId(workFlowTemplateBO.getId());
                workFlowNodeBO.setTypeNo(workFlowNodePO.getNodeParam().getString("processType"));

                String datasourceTypeName = workFlowNodePO.getNodeParam().optString("datasourceTypeName");
                if(!Validate.isEmpty(datasourceTypeName)){
                    processList.add(datasourceTypeName);
                }

                workFlowNodeBO.setNodeParam(workFlowNodePO.getNodeParam().toString());

                workFlowNodeBO.setPreFlowIdIds(String.valueOf(flowId));

                workFlowTemplateDataService.addWorkFlowNode(workFlowNodeBO);

                if(null != flowId && flowId > 0){

                    WorkFlowNodeBO workFlowNodeBOTemp = new WorkFlowNodeBO();
                    workFlowNodeBOTemp.setFlowId(flowId);
                    workFlowNodeBOTemp.setNextFlowIdIds(String.valueOf(workFlowNodeBO.getFlowId()));

                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBOTemp);
                }

                flowId = workFlowNodeBO.getFlowId();

            }

        }else {
            WorkFlowTemplateBO workFlowTemplateBO = new WorkFlowTemplateBO();

            workFlowTemplateBO.setId(id);
            workFlowTemplateBO.setName(workFlowTemplateDetailPO.getTemplateName());

            workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);

            workFlowTemplateDataService.deleteWorkFlowNodeByTemplateId(id);

            List<WorkFlowNodePO> workFlowNodePOList = workFlowTemplateDetailPO.getWorkFlowInfoList();
            Long flowId = 0L;
            for (WorkFlowNodePO workFlowNodePO:workFlowNodePOList) {
                WorkFlowNodeBO workFlowNodeBO = new WorkFlowNodeBO();

                workFlowNodeBO.setName(workFlowNodePO.getName());
                workFlowNodeBO.setTemplateId(workFlowTemplateBO.getId());
                workFlowNodeBO.setTypeNo(workFlowNodePO.getNodeParam().getString("processType"));

                String datasourceTypeName = workFlowNodePO.getNodeParam().optString("datasourceTypeName");
                if(!Validate.isEmpty(datasourceTypeName)){
                    processList.add(datasourceTypeName);
                }

                workFlowNodeBO.setNodeParam(workFlowNodePO.getNodeParam().toString());

                workFlowNodeBO.setPreFlowIdIds(String.valueOf(flowId));

                workFlowTemplateDataService.addWorkFlowNode(workFlowNodeBO);

                if(null != flowId && flowId > 0){

                    WorkFlowNodeBO workFlowNodeBOTemp = new WorkFlowNodeBO();
                    workFlowNodeBOTemp.setFlowId(flowId);
                    workFlowNodeBOTemp.setNextFlowIdIds(String.valueOf(workFlowNodeBO.getFlowId()));

                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBOTemp);
                }

                flowId = workFlowNodeBO.getFlowId();

            }

        }

        if(!Validate.isEmpty(processList)){

            WorkFlowTemplateBO workFlowTemplateBO = new WorkFlowTemplateBO();

            workFlowTemplateBO.setId(id);
            workFlowTemplateBO.setProcess(processList.toString());

            workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
        }

        return 1;
    }

    @Override
    @Transactional
    public int deleteWorkFlowTemplateDetail(int workFlowTemplateId) throws BizException {

        workFlowTemplateDataService.deleteWorkTemplateById(workFlowTemplateId);
        workFlowTemplateDataService.deleteWorkFlowNodeByTemplateId(workFlowTemplateId);

        return 1;
    }

    @Override
    public int updateWorkFlowNode(WorkFlowNodeBO workFlowNodeBO) throws BizException {
        return workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
    }

    @Override
    public int updateWorkTemplate(WorkFlowTemplateBO workFlowTemplateBO) throws BizException {
        return workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
    }

    @Override
    public int getVisWorkTemplateIsExistingByName(String name) throws BizException {
        return workFlowTemplateDataService.getVisWorkTemplateIsExistingByName(name);
    }

    @Override
    @Transactional
    public List<Map<String, Object>> addVisWorkFlowTemplateNodeParam(String body) throws DataServiceException {
        String analysisLevel= "";
        JSONObject jsonObject = JSONObject.parseObject(body);
        String templateId = jsonObject.getString("templateId");
        com.alibaba.fastjson.JSONArray nodeList = jsonObject.getJSONArray("nodeList");
        //存放nodeId与flowDetailId的映射关系map
        Map<String,Long> nodeIdFlowDetailMap = new HashMap<>();
        //存放flowDetailId与workFlowDetail的映射关系map
        Map<Long,WorkFlowNodeBO> flowDetailIdWorkFlowDetailMap = new HashMap<>();
        //聚合传入的nodeList
        Map<Long,Object> nodeMap = new HashMap<>();
        for(Object node : nodeList){
            JSONObject j = JSONObject.parseObject(node.toString());
            JSONObject nodeJsonObject = j.getJSONObject("data");
            if(nodeJsonObject.containsKey("flowDetailId")){
                long flowDetailId = nodeJsonObject.getLong("flowDetailId");
                nodeMap.put(flowDetailId,node);
            }
        }
        //查询所有的输入参数（input_param）。
        List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowDataService.getWorkFlowInputParamBoList();
        //聚合所有的输入参数
        Map<Integer,WorkFlowInputParamBo> workFlowInputParamBoMap = new HashMap<>();
        for(WorkFlowInputParamBo workFlowInputParamBo : workFlowInputParamBoList){
            workFlowInputParamBoMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo);
        }
        //该list用来记录如果有被删除的节点时，获取该节点的下级节点并存入该list中。在后面将他的下级节点更新为无效
        List<Long> deletedNextDetailList = new ArrayList<>();
        //这一步主要是判断是否有被删除的节点，如果有则需要将数据库的记录删除掉
        List<WorkFlowNodeBO> workFlowNodeBOList = workFlowTemplateDataService.getWorkFlowNodeListByTemplateId(Integer.parseInt(templateId));
        for(WorkFlowNodeBO workFlowNodeBO : workFlowNodeBOList){
            long flowId = workFlowNodeBO.getFlowId();
            Object object = nodeMap.get(flowId);
            if(object==null){
                //首先尝试移除掉被删除的节点（这样做是为了如果连续删除多个节点，则只保留最后一个删除节点的下级节点）
                deletedNextDetailList.remove(flowId);
                //获取即将被删除节点的下级节点（可能有多个），并将它们添加至beforehand
                String nextFlowIdIds=workFlowNodeBO.getNextFlowIdIds();
                if(!Validate.isEmpty(nextFlowIdIds)){
                    for(String nextFlowDetailId : nextFlowIdIds.split(",")){
                        deletedNextDetailList.add(Long.parseLong(nextFlowDetailId));
                    }
                }
                workFlowTemplateDataService.deleteWorkFlowNodeByFlowId(flowId);
                workFlowTemplateDataService.deleteWorkFlowTemplateNodeParamByFlowId(flowId);
                visWorkFlowService.delWorkFlowTemplateOutputFiled(flowId);
            }
        }
        //先把nodeList中的data和nodeId进行关联。
        Map<String,JSONObject> dataNodeIdMap = new HashMap<>();
        //存放首节点的list
        List<String> firstNodeIdList = new ArrayList<>();
        com.alibaba.fastjson.JSONArray exchangeNodeList = new com.alibaba.fastjson.JSONArray();
        for(Object node : nodeList){
            JSONObject j = JSONObject.parseObject(node.toString());
            String nodeId = j.getString("nodeId");
            dataNodeIdMap.put(nodeId,j);
            //如果当前节点的上节点为空,则认为当前节点为首节点，或单独节点
            if(Validate.isEmpty(j.getJSONArray("froms"))){
                firstNodeIdList.add(nodeId);
            }
        }
        //遍历每一个首节点。
        for(String nodeId : firstNodeIdList){
            //利用递归将后面的所有节点都放在exchangeNodeList中
            exchangeNodeList = getNextNodeDataByNodeId(nodeId,dataNodeIdMap,exchangeNodeList);
        }

        //开始修改或添加节点信息 work_flow_node
        for(Object node : nodeList){
            WorkFlowNodeBO workFlowNodeBO = new WorkFlowNodeBO();
            JSONObject j = JSONObject.parseObject(node.toString());
            JSONObject jsonObject1 = j.getJSONObject("data");
            Long flowDetailId = jsonObject1.getLong("flowDetailId");
            String nodeId = j.getString("nodeId");
            String typeNo = jsonObject1.getString("typeNo");
            Boolean isSave = jsonObject1.getBoolean("isSave");
            String nodeInfo = j.getString("nodeInfo");
            com.alibaba.fastjson.JSONArray jsonArray = jsonObject1.getJSONArray("paramArray");
            String datasourceType = null;
            //这里主要是将输入参数里的数据源类型的值取出来，用于在后面添加storageTypeTable时的查询参数
            if(!Validate.isEmpty(jsonArray)) {
                for (Object o : jsonArray) {
                    JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                    WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamJson.getInteger("inputParamId"));
                    if(workFlowInputParamBo.getTypeNo().equals(typeNo) &&
                            (workFlowInputParamBo.getParamEnName().equals("datasourceTypeId")||
                                    workFlowInputParamBo.getParamEnName().equals("typeName"))){
                        datasourceType = inputParamJson.getString("value");
                        break;
                    }
                }
            }
            //如果节点存在，则表示更新，不存在则表示添加
            if(flowDetailId != null){
                workFlowNodeBO.setTypeNo(typeNo);
                workFlowNodeBO.setFlowId(flowDetailId);
                workFlowNodeBO.setNodeInfo(nodeInfo);
                workFlowNodeBO.setSave(isSave);
                workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
            }else{
                workFlowNodeBO.setNodeInfo(nodeInfo);
                workFlowNodeBO.setTypeNo(typeNo);
                workFlowNodeBO.setTemplateId(Integer.parseInt(templateId));
                workFlowNodeBO.setSave(isSave);
                workFlowTemplateDataService.addWorkFlowNode(workFlowNodeBO);
                flowDetailId = workFlowNodeBO.getFlowId();
            }
            //将nodeId和flowDetailId进行关联
            nodeIdFlowDetailMap.put(nodeId,flowDetailId);
            flowDetailIdWorkFlowDetailMap.put(flowDetailId,workFlowNodeBO);
            String crawlType = "";
            //开始添加节点的参数配置
            for (Object o : jsonArray) {
                WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo = new WorkFlowTemplateNodeParamBo();
                JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                int inputParamId = inputParamJson.getInteger("inputParamId");
                //查询出当前参数的inputParamId所对应的记录，用于在添加work_flow_node_param时，需要一些冗余字段
                WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamId);
                //首先获取该控件的值
                switch (workFlowInputParamBo.getParamEnName()){
                    case "url":
                        String value = inputParamJson.getString("value");
                        String fileBase = System.getProperty("upload.dir");
                        String fileParamtertm = "/project/temp/";
                        String fileParamter = "/project/importfile/";
                        if(!Validate.isEmpty(value) && value.indexOf(fileParamter)<0){
                            String url = value;
                            //把上传文件移动到服务器目录
                            String originalUrl = fileBase + fileParamtertm + url;
                            File file = new File(originalUrl);
                            if (file.exists()) {
                                //上传文件到hdfs。
                                baseSaoHDFS.uploadFile(originalUrl,fileBase + fileParamter + url);
                                file.delete();
                                workFlowTemplateNodeParamBo.setInputParamValue(fileBase + fileParamter + url);
                            }
                        }
                        break;
                    case "storageTypeTable":
                        if(!Validate.isEmpty(datasourceType)){
                            StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(datasourceType));
                            workFlowTemplateNodeParamBo.setInputParamValue(storageTypePO.getStorageTypeTable());
                        }
                        break;
                    case "wordSegmentationObject":
                        if(inputParamJson.getString("value")!=null &&
                                (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                        inputParamJson.getString("value").equals("topicAnalysisDefinition")||
                                        inputParamJson.getString("value").equals("themeAnalysisSetting"))){
                            workFlowTemplateNodeParamBo.setInputParamValue(analysisLevel);
                        }else{
                            workFlowTemplateNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                        }
                        break;
                    case "analysisLevel":
                        analysisLevel = inputParamJson.getString("value");
                        workFlowTemplateNodeParamBo.setInputParamValue(analysisLevel);
                        break;
                    case "themeAnalysisSettingObject":
                        if(inputParamJson.getString("value")!=null &&
                                (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                        inputParamJson.getString("value").equals("wordSegmentation")||
                                        inputParamJson.getString("value").equals("topicAnalysisDefinition"))){
                            workFlowTemplateNodeParamBo.setInputParamValue(analysisLevel);
                        }else{
                            workFlowTemplateNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                        }
                        break;
                    case "topicAnalysisDefinitionObject":
                        if(inputParamJson.getString("value")!=null &&
                                (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                        inputParamJson.getString("value").equals("wordSegmentation")||
                                        inputParamJson.getString("value").equals("themeAnalysisSetting"))){
                            workFlowTemplateNodeParamBo.setInputParamValue(analysisLevel);
                        }else{
                            workFlowTemplateNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                        }
                        break;
                    case "crawlFreq":
                        String  quartzTime = inputParamJson.getString("value");
                        if(quartzTime.equals("* * * * * *")){
                            quartzTime = "";
                            workFlowTemplateNodeParamBo.setInputParamValue(quartzTime);
                        }else{
                            if(quartzTime.endsWith("*")){
                                quartzTime = quartzTime.substring(0,quartzTime.length()-1);
                                quartzTime += "?";
                            }
                            workFlowTemplateNodeParamBo.setInputParamValue(quartzTime);
                        }
                        break;
                    case "startFreqTypeName":
                        String  quartzTime2 = inputParamJson.getString("value");
                        if(quartzTime2.equals("* * * * * *")){
                            quartzTime2 = "";
                            workFlowTemplateNodeParamBo.setInputParamValue(quartzTime2);
                        }else{
                            if(quartzTime2.endsWith("*")){
                                quartzTime2= quartzTime2.substring(0,quartzTime2.length()-1);
                                quartzTime2 += "?";
                            }
                            workFlowTemplateNodeParamBo.setInputParamValue(quartzTime2);
                        }
                        break;
                    default:
                        if(workFlowInputParamBo.getParamEnName().equals("crawlType")){
                            crawlType = inputParamJson.getString("value");
                        }
                        workFlowTemplateNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                }
                //如果有paramId 则表示是更新。如果没有则表示添加
                if (inputParamJson.getInteger("paramId") != null){
                    workFlowTemplateNodeParamBo.setParamId(inputParamJson.getLong("paramId"));
                    //这里这样子做是因为bdi项目会有非常多的数据源。不便将config存入数据库，而选择存入缓存
                    if(("6".equals(inputParamJson.getString("inputParamId")) || "2".equals(inputParamJson.getString("inputParamId"))) &&
                            !Validate.isEmpty(inputParamJson.getString("config"))){
                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                            redisClient.set(RedisKey.dataSourceConfig.name(),inputParamJson.getString("config"));
                        }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                            redisClient.set(RedisKey.mDataSourceConfig.name(),inputParamJson.getString("config"));
                        }
                    }else {
                        workFlowTemplateNodeParamBo.setConfig(inputParamJson.getString("config"));
                    }
                    workFlowTemplateDataService.updateWorkFlowTemplateNodeParam(workFlowTemplateNodeParamBo);
                }else{
                    workFlowTemplateNodeParamBo.setWorkFlowTemplateNodeId(flowDetailId.intValue());
                    workFlowTemplateNodeParamBo.setTemplateId(Integer.parseInt(templateId));
                    workFlowTemplateNodeParamBo.setTypeNo(typeNo);
                    workFlowTemplateNodeParamBo.setInputParamId(inputParamJson.getInteger("inputParamId"));
                    workFlowTemplateNodeParamBo.setInputParamCnName(workFlowInputParamBo.getParamCnName());
                    workFlowTemplateNodeParamBo.setInputParamType(workFlowInputParamBo.getParamType());
                    //这里这样子做是因为bdi项目会有非常多的数据源。不便将config存入数据库，而选择存入缓存
                    if(("6".equals(inputParamJson.getString("inputParamId")) || "2".equals(inputParamJson.getString("inputParamId"))) &&
                            !Validate.isEmpty(inputParamJson.getString("config"))){
                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                            redisClient.set(RedisKey.dataSourceConfig.name(),inputParamJson.getString("config"));
                        }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                            redisClient.set(RedisKey.mDataSourceConfig.name(),inputParamJson.getString("config"));
                        }
                    }else {
                        workFlowTemplateNodeParamBo.setConfig(inputParamJson.getString("config"));
                    }
                    workFlowTemplateDataService.addWorkFlowTemplateNodeParam(workFlowTemplateNodeParamBo);
                }
            }

            //1.2 继续添加输出字段
            //首先判断其节点类型
            switch (typeNo){
                case Constants.WORK_FLOW_TYPE_NO_DATAIMPORT:
                    List<String> list = new ArrayList<>();
                    //取出导入节点的字段映射。然后查询base系统，进行聚合
                    for (Object o : jsonArray) {
                        JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                        WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamJson.getInteger("inputParamId"));
                        if(workFlowInputParamBo.getParamEnName().equals("origainRelation")){
                            String origainRelation = inputParamJson.getString("value");
                            if(!Validate.isEmpty(origainRelation)){
                                com.alibaba.fastjson.JSONArray origainRelationJson = JSONObject.parseArray(origainRelation);
                                for(Object object :origainRelationJson){
                                    JSONObject origainRelationJsonObject = JSONObject.parseObject(object.toString());
                                    if(!Validate.isEmpty(origainRelationJsonObject.getString("key"))){
                                        list.add(origainRelationJsonObject.getString("key"));
                                    }
                                }
                                //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                                if(!Validate.isEmpty(visWorkFlowService.getWorkFlowTemplateOutputFiledList(workFlowNodeBO.getFlowId().intValue()))){
                                    visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                                }
                                addWorkFlowOutputFiled(workFlowNodeBO,list,typeNo,datasourceType);
                                break;
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATACRAWL:
                    //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                    if(!Validate.isEmpty(visWorkFlowService.getWorkFlowTemplateOutputFiledList(workFlowNodeBO.getFlowId().intValue()))){
                        visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                    }
                    addWorkFlowOutputFiled(workFlowNodeBO,null,typeNo,datasourceType);
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL:
                    //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                    if(!Validate.isEmpty(visWorkFlowService.getWorkFlowTemplateOutputFiledList(workFlowNodeBO.getFlowId().intValue()))){
                        visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                    }
                    addWorkFlowOutputFiled(workFlowNodeBO,null,typeNo,datasourceType);
                    break;
                case Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT:
                    if(!Validate.isEmpty(analysisLevel)){
                        List<JobTypeResultField> jobTypeResultFieldList =
                                getJobTypeResultFieldListByResultTypeId(analysisLevel);
                        if(!Validate.isEmpty(jobTypeResultFieldList)){
                            if(!Validate.isEmpty(visWorkFlowService.getWorkFlowTemplateOutputFiledList(workFlowNodeBO.getFlowId().intValue()))){
                                visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList){
                                String filedType = "";
                                TemplateOutputFiledBO templateOutputFiledBO = new TemplateOutputFiledBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                templateOutputFiledBO.setFiledCnName(jobTypeResultField.getColName());
                                templateOutputFiledBO.setFiledEnName(jobTypeResultField.getFieldName());
                                templateOutputFiledBO.setFlowId(workFlowNodeBO.getFlowId().intValue());
                                templateOutputFiledBO.setFiledType(filedType);
                                templateOutputFiledBO.setStorageTypeTable(analysisLevel);
                                templateOutputFiledBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION:
                    if(!Validate.isEmpty(analysisLevel)){
                        List<JobTypeResultField> jobTypeResultFieldList2 =
                                getJobTypeResultFieldListByResultTypeId(analysisLevel);
                        if(!Validate.isEmpty(jobTypeResultFieldList2)){
                            if(!Validate.isEmpty(visWorkFlowService.getWorkFlowTemplateOutputFiledList(workFlowNodeBO.getFlowId().intValue()))){
                                visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList2){
                                if(jobTypeResultField.getColName().equals("主题json")||
                                        jobTypeResultField.getColName().equals("话题json")){
                                    continue;
                                }
                                String filedType = "";
                                TemplateOutputFiledBO templateOutputFiledBO = new TemplateOutputFiledBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                templateOutputFiledBO.setFiledCnName(jobTypeResultField.getColName());
                                templateOutputFiledBO.setFiledEnName(jobTypeResultField.getFieldName());
                                templateOutputFiledBO.setFlowId(workFlowNodeBO.getFlowId().intValue());
                                templateOutputFiledBO.setFiledType(filedType);
                                templateOutputFiledBO.setStorageTypeTable(analysisLevel);
                                templateOutputFiledBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING:
                    if(!Validate.isEmpty(analysisLevel)){
                        List<JobTypeResultField> jobTypeResultFieldList3 =
                                getJobTypeResultFieldListByResultTypeId(analysisLevel);
                        if(!Validate.isEmpty(jobTypeResultFieldList3)){
                            if(!Validate.isEmpty(visWorkFlowService.getWorkFlowTemplateOutputFiledList(workFlowNodeBO.getFlowId().intValue()))){
                                visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList3){
                                if(jobTypeResultField.getColName().equals("分词结果")||
                                        jobTypeResultField.getColName().equals("关键词结果")||
                                        jobTypeResultField.getColName().equals("话题json")){
                                    continue;
                                }
                                String filedType = "";
                                TemplateOutputFiledBO templateOutputFiledBO = new TemplateOutputFiledBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                templateOutputFiledBO.setFiledCnName(jobTypeResultField.getColName());
                                templateOutputFiledBO.setFiledEnName(jobTypeResultField.getFieldName());
                                templateOutputFiledBO.setFlowId(workFlowNodeBO.getFlowId().intValue());
                                templateOutputFiledBO.setFiledType(filedType);
                                templateOutputFiledBO.setStorageTypeTable(analysisLevel);
                                templateOutputFiledBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION:
                    if(!Validate.isEmpty(analysisLevel)){
                        List<JobTypeResultField> jobTypeResultFieldList4 =
                                getJobTypeResultFieldListByResultTypeId(analysisLevel);
                        if(!Validate.isEmpty(jobTypeResultFieldList4)){
                            if(!Validate.isEmpty(visWorkFlowService.getWorkFlowTemplateOutputFiledList(workFlowNodeBO.getFlowId().intValue()))){
                                visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList4){
                                if(jobTypeResultField.getColName().equals("分词结果")||
                                        jobTypeResultField.getColName().equals("关键词结果")||
                                        jobTypeResultField.getColName().equals("主题json")){
                                    continue;
                                }
                                String filedType = "";
                                TemplateOutputFiledBO templateOutputFiledBO = new TemplateOutputFiledBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                templateOutputFiledBO.setFiledCnName(jobTypeResultField.getColName());
                                templateOutputFiledBO.setFiledEnName(jobTypeResultField.getFieldName());
                                templateOutputFiledBO.setFlowId(workFlowNodeBO.getFlowId().intValue());
                                templateOutputFiledBO.setFiledType(filedType);
                                templateOutputFiledBO.setStorageTypeTable(analysisLevel);
                                templateOutputFiledBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT:
                    break;
            }
        }

        //该map主要用于在校验是用
        Map<Long,WorkFlowNodeBO> detailMap = new HashMap<>();
        //2 再次进行遍历 进行next_folw_detail和pre_flow_detail的更新
        for(Object node : nodeList){
            WorkFlowNodeBO workFlowNodeBO = new WorkFlowNodeBO();
            JSONObject j = JSONObject.parseObject(node.toString());
            String nodeId = j.getString("nodeId");
            //获取上节点的nodeId
            com.alibaba.fastjson.JSONArray froms = j.getJSONArray("froms");
            String preFlowDetailIds = "";
            if(!Validate.isEmpty(froms)){
                for(int i= 0;i<froms.size();i++){
                    Long preFlowDetailLong =  nodeIdFlowDetailMap.get(froms.get(i));
                    preFlowDetailIds += preFlowDetailLong+",";
                }
                preFlowDetailIds = preFlowDetailIds.substring(0,preFlowDetailIds.length()-1);
                workFlowNodeBO.setPreFlowIdIds(preFlowDetailIds);
            }else{
                workFlowNodeBO.setPreFlowIdIds("0");
            }
            //获取下节点的nodeId
            com.alibaba.fastjson.JSONArray tos = j.getJSONArray("tos");
            String nextFlowDetailIds = "";
            if(!Validate.isEmpty(tos)){
                for(int i= 0;i<tos.size();i++){
                    if(tos.get(i) instanceof com.alibaba.fastjson.JSONArray){
                        com.alibaba.fastjson.JSONArray subTos = ((com.alibaba.fastjson.JSONArray) tos.get(i));
                        for(int k = 0;k<subTos.size();k++){
                            Long nextFlowDetailLong =  nodeIdFlowDetailMap.get(subTos.get(k));
                            nextFlowDetailIds += nextFlowDetailLong+",";
                        }
                    }else{
                        Long nextFlowDetailLong =  nodeIdFlowDetailMap.get(tos.get(i));
                        nextFlowDetailIds += nextFlowDetailLong+",";
                    }
                }
                nextFlowDetailIds = nextFlowDetailIds.substring(0,nextFlowDetailIds.length()-1);
                workFlowNodeBO.setNextFlowIdIds(nextFlowDetailIds);
            }else{
                workFlowNodeBO.setNextFlowIdIds(nextFlowDetailIds);
            }
            workFlowNodeBO.setFlowId(nodeIdFlowDetailMap.get(nodeId));
            workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
            detailMap.put(workFlowNodeBO.getFlowId(),workFlowNodeBO);
        }

        //3 这一步主要判断该工作流里是否有条件节点，如果有则需要将条件节点的paramValue值附上deitailId
        for(Map.Entry<String,Long> entry : nodeIdFlowDetailMap.entrySet()){
            Long detailId = entry.getValue();//取出每一个nodeId所对应的detailId
            WorkFlowNodeBO workFlowNodeBO = flowDetailIdWorkFlowDetailMap.get(detailId);//查询出每个detailId对应的workFlowDetail节点信息
            if(workFlowNodeBO.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_CONDITION)){//判断该节点是否为条件节点
                List<WorkFlowTemplateNodeParamBo> list = workFlowTemplateDataService.getWorkFlowTemplateNodeParamByFlowId(detailId);//如果是的话则查询出该节点所有的输入参数值
                if(!Validate.isEmpty(list)){
                    loop : for(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo : list){//遍历该节点的所有参数值
                        int inputParamId = workFlowTemplateNodeParamBo.getInputParamId();
                        if(inputParamId == 42){//找到条件设置的输入参数，并取出其值
                            String inputParamValue = workFlowTemplateNodeParamBo.getInputParamValue();
                            com.alibaba.fastjson.JSONArray inputParamValueArray = com.alibaba.fastjson.JSONArray.parseArray(inputParamValue);//转为jsonArray
                            if(!Validate.isEmpty(inputParamValueArray)){//判空
                                for(Object o : inputParamValueArray){
                                    List<Long> nextDetailIdList = new ArrayList<>();
                                    JSONObject conditionJson =(JSONObject) o;//注意这里使用强转，不会产生新的对象
                                    com.alibaba.fastjson.JSONArray nodeIdArray = conditionJson.getJSONArray("nodeId");//取出其所有下节点的nodeId
                                    if(!Validate.isEmpty(nodeIdArray)){//判空
                                        for(Object nodeId : nodeIdArray){
                                            Long nextDetailId = nodeIdFlowDetailMap.get(nodeId);//取出每个下节点所对应的detailId，并放入list
                                            nextDetailIdList.add(nextDetailId);
                                        }
                                        conditionJson.put("nextDetailId",nextDetailIdList);//在新增一个jsonObject,同时inputParamValueArray也被改变
                                    }
                                }
                                workFlowTemplateNodeParamBo.setInputParamValue(inputParamValueArray.toJSONString());
                                workFlowTemplateDataService.updateWorkFlowTemplateNodeParam(workFlowTemplateNodeParamBo);//在将其重新更新
                                break loop;
                            }else{
                                break loop;
                            }
                        }
                    }
                }
            }
        }

        //4 该步骤主要为输解决出节点和数据筛选节点配置输出字段，因为这两个节点必须要上一个节点的信息，所以只能在更新了上下节点关系后才能添加输出字段
        List<String> TypeNoList = new ArrayList<>();
        TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT);
        TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATAFILTER);
        TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_CONDITION);
        List<WorkFlowNodeBO> workFlowNodeBOList1 = workFlowTemplateDataService.getWorkFlowNodeListByTypeNoList(TypeNoList,Long.parseLong(templateId));
        if(!Validate.isEmpty(workFlowNodeBOList1)){
            for(WorkFlowNodeBO workFlowNodeBO : workFlowNodeBOList1){
                if(workFlowNodeBO.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT)){
                    //查到上一个节点的信息，并判断上一个节点是什么类型的节点。
                    String preFlowIdIds = workFlowNodeBO.getPreFlowIdIds();
                    if(!preFlowIdIds.equals("0")){
                        WorkFlowNodeBO workFlowNodeBO1 = workFlowTemplateDataService.getWorkFlowDetailByFlowId(Long.parseLong(preFlowIdIds));
                        //获得上节点的输出字段。
                        List<TemplateOutputFiledBO> templateOutputFiledBO = visWorkFlowService.getWorkFlowTemplateOutputFiledList(Integer.parseInt(preFlowIdIds));
                        //如果上节点是抓取或者是导入，则需要将输出节点选择的字段和上节点的输出字段做比较。
                        if(workFlowNodeBO1.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)||
                                workFlowNodeBO1.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                            String [] fieldArray = null;
                            //更具该该输出节点的detailId拿到该输出节点的所有配置，然后找到一个叫‘选择字段’的配置。并取出其值
                            List<WorkFlowTemplateNodeParamBo> workFlowTemplateNodeParamBoList = workFlowDataService.getTemplateNodeParamByTemplateFlowId(workFlowNodeBO.getFlowId());
                            for(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo :workFlowTemplateNodeParamBoList){
                                if(workFlowTemplateNodeParamBo.getInputParamCnName().equals("选择字段")){
                                    String fields = workFlowTemplateNodeParamBo.getInputParamValue();
                                    if (!Validate.isEmpty(fields)) {
                                        fieldArray = fields.split(",");
                                        break;
                                    }
                                }
                            }
                            if(fieldArray!=null){
                                Map<String,TemplateOutputFiledBO> visWorkFlowBOMap = new HashMap<>();
                                for(TemplateOutputFiledBO templateOutputFiledBO1 : templateOutputFiledBO){
                                    visWorkFlowBOMap.put(templateOutputFiledBO1.getFiledEnName(),templateOutputFiledBO1);
                                }
                                //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                                if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowNodeBO.getFlowId().intValue()))){
                                    visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                                }
                                for(int i = 0;i<fieldArray.length;i++){
                                    if(visWorkFlowBOMap.get(fieldArray[i]) != null){//如果有匹配的字段 就重新添加到输出节点的输出字段
                                        TemplateOutputFiledBO templateOutputFiledBO1 = visWorkFlowBOMap.get(fieldArray[i]);
                                        templateOutputFiledBO1.setFlowId(workFlowNodeBO.getFlowId().intValue());
                                        visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO1);
                                    }
                                }
                            }
                        }else{
                            //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowNodeBO.getFlowId().intValue()))){
                                visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                            }
                            for(TemplateOutputFiledBO templateOutputFiledBO1 : templateOutputFiledBO){
                                templateOutputFiledBO1.setFlowId(workFlowNodeBO.getFlowId().intValue());
                                visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO1);
                            }
                        }
                    }
                }else if(workFlowNodeBO.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAFILTER)||
                        workFlowNodeBO.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_CONDITION)){
                    //查到上一个节点的信息。
                    String preFlowIdIds = workFlowNodeBO.getPreFlowIdIds();
                    if(!preFlowIdIds.equals("0")){
                        //根据业务。数据筛选节点和条件节点的上节点有且只有一个。
                        List<TemplateOutputFiledBO> list = visWorkFlowService.getWorkFlowTemplateOutputFiledList(Integer.parseInt(preFlowIdIds));
                        //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                        if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowNodeBO.getFlowId().intValue()))){
                            visWorkFlowService.delWorkFlowTemplateOutputFiled(workFlowNodeBO.getFlowId());
                        }
                        for(TemplateOutputFiledBO templateOutputFiledBO : list){
                            templateOutputFiledBO.setFlowId(workFlowNodeBO.getFlowId().intValue());//将每个上节点的输出字段重新赋值给当前数据筛选节点
                            visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO);//重新添加。。
                        }
                    }
                }
            }
        }

        //5 校验项目
        //如果有被删除的节点，则直接将他的下节点更新为无效。并将该模板更新为无效
        int count = 0;
        if(!Validate.isEmpty(deletedNextDetailList)){
            for (Long flowId: deletedNextDetailList) {
                //这里需要确认被删除的节点的下级节点是否已经连接了上级节点。如果连接了上级节点，则不更新状态
                WorkFlowNodeBO workFlowNodeBO = detailMap.get(flowId);
                if(Validate.isEmpty(workFlowNodeBO.getPreFlowIdIds())){
                    //将下个节点设置为无效
                    WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                    workFlowNodeBO1.setFlowId(flowId);
                    workFlowNodeBO1.setJobStatus(5);
                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                }else{
                    //因为可能涉及到有多个被删除的节点，这里使用计数的方式，如果被删除节点的下级节点重新连接了上级节点，则+1。
                    count++;
                }
                //最后判断所有的被删除节点的下级节点是否都连接了上级节点，如果全都链接了。则继续使用校验的方法。如果没有全部链接，则更新模板状态
                if(count == deletedNextDetailList.size()) {
                    verifyProjectFlowDetail(templateId, detailMap);
                }else {
                    //再将该模板设置为无效状态
                    WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                    workFlowTemplateBO.setStatus(0);
                    workFlowTemplateBO.setId(Integer.parseInt(templateId));
                    workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                }
            }
        }else {
            verifyProjectFlowDetail(templateId,detailMap);
        }

        //6 保存图片,如果有节点才保存。没有节点则不保存图片，并且将该工作流设置为配置中
        List<Map<String,Object>> reslutList = new ArrayList<>();
        if(!Validate.isEmpty(nodeList)){
            String imgStr = jsonObject.getString("file");
            imgStr = imgStr.split(",")[1];
            upload = System.getProperty("upload.dir");

            if(Validate.isEmpty(upload)){
                upload = "/data/upload/dev_dpmbs/";
            }
            if(!upload.endsWith("/")){
                upload+="/";
            }

            String imgPath = upload+IMG_PATH+templateId+".png";
            LoggerUtil.infoTrace(loggerName,"图片路径为:"+imgPath);

            Base64Util.GenerateImage(imgStr,imgPath);
            WorkFlowTemplateBO workFlowTemplateBO = new WorkFlowTemplateBO();
            workFlowTemplateBO.setImgUrl("/"+IMG_PATH+templateId+".png");
//            workFlowTemplateBO.setStatus(1);
            workFlowTemplateBO.setId(Integer.parseInt(templateId));
            workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);

            for(Map.Entry<String,Long> entry : nodeIdFlowDetailMap.entrySet()){
                Map<String,Object> resultMap = new HashMap<>();
                resultMap.put("nodeId",entry.getKey());
                resultMap.put("detailId",entry.getValue());
                List<WorkFlowTemplateNodeParamBo> list = workFlowTemplateDataService
                        .getWorkFlowTemplateNodeParamByFlowId(entry.getValue());
                List<Map<String,Object>> reslutParamList = new ArrayList<>();
                for(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo : list){
                    Map<String,Object> map = new HashMap<>();
                    map.put("paramId",workFlowTemplateNodeParamBo.getParamId());
                    map.put("inputParamId",workFlowTemplateNodeParamBo.getInputParamId());
                    reslutParamList.add(map);
                }
                resultMap.put("paramArray",reslutParamList);
                reslutList.add(resultMap);
            }
        }else{
            WorkFlowTemplateBO workFlowTemplateBO = new WorkFlowTemplateBO();
            workFlowTemplateBO.setStatus(0);
            workFlowTemplateBO.setId(Integer.parseInt(templateId));
            workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
        }
        return reslutList;
    }

    /**
     * 根据数据源类型查询出存储字段并添加至work_flow_output_filed表中
     * @param workFlowNodeBO
     * @param list
     * @param typeNo
     * @param dataSourceType
     * @throws WebException
     */
    public void addWorkFlowOutputFiled(WorkFlowNodeBO workFlowNodeBO,List<String> list,String typeNo,String dataSourceType)throws WebException{
        if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)||
                typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
            if(!Validate.isEmpty(dataSourceType)){
                List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationHasRuleList(dataSourceType,typeNo);
                StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(dataSourceType));
                for(StorageTypeFieldPO storageTypeFieldPO :storageTypeFieldPOList){
                    //创建输出表bo
                    TemplateOutputFiledBO templateOutputFiledBO = new TemplateOutputFiledBO();
                    templateOutputFiledBO.setFiledCnName(storageTypeFieldPO.getFieldCnName());
                    templateOutputFiledBO.setFiledEnName(storageTypeFieldPO.getFieldEnName());
                    templateOutputFiledBO.setFiledId(storageTypeFieldPO.getId());
                    templateOutputFiledBO.setFlowId(workFlowNodeBO.getFlowId().intValue());
                    templateOutputFiledBO.setFiledType(storageTypeFieldPO.getFieldType());
                    templateOutputFiledBO.setIsCustomed(0);
                    templateOutputFiledBO.setStorageTypeTable(storageTypePO.getStorageTypeTable());
                    visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO);
                }
            }
        }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)){
            List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(dataSourceType);
            StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(dataSourceType));
            Map<String,StorageTypeFieldPO> map = new HashMap<>();
            for(StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList){
                map.put(storageTypeFieldPO.getFieldEnName(),storageTypeFieldPO);
            }
            for(String str : list){
                StorageTypeFieldPO storageTypeFieldPO = map.get(str);
                if(storageTypeFieldPO!=null){
                    //创建输出表bo
                    TemplateOutputFiledBO templateOutputFiledBO = new TemplateOutputFiledBO();
                    templateOutputFiledBO.setFiledCnName(storageTypeFieldPO.getFieldCnName());
                    templateOutputFiledBO.setFiledEnName(storageTypeFieldPO.getFieldEnName());
                    templateOutputFiledBO.setFiledId(storageTypeFieldPO.getId());
                    templateOutputFiledBO.setFlowId(workFlowNodeBO.getFlowId().intValue());
                    templateOutputFiledBO.setFiledType(storageTypeFieldPO.getFieldType());
                    templateOutputFiledBO.setIsCustomed(0);
                    templateOutputFiledBO.setStorageTypeTable(storageTypePO.getStorageTypeTable());
                    visWorkFlowService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO);
                }
            }
        }
    }

    /**
     * 根据ResultTypeId查询job_type_result_field得到不同分析层级的所有字段
     * @param analysisLevel
     * @return
     */
    public List<JobTypeResultField> getJobTypeResultFieldListByResultTypeId(String analysisLevel){
        long jobResultTypeId = 0;
        switch (analysisLevel){
            case "sentence":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_SENTENCE;
                break;
            case "section":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_SECTION;
                break;
            case "article":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_ARTICLE;
                break;
        }
        List<JobTypeResultField> jobTypeResultFieldList = jobTypeService.getResultFieldListByResultTypeId(jobResultTypeId);
        return jobTypeResultFieldList;
    }

    /**
     * 校验项目
     * @param  detailMap
     * @param  templateId
     * @throws BizException
     */
    public void verifyProjectFlowDetail(String templateId,Map<Long,WorkFlowNodeBO> detailMap) throws BizException{
        //尝试校验各个节点的依赖。并修改其模板的状态
        //先根据模板的work_flow_node_id查询该模板下所有的输入参数值
        Map<String,String> map = new HashMap<>();
        map.put("workFlowTemplateId",templateId);
        List<WorkFlowTemplateNodeParamBo> workFlowTemplateNodeParamBos =
                workFlowDataService.getWorkFlowTemplateNodeParamBoListByMap(map);
        //查询所有不能为空的参数
        List<WorkFlowInputParamRelationBO> workFlowInputParamRelationBOS = workFlowDataService.getWorkFlowInputParamRelationBO();
        //聚合
        Map<Integer,WorkFlowInputParamRelationBO> workFlowInputParamRelationBOMap = new HashMap<>();
        for(WorkFlowInputParamRelationBO workFlowInputParamRelationBO : workFlowInputParamRelationBOS){
            workFlowInputParamRelationBOMap.put(workFlowInputParamRelationBO.getInputParamId(),workFlowInputParamRelationBO);
        }

        boolean flag = true;
        loopOut:for(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo : workFlowTemplateNodeParamBos){//遍历所有的输入参数
            com.alibaba.fastjson.JSONArray jsonArray = null;
            int inputParamId = workFlowTemplateNodeParamBo.getInputParamId();
            WorkFlowInputParamRelationBO workFlowInputParamRelationBO = workFlowInputParamRelationBOMap.get(inputParamId);
            if(workFlowInputParamRelationBO!=null){
                if(Validate.isEmpty(workFlowTemplateNodeParamBo.getInputParamValue())){//如果某一个输入参数的值为空，则表示该工作流配置未完成。需将工作流更新为配置中
                    WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                    workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                    workFlowNodeBO1.setJobStatus(5);
                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                    WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                    workFlowTemplateBO.setStatus(0);
                    workFlowTemplateBO.setId(Integer.parseInt(templateId));
                    workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                    flag = false;
                    break;
                }else if(workFlowInputParamRelationBO.getRelationType().equals("rely")){
                    WorkFlowNodeBO workFlowNodeBO = detailMap.get(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                    String preDetailId = workFlowNodeBO.getPreFlowIdIds().split(",")[0];
                    if(!Validate.isEmpty(preDetailId)){
                        List<TemplateOutputFiledBO> templateOutputFiledBOList = visWorkFlowService.getWorkFlowTemplateOutputFiledList(Integer.parseInt(preDetailId));
                        List<String> fieldEnNameList = new ArrayList<>();
                        for(TemplateOutputFiledBO templateOutputFiledBO : templateOutputFiledBOList){
                            fieldEnNameList.add(templateOutputFiledBO.getFiledEnName());
                        }
                        switch (workFlowInputParamRelationBO.getInputParamId()) {
                            case 18:
                                //jsonArray = JSONObject.parseObject(workFlowNodeParamBo.getConfig()).getJSONArray("list");
                                jsonArray = JSONObject.parseArray(workFlowTemplateNodeParamBo.getInputParamValue());
                                for (Object o1 : jsonArray) {
                                    JSONObject jsonObject1 = JSONObject.parseObject(o1.toString());
                                    String fieldEnName = jsonObject1.getString("contentType");
                                    //String fieldEnName = jsonObject1.getString("fieldEnName");
                                    if (!fieldEnNameList.contains(fieldEnName)) {
                                        WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                                        workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                                        workFlowNodeBO1.setJobStatus(5);
                                        workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                                        //并将当前工作流设置为配置中
                                        WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                                        workFlowTemplateBO.setStatus(0);
                                        workFlowTemplateBO.setId(Integer.parseInt(templateId));
                                        workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                                        flag = false;
                                        break loopOut;
                                    }
                                }
                                if(flag){
                                    workFlowNodeBO.setJobStatus(0);
                                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
                                }
                                break;
                            case 40:
                                jsonArray = JSONObject.parseArray(workFlowTemplateNodeParamBo.getInputParamValue());
                                for (Object o1 : jsonArray) {
                                    JSONObject jsonObject1 = JSONObject.parseObject(o1.toString());
                                    String filed = jsonObject1.getString("filed");
                                    if (!fieldEnNameList.contains(filed)) {
                                        WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                                        workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                                        workFlowNodeBO1.setJobStatus(5);
                                        workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                                        //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                        WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                                        workFlowTemplateBO.setStatus(0);
                                        workFlowTemplateBO.setId(Integer.parseInt(templateId));
                                        workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                                        flag = false;
                                        break loopOut;
                                    }
                                }
                                if(flag){
                                    workFlowNodeBO.setJobStatus(0);
                                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
                                }
                                break;
                            case 42:
                                jsonArray = JSONObject.parseArray(workFlowTemplateNodeParamBo.getInputParamValue());
                                for (Object o1 : jsonArray) {
                                    JSONObject jsonObject1 = JSONObject.parseObject(o1.toString());
                                    com.alibaba.fastjson.JSONArray paramArray = jsonObject1.getJSONArray("paramArray");
                                    if(!Validate.isEmpty(paramArray)){
                                        for(Object param : paramArray){
                                            JSONObject paramJson = JSONObject.parseObject(param.toString());
                                            String filed = paramJson.getString("filed");
                                            if (!fieldEnNameList.contains(filed)) {
                                                WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                                                workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                                                workFlowNodeBO1.setJobStatus(5);
                                                workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                                                //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                                WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                                                workFlowTemplateBO.setStatus(0);
                                                workFlowTemplateBO.setId(Integer.parseInt(templateId));
                                                workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                                                flag = false;
                                                break loopOut;
                                            }
                                        }
                                    }
                                }
                                if(flag){
                                    workFlowNodeBO.setJobStatus(0);
                                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
                                }
                                break;
                            case 37:
                                //如果是输出节点的字段
                                String[] strArray = workFlowTemplateNodeParamBo.getInputParamValue().split(",");
                                //查询上个节点的type_no
                                 WorkFlowNodeBO workFlowNodeBO2=workFlowTemplateDataService.getWorkFlowDetailByFlowId(Long.parseLong(preDetailId));
                                 //判断是否是数据导入
                                 if (Constants.WORK_FLOW_TYPE_NO_DATAIMPORT.equals(workFlowNodeBO2.getTypeNo())){
                                     List<WorkFlowTemplateNodeParamBo> workFlowTemplateNodeParamBoList=workFlowTemplateDataService.getWorkFlowTemplateNodeParamByFlowId(workFlowNodeBO2.getFlowId());
                                        String dataSourceTypeId="";
                                     for (WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo1:workFlowTemplateNodeParamBoList
                                          ) {
                                         //获取数据类型id
                                         if(workFlowTemplateNodeParamBo1.getInputParamId()==3){
                                             dataSourceTypeId= workFlowTemplateNodeParamBo1.getInputParamValue();

                                         }
                                     }
                                     List<StorageTypeFieldPO> datasourceTypeId1 =dataSourceTypeService.getDataSourceTypeRelationList(dataSourceTypeId);
                                     List<String> fieldEnNameList1 = new ArrayList<>();
                                     for (StorageTypeFieldPO storageTypeFieldPO:datasourceTypeId1
                                             ) {
                                         fieldEnNameList1.add(storageTypeFieldPO.getFieldEnName());
                                     }
                                     for (String o1 : strArray) {
                                         if (!fieldEnNameList1.contains(o1)) {
                                             WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                                             workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                                             workFlowNodeBO1.setJobStatus(5);
                                             workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                                             //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                             //并将当前工作流设置为配置中
                                             WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                                             workFlowTemplateBO.setStatus(0);
                                             workFlowTemplateBO.setId(Integer.parseInt(templateId));
                                             workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                                             flag = false;
                                             break loopOut;
                                         }
                                     }
                                 }else{
                                     for (String o1 : strArray) {
                                         if (!fieldEnNameList.contains(o1)) {
                                             WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                                             workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                                             workFlowNodeBO1.setJobStatus(5);
                                             workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                                             //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                             //并将当前工作流设置为配置中
                                             WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                                             workFlowTemplateBO.setStatus(0);
                                             workFlowTemplateBO.setId(Integer.parseInt(templateId));
                                             workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                                             flag = false;
                                             break loopOut;
                                         }
                                     }
                                 }
                                if(flag){
                                    workFlowNodeBO.setJobStatus(0);
                                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
                                }
                                break;
                            case 44:
                                //jsonArray = JSONObject.parseObject(workFlowNodeParamBo.getConfig()).getJSONArray("list");
                                jsonArray = JSONObject.parseArray(workFlowTemplateNodeParamBo.getInputParamValue());
                                for (Object o1 : jsonArray) {
                                    JSONObject jsonObject1 = JSONObject.parseObject(o1.toString());
                                    String file = jsonObject1.getString("fieldEnName");
                                    if (!fieldEnNameList.contains(file)) {
                                        WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                                        workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                                        workFlowNodeBO1.setJobStatus(5);
                                        workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                                        //并将当前工作流设置为配置中
                                        WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                                        workFlowTemplateBO.setStatus(0);
                                        workFlowTemplateBO.setId(Integer.parseInt(templateId));
                                        workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                                        flag = false;
                                        break loopOut;
                                    }
                                }
                                if(flag){
                                    workFlowNodeBO.setJobStatus(0);
                                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
                                }
                                break;
                            default:
                                String storageTable = workFlowTemplateNodeParamBo.getInputParamValue();
                                if (!templateOutputFiledBOList.get(0).getStorageTypeTable().equals(storageTable)) {
                                    WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                                    workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                                    workFlowNodeBO1.setJobStatus(5);
                                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                                    WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
                                    workFlowTemplateBO.setStatus(0);
                                    workFlowTemplateBO.setId(Integer.parseInt(templateId));
                                    workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
                                    flag = false;
                                    break loopOut;
                                }
                                if(flag){
                                    workFlowNodeBO.setJobStatus(0);
                                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO);
                                }
                        }
                    }
                }else if(workFlowTemplateNodeParamBo.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT) || workFlowTemplateNodeParamBo.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                    WorkFlowNodeBO workFlowNodeBO1=new WorkFlowNodeBO();
                    workFlowNodeBO1.setFlowId(workFlowTemplateNodeParamBo.getWorkFlowTemplateNodeId().longValue());
                    workFlowNodeBO1.setJobStatus(0);
                    workFlowTemplateDataService.updateWorkFlowNode(workFlowNodeBO1);
                }
            }
        }
        if(flag){
            //如果所有节点的配置都没有问题则将该模板设置为有效状态
            WorkFlowTemplateBO workFlowTemplateBO= new WorkFlowTemplateBO();
            workFlowTemplateBO.setStatus(1);
            workFlowTemplateBO.setId(Integer.parseInt(templateId));
            workFlowTemplateDataService.updateWorkTemplate(workFlowTemplateBO);
        }
    }

    //递归查询将所有的下级节点的参数以及信息加载至exchangeNodeList中
    public com.alibaba.fastjson.JSONArray getNextNodeDataByNodeId(String nodeId, Map<String,JSONObject> dataNodeIdMap, com.alibaba.fastjson.JSONArray exchangeNodeList){
        JSONObject jsonObject1 = dataNodeIdMap.get(nodeId);
        exchangeNodeList.add(jsonObject1);
        com.alibaba.fastjson.JSONArray jsonArray = jsonObject1.getJSONArray("tos");
        if(Validate.isEmpty(jsonArray)){
            return exchangeNodeList;
        }else{
            for(Object o : jsonArray){
                if(o instanceof String){
                    exchangeNodeList = getNextNodeDataByNodeId(o.toString(),dataNodeIdMap,exchangeNodeList);
                }else{
                    for(Object o1 : (com.alibaba.fastjson.JSONArray)o){
                        exchangeNodeList = getNextNodeDataByNodeId(o1.toString(),dataNodeIdMap,exchangeNodeList);
                    }
                }
            }
        }
        return exchangeNodeList;
    }
}