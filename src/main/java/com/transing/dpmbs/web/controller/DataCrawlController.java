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
import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.ContentTypeService;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ParamService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.constant.RedisKey;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.ParamBO;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.biz.service.WorkFlowTemplateService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("dataCrawlController")
@Api(value = "数据抓取", description = "数据抓取相关接口接口", position = 2)
@RequestMapping("/dataCrawl")
public class DataCrawlController {

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private WorkFlowTemplateService workFlowTemplateService;

    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @Resource
    private ContentTypeService contentTypeService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @Resource
    private ParamService paramService;

    @Resource
    private JobTypeService jobTypeService;
    @Resource
    private BaseDaoRedis redis;
    @RequestMapping(value = "/getDataCrawlList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询 数据抓取 接口", notes = "", position = 0)
    public List<DataCrawlPO> getDataCrawlList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                                      @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo",required = true) String typeNo,
                                                      HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);
        List<DataCrawlPO> dataCrawlPOList = new ArrayList<>();

        List<WorkFlowDetail> workFlowDetailList = workFlowService.getFirstDetailByProjectId(projectIdInt);
        for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
            if(!workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL) && !workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
                continue;
            }

            WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(workFlowDetail.getFlowDetailId());

            int jobStatus = workFlowDetail.getJobStatus();
            String statusName = "";
            switch (jobStatus){
                case 0:statusName="未启动";
                    break;
                case 1:statusName="运行中";
                    break;
                case 2:statusName="已完成";
                    break;
                case 3:statusName="已执行";
                    break;
                case 4:statusName="停止";
                    break;
                case 9:statusName="异常";
                    break;
            }
            //从redis中获取数量
            String counts=redis.get(RedisKey.resultNum_suffix.name()+workFlowDetail.getFlowDetailId());
            Integer count=0;
            if(counts!=null){
                count=Integer.parseInt(counts);
            }
            DataCrawlPO dataCrawlPO = JSON.parseObject(workFlowParam.getJsonParam(),DataCrawlPO.class);
            dataCrawlPO.setParamId(workFlowParam.getParamId());
            dataCrawlPO.getJsonParam().setParamId(workFlowParam.getParamId());
            dataCrawlPO.setStatusName(statusName);
            dataCrawlPO.setCreatedDate(workFlowParam.getCreatedDate());
            dataCrawlPO.setCount(count);
            dataCrawlPOList.add(dataCrawlPO);

        }

        return dataCrawlPOList;
    }

    @RequestMapping(value = "/getCrawlInputParamList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 输入参数 数据 接口", notes = "", position = 0)
    public JSONArray getCrawlInputParamList(@RequestParam(value = "datasourceTypeId",required = true)@ApiParam(value = "数据源类型id",required = true) String datasourceTypeId,
                                                      HttpServletRequest req, HttpServletResponse res) {

        if (datasourceTypeId == null || "".equals(datasourceTypeId) || !datasourceTypeId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long datasourceTypeIdInt = Long.parseLong(datasourceTypeId);

        String crawlServer = WebUtil.getBaseServerByEnv();

        Object resultObj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServer+"/common/getCrawlInputParamsByDatasourceType.json?datasourceTypeId="+datasourceTypeIdInt,"get",null);
        JSONArray jsonArray = new JSONArray();
        if(null != resultObj){
            jsonArray = (JSONArray) resultObj;

            /*if(!Validate.isEmpty(jsonArray)){
                for (int i = 0; i < jsonArray.size(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String styleCode = jsonObject.getString("styleCode");
                    if(styleCode.equals("input")){
                        jsonObject.put("paramValue","");
                    }else if(styleCode.equals("input-file")){
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("value","");
                        jsonObj.put("file","");
                        jsonObject.put("paramValue","jsonObj");
                    }else if(styleCode.equals("checkbox")){
                        jsonObject.put("paramValue",true);
                    }else if(styleCode.equals("datetime")){
                        jsonObject.put("paramValue","2017/8/18");
                    }else if(styleCode.equals("file")){
                        jsonObject.put("paramValue","");
                    }
                }
            }*/

        }

        return jsonArray;
    }

    /*@RequestMapping(value = "/getSemanticAnalysisObjectListFromShowData.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询 语义分析结果 接口", notes = "", position = 0)
    public DataImportListFromShowDataPo getSemanticResultList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目 id",required = true) String projectId,
                                              @RequestParam(value = "paramId",required = true)@ApiParam(value = "项目 id",required = true) String paramId,
                                              @RequestParam(value = "type",required = true)@ApiParam(value = "分词结果的类型(sentence-句级,section-段级,article-文级)",required = true) String type,
                                              @RequestParam(value = "page",required = true)@ApiParam(value = "页码 默认为1 ",required = true) String page,
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

        long paramIdInt = Long.parseLong(paramId);
        long pageInt = null != page&&!"".equals(page)?Long.parseLong(page):1;
        long sizeInt = null != size&&!"".equals(size)?Long.parseLong(size):10;

        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);
        if(null == workFlowParam){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        String jsonParam = workFlowParam.getJsonParam();
        SemanticAnalysisObjectPO semanticAnalysisObjectPO = JSON.parseObject(jsonParam,SemanticAnalysisObjectPO.class);

        String dataTypeName = semanticAnalysisObjectPO.getDataSourceTypeName();

        List<List> dataList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();

        DataImportListFromShowDataPo dataImportListFromShowDataPo = new DataImportListFromShowDataPo();

        long firstIndexId = 0;
        Map<String,String> postData = new HashMap<>();
        postData.put("resultType",type);
        postData.put("filterJSON","{\"projectID\":"+projectId+",\"dataSourceType\":\""+dataTypeName+"\"}");
        postData.put("startRow","0");
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

        long lastIndexId = firstIndexId + ((pageInt-1) * sizeInt)-1;//算出最后的indexId

        Map<String,String> postData2 = new HashMap<>();
        postData2.put("resultType",type);
        postData2.put("filterJSON","{\"projectID\":"+projectId+",\"dataSourceType\":\""+dataTypeName+"\"}");
        postData2.put("startRow",String.valueOf(lastIndexId));
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
    }*/

    @RequestMapping(value = "/getTemplateFile.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "模板下载", notes = "", position = 0)
    public void getTemplateFile(HttpServletRequest req, HttpServletResponse res) {
        try {
            HttpClientHelper httpClientHelper = new HttpClientHelper();
            String crawlServerByEnv = WebUtil.getCrawlServerByEnv();

            HttpResponse getTermListResponse = httpClientHelper.doPostAndRetBytes(crawlServerByEnv+"/getTemplateFile.json", null, "utf-8", "utf-8", null, null);

            byte [] b = getTermListResponse.getContentBytes();

            OutputStream out = res.getOutputStream();

            res.reset();
            res.setHeader("Content-type", "application/xls");
            res.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder
                            .encode("关键词模板.xls", "UTF-8"));

            out.write(b);
            out.flush();
            out.close();

        }catch (HttpException e) {
            LoggerUtil.errorTrace(this.getClass().getSimpleName(), e);
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        } catch (IOException e) {
            LoggerUtil.errorTrace(this.getClass().getSimpleName(), e);
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
    }

    @RequestMapping(value = "/uploadFile.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "上传文件", notes = "", position = 0)
    public Map<String,String> uploadFile(@RequestParam(value = "file", required = true) MultipartFile file,
                                         HttpServletRequest req, HttpServletResponse res) {

        String fileName = System.currentTimeMillis() + file.getOriginalFilename();

        String fileBase = System.getProperty("upload.dir");
        String fileParamter = "/project/datacrawl/tmp/";
        String fileDir = fileBase + fileParamter + fileName;

        File destFileDir = new File(fileBase + fileParamter);
        if (!destFileDir.exists()) {
            destFileDir.mkdirs();
        }

        File destFile = new File(fileDir);
        if(!destFile.exists()){
            try {
                destFile.createNewFile();

                file.transferTo(destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String,String> returnMap = new HashMap<>();

        returnMap.put("url",fileName);

        return returnMap;
    }

    @RequestMapping(value = "/getUploadFile.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取上传文件", notes = "", position = 0)
    public void getUploadFile(@RequestParam(value = "url", required = true) @ApiParam(value = "文件地址", required = true) String url,
                        HttpServletRequest req, HttpServletResponse res) {

        if(Validate.isEmpty(url)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        LoggerUtil.debugTrace(this.getClass().getSimpleName(),"uploadFIle==="+url);

        String fileBase = System.getProperty("upload.dir");
        String fileParamter = "/project/datacrawl/tmp/";
        url=fileBase+fileParamter+url;
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

                LoggerUtil.debugTrace(this.getClass().getSimpleName(),"uploadFIle==="+url);

                out.flush();
                out.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/saveCrawlObject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存 数据抓取 接口", notes = "", position = 0)
    public CommonResultCodePO saveCrawlObject(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
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
        DataCrawlJsonParamPO dataCrawlJsonParamPO;

        try {
            dataCrawlJsonParamPO = JSON.parseObject(jsonParam, DataCrawlJsonParamPO.class);//转换 传上来的jsonParam
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        String datasourceTypeId = dataCrawlJsonParamPO.getDatasourceTypeId();
        DatasourceTypePO datasourceTypePO2 = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(datasourceTypeId));

        String storageTypeTable = datasourceTypePO2.getStorageTypeTable();

        dataCrawlJsonParamPO.setStorageTypeTable(storageTypeTable);

        DataCrawlPO dataCrawlPO = new DataCrawlPO();

        dataCrawlPO.setCrawlWay(dataCrawlJsonParamPO.getCrawlWay());
        dataCrawlPO.setCrawlType(dataCrawlJsonParamPO.getCrawlType());

        List<String> typeList = new ArrayList<>();
        typeList.add(com.transing.dpmbs.constant.Constants.PARAM_TYPE_CRAWL_WAY);
        List<ParamBO> paramBOList = paramService.getKeyValueListByType(typeList);
        for (ParamBO param:paramBOList) {
            if(param.getKey().equals(dataCrawlJsonParamPO.getCrawlWay())){

                dataCrawlJsonParamPO.setCrawlWayName(param.getValue());
                dataCrawlPO.setCrawlWayName(dataCrawlJsonParamPO.getCrawlWayName());
                break;

            }
        }
        dataCrawlPO.setWorkFlowTemplateId(dataCrawlJsonParamPO.getWorkFlowTemplateId());

        String crawlWay = dataCrawlJsonParamPO.getCrawlWay();
        if(crawlWay.equals("process")){
            int workFlowTemplateId = dataCrawlJsonParamPO.getWorkFlowTemplateId();
            List<WorkFlowNodeBO> workFlowNodeBOList = workFlowTemplateService.getWorkFlowNodeListByTemplateId(workFlowTemplateId);
            if( !Validate.isEmpty(workFlowNodeBOList)){
                WorkFlowNodeBO workFlowNodeBO = workFlowNodeBOList.get(0);
                String nodeParam = workFlowNodeBO.getNodeParam();
                JSONObject jsonObject = JSONObject.fromObject(nodeParam);
                dataCrawlJsonParamPO.setDatasourceId(jsonObject.optString("datasourceId"));
                dataCrawlJsonParamPO.setDatasourceTypeId(jsonObject.optString("datasourceTypeId"));

            }
        }

        String sourceType = "";
        if(dataCrawlJsonParamPO.getCrawlType().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
            typeNo = Constants.WORK_FLOW_TYPE_NO_DATACRAWL;
            sourceType = "1";
        }else if(dataCrawlJsonParamPO.getCrawlType().equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
            typeNo = Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL;
            sourceType = "2";
        }

        DatasourcePO datasourcePO = dataSourceTypeService.getDatasourceById(Long.parseLong(dataCrawlJsonParamPO.getDatasourceId()),sourceType);
        if(null == dataCrawlPO){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        for (DatasourceTypePO datasourceTypePO:datasourcePO.getDatasourceTypes()) {
            if(datasourceTypePO.getTypeId() == Long.parseLong(dataCrawlJsonParamPO.getDatasourceTypeId())){
                dataCrawlPO.setDatasourceTypeName(datasourceTypePO.getTypeName());
                dataCrawlJsonParamPO.setDatasourceTypeName(datasourceTypePO.getTypeName());
            }
        }

        dataCrawlPO.setJsonParam(dataCrawlJsonParamPO);
        dataCrawlPO.setDatasourceName(datasourcePO.getDatasourceName());
        dataCrawlJsonParamPO.setDatasourceName(datasourcePO.getDatasourceName());
        dataCrawlPO.setTaskName(dataCrawlJsonParamPO.getTaskName());
        String crawlFreqType = dataCrawlJsonParamPO.getCrawlFreqType();
        if ("1".equals(crawlFreqType)){
            dataCrawlPO.setCrawlFreq("单次抓取");
            dataCrawlJsonParamPO.setQuartzTime("");
        }else if("2".equals(crawlFreqType)){
            dataCrawlPO.setCrawlFreq(dataCrawlJsonParamPO.getQuartzTime());
        }

        List<JSONObject> dataCrawlInputParamPOList = dataCrawlJsonParamPO.getInputParamArray();
        String inputParams = "";
        if(null != dataCrawlInputParamPOList && dataCrawlInputParamPOList.size() > 0){
            for (JSONObject dataCrawlInputParamPO : dataCrawlInputParamPOList) {
                inputParams += dataCrawlInputParamPO.optString("paramCnName","") + dataCrawlInputParamPO.optString("paramValue","") + "\n";//TODO
            }
        }

        dataCrawlPO.setInputParams(inputParams);

        String quartzTime = dataCrawlJsonParamPO.getQuartzTime();
        if(quartzTime.endsWith("*")){//因为前端做不到 cron表达式的日期和周 要互斥。所以，后端把周如果是* 则 替换成?
            quartzTime = quartzTime.substring(0,quartzTime.length()-1);
            quartzTime += "?";
        }

        String jsonParamStr = JSON.toJSONString(dataCrawlPO);

        Long paramId = dataCrawlJsonParamPO.getParamId();
        if( null != paramId && paramId > 0){
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setParamId(paramId);
            workFlowParam.setJsonParam(jsonParamStr);
            workFlowService.updateWorkFlowParam(workFlowParam);//同时更新 输出字段

            workFlowParam = workFlowService.getWorkFlowParamByParamId(paramId);

            WorkFlowDetail workFlowDetail = new WorkFlowDetail();
            workFlowDetail.setFlowDetailId(workFlowParam.getFlowDetailId());
            workFlowDetail.setQuartzTime(quartzTime);
            workFlowService.updateWorkFlowDetailQuartzTime(workFlowDetail);
        }else{
            dataCrawlPO.setParamId(paramId);

            if(crawlWay.equals("process")){
                paramId = workFlowService.addWorkDetail(projectIdInt,typeNo,jsonParamStr,WorkFlowParam.PARAM_TYPE_PRIVATE,quartzTime,Long.parseLong(dataCrawlJsonParamPO.getDatasourceTypeId()),dataCrawlJsonParamPO.getWorkFlowTemplateId());
            }else if(crawlWay.equals("data")){
                paramId = workFlowService.addWorkDetail("0",projectIdInt,typeNo,jsonParamStr,WorkFlowParam.PARAM_TYPE_PRIVATE,quartzTime,Long.parseLong(dataCrawlJsonParamPO.getDatasourceTypeId()));
            }

            WorkFlowParam workFlowPara = workFlowService.getWorkFlowParamByParamId(paramId);
            long detailId = workFlowPara.getFlowDetailId();

            if (paramId > 0) {//如果 添加导入数据成功

                ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                projectJobTypeBO.setProjectId(projectIdInt);
                projectJobTypeBO.setTypeNo(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT);
                ProjectJobTypeBO projectJobType = projectJobTypeService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);

                if(null != projectJobType){
                    //添加 数据语义分析对象设置======================Start=============================================================

                    String jsonPara = workFlowPara.getJsonParam();
                    JSONObject jsonObject = JSONObject.fromObject(jsonPara);
                    Object datasourceTypeIdObj = jsonObject.get("datasourceTypeId");
                    if(null == datasourceTypeIdObj ){
                        datasourceTypeIdObj = jsonObject.getJSONObject("jsonParam").get("datasourceTypeId");
                    }

                    long dataSourceTypeId = Long.parseLong(datasourceTypeIdObj.toString());//数据源类型id
                    String dataSourceTypeName = dataCrawlPO.getJsonParam().getDatasourceTypeName();//数据源类型名字

                    //判断 语义分析对象是否添加过这个数据源类型的数据了 如果有则不添加
                    List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT, projectIdInt);

                    boolean isNeedAdd = true;//默认需要添加语义分析对象

                    if (null != workFlowParamList && workFlowParamList.size() > 0) {
                        for (WorkFlowParam workFlowParam : workFlowParamList) {
                            String jsonStr = workFlowParam.getJsonParam();
                            SemanticAnalysisObjectPO semanticAnalysisObject = JSON.parseObject(jsonStr, SemanticAnalysisObjectPO.class);
                            long seamDataSourceTypeId = semanticAnalysisObject.getJsonParam().getDataSourceTypeId();
                            if (seamDataSourceTypeId == dataSourceTypeId) {//如果语义分析对象已经添加过 这个数据源类型 则 设置 isNeedAdd 为false

                                //不需要添加，
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
                        semanticJsonParam.setStorageTypeTable(dataCrawlJsonParamPO.getStorageTypeTable());//新增 存储表名

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

        }

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        return commonResultCodePO;

    }

    @RequestMapping(value = "/delCrawlObject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除 抓取数据 接口", notes = "", position = 0)
    public CommonResultCodePO delCrawlObject(@RequestParam(value = "paramId",required = true)@ApiParam(value = "参数id",required = true) String paramId,
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

    @RequestMapping(value = "/startCrawlObject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "启动 抓取数据 接口", notes = "", position = 0)
    public CommonResultCodePO startCrawlObject(@RequestParam(value = "paramId",required = true)@ApiParam(value = "参数id",required = true) String paramId,
                                               @RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                               HttpServletRequest req, HttpServletResponse res) {
        if (paramId == null || "".equals(paramId) || !paramId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long paramIdInt = Long.parseLong(paramId);
        long projectIdInt = Long.parseLong(projectId);

        boolean isSecc = workFlowService.startWorkFlowByParamId(paramIdInt,projectIdInt,null);

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        if(!isSecc){
            throw new WebException(MySystemCode.BIZ_START_PROJECT_EXCEPTION);
        }

        return commonResultCodePO;

    }

    @RequestMapping(value = "/stopCrawlObject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "停止 抓取数据 接口", notes = "", position = 0)
    public CommonResultCodePO stopCrawlObject(@RequestParam(value = "paramId",required = true)@ApiParam(value = "参数id",required = true) String paramId,
                                               @RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                               HttpServletRequest req, HttpServletResponse res) {
        if (paramId == null || "".equals(paramId) || !paramId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long paramIdInt = Long.parseLong(paramId);
        long projectIdInt = Long.parseLong(projectId);

        boolean isSecc = workFlowService.stopWorkFlowByParamId(paramIdInt,projectIdInt);

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setCode(0);

        if(!isSecc){
            throw new WebException(MySystemCode.BIZ_START_PROJECT_EXCEPTION);
        }

        return commonResultCodePO;

    }


    @RequestMapping(value = "/getWorkFlowTemplateList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询抓取流程配置list 接口", notes = "", position = 0)
    public List<CrawlWorkFlowTemplatePO> getWorkFlowTemplateList(HttpServletRequest req, HttpServletResponse res) {

        List<WorkFlowTemplateBO> workFlowTemplateBOList = workFlowTemplateService.getWorkFlowTemplateListByParam(WorkFlowTemplateBO.STATUS_VALID,null,null,null);

        List<CrawlWorkFlowTemplatePO> crawlWorkFlowTemplatePOList = new ArrayList<>();

        for (WorkFlowTemplateBO workFlowTemplateBO:workFlowTemplateBOList) {
            CrawlWorkFlowTemplatePO crawlWorkFlowTemplatePO = new CrawlWorkFlowTemplatePO();
            crawlWorkFlowTemplatePO.setId(workFlowTemplateBO.getId());
            crawlWorkFlowTemplatePO.setName(workFlowTemplateBO.getName());

            List<WorkFlowNodeBO> workFlowNodeBOList = workFlowTemplateService.getWorkFlowNodeListByTemplateId(workFlowTemplateBO.getId());
            if(!Validate.isEmpty(workFlowNodeBOList)){
                WorkFlowNodeBO workFlowNodeBO = workFlowNodeBOList.get(0);
                String nodeParam = workFlowNodeBO.getNodeParam();
                long datasourceTypeId = JSONObject.fromObject(nodeParam).optLong("datasourceTypeId",0);
                crawlWorkFlowTemplatePO.setDatasourceTypeId(datasourceTypeId);

                crawlWorkFlowTemplatePOList.add(crawlWorkFlowTemplatePO);
            }

        }


        return crawlWorkFlowTemplatePOList;

    }

    @RequestMapping(value = "/getCrawlWayList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询抓取方式接口", notes = "", position = 0)
    public List<ParamBO> getCrawlWayList(HttpServletRequest req, HttpServletResponse res) {

        List<String> typeList = new ArrayList<>();
        typeList.add(com.transing.dpmbs.constant.Constants.PARAM_TYPE_CRAWL_WAY);
        return paramService.getKeyValueListByType(typeList);

    }

    @RequestMapping(value = "/getCrawlTypeList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询抓取类型配置list 接口", notes = "", position = 0)
    public List<ParamBO> getCrawlTypeList(HttpServletRequest req, HttpServletResponse res) {

        List<ParamBO> paramBOList = new ArrayList<>();

        List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();
        for (JobTypeInfo jobTypeInfo:jobTypeInfoList) {
            String typeNo = jobTypeInfo.getTypeNo();
            if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL) || typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
                String typeName = jobTypeInfo.getTypeName();
                ParamBO paramBO = new ParamBO();
                paramBO.setKey(typeNo);
                paramBO.setValue(typeName);
                paramBOList.add(paramBO);
            }
        }

        return paramBOList;

    }

}
