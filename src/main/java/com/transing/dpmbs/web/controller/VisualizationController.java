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
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.mongodb.MongoNamespace;
import com.transing.dpmbs.biz.service.MongoDBService;
import com.transing.dpmbs.biz.service.VisualizationService;
import com.transing.dpmbs.constant.Constants;
import com.transing.dpmbs.constant.MongoDBDbNames;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.util.Base64Util;
import com.transing.dpmbs.util.chart.ChartProcess;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.CommonPO;
import com.transing.dpmbs.web.po.StatisticsAnalysisPo;
import com.transing.dpmbs.web.po.StatisticsFieldAndFilter;
import com.transing.dpmbs.web.po.StatisticsObject;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Controller("visualizationController")
@Api(value = "可视化", description = "可视化相关接口接口", position = 2)
@RequestMapping("/visualization")
public class VisualizationController {

    @Resource
    private VisualizationService visualizationService;

    @Resource
    private MongoDBService mongoDBService;

    @Resource
    private WorkFlowService workFlowService;

    @RequestMapping(value = "/toVisualizationList.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "跳转可视化列表", position = 0)
    public ModelAndView toVisualizationList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                            HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);

        Map<String,Object> map = new HashMap<>();
        map.put("projectId",projectIdInt);

        req.setAttribute("mapJson", JSON.toJSONString(map));
        return new ModelAndView("visualization/visualizationList/visualizationList");
    }

    @RequestMapping(value = "/toVisualizationTemplate.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "跳转可视化选择模板页面", position = 0)
    public ModelAndView toVisualizationTemplate(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                        HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);

        Map<String,Object> map = new HashMap<>();
        map.put("projectId",projectIdInt);
        map.put("url","/visualization/toVisualizationPlatform.html");

        req.setAttribute("mapJson", JSON.toJSONString(map));
        return new ModelAndView("visualization/visualizationTemplate/visualizationTemplate");
    }

    @RequestMapping(value = "/deleteVisualization.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除可视化", position = 0)
    public CommonPO deleteVisualization(@RequestParam(value = "id",required = true)@ApiParam(value = "可视化id",required = true) String visId,
                                                 HttpServletRequest req, HttpServletResponse res) {

        if (visId == null || "".equals(visId) || !visId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int visIdInt = Integer.parseInt(visId);

        visualizationService.deleteVisById(visIdInt);
        visualizationService.deleteVisModuleByVisId(visIdInt);

        CommonPO commonPO = new CommonPO();

        return commonPO;

    }

    @RequestMapping(value = "/toVisualizationPlatform.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "跳转可视化工作台", position = 0)
    public ModelAndView toVisualizationWorkbench(@RequestParam(value = "visId",required = true)@ApiParam(value = "可视化id",required = true) String visId,
                                                 HttpServletRequest req, HttpServletResponse res) {

        if (visId == null || "".equals(visId) || !visId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int visIdInt = Integer.parseInt(visId);

        Map<String,Object> map = new HashMap<>();

        User user = (User) req.getSession().getAttribute(Constants.WITH_SESSION_USER);
        if(null != user){
            String account = user.getAccount();
            map.put("userName",account);
        }

        VisualizationBO visualizationBO = visualizationService.getVisualizationById(visIdInt);
        if(null != visualizationBO){
            map.put("projectName",visualizationBO.getName());
            map.put("projectId",visualizationBO.getProjectId());
        }

        map.put("visId",visIdInt);
        map.put("backUrl","/visualization/toVisualizationList.html?projectId="+visualizationBO.getProjectId());
        map.put("saveUrl","/visualization/toVisualizationList.html?projectId="+visualizationBO.getProjectId());

        req.setAttribute("mapJson", JSON.toJSONString(map));
        return new ModelAndView("visualization/visualizationPlatform/visualizationPlatform");
    }

    @RequestMapping(value = "/getVisList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询 可视化列表 接口", notes = "", position = 0)
    public List<VisualizationBO> getVisList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                                            HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);

        return visualizationService.getVisualizationList(projectIdInt);
    }

    @RequestMapping(value = "/getImage.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取上传文件", notes = "", position = 0)
    public void getImage(@RequestParam(value = "image", required = true) @ApiParam(value = "文件地址", required = true) String image,
                              HttpServletRequest req, HttpServletResponse res) {

        if(Validate.isEmpty(image)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        File file = new File(image);
        if (file.exists()) {
            try {
                InputStream inputStream = null;
                //根据路径获取要下载的文件输入流
                inputStream = new FileInputStream(file);

                String type = image.split("\\.")[1];

                res.reset();
                res.setHeader("Content-type", "APPLICATION/OCTET-STREAM");
                res.setHeader("Content-Disposition",
                        "attachment; filename=" + URLEncoder
                                .encode(System.currentTimeMillis()+"."+type, "UTF-8"));

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

    @RequestMapping(value = "/saveVisInfo.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存可视化图表信息", notes = "", position = 0)
    public CommonPO saveVisInfo(@RequestParam(value = "visId",required = true)@ApiParam(value = "可视化id",required = true) String visId,
                                @RequestParam(value = "settingsArray",required = true)@ApiParam(value = "设置array",required = true) String settingArray,
                                @RequestParam(value = "chartsArray",required = true)@ApiParam(value = "数据json",required = true) String chartsArray,
                                @RequestParam(value = "image",required = false)@ApiParam(value = "数据json",required = false) String image,
                                @RequestParam(value = "backSetting",required = false)@ApiParam(value = "数据json",required = false) String backSetting,
                                HttpServletRequest req, HttpServletResponse res) {

        if (visId == null || "".equals(visId) || !visId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int visIdInt = Integer.parseInt(visId);

        if(!Validate.isEmpty(image)){
            image = image.substring(image.indexOf(",")+1,image.length());

            String fileName = "visId"+System.currentTimeMillis()+".png";
            String fileBase = System.getProperty("upload.dir");
            String fileParamter = "/project/vis/tmp/";
            String fileDir = fileBase + fileParamter + fileName;

            File file = new File(fileBase + fileParamter);
            if(!file.exists()){
                file.mkdirs();
            }

            if(Base64Util.GenerateImage(image,fileDir)){
                VisualizationBO visualizationBO = new VisualizationBO();
                visualizationBO.setId(visIdInt);
                visualizationBO.setImage(fileDir);
                visualizationService.updateVisualization(visualizationBO);
            }

        }

        if(!Validate.isEmpty(backSetting)){
            VisualizationBO visualizationBO = new VisualizationBO();
            visualizationBO.setId(visIdInt);
            visualizationBO.setBackSetting(backSetting);
            visualizationService.updateVisualization(visualizationBO);
        }

        visualizationService.deleteVisModuleByVisId(visIdInt);

        VisModuleBO visModuleBO = new VisModuleBO();

        visModuleBO.setVisId(visIdInt);
        JSONArray jsonArray = JSONArray.fromObject(settingArray);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String type = jsonObject.getJSONObject("settings").getString("type");
            if("chart".equals(type)){
                jsonObject.remove("data");
                jsonObject.remove("tableData");

                jsonArray.set(i,jsonObject);
            }
        }

        JSONArray chartJsonArray = JSONArray.fromObject(chartsArray);
        for (int i = 0; i < chartJsonArray.size(); i++) {
            JSONObject jsonObject = chartJsonArray.getJSONObject(i);

            String type = jsonObject.getString("type");
            if("chart".equals(type)){
                jsonObject.remove("option");

                chartJsonArray.set(i,jsonObject);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("settingArray",jsonArray);
        jsonObject.put("chartsArray",chartJsonArray);

        visModuleBO.setJsonParam(JSON.toJSONString(jsonObject));

        visualizationService.addVisModule(visModuleBO);

        CommonPO commonPO = new CommonPO();

        return commonPO;
    }

    @RequestMapping(value = "/getVisModuleList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取可视化图表 保存的 数据", notes = "", position = 0)
    public Map<String,Object> getVisModuleList(@RequestParam(value = "visId",required = true)@ApiParam(value = "可视化id",required = true) String visId,
                                HttpServletRequest req, HttpServletResponse res) {

        if (visId == null || "".equals(visId) || !visId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int visIdInt = Integer.parseInt(visId);

        JSONArray settingsArray = new JSONArray();
        JSONArray chartsArr = new JSONArray();

        VisualizationBO visualizationBO = visualizationService.getVisualizationById(visIdInt);

        List<VisModuleBO> visModuleBOList = visualizationService.getVisModuleList(visIdInt);
        if(!Validate.isEmpty(visModuleBOList)){
            for (VisModuleBO visModuleBO:visModuleBOList) {
                String jsonParam = visModuleBO.getJsonParam();

                JSONObject jsonParamObj = JSONObject.fromObject(jsonParam);

                JSONArray jsonArray = jsonParamObj.getJSONArray("settingArray");
                JSONArray chartsArray = jsonParamObj.getJSONArray("chartsArray");

                if(!Validate.isEmpty(jsonArray)){
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Object key = jsonObject.get("key");

                        int visChartId = jsonObject.getInt("visChartId");

                        VisChartBOWithBLOBs visChartBOWithBLOBs = visualizationService.getVisChartById(visChartId);
                        String processClass = visChartBOWithBLOBs.getProcessClass();

                        JSONObject settingJsonObject = jsonObject.getJSONObject("settings");
                        String type = settingJsonObject.getString("type");
                        if(!"chart".equals(type)){
                            settingsArray.add(jsonObject);

                            if(!Validate.isEmpty(chartsArray)){
                                for (int j = 0; j < chartsArray.size(); j++) {
                                    JSONObject object = chartsArray.getJSONObject(j);
                                    String key1 = object.getString("key");
                                    if(key1.equals(key.toString())){
                                        chartsArr.add(object);
                                        break;
                                    }
                                }
                            }

                            continue;
                        }

                        JSONObject inputSettingsParam = jsonObject.getJSONObject("inputSettingsParam");

                        JSONObject chartsJsonObject = null;
                        if(!Validate.isEmpty(chartsArray)){
                            for (int j = 0; j < chartsArray.size(); j++) {
                                JSONObject object = chartsArray.getJSONObject(j);
                                String key1 = object.getString("key");
                                if(key1.equals(key.toString())){
                                    chartsJsonObject = object;
                                    break;
                                }
                            }
                        }

                        if(null != inputSettingsParam && !inputSettingsParam.isEmpty()){

                            jsonObject.put("inputSettingsParam",inputSettingsParam.toString());

                            JSONArray fieldArray = new JSONArray();
                            JSONObject fieldJsonObject = new JSONObject();

                            String inputFieldArray = visChartBOWithBLOBs.getInputFieldArray();
                            JSONArray inputFieldJsonArray = JSONArray.fromObject(inputFieldArray);
                            Set<String> inputFieldKeySet = new HashSet();
                            for (int j = 0; j < inputFieldJsonArray.size(); j++) {
                                JSONObject inputFieldJSONObject = inputFieldJsonArray.getJSONObject(j);
                                String field = inputFieldJSONObject.getString("field");
                                inputFieldKeySet.add(field);
                            }
                            Set<String> keySet = inputSettingsParam.keySet();
                            for (String keyString:keySet) {
                                if(inputFieldKeySet.contains(keyString)){
                                    fieldJsonObject.put(keyString,inputSettingsParam.get(keyString));
                                }
                            }
                            fieldArray.add(fieldJsonObject);

                            //TODO 通过paramId查询数据
                            List<Map<String,Object>> mapList = new ArrayList<>();
                            Object limitNumObj = inputSettingsParam.get("limitNum");
                            int limitNum = null != limitNumObj && !"".equals(limitNumObj)?Integer.parseInt(limitNumObj.toString()):100;
                            JSONArray sortOrderArray = inputSettingsParam.getJSONArray("sortOrder");
                            Document sort = null;
                            if (!Validate.isEmpty(sortOrderArray)){
                                JSONObject jsonObj = sortOrderArray.getJSONObject(sortOrderArray.size()-1);
                                String field = jsonObj.getString("field");
                                String order = jsonObj.getString("order");

                                int sortNum = 0;
                                if("desc".equals(order)){
                                    sortNum = -1;
                                }else if("asc".equals(order)){
                                    sortNum = 1;
                                }
                                sort = new Document();
                                sort.put(field,sortNum);

                            }

                            long paramIdInt = inputSettingsParam.getLong("paramId");

                            WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);

                            long detailId = workFlowParam.getFlowDetailId();
                            long projectId = workFlowParam.getProjectId();
                            Document filter = new Document();
                            filter.put("projectId",projectId);
                            filter.put("detailId",detailId);

                            Map<String, Object> map = mongoDBService.findPageDocument(1,limitNum, MongoDBDbNames.DB_DPM,MongoDBDbNames.COLLECTION_STATICAL_NAME,filter,sort);
                            mapList = (List) map.get("pageData");

                            try{

                                ChartProcess chartProcess = (ChartProcess) Class.forName(processClass).newInstance();
                                chartProcess.init(mapList,fieldArray,JSONObject.fromObject(visChartBOWithBLOBs.getDefaultDataArray()));
                                Map<String,Object> returnMap = chartProcess.conversionChart();

                                chartsJsonObject.put("key",key);
                                chartsJsonObject.put("type",type);
                                chartsJsonObject.put("option", returnMap.get(ChartProcess.KEY_DATAARRAY));
                                chartsJsonObject.put("tableData",returnMap.get(ChartProcess.KEY_TABLEARRAY));

                                jsonObject.put("data",returnMap.get(ChartProcess.KEY_DATAARRAY));
                                jsonObject.put("tableData",returnMap.get(ChartProcess.KEY_TABLEARRAY));

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }else {

                            jsonObject.put("inputSettingsParam",new JSONObject());
                            Object inputParam = jsonObject.get("inputParam");
                            if(inputParam == null){
                                jsonObject.put("inputParam",new JSONObject());
                            }

                            chartsJsonObject.put("key",key);
                            chartsJsonObject.put("type",type);

                            if(JSONUtils.mayBeJSON(visChartBOWithBLOBs.getDefaultDataArray())){
                                chartsJsonObject.put("option", JSONObject.fromObject(visChartBOWithBLOBs.getDefaultDataArray()));
                            }else {
                                chartsJsonObject.put("option", visChartBOWithBLOBs.getDefaultDataArray());
                            }

                            if(JSONUtils.mayBeJSON(visChartBOWithBLOBs.getDefaultTableArray())){
                                chartsJsonObject.put("tableData",JSONArray.fromObject(visChartBOWithBLOBs.getDefaultTableArray()));
                            }else {
                                chartsJsonObject.put("tableData",visChartBOWithBLOBs.getDefaultTableArray());
                            }

                            if(JSONUtils.mayBeJSON(visChartBOWithBLOBs.getDefaultDataArray())){
                                jsonObject.put("data",JSONObject.fromObject(visChartBOWithBLOBs.getDefaultDataArray()));
                            }else {
                                jsonObject.put("data",visChartBOWithBLOBs.getDefaultDataArray());
                            }

                            if(JSONUtils.mayBeJSON(visChartBOWithBLOBs.getDefaultTableArray())){
                                jsonObject.put("tableData",JSONArray.fromObject(visChartBOWithBLOBs.getDefaultTableArray()));
                            }else {
                                jsonObject.put("tableData",visChartBOWithBLOBs.getDefaultTableArray());
                            }

                        }

                        settingsArray.add(jsonObject);
                        chartsArr.add(chartsJsonObject);

                    }
                }
            }
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("settingsArray",settingsArray);
        resultMap.put("chartsArray",chartsArr);
        if(!Validate.isEmpty(visualizationBO.getBackSetting()) && JSONUtils.mayBeJSON(visualizationBO.getBackSetting())){
            resultMap.put("backSetting",JSONObject.fromObject(visualizationBO.getBackSetting()));
        }

        return resultMap;
    }




    @RequestMapping(value = "/getVisLeftMenuList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取可视化图表左侧栏数据 接口", notes = "", position = 0)
    public Map<String,Object> getVisLeftMenuList(HttpServletRequest req, HttpServletResponse res) {

        Map<String,Object> resultMap = new HashMap<>();

        List<CategoryBO> categoryList = visualizationService.getVisCategoryList();
        resultMap.put("menuList",categoryList);

        Map<Integer,String> categoryMap = new HashMap<>();
        if(!Validate.isEmpty(categoryList)){
            for (CategoryBO categoryBO:categoryList) {
                int id = categoryBO.getId();
                String type = categoryBO.getType();
                categoryMap.put(id,type);
            }
        }

        List<VisChartBOWithBLOBs> visChartList = visualizationService.getVisChartList();
        if(!Validate.isEmpty(visChartList)){

            Map<String,List<Map<String,Object>>> visChartMap = new HashMap<>();
            for (VisChartBOWithBLOBs visChartBOWithBLOBs:visChartList) {

                int categoryId = visChartBOWithBLOBs.getCategoryId();
                String type = categoryMap.get(categoryId);

                List<Map<String,Object>> mapList = visChartMap.get(type);
                if(null == mapList){
                    mapList = new ArrayList<>();
                    visChartMap.put(type,mapList);
                }

                Map<String,Object> chartMap = new HashMap<>();
                chartMap.put("typeName",visChartBOWithBLOBs.getTypeName());

                List<Map<String,Object>> typeItems = new ArrayList<>();
                Map<String,Object> objectMap = new HashMap<>();
                objectMap.put("name",visChartBOWithBLOBs.getName());
                objectMap.put("img",visChartBOWithBLOBs.getImg());
                objectMap.put("visChartId",visChartBOWithBLOBs.getId());
                objectMap.put("categoryId",visChartBOWithBLOBs.getCategoryId());
                objectMap.put("type",visChartBOWithBLOBs.getType());
                objectMap.put("pType",type);
                typeItems.add(objectMap);

                chartMap.put("typeItems",typeItems);

                mapList.add(chartMap);

            }

            List<Map<String,Object>> visChartListMap = new ArrayList<>();
            if(!Validate.isEmpty(visChartMap)){
                for (Map.Entry<String, List<Map<String, Object>>> entry:visChartMap.entrySet()) {
                    Map<String,Object> objectMap = new HashMap<>();
                    objectMap.put("type",entry.getKey());
                    objectMap.put("list",entry.getValue());

                    visChartListMap.add(objectMap);
                }
            }

            resultMap.put("menudata",visChartListMap);

        }

        return resultMap;
    }

    @RequestMapping(value = "/getVisRightSettingList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取可视化图表右侧栏参数设置数据", notes = "", position = 0)
    public Map<String,Object> getVisRightSettingList(HttpServletRequest req, HttpServletResponse res) {

        List<VisJsonParamBO> visJsonParamBOList = visualizationService.getVisSettingJsonParamList();
        List<Map<String,Object>> mapList = new ArrayList<>();
        if(!Validate.isEmpty(visJsonParamBOList)){
            for (VisJsonParamBO visJsonParamBO:visJsonParamBOList) {
                try {

                    Map<String,Object> map = new HashMap<>();
                    JSONArray jsonArray = JSONArray.fromObject(visJsonParamBO.getParamSettings());

                    map.put("paramSettings",jsonArray);
                    map.put("type",visJsonParamBO.getType());
                    map.put("position",visJsonParamBO.getPosition());
                    map.put("id",visJsonParamBO.getId());

                    mapList.add(map);
                }catch (Exception e){

                }
            }
        }
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("settingList",mapList);

        return returnMap;
    }

    @RequestMapping(value = "/getVisBackSetting.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取画板参数设置数据", notes = "", position = 0)
    public JSONObject getVisBackSetting(HttpServletRequest req, HttpServletResponse res) {

        Map<String,Object> returnMap = new HashMap<>();

        List<VisJsonParamBO> visJsonParamBOList = visualizationService.getVisSettingJsonParamListByType("back");
        if(!Validate.isEmpty(visJsonParamBOList)){
            for (VisJsonParamBO visJsonParamBO:visJsonParamBOList) {
                try {
                    JSONObject jsonObject = JSONObject.fromObject(visJsonParamBO.getParamSettings());
                    return jsonObject;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/getVisRightDefaultList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取可视化图表右侧栏默认数据以及数据选择需要的数据", notes = "", position = 0)
    public Map<String,Object> getVisRightDefaultList(@RequestParam(value = "projectId",required = true)@ApiParam(value = "项目id",required = true) String projectId,
                        HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        long projectIdInt = Long.parseLong(projectId);

        Map<String,Object> returnMap = new HashMap<>();
        List<Map<String,Object>> defaultDataMapList = new ArrayList<>();

        List<VisChartBOWithBLOBs> visChartList = visualizationService.getVisChartList();
        if(!Validate.isEmpty(visChartList)){
            for (VisChartBOWithBLOBs visChartBOWithBLOBs:visChartList) {
                Map<String,Object> map = new HashMap<>();
                map.put("visChartId",visChartBOWithBLOBs.getId());

                if(JSONUtils.mayBeJSON(visChartBOWithBLOBs.getDefaultDataArray())){
                    map.put("defaultDataArray", JSONObject.fromObject(visChartBOWithBLOBs.getDefaultDataArray()));
                }else {
                    map.put("defaultDataArray", visChartBOWithBLOBs.getDefaultDataArray());
                }

                if(JSONUtils.mayBeJSON(visChartBOWithBLOBs.getDefaultTableArray())){
                    map.put("defaultTableArray", JSONArray.fromObject(visChartBOWithBLOBs.getDefaultTableArray()));
                }else {
                    map.put("defaultTableArray", visChartBOWithBLOBs.getDefaultTableArray());
                }

                if(JSONUtils.mayBeJSON(visChartBOWithBLOBs.getInputFieldArray())){
                    map.put("inputFieldArray", JSONArray.fromObject(visChartBOWithBLOBs.getInputFieldArray()));
                }else {
                    map.put("inputFieldArray", visChartBOWithBLOBs.getInputFieldArray());
                }
                defaultDataMapList.add(map);
            }
        }

        returnMap.put("defaultData",defaultDataMapList);

        List<Map<String,Object>> statisticsDataMapList = new ArrayList<>();

        Map<String,Object> statisticsMap = new HashMap<>();
        statisticsMap.put("dataResultId",1);
        statisticsMap.put("dataResult","统计分析");

        List<Map<String,Object>> itemList = new ArrayList<>();

        List<WorkFlowParam> workFlowParamList = workFlowService.getWorkFlowParamListByParam(com.transing.workflow.constant.Constants.WORK_FLOW_TYPE_NO_STATISTICAL,projectIdInt);
        if(!Validate.isEmpty(workFlowParamList)){
            for (WorkFlowParam workFlowParam:workFlowParamList) {
                StatisticsAnalysisPo statisticsAnalysisPo = JSON.parseObject(workFlowParam.getJsonParam(), StatisticsAnalysisPo.class);

                Map<String,Object> itemMap = new HashMap<>();
                itemMap.put("paramId",workFlowParam.getParamId());
                itemMap.put("name",statisticsAnalysisPo.getName());

                List<Map<String,Object>> fieldList = new ArrayList<>();
                List<StatisticsFieldAndFilter> statisticsFieldAndFilterList =  statisticsAnalysisPo.getJsonParam().getFieldAndFilter();
                if(!Validate.isEmpty(statisticsFieldAndFilterList)){
                    for (StatisticsFieldAndFilter statisticsFieldAndFilter:statisticsFieldAndFilterList) {
                        String fieldType = statisticsFieldAndFilter.getFieldType();
                        if("dimension".equals(fieldType)){

                            Map<String,Object> fieldMap = new HashMap<>();
                            fieldMap.put("field",statisticsFieldAndFilter.getField());
                            fieldMap.put("fieldName",statisticsFieldAndFilter.getFieldName());
                            fieldList.add(fieldMap);

                        }
                    }
                }

                List<StatisticsObject> statisticsObjectList = statisticsAnalysisPo.getJsonParam().getStatisticsObject();
                if(!Validate.isEmpty(statisticsObjectList)){
                    for (StatisticsObject statisticsObject:statisticsObjectList) {

                        Map<String,Object> fieldMap = new HashMap<>();
                        String type = statisticsObject.getType();
                        fieldMap.put("field",statisticsObject.getField()+"@Statistics");
                        String fieldName = statisticsObject.getFieldZh().split(",")[2];

                        if("count".equals(type)){
                            fieldName += " 计数";
                        }else if("average".equals(type)){
                            fieldName += " 平均值";
                        }else if("sum".equals(type)){
                            fieldName += " 求和";
                        }else if("max".equals(type)){
                            fieldName += " 最大值";
                        }else if("min".equals(type)){
                            fieldName += " 最小值";
                        }else if("middle".equals(type)){
                            fieldName += " 中位值";
                        }

                        fieldMap.put("fieldName",fieldName);
                        fieldList.add(fieldMap);
                    }
                }

                itemMap.put("fieldList",fieldList);

                itemList.add(itemMap);
            }
        }

        statisticsMap.put("itemList",itemList);
        statisticsDataMapList.add(statisticsMap);

        returnMap.put("statisticsData",statisticsDataMapList);

        return returnMap;
    }

    @RequestMapping(value = "/getVisData.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value="查询可视化数据接口",notes = "", position = 0)
    public Map<String ,Object> getVisData(@RequestParam(value = "jsonParam",required = true)@ApiParam(value = "选择的数据Json",required = true) String jsonParam,
                                          HttpServletRequest req, HttpServletResponse res){

        if (Validate.isEmpty(jsonParam)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int visChartIdInt = 0;
        int dataResultId = 0;
        long paramIdInt = 0;
        JSONObject inputJsonObject = null;
        WorkFlowParam workFlowParam = null;
        VisChartBOWithBLOBs visChartBOWithBLOBs = null;
        try {
            inputJsonObject = JSONObject.fromObject(jsonParam);
            visChartIdInt = inputJsonObject.getInt("visChartId");
            dataResultId = inputJsonObject.getInt("dataResultId");
            paramIdInt = inputJsonObject.getLong("paramId");

            visChartBOWithBLOBs = visualizationService.getVisChartById(visChartIdInt);
            if(null == visChartBOWithBLOBs){
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }
            String inputFieldArray = visChartBOWithBLOBs.getInputFieldArray();
            JSONArray inputFieldJsonArray = JSONArray.fromObject(inputFieldArray);
            for (int i = 0; i < inputFieldJsonArray.size(); i++) {
                JSONObject inputFieldJSONObject = inputFieldJsonArray.getJSONObject(i);
                String field = inputFieldJSONObject.getString("field");
                Object o = inputJsonObject.get(field);
                if(null == o || "".equals(o.toString().trim())){
                    throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
                }
            }

            workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);
            if(null == workFlowParam){
                throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        Map<String,Object> returnMap = new HashMap<>();

        try {

            String processClass = visChartBOWithBLOBs.getProcessClass();

            //TODO 通过paramId查询数据
            List<Map<String,Object>> mapList = new ArrayList<>();
            Object limitNumObj = inputJsonObject.get("limitNum");
            int limitNum = null != limitNumObj && !"".equals(limitNumObj)?Integer.parseInt(limitNumObj.toString()):100;
            JSONArray sortOrderArray = inputJsonObject.getJSONArray("sortOrder");
            Document sort = null;
            if (!Validate.isEmpty(sortOrderArray)){
                JSONObject jsonObject = sortOrderArray.getJSONObject(sortOrderArray.size()-1);
                String field = jsonObject.getString("field");
                String order = jsonObject.getString("order");

                int sortNum = 0;
                if("desc".equals(order)){
                    sortNum = -1;
                }else if("asc".equals(order)){
                    sortNum = 1;
                }
                sort = new Document();
                sort.put(field,sortNum);

            }

            long detailId = workFlowParam.getFlowDetailId();
            long projectId = workFlowParam.getProjectId();
            Document filter = new Document();
            filter.put("projectId",projectId);
            filter.put("detailId",detailId);

            Map<String, Object> map = mongoDBService.findPageDocument(1,limitNum, MongoDBDbNames.DB_DPM,MongoDBDbNames.COLLECTION_STATICAL_NAME,filter,sort);
            mapList = (List) map.get("pageData");

            JSONArray fieldArray = new JSONArray();
            JSONObject fieldJsonObject = new JSONObject();

            String inputFieldArray = visChartBOWithBLOBs.getInputFieldArray();
            JSONArray inputFieldJsonArray = JSONArray.fromObject(inputFieldArray);
            Set<String> inputFieldKeySet = new HashSet();
            for (int i = 0; i < inputFieldJsonArray.size(); i++) {
                JSONObject inputFieldJSONObject = inputFieldJsonArray.getJSONObject(i);
                String field = inputFieldJSONObject.getString("field");
                inputFieldKeySet.add(field);
            }

            Set<String> keySet = inputJsonObject.keySet();
            for (String keyString:keySet) {
                if(inputFieldKeySet.contains(keyString)){
                    fieldJsonObject.put(keyString,inputJsonObject.get(keyString));
                }
            }
            fieldArray.add(fieldJsonObject);

            ChartProcess chartProcess = (ChartProcess) Class.forName(processClass).newInstance();
            chartProcess.init(mapList,fieldArray,JSONObject.fromObject(visChartBOWithBLOBs.getDefaultDataArray()));
            returnMap = chartProcess.conversionChart();

        }catch (Exception e){
            e.printStackTrace();
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }

        return returnMap;
    }



    @RequestMapping(value = "/getVisTemplate.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value="查询模板",notes = "", position = 0)
    public List<Map<String ,Object>> getVisTemplate(){
        List<Map<String,Object>> resultList = new ArrayList<>();
        List<VisTemplateBO> visualizationBOList = visualizationService.getVisTemplateList();
        if(visualizationBOList!=null&&visualizationBOList.size()!=0){
            for(int i=0;i<visualizationBOList.size();i++){
                Map<String,Object> resultMap = new HashMap<>();
                resultMap.put("id",visualizationBOList.get(i).getId());
                resultMap.put("url",visualizationBOList.get(i).getUrl());
                resultList.add(resultMap);
            }
        }
        return resultList;
    }
    @RequestMapping(value = "/saveVis.json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value="保存可视化信息",notes = "", position = 0)
    public Map<String,Object> saveVis(@RequestParam(value="name",required = true) String name,
                        @RequestParam(value = "tepmId",required = true) String tepmId,
                        @RequestParam(value = "projectId",required = true) String projectId){
        VisualizationBO vb = new VisualizationBO();
        vb.setName(name);
        vb.setProjectId(Integer.valueOf(projectId));
        int resultNum = visualizationService.addVisualization(vb);
        if(resultNum <= 0){
            throw new WebException(MySystemCode.SAVEVIS_EXCEPTION);
        }

        Map<String,Object> returnMap = new HashMap<>();

        returnMap.put("visId",vb.getId());
        returnMap.put("url","/visualization//toVisualizationPlatform.html?visId="+vb.getId());

        return returnMap;
    }
}
