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
import com.transing.dpmbs.biz.service.ContentTypeService;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ParamService;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.ParamBO;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowInfo;
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
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller("semanticAnalysisObjectController")
@Api(value = "语义分析", description = "语义分析相关接口接口", position = 2)
@RequestMapping("/semanticAnalysisObject")
public class SemanticAnalysisObjectController {

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @Resource
    private ContentTypeService contentTypeService;
    @Resource
    private ParamService paramService;

    @RequestMapping(value = "/getAnalysisObjectList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 语义分析 接口", notes = "", position = 0)
    public CommonListPO getAnalysisObjectList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                                      @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo",required = true) String typeNo,
                                                      HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);
        List<SemanticAnalysisObjectPO> semanticAnalysisObjectPOList = new ArrayList<>();

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(typeNo,projectIdInt);

        List<String> typeNoList = new ArrayList<>();
        typeNoList.add(typeNo);
        List<WorkFlowDetail> detailList = workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,projectIdInt);
        Map<Long,WorkFlowDetail>detailMap = new HashMap<>();

        for (WorkFlowDetail workFlowDetail:detailList) {
            detailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
        }

        if(null != workFlowParamList && workFlowParamList.size() > 0){
            for (WorkFlowParam workFlowParam :workFlowParamList) {
                WorkFlowDetail workFlowDetail = detailMap.get(workFlowParam.getFlowDetailId());
                SemanticAnalysisObjectPO semanticAnalysisObjectPO = JSON.parseObject(workFlowParam.getJsonParam(),SemanticAnalysisObjectPO.class);
                semanticAnalysisObjectPO.setParamId(workFlowParam.getParamId());

                semanticAnalysisObjectPO.setJobStatus(workFlowDetail.getJobStatus());
                semanticAnalysisObjectPO.setJobProgress(workFlowDetail.getJobProgress());

                semanticAnalysisObjectPOList.add(semanticAnalysisObjectPO);
            }
        }

        CommonListPO commonListPO = new CommonListPO();
        commonListPO.setList(semanticAnalysisObjectPOList);

        return commonListPO;
    }

    @RequestMapping(value = "/getContentTypeList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 语义分析 对象 下拉选项 接口", notes = "", position = 0)
    public List<ContentTypePO> getContentTypeList(@RequestParam(value = "dataSourceTypeId",required = true)@ApiParam(value = "数据源类型 id",required = true) String dataSourceTypeId,
                                              HttpServletRequest req, HttpServletResponse res) {

        if (dataSourceTypeId == null || "".equals(dataSourceTypeId) || !dataSourceTypeId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long dataSourceTypeIdInt = Long.parseLong(dataSourceTypeId);

        List<ContentTypePO> contentTypePOList = new ArrayList<>();
        contentTypePOList = contentTypeService.getContentTypeList(dataSourceTypeIdInt);

        return contentTypePOList;
    }


    @RequestMapping(value = "/getSemanticAnalysisObjectListFromShowData.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询 语义分析结果 接口", notes = "", position = 0)
    public DataImportListFromShowDataPo getSemanticResultList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目 id",required = true) String projectId,
                                              @RequestParam(value = "paramId",required = true)@ApiParam(value = "项目 id",required = true) String paramId,
                                              @RequestParam(value = "type",required = true)@ApiParam(value = "分词结果的类型(sentence-句级,section-段级,article-文级)",required = true) String type,
                                              @RequestParam(value = "flag", required = false) @ApiParam(value = "flag", required = false) String flag,
                                              @RequestParam(value = "size",required = true)@ApiParam(value = "查询记录数 默认 10 条",required = true) String size,
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

        if("next".equals(flag)){
            flag = "gt";
        }else if("pre".equals(flag)){
            flag = "lt";
        }

        long paramIdInt = Long.parseLong(paramId);
        long sizeInt = null != size&&!"".equals(size)?Long.parseLong(size):10;

        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);
        if(null == workFlowParam){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        String jsonParam = workFlowParam.getJsonParam();
        SemanticAnalysisObjectPO semanticAnalysisObjectPO = JSON.parseObject(jsonParam,SemanticAnalysisObjectPO.class);

        String dataTypeName = Long.toString(semanticAnalysisObjectPO.getJsonParam().getDataSourceTypeId());

        List<List> dataList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();

        DataImportListFromShowDataPo dataImportListFromShowDataPo = new DataImportListFromShowDataPo();

        long firstIndexId = 0;
        Map<String,String> postData = new HashMap<>();
        postData.put("resultType",type);
        postData.put("filterJSON","{\"projectID\":"+projectId+",\"dataSourceType\":\""+dataTypeName+"\"}");
        postData.put("startRow","0");
        postData.put("flag","gt");
        postData.put("rows","1");

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
        postData2.put("filterJSON","{\"projectID\":"+projectId+",\"dataSourceType\":\""+dataTypeName+"\"}");
        postData2.put("flag",flag);
        postData2.put("rows",String.valueOf(sizeInt));

        Object resultObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getSemanticAnalysisDataList.json", "post", postData2);
        if(null != resultObject){
            JSONObject jsonObject = (JSONObject) resultObject;
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(null != jsonArray && jsonArray.size() > 0){
                JSONObject jsonObj = jsonArray.getJSONObject(0);
                Set<String> keySet = jsonObj.keySet();

                for (String key:keySet) {
                    titleList.add(key);
                }

                for (int i = 0; i < jsonArray.size(); i++) {
                    List<String> data = new ArrayList<>();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    for (String key:keySet) {
                        Object result = jsonObject1.get(key);
                         if(null != result && !"".equals(result)){
                             data.add(result.toString());
                         }else {
                             data.add("");
                         }
                    }
                    dataList.add(data);
                }
            }
        }

        dataImportListFromShowDataPo.setDataList(dataList);
        dataImportListFromShowDataPo.setTitleList(titleList);

        return dataImportListFromShowDataPo;
    }


    @RequestMapping(value = "/saveAnalysisObjectList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存 语义分析 接口", notes = "", position = 0)
    public CommonResultCodePO saveAnalysisObjectList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
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
        SemanticAnalysisObjectPO semanticAnalysisObjectPO = null;

        try {
            semanticAnalysisObjectPO = JSON.parseObject(jsonParam, SemanticAnalysisObjectPO.class);//转换 传上来的jsonParam
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        SemanticJsonParam semanticJsonParam = semanticAnalysisObjectPO.getJsonParam();
        /*SemanticJsonParam semanticJsonParam = null;
        try {
            semanticJsonParam = JSON.parseObject(jsonParam, SemanticJsonParam.class);//转换 传上来的jsonParam
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }*/

        //聚合数据源类型 id 对应名字
        /*Map<Long,String> dataSourceTypeNameMap = new HashMap<>();
        List<DataSourceType> dataSourceTypeList = dataSourceTypeService.getDataSourceTypeList();
        for (DataSourceType dataSourceType:dataSourceTypeList) {
            dataSourceTypeNameMap.put(dataSourceType.getId(),dataSourceType.getName());
        }*/

        String analysisObjectName = "";//分析对象
        String sentence = "";//句级
        String section = "";//段级
        String article = "";//文级

        //设置 数据源类型id 以及名字
        long dataSourceTypeId = semanticJsonParam.getDataSourceTypeId();
        String dataSourceTypeName = semanticJsonParam.getDataSourceTypeName();
        semanticAnalysisObjectPO.setDataSourceTypeName(dataSourceTypeName);
        semanticJsonParam.setDataSourceTypeName(dataSourceTypeName);

        Set<Long> dataSourceTypeSet = new HashSet<>();

        //封装 分析层级
        List<AnalysisHierarchy> analysisHierarchyList = semanticJsonParam.getAnalysisHierarchy();
        if(null != analysisHierarchyList && analysisHierarchyList.size() > 0){
            for (AnalysisHierarchy analysisHierarchy:analysisHierarchyList) {
                String hierarchy = analysisHierarchy.getHierarchy();
                String [] hierarchyArray = hierarchy.split(",");
                List<String> hierarchyList = Arrays.asList(hierarchyArray);
                int type = analysisHierarchy.getType();
                switch (type){
                    case 1:
                        if(hierarchyList.contains("1")){
                            sentence += "分词+";
                        }
                        if(hierarchyList.contains("2")){
                            section += "分词+";
                        }
                        if(hierarchyList.contains("3")){
                            article += "分词+";
                        }
                        break;
                    case 2:
                        if(hierarchyList.contains("1")){
                            sentence += "主题+";
                        }
                        if(hierarchyList.contains("2")){
                            section += "主题+";
                        }
                        if(hierarchyList.contains("3")){
                            article += "主题+";
                        }
                        break;
                    case 3:
                        if(hierarchyList.contains("1")){
                            sentence += "话题+";
                        }
                        if(hierarchyList.contains("2")){
                            section += "话题+";
                        }
                        if(hierarchyList.contains("3")){
                            article += "话题+";
                        }
                        break;
                }

            }

            if(!"".equals(sentence)){
                dataSourceTypeSet.add(-1L);
                sentence = sentence.substring(0,sentence.length() - 1);
            }else{
                sentence = "无";
            }
            if(!"".equals(section)){
                dataSourceTypeSet.add(-2L);
                section = section.substring(0,section.length() - 1);
            }else{
                section = "无";
            }
            if(!"".equals(article)){
                dataSourceTypeSet.add(-3L);
                article = article.substring(0,article.length() - 1);
            }else{
                article = "无";
            }
        }

        List<Long> dataSourceTypesList = new ArrayList<>();
        if(!Validate.isEmpty(dataSourceTypeSet)){
            dataSourceTypesList.addAll(dataSourceTypeSet);
        }

        semanticAnalysisObjectPO.setSentence(sentence);
        semanticAnalysisObjectPO.setSection(section);
        semanticAnalysisObjectPO.setArticle(article);

        //设置分析对象
         List<AnalysisObject> analysisObjectList = semanticJsonParam.getAnalysisObject();

        List<ContentTypePO> contentTypePOList = contentTypeService.getContentTypeList(dataSourceTypeId);
        Map<String,String> contentTypeMap = new HashMap<>();
        if(null != contentTypePOList && contentTypePOList.size() > 0){
            for (ContentTypePO contentTypePO:contentTypePOList) {
                contentTypeMap.put(contentTypePO.getContentType(),contentTypePO.getContentTypeName());
            }
        }

        if(null != analysisObjectList && analysisObjectList.size() > 0){
            for (AnalysisObject analysisObject:analysisObjectList) {
                String contentType =  analysisObject.getContentType();
                String contentTypeName = contentTypeMap.get(contentType);

                analysisObjectName += contentTypeName;//追加 分析对象名字

                int subType = analysisObject.getSubType();
                int value = analysisObject.getValue();

                switch (subType){
                    case 1:
                        analysisObjectName += "全文+";
                        break;
                    case 2:
                        analysisObjectName += value+"字+";
                        break;
                    case 3:
                        analysisObjectName += value+"段+";
                        break;
                    case 4:
                        analysisObjectName += value + "句+";
                        break;
                }
            }
            analysisObjectName = analysisObjectName.substring(0,analysisObjectName.length()-1);
        }

        semanticAnalysisObjectPO.setAnalysisObjectName(analysisObjectName);

        semanticAnalysisObjectPO.setJsonParam(semanticJsonParam);

        String jsonParamStr = JSON.toJSONString(semanticAnalysisObjectPO);

        Long paramId = semanticAnalysisObjectPO.getParamId();
        if( null != paramId && paramId > 0){
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setParamId(paramId);
            workFlowParam.setJsonParam(jsonParamStr);
            workFlowService.updateWorkFlowParam(workFlowParam,dataSourceTypesList);//同时更新 输出字段
        }else{
            //workFlowService.addWorkDetail(projectIdInt,typeNo,jsonParamStr,WorkFlowParam.PARAM_TYPE_COMON);
        }

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        return commonResultCodePO;

    }

    @RequestMapping(value = "/getAnalysisObjectHomeList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 语义分析对象，分词，主题，话题list 接口", notes = "", position = 0)
    public SegSubjectHotpostHomtShowPO getAnalysisObjectHomeList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                                                @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo（多个英文都好隔开）",required = true) String typeNo,
                                                                HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);

        String [] typeNoArray = typeNo.split(",");
        List<String> typeNoList = new ArrayList<>();
        for (String typeNoStr:typeNoArray) {
            typeNoList.add(typeNoStr);
        }
        typeNoList.add(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT);

        List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,projectIdInt);
        Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
        if(null != workFlowDetailList && workFlowDetailList.size() > 0){
            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
            }
        }

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByTypeNoList(typeNoList,projectIdInt);
        Map<String,List<WorkFlowParam>> workFlowParamMap = new HashMap<>();
        if(null != workFlowParamList && workFlowParamList.size() > 0){
            for (WorkFlowParam workFlowParam:workFlowParamList) {
                String paramTypeNo = workFlowParam.getTypeNo();
                List<WorkFlowParam> workFlowParamLi = workFlowParamMap.get(paramTypeNo);
                if(null == workFlowParamLi){
                    workFlowParamLi = new ArrayList<>();
                    workFlowParamMap.put(paramTypeNo,workFlowParamLi);
                }
                workFlowParamLi.add(workFlowParam);
            }
        }

        List<SegHomeShowPO> segHomeShowPOList = new ArrayList<>();
        List<SubjectHomeShowPO> subjectShowList = new ArrayList<>();
        List<HotspotsHomeShowPO> hotspotsPOList = new ArrayList<>();

        List<WorkFlowParam> segList = workFlowParamMap.get(Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION);
        List<WorkFlowParam> subjectList = workFlowParamMap.get(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING);
        List<WorkFlowParam> hotspotsList = workFlowParamMap.get(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION);

        if(!Validate.isEmpty(segList)){
            for (WorkFlowParam segWorkParam:segList) {
                SegHomeShowPO segHomeShowPO = new SegHomeShowPO();

                String jsonParam = segWorkParam.getJsonParam();
                String wordLiraryNames = "";

                SegSetPO segSetPO = JSON.parseObject(jsonParam,SegSetPO.class);
                List<WordLibraryPO> wordLibraryPOList = segSetPO.getWordLibraryList();
                if(null != wordLibraryPOList && wordLibraryPOList.size() > 0){
                    for (WordLibraryPO wordLibraryPO:wordLibraryPOList) {
                        wordLiraryNames += wordLibraryPO.getName()+",";
                    }
                }
                if(!Validate.isEmpty(wordLiraryNames)){
                    wordLiraryNames = wordLiraryNames.substring(0,wordLiraryNames.length()-1);
                }

                segHomeShowPO.setWordLibrarys(wordLiraryNames);

                switch (segSetPO.getActionType()){
                    case 0:
                        segHomeShowPO.setActionTypeName("不用新词");
                        break;
                    case 1:
                        segHomeShowPO.setActionTypeName("自动判断");
                        break;
                }

                segHomeShowPO.setParamId(segWorkParam.getParamId());
                WorkFlowDetail segWorkDetail = workFlowDetailMap.get(segWorkParam.getFlowDetailId());

                segHomeShowPO.setJobProgress(segWorkDetail.getJobProgress());
                segHomeShowPO.setJobStatus(segWorkDetail.getJobStatus());
                segHomeShowPO.setErrorMsg(segWorkDetail.getErrorMsg());

                segHomeShowPOList.add(segHomeShowPO);

            }
        }

        if(!Validate.isEmpty(subjectList)){
            for (WorkFlowParam subjectWorkFlowParam:subjectList) {

                String jsonParam = subjectWorkFlowParam.getJsonParam();
                long paramId = subjectWorkFlowParam.getParamId();
                WorkFlowDetail subjectWorkFlowDetail = workFlowDetailMap.get(subjectWorkFlowParam.getFlowDetailId());

                SubjectHomeShowPO subjectHomeShowPO = JSON.parseObject(jsonParam,SubjectHomeShowPO.class);

                subjectHomeShowPO.setJobStatus(subjectWorkFlowDetail.getJobStatus());
                subjectHomeShowPO.setJobProgress(subjectWorkFlowDetail.getJobProgress());
                subjectHomeShowPO.setErrorMsg(subjectWorkFlowDetail.getErrorMsg());
                subjectHomeShowPO.setParamId(paramId);

                subjectShowList.add(subjectHomeShowPO);

            }
        }

        if(!Validate.isEmpty(hotspotsList)){
            for (WorkFlowParam hotspotsWorkFlowParam:hotspotsList) {
                String jsonParam = hotspotsWorkFlowParam.getJsonParam();
                long paramId = hotspotsWorkFlowParam.getParamId();
                WorkFlowDetail hotpotsWorkFlowDetail = workFlowDetailMap.get(hotspotsWorkFlowParam.getFlowDetailId());

                HotspotsHomeShowPO hotspotsHomeShowPO = JSON.parseObject(jsonParam,HotspotsHomeShowPO.class);

                hotspotsHomeShowPO.setParamId(paramId);
                hotspotsHomeShowPO.setJobProgress(hotpotsWorkFlowDetail.getJobProgress());
                hotspotsHomeShowPO.setJobStatus(hotpotsWorkFlowDetail.getJobStatus());
                hotspotsHomeShowPO.setErrorMsg(hotpotsWorkFlowDetail.getErrorMsg());

                hotspotsPOList.add(hotspotsHomeShowPO);

            }
        }


        SegSubjectHotpostHomtShowPO segSubjectHotpostHomtShowPO = new SegSubjectHotpostHomtShowPO();
        segSubjectHotpostHomtShowPO.setSegHomeShowPOList(segHomeShowPOList);
        segSubjectHotpostHomtShowPO.setHotspotsHomeShowPOList(hotspotsPOList);
        segSubjectHotpostHomtShowPO.setSubjectHomeShowPOList(subjectShowList);

        return segSubjectHotpostHomtShowPO;
    }

    @RequestMapping(value = "/getAnalysisLevel.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询语义层级", notes = "", position = 0)
    public List<Map<String,String>> getAnalysisLevel(){
        List<Map<String,String>> resultList = new ArrayList<>();
        String analysisLevel = Constants.PARAM_TYPE;
        List<ParamBO>list =  paramService.getParamBoListByType(analysisLevel);
        if(!Validate.isEmpty(list)){
            for(ParamBO paramBO : list){
                Map<String,String> resultMap = new HashMap<>();
                resultMap.put("key",paramBO.getKey());
                resultMap.put("value",paramBO.getValue());
                resultList.add(resultMap);
            }
        }else{
            throw new WebException(MySystemCode.BIZ_DATA_QUERY_EXCEPTION_MESSAGE);
        }
        return resultList;
    }


}
