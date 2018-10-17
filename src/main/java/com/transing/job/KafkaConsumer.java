package com.transing.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.logicframework.biz.service.mq.consumer.BaseKafkaConsumer;
import com.jeeframework.logicframework.biz.service.mq.producer.BaseKafkaProducer;
import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.constant.RedisKey;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by byron on 2018/3/22 0022.
 */
public class KafkaConsumer extends BaseKafkaConsumer{
    @Resource
    private BaseDaoRedis redisClient;
    @Resource
    private WorkFlowService workFlowService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;
    @Resource
    private JobTypeService jobTypeService;
    @Resource
    private BaseKafkaProducer kafkaProducer;

    @Override
    public void dealMessage(String message) {
        LoggerUtil.infoTrace("====================kafka收到信息==========================message:"+message);
        try{
            JSONObject jsonObjectStr = JSONObject.parseObject(message);
            if(Validate.isEmpty(jsonObjectStr.getString("detailId"))||
                    Validate.isEmpty(jsonObjectStr.getString("projectId"))||
                    Validate.isEmpty(jsonObjectStr.getString("flowId"))||
                    Validate.isEmpty(jsonObjectStr.getString("dataJsonArray"))||
                    Validate.isEmpty(jsonObjectStr.getString("workFlowId"))){
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }

            Long flowId = jsonObjectStr.getLong("flowId");
            Long projectId = jsonObjectStr.getLong("projectId");
            Long detailId = jsonObjectStr.getLong("detailId");
            Long workFlowId = jsonObjectStr.getLong("workFlowId");
            JSONArray dataJsonArray = JSONArray.parseArray(jsonObjectStr.getString("dataJsonArray"));
            net.sf.json.JSONArray dataJsonArrayObj = net.sf.json.JSONArray.fromObject(dataJsonArray);

            //首先判断该项目是否是可视化项目还是常规项目，利用workFlowId来判断,如果不为0则表示可视化项目
            if(workFlowId != 0){
                //从redis取出workFlowDetailList
                String detailListStr = redisClient.get(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId);
                List<WorkFlowDetail> workFlowDetailListTem;
                if(!Validate.isEmpty(detailListStr) && !"null".equals(detailListStr)){
                    workFlowDetailListTem = JSON.parseArray(detailListStr,WorkFlowDetail.class);
                }else{
                    workFlowDetailListTem = workFlowService.getWorkFlowDetailByWorkFlowId(workFlowId);
                    String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                    redisClient.set(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId,workFlowDetailListStr);
                }
                //聚合workFlowDetailListTem key为节点id，val为节点信息
                Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
                for (WorkFlowDetail workFlowDetail:workFlowDetailListTem) {
                    workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
                }

                //从redis取出workFlowParamList
                String paramListStr = redisClient.get(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId);
                List<WorkFlowParam> workFlowParamList;
                if(!Validate.isEmpty(paramListStr) && !"null".equals(paramListStr)){
                    workFlowParamList = JSON.parseArray(paramListStr,WorkFlowParam.class);
                }else {
                    workFlowParamList = new ArrayList<>();

                    List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowService.getWorkFlowInputParamBoList();//获取所有的输入参数
                    Map<Integer,String> inputParamMap = new HashMap<>();
                    for (WorkFlowInputParamBo workFlowInputParamBo:workFlowInputParamBoList) {//将输入参数list聚合成map  key为输入参数id，value为输入参数英文名
                        inputParamMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo.getParamEnName());
                    }
                    List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowService.getWorkFlowNodeParamByWorkFlowId(workFlowId);

                    Map<Long, net.sf.json.JSONObject> detailJsonMap = new HashMap<>();//用于存放flowDetailId，json的map，jsonObject为该节点所有的输入参数的英文名和值
                    for (WorkFlowNodeParamBo workFlowNodeParamBo:workFlowNodeParamBoList) {//遍历该工作流下所有的输出参数值
                        int inputParamId = workFlowNodeParamBo.getInputParamId();
                        long flowDetailId = workFlowNodeParamBo.getFlowDetailId();
                        net.sf.json.JSONObject jsonObject = detailJsonMap.get(flowDetailId);
                        if(null == jsonObject){
                            jsonObject = new net.sf.json.JSONObject();
                            detailJsonMap.put(flowDetailId,jsonObject);
                        }
                        String paramEnName = inputParamMap.get(inputParamId);
                        String paramValue = workFlowNodeParamBo.getInputParamValue();
                        if(Validate.isEmpty(paramValue)){
                            paramValue = "";
                        }
                        jsonObject.put(paramEnName,paramValue);
                    }

                    for (WorkFlowDetail workFlowDetail:workFlowDetailListTem) {//遍历项目下或者工作流下所有的节点信息

                        WorkFlowParam workFlowParam = new WorkFlowParam();
                        net.sf.json.JSONObject jsonParamObject = new net.sf.json.JSONObject();//创建一个jsonParamObject
                        net.sf.json.JSONObject jsonObject = detailJsonMap.get(workFlowDetail.getFlowDetailId());//获取该节点所有的输入参数以及值
                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(workFlowDetail.getTypeNo())
                                ||Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(workFlowDetail.getTypeNo())){

                            String crawlType = jsonObject.getString("crawlType");//取出英文名为crawlType输入参数的值，即抓取类型

                            if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                                crawlType = "1";//如果是1 ，一表示常规抓取
                                workFlowDetail.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
                                workFlowParam.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
                            }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                                crawlType = "2";
                                workFlowDetail.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);
                                workFlowParam.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);
                            }

