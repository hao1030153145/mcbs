package com.transing.workflow.util.quartz;

import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.transing.dpmbs.util.QuartzManager;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by byron on 2018/4/11 0011.
 */
public class ExecuteStopJobQuartz implements Job {
    public static final String logName="ExecuteStopJobQuartz";
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        JobDataMap jobDataMap = arg0.getJobDetail().getJobDataMap();
        String workFlowDetail= (String)jobDataMap.get("jobName");
        String jobTime= (String)jobDataMap.get("jobTime");
        LoggerUtil.debugTrace(logName,"begin process executeJob");
        QuartzManager.removeJob(workFlowDetail);//删除启动的定时任务
        LoggerUtil.debugTrace(logName,"finish process executeJob");
        QuartzManager.removeJob(jobTime);//删除他本身

        //更新状态为停止
        WorkFlowService workFlowService = (WorkFlowService) jobDataMap.get("workFlowService");
        Long detailId = (Long) jobDataMap.get("detailId");
        workFlowService.updateWorkFlowDetailToStopByWorkFlowDetailId(detailId);

    }
}
