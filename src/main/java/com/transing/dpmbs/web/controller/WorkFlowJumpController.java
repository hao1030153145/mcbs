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
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.dpmbs.web.po.DatasourceTypePO;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.*;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller("workFlowJumpController")
@Api(value = "工作流跳转", description = "工作流跳转访问接口", position = 2)
public class WorkFlowJumpController {

    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private JobTypeService jobTypeService;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @RequestMapping(value = "/toDataModular.html",method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView toDataModular(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                      @RequestParam(value = "paramId",required = true)@ApiParam(value = "参数 id",required = true) String paramId,
                                      @RequestParam(value = "typeNo",required = true)@ApiParam(value = "typeNo",required = true) String typeNo,
                                      HttpServletRequest req, HttpServletResponse res){
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (paramId == null || "".equals(paramId) || !paramId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (typeNo == null || "".equals(typeNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long paramIdInt = Long.parseLong(paramId);

        String html = "showData/showData";

        Map<String,Object> reMap = new HashMap<>();

        reMap.put("projectId",projectId);
        reMap.put("typeNo",typeNo);
        reMap.put("paramId",paramId);
        if(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT.equals(typeNo)
                ||Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION.equals(typeNo)
                ||Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING.equals(typeNo)
                ||Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(typeNo)){
            List<Map<String,Object>> tabList = new ArrayList<>();

            Map<String,Object> map1 = new HashMap<>();
            map1.put("id","sentence");
            map1.put("name","句级");
            tabList.add(map1);

            Map<String,Object> map2 = new HashMap<>();
            map2.put("id","section");
            map2.put("name","段级");
            tabList.add(map2);

            Map<String,Object> map3 = new HashMap<>();
            map3.put("id","article");
            map3.put("name","文级");
            tabList.add(map3);

            reMap.put("tabList",tabList);
        }if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(typeNo)
                ||Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(typeNo)){
            List<WorkFlowParam> list = new ArrayList<>();
            WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);
            list.add(workFlowParam);
            if(null != workFlowParam) {
                List<Map<String, Object>> tabList = new ArrayList<>();
                WorkFlowDetail workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(workFlowParam.getFlowDetailId());
                if (null != workFlowDetail) {
                    String nextFlowIdId = workFlowDetail.getNextFlowDetailIds();
                    if (!Validate.isEmpty(nextFlowIdId) && !nextFlowIdId.equals("0")) {
                        workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(nextFlowIdId));
                    }
                    while (!Validate.isEmpty(nextFlowIdId) && !nextFlowIdId.equals("0") &&
                            (workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL) || workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL))) {
                        workFlowParam = workFlowService.getWorkFlowParamByDetailId(Long.parseLong(nextFlowIdId));
                        workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(nextFlowIdId));
                        nextFlowIdId = workFlowDetail.getNextFlowDetailIds();
                        if (!Validate.isEmpty(nextFlowIdId) && !nextFlowIdId.equals("0")) {
                            workFlowDetail = workFlowService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(nextFlowIdId));
                        }
                        list.add(workFlowParam);
                    }
                }


