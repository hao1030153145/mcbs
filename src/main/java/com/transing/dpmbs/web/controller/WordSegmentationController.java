/**
 * @project: dpmbs
 * @Title: UserController.java
 * @Package: com.transing.dpmbs.web.controller
 * <p>
 * Copyright (c) 2014-2017 Jeeframework Limited, Inc.
 * All rights reserved.
 */
package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.JobTypeResultField;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller("wordSegmentationController")
@Api(value = "分词设置", description = "分词设置相关接口", position = 2)
@RequestMapping("/wordSegmentation")
public class WordSegmentationController {

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private JobTypeService jobTypeService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @RequestMapping(value = "/getWordLibraryList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 词库list 接口", notes = "", position = 0)
    public List<WordLibraryPO> getWordLibraryList(HttpServletRequest req, HttpServletResponse res) {

        List<WordLibraryPO> wordLibraryPOList = null;

        String apbsServer = WebUtil.getApbsServerByEnv();
        HttpClientHelper httpClientHelper = new HttpClientHelper();
        try {
            HttpResponse httpResponse = httpClientHelper.doGet(apbsServer+"/getTermList.json",null,null,null,null);
            String content = httpResponse.getContent();
            CommonPO commonPO = JSON.parseObject(content,CommonPO.class);

            String data = commonPO.getData();

            wordLibraryPOList = JSON.parseArray(data,WordLibraryPO.class);

        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return wordLibraryPOList;
    }

    @RequestMapping(value = "/getSegSet.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 分词设置 接口", notes = "", position = 0)
    public SegSetPO getSegSet(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                                      @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo",required = true) String typeNo,
                                                      HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);
        SegSetPO segSetPO = new SegSetPO();

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(typeNo,projectIdInt);

        if(null != workFlowParamList && workFlowParamList.size() > 0){
            for (WorkFlowParam workFlowParam :workFlowParamList) {
                String segJsonParamStr = workFlowParam.getJsonParam();
                segSetPO = JSON.parseObject(segJsonParamStr,SegSetPO.class);
                segSetPO.setParamId(workFlowParam.getParamId());
            }
        }

        return segSetPO;
    }

    @RequestMapping(value = "/saveSegSet.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存 分词 接口", notes = "", position = 0)
    public CommonResultCodePO saveSegSet(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                                      @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo",required = true) String typeNo,
                                                      @RequestParam(value = "jsonParam",required = true)@ApiParam(value = "jsonParam",required = true) String jsonParam,
                                                      HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);
        SegSetPO segSetPO = null;
        try {
            segSetPO = JSON.parseObject(jsonParam,SegSetPO.class);
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        Long paramId = segSetPO.getParamId();
        if( null != paramId && paramId > 0){
            String jsonParamStr = JSON.toJSONString(segSetPO);
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setParamId(paramId);
            workFlowParam.setJsonParam(jsonParamStr);
            workFlowService.updateWorkFlowParam(workFlowParam);
        }else{

            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(typeNo,projectIdInt);
            if(!Validate.isEmpty(workFlowParamList)){
                throw new WebException(MySystemCode.BIZ_WORKDSEG_EXIET_EXCEPTION);
            }

            String preTypeNo = Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT;
            List<ProjectJobTypeBO> projectJobTypeBOList =  projectJobTypeService.getProjectJobTypeListByProjectId(Long.parseLong(projectId));
            if(null != projectJobTypeBOList && projectJobTypeBOList.size() > 0){
                for (ProjectJobTypeBO projectJobTypeBO:projectJobTypeBOList) {
                    if(Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION.equals(projectJobTypeBO.getTypeNo())){
                        preTypeNo = projectJobTypeBO.getPreTypeNo();
                        break;
                    }
                }
            }
            String preDetailIds = "";
            workFlowParamList = workFlowService.getWorkFlowParamListByParam(preTypeNo,Long.parseLong(projectId));
            if(!Validate.isEmpty(workFlowParamList)){
                for (WorkFlowParam workFlowParam:workFlowParamList) {
                    long flowDetailId = workFlowParam.getFlowDetailId();
                    preDetailIds += flowDetailId + ",";
                }
            }

            if(!Validate.isEmpty(preDetailIds)){
                preDetailIds = preDetailIds.substring(0,preDetailIds.length()-1);
            }

            workFlowService.addWorkDetail(preDetailIds,projectIdInt,typeNo,jsonParam,WorkFlowParam.PARAM_TYPE_PRIVATE);
        }

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        return commonResultCodePO;

    }


