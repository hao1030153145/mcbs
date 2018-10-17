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
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("hotspotsSetController")
@Api(value = "话题分析设置", description = "话题分析设置相关接口", position = 2)
@RequestMapping("/hotspotsSet")
public class HotspotsSetController {

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private JobTypeService jobTypeService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @RequestMapping(value = "/getTopicList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 话题定义 list 接口", notes = "", position = 0)
    public List<TopicPO> getTopicList(HttpServletRequest req, HttpServletResponse res) {

        String apbsServer = WebUtil.getApbsServerByEnv();
        Map<String,String> postData = new HashMap<>();
        Object object = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),apbsServer+"/getTopicList.json","get",postData);

        JSONArray jsonArray = (JSONArray) object;

        List<TopicPO> topicPOList = JSON.parseArray(jsonArray.toString(),TopicPO.class);

        return topicPOList;

    }

    @RequestMapping(value = "/getSubjectAresList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 主题选择的主题域 list 接口", notes = "", position = 0)
    public HotspotsSubjectAresPO getSubjectAresList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                                  @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo",required = true) String typeNo,
                                                  HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);
        List<SubjectAresPO> subjectList = new ArrayList<>();
        List<SubjectAresPO> subjectAresList = new ArrayList<>();
        HotspotsSubjectAresPO hotspotsSubjectAresPO = new HotspotsSubjectAresPO();

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING,projectIdInt);//查询主题分析 的参数
        if(null != workFlowParamList && workFlowParamList.size() > 0){

            for (WorkFlowParam workFlowParam:workFlowParamList) {

                SubjectAresPO subjectAresPO = new SubjectAresPO();

                String subJsonParam = workFlowParam.getJsonParam();
                SubjectSetPO subjectSetPO = JSON.parseObject(subJsonParam,SubjectSetPO.class);

                long subjectAresId =  subjectSetPO.getSubjectAresId();
                String subjectAresName = subjectSetPO.getSubjectAresName();
                subjectAresPO.setId(subjectAresId);
                subjectAresPO.setName(subjectAresName);
                subjectAresPO.setPid(0);

                subjectAresList.add(subjectAresPO);

                String subjectIds = subjectSetPO.getSubjectIds();
                String subJectNames = subjectSetPO.getSubjectNames();
                if(null != subjectIds && !"".equals(subjectIds)){

                    String [] subjectIdArray = subjectIds.split(",");
                    String [] subjectNameArray = subJectNames.split(",");
                    for (int i = 0; i < subjectIdArray.length; i++) {
                        SubjectAresPO subjectAres = new SubjectAresPO();
                        String subIdStr = subjectIdArray[i];
                        long subId = Long.parseLong(subIdStr);
                        String subName = subjectNameArray[i];

                        subjectAres.setId(subId);
                        subjectAres.setName(subName);
                        subjectAres.setPid(subjectAresId);

                        subjectList.add(subjectAres);
                    }
                }
            }
        }

        hotspotsSubjectAresPO.setAres(subjectAresList);
        hotspotsSubjectAresPO.setSubject(subjectList);

        return hotspotsSubjectAresPO;
    }

    @RequestMapping(value = "/getHotspotsSetList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 话题分析设置 接口", notes = "", position = 0)
    public List<HotspotsPO> getHotspotsList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
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
        List<HotspotsPO> HotspotsPOList = new ArrayList<>();//用来装组装好的主题设置分析对象
        if(null != workFlowParamList && workFlowParamList.size() > 0){

            for (WorkFlowParam workFlowParam :workFlowParamList) {
                String hotspotsJsonParamStr = workFlowParam.getJsonParam();

                HotspotsPO hotspotsPO = JSON.parseObject(hotspotsJsonParamStr,HotspotsPO.class);
                hotspotsPO.setParamId(workFlowParam.getParamId());

                HotspotsPOList.add(hotspotsPO);
            }
        }

        return HotspotsPOList;
    }

    @RequestMapping(value = "/saveHotspotsSet.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存 话题分析设置 接口", notes = "", position = 0)
    public CommonResultCodePO saveHotspotsSet(@RequestParam(value = "projectId",required = false)@ApiParam(value = "项目id",required = false) String projectId,
                                                      @RequestParam(value = "typeNo",required = false)@ApiParam(value = "typeNo",required = false) String typeNo,
                                                      @RequestParam(value = "jsonParam",required = true)@ApiParam(value = "jsonParam",required = true) String jsonParam,
                                                      HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);
        HotspotsPO hotspotsPO = null;
        try {
            hotspotsPO = JSON.parseObject(jsonParam,HotspotsPO.class);
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        if(hotspotsPO.getStartFreqType() != 1){
            if(Validate.isEmpty(hotspotsPO.getStartFreqValue())){
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }
        }

        Map<Long,String> subjecMap = new HashMap<>();//聚合 id，对象。方便下面通过id 取名字

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING,projectIdInt);//查询主题分析 的参数
        if(null != workFlowParamList && workFlowParamList.size() > 0){
            for (WorkFlowParam workFlowParam:workFlowParamList) {

                String subJsonParam = workFlowParam.getJsonParam();
                SubjectSetPO subjectSetPO = JSON.parseObject(subJsonParam,SubjectSetPO.class);

                long subjectAresId =  subjectSetPO.getSubjectAresId();
                String subjectAresName = subjectSetPO.getSubjectAresName();

                subjecMap.put(subjectAresId,subjectAresName);

                String subjectIds = subjectSetPO.getSubjectIds();
                String subJectNames = subjectSetPO.getSubjectNames();
                if(null != subjectIds && !"".equals(subjectIds)){

                    String [] subjectIdArray = subjectIds.split(",");
                    String [] subjectNameArray = subJectNames.split(",");
                    for (int i = 0; i < subjectIdArray.length; i++) {
                        String subIdStr = subjectIdArray[i];
                        long subId = Long.parseLong(subIdStr);
                        String subName = subjectNameArray[i];

                        subjecMap.put(subId,subName);

                    }
                }
            }
        }

        int scoringWay = hotspotsPO.getScoringWay();
        switch (scoringWay){
            case 1:
                hotspotsPO.setScoringWayName("标准");
                break;
            case 2:
                hotspotsPO.setScoringWayName("近期加权");
                break;
        }

        String realtionshipSymbol = null;
        int relationship = hotspotsPO.getRelationship();
        switch (relationship){
            case 1:
                realtionshipSymbol = "/";
                break;
            case 2:
                realtionshipSymbol = "&";
                break;
        }

        String definitionName = "";

        List<SubjectIdPO> subjectIdPOList = hotspotsPO.getSubjectAresList();
        if(null != subjectIdPOList && subjectIdPOList.size() > 0){
            for (int i = 0; i < subjectIdPOList.size(); i++) {
                SubjectIdPO subjectIdPO = subjectIdPOList.get(i);

                long aresId = Long.parseLong(subjectIdPO.getAresId());
                String aresName = subjecMap.get(aresId);
                String subjectIds = subjectIdPO.getSubjectIds();
                String[] subjectIdArray = subjectIds.split(",");
                for (String subjectId:subjectIdArray) {

                    String subjectName = subjecMap.get(Long.parseLong(subjectId));

                    if(i == 0){
                        definitionName += subjectName + realtionshipSymbol;
                    }else{
                        definitionName += subjectName + "/";
                    }
                }

                definitionName = definitionName.substring(0,definitionName.length()-1);
                definitionName += "+";

            }
        }

        if(!"".equals(definitionName)){
            definitionName = definitionName.substring(0,definitionName.length()-1);
        }

        hotspotsPO.setDefinitionName(definitionName);

        Long paramId = hotspotsPO.getParamId();

        String quartzTime = "";

        int startFreqType = hotspotsPO.getStartFreqType();
        String startFreqValue = hotspotsPO.getStartFreqValue();
        String startFreqName = "";
        String[] startFreqValueArray = startFreqValue.split(",");
        String timeValue = "";
        for (String value:startFreqValueArray) {
            timeValue += value + ",";
        }
        if(!"".equals(timeValue)){
            timeValue = timeValue.substring(0,timeValue.length()-1);
        }
        switch (startFreqType){
            case 1:
                startFreqName = "单次";
                break;
            case 2:
                quartzTime = "0 0 "+timeValue+" * * ?";
                startFreqName = "每日"+timeValue+"时";
                break;
            case 3:
                quartzTime = "0 0 0 ? * "+timeValue+"";
                startFreqName = "每周"+ timeValue + "";
                break;
            case 4:
                quartzTime = "0 0 0 "+timeValue+" * ?";
                startFreqName = "每月"+timeValue+"日";
                break;
        }

        hotspotsPO.setStartFreqTypeName(startFreqName);

        String jsonParamStr = JSON.toJSONString(hotspotsPO);

        //判断paramId 如果有值 则更新操作 否则新增
        if( null != paramId && paramId > 0){
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setParamId(paramId);
            workFlowParam.setJsonParam(jsonParamStr);
            workFlowService.updateWorkFlowParam(workFlowParam);

            workFlowParam = workFlowService.getWorkFlowParamByParamId(paramId);

            WorkFlowDetail workFlowDetail = new WorkFlowDetail();
            workFlowDetail.setFlowDetailId(workFlowParam.getFlowDetailId());
            workFlowDetail.setQuartzTime(quartzTime);
            workFlowService.updateWorkFlowDetailQuartzTime(workFlowDetail);
        }else{

            workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION, Long.parseLong(projectId));

            String preDetailId = "";
            if(null != workFlowParamList && workFlowParamList.size() > 0){
                WorkFlowParam workFlowParam = workFlowParamList.get(workFlowParamList.size()-1);
                preDetailId = Long.toString(workFlowParam.getFlowDetailId());
            }else {
                String preTypeNo = Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION;
                ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                projectJobTypeBO.setProjectId(Long.parseLong(projectId));
                projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION);
                ProjectJobTypeBO projectJobType = projectJobTypeService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);
                if(null != projectJobType){
                    preTypeNo = projectJobType.getPreTypeNo();
                }
                workFlowParamList = workFlowService.getWorkFlowParamListByParam(preTypeNo,Long.parseLong(projectId));

                if(null != workFlowParamList && workFlowParamList.size() > 0){
                    for (WorkFlowParam workFlowParam:workFlowParamList) {
                        preDetailId += Long.toString(workFlowParam.getFlowDetailId())+",";
                    }

                    if(preDetailId.length() > 0){
                        preDetailId = preDetailId.substring(0,preDetailId.length()-1);
                    }
                }

            }

            workFlowService.addWorkDetail(preDetailId,projectIdInt,typeNo,jsonParamStr,WorkFlowParam.PARAM_TYPE_PRIVATE,quartzTime);
        }

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        return commonResultCodePO;

    }

    @RequestMapping(value = "/deleteHotspotsSet.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除 话题分析设置 接口", notes = "", position = 0)
    public CommonResultCodePO deleteHotspotsSet(@RequestParam(value = "paramId",required = true)@ApiParam(value = "参数id",required = true) String paramId,
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


    @RequestMapping(value = "/getHotpostSemanticResultList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询 话题 语义分析结果 接口", notes = "", position = 0)
    public DataImportListFromShowDataPo getHotpostSemanticResultList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目 id",required = true) String projectId,
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

        long lastIndexIdInt = 0;
        if(!Validate.isEmpty(lastIndexId)){

            if(!lastIndexId.matches("\\d+")){
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }

            lastIndexIdInt = Long.parseLong(lastIndexId);
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
        keyList.remove("themeJSON");
        titleList.remove("分词结果");
        titleList.remove("关键词结果");
        titleList.remove("主题json");

        for(int i= 0;i<titleList.size();i++){
            if(titleList.get(i).equals("indexId")){
                String firstStr = titleList.get(0);
                titleList.set(0,titleList.get(i));
                titleList.set(i,firstStr);
            }else if(titleList.get(i).equals("项目id")){
                String secondStr = titleList.get(1);
                titleList.set(1,titleList.get(i));
                titleList.set(i,secondStr);
            }else if(titleList.get(i).equals("语料id")){
                String secondStr = titleList.get(2);
                titleList.set(2,titleList.get(i));
                titleList.set(i,secondStr);
            }else if(titleList.get(i).equals("段落id")){
                String thirdStr = titleList.get(3);
                titleList.set(3,titleList.get(i));
                titleList.set(i,thirdStr);
            }else if(titleList.get(i).equals("文章id")){
                String thirdStr = titleList.get(4);
                titleList.set(4,titleList.get(i));
                titleList.set(i,thirdStr);
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

        long firstIndexId = 0;
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
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(null != jsonArray && jsonArray.size() > 0) {
                JSONObject jsonObj = jsonArray.getJSONObject(0);
                String indexIdStr = jsonObj.getString("indexId");
                firstIndexId = Long.parseLong(indexIdStr);
            }
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
