package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.constant.RedisKey;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.filter.ProjectCreateFilter;
import com.transing.dpmbs.web.filter.ProjectFilter;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowInfo;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("projectController")
@Api(value = "任务管理", description = "系统任务管理相关的访问接口", position = 3)
@RequestMapping(path = "/project")
public class ProjectController {
    @Resource
    private ProjectService projectService;
    @Resource
    private WorkFlowService workFlowService;
    @Resource
    private ProjectJobTypeService projectJobTypeService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;
    @Resource
    private BaseDaoRedis redisClient;

    private static final String PATH = "/export/getAllDataApi.json";

    @RequestMapping(value = "/projectCreatePage.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "新建项目界面", position = 0)
    public ModelAndView projectCreatePageHtml(@RequestParam(value = "projectId", required = false) @ApiParam(value = "项目") String projectId,
                                              @RequestParam(value = "isCopy", required = false) @ApiParam(value = "复制（1为复制，0为不是复制）") String isCopy,
                                              @RequestParam(value = "from", required = true) @ApiParam(value = "1为项目主页来的，0为其他上一步下一步来的或者新建", required = true) String from,
                                              HttpServletRequest req, HttpServletResponse res) {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("projectId", projectId);
        retMap.put("status", from);
        if(Validate.isEmpty(isCopy)){
            isCopy = "0";
        }
        retMap.put("isCopy", isCopy);
        req.setAttribute("mapJson", JSON.toJSONString(retMap));
        return new ModelAndView("projectManager/createPage/projectCreatePage");
    }

    @RequestMapping(value = "/projectHomePage.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "项目主页界面", position = 0)
    public ModelAndView projectHomePageHtml(@RequestParam(value = "projectId", required = false) @ApiParam(value = "项目id") String projectId,
                                            HttpServletRequest req, HttpServletResponse res) {
        Map<String, Object> retMap = new HashMap<String, Object>();

        retMap.put("projectId", projectId);
        List<Map<String, Object>> workFlowMapList = new ArrayList<>();

        long projectIdInt = Long.parseLong(projectId);

        List<ProjectJobTypeBO> projectJobTypeBOList = projectJobTypeService.getProjectJobTypeListByProjectId(projectIdInt);

        int status = -1;
        ProjectOne projectOne = projectService.getProjectInf(projectIdInt);
        if (projectOne == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }

        String projectStatus = projectOne.getStatus();

        if(null != projectJobTypeBOList && projectJobTypeBOList.size() > 0){
            out:for (int i = 0; i < projectJobTypeBOList.size(); i++) {
                ProjectJobTypeBO projectJobTypeBO1 = projectJobTypeBOList.get(i);

                String typeNo = projectJobTypeBO1.getTypeNo();

                Map<String, Object> workFlowMap = new HashMap<>();
                workFlowMap.put("typeNo", typeNo);
                workFlowMap.put("url", "/toModular.html?" + "from=1&toModular="+typeNo+"&projectId=" + projectId + "&typeNo=" + typeNo);
                workFlowMapList.add(workFlowMap);

                List<String> typeNoList = new ArrayList<>();
                typeNoList.add(typeNo);
                List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,projectIdInt);

                //如果项目 状态为待配置，或者配置中 才判断 配置中和 待启动 状态。
                if("0".equals(projectStatus) || "1".equals(projectStatus)){
                    int totalJobNum = workFlowDetailList.size();

                    if(totalJobNum > 0 && status == -1){
                        status = 2;
                    }else if(totalJobNum > 0 && status == 0){
                        status = 1;
                    }else if(totalJobNum > 0 && status == 2){
                        status = 2;
                    }else if(totalJobNum <= 0 && status == -1){
                        status = 0;
                    }else if(totalJobNum <= 0 && status == 2){
                        status = 1;
                    }else if(status == 1){
                        status = 1;
                    }
                }

            }
        }

        retMap.put("workFlowList", workFlowMapList);
        if(status != -1){
            ProjectStatusFilter filter = new ProjectStatusFilter();
            filter.setId(projectIdInt);
            filter.setStatus(status);
            projectService.updateProjectStatus(filter);
        }

        retMap.put("status", projectOne.getStatus());
        String retMapStr = JSON.toJSONString(retMap);

        req.setAttribute("mapJson", retMapStr);
        return new ModelAndView("projectManager/homePage/projectHomePage");
    }

    @RequestMapping(value = "/projectListPage.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "新建项目界面", position = 0)
    public ModelAndView projectListPageHtml(@RequestParam(value = "type",required = false,defaultValue = "page") String type,
                                            HttpServletRequest req, HttpServletResponse res) {
        /*Map<String, Object> retMap = new HashMap<String, Object>();
        req.setAttribute("result", retMap);*/
        if ( type.equals("vis")){
            return new ModelAndView("newProject/visualization/projectList");
        }else {
            return new ModelAndView("projectManager/listPage/projectListPage");
        }
    }

    @RequestMapping(value = "/getProjectList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取任务列表", notes = "", position = 0)
    public ProjectPo getProjectList(@RequestParam(value = "managerId", required = false) @ApiParam(value = "项⽬经理") String managerId,
                                    @RequestParam(value = "customerId", required = false) @ApiParam(value = "用户") String customerId,
                                    @RequestParam(value = "status", required = false) @ApiParam(value = "状态") String status,
                                    @RequestParam(value = "type", required = false) @ApiParam(value = "类型") String type,
                                    @RequestParam(value = "sortStatus", required = false) @ApiParam(value = "排序字段") String sortStatus,
                                    @RequestParam(value = "page", required = true) @ApiParam(value = "页数") String page,
                                    @RequestParam(value = "size", required = true) @ApiParam(value = "请求个数") String size,
                                    @RequestParam(value = "projectType", required = false) @ApiParam(value = "项目类型（vis 表示可视化项目，page 表示 原来的页面配置项目)不传则查询为page的") String projectType,
                                    @RequestParam(value = "projectName",required = false) @ApiParam(value = "项目名称") String projectName,
                                    HttpServletRequest req, HttpServletResponse res){
        ProjectPo projectPo = new ProjectPo();

        ProjectFilter projectFilter = new ProjectFilter();
        projectFilter.setManager(managerId);
        projectFilter.setCustomer(customerId);
        projectFilter.setStatus(status);
        projectFilter.setType(type);
        if (projectName ==null ||"".equals(projectName)){
            projectFilter.setName(projectName);
        }else{
            projectFilter.setName(projectName+"%");
        }
        if(Validate.isEmpty(projectType)){
            projectType = "page";
        }

        projectFilter.setProjectType(projectType);

        if (sortStatus != null) {
            projectFilter.setSortStatus(sortStatus.split("\\s+")[0]);
            projectFilter.setDirect(sortStatus.split("\\s+")[1]);
        }
        if (page != null) {
            projectFilter.setPage((Long.parseLong(page) - 1) * Long.parseLong(size));
        }
        if (size != null) {
            projectFilter.setSize(Integer.parseInt(size));
        }
        //获取查询到的Project列表
        List<Project> result = projectService.getProjectList(projectFilter);
        List<Map<String,Object>> mapList = new ArrayList<>();
        for(Project project : result){
            //定义一个map集合用于存放project和visstauts
            Map<String,Object> map = new HashMap<>();
            //查询typeNo列表
              List<String> typeNoList = new ArrayList<>();
            typeNoList.add(Constants.WORK_FLOW_TYPE_NO_STATISTICAL);
            List<WorkFlowDetail> workFlowDetailList =workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,project.getId());
            //定义一个字段visstauts
            String visstauts = "0";
            if(!Validate.isEmpty(workFlowDetailList)){
                int finishStatisticalNum = 0;
                for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
                    if(workFlowDetail.getJobStatus() == 2){
                        finishStatisticalNum++;
                    }
                }

                if(finishStatisticalNum >= workFlowDetailList.size()){
                    List<VisualizationBO> visList = projectService.getVisualizationBOListByProjectId(project.getId());
                    visstauts = "1";//表示可以可视化
                    if(!Validate.isEmpty(visList)){
                        for(VisualizationBO vis : visList) {
                            if (vis.getImage() != null) {
                                visstauts = "2";//表示已经可视化过
                            }else {
                                visstauts = "1";//表示可以可视化
                                break;
                            }
                        }
                    }
                }

            }
            try{
                Class clazz = project.getClass();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field:fields) {
                    field.setAccessible(true);
                    map.put(field.getName(),field.get(project));
                }
                map.put("visStatus",visstauts);
            }catch (Exception e){
                e.printStackTrace();
            }
            mapList.add(map);
        }
        long count = projectService.getProjectCount(projectFilter);
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
        projectPo.setProjectList(mapList);
        projectPo.setCount(count);
        return projectPo;
    }

    @RequestMapping(value = "/getProjectManagerList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取项目经理列表", notes = "", position = 0)
    public ManagerPo getProjectManagerList(HttpServletRequest req, HttpServletResponse res) {
        ManagerPo managerPo = new ManagerPo();
        List<Manager> result = projectService.getProjectManager();
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
        managerPo.setList(result);
        return managerPo;
    }

    @RequestMapping(value = "/getCustomerList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取用户列表", notes = "", position = 0)
    public CustomerPo getCustomerList(HttpServletRequest req, HttpServletResponse res) {
        CustomerPo customerPo = new CustomerPo();
        List<Customer> result = projectService.getCustomerList();
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
        customerPo.setList(result);
        return customerPo;
    }

    @RequestMapping(value = "/getStatusList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取类型列表", notes = "", position = 0)
    public StatusPo getStatusList(HttpServletRequest req, HttpServletResponse res) {
        StatusPo statusPo = new StatusPo();
        List<Status> result = projectService.getStatusList();
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
        statusPo.setList(result);
        return statusPo;
    }

    @RequestMapping(value = "/createProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "新建项目", notes = "", position = 0)
    public CreateProjectPo createProject(@RequestParam(value = "projectId", required = false) @ApiParam(value = "项目id") String projectId,
                                         @RequestParam(value = "from", required = true) @ApiParam(value = "1为项目主页来的，0为其他上一步下一步来的或者新建") String from,
                                         @RequestParam(value = "projectName", required = true) @ApiParam(value = "项目名称") String projectName,
                                         @RequestParam(value = "projectDescribe", required = false) @ApiParam(value = "项目描述") String projectDescribe,
                                         @RequestParam(value = "typeId", required = true) @ApiParam(value = "项目类型Id") String typeId,
                                         @RequestParam(value = "managerId", required = true) @ApiParam(value = "项目经理名称id") String managerId,
                                         @RequestParam(value = "customerId", required = true) @ApiParam(value = "项目经理名称id") String customerId,
                                         @RequestParam(value = "startTime", required = true) @ApiParam(value = "开始时间") String startTime,
                                         @RequestParam(value = "endTime", required = true) @ApiParam(value = "结束时间") String endTime,
                                         @RequestParam(value = "typeNos", required = true) @ApiParam(value = "typeNos 多个 英文逗号分割") String typeNos,
                                         @RequestParam(value = "projectType", defaultValue = "page") @ApiParam(value = "项目类型") String projectType,
                                         HttpServletRequest req, HttpServletResponse res) {

        if(Validate.isEmpty(typeNos)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        CreateProjectPo createProjectPo = new CreateProjectPo();
        ProjectCreateFilter filter;

        if (projectId != null) {

            long projectIdInt = Long.parseLong(projectId);
            filter = new ProjectCreateFilter(projectId, projectName, projectDescribe, typeId, managerId, customerId, startTime, endTime,projectType);

            ProjectOne projectOne = projectService.getProjectInf(projectIdInt);
            if(!projectOne.getProjectName().equals(filter.getProjectName())){
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

            filter = new ProjectCreateFilter(projectName, projectDescribe, typeId, managerId, customerId, startTime, endTime,projectType);
            Integer result = projectService.createProject(filter);
            if (result == null) {
                throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
            }

            //添加 workInfo信息
            workFlowService.  addWorkInfo(Long.parseLong(filter.getId()),typeNos);

        }

        String url;
        if (from.equals("1")) {
            //返回项目主页
            url = "/project/projectHomePage.html?projectId="+filter.getId();
        } else {
            //点击下一步进入导入页面
            url = "/toModular.html?" + "from=0&projectId=" + filter.getId();
        }
        createProjectPo.setUrl(String.valueOf(url));
        return createProjectPo;
    }

    @RequestMapping(value = "/getProjectInf.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取任务信息", position = 0)
    public Map<String,Object> getProjectInf(@RequestParam(value = "projectId", required = false) @ApiParam(value = "项目") String projectId,
                                            HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        ProjectOnePo projectOnePo = new ProjectOnePo();
        long projectIdInt = Long.parseLong(projectId);
        ProjectOne result = projectService.getProjectInf(projectIdInt);
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
        projectOnePo.setProjectOne(result);

        Map<String,Object> returnMap = new HashMap<>();
        Map<String,Object> projectMap = new HashMap<>();
        Class clazz = result.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if(null != fields && fields.length > 0){
            for (Field field:fields) {
                String fieldName = field.getName();
                field.setAccessible(true);
                Object object = new Object();
                try {
                    object = field.get(result);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                projectMap.put(fieldName,object);
            }
        }

        List<ProjectJobTypeBO> projectJobTypeBOList = projectJobTypeService.getProjectJobTypeListByProjectId(projectIdInt);
        String typeNos = "";
        if(!Validate.isEmpty(projectJobTypeBOList)){
            for (ProjectJobTypeBO projectJobTypeBO:projectJobTypeBOList) {
                typeNos += projectJobTypeBO.getTypeNo()+",";
            }
        }
        if(!Validate.isEmpty(typeNos)){
            typeNos = typeNos.substring(0,typeNos.length()-1);
        }

        projectMap.put("typeNos",typeNos);
        returnMap.put("projectOne",projectMap);

        return returnMap;
    }

    @RequestMapping(value = "/updateDelProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除项目", notes = "", position = 0)
    public DeleteProjectPo updateDelProject(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id") String projectId,
                                            HttpServletRequest req, HttpServletResponse res) {
        DeleteProjectPo deleteProjectPo = new DeleteProjectPo();
        Integer result = projectService.updateDelProject(projectId);
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
        return deleteProjectPo;
    }

    @RequestMapping(value = "/startProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "启动项目", notes = "", position = 0)
    public StartProjectPo startProject(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id") String projectId,
                                       HttpServletRequest req, HttpServletResponse res) {
        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);
        StartProjectPo startProjectPo = new StartProjectPo();
        //根据项目id查询project表的项目信息info
        ProjectOne projectOne = projectService.getProjectInf(projectIdInt);
        if(null == projectOne){
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }

        if(ProjectOne.PROJECTTYPE_PAGE.equals(projectOne.getProjectType())){
            //根据项目id查询project_job_type表的上下节点信息
            List<ProjectJobTypeBO> projectJobTypeBOList = projectJobTypeService.getProjectJobTypeListByProjectId(projectIdInt);
            for (ProjectJobTypeBO projectJobTypeBO:projectJobTypeBOList) {
                String typeNo = projectJobTypeBO.getTypeNo();//获取每一个节点编号
                List<String> typeNoList = new ArrayList<>();
                typeNoList.add(typeNo);
                if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                    typeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);
                }
                //根据项目id和当前节点编号查询work_flow_detail表下当前节点的详情信息
                List<WorkFlowDetail> detailList = workFlowService.getWorkFlowDetailListByTypeNoList(typeNoList,projectIdInt);
                if(Validate.isEmpty(detailList)){
                    throw new WebException(MySystemCode.BIZ_PROJECT_NOT_COMPLETE_EXCEPTION);
                }
            }
        }

        String status = projectOne.getStatus();
        boolean isSucc = false;
        if("5".equals(status)){//判断项目的状态是否为已完成(好像不可能等于5)
            isSucc = workFlowService.updateWorkFlowToRunningIfFinishByProjectId(projectIdInt);
        }else if("2".equals(status) || "4".equals(status) || "9".equals(status) ){//如果项目为未启动，停止，出错
            isSucc = workFlowService.updateWorkFlowToRunningByProjectId(projectIdInt);
        }

        if(isSucc){
            Integer result = projectService.startProject(projectId);
            if (result == null || result <= 0) {
                throw new WebException(MySystemCode.BIZ_START_PROJECT_EXCEPTION);
            }
        }else {
            throw new WebException(MySystemCode.BIZ_START_PROJECT_EXCEPTION);
        }

        return startProjectPo;
    }

    @RequestMapping(value = "/copyProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "复制项目", notes = "", position = 0)
    public CopyProjectPo copyProject(@RequestParam(value = "projectId", required = false) @ApiParam(value = "项目id") String projectId,
                                     @RequestParam(value = "projectName", required = true) @ApiParam(value = "项目名称") String projectName,
                                     @RequestParam(value = "projectDescribe", required = false) @ApiParam(value = "项目描述") String projectDescribe,
                                     @RequestParam(value = "typeId", required = true) @ApiParam(value = "项目类型Id") String typeId,
                                     @RequestParam(value = "managerId", required = true) @ApiParam(value = "项目经理名称id") String managerId,
                                     @RequestParam(value = "customerId", required = true) @ApiParam(value = "项目经理名称id") String customerId,
                                     @RequestParam(value = "startTime", required = true) @ApiParam(value = "开始时间") String startTime,
                                     @RequestParam(value = "endTime", required = true) @ApiParam(value = "结束时间") String endTime,
                                     HttpServletRequest req, HttpServletResponse res) {

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        Long projectIdInt = Long.parseLong(projectId);

        Long newProjectId = projectService.copyProject(projectIdInt,projectName,projectDescribe,typeId,managerId,customerId,startTime,endTime);
        if(null == newProjectId){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        String url = "/project/projectHomePage.html?projectId="+newProjectId;
        CopyProjectPo copyProjectPo = new CopyProjectPo();
        copyProjectPo.setUrl(url);
        copyProjectPo.setProjectId(newProjectId);
        return copyProjectPo;
    }

    @RequestMapping(value = "/stopProject.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "停止项目", notes = "", position = 0)
    public StopProjectPo stopProject(@RequestParam(value = "projectId", required = true) @ApiParam(value = "项目id") String projectId,
                                     HttpServletRequest req, HttpServletResponse res) {
        StopProjectPo stopProjectPo = new StopProjectPo();
        Integer result = projectService.stopProject(projectId);
        if (result == null) {
            throw new WebException(SystemCode.SYS_CONTROLLER_EXCEPTION);
        }
        long projectIdInt = Long.parseLong(projectId);
        workFlowService.updateWorkFlowToStopByProjectId(projectIdInt);
        return stopProjectPo;
    }


    @RequestMapping(value = "/getApi.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询项目数据API", notes = "", position = 0)
    public Map<String,Object> getApi(@RequestParam(value = "projectId") @ApiParam(value = "项目id") String projectId){
        Map<String,Object> resultMap = new HashMap<>();
         List<WorkFlowDetail> workFlowDetailList = workFlowService.getWorkFlowDetailByProjectId(Long.parseLong(projectId));
         String dataSourceType = "";
         for(WorkFlowDetail workFlowDetail : workFlowDetailList){
             String typeNo = workFlowDetail.getTypeNo();
             if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                 dataSourceType = workFlowDetail.getDataSourceType();
                 break;
             }
         }
         if(!Validate.isEmpty(dataSourceType)){
             StorageTypePO storageTypePO= dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(dataSourceType));
             String storageTable = storageTypePO.getStorageTypeTable();
             String url = PATH+"?projectId="+projectId+"&storageTable="+storageTable;
             resultMap.put("url",url);
            return resultMap;
         }else{
             return null;
         }
    }
}
