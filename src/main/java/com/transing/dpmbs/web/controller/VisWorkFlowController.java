package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.*;
import com.transing.dpmbs.constant.Constants;
import com.transing.dpmbs.constant.RedisKey;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.util.Base64Util;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.filter.ProjectCreateFilter;
import com.transing.dpmbs.web.filter.ProjectExportFilter;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.dpmbs.web.filter.WorkFlowListFilter;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.biz.service.WorkFlowTemplateService;
import com.transing.workflow.integration.bo.*;
import com.transing.workflow.util.VisWorkFlowProjectStart;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import org.apache.http.HttpException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by byron on 2018/1/4 0004.
 */
@Controller("visWorkFlowController")
@Api(value = "可视化工作流项目", description = "可视化工作流项目相关接口", position = 2)
@RequestMapping("/visWorkFlow")
public class VisWorkFlowController {
    @Resource
    private ProjectService projectService;
    @Resource
    private WorkFlowService workFlowService;
    @Resource
    private VisWorkFlowService visWorkFlowService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;
    @Resource
    private ProjectExportService projectExportService;
    @Resource
    private BaseDaoRedis redisClient;
    private String uploadPath = WebUtil.getDpmbsUploadByEnv();

    @RequestMapping(value = "/saveOrUpdateProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存（更新）可视化工作流项目", position = 0)
    public Map<String, Object> saveOrUpdateProject(@RequestParam(value = "projectId", required = false) @ApiParam(value = "项目id") String projectId,
                                                   @RequestParam(value = "projectName") @ApiParam(value = "项目名称") String projectName,
                                                   @RequestParam(value = "projectDescribe", required = false) @ApiParam(value = "项目描述") String projectDescribe,
                                                   @RequestParam(value = "typeId") @ApiParam(value = "项目类型Id") String typeId,
                                                   @RequestParam(value = "managerId") @ApiParam(value = "项目经理名称id") String managerId,
                                                   @RequestParam(value = "customerId") @ApiParam(value = "项目经理名称id") String customerId,
                                                   @RequestParam(value = "startTime") @ApiParam(value = "开始时间") String startTime,
                                                   @RequestParam(value = "endTime") @ApiParam(value = "结束时间") String endTime,
                                                   @RequestParam(value = "projectType") @ApiParam(value = "项目类型") String projectType
    ) {
        Map<String, Object> resultMap = new HashMap<>();
        ProjectCreateFilter filter;
        //过滤掉特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher m = pattern.matcher(projectName);
        if(m.find()){
            throw new WebException(MySystemCode.BIZ_PROJECTEXPORT_FILENAME_NOALLOW);
        }
        if(projectName.length()>50){
            throw new WebException(MySystemCode.BIZ_PROJECTNAMETOOLONG);
        }
        if (projectId != null && !projectId.equals("")) {
            long projectIdInt = Long.parseLong(projectId);
            filter = new ProjectCreateFilter(projectId, projectName, projectDescribe, typeId, managerId, customerId, startTime, endTime, projectType);

            ProjectOne projectOne = projectService.getProjectInf(projectIdInt);
            if (!projectOne.getProjectName().equals(filter.getProjectName())) {
                if (projectService.selectProject(projectName) > 0) {
                    throw new WebException(MySystemCode.BIZ_CREATE_PROJECT);
                }
            }
            Integer result = projectService.updateProject(filter);
            if (result == null) {
                throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
            }
        } else {
            if (projectService.selectProject(projectName) > 0) {
                throw new WebException(MySystemCode.BIZ_CREATE_PROJECT);
            }
            filter = new ProjectCreateFilter(projectName, projectDescribe, typeId, managerId, customerId, startTime, endTime, projectType);
            filter.setStatus("2");
            Integer result = projectService.createProject(filter);
            if (result == null) {
                throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
            }
            projectId = filter.getId();
        }
        resultMap.put("projectId", projectId);
        return resultMap;
    }


    @Resource
    private WorkFlowTemplateService workFlowTemplateService;