    @RequestMapping(value = "/getSegSemanticResultList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询 分词 语义分析结果 接口", notes = "", position = 0)
    public DataImportListFromShowDataPo getSegSemanticResultList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目 id",required = true) String projectId,
                                                              @RequestParam(value = "paramId",required = true)@ApiParam(value = "参数 id",required = true) String paramId,
                                                              @RequestParam(value = "type",required = true)@ApiParam(value = "分词结果的类型(sentence-句级,section-段级,article-文级)",required = true) String type,
                                                              @RequestParam(value = "flag", required = false) @ApiParam(value = "flag", required = false) String flag,
                                                              @RequestParam(value = "size",required = true)@ApiParam(value = "查询记录数 默认 10 条",required = true) String size,
                                                              @RequestParam(value = "lastIndexId",required = false)@ApiParam(value = "最后一个indexId",required = true) String lastIndexId,
                                                              HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        if (paramId == null || "".equals(paramId) || !paramId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        if (type == null || "".equals(type)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        if(Validate.isEmpty(flag)){
            flag = "next";
        }
        String ordBy = "asc";
        if("next".equals(flag)){
            flag = "gt";
        }else if("pre".equals(flag)){
            flag = "lt";
            ordBy = "desc";
        }

        long paramIdInt = Long.parseLong(paramId);
        long sizeInt = null != size&&!"".equals(size)?Long.parseLong(size):10;

        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);
        if(null == workFlowParam){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        List<List> dataList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        List<String> keyList = new ArrayList<>();

        int jobResultTypeId = 0;
        switch (type){
            case "sentence":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_SENTENCE;
                break;
            case "section":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_SECTION;
                break;
            case "article":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_ARTICLE;
                break;
        }

        List<JobTypeResultField> jobTypeResultFieldList = jobTypeService.getResultFieldListByResultTypeId(jobResultTypeId);
        if(!Validate.isEmpty(jobTypeResultFieldList)){
            for (JobTypeResultField jobTypeResultField:jobTypeResultFieldList) {
                titleList.add(jobTypeResultField.getColName());
                keyList.add(jobTypeResultField.getFieldName());
            }
        }

        //移除掉主题json 和话题 json
        keyList.remove("themeJSON");
        keyList.remove("topicJSON");
        titleList.remove("主题json");
        titleList.remove("话题json");

        for(int i= 0;i<titleList.size();i++){
            String tag= "";
            switch(titleList.get(i)){
                case "indexId":
                    tag = titleList.get(0);
                    titleList.set(0,titleList.get(i));
                    titleList.set(i,tag);
                    break;
                case "项目id":
                    tag = titleList.get(1);
                    titleList.set(1,titleList.get(i));
                    titleList.set(i,tag);
                    break;
                case "语料id":
                    tag = titleList.get(2);
                    titleList.set(2,titleList.get(i));
                    titleList.set(i,tag);
                    break;
                case "段落id":
                    tag = titleList.get(3);
                    titleList.set(3,titleList.get(i));
                    titleList.set(i,tag);
                    break;
                case "文章id":
                    tag = titleList.get(4);
                    titleList.set(4,titleList.get(i));
                    titleList.set(i,tag);
                    break;
            }
        }
        for(int i= 0;i<keyList.size();i++){
            if(keyList.get(i).equals("indexId")){
                String firstStr = keyList.get(0);
                keyList.set(0,keyList.get(i));
                keyList.set(i,firstStr);
            }else if(keyList.get(i).equals("projectID")){
                String secondStr = keyList.get(1);
                keyList.set(1,keyList.get(i));
                keyList.set(i,secondStr);
            }else if(keyList.get(i).equals("corpusId")){
                String secondStr = keyList.get(2);
                keyList.set(2,keyList.get(i));
                keyList.set(i,secondStr);
            }else if(keyList.get(i).equals("pid")){
                String thirdStr = keyList.get(3);
                keyList.set(3,keyList.get(i));
                keyList.set(i,thirdStr);
            }else if(keyList.get(i).equals("aid")){
                String thirdStr = keyList.get(4);
                keyList.set(4,keyList.get(i));
                keyList.set(i,thirdStr);
            }
        }


        DataImportListFromShowDataPo dataImportListFromShowDataPo = new DataImportListFromShowDataPo();

        long lastIndexIdInt = 0;
        if(!Validate.isEmpty(lastIndexId)){

            if(!lastIndexId.matches("\\d+")){
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }

            lastIndexIdInt = Long.parseLong(lastIndexId);
        }

        Map<String,String> postData = new HashMap<>();
        postData.put("resultType",type);
        postData.put("filterJSON","{\"projectID\":"+projectId+"}");
        postData.put("startRow","0");
        postData.put("flag","gt");
        postData.put("rows","1");
        postData.put("ordBy", ordBy);

        Object firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getSemanticAnalysisDataList.json", "post", postData);
        if(null != firstObject){
            JSONObject jsonObject = (JSONObject) firstObject;
            Object total = jsonObject.get("total");
            dataImportListFromShowDataPo.setCount(total.toString());
        }

        Map<String,String> postData2 = new HashMap<>();
        postData2.put("resultType",type);
        postData2.put("filterJSON","{\"projectID\":"+projectId+"}");
        postData2.put("startRow",String.valueOf(lastIndexIdInt));
        postData2.put("flag",flag);
        postData2.put("rows",String.valueOf(sizeInt));
        postData2.put("ordBy", ordBy);

        Object resultObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getSemanticAnalysisDataList.json", "post", postData2);
        if(null != resultObject){
            JSONObject jsonObject = (JSONObject) resultObject;
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(null != jsonArray && jsonArray.size() > 0){
                //判断是倒序还是顺序。如果是正序，则从jsonArray的第一个开始添加
                //如果是倒序，则从则从jsonArray的最后一个开始添加
                if(ordBy.equals("asc")){
                    for (int i = 0; i < jsonArray.size(); i++) {
                        List<String> data = new ArrayList<>();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        for (String key:keyList) {
                            Object result = jsonObject1.get(key);
                            if(null != result && !"".equals(result)){
                                if(key.equals("dataSourceType")){
                                    DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(result.toString()));
                                    data.add(datasourceTypePO.getTypeName());
                                }else{
                                    data.add(result.toString());
                                }
                            }else {
                                data.add("");
                            }
                        }
                        dataList.add(data);
                    }
                }else{
                    for (int i = jsonArray.size()-1; i >=0; i--) {
                        List<String> data = new ArrayList<>();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        for (String key:keyList) {
                            Object result = jsonObject1.get(key);
                            if(null != result && !"".equals(result)){
                                if(key.equals("dataSourceType")){
                                    DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(result.toString()));
                                    data.add(datasourceTypePO.getTypeName());
                                }else{
                                    data.add(result.toString());
                                }
                            }else {
                                data.add("");
                            }
                        }
                        dataList.add(data);
                    }
                }
            }
        }

        dataImportListFromShowDataPo.setDataList(dataList);
        dataImportListFromShowDataPo.setTitleList(titleList);

        return dataImportListFromShowDataPo;
    }


}
