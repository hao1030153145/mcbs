package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.sun.corba.se.impl.io.FVDCodeBaseImpl;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.biz.service.WorkFlowTemplateService;
import com.transing.workflow.constant.Constants;
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
import java.util.*;

@Controller("statisticsAnalysisController")
@Api(value = "统计分析", description = "统计分析相关的访问接口", position = 3)
@RequestMapping(path = "/statisticsAnalysis")
public class StatisticsAnalysisController {
    @Resource
    private WorkFlowService workFlowService;
    @Resource
    private ProjectJobTypeService projectJobTypeService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;
    @Resource
    private WorkFlowTemplateService workFlowTemplateService;

    @RequestMapping(value = "/getStatisticsAnalysisList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取统计分析对象列表 ", notes = "", position = 0)
    public List<StatisticsAnalysisPo> getStatisticsAnalysisList(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
                                                                @RequestParam(value = "typeNo", required = true) @ApiParam(value = "typeNo", required = true) String typeNo) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);
        List<StatisticsAnalysisPo> statisticsAnalysisPoList = new ArrayList<>();

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(typeNo, projectIdInt);

        if (null != workFlowParamList && workFlowParamList.size() > 0) {
            for (WorkFlowParam workFlowParam : workFlowParamList) {
                String jsonStr = workFlowParam.getJsonParam();
                StatisticsAnalysisPo statisticsAnalysisPo = JSON.parseObject(jsonStr, StatisticsAnalysisPo.class);
                statisticsAnalysisPo.setParamId(workFlowParam.getParamId());
                statisticsAnalysisPo.getJsonParam().setParamId(workFlowParam.getParamId());

                WorkFlowDetail detail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(workFlowParam.getFlowDetailId());
                statisticsAnalysisPo.setJobStatus(detail.getJobStatus());
                statisticsAnalysisPo.setJobProgress(detail.getJobProgress());
                statisticsAnalysisPo.setErrorMsg(detail.getErrorMsg());

                statisticsAnalysisPoList.add(statisticsAnalysisPo);
            }
        }
        return statisticsAnalysisPoList;
    }

    @RequestMapping(value = "/saveStatisticsAnalysis.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存 ", notes = "", position = 0)
    public SaveStatisticsAnalysisPO saveStatisticsAnalysis(@RequestParam(value = "jsonParam", required = true) @ApiParam(value = "输入的json", required = true) String jsonParam,
                                                           @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
                                                           @RequestParam(value = "typeNo", required = false) @ApiParam(value = "typeNo", required = false) String typeNo) {
        if (jsonParam == null || "".equals(jsonParam)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);

        StatisticsAnalysisPo statisticsAnalysisPo = new StatisticsAnalysisPo();
        SaveStatisticsAnalysisPO saveStatisticsAnalysisPO = new SaveStatisticsAnalysisPO();
        //解析jsonParam
        StatisticsAnalysisJsonParam statisticsAnalysisJsonParam = JSON.parseObject(jsonParam, StatisticsAnalysisJsonParam.class);
        //组装参数
        statisticsAnalysisPo.setJsonParam(statisticsAnalysisJsonParam);
        statisticsAnalysisPo.setName(statisticsAnalysisJsonParam.getName());
        List<StatisticsAnalysisDataTypePO> statisticsDataTypeList = statisticsAnalysisJsonParam.getDataType();
        List<StatisticsFieldAndFilter> statisticsFieldAndFilterList = statisticsAnalysisJsonParam.getFieldAndFilter();
        String dataTpyeString = "";
        String dimensionString = "";
        String conditionString = "";
        if (statisticsDataTypeList != null) {
            for (StatisticsAnalysisDataTypePO statisticsDataType : statisticsDataTypeList) {
                if(statisticsDataType == null){
                    throw new WebException(MySystemCode.BIZ_PARAMERRO_EXCEPTION);
                }
                List<String> datasourceTypeList = statisticsDataType.getDatasourceType();
                List<String> detailIdList = new ArrayList<>();
                statisticsDataType.setDetailId(detailIdList);
                List<String> typeNoList = new ArrayList<>();
                typeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
                typeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT);

                List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,projectIdInt);
                if(!Validate.isEmpty(workFlowDetailList)){
                    for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                        String dataSourceType = workFlowDetail.getDataSourceType();
                        if(datasourceTypeList.contains(dataSourceType)){
                            detailIdList.add(Long.toString(workFlowDetail.getFlowDetailId()));
                        }
                    }
                }

                String storageTypeTableName = dataSourceTypeService.getStorageTypeList(statisticsDataType.getStorageTypeTableId()).getStorageTypeName();
                dataTpyeString += storageTypeTableName + ",";
            }
        }

        List<StatisticsObject> statisticsObjectList = statisticsAnalysisJsonParam.getStatisticsObject();
        Map<String,Integer> statisticsObjectMap = new HashMap<>();
        if(!Validate.isEmpty(statisticsDataTypeList)){
            for (StatisticsObject statisticsObject:statisticsObjectList) {
                if(null == statisticsObject){
                    throw new WebException(MySystemCode.BIZ_PARAMERRO_EXCEPTION);
                }
                String field = statisticsObject.getField();
                statisticsObjectMap.put(field,1);
            }
        }

        if (statisticsFieldAndFilterList != null) {
            for (StatisticsFieldAndFilter statisticsFieldAndFilter : statisticsFieldAndFilterList) {
                if(null == statisticsFieldAndFilter){
                    throw new WebException(MySystemCode.BIZ_PARAMERRO_EXCEPTION);
                }
                if (statisticsFieldAndFilter.getFieldType().equals("dimension")) {

                    String fild = statisticsFieldAndFilter.getField();
                    Integer i = statisticsObjectMap.get(fild);
                    if(null != i && i > 0){
                        throw new WebException(MySystemCode.BIZ_PARAMERRO_EXCEPTION);
                    }

                    dimensionString += statisticsFieldAndFilter.getFieldName() + "+";
                }else {
                    String conditionType = statisticsFieldAndFilter.getConditionType();
                    String conditionExp = statisticsFieldAndFilter.getConditionExp();
                    if("number".equals(conditionType) && "between".equals(conditionExp)){
                        conditionString += statisticsFieldAndFilter.getFieldName() + "{" + statisticsFieldAndFilter.getConditionValue() + " - "+statisticsFieldAndFilter.getConditionValue2()+"}+";
                    }else if("dateTime".equals(conditionType) && "fromTo".equals(conditionExp)){
                        conditionString += statisticsFieldAndFilter.getFieldName() + "{" + statisticsFieldAndFilter.getConditionValue() + " - "+statisticsFieldAndFilter.getConditionValue2()+"}+";
                    }else {
                        conditionString += statisticsFieldAndFilter.getFieldName() + "{" + statisticsFieldAndFilter.getConditionValue() + "}+";
                    }

                }
            }
        }
        if (dataTpyeString.length() > 0) {
            statisticsAnalysisPo.setDataType(dataTpyeString.substring(0, dataTpyeString.length() - 1));
        } else {
            statisticsAnalysisPo.setDataType(dataTpyeString);
        }
        if (dimensionString.length() > 0) {
            statisticsAnalysisPo.setDimension(dimensionString.substring(0, dimensionString.length() - 1));
        } else {
            statisticsAnalysisPo.setDimension(dimensionString);
        }
        if (conditionString.length() > 0) {
            statisticsAnalysisPo.setCondition(conditionString.substring(0, conditionString.length() - 1));
        } else {
            statisticsAnalysisPo.setCondition(conditionString);
        }
        String jsonParamStr = JSON.toJSONString(statisticsAnalysisPo);

        //更具有误paramId来判断更新还是新增
        Long paramId = statisticsAnalysisJsonParam.getParamId();
        if (null != paramId && paramId > 0) {
            //更新
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setParamId(paramId);
            workFlowParam.setJsonParam(jsonParamStr);
            workFlowService.updateWorkFlowParam(workFlowParam);
        } else {
            //新增
            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_STATISTICAL, Long.parseLong(projectId));
            String preDetailIds = "";
            if (null != workFlowParamList && workFlowParamList.size() > 0) {
                WorkFlowParam workFlowParam = workFlowParamList.get(workFlowParamList.size() - 1);
                preDetailIds = Long.toString(workFlowParam.getFlowDetailId());
            } else {
                String preTypeNo = Constants.WORK_FLOW_TYPE_NO_STATISTICAL;
                ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                projectJobTypeBO.setProjectId(Long.parseLong(projectId));
                projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_STATISTICAL);
                ProjectJobTypeBO projectJobType = projectJobTypeService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);
                if (null != projectJobType) {
                    preTypeNo = projectJobType.getPreTypeNo();
                }

                List<String> typeNoList = new ArrayList<>();
                typeNoList.add(preTypeNo);

                if(preTypeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)
                        ||preTypeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)){

                    List<WorkFlowDetail> workFlowDetailList = workFlowService.getFirstDetailByProjectId(projectIdInt);

                    for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                        long detailId = getLastDetailId(workFlowDetail.getFlowDetailId());
                        preDetailIds += detailId + ",";
                    }
                    if(!Validate.isEmpty(preDetailIds)){
                        preDetailIds = preDetailIds.substring(0,preDetailIds.length()-1);
                    }

                }else {
                    List<WorkFlowDetail> workFlowDetailList =  workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,Long.parseLong(projectId));

                    if(!Validate.isEmpty(workFlowDetailList)){
                        preDetailIds = ""+workFlowDetailList.get(workFlowDetailList.size()-1).getFlowDetailId();
                    }

                }
            }
            workFlowService.addWorkDetail(preDetailIds, projectIdInt, typeNo, jsonParamStr, WorkFlowParam.PARAM_TYPE_PRIVATE, "0");
        }
        return saveStatisticsAnalysisPO;
    }

    public Long getLastDetailId(long detailId){

        WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailId);
        if(null != workFlowDetail){
            Integer workFlowTemplateId = workFlowDetail.getWorkFlowTemplateId();
            if(null != workFlowTemplateId && workFlowTemplateId > 0){
                String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
                if(!Validate.isEmpty(nextFlowDetailIds)){
                    String [] nextFlowDetailIdArray = nextFlowDetailIds.split(",");

                    for (String detailIdStr :nextFlowDetailIdArray) {
                        detailId = Long.parseLong(detailIdStr);
                        WorkFlowDetail nextWorkFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailId);

                        if(nextWorkFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATACRAWL)
                                ||nextWorkFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
                            return getLastDetailId(nextWorkFlowDetail.getFlowDetailId());
                        }else {
                            return workFlowDetail.getFlowDetailId();
                        }

                    }

                }

            }else {
                return workFlowDetail.getFlowDetailId();
            }

        }

        return detailId;
    }

    @RequestMapping(value = "/delStatisticsAnalysis.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除 ", notes = "", position = 0)
    public CommonResultCodePO delStatisticsAnalysis(@RequestParam(value = "paramId", required = true) @ApiParam(value = "paramId", required = true) String paramId) {
        if (paramId == null || "".equals(paramId) || !paramId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long paramIdInt = Long.parseLong(paramId);

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        boolean isSeccful = workFlowService.deleteWorkFlowParam(paramIdInt);
        boolean isError = !isSeccful;
        if (isError) {
            throw new WebException(MySystemCode.BIZ_DELETE_EXCEPTION);
        }

        return commonResultCodePO;
    }

    @RequestMapping(value = "/getDataTypeList.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value="数据类型list",notes = "",position = 0)
    public List<StatisticsDataType> getDataTypeList(@RequestParam(value = "projectId",required = true) @ApiParam(value = "项目id",required = true) String projectId){
        if(projectId!=null&&!projectId.equals("")){
            List<WorkFlowParam> WorkFlowParamList = workFlowService.getWorkFlowParamByProJectId(Long.parseLong(projectId));
            List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailByProjectId(Long.parseLong(projectId));

            /*Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
            }*/

            Map<String,Set<StatisticsDatasourceType>> tableMap = new HashMap<>();
            Map<String,String> datasourceTypeMap = new HashMap<>();
            List<StatisticsDataType> statisticsDataTypeList = new ArrayList<>();
            for(WorkFlowParam workFlowParam :WorkFlowParamList){
                StatisticsDataType statisticsDataType=null;
                if(workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)
                        || workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)
                        || workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)) {

                    StatisticsDatasourceType statisticsDatasourceType = new StatisticsDatasourceType();
                    JSONObject jsonObject = JSONObject.fromObject(workFlowParam.getJsonParam());
                    String storageTypeTable = null;
                    String datasourceTypeId = null;
                    String datasourceTypeName = null;
                    if(workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL) || workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL) ){

                        /*WorkFlowDetail workFlowDetail = workFlowDetailMap.get(workFlowParam.getFlowDetailId());
                        if(null != workFlowDetail.getWorkFlowTemplateId() && workFlowDetail.getWorkFlowTemplateId() > 0){
                            boolean isLastProcess =  isLastProcessDetailId(workFlowDetail,workFlowDetailMap);
                            if(!isLastProcess){
                                continue;
                            }
                        }*/

                        JSONObject jsonObject1 = (JSONObject) jsonObject.get("jsonParam");

                        if(null != jsonObject1){
                            storageTypeTable = (String) jsonObject1.get("storageTypeTable");
                            datasourceTypeId = (String) jsonObject1.get("datasourceTypeId");
                            datasourceTypeName = (String) jsonObject1.get("datasourceTypeName");
                        }else {
                            datasourceTypeId = jsonObject.getString("datasourceTypeId");
                            datasourceTypeName = jsonObject.getString("datasourceTypeName");

                            if(Validate.isEmpty(datasourceTypeId)){
                                continue;
                            }

                            DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(datasourceTypeId));

                            if(null == datasourceTypePO){
                                continue;
                            }

                            storageTypeTable = datasourceTypePO.getStorageTypeTable();

                            if(Validate.isEmpty(storageTypeTable)){
                                continue;
                            }

                        }

                    }else if(workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)){
                        storageTypeTable = (String) jsonObject.get("storageTypeTable");
                        datasourceTypeId = (String) jsonObject.get("typeId");
                        datasourceTypeName = (String) jsonObject.get("typeName");
                    }

                    statisticsDatasourceType.setDatasourceTypeId(datasourceTypeId);
                    statisticsDatasourceType.setDatasourceTypeName(datasourceTypeName);

                    if(null== datasourceTypeMap.get(datasourceTypeId)){
                        Set<StatisticsDatasourceType> statisticsDatasourceTypeList = tableMap.get(storageTypeTable);
                        if(null == statisticsDatasourceTypeList){
                            statisticsDatasourceTypeList = new HashSet<>();
                            tableMap.put(storageTypeTable,statisticsDatasourceTypeList);
                        }
                        statisticsDatasourceTypeList.add(statisticsDatasourceType);

                        datasourceTypeMap.put(datasourceTypeId,storageTypeTable);
                    }
                }
            }
            for (Map.Entry<String,Set<StatisticsDatasourceType>> entry:tableMap.entrySet()) {
                StatisticsDataType statisticsDataType = new StatisticsDataType();
                String storageTypeTable = entry.getKey();
                Set<StatisticsDatasourceType> statisticsDatasourceTypeList = entry.getValue();
                statisticsDataType.setStorageTypeTableId(storageTypeTable);
                statisticsDataType.setDatasourceType(statisticsDatasourceTypeList);
                StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeList(storageTypeTable);
                statisticsDataType.setStorageTypeTableName(storageTypePO.getStorageTypeName());
                statisticsDataTypeList.add(statisticsDataType);
            }
            return statisticsDataTypeList;
        }else{
            return null;
        }
    }

    /**
     * 判断是否是最后一个流程节点
     * @param workFlowDetail
     * @param workFlowDetailMap
     * @return
     */
    boolean isLastProcessDetailId(WorkFlowDetail workFlowDetail,Map<Long,WorkFlowDetail> workFlowDetailMap){
        String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
        String[] nextFlowDetailIdArray = nextFlowDetailIds.split(",");
        for (String detailStr:nextFlowDetailIdArray) {
            long detailIdInt = Long.parseLong(detailStr);
            WorkFlowDetail nextWorkFlowDetail = workFlowDetailMap.get(detailIdInt);
            String typeNo = nextWorkFlowDetail.getTypeNo();
            if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL) || typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL) ){
                return false;
            }
        }

        return true;
    }


    @RequestMapping(value = "/getFieldFilterList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "数据类型list ", notes = "", position = 0)
    public FieldFilterPO getFieldFilterList(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
                                                 @RequestParam(value = "dataType", required = true) @ApiParam(value = "选择的数据类型", required = true) String dataType) {
        if (Validate.isEmpty(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(dataType)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        JSONArray dataTypeJsonArray = null;
        try{
            dataTypeJsonArray = JSONArray.fromObject(dataType);
            if(Validate.isEmpty(dataTypeJsonArray)){
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);

        FieldFilterPO fieldFilterPO = new FieldFilterPO();
        List<FieldCategory> fieldCategoryList = new ArrayList<>();
        List<Condition> conditionList = new ArrayList<>();
        List<FieldType> fieldTypeList = new ArrayList<>();
        List<Field> fieldList = new ArrayList<>();
        List<StatisticsType> statisticsType = new ArrayList<>();
        List<DimensionActionPO> dimensionActionList = new ArrayList<>();


        //-------------原始字段start------------------
        Map<String,Integer> fieldFreq = new HashMap<>();
        Map<String,StorageTypeFieldPO> fieldMap = new HashMap<>();
        for (int i = 0; i < dataTypeJsonArray.size(); i++) {
            JSONObject jsonObject = dataTypeJsonArray.getJSONObject(i);
            JSONArray jsonArray = jsonObject.getJSONArray("datasourceType");

            if(Validate.isEmpty(jsonArray)){
                continue;
            }

            String datasourceTypeId = jsonArray.getString(0);//只需要查询一个数据源类型

            List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(datasourceTypeId);
            if(!Validate.isEmpty(storageTypeFieldPOList)){
                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                    String field = storageTypeFieldPO.getFieldEnName();

                    if(fieldFreq.containsKey(field)){
                        fieldFreq.put(field,fieldFreq.get(field)+1);
                    }else {
                        fieldFreq.put(field,1);
                    }
                    fieldMap.put(field,storageTypeFieldPO);
                }
            }
        }

        FieldCategory sourceFieldCategory = new FieldCategory();
        List<Field> categoryFieldList = new ArrayList<>();
        sourceFieldCategory.setFieldCategory("source");
        sourceFieldCategory.setFieldCategoryName("原始字段");
        sourceFieldCategory.setFieldList(categoryFieldList);
        for (Map.Entry<String,Integer> entry:fieldFreq.entrySet()) {
            String field = entry.getKey();
            int freq = entry.getValue();
            if(freq >= dataTypeJsonArray.size()){
                StorageTypeFieldPO storageTypeFieldPO = fieldMap.get(field);
                Field fieldObj = new Field();
                fieldObj.setField(field);
                fieldObj.setFieldName(storageTypeFieldPO.getFieldCnName());

                if("float".equals(storageTypeFieldPO.getFieldType())){
                    storageTypeFieldPO.setFieldType("int");
                }

                fieldObj.setFieldTypeOf(storageTypeFieldPO.getFieldType());

                categoryFieldList.add(fieldObj);

                fieldList.add(fieldObj);
            }

        }

        fieldCategoryList.add(sourceFieldCategory);
        //----------原始字段end


        //-----------语义字段start---------
        FieldCategory corpusFieldCategory = new FieldCategory();
        corpusFieldCategory.setFieldCategory("corpus");
        corpusFieldCategory.setFieldCategoryName("语义字段");
        List<Field> corpusFieldList = new ArrayList<>();
        corpusFieldCategory.setFieldList(corpusFieldList);
        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING,projectIdInt);//主题分析
        if(null != workFlowParamList && workFlowParamList.size() > 0){

            for (WorkFlowParam workFlowParam :workFlowParamList) {
                String subjectJsonParamStr = workFlowParam.getJsonParam();
                SubjectSetPO subjectSetPO = JSON.parseObject(subjectJsonParamStr,SubjectSetPO.class);
                String subjectAresName = subjectSetPO.getSubjectAresName();

                Field fieldObj = new Field();
                fieldObj.setField("themeJSON@"+subjectAresName);
                fieldObj.setFieldName("主题域:"+subjectAresName);
                fieldObj.setFieldTypeOf("text");

                corpusFieldList.add(fieldObj);
                fieldList.add(fieldObj);
            }
        }

        List<WorkFlowParam> workFlowParamLis = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION,projectIdInt);//话题分析
        if(null != workFlowParamLis && workFlowParamLis.size() > 0){

            for (WorkFlowParam workFlowParam :workFlowParamLis) {
                String hotspotsJsonParamStr = workFlowParam.getJsonParam();
                HotspotsPO hotspotsPO = JSON.parseObject(hotspotsJsonParamStr,HotspotsPO.class);

                String resultName = hotspotsPO.getResultName();

                Field fieldObj = new Field();
                fieldObj.setField("topicJSON@"+resultName);
                fieldObj.setFieldName("话题:"+resultName);
                fieldObj.setFieldTypeOf("text");

                corpusFieldList.add(fieldObj);
                fieldList.add(fieldObj);
            }
        }

        List<WorkFlowParam> workFlowParamLi = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION,projectIdInt);//分词
        if(!Validate.isEmpty(workFlowParamLi)){
            Field fieldObj = new Field();
            fieldObj.setField("splitResult");
            fieldObj.setFieldName("分词");
            fieldObj.setFieldTypeOf("text");

            corpusFieldList.add(fieldObj);
            fieldList.add(fieldObj);

            Field fieldObj2 = new Field();
            fieldObj2.setField("keywordResult");
            fieldObj2.setFieldName("关键词分词");
            fieldObj2.setFieldTypeOf("text");

            corpusFieldList.add(fieldObj2);
            fieldList.add(fieldObj2);
        }

        if(!Validate.isEmpty(corpusFieldList)){
            fieldCategoryList.add(corpusFieldCategory);
        }
        //-----------语义字段end----

        //----条件---start
        Condition classCondition = new Condition();
        classCondition.setTypeOf("text");
        classCondition.setCondition("classification");
        classCondition.setConditionName("分类");
        List<ConditionExp> classConditionExpList = new ArrayList<>();
        classCondition.setConditionExpList(classConditionExpList);
        ConditionExp classConditionExp = new ConditionExp();
        classConditionExp.setConditionExp("be");
        classConditionExp.setConditionExpName("是");
        classConditionExpList.add(classConditionExp);

        ConditionExp classConditionExp2 = new ConditionExp();
        classConditionExp2.setConditionExp("notBe");
        classConditionExp2.setConditionExpName("不是");
        classConditionExpList.add(classConditionExp2);

        ConditionExp classConditionExp3 = new ConditionExp();
        classConditionExp3.setConditionExp("include");
        classConditionExp3.setConditionExpName("包括");
        classConditionExpList.add(classConditionExp3);

        ConditionExp classConditionExp4 = new ConditionExp();
        classConditionExp4.setConditionExp("notInclude");
        classConditionExp4.setConditionExpName("不包括");
        classConditionExpList.add(classConditionExp4);
        conditionList.add(classCondition);

        Condition numberCondition = new Condition();
        numberCondition.setTypeOf("int");
        numberCondition.setCondition("number");
        numberCondition.setConditionName("数值");
        List<ConditionExp> numberConditionExpList = new ArrayList<>();
        numberCondition.setConditionExpList(numberConditionExpList);
        ConditionExp numberConditionExp = new ConditionExp();
        numberConditionExp.setConditionExp("equal");
        numberConditionExp.setConditionExpName("等于");
        numberConditionExpList.add(numberConditionExp);

        ConditionExp numberConditionExp2 = new ConditionExp();
        numberConditionExp2.setConditionExp("gt");
        numberConditionExp2.setConditionExpName("大于");
        numberConditionExpList.add(numberConditionExp2);

        ConditionExp numberConditionExp3 = new ConditionExp();
        numberConditionExp3.setConditionExp("lt");
        numberConditionExp3.setConditionExpName("小于");
        numberConditionExpList.add(numberConditionExp3);

        ConditionExp numberConditionExp4 = new ConditionExp();
        numberConditionExp4.setConditionExp("gte");
        numberConditionExp4.setConditionExpName("大于等于");
        numberConditionExpList.add(numberConditionExp4);

        ConditionExp numberConditionExp5 = new ConditionExp();
        numberConditionExp5.setConditionExp("lte");
        numberConditionExp5.setConditionExpName("小于等于");
        numberConditionExpList.add(numberConditionExp5);

        ConditionExp numberConditionExp6 = new ConditionExp();
        numberConditionExp6.setConditionExp("between");
        numberConditionExp6.setConditionExpName("介于");
        numberConditionExpList.add(numberConditionExp6);

        conditionList.add(numberCondition);

        Condition dateTimeCondition = new Condition();
        dateTimeCondition.setTypeOf("datetime");
        dateTimeCondition.setCondition("dateTime");
        dateTimeCondition.setConditionName("时间");
        List<ConditionExp> dateTimeConditionExpList = new ArrayList<>();
        dateTimeCondition.setConditionExpList(dateTimeConditionExpList);
        ConditionExp dateConditionExp = new ConditionExp();
        dateConditionExp.setConditionExp("specific");
        dateConditionExp.setConditionExpName("指定");
        dateTimeConditionExpList.add(dateConditionExp);

        ConditionExp dateConditionExp2 = new ConditionExp();
        dateConditionExp2.setConditionExp("fromTo");
        dateConditionExp2.setConditionExpName("起止");
        dateTimeConditionExpList.add(dateConditionExp2);

        conditionList.add(dateTimeCondition);

        //---条件end----

        //-------维度/条件start---------

        FieldType fieldType = new FieldType();
        fieldType.setName("维度");
        fieldType.setValue("dimension");
        fieldTypeList.add(fieldType);

        FieldType fieldType2 = new FieldType();
        fieldType2.setName("条件");
        fieldType2.setValue("condition");
        fieldTypeList.add(fieldType2);

        //-------维度/条件end---------

        //--------------维度操作start--------
        DimensionActionPO dimensionActionPO = new DimensionActionPO();
        dimensionActionPO.setAction("split");
        dimensionActionPO.setActionName("拆分");
        dimensionActionPO.setTypeOf("text");
        dimensionActionList.add(dimensionActionPO);

        DimensionActionPO dimensionActionPO2 = new DimensionActionPO();
        dimensionActionPO2.setAction("format");
        dimensionActionPO2.setActionName("格式化");
        dimensionActionPO2.setTypeOf("datetime");

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key","yyyy");
        jsonObject.put("value","按年");
        jsonArray.add(jsonObject);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("key","yyyy-MM");
        jsonObject2.put("value","按月");
        jsonArray.add(jsonObject2);

        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("key","yyyy-MM-dd");
        jsonObject3.put("value","按日");
        jsonArray.add(jsonObject3);

        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("key","yyyy-MM-dd HH");
        jsonObject4.put("value","按小时");
        jsonArray.add(jsonObject4);

        dimensionActionPO2.setActionProp(jsonArray);
        dimensionActionList.add(dimensionActionPO2);



        //--------------维度操作end----------


        //----------统计字段start------

        StatisticsType statisticsTypeObj = new StatisticsType();
        statisticsTypeObj.setTypeOf("text");
        statisticsTypeObj.setType("count");
        statisticsTypeObj.setTypeName("计数");
        statisticsType.add(statisticsTypeObj);

        StatisticsType statisticsTypeObj2 = new StatisticsType();
        statisticsTypeObj2.setTypeOf("int");
        statisticsTypeObj2.setType("average");
        statisticsTypeObj2.setTypeName("平均值");
        statisticsType.add(statisticsTypeObj2);

        StatisticsType statisticsTypeObj3 = new StatisticsType();
        statisticsTypeObj3.setTypeOf("int");
        statisticsTypeObj3.setType("sum");
        statisticsTypeObj3.setTypeName("求和");
        statisticsType.add(statisticsTypeObj3);

        StatisticsType statisticsTypeObj4 = new StatisticsType();
        statisticsTypeObj4.setTypeOf("int");
        statisticsTypeObj4.setType("max");
        statisticsTypeObj4.setTypeName("最大值");
        statisticsType.add(statisticsTypeObj4);

        StatisticsType statisticsTypeObj5 = new StatisticsType();
        statisticsTypeObj5.setTypeOf("int");
        statisticsTypeObj5.setType("min");
        statisticsTypeObj5.setTypeName("最小值");
        statisticsType.add(statisticsTypeObj5);

        StatisticsType statisticsTypeObj6 = new StatisticsType();
        statisticsTypeObj6.setTypeOf("int");
        statisticsTypeObj6.setType("middle");
        statisticsTypeObj6.setTypeName("中位值");
        statisticsType.add(statisticsTypeObj6);
        //----------统计字段end------

        fieldFilterPO.setConditionList(conditionList);
        fieldFilterPO.setFieldCategoryList(fieldCategoryList);
        fieldFilterPO.setFieldList(fieldList);
        fieldFilterPO.setFieldTypeList(fieldTypeList);
        fieldFilterPO.setStatisticsType(statisticsType);
        fieldFilterPO.setDimensionActionList(dimensionActionList);

        return fieldFilterPO;
    }

    @RequestMapping(value = "/getAnalysisHierarchyList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "语义层级list ", notes = "", position = 0)
    public List<StatisticsAnalysisHierarchyPO> getAnalysisHierarchyList(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        List<StatisticsAnalysisHierarchyPO> statisticsAnalysisHierarchyPOList = new ArrayList<>();
        long projectIdInt = Long.parseLong(projectId);

        ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
        projectJobTypeBO.setProjectId(projectIdInt);
        projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT);
        ProjectJobTypeBO projectJobType = projectJobTypeService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);
        if (projectJobType != null) {
            StatisticsAnalysisHierarchyPO statisticsAnalysisHierarchyPOArticle = new StatisticsAnalysisHierarchyPO();
            statisticsAnalysisHierarchyPOArticle.setName("文级");
            statisticsAnalysisHierarchyPOArticle.setValue("article");
            StatisticsAnalysisHierarchyPO statisticsAnalysisHierarchyPOSentence = new StatisticsAnalysisHierarchyPO();
            statisticsAnalysisHierarchyPOSentence.setName("段级");
            statisticsAnalysisHierarchyPOSentence.setValue("section");
            StatisticsAnalysisHierarchyPO statisticsAnalysisHierarchySection = new StatisticsAnalysisHierarchyPO();
            statisticsAnalysisHierarchySection.setName("句级");
            statisticsAnalysisHierarchySection.setValue("sentence");
            statisticsAnalysisHierarchyPOList.add(statisticsAnalysisHierarchyPOArticle);
            statisticsAnalysisHierarchyPOList.add(statisticsAnalysisHierarchyPOSentence);
            statisticsAnalysisHierarchyPOList.add(statisticsAnalysisHierarchySection);
        }
        return statisticsAnalysisHierarchyPOList;
    }
}
