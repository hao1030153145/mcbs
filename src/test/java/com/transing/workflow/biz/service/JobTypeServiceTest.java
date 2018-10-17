package com.transing.workflow.biz.service;

import com.jeeframework.testframework.AbstractSpringBaseTest;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.integration.bo.JobTypeInfo;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 包: com.transing.dpmbs.biz.service
 * 源文件:JobTypeServiceTest.java
 *
 * @author Sunny  Copyright 2016 成都创行, Inc. All rights reserved.2017年04月19日
 */
public class JobTypeServiceTest extends AbstractSpringBaseTest
{
    @Resource
    private JobTypeService jobTypeService;

    @Test
    public void testGetValidJobByJobNo()
    {
        String typeNo="123";
        String nem = "ss";

        JobTypeInfo jobTypeInfo =null;

        jobTypeInfo = jobTypeService.getValidJobTypeByTypeNo(typeNo);

        System.out.println(jobTypeInfo);
    }

    @Test
    public void testGetAllValidJobInfo()
    {
        System.out.println(jobTypeService.getAllValidJobTypeInfo());
    }
}
