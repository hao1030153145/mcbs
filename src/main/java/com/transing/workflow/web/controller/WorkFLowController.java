/**
 * @project: dpmbs
 * @Title: UserController.java
 * @Package: com.transing.dpmbs.web.controller
 * <p>
 * Copyright (c) 2014-2017 Jeeframework Limited, Inc.
 * All rights reserved.
 */
package com.transing.workflow.web.controller;

import com.alibaba.fastjson.JSON;

import com.jeeframework.logicframework.biz.service.mq.producer.BaseKafkaProducer;

import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.encrypt.MD5Util;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.*;
import com.transing.dpmbs.constant.RedisKey;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.integration.bo.ImportData;

import com.transing.dpmbs.integration.bo.ParamBO;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.integration.bo.ProjectOne;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.QuartzManager;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.dpmbs.web.filter.WorkFlowListFilter;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.biz.service.WorkFlowTemplateService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;
import com.transing.workflow.util.WorkFlowExecuteMethod;
import com.transing.workflow.util.quartz.ExecuteCrawlJobQuartz;
import com.transing.workflow.util.quartz.ExecuteGetCountJobQuartz;
import com.transing.workflow.util.quartz.ExecuteStopJobQuartz;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.text.SimpleDateFormat;

@Controller("workFLowController")
@Api(value = "工作流交互", description = "工作流访问接口", position = 2)
public class WorkFLowController {

    @Resource
    private JobTypeService jobTypeService;

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @Resource
    private WorkFlowExecuteMethod workFlowExecuteMethod;

    @Resource
    private ProjectService projectService;

    @Resource
    private BaseDaoRedis redisClient;


    @Resource
    private WorkFlowTemplateService workFlowTemplateService;

    @Resource
    private ParamService paramService;

    @Resource
    private ContentTypeService contentTypeService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;


    public static final String GET_DATASOURCETYPEANDTABLENAME_API = "/common/getDataSourceTypeAndTableName.json";

