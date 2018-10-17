package com.transing.workflow.util;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.logicframework.biz.service.mq.producer.BaseKafkaProducer;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.integration.bo.ProjectOne;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.QuartzManager;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.po.DataCrawlPO;
import com.transing.dpmbs.web.po.DatasourcePO;
import com.transing.dpmbs.web.po.DatasourceTypePO;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;
import com.transing.workflow.util.quartz.ExecuteCrawlJobQuartz;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Administrator on 2017/7/12.
 */
public class WorkFlowStart implements Runnable{

    private JobTypeService jobTypeService = SpringContextHolder.getBean("jobTypeService");

    private WorkFlowService workFlowService = SpringContextHolder.getBean("workFlowService");

    private WorkFlowExecuteMethod workFlowExecuteMethod = SpringContextHolder.getBean("workFlowExecuteMethod");

    private ProjectJobTypeService projectJobTypeService = SpringContextHolder.getBean("projectJobTypeService");

    private ProjectService projectService = SpringContextHolder.getBean("projectService");

    private DataSourceTypeService dataSourceTypeService = SpringContextHolder.getBean("dataSourceTypeService");

    public long projectId = 0;

    private List<WorkFlowDetail> firstDetailList;
    private String batchNo;
    public WorkFlowStart(long projectId){
        this.projectId = projectId;
    }

    public WorkFlowStart(long projectId, List<WorkFlowDetail> firstDetailList,String batchNo) {
        this.projectId = projectId;
        this.firstDetailList = firstDetailList;
        this.batchNo=batchNo;
    }

    @Override
    public void run() {
        LoggerUtil.debugTrace(this.getClass().getSimpleName(),"==================WorkFlowSTart====startTime"+System.currentTimeMillis());

        if(Validate.isEmpty(firstDetailList)){
            firstDetailList =  workFlowService.getFirstDetailByProjectId(projectId);//根据项目id获取流程表的首节点
        }
        Map<String,JobTypeInfo> jobTypeInfoMap = new HashMap();
        List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();//查询所有有效的节点

        if(null != jobTypeInfoList && jobTypeInfoList.size() > 0){
            //聚合jobType 方便 下面根据typeNo 取出jobType对象
            for (JobTypeInfo jobTypeInfo :jobTypeInfoList) {
                jobTypeInfoMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
            }
        }

        if(!Validate.isEmpty(firstDetailList)) {
            for (WorkFlowDetail workFlowDetail : firstDetailList) {
                if(workFlowDetail.getJobStatus() != 2){//判断该首节点是否为已完成
                    String typeNo = workFlowDetail.getTypeNo();
                    JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(typeNo);//判断该节点的编号是存在

                    //根据节点的流程id获取该节点的工作流参数信息  注意现在该线程不需判断是否是可视化项目了。因为可视化项目的线程为另外一个了
                    WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(workFlowDetail.getFlowDetailId());
                    workFlowParam.setWorkFlowId(0L);

                    if (typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)) {
                        String quartzTime = workFlowDetail.getQuartzTime();
                        if (!Validate.isEmpty(quartzTime)) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("jobTypeInfo", jobTypeInfo);
                            map.put("workFlowDetail", workFlowDetail);
                            map.put("workFlowParam", workFlowParam);
                            map.put("batchNo",batchNo);
                            try {
                                QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            QuartzManager.addJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId(), ExecuteCrawlJobQuartz.class, workFlowDetail.getQuartzTime(), map);
                        } else {
                            workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(), jobTypeInfo, workFlowDetail,workFlowParam,workFlowDetail.getFlowDetailId(),batchNo,null);
                        }
                    } else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){

                        String quartzTime = workFlowDetail.getQuartzTime();

                        //兼容 把 传入的参数变为 array
                        DataCrawlPO dataCrawlPO = JSON.parseObject(workFlowParam.getJsonParam(),DataCrawlPO.class);
                        JSONArray jsonArray = dataCrawlPO.getJsonParam().getInputParamArray();
                        JSONArray inputArray = new JSONArray();
                        inputArray.add(jsonArray);
                        dataCrawlPO.getJsonParam().setInputParamArray(inputArray);
                        workFlowParam.setJsonParam(JSON.toJSONString(dataCrawlPO));

                        if (!Validate.isEmpty(quartzTime)) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("jobTypeInfo", jobTypeInfo);
                            map.put("workFlowDetail", workFlowDetail);
                            map.put("workFlowParam", workFlowParam);
                            map.put("batchNo",batchNo);
                            try {
                                QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            QuartzManager.addJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId(), ExecuteCrawlJobQuartz.class, workFlowDetail.getQuartzTime(), map);
                        } else {
                           workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(), jobTypeInfo, workFlowDetail,workFlowParam,workFlowDetail.getFlowDetailId(),batchNo,null);
                        }

                    }else if (typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)) {
                        workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(), jobTypeInfo, workFlowDetail,workFlowParam,workFlowDetail.getFlowDetailId(),batchNo,null);
                    }

                    LoggerUtil.debugTrace(this.getClass().getSimpleName(),"==================WorkFlowSTart==detailId="+workFlowDetail.getFlowDetailId()+"==endTime"+System.currentTimeMillis());
                }
            }
        }

        /*List<ProjectJobTypeBO> projectJobTypeBOList = projectJobTypeService.getProjectJobTypeListByProjectId(projectId);
        List<Future<String>> results = new ArrayList<Future<String>>();
        ExecutorService es = Executors.newCachedThreadPool();

        if(!Validate.isEmpty(projectJobTypeBOList)){
            for (ProjectJobTypeBO projectJobTypeBO:projectJobTypeBOList) {
                String typeNo = projectJobTypeBO.getTypeNo();
                JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(typeNo);
                if(null != jobTypeInfo){
                    if(jobTypeInfo.getJobClassify() == 2){//状态节点才启动线程来执行
                        results.add(es.submit(new WorkFlowJobProcesser(typeNo,projectId)));
                    }
                }

            }
        }*/

        //恢复流程节点
        String dpmssServerByEnv = WebUtil.getDpmssServerByEnv();
        Map<String,String> postData = new HashMap<>();
        postData.put("projectId",Long.toString(projectId));
        CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),dpmssServerByEnv+"/restoreErrorTask.json","post",postData);

        /*try {

            es.shutdown();

            boolean isError = false;//判断是否出现错误
            String errorMessage = null;
            while (true){//循环判断线程
                int finishNum = 0;
                for (Future<String> future:results) {
                    if(isError){
                        future.cancel(true);
                        continue;
                    }
                    String message = future.get();
                    if(!Validate.isEmpty(message)){//如果message 不为空则表示 出现错误
                        isError = true;//停止其他正在执行的线程
                        future.cancel(true);
                        errorMessage = message;
                    }
                    if(future.isDone()){
                        finishNum++;
                    }
                }

                if(!Validate.isEmpty(errorMessage)){//如果有错误，则抛出第一个线程抛出的错误。
                    throw new Exception(errorMessage);
                }

                if(finishNum >= results.size()){
                    break;
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }*/


    }
}
