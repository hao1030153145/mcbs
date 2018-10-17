package com.transing.workflow.util;

import com.alibaba.fastjson.JSON;
import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.logicframework.biz.service.mq.producer.BaseKafkaProducer;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.util.QuartzManager;
import com.transing.dpmbs.web.po.DataCrawlPO;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;
import com.transing.workflow.util.quartz.ExecuteCrawlJobQuartz;
import net.sf.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by byron on 2018/3/21 0021.
 */
public class VisWorkFlowStart implements Runnable {

    private JobTypeService jobTypeService = SpringContextHolder.getBean("jobTypeService");

    private WorkFlowService workFlowService = SpringContextHolder.getBean("workFlowService");

    private WorkFlowExecuteMethod workFlowExecuteMethod = SpringContextHolder.getBean("workFlowExecuteMethod");

    private DataSourceTypeService dataSourceTypeService = SpringContextHolder.getBean("dataSourceTypeService");

    public long workFlowId = 0;

    private List<WorkFlowDetail> firstDetailList;
    public String preRun=null;
    public Map<Long,WorkFlowParam> workFlowParamMap;



    public VisWorkFlowStart(Long workFlowId,Map<Long,WorkFlowParam> workFlowParamMap,String preRun){
        this.workFlowId = workFlowId;
        this.workFlowParamMap = workFlowParamMap;
        this.preRun=preRun;
    }

    @Override
    public void run() {
        LoggerUtil.debugTrace(this.getClass().getSimpleName(),"==================VisWorkFlowStart开始工作啦====startTime"+System.currentTimeMillis());
        if(Validate.isEmpty(firstDetailList)){
            firstDetailList =  workFlowService.getFirstDetailByWorkFlowId(workFlowId);//根据工作流id获取流程表的首节点
        }

        Map<String,JobTypeInfo> jobTypeInfoMap = new HashMap();
        List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();//查询所有有效的节点
        if(null != jobTypeInfoList && jobTypeInfoList.size() > 0){
            //聚合jobType 方便 下面根据typeNo 取出jobType对象
            for (JobTypeInfo jobTypeInfo :jobTypeInfoList) {
                jobTypeInfoMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
            }
        }

        //查询输入参数配置表，并聚合成map
        List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowService.getWorkFlowInputParamBoList();
        Map<Integer, String> inputParamMap = new HashMap<>();
        for (WorkFlowInputParamBo workFlowInputParamBo : workFlowInputParamBoList) {
            inputParamMap.put(workFlowInputParamBo.getId(), workFlowInputParamBo.getParamEnName());
        }

        if(!Validate.isEmpty(firstDetailList)) {
            for (WorkFlowDetail workFlowDetail : firstDetailList) {
                if(workFlowDetail.getJobStatus()==5){
                    continue;

                }
                //判断该首节点是否为已完成
                    String typeNo = workFlowDetail.getTypeNo();
                    JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(typeNo);//根据typeNo取出对应的JobTypeInfo。
                    //查询首节点的输入参数值。
                    WorkFlowParam workFlowParam = workFlowParamMap.get(workFlowDetail.getFlowDetailId());

                    if (typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)) {
                        String quartzTime = workFlowDetail.getQuartzTime();
                        if (!Validate.isEmpty(quartzTime)&&preRun==null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("jobTypeInfo", jobTypeInfo);
                            map.put("workFlowDetail", workFlowDetail);
                            map.put("workFlowParam", workFlowParam);
                            try {
                                QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            QuartzManager.addJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId(), ExecuteCrawlJobQuartz.class, workFlowDetail.getQuartzTime(), map);
                        } else {
                            workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(), jobTypeInfo, workFlowDetail,workFlowParam,workFlowDetail.getFlowDetailId(),null,preRun);
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
                        //判断是否是循环任务并且不是试运行状态
                        if (!Validate.isEmpty(quartzTime)&&preRun==null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("jobTypeInfo", jobTypeInfo);
                            map.put("workFlowDetail", workFlowDetail);
                            map.put("workFlowParam", workFlowParam);
                            try {
                                QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            QuartzManager.addJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId(), ExecuteCrawlJobQuartz.class, workFlowDetail.getQuartzTime(), map);
                        } else {
                            workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(), jobTypeInfo, workFlowDetail,workFlowParam,workFlowDetail.getFlowDetailId(),null,preRun);
                        }
                    }else if (typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)) {
                        workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(), jobTypeInfo, workFlowDetail,workFlowParam,workFlowDetail.getFlowDetailId(),null,preRun);
                    }
                    LoggerUtil.debugTrace(this.getClass().getSimpleName(),"==================WorkFlowSTart==detailId="+workFlowDetail.getFlowDetailId()+"==endTime"+System.currentTimeMillis());


            }
        }
    }
}
