package com.transing.dpmbs.util;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.DataCrawlPO;
import com.transing.dpmbs.web.po.DatasourcePO;
import com.transing.dpmbs.web.po.DatasourceTypePO;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;
import com.transing.workflow.util.quartz.ExecuteCrawlJobQuartz;
import com.transing.workflow.util.quartz.ExecuteGetCountJobQuartz;
import com.transing.workflow.util.quartz.ExecuteStopJobQuartz;
import com.transing.workflow.util.quartz.ExecuteTopicJobQuartz;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.avro.generic.GenericData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

@Component("quartzRecover")
public class QuartzRecover implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private JobTypeService jobTypeService;

    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if ( event.getApplicationContext (). getParent() == null) {

            SpringContextHolder.setApplicationContextByStatic(event.getApplicationContext());

            List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();
            Map<String,JobTypeInfo> jobTypeInfoMap = new HashMap<>();
            for (JobTypeInfo jobTypeInfo:jobTypeInfoList) {
                jobTypeInfoMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
            }
            List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailByStatus(WorkFlowDetail.JOB_STATUS_RUNING);
            if(Validate.isEmpty(workFlowDetailList)){
                workFlowDetailList = new ArrayList<>();
            }

            List<WorkFlowDetail> workFlowDetailList2 = workFlowService.getWorkFlowDetailByStatus(WorkFlowDetail.JOB_STATUS_COMPLETE);
            if(!Validate.isEmpty(workFlowDetailList2)){
                workFlowDetailList.addAll(workFlowDetailList2);
            }

            List<WorkFlowDetail> workFlowDetailList3 = workFlowService.getWorkFlowDetailByStatus(WorkFlowDetail.JOB_STATUS_EXCEPTION);
            if(!Validate.isEmpty(workFlowDetailList3)){
                workFlowDetailList.addAll(workFlowDetailList3);
            }
            LoggerUtil.debugTrace("正在运行的workFlowDetailList==="+workFlowDetailList.size()+",已完成的workFlowDetailList==="+workFlowDetailList2.size()+",异常的workFlowDetailList==="+workFlowDetailList3.size());

            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                Long projectId=workFlowDetail.getProjectId();
                String quartzTime = workFlowDetail.getQuartzTime();
                if(Validate.isEmpty(quartzTime)){
                    continue;
                }

                String typeNo = workFlowDetail.getTypeNo();
                JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(typeNo);

                if(JobTypeInfo.TYPE_CLASSIFY_LOOP == jobTypeInfo.getTypeClassify()){//如果为循环节点
                    WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(workFlowDetail.getFlowDetailId());

                    if(null == workFlowParam){

                        workFlowParam = new WorkFlowParam();

                        List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowService.getWorkFlowNodeParamByFlowDetailId(workFlowDetail.getFlowDetailId());

                        if(Validate.isEmpty(workFlowNodeParamBoList)){
                            continue;
                        }

                        List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowService.getWorkFlowInputParamBoList();
                        Map<Integer,String> inputParamMap = new HashMap<>();
                        for (WorkFlowInputParamBo workFlowInputParamBo:workFlowInputParamBoList) {
                            inputParamMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo.getParamEnName());
                        }

                        JSONObject jsonObject = new JSONObject();
                        for (WorkFlowNodeParamBo workFlowNodeParamBo:workFlowNodeParamBoList) {
                            int inputParamId = workFlowNodeParamBo.getInputParamId();
                            String paramEnName = inputParamMap.get(inputParamId);
                            String paramValue = workFlowNodeParamBo.getInputParamValue();
                            if(Validate.isEmpty(paramValue)){
                                paramValue = "";
                            }

                            jsonObject.put(paramEnName,paramValue);

                        }

                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(workFlowDetail.getTypeNo())
                                ||Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(workFlowDetail.getTypeNo())){
                            String crawlType = jsonObject.getString("crawlType");

                            if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                                crawlType = "1";
                                workFlowDetail.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
                                workFlowParam.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
                            }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                                crawlType = "2";
                                workFlowDetail.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);
                                workFlowParam.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);
                            }

                            Object datasourceIdObj = jsonObject.get("datasourceId");
                            Object datasourceTypeId = jsonObject.get("datasourceTypeId");

                            DatasourcePO datasourcePO = dataSourceTypeService.getDatasourceById(Long.parseLong(datasourceIdObj.toString()),crawlType);
                            DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(datasourceTypeId.toString()));

                            jsonObject.put("datasourceName",datasourcePO.getDatasourceName());
                            jsonObject.put("datasourceTypeName",datasourceTypePO.getTypeName());

                            net.sf.json.JSONObject jsonParam = new net.sf.json.JSONObject();

                            jsonObject.put("taskName","抓取"+datasourceTypePO.getTypeName());

                            jsonParam.put("jsonParam",jsonObject);


                            workFlowParam.setTypeNo(workFlowDetail.getTypeNo());

                            workFlowParam.setFlowId(workFlowDetail.getFlowId());
                            workFlowParam.setParamType(WorkFlowParam.PARAM_TYPE_PRIVATE);
                            workFlowParam.setFlowDetailId(workFlowDetail.getFlowDetailId());
                            workFlowParam.setWorkFlowId(workFlowDetail.getWorkFlowId());
                            workFlowParam.setTypeNo(workFlowDetail.getTypeNo());
                            workFlowParam.setProjectId(workFlowDetail.getProjectId());
                            workFlowParam.setJsonParam(jsonParam.toString());
                        }else if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(workFlowDetail.getTypeNo())){

                            workFlowParam = new WorkFlowParam();

                            workFlowParam.setTypeNo(workFlowDetail.getTypeNo());

                            workFlowParam.setFlowId(workFlowDetail.getFlowId());
                            workFlowParam.setParamType(WorkFlowParam.PARAM_TYPE_PRIVATE);
                            workFlowParam.setFlowDetailId(workFlowDetail.getFlowDetailId());
                            workFlowParam.setTypeNo(workFlowDetail.getTypeNo());
                            workFlowParam.setWorkFlowId(workFlowDetail.getWorkFlowId());
                            workFlowParam.setProjectId(workFlowDetail.getProjectId());
                            workFlowParam.setJsonParam(jsonObject.toString());
                        }

                    }else{
                        workFlowParam.setWorkFlowId(0l);
                    }


                    if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(workFlowParam.getTypeNo())
                            ||Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(workFlowParam.getTypeNo())){

                        //如果抓取不是首节点 则跳过
                        if(!workFlowDetail.getPrevFlowDetailIds().equals("0")){
                            continue;
                        }

                        DataCrawlPO dataCrawlPO = JSON.parseObject(workFlowParam.getJsonParam(),DataCrawlPO.class);


                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(workFlowParam.getTypeNo())){
                            //兼容 把 传入的参数变为 array
                            JSONArray jsonArray = dataCrawlPO.getJsonParam().getInputParamArray();
                            JSONArray inputArray = new JSONArray();
                            inputArray.add(jsonArray);
                            dataCrawlPO.getJsonParam().setInputParamArray(inputArray);
                            workFlowParam.setJsonParam(JSON.toJSONString(dataCrawlPO));

                        }

                        Long sustainTime = dataCrawlPO.getJsonParam().getSustainTime();
                        String batchNo=dataCrawlPO.getBatchNo();
                        if(null != sustainTime && sustainTime > 0){
                            Date jobBeginTime = workFlowDetail.getJobBeginTime();
                            Long time = jobBeginTime.getTime()+sustainTime*1000;
                            LoggerUtil.debugTrace("projectId:="+projectId+"    detailId:="+workFlowDetail.getFlowDetailId()+"     time:="+time+"     new Date().getTime():="+new Date().getTime());
                            if(new Date().getTime() < time){//如果持续的结束时间大于当前时间则需要添加停止

                                Map<String, Object> map = new HashMap<>();
                                map.put("jobTypeInfo", jobTypeInfo);
                                map.put("workFlowDetail", workFlowDetail);
                                map.put("workFlowParam", workFlowParam);
                                map.put("batchNo",batchNo);
                                map.put("projectId",projectId);
                                LoggerUtil.debugTrace("====batchNo:="+batchNo+"    projectId:="+projectId);
                                String jobs=projectId+"_"+batchNo+System.currentTimeMillis();
                                String quartzTimes=0+" "+0+"/"+10+" * * * ?";

                                try {
                                    if (batchNo!=null){
                                        QuartzManager.removeJob(jobs+"fenxi");
                                        QuartzManager.addJob(jobs+"fenxi",ExecuteGetCountJobQuartz.class,quartzTimes,map);
                                    }
                                    QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                                    QuartzManager.addJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId(), ExecuteCrawlJobQuartz.class, workFlowDetail.getQuartzTime(), map);
                                }catch (RuntimeException e){
                                    e.printStackTrace();
                                    continue;
                                }


                                Date endTime = new Date(time);

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(endTime);

                                int year = calendar.get(calendar.YEAR);
                                int month = calendar.get(calendar.MONTH)+1;
                                int date = calendar.get(calendar.DAY_OF_MONTH);
                                int hour = calendar.get(calendar.HOUR_OF_DAY);
                                String sustainTimeStr = "0 0 "+hour+" "+date+" "+month+" ? "+year;

                                Map<String, Object> jobMap = new HashMap<>();
                                jobMap.put("jobName", workFlowParam.getTypeNo()+workFlowParam.getFlowDetailId());
                                jobMap.put("workFlowService", workFlowService);
                                jobMap.put("detailId", workFlowParam.getFlowDetailId());
                                String jobTime = workFlowParam.getProjectId()+"_"+System.currentTimeMillis();
                                jobMap.put("jobTime",jobTime);
                                if(batchNo!=null){
                                    //移除分析的定时任务
                                    Map<String, Object> jobMap1 = new HashMap<>();
                                    jobMap1.put("jobName",jobs+"fenxi");
                                    jobMap1.put("jobTime",jobTime+"stop");
                                    QuartzManager.addJob(jobTime+"stop",ExecuteStopJobQuartz.class,sustainTimeStr,jobMap1);
                                }
                                QuartzManager.addJob(jobTime,ExecuteStopJobQuartz.class,sustainTimeStr,jobMap);

                            }
                        }else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("jobTypeInfo", jobTypeInfo);
                            map.put("workFlowDetail", workFlowDetail);
                            map.put("workFlowParam", workFlowParam);
                            try{
                                QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                                QuartzManager.addJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId(), ExecuteCrawlJobQuartz.class, workFlowDetail.getQuartzTime(), map);
                            }catch (RuntimeException e){
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }else if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(workFlowParam.getTypeNo())){
                        Map<String, Object> map = new HashMap<>();
                        map.put("jobTypeInfo",jobTypeInfo);
                        map.put("workFlowDetail",workFlowDetail);
                        map.put("workFlowService",workFlowService);
                        map.put("projectService",projectService);
                        map.put("projectJobTypeService",projectJobTypeService);
                        try {
                            QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                            QuartzManager.addJob(workFlowDetail.getTypeNo()+workFlowDetail.getFlowDetailId(),ExecuteTopicJobQuartz.class,workFlowDetail.getQuartzTime(),map);
                        }catch (RuntimeException e){
                            e.printStackTrace();
                            continue;
                        }

                    }

                }


            }

            System.out.println("quartzEnd============="+System.currentTimeMillis());


        }

    }
}
