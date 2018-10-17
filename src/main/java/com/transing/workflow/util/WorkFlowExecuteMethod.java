package com.transing.workflow.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.biz.service.VisWorkFlowService;
import com.transing.dpmbs.integration.bo.VisWorkFlowBO;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowParam;
import net.sf.json.JSONArray;
import org.apache.http.HttpException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/25.
 */
@Component("workFlowExecuteMethod")
public class WorkFlowExecuteMethod {

    @Resource
    private WorkFlowService workFlowService;
    @Resource
    private ProjectService projectService;
    @Resource
    private VisWorkFlowService visWorkFlowService;

    public int executeJob(String logName, String resultParam,JobTypeInfo jobTypeInfo, WorkFlowDetail workFlowDetail)
    {

        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(workFlowDetail.getFlowDetailId());

        LoggerUtil.debugTrace(logName,"begin process executeJob:projectId:"+workFlowDetail.getProjectId()+" typeNo:"+jobTypeInfo.getTypeNo());

        String dpmssServer = WebUtil.getDpmssServerByEnv();
        String crawlServer = WebUtil.getCrawlServerByEnv();

        int jobType = jobTypeInfo.getJobType();

        String typeNo = workFlowDetail.getTypeNo();
        String jsonParam = workFlowParam.getJsonParam().toString();
        long projectId = workFlowParam.getProjectId();
        long flowId = workFlowParam.getFlowId();
        long flowDetailId = workFlowParam.getFlowDetailId();
        int paramType = workFlowParam.getParamType();


        String exectUrl = "";
        String progressUrl = "";
        switch (jobType){
            case JobTypeInfo.JOB_TYPE_CRAWL:
                String mCrawlServer = WebUtil.getMCrawlServerByEnv();
                if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                    exectUrl = crawlServer+jobTypeInfo.getExecuteUrl();
                    progressUrl = crawlServer+jobTypeInfo.getProgressUrl();
                }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
                    exectUrl = mCrawlServer+jobTypeInfo.getExecuteUrl();
                    progressUrl = mCrawlServer+jobTypeInfo.getProgressUrl();
                }
                break;
            case JobTypeInfo.JOB_TYPE_ANALYSIS:
                exectUrl = dpmssServer+jobTypeInfo.getExecuteUrl();
                progressUrl = dpmssServer+jobTypeInfo.getProgressUrl();
                break;
            default://默认执行抓取
                exectUrl = dpmssServer+jobTypeInfo.getExecuteUrl();
                progressUrl = dpmssServer+jobTypeInfo.getProgressUrl();
        }


        Map<String,String> postData = new HashMap<>();
        postData.put("projectId",""+projectId);
        postData.put("flowId",""+flowId);
        postData.put("flowDetailId",""+flowDetailId);
        postData.put("typeNo",typeNo);
        postData.put("jsonParam",jsonParam);
        postData.put("paramType",""+paramType);
        postData.put("resultParam",resultParam);

        HttpClientHelper httpClientHelper = new HttpClientHelper();
        try {
            HttpResponse httpResponse = httpClientHelper.doPost(exectUrl,postData,"utf-8",null,null,null);

            String jsonContent = httpResponse.getContent();

            JSONObject jsonObject = JSON.parseObject(jsonContent);
            Object code = jsonObject.get("code");
            Object message = jsonObject.get("message");

            LoggerUtil.debugTrace(logName,"begin process executeJob Sucess 返回code:"+code+" :projectId:"+workFlowParam.getProjectId()+" typeNo:"+jobTypeInfo.getTypeNo()+"jsonContent:"+jsonContent);

            if(code.toString().equals("0")){
                if(jobType == JobTypeInfo.JOB_TYPE_ANALYSIS){
                    boolean isFinish = false;
                    while (!isFinish){
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        HttpResponse httpResponseGet = httpClientHelper.doGet(progressUrl+"?projectId="+projectId+"&flowId="+flowId+"&flowDetailId="+flowDetailId+"&typeNo="+typeNo,null,null,null,null);
                        String jsonResult = httpResponseGet.getContent();

                        JSONObject jsonObjectResult = JSON.parseObject(jsonResult);

                        LoggerUtil.debugTrace(logName,"begin process progressJob Sucess :projectId:"+workFlowParam.getProjectId()+" typeNo:"+jobTypeInfo.getTypeNo()+"jsonObjectResult:"+jsonObjectResult);

                        Object reCode = jsonObjectResult.get("code");

                        if(reCode.toString().equals("0")){

                            jsonObjectResult = jsonObjectResult.getJSONObject("data");

                            Object jobStatus = jsonObjectResult.get("jobStatus");
                            Object jobProgress = jsonObjectResult.get("jobProgress");
                            Object errorMsg = jsonObjectResult.get("errorMsg");

                            if(null != jobProgress && !"".equals(jobProgress) && !"0".equals(jobProgress.toString())){
                                int jobProgressInt = Integer.parseInt(jobProgress.toString());
                                workFlowService.updateWorkFlowDetailProgress(flowDetailId,jobProgressInt);
                            }

                            if(null != jobStatus && jobStatus.toString().equals("2")){
                                isFinish = true;

                                Object resultPara = jsonObjectResult.get("resultParam");
                                if(null != resultPara&& !Validate.isEmpty(resultPara.toString())){
                                    net.sf.json.JSONObject newJsonObject = net.sf.json.JSONObject.fromObject(resultPara.toString());
                                    Map<String, net.sf.json.JSONObject> newJsonObjectMap = new HashMap<>();
                                    Object detailIdObj = newJsonObject.get("detailId");
                                    Object hierarchy = newJsonObject.get("hierarchy");
                                    if(null != hierarchy){
                                        Object dataSourceType = newJsonObject.get("dataSourceType");
                                        newJsonObjectMap.put(detailIdObj.toString()+"&"+dataSourceType.toString()+"&"+hierarchy.toString(),newJsonObject);
                                    }else {
                                        newJsonObjectMap.put(detailIdObj.toString(),newJsonObject);
                                    }

                                    String oldResultParam = workFlowDetail.getResultParam();
                                    JSONArray oldJsonArray = JSONArray.fromObject(oldResultParam);

                                    if(null != oldJsonArray && oldJsonArray.size() > 0){
                                        for (int i = 0; i < oldJsonArray.size(); i++) {
                                            net.sf.json.JSONObject jsonObj = oldJsonArray.getJSONObject(i);

                                            Object detailIdObj2 = jsonObj.get("detailId");
                                            Object hierarchy2 = jsonObj.get("hierarchy");
                                            if(null != hierarchy2){
                                                Object dataSourceType = jsonObj.get("dataSourceType");
                                                net.sf.json.JSONObject newJsonObj = newJsonObjectMap.get(detailIdObj2.toString()+"&"+dataSourceType.toString()+"&"+hierarchy2.toString());
                                                if(null != newJsonObj){
                                                    oldJsonArray.set(i,newJsonObj);
                                                }
                                            }else{
                                                net.sf.json.JSONObject newJsonObj = newJsonObjectMap.get(detailIdObj2.toString());
                                                if(null != newJsonObj){
                                                    oldJsonArray.set(i,newJsonObj);
                                                }
                                            }

                                        }
                                    }

                                    workFlowDetail.setResultParam(oldJsonArray.toString());
                                }

                                workFlowDetail.setJobStatus(3);//状态为已执行
                                workFlowService.updateWorkFlowDetail(workFlowDetail);

                                return 3;//返回status 为 已执行状态

                                /*workFlowService.updateWorkFlowDetailToFinish(flowDetailId);
                                workFlowService.updateWorkFlowInfoComNum(flowId);
                                workFlowService.updateWorkFlowInfoToFinishIfComNum(flowId);

                                //更新状态
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
                                }*/

                            }else if(null != jobStatus && jobStatus.toString().equals("9")){

                                isFinish = true;//跳出循环停止工作流
                                //报错 停止工作流
                                updateToStop(workFlowDetail,errorMsg.toString());

                                return 9;//返回status 为 错误状态

                            }

                        }
                    }

                }

            }else{

                if(null == message ){
                    message = "请求 “"+jobType+"” 系统 执行job 报错 返回code 不为0";
                }

                //报错 停止工作流
                updateToStop(workFlowDetail,message.toString());

                return 9;//返回status 为 错误状态

            }

        } catch (HttpException e) {

            e.printStackTrace();
            //报错 停止工作流
            updateToStop(workFlowDetail,e.toString());

            return 9;//返回status 为 错误状态
        } catch (IOException e) {

            e.printStackTrace();
            //报错 停止工作流
            updateToStop(workFlowDetail,e.toString());

            return 9;//返回status 为 错误状态
        }

        return 3;
    }


    public void executeFirstJob(String logName, JobTypeInfo jobTypeInfo, WorkFlowDetail workFlowDetail,WorkFlowParam workFlowParam,long firstDetailId,String batchNo,String preRun)
    {

        LoggerUtil.debugTrace(logName,"begin process executeJob:projectId:"+workFlowDetail.getProjectId()+" typeNo:"+jobTypeInfo.getTypeNo()+",detailId = "+workFlowParam.getFlowDetailId()+",firstDetailId="+firstDetailId+",workFlowId="+workFlowParam.getWorkFlowId());

        String dpmssServer = WebUtil.getDpmssServerByEnv();
        String crawlServer = WebUtil.getCrawlServerByEnv();
        String corpus=WebUtil.getCorpusServerByEnv();

        int jobType = jobTypeInfo.getJobType();//获取节点类型

        String typeNo = workFlowParam.getTypeNo();//获取节点编号
        String jsonParam = workFlowParam.getJsonParam().toString();
        long projectId = workFlowParam.getProjectId();
        long flowId = workFlowParam.getFlowId();//获取流程id
        long flowDetailId = workFlowParam.getFlowDetailId();//获取工作流详细id
        int paramType = workFlowParam.getParamType();//获取该节点的参数类型


        String exectUrl = "";
        String progressUrl = "";

        switch (jobType){
            case JobTypeInfo.JOB_TYPE_CRAWL://如果是抓取节点
                String mCrawlServer = WebUtil.getMCrawlServerByEnv();//获取移动抓取地址
                if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){//判断是否是抓取或者移动抓取
                    exectUrl = crawlServer+jobTypeInfo.getExecuteUrl();
                    progressUrl = crawlServer+jobTypeInfo.getProgressUrl();
                }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
                exectUrl = mCrawlServer+jobTypeInfo.getExecuteUrl();
                progressUrl = mCrawlServer+jobTypeInfo.getProgressUrl();
            }
                break;
            case JobTypeInfo.JOB_TYPE_ANALYSIS:
                exectUrl = dpmssServer+jobTypeInfo.getExecuteUrl();
                progressUrl = dpmssServer+jobTypeInfo.getProgressUrl();
                break;
            default://默认执行抓取
                exectUrl = dpmssServer+jobTypeInfo.getExecuteUrl();
                progressUrl = dpmssServer+jobTypeInfo.getProgressUrl();
        }
        LoggerUtil.debugTrace("detailIds"+flowDetailId+"preRun"+preRun);
        String dataType=null;
        List<VisWorkFlowBO> visWorkFlowBOList = visWorkFlowService.getVisWorkFlowList(new Long(flowDetailId).intValue());
        if(visWorkFlowBOList.size()>0){
            for (VisWorkFlowBO visWorkFlowBO:visWorkFlowBOList
                 ) {
                dataType=visWorkFlowBO.getStorageTypeTable();
                break;
            }
        }
        if(preRun!=null){
            try {
                HttpClientHelper httpClientHelper = new HttpClientHelper();
                Map<String,String> postData = new HashMap<>();
                postData.put("detailId",""+flowDetailId);
                postData.put("dataType",dataType);
                postData.put("field","preRun");
                postData.put("flag","no-null");
                HttpResponse httpResponse = httpClientHelper.doPost(corpus+"/delDataLists.json",postData,"utf-8",null,null,null);
                String jsonContent = httpResponse.getContent();
                JSONObject jsonObject = JSON.parseObject(jsonContent);
                Object code = jsonObject.get("code");
                LoggerUtil.debugTrace("detailIds"+flowDetailId+"preRun"+preRun+"  code"+code);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        Map<String,String> postData = new HashMap<>();
        postData.put("projectId",""+projectId);
        postData.put("flowId",""+flowId);
        postData.put("flowDetailId",""+flowDetailId);
        postData.put("firstDetailId",""+firstDetailId);
        postData.put("workFlowId",workFlowParam.getWorkFlowId()+"");
        postData.put("typeNo",typeNo);
        postData.put("jsonParam",jsonParam);
        postData.put("paramType",""+paramType);
        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("batchNo",batchNo);
        jsonObject1.put("preRun",preRun);
        jsonObject1.put("flowDetailId",flowDetailId);
        postData.put("travelParams",jsonObject1.toString());
        LoggerUtil.debugTrace("jsonObject1:"+jsonObject1+"postData:"+postData.toString());
        HttpClientHelper httpClientHelper = new HttpClientHelper();
        try {
            HttpResponse httpResponse = httpClientHelper.doPost(exectUrl,postData,"utf-8",null,null,null);

            String jsonContent = httpResponse.getContent();

            JSONObject jsonObject = JSON.parseObject(jsonContent);
            Object code = jsonObject.get("code");
            Object message = jsonObject.get("message");

            LoggerUtil.debugTrace(logName,"begin process executeJob Sucess 返回code:"+code+" :projectId:"+workFlowParam.getProjectId()+" typeNo:"+jobTypeInfo.getTypeNo()+"jsonContent:"+jsonContent+",detailId = "+workFlowDetail.getFlowDetailId()+",firstDetailId="+firstDetailId);

            if(code.toString().equals("0")){
                /*if(jobType == JobTypeInfo.JOB_TYPE_ANALYSIS){
                    boolean isFinish = false;
                    while (!isFinish){
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        HttpResponse httpResponseGet = httpClientHelper.doGet(progressUrl+"?projectId="+projectId+"&flowId="+flowId+"&flowDetailId="+flowDetailId+"&typeNo="+typeNo,null,null,null,null);
                        String jsonResult = httpResponseGet.getContent();

                        JSONObject jsonObjectResult = JSON.parseObject(jsonResult);

                        LoggerUtil.debugTrace(logName,"begin process progressJob Sucess :projectId:"+workFlowParam.getProjectId()+" typeNo:"+jobTypeInfo.getTypeNo()+"jsonObjectResult:"+jsonObjectResult);

                        Object reCode = jsonObjectResult.get("code");

                        if(reCode.toString().equals("0")){

                            jsonObjectResult = jsonObjectResult.getJSONObject("data");

                            Object jobStatus = jsonObjectResult.get("jobStatus");
                            Object jobProgress = jsonObjectResult.get("jobProgress");
                            Object errorMsg = jsonObjectResult.get("errorMsg");

                            if(null != jobProgress && !"".equals(jobProgress) && !"0".equals(jobProgress.toString())){
                                int jobProgressInt = Integer.parseInt(jobProgress.toString());
                                workFlowService.updateWorkFlowDetailProgress(flowDetailId,jobProgressInt);
                            }

                            if(null != jobStatus && jobStatus.toString().equals("2")){
                                isFinish = true;

                                workFlowService.updateWorkFlowDetailToFinish(flowDetailId);

                               /*//*//**//*更新状态
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

                            }else if(null != jobStatus && jobStatus.toString().equals("9")){

                                isFinish = true;//跳出循环停止工作流
                                //报错 停止工作流
                                updateToStop(workFlowDetail,errorMsg.toString());

                            }

                        }
                    }

                }*/

            }else{

                if(null == message ){
                    message = "请求 “"+jobType+"” 系统 执行job 报错 返回code 不为0";
                }
                if(preRun==null){
                    //报错 停止工作流
                    updateToStop(workFlowDetail,message.toString());
                }


            }

        } catch (HttpException e) {

            e.printStackTrace();
            //报错 停止工作流
            if(preRun==null){
                updateToStop(workFlowDetail,e.toString());
            }

        } catch (IOException e) {

            e.printStackTrace();
            //报错 停止工作流
            if(preRun==null){
                updateToStop(workFlowDetail,e.toString());
            }

        }

    }

    private void updateToStop(WorkFlowDetail workFlowDetail,String message){
        workFlowService.updateWorkFlowDetailToExceptionByWorkFlowDetailId(workFlowDetail.getFlowDetailId(),message.toString());

        //停止后面的工作流 执行
//        updateNextDetailToStop(workFlowDetail);

        ProjectStatusFilter filter = new ProjectStatusFilter();
        filter.setId(workFlowDetail.getProjectId());
        filter.setStatus(9);
        projectService.updateProjectStatus(filter);
    }

    private boolean updateNextDetailToStop(WorkFlowDetail workFlowDetail){

        String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
        if(!Validate.isEmpty(nextFlowDetailIds)){
            String [] nextDetailArray = nextFlowDetailIds.split(",");
            for (String detailIdStr:nextDetailArray) {
                long detail = Long.parseLong(detailIdStr);
                WorkFlowDetail workFlowDetail2 = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detail);

                workFlowService.updateWorkFlowDetailToStopByWorkFlowDetailId(detail);//把找到的更新为停止
                //递归调用 停止他的下一个节点
                updateNextDetailToStop(workFlowDetail2);
            }
        }

        return true;
    }

}