    @RequestMapping(value = "/toWorkFlow.html", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView toWorkFlow(@RequestParam(value = "toModular",required = true)@ApiParam(value = "要跳转的模块（dataImport 导入" +
            "dataOutput为输出数据" +
            "semanticAnalysisObject语义分析" +
            "themeAnalysisSetting 主题分析" +
            "topicAnalysisDefinition 话题分析" +
            "wordSegmentation分词" +
            "fileOutput文件输出）",required = true) String toModular,
                                   HttpServletRequest req, HttpServletResponse res) {

        String html = "workFlow/";
        String typeNo = "";

        switch (toModular){
            case Constants.WORK_FLOW_TYPE_NO_DATAIMPORT:
                html += "dataImport";
                typeNo = Constants.WORK_FLOW_TYPE_NO_DATAIMPORT;
                break;
            case Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT:
                html += "semanticAnalysisObject";
                typeNo = Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT;
                break;
            case Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION:
                html += "wordSegmentation";
                typeNo = Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION;
                break;
            case Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING:
                html += "themeAnalysisSetting";
                typeNo = Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING;
                break;
            case Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION:
                html += "topicAnalysisDefinition";
                typeNo = Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION;
                break;
            case Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT:
                html += "dataOutput";
                typeNo = Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT;
                break;
            case Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT:
                html += "fileOutput";
                typeNo = Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT;
                break;
            case Constants.WORK_FLOW_TYPE_NO_DATACRAWL:
                html += "dataCrawl";
                typeNo = Constants.WORK_FLOW_TYPE_NO_DATACRAWL;
                break;
            case Constants.WORK_FLOW_TYPE_NO_STATISTICAL:
                html += "statisticalAnalysis";
                typeNo = Constants.WORK_FLOW_TYPE_NO_STATISTICAL;
                break;
        }

        req.setAttribute("typeNo",typeNo);

        return new ModelAndView(html);
    }

    @RequestMapping(value = "/acceptCallback.json", method = RequestMethod.POST)
    @ResponseBody
    public CommonPO acceptCallback(@RequestParam(value = "detailId")@ApiParam(value = "detailId",required = true) String detailId,
                                   @RequestParam(value = "projectId")@ApiParam(value = "projectId",required = true) String projectId,
                                   @RequestParam(value = "flowId")@ApiParam(value = "flowId",required = true) String flowId,
                                   @RequestParam(value = "progress",required = true)@ApiParam(value = "progress",required = true) String progress,
                                   @RequestParam(value = "status",required = true)@ApiParam(value = "progress",required = true) String status,
                                   @RequestParam(value = "errorMessage",required = true)@ApiParam(value = "progress",required = true) String errorMessage,
                                   @RequestParam(value = "num",required = false)@ApiParam(value = "num",required = false) String num,
                                   @RequestParam(value = "dataJsonArray",required = false)@ApiParam(value = "dataJsonArray",required = false)String dataJsonArray,
                                   @RequestParam(value = "workFlowId") @ApiParam("工作流id") String workFlowId,
                                   @RequestParam(value = "travelParams",required = false)@ApiParam(value = "travelParams",required = false)String travelParams
                                   ){

        if(Validate.isEmpty(detailId) || !detailId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(projectId) || !projectId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(flowId) || !flowId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(status) || !status.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(progress) || !progress.matches("\\d+")){
            progress = "0";
        }
        String batchNo=null;
        String preRun=null;
        JSONObject jsonObject0=JSONObject.fromObject(travelParams);
        if(jsonObject0.size()>0){
//            batchNo=jsonObject.getString("preRun");
            if(jsonObject0.containsKey("preRun")){
                preRun=jsonObject0.getString("preRun");
            }
           if(jsonObject0.containsKey("batchNo")){
               batchNo=jsonObject0.getString("batchNo");
           }
        }else {
            preRun=null;

        }
        CommonPO commonPO = new CommonPO();
        commonPO.setCode(0);

        Long projectIdInt = Long.parseLong(projectId);
        long detailInt = Long.parseLong(detailId);
        Long workFlowIdInt = Long.parseLong(workFlowId);


        LoggerUtil.debugTrace(this.getClass().getSimpleName(),detailId+"====progress="+progress+"===========status="+status+"====errorMessage="+errorMessage+"===dataJsonArray"+dataJsonArray+"======workFlowId"+workFlowId+"preRun:="+preRun);
        //传递数据
        if(Validate.isEmpty(dataJsonArray)){
            dataJsonArray = "[]";
        }
        String detailListStr;

        if(workFlowIdInt!=0){//如果是工作流项目，则应从缓存中取出startWorkFlowDetailList_suffix
            detailListStr = redisClient.get(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId);
        }else{
            detailListStr = redisClient.get(RedisKey.startProjectDetailList_suffix.name()+projectIdInt);
        }
        List<WorkFlowDetail> workFlowDetailListTem = null;
        if(!Validate.isEmpty(detailListStr) && !"null".equals(detailListStr)){
            workFlowDetailListTem = JSON.parseArray(detailListStr,WorkFlowDetail.class);
        }else {
            //把两个list添加进redis缓存
            //这里需要判断是否存在workFlowId，如果有的话则表示该项目是可视化项目，只需通过workFlowId查询节点信息即可
            if(workFlowIdInt!=0){
                workFlowDetailListTem = workFlowService.getWorkFlowDetailByWorkFlowId(workFlowIdInt);
                String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                redisClient.set(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowIdInt,workFlowDetailListStr);
                redisClient.expire(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowIdInt,7200);
            }else{
                workFlowDetailListTem = workFlowService.getWorkFlowDetailByProjectId(projectIdInt);
                String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                redisClient.set(RedisKey.startProjectDetailList_suffix.name()+projectIdInt,workFlowDetailListStr);
                redisClient.expire(RedisKey.startProjectDetailList_suffix.name()+projectIdInt,7200);
            }
        }
        Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
        for (WorkFlowDetail workFlowDetail:workFlowDetailListTem) {//聚合workFlowDetailListTem key为节点id，val为节点信息
            workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
        }


        //如果是工作流项目，则应从缓存中取出startWorkFlowDetailList_suffix
        List<WorkFlowParam> workFlowParamList = null;
        String paramListStr;
        if(workFlowIdInt!=0){
            paramListStr = redisClient.get(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId);
        }else{
                workFlowParamList = workFlowService.getWorkFlowParamByProJectId(projectIdInt);
                String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                redisClient.set(RedisKey.startProjectParamList_suffix.name()+projectIdInt,workFlowParamListStr);
                redisClient.expire(RedisKey.startProjectParamList_suffix.name()+projectIdInt,4*3600);

            paramListStr = redisClient.get(RedisKey.startProjectParamList_suffix.name()+projectIdInt);
        }



        if(!Validate.isEmpty(paramListStr) && !"null".equals(paramListStr)){
            workFlowParamList = JSON.parseArray(paramListStr,WorkFlowParam.class);
        }else {

            ProjectOne projectOne = projectService.getProjectInf(projectIdInt);

            //判断项目是否是可视化项目
            if(workFlowIdInt!=0){
                workFlowParamList = new ArrayList<>();
                List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowService.getWorkFlowInputParamBoList();//获取所有的输入参数
                Map<Integer,String> inputParamMap = new HashMap<>();
                for (WorkFlowInputParamBo workFlowInputParamBo:workFlowInputParamBoList) {//将输入参数list聚合成map  key为输入参数id，value为输入参数英文名
                    inputParamMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo.getParamEnName());
                }
                //如果是可视化项目就直接查询工作流workFlowId的节点参数
                List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowService.getWorkFlowNodeParamByWorkFlowId(workFlowIdInt);

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

//                    workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);

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
                    }else if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(workFlowDetail.getTypeNo())){
                        jsonParamObject = jsonObject;
                    }else if(Constants.WORK_FLOW_TYPE_NO_DATAFILTER.equals(workFlowDetail.getTypeNo())){
                        jsonParamObject = jsonObject;
                    }else if(Constants.WORK_FLOW_TYPE_NO_CONDITION.equals(workFlowDetail.getTypeNo())){
                        jsonParamObject = jsonObject;
                    }else if(Constants.WORK_FLOW_TYPE_NO_PUShOSS.equals(workFlowDetail.getTypeNo())){
                        jsonParamObject = jsonObject;
                    }
                    workFlowParam.setFlowId(workFlowDetail.getFlowId());
                    workFlowParam.setParamType(WorkFlowParam.PARAM_TYPE_PRIVATE);
                    workFlowParam.setFlowDetailId(workFlowDetail.getFlowDetailId());
                    workFlowParam.setTypeNo(workFlowDetail.getTypeNo());
                    workFlowParam.setProjectId(workFlowDetail.getProjectId());
                    workFlowParam.setJsonParam(jsonParamObject.toString());
                    workFlowParam.setWorkFlowId(workFlowIdInt);
                    workFlowParamList.add(workFlowParam);
                }
                String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                redisClient.set(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId,workFlowParamListStr);
                redisClient.expire(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId,4*3600);
            }else if (ProjectOne.PROJECTTYPE_PAGE.equals(projectOne.getProjectType())){
                workFlowParamList = workFlowService.getWorkFlowParamByProJectId(projectIdInt);
                String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                redisClient.set(RedisKey.startProjectParamList_suffix.name()+projectIdInt,workFlowParamListStr);
                redisClient.expire(RedisKey.startProjectParamList_suffix.name()+projectIdInt,4*3600);
            }
        }
        Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
        for (WorkFlowParam workFlowParam:workFlowParamList) {
            workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
        }

        JSONArray dataJsonArrayObj = JSONArray.fromObject(dataJsonArray);

        WorkFlowDetail detial = workFlowDetailMap.get(detailInt);
        if(null != detial){

            Map<String,JobTypeInfo> jobTypeInfoMap = new HashMap();
            List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();
            if(null != jobTypeInfoList && jobTypeInfoList.size() > 0){
                //聚合jobType 方便 下面根据typeNo 取出jobType对象
                for (JobTypeInfo jobTypeInfo :jobTypeInfoList) {
                    jobTypeInfoMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
                }
            }

            //找到第一个抓取节点
            long firstDetailId = detial.getFlowDetailId();

            String detialPrevFlowDetailIds = detial.getPrevFlowDetailIds();
            while (null != detialPrevFlowDetailIds && !"0".equals(detialPrevFlowDetailIds)){
                String[] idArray = detialPrevFlowDetailIds.split(",");
                if(!Validate.isEmpty(idArray)){
                    Long id = Long.parseLong(idArray[0]);
                    WorkFlowDetail flowDetail = workFlowDetailMap.get(id);
                    detialPrevFlowDetailIds = flowDetail.getPrevFlowDetailIds();
                    if("0".equals(detialPrevFlowDetailIds)){
                        firstDetailId = flowDetail.getFlowDetailId();
                        break;
                    }
                }
            }

//            WorkFlowDetail workFlowDetail = new WorkFlowDetail();
//            workFlowDetail.setFlowDetailId(detailInt);
//            if(!status.equals("2")){
//                workFlowDetail.setJobStatus(Integer.parseInt(status));
//            }
//            workFlowDetail.setErrorMsg(errorMessage);
//            workFlowDetail.setJobProgress(progressInt);
//
//
//            if(status.equals("2")){//是完成状态
//
//                WorkFlowDetail firstDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(firstDetailId);
//
//                if(firstDetail.getFlowDetailId() == detailInt){
//                    firstDetail.setJobStatus(2);
//                }
//
//                status = Integer.toString(firstDetail.getJobStatus());
//
//                if(firstDetail.getJobStatus() == 2){
//
//                    workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());
//
//                    //判断整个项目是否完成
//                    List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByTypeNoList(null,projectIdInt);
//
//                    int finishNum = 0;
//                    for (WorkFlowDetail detail:workFlowDetailList) {
//                        int s = detail.getJobStatus();
//                        if(s == 2){
//                            finishNum ++ ;
//                        }else {
//                            break;
//                        }
//                    }
//
//                    if (finishNum >= workFlowDetailList.size()){
//                        ProjectStatusFilter filter = new ProjectStatusFilter();
//                        filter.setId(projectIdInt);
//                        filter.setStatus(5);
//                        projectService.updateProjectStatus(filter);
//                    }
//
//                }
//
//            }
//
//
            ProjectOne projectOne = projectService.getProjectInf(projectIdInt);

            //更新抓取num
//            if(!Validate.isEmpty(num)){
//                if(Validate.isEmpty(num) || !num.matches("\\d+")){
//                    throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
//                }
//
//                //如果是页面配置的项目
//                if(ProjectOne.PROJECTTYPE_PAGE.equals(projectOne.getProjectType())){
//                    if(firstDetailId == detial.getFlowDetailId()){//只更新第一条
//                        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(firstDetailId);
//
//                        if(null != workFlowParam){
//
//                            String typeNo = workFlowParam.getTypeNo();
//                            if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
//                                String jsonParam = workFlowParam.getJsonParam();
//                                DataCrawlPO dataCrawlPO = JSON.parseObject(jsonParam,DataCrawlPO.class);
//
//                                dataCrawlPO.setCount(dataCrawlPO.getCount()+Integer.parseInt(num));
//
//                                jsonParam = JSON.toJSONString(dataCrawlPO);
//                                workFlowParam.setJsonParam(jsonParam);
//                                workFlowService.updateWorkFlowParam(workFlowParam);
//                            }
//
//                        }
//                    }
//                }
//            }

            String nextFlowDetailIds = detial.getNextFlowDetailIds();
            LoggerUtil.debugTrace("====nextFlowDetailIds"+nextFlowDetailIds);
            if(!Validate.isEmpty(nextFlowDetailIds)){
                String[] nextDetailArray =nextFlowDetailIds.split(",");
                for (String detialStr:nextDetailArray) {
                    long detailIdLong = Long.parseLong(detialStr);
                    WorkFlowParam workFlowParam = workFlowParamMap.get(detailIdLong);
                    if(workFlowIdInt == 0){
                        workFlowParam.setWorkFlowId(workFlowIdInt);
                    }

                    JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(workFlowParam.getTypeNo());

                    //如果下个节点是流程节点
                    LoggerUtil.debugTrace("====jobTypeInfo.getJobClassify():"+jobTypeInfo.getJobClassify()+"     jobTypeInfo.getTypeNo()"+jobTypeInfo.getTypeNo());
                    if(jobTypeInfo.getJobClassify() == 1){
                        if(Validate.isEmpty(dataJsonArrayObj)){
                            return commonPO;
                        }
                        if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(jobTypeInfo.getTypeNo())){
                            WorkFlowDetail nextDetail = workFlowDetailMap.get(detailIdLong);
                            LoggerUtil.debugTrace("nextDetail.getFlowId:="+nextDetail.getFlowId());
                            if(null != nextDetail.getFlowId() && nextDetail.getFlowId() > 0){
                                DataCrawlPO dataCrawlPO = new DataCrawlPO();
                                String jsonParam = workFlowParam.getJsonParam();
                                JSONObject jsonObject = JSONObject.fromObject(jsonParam);
                                LoggerUtil.debugTrace("projectOne.getProjectType="+projectOne.getProjectType());
                                if(ProjectOne.PROJECTTYPE_VIS.equals(projectOne.getProjectType())){
                                    jsonObject = jsonObject.getJSONObject("jsonParam");
                                }

                                dataCrawlPO.setTaskName("流程抓取"+jsonObject.optString("datasourceTypeName",""));
                                DataCrawlJsonParamPO dataCrawlJsonParamPO = new DataCrawlJsonParamPO();
                                dataCrawlJsonParamPO.setDatasourceTypeId(jsonObject.optString("datasourceTypeId","0"));
                                dataCrawlJsonParamPO.setDatasourceId(jsonObject.optString("datasourceId","0"));
                                dataCrawlJsonParamPO.setDatasourceName(jsonObject.optString("datasourceName",""));
                                dataCrawlJsonParamPO.setDatasourceTypeName(jsonObject.optString("datasourceTypeName",""));
                                dataCrawlJsonParamPO.setTaskName("流程抓取"+jsonObject.optString("datasourceTypeName",""));
                                dataCrawlJsonParamPO.setCrawlWay(jsonObject.optString("crawlWay",""));
                                String crawlServer = WebUtil.getBaseServerByEnv();
                                Object resultObj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServer+"/common/getCrawlInputParamsByDatasourceType.json?datasourceTypeId="+dataCrawlJsonParamPO.getDatasourceTypeId(),"get",null);
                                Map<Long,JSONObject> inputParamMap = new HashMap<>();
                                if(null != resultObj){
                                    JSONArray jsonArray = (JSONArray) resultObj;
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JSONObject inputParam = jsonArray.getJSONObject(i);

                                        inputParamMap.put(inputParam.getLong("id"),inputParam);
                                    }

                                }

                                WorkFlowParam preWorkFlowParam = workFlowParamMap.get(detial.getFlowDetailId());//查询上一个节点的数据源类型id
                                String preDatasourceTypeId = null;
                                JSONObject preJsonObject = JSONObject.fromObject(preWorkFlowParam.getJsonParam());
                                LoggerUtil.debugTrace("preJsonObject="+preJsonObject);
                                Object jsonParamObj = preJsonObject.get("jsonParam");
                                if(null != jsonParamObj){
                                    preDatasourceTypeId = JSONObject.fromObject(jsonParamObj).optString("datasourceTypeId");
                                }else {
                                    preDatasourceTypeId = preJsonObject.optString("datasourceTypeId");
                                }

                                List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(preDatasourceTypeId);
                                Map<Integer,String> fieldNameMap = new HashMap<>();
                                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                                    fieldNameMap.put(storageTypeFieldPO.getId(),storageTypeFieldPO.getFieldEnName());
                                }
                                JSONArray mappingJsonArray = jsonObject.getJSONArray("mappingJson");

                                JSONArray inputParamArray = new JSONArray();
                                LoggerUtil.debugTrace("mappingJsonArray="+mappingJsonArray.size());
                                for (int i = 0; i <mappingJsonArray.size() ; i++) {
                                    JSONObject mappingJSONObject = mappingJsonArray.getJSONObject(i);

                                    Long inputParamId = mappingJSONObject.optLong("inputParamId");

                                    JSONObject inputParam = inputParamMap.get(inputParamId);

                                    String fieldEnName = fieldNameMap.get(mappingJSONObject.optInt("fieldId"));
                                    LoggerUtil.debugTrace("inputParam="+inputParam.toString()+"       fieldEnName= "+fieldEnName+"      inputParamId"+inputParamId);
                                    if(null != inputParam && !Validate.isEmpty(fieldEnName)){
                                        StringBuffer paramValueBuff = new StringBuffer("");
                                        String paramValue = "";
                                        for (int j = 0; j < dataJsonArrayObj.size(); j++) {
                                            JSONObject dataJsonObject = dataJsonArrayObj.getJSONObject(j);

                                            String fieldValue = dataJsonObject.optString(fieldEnName);
                                            if(!Validate.isEmpty(fieldValue)){
                                                paramValueBuff.append(dataJsonObject.optString(fieldEnName)+",");
                                            }
                                        }
                                        LoggerUtil.debugTrace("paramValueBuff="+paramValueBuff.length());
                                        if(paramValueBuff.length() > 0){
                                            paramValue = paramValueBuff.substring(0,paramValueBuff.length()-1);
                                        }else {
                                            //如果没有参数直接返回
                                            return commonPO;
                                        }

                                        inputParam.put("paramValue",paramValue);

                                        inputParamArray.add(inputParam);
                                    }

                                }

                                dataCrawlJsonParamPO.setInputParamArray(inputParamArray);
                                dataCrawlPO.setJsonParam(dataCrawlJsonParamPO);

                                workFlowParam.setJsonParam(JSON.toJSONString(dataCrawlPO));

                            }
                            LoggerUtil.debugTrace("====workFlowParam:"+workFlowParam+"    batchNo"+batchNo+"      jobTypeInfo.getTypeNo"+jobTypeInfo.getTypeNo()+"preRun:="+preRun);
                            workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(),jobTypeInfo,detial,workFlowParam,firstDetailId,batchNo,preRun);

                        }else if (Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(jobTypeInfo.getTypeNo())){
                            WorkFlowDetail nextDetail = workFlowDetailMap.get(detailIdLong);
                            LoggerUtil.debugTrace("===nextDetail:="+nextDetail);
                            if(null != nextDetail){
                                String jsonParam = workFlowParam.getJsonParam();
                                JSONObject jsonObject = JSONObject.fromObject(jsonParam);

                                if(ProjectOne.PROJECTTYPE_VIS.equals(projectOne.getProjectType())){
                                    jsonObject = jsonObject.getJSONObject("jsonParam");
                                }

                                DataCrawlPO dataCrawlPO = new DataCrawlPO();
                                dataCrawlPO.setTaskName("流程抓取"+jsonObject.optString("datasourceTypeName",""));
                                DataCrawlJsonParamPO dataCrawlJsonParamPO = new DataCrawlJsonParamPO();
                                dataCrawlJsonParamPO.setDatasourceTypeId(jsonObject.optString("datasourceTypeId","0"));
                                dataCrawlJsonParamPO.setDatasourceId(jsonObject.optString("datasourceId","0"));
                                dataCrawlJsonParamPO.setDatasourceName(jsonObject.optString("datasourceName",""));
                                dataCrawlJsonParamPO.setDatasourceTypeName(jsonObject.optString("datasourceTypeName",""));
                                dataCrawlJsonParamPO.setTaskName("流程抓取"+jsonObject.optString("datasourceTypeName",""));

                                /*String crawlServer = WebUtil.getBaseServerByEnv();
                                Object resultObj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServer+"/common/getCrawlInputParamsByDatasourceType.json?datasourceTypeId="+dataCrawlJsonParamPO.getDatasourceTypeId(),"get",null);
                                Map<Long,JSONObject> inputParamMap = new HashMap<>();
                                if(null != resultObj){
                                    JSONArray jsonArray = (JSONArray) resultObj;
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JSONObject inputParam = jsonArray.getJSONObject(i);

                                        inputParamMap.put(inputParam.getLong("id"),inputParam);
                                    }

                                }*/

                                WorkFlowParam preWorkFlowParam = workFlowParamMap.get(detial.getFlowDetailId());//查询上一个节点的数据源类型id
                                String preDatasourceTypeId = null;
                                JSONObject preJsonObject = JSONObject.fromObject(preWorkFlowParam.getJsonParam());
                                Object jsonParamObj = preJsonObject.get("jsonParam");
                                LoggerUtil.debugTrace("===jsonParamObj"+jsonParamObj);
                                if(null != jsonParamObj){
                                    preDatasourceTypeId = JSONObject.fromObject(jsonParamObj).optString("datasourceTypeId");
                                }else {
                                    preDatasourceTypeId = preJsonObject.optString("datasourceTypeId");
                                }

                                List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(preDatasourceTypeId);
                                Map<Integer,String> fieldNameMap = new HashMap<>();
                                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                                    fieldNameMap.put(storageTypeFieldPO.getId(),storageTypeFieldPO.getFieldEnName());
                                }

                                JSONArray mappingJsonArray = jsonObject.getJSONArray("mappingJson");

                                JSONArray inputParamArray = new JSONArray();
                                for (int j = 0; j < dataJsonArrayObj.size(); j++) {

                                    JSONArray inputArray = new JSONArray();
                                    for (int i = 0; i <mappingJsonArray.size() ; i++) {
                                        JSONObject mappingJSONObject = mappingJsonArray.getJSONObject(i);

                                        Long inputParamId = mappingJSONObject.optLong("inputParamId");

                                        JSONObject inputParam = new JSONObject();

                                        String fieldEnName = fieldNameMap.get(mappingJSONObject.optInt("fieldId"));
                                        LoggerUtil.debugTrace("====fieldEnName+"+fieldEnName);
                                        if(null != inputParam && !Validate.isEmpty(fieldEnName)) {

                                            JSONObject dataJsonObject = dataJsonArrayObj.getJSONObject(j);

                                            String filedValue = dataJsonObject.optString(fieldEnName);

                                            if(Validate.isEmpty(filedValue)){
                                                filedValue = "";
                                            }

                                            inputParam.put("paramValue",filedValue);
                                            inputParam.put("id",inputParamId);

                                            inputArray.add(inputParam);
                                        }
                                    }

                                    if(!Validate.isEmpty(inputArray)){
                                        inputParamArray.add(inputArray);
                                    }
                                }

                                if(!Validate.isEmpty(inputParamArray)){
                                    dataCrawlPO.setInputParams(JSON.toJSONString(inputParamArray));
                                }else {
                                    //没有 参数 直接返回
                                    return commonPO;
                                }

                                dataCrawlJsonParamPO.setInputParamArray(inputParamArray);
                                dataCrawlPO.setJsonParam(dataCrawlJsonParamPO);

                                workFlowParam.setJsonParam(JSON.toJSONString(dataCrawlPO));

                            }
                            LoggerUtil.debugTrace("====workFlowParam:"+workFlowParam+"    batchNo"+batchNo+"preRun"+preRun);
                            workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(),jobTypeInfo,detial,workFlowParam,firstDetailId,batchNo,preRun);

                        }else{
                            JSONArray nextJsonArray = new JSONArray();
                            //获取传回来detail的数据
                            String dataSourceType = detial.getDataSourceType();

                            if(!Validate.isEmpty(dataSourceType) && !Validate.isEmpty(dataJsonArrayObj)){

                                JSONArray nextIds = new JSONArray();
                                for (Object object:dataJsonArrayObj) {
                                    JSONObject jsonObject = (JSONObject) object;
                                    nextIds.add(jsonObject);//改成数据。
                                }

                                StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(dataSourceType));
                                String storageTypeTable = storageTypePO.getStorageTypeTable();

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(Constants.DATA_TYPE_KEY,storageTypeTable);
                                jsonObject.put(Constants.DATA_IDS_KEY,nextIds);

                                nextJsonArray.add(jsonObject);
                            }

                            String dpmssServerByEnv = WebUtil.getDpmssServerByEnv();
                            Map<String,String> postData = new HashMap<>();
                            postData.put("name",workFlowParam.getFlowDetailId()+"#exec#"+workFlowParam.getTypeNo());

                            postData.put("param",nextJsonArray.toString());
                            postData.put("errorMessage","");
                            postData.put("flowDetailId",Long.toString(workFlowParam.getFlowDetailId()));
                            postData.put("projectId",Long.toString(workFlowParam.getProjectId()));
                            postData.put("flowId",Long.toString(workFlowParam.getFlowId()));
                            postData.put("workFlowId",Long.toString(workFlowParam.getWorkFlowId()));
                            postData.put("jobStatus",status);
                            postData.put("typeNo",workFlowParam.getTypeNo());
                            postData.put("resultJsonParam","");
                            postData.put("jobProgress",progress);

                            JSONObject jsonParamObject = new JSONObject();
                            jsonParamObject.put("jsonParam",JSONObject.fromObject(workFlowParam.getJsonParam()));
                            jsonParamObject.put("isEnd",status.equals("2"));

                            postData.put("jsonParam",jsonParamObject.toString());
                            postData.put("paramType",Integer.toString(workFlowParam.getParamType()));
                            postData.put("preRun",preRun);
                            CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),dpmssServerByEnv+"/submitTask.json","post",postData);
                        }
                    }
                }
            }
        }

        return commonPO;

    }

    @RequestMapping(value = "/getNextDetailListByDetailId.json", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String,Object>> getNextDetailListByDetailId(@RequestParam(value = "projectId")@ApiParam(value = "projectId",required = true) String projectId,
                                                                @RequestParam(value = "detailId")@ApiParam(value = "detailId",required = true) String detailId,
                                                                @RequestParam(value = "workFlowId")@ApiParam(value = "workFlowId",required = true) String workFlowId){
        if(Validate.isEmpty(detailId) || !detailId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        if(Validate.isEmpty(projectId) || !projectId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);
        long workFlowIdInt = Long.parseLong(workFlowId);

        List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();

        Map<String,JobTypeInfo> jobTypeInfoMap = new HashMap<>();
        for (JobTypeInfo jobTypeInfo:jobTypeInfoList) {
            jobTypeInfoMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
        }

        long detailInt = Long.parseLong(detailId);
        String detailListStr;
        if(workFlowIdInt == 0){
            detailListStr = redisClient.get(RedisKey.startProjectDetailList_suffix.name()+projectIdInt);
        }else{
            detailListStr = redisClient.get(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowIdInt);
        }

        List<WorkFlowDetail> workFlowDetailListTem;
        if(!Validate.isEmpty(detailListStr) && !"null".equals(detailListStr)){
            workFlowDetailListTem = JSON.parseArray(detailListStr,WorkFlowDetail.class);
        }else {
            if(workFlowIdInt == 0){
                workFlowDetailListTem = workFlowService.getWorkFlowDetailByProjectId(projectIdInt);
                String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                redisClient.set(RedisKey.startProjectDetailList_suffix.name()+projectIdInt,workFlowDetailListStr);
                redisClient.expire(RedisKey.startProjectDetailList_suffix.name()+projectIdInt,4*3600);
            }else{
                workFlowDetailListTem = workFlowService.getWorkFlowDetailByWorkFlowId(workFlowIdInt);
                String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                redisClient.set(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowIdInt,workFlowDetailListStr);
                redisClient.expire(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowIdInt,4*3600);
            }
        }
        Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
        for (WorkFlowDetail workFlowDetail:workFlowDetailListTem) {
            workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
        }

        /*String paramListStr = redisClient.get(RedisKey.startProjectParamList_suffix.name()+projectIdInt);
        List<WorkFlowParam> workFlowParamList = null;
        if(!Validate.isEmpty(paramListStr) && !"null".equals(paramListStr)){
            workFlowParamList = JSON.parseArray(paramListStr,WorkFlowParam.class);
        }else {

            workFlowParamList = workFlowService.getWorkFlowParamByProJectId(projectIdInt);
            String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
            redisClient.set(RedisKey.startProjectParamList_suffix.name()+projectIdInt,workFlowParamListStr);
        }
        Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
        for (WorkFlowParam workFlowParam:workFlowParamList) {
            workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
        }*/
        String paramListStr;
        if(workFlowIdInt == 0){
            paramListStr = redisClient.get(RedisKey.startProjectParamList_suffix.name()+projectIdInt);
        }else{
            paramListStr = redisClient.get(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId);
        }
        List<WorkFlowParam> workFlowParamList = new ArrayList<>();
        if(!Validate.isEmpty(paramListStr) && !"null".equals(paramListStr)){
            workFlowParamList = JSON.parseArray(paramListStr,WorkFlowParam.class);
        }else {
            //判断项目是否是可视化项目
            if(workFlowIdInt != 0){

                List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowService.getWorkFlowInputParamBoList();
                Map<Integer,String> inputParamMap = new HashMap<>();
                for (WorkFlowInputParamBo workFlowInputParamBo:workFlowInputParamBoList) {
                    inputParamMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo.getParamEnName());
                }

                List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowService.getWorkFlowNodeParamByWorkFlowId(workFlowIdInt);

                Map<Long, net.sf.json.JSONObject> detailJsonMap = new HashMap<>();
                for (WorkFlowNodeParamBo workFlowNodeParamBo:workFlowNodeParamBoList) {
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

                for (WorkFlowDetail workFlowDetail:workFlowDetailListTem) {

                    workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);

                    net.sf.json.JSONObject jsonParamObject = new net.sf.json.JSONObject();

                    net.sf.json.JSONObject jsonObject = detailJsonMap.get(workFlowDetail.getFlowDetailId());
                    if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(workFlowDetail.getTypeNo())
                            ||Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(workFlowDetail.getTypeNo())){

                        String crawlType = jsonObject.getString("crawlType");

                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                            crawlType = "1";
                        }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                            crawlType = "2";
                        }

                        Object datasourceIdObj = jsonObject.get("datasourceId");
                        Object datasouceTypeIdObj = jsonObject.get("datasourceTypeId");

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
                    }else if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(workFlowDetail.getTypeNo())){
                        jsonParamObject = jsonObject;
                    }else if(Constants.WORK_FLOW_TYPE_NO_DATAFILTER.equals(workFlowDetail.getTypeNo())){
                        jsonParamObject = jsonObject;
                    }else if(Constants.WORK_FLOW_TYPE_NO_PUShOSS.equals(workFlowDetail.getTypeNo())){
                        jsonParamObject = jsonObject;
                    }else if(Constants.WORK_FLOW_TYPE_NO_CONDITION.equals(workFlowDetail.getTypeNo())){
                        jsonParamObject = jsonObject;
                    }

                    WorkFlowParam workFlowParam = new WorkFlowParam();
                    workFlowParam.setFlowId(workFlowDetail.getFlowId());
                    workFlowParam.setParamType(WorkFlowParam.PARAM_TYPE_PRIVATE);
                    workFlowParam.setFlowDetailId(workFlowDetail.getFlowDetailId());
                    workFlowParam.setTypeNo(workFlowDetail.getTypeNo());
                    workFlowParam.setProjectId(workFlowDetail.getProjectId());
                    workFlowParam.setJsonParam(jsonParamObject.toString());
                    workFlowParam.setWorkFlowId(workFlowIdInt);

                    workFlowParamList.add(workFlowParam);
                }
                String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                redisClient.set(RedisKey.startWorkFlowParamList_suffix.name()+workFlowIdInt,workFlowParamListStr);
                redisClient.expire(RedisKey.startWorkFlowParamList_suffix.name()+workFlowIdInt,4*3600);
            }else{
                workFlowParamList = workFlowService.getWorkFlowParamByProJectId(projectIdInt);
                String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                redisClient.set(RedisKey.startProjectParamList_suffix.name()+projectIdInt,workFlowParamListStr);
                redisClient.expire(RedisKey.startProjectParamList_suffix.name()+projectIdInt,4*3600);
            }
        }
        Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
        for (WorkFlowParam workFlowParam:workFlowParamList) {
            workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
        }

        List<Map<String,Object>> mapList = new ArrayList<>();

        WorkFlowDetail workFlowDetail = workFlowDetailMap.get(detailInt);
        if(null != workFlowDetail) {
            String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
            if (!Validate.isEmpty(nextFlowDetailIds)) {
                String[] nextDetailArray = nextFlowDetailIds.split(",");
                for (String detialStr : nextDetailArray) {
                    long detailIdLong = Long.parseLong(detialStr);

                    Map<String,Object> paramMap = new HashMap<>();

                    WorkFlowParam workFlowParam = workFlowParamMap.get(detailIdLong);

                    JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(workFlowParam.getTypeNo());

                    if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(jobTypeInfo.getTypeNo())){
                        //判断如果是话题 需要判断是否是 流程节点
                        HotspotsPO hotspotsPO = JSON.parseObject(workFlowParam.getJsonParam(),HotspotsPO.class);

                        //如果话题list不为空 则表示 是流程节点
                        if(!Validate.isEmpty(hotspotsPO.getTopicList())){
                            jobTypeInfo.setJobClassify(JobTypeInfo.JOB_CLASSIFY_PROCESS);
                        }

                    }

                    paramMap.put("workFlowParam",workFlowParam);

                    paramMap.put("jobTypeInfo",jobTypeInfo);

                    mapList.add(paramMap);

                }
            }
        }

        return mapList;

    }

    @RequestMapping(value = "/completeDetailByDetailId.json", method = RequestMethod.POST)
    @ResponseBody
    public void completeDetailByDetailId(@RequestParam(value = "detailId")@ApiParam(value = "detailId",required = true) String detailId,
                                         @RequestParam(value = "resultJson",required = false)@ApiParam(value = "resultJson",required = false) String resultJson,
                                         @RequestParam(value = "preRun",required = false)@ApiParam(value = "preRun",required = false) String preRun){
        if(Validate.isEmpty(detailId) || !detailId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long detailIdInt = Long.parseLong(detailId);

        int i = workFlowService.updateWorkFlowDetailToFinish(detailIdInt);

        if(i > 0){

            WorkFlowDetail detail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailIdInt);

            if(null != detail){

                //取出参数 更新返回的参数
                if(!Validate.isEmpty(resultJson)){

                    /*String paramListStr = redisClient.get(RedisKey.startProjectParamList_suffix.name()+detail.getProjectId());
                    List<WorkFlowParam> workFlowParamList = null;
                    if(!Validate.isEmpty(paramListStr) && !"null".equals(paramListStr)){
                        workFlowParamList = JSON.parseArray(paramListStr,WorkFlowParam.class);
                    }else {

                        workFlowParamList = workFlowService.getWorkFlowParamByProJectId(detail.getProjectId());
                        String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                        redisClient.set(RedisKey.startProjectParamList_suffix.name()+detail.getProjectId(),workFlowParamListStr);
                    }
                    Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
                    for (WorkFlowParam workFlowParam:workFlowParamList) {
                        workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
                    }*/


                    ProjectOne projectOne = projectService.getProjectInf(detail.getProjectId());
                    if(ProjectOne.PROJECTTYPE_PAGE.equals(projectOne.getProjectType())){
                        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(detailIdInt);
                        if(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT.equals(workFlowParam.getTypeNo())){
                            ImportData importData = JSON.parseObject(workFlowParam.getJsonParam(), ImportData.class);

                            JSONObject resultJsonObject = JSONObject.fromObject(resultJson);
                            long count = resultJsonObject.optLong("count");
                            importData.setCount(String.valueOf(count));

                            workFlowParam.setJsonParam(com.alibaba.fastjson.JSONObject.toJSONString(importData));
                            workFlowService.updateWorkFlowParam(workFlowParam);
                        }
                    }

                }

                updateProjectIfFinshAndExecuteNextNode(detail.getProjectId(),detail.getFlowDetailId(),detail.getWorkFlowId(),preRun);
            }
        }
    }


    public void updateProjectIfFinshAndExecuteNextNode(Long projectId,Long detailId,Long workFlowId,String preRun){
        List<WorkFlowDetail> workFlowDetailList;
        if(workFlowId == 0){
            workFlowDetailList = workFlowService.getWorkFlowDetailByProjectId(projectId);
        }else{
             workFlowDetailList = workFlowService.getWorkFlowDetailByWorkFlowId(workFlowId);
        }
        //判断整个工作流是否完成
        int finishNum = 0;
        if(preRun==null){
            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                int status = workFlowDetail.getJobStatus();
                if(status == 2){
                    finishNum ++ ;
                }else {
                    break;
                }
            }
        }else{
            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                int status = workFlowDetail.getJobStatus();
                if(status == 6 ||status==5){
                    finishNum ++ ;
                }else {
                    break;
                }
            }
        }


        if (finishNum >= workFlowDetailList.size()){

            if(workFlowId == 0){//如果是常规项目则更新项目状态
                ProjectStatusFilter filter = new ProjectStatusFilter();
                filter.setId(projectId);
                filter.setStatus(5);
                projectService.updateProjectStatus(filter);
                //项目完成了，删除redis存入的数据
                redisClient.del(RedisKey.startProjectDetailList_suffix.name()+projectId);
                redisClient.del(RedisKey.startProjectParamList_suffix.name()+projectId);
            }else{//如果是可视化工作流 则更新工作流状态
                if(preRun==null){
                    WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                    workFlowListBO.setWorkFlowId(workFlowId);
                    workFlowListBO.setFinishTime("完成");
                    workFlowListBO.setStatus(2);
                    workFlowService.updateWorkFlowListStatus(workFlowListBO);
                    //根据项目id查询出该项目下的所有工作流
                    WorkFlowListFilter workFlowListFilter=new WorkFlowListFilter();
                    workFlowListFilter.setProjectId(projectId);
                    List<WorkFlowListBO> workFlowList=workFlowService.getWorkFlowListPOByFilter(workFlowListFilter);
                    //循环判断工作流的状态
                    Integer size=workFlowList.size();
                    Integer  count =0;
                    for (WorkFlowListBO workFlowListBO1:workFlowList){
                        if(workFlowListBO1.getStatus()==2){
                            count++;
                        }
                    }
                    if(size==count){
                        ProjectStatusFilter projectStatusFilter1=new ProjectStatusFilter();
                        projectStatusFilter1.setId(projectId);
                        projectStatusFilter1.setStatus(5);
                        projectService.updateProjectStatus(projectStatusFilter1);
                    }
                }else {
                    List<WorkFlowDetail> workFlowDetailList2=workFlowService.getWorkFlowDetailListByWorkFlowIdAndStatus(workFlowId);
                    //判断是否有无效节点
                    if(workFlowDetailList2.size()>0){
                        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                        workFlowListBO.setWorkFlowId(workFlowId);
                        workFlowListBO.setFinishTime("完成");
                        workFlowListBO.setStatus(5);
                        workFlowService.updateWorkFlowListStatus(workFlowListBO);
                    }else{
                        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                        workFlowListBO.setWorkFlowId(workFlowId);
                        workFlowListBO.setFinishTime("完成");
                        workFlowListBO.setStatus(3);
                        workFlowService.updateWorkFlowListStatus(workFlowListBO);
                    }
                }
                redisClient.del(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId);
                redisClient.del(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId);

            }

            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                redisClient.del(RedisKey.totalTaskNum_suffix.name()+workFlowDetail.getFlowDetailId());
                redisClient.del(RedisKey.finishNum_suffix.name()+workFlowDetail.getFlowDetailId());
                redisClient.del(RedisKey.resultNum_suffix.name()+workFlowDetail.getFlowDetailId());
            }

        }else {//项目没有完成 则 判断执行下一个节点
            //更新下缓存
            String workFlowDetailListStr = JSON.toJSONString(workFlowDetailList);
            if(workFlowId == 0){
                redisClient.set(RedisKey.startProjectDetailList_suffix.name()+projectId,workFlowDetailListStr);
                redisClient.expire(RedisKey.startProjectDetailList_suffix.name()+projectId,4*3600);
            }else{
                redisClient.set(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId,workFlowDetailListStr);
                redisClient.expire(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId,4*3600);
            }
            Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                workFlowDetailMap.put(workFlowDetail.getFlowDetailId(), workFlowDetail);
            }
            //聚合所有节点的基本信息
            List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();
            Map<String,JobTypeInfo> jobTypeInfoMap = new HashMap<>();
            for (JobTypeInfo jobTypeInfo:jobTypeInfoList) {
                jobTypeInfoMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
            }

            String paramListStr;
            if(workFlowId == 0){
                paramListStr = redisClient.get(RedisKey.startProjectParamList_suffix.name()+projectId);
            }else{
                paramListStr = redisClient.get(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId);
            }

            List<WorkFlowParam> workFlowParamList = null;
            if(!Validate.isEmpty(paramListStr) && !"null".equals(paramListStr)){
                workFlowParamList = JSON.parseArray(paramListStr,WorkFlowParam.class);
            }else {
                //判断项目是否是可视化工作流
                if(workFlowId != 0){
                    List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowService.getWorkFlowInputParamBoList();
                    Map<Integer,String> inputParamMap = new HashMap<>();
                    for (WorkFlowInputParamBo workFlowInputParamBo:workFlowInputParamBoList) {
                        inputParamMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo.getParamEnName());
                    }
                    //根据工作流取出该工作流下所有的nodeparam
                    List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowService.getWorkFlowNodeParamByWorkFlowId(workFlowId);

                    Map<Long, net.sf.json.JSONObject> detailJsonMap = new HashMap<>();
                    for (WorkFlowNodeParamBo workFlowNodeParamBo:workFlowNodeParamBoList) {
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

                    for (WorkFlowDetail workFlowDetail:workFlowDetailList) {

                        workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);

                        net.sf.json.JSONObject jsonParamObject = new net.sf.json.JSONObject();

                        net.sf.json.JSONObject jsonObject = detailJsonMap.get(workFlowDetail.getFlowDetailId());
                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(workFlowDetail.getTypeNo())
                                ||Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(workFlowDetail.getTypeNo())){

                            String crawlType = jsonObject.getString("crawlType");

                            if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                                crawlType = "1";
                            }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                                crawlType = "2";
                            }

                            Object datasourceIdObj = jsonObject.get("datasourceId");
                            Object datasouceTypeIdObj = jsonObject.get("datasourceTypeId");

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
                        }else if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(workFlowDetail.getTypeNo())){
                            jsonParamObject = jsonObject;
                        }else if(Constants.WORK_FLOW_TYPE_NO_CONDITION.equals(workFlowDetail.getTypeNo())){
                            jsonParamObject = jsonObject;
                        }else if(Constants.WORK_FLOW_TYPE_NO_PUShOSS.equals(workFlowDetail.getTypeNo())){
                            jsonParamObject = jsonObject;
                        }else if(Constants.WORK_FLOW_TYPE_NO_DATAFILTER.equals(workFlowDetail.getTypeNo())){
                            jsonParamObject = jsonObject;
                        }

                        WorkFlowParam workFlowParam = new WorkFlowParam();
                        workFlowParam.setFlowId(workFlowDetail.getFlowId());
                        workFlowParam.setParamType(WorkFlowParam.PARAM_TYPE_PRIVATE);
                        workFlowParam.setFlowDetailId(workFlowDetail.getFlowDetailId());
                        workFlowParam.setTypeNo(workFlowDetail.getTypeNo());
                        workFlowParam.setProjectId(workFlowDetail.getProjectId());
                        workFlowParam.setJsonParam(jsonParamObject.toString());
                        workFlowParam.setWorkFlowId(workFlowId);
                        workFlowParamList.add(workFlowParam);
                    }
                    String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                    redisClient.set(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId,workFlowParamListStr);
                }else{
                    workFlowParamList = workFlowService.getWorkFlowParamByProJectId(projectId);
                    String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
                    redisClient.set(RedisKey.startProjectParamList_suffix.name()+projectId,workFlowParamListStr);
                }
            }
            Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
            for (WorkFlowParam workFlowParam:workFlowParamList) {//聚合map
                workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
            }
            WorkFlowDetail detail = workFlowDetailMap.get(detailId);
            //执行下节点
            executeNextNode(detail,jobTypeInfoMap,workFlowDetailMap,workFlowParamMap,preRun);
        }
    }


    public boolean executeNextNode(WorkFlowDetail detail,Map<String,JobTypeInfo> jobTypeInfoMap,Map<Long,WorkFlowDetail> workFlowDetailMap,Map<Long,WorkFlowParam> workFlowParamMap,String preRun){
        String nextFlowDetailIds = detail.getNextFlowDetailIds();
        if(!Validate.isEmpty(nextFlowDetailIds)){
            String [] nextDetailIdArray = nextFlowDetailIds.split(",");
            for (String nextDetailId:nextDetailIdArray) {
                long nextDetailIInt = Long.parseLong(nextDetailId);

                WorkFlowDetail workFlowDetail = workFlowDetailMap.get(nextDetailIInt);
                if(null != workFlowDetail){
                    JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(workFlowDetail.getTypeNo());
                    if(null != jobTypeInfo){
                        //判断是否是 状态节点

                        //判断是否是话题节点 如果是 则需要判断话题节点参数，是否选择了话题项目 //TODO 暂时还做不到话题分析 为动态流程节点所以暂时不考虑判断话题节点
                        if(jobTypeInfo.getJobClassify() == 2){
                            //判断自己节点的前所有节点是否都完成
                            String prevDetailIds = workFlowDetail.getPrevFlowDetailIds();
                            if(!Validate.isEmpty(prevDetailIds)){
                                String [] preDetailIdStr = prevDetailIds.split(",");
                                int detailFinishNum = 0;
                                for (String preDetailId:preDetailIdStr) {

                                    Long preDetailIdInt = Long.parseLong(preDetailId);
                                    WorkFlowDetail flowDetail = workFlowDetailMap.get(preDetailIdInt);

                                    if(flowDetail.getJobStatus() == 2){
                                        detailFinishNum ++ ;
                                    }else {
                                        break;
                                    }

                                }

                                //表示自己节点的前所有节点都已完成，可以进行下一个节点执行了
                                if(detailFinishNum >= preDetailIdStr.length){

                                    int succNum = workFlowService.updateWorkFlowDetailToStart(workFlowDetail.getFlowDetailId());

                                    //如果更新成功 才提交zk任务
                                    if(succNum > 0){
                                        WorkFlowParam workFlowParam = workFlowParamMap.get(workFlowDetail.getFlowDetailId());

                                        String dpmssServerByEnv = WebUtil.getDpmssServerByEnv();
                                        Map<String,String> postData = new HashMap<>();
                                        postData.put("name",workFlowParam.getFlowDetailId()+"#exec#"+workFlowParam.getTypeNo());

                                        postData.put("param","[]");//空数据
                                        postData.put("errorMessage","");
                                        postData.put("flowDetailId",Long.toString(workFlowParam.getFlowDetailId()));
                                        postData.put("projectId",Long.toString(workFlowParam.getProjectId()));
                                        postData.put("workFlowId",Long.toString(workFlowParam.getFlowDetailId()));
                                        postData.put("flowId",Long.toString(workFlowParam.getFlowId()));
                                        postData.put("jobStatus","1");
                                        postData.put("typeNo",workFlowParam.getTypeNo());
                                        postData.put("resultJsonParam","");
                                        postData.put("jobProgress","0");

                                        JSONObject jsonParamObject = new JSONObject();
                                        jsonParamObject.put("jsonParam",JSONObject.fromObject(workFlowParam.getJsonParam()));
                                        jsonParamObject.put("isEnd",true);

                                        postData.put("jsonParam",jsonParamObject.toString());
                                        postData.put("paramType",Integer.toString(workFlowParam.getParamType()));
                                        postData.put("preRun",preRun);
                                        CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),dpmssServerByEnv+"/submitTask.json","post",postData);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    @RequestMapping(value = "/exceptionDetailByDetailId.json", method = RequestMethod.POST)
    @ResponseBody
    public void exceptionDetailByDetailId(@RequestParam(value = "detailId")@ApiParam(value = "detailId",required = true) String detailId,
                                          @RequestParam(value = "erroMsg")@ApiParam(value = "erroMsg",required = true) String erroMsg,
                                          @RequestParam(value = "workFlowId")@ApiParam(value = "workFlowId",required = true) String workFlowId,
                                          @RequestParam(value = "projectId")@ApiParam(value = "projectId",required = true) String projectId){
        if(Validate.isEmpty(detailId) || !detailId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        LoggerUtil.debugTrace(this.getClass().getSimpleName(),"errorId"+detailId);

        long detailIdInt = Long.parseLong(detailId);
        long workFlowIdInt = Long.parseLong(workFlowId);
        long projectIdInt = Long.parseLong(projectId);
        if(Validate.isEmpty(erroMsg)){
            erroMsg = "未知错误!";
        }

        workFlowService.updateWorkFlowDetailExceptionByMap(erroMsg,detailIdInt);
        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
        workFlowListBO.setStatus(9);
        workFlowListBO.setFinishTime("完成");
        workFlowListBO.setWorkFlowId(workFlowIdInt);
        workFlowService.updateWorkFlowListStatus(workFlowListBO);
        ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
        projectStatusFilter.setId(projectIdInt);
        projectStatusFilter.setStatus(9);
        projectService.updateProjectStatus(projectStatusFilter);
    }

    @RequestMapping(value = "/updateTotalTaskNum.json", method = RequestMethod.POST)
    @ResponseBody
    public boolean updateTotalTaskNum(@RequestParam(value = "detailId")@ApiParam(value = "detailId",required = true) String detailId,
                                      @RequestParam(value = "totalNum")@ApiParam(value = "totalNum",required = true) String totalNum,
                                      @RequestParam(value = "projectId")@ApiParam(value = "projectId",required = true) String projectId,
                                      @RequestParam(value="workFlowId")@ApiParam(value="workFlowId",required = true)String workFlowId){


        //判断是否是抓取节点
        //如果是抓取节点 则 增量设置总数量，因为抓取有可能第二个节点的数量不一定和第一个节点数量对应不上。
        //如果不是抓取节点则判断是否是首节点如果是首节点则 直接设置值
        System.out.println("updateTotalTaskNum.json =====detailId:"+detailId+"====totalNum:"+totalNum+"====projectId:"+projectId+"====workFlowId:"+workFlowId);

        if(Validate.isEmpty(detailId) || !detailId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(projectId) || !projectId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(totalNum) || !totalNum.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        Long detailIdInt = Long.parseLong(detailId);
        Long projectIdInt = Long.parseLong(projectId);
        Long workFlowIdInt = Long.parseLong(workFlowId);

        String detailListStr = "";
        if(workFlowIdInt == 0){//如果为0则是常规项目，使用projectId
            detailListStr = redisClient.get(RedisKey.startProjectDetailList_suffix.name()+projectId);
        }else{
            detailListStr = redisClient.get(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId);
        }
        LoggerUtil.debugTrace("updateTotalTaskNum.json==detailListStr==="+detailListStr);
        List<WorkFlowDetail> workFlowDetailListTem = null;
//        if(!Validate.isEmpty(detailListStr) && !"null".equals(detailListStr)){
//            workFlowDetailListTem = JSON.parseArray(detailListStr,WorkFlowDetail.class);
//        }else {
            //把两个list添加进redis缓存
            if(workFlowIdInt == 0){
                workFlowDetailListTem = workFlowService.getWorkFlowDetailByProjectId(projectIdInt);
                String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                redisClient.set(RedisKey.startProjectDetailList_suffix.name()+projectIdInt,workFlowDetailListStr);
                redisClient.expire(RedisKey.startProjectDetailList_suffix.name()+projectIdInt,4*3600);
            }else{
                workFlowDetailListTem = workFlowService.getWorkFlowDetailByWorkFlowId(workFlowIdInt);
                String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                redisClient.set(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowIdInt,workFlowDetailListStr);
                redisClient.expire(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowIdInt,4*3600);
            }

        Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
        for (WorkFlowDetail workFlowDetail:workFlowDetailListTem) {//聚合成map
            workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
        }
        //取出当前节点的节点信息
        WorkFlowDetail workFlowDetail = workFlowDetailMap.get(detailIdInt);
        if(null != workFlowDetail){
            String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
            System.out.println("detailId="+detailId+"的上一个节点是="+prevFlowDetailIds);
            //是首节点
            if("0".equals(prevFlowDetailIds)){
                redisClient.set(RedisKey.totalTaskNum_suffix+detailId,totalNum);
                System.out.println("更新detailId="+detailId+",totalNum="+totalNum+"成功!");
            }
        }
        return true;
    }

    @RequestMapping(value = "/updateResultNumAndFinishNum.json", method = RequestMethod.POST)
    @ResponseBody
    public boolean updateResultNumAndFinishNum(@RequestParam(value = "detailId")@ApiParam(value = "detailId",required = true) String detailId,
                                      @RequestParam(value = "finishNum",defaultValue = "0")@ApiParam(value = "finishNum",required = true) String finishNum,
                                      @RequestParam(value = "resultNum",defaultValue = "0")@ApiParam(value = "resultNum",required = true) String resultNum,
                                      @RequestParam(value = "projectId")@ApiParam(value = "projectId",required = true) String projectId,
                                      @RequestParam(value = "workFlowId") @ApiParam(value = "workFlowId",required = true) String workFlowId,
                                      @RequestParam(value = "preRun",required = false) @ApiParam(value = "preRun",required = false) String preRun,
                                      @RequestParam(value = "travelParams",required = false)@ApiParam(value = "travelParams",required = false)String travelParams){

        LoggerUtil.debugTrace("updateResultNumAndFinishNum.json =====detailId="+detailId+"====finishNum="+finishNum+"===resultNum"+resultNum+"====projectId="+projectId+"preRun:="+preRun+"travelParams:="+travelParams);

        //累加完成任务数量和结果数量。
        //判断上个节点（有可能多个）的状态，如果都为完成则取出所有上个节点的结果数，累加更新为当前节点的总数量。
        //判断当完成任务数量（finishNum）和总任务数量（totalNum）一样，则表示当前节点完成。

        if(Validate.isEmpty(detailId) || !detailId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(projectId) || !projectId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(finishNum) || !finishNum.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(resultNum) || !resultNum.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(!Validate.isEmpty(travelParams)){
        JSONObject jsonObject=JSONObject.fromObject(travelParams);
        if(jsonObject.size()>0){
            if(jsonObject.containsKey("preRun")){
                String preRun1=jsonObject.getString("preRun");
                if(preRun1==null) {
                    preRun = null;
                }else {
                    preRun=preRun1;
                }
            }

        }
        }
        if(("").equals(preRun)||preRun==null){
                preRun=null;
        }

        LoggerUtil.debugTrace("===preRun:"+preRun+"travelParams:"+travelParams);
        Long detailIdInt = Long.parseLong(detailId);
        Long projectIdInt = Long.parseLong(projectId);
        Long finishNumInt = Long.parseLong(finishNum);
        Long resultNumInt = Long.parseLong(resultNum);
        Long workFlowIdInt = Long.parseLong(workFlowId);

        redisClient.incrBy(RedisKey.finishNum_suffix.name()+detailId,finishNumInt);
        redisClient.incrBy(RedisKey.resultNum_suffix.name()+detailId,resultNumInt);
        redisClient.expire(RedisKey.resultNum_suffix.name()+detailId,7*86400);
        updateStateFinishRecursive(projectIdInt,detailIdInt,workFlowIdInt,preRun);
        return true;
    }


    public void updateStateFinishRecursive(long projectId,long detailIdInt,long workFlowId,String preRun){

        String totalTaskNumStr = redisClient.get(RedisKey.totalTaskNum_suffix.name()+detailIdInt);//获取当前节点的总任务数
        LoggerUtil.debugTrace("detailIdInt:"+detailIdInt+"preRun:"+preRun+"totalTaskNumStr:"+totalTaskNumStr);
        String detailListStr = "";
        if(workFlowId == 0){//如果为0则是常规项目，使用projectId
            detailListStr = redisClient.get(RedisKey.startProjectDetailList_suffix.name()+projectId);
        }else{
            detailListStr = redisClient.get(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId);
        }
        LoggerUtil.debugTrace("detailListStr:"+detailListStr+"detailIdInt:="+detailIdInt);
        List<WorkFlowDetail> workFlowDetailListTem = null;
        if(!Validate.isEmpty(detailListStr) && !"null".equals(detailListStr)){
            workFlowDetailListTem = JSON.parseArray(detailListStr,WorkFlowDetail.class);
        }else {
            //把两个list添加进redis缓存
            if(workFlowId == 0){
                workFlowDetailListTem = workFlowService.getWorkFlowDetailByProjectId(projectId);
                String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                redisClient.set(RedisKey.startProjectDetailList_suffix.name()+projectId,workFlowDetailListStr);
                redisClient.expire(RedisKey.startProjectDetailList_suffix.name()+projectId,4*3600);
            }else{
                workFlowDetailListTem = workFlowService.getWorkFlowDetailByWorkFlowId(workFlowId);
                String workFlowDetailListStr = JSON.toJSONString(workFlowDetailListTem);
                redisClient.set(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId,workFlowDetailListStr);
                redisClient.expire(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId,4*3600);
            }
        }
        Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
        Map<Long,Integer> workFlowDetailIndexMap = new HashMap<>();
        for (int i = 0; i < workFlowDetailListTem.size(); i++) {//聚合数据成为map
            WorkFlowDetail workFlowDetail = workFlowDetailListTem.get(i);
            workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
            workFlowDetailIndexMap.put(workFlowDetail.getFlowDetailId(),i);
        }
        LoggerUtil.debugTrace("detailIdInt:="+detailIdInt+"totalTaskNumStr:="+totalTaskNumStr);
        if(Validate.isEmpty(totalTaskNumStr) || "null".equals(totalTaskNumStr)){
            WorkFlowDetail workFlowDetail = workFlowDetailMap.get(detailIdInt);

            if(null != workFlowDetail){
                if(preRun==null){
                    String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                    //如果存在上节点
                    if(!"0".equals(prevFlowDetailIds)){
                        String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");

                        int finishJobNum = 0;
                        for (String detailIdStr:prevFlowDetailIdArray) {
                            Long prevDetailId = Long.parseLong(detailIdStr);
                            WorkFlowDetail prevDetail = workFlowDetailMap.get(prevDetailId);
                            if(prevDetail.getJobStatus() == 2){//如果是完成
                                finishJobNum ++;
                            }

                        }

                        //如果前面的节点都完成了
                        if(finishJobNum >= prevFlowDetailIdArray.length){
                            Long preResultNum = 0L;
                            for (String detailIdStr:prevFlowDetailIdArray) {
                                if(!Validate.isEmpty(redisClient.get(RedisKey.resultNum_suffix.name()+detailIdStr))){
                                    preResultNum += Long.parseLong(redisClient.get(RedisKey.resultNum_suffix.name()+detailIdStr));
                                }
                            }
                            redisClient.set(RedisKey.totalTaskNum_suffix.name()+detailIdInt,Long.toString(preResultNum));
                        }
                    }
                }else{
                    String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                    //如果存在上节点
                    if(!"0".equals(prevFlowDetailIds)){
                        String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");

                        int finishJobNum = 0;
                        for (String detailIdStr:prevFlowDetailIdArray) {
                            Long prevDetailId = Long.parseLong(detailIdStr);
                            WorkFlowDetail prevDetail = workFlowDetailMap.get(prevDetailId);
                            if(prevDetail.getJobStatus() == 6){//如果是完成
                                finishJobNum ++;
                            }

                        }

                        //如果前面的节点都完成了
                        if(finishJobNum >= prevFlowDetailIdArray.length){
                            Long preResultNum = 0L;
                            for (String detailIdStr:prevFlowDetailIdArray) {
                                if(!Validate.isEmpty(redisClient.get(RedisKey.resultNum_suffix.name()+detailIdStr))){
                                    preResultNum += Long.parseLong(redisClient.get(RedisKey.resultNum_suffix.name()+detailIdStr));
                                }
                            }
                            redisClient.set(RedisKey.totalTaskNum_suffix.name()+detailIdInt,Long.toString(preResultNum));
                        }
                    }
                }

            }
        }
        LoggerUtil.debugTrace("RedisKey.totalTaskNum_suffix.name()+detailIdInt"+RedisKey.totalTaskNum_suffix.name()+detailIdInt);
        totalTaskNumStr = redisClient.get(RedisKey.totalTaskNum_suffix.name()+detailIdInt);
        if(!Validate.isEmpty(totalTaskNumStr) && !"null".equals(totalTaskNumStr)){
            Long totalTaskNum = Long.parseLong(totalTaskNumStr);
            String finishNumStr = "";
            Long nextDetailFinishNumInt = 0l;
           //根据当前节点查询出上级节点
            WorkFlowDetail currentWorkFlowDetail = workFlowDetailMap.get(detailIdInt);
            WorkFlowDetail prevWorkFlowDetail = workFlowDetailMap.get(Long.parseLong(currentWorkFlowDetail.getPrevFlowDetailIds()));//目前只考虑上级节点只有一个的情况
            //判断是否有上节点并且上节点是条件节点
            if(prevWorkFlowDetail !=null && Constants.WORK_FLOW_TYPE_NO_CONDITION.equals(prevWorkFlowDetail.getTypeNo())) {
                //如果上个节点是条件节点，则需要获取当前节点的所有兄弟节点（对于条件节点来说）
                String[] conditionNextDetails = prevWorkFlowDetail.getNextFlowDetailIds().split(",");//获取条件节点所有的下级节点
                for (String nextDetail : conditionNextDetails) {
                    //取出条件节点中每一个下级节点的finishNum数
                    String nextDetailFinishNum = redisClient.get(RedisKey.finishNum_suffix.name() + nextDetail);
                    LoggerUtil.debugTrace("当前节点为：" + detailIdInt + ",条件节点的下级节点为：" + nextDetail + ",完成数为：" + nextDetailFinishNum);
                    if (!Validate.isEmpty(nextDetailFinishNum)) {
                        nextDetailFinishNumInt += Long.parseLong(nextDetailFinishNum);
                    }
                    finishNumStr = String.valueOf(nextDetailFinishNumInt);
                }
                Long finishNum2 = Long.parseLong(finishNumStr);
                LoggerUtil.debugTrace("当前节点为：" + detailIdInt + ",条件节点的下级节点总完成数为：" + finishNum2);
                if (finishNum2 >= totalTaskNum) {
                    for (String nextDetail : conditionNextDetails) {
                        //将条件节点所有的子节点更新为完成
                        if(preRun==null){
                            int i = workFlowService.updateWorkFlowDetailToFinish(Long.parseLong(nextDetail));
                        }else{
                            int i=workFlowService.updateWorkFlowDetailToPreRunFinish(Long.parseLong(nextDetail));
                        }

                        //判断项目是否完成  和  执行下一个状态节点
                        updateProjectIfFinshAndExecuteNextNode(projectId, Long.parseLong(nextDetail), workFlowId,preRun);

                        WorkFlowDetail workFlowDetail = workFlowDetailMap.get(Long.parseLong(nextDetail));

                        String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();

                        if (!Validate.isEmpty(nextFlowDetailIds)) {
                            String[] nextDetailIdArray = nextFlowDetailIds.split(",");
                            for (String nextDetailId : nextDetailIdArray) {
                                long nextDetailIdInt = Long.parseLong(nextDetailId);
                                WorkFlowDetail nextWorkFlowDetail = workFlowDetailMap.get(nextDetailIdInt);
                                //如果下一个节点在进行中则递归调用
                                if (nextWorkFlowDetail.getJobStatus() == 1) {
                                    updateStateFinishRecursive(projectId, nextWorkFlowDetail.getFlowDetailId(), workFlowId,preRun);
                                }
                            }
                        }
                    }
                }
            }else{
                finishNumStr = redisClient.get(RedisKey.finishNum_suffix.name()+detailIdInt);
                if(Validate.isEmpty(finishNumStr)){
                    finishNumStr = "0";
                }
                Long finishNum = Long.parseLong(finishNumStr);
                if(finishNum >= totalTaskNum){//当当前节点的完成数大于等于总任务数时。即当前节点执行完毕
                    //将节点状态更新为完成
                    if(preRun==null){
                        int i = workFlowService.updateWorkFlowDetailToFinish(detailIdInt);
                    }else{
                        int i=workFlowService.updateWorkFlowDetailToPreRunFinish(detailIdInt);
                    }

                    //判断项目是否完成  和  执行下一个状态节点
                    updateProjectIfFinshAndExecuteNextNode(projectId,detailIdInt,workFlowId,preRun);

                    WorkFlowDetail workFlowDetail = workFlowDetailMap.get(detailIdInt);

                    String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
                    for(Map.Entry entry : workFlowDetailMap.entrySet()){
                        System.out.println("====hhhhhh=="+JSONObject.fromObject(entry.getValue())+"=====");
                    }
                    if(!Validate.isEmpty(nextFlowDetailIds)){
                        String[] nextDetailIdArray = nextFlowDetailIds.split(",");

                        for (String nextDetailId:nextDetailIdArray) {
                            long nextDetailIdInt = Long.parseLong(nextDetailId);
                            WorkFlowDetail nextWorkFlowDetail = workFlowDetailMap.get(nextDetailIdInt);
                            //如果下一个节点在进行中则递归调用
                            LoggerUtil.debugTrace("nextDetailIdInt:="+nextDetailIdInt+"nextWorkFlowDetail.getJobStatus:="+nextWorkFlowDetail.getJobStatus());
                            if(nextWorkFlowDetail.getJobStatus() == 1){
                                updateStateFinishRecursive(projectId,nextWorkFlowDetail.getFlowDetailId(),workFlowId,preRun);
                            }
                        }
                    }
                }
            }

        }

    }
    @RequestMapping(value = "/getWorkFlowParamCallback.json", method = RequestMethod.GET)
    @ResponseBody
    public List<WorkFlowParam> getWorkFlowParamCallback(@RequestParam(value = "projectId") @ApiParam(value = "projectId",required = true) String projectId,
                                                        @RequestParam(value = "typeNos",required = false) @ApiParam(value = "typeNos",required = false) String typeNos){

        List<WorkFlowParam> resultList;
        List<String> typeList = new ArrayList<>();
        String resultStr = redisClient.get(RedisKey.typeNos_suffix.name()+"_"+typeNos+projectId);
        if(Validate.isEmpty(resultStr)){
            if(!Validate.isEmpty(typeNos)){
                String[] typeArray = typeNos.split(",");
                for(int i = 0;i<typeArray.length;i++){
                    typeList.add(typeArray[i]);
                }
                resultList = workFlowService.getWorkFlowParamListByTypeNoList(typeList,Long.parseLong(projectId));
                if(!Validate.isEmpty(resultList)){
                    resultStr = JSON.toJSONString(resultList);
                    redisClient.set(RedisKey.typeNos_suffix.name() + "_" + typeNos + projectId, resultStr);
                    redisClient.expire(RedisKey.typeNos_suffix.name() + "_" + typeNos + projectId, 86400);
                }
                return resultList;
            }else{
                resultList = workFlowService.getWorkFlowParamByProJectId(Long.parseLong(projectId));
                if(!Validate.isEmpty(resultList)){
                    resultStr = JSON.toJSONString(resultList);
                    redisClient.set(RedisKey.typeNos_suffix.name()+"_"+typeNos+projectId,resultStr);
                    redisClient.expire(RedisKey.typeNos_suffix.name()+"_"+typeNos+projectId,86400);
                }
                return resultList;
            }
        }
        resultStr = redisClient.get(RedisKey.typeNos_suffix.name()+"_"+typeNos+projectId);
        resultList = com.alibaba.fastjson.JSONArray.parseArray(resultStr,WorkFlowParam.class);
        return resultList;
    }

    @RequestMapping(value = "/addWorkFlowTask.json", method = RequestMethod.POST)
    @ResponseBody
    public SendMailPO addWorkFlowTask(@RequestParam("projectId") @ApiParam("项目id") String projectId,
                                              @RequestParam("workFlowId") @ApiParam("流程id") String workFlowId,
                                              @RequestParam(value = "crawlFreq",required = false) @ApiParam("抓取频率") String crawlFreq,
                                              @RequestParam("value") @ApiParam("配置流程抓取的输入参数") String value,
                                              @RequestParam("token") @ApiParam("token") String token,
                                                @RequestParam("batchNo") @ApiParam("batchNo") String batchNo){
        String crawlServerByEnv = WebUtil.getBaseServerByEnv();
        String string = MD5Util.encrypt(projectId+Constants.TOKEN);
        if(!Validate.isEmpty(token)){
            if(!string.equals(token)){
                throw new WebException(MySystemCode.TOKEN_ERROR);
            }
        }else{
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(batchNo==null || "".equals(batchNo)){
            throw  new WebException((SystemCode.SYS_REQUEST_EXCEPTION));
        }
        long projectIdInt = Long.parseLong(projectId);
        long workFlowIdInt = Long.parseLong(workFlowId);

        //1、根据项目id查询所有的 数据抓取 流程列表
        List<DataCrawlPO> dataCrawlPOList = new ArrayList<>();
        SendMailPO sendMailPO = new SendMailPO();

        List<WorkFlowDetail> workFlowDetailList = workFlowService.getFirstDetailByProjectId(projectIdInt);
        for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
            if(!workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL) && !workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
                continue;
            }
            WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(workFlowDetail.getFlowDetailId());
            int jobStatus = workFlowDetail.getJobStatus();
            String statusName = "";
            switch (jobStatus){
                case 0:statusName="未启动";
                    break;
                case 1:statusName="运行中";
                    break;
                case 2:statusName="已完成";
                    break;
                case 3:statusName="已执行";
                    break;
                case 4:statusName="停止";
                    break;
                case 9:statusName="异常";
                    break;
            }
            DataCrawlPO dataCrawlPO = JSON.parseObject(workFlowParam.getJsonParam(),DataCrawlPO.class);
            dataCrawlPO.setParamId(workFlowParam.getParamId());
            dataCrawlPO.getJsonParam().setParamId(workFlowParam.getParamId());
            dataCrawlPO.setStatusName(statusName);
            dataCrawlPOList.add(dataCrawlPO);
        }
        //2、查询所有已生效的流程
        List<WorkFlowTemplateBO> workFlowTemplateBOList = workFlowTemplateService.getWorkFlowTemplateListByParam(WorkFlowTemplateBO.STATUS_VALID,null,null,null);

        List<CrawlWorkFlowTemplatePO> crawlWorkFlowTemplatePOList = new ArrayList<>();
        for (WorkFlowTemplateBO workFlowTemplateBO:workFlowTemplateBOList) {
            CrawlWorkFlowTemplatePO crawlWorkFlowTemplatePO = new CrawlWorkFlowTemplatePO();
            crawlWorkFlowTemplatePO.setId(workFlowTemplateBO.getId());
            crawlWorkFlowTemplatePO.setName(workFlowTemplateBO.getName());
            List<WorkFlowNodeBO> workFlowNodeBOList = workFlowTemplateService.getWorkFlowNodeListByTemplateId(workFlowTemplateBO.getId());
            if(!Validate.isEmpty(workFlowNodeBOList)){
                WorkFlowNodeBO workFlowNodeBO = workFlowNodeBOList.get(0);
                String nodeParam = workFlowNodeBO.getNodeParam();
                long datasourceTypeId = JSONObject.fromObject(nodeParam).optLong("datasourceTypeId",0);
                crawlWorkFlowTemplatePO.setDatasourceTypeId(datasourceTypeId);
                crawlWorkFlowTemplatePOList.add(crawlWorkFlowTemplatePO);
            }
        }
        long dataSourceTypeId = 0;//数据源类型id
        long dataSourceId = 0;//数据源id
        //3、遍历 查到传过来的数据源类型id
        for(CrawlWorkFlowTemplatePO crawlWorkFlowTemplatePO : crawlWorkFlowTemplatePOList){
            if(workFlowIdInt == crawlWorkFlowTemplatePO.getId()){
                dataSourceTypeId = crawlWorkFlowTemplatePO.getDatasourceTypeId();
                break;
            }
        }
        //4、根据数据源类型id查询该数据源类型的输入参数
        Object resultObj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServerByEnv+"/common/getCrawlInputParamsByDatasourceType.json?datasourceTypeId="+dataSourceTypeId,"get",null);
        JSONArray jsonArray =null;
        if(null != resultObj) {
            jsonArray = (JSONArray) resultObj;
        }

        if(jsonArray!=null){
            JSONObject valueJson = JSONObject.fromObject(value);
            for(Object o : jsonArray){
                JSONObject jsonObject1 = (JSONObject) o;
                dataSourceId =jsonObject1.getLong("datasourceId");
                String paramEnName = jsonObject1.getString("paramEnName");
                if(valueJson.getString(paramEnName) != null&&paramEnName.equals("page")){
                    JSONObject pageJson = valueJson.getJSONObject(paramEnName);
                    jsonObject1.put("paramValue",pageJson);
                }else if (valueJson.getString(paramEnName) != null && !paramEnName.equals("page")){
                    JSONObject jsonValue = new JSONObject();
                    jsonObject1.put("paramValue",valueJson.getString(paramEnName));
                }
            }
        }else{
            throw new WebException("数据源类型没有输入参数");
        }
        List<DatasourcePO> datasourcePOList = dataSourceTypeService.getDataSourceTypeList(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
        String dataSourceName = "";//数据源名称
        for(DatasourcePO datasourcePO : datasourcePOList){
            if(datasourcePO.getDatasourceId()==dataSourceId){
                dataSourceName = datasourcePO.getDatasourceName();
                break;
            }
        }
        //6、根据数据源类型查询数据类型po
        DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(dataSourceTypeId);

        DataCrawlJsonParamPO dataCrawlJsonParamPO = new DataCrawlJsonParamPO();
        dataCrawlJsonParamPO.setCrawlWay("process");
        dataCrawlJsonParamPO.setCrawlWayName("流程抓取");
        dataCrawlJsonParamPO.setCrawlType(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
        dataCrawlJsonParamPO.setDatasourceTypeId(dataSourceTypeId+"");
        dataCrawlJsonParamPO.setDatasourceId(dataSourceId+"");
        dataCrawlJsonParamPO.setInputParamArray(jsonArray);
        dataCrawlJsonParamPO.setDatasourceName(dataSourceName);
        dataCrawlJsonParamPO.setTaskName(dataSourceName);
        dataCrawlJsonParamPO.setCrawlFreqType(2+"");

        //持续时间
        String sustainTime = "";
        String quartzTimes="";
        if(crawlFreq ==null){
            dataCrawlJsonParamPO.setSustainTime(86400L);
            dataCrawlJsonParamPO.setQuartzTime("0 0/1 * * * ?");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,1);
            int year = calendar.get(calendar.YEAR);
            int month = calendar.get(calendar.MONTH)+1;
            int date = calendar.get(calendar.DAY_OF_MONTH);
            int hour = calendar.get(calendar.HOUR_OF_DAY);
            int minute = calendar.get(calendar.MINUTE);
            //sustainTime = "0 0 "+hour+" "+date+" "+month+" ? "+year;
            sustainTime = "0 "+minute+" "+hour+" "+date+" "+month+" ? "+year;
        }else{
            Calendar calendar = Calendar.getInstance();
            int preYear = calendar.get(calendar.YEAR);//当前年
            int preMonth = calendar.get(calendar.MONTH)+1;//当前月
            int preDate = calendar.get(calendar.DAY_OF_MONTH);//当前日
            int preHour = calendar.get(calendar.HOUR_OF_DAY);//当前时
            int preMinute = calendar.get(calendar.MINUTE);//当前分
            int preSecond = calendar.get(calendar.SECOND);//当前秒

            String[] str = crawlFreq.split(",");//3600
            Long minuteLag = Long.parseLong(str[0])/60;//60
            quartzTimes=0+" "+0+"/"+10+" * * * ?";
            if(minuteLag>=1 && minuteLag<60){//如果在60分钟以内
                //获取当前分钟数
                dataCrawlJsonParamPO.setQuartzTime(0+" "+0+"/"+minuteLag+" * * * ?");
            }else if(minuteLag/60<24 && minuteLag/60>=1){//如果在24小时内
                Long hours = minuteLag/60;
                dataCrawlJsonParamPO.setQuartzTime(0+" "+preMinute+" "+preHour+"/"+hours+" * * ?");
            }else if(minuteLag/1440>=1 && minuteLag/1400<30){//如果在一个月内
                Long day = minuteLag/1440;
                dataCrawlJsonParamPO.setQuartzTime(0+" "+preMinute+" "+preHour+" "+preDate+"/"+day+" * ?");
            }else{
                dataCrawlJsonParamPO.setQuartzTime(0+"/"+str[0]+" * * * * ?");
            }
            long sustainTimeLong = Long.parseLong(str[1]);
            Long dd = sustainTimeLong/86400;//持续多少天
            dataCrawlJsonParamPO.setSustainTime(sustainTimeLong);
            calendar.add(Calendar.DATE,dd.intValue());
            int year = calendar.get(calendar.YEAR);
            int month = calendar.get(calendar.MONTH)+1;
            int date = calendar.get(calendar.DAY_OF_MONTH);
            int hour = calendar.get(calendar.HOUR_OF_DAY);
            int minute = calendar.get(calendar.MINUTE);//当前分
            int second = calendar.get(calendar.SECOND);//当前秒
            sustainTime = second+" "+minute+" "+hour+" "+date+" "+month+" ? "+year;
        }
        dataCrawlJsonParamPO.setStatus(0);
        dataCrawlJsonParamPO.setStorageTypeTable(datasourceTypePO.getStorageTypeTable());
        dataCrawlJsonParamPO.setDatasourceTypeName(datasourceTypePO.getTypeName());
        dataCrawlJsonParamPO.setWorkFlowTemplateId(Integer.parseInt(workFlowId));
        DataCrawlPO dataCrawlPO = new DataCrawlPO();
        dataCrawlPO.setCrawlWay(dataCrawlJsonParamPO.getCrawlWay());
        dataCrawlPO.setCrawlType(dataCrawlJsonParamPO.getCrawlType());
        dataCrawlPO.setBatchNo(batchNo);
        List<String> typeList = new ArrayList<>();
        typeList.add(com.transing.dpmbs.constant.Constants.PARAM_TYPE_CRAWL_WAY);
        List<ParamBO> paramBOList = paramService.getKeyValueListByType(typeList);
        for (ParamBO param:paramBOList) {
            if(param.getKey().equals(dataCrawlJsonParamPO.getCrawlWay())){

                dataCrawlJsonParamPO.setCrawlWayName(param.getValue());
                dataCrawlPO.setCrawlWayName(dataCrawlJsonParamPO.getCrawlWayName());
                break;
            }
        }
        dataCrawlPO.setWorkFlowTemplateId(dataCrawlJsonParamPO.getWorkFlowTemplateId());

        String crawlWay = dataCrawlJsonParamPO.getCrawlWay();
        if(crawlWay.equals("process")){
            int workFlowTemplateId = dataCrawlJsonParamPO.getWorkFlowTemplateId();
            List<WorkFlowNodeBO> workFlowNodeBOList = workFlowTemplateService.getWorkFlowNodeListByTemplateId(workFlowTemplateId);
            if( !Validate.isEmpty(workFlowNodeBOList)){
                WorkFlowNodeBO workFlowNodeBO = workFlowNodeBOList.get(0);
                String nodeParam = workFlowNodeBO.getNodeParam();
                JSONObject jsonObject = JSONObject.fromObject(nodeParam);
                dataCrawlJsonParamPO.setDatasourceId(jsonObject.optString("datasourceId"));
                dataCrawlJsonParamPO.setDatasourceTypeId(jsonObject.optString("datasourceTypeId"));
            }
        }

        dataCrawlPO.setDatasourceTypeName(datasourceTypePO.getTypeName());
        dataCrawlJsonParamPO.setDatasourceTypeName(datasourceTypePO.getTypeName());

        dataCrawlPO.setJsonParam(dataCrawlJsonParamPO);
        dataCrawlPO.setDatasourceName(dataSourceName);
        dataCrawlJsonParamPO.setDatasourceName(dataSourceName);
        dataCrawlPO.setTaskName(dataCrawlJsonParamPO.getTaskName());
        String crawlFreqType = dataCrawlJsonParamPO.getCrawlFreqType();
        if ("1".equals(crawlFreqType)){
            dataCrawlPO.setCrawlFreq("单次抓取");
            dataCrawlJsonParamPO.setQuartzTime("");
        }else if("2".equals(crawlFreqType)){
            dataCrawlPO.setCrawlFreq(dataCrawlJsonParamPO.getQuartzTime());
        }

        List<JSONObject> dataCrawlInputParamPOList = dataCrawlJsonParamPO.getInputParamArray();
        String inputParams = "";

        if(null != dataCrawlInputParamPOList && dataCrawlInputParamPOList.size() > 0){
            for (JSONObject dataCrawlInputParamPO : dataCrawlInputParamPOList) {
                inputParams += dataCrawlInputParamPO.optString("paramCnName","") + dataCrawlInputParamPO.optString("paramValue","") + "\n";//TODO
            }
        }

        dataCrawlPO.setInputParams(inputParams);

        String quartzTime = dataCrawlJsonParamPO.getQuartzTime();
        if(quartzTime.endsWith("*")){//因为前端做不到 cron表达式的日期和周 要互斥。所以，后端把周如果是* 则 替换成?
            quartzTime = quartzTime.substring(0,quartzTime.length()-1);
            quartzTime += "?";
        }

        String jsonParamStr = JSON.toJSONString(dataCrawlPO);

        Long paramId = dataCrawlJsonParamPO.getParamId();
        long detailId = 0;
        if( null != paramId && paramId > 0){
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setParamId(paramId);
            workFlowParam.setJsonParam(jsonParamStr);
            workFlowService.updateWorkFlowParam(workFlowParam);//同时更新 输出字段
            workFlowParam = workFlowService.getWorkFlowParamByParamId(paramId);
            WorkFlowDetail workFlowDetail = new WorkFlowDetail();
            workFlowDetail.setFlowDetailId(workFlowParam.getFlowDetailId());
            workFlowDetail.setQuartzTime(quartzTime);
            workFlowService.updateWorkFlowDetailQuartzTime(workFlowDetail);
        }else{
            dataCrawlPO.setParamId(paramId);

            if(crawlWay.equals("process")){
                paramId = workFlowService.addWorkDetail(projectIdInt,Constants.WORK_FLOW_TYPE_NO_DATACRAWL,jsonParamStr,WorkFlowParam.PARAM_TYPE_PRIVATE,quartzTime,Long.parseLong(dataCrawlJsonParamPO.getDatasourceTypeId()),dataCrawlJsonParamPO.getWorkFlowTemplateId());
            }else if(crawlWay.equals("data")){
                paramId = workFlowService.addWorkDetail("0",projectIdInt,Constants.WORK_FLOW_TYPE_NO_DATACRAWL,jsonParamStr,WorkFlowParam.PARAM_TYPE_PRIVATE,quartzTime,Long.parseLong(dataCrawlJsonParamPO.getDatasourceTypeId()));
            }

            WorkFlowParam workFlowPara = workFlowService.getWorkFlowParamByParamId(paramId);
            detailId = workFlowPara.getFlowDetailId();

            if (paramId > 0) {//如果 添加导入数据成功

                ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                projectJobTypeBO.setProjectId(projectIdInt);
                projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT);
                ProjectJobTypeBO projectJobType = projectJobTypeService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);

                if(null != projectJobType){
                    //添加 数据语义分析对象设置======================Start=============================================================

                    String jsonPara = workFlowPara.getJsonParam();
                    JSONObject jsonObject = JSONObject.fromObject(jsonPara);
                    Object datasourceTypeIdObj = jsonObject.get("datasourceTypeId");
                    if(null == datasourceTypeIdObj ){
                        datasourceTypeIdObj = jsonObject.getJSONObject("jsonParam").get("datasourceTypeId");
                    }

//                    long dataSourceTypeId = Long.parseLong(datasourceTypeIdObj.toString());//数据源类型id
                    String dataSourceTypeName = dataCrawlPO.getJsonParam().getDatasourceTypeName();//数据源类型名字

                    //判断 语义分析对象是否添加过这个数据源类型的数据了 如果有则不添加
                    List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT, projectIdInt);

                    boolean isNeedAdd = true;//默认需要添加语义分析对象

                    if (null != workFlowParamList && workFlowParamList.size() > 0) {
                        for (WorkFlowParam workFlowParam : workFlowParamList) {
                            String jsonStr = workFlowParam.getJsonParam();
                            SemanticAnalysisObjectPO semanticAnalysisObject = JSON.parseObject(jsonStr, SemanticAnalysisObjectPO.class);
                            long seamDataSourceTypeId = semanticAnalysisObject.getJsonParam().getDataSourceTypeId();
                            if (seamDataSourceTypeId == dataSourceTypeId) {//如果语义分析对象已经添加过 这个数据源类型 则 设置 isNeedAdd 为false

                                //不需要添加，
                                isNeedAdd = false;
                                long flowDetailId = workFlowParam.getFlowDetailId();

                                //更新上一个节点的next
                                WorkFlowDetail preDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailId);
                                String nextFlowDetailIds = preDetail.getNextFlowDetailIds();

                                if(!Validate.isEmpty(nextFlowDetailIds)){
                                    nextFlowDetailIds += ","+flowDetailId;
                                }else {
                                    nextFlowDetailIds = Long.toString(flowDetailId);
                                }
                                preDetail.setNextFlowDetailIds(nextFlowDetailIds);
                                workFlowService.updateWorkFlowDetail(preDetail);

                                //但需要更新这个语义节点的上一个节点ids，
                                WorkFlowDetail workFlowDetai2 = workFlowService.getWorkFlowDetailByWorkFlowDetailId(flowDetailId);
                                String prevFlowDetailIds = workFlowDetai2.getPrevFlowDetailIds();

                                if(null != prevFlowDetailIds && !"".equals(prevFlowDetailIds)){
                                    prevFlowDetailIds += ","+detailId;
                                }

                                workFlowDetai2.setPrevFlowDetailIds(prevFlowDetailIds);

                                workFlowService.updateWorkFlowDetail(workFlowDetai2);

                            }
                        }
                    }

                    if (isNeedAdd) {
                        List<ContentTypePO> contentTypePOList = contentTypeService.getContentTypeList(dataSourceTypeId);

                        SemanticAnalysisObjectPO semanticAnalysisObjectPO = new SemanticAnalysisObjectPO();// 语义分析对象 返回的po

                        semanticAnalysisObjectPO.setDataSourceTypeName(dataSourceTypeName);
                        semanticAnalysisObjectPO.setSentence("分词+主题+话题");
                        semanticAnalysisObjectPO.setSection("无");
                        semanticAnalysisObjectPO.setArticle("分词+主题+话题");

                        SemanticJsonParam semanticJsonParam = new SemanticJsonParam();//语义分析对象 jsonPO
                        semanticJsonParam.setDataSourceTypeId(dataSourceTypeId);
                        semanticJsonParam.setDataSourceTypeName(dataSourceTypeName);
                        semanticJsonParam.setStorageTypeTable(dataCrawlJsonParamPO.getStorageTypeTable());//新增 存储表名

                        List<AnalysisObject> analysisObjectList = new ArrayList<>();

                        String anAyleObjectName = "";

                        if (null != contentTypePOList && contentTypePOList.size() > 0) {
                            for (ContentTypePO contentTypePO : contentTypePOList) {
                                if (contentTypePO.getIsDefault() == 1) {
                                    AnalysisObject analysisObject = new AnalysisObject();//分析对象
                                    analysisObject.setContentType(contentTypePO.getContentType());//设置 为默认 的 contentType
                                    analysisObject.setSubType(1);//设置为全文
                                    analysisObject.setValue(0);//全文
                                    analysisObjectList.add(analysisObject);

                                    anAyleObjectName += contentTypePO.getContentTypeName() + "全文+";
                                }
                            }
                            if (!"".equals(anAyleObjectName)) {
                                anAyleObjectName = anAyleObjectName.substring(0, anAyleObjectName.length() - 1);
                            }
                        }

                        semanticAnalysisObjectPO.setAnalysisObjectName(anAyleObjectName);

                        List<AnalysisHierarchy> analysisHierarchyList = new ArrayList<>();
                        AnalysisHierarchy analysisHierarchy = new AnalysisHierarchy();//分析层级
                        analysisHierarchy.setType(1);
                        analysisHierarchy.setHierarchy("1,3");
                        analysisHierarchyList.add(analysisHierarchy);
                        AnalysisHierarchy analysisHierarchy2 = new AnalysisHierarchy();//分析层级
                        analysisHierarchy2.setType(2);
                        analysisHierarchy2.setHierarchy("1,3");
                        analysisHierarchy2.setCalculation(String.valueOf(2));
                        analysisHierarchyList.add(analysisHierarchy2);
                        AnalysisHierarchy analysisHierarchy3 = new AnalysisHierarchy();//分析层级
                        analysisHierarchy3.setType(3);
                        analysisHierarchy3.setHierarchy("1,3");
                        analysisHierarchy3.setCalculation(String.valueOf(3));
                        analysisHierarchyList.add(analysisHierarchy3);
                        semanticJsonParam.setAnalysisHierarchy(analysisHierarchyList);
                        semanticJsonParam.setAnalysisObject(analysisObjectList);
                        semanticAnalysisObjectPO.setJsonParam(semanticJsonParam);
                        List<Long> dataSourceTypes = new ArrayList<>();//添加结果类型
                        dataSourceTypes.add(-1L);//句级
                        dataSourceTypes.add(-3L);//文级
                        String semanticJsonPara = JSON.toJSONString(semanticAnalysisObjectPO);
                        workFlowService.addWorkDetail(Long.toString(detailId),projectIdInt, Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT, semanticJsonPara, WorkFlowParam.PARAM_TYPE_COMON, null, dataSourceTypes);
                    }
                }
            }
        }

        //取出第一个工作流paramId并且进行执行
        WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailId);
        String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
        while (!prevFlowDetailIds.equals("0")){//这里排除了上节点如果为多个的情况
            workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(prevFlowDetailIds));
            prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
        }
        //根据第一个流程id查询出第一个WorkFlowParam
        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(workFlowDetail.getFlowDetailId());

        //启动该流程
        boolean isSecc = workFlowService.startWorkFlowByParamId(workFlowParam.getParamId(),projectIdInt,batchNo);
        //当循环任务启动后，需要在配置一个定时任务，用来移除循环任务。
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("jobName", workFlowParam.getTypeNo()+workFlowParam.getFlowDetailId());
            map.put("workFlowService", workFlowService);
            map.put("detailId", workFlowParam.getFlowDetailId());
            String jobTime = projectIdInt+"_"+System.currentTimeMillis();
            map.put("jobTime",jobTime);
            String jobs=projectIdInt+"_"+batchNo+System.currentTimeMillis();
            Map<String,Object> map1=new HashMap<>();
            map1.put("jobName", workFlowParam.getTypeNo()+workFlowParam.getFlowDetailId());
            map1.put("projectId",projectId);
            map1.put("batchNo",batchNo);
            map1.put("jobTime",jobTime);
            Map<String,Object> map2=new HashMap<>();
            map2.put("jobName",jobs+"fenxi");
            map2.put("jobTime",jobTime);
            LoggerUtil.debugTrace("---=====batchNo:="+batchNo);
            QuartzManager.addJob(jobs+"fenxi",ExecuteGetCountJobQuartz.class,quartzTimes,map1);
            QuartzManager.addJob(jobs+"Stop",ExecuteStopJobQuartz.class,sustainTime,map2);
            QuartzManager.addJob(jobTime,ExecuteStopJobQuartz.class,sustainTime,map);
        }catch (Exception e){
            e.printStackTrace();
        }


        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();

        sendMailPO.setCode(0);
        sendMailPO.setMessage("创建启动成功");
        LoggerUtil.infoTrace("============================isSecc:"+isSecc+"=============");
        if(!isSecc){
            throw new WebException(MySystemCode.BIZ_START_PROJECT_EXCEPTION);
        }
        sendMailPO.setParamId(paramId);
        sendMailPO.setFlowDetailId(detailId);
        return sendMailPO;
    }


    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        System.out.println(calendar.get(calendar.YEAR));
        System.out.println(calendar.get(calendar.MONTH)+1);
        System.out.println(calendar.get(calendar.DAY_OF_MONTH));
        System.out.println(calendar.get(calendar.HOUR_OF_DAY)+24);
        System.out.println(calendar.get(calendar.MINUTE));
        System.out.println(calendar.get(calendar.SECOND));
    }
}
