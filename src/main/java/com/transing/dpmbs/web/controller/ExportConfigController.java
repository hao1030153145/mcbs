package com.transing.dpmbs.web.controller;

import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.po.DatasourceTypePO;
import com.transing.dpmbs.web.po.DeleteProjectPo;
import com.transing.dpmbs.web.po.OutDataSourcePO;
import com.transing.dpmbs.web.po.StorageTypeFieldPO;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.util.ArrayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 包: com.transing.dpmbs.web.controller
 * 源文件:ExportConfigController.java
 * 文件超出配置controller
 *
 * @author Allen  Copyright 2016 成都创行, Inc. All rights reserved.2017年05月09日
 */
@Controller(value = "exportConfigController")
@RequestMapping(value = "exportConfig")
@Api(value = "文件导出配置", position = 3, description = "文件导出的相关配置")
public class ExportConfigController
{
    @Resource
    private JobTypeService jobTypeService;

    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @RequestMapping(value = "/getExportDataTypeList.json", method = RequestMethod.GET)
    @ApiOperation(value = "查询 导出数据输出类型", position = 1)
    @ResponseBody
    public List<Map<String, String>> queryExportDataTypeList(
            @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id") String projectId,
            HttpServletRequest request)
    {
        if (Validate.isEmpty(projectId) || !projectId.matches("\\d+"))
        {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("id", "1");
        resultMap.put("name", "原始");
        result.add(resultMap);
        List<OutDataSourceBo> outDataSources = jobTypeService
                .getOutDataSource(Long.parseLong(projectId));
        if (outDataSources != null && outDataSources.size() > 0)
        {
            for (OutDataSourceBo bo : outDataSources)
            {
                if (bo.getReusltTypeId() < 0)
                {
                    resultMap = new HashMap<String, String>();
                    resultMap.put("id", "2");
                    resultMap.put("name", "语义数据");
                    result.add(resultMap);
                    Map<String, String> resultMap1 = new HashMap<String, String>();
                    resultMap1.put("id", "3");
                    resultMap1.put("name", "原始+语义数据");
                    result.add(resultMap1);
                    break;
                }
            }
        }
        return result;
    }

    @RequestMapping(value = "getDataTypeRelationList.json", method = RequestMethod.GET)
    @ApiOperation(value = "查询 查询数据类型下的字段值", position = 2)
    @ResponseBody
    public Map<String, Object> queryDataTypeRelationList(
            @RequestParam(value = "dataSourceTypeId", required = true) @ApiParam(value = "数据类型id") String dataSourceTypeId,
            @RequestParam(value = "exportDataTypeId", required = true) @ApiParam(value = "exportDataTypeId") String exportDataTypeId,
            @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
            HttpServletRequest request)
    {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);

        Map<String, Object> map = new HashMap<String, Object>();
        if (Validate.isEmpty(dataSourceTypeId) ||
                !dataSourceTypeId.matches("\\d+"))
        {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (!exportDataTypeId.equalsIgnoreCase("2"))
        {

            JobTypeResultBO bo=jobTypeService.getJobTypeResultByResultTypeId(Long.parseLong(dataSourceTypeId));

            List<RelationOrigin> relationOriginList = null;

            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT,projectIdInt);
            if(!Validate.isEmpty(workFlowParamList)){
                for (WorkFlowParam workFlowParam:workFlowParamList) {
                    String jsonParam = workFlowParam.getJsonParam();
                    ImportData importData = com.alibaba.fastjson.JSON.parseObject(jsonParam, ImportData.class);
                    String typeId = importData.getTypeId();

                    if(bo.getDataSourceType() == Long.parseLong(typeId)){
                        String originRelation = importData.getOrigainRelation();

                        relationOriginList = com.alibaba.fastjson.JSONObject.parseArray(originRelation, RelationOrigin.class);

                        break;
                    }

                }
            }

            List<String> keyList = new ArrayList<>();

            if(!Validate.isEmpty(relationOriginList)) {
                for (RelationOrigin relationOrigin : relationOriginList) {
                    String key = relationOrigin.getKey();
                    if (!Validate.isEmpty(key.trim())) {
                        keyList.add(key);
                    }
                }
            }

            List<String> typeNoList = new ArrayList<>();
            List<ProjectJobTypeBO> projectJobTypeBOList = projectJobTypeService.getProjectJobTypeListByProjectId(projectIdInt);
            if(Validate.isEmpty(projectJobTypeBOList)){
                for (ProjectJobTypeBO projectJobTypeBO:projectJobTypeBOList) {
                    String typeNo = projectJobTypeBO.getTypeNo();
                    typeNoList.add(typeNo);
                }
            }

            List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService
                    .getDataSourceTypeRelationList(bo.getDataSourceType()+"");
            if (storageTypeFieldPOList != null && storageTypeFieldPOList.size() > 0)
            {
                List<Map<String, String>> dataSourceRelations = new ArrayList<Map<String, String>>();
                Map<String, String> relationMap = null;
                if(keyList.size() > 0 && (typeNoList.contains(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT) && !typeNoList.contains(Constants.WORK_FLOW_TYPE_NO_DATACRAWL))){
                    for (StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList)
                    {

                        if(keyList.contains(storageTypeFieldPO.getFieldEnName())){
                            relationMap = new HashMap<String, String>();
                            relationMap.put("fieldId", storageTypeFieldPO.getId().toString());
                            relationMap.put("fieldName", storageTypeFieldPO.getFieldCnName());
                            dataSourceRelations.add(relationMap);
                        }

                    }
                }else {
                    for (StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList)
                    {

                        relationMap = new HashMap<String, String>();
                        relationMap.put("fieldId", storageTypeFieldPO.getId().toString());
                        relationMap.put("fieldName", storageTypeFieldPO.getFieldCnName());
                        dataSourceRelations.add(relationMap);

                    }
                }

                map.put("sourceFieldData", dataSourceRelations);
            }
        }
        if (!exportDataTypeId.equalsIgnoreCase("1"))
        {
            String[] semanticAnalysis = new String[] { "分词", "主题", "话题" };
            List<Map<String, String>> analysisData = new ArrayList<Map<String, String>>();
            Map<String, String> analysisMap = null;
            for (int i = 1; i < 4; i++)
            {
                analysisMap = new HashMap<String, String>();
                analysisMap.put("fieldId", i + "");
                analysisMap.put("fieldName", semanticAnalysis[i - 1]);
                analysisData.add(analysisMap);
            }
            map.put("semanticAnalysisData", analysisData);
            String[] analysisObject = new String[] { "句级", "段级", "文级" };
            List<Map<String, String>> analysisObjectList = new ArrayList<Map<String, String>>();
            Map<String, String> analysisObjectMap = null;
            for (int j = 1; j < 4; j++)
            {
                analysisObjectMap = new HashMap<String, String>();
                analysisObjectMap.put("fieldId", j + "");
                analysisObjectMap.put("fieldName", analysisObject[j - 1]);
                analysisObjectList.add(analysisObjectMap);
            }
            map.put("analysisOjectData", analysisObjectList);
        }
        if (!map.containsKey("sourceFieldData"))
        {
            map.put("sourceFieldData", new ArrayList<Map>());
        }
        if (!map.containsKey("semanticAnalysisData"))
        {
            map.put("semanticAnalysisData", new ArrayList<Map>());
            map.put("analysisOjectData", new ArrayList<Map>());
        }
        return map;
    }

