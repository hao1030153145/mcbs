package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.logicframework.integration.sao.hdfs.BaseSaoHDFS;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.ContentTypeService;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.MongoDBService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.constant.MongoDBDbNames;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.util.*;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.JobTypeResultBO;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import org.bson.Document;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("importDataController")
@Api(value = "导入数据", description = "导入数据相关的访问接口", position = 3)
@RequestMapping(path = "/importData")
public class ImportDataController {
    @Resource
    private DataSourceTypeService dataSourceTypeService;
    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private ContentTypeService contentTypeService;
    @Resource
    private JobTypeService jobTypeService;
    @Resource
    private MongoDBService mongoDBService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @Resource
    private BaseSaoHDFS baseSaoHDFS;

    @RequestMapping(value = "/uploadFile.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "上传文件", notes = "", position = 0)
    public ImportDataUploadPo uploadFile(@RequestParam(value = "id", required = true) @ApiParam(value = "数据源id") String id,
                                               @RequestParam(value = "file", required = true) MultipartFile file,
                                               HttpServletRequest req, HttpServletResponse res) {
        ImportDataUploadPo importDataUploadPo = new ImportDataUploadPo();

        try {

            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            List<String> origainFieldList = getFieldList(file, req, fileName);
            if(null == origainFieldList){
                throw new WebException(MySystemCode.BIZ_EXCEL_SIZE);
            }

            importDataUploadPo.setOrigainFieldList(origainFieldList);
            List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(id);
            List<String> fieldNameList = new ArrayList<>();
            List<String> fieldAnnotationList = new ArrayList<>();

            Map<String, String> fieldAnnotationMap = new HashMap<>();
            for (StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList) {
                fieldNameList.add(storageTypeFieldPO.getFieldEnName());
                fieldAnnotationMap.put(storageTypeFieldPO.getFieldEnName(), storageTypeFieldPO.getFieldCnName());
            }

            //根据排序后的 fieldName List 添加fieldAnnotationList
            for (String fieldName : fieldNameList) {
                fieldAnnotationList.add(fieldAnnotationMap.get(fieldName));
            }


            importDataUploadPo.setFieldNameList(fieldNameList);
            importDataUploadPo.setFieldAnnotationList(fieldAnnotationList);
            importDataUploadPo.setUrl(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return importDataUploadPo;
    }

    @RequestMapping(value = "/getDataSourceType.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取数据源列表", notes = "", position = 0)
    public List<DatasourcePO> getDataSourceType(@RequestParam(value = "sourceType",required = false) @ApiParam(value = "sourceType",required = false) String sourceType,
            HttpServletRequest req, HttpServletResponse res) {
        if(Validate.isEmpty(sourceType)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        List<DatasourcePO> datasourcePOList = dataSourceTypeService.getDataSourceTypeList(sourceType);
        return datasourcePOList;
    }

    @RequestMapping(value = "/import.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "导入", notes = "", position = 0)
    public ImportDataPo importProject(@RequestParam(value = "typeNo", required = true) @ApiParam(value = "typeNo", required = true) String typeNo,
                                      @RequestParam(value = "url", required = true) @ApiParam(value = "上传文件地址") String url,
                                      @RequestParam(value = "name", required = true) @ApiParam(value = "上传文件名称") String name,
                                      @RequestParam(value = "typeName", required = true) @ApiParam(value = "上传文件类型名称") String typeName,
                                      @RequestParam(value = "typeId", required = true) @ApiParam(value = "上传文件类型id") String typeId,
                                      @RequestParam(value = "storageTypeTable", required = true) @ApiParam(value = "存储表名") String storageTypeTable,
                                      @RequestParam(value = "origainField", required = false) @ApiParam(value = "上传文件原字段,现字段对应关系") String origainRelation,
                                      @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id") String projectId,
                                      HttpServletRequest req, HttpServletResponse res) {

        if(Validate.isEmpty(origainRelation)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        List<RelationOrigin> relationOriginList = JSON.parseArray(origainRelation,RelationOrigin.class);
        List<String> keyList = new ArrayList<>();
        for (RelationOrigin relationOrigin:relationOriginList) {
            String key = relationOrigin.getKey().trim();
            if(!Validate.isEmpty(key)){
                keyList.add(key);
            }
        }

        if(Validate.isEmpty(keyList)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        ImportDataPo importDataPo = new ImportDataPo();
        //把上传文件移动到服务器目录
        String fileBase = System.getProperty("upload.dir");
        String fileParamtertm = "/project/temp/";
        String fileParamter = "/project/importfile/";
        String originalUrl = fileBase + fileParamtertm + url;
        String idcUrl = fileBase + fileParamter + url;
        File file = new File(originalUrl);

        if (file.exists()) {
            //上传文件到hdfs。
            baseSaoHDFS.uploadFile(originalUrl,fileBase + fileParamter + url);
//            baseSaoHDFS.uploadFile(originalUrl,"/test1/"+url);

            file.delete();

        }
        //调用工作流接口
        try {

//            long length = ExcelUtil.readExcelRowNum(fileBase + fileParamter + url);

            /*if (url.endsWith(".xlsx")) {
                length = ExcelUtil.readXlsxLength(fileBase + fileParamter + url);
            } else {
                length = ExcelUtil.readXlsLength(fileBase + fileParamter + url);
            }*/
            ImportData importData = new ImportData(projectId, name, typeName, typeId, String.valueOf(0), idcUrl, origainRelation, "0",storageTypeTable);
            String jsonParam = JSONObject.toJSONString(importData);
            long projectIdInt = Long.parseLong(projectId);
            long paramId = workFlowService.addWorkDetail("0",projectIdInt, typeNo, jsonParam, WorkFlowParam.PARAM_TYPE_PRIVATE, null, Long.parseLong(typeId));

            WorkFlowParam workFlowPara = workFlowService.getWorkFlowParamByParamId(paramId);
            long detailId = workFlowPara.getFlowDetailId();

            if (paramId > 0) {//如果 添加导入数据成功

                ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                projectJobTypeBO.setProjectId(projectIdInt);
                projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT);
                ProjectJobTypeBO projectJobType = projectJobTypeService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);

                if(null != projectJobType){
                    //添加 数据语义分析对象设置======================Start=============================================================
                    long dataSourceTypeId = Long.parseLong(importData.getTypeId());//数据源类型id
                    String dataSourceTypeName = importData.getTypeName();//数据源类型名字

                    //判断 语义分析对象是否添加过这个数据源类型的数据了 如果有则不添加
                    List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT, projectIdInt);

                    boolean isNeedAdd = true;//默认需要添加语义分析对象

                    if (null != workFlowParamList && workFlowParamList.size() > 0) {
                        for (WorkFlowParam workFlowParam : workFlowParamList) {
                            String jsonStr = workFlowParam.getJsonParam();
                            SemanticAnalysisObjectPO semanticAnalysisObject = JSON.parseObject(jsonStr, SemanticAnalysisObjectPO.class);
                            long seamDataSourceTypeId = semanticAnalysisObject.getJsonParam().getDataSourceTypeId();
                            if (seamDataSourceTypeId == dataSourceTypeId) {//如果语义分析对象已经添加过 这个数据源类型 则 设置 isNeedAdd 为false

                                //不需要添加，但需要更新这个语义节点的上一个节点ids，
                                isNeedAdd = false;

                                long flowDetailId = workFlowParam.getFlowDetailId();

                                //更新上一个节点的next
                                WorkFlowDetail preDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailId);
                                String nextFlowDetailIds = preDetail.getNextFlowDetailIds();

                                if(!Validate.isEmpty(nextFlowDetailIds)){
                                    nextFlowDetailIds += ","+flowDetailId;
                                }else {
                                    nextFlowDetailIds = Long.toString(flowDetailId);
                                }
                                preDetail.setNextFlowDetailIds(nextFlowDetailIds);
                                workFlowService.updateWorkFlowDetail(preDetail);

                                //但需要更新这个语义节点的上一个节点ids，
                                WorkFlowDetail workFlowDetai2 = workFlowService.getWorkFlowDetailByWorkFlowDetailId(flowDetailId);
                                String prevFlowDetailIds = workFlowDetai2.getPrevFlowDetailIds();

                                if(null != prevFlowDetailIds && !"".equals(prevFlowDetailIds)){
                                    prevFlowDetailIds += ","+detailId;
                                }

                                workFlowDetai2.setPrevFlowDetailIds(prevFlowDetailIds);

                                workFlowService.updateWorkFlowDetail(workFlowDetai2);

                            }
                        }
                    }

                    if (isNeedAdd) {
                        List<ContentTypePO> contentTypePOList = contentTypeService.getContentTypeList(dataSourceTypeId);

                        SemanticAnalysisObjectPO semanticAnalysisObjectPO = new SemanticAnalysisObjectPO();// 语义分析对象 返回的po

                        semanticAnalysisObjectPO.setDataSourceTypeName(dataSourceTypeName);
                        semanticAnalysisObjectPO.setSentence("分词+主题+话题");
                        semanticAnalysisObjectPO.setSection("无");
                        semanticAnalysisObjectPO.setArticle("分词+主题+话题");

                        SemanticJsonParam semanticJsonParam = new SemanticJsonParam();//语义分析对象 jsonPO
                        semanticJsonParam.setDataSourceTypeId(dataSourceTypeId);
                        semanticJsonParam.setDataSourceTypeName(dataSourceTypeName);
                        semanticJsonParam.setStorageTypeTable(storageTypeTable);//新增存储表名

                        List<AnalysisObject> analysisObjectList = new ArrayList<>();

                        String anAyleObjectName = "";

                        if (null != contentTypePOList && contentTypePOList.size() > 0) {
                            for (ContentTypePO contentTypePO : contentTypePOList) {
                                if (contentTypePO.getIsDefault() == 1) {
                                    AnalysisObject analysisObject = new AnalysisObject();//分析对象
                                    analysisObject.setContentType(contentTypePO.getContentType());//设置 为默认 的 contentType
                                    analysisObject.setSubType(1);//设置为全文
                                    analysisObject.setValue(0);//全文
                                    analysisObjectList.add(analysisObject);

                                    anAyleObjectName += contentTypePO.getContentTypeName() + "全文+";
                                }
                            }
                            if (!"".equals(anAyleObjectName)) {
                                anAyleObjectName = anAyleObjectName.substring(0, anAyleObjectName.length() - 1);
                            }
                        }

                        semanticAnalysisObjectPO.setAnalysisObjectName(anAyleObjectName);

                        List<AnalysisHierarchy> analysisHierarchyList = new ArrayList<>();
                        AnalysisHierarchy analysisHierarchy = new AnalysisHierarchy();//分析层级
                        analysisHierarchy.setType(1);
                        analysisHierarchy.setHierarchy("1,3");
                        analysisHierarchyList.add(analysisHierarchy);
                        AnalysisHierarchy analysisHierarchy2 = new AnalysisHierarchy();//分析层级
                        analysisHierarchy2.setType(2);
                        analysisHierarchy2.setHierarchy("1,3");
                        analysisHierarchy2.setCalculation(String.valueOf(2));
                        analysisHierarchyList.add(analysisHierarchy2);
                        AnalysisHierarchy analysisHierarchy3 = new AnalysisHierarchy();//分析层级
                        analysisHierarchy3.setType(3);
                        analysisHierarchy3.setHierarchy("1,3");
                        analysisHierarchy3.setCalculation(String.valueOf(3));
                        analysisHierarchyList.add(analysisHierarchy3);

                        semanticJsonParam.setAnalysisHierarchy(analysisHierarchyList);
                        semanticJsonParam.setAnalysisObject(analysisObjectList);

                        semanticAnalysisObjectPO.setJsonParam(semanticJsonParam);

                        List<Long> dataSourceTypes = new ArrayList<>();//添加结果类型
                        dataSourceTypes.add(-1L);//句级
                        dataSourceTypes.add(-3L);//文级

                        String semanticJsonPara = JSON.toJSONString(semanticAnalysisObjectPO);

                        workFlowService.addWorkDetail(Long.toString(detailId),projectIdInt, Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT, semanticJsonPara, WorkFlowParam.PARAM_TYPE_COMON, null, dataSourceTypes);

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return importDataPo;
    }

    @RequestMapping(value = "/importList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询导入数据列表", notes = "", position = 0)
    public ImportDataListPO importList(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
                                          @RequestParam(value = "typeNo", required = true) @ApiParam(value = "typeNo", required = true) String typeNo,
                                          HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);
        ImportDataListPO importDataListPO = new ImportDataListPO();
        List<ImportDataDetail> importDataList = new ArrayList<>();

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(typeNo, projectIdInt);

        if (null != workFlowParamList && workFlowParamList.size() > 0) {
            for (WorkFlowParam workFlowParam : workFlowParamList) {
                String segJsonParamStr = workFlowParam.getJsonParam();
                ImportDataDetail importData = JSON.parseObject(segJsonParamStr, ImportDataDetail.class);
                importData.setParamId(String.valueOf(workFlowParam.getParamId()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                importData.setUpdatedDate(sdf.format(workFlowParam.getUpdatedDate()));
                /**
                 * 更新任务的进度
                 * by allen
                 * v1.1.1
                 */
                Long detailId = workFlowParam.getFlowDetailId();
                WorkFlowDetail detail = workFlowService
                        .getWorkFlowDetailByWorkFlowDetailId(detailId);
                importData.setJobStatus(detail.getJobStatus()+"");
                importData.setErrorMsg(detail.getErrorMsg());
                importData.setJobProgress(detail.getJobProgress()+"");
                importDataList.add(importData);

                Map<String,String> paramMap=new HashMap<String,String>();
                paramMap.put("projectId",workFlowParam.getProjectId()+"");
                paramMap.put("flowId",workFlowParam.getFlowId()+"");
                paramMap.put("flowDetailId",workFlowParam.getFlowDetailId()+"");
                paramMap.put("typeNo",workFlowParam.getTypeNo());
                importData.setCount(roteGetResultParamSerivece(paramMap));
            }
        }
        importDataListPO.setImportDataDetailList(importDataList);
        return importDataListPO;
    }

    @RequestMapping(value = "/deleteImport.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除导入列表", notes = "", position = 0)
    public DeleteProjectPo deleteImport(@RequestParam(value = "paramId", required = true) @ApiParam(value = "参数id", required = true) String paramId,
                                        HttpServletRequest req, HttpServletResponse res) {
        DeleteProjectPo deleteProjectPo = new DeleteProjectPo();

        long paramIdInt = Long.parseLong(paramId);

        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);

        boolean isSucc = workFlowService.deleteWorkFlowParam(paramIdInt);

        /*if (isSucc) {

            String json = workFlowParam.getJsonParam();

            ImportDataDetail importData = JSON.parseObject(json, ImportDataDetail.class);

            String projectId = importData.getProjectid();
            long projectIdInt = Long.parseLong(projectId);
            String dataSourceTypeId = importData.getTypeId();
            long dataSourceTypeIdInt = Long.parseLong(dataSourceTypeId);

            boolean isNeesDel = true;

            List<WorkFlowParam> importWorkFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT, projectIdInt);
            if(!Validate.isEmpty(importWorkFlowParamList)){
                for (WorkFlowParam importWorkFlowParam:importWorkFlowParamList) {
                    String json2 = importWorkFlowParam.getJsonParam();

                    ImportDataDetail importData2 = JSON.parseObject(json, ImportDataDetail.class);

                    String dataSourceTypeId2 = importData.getTypeId();
                    long dataSourceTypeIdInt2 = Long.parseLong(dataSourceTypeId);

                    if(dataSourceTypeIdInt == dataSourceTypeIdInt2){
                        isNeesDel = false;
                        break;
                    }

                }
            }

            if(isNeesDel){
                //删除 语义分析对象设置
                List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT, projectIdInt);
                if (null != workFlowParamList && workFlowParamList.size() > 0) {
                    for (WorkFlowParam workFlowPar : workFlowParamList) {
                        String jsonParamStr = workFlowPar.getJsonParam();

                        SemanticAnalysisObjectPO semanticAnalysisObjectPO = JSON.parseObject(jsonParamStr, SemanticAnalysisObjectPO.class);

                        long semDataTypeSource = semanticAnalysisObjectPO.getJsonParam().getDataSourceTypeId();
                        if (dataSourceTypeIdInt == semDataTypeSource) {
                            workFlowService.deleteWorkFlowParam(workFlowPar.getParamId());
                        }

                    }
                }

            }

        }*/
        return deleteProjectPo;
    }

    @RequestMapping(value = "/getDataImportListFromShowData.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查看", notes = "", position = 0)
    public DataImportListFromShowDataPo getDataImportListFromShowData(@RequestParam(value = "detailId", required = true) @ApiParam(value = "detailId", required = true) String detailId,
                                                                      @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目 id", required = true) String projectId,
                                                                      @RequestParam(value = "type",required = false)@ApiParam(value = "数据源类型id",required = true) String type,
                                                                      @RequestParam(value = "lastIndexId",required = false)@ApiParam(value = "最后一个id",required = true) String lastIndexId,
                                                                      @RequestParam(value = "flag", required = false) @ApiParam(value = "flag", required = false) String flag,
                                                                      @RequestParam(value = "size", required = false) @ApiParam(value = "查询条数", required = true) String size,
                                                                       @RequestParam(value = "page", required = false) @ApiParam(value = "页码", required = true) String page,
                                                                      HttpServletRequest req, HttpServletResponse res) {
        DataImportListFromShowDataPo dataImportListFromShowDataPo = new DataImportListFromShowDataPo();
        if (Validate.isEmpty(flag)) {
            flag = "next";
        }
        String ordBy = "asc";
        if ("next".equals(flag)) {

            flag = "gt";
        } else if ("pre".equals(flag)) {

            flag = "gt";
            ordBy = "asc";
        }
        long sizeInt = null != size && !"".equals(size) ? Long.parseLong(size) : 15;
        long lastIndexIdInt = null != lastIndexId && !"".equals(lastIndexId) ? Long.parseLong(lastIndexId) : 0;

        String [] str = detailId.split("&");

        long detailIdInt = Long.parseLong(str[str.length-1]);
        //根据detailId查询WorkFlowParam
        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(detailIdInt);
        if (null == workFlowParam) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        //获取到jsonParam
        String jsonParam = workFlowParam.getJsonParam();
        String originRelation = "";
        String dataType = "";
        try {
            //判断是否是导入还是抓取
            if (workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)) {
                ImportData importData = JSONObject.parseObject(jsonParam, ImportData.class);
                originRelation = importData.getOrigainRelation();
                dataType = importData.getStorageTypeTable();
            } else if (workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL) ||
                    workFlowParam.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)) {
                //如果是抓取，则需要获取到stroageType（存储表名） type默认会传
                if (Validate.isEmpty(type)) {
                    DataCrawlPO dataCrawlPO = JSONObject.parseObject(jsonParam, DataCrawlPO.class);
                    dataType = dataCrawlPO.getJsonParam().getStorageTypeTable();
                } else {
                    dataType = type;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //组装语料库查询条件（首先查一条得到该节点下所有的数据）
        Map<String, String> postData = new HashMap<>();
        postData.put("dataType", dataType);
        postData.put("filterJSON", "{\"projectID\":" + projectId + ",\"detailId\":\"" + detailId + "\"}");
        postData.put("startRow", "1");
        postData.put("flag", "gt");
        postData.put("rows", "1");
        postData.put("ordBy", ordBy);
        //开始查询
        Object firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getDataInSearcher.json", "post", postData);
        Object total = null;
        if (null != firstObject) {
            net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) firstObject;
            if (jsonObject.size() <= 0) {
                throw new WebException(MySystemCode.BIZ_DATA_QUERY_EXCEPTION);
            }
            //获取total
            total = jsonObject.get("total");
            dataImportListFromShowDataPo.setCount(total.toString());
        }else{

        }
        //再次组装语料库查询条件，根据传过来的size和lastIndexId、flag等条件查询出部分记录

        Map<String, String> getTermListByTermNamePostData = new HashMap<String, String>();
        getTermListByTermNamePostData.put("dataType", dataType);
        getTermListByTermNamePostData.put("filterJSON", "{\"projectID\":" + projectId + ",\"detailId\":\"" + detailId + "\"}");
        getTermListByTermNamePostData.put("startRow",page);
        getTermListByTermNamePostData.put("flag", flag);
        getTermListByTermNamePostData.put("rows", String.valueOf(sizeInt));
        getTermListByTermNamePostData.put("ordBy", ordBy);
        //开始查询
        Object ResultObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getDataInSearcher.json", "post", getTermListByTermNamePostData);

        if (null != ResultObject) {
            net.sf.json.JSONObject ResultObjectContent = (net.sf.json.JSONObject) ResultObject;
            //得到数据Array
            JSONArray semanticAnalysisDataArray = ResultObjectContent.getJSONArray("data");
            if (null != semanticAnalysisDataArray && semanticAnalysisDataArray.size() > 0) {
                List<List> dataList = new ArrayList<>();//创建最终数据存放list
                List<String> titleList = new ArrayList<>();//中文标题list
                List<Map<String, String>> keyList = new ArrayList<>();//英文标题list（与中文标题对应）
                //通过detailId查询WorkFlowDetail表
                WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(detailIdInt);
                //远程调用base系统拿到DataSourceType对应的存储表字段
                List<StorageTypeFieldPO> list = dataSourceTypeService.getDataSourceTypeRelationList(workFlowDetail.getDataSourceType());

                Map<String,String> indexIdMap = new HashMap<>();
                indexIdMap.put("key","indexId");
                indexIdMap.put("fieldType","int");
                titleList.add("indexId");
                keyList.add(indexIdMap);
                //判断是否存在原始条件（这里其实就是判断是否是抓取还是导入）
                if (!Validate.isEmpty(originRelation)) {
                    List<RelationOrigin> relationOriginList = JSONObject.parseArray(originRelation, RelationOrigin.class);
                    for (RelationOrigin relationOrigin : relationOriginList) {
                        if (relationOrigin.getKey() != null && !relationOrigin.getKey().equals("")) {
                            String key = relationOrigin.getKey();
                            for (StorageTypeFieldPO s : list) {//遍历所有的存储字段，并判断该key是否有与之对应的存储字段，如果有则将该字段的中文名存入中文标题list
                                if (s.getFieldEnName().equals(key)) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("key", key);
                                    map.put("fieldType", s.getFieldType());
                                    keyList.add(map);
                                }
                            }
                            titleList.add(relationOrigin.getValue());
                        }
                    }
                } else {
                    /*//取出一条数据用于得到所有的key
                    net.sf.json.JSONObject jsonObject = semanticAnalysisDataArray.getJSONObject(0);
                    Set<String> keySet = jsonObject.keySet();
                    for (String key : keySet) {//遍历所有的key
                        for (StorageTypeFieldPO s : list) {//遍历所有的存储字段，并判断该key是否有与之对应的存储字段，如果有则将该字段的中文名存入中文标题list
                            if (s.getFieldEnName().equals(key)) {
                                Map<String, String> map = new HashMap<>();
                                map.put("key", key);
                                map.put("fieldType", s.getFieldType());
                                keyList.add(map);
                                titleList.add(s.getFieldCnName());
                            }
                        }
                    }*/

                    for (StorageTypeFieldPO s : list) {//遍历所有的存储字段，并判断该key是否有与之对应的存储字段，如果有则将该字段的中文名存入中文标题list
                        Map<String, String> map = new HashMap<>();
                        map.put("key", s.getFieldEnName());
                        map.put("fieldType", s.getFieldType());
                        keyList.add(map);

                        titleList.add(s.getFieldCnName());
                    }

                }
                //开始遍历所有的数据记录,判断是倒序还是顺序。如果是正序，则从semanticAnalysisDataArray的第一个开始添加
                //如果是倒序，则从则从semanticAnalysisDataArray的最后一个开始添加
                if (ordBy.equals("asc")) {
                    for (int i = 0; i <= semanticAnalysisDataArray.size() - 1; i++) {
                        List<String> subList = new ArrayList<>();
                        //转换为json
                        net.sf.json.JSONObject jsonObject = semanticAnalysisDataArray.getJSONObject(i);
                        //取出每一个key对应的值,存入subList中
                        for (Map<String, String> map : keyList) {
                            Object key = map.get("key");
                            String field = map.get("fieldType").toString();
                            Object valObj = jsonObject.get(key);
                            String value = null != valObj ? valObj.toString() : "";
                            //判断该key是否是时间类型
                            if (field.equalsIgnoreCase("datetime")&&!value.equals("")) {
                                net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(value);
                                Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                value = format.format(date);
                            }
                            subList.add(value);
                        }
                        dataList.add(subList);
                    }
                    dataImportListFromShowDataPo.setDataList(dataList);
                    dataImportListFromShowDataPo.setTitleList(titleList);
                } else {
                    for (int i = semanticAnalysisDataArray.size() - 1; i >= 0; i--) {
                        List<String> subList = new ArrayList<>();
                        //转换为json
                        net.sf.json.JSONObject jsonObject = semanticAnalysisDataArray.getJSONObject(i);
                        //取出每一个key对应的值,存入subList中
                        for (Map<String, String> map : keyList) {
                            Object key = map.get("key");
                            String field = map.get("fieldType").toString();
                            Object valObj = jsonObject.get(key);
                            String value = null != valObj ? valObj.toString() : "";
                            //判断该key是否是时间类型
                            if (field.equalsIgnoreCase("datetime")&&!value.equals("")) {
                                net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(value);
                                Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                value = format.format(date);
                            }else{

                            }
                            subList.add(value);
                        }
                        dataList.add(subList);
                    }

                    dataImportListFromShowDataPo.setDataList(dataList);
                    dataImportListFromShowDataPo.setTitleList(titleList);
                    if (total != null) {
                        dataImportListFromShowDataPo.setCount(total.toString());
                    } else {
                        dataImportListFromShowDataPo.setCount("0");
                    }
                }
            }
        }
        return dataImportListFromShowDataPo;
    }


    @RequestMapping(value = "/getUploadFile.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取导入文件", notes = "", position = 0)
    public void getFile(@RequestParam(value = "url", required = true) @ApiParam(value = "文件地址", required = true) String url,
                        HttpServletRequest req, HttpServletResponse res) {
        DeleteProjectPo deleteProjectPo = new DeleteProjectPo();

        File file = new File(url);
        if (file.exists()) {
            try {
                InputStream inputStream = null;
                //根据路径获取要下载的文件输入流
                inputStream = new FileInputStream(file);

                OutputStream out = null;
                out = res.getOutputStream();
                //创建数据缓冲区
                byte[] b = new byte[1024];
                int length;
                while ((length = inputStream.read(b)) > 0) {
                    //把文件流写到缓冲区里
                    out.write(b, 0, length);
                }
                out.flush();
                out.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getFieldList(MultipartFile file, HttpServletRequest req, String fileName) throws Exception {
        List<String> fieldList = null;
        String fileBase = System.getProperty("upload.dir");
        String fileParamter = "/project/temp/";
        String fileDir = fileBase + fileParamter + fileName;

        File destFile = new File(fileBase + fileParamter);
        if (!destFile.exists()) {
            destFile.mkdirs();
        }

        File fileExcel = new File(fileDir);
        file.transferTo(fileExcel);

        /*if (fileDir.endsWith(".xlsx")) {
            fieldList = ExcelUtil.readXlsx(fileBase + fileParamter + fileName);
        } else {
            fieldList = ExcelUtil.readXls(fileBase + fileParamter + fileName);
        }*/

        String fileType = CheckExcelFileTypeUtil.getFileType(fileExcel.getAbsolutePath());
        if(null == fileType){
            if(fileName.endsWith("csv")){
                try {
                    CSVFileUtil csvFileUtil = new CSVFileUtil(fileExcel.getAbsolutePath(),"gb2312");
                    String source = csvFileUtil.readLine();
                    fieldList = CSVFileUtil.fromCSVLinetoArray(source);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(null != fileType && (fileType.equals("xls"))){
            fieldList = ExcelUtil.readExcelFirstRow(fileBase + fileParamter + fileName);
        }else if(fileType.equals("xlsx")){
            List<List<String>> list = XLSCovertCSVReader
                    .readerExcel(fileExcel.getAbsolutePath());
            if(!Validate.isEmpty(list) && list.size() > 0){
                fieldList = list.get(0);
            }
        }
        destFile.delete();
        return fieldList;
    }


    private String roteGetResultParamSerivece(Map<String,String> paramMap){
        try
        {
            StringBuffer stringBuffer=new StringBuffer("");
            JobTypeInfo jobTypeInfo = jobTypeService
                    .getValidJobTypeByTypeNo(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT);
            String url = jobTypeInfo.getResultUrl();
            String roteUrl=WebUtil.getDpmssServerByEnv();
            if(!url.startsWith("http://")){
                url=roteUrl+url;
            }
            HttpClientHelper httpClient = new HttpClientHelper();
            HttpResponse response=httpClient.doPost(url,paramMap,"utf-8","utf-8",null,null);
            if(response.getStatusCode()==200){
                String content=response.getContent();
                net.sf.json.JSONObject object= net.sf.json.JSONObject.fromObject(content);
                JSONArray data=object.getJSONArray("data");
                if(data!=null&&data.size()>0){
                    int paramId=0;
                    int maxNum=0;
                    for (int i=0;i<data.size();i++)
                    {
                        net.sf.json.JSONObject ob= data.getJSONObject(i);
                        if(ob.getInt("paramId")>paramId){
                            paramId=ob.getInt("paramId");
                            maxNum=i;
                        }
                    }
                    net.sf.json.JSONObject object1=data.getJSONObject(maxNum);
                    String count=object1.getJSONObject("resultJsonParam").getString("count");

                    stringBuffer.append(count);
                }
            }
            return stringBuffer.toString();
        }catch (Exception e){
            return "0";
        }

    }


}
