package com.transing.workflow.util;

import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.util.validate.Validate;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowInfo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */

@Component("workFlowExec")
public class WorkFlowExec implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        SpringContextHolder.setApplicationContextByStatic(applicationContext);

        /*//第一次启动执行 工作流线程前，去把以前正在执行的节点，再次启动，等待执行。
        WorkFlowService workFlowService = applicationContext.getBean(WorkFlowService.class);
        List<WorkFlowInfo> workFlowInfoList = workFlowService.getRunningWorkFlowInfo();
        if(!Validate.isEmpty(workFlowInfoList)){
            for (WorkFlowInfo workFlowInfo:workFlowInfoList) {
                long flowId = workFlowInfo.getFlowId();
                List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByWorkFlowId(flowId);
                if(!Validate.isEmpty(workFlowDetailList)){
                    for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                        int jobStatus = workFlowDetail.getJobStatus();
                        if(jobStatus == 1){
                            workFlowService.updateWorkFlowDetailToInitByWorkFlowDetailId(workFlowDetail.getFlowDetailId());//更新 工作流任务 为等待执行
                        }
                    }
                }
                workFlowService.updateWorkFlowInfoToInitByFlowId(flowId);//更新工作流节点 状态为等待执行
            }
        }*/

    }
}