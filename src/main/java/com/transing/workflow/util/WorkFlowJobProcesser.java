package com.transing.workflow.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.QuartzManager;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.dpmbs.web.po.AnalysisHierarchy;
import com.transing.dpmbs.web.po.DatasourceTypePO;
import com.transing.dpmbs.web.po.SemanticAnalysisObjectPO;
import com.transing.dpmbs.web.po.SemanticJsonParam;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.transing.workflow.util.quartz.ExecuteTopicJobQuartz;
import net.sf.json.JSONArray;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * 包: com.transing.workflow.util
 * 源文件:WorkFlowJobProcesser.java
 *
 * @author Sunny  Copyright 2016 成都创行, Inc. All rights reserved.2017年04月20日
 */

public class WorkFlowJobProcesser implements Callable<String>
{

    public String typeNo = "";
    public long projectId = 0;
    public final String logName="WorkFlowJobProcesser typeNo="+typeNo+"-----projectId="+projectId;

    private JobTypeService jobTypeService = SpringContextHolder.getBean("jobTypeService");

    private WorkFlowService workFlowService = SpringContextHolder.getBean("workFlowService");

    private WorkFlowExecuteMethod workFlowExecuteMethod = SpringContextHolder.getBean("workFlowExecuteMethod");

    private DataSourceTypeService dataSourceTypeService = SpringContextHolder.getBean("dataSourceTypeService");

    private ProjectService projectService = SpringContextHolder.getBean("projectService");

    private ProjectJobTypeService projectJobTypeService = SpringContextHolder.getBean("projectJobTypeService");

    public WorkFlowJobProcesser (String typeNo,long projectId){
        this.typeNo = typeNo;
        this.projectId = projectId;
    }

    public void execute()
    {

        long begin=System.currentTimeMillis();
        long end=0;
        List<WorkFlowDetail> detailList = workFlowService.getExecDetailListByTypeNo(typeNo,projectId);

        if(!Validate.isEmpty(detailList)){
            Map<String,JobTypeInfo> jobTypeMap = new HashMap<>();
            List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();

            if(null != jobTypeInfoList && jobTypeInfoList.size() > 0){
                //聚合jobType 方便 下面根据typeNo 取出jobType对象
                for (JobTypeInfo jobTypeInfo :jobTypeInfoList) {
                    jobTypeMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
                }
            }

            for (WorkFlowDetail workFlowDetail:detailList) {

                LoggerUtil.debugTrace(this.getClass().getSimpleName(),"==================WorkFlowProcesser===typNo="+workFlowDetail.getTypeNo()+"==detailId="+workFlowDetail.getFlowDetailId()+"==startTime"+System.currentTimeMillis());

                try {
                    //查询detail 的处理数据，是否有数据需要处理
                    if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT)){

                        String resultParam = workFlowDetail.getResultParam();
                        if (Validate.isEmpty(resultParam)) {

                            String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                            String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                            JSONArray jsonArray = new JSONArray();
                            for (String prevFlowDetailId : prevFlowDetailIdArray) {
                                JSONObject jsonObject = new JSONObject();

                                long detailId = Long.parseLong(prevFlowDetailId);

                                WorkFlowDetail detail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailId);
                                String dataSourceType = detail.getDataSourceType();

                                jsonObject.put("detailId", Long.toString(detailId));
                                jsonObject.put("dataSourceType", dataSourceType);
                                jsonObject.put("lastIndexId", 0);

                                jsonArray.add(jsonObject);

                            }

                            resultParam = jsonArray.toString();

                            if(jsonArray.size() > 0){
                                WorkFlowDetail newWorkFLowDetail = new WorkFlowDetail();
                                newWorkFLowDetail.setFlowDetailId(workFlowDetail.getFlowDetailId());
                                newWorkFLowDetail.setResultParam(jsonArray.toString());
                                workFlowService.updateWorkFlowDetail(newWorkFLowDetail);
                            }

                        }

                        JSONArray jsonArray = JSONArray.fromObject(resultParam);
                        workFlowDetail.setResultParam(resultParam);

                        if (null != jsonArray && jsonArray.size() > 0) {
                            int finish = 0;
                            int stop = 0;
                            for (int i = 0; i < jsonArray.size(); i++) {
                                net.sf.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Object detailIdObj = jsonObject.get("detailId");
                                Object dataSourceTypeObj = jsonObject.get("dataSourceType");
                                Object lastIndexIdObj = jsonObject.get("lastIndexId");

                                DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(dataSourceTypeObj.toString()));

                                Map<String, String> postData = new HashMap<>();
                                postData.put("dataType", datasourceTypePO.getStorageTypeTable());
                                postData.put("filterJSON", "{\"projectID\":" + workFlowDetail.getProjectId() + ",\"detailId\":" + detailIdObj + "}");
                                postData.put("startRow", lastIndexIdObj.toString());
                                postData.put("rows", "1");

                                Object firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getDataInSearcher.json", "post", postData);
                                Object total = null;
                                if (null != firstObject) {
                                    net.sf.json.JSONObject jsonObj = (net.sf.json.JSONObject) firstObject;
                                    total = jsonObj.get("total");

                                    if (Long.parseLong(total.toString()) > 0) {
                                        JobTypeInfo jobTypeInfo = jobTypeMap.get(workFlowDetail.getTypeNo());

                                        workFlowExecuteMethod.executeJob(logName, jsonObject.toString(), jobTypeInfo, workFlowDetail);
                                    }else{
                                        WorkFlowDetail firstWorkFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(detailIdObj.toString()));
                                        int jobStatus = firstWorkFlowDetail.getJobStatus();
                                        if(jobStatus == 2){
                                            finish++;
                                        }else if(jobStatus == 4){
                                            stop++;
                                        }

                                    }

                                }

                            }

                            if(finish != 0 && finish >= jsonArray.size() ){//表示当前的detail 应该更新为完成状态
                                workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());

                                //判断整个项目是否完成
                                updateProjectFinish(workFlowDetail.getProjectId());

                            }

