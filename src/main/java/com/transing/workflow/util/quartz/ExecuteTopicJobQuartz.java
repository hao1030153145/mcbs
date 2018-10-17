package com.transing.workflow.util.quartz;

import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowInfo;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.transing.workflow.util.WorkFlowExecuteMethod;
import com.transing.workflow.util.WorkFlowJobProcesser;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

/**
 * @Description: 任务执行类
 *
 * @ClassName: ExecuteTopicJobQuartz
 *
 * @author summer
 */
public class ExecuteTopicJobQuartz implements Job {

    public static final String logName="ExecuteTopicJobQuartz";

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        JobDataMap jobDataMap = arg0.getJobDetail().getJobDataMap();

        WorkFlowExecuteMethod workFlowExecuteMethod = SpringContextHolder.getBean("workFlowExecuteMethod");
        WorkFlowDetail workFlowDetail = (WorkFlowDetail) jobDataMap.get("workFlowDetail");
        WorkFlowService  workFlowService = ( WorkFlowService ) jobDataMap.get("workFlowService");
        ProjectService projectService = ( ProjectService ) jobDataMap.get("projectService");
        JobTypeInfo jobTypeInfo = (JobTypeInfo) jobDataMap.get("jobTypeInfo");

        LoggerUtil.debugTrace(logName,"begin process executeJob:projectId:"+workFlowDetail.getProjectId()+" typeNo:"+workFlowDetail.getTypeNo()+" time:"+System.currentTimeMillis());

        int execStatus = workFlowExecuteMethod.executeJob(logName,"",jobTypeInfo,workFlowDetail);
        if(execStatus == 3){//已执行
            workFlowService.updateWorkFlowDetailToFinish(workFlowDetail.getFlowDetailId());
            workFlowService.updateWorkFlowInfoComNum(workFlowDetail.getFlowId());
            workFlowService.updateWorkFlowInfoToFinishIfComNum(workFlowDetail.getFlowId());

            //判断整个项目是否完成
            long projectId = workFlowDetail.getProjectId();

            //判断整个项目是否完成
            List<WorkFlowInfo> workFlowInfoList = workFlowService.getWorkFlowInfoByProjectId(projectId);
            int finishNum = 0;
            for (WorkFlowInfo workFlowInfo:workFlowInfoList) {
                int status = workFlowInfo.getStatus();
                if(status == 2){
                    finishNum ++ ;
                }else {
                    break;
                }
            }

            if (finishNum >= workFlowInfoList.size()){
                ProjectStatusFilter filter = new ProjectStatusFilter();
                filter.setId(projectId);
                filter.setStatus(5);
                projectService.updateProjectStatus(filter);
            }
        }

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