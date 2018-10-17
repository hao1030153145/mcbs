/**
 * @project: dpmbs
 * @Title: UserController.java
 * @Package: com.transing.dpmbs.web.controller
 * <p>
 * Copyright (c) 2014-2017 Jeeframework Limited, Inc.
 * All rights reserved.
 */
package com.transing.workflow.web.controller;

import com.alibaba.fastjson.JSON;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.integration.bo.ParamBO;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowTemplateService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.WorkFlowNodeBO;
import com.transing.workflow.integration.bo.WorkFlowTemplateBO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("workFLowTemplateController")
@Api(value = "工作流模板", description = "工作流模板访问接口", position = 2)
@RequestMapping(path = "/workFlowTemplate")
public class WorkFLowTemplateController {

    @Resource
    private WorkFlowTemplateService workFlowTemplateService;

    @Resource
    private JobTypeService jobTypeService;
    private String uploadPath = WebUtil.getDpmbsUploadByEnv();

    /**
     * 这个是跳转到非可视化模板列表的跳转接口
     * @param req
     * @param res
     * @return
     */
    @RequestMapping(value = "/toWorkFlowTemplateList.html", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView toWorkFlow(HttpServletRequest req, HttpServletResponse res) {

        String html = "workFlowTemplateManager/listPage/workFlowTemplateList";
        return new ModelAndView(html);
    }

    /**
     * 这个是跳转到可视化模板列表的跳转接口
     * @param req
     * @param res
     * @return
     */
    @RequestMapping(value = "/toVisWorkFlowTemplateList.html",method = RequestMethod.GET)
    @ResponseBody
    public  ModelAndView toVisTemplateList(HttpServletRequest req, HttpServletResponse res){
        String html = "newProject/visualization/templateList";
        return  new ModelAndView(html);
    }

    @RequestMapping(value = "/deleteWorkFlowTemplate.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "逻辑删除可视化工作流模板",position = 0)
    public DeleteProjectPo logicDeleteVisWorkFlowTemplateByIds(@RequestParam(value = "templateId", required = true) @ApiParam(value = "模板的id") String templateId,
                                                               HttpServletRequest req, HttpServletResponse res){

        if(Validate.isEmpty(templateId)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        DeleteProjectPo deleteProjectPo = new DeleteProjectPo();
        System.out.print("123");
        List<Integer> list = new ArrayList<Integer>();
        String[] nums=templateId.split(",");
        for(int i=0;i<nums.length;i++){
            if (templateId.matches("\\d+")){
                list.add(Integer.parseInt(nums[i]));
            }
        }
        Integer result = workFlowTemplateService.logicDeleteVisWorkFlowTemplateByIds(list);
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
        return deleteProjectPo;
    }

    @RequestMapping(value = "/updateVisWorkFlowTemplate.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "编辑可视化工作流模板", position = 0)
    public WorkFlowTemplateBO updateVisWorkFlowTemplate(@RequestParam(value = "templateId") @ApiParam(value = "模板id") String templateId) {
        WorkFlowTemplateBO projectOne = workFlowTemplateService.getWorkFlowTemplateListById(Integer.parseInt(templateId));
        return projectOne;
    }

    /**
     * 不分页的查询可视化模板列表
     * @param name
     * @param createTime
     * @param status
     * @param res
     * @param req
     * @return
     */
    @RequestMapping(value = "/getWorkFlowTemplateListNoPage.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "不分页查询可视化模板列表", position = 0)
    public WorkFlowTemplateListPO getWorkFlowTemplateList(@RequestParam(value = "workFlowTemplateName", required = false) @ApiParam(value = "模板名称", required = false) String name,
                                                          @RequestParam(value = "createTime",required = false) @ApiParam(value = "创建时间", required = false) String createTime,
                                                          @RequestParam(value = "status",required = false) @ApiParam(value = "状态", required = false) String status,
                                                          HttpServletResponse res, HttpServletRequest req){

        Map<String, Object> param = new HashMap<>();

        if (name != null && "" != name) {
            param.put("name", name);
        }
        if (status != null && "" != status){
            param.put("status", status);
        }
        if (createTime != null && "" != createTime) {
            param.put("createTime", createTime);
        }
        WorkFlowTemplateListPO workFlowTemplateListPO = new WorkFlowTemplateListPO();
        List<WorkFlowTemplatePO> list = workFlowTemplateService.getWorkFlowTemplateListByCondition(param);
        for(WorkFlowTemplatePO workFlowTemplatePO : list){
            workFlowTemplatePO.setImgUrl(uploadPath+workFlowTemplatePO.getImgUrl());
        }
        workFlowTemplateListPO.setWorkFlowTemplateList(list);
        return  workFlowTemplateListPO;
    }


    /**
     * 分页查询非可视化的模板列表
     * @param page
     * @param size
     * @param name
     * @param req
     * @param res
     * @return
     */
    @RequestMapping(value = "/getWorkFlowTemplateList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "分页查询非可视化的模板列表",position = 0)
    public WorkFlowTemplateListPO acceptCallback(@RequestParam(value = "page",required = false)@ApiParam(value = "page ",required = true) String page,
                                                 @RequestParam(value = "size",required = false)@ApiParam(value = "size",required = true) String size,
                                                 @RequestParam(value = "name",required = false)@ApiParam(value = "name",required = true) String name,
                                                 HttpServletRequest req, HttpServletResponse res){

        int sizeInt = 15;
        if(!Validate.isEmpty(size) && size.matches("\\d+")){
            sizeInt = Integer.parseInt(size);
        }

        int pageInt = 0;
        if(!Validate.isEmpty(page) && page.matches("\\d+")){
            pageInt = Integer.parseInt(page);
            pageInt = (pageInt-1) * sizeInt;
        }

        List<WorkFlowTemplateBO> workFlowTemplateBOList = workFlowTemplateService.getWorkFlowTemplateListByParam(null,name,pageInt,sizeInt);
        // 获得非可视化模板列表的数目
        int count = workFlowTemplateService.getWorkFlowTemplateCountByParam(null,name);

        WorkFlowTemplateListPO workFlowTemplateListPO = new WorkFlowTemplateListPO();
        workFlowTemplateListPO.setCount(count);

        List<WorkFlowTemplatePO> workFlowTemplatePOList = new ArrayList<>();
        if(!Validate.isEmpty(workFlowTemplateBOList)){
            for (WorkFlowTemplateBO workFlowTemplateBO:workFlowTemplateBOList) {
                WorkFlowTemplatePO workFlowTemplatePO = new WorkFlowTemplatePO();
                workFlowTemplatePO.setId(workFlowTemplateBO.getId());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if(workFlowTemplateBO.getLastmodifyTime() == null){
                        workFlowTemplatePO.setLastmodifyTime(new Date());
                    }else {
                        workFlowTemplatePO.setLastmodifyTime(sdf.parse(workFlowTemplateBO.getLastmodifyTime()));
                    }
                    if (workFlowTemplateBO.getLastmodifyTime() == null){
                        workFlowTemplatePO.setCreateTime(new Date());
                    }else {
                        workFlowTemplatePO.setCreateTime(sdf.parse(workFlowTemplateBO.getCreateTime()));
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                workFlowTemplatePO.setFlowType(workFlowTemplateBO.getFlowType());
//                workFlowTemplatePO.setLastmodifyTime(workFlowTemplateBO.getLastmodifyTime());
                workFlowTemplatePO.setName(workFlowTemplateBO.getName());
                String process = workFlowTemplateBO.getProcess();
                if(!Validate.isEmpty(process) && JSONUtils.mayBeJSON(process)){
                    workFlowTemplatePO.setProcess(JSONArray.fromObject(process));
                }

                int status = workFlowTemplateBO.getStatus();
                workFlowTemplatePO.setStatus(status);

                String statusName = "";
                if(status == 1){
                    statusName = "生效";
                }else if(status == 0){
                    statusName = "失效";
                }

                workFlowTemplatePO.setStatusName(statusName);

                workFlowTemplatePOList.add(workFlowTemplatePO);

            }
        }

        workFlowTemplateListPO.setWorkFlowTemplateList(workFlowTemplatePOList);

        return workFlowTemplateListPO;

    }

    /**
     * 分页查询可视化的模板列表
     * @param page
     * @param size
     * @param workFlowTemplateName
     * @param createTime
     * @param res
     * @param req
     * @return
     */
    @RequestMapping(value = "/getVisWorkFlowTemplateList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "分页查询可视化的模板列表",position = 0)
    public WorkFlowTemplateListPO acceptVisCallback(@RequestParam(value = "page",required = false)@ApiParam(value = "page ",required = true) String page,
                                                    @RequestParam(value = "size",required = false)@ApiParam(value = "size",required = true) String size,
                                                    @RequestParam(value = "workFlowTemplateName",required = false)@ApiParam(value = "name",required = true) String workFlowTemplateName,
                                                    @RequestParam(value = "createTime",required = false)@ApiParam(value = "createTime",required = true) String createTime,
                                                    HttpServletResponse res, HttpServletRequest req ){
        String endTime = "";
        int sizeInt = 10;
        if(!Validate.isEmpty(size) && size.matches("\\d+")){
            sizeInt = Integer.parseInt(size);
        }

        int pageInt = 0;
        if(!Validate.isEmpty(page) && page.matches("\\d+")){
            pageInt = Integer.parseInt(page);
            pageInt = (pageInt-1) * sizeInt;
        }

        if (!Validate.isEmpty(createTime)){
            endTime = createTime + " 23:59:59";
            createTime = createTime + " 00:00:00";
        }


        List<WorkFlowTemplateBO> visWorkFlowTemplateBOList = workFlowTemplateService.getVisWorkFlowTemplateListByParam(null,workFlowTemplateName,pageInt,sizeInt,createTime,endTime);
        // 获得可视化模板列表的数目
        int count = workFlowTemplateService.getVisWorkFlowTemplateCountByParam(null,workFlowTemplateName,createTime,endTime);

        WorkFlowTemplateListPO visWorkFlowTemplateListPO = new WorkFlowTemplateListPO();
        visWorkFlowTemplateListPO.setCount(count);

        List<WorkFlowTemplatePO> visWorkFlowTemplatePOList = new ArrayList<>();
        if(!Validate.isEmpty(visWorkFlowTemplateBOList)){
            for (WorkFlowTemplateBO visWorkFlowTemplateBO:visWorkFlowTemplateBOList) {
                WorkFlowTemplatePO visWorkFlowTemplatePO = new WorkFlowTemplatePO();
                visWorkFlowTemplatePO.setId(visWorkFlowTemplateBO.getId());
                // 下面是把返回的字符串类型的时间传话为date类型
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    System.out.print("123");
                    visWorkFlowTemplatePO.setCreateTime(sdf.parse(visWorkFlowTemplateBO.getCreateTime()));
                    visWorkFlowTemplatePO.setLastmodifyTime(sdf.parse(visWorkFlowTemplateBO.getLastmodifyTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                visWorkFlowTemplatePO.setFlowType(visWorkFlowTemplateBO.getFlowType());
                visWorkFlowTemplatePO.setName(visWorkFlowTemplateBO.getName());
                visWorkFlowTemplatePO.setImgUrl(uploadPath+visWorkFlowTemplateBO.getImgUrl());
                String process = visWorkFlowTemplateBO.getProcess();
                if(!Validate.isEmpty(process) && JSONUtils.mayBeJSON(process)){
                    visWorkFlowTemplatePO.setProcess(JSONArray.fromObject(process));
                }

                int status = visWorkFlowTemplateBO.getStatus();
                visWorkFlowTemplatePO.setStatus(status);

                String statusName = "";
                if(status == 1){
                    statusName = "生效";
                }else if(status == 0){
                    statusName = "失效";
                }

                visWorkFlowTemplatePO.setStatusName(statusName);

                visWorkFlowTemplatePOList.add(visWorkFlowTemplatePO);

            }
        }

        visWorkFlowTemplateListPO.setWorkFlowTemplateList(visWorkFlowTemplatePOList);

        return visWorkFlowTemplateListPO;

    }

    @RequestMapping(value = "/toDetail.html", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView toDetail(@RequestParam(value = "workTemplateId",required = false)@ApiParam("抓取配置模板id")String workTemplateId,
            HttpServletRequest req, HttpServletResponse res) {

        Map<String,Object> param = new HashMap<>();

        param.put("workFlowTemplateId",workTemplateId);

        req.setAttribute("data",JSON.toJSONString(param));

        String html = "process/processConfigure";
        return new ModelAndView(html);
    }

    @RequestMapping(value = "/getDetail.json", method = RequestMethod.GET)
    @ResponseBody
    public WorkFlowTemplateDetailPO getDetail(@RequestParam(value = "workTemplateId")@ApiParam("抓取配置模板id")String workTemplateId){

        if(Validate.isEmpty(workTemplateId) || !workTemplateId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int workTemplateIdInt = Integer.parseInt(workTemplateId);

        WorkFlowTemplateDetailPO workFlowTemplateDetailPO = new WorkFlowTemplateDetailPO();
        WorkFlowTemplateBO workFlowTemplateBO = workFlowTemplateService.getWorkFlowTemplateListById(workTemplateIdInt);
        if(null != workFlowTemplateBO){

            workFlowTemplateDetailPO.setId(workFlowTemplateBO.getId());
            workFlowTemplateDetailPO.setTemplateName(workFlowTemplateBO.getName());
            List<WorkFlowNodeBO> workFlowNodeBOList = workFlowTemplateService.getWorkFlowNodeListByTemplateId(workTemplateIdInt);
            List<WorkFlowNodePO> workFlowInfoList = new ArrayList<>();
            for (WorkFlowNodeBO workFlowNodeBO:workFlowNodeBOList) {
                WorkFlowNodePO workFlowNodePO = new WorkFlowNodePO();
                workFlowNodePO.setFlowId(workFlowNodeBO.getFlowId());
                workFlowNodePO.setName(workFlowNodeBO.getName());
                workFlowNodePO.setTypeNo(workFlowNodeBO.getTypeNo());
                String nodeParamStr = workFlowNodeBO.getNodeParam();

                if(!Validate.isEmpty(nodeParamStr) && JSONUtils.mayBeJSON(nodeParamStr)){
                    workFlowNodePO.setNodeParam(JSONObject.fromObject(nodeParamStr));
                }
                workFlowInfoList.add(workFlowNodePO);
            }

            workFlowTemplateDetailPO.setWorkFlowInfoList(workFlowInfoList);

        }


        return workFlowTemplateDetailPO;

    }

    @RequestMapping(value = "/saveDetail.json", method = RequestMethod.POST)
    @ResponseBody
    public void saveDetail(@RequestParam(value = "jsonParam")@ApiParam("json数据")String jsonParam){
        if(Validate.isEmpty(jsonParam)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        WorkFlowTemplateDetailPO workFlowTemplateDetailPO = null;
        try {
            workFlowTemplateDetailPO = JSON.parseObject(jsonParam,WorkFlowTemplateDetailPO.class);
        }catch (Exception e){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int i =workFlowTemplateService.saveWorkFlowTemplateDeial(workFlowTemplateDetailPO);
        if(i <= 0){
            throw new WebException(MySystemCode.SAVEVIS_EXCEPTION);
        }

    }

    @RequestMapping(value = "/delWorkFlowTemplate.json", method = RequestMethod.POST)
    @ResponseBody
    public void delWorkFlowTemplate(@RequestParam(value = "workFlowTemplateId")@ApiParam("workFlowTemplateId")String workFlowTemplateId){

        if(Validate.isEmpty(workFlowTemplateId) || !workFlowTemplateId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        int workFlowTemplateIdInt = Integer.parseInt(workFlowTemplateId);

        int i = workFlowTemplateService.deleteWorkFlowTemplateDetail(workFlowTemplateIdInt);

        if(i <= 0){
            throw new WebException(MySystemCode.BIZ_DELETE_EXCEPTION);
        }

    }

    @RequestMapping(value = "/enableWorkFlowTemplate.json", method = RequestMethod.POST)
    @ResponseBody
    public void enableWorkFlowTemplate(@RequestParam(value = "workFlowTemplateId")@ApiParam("workFlowTemplateId")String workFlowTemplateId){

        if(Validate.isEmpty(workFlowTemplateId) || !workFlowTemplateId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        int workFlowTemplateIdInt = Integer.parseInt(workFlowTemplateId);


        WorkFlowTemplateBO workFlowTemplateBO = new WorkFlowTemplateBO();
        workFlowTemplateBO.setId(workFlowTemplateIdInt);
        workFlowTemplateBO.setStatus(WorkFlowTemplateBO.STATUS_VALID);
        int i = workFlowTemplateService.updateWorkTemplate(workFlowTemplateBO);

        if(i <= 0){
            throw new WebException(MySystemCode.ACTION_EXCEPTION);
        }

    }

    @RequestMapping(value = "/disabledWorkFlowTemplate.json", method = RequestMethod.POST)
    @ResponseBody
    public void disabledWorkFlowTemplate(@RequestParam(value = "workFlowTemplateId")@ApiParam("workFlowTemplateId")String workFlowTemplateId){

        if(Validate.isEmpty(workFlowTemplateId) || !workFlowTemplateId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        int workFlowTemplateIdInt = Integer.parseInt(workFlowTemplateId);


        WorkFlowTemplateBO workFlowTemplateBO = new WorkFlowTemplateBO();
        workFlowTemplateBO.setId(workFlowTemplateIdInt);
        workFlowTemplateBO.setStatus(WorkFlowTemplateBO.STATUS_NOT_VALID);
        int i = workFlowTemplateService.updateWorkTemplate(workFlowTemplateBO);

        if(i <= 0){
            throw new WebException(MySystemCode.ACTION_EXCEPTION);
        }

    }

    @RequestMapping(value = "/getWorkFlowTypeList.json", method = RequestMethod.GET)
    @ResponseBody
    public List<ParamBO> getWorkFlowTypeList(){

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
    @RequestMapping(value="/saveWorkFlowTemplateNodeInfo.json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存可视化工作流模板节点配置信息", position = 0)
    public List<Map<String, Object>> saveWorkFlowTemplateNodeInfo(@RequestParam(value = "body") String body){
        List<Map<String, Object>> reslutMap = workFlowTemplateService.addVisWorkFlowTemplateNodeParam(body);
        return reslutMap;
    }
}