                            if(stop != 0 && stop >= jsonArray.size() ){//表示当前的detail 应该更新为停止状态
                                workFlowService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetail.getFlowDetailId());
                            }

                        }

                    } else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION)){

                        String resultParam = workFlowDetail.getResultParam();
                        if (Validate.isEmpty(resultParam)) {

                            String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                            String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                            JSONArray jsonArray = new JSONArray();
                            for (String prevFlowDetailId : prevFlowDetailIdArray) {  
                                long detailId = Long.parseLong(prevFlowDetailId);

                                WorkFlowDetail detail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailId);
                                String resultPara = detail.getResultParam();
                                if(Validate.isEmpty(resultPara)){//如果前一个节点的参数为空的话，就暂时跳过，等待前一个节点 更新上参数
                                    return;
                                }
                                JSONArray resultJsonArray = JSONArray.fromObject(resultPara);
                                for (int i = 0; i < resultJsonArray.size(); i++) {
                                    net.sf.json.JSONObject jsonObj = resultJsonArray.getJSONObject(i);
                                    Object detailIdObj = jsonObj.get("detailId");
                                    Object dataSourceTypeObj = jsonObj.get("dataSourceType");

                                    String hierarchy = "";
                                    for (int j = 1; j <= 3; j++) {
                                        hierarchy += getDataTypeByHierarchy(j)+",";
                                    }
                                    if(!Validate.isEmpty(hierarchy)){
                                        hierarchy = hierarchy.substring(0,hierarchy.length()-1);
                                    }

                                    JSONObject jsonObject = new JSONObject();

                                    jsonObject.put("detailId", detailIdObj);
                                    jsonObject.put("dataSourceType", dataSourceTypeObj);
                                    jsonObject.put("hierarchy", hierarchy);
                                    jsonObject.put("lastIndexId", 0);

                                    jsonArray.add(jsonObject);


                                }

                            }

                            resultParam = jsonArray.toString();

                            if(jsonArray.size() > 0){
                                WorkFlowDetail newWorkFLowDetail = new WorkFlowDetail();
                                newWorkFLowDetail.setFlowDetailId(workFlowDetail.getFlowDetailId());
                                newWorkFLowDetail.setResultParam(jsonArray.toString());
                                workFlowService.updateWorkFlowDetail(newWorkFLowDetail);
                            }

                        }

                        JSONArray jsonArray = JSONArray.fromObject(resultParam);
                        workFlowDetail.setResultParam(resultParam);

                        if (null != jsonArray && jsonArray.size() > 0) {
                            int finish = 0;
                            int stop = 0;
                            for (int i = 0; i < jsonArray.size(); i++) {
                                net.sf.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Object detailIdObj = jsonObject.get("detailId");
                                Object dataSourceTypeObj = jsonObject.get("dataSourceType");
                                Object hierarchy = jsonObject.get("hierarchy");
                                Object lastIndexIdObj = jsonObject.get("lastIndexId");

                                /*if(i != 0){//如果前一个层级还没做 则跳过 从低层级执行到高层级。执行了之后，后面的typeNo就可以执行了
                                    net.sf.json.JSONObject preJsonObject = jsonArray.getJSONObject(i-1);
                                    Object preLastIndexIdObj = preJsonObject.get("lastIndexId");
                                    long preLastIndexIdInt = Long.parseLong(preLastIndexIdObj.toString());
                                    if(preLastIndexIdInt <= 0){
                                        continue;
                                    }
                                }*/

                                String[] resultTypeArray = hierarchy.toString().split(",");
                                Map<String, String> postData = new HashMap<>();
                                postData.put("resultType", resultTypeArray[resultTypeArray.length-1]);
                                postData.put("filterJSON", "{\"projectID\":" + workFlowDetail.getProjectId() + ",\"detailId\":" + detailIdObj + ",\"dataSourceType\":"+dataSourceTypeObj.toString()+"}");
                                postData.put("startRow", lastIndexIdObj.toString());
                                postData.put("rows", "1");

                                Object firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getSemanticAnalysisDataList.json", "post", postData);
                                Object total = null;
                                if (null != firstObject) {
                                    net.sf.json.JSONObject jsonObj = (net.sf.json.JSONObject) firstObject;
                                    total = jsonObj.get("total");

                                    if (Long.parseLong(total.toString()) > 0) {
                                        JobTypeInfo jobTypeInfo = jobTypeMap.get(workFlowDetail.getTypeNo());

                                        workFlowExecuteMethod.executeJob(logName, jsonObject.toString(), jobTypeInfo, workFlowDetail);
                                    }else{
                                        WorkFlowDetail firstWorkFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(detailIdObj.toString()));
                                        int jobStatus = firstWorkFlowDetail.getJobStatus();
                                        if(jobStatus == 2){
                                            finish++;
                                        }else if(jobStatus == 4){
                                            stop ++;
                                        }

                                    }

                                }

                            }

                            if(finish != 0 && finish >= jsonArray.size() ){//表示当前的detail 应该更新为完成状态

                                String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                                String [] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                                int finishNum = 0;
                                for (String preDetailId:prevFlowDetailIdArray) {
                                    WorkFlowDetail prevDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(preDetailId));
                                    if(prevDetail.getJobStatus() == 2){
                                        finishNum ++ ;
                                    }
                                }

                                if(finishNum !=0 && finishNum >= prevFlowDetailIdArray.length){
                                    workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());

                                    //判断整个项目是否完成
                                    updateProjectFinish(workFlowDetail.getProjectId());
                                }

                            }

                            if(stop != 0 && stop >= jsonArray.size() ){//表示当前的detail 应该更新为停止状态
                                workFlowService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetail.getFlowDetailId());
                            }

                        }


                    }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING)){
                        String resultParam = workFlowDetail.getResultParam();
                        if (Validate.isEmpty(resultParam)) {

                            String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                            String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                            JSONArray jsonArray = new JSONArray();
                            for (String prevFlowDetailId : prevFlowDetailIdArray) {
                                long detailId = Long.parseLong(prevFlowDetailId);

                                WorkFlowDetail detail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailId);

                                String resultPara = detail.getResultParam();
                                if(Validate.isEmpty(resultPara)){//如果前一个节点的参数为空的话，就暂时跳过，等待前一个节点 更新上参数
                                    return;
                                }

                                JSONArray resultJsonArray = JSONArray.fromObject(resultPara);
                                Map<String, net.sf.json.JSONObject> detailJsonObjectMap = new HashMap<>();
                                for (int i = 0; i < resultJsonArray.size(); i++) {
                                    net.sf.json.JSONObject jsonObj = resultJsonArray.getJSONObject(i);
                                    Object detailIdObj = jsonObj.get("detailId");

                                    detailJsonObjectMap.put(detailIdObj.toString(),jsonObj);
                                }

                                Map<String, AnalysisHierarchy> hierarchyMap = getAnalysisHierarchy(workFlowDetail.getProjectId(),AnalysisHierarchy.ANALYSIS_HIERARCHY_TYPE_SUBJECT);
                                for (Map.Entry<String, net.sf.json.JSONObject> entry:detailJsonObjectMap.entrySet()) {
                                    String detailIdStr = entry.getKey();
                                    net.sf.json.JSONObject jsonObject = entry.getValue();
                                    Object dataSourceTypeObj = jsonObject.get("dataSourceType");

                                    AnalysisHierarchy analysisHierarchy = hierarchyMap.get(dataSourceTypeObj.toString());
                                    String hierarchy = analysisHierarchy.getHierarchy();
                                    if(!Validate.isEmpty(hierarchy)){
                                        String [] hierarchyArray = hierarchy.split(",");
                                        List<Integer> hierarchyList = new ArrayList<>();
                                        for (String hierarch:hierarchyArray) {
                                            hierarchyList.add(Integer.parseInt(hierarch));
                                        }
                                        Collections.sort(hierarchyList);

                                        String dataType = "";
                                        for (int hier:hierarchyList) {
                                            dataType += getDataTypeByHierarchy(hier)+",";
                                        }
                                        if(!Validate.isEmpty(dataType)){
                                            dataType = dataType.substring(0,dataType.length()-1);
                                        }

                                        net.sf.json.JSONObject jsonObj = new net.sf.json.JSONObject();
                                        jsonObj.put("detailId",detailIdStr);
                                        jsonObj.put("dataSourceType",dataSourceTypeObj.toString());
                                        jsonObj.put("hierarchy",dataType);
                                        jsonObj.put("lastIndexId",0);

                                        jsonArray.add(jsonObj);

                                    }

                                }

                            }

                            resultParam = jsonArray.toString();

                            if(jsonArray.size() > 0){
                                WorkFlowDetail newWorkFLowDetail = new WorkFlowDetail();
                                newWorkFLowDetail.setFlowDetailId(workFlowDetail.getFlowDetailId());
                                newWorkFLowDetail.setResultParam(jsonArray.toString());
                                workFlowService.updateWorkFlowDetail(newWorkFLowDetail);
                            }

                        }


                        JSONArray jsonArray = JSONArray.fromObject(resultParam);
                        workFlowDetail.setResultParam(resultParam);

                        if (null != jsonArray && jsonArray.size() > 0) {

                            Map<String,Map<String,Long>> segMap = new HashMap<>();
                            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION,workFlowDetail.getProjectId());
                            if(null != workFlowParamList && workFlowParamList.size() > 0){
                                WorkFlowParam workFlowParam = workFlowParamList.get(0);
                                WorkFlowDetail workFlowDetail1 = workFlowService.getWorkFlowDetailByWorkFlowDetailId(workFlowParam.getFlowDetailId());
                                if(null != workFlowDetail1){
                                    String resultPa = workFlowDetail1.getResultParam();
                                    if(!Validate.isEmpty(resultPa)){
                                        JSONArray resultJsonArray = JSONArray.fromObject(resultPa);
                                        for (int i = 0; i < resultJsonArray.size(); i++) {
                                            net.sf.json.JSONObject jsonObject = resultJsonArray.getJSONObject(i);

                                            String detailId = jsonObject.get("detailId").toString();
                                            String dataSourceType = jsonObject.get("dataSourceType").toString();
                                            String lastIndexId = jsonObject.get("lastIndexId").toString();

                                            Map<String,Long> segLastIndexDetail = new HashMap<>();
                                            segLastIndexDetail.put("lastIndexId",Long.parseLong(lastIndexId));
                                            segLastIndexDetail.put("detailId",workFlowDetail1.getFlowDetailId());

                                            segMap.put(detailId+"&"+dataSourceType,segLastIndexDetail);

                                        }
                                    }
                                }
                            }

                            if(Validate.isEmpty(segMap)){//如果为空则跳过
                                continue;
                            }

                            int finish = 0;
                            int stop = 0;
                            for (int i = 0; i < jsonArray.size(); i++) {
                                net.sf.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Object detailIdObj = jsonObject.get("detailId");
                                Object dataSourceTypeObj = jsonObject.get("dataSourceType");
                                Object hierarchy = jsonObject.get("hierarchy");
                                Object lastIndexIdObj = jsonObject.get("lastIndexId");
                                long lastIndexIdInt = Long.parseLong(lastIndexIdObj.toString());

                                /*if(i != 0){//如果前一个层级还没做 则跳过
                                    net.sf.json.JSONObject preJsonObject = jsonArray.getJSONObject(i-1);
                                    Object preLastIndexIdObj = preJsonObject.get("lastIndexId");
                                    long preLastIndexIdInt = Long.parseLong(preLastIndexIdObj.toString());
                                    if(preLastIndexIdInt <= 0){
                                        continue;
                                    }
                                }*/

                                Map<String,Long> segLastIndexDetail = segMap.get(detailIdObj+"&"+dataSourceTypeObj);
                                long segLastIndexId = segLastIndexDetail.get("lastIndexId");
                                long segDetailId = segLastIndexDetail.get("detailId");
                                if(segLastIndexId <= lastIndexIdInt){//如果分词 没有 处理 语义数据 则 不执行 主题分析。（主题分析 依赖 于 分词结果）
                                    WorkFlowDetail firstWorkFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(segDetailId);
                                    int jobStatus = firstWorkFlowDetail.getJobStatus();
                                    if(jobStatus == 2){
                                        finish++;
                                    }else if(jobStatus == 4){
                                        stop ++;
                                    }

                                    continue;
                                }

                                String[] resultTypeArray = hierarchy.toString().split(",");
                                Map<String, String> postData = new HashMap<>();
                                postData.put("resultType", resultTypeArray[resultTypeArray.length-1]);
                                postData.put("filterJSON", "{\"projectID\":" + workFlowDetail.getProjectId() + ",\"detailId\":" + detailIdObj + ",\"dataSourceType\":"+dataSourceTypeObj.toString()+"}");
                                postData.put("startRow", lastIndexIdObj.toString());
                                postData.put("rows", "1");

                                Object firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getSemanticAnalysisDataList.json", "post", postData);
                                Object total = null;
                                if (null != firstObject) {
                                    net.sf.json.JSONObject jsonObj = (net.sf.json.JSONObject) firstObject;
                                    total = jsonObj.get("total");

                                    if (Long.parseLong(total.toString()) > 0) {
                                        JobTypeInfo jobTypeInfo = jobTypeMap.get(workFlowDetail.getTypeNo());

                                        workFlowExecuteMethod.executeJob(logName, jsonObject.toString(), jobTypeInfo, workFlowDetail);
                                    }else{
                                        WorkFlowDetail firstWorkFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(detailIdObj.toString()));
                                        int jobStatus = firstWorkFlowDetail.getJobStatus();
                                        if(jobStatus == 2){
                                            finish++;
                                        }else if(jobStatus == 4){
                                            stop ++;
                                        }

                                    }

                                }

                            }

                            if(finish != 0 && finish >= jsonArray.size() ){//表示当前的detail 应该更新为完成状态
                                String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                                String [] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                                int finishNum = 0;
                                for (String preDetailId:prevFlowDetailIdArray) {
                                    WorkFlowDetail prevDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(preDetailId));
                                    if(prevDetail.getJobStatus() == 2){
                                        finishNum ++ ;
                                    }
                                }

                                if(finishNum !=0 && finishNum >= prevFlowDetailIdArray.length){
                                    workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());

                                    //判断整个项目是否完成
                                    updateProjectFinish(workFlowDetail.getProjectId());
                                }

                            }

                            if(stop != 0 && stop >= jsonArray.size() ){//表示当前的detail 应该更新为停止状态
                                workFlowService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetail.getFlowDetailId());
                            }

                        }

                    }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION)){

                        String quartzTime = workFlowDetail.getQuartzTime();
                        JobTypeInfo jobTypeInfo = jobTypeMap.get(workFlowDetail.getTypeNo());
                        if(!Validate.isEmpty(quartzTime)){
                            boolean existJob = QuartzManager.existJob(workFlowDetail.getTypeNo()+workFlowDetail.getFlowDetailId());
                            if(!existJob){
                                Map<String,Object> map = new HashMap<>();
                                map.put("jobTypeInfo",jobTypeInfo);
                                map.put("workFlowDetail",workFlowDetail);
                                map.put("workFlowService",workFlowService);
                                map.put("projectService",projectService);
                                map.put("projectJobTypeService",projectJobTypeService);

                                QuartzManager.addJob(workFlowDetail.getTypeNo()+workFlowDetail.getFlowDetailId(),ExecuteTopicJobQuartz.class,workFlowDetail.getQuartzTime(),map);
                            }
                        }else {
                            //判断所有数据节点都已完成了，才进行话题算法。

                            String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                            boolean isFinish = isAllFinish(prevFlowDetailIds);

                            if(isFinish){
                                int status = workFlowExecuteMethod.executeJob(logName, "", jobTypeInfo, workFlowDetail);//话题算法是全量数据不需要 数据参数
                                if(status == 3){//已执行
                                    workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());

                                    //判断整个项目是否完成
                                    updateProjectFinish(workFlowDetail.getProjectId());
                                }
                            }

                        }
                    }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT)){//如果是数据输出直接更新完成状态

                        String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                        boolean isFinish = isAllFinish(prevFlowDetailIds);

                        if(isFinish){

                            workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());

                            //判断整个项目是否完成
                            updateProjectFinish(workFlowDetail.getProjectId());

                        }
                    }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT)){

                        String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                        boolean isFinish = isAllFinish(prevFlowDetailIds);

                        if(isFinish){
                            int status = workFlowExecuteMethod.executeJob(logName, "", jobTypeMap.get(typeNo), workFlowDetail);//文件导出 是全量数据不需要 数据参数
                            if(status == 3){//已执行
                                workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());

                                //判断整个项目是否完成
                                updateProjectFinish(workFlowDetail.getProjectId());
                            }
                        }

                    }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_STATISTICAL)){

                        String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                        boolean isFinish = isAllFinish(prevFlowDetailIds);

                        if(isFinish){
                            int status = workFlowExecuteMethod.executeJob(logName, "", jobTypeMap.get(typeNo), workFlowDetail);
                            if(status == 3){//已执行
                                workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());

                                //判断整个项目是否完成
                                updateProjectFinish(workFlowDetail.getProjectId());
                            }
                        }

                    }

                    LoggerUtil.debugTrace(this.getClass().getSimpleName(),"==================WorkFlowSTart===typNo="+workFlowDetail.getTypeNo()+"===detailId="+workFlowDetail.getFlowDetailId()+"==endTime"+System.currentTimeMillis());

                }catch (Exception e){
                    e.printStackTrace();
                    LoggerUtil.debugTrace(logName,e.toString());
                    updateToStop(workFlowDetail,e.getMessage());
                }

                /*//根据detail 查询 param
                JobTypeInfo jobTypeInfo = jobTypeMap.get(workFlowDetail.getTypeNo());

                if(null != workFlowDetail.getQuartzTime() && !"".equals(workFlowDetail.getQuartzTime())){//如果 是循环任务则 加入 quartz循环 执行
                    *//*Map<String,Object> map = new HashMap<>();
                    map.put("jobTypeInfo",jobTypeInfo);
                    map.put("workFlowParam",workFlowParam);
                    map.put("workFlowService",workFlowService);
                    map.put("jobTypeMap",jobTypeMap);
                    QuartzManager.addJob(jobTypeInfo.getTypeNo()+workFlowDetail.getFlowDetailId(), ExecuteTopicJobQuartz.class,workFlowDetail.getQuartzTime(),map);*//*

                }else{//如果 不为循环任务 则 直接执行
                    workFlowExecuteMethod.executeJob(logName,jobTypeInfo,workFlowDetail);
                }*/


            }
        }

        /*List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();

        if(null != jobTypeInfoList && jobTypeInfoList.size() > 0){

            //聚合jobType 方便 下面根据typeNo 取出jobType对象
            Map<String,JobTypeInfo> jobTypeMap = new HashMap<>();
            for (JobTypeInfo jobTypeInfo :jobTypeInfoList) {
                jobTypeMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
            }

            //1,查询出所有未处理和处理中的流程
            List<WorkFlowInfo>  list =  workFlowService.getAllNeedProcessWorkFlowInfo();

            if(list!=null&&list.size()>0)
            {
                // 2 遍历所有需要处理的工作流进行处理
                for (WorkFlowInfo workFlowInfo:list)
                {
                    LoggerUtil.debugTrace(logName,"begin process workFlowInfo:projectId:"+workFlowInfo.getProjectId()+" workFlowId:"+workFlowInfo.getFlowId());

                    long flowId = workFlowInfo.getFlowId();
                    int flowResult = workFlowService.updateWorkFlowInfoToStart(flowId);//更新 workFlowInfo 为执行状态
                    if(flowResult > 0){//如果状态成功更新为执行状态才执行下面的代码

                        List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByWorkFlowId(flowId);
                        if(null != workFlowDetailList && workFlowDetailList.size() > 0){
                            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {

                                LoggerUtil.debugTrace(logName,"begin process workFlowDetail:projectId:"+workFlowInfo.getProjectId()+" workFlowDetailId:"+workFlowDetail.getFlowDetailId());

                                int flowDetailResult = workFlowService.updateWorkFlowDetailToStart(workFlowDetail.getFlowDetailId());//则更改状态为启动

                                if(flowDetailResult > 0 ){//状态 成功更新为 启动 才执行下面的代码
                                    //根据detail 查询 param
                                    WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(workFlowDetail.getFlowDetailId());

                                    JobTypeInfo jobTypeInfo = jobTypeMap.get(workFlowParam.getTypeNo());

                                    if(null != workFlowDetail.getQuartzTime() && !"".equals(workFlowDetail.getQuartzTime())){//如果 是循环任务则 加入 quartz循环 执行
                                        Map<String,Object> map = new HashMap<>();
                                        map.put("jobTypeInfo",jobTypeInfo);
                                        map.put("workFlowParam",workFlowParam);
                                        map.put("workFlowService",workFlowService);
                                        map.put("jobTypeMap",jobTypeMap);
                                        QuartzManager.addJob(jobTypeInfo.getTypeNo()+workFlowDetail.getFlowDetailId(), ExecuteTopicJobQuartz.class,workFlowDetail.getQuartzTime(),map);

                                    }else{//如果 不为循环任务 则 直接执行
                                        //workFlowExecuteMethod.executeJob(logName,jobTypeInfo,workFlowParam);
                                    }

                                }
                            }
                        }
                    }

                }
            }
        }*/

        //3,
    }

    @Override
    public String call() {

        LoggerUtil.debugTrace(logName,"=============================start Exec typeNo="+typeNo);

        boolean isEnd = false;
        String returnStr = "";
        while (!isEnd){
            try {

                execute();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<String> typeNoList = new ArrayList<>();
                typeNoList.add(typeNo);
                List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,projectId);

                for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                    if(workFlowDetail.getJobStatus() == 2){
                        isEnd = true;
                        break;
                    }else if(workFlowDetail.getJobStatus() == 9){
                        isEnd = true;
                        returnStr = "faild";
                        break;
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }


        return returnStr;
    }

    private Map<String, AnalysisHierarchy> getAnalysisHierarchy(long projectId,int hierarchyType) {

        WorkFlowParam workFlowParamFilter = new WorkFlowParam();
        workFlowParamFilter.setProjectId(projectId);
        workFlowParamFilter.setParamType(WorkFlowParam.PARAM_TYPE_COMON);
        List<WorkFlowParam> publicWorkFlowParamBOList = workFlowService.getWorkFlowParamByFlowParam(workFlowParamFilter);

        Map<String, AnalysisHierarchy> dataSourceTypeAnalysisHierarchyMap = new HashMap<>();
        String semanticAnalysisObjectJSONString = "";//语义分析对象定义
        if (!Validate.isEmpty(publicWorkFlowParamBOList)) {
            for (WorkFlowParam workFlowParamBO : publicWorkFlowParamBOList) {
                String typeNo = workFlowParamBO.getTypeNo();

                if (Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT.equals(typeNo)) {
                    semanticAnalysisObjectJSONString = workFlowParamBO.getJsonParam();

                    if (!Validate.isEmpty(semanticAnalysisObjectJSONString)) {
                        SemanticAnalysisObjectPO semanticAnalysisObject = JSON.parseObject
                                (semanticAnalysisObjectJSONString,
                                        SemanticAnalysisObjectPO.class);

                        SemanticJsonParam semanticJsonParam = semanticAnalysisObject.getJsonParam();

                        long dataSourceTypeIdInt = semanticJsonParam.getDataSourceTypeId();

                        List<AnalysisHierarchy> analysisHierarchyList = semanticJsonParam.getAnalysisHierarchy();

                        for (AnalysisHierarchy analysisHierarchyTmp : analysisHierarchyList) {
                            int analysisHierarchyType = analysisHierarchyTmp.getType();
                            if (analysisHierarchyType == hierarchyType) {
                                dataSourceTypeAnalysisHierarchyMap.put(Long.toString(dataSourceTypeIdInt), analysisHierarchyTmp);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return dataSourceTypeAnalysisHierarchyMap;
    }

    /**
     * 根据语义分析层级数字获取数据层级类型标识
     *
     * @param hierarchyInt 分析层级
     * @return
     */
    private String getDataTypeByHierarchy(int hierarchyInt) {
        String dataType = null;
        switch (hierarchyInt) {
            case AnalysisHierarchy.ANALYSIS_HIERARCHY_SENTENCE:
                dataType = "sentence";
                break;
            case AnalysisHierarchy.ANALYSIS_HIERARCHY_SECTION:
                dataType = "section";
                break;
            case AnalysisHierarchy.ANALYSIS_HIERARCHY_ARTICLE:
                dataType = "article";
                break;
        }
        return dataType;
    }

    private void updateToStop(WorkFlowDetail workFlowDetail,String message){
        workFlowService.updateWorkFlowDetailToExceptionByWorkFlowDetailId(workFlowDetail.getFlowDetailId(),message.toString());

        //停止后面的工作流 执行
        updateNextDetailToStop(workFlowDetail);

        ProjectStatusFilter filter = new ProjectStatusFilter();
        filter.setId(workFlowDetail.getProjectId());
        filter.setStatus(9);
        projectService.updateProjectStatus(filter);
    }

    private boolean updateNextDetailToStop(WorkFlowDetail workFlowDetail){

        String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
        if(!Validate.isEmpty(nextFlowDetailIds)){
            String [] nextDetailArray = nextFlowDetailIds.split(",");
            for (String detailIdStr:nextDetailArray) {
                long detail = Long.parseLong(detailIdStr);
                WorkFlowDetail workFlowDetail2 = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detail);

                workFlowService.updateWorkFlowDetailToStopByWorkFlowDetailId(detail);//把找到的更新为停止
                //递归调用 停止他的下一个节点
                updateNextDetailToStop(workFlowDetail2);
            }
        }

        return true;
    }

    public boolean updateProjectFinish(long projectId){
        //判断整个项目是否完成
        List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByTypeNoList(null,projectId);

        int finishNum = 0;
        for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
            int status = workFlowDetail.getJobStatus();
            if(status == 2){
                finishNum ++ ;
            }else {
                break;
            }
        }

        if (finishNum >= workFlowDetailList.size()){
            ProjectStatusFilter filter = new ProjectStatusFilter();
            filter.setId(projectId);
            filter.setStatus(5);
            projectService.updateProjectStatus(filter);
        }
        return true;
    }

    private boolean isAllFinish(String preDetailids){

        boolean isFinish = true;
        if(!Validate.isEmpty(preDetailids) && !"0".equals(preDetailids)){
            String [] prevFlowDetailIdArray = preDetailids.split(",");
            for (String preDetailId:prevFlowDetailIdArray) {
                WorkFlowDetail prevDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(preDetailId));
                if(prevDetail.getJobStatus() != 2){
                    isFinish = false;
                    break;
                }
                isFinish = isAllFinish(prevDetail.getPrevFlowDetailIds());
            }
        }

        return isFinish;
    }

    /*@Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
            while (true){

            }
        }
    }*/
}