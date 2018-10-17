package com.transing.workflow.util;

import com.jeeframework.core.context.support.SpringContextHolder;
import com.transing.dpmbs.integration.bo.WorkFlowListBO;
import com.transing.workflow.biz.service.WorkFlowService;

import java.util.List;

/**
 * Created by byron on 2018/4/27 0027.
 */
public class VisWorkFlowProjectStart implements Runnable{

    private WorkFlowService workFlowService = SpringContextHolder.getBean("workFlowService");

    private List<WorkFlowListBO> workFlowListBOS;
    public VisWorkFlowProjectStart(List<WorkFlowListBO> workFlowListBOS) {
        this.workFlowListBOS = workFlowListBOS;
    }

    @Override
    public void run() {
        for(WorkFlowListBO workFlowListBO : workFlowListBOS){
            workFlowService.startVisWorkFlow(workFlowListBO.getWorkFlowId(),null,null);
        }
    }
}
