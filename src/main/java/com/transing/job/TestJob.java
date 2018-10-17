/*
 * @project: test4temp
 * @package: com.test4emp.job
 * @title:   TestJob.java 
 *
 * Copyright (c) 2017 jeeframework Limited, Inc.
 * All rights reserved.
 */
package com.transing.job;

import com.jeeframework.jeetask.task.Job;
import com.jeeframework.jeetask.task.Task;
import com.jeeframework.jeetask.task.context.JobContext;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.transing.task.rdb.impl.DpmTask;

/**
 * 测试用的作业执行类
 *
 * @author lance
 * @version 1.0 2017-08-30 17:35
 */
public class TestJob implements Job {

    @Override
    public void doJob(JobContext jobContext) {
        int i = 0;
        Task task = jobContext.getTask();
        DpmTask dpmTask = (DpmTask)task ;

        while (true) {
            LoggerUtil.debugTrace("testJob", "dojob  =   " + dpmTask.getParam());

            i = i + 1;
            if (i > 5) {
                break;
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
