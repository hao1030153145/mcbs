package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.logicframework.integration.sao.hdfs.BaseSaoHDFS;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ParamService;
import com.transing.dpmbs.biz.service.ProjectExportService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.impl.local.DataSourceTypeServicePojo;
import com.transing.dpmbs.constant.Constants;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.filter.ProjectExportFilter;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.biz.service.WorkFlowTemplateService;
import com.transing.workflow.integration.bo.*;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.http.HttpException;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/11/21 0021.
 */
@Controller("projectExportController")
@Api(value = "导出任务管理", description = "导出任务接口", position = 3)
@RequestMapping(path = "/projectExport")
public class ProjectExportController {
    @Resource
    private ProjectExportService projectExportService;
    @Resource
    private JobTypeService jobTypeService;
    @Resource
    private ParamService paramService;
    @Resource
    private WorkFlowService workFlowService;
    @Resource
    private WorkFlowTemplateService workFlowTemplateService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @Resource
    private BaseSaoHDFS baseSaoHDFS;


    @RequestMapping(value = "/getProjectList.html", method = RequestMethod.GET)
    @ApiOperation(value = "进入项目导出列表页面", position = 0)
    @ResponseBody
    public ModelAndView getProjectList(@RequestParam(value="projectId") @ApiParam(value="项目Id") String projectId, HttpServletRequest req, HttpServletResponse res){
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("project",projectId);
        req.setAttribute("result", retMap);
        return new ModelAndView("/newProject/generalExport/projectList");
    }


    @RequestMapping(value = "/createExport.html", method = RequestMethod.GET)
    @ApiOperation(value = "进入项目创建导出页面", position = 0)
    @ResponseBody
    public ModelAndView createExport(HttpServletRequest req, HttpServletResponse res){
        Map<String, Object> retMap = new HashMap<>();
        req.setAttribute("result", retMap);
        return new ModelAndView("/newProject/generalExport/createExport");
    }

