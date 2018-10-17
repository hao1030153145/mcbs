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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Controller("subjectSetController")
@Api(value = "主题分析设置", description = "主题分析设置相关接口", position = 2)
@RequestMapping("/subjectSet")
public class SubjectSetController {

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private JobTypeService jobTypeService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @Resource
    private DataSourceTypeService dataSourceTypeService;

    private final String GET_PROJECTLIST_BY_MATRIX = "/getProjectListByMatrix.json";
    private final String GET_SUBJECT_BY_TYPEID = "/getSubjectByTypeId.json";

    @RequestMapping(value = "/getSubjectAresList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 主题域list 接口", notes = "", position = 0)
    public List<SubjectListPO> getWordLibraryList(HttpServletRequest req, HttpServletResponse res) {

        List<SubjectListPO> subjectListPOList = new ArrayList<>();

        String apbsServerByEnv = WebUtil.getApbsServerByEnv();

        Object object = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),apbsServerByEnv+GET_PROJECTLIST_BY_MATRIX,"get",null);
        if(null != object){
            JSONArray jsonArray = (JSONArray) object;
            subjectListPOList = JSON.parseArray(jsonArray.toString(),SubjectListPO.class);
        }

        return subjectListPOList;
    }

    @RequestMapping(value = "getSubjectByTypeId.json",method = RequestMethod.GET)
    @ApiOperation(value = "通过主题分类id 查询 下一级的分类主题", position = 1)
    @ResponseBody
    List<LexContextPO> getSubjectByTypeId(@RequestParam(value = "typeSpId")@ApiParam(value = "分类id") String typeSpId,
                                          HttpServletRequest req, HttpServletResponse res) {

        List<LexContextPO> lexContextPOList = new ArrayList<>();

        if (Validate.isEmpty(typeSpId) || !typeSpId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        int typeSpIdInt = Integer.parseInt(typeSpId);

        String apbsServerByEnv = WebUtil.getApbsServerByEnv();

        Object object = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),apbsServerByEnv+GET_SUBJECT_BY_TYPEID+"?typeSpId="+typeSpIdInt,"get",null);
        if(null != object){
            JSONArray jsonArray = (JSONArray) object;
            lexContextPOList = JSON.parseArray(jsonArray.toString(),LexContextPO.class);
        }

        return lexContextPOList;

    }

    @RequestMapping(value = "/getSubjectSetList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 主题分析设置 接口", notes = "", position = 0)
    public List<SubjectSetPO> getSubjectSetList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                                @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo",required = true) String typeNo,
                                                HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);

        //查询出参数列表
        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(typeNo,projectIdInt);
        List<SubjectSetPO> subjectSetPOList = new ArrayList<>();//用来装组装好的主题设置分析对象
        if(null != workFlowParamList && workFlowParamList.size() > 0){

            for (WorkFlowParam workFlowParam :workFlowParamList) {
                String subjectJsonParamStr = workFlowParam.getJsonParam();
                SubjectSetPO subjectSetPO = JSON.parseObject(subjectJsonParamStr,SubjectSetPO.class);
                subjectSetPO.setParamId(workFlowParam.getParamId());

                subjectSetPOList.add(subjectSetPO);

            }
        }

        return subjectSetPOList;
    }

    @RequestMapping(value = "/saveSubjectSet.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存 主题分析设置 接口", notes = "", position = 0)
    public CommonResultCodePO saveSubjectSet(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                                      @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo",required = true) String typeNo,
                                                      @RequestParam(value = "jsonParam",required = true)@ApiParam(value = "typeNo",required = true) String jsonParam,
                                                      HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);
        SubjectSetPO subjectSetPO = null;
        try {
            subjectSetPO = JSON.parseObject(jsonParam,SubjectSetPO.class);
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        List<SubjectAresPO> subjectAresPOList = new ArrayList<>();

        String apbsServer = WebUtil.getApbsServerByEnv();
        HttpClientHelper httpClientHelper = new HttpClientHelper();
        try {
            HttpResponse httpResponse = httpClientHelper.doGet(apbsServer+"/getSubjectList.json",null,null,null,null);
            String content = httpResponse.getContent();
            CommonPO commonPO = JSON.parseObject(content,CommonPO.class);

            String data = commonPO.getData();

            subjectAresPOList = JSON.parseArray(data,SubjectAresPO.class);

        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Long,String> subjecMap = new HashMap<>();//聚合 id，对象。方便下面通过id 取名字
        for (SubjectAresPO subjectAresPO:subjectAresPOList) {
            subjecMap.put(subjectAresPO.getId(),subjectAresPO.getName());
        }

        //设置 主题域id 以及 相应的名字
        long subjectAresId = subjectSetPO.getSubjectAresId();
        String subjectAresName = subjecMap.get(subjectAresId);
        subjectSetPO.setSubjectAresName(subjectAresName);

        //设置主题 ids 以及相应的名字
        String subjectIds = subjectSetPO.getSubjectIds();
        String [] subjectIdArray = subjectIds.split(",");

        String subjectNameStr = "";
        for (String subjectIdStr:subjectIdArray) {
            long subjectId = Long.parseLong(subjectIdStr);
            String subjectName = subjecMap.get(subjectId);
            subjectNameStr += subjectName+",";
        }
        if(!"".equals(subjectNameStr)){
            subjectNameStr = subjectNameStr.substring(0,subjectNameStr.length()-1);
        }
        subjectSetPO.setSubjectNames(subjectNameStr);

        int resultsStrategyType = subjectSetPO.getResultsStrategyType();
        int value = subjectSetPO.getResultsStrategyTypeValue();
        switch (resultsStrategyType){
            case 1:
                subjectSetPO.setResultsStrategyTypeName("精英团");
                break;
            case 2:
                subjectSetPO.setResultsStrategyTypeName("Top "+value);
                break;
            case 3:
                subjectSetPO.setResultsStrategyTypeName("Top "+value+"%");
                break;
            case 4:
                subjectSetPO.setResultsStrategyTypeName("阈值 "+value);
                break;
        }

        Long paramId = subjectSetPO.getParamId();

        String jsonParamStr = JSON.toJSONString(subjectSetPO);

        //判断paramId 如果有值 则更新操作 否则新增
        if( null != paramId && paramId > 0){
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setParamId(paramId);
            workFlowParam.setJsonParam(jsonParamStr);
            workFlowService.updateWorkFlowParam(workFlowParam);
        }else{

            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(typeNo,projectIdInt);
            if(null != workFlowParamList && workFlowParamList.size() > 0){
                for (WorkFlowParam workFlowParam:workFlowParamList) {
                    String jsonStr = workFlowParam.getJsonParam();
                    SubjectSetPO subjectSet = JSON.parseObject(jsonStr,SubjectSetPO.class);
                    long alSubjectAresId = subjectSet.getSubjectAresId();//已经选择过的主题域id
                    if(alSubjectAresId == subjectSetPO.getSubjectAresId()){
                        throw new WebException(MySystemCode.BIZ_SUBJECTSET_EXIET_EXCEPTION);
                    }
                }
            }

            /*workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING, Long.parseLong(projectId));

            long preDetailId = 0;
            if(null != workFlowParamList && workFlowParamList.size() > 0){
                WorkFlowParam workFlowParam = workFlowParamList.get(workFlowParamList.size()-1);
                preDetailId = workFlowParam.getFlowDetailId();
            }else {


            }*/

            long preDetailId = 0;
            String nextDetialIds = "";
            String preTypeNo = Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING;
            String nextTypeNo = Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION;
            ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
            projectJobTypeBO.setProjectId(Long.parseLong(projectId));
            projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING);
            ProjectJobTypeBO projectJobType = projectJobTypeService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);
            if(null != projectJobType){
                preTypeNo = projectJobType.getPreTypeNo();
                nextTypeNo = projectJobType.getNextTypeNo();
            }

            workFlowParamList = workFlowService.getWorkFlowParamListByParam(preTypeNo,Long.parseLong(projectId));

            List<WorkFlowParam> nextWorkFlowParamList = workFlowService.getWorkFlowParamListByParam(nextTypeNo,Long.parseLong(projectId));

            if(null != workFlowParamList && workFlowParamList.size() > 0){
                WorkFlowParam workFlowParam = workFlowParamList.get(workFlowParamList.size()-1);
                preDetailId = workFlowParam.getFlowDetailId();
            }
            if (!Validate.isEmpty(nextWorkFlowParamList)) {
                WorkFlowParam workFlowParam = nextWorkFlowParamList.get(0);
                nextDetialIds = Long.toString(workFlowParam.getFlowDetailId());
                workFlowService.addWorkDetail(Long.toString(preDetailId),nextDetialIds,projectIdInt,typeNo,jsonParamStr,WorkFlowParam.PARAM_TYPE_PRIVATE,null,null);
            }else {
                workFlowService.addWorkDetail(Long.toString(preDetailId),projectIdInt,typeNo,jsonParamStr,WorkFlowParam.PARAM_TYPE_PRIVATE);
            }

        }

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        return commonResultCodePO;

    }

    @RequestMapping(value = "/deleteSubjectSet.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除 主题分析设置 接口", notes = "", position = 0)
    public CommonResultCodePO deleteSubjectSet(@RequestParam(value = "paramId",required = true)@ApiParam(value = "参数id",required = true) String paramId,
                                         HttpServletRequest req, HttpServletResponse res) {
        if (paramId == null || "".equals(paramId) || !paramId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long paramIdInt = Long.parseLong(paramId);

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        boolean isSecc = workFlowService.deleteWorkFlowParam(paramIdInt);

        if(!isSecc){
            throw new WebException(MySystemCode.BIZ_DELETE_EXCEPTION);
        }

        return commonResultCodePO;

    }

    @RequestMapping(value = "/getSubjectSemanticResultList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询 主题 语义分析结果 接口", notes = "", position = 0)
    public DataImportListFromShowDataPo getSubjectSemanticResultList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目 id",required = true) String projectId,
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

        long jobResultTypeId = 0;
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
        keyList.remove("splitResult");
        keyList.remove("keywordResult");
        keyList.remove("topicJSON");
        titleList.remove("分词结果");
        titleList.remove("关键词结果");
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
    @RequestMapping(value = "/getSubject.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "可视化工作流项目查询主题域", notes = "", position = 0)
    public List<Map<String,Object>> getSubject() throws Exception{
        List<Map<String,Object>> resultList = new ArrayList<>();
        String apbsServerByEnv = WebUtil.getApbsServerByEnv();
        Object object = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),apbsServerByEnv+GET_PROJECTLIST_BY_MATRIX,"get",null);
        if(null != object){
            JSONArray jsonArray = (JSONArray) object;
            List<SubjectListPO> subjectListPOList = JSON.parseArray(jsonArray.toString(),SubjectListPO.class);
            if(!Validate.isEmpty(subjectListPOList)){
                for(SubjectListPO subjectListPO : subjectListPOList){
                    Map<String,Object> map = new HashMap<>();
                    map.put("id",subjectListPO.getId());
                    map.put("pid",subjectListPO.getPid());
                    map.put("name",subjectListPO.getName());
                    if(subjectListPO.getProjectPO()!=null){
                        map.put("projectPO",subjectListPO.getProjectPO());
                        Object object2 = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),apbsServerByEnv+GET_SUBJECT_BY_TYPEID+"?typeSpId="+subjectListPO.getId(),"get",null);
                        if(null != object2){
                            JSONArray jsonArray2 = (JSONArray) object2;
                            List<LexContextPO> lexContextPOList = JSON.parseArray(jsonArray2.toString(),LexContextPO.class);
                            map.put("lexContextPOList",lexContextPOList);
                        }
                    }else{
                        map.put("projectPO",null);
                    }

                    resultList.add(map);
                }
            }
        }
        return resultList;
    }
}
