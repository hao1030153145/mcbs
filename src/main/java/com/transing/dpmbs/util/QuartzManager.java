package com.transing.dpmbs.util;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Map;

/**
 *
 * @author summer
 */

public class QuartzManager {
    private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
    private static String TRIGGER_GROUP_NAME = "EXTJWEB_TRIGGERGROUP_NAME";

    /**
     * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     *
     * @param jobName
     *            任务名
     * @param cls
     *            任务
     * @param time
     *            时间设置，参考quartz说明文档
     *
     * @Title: QuartzManager.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-6-26 下午03:47:44
     * @version V2.0
     */
    @SuppressWarnings("unchecked")
    public static void addJob(String jobName, Class cls, String time) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(cls)//执行job的class
                    .withIdentity(jobName, JOB_GROUP_NAME)// 任务名，任务组
                    .build();
//            JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, cls);// 任务名，任务组，任务执行类
            // 触发器
            CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger()
                    .withIdentity(jobName, TRIGGER_GROUP_NAME)// 触发器名,触发器组
                    .withSchedule(CronScheduleBuilder.cronSchedule(time))// 触发器时间设定
                    .build();
//            CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
//            trigger.setCronExpression(time);// 触发器时间设定
            sched.scheduleJob(jobDetail, trigger);
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 添加一个 任务 job 并启动运行
     * @param jobName 任务名
     * @param cls 执行的class
     * @param time 执行的time表达式
     * @param map 执行需要的参数
     */
    public static void addJob(String jobName, Class cls, String time,Map map) {
        try {
//            LoggerUtil.debugTrace("正在添加一个定时任务:"+jobName+"，time："+time+",map:"+ JSONObject.fromObject(map));
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(cls)//执行job的class
                    .withIdentity(jobName, JOB_GROUP_NAME)// 任务名，任务组
                    .build();
//            JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, cls);// 任务名，任务组，任务执行类
            jobDetail.getJobDataMap().putAll(map);
            // 触发器
            CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger()
                    .withIdentity(jobName, TRIGGER_GROUP_NAME)// 触发器名,触发器组
                    .withSchedule(CronScheduleBuilder.cronSchedule(time))// 触发器时间设定
                    .build();
//            CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
//            trigger.setCronExpression(time);// 触发器时间设定
            sched.scheduleJob(jobDetail, trigger);
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean existJob(String jobName){
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail jobDetail = sched.getJobDetail(new JobKey(jobName,JOB_GROUP_NAME));
            if(null != jobDetail){
                return true;
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }


    /**
     * @Description: 添加一个定时任务
     *
     * @param jobName
     *            任务名
     * @param jobGroupName
     *            任务组名
     * @param triggerName
     *            触发器名
     * @param triggerGroupName
     *            触发器组名
     * @param jobClass
     *            任务
     * @param time
     *            时间设置，参考quartz说明文档
     *
     * @Title: QuartzManager.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-6-26 下午03:48:15
     * @version V2.0
     */
    @SuppressWarnings("unchecked")
    public static void addJob(String jobName, String jobGroupName,
                              String triggerName, String triggerGroupName, Class jobClass,
                              String time) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(jobClass)//执行job的class
                    .withIdentity(jobName, JOB_GROUP_NAME)// 任务名，任务组
                    .build();
//            JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, cls);// 任务名，任务组，任务执行类
            // 触发器
            CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger()
                    .withIdentity(jobName, TRIGGER_GROUP_NAME)// 触发器名,触发器组
                    .withSchedule(CronScheduleBuilder.cronSchedule(time))// 触发器时间设定
                    .build();
//            CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
            sched.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     * @param time
     *
     * @Title: QuartzManager.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-6-26 下午03:49:21
     * @version V2.0
     */
    @SuppressWarnings("unchecked")
    public static void modifyJobTime(String jobName, String time) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(new TriggerKey(jobName,TRIGGER_GROUP_NAME));
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                JobDetail jobDetail = sched.getJobDetail(new JobKey(jobName,JOB_GROUP_NAME));
                Class objJobClass = jobDetail.getJobClass();
                removeJob(jobName);
                addJob(jobName, objJobClass, time);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 修改一个任务的触发时间
     *
     * @param triggerName
     * @param triggerGroupName
     * @param time
     *
     * @Title: QuartzManager.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-6-26 下午03:49:37
     * @version V2.0
     *//*
    public static void modifyJobTime(String triggerName,
                                     String triggerGroupName, String time) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(new TriggerKey(triggerName,TRIGGER_GROUP_NAME));
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                CronTrigger ct = (CronTrigger) trigger;
                // 修改时间
                ct.setCronExpression(time);
                // 重启触发器
                sched.resumeTrigger(triggerName, triggerGroupName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    /**
     * @Description: 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     *
     * @Title: QuartzManager.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-6-26 下午03:49:51
     * @version V2.0
     */
    public static void removeJob(String jobName) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();

            sched.pauseTrigger(new TriggerKey(jobName,TRIGGER_GROUP_NAME));// 停止触发器
            sched.unscheduleJob(new TriggerKey(jobName,TRIGGER_GROUP_NAME));// 移除触发器
            sched.deleteJob(new JobKey (jobName,JOB_GROUP_NAME));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 移除一个任务
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     *
     * @Title: QuartzManager.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-6-26 下午03:50:01
     * @version V2.0
     */
    public static void removeJob(String jobName, String jobGroupName,
                                 String triggerName, String triggerGroupName) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.pauseTrigger(new TriggerKey(triggerName,triggerGroupName));// 停止触发器
            sched.unscheduleJob(new TriggerKey(triggerName,triggerGroupName));// 移除触发器
            sched.deleteJob(new JobKey(jobName,jobGroupName));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:启动所有定时任务
     *
     *
     * @Title: QuartzManager.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-6-26 下午03:50:18
     * @version V2.0
     */
    public static void startJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     *
     *
     * @Title: QuartzManager.java
     * @Copyright: Copyright (c) 2014
     *
     * @author Comsys-LZP
     * @date 2014-6-26 下午03:50:26
     * @version V2.0
     */
    public static void shutdownJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