    @RequestMapping(value = "/getProjectExportList.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询导出任务列表",position=0)
    public ProjectPo getProjectExportList(@RequestParam(value = "page") @ApiParam(value = "页数") String page,
                                          @RequestParam(value = "size") @ApiParam(value = "请求个数") String size,
                                          @RequestParam(value="fileName",required = false) @ApiParam(value="文件名称") String fileName,
                                          @RequestParam(value="exportDataType",required = false) @ApiParam(value="导出数据类型") String exportDataType,
                                          @RequestParam(value="createTime",required = false) @ApiParam(value = "创建时间") String createTime,
                                          @RequestParam(value="projectType",required = false) @ApiParam(value = "项目类型") String projectType,
                                          @RequestParam(value = "projectId") @ApiParam(value = "项目id") String projectId,
                                          @RequestParam(value= "status",required = false)@ApiParam(value="状态") String status){
        ProjectPo projectPo = new ProjectPo();//返回结果
        List<Map<String,Object>> filterResult = new ArrayList<>();
        try{
            ProjectExportFilter projectExportFilter = new ProjectExportFilter();
            if(createTime!=null &&!createTime.equals("")){
                projectExportFilter.setCreateTimeStart(createTime+" 00:00:00");
                projectExportFilter.setCreateTimeEnd(createTime+" 23:59:59");
            }
            if(status!=null && !status.equals("")){
                projectExportFilter.setStatus(status);
            }
            projectExportFilter.setProjectId(Integer.parseInt(projectId));
            projectExportFilter.setPage((Long.parseLong(page)-1)*Long.parseLong(size));
            projectExportFilter.setSize(Long.parseLong(size));
            //通过项目id和创建时间查询出导出任务列表
            List<ProjectExportPo> result = projectExportService.getProjectExportListByProjectExportFilter(projectExportFilter);
            int resultCount = result.size();

            for (int i = resultCount-1; i >= 0; i--) {
                Map<String,Object> map = new HashMap<>();
                JSONObject jsonObject = JSON.parseObject(result.get(i).getJsonParam());//取出每个ProjectExportBO的jsonParam字段并转换成json格式
                //判断fileName是否为空并且是否等于jsonObject.getString("filename")
                if(fileName != null &&!fileName.equals("") && jsonObject.getString("fileName").indexOf(fileName)==-1){
                    result.remove(i);
                    continue;
                }
                if(exportDataType!=null && !jsonObject.get("exportChoice").equals(exportDataType) && !exportDataType.equals("")){//同理
                    result.remove(i);
                    continue;
                }

                map = jsonObject;
                map.put("id",result.get(i).getId());
                map.put("createTime",result.get(i).getCreateTime());
                map.put("status",result.get(i).getStatus());
                map.put("errorMessage",result.get(i).getErrorMessage());
                map.put("progress",result.get(i).getProgress());
                filterResult.add(map);
            }
            List<Map<String,Object>> filterResult2 = new ArrayList<>();
            for(int i=filterResult.size()-1;i>=0;i--){
                filterResult2.add(filterResult.get(i));
            }
//            int filterResult2Count = filterResult2.size();
//            if(Integer.parseInt(size)<filterResult2Count){
//                filterResult2 = filterResult2.subList((Integer.parseInt(page)-1)*Integer.parseInt(size),Integer.parseInt(size));
//            }
            projectPo.setProjectList(filterResult2);
            int count = projectExportService.getProjectExportCount(Integer.parseInt(projectId));
            projectPo.setCount(count);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return projectPo;
    }
    @RequestMapping(value = "/getConditionList.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "原始数据查询条件",position=0)
    public List<Map<String,Object>> getConditionList(){

        List<ParamBO> paramBOList = paramService.getParamBOList();
        Map<String,List<ParamBO>> listMap = new HashMap<>();
        for (ParamBO paramBO:paramBOList) {
            String type = paramBO.getType();
            List<ParamBO> list = listMap.get(type);//通过map的key取出value值,第一次取，永远是空，
            if(list == null){//如果value值是null,就创建一个list 并存入listMap中
                list = new ArrayList<>();
                listMap.put(type,list);
            }
            list.add(paramBO);//再将paramBO对象添加至list中
        }

        List<ParamBO> paramBOS = listMap.get(Constants.PARAM_TYPE_TYPEOF);
        List<Map<String,Object>> resultList = new ArrayList<>();
        for (ParamBO paramBO:paramBOS) {
            Map<String,Object> paramMap = new HashMap<>();
            List<Map<String,Object>> mapList = new ArrayList<>();
            List<ParamBO> mapList2 = listMap.get(paramBO.getValue());
            for(ParamBO paramBO2 : mapList2){
                Map<String,Object> map = new HashMap<>();
                map.put("conditionExp",paramBO2.getValue());
                map.put("conditionExpName",paramBO2.getKey());
                mapList.add(map);
            }
            paramMap.put("typeOf",paramBO.getValue());
            paramMap.put("conditionExpList",mapList);
            resultList.add(paramMap);
        }
        return resultList;
    }
    @RequestMapping( value = "/getFieldList.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据detailId查询字段list")
    public Set<CustomMap<String,Object>> getFieldList(@RequestParam(value = "nodeTaskDetailId") String nodeTaskDetailId,
                                                        @RequestParam(value = "detailId") String detailIds){
        Set<CustomMap<String,Object>> result = new HashSet<>();//返回结果,这里我自己定义了一个map，重写了它的equals方法,用于去除重复
        //将得到的detailIds转换为字符串数组
        String[] detailIdArray = detailIds.split(",");
        for(int i=0;i<detailIdArray.length;i++){
            CustomMap<String,Object> map = new CustomMap<>();
            //根据detailId 去查询work_flow_detail表记录
            WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(detailIdArray[i]));

            if(null == workFlowDetail){
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }

            if(workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)){
                WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(workFlowDetail.getFlowDetailId());
                String jsonParam = workFlowParam.getJsonParam();
                ImportData importData = com.alibaba.fastjson.JSON.parseObject(jsonParam, ImportData.class);

                String storageTypeTable = importData.getStorageTypeTable();
                StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeList(storageTypeTable);

                map.put("key",storageTypeTable);
                map.put("value",storageTypePO.getStorageTypeName());

                String origainRelation = importData.getOrigainRelation();

                List<RelationOrigin> relationOriginList = com.alibaba.fastjson.JSONObject.parseArray(origainRelation, RelationOrigin.class);
                Set<String> keySet = new HashSet<>();
                if(!Validate.isEmpty(relationOriginList)){
                    for (RelationOrigin relationOrigin:relationOriginList) {
                        keySet.add(relationOrigin.getKey().trim());
                    }
                }

                List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(importData.getTypeId());

                List<Map<String,Object>> list = new ArrayList<>();

                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                    if(keySet.contains(storageTypeFieldPO.getFieldEnName().trim())){
                        Map<String,Object> map2 = new HashMap<>();
                        map2.put("fieldCnName",storageTypeFieldPO.getFieldCnName());
                        map2.put("fieldEnName",storageTypeFieldPO.getFieldEnName());

                        if(storageTypeFieldPO.getFieldType().equals("int")){
                            map2.put("fieldType","number");
                        }else{
                            map2.put("fieldType",storageTypeFieldPO.getFieldType());
                        }

                        map2.put("id",storageTypeFieldPO.getId());
                        list.add(map2);

                    }
                }

                map.put("list",list);
                result.add(map);

            }else if(workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATACRAWL) ||
                    workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){

                Set<Map<String,String>> datasoureceTypeSet = new HashSet<>();//用来接收所有数据源类型的

                //如果传入了节点id 则以节点id为准
                if(!Validate.isEmpty(nodeTaskDetailId)){
                    workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(nodeTaskDetailId));
                }

                Map<String,String> paramMap = new HashMap<>();
                paramMap.put("typeNo",workFlowDetail.getTypeNo());
                paramMap.put("datasourceTypeId",workFlowDetail.getDataSourceType());

                datasoureceTypeSet.add(paramMap);

                Integer workFlowTemplateId = workFlowDetail.getWorkFlowTemplateId();
                //如果模板id不为空 且 节点 id为空 则查询全部
                if(null != workFlowTemplateId && workFlowTemplateId > 0 && Validate.isEmpty(nodeTaskDetailId)){
                    getAllProcessDetail(workFlowDetail,datasoureceTypeSet);
                }

                Map<String,Set<String>> storageTableFieldSet = new HashMap<>();