                Long detailId = 0L;
                for (WorkFlowParam w : list) {
                    String jsonParam = w.getJsonParam();
                    String detailIds = "";
                    JSONObject jsonObject = JSONObject.fromObject(jsonParam);
                    Object datasourceTypeIdObj = jsonObject.get("datasourceTypeId");
                    if (null == datasourceTypeIdObj) {
                        datasourceTypeIdObj = jsonObject.getJSONObject("jsonParam").get("datasourceTypeId");
                    }
                    if (null != datasourceTypeIdObj) {
                        DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(datasourceTypeIdObj.toString()));
                        Map<String, Object> map = new HashMap<>();
//                        if(datasourceTypePO.getStorageTypeTable().equals(storageTable)) {
//                            detailId = w.getFlowDetailId();
//                            map.put("id",storageTable);
//                            map.put("name",datasourceTypePO.getTypeName());
//                            for(Map<String,Object> m : tabList){
//                                if(m.get("id").equals(map.get("id"))){
//                                    detailIds += m.get("detailId")+"&".toString();
//                                    String detail
//                                    map.put("detailId",(detailIds+detailId).substring(0,detailIds.length()-1));
//                                }else{
//                                    continue;
//                                }
//                            }
//                            tabList.add(map);
//                        }else{
                        detailId = w.getFlowDetailId();
                        map.put("id", datasourceTypePO.getStorageTypeTable());
                        map.put("name", datasourceTypePO.getTypeName());
                        map.put("detailId", detailId);
                        tabList.add(map);
//                        }
                    }
                }
                List<Map<String, Object>> list2 = new ArrayList<>();
                Set<String> list3 = new HashSet<>();

                for(Map<String,Object> m : tabList){
                    list3.add(m.get("id").toString());
                }
                for(String str :list3){
                    String detailIds = "";
                    for(Map<String,Object> m :tabList){
                        if(m.get("id").equals(str)){
                            detailIds += "&"+m.get("detailId").toString();
                        }
                    }
                    for(Map<String,Object> m :tabList){
                        if(m.get("id").equals(str)){
                            m.put("detailId",detailIds.substring(1,detailIds.length()));
                        }
                    }
                }
                if(!Validate.isEmpty(tabList)){
                    reMap.put("tabList",tabList);
                }
            }
        }else{
            List<Map<String,Object>> tabList = new ArrayList<>();
            WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);
            String jsonParam = workFlowParam.getJsonParam();
            JSONObject jsonObject = JSONObject.fromObject(jsonParam);
            Object datasourceTypeIdObj = jsonObject.get("typeId");
            if(null != datasourceTypeIdObj) {
                DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(datasourceTypeIdObj.toString()));
                Map<String,Object> map = new HashMap<>();
                map.put("id",datasourceTypePO.getStorageTypeTable());
                map.put("name",datasourceTypePO.getTypeName());
                map.put("detailId",workFlowParam.getFlowDetailId());
                tabList.add(map);
            }
            if(!Validate.isEmpty(tabList)){
                reMap.put("tabList",tabList);
            }
        }
        String mapJson = JSON.toJSONString(reMap);

        req.setAttribute("mapJson",mapJson);

        return new ModelAndView(html);
    }

    @RequestMapping(value = "/toModular.html", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView toModular(@RequestParam(value = "from",required = true)@ApiParam(value = "1为项目主页来的，0为其他上一步下一步来的",required = true) String from,
                            @RequestParam(value = "toModular",required = false)@ApiParam(value = "要跳转的模块（dataImport 导入" +
                                    "dataOutput为输出数据" +
                                    "semanticAnalysisObject语义分析" +
                                    "themeAnalysisSetting 主题分析" +
                                    "topicAnalysisDefinition 话题分析" +
                                    "wordSegmentation分词" +
                                    "fileOutput文件输出 " +
                                    "projectList 项目列表）",required = false) String toModular,
                           @RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                           @RequestParam(value = "typeNo",required = false)@ApiParam(value = "typeNo",required = false) String typeNo,
            HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);

        List<ProjectJobTypeBO> projectJobTypeBOList = projectJobTypeService.getProjectJobTypeListByProjectId(projectIdInt);
        Map<String,ProjectJobTypeBO> projectJobTypeMap = new HashMap<>();
        if(!Validate.isEmpty(projectJobTypeBOList)){
            for (ProjectJobTypeBO projectJobTypeBO:projectJobTypeBOList) {
                projectJobTypeMap.put(projectJobTypeBO.getTypeNo(),projectJobTypeBO);
            }
        }

        if(Validate.isEmpty(toModular)){
            ProjectJobTypeBO projectJobTypeBO = projectJobTypeBOList.get(0);
            toModular = projectJobTypeBO.getTypeNo();
            typeNo = projectJobTypeBO.getTypeNo();
        }

        String html = "projectManager/workFlowLayoutPage/";
        int state = 0;
        String previousUrl = "";
        String nextUrl = "";
        String finishUrl = "/project/projectHomePage.html?projectId="+projectId;

        ProjectJobTypeBO projectJobTypeBO = projectJobTypeMap.get(toModular);

        if(null != projectJobTypeBO){
            html += projectJobTypeBO.getTypeNo()+"Page";
            if(Validate.isEmpty(projectJobTypeBO.getPreTypeNo())){
                previousUrl = "/project/projectCreatePage.html?projectId="+projectId+"&from="+from;
            }else {
                previousUrl = "/toModular.html?"+"from=0&toModular="+projectJobTypeBO.getPreTypeNo()+"&projectId="+projectId+"&typeNo="+projectJobTypeBO.getPreTypeNo();
            }

            if(Validate.isEmpty(projectJobTypeBO.getNextTypeNo())){
                finishUrl = "/toModular.html?"+"from=0&toModular=projectList&projectId="+projectId+"&typeNo="+Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT;
                nextUrl = finishUrl;
            }else {
                nextUrl = "/toModular.html?"+"from=0&toModular="+projectJobTypeBO.getNextTypeNo()+"&projectId="+projectId+"&typeNo="+projectJobTypeBO.getNextTypeNo();
            }
        }else {
            html = "projectManager/listPage/projectListPage";
        }

        if("1".equals(from)){
            state = 1;
        }

        Map<String,Object> reMap = new HashMap<>();

        reMap.put("projectId",projectId);

        JobTypeInfo jobTypeInfo = jobTypeService.getValidJobTypeByTypeNo(typeNo);

        if(null != jobTypeInfo){
            String url = jobTypeInfo.getParamConfigUrl();

            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("typeNo",typeNo);
            paramMap.put("url",url);

            reMap.put("obj",paramMap);
        }

        reMap.put("pre",previousUrl);
        reMap.put("next",nextUrl);
        reMap.put("finish",finishUrl);
        reMap.put("status",state);

        String mapJson = JSON.toJSONString(reMap);

        req.setAttribute("mapJson",mapJson);

        /**
         * 循环 workInfo 判断 totalJobNum 是否大于0 来更新 项目 的状态
         */
        long status = -1;
        ProjectOne projectOne = projectService.getProjectInf(projectIdInt);
        if (projectOne == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }

        String projectStatus = projectOne.getStatus();
        //如果项目 状态为待配置，或者配置中 才判断 配置中和 待启动 状态。
        if("0".equals(projectStatus) || "1".equals(projectStatus)){

            if(null != projectJobTypeBOList && projectJobTypeBOList.size() > 0){
                out:for (int i = 0; i < projectJobTypeBOList.size(); i++) {
                    ProjectJobTypeBO projectJobTypeBO1 = projectJobTypeBOList.get(i);

                    String typeNo1 = projectJobTypeBO1.getTypeNo();
                    List<String> typeNoList = new ArrayList<>();
                    typeNoList.add(typeNo1);
                    if(typeNo1.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                        typeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);
                    }
                    List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,projectIdInt);

                    int totalJobNum = workFlowDetailList.size();

                    for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                        int infoStatus = workFlowDetail.getJobStatus();
                        if(infoStatus == 2){
                            status = -1;
                            break out;
                        }
                    }

                    if(totalJobNum > 0 && status == -1){
                        status = 2;
                    }else if(totalJobNum > 0 && status == 0){
                        status = 1;
                        break;
                    }else if(totalJobNum > 0 && status == 2){
                        status = 2;
                    }else if(totalJobNum <= 0 && status == -1){
                        status = 0;
                    }else if(totalJobNum <= 0 && status == 2){
                        status = 1;
                        break;
                    }

                }
            }

            if(status != -1){
                ProjectStatusFilter filter = new ProjectStatusFilter();
                filter.setId(projectIdInt);
                filter.setStatus(status);
                projectService.updateProjectStatus(filter);
            }

        }

        return new ModelAndView(html);
    }

}