    @RequestMapping(value = "/saveExportConfig.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存 保存文件下载的配置", position = 3)
    public void saveExportConfig(HttpServletRequest request,
            @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id") String projectId,
            @RequestParam(value = "typeNo", required = false) @ApiParam(value = "typeNo") String typeNo,
            @RequestParam(value = "exportName",required = true)@ApiParam(value = "文件名称") String exportName,
            @RequestParam(value = "exportType", required = false) @ApiParam(value = "文件导出方式(excel)") String exportType,
            @RequestParam(value = "exportDataType", required = true) @ApiParam(value = "导出数据类型") String exportDataType,
            @RequestParam(value = "dataSourceType", required = true) @ApiParam(value = "具体数据类型") String dataSourceType,
            @RequestParam(value = "dataSourceTypeField", required = true) @ApiParam(value = "导出数据字段值") String dataSourceTypeField,
            @RequestParam(value = "analysisLevel", required = false) @ApiParam(value = "分析层级") String analysisLevel,
            @RequestParam(value = "analysisObjectName", required = false) @ApiParam(value = "分析对象") String analysisObjectName)
    {
        if (Validate.isEmpty(projectId) || !projectId.matches("\\d+"))
        {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (Validate.isEmpty(typeNo))
        {
            typeNo = "fileOutput";
        }
        if (Validate.isEmpty(exportType))
        {
            exportType = "excel";
        }
        try
        {
            ProjectOne projectOne = projectService
                    .getProjectInf(Long.parseLong(projectId));
            JobTypeResultBO bo=jobTypeService.getJobTypeResultByResultTypeId(Long.parseLong(dataSourceType));
            DatasourceTypePO datasourceTypePO = dataSourceTypeService
                    .getDataSourceTypeById(bo.getDataSourceType());

            StringBuffer relationCHName = new StringBuffer();
            StringBuffer relatoinENName = new StringBuffer();
            List<String> FieldList = Arrays
                    .asList(dataSourceTypeField.split(","));
            List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService
                    .getDataSourceTypeRelationList(bo.getDataSourceType()+"");
            if (storageTypeFieldPOList != null && storageTypeFieldPOList.size() > 0)
            {
                for (StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList)
                {
                    if (FieldList.contains(storageTypeFieldPO.getId().toString()))
                    {
                        relationCHName
                                .append(storageTypeFieldPO.getFieldCnName() + "+");
                        relatoinENName.append(storageTypeFieldPO.getFieldEnName() + "+");
                    }
                }
            }
            String analysisNew = analysisLevel.replace("1", "分词")
                    .replace("2", "主题").replace("3", "话题").replace(",", "+");
            String analysisObject = analysisObjectName.replace("1", "句级")
                    .replace("2", "段级").replace("3", "文级").replace(",", "+");
            JSONObject object = JSONObject.fromObject("{}");
            object.put("exportType", exportType);
            object.put("dataSourceTypeName", datasourceTypePO.getTypeName());
            object.put("storageTypeTable", datasourceTypePO.getStorageTypeTable());
            if(relationCHName.toString().length()>0)
            {
                object.put("queryFieldName", relationCHName.toString()
                        .substring(0, relationCHName.toString().length() - 1));
            }else{
                object.put("queryFieldName","");
            }
            object.put("filename",exportName);
            /*object.put("filename",
                    projectOne.getProjectName() + "_" + dataSourcePo.getName() +
                            "_" + (new Date().getTime()));*/
            object.put("analysisLevel", analysisNew);
            object.put("analysisObjectName", analysisObject);
            JSONObject jsonParamObj = JSONObject.fromObject("{}");
            jsonParamObj.put("exportDataType", exportDataType);
            jsonParamObj.put("dataSourceType", dataSourceType);
            jsonParamObj.put("dataSourceTypeField", dataSourceTypeField);
            if(relatoinENName.toString().length()>0)
            {
                jsonParamObj.put("dataSourceTypeFieldName",
                        relatoinENName.toString().substring(0,
                                relatoinENName.toString().length() - 1));
            }else{
                jsonParamObj.put("dataSourceTypeFieldName","");
            }
            jsonParamObj.put("analysisLevel", analysisLevel);
            jsonParamObj.put("analysisObjectId", analysisObjectName);
            object.put("jsonParam", jsonParamObj);

            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT,Long.parseLong(projectId));

            long preDetailId = 0;
            if(null != workFlowParamList && workFlowParamList.size() > 0){
                WorkFlowParam workFlowParam = workFlowParamList.get(workFlowParamList.size()-1);
                preDetailId = workFlowParam.getFlowDetailId();
            }else {
                String preTypeNo = Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT;
                ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                projectJobTypeBO.setProjectId(Long.parseLong(projectId));
                projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_FILEOUTPUT);
                ProjectJobTypeBO projectJobType = projectJobTypeService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);
                if(null != projectJobType){
                    preTypeNo = projectJobType.getPreTypeNo();
                }

                if(preTypeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                    List<String> typeNoList = new ArrayList<>();
                    typeNoList.add(preTypeNo);
                    typeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);

                    workFlowParamList = workFlowService.getWorkFlowParamListByTypeNoList(typeNoList,Long.parseLong(projectId));
                }else {
                    workFlowParamList = workFlowService.getWorkFlowParamListByParam(preTypeNo,Long.parseLong(projectId));
                }