    @RequestMapping(value = "/toCreateVisWorkFlowProject.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "跳转到可视化项目创建页面", position = 0)
    public ModelAndView createVisWorkFlowProjectHtml(HttpServletRequest req, HttpServletResponse res) {
        return new ModelAndView("newProject/visualization/createProject");
    }

    @RequestMapping(value = "/toWorkFlowList.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "跳转到可视化工作流列表页面", position = 0)
    public ModelAndView WorkFlowList(@RequestParam(value = "projectId") @ApiParam(value = "项目id") String projectId,
            HttpServletRequest req, HttpServletResponse res) {
        Map<String, Object> resultMap = new HashMap<>();
        if ("" != projectId && null != projectId ) {
            resultMap.put("projectId", projectId);
        }else{
            throw new WebException(MySystemCode.BIZ_PARAMERRO_EXCEPTION_MESSAGE);
        }
        req.setAttribute("resultMap", resultMap);
        return new ModelAndView("newProject/visualization/workFlowList");
    }

    @RequestMapping(value = "/getWorkFlowList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询可视化工作流列表", position = 0)
    public CommonPageListPO getWorkFlowList(@RequestParam(value = "projectId") @ApiParam(value = "项目id",required = true) Long projectId,
                                            @RequestParam(value = "workFlowName",required = false) @ApiParam(value="工作流名称") String workFlowName,
                                            @RequestParam(value = "status",required = false) @ApiParam(value="工作流状态") Integer status,
                                            @RequestParam(value = "page") @ApiParam(value = "页码",required = true) Integer page,
                                            @RequestParam(value = "pageSize") @ApiParam(value = "每页条数",required = true) Integer pageSize,
                                            @RequestParam(value = "sort",required = false) @ApiParam(value = "排序字段及排序方式") String sort){
        CommonPageListPO commonPageListPO = new CommonPageListPO();

        WorkFlowListFilter workFlowListFilter = new WorkFlowListFilter();
        workFlowListFilter.setProjectId(projectId);
        workFlowListFilter.setStatus(status);
        workFlowListFilter.setWorkFlowName(workFlowName);
        workFlowListFilter.setPage((page-1)*pageSize);
        workFlowListFilter.setSize(pageSize);
        workFlowListFilter.setSort(sort);
        long count=workFlowService.workFlowListCount(projectId,status);
        List<WorkFlowListBO> workFlowListBOList = workFlowService.getWorkFlowListPOByFilter(workFlowListFilter);
        for(WorkFlowListBO workFlowListBO:workFlowListBOList){
            if(workFlowListBO.getImg()!=null){
                workFlowListBO.setImg(uploadPath+workFlowListBO.getImg()+"?"+new Date().getTime());
            }
        }
        commonPageListPO.setCount(count);
        commonPageListPO.setDataList(workFlowListBOList);
        return commonPageListPO;
    }

    @RequestMapping(value = "/addWorkFlow.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "创建可视化工作流", position = 0)
    public Map<String,Long> addWorkFlow(@RequestParam(value = "projectId") @ApiParam(value = "项目id",required = true) Long projectId,
                            @RequestParam(value = "workFlowTemplateId",required = false) @ApiParam(value = "工作流模板id") Integer workFlowTemplateId,
                            @RequestParam(value = "workFlowName") @ApiParam(value = "工作流名称") String workFlowName){
        Map<String,Long> resultMap = new HashMap<>();
        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
        //检验该名称是否已经存在
        if(workFlowService.getWorkFlowListBOByWorkFlowName(projectId,workFlowName)>0){
            throw new WebException(MySystemCode.BIZ_CREATE_PROJECT);
        }
        if(workFlowName.length()>50){
            throw new WebException(MySystemCode.BIZ_PROJECTNAMETOOLONG);
        }
        workFlowListBO.setWorkFlowName(workFlowName);
        workFlowListBO.setProjectId(projectId);
        if(workFlowTemplateId!=null){
            workFlowListBO.setWorkFlowTemplateId(workFlowTemplateId);
            WorkFlowTemplateBO workFlowTemplateBO = workFlowTemplateService.getWorkFlowTemplateListById(workFlowTemplateId);
            workFlowListBO.setImg(workFlowTemplateBO.getImgUrl());
        }
        workFlowService.addWorkFlowListBO(workFlowListBO);
        Long workFlowId = workFlowListBO.getWorkFlowId();
        //添加完成后，需要判断如果有templateId 就把模板复制给工作流
        if (workFlowTemplateId != null) {
            workFlowService.addProjectDetailIdByTemplateId(workFlowTemplateId, workFlowId,projectId);//通过模板创建工作流
        }
        resultMap.put("workFlowId",workFlowId);
        return resultMap;
    }

    @RequestMapping(value = "/getWorkFlowListBo.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "可视化工作流-回显", position = 0)
    public WorkFlowListBO getWorkFlowListBo(@RequestParam(value = "workFlowId") @ApiParam(value = "工作流id") String workFlowId){
        WorkFlowListBO workFlowListBO = workFlowService.getWorkFlowListBOByWorkFlowId(Long.parseLong(workFlowId));
        return workFlowListBO;
    }

    @RequestMapping(value = "/updateWorkFlowListBo.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新可视化工作流", position = 0)
    public void updateWorkFlowListBo(@RequestParam(value = "workFlowId") @ApiParam(value = "工作流id") String workFlowId,
                                               @RequestParam(value = "workFlowName") @ApiParam(value = "工作流名称") String workFlowName){
        if(workFlowName.length()>50){
            throw new WebException(MySystemCode.BIZ_PROJECTNAMETOOLONG);
        }
        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
        workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
        workFlowListBO.setWorkFlowName(workFlowName);
        workFlowService.updateWorkFlowListStatus(workFlowListBO);
    }


    @RequestMapping(value = "/delWorkFlow.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除可视化工作流", position = 0)
    public void delWorkFlow(@RequestParam(value = "workFlowId") @ApiParam("工作流id") Long workFlowId){
        workFlowService.delWorkFlowByWorkFlowId(workFlowId);
    }

    @RequestMapping(value = "/delVisWorkFlowProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除可视化工作流项目", position = 0)
    public void delVisWorkFlowProject(@RequestParam(value = "projectId") @ApiParam("工作流项目id") Long projectId){
        WorkFlowListFilter workFlowListFilter = new WorkFlowListFilter();
        workFlowListFilter.setProjectId(projectId);
        List<WorkFlowListBO> workFlowListBOList = workFlowService.getWorkFlowListPOByFilter(workFlowListFilter);
        if(!Validate.isEmpty(workFlowListBOList)){
            for(WorkFlowListBO workFlowListBO : workFlowListBOList){
                workFlowService.delWorkFlowByWorkFlowId(workFlowListBO.getWorkFlowId());
            }
        }else{
            projectService.deleteProjectByProjectId(projectId);
        }
    }


    @RequestMapping(value = "/toWorkFlow.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "跳转到可视化工作台", position = 0)
    public ModelAndView getVisWorkFlowHtml(@RequestParam(value = "workFlowId", required = false) @ApiParam(value = "工作流id", required = false) String workFlowId,
                                           @RequestParam(value = "templateId", required = false) @ApiParam(value = "模板的id", required = false) String templateId,
                                           @RequestParam(value = "projectId", required = false) @ApiParam(value = "项目id", required = false) String projectId,
                                           HttpServletResponse res, HttpServletRequest req) {

        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (!Validate.isEmpty(workFlowId) && !Validate.isEmpty(projectId)) {
            resultMap.put("workFlowId", workFlowId);
            resultMap.put("projectId", projectId);
        } else if(!Validate.isEmpty(templateId)){
            resultMap.put("templateId",templateId);
        }else{
            throw new WebException(MySystemCode.BIZ_PARAMERRO_EXCEPTION_MESSAGE);
        }
        req.setAttribute("resultMap", resultMap);
        return new ModelAndView("newProject/visualization/nodeDesk");

    }

    @RequestMapping(value = "/saveOrUpdateVisTemplate.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存或者更新可视化工作流模板", position = 0)
    public Map<String, Object> saveOrUpdateVisWorkFlowTemplate(@RequestParam(value = "templateId", required = false) @ApiParam(value = "模板id", required = false) String templateId,
                                                               @RequestParam(value = "templateName", required = true) @ApiParam(value = "模板名字", required = false) String templateName,
                                                               HttpServletRequest req, HttpServletResponse res) {

        if(templateName.length()>50){
            throw new WebException(MySystemCode.BIZ_PROJECTNAMETOOLONG);
        }
        Map<String, Object> map = new HashMap<>();

        // 对时间进行格式转换
        String fmt = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        String dateStr = sdf.format(new Date());

        if (Validate.isEmpty(templateName)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION_MESSAGE);
        }


        List<String> names = workFlowTemplateService.getVisTemplateNameList();

        for (String name : names) {
            if (name.equals(templateName)){
                throw new WebException(MySystemCode.BIZ_CREATE_PROJECT);
            }
        }

        WorkFlowTemplateBO workFlowTemplateBO = new WorkFlowTemplateBO();
        workFlowTemplateBO.setName(templateName);

        if ("" != templateId && null != templateId) { // 如果传过来的id不是空值，那就是编辑模板列表，为更新
            workFlowTemplateBO.setId(Integer.parseInt(templateId));
            workFlowTemplateBO.setLastmodifyTime(dateStr);
            workFlowTemplateService.updateWorkTemplate(workFlowTemplateBO);
        } else {  // 传过来的id是空值，那就是添加模板列表，为增加
            workFlowTemplateBO.setStatus(0);

            workFlowTemplateBO.setLastmodifyTime(dateStr);
            workFlowTemplateBO.setCreateTime(dateStr);
            workFlowTemplateBO.setIsDelete(0);
            workFlowTemplateBO.setFlowType("workFlow");
            workFlowTemplateService.addWorkFlowTemplate(workFlowTemplateBO);
        }

        List<WorkFlowTemplateBO> workFlowTemplateBOList = workFlowTemplateService.getVisWorkFlowTemplateListByParam(null, templateName, 0, 10, null, null);
        String id = String.valueOf(workFlowTemplateBOList.get(0).getId());
        map.put("id", Integer.parseInt(id));
        return map;

    }

    /**
     * 根据项目id和项目对应的节点id来查询节点数据
     *
     * @param flowDetailId
     * @param projectId
     * @param lastIndexId
     * @param size
     * @return
     */
    @RequestMapping(value = "/getWorkFlowNodeData.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询可视化节点数据", position = 0)
    public DataImportListFromShowDataPo getVisWorkFlowNodeDate(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id", required = true) String projectId,
                                                               @RequestParam(value = "flowDetailId", required = true) @ApiParam(value = "工作流节点id", required = true) String flowDetailId,
                                                               @RequestParam(value = "page", required = false) @ApiParam(value = "页码", required = true) String page,
                                                               @RequestParam(value = "lastIndexId", required = false) @ApiParam(value = "当前页最后数据（开始或结束）的indexID", required = false) String lastIndexId,
                                                               @RequestParam(value = "flag", required = false) @ApiParam(value = "判断是上一页还是下一页的flag", required = false) String flag,
                                                               @RequestParam(value = "size", required = false) @ApiParam(value = "查询条数", required = true) String size,
                                                               @RequestParam(value = "preRun", required = false) @ApiParam(value = "试运行", required = true) String preRun) {


        String inputName ="";
        List<WorkFlowNodeParamBo> workFlowNodeParamBos = workFlowService.getWorkFlowNodeParamByFlowDetailId(Integer.parseInt(flowDetailId));
        for (WorkFlowNodeParamBo workFlowNodeParamBo : workFlowNodeParamBos){
            if ("节点名称".equals(workFlowNodeParamBo.getInputParamCnName())){
                inputName = workFlowNodeParamBo.getInputParamValue();
                break;
            }
        }

        String typeNo = workFlowTemplateService.getTypeNoByDetailId(Integer.parseInt(flowDetailId));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date());

        if ("dataOutput".equals(typeNo)){
            DataImportListFromShowDataPo dataImportListFromShowDataPo = new DataImportListFromShowDataPo();

            List<String> headList = new ArrayList<>();
            List<String> bodyList = new ArrayList<>();

            // 首先，封装一个标题list
            headList.add("任务ID");
            headList.add("名称");
            headList.add("URL");
            headList.add("创建时间");
            // 再将数据封装进list
            bodyList.add(flowDetailId);
            bodyList.add(inputName);
            bodyList.add("/export/getVisWorkFlowDataApi.json?detailId="+flowDetailId+"&projectID="+projectId+"&page=1&size=15\n");
            bodyList.add(dateStr);

            List<List> dataList = new ArrayList<>();

            dataList.add(bodyList);

            dataImportListFromShowDataPo.setDataList(dataList);
            dataImportListFromShowDataPo.setTitleList(headList);
            dataImportListFromShowDataPo.setCount("1");

            return dataImportListFromShowDataPo;

        }
        if(preRun==null||("").equals(preRun)){
            preRun=null;
        }
        String storageType = "";
        Object firstObject = null; // 用来存放第二次查询语料库数据的数据
        Object secondObject = null; // 用来存放第二次查询语料库数据的数据
        Integer indexId = null; // 存放语料库数据的第一个indexId

        /*int detailId = Integer.parseInt(flowDetailId);
        int status = workFlowTemplateService.getStatusByDetailId(detailId);
        if (status != 2){
            throw new WebException(MySystemCode.BIZ_PROJECT_NOT_COMPLETE_EXCEPTION_MESSAGE);
        }*/

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
        if(preRun==null){
            flag="null";
        }else{
            flag="no-null";
        }
        DataImportListFromShowDataPo dataImportListFromShowDataPo = new  DataImportListFromShowDataPo();

        // 创建一个存放中文标题的list集合
        List<String> keyList = new ArrayList<String>();
        // 创建一个存放英文标题的list集合
        List<String> valueList = new ArrayList<String>();

        // 创建一个存放file_type的集合
        List<String> fileList = new ArrayList<>();

        List<Map<String, String>> mapList = new ArrayList<>();

        // 创建一个最终存放含有该标题的数据集合
        List<List> lastDateList = new ArrayList<>();

        //获得输出数据表的数据
        List<VisWorkFlowBO> visWorkFlowBOList = visWorkFlowService.getVisWorkFlowList(Integer.parseInt(flowDetailId));
        // 循环该数据获得数据里面对应的表名

        for (int i = 0; i < visWorkFlowBOList.size(); i++) {

            if (visWorkFlowBOList.get(i).getFiledEnName().equals("indexId")) {
                continue;
            }

            storageType = visWorkFlowBOList.get(i).getStorageTypeTable();

            keyList.add(visWorkFlowBOList.get(i).getFiledCnName());//循环将中文标题存入keylist

            Map<String, String> map = new HashMap<>();
            map.put("value", visWorkFlowBOList.get(i).getFiledEnName());

            map.put("fileType", visWorkFlowBOList.get(i).getFiledType());
            map.put("indexId", "indexId");
            mapList.add(map);
            //valueList.add(visWorkFlowBOList.get(i).getFiledEnName());// 循环将英文标题存入valuelist
            //fileList.add(visWorkFlowBOList.get(i).getFiledType());// 循环将fieldType存入fileList

        }

        keyList.add("indexId");
        // 因为我们需要分页每条数据的indexId为分页做准备，就需要在valueList里面加一个indexId方便取出
        //valueList.add("indexId");

        //首次组装语料库查询条件（首先查一条得到该节点下所有的数据还有得到查询的总数据还要的到第一条数据的indexId）
//        Map<String, String> firstPostData = new HashMap<>();
//        //firstPostData.put("dataType", storageType);
//        firstPostData.put("filterJSON", "{\"projectID\":" + projectId + "}");
//        // "{\"projectID\":" + projectId +  "}"
//        firstPostData.put("startRow", "0");
//        firstPostData.put("flag", "gt");
//        firstPostData.put("rows", "1");
//        firstPostData.put("ordBy", "asc");
//        //开始查询
//        if (storageType.equals("article") || storageType.equals("sentence") || storageType.equals("section")) {
//            // 根据传入的flow_detail_id查询出来来的类型的数据 和project_id 得到语料库的数据
//            System.out.print("1");
//            firstPostData.put("resultType", storageType);
//            firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getSemanticAnalysisDataList.json", "post", firstPostData);
//        } else {
//            // 根据传入的flow_detail_id查询出来来的类型的数据 和project_id 得到语料库的数据
//            firstPostData.put("dataType", storageType);
//            firstObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getDataInSearcher.json", "post", firstPostData);
//        }
        Object total = null; // 获得查询数据的总条数
//        if (null != firstObject) {
//            net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) firstObject;
//            if (jsonObject.size() <= 0) {
//                return dataImportListFromShowDataPo;
//            }
//            //获取total
//            total = jsonObject.get("total");
//            // 下面是进过一系列的转换得到indexId
//            JSONArray jsonArray = jsonObject.getJSONArray("data");
//            if (jsonArray.size() < 1) {
//                return dataImportListFromShowDataPo;
//            }
//            indexId = Integer.parseInt(jsonArray.getJSONObject(0).get("indexId").toString());
//
//            dataImportListFromShowDataPo.setCount(total.toString());
//        }
//
//
//        // 再次组装预料库查询条件之前先给page进行一系列的处理
        int sizeInt = (null != size) ? Integer.parseInt(size) : 10;
//
//        /*if (page != null && !"".equals(page)){
//            lastIndexId = String.valueOf(indexId-1 + sizeInt*(Integer.parseInt(page)-1)) ;
//        }*/
//        //String realLastIndexId = "";
//        if (indexId == null) {
//            return dataImportListFromShowDataPo;
//        }
//        if ("gt".equals(flag) || flag == "gt") {
//            lastIndexId = String.valueOf(indexId - 1 + sizeInt * (Integer.parseInt(page) - 1));
//        } else if ("lt".equals(flag) || flag == "lt") {
//            lastIndexId = String.valueOf(indexId + sizeInt * (Integer.parseInt(page)));
//        }

        //再次组装语料库查询条件（首先查一条得到该节点下所有的数据）
        Map<String, String> secondPostData = new HashMap<>();
        if(preRun!=null &&("2").equals(page)&&("dataImport").equals(typeNo)){
            //secondPostData.put("dataType", storageType);
            LoggerUtil.debugTrace("preRun:="+preRun+"flag:="+flag);
            secondPostData.put("filterJSON", "{\"projectID\":" + projectId + ",\"detailId\":\"" + flowDetailId +"\"}");
            //"{\"projectID\":" + projectId + ",\"detailId\":\"" + flowDetailId + "\"}"
            secondPostData.put("startRow",page);
            secondPostData.put("flag",flag);
            secondPostData.put("rows","6");
            secondPostData.put("field","preRun");
            secondPostData.put("ordBy",ordBy);
        }else{
            //secondPostData.put("dataType", storageType);
            LoggerUtil.debugTrace("preRun:="+preRun+"flag:="+flag);
            secondPostData.put("filterJSON", "{\"projectID\":" + projectId + ",\"detailId\":\"" + flowDetailId +"\"}");
            //"{\"projectID\":" + projectId + ",\"detailId\":\"" + flowDetailId + "\"}"
            secondPostData.put("startRow",page);
            secondPostData.put("flag",flag);
            secondPostData.put("rows",String.valueOf(sizeInt));
            secondPostData.put("field","preRun");
            secondPostData.put("ordBy",ordBy);
        }

        if (storageType.equals("article") || storageType.equals("sentence") || storageType.equals("section")) {
            // 根据传入的flow_detail_id查询出来来的类型的数据 和project_id 得到语料库的数据
            secondPostData.put("resultType", storageType);
            secondObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getSemanticAnalysisDataList.json", "post", secondPostData);
        } else {
            // 根据传入的flow_detail_id查询出来来的类型的数据 和project_id 得到语料库的数据
            secondPostData.put("dataType", storageType);
            secondObject = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/getDataInSearcher.json", "post", secondPostData);
        }

        if (null != secondObject) {
            net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject) secondObject;
            if (jsonObject.size() <= 0) {
                return dataImportListFromShowDataPo;
            }
            if("dataImport".equals(typeNo)&&preRun!=null){
                total=20;
            }else{
                total=jsonObject.getString("total");
            }
            dataImportListFromShowDataPo.setCount(total.toString());
//            System.out.print("123");//消除重复语句
            //获取total
            /*total = jsonObject.get("total");*/
            /*dataImportListFromShowDataPo.setCount(total.toString());*/

            JSONArray semanticAnalysisDataArray = jsonObject.getJSONArray("data");
            if (null != semanticAnalysisDataArray && semanticAnalysisDataArray.size() > 0) {

                //开始遍历所有的数据记录,判断是倒序还是顺序。如果是正序，则从semanticAnalysisDataArray的第一个开始添加
                //如果是倒序，则从则从semanticAnalysisDataArray的最后一个开始添加
                if (ordBy.equals("asc")) {
                    for (int i = 0; i <= semanticAnalysisDataArray.size() - 1; i++) {
                        List<String> subList = new ArrayList<>();
                        //转换为json
                        net.sf.json.JSONObject dataJsonObject = semanticAnalysisDataArray.getJSONObject(i);
                        //取出每一个key对应的值,存入subList中
                        String indexID = "";
                        // String indexID = dataJsonObject.get("indexId").toString();
                        for (Map<String, String> map1 : mapList) {
                            Object key = map1.get("value");
                            String field = map1.get("fileType").toString();

                            if (map1.get("value") == "indexId") {

                            }
                            String indexId1 = map1.get("indexId");
                            indexID = dataJsonObject.get(indexId1).toString();

                            Object valObj = dataJsonObject.get(key);
                            String value = null != valObj ? valObj.toString() : "";
                            //判断该key是否是时间类型
                            if (field.equalsIgnoreCase("datetime") && !value.equals("")) {
                                net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(value);
                                Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                value = format.format(date);
                            }
                            subList.add(value);

                        }
                        subList.add(indexID);
                        lastDateList.add(subList);
                    }
                    dataImportListFromShowDataPo.setDataList(lastDateList);
                    dataImportListFromShowDataPo.setTitleList(keyList);
                } else {
                    for (int i = semanticAnalysisDataArray.size() - 1; i >= 0; i--) {
                        List<String> subList = new ArrayList<>();
                        //转换为json
                        net.sf.json.JSONObject dataJsonObject = semanticAnalysisDataArray.getJSONObject(i);
                        //取出每一个key对应的值,存入subList中
                        String indexID = "";
                        //取出每一个key对应的值,存入subList中
                        for (Map<String, String> map1 : mapList) {
                            Object key = map1.get("value");
                            String field = map1.get("fileType").toString();

                            if (map1.get("value") == "indexId") {

                            }
                            String indexId1 = map1.get("indexId");
                            indexID = dataJsonObject.get(indexId1).toString();

                            Object valObj = dataJsonObject.get(key);
                            String value = null != valObj ? valObj.toString() : "";
                            //判断该key是否是时间类型
                            if (field.equalsIgnoreCase("datetime") && !value.equals("")) {
                                net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(value);
                                Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                value = format.format(date);
                            }
                            subList.add(value);

                        }
                        subList.add(indexID);
                        lastDateList.add(subList);
                    }
                    dataImportListFromShowDataPo.setDataList(lastDateList);
                    dataImportListFromShowDataPo.setTitleList(keyList);
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

    @RequestMapping(value = "/getWorkFlowNodeList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询可视化工作流节点参数信息", position = 0)
    public Map<String, List<Map<String, Object>>> getWorkFlowNodeList() {
        Map<String, List<Map<String, Object>>> map = workFlowService.getWorkFlowNodeList();
        return map;
    }

    @RequestMapping(value = "/getProjectWorkFlowNodeInfo.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询工作流节点数据信息——回显数据", position = 0)
    public Map<String, Object> getProjectWorkFlowNodeInfo(@RequestParam(value = "workFlowId", required = false) @ApiParam(value = "工作流id") String workFlowId,
                                                          @RequestParam(value = "templateId", required = false) @ApiParam(value = "模板id") String templateId,
                                                          @RequestParam(value="projectId",required = false) @ApiParam(value = "项目id") String projectId) {
        Map<String, Object> resultMap = new HashMap<>();
        //查询work_flow_input_param
        List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowService.getWorkFlowInputParamBoList();
        if (Validate.isEmpty(workFlowInputParamBoList)) {
            throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
        }
        //聚合workFlowInputParamBoList
        Map<Integer, WorkFlowInputParamBo> workFlowInputParamBoMap = new HashMap<>();
        for (WorkFlowInputParamBo workFlowInputParamBo : workFlowInputParamBoList) {
            workFlowInputParamBoMap.put(workFlowInputParamBo.getId(), workFlowInputParamBo);
        }

        //将styleBOList存入缓存
        String styleBOListStr = redisClient.get(RedisKey.styleBOList_suffix.name());
        List<StyleBO> styleBOList;
        if(!Validate.isEmpty(styleBOListStr)){
            styleBOList = com.alibaba.fastjson.JSONArray.parseArray(styleBOListStr,StyleBO.class);
        }else{
            //查询style表
            styleBOList = workFlowService.getStyleBOList();
            if(Validate.isEmpty(styleBOList)){
                throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
            }
            redisClient.set(RedisKey.styleBOList_suffix.name(),JSON.toJSONString(styleBOList));
            redisClient.expire(RedisKey.styleBOList_suffix.name(),7200);//设置过期时间为2小时
        }
        Map<Integer, StyleBO> styleBOMap = new HashMap<>();
        //聚合styleBOList
        for (StyleBO styleBO : styleBOList) {
            styleBOMap.put(styleBO.getId(), styleBO);
        }
        //将jobTypeInfoList存入缓存
        String jobTypeInfoListStr = redisClient.get(RedisKey.jobTypeInfoList_suffix.name());
        List<JobTypeInfo> jobTypeInfoList;
        if(!Validate.isEmpty(jobTypeInfoListStr)){
            jobTypeInfoList = com.alibaba.fastjson.JSONArray.parseArray(jobTypeInfoListStr,JobTypeInfo.class);
        }else {
            //查询job_type_info
            jobTypeInfoList = workFlowService.getJobTypeInfo();
            if(Validate.isEmpty(jobTypeInfoList)){
                throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
            }
            redisClient.set(RedisKey.jobTypeInfoList_suffix.name(),JSON.toJSONString(jobTypeInfoList));
            redisClient.expire(RedisKey.jobTypeInfoList_suffix.name(),7200);//设置过期时间为2小时
        }
        //聚合jobTypeInfoList
        Map<String, JobTypeInfo> jobTypeInfoMap = new HashMap<>();
        for (JobTypeInfo jobTypeInfo : jobTypeInfoList) {
            jobTypeInfoMap.put(jobTypeInfo.getTypeNo(), jobTypeInfo);
        }
        if (!Validate.isEmpty(workFlowId) && !Validate.isEmpty(projectId)) {
            resultMap.put("workFlowId", workFlowId);
            //根据工作流id查询工作流的所有节点
            List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailByWorkFlowIdAndProjectId(Long.parseLong(workFlowId),Long.parseLong(projectId));
            if (Validate.isEmpty(workFlowDetailList)) {
                throw new WebException(MySystemCode.SYS_CONTROLLER_EXCEPTION);
            }
            List<Map<String, Object>> nodeList = new ArrayList<>();
            for (WorkFlowDetail workFlowDetail : workFlowDetailList) {
                //首先根据当前节点查询是否有上一个节点(目前业务上一个节点只能有一个)后面备用
                String preFlowDetails = workFlowDetail.getPrevFlowDetailIds();
                WorkFlowDetail preWorkFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(preFlowDetails));
                Map<String, Object> nodeMap = new HashMap<>();
                nodeMap.put("nodeInfo", JSONObject.parse(workFlowDetail.getNodeInfo()));
                JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(workFlowDetail.getTypeNo());
                Map<String, Object> dataMap = new HashMap<>();
                if(jobTypeInfo.getImgUrl()!=null){
                    String[] imgArray = jobTypeInfo.getImgUrl().split(",");
                    String imgStr = "";
                    for(int i = 0;i<imgArray.length;i++){
                        imgStr += uploadPath+imgArray[i]+",";
                    }
                    imgStr = imgStr.substring(0,imgStr.length()-1);
                    dataMap.put("imgUrl",imgStr);
                }
                dataMap.put("queryUrl", jobTypeInfo.getQueryUrl());
                dataMap.put("inputNum", jobTypeInfo.getInputNum());
                dataMap.put("jobTypeCategoryId", jobTypeInfo.getJobTypeCategoryId());
                dataMap.put("typeNo", jobTypeInfo.getTypeNo());
                if (!Validate.isEmpty(workFlowDetail.getQuartzTime())) {
                    dataMap.put("typeClassify", 2);
                } else {
                    dataMap.put("typeClassify", 1);
                }
                dataMap.put("jobClassify", jobTypeInfo.getJobClassify());
                dataMap.put("tip", jobTypeInfo.getTip());
                dataMap.put("processType", jobTypeInfo.getProgressUrl());
                dataMap.put("flowDetailId", workFlowDetail.getFlowDetailId());
                dataMap.put("jobStatus", workFlowDetail.getJobStatus());
                dataMap.put("isSave", workFlowDetail.getSave());
                //根据workFlowDetailId查询单个节点的所有输入参数值。
                List<WorkFlowNodeParamBo> workFlowNodeParamBoList
                        = workFlowService.getWorkFlowNodeParamByFlowDetailId(workFlowDetail.getFlowDetailId());
                List<Map<String, Object>> paramArray = new ArrayList<>();
                //遍历每个输入参数的值
                String crawlType = "";
                for (WorkFlowNodeParamBo workFlowNodeParamBo : workFlowNodeParamBoList) {
                    int inputParamId = workFlowNodeParamBo.getInputParamId();
                    Integer styleId = workFlowInputParamBoMap.get(inputParamId).getStyleId();
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("inputParamId", inputParamId);
                    paramMap.put("paramId", workFlowNodeParamBo.getParamId());
                    paramMap.put("nextParamId", workFlowInputParamBoMap.get(inputParamId).getNextParamId());
                    paramMap.put("requestUrl", workFlowInputParamBoMap.get(inputParamId).getRequestUrl());
                    paramMap.put("paramEnName", workFlowInputParamBoMap.get(inputParamId).getParamEnName());
                    paramMap.put("paramCnName", workFlowInputParamBoMap.get(inputParamId).getParamCnName());
                    paramMap.put("restrictions", workFlowInputParamBoMap.get(inputParamId).getRestrictions());
                    paramMap.put("preParamId", workFlowInputParamBoMap.get(inputParamId).getPreParamId());
                    paramMap.put("filedMapping", JSONObject.parse(workFlowInputParamBoMap.get(inputParamId).getFiledMapping()));
                    if (styleId != null) {
                        paramMap.put("styleCode", styleBOMap.get(styleId).getStyleCode());
                    } else {
                        paramMap.put("styleCode", null);
                    }
                    //将值类型为jsonArray的转换成JsonArray
                    if (!Validate.isEmpty(workFlowNodeParamBo.getInputParamType()) &&
                            workFlowNodeParamBo.getInputParamType().equals("jsonArray")) {
                        paramMap.put("value", JSONObject.parseArray(workFlowNodeParamBo.getInputParamValue()));
                    } else {
                        if (!Validate.isEmpty(workFlowNodeParamBo.getInputParamValue()) &&
                                (workFlowNodeParamBo.getInputParamValue().equals(com.transing.workflow.constant.Constants.DATA_TYPE_ARTICLE) ||
                                        workFlowNodeParamBo.getInputParamValue().equals(com.transing.workflow.constant.Constants.DATA_TYPE_SECTION) ||
                                        workFlowNodeParamBo.getInputParamValue().equals(com.transing.workflow.constant.Constants.DATA_TYPE_SENTENCE))&&
                                preWorkFlowDetail !=null) {
                            switch (preWorkFlowDetail.getTypeNo()) {
                                case com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT:
                                    paramMap.put("value", "semanticAnalysisObject");
                                    break;
                                case com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION:
                                    paramMap.put("value", "wordSegmentation");
                                    break;
                                case com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING:
                                    paramMap.put("value", "themeAnalysisSetting");
                                    break;
                                case com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION:
                                    paramMap.put("value", "topicAnalysisDefinition");
                                    break;
                                default:
                                    paramMap.put("value", workFlowNodeParamBo.getInputParamValue());
                            }
                        } else {
                            if(inputParamId == 8){
                                crawlType = workFlowNodeParamBo.getInputParamValue();
                            }
                            paramMap.put("value", workFlowNodeParamBo.getInputParamValue());
                        }
                    }
                    paramMap.put("required", workFlowInputParamBoMap.get(inputParamId).getIsRequired());
                    //该判断主要是将bdi项目的数据源从redis中取出。而不再从数据库中取出，根据不同的抓取类型来选择不同的缓存取出
                    if (workFlowNodeParamBo.getInputParamId() == 6) {
                        if(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                            String config = redisClient.get(RedisKey.dataSourceConfig.name());
                            if (!Validate.isEmpty(config)) {
                                paramMap.put("config", config);
                            } else {
                                paramMap.put("config", null);
                            }
                        }else if(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                            String config = redisClient.get(RedisKey.mDataSourceConfig.name());
                            if (!Validate.isEmpty(config)) {
                                paramMap.put("config", config);
                            } else {
                                paramMap.put("config", null);
                            }
                        }
                    } else if(workFlowNodeParamBo.getInputParamId() == 2){
                        String config = redisClient.get(RedisKey.dataSourceConfig.name());
                        if (!Validate.isEmpty(config)) {
                            paramMap.put("config", config);
                        } else {
                            paramMap.put("config", null);
                        }
                    }else{
                        paramMap.put("config", workFlowNodeParamBo.getConfig());
                    }
                    paramArray.add(paramMap);
                }
                dataMap.put("paramArray", paramArray);
                nodeMap.put("data", dataMap);
                nodeList.add(nodeMap);
            }
            resultMap.put("nodeList", nodeList);
        } else if (!Validate.isEmpty(templateId)) {
            if (!Validate.isEmpty(workFlowId)) {
                resultMap.put("workFlowId", workFlowId);
            }
            resultMap.put("templateId", templateId);
            //根据模板id查询模板的所有节点
            List<WorkFlowNodeBO> workFlowNodeBOList = workFlowService.getWorkFlowNodeByTemplateId(Integer.parseInt(templateId));
            if (Validate.isEmpty(workFlowNodeBOList)) {
                throw new WebException(MySystemCode.ACTION_EXCEPTION);
            }

            List<Map<String, Object>> nodeList = new ArrayList<>();
            for (WorkFlowNodeBO workFlowNodeBO : workFlowNodeBOList) {
                //第一步首先根据当前节点查询是否有上一个节点(目前业务上一个节点只能有一个)
                String preFlowDetails = workFlowNodeBO.getPreFlowIdIds();
                WorkFlowNodeBO preWorkFlowNodeBo = workFlowService.getWorkFlowNodeByFlowId(Long.parseLong(preFlowDetails));
                Map<String, Object> nodeMap = new HashMap<>();
                nodeMap.put("nodeInfo", JSONObject.parse(workFlowNodeBO.getNodeInfo()));
                JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(workFlowNodeBO.getTypeNo());
                Map<String, Object> dataMap = new HashMap<>();
                if(jobTypeInfo.getImgUrl()!=null){
                    String[] imgArray = jobTypeInfo.getImgUrl().split(",");
                    String imgStr = "";
                    for(int i = 0;i<imgArray.length;i++){
                        imgStr += uploadPath+imgArray[i]+",";
                    }
                    imgStr = imgStr.substring(0,imgStr.length()-1);
                    dataMap.put("imgUrl",imgStr);
                }
                dataMap.put("queryUrl", jobTypeInfo.getQueryUrl());
                dataMap.put("inputNum", jobTypeInfo.getInputNum());
                dataMap.put("jobTypeCategoryId", jobTypeInfo.getJobTypeCategoryId());
                dataMap.put("typeNo", jobTypeInfo.getTypeNo());
                dataMap.put("typeClassify", jobTypeInfo.getTypeClassify());
                dataMap.put("jobClassify", jobTypeInfo.getJobClassify());
                dataMap.put("tip", jobTypeInfo.getTip());
                dataMap.put("processType", jobTypeInfo.getProgressUrl());
                dataMap.put("flowDetailId", workFlowNodeBO.getFlowId());
                dataMap.put("jobStatus", workFlowNodeBO.getJobStatus());
                dataMap.put("isSave", workFlowNodeBO.getSave());
                //根据workFlowNodeBO查询单个节点的所有输入参数值。
                List<WorkFlowTemplateNodeParamBo> workFlowTemplateNodeParamBoList
                        = workFlowService.getTemplateNodeParamByTemplateFlowId(workFlowNodeBO.getFlowId());
                List<Map<String, Object>> paramArray = new ArrayList<>();
                String crawlType = "";
                for (WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo : workFlowTemplateNodeParamBoList) {
                    int inputParamId = workFlowTemplateNodeParamBo.getInputParamId();
                    Integer styleId = workFlowInputParamBoMap.get(inputParamId).getStyleId();
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("inputParamId", inputParamId);
                    paramMap.put("paramId", workFlowTemplateNodeParamBo.getParamId());
                    paramMap.put("nextParamId", workFlowInputParamBoMap.get(inputParamId).getNextParamId());
                    paramMap.put("requestUrl", workFlowInputParamBoMap.get(inputParamId).getRequestUrl());
                    paramMap.put("paramEnName", workFlowInputParamBoMap.get(inputParamId).getParamEnName());
                    paramMap.put("paramCnName", workFlowInputParamBoMap.get(inputParamId).getParamCnName());
                    paramMap.put("restrictions", workFlowInputParamBoMap.get(inputParamId).getRestrictions());
                    paramMap.put("preParamId", workFlowInputParamBoMap.get(inputParamId).getPreParamId());
                    paramMap.put("filedMapping", JSONObject.parse(workFlowInputParamBoMap.get(inputParamId).getFiledMapping()));
                    if (styleId != null) {
                        paramMap.put("styleCode", styleBOMap.get(styleId).getStyleCode());
                    } else {
                        paramMap.put("styleCode", null);
                    }
                    //将值类型为jsonArray的转换成JsonArray
                    if (!Validate.isEmpty(workFlowTemplateNodeParamBo.getInputParamType()) &&
                            workFlowTemplateNodeParamBo.getInputParamType().equals("jsonArray")) {
                        paramMap.put("value", JSONObject.parseArray(workFlowTemplateNodeParamBo.getInputParamValue()));
                    } else {
                        if (!Validate.isEmpty(workFlowTemplateNodeParamBo.getInputParamValue()) &&
                                (workFlowTemplateNodeParamBo.getInputParamValue().equals(com.transing.workflow.constant.Constants.DATA_TYPE_ARTICLE) ||
                                        workFlowTemplateNodeParamBo.getInputParamValue().equals(com.transing.workflow.constant.Constants.DATA_TYPE_SECTION) ||
                                        workFlowTemplateNodeParamBo.getInputParamValue().equals(com.transing.workflow.constant.Constants.DATA_TYPE_SENTENCE)) &&
                                preWorkFlowNodeBo !=null) {
                            switch (preWorkFlowNodeBo.getTypeNo()) {
                                case com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT:
                                    paramMap.put("value", "semanticAnalysisObject");
                                    break;
                                case com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION:
                                    paramMap.put("value", "wordSegmentation");
                                    break;
                                case com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING:
                                    paramMap.put("value", "themeAnalysisSetting");
                                    break;
                                case com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION:
                                    paramMap.put("value", "topicAnalysisDefinition");
                                    break;
                                default:
                                    if(inputParamId == 8){
                                        crawlType = workFlowTemplateNodeParamBo.getInputParamValue();
                                    }
                                    paramMap.put("value", workFlowTemplateNodeParamBo.getInputParamValue());
                            }
                        } else {
                            paramMap.put("value", workFlowTemplateNodeParamBo.getInputParamValue());
                        }
                    }
                    paramMap.put("required", workFlowInputParamBoMap.get(inputParamId).getIsRequired());
                    //该判断主要是将bdi项目的数据源从redis中取出。而不再从数据库中取出
                    //该判断主要是将bdi项目的数据源从redis中取出。而不再从数据库中取出，根据不同的抓取类型来选择不同的缓存取出
                    if (workFlowTemplateNodeParamBo.getInputParamId() == 6) {
                        if(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                            String config = redisClient.get(RedisKey.dataSourceConfig.name());
                            if (!Validate.isEmpty(config)) {
                                paramMap.put("config", config);
                            } else {
                                paramMap.put("config", null);
                            }
                        }else if(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                            String config = redisClient.get(RedisKey.mDataSourceConfig.name());
                            if (!Validate.isEmpty(config)) {
                                paramMap.put("config", config);
                            } else {
                                paramMap.put("config", null);
                            }
                        }
                    } else if(workFlowTemplateNodeParamBo.getInputParamId() == 2){
                        String config = redisClient.get(RedisKey.dataSourceConfig.name());
                        if (!Validate.isEmpty(config)) {
                            paramMap.put("config", config);
                        } else {
                            paramMap.put("config", null);
                        }
                    }else{
                        paramMap.put("config", workFlowTemplateNodeParamBo.getConfig());
                    }
                    paramArray.add(paramMap);
                }
                dataMap.put("paramArray", paramArray);
                nodeMap.put("data", dataMap);
                nodeList.add(nodeMap);
            }
            resultMap.put("nodeList", nodeList);
        } else {
            throw new WebException(MySystemCode.SYS_REQUEST_EXCEPTION);
        }
        return resultMap;
    }

    @RequestMapping(value = "/savetWorkFlowProjectNodeInfo.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存可视化工作流项目节点配置信息", position = 0)
    public List<Map<String, Object>> savetWorkFlowProjectNodeInfo(@RequestParam(value = "body") String body) {
        List<Map<String, Object>> reslutMap = workFlowService.addVisWorkFlowNodeParam(body);
        return reslutMap;
    }

    @RequestMapping(value = "/startVisWorkFlow.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "启动可视化工作流", notes = "", position = 0)
    public Map<String,Object> startVisWorkFlow(@RequestParam(value = "workFlowId", required = true) @ApiParam(value = "工作流id") String workFlowId,
                                 HttpServletRequest req, HttpServletResponse res){
        Map<String,Object> result = new HashMap<>();
        if (workFlowId == null || "".equals(workFlowId) || !workFlowId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long workFlowIdInt = Long.parseLong(workFlowId);
        workFlowService.startVisWorkFlow(workFlowIdInt,null,null);
        return result;
    }

    @RequestMapping(value = "/startVisWorkFlowProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "启动可视化工作流项目", notes = "", position = 0)
    public Map<String,Object> startVisWorkFlowProject(@RequestParam(value = "projectId", required = true) @ApiParam(value = "可视化工作流项目id") String projectId,
                                                      @RequestParam(value = "flag", required = false) @ApiParam(value = "") boolean flag){
        Map<String,Object> result = new HashMap<>();
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);
        List<String> condition = new ArrayList<>();
        condition.add("2");
        condition.add("3");
        condition.add("4");
        if (flag) {
            //先查询出该项目下所有未启动、已完成、已停止的工作流
            List<WorkFlowListBO> workFlowListBOList = workFlowService.getWorkFlowListByIncludeStatus(condition,projectIdInt);
            if(!Validate.isEmpty(workFlowListBOList)){
                //在修改该工作流项目的状态
                ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
                projectStatusFilter.setId(projectIdInt);
                projectStatusFilter.setStatus(3);
                projectService.updateProjectStatus(projectStatusFilter);
                result.put("succeed","成功启动"+workFlowListBOList.size()+"个的工作流");
                new Thread(new VisWorkFlowProjectStart(workFlowListBOList)).start();
            }else {
                throw new WebException(MySystemCode.START_VISPROJECT_ERROR);
            }
        }else{
            condition.add("5");
            List<WorkFlowListBO> workFlowListBOList = workFlowService.getWorkFlowListByIncludeStatus(condition,projectIdInt);
            if(!Validate.isEmpty(workFlowListBOList)){
                boolean isFlag = true;
                for(WorkFlowListBO workFlowListBO : workFlowListBOList){
                    if(workFlowListBO.getStatus()==5){
                        isFlag = false;
                        result.put("message","有配置中的任务，是否需要继续启动");
                        break;
                    }
                }
                if(isFlag){
                    ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
                    projectStatusFilter.setId(projectIdInt);
                    projectStatusFilter.setStatus(3);
                    projectService.updateProjectStatus(projectStatusFilter);
                    result.put("succeed","成功启动"+workFlowListBOList.size()+"个的工作流");
                    new Thread(new VisWorkFlowProjectStart(workFlowListBOList)).start();
                }
            }else{
                throw new WebException(MySystemCode.START_VISPROJECT_ERROR);
            }
        }
        return result;
    }

    @RequestMapping(value = "/stopVisWorkFlow.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "停止可视化工作流", notes = "", position = 0)
    public StopProjectPo stopVisWorkFlow(@RequestParam(value = "workFlowId", required = true) @ApiParam(value = "工作流id") String workFlowId,
                                 HttpServletRequest req, HttpServletResponse res){
        StopProjectPo stopProjectPo = new StopProjectPo();
        if (workFlowId == null || "".equals(workFlowId) || !workFlowId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        Long workFlowIdInt = Long.parseLong(workFlowId);
        workFlowService.stopWorkFlowListByWorkFlowId(workFlowIdInt);
        //根据workFlowId查询出projectId
        WorkFlowListBO workFlowListBO=workFlowService.getWorkFlowListBOByWorkFlowId(workFlowIdInt);
        Long projectId=workFlowListBO.getProjectId();
        //在根据projectId查询出所有的工作流
        List<String> list=new ArrayList<>();
        list.add("9");
        list.add("1");
        List<WorkFlowListBO> workFlowListBOList=workFlowService.getWorkFlowListByIncludeStatus(list,projectId);
        int count=0;
        int count1=0;
        for (WorkFlowListBO workFlowListBO1:workFlowListBOList) {
                if(workFlowListBO1.getStatus()==9){
                    count++;
                }else {
                    count1++;
                }
        }
        if(count>0){
            ProjectStatusFilter projectStatusFilter=new ProjectStatusFilter();
            projectStatusFilter.setId(projectId);
            projectStatusFilter.setStatus(9);
            projectService.updateProjectStatus(projectStatusFilter);
            return stopProjectPo;
        }else if(count1>0){
            ProjectStatusFilter projectStatusFilter=new ProjectStatusFilter();
            projectStatusFilter.setId(projectId);
            projectStatusFilter.setStatus(3);
            projectService.updateProjectStatus(projectStatusFilter);
            return stopProjectPo;
        }else if(count1==0){
            ProjectStatusFilter projectStatusFilter=new ProjectStatusFilter();
            projectStatusFilter.setId(projectId);
            projectStatusFilter.setStatus(4);
            projectService.updateProjectStatus(projectStatusFilter);
            return stopProjectPo;
        }
        return stopProjectPo;
    }

    @RequestMapping(value = "/stopVisWorkFlowProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "停止可视化工作流项目", notes = "", position = 0)
    public StopProjectPo stopVisWorkFlowProject(@RequestParam(value = "projectId", required = true) @ApiParam(value = "可视化工作流项目id") String projectId,
                                HttpServletRequest req, HttpServletResponse res){
        StopProjectPo stopProjectPo = new StopProjectPo();
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        Long projectIdInt = Long.parseLong(projectId);
        //先停止项目
        ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
        projectStatusFilter.setId(projectIdInt);
        projectStatusFilter.setStatus(4);
        projectService.updateProjectStatus(projectStatusFilter);
        //在查询出该项目下所有正在进行中的工作流
        WorkFlowListFilter workFlowListFilter = new WorkFlowListFilter();
        workFlowListFilter.setStatus(1);
        workFlowListFilter.setProjectId(projectIdInt);
        List<WorkFlowListBO> workFlowListBOList = workFlowService.getWorkFlowListPOByFilter(workFlowListFilter);
        for(WorkFlowListBO workFlowListBO : workFlowListBOList){
            workFlowService.stopWorkFlowListByWorkFlowId(workFlowListBO.getWorkFlowId());
        }
        return stopProjectPo;
    }

    @RequestMapping(value = "/updateVisWorkFlowProject.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "编辑可视化工作流项目", position = 0)
    public ProjectOne updateVisWorkFlowProject(@RequestParam(value = "projectId") @ApiParam(value = "项目id") String projectId) {
        ProjectOne projectOne = projectService.getProjectInf(Long.parseLong(projectId));
        return projectOne;
    }

    @RequestMapping(value = "/savaCopyVisWorkFlowProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "另存可视化工作流", position = 0)
    public Map<String, Object> savaCopyVisWorkFlowProject(@RequestParam(value = "workFlowName") @ApiParam(value = "工作流名称") String workFlowName,
                                                          @RequestParam(value = "projectId") @ApiParam(value = "项目名称") String projectId,
                                                          @RequestParam(value = "body") @ApiParam(value = "节点参数") String body) {

        if(workFlowName.length()>50){
            throw new WebException(MySystemCode.BIZ_PROJECTNAMETOOLONG);
        }
        Map<String, Object> resultMap = new HashMap<>();
        Long projectIdInt = Long.parseLong(projectId);
        //校验输入的名称是否存在
        if(workFlowService.getWorkFlowListBOByWorkFlowName(projectIdInt,workFlowName)>0){
            throw new WebException(MySystemCode.BIZ_CREATE_PROJECT);
        }

        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
        workFlowListBO.setWorkFlowName(workFlowName);
        workFlowListBO.setProjectId(projectIdInt);
        workFlowService.addWorkFlowListBO(workFlowListBO);
        Long workFlowId = workFlowListBO.getWorkFlowId();

        //尝试调用
        JSONObject jsonObject = JSONObject.parseObject(body);
        jsonObject.put("workFlowId", workFlowId);
        //这里尝试需要将所有节点的flowdetailId全部设置为空，才能添加。
        com.alibaba.fastjson.JSONArray nodeList = jsonObject.getJSONArray("nodeList");
        for (Object node : nodeList) {
            JSONObject nodeJsonObject = (JSONObject) node;
            JSONObject jsonObject1 = nodeJsonObject.getJSONObject("data");
            com.alibaba.fastjson.JSONArray paramArray = jsonObject1.getJSONArray("paramArray");
            if (jsonObject1.containsKey("flowDetailId")) {
                jsonObject1.remove("flowDetailId");//因为是另存，所以每次都要新增，然后必须将原项目所有的flowDetailId全部清空。
                for (Object param : paramArray) {
                    JSONObject paramJsonObject = (JSONObject) param;
                    if (paramJsonObject.containsKey("paramId")) {//同理，需要将所有的paramId清空。
                        paramJsonObject.remove("paramId");
                    }
                }
            }
        }
        //这个时候在将body传到addVisWorkFlowNodeParam方法去。
        workFlowService.addVisWorkFlowNodeParam(jsonObject.toJSONString());
        resultMap.put("message", "另存成功");
        return resultMap;
    }

    @RequestMapping(value = "/toVisWorkFlowProjecExport.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "可视化工作流——导出任务列表页面", position = 0)
    public ModelAndView toVisWorkFlowProjecExport(@RequestParam(value = "projectId") @ApiParam(value = "项目id") String projectId,
                                                  HttpServletRequest req, HttpServletResponse resp) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("projectId", projectId);
        req.setAttribute("result", retMap);
        return new ModelAndView("newProject/visualization/visWorkFlowProjectExport");
    }

    @RequestMapping(value = "/getVisWorkFlowProjectExportList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "可视化工作流——查询导出任务列表", position = 0)
    public ProjectPo getVisWorkFlowProjectExportList(@RequestParam(value = "projectId") @ApiParam("项目id") String projectId,
                                                     @RequestParam(value = "page") @ApiParam("页码") String page,
                                                     @RequestParam(value = "size") @ApiParam("每页条数") String size,
                                                     @RequestParam(value = "fileName", required = false) @ApiParam("文件名") String fileName,
                                                     @RequestParam(value = "typeNo", required = false) @ApiParam("节点类型") String typeNo,
                                                     @RequestParam(value = "createTime", required = false) @ApiParam("创建时间") String createTime,
                                                     @RequestParam(value = "status", required = false) @ApiParam("状态") String status) {

        ProjectPo projectPo = new ProjectPo();
        List<Map<String, Object>> filterResult = new ArrayList<>();
        try {
            ProjectExportFilter projectExportFilter = new ProjectExportFilter();
            if (createTime != null && !createTime.equals("")) {
                projectExportFilter.setCreateTimeStart(createTime + " 00:00:00");
                projectExportFilter.setCreateTimeEnd(createTime + " 23:59:59");
            }
            if (status != null && !status.equals("")) {
                projectExportFilter.setStatus(status);
            }
            projectExportFilter.setProjectId(Integer.parseInt(projectId));
            projectExportFilter.setPage((Long.parseLong(page) - 1) * Long.parseLong(size));
            projectExportFilter.setSize(Long.parseLong(size));
            List<ProjectExportPo> result = projectExportService.getProjectExportListByProjectExportFilter(projectExportFilter);
            int resultCount = result.size();
            for (int i = resultCount - 1; i >= 0; i--) {
                JSONObject jsonObject = JSON.parseObject(result.get(i).getJsonParam());//取出每个ProjectExportBO的jsonParam字段并转换成json格式
                JSONObject jsonObject1 = jsonObject.getJSONObject("typeNo");
                //判断fileName是否为空并且是否等于jsonObject.getString("filename")
                if (fileName != null && !fileName.equals("") && jsonObject.getString("fileName").indexOf(fileName) == -1) {
                    result.remove(i);
                    continue;
                }
                if (typeNo != null && !jsonObject1.getString("value").equals(typeNo) && !typeNo.equals("")) {//同理
                    result.remove(i);
                    continue;
                }
                Map<String, Object> map = jsonObject;
                map.put("id", result.get(i).getId());
                map.put("createTime", result.get(i).getCreateTime());
                map.put("status", result.get(i).getStatus());
                map.put("errorMessage", result.get(i).getErrorMessage());
                map.put("progress", result.get(i).getProgress());
                filterResult.add(map);
            }
            List<Map<String, Object>> filterResult2 = new ArrayList<>();
            for (int i = filterResult.size() - 1; i >= 0; i--) {
                filterResult2.add(filterResult.get(i));
            }
            projectPo.setProjectList(filterResult2);
            int count = projectExportService.getProjectExportCount(Integer.parseInt(projectId));
            projectPo.setCount(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projectPo;
    }

    @RequestMapping(value = "/getExportDetailType.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "可视化工作流——查询导出任务节点类型", position = 0)
    public Set<Map<String, String>> getExportDetailType(@RequestParam(value = "workFlowId") @ApiParam("工作流id") String workFlowId) {
        Set<Map<String, String>> resultSet = new HashSet<>();
        //通过项目id查询该项目下所有节点类型
        List<WorkFlowDetail> workFlowDetailList
                = workFlowService.getWorkFlowDetailByWorkFlowId(Long.parseLong(workFlowId));
        //查询job_type_info表得到每一个节点的基本信息
        List<JobTypeInfo> jobTypeInfoList = workFlowService.getJobTypeInfo();
        Map<String, JobTypeInfo> jobTypeInfoMap = new HashMap<>();
        for (JobTypeInfo jobTypeInfo : jobTypeInfoList) {
            jobTypeInfoMap.put(jobTypeInfo.getTypeNo(), jobTypeInfo);
        }

        for (WorkFlowDetail workFlowDetail : workFlowDetailList) {
            Map<String, String> map = new HashMap<>();
            String typeName = jobTypeInfoMap.get(workFlowDetail.getTypeNo()).getTypeName();
            map.put("key", typeName);

            map.put("value", workFlowDetail.getTypeNo());
            resultSet.add(map);
        }
        return resultSet;
    }

    @RequestMapping(value = "/toCreateVisWorkFlowProjectExport.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "可视化工作流——创建导出任务页面", position = 0)
    public ModelAndView toCreateVisWorkFlowProjectExport(@RequestParam(value = "projectId") @ApiParam("项目id") String projectId,
                                                         HttpServletRequest req, HttpServletResponse resp) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("projectId", projectId);
        req.setAttribute("result", retMap);
        return new ModelAndView("newProject/visualization/createVisWorkFlowProjectExport");
    }

    @RequestMapping(value = "/getVisWorkFlowExportDetailTask.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "可视化工作流——查看导出节点任务", position = 0)
    public List<Map<String, Object>> getVisWorkFlowExportDetailTask(@RequestParam(value = "projectId") @ApiParam("项目id") String projectId,
                                                                    @RequestParam(value = "typeNo") @ApiParam("节点类型") String typeNo) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<WorkFlowNodeParamBo> list = workFlowService.getWrokFlowNodeParamListByMap(Long.parseLong(projectId), typeNo);
        if (!Validate.isEmpty(list)) {
            for (WorkFlowNodeParamBo workFlowNodeParamBo : list) {
                if (workFlowNodeParamBo.getInputParamCnName().equals("节点名称")) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("flowDetailId", workFlowNodeParamBo.getFlowDetailId());
                    map.put("taskName", workFlowNodeParamBo.getInputParamValue());
                    resultList.add(map);
                }
            }
        }

        return resultList;
    }
    @RequestMapping(value = "/getDownLoadCoding.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "推送节点-查询下载编码", position = 0)
    public List<ParamBO> getDownLoadCoding() {
        List<ParamBO> list = workFlowService.getDownloadCoding();
        return list;
    }

    @RequestMapping(value = "/getWorkFlowDetailOutputField.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "可视化工作流——查询节点选择值", position = 0)
    public List<VisWorkFlowBO> getWorkFlowDetailOutputField(@RequestParam(value = "flowDetailId") @ApiParam("流程节点id") String flowDetailId) {
        //根据flowDetailId查询work_flow_output_field表得到输出字段
        List<VisWorkFlowBO> list = visWorkFlowService.getVisWorkFlowList(Integer.parseInt(flowDetailId));
        return list;
    }

    @RequestMapping(value = "/addVisWorkFlowProjectExportTask.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "可视化工作流——保存可视化工作流导出任务", position = 0)
    public CommonResultCodePO addVisWorkFlowProjectExportTask(@RequestParam("body") @ApiParam("body") String body) {
        JSONObject jsonObjectParam = JSONObject.parseObject(body);
        ProjectExportBO projectExportBO = new ProjectExportBO();
        int workFlowId = Integer.parseInt(jsonObjectParam.getString("workFlowId"));//获取json中的项目id

        JSONObject typeNoJsonObject = jsonObjectParam.getJSONObject("typeNo");
        String typeNo = typeNoJsonObject.getString("value");
        if (typeNo.equals("wordSegmentation") ||
                typeNo.equals("topicAnalysisDefinition") ||
                typeNo.equals("themeAnalysisSetting")) {

            List<WorkFlowDetail> list = workFlowService.getWorkFlowDetailByWorkFlowId(Long.parseLong(jsonObjectParam.getString("workFlowId")));
            Map<Long, WorkFlowDetail> map = new HashMap<>();
            for (WorkFlowDetail workFlowDetail : list) {
                map.put(workFlowDetail.getFlowDetailId(), workFlowDetail);
            }

            Long flowDetailId = Long.parseLong(jsonObjectParam.getString("detailTask"));//获取detailId
            flowDetailId = recursionSemanticAnalysisObject(flowDetailId, map);
            jsonObjectParam.put("detailTask", flowDetailId);
        }

        String fileName = jsonObjectParam.getString("fileName");
        //过滤掉特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher m = pattern.matcher(fileName);
        if (m.find()) {
            throw new WebException(MySystemCode.BIZ_PROJECTEXPORT_FILENAME_NOALLOW);
        }
        //查询导出文件名是否存在
        ProjectExportFilter filter = new ProjectExportFilter();
        filter.setProjectId(workFlowId);
        List<ProjectExportPo> list = projectExportService.getProjectExportListByProjectExportFilter(filter);
        if (!Validate.isEmpty(list)) {
            for (ProjectExportPo p : list) {
                JSONObject pj = JSON.parseObject(p.getJsonParam());
                if (fileName.equals(pj.getString("fileName"))) {
                    throw new WebException(MySystemCode.BIZ_CREATE_PROJECTEXPORT);
                }
            }
        }
        projectExportBO.setProjectId(workFlowId);
        projectExportBO.setJsonParam(jsonObjectParam.toJSONString());
        projectExportBO.setStatus(1);//在保存时就将任务状态更新为进行中，保存后就开始进行导出
        projectExportBO.setProgress(0);
        //新增
        projectExportService.addProjectExportBO(projectExportBO);

        String dpmssServer = WebUtil.getDpmssServerByEnv();
        new Thread(new ExecuteProjectExport(String.valueOf(projectExportBO.getId()), dpmssServer)).start();
        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        commonResultCodePO.setMessage("保存成功");
        return commonResultCodePO;
    }


    @RequestMapping(value = "/getObjectByTypeNoAndDataSourceId.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据数据源类型id和节点类型查询输出字段", position = 0)
    public List<Map<String, Object>> getObjectByTypeNoAndDataSourceId(@RequestParam("typeNo") @ApiParam("节点类型") String typeNo,
                                                                      @RequestParam(value = "dataSourceTypeId", required = false) @ApiParam("dataSourceTypeId") String dataSourceTypeId) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        switch (typeNo) {
            case "dataImport":
                if (!Validate.isEmpty(dataSourceTypeId)) {
                    List<StorageTypeFieldPO> list = dataSourceTypeService.getDataSourceTypeRelationList(dataSourceTypeId);
                    for (StorageTypeFieldPO storageTypeFieldPO : list) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", storageTypeFieldPO.getId());
                        map.put("fieldCnName", storageTypeFieldPO.getFieldCnName());
                        map.put("fieldEnName", storageTypeFieldPO.getFieldEnName());
                        map.put("fieldType", storageTypeFieldPO.getFieldType());
                        resultList.add(map);
                    }
                } else {
                    throw new WebException("dataSourceTypeId不能为空");
                }
                break;
            case "dataCrawl":
                if (!Validate.isEmpty(dataSourceTypeId)) {
                    List<StorageTypeFieldPO> list = dataSourceTypeService.getDataSourceTypeRelationHasRuleList(dataSourceTypeId,typeNo);
                    for (StorageTypeFieldPO storageTypeFieldPO : list) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", storageTypeFieldPO.getId());
                        map.put("fieldCnName", storageTypeFieldPO.getFieldCnName());
                        map.put("fieldEnName", storageTypeFieldPO.getFieldEnName());
                        map.put("fieldType", storageTypeFieldPO.getFieldType());
                        resultList.add(map);
                    }
                } else {
                    throw new WebException("dataSourceTypeId不能为空");
                }
                break;
            case "semanticAnalysisObject":
                Map<String, Object> map = new HashMap<>();
                map.put("fieldCnName", "语义结果");
                map.put("fieldEnName", "semanticAnalysisObject");
                resultList.add(map);
                break;
            case "wordSegmentation":
                Map<String, Object> wordSegmentationMap = new HashMap<>();
                wordSegmentationMap.put("fieldCnName", "分词结果");
                wordSegmentationMap.put("fieldEnName", "wordSegmentation");
                resultList.add(wordSegmentationMap);
                break;
            case "themeAnalysisSetting":
                Map<String, Object> themeAnalysisSettingMap = new HashMap<>();
                themeAnalysisSettingMap.put("fieldCnName", "主题结果");
                themeAnalysisSettingMap.put("fieldEnName", "themeAnalysisSetting");
                resultList.add(themeAnalysisSettingMap);
                break;
            case "topicAnalysisDefinition":
                Map<String, Object> topicAnalysisDefinitionMap = new HashMap<>();
                topicAnalysisDefinitionMap.put("fieldCnName", "话题结果");
                topicAnalysisDefinitionMap.put("fieldEnName", "topicAnalysisDefinition");
                resultList.add(topicAnalysisDefinitionMap);
                break;
            default:
                throw new WebException("typeNo不能为空");
        }
        return resultList;
    }

    //执行导出的内部类。
    class ExecuteProjectExport implements Runnable {

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
            Map<String, String> map = new HashMap<>();
            //将dpmss系统需要的参数保存在map中，一并传过去
            String projectId = projectExportBO.getProjectId() + "";
            map.put("projectId", projectId);
            map.put("flowId", id);
            map.put("flowDetailId", id);
            map.put("typeNo", Constants.VISPROJECTEXPORT_TYPE);
            map.put("jsonParam", projectExportBO.getJsonParam());
            map.put("paramType", "0");
            HttpClientHelper httpClientHelper = new HttpClientHelper();
            //得到远程调用的地址
            String excutorUrl = dpmssServer + Constants.PROJECTEXPORT_EXECUTEUrl;
            try {
                //远程调用
                HttpResponse httpResponse = httpClientHelper.doPost(excutorUrl, map, "utf-8", null, null, null);
                String content = httpResponse.getContent();
                JSONObject jsonObject = JSONObject.parseObject(content);
                Object code = jsonObject.get("code");
                Object message = jsonObject.get("message");
                if (!code.toString().equals("0")) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("id", id);
                    map1.put("status", 9 + "");
                    map1.put("errorMessage", message.toString());
                    projectExportService.updateStatusById(map1);
                }
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @RequestMapping(value = "/startProjectExport.json", method = RequestMethod.POST)
    @ApiOperation(value = "执行项目导出", notes = "", position = 0)
    @ResponseBody
    public CommonResultCodePO startExportProject(@RequestParam(value = "projectExportTaskId") @ApiParam(value = "导出项目任务id") String id) {
        String dpmssServer = WebUtil.getDpmssServerByEnv();

        Map<String, String> map1 = new HashMap<>();
        map1.put("id", id);
        map1.put("status", 1 + "");
        projectExportService.updateStatusById(map1);

        new Thread(new ExecuteProjectExport(id, dpmssServer)).start();

        CommonResultCodePO commonResultCodePO = new CommonResultCodePO();
        return commonResultCodePO;
    }

    //递归查到
    public Long recursionSemanticAnalysisObject(Long detailId, Map<Long, WorkFlowDetail> map) {
        WorkFlowDetail workFlowDetail = map.get(detailId);
        if (workFlowDetail.getTypeNo().equals(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT)) {
            return workFlowDetail.getFlowDetailId();
        } else {
            Long preDetailId = Long.parseLong(workFlowDetail.getPrevFlowDetailIds());
            return recursionSemanticAnalysisObject(preDetailId, map);
        }
    }
    @RequestMapping(value = "/savetWorkFlowProjectAndPreRun.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存可视化工作流项目节点配置信息并试运行", position = 0)
    public Map<String, Object> savetWorkFlowProjectAndPreRun(@RequestParam(value = "body") String body) {
        Map<String, Object> reslutMap = workFlowService.addVisWorkFlowNodeParamAndRun(body);
        return reslutMap;
    }
    @RequestMapping(value = "/saveOnceWorkFlowProjectNodeInfo.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "确定按钮小保存", position = 0)
    public List<Map<String, Object>> saveOnceWorkFlowProjectNodeInfo(@RequestParam(value = "body") String body) {
        List<Map<String, Object>> reslutMap = workFlowService.saveOnceWorkFlowProjectNodeInfo(body);
        return reslutMap;
    }
    @RequestMapping(value = "/getNodeProgress.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "节点的实时状态", position = 0)
    public Map<String, Object> getNodeProgress(@RequestParam(value = "projectId") @ApiParam("项目id") String projectId,
                                                @RequestParam(value = "workFlowId") @ApiParam("工作流id") String workFlowId,
                                               @RequestParam(value = "progress") @ApiParam("工作流id") String progress) {

        if(Validate.isEmpty(projectId)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(workFlowId)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        Map<String, Object> reslutMap = workFlowService.getNodeProgress(projectId,workFlowId,progress);
        return reslutMap;
    }
    @RequestMapping(value = "/testSavetWorkFlowProjectNodeInfo.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存-测试用", position = 0)
    public Map<String, Object> testSavetWorkFlowProjectNodeInfo(@RequestParam(value = "filePath") String filePath) {
        String body = Base64Util.readToString(filePath);
        Map<String, Object> reslutMap = workFlowService.addVisWorkFlowNodeParamAndRun(body);
        return reslutMap;
    }
}