                            Object datasourceIdObj = jsonObject.get("datasourceId");//取出数据源的值
                            Object datasouceTypeIdObj = jsonObject.get("datasourceTypeId");//取出数据源类型的值
                            //根据数据源和抓取类型查询数据源po，和数据源类型po
                            DatasourcePO datasourcePO = dataSourceTypeService.getDatasourceById(Long.parseLong(datasourceIdObj.toString()),crawlType);
                            DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(datasouceTypeIdObj.toString()));
                            jsonObject.put("datasourceName",datasourcePO.getDatasourceName());
                            jsonObject.put("datasourceTypeName",datasourceTypePO.getTypeName());
                            net.sf.json.JSONObject jsonParam = new net.sf.json.JSONObject();
                            jsonObject.put("taskName","抓取"+datasourceTypePO.getTypeName());
                            jsonParam.put("jsonParam",jsonObject);
                            jsonParamObject = jsonParam;
                        }else if(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT.equals(workFlowDetail.getTypeNo())){
                            jsonParamObject = jsonObject;
                        }else if(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT.equals(workFlowDetail.getTypeNo())){
                            net.sf.json.JSONObject jsonParam = new net.sf.json.JSONObject();
                            jsonParam.put("jsonParam",jsonObject);
                            jsonParamObject = jsonParam;
                        }else if(Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION.equals(workFlowDetail.getTypeNo())){
                            jsonParamObject = jsonObject;
                        }else if(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING.equals(workFlowDetail.getTypeNo())){
                            jsonParamObject.putAll(jsonObject);
                            jsonParamObject.putAll(jsonObject.getJSONObject("resultsStrategyType"));
                        }else if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(workFlowDetail.getTypeNo())||
                                Constants.WORK_FLOW_TYPE_NO_DATAFILTER.equals(workFlowDetail.getTypeNo())||
                                Constants.WORK_FLOW_TYPE_NO_PUShOSS.equals(workFlowDetail.getTypeNo())||
                                Constants.WORK_FLOW_TYPE_NO_CONDITION.equals(workFlowDetail.getTypeNo())){
                            jsonParamObject = jsonObject;
                        }

                        workFlowParam.setFlowId(workFlowDetail.getFlowId());
                        workFlowParam.setParamType(WorkFlowParam.PARAM_TYPE_PRIVATE);
                        workFlowParam.setFlowDetailId(workFlowDetail.getFlowDetailId());
                        workFlowParam.setTypeNo(workFlowDetail.getTypeNo());
                        workFlowParam.setWorkFlowId(workFlowId);
                        workFlowParam.setProjectId(workFlowDetail.getProjectId());
                        workFlowParam.setJsonParam(jsonParamObject.toString());
                        workFlowParamList.add(workFlowParam);
                    }
                    String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                    redisClient.set(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId,workFlowParamListStr);
                }
                //聚合成map
                Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
                for (WorkFlowParam workFlowParam:workFlowParamList) {
                    workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
                }
                WorkFlowDetail detail = workFlowDetailMap.get(detailId);
                if(null != detail) {

                    Map<String, JobTypeInfo> jobTypeInfoMap = new HashMap();
                    List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();
                    if (null != jobTypeInfoList && jobTypeInfoList.size() > 0) {
                        //聚合jobType 方便 下面根据typeNo 取出jobType对象
                        for (JobTypeInfo jobTypeInfo : jobTypeInfoList) {
                            jobTypeInfoMap.put(jobTypeInfo.getTypeNo(), jobTypeInfo);
                        }
                    }
                    JobTypeInfo detailJobTypeInfo = jobTypeInfoMap.get(detail.getTypeNo());//获取当前节点的类型
                    //判断当前节点是否是流程节点，如果是流程节点，则取出所有下节点的信息。并将数据传递个下节点去执行
                    //如果是状态节点，则直接将当前节点的信息和数据传递到下个topic中去执行 因为所有的状态节点都来自于dpmss并且当前节点已经是下节点的信息。
                    if(detailJobTypeInfo.getJobClassify()==2){
                        kafkaProducer.send(Constants.KAFKA_DPMSS_TOPIC,jsonObjectStr.toJSONString());
                    }else if(detailJobTypeInfo.getJobClassify()==1){
                        //获取下一个节点信息
                        String nextFlowDetailIds = detail.getNextFlowDetailIds();
                        if (!Validate.isEmpty(nextFlowDetailIds)) {
                            String[] nextDetailArray = nextFlowDetailIds.split(",");
                            for (String nextDetailStr : nextDetailArray) {
                                long detailIdLong = Long.parseLong(nextDetailStr);
                                //取出下个节点的workFlowParam信息
                                WorkFlowParam workFlowParam = workFlowParamMap.get(detailIdLong);
                                JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(workFlowParam.getTypeNo());
                                if(jobTypeInfo.getJobType() == 1) {
                                    if (Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(jobTypeInfo.getTypeNo())) {
                                        WorkFlowDetail nextDetail = workFlowDetailMap.get(detailIdLong);
                                        if (null != nextDetail.getFlowId() && nextDetail.getFlowId() > 0) {
                                            DataCrawlPO dataCrawlPO = new DataCrawlPO();
                                            String jsonParam = workFlowParam.getJsonParam();
                                            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(jsonParam);
                                            jsonObject = jsonObject.getJSONObject("jsonParam");
                                            dataCrawlPO.setTaskName("流程抓取" + jsonObject.optString("datasourceTypeName", ""));
                                            DataCrawlJsonParamPO dataCrawlJsonParamPO = new DataCrawlJsonParamPO();
                                            dataCrawlJsonParamPO.setDatasourceTypeId(jsonObject.optString("datasourceTypeId", "0"));
                                            dataCrawlJsonParamPO.setDatasourceId(jsonObject.optString("datasourceId", "0"));
                                            dataCrawlJsonParamPO.setDatasourceName(jsonObject.optString("datasourceName", ""));
                                            dataCrawlJsonParamPO.setDatasourceTypeName(jsonObject.optString("datasourceTypeName", ""));
                                            dataCrawlJsonParamPO.setTaskName("流程抓取" + jsonObject.optString("datasourceTypeName", ""));

                                            String crawlServer = WebUtil.getBaseServerByEnv();
                                            Object resultObj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(), crawlServer + "/common/getCrawlInputParamsByDatasourceType.json?datasourceTypeId=" + dataCrawlJsonParamPO.getDatasourceTypeId(), "get", null);
                                            Map<Long, net.sf.json.JSONObject> inputParamMap = new HashMap<>();
                                            if (null != resultObj) {
                                                net.sf.json.JSONArray jsonArray = (net.sf.json.JSONArray) resultObj;
                                                for (int i = 0; i < jsonArray.size(); i++) {
                                                    net.sf.json.JSONObject inputParam = jsonArray.getJSONObject(i);
                                                    inputParamMap.put(inputParam.getLong("id"), inputParam);
                                                }

                                            }

                                            WorkFlowParam preWorkFlowParam = workFlowParamMap.get(detail.getFlowDetailId());//查询上一个节点的数据源类型id
                                            String preDatasourceTypeId = null;
                                            net.sf.json.JSONObject preJsonObject = net.sf.json.JSONObject.fromObject(preWorkFlowParam.getJsonParam());
                                            Object jsonParamObj = preJsonObject.get("jsonParam");
                                            if (null != jsonParamObj) {
                                                preDatasourceTypeId = net.sf.json.JSONObject.fromObject(jsonParamObj).optString("datasourceTypeId");
                                            } else {
                                                preDatasourceTypeId = preJsonObject.optString("datasourceTypeId");
                                            }

                                            List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(preDatasourceTypeId);
                                            Map<Integer, String> fieldNameMap = new HashMap<>();
                                            for (StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList) {
                                                fieldNameMap.put(storageTypeFieldPO.getId(), storageTypeFieldPO.getFieldEnName());
                                            }
                                            net.sf.json.JSONArray mappingJsonArray = jsonObject.getJSONArray("mappingJson");
                                            net.sf.json.JSONArray inputParamArray = new net.sf.json.JSONArray();
                                            for (int i = 0; i < mappingJsonArray.size(); i++) {
                                                net.sf.json.JSONObject mappingJSONObject = mappingJsonArray.getJSONObject(i);
                                                Long inputParamId = mappingJSONObject.optLong("inputParamId");
                                                net.sf.json.JSONObject inputParam = inputParamMap.get(inputParamId);
                                                String fieldEnName = fieldNameMap.get(mappingJSONObject.optInt("fieldId"));
                                                if (null != inputParam && !Validate.isEmpty(fieldEnName)) {
                                                    StringBuffer paramValueBuff = new StringBuffer("");
                                                    String paramValue = "";
                                                    for (int j = 0; j < dataJsonArrayObj.size(); j++) {
                                                        net.sf.json.JSONObject dataJsonObject = dataJsonArrayObj.getJSONObject(j);
                                                        String fieldValue = dataJsonObject.optString(fieldEnName);
                                                        if (!Validate.isEmpty(fieldValue)) {
                                                            paramValueBuff.append(dataJsonObject.optString(fieldEnName) + ",");
                                                        }
                                                    }
                                                    if (paramValueBuff.length() > 0) {
                                                        paramValue = paramValueBuff.substring(0, paramValueBuff.length() - 1);
                                                    } else {
                                                        throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
                                                    }
                                                    inputParam.put("paramValue", paramValue);
                                                    inputParamArray.add(inputParam);
                                                }

                                            }

                                            dataCrawlJsonParamPO.setInputParamArray(inputParamArray);
                                            dataCrawlPO.setJsonParam(dataCrawlJsonParamPO);
                                            workFlowParam.setJsonParam(JSON.toJSONString(dataCrawlPO));
                                        }
                                        kafkaProducer.send(Constants.KAFKA_M_CRAWL_TOPIC, JSON.toJSONString(workFlowParam));
                                    } else if (Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(jobTypeInfo.getTypeNo())) {
                                        WorkFlowDetail nextDetail = workFlowDetailMap.get(detailIdLong);
                                        if (null != nextDetail) {
                                            String jsonParam = workFlowParam.getJsonParam();
                                            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(jsonParam);
                                            jsonObject = jsonObject.getJSONObject("jsonParam");
                                            DataCrawlPO dataCrawlPO = new DataCrawlPO();
                                            dataCrawlPO.setTaskName("流程抓取" + jsonObject.optString("datasourceTypeName", ""));
                                            DataCrawlJsonParamPO dataCrawlJsonParamPO = new DataCrawlJsonParamPO();
                                            dataCrawlJsonParamPO.setDatasourceTypeId(jsonObject.optString("datasourceTypeId", "0"));
                                            dataCrawlJsonParamPO.setDatasourceId(jsonObject.optString("datasourceId", "0"));
                                            dataCrawlJsonParamPO.setDatasourceName(jsonObject.optString("datasourceName", ""));
                                            dataCrawlJsonParamPO.setDatasourceTypeName(jsonObject.optString("datasourceTypeName", ""));
                                            dataCrawlJsonParamPO.setTaskName("流程抓取" + jsonObject.optString("datasourceTypeName", ""));
                                            WorkFlowParam preWorkFlowParam = workFlowParamMap.get(detail.getFlowDetailId());//查询上一个节点的数据源类型id
                                            String preDatasourceTypeId = null;
                                            net.sf.json.JSONObject preJsonObject = net.sf.json.JSONObject.fromObject(preWorkFlowParam.getJsonParam());
                                            Object jsonParamObj = preJsonObject.get("jsonParam");
                                            if (null != jsonParamObj) {
                                                preDatasourceTypeId = net.sf.json.JSONObject.fromObject(jsonParamObj).optString("datasourceTypeId");
                                            } else {
                                                preDatasourceTypeId = preJsonObject.optString("datasourceTypeId");
                                            }

                                            List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(preDatasourceTypeId);
                                            Map<Integer, String> fieldNameMap = new HashMap<>();
                                            for (StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList) {
                                                fieldNameMap.put(storageTypeFieldPO.getId(), storageTypeFieldPO.getFieldEnName());
                                            }

                                            net.sf.json.JSONArray mappingJsonArray = jsonObject.getJSONArray("mappingJson");

                                            net.sf.json.JSONArray inputParamArray = new net.sf.json.JSONArray();
                                            for (int j = 0; j < dataJsonArrayObj.size(); j++) {

                                                net.sf.json.JSONArray inputArray = new net.sf.json.JSONArray();
                                                for (int i = 0; i < mappingJsonArray.size(); i++) {
                                                    net.sf.json.JSONObject mappingJSONObject = mappingJsonArray.getJSONObject(i);

                                                    Long inputParamId = mappingJSONObject.optLong("inputParamId");

                                                    net.sf.json.JSONObject inputParam = new net.sf.json.JSONObject();

                                                    String fieldEnName = fieldNameMap.get(mappingJSONObject.optInt("fieldId"));

                                                    if (null != inputParam && !Validate.isEmpty(fieldEnName)) {

                                                        net.sf.json.JSONObject dataJsonObject = dataJsonArrayObj.getJSONObject(j);

                                                        String filedValue = dataJsonObject.optString(fieldEnName);

                                                        if (Validate.isEmpty(filedValue)) {
                                                            filedValue = "";
                                                        }

                                                        inputParam.put("paramValue", filedValue);
                                                        inputParam.put("id", inputParamId);

                                                        inputArray.add(inputParam);
                                                    }
                                                }

                                                if (!Validate.isEmpty(inputArray)) {
                                                    inputParamArray.add(inputArray);
                                                }
                                            }

                                            if (!Validate.isEmpty(inputParamArray)) {
                                                dataCrawlPO.setInputParams(JSON.toJSONString(inputParamArray));
                                            } else {
                                                throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
                                            }

                                            dataCrawlJsonParamPO.setInputParamArray(inputParamArray);
                                            dataCrawlPO.setJsonParam(dataCrawlJsonParamPO);
                                            workFlowParam.setJsonParam(JSON.toJSONString(dataCrawlPO));
                                            kafkaProducer.send(Constants.KAFKA_CRAWL_TOPIC, JSON.toJSONString(workFlowParam));
                                        }
                                    }
                                }else if(jobTypeInfo.getJobType() == 2){
                                    if(jobTypeInfo.getJobClassify() == 1) {//流程节点
                                        //判断是否是条件节点
                                        if(jobTypeInfo.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_CONDITION)){
                                            //如果是条件节点，因为条件节点的下节点是放在数据里面的，所以需要从数据中取出下节点的detailId
                                            net.sf.json.JSONObject jsonObject1 = dataJsonArrayObj.getJSONObject(0);
                                            net.sf.json.JSONArray nextDetailIdArray = jsonObject1.getJSONArray("nextDetailId");
                                            //如果下一个节点在数据中nextDetailIdArray中。就直接将数据发个下个节点
                                            if(nextDetailIdArray.contains(nextDetailStr)){
                                                net.sf.json.JSONArray nextJsonArray = new net.sf.json.JSONArray();
                                                //获取传回来detail的数据
                                                String dataSourceType = detail.getDataSourceType();
                                                if(!Validate.isEmpty(dataSourceType) && !Validate.isEmpty(dataJsonArrayObj)) {
                                                    net.sf.json.JSONArray nextIds = new net.sf.json.JSONArray();
                                                    for (Object object : dataJsonArrayObj) {
                                                        net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) object;
                                                        nextIds.add(jsonObject);
                                                    }
                                                    StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(dataSourceType));
                                                    String storageTypeTable = storageTypePO.getStorageTypeTable();
                                                    net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
                                                    jsonObject.put(Constants.DATA_TYPE_KEY, storageTypeTable);
                                                    jsonObject.put(Constants.DATA_IDS_KEY, nextIds);
                                                    nextJsonArray.add(jsonObject);
                                                }

                                                Map<String,String> postData = new HashMap<>();
                                                postData.put("param",nextJsonArray.toString());
                                                postData.put("flowDetailId",Long.toString(workFlowParam.getFlowDetailId()));
                                                postData.put("projectId",Long.toString(workFlowParam.getProjectId()));
                                                postData.put("flowId",Long.toString(workFlowParam.getFlowId()));
                                                postData.put("typeNo",workFlowParam.getTypeNo());
                                                postData.put("workFlowId",workFlowId+"");

                                                net.sf.json.JSONObject jsonParamObject = new net.sf.json.JSONObject();
                                                jsonParamObject.put("jsonParam", net.sf.json.JSONObject.fromObject(workFlowParam.getJsonParam()));
                                                postData.put("jsonParam",jsonParamObject.toString());
                                                postData.put("paramType",Integer.toString(workFlowParam.getParamType()));
                                                kafkaProducer.send(Constants.KAFKA_DPMSS_TOPIC,JSON.toJSONString(postData));
                                            }else{
                                                continue;
                                            }
                                        }else{
                                            net.sf.json.JSONArray nextJsonArray = new net.sf.json.JSONArray();
                                            //获取传回来detail的数据
                                            String dataSourceType = detail.getDataSourceType();
                                            if(!Validate.isEmpty(dataSourceType) && !Validate.isEmpty(dataJsonArrayObj)) {
                                                net.sf.json.JSONArray nextIds = new net.sf.json.JSONArray();
                                                for (Object object : dataJsonArrayObj) {
                                                    net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) object;
                                                    nextIds.add(jsonObject);
                                                }
                                                StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(dataSourceType));
                                                String storageTypeTable = storageTypePO.getStorageTypeTable();
                                                net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
                                                jsonObject.put(Constants.DATA_TYPE_KEY, storageTypeTable);
                                                jsonObject.put(Constants.DATA_IDS_KEY, nextIds);
                                                nextJsonArray.add(jsonObject);
                                            }

                                            Map<String,String> postData = new HashMap<>();
                                            postData.put("param",nextJsonArray.toString());
                                            postData.put("flowDetailId",Long.toString(workFlowParam.getFlowDetailId()));
                                            postData.put("projectId",Long.toString(workFlowParam.getProjectId()));
                                            postData.put("flowId",Long.toString(workFlowParam.getFlowId()));
                                            postData.put("typeNo",workFlowParam.getTypeNo());
                                            postData.put("workFlowId",workFlowId+"");

                                            net.sf.json.JSONObject jsonParamObject = new net.sf.json.JSONObject();
                                            jsonParamObject.put("jsonParam", net.sf.json.JSONObject.fromObject(workFlowParam.getJsonParam()));
                                            postData.put("jsonParam",jsonParamObject.toString());
                                            postData.put("paramType",Integer.toString(workFlowParam.getParamType()));
                                            kafkaProducer.send(Constants.KAFKA_DPMSS_TOPIC,JSON.toJSONString(postData));
                                        }
                                    }
                                }else if(jobTypeInfo.getJobClassify() == 2){//这里如果是状态节点则直接跳过，因为状态节点不在这里执行
                                   continue;
                                }
                            }
                        }
                    }
                }
            }else{
                //常规项目的
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