                if(null != workFlowParamList && workFlowParamList.size() > 0){
                    WorkFlowParam workFlowParam = workFlowParamList.get(workFlowParamList.size()-1);
                    preDetailId = workFlowParam.getFlowDetailId();
                }

            }

            workFlowService.addWorkDetail(Long.toString(preDetailId),Long.parseLong(projectId), typeNo,
                    object.toString(),
                    WorkFlowParam.PARAM_TYPE_PRIVATE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new WebException(SystemCode.SYS_APPSERVER_EXCEPTION);
        }
    }

    @RequestMapping(value = "/getExportFileConfigList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 查询项目中对文件下载的配置信息")
    public List<JSONObject> getExportFileConfigList(
            @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id") String projectId,
            @RequestParam(value = "typeNo", required = false) @ApiParam(value = "typeNo") String typeNo,
            HttpServletRequest request)
    {
        if (!projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        try
        {
            if(Validate.isEmpty(typeNo)){
               typeNo="fileOutput";
            }
            List<JSONObject> resultList = new ArrayList<JSONObject>();
            List<WorkFlowParam> workFlowParams = workFlowService
                    .getWorkFlowParamListByParam(typeNo,
                            Long.parseLong(projectId));
            JSONObject jsonObject = null;
            if (workFlowParams != null && workFlowParams.size() > 0)
            {
                for (WorkFlowParam workFlowParam : workFlowParams)
                {
                    Map<String,String> paramMap=new HashMap<String,String>();
                    paramMap.put("projectId",workFlowParam.getProjectId()+"");
                    paramMap.put("flowId",workFlowParam.getFlowId()+"");
                    paramMap.put("flowDetailId",workFlowParam.getFlowDetailId()+"");
                    paramMap.put("typeNo",workFlowParam.getTypeNo());
                    Long detailId = workFlowParam.getFlowDetailId();
                    WorkFlowDetail detail = workFlowService
                            .getWorkFlowDetailByWorkFlowDetailId(detailId);
                    String jsonParm = workFlowParam.getJsonParam();
                    jsonObject = JSONObject.fromObject(jsonParm);
                    jsonObject.put("jobStatus", detail.getJobStatus());
                    jsonObject.put("paramId", workFlowParam.getParamId());
                    jsonObject.put("jobProgress",detail.getJobProgress()+"");
                    String fileUrl=roteGetResultParamSerivece(paramMap);
                    jsonObject.put("fileUrl",fileUrl);
                    jsonObject.put("errorMsg",detail.getErrorMsg());
                    resultList.add(jsonObject);
                }
            }
            return resultList;
        }
        catch (Exception e)
        {
            throw new WebException(SystemCode.SYS_APPSERVER_EXCEPTION);
        }
    }

    private String roteGetResultParamSerivece(Map<String,String> paramMap){
        try
        {
            StringBuffer stringBuffer=new StringBuffer("");
            JobTypeInfo jobTypeInfo = jobTypeService
                    .getValidJobTypeByTypeNo("fileOutput");
            String url = jobTypeInfo.getResultUrl();
            String roteUrl=WebUtil.getDpmssServerByEnv();
            if(!url.startsWith("http://")){
                url=roteUrl+url;
            }
            HttpClientHelper httpClient = new HttpClientHelper();
            HttpResponse response=httpClient.doPost(url,paramMap,"utf-8","utf-8",null,null);
            if(response.getStatusCode()==200){
                String content=response.getContent();
                JSONObject object=JSONObject.fromObject(content);
                JSONArray data=object.getJSONArray("data");
                if(data!=null&&data.size()>0){
                    int paramId=0;
                    int maxNum=0;
                    for (int i=0;i<data.size();i++)
                    {
                        JSONObject ob= data.getJSONObject(i);
                        if(ob.getInt("paramId")>paramId){
                            paramId=ob.getInt("paramId");
                            maxNum=i;
                        }
                    }
                    JSONObject object1=data.getJSONObject(maxNum);
                    String fileUrl=object1.getJSONObject("resultJsonParam").getString("fileUrl");
                    String webFile=WebUtil.getDpmssFilePathByEnv();
                    if(!Validate.isEmpty(fileUrl)){
                        String [] fileUrls=fileUrl.split(",");
                        for (String urlpath:fileUrls){
                            stringBuffer.append(webFile+"/"+urlpath);
                            stringBuffer.append(",");
                        }
                    }
                }
            }
            return stringBuffer.toString().substring(0,stringBuffer.toString().length()-1);
        }catch (Exception e){
            return "";
        }

    }

    @RequestMapping(value = "/getDataSourceTypeList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "输出数据源列表", notes = "", position = 0)
    public OutDataSourcePO getOutDataSource(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
            HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        OutDataSourcePO outDataSourcePO = new OutDataSourcePO();
        long projectIdInt = Long.parseLong(projectId);

        List<OutDataSourceBo> result = jobTypeService.getOutDataSourceRejectAnlysis(projectIdInt);
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION_MESSAGE);
        }

        outDataSourcePO.setOutDataSourceList(result);
        return outDataSourcePO;
    }

    @RequestMapping(value = "/deleteExportFileConfig.json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "文件导出的配置的删除",position = 3)
    public DeleteProjectPo delExportConfigFileInfo(@RequestParam(value = "paramId",required = true)@ApiParam(value = "参数id",required = true) String paramId){
        DeleteProjectPo deleteProjectPo = new DeleteProjectPo();
        long paramIdInt = Long.parseLong(paramId);
        workFlowService.deleteWorkFlowParam(paramIdInt);
        return deleteProjectPo;
    }

}

