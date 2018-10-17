package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.util.encrypt.MD5Util;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.VisWorkFlowService;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.util.XmlExerciseUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("exportDataController")
@Api(value = "output", description = "输出数据相关的访问接口", position = 3)
@RequestMapping(path = "/export")
public class ExportDataController {
    @Resource
    private WorkFlowService workFlowService;
    @Resource
    private JobTypeService jobTypeService;
    @Resource
    private ProjectJobTypeService projectJobTypeService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;
    @Resource
    private VisWorkFlowService visWorkFlowService;

    @RequestMapping(value = "/getOutDataSource.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "输出数据源列表", notes = "", position = 0)
    public OutDataSourcePO getOutDataSource(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
                                            HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        OutDataSourcePO outDataSourcePO = new OutDataSourcePO();
        long projectIdInt = Long.parseLong(projectId);

        List<OutDataSourceBo> result = jobTypeService.getOutDataSource(projectIdInt);
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION_MESSAGE);
        }

        outDataSourcePO.setOutDataSourceList(result);
        return outDataSourcePO;
    }

    @RequestMapping(value = "/getOutDataSourceDetail.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "输出数据源 查询字段 选择字段 列表", notes = "", position = 0)
    public OutDataSourceDetailPO getOutDataSourceDetail(@RequestParam(value = "resultTypeId", required = true) @ApiParam(value = "输出结果类型id", required = true) String resultTypeId,
                                                        @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
                                                        HttpServletRequest req, HttpServletResponse res) {
        if (resultTypeId == null || "".equals(resultTypeId)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);

        OutDataSourceDetailPO outDataSourceDetailPO = new OutDataSourceDetailPO();
        long resultTypeIdInt = Long.parseLong(resultTypeId);

        List<OutDataSourceDetailBo> result = jobTypeService.getOutDataSourceDetail(resultTypeIdInt);
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION_MESSAGE);
        }

        List<OutDataSourceDemoParamter> result2 = jobTypeService.getOutDataSourceDemoParamterList(resultTypeIdInt);
        OutDataSourceDemoParamter outDataSourceDemoParamter = new OutDataSourceDemoParamter();
        outDataSourceDemoParamter.setFieldName("page");
        outDataSourceDemoParamter.setFieldDesc("页码，默认为1");
        result2.add(outDataSourceDemoParamter);

        OutDataSourceDemoParamter outDataSourceDemoParamter2 = new OutDataSourceDemoParamter();

        outDataSourceDemoParamter2.setFieldName("size");
        outDataSourceDemoParamter2.setFieldDesc("一页显示记录数 默认为 15");
        result2.add(outDataSourceDemoParamter2);
        if (result2 == null) {
            throw new WebException(SystemCode.BIZ_LOGIN_PASSNOTRIGHT_EXCEPTION);
        }
        //T调用调度系统接口
        String url = "";
        outDataSourceDetailPO.setOutDataSourceDetailBoList(result);
        outDataSourceDetailPO.setOutDataSourceDemoParamterList(result2);
        outDataSourceDetailPO.setUrl("/export/getDataApi.json?startRow=0&rows=15");
        return outDataSourceDetailPO;

    }

    @RequestMapping(value = "/saveOutput.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加输出项", notes = "", position = 0)
    public AddOutputPO addOutput(@RequestParam(value = "name", required = true) @ApiParam(value = "名称", required = true) String name,
                                 @RequestParam(value = "typeName", required = true) @ApiParam(value = "数据类型名称", required = true) String typeName,
                                 @RequestParam(value = "type", required = true) @ApiParam(value = "数据类型id", required = true) String type,
                                 @RequestParam(value = "apiType", required = true) @ApiParam(value = "输出类型", required = true) String apiType,
                                 @RequestParam(value = "mapRelationValue", required = false) @ApiParam(value = "勾选对应的id", required = false) String mapRelationValue,
                                 @RequestParam(value = "mapRelationValueName", required = false) @ApiParam(value = "勾选对应的id", required = false) String mapRelationValueName,
                                 @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id") String projectId,
                                 @RequestParam(value = "typeNo", required = true) @ApiParam(value = "typeNo", required = true) String typeNo,
                                 @RequestParam(value = "paramId", required = false) @ApiParam(value = "typeNo", required = true) String paramId,
                                 HttpServletRequest req, HttpServletResponse res) {
        if(Validate.isEmpty(type) || !type.matches("\\d+")){
            throw new WebException(MySystemCode.SYS_REQUEST_EXCEPTION);
        }
        long datasourceTypeId = Long.parseLong(type);

        AddOutputPO addOutputPO = new AddOutputPO();

        long projectIdInt = Long.parseLong(projectId);

        String storageTypeTable = "";
        if(datasourceTypeId > 0){
            DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(datasourceTypeId);
            typeName = datasourceTypePO.getTypeName();
            storageTypeTable = datasourceTypePO.getStorageTypeTable();
        }else{
            if(datasourceTypeId == -1){
                typeName = "sentence";
            }else if(datasourceTypeId == -2){
                typeName = "section";
            }else if(datasourceTypeId == -3){
                typeName = "article";
            }
        }

        OutPutData outPutData = new OutPutData(name, typeName, type, apiType, mapRelationValue, mapRelationValueName,storageTypeTable);
        String jsonParam = JSONObject.toJSONString(outPutData);
        if (paramId == null) {

            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT,Long.parseLong(projectId));

            long preDetailId = 0;
            if(null != workFlowParamList && workFlowParamList.size() > 0){
                WorkFlowParam workFlowParam = workFlowParamList.get(workFlowParamList.size()-1);
                preDetailId = workFlowParam.getFlowDetailId();
            }else {
                String preTypeNo = Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT;
                ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                projectJobTypeBO.setProjectId(Long.parseLong(projectId));
                projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT);
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

            long paramIdInt = workFlowService.addWorkDetail(Long.toString(preDetailId),projectIdInt, typeNo, jsonParam, WorkFlowParam.PARAM_TYPE_PRIVATE);

            WorkFlowParam workFlowParam = new WorkFlowParam();

            outPutData.setUrl("/export/getDataApi.json?paramId=" + paramIdInt + "&page=1&size=15");
            String jsonPar = JSONObject.toJSONString(outPutData);
            workFlowParam.setParamId(paramIdInt);
            workFlowParam.setJsonParam(jsonPar);
            workFlowService.updateWorkFlowParam(workFlowParam);
        } else {

            outPutData.setUrl("/export/getDataApi.json?paramId=" + paramId + "&page=1&size=15");

            jsonParam = JSONObject.toJSONString(outPutData);

            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setParamId(Long.parseLong(paramId));
            workFlowParam.setJsonParam(jsonParam);
            workFlowService.updateWorkFlowParam(workFlowParam);
        }
        return addOutputPO;
    }

    @RequestMapping(value = "/getOutputList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询输出项列表", notes = "", position = 0)
    public OutputDataListPO getOutputList(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
                                          @RequestParam(value = "typeNo", required = true) @ApiParam(value = "typeNo", required = true) String typeNo,
                                          HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);
        OutputDataListPO outputDataListPO = new OutputDataListPO();
        List<OutPutDataDetail> outPutDataDetailList = new ArrayList<>();

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(typeNo, projectIdInt);
        if (null != workFlowParamList && workFlowParamList.size() > 0) {
            for (WorkFlowParam workFlowParam : workFlowParamList) {
                String segJsonParamStr = workFlowParam.getJsonParam();
                OutPutDataDetail outPutDataDetail = JSON.parseObject(segJsonParamStr, OutPutDataDetail.class);
                outPutDataDetail.setParamId(String.valueOf(workFlowParam.getParamId()));
                outPutDataDetailList.add(outPutDataDetail);
            }
        }
        outputDataListPO.setOutPutDataDetailList(outPutDataDetailList);
        return outputDataListPO;
    }

    @RequestMapping(value = "/deleteOutput.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除导出列表", notes = "", position = 0)
    public DeleteProjectPo deleteImport(@RequestParam(value = "paramId", required = true) @ApiParam(value = "参数id", required = true) String paramId,
                                        HttpServletRequest req, HttpServletResponse res) {
        DeleteProjectPo deleteProjectPo = new DeleteProjectPo();

        long paramIdInt = Long.parseLong(paramId);
        workFlowService.deleteWorkFlowParam(paramIdInt);
        return deleteProjectPo;
    }

    @RequestMapping(value = "/getDataApi.json", method = RequestMethod.GET)
    @ApiOperation(value = "查询输出json api ", notes = "", position = 0)
    public void getDataApiJson(@RequestParam(value = "paramId", required = false) @ApiParam(value = "参数id", required = false) String paramId,
                               @RequestParam(value = "page",required = true)@ApiParam(value = "页码 默认为1 ",required = true) String page,
                               @RequestParam(value = "size",required = true)@ApiParam(value = "查询记录数 默认 15 条",required = true) String size,
                               HttpServletRequest req, HttpServletResponse res) {
        //1 paramId 查询到 jsonParam 信息。
        //2 解析 jsonParam 信息。
        //3 根据解析的jsonParam信息 查询 JobTypeResultBO对象
        //4 通过 jobTypeResultBO 取出queryUrl 。
        //5 组装 queryUrl（需要加上projectId 参数）查询出数据。
        //6 根据解析的jsonParam 信息 返回json 或者xml 信息。
        if (paramId != null || !"".equals(paramId) || paramId.matches("\\d+")) {
            Map<String, Object> map = new HashMap();
            Map<String, String> getTermListByTermNamePostData = new HashMap<String, String>();

            Map<String, String[]> paramMap = req.getParameterMap();
            Set<String> keys = paramMap.keySet();
            if (null != keys && !keys.isEmpty()) {
                for (String key : keys) {
                    if (!"paramId".equals(key) && !"page".equals(key) && !"size".equals(key) && !"testInLogin".equals(key)) {
                        String[] values = paramMap.get(key);
                        map.put(key, values[0]);
                    }
                }
            }

            long paramIdInt = Long.parseLong(paramId);
            long pageInt = null != page&&!"".equals(page)?Long.parseLong(page):1;
            long sizeInt = null != size&&!"".equals(size)?Long.parseLong(size):15;

            WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);
            if (null != workFlowParam) {
                String jsonParam = workFlowParam.getJsonParam();
                OutPutData outPutData = JSON.parseObject(jsonParam, OutPutData.class);
                //根据数据源类型id查询该数据源所有的字段
                List<StorageTypeFieldPO> storageTypeFieldPOS = dataSourceTypeService.getDataSourceTypeRelationList(outPutData.getType());
                Map<String,String> storageTypeMap = new HashMap<>();
                for(StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOS){
                    storageTypeMap.put(storageTypeFieldPO.getFieldEnName(),storageTypeFieldPO.getFieldType());
                }

                String apiType = outPutData.getApiType();
                String typeName = outPutData.getStorageTypeTable();

                List<String> hierarchyList = new ArrayList<>();
                hierarchyList.add("sentence");
                hierarchyList.add("section");
                hierarchyList.add("article");

                String corpusServer = WebUtil.getCorpusServerByEnv();
                String getDataUrl = "";
                if(hierarchyList.contains(typeName)){//说明是分析层级
                    getDataUrl = "/getSemanticAnalysisDataList.json";

                    getTermListByTermNamePostData.put("resultType",typeName);
                }else{
                    getDataUrl = "/getDataInSearcher.json";
                    getTermListByTermNamePostData.put("dataType",typeName);
                }

                map.put("projectID", workFlowParam.getProjectId());

                String filterJSON = JSONObject.toJSONString(map);

                getTermListByTermNamePostData.put("filterJSON", filterJSON);

                long firstIndexId = 0;

                getTermListByTermNamePostData.put("startRow","0");
                getTermListByTermNamePostData.put("rows","1");

                Object firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), corpusServer+getDataUrl, "post", getTermListByTermNamePostData);
                if(null != firstObject){
                    net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) firstObject;
                    Object total = jsonObject.get("total");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if(null != jsonArray && jsonArray.size() > 0) {
                        net.sf.json.JSONObject jsonObj = jsonArray.getJSONObject(0);
                        String indexIdStr = jsonObj.getString("indexId");
                        firstIndexId = Long.parseLong(indexIdStr);
                    }
                }

                long lastIndexId = firstIndexId + ((pageInt-1) * sizeInt)-1;//算出最后的indexId

                getTermListByTermNamePostData.put("startRow", Long.toString(lastIndexId));
                getTermListByTermNamePostData.put("rows", Long.toString(sizeInt));
                Object ResultObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), corpusServer+getDataUrl, "post", getTermListByTermNamePostData);
                if (null == ResultObject) {
                    throw new WebException(MySystemCode.BIZ_DATA_QUERY_EXCEPTION);
                }
                net.sf.json.JSONObject ResultObjectContent = (net.sf.json.JSONObject) ResultObject;
                JSONArray semanticAnalysisDataArray = ResultObjectContent.getJSONArray("data");

                String mapRelationValueNames = outPutData.getMapRelationValueName();
                String[] mapRelationValueNameArray = mapRelationValueNames.split(",");

                JSONArray resultJsonArray = new JSONArray();

                if (null != mapRelationValueNameArray && mapRelationValueNameArray.length > 0) {

                    for (int i = 0; i < semanticAnalysisDataArray.size(); i++) {
                        net.sf.json.JSONObject jsonObject = semanticAnalysisDataArray.getJSONObject(i);

                        net.sf.json.JSONObject resultJsonObject = new net.sf.json.JSONObject();

                        for (int j = 0; j < mapRelationValueNameArray.length; j++) {
                            String fieldName = mapRelationValueNameArray[j];
                            Object valObj = jsonObject.get(fieldName);
                            //如果该字段的filedType是时间类型
                            if(storageTypeMap.get(fieldName).equalsIgnoreCase("datetime") && valObj != null){
                                net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(valObj);
                                Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String value = format.format(date);
                                resultJsonObject.put(fieldName, value);
                            }else{
                                resultJsonObject.put(fieldName, valObj);
                            }
                        }

                        resultJsonArray.add(resultJsonObject);

                    }
                } else {
                    resultJsonArray = semanticAnalysisDataArray;
                }

                if (null != resultJsonArray && resultJsonArray.size() > 0) {

                    PrintWriter out = null;
                    try {
                        out = res.getWriter();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (OutPutData.API_TYPE_JSON.equals(apiType)) {
                        res.setContentType("application/json;charset=utf-8");

                        out.append(resultJsonArray.toString());
                    } else if (OutPutData.API_TYPE_XML.equals(apiType)) {

                        res.setContentType("text/xml;charset=utf-8");
                        res.setHeader("Cache-control", "no-cache");

                        String jsonString = JSONObject.toJSONString(resultJsonArray);
                        String xmlStr = XmlExerciseUtil.json2xml(jsonString);

                        out.append(xmlStr);
                    }

                    out.flush();
                    out.close();

                }

            }
        }

    }

    @RequestMapping(value = "/getVisWorkFlowDataApi.json", method = RequestMethod.GET)
    @ApiOperation(value = "可视化工作流查询输出json api ", notes = "", position = 0)
    public void getVisWorkFlowDataApi(@RequestParam(value = "detailId") @ApiParam(value = "节点id", required = false) String detailId,
                                      @RequestParam(value="projectID") @ApiParam(value="项目id") String projectId,
                                      @RequestParam(value = "page")@ApiParam(value = "页码 默认为1 ",required = true) String page,
                                      @RequestParam(value = "size")@ApiParam(value = "查询记录数 默认 15 条",required = true) String size,
                                      HttpServletRequest req, HttpServletResponse res) {

        if ((detailId != null || !"".equals(detailId) || detailId.matches("\\d+")) && (
                projectId != null || !"".equals(projectId) || projectId.matches("\\d+"))) {
            //查询该节点的参数配置
            List<WorkFlowNodeParamBo> list = workFlowService.getWorkFlowNodeParamByFlowDetailId(Long.parseLong(detailId));
            String apiType = "";
            for (WorkFlowNodeParamBo workFlowNodeParamBo : list) {
                if (workFlowNodeParamBo.getInputParamCnName().equals("API形式")) {
                    apiType = workFlowNodeParamBo.getInputParamValue();
                }
            }
            long pageInt = null != page && !"".equals(page) ? Long.parseLong(page) : 1;
            long sizeInt = null != size && !"".equals(size) ? Long.parseLong(size) : 15;
            //查询该节点的输出字段
            List<VisWorkFlowBO> visWorkFlowBOS = visWorkFlowService.getVisWorkFlowList(Integer.parseInt(detailId));
            //得到存储表
            String dataType = visWorkFlowBOS.get(0).getStorageTypeTable();
            Map<String, String> getTermListByTermNamePostData = new HashMap<>();
            String corpusServer = WebUtil.getCorpusServerByEnv();
            String getDataUrl = "";
            if (dataType.equals(Constants.DATA_TYPE_ARTICLE) ||
                    dataType.equals(Constants.DATA_TYPE_SECTION) ||
                    dataType.equals(Constants.DATA_TYPE_SENTENCE)) {//说明是分析层级
                getDataUrl = "/getSemanticAnalysisDataList.json";

                getTermListByTermNamePostData.put("resultType", dataType);
            } else {
                getDataUrl = "/getDataInSearcher.json";
                getTermListByTermNamePostData.put("dataType", dataType);
            }
            Map<String, Object> map = new HashMap();

            Map<String, String[]> paramMap = req.getParameterMap();
            Set<String> keys = paramMap.keySet();
            if (null != keys && !keys.isEmpty()) {
                for (String key : keys) {
                    if (!key.equals("detailId") && !"page".equals(key) && !"size".equals(key) && !"testInLogin".equals(key)) {
                        String[] values = paramMap.get(key);
                        map.put(key, values[0]);
                    }
                }
            }

//            map.put("projectID", projectId);

            String filterJSON = JSONObject.toJSONString(map);

            getTermListByTermNamePostData.put("filterJSON", filterJSON);

            long firstIndexId = 0;

            getTermListByTermNamePostData.put("startRow", "0");
            getTermListByTermNamePostData.put("rows", "1");

            Object firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), corpusServer + getDataUrl, "post", getTermListByTermNamePostData);
            if (null != firstObject) {
                net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) firstObject;
                Object total = jsonObject.get("total");
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (null != jsonArray && jsonArray.size() > 0) {
                    net.sf.json.JSONObject jsonObj = jsonArray.getJSONObject(0);
                    String indexIdStr = jsonObj.getString("indexId");
                    firstIndexId = Long.parseLong(indexIdStr);
                }
            }

            long lastIndexId = firstIndexId + ((pageInt - 1) * sizeInt) - 1;//算出最后的indexId
            getTermListByTermNamePostData.put("startRow", Long.toString(lastIndexId));
            getTermListByTermNamePostData.put("rows", Long.toString(sizeInt));
            Object ResultObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), corpusServer + getDataUrl, "post", getTermListByTermNamePostData);
            if (null == ResultObject) {
                throw new WebException(MySystemCode.BIZ_DATA_QUERY_EXCEPTION);
            }
            net.sf.json.JSONObject ResultObjectContent = (net.sf.json.JSONObject) ResultObject;
            JSONArray semanticAnalysisDataArray = ResultObjectContent.getJSONArray("data");
            JSONArray resultJsonArray = new JSONArray();
            if (!Validate.isEmpty(visWorkFlowBOS)) {

                for (int i = 0; i < semanticAnalysisDataArray.size(); i++) {
                    net.sf.json.JSONObject jsonObject = semanticAnalysisDataArray.getJSONObject(i);
                    net.sf.json.JSONObject resultJsonObject = new net.sf.json.JSONObject();
                    for (VisWorkFlowBO visWorkFlowBO : visWorkFlowBOS) {
                        String fieldName = visWorkFlowBO.getFiledEnName();
                        Object valObj = jsonObject.get(fieldName);
                        //如果该字段的filedType是时间类型
                        if(visWorkFlowBO.getFiledType().equalsIgnoreCase("datetime") && valObj != null){
                            net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(valObj);
                            Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String value = format.format(date);
                            resultJsonObject.put(fieldName, value);
                        }else{
                            resultJsonObject.put(fieldName, valObj);
                        }
                    }
                    resultJsonArray.add(resultJsonObject);
                }
            } else {
                resultJsonArray = semanticAnalysisDataArray;
            }

            if (null != resultJsonArray && resultJsonArray.size() > 0) {
                PrintWriter out = null;
                try {
                    out = res.getWriter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (OutPutData.API_TYPE_JSON.equalsIgnoreCase(apiType)) {
                    res.setContentType("application/json;charset=utf-8");
                    out.append(resultJsonArray.toString());
                } else if (OutPutData.API_TYPE_XML.equalsIgnoreCase(apiType)) {
                    res.setContentType("text/xml;charset=utf-8");
                    res.setHeader("Cache-control", "no-cache");
                    String jsonString = JSONObject.toJSONString(resultJsonArray);
                    String xmlStr = XmlExerciseUtil.json2xml(jsonString);
                    out.append(xmlStr);
                }
                out.flush();
                out.close();
            }
        }
    }
    @RequestMapping(value = "/getAllDataApi.json", method = RequestMethod.GET)
    @ApiOperation(value = "根据表和项目id查询数据", notes = "", position = 0)
    public void getAllDataApi(@RequestParam (value = "projectId") @ApiParam(value = "项目id") String projectId,
                              @RequestParam(value = "storageTable") @ApiParam(value = "存储表名") String storageTable,
                              @RequestParam(value = "detailIds",required = false) @ApiParam(value = "流程id") String detailIds,
                              @RequestParam(value = "fieldStr",required = false) @ApiParam(value = "") String fieldStr,
                              @RequestParam(value = "page",required = false) @ApiParam(value = "页码") String page,
                              @RequestParam(value = "size",required = false) @ApiParam(value = "每页条数") String size,
                              @RequestParam(value = "ordBy",required = false) @ApiParam(value = "排序") String ordBy,
                              @RequestParam(value = "token",required = true) @ApiParam(value = "token") String token,
                              HttpServletResponse res,HttpServletRequest req){
        if(Validate.isEmpty(projectId) || Validate.isEmpty(storageTable)){
            throw new WebException(MySystemCode.SYS_CONTROLLER_EXCEPTION_MESSAGE);
        }

        String string = MD5Util.encrypt(projectId+Constants.TOKEN);
        if(!Validate.isEmpty(token)){
            if(!string.equals(token)){
                throw new WebException(MySystemCode.TOKEN_ERROR);
            }
        }else{
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long pageInt = null != page&&!"".equals(page)?Long.parseLong(page):1;
        long sizeInt = null != size&&!"".equals(size)?Long.parseLong(size):15;
        ordBy = ordBy!=null&&!"".equals(ordBy)?ordBy : "desc";

        Map<String,Object> map = new HashMap<>();
        map.put("projectID",projectId);
        if(!Validate.isEmpty(detailIds)){
            String[] detailIdArr = detailIds.split(",");
            String detailId = "";
            for(String str : detailIdArr){
                detailId += str+"&";
            }
            detailId = detailId.substring(0,detailId.length()-1);
            map.put("detailId",detailId);
        }
        String filterJSON = JSONObject.toJSONString(map);

        Map<String,String> postMap = new HashMap<>();
        postMap.put("dataType",storageTable);
        postMap.put("filterJSON",filterJSON);
        long firstIndexId = 0;
        postMap.put("startRow","0");
        postMap.put("rows","1");
        String corpusServer = WebUtil.getCorpusServerByEnv();
        String getDataUrl = "/getDataInSearcher.json";
        Object firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), corpusServer+getDataUrl, "post", postMap);
        if(null != firstObject){
            net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) firstObject;
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(null != jsonArray && jsonArray.size() > 0) {
                net.sf.json.JSONObject jsonObj = jsonArray.getJSONObject(0);
                String indexIdStr = jsonObj.getString("indexId");
                firstIndexId = Long.parseLong(indexIdStr);
            }
        }
        long lastIndexId = firstIndexId + ((pageInt - 1) * sizeInt) - 1;//算出最后的indexId
        postMap.put("startRow", Long.toString(lastIndexId));
        postMap.put("rows", Long.toString(sizeInt));
        postMap.put("ordBy", ordBy);
        Object ResultObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), corpusServer+getDataUrl, "post", postMap);
        if (null == ResultObject) {
            throw new WebException(MySystemCode.BIZ_DATA_QUERY_EXCEPTION);
        }
        net.sf.json.JSONObject ResultObjectContent = (net.sf.json.JSONObject) ResultObject;
        JSONArray semanticAnalysisDataArray = ResultObjectContent.getJSONArray("data");
        JSONArray resultJsonArray = new JSONArray();
        if(!Validate.isEmpty(semanticAnalysisDataArray)){
            if(!Validate.isEmpty(fieldStr)) {
                String[] fieldArr = fieldStr.split(",");
                for (int i = 0; i < semanticAnalysisDataArray.size(); i++) {
                    net.sf.json.JSONObject jsonObject = semanticAnalysisDataArray.getJSONObject(i);

                    jsonObject.remove("projectID");
                    jsonObject.remove("detailId");
                    jsonObject.remove("indexId");
                    jsonObject.remove("uniqueValue");

                    net.sf.json.JSONObject resultJsonObject = new net.sf.json.JSONObject();
                    for (int j = 0; j < fieldArr.length; j++) {
                        String fieldName = fieldArr[j];
                        Object valObj = jsonObject.get(fieldName);
                        if((fieldName.equalsIgnoreCase("crawlTime") ||
                                fieldName.equalsIgnoreCase("dateTime")||
                                fieldName.equalsIgnoreCase("publishTime"))&& valObj != null){
                            net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(valObj);
                            Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String value = format.format(date);
                            resultJsonObject.put(fieldName, value);
                        }else{
                            resultJsonObject.put(fieldName, valObj);
                        }
                    }
                    resultJsonArray.add(resultJsonObject);
                }
            }else{
                for (int i = 0; i < semanticAnalysisDataArray.size(); i++) {
                    net.sf.json.JSONObject jsonObject = semanticAnalysisDataArray.getJSONObject(i);

                    jsonObject.remove("projectID");
                    jsonObject.remove("detailId");
                    jsonObject.remove("indexId");
                    jsonObject.remove("uniqueValue");

                    net.sf.json.JSONObject resultJsonObject = new net.sf.json.JSONObject();
                    Set<String> strings = jsonObject.keySet();
                    for(String field : strings){
                        Object valObj = jsonObject.get(field);
                        if((field.equalsIgnoreCase("crawlTime")||
                                field.equalsIgnoreCase("dateTime")||
                                field.equalsIgnoreCase("publishTime"))&& valObj != null){
                            net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(valObj);
                            Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String value = format.format(date);
                            resultJsonObject.put(field, value);
                        }else{
                            resultJsonObject.put(field, valObj);
                        }
                    }
                    resultJsonArray.add(resultJsonObject);
                }
            }

            if (null != resultJsonArray && resultJsonArray.size() > 0) {
                PrintWriter out = null;
                try {
                    out = res.getWriter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                res.setContentType("application/json;charset=utf-8");
                out.append(resultJsonArray.toString());
                out.flush();
                out.close();
            }
        }else{
            throw new WebException(MySystemCode.BIZ_DATA_QUERY_EXCEPTION);
        }
    }

}
