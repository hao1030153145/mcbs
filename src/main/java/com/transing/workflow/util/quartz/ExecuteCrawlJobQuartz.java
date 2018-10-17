package com.transing.workflow.util.quartz;

import com.alibaba.druid.support.json.JSONUtils;
import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.logicframework.biz.service.mq.producer.BaseKafkaProducer;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.integration.bo.WorkFlowListBO;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.transing.workflow.util.WorkFlowExecuteMethod;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 任务执行类
 *
 * @ClassName: ExecuteTopicJobQuartz
 *
 * @author summer
 */
public class ExecuteCrawlJobQuartz implements Job {

    public static final String logName="ExecuteCrawlJobQuartz";

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        JobDataMap jobDataMap = arg0.getJobDetail().getJobDataMap();
        WorkFlowExecuteMethod workFlowExecuteMethod = SpringContextHolder.getBean("workFlowExecuteMethod");
        WorkFlowService workFlowService=SpringContextHolder.getBean("workFlowService");
        ProjectService projectService=SpringContextHolder.getBean("projectService");
        WorkFlowDetail workFlowDetail = (WorkFlowDetail) jobDataMap.get("workFlowDetail");
        String batchNo=jobDataMap.getString("batchNo");
        //改变该工作流在数据库的状态
        workFlowService.updateWorkFlowDetailToRunningByWorkFlowDetailId(workFlowDetail.getFlowDetailId());
        Long workFlowId=workFlowDetail.getWorkFlowId();
        Long projectId=workFlowDetail.getProjectId();
        //判断是不是可视化项目
        //是普通项目
        if(workFlowId==0){
            //改变项目的状态
            ProjectStatusFilter projectStatusFilter=new ProjectStatusFilter();
            projectStatusFilter.setStatus(3);
            projectStatusFilter.setId(projectId);
            projectService.updateProjectStatus(projectStatusFilter);
        }else {
           // workFlowService.updateWorkFlowDetailByWorkFlowId(workFlowId);
            //改变工作流的状态
            WorkFlowListBO workFlowListBO=new WorkFlowListBO();
            workFlowListBO.setStatus(1);
            workFlowListBO.setWorkFlowId(workFlowId);
            workFlowService.updateWorkFlowListStatus(workFlowListBO);
            //再改变项目的状态
            ProjectStatusFilter projectStatusFilter=new ProjectStatusFilter();
            projectStatusFilter.setStatus(3);
            projectStatusFilter.setId(projectId);
            projectService.updateProjectStatus(projectStatusFilter);
        }
        WorkFlowParam workFlowParam = (WorkFlowParam) jobDataMap.get("workFlowParam");
        JobTypeInfo jobTypeInfo = (JobTypeInfo) jobDataMap.get("jobTypeInfo");

        LoggerUtil.debugTrace(logName,"begin process executeJob:projectId:"+workFlowDetail.getProjectId()+" typeNo:"+workFlowDetail.getTypeNo()+" time:"+System.currentTimeMillis());

        workFlowExecuteMethod.executeFirstJob(logName,jobTypeInfo,workFlowDetail,workFlowParam,workFlowDetail.getFlowDetailId(),batchNo,null);

        LoggerUtil.debugTrace(logName,"begin process executeJob:projectId:"+workFlowDetail.getProjectId()+" typeNo:"+jobTypeInfo.getTypeNo());

        //执行文件导出
        /*List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT,workFlowParam.getProjectId());

        if(null != workFlowParamList && workFlowParamList.size() > 0){
            for (WorkFlowParam fileOutWorkFlowParam:workFlowParamList) {
                //workFlowExecuteMethod.executeJob(logName+Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT,jobTypeMap.get(Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT),fileOutWorkFlowParam);
            }

        }*/

    }

}