                if(!Validate.isEmpty(datasoureceTypeSet)){
                    for (Map<String,String> datasourceTypeAndTypeNo :datasoureceTypeSet) {

                        String dataSourceTypeId = datasourceTypeAndTypeNo.get("datasourceTypeId");
                        String typeNo = datasourceTypeAndTypeNo.get("typeNo");

                        //远程调用base系统 首先根据数据源类型id查询 数据源存储表

                        StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(dataSourceTypeId));

                        map.put("key",storageTypePO.getStorageTypeTable());
                        map.put("value",storageTypePO.getStorageTypeName());

                        Set<String> filedSet = storageTableFieldSet.get(storageTypePO.getStorageTypeTable());
                        if(null == filedSet){
                            filedSet = new HashSet<>();
                            storageTableFieldSet.put(storageTypePO.getStorageTypeTable(),filedSet);
                        }
                        //查询有规则的字段
                        List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationHasRuleList(dataSourceTypeId,typeNo);
                        Map<Integer,StorageTypeFieldPO> storageTypeFieldPOMap = new HashMap<>();
                        for(StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList){
                            storageTypeFieldPOMap.put(storageTypeFieldPO.getId(),storageTypeFieldPO);
                        }

                        //查询全部字段
                        List<StorageTypeFieldPO>  list2 = dataSourceTypeService.getDataSourceTypeRelationList(dataSourceTypeId);

                        List<Map<String,Object>> list = (List<Map<String, Object>>) map.get("list");
                        if(null == list){
                            list = new ArrayList<>();
                        }

                        if(!Validate.isEmpty(list2)){
                            for (StorageTypeFieldPO storageTypeFieldPO:list2) {
                                if(!filedSet.contains(storageTypeFieldPO.getFieldEnName())){
                                    filedSet.add(storageTypeFieldPO.getFieldEnName());
                                }else {
                                    continue;
                                }

                                Map<String,Object> map2 = new HashMap<>();
                                map2.put("fieldCnName",storageTypeFieldPO.getFieldCnName());
                                map2.put("fieldEnName",storageTypeFieldPO.getFieldEnName());
                                //如果该字段属于有规则字段
                                if(storageTypeFieldPOMap.get(storageTypeFieldPO.getId()) != null){
                                    map2.put("flag",true);
                                }
                                if(storageTypeFieldPO.getFieldType().equals("int")){
                                    map2.put("fieldType","number");
                                }else{
                                    map2.put("fieldType",storageTypeFieldPO.getFieldType());
                                }

                                map2.put("id",storageTypeFieldPO.getId());
                                list.add(map2);
                            }
                        }

                        map.put("list",list);
                        result.add(map);

                    }
                }

            }

            /*String url = WebUtil.getBaseServerByEnv();
            String url2 = DataSourceTypeServicePojo.GET_DATASOURCETYPEANDTABLENAME_API;
            String excutorUrl = url+url2;
            HttpClientHelper httpClientHelper = new HttpClientHelper();
            HttpResponse httpResponseGet = httpClientHelper.doGet(excutorUrl+"?datasourceTypeId="+dataSourceTypeId,null,null,null,null);
            String content = httpResponseGet.getContent();
            //解析返回值
            JSONObject jsonObjectContent = JSONObject.parseObject(content);
            String code = jsonObjectContent.getString("code");
            if(code.equals("0")) {
                //解析data  并得到storageTypeId，storageTypeName，storageTypeTable
                JSONObject dataJsonObject = jsonObjectContent.getJSONObject("data");
                int storageTypeId = dataJsonObject.getInteger("storageTypeId");
                String storageTypeName = dataJsonObject.getString("storageTypeName");
                String storageTypeTable = dataJsonObject.getString("storageTypeTable");

                //继续调用远程服务，然后根据数据源类型id和存储表id查询存储字段
                url2 = DataSourceTypeServicePojo.GET_STORAGETYPEFIELDLISTBYTYPEID_API;
                excutorUrl = url+url2;
                httpResponseGet = httpClientHelper.doGet(excutorUrl+"?datasourceTypeId="+dataSourceTypeId+"&storageTypeId="+storageTypeId,null,null,null,null);
                content = httpResponseGet.getContent();
                //解析返回值
                jsonObjectContent = JSONObject.parseObject(content);
                code = jsonObjectContent.getString("code");
                List<Map<String,Object>> list = new ArrayList<>();
                if(code.equals("0")){
                    JSONArray jsonArray = jsonObjectContent.getJSONArray("data");
                    for(Object object : jsonArray){
                        JSONObject jsonObject = (JSONObject)object;
                        Map<String,Object> map2 = new HashMap<>();
                        map2.put("fieldCnName",jsonObject.getString("fieldCnName"));
                        map2.put("fieldEnName",jsonObject.getString("fieldEnName"));
                        if(jsonObject.getString("fieldType").equals("int")){
                            map2.put("fieldType","number");
                        }else{
                            map2.put("fieldType",jsonObject.getString("fieldType"));
                        }
                        map2.put("id",jsonObject.get("id"));
                        list.add(map2);
                    }
                }
                map.put("list",list);
            }
            result.add(map);*/
        }
        return result;
    }

    public void getAllProcessDetail(WorkFlowDetail workFlowDetail,Set<Map<String,String>> datasoureceTypeSet){

        String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
        if(!Validate.isEmpty(nextFlowDetailIds)){
            String [] nextDetailArray = nextFlowDetailIds.split(",");
            for (String detail :nextDetailArray) {
                workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(detail));
                if(workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATACRAWL)
                        ||workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){

                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("typeNo",workFlowDetail.getTypeNo());
                    paramMap.put("datasourceTypeId",workFlowDetail.getDataSourceType());
                    datasoureceTypeSet.add(paramMap);

                    getAllProcessDetail(workFlowDetail,datasoureceTypeSet);
                }
            }
        }
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
                            detailId = workFlowDetail.getFlowDetailId();
                            return workFlowDetail.getFlowDetailId();
                        }

                    }

                }

            }else {
                detailId = workFlowDetail.getFlowDetailId();
                return workFlowDetail.getFlowDetailId();
            }

        }

        return detailId;
    }

    @RequestMapping(value = "/saveProjectExport.json",method = RequestMethod.POST)
    @ApiOperation(value = "保存导出接口",position=0)
    @ResponseBody
    public CommonResultCodePO saveProjectExport(@RequestParam(value = "body") String body) throws Exception {
        //将传入的jsonString转为JSON
        JSONObject jsonObjectParam = JSONObject.parseObject(body);
        //创建project_export对象
        ProjectExportBO projectExportBO = new ProjectExportBO();
        int projectId = Integer.parseInt(jsonObjectParam.getString("projectId"));//获取json中的项目id

        String fileName = jsonObjectParam.getString("fileName");
        //过滤掉特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher m = pattern.matcher(fileName);
        if(m.find()){
            throw new WebException(MySystemCode.BIZ_PROJECTEXPORT_FILENAME_NOALLOW);
        }
        //查询导出文件名是否存在
        ProjectExportFilter filter = new ProjectExportFilter();
        filter.setProjectId(projectId);
        List<ProjectExportPo> list = projectExportService.getProjectExportListByProjectExportFilter(filter);
        if(!Validate.isEmpty(list)){
            for(ProjectExportPo p : list){
                JSONObject pj = JSON.parseObject(p.getJsonParam());
                if(fileName.equals(pj.getString("fileName"))){
                    throw new WebException(MySystemCode.BIZ_CREATE_PROJECTEXPORT);
                }
            }
        }
        //新建一个json 将传过来的部分参数放在里面
        JSONObject newJsonObject = new JSONObject();
        JSONArray detailIdArray = jsonObjectParam.getJSONArray("detailIdArray");

        JSONArray newDetailIdArray = new JSONArray();
        //如果是流程节点则找出最后一个节点
        for (Object object :detailIdArray) {
            Long detailId = Long.parseLong(object.toString());
            detailId = getLastDetailId(detailId);
            newDetailIdArray.add(detailId);
        }

        String exportChoice = jsonObjectParam.getString("exportChoice");
        newJsonObject.put("exportType",jsonObjectParam.getString("exportType"));//导出的文件类型。csv 或者excel
        newJsonObject.put("fileName",fileName);//文件名称
        newJsonObject.put("exportChoice",exportChoice);//导出选择,英文
        newJsonObject.put("exportChoiceCnName",jsonObjectParam.getString("exportChoiceCnName"));//导出选择，中文
        newJsonObject.put("taskName",jsonObjectParam.getJSONArray("taskNameArray").get(0).toString().replaceAll("\"",""));//任务名称
        newJsonObject.put("detailIdArray",newDetailIdArray);//任务detailId数组
        //判断该项目的导出选择是否是原始数据或者原始+语义数据
        if(exportChoice.equals("originalData")){
            String dataWay = jsonObjectParam.getString("dataWay");
            newJsonObject.put("dataWay",dataWay);      //数据方式（结果数据、节点数据)
//            if(dataWay.equals("node")){
            String nodeTaskDetailId = jsonObjectParam.getString("nodeTaskDetailId");
            if(!nodeTaskDetailId.equals("")&&nodeTaskDetailId!=null){
                newDetailIdArray = new JSONArray();
                newDetailIdArray.add(nodeTaskDetailId);
                newJsonObject.put("detailIdArray",newDetailIdArray);
                WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Integer.parseInt(nodeTaskDetailId));
                newJsonObject.put("dataSourceTypeId",workFlowDetail.getDataSourceType());//数据源类型
            }else {
                //通过detailId去查询workFlowDetail表.得到记录
                WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(newDetailIdArray.getLong(0));
                if(workFlowDetail.getWorkFlowTemplateId()!=null){
                    List<WorkFlowNodeBO> workFlowNodeBOList = workFlowService.getWorkFlowNodeByTemplateId(workFlowDetail.getWorkFlowTemplateId());
                    //如果是结果数据，则只需要查询出最后一个节点的datasourceTypeId
                    JSONObject jsonObject =JSONObject.parseObject(workFlowNodeBOList.get(workFlowNodeBOList.size()-1).getNodeParam());
                    String dataSourceTypeId = jsonObject.getString("datasourceTypeId");
                    newJsonObject.put("dataSourceTypeId",dataSourceTypeId);//数据源类型
                }else{
                    newJsonObject.put("dataSourceTypeId",workFlowDetail.getDataSourceType());//数据源类型
                }
            }
            newJsonObject.put("fieldAndFilter",jsonObjectParam.get("fieldAndFilter"));  //原始数据筛选条件
            JSONArray jsonArray = jsonObjectParam.getJSONArray("dataSourceTypeArray");
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String storageTypeTable = jsonObject.getString("key");
                newJsonObject.put("storageTypeTable",storageTypeTable);   //存储类型的表
            }
            newJsonObject.put("dataSourceTypeField",jsonArray);  //数据源类型字段数组
        }else if(exportChoice.equals("orAndAn")){//如果是原始+语义
            String dataWay = jsonObjectParam.getString("dataWay");
            newJsonObject.put("dataWay",dataWay);      //数据方式（结果数据、节点数据)
            String nodeTaskDetailId = jsonObjectParam.getString("nodeTaskDetailId");
            if(!nodeTaskDetailId.equals("")&&nodeTaskDetailId!=null){
                newJsonObject.put("detailIdArray",nodeTaskDetailId);
                WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Integer.parseInt(nodeTaskDetailId));
                newJsonObject.put("dataSourceTypeId",workFlowDetail.getDataSourceType());//数据源类型
            }else {
                //通过detailId去查询workFlowDetail表.得到记录
                WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(newDetailIdArray.getLong(0));
                if(workFlowDetail.getWorkFlowTemplateId()!=null){
                    List<WorkFlowNodeBO> workFlowNodeBOList = workFlowService.getWorkFlowNodeByTemplateId(workFlowDetail.getWorkFlowTemplateId());
                    if(!Validate.isEmpty(workFlowNodeBOList)){
                        //如果是结果数据，则只需要查询出最后一个节点的datasourceTypeId
                        JSONObject jsonObject =JSONObject.parseObject(workFlowNodeBOList.get(workFlowNodeBOList.size()-1).getNodeParam());
                        String dataSourceTypeId = jsonObject.getString("datasourceTypeId");
                        newJsonObject.put("dataSourceTypeId",dataSourceTypeId);//数据源类型
                    }else {
                        newJsonObject.put("dataSourceTypeId",workFlowDetail.getDataSourceType());//数据源类型
                    }
                }else{
                    newJsonObject.put("dataSourceTypeId",workFlowDetail.getDataSourceType());//数据源类型
                }
            }
            newJsonObject.put("fieldAndFilter",jsonObjectParam.get("fieldAndFilter"));  //原始数据筛选条件
            JSONArray jsonArray = jsonObjectParam.getJSONArray("dataSourceTypeArray");
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String storageTypeTable = jsonObject.getString("key");
                newJsonObject.put("storageTypeTable",storageTypeTable);   //存储类型的表
            }
            newJsonObject.put("dataSourceTypeField",jsonArray);  //数据源类型字段数组
            newJsonObject.put("level",jsonObjectParam.getJSONObject("level"));
        }else if(exportChoice.equals("semanticResult")){
            WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(newDetailIdArray.getLong(0));
            StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(workFlowDetail.getDataSourceType()));
            if(storageTypePO!=null){
                newJsonObject.put("storageTypeTable",storageTypePO.getStorageTypeTable());//存储类型的表
            }else{
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }
            newJsonObject.put("level",jsonObjectParam.getJSONObject("level"));
        }else{

        }
        //赋值
        projectExportBO.setProjectId(projectId);
        projectExportBO.setJsonParam(newJsonObject.toJSONString());
        projectExportBO.setStatus(1);//在保存时就将任务状态更新为进行中，保存后就开始进行导出
        projectExportBO.setProgress(0);
        //新增
        projectExportService.addProjectExportBO(projectExportBO);

        //执行导出

        String dpmssServer = WebUtil.getDpmssServerByEnv();
        new Thread(new ExecuteProjectExport(String.valueOf(projectExportBO.getId()),dpmssServer)).start();
//        startExportProject(String.valueOf(projectExportBO.getId()));
        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        return commonResultCodePO;
    }

    @RequestMapping(value="/getDataTypeList.json",method = RequestMethod.GET)
    @ApiOperation(value="查询导出选择的下拉选项")
    @ResponseBody
    public List<Map<String,Object>> getDataTypeList(){
        //查询出所有Constants.PARAM_TYPE_EXPORTSELECT的paramBo
        List<ParamBO> list = paramService.getParamBoListByType(Constants.PARAM_TYPE_EXPORTSELECT);

        List<Map<String,Object>> result = new ArrayList<>();
        //遍历所有paramBo，得到每个paramBo的key和value
        for(ParamBO paramBO : list){
            Map<String,Object> map = new HashMap<>();
            map.put("key",paramBO.getKey());
            map.put("value",paramBO.getValue());
            result.add(map);
        }
        return result;
    }

    @RequestMapping(value="/getTaskNameList.json",method = RequestMethod.GET)
    @ApiOperation(value="查询任务名称下拉选项")
    @ResponseBody
    public List<Map<String,Object>> getTaskNameList(@RequestParam(value="projectId") String projectId,
                                                    @RequestParam(value="exportChoice") String exportChoice){
       List<Map<String,Object>> result = new ArrayList<>();//返回需要的lsit

        if(Validate.isEmpty(exportChoice)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        if("orAndAn".equals(exportChoice)
                ||"originalData".equals(exportChoice)
                ||"semanticResult".equals(exportChoice)){

            //通过项目id查询 首节点 的 WorkFlowDetail List
            List<WorkFlowDetail> workFlowDetailList = workFlowService.getFirstDetailByProjectId(Long.parseLong(projectId));

            //查询所有的param
            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamByProJectId(Long.parseLong(projectId));
            Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
            for (WorkFlowParam workFlowParam:workFlowParamList) {
                workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
            }

            //遍历所有的WorkFlowDetail。拿到每一个typeNo并存入到list中
            for(WorkFlowDetail workFlowDetail : workFlowDetailList){
                //判断是否为首节点 如果是首节点，则添加至list中
                if(workFlowDetail.getPrevFlowDetailIds().equals("0")){
                    Map<String,Object> map = new HashMap<>();
                    map.put("detailId",workFlowDetail.getFlowDetailId());

                    WorkFlowParam workFlowParam = workFlowParamMap.get(workFlowDetail.getFlowDetailId());
                    String jsonParam = workFlowParam.getJsonParam();
                    JSONObject jsonObject = JSONObject.parseObject(jsonParam);

                    if(workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)){

                        String typeName = jsonObject.getString("typeName");
                        map.put("taskName","导入 "+typeName);

                    }else if(workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATACRAWL) ||
                            workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){

                        String taskName = jsonObject.getString("taskName");
                        map.put("taskName","抓取 "+taskName);

                    }

                    map.put("flowId",workFlowParam.getFlowId());
                    result.add(map);

                }
            }

        }
        /*else if("semanticResult".equals(exportChoice)){
            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT,Long.parseLong(projectId));

            for (WorkFlowParam workFlowParam:workFlowParamList) {

                SemanticAnalysisObjectPO semanticAnalysisObjectPO = JSON.parseObject(workFlowParam.getJsonParam(),SemanticAnalysisObjectPO.class);

                Map<String,Object> map = new HashMap<>();
                map.put("detailId",workFlowParam.getFlowDetailId());
                String taskName = semanticAnalysisObjectPO.getDataSourceTypeName();
                map.put("taskName","语义 "+taskName);

                map.put("flowId",workFlowParam.getFlowId());
                result.add(map);

            }

        }*/
        else if("statisticalResult".equals(exportChoice)){

            List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_STATISTICAL,Long.parseLong(projectId));

            for (WorkFlowParam workFlowParam:workFlowParamList) {

                StatisticsAnalysisPo statisticsAnalysisPo = JSON.parseObject(workFlowParam.getJsonParam(), StatisticsAnalysisPo.class);

                Map<String,Object> map = new HashMap<>();
                map.put("detailId",workFlowParam.getFlowDetailId());
                String taskName = statisticsAnalysisPo.getName();
                map.put("taskName","统计 "+taskName);

                map.put("flowId",workFlowParam.getFlowId());
                result.add(map);

            }

        }

        /*//再次遍历list
        for(Long detailId : list){

            //通过detailId查询出WorkFlowParam.
            WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(detailId);
            JSONObject jsonObject = JSONObject.parseObject(workFlowParam.getJsonParam());

            if(workFlowParam.getTypeNo().equals("dataImport")){
                map.put("taskName","导入 "+jsonObject.get("taskName"));
            }else{
                map.put("taskName","导出 "+jsonObject.get("taskName"));
            }
            map.put("flowId",workFlowParam.getFlowId());
            result.add(map);
        }*/
        return result;
    }

    @RequestMapping(value="/getNodeNameList.json",method = RequestMethod.GET)
    @ApiOperation(value="根据detailId查询节点任务名称")
    @ResponseBody
    public List<Map<String,Object>> getNodeNameList(@RequestParam(value="detailId") String detailId){
        List<Map<String,Object>> result = new ArrayList<>();//返回list
        //通过detailId查询WorkFlowParam
        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByDetailId(Long.parseLong(detailId));
        //这里需要判断该流程的节点类型是否是抓取
        if(workFlowParam.getTypeNo().equals("dataCrawl")){
            //得到workFlowParam中的jsonParam
            JSONObject jsonObject = JSONObject.parseObject(workFlowParam.getJsonParam());
            if(jsonObject.getString("crawlWay").equals("data")){//如果是数据抓取，则只需要获取datasourceTypeName
                Map<String,Object> map = new HashMap<>();
                String datasourceTypeName = jsonObject.getString("datasourceTypeName");
                map.put("detailId",detailId);
                map.put("name",datasourceTypeName);
                result.add(map);
            }else{//如果是流程抓取，则可以获取workFlowTemplateId 来查询process
                String workFlowTemplateId = jsonObject.getString("workFlowTemplateId");
                WorkFlowTemplateBO workFlowTemplateBO = workFlowTemplateService.getWorkFlowTemplateListById(Integer.parseInt(workFlowTemplateId));
                //取出来的是一个数组字符串 要转为数组
                String[] process = workFlowTemplateBO.getProcess().replace("[","").replaceAll("]","").split(",");
                for(int i=0;i<process.length;i++){
                    Map<String,Object> map = new HashMap<>();
                    map.put("detailId",workFlowParam.getFlowDetailId()+i);
                    map.put("name",process[i].replaceAll("\"",""));
                    result.add(map);
                }
            }
        }
        return result;
    }

    @RequestMapping(value="/getExportFileTypeList.json",method = RequestMethod.GET)
    @ApiOperation(value="查询导出形式的下拉选项")
    @ResponseBody
    public List<Map<String,Object>> getExportFileTypeList(){
        //查询出所有Constants.PARAM_TYPE_EXPORTSELECT的paramBo
        List<ParamBO> list = paramService.getParamBoListByType(Constants.PARAM_TYPE_FILETYPE);
        List<Map<String,Object>> result = new ArrayList<>();
        //遍历所有paramBo，得到每个paramBo的key和value
        for(ParamBO paramBO : list){
            Map<String,Object> map = new HashMap<>();
            map.put("key",paramBO.getKey());
            map.put("value",paramBO.getValue());
            result.add(map);
        }
        return result;
    }

    @RequestMapping(value = "/startProjectExport.json", method = RequestMethod.POST)
    @ApiOperation(value = "执行项目导出", notes = "", position = 0)
    @ResponseBody
    public CommonResultCodePO startExportProject(@RequestParam(value = "projectExportTaskId") @ApiParam(value = "导出项目任务id") String id){
        String dpmssServer = WebUtil.getDpmssServerByEnv();

        new Thread(new ExecuteProjectExport(id,dpmssServer)).start();

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        return commonResultCodePO;
    }

    @RequestMapping(value = "/updateProjectExport.json", method = RequestMethod.POST)
    @ApiOperation(value = "更新导出的回调方法", notes = "", position = 0)
    @ResponseBody
    public CommonResultCodePO completeProjectExport(@RequestParam(value = "id") @ApiParam(value = "导出项目任务id") String id,
                                                    @RequestParam(value = "status",defaultValue = "2") @ApiParam(value = "任务状态") String status,
                                                    @RequestParam(value = "errorMessage",required = false) @ApiParam(value = "导出项目任务id") String errorMessage,
                                                    @RequestParam(value = "fileUrl",required = false) @ApiParam(value = "导出项目完成的下载url") String fileUrl) throws Exception {

        if (id == null || "".equals(id) || !id.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        Map<String,String> map1 = new HashMap<>();
        map1.put("id",id);
        map1.put("status",status);

        if(!Validate.isEmpty(fileUrl)){
            String fileBase = System.getProperty("upload.dir");
            String fileParam = "/project/export/";
            String fileName = id+System.currentTimeMillis()+".zip";

            File fileDir = new File(fileBase + fileParam);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }

            File file = new File(fileBase + fileParam + fileName);

            //下载文件
            try {
                baseSaoHDFS.downloadFile(fileUrl,file.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception(e);
            }

            net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
            jsonObject.put("fileUrl",fileParam + fileName);

            map1.put("resultJsonParam",jsonObject.toString());
        }

        if(!Validate.isEmpty(errorMessage)){
            map1.put("errorMessage",errorMessage);
        }

        projectExportService.updateStatusById(map1);

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        return commonResultCodePO;
    }


    class ExecuteProjectExport implements Runnable{

        private String id;
        private String dpmssServer;

        public ExecuteProjectExport(String id, String dpmssServer) {
            this.id = id;
            this.dpmssServer = dpmssServer;
        }

        @Override
        public void run() {

            //通过项目id得到导出项目的信息
            ProjectExportBO projectExportBO = projectExportService.getProjectExportById(Long.parseLong(id));
            Map<String,String> map = new HashMap<>();
            //将dpmss系统需要的参数保存在map中，一并传过去
            String projectId = projectExportBO.getProjectId()+"";
            map.put("projectId",projectId);
            map.put("flowId",id);
            map.put("flowDetailId",id);
            map.put("workFlowId", "0");
            map.put("typeNo",Constants.PROJECTEXPORT_TYPE);
            map.put("jsonParam",projectExportBO.getJsonParam());
            map.put("paramType","0");
            HttpClientHelper httpClientHelper = new HttpClientHelper();
            //得到远程调用的地址
            String excutorUrl = dpmssServer+Constants.PROJECTEXPORT_EXECUTEUrl;
            String progressUrl = dpmssServer+Constants.PROJECTEXPORT_PROGRESSURL;
            String resultUrl = dpmssServer+Constants.PROJECTEXPORT_RESULTURL;
            try {

                //远程调用
                HttpResponse httpResponse = httpClientHelper.doPost(excutorUrl, map, "utf-8", null, null, null);
                String content = httpResponse.getContent();
                JSONObject jsonObject = JSONObject.parseObject(content);
                Object code = jsonObject.get("code");
                Object message = jsonObject.get("message");
                if(code.toString().equals("0")) {
                    /*boolean isFinish = false;
                    while (!isFinish){
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        HttpResponse httpResponseGet = httpClientHelper.doGet(progressUrl+"?projectId="+projectId+"&flowId="+id+"&flowDetailId="+id+"&typeNo=projectOutput",null,null,null,null);
                        String jsonResult = httpResponseGet.getContent();
                        JSONObject jsonObjectResult = JSON.parseObject(jsonResult);
                        Object reCode = jsonObjectResult.get("code");
                        if(reCode.toString().equals("0")){
                            jsonObjectResult = jsonObjectResult.getJSONObject("data");
                            String jobStatus = jsonObjectResult.getString("jobStatus");
                            String jobProgress = jsonObjectResult.getString("jobProgress");
                            String errorMessage= jsonObjectResult.getString("errorMsg");

                            int jobProgressInt = Integer.parseInt(jobProgress.toString());
                            projectExportService.updateProgressById(Long.parseLong(id),jobProgressInt);
                            if(null != jobStatus&& jobStatus.toString().equals("2")){
                                isFinish = true;
                                Map<String,String> map1 = new HashMap<>();
                                map1.put("id",id);
                                map1.put("status",jobStatus);
                                //调用远程服务
                                httpResponse = httpClientHelper.doPost(resultUrl,map,"utf-8",null,null,null);
                                content = httpResponse.getContent();
                                JSONObject jsonObject1 = JSONObject.parseObject(content);
                                JSONArray jsonArray = jsonObject1.getJSONArray("data");
                                JSONObject J = (JSONObject)jsonArray.get(jsonArray.size()-1);
                                String resultJsonParam = J.getString("resultJsonParam");
                                map1.put("resultJsonParam",resultJsonParam);
                                projectExportService.updateStatusById(map1);
                            } else if(null != jobStatus&& jobStatus.toString().equals("9")){
                                isFinish = true;
                                Map<String,String> map1 = new HashMap<>();
                                map1.put("id",id);
                                map1.put("status",jobStatus);
                                map1.put("errorMessage",errorMessage);
                                projectExportService.updateStatusById(map1);
                            }
                        }
                    }*/
                }else {
                    Map<String,String> map1 = new HashMap<>();
                    map1.put("id",id);
                    map1.put("status",9+"");
                    map1.put("errorMessage",message.toString());
                    projectExportService.updateStatusById(map1);
                }
            }catch (HttpException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @RequestMapping(value = "/deleteExportTask.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除导出任务", notes = "", position = 0)
    public CommonResultCodePO deleteExportTask(@RequestParam(value = "id") String id){
        String [] ids = id.split(",");
        String dpmssFilePathByEnv = System.getProperty("download.dir");
        if(ids!=null){
            for(int i=0;i<ids.length;i++){
                ProjectExportBO  projectExportBO = projectExportService.getProjectExportById(Integer.parseInt(ids[i]));
                if(projectExportBO.getResultJsonParam()!=null){
                    String reslutJsonParam = projectExportBO.getResultJsonParam();
                    JSONObject jsonObject = JSONObject.parseObject(reslutJsonParam);
                    String fileName = jsonObject.getString("fileUrl");
                    String downloadUrl = dpmssFilePathByEnv+"/"+fileName;
                    File file = new File(downloadUrl);
                    if(file.exists()){
                        file.delete();
                    }
                    projectExportService.deleteExportProjectById(Integer.parseInt(ids[i]));
                }else{
                    projectExportService.deleteExportProjectById(Integer.parseInt(ids[i]));
                }
            }
        }
        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        return commonResultCodePO;
    }

    @RequestMapping(value = "/downloadTask.json" ,method = RequestMethod.GET)
    @ApiOperation(value = "下载", notes = "", position = 0)
    public void download(@RequestParam(value="id") String id, HttpServletResponse resp) throws Exception{
        Map<String, Object> map = new HashMap<>();
        try {
            ProjectExportBO projectExportBO = projectExportService.getProjectExportById(Long.parseLong(id));
            JSONObject jsonParam = JSONObject.parseObject(projectExportBO.getJsonParam());
            String zipName = projectExportBO.getResultJsonParam();
            JSONObject jsonObject = JSONObject.parseObject(zipName);
            String zipUrl = jsonObject.getString("fileUrl");

            String fileBase = System.getProperty("upload.dir");

            File file = new File(fileBase + zipUrl);

            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf("."));

            String fileNamePath = jsonParam.getString("fileName")+suffix;

            if(file.exists()){

                resp.setContentType("multipart/form-data");
                //处理下载弹出框名字的编码问题
                resp.setHeader("Content-Disposition", "attachment;fileName="
                        + new String( fileNamePath.getBytes("gb2312"), "ISO8859-1" ));

                //利用输入输出流对文件进行下载
                InputStream inputStream = new FileInputStream(file);

                OutputStream os = resp.getOutputStream();
                byte[] b = new byte[2048];
                int length;
                while ((length = inputStream.read(b)) > 0) {
                    os.write(b, 0, length);
                }
                // 这里主要关闭。
                os.close();

                inputStream.close();

            }
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
    }

    @RequestMapping(value = "/testDown.json" ,method = RequestMethod.GET)
    @ApiOperation(value = "返回下载路径", notes = "", position = 0)
    public void testDown(HttpServletResponse resp){

        try {

            File file = new File("D:\\output.csv");

            resp.setContentType("multipart/form-data");
            //处理下载弹出框名字的编码问题
            resp.setHeader("Content-Disposition", "attachment;fileName="
                    + new String( file.getName().getBytes("gb2312"), "ISO8859-1" ));

            //利用输入输出流对文件进行下载
            InputStream inputStream = new FileInputStream(file);

            OutputStream os = resp.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
            // 这里主要关闭。
            os.close();

            inputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
