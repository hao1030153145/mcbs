package com.transing.workflow.biz.service;

import com.jeeframework.testframework.AbstractSpringBaseTest;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.integration.bo.WorkFlowInfo;
import com.transing.workflow.util.WorkFlowJobProcesser;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 包: com.transing.dpmbs.biz.service
 * 源文件:WorkFlowServiceTest.java
 *
 * @author Sunny  Copyright 2016 成都创行, Inc. All rights reserved.2017年04月20日
 */
public class WorkFlowServiceTest extends AbstractSpringBaseTest
{
    @Resource
    private WorkFlowService workFlowService;
    @Test
    public void getWorkFlowInfoByWorkFlowId()
    {
        int flowId =1;

        WorkFlowInfo workInfo=null;

        workInfo = workFlowService.getWorkFlowInfoByWorkFlowId(flowId);

        System.out.println(workInfo);
    }

    @Test
    public void test(){
        new WorkFlowJobProcesser("fileOutput",601).call();
    }

    @Test
    public void getWorkFlowDetailByWorkFlowDetailId()
    {
        System.out.println(workFlowService.getWorkFlowDetailByWorkFlowDetailId(111));
    }

    @Test
    public void getWorkFlowDetailListByWorkFlowId()
    {
        System.out.println(workFlowService.getWorkFlowDetailListByWorkFlowId(1));
    }
}
