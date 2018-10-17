package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.ProjectService;
import com.transing.dpmbs.integration.ProjectDataService;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.web.filter.ProjectCreateFilter;
import com.transing.dpmbs.web.filter.ProjectFilter;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.WorkFlowDataService;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("projectService")
public class ProjectServicePojo extends BaseService implements ProjectService {
    @Resource
    private ProjectDataService projectDataService;

    @Resource
    private WorkFlowDataService workFlowDataService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @Override
    public List<Project> getProjectList(ProjectFilter projectFilter) throws BizException {
        try {
            List<Project> projectList = projectDataService.getProjectList(projectFilter);
            List<Manager> managerList = projectDataService.getProjectManagerList();
            List<Customer> customerList = projectDataService.getCustomerList();
            List<Status> typeList = projectDataService.getStatusList();
            Map<String, String> managerMap = new HashMap<>();
            Map<String, String> customerMap = new HashMap<>();
            Map<String, String> typeMap = new HashMap<>();
            for (Manager manager : managerList) {
                managerMap.put(String.valueOf(manager.getId()), manager.getUsername());
            }
            for (Customer customer : customerList) {
                customerMap.put(String.valueOf(customer.getId()), customer.getName());
            }
            for (Status status : typeList) {
                typeMap.put(String.valueOf(status.getId()), status.getName());
            }
            for (Project project : projectList) {
                project.getStartTime();
                project.getEndTime();
                project.setManager(managerMap.get(project.getManager()));
                project.setCustomer(customerMap.get(project.getCustomer()));
                project.setType(typeMap.get(project.getType()));
            }
            return projectList;
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public ProjectOne getProjectInf(long projectId) throws BizException {
        try {
            Project project = projectDataService.getProjectInf(projectId);
            if (project == null) {
                return null;
            }
            ProjectOne projectOne = new ProjectOne(project.getId(), project.getName(), project.getDescribes(), project.getManager(), project.getCustomer(), project.getStartTime(), project.getEndTime(), project.getType(),project.getStatus(),project.getProjectType());
            Manager manager = projectDataService.getProjectManager(project.getManager());
            Customer customer = projectDataService.getCustomer(project.getCustomer());
            Status status = projectDataService.getStatus(project.getType());
            if (customer != null) {
                projectOne.setCustomerName(customer.getName());
            }
            if (manager != null) {
                projectOne.setManagerName(manager.getUsername());
            }
            if (status != null) {
                projectOne.setTypeName(status.getName());
            }
            return projectOne;
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }


    @Override
    public long getProjectCount(ProjectFilter projectFilter) throws BizException {
        try {
            return projectDataService.getProjectCount(projectFilter);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public List<Manager> getProjectManager() throws BizException {
        try {
            return projectDataService.getProjectManagerList();
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public List<Customer> getCustomerList() throws BizException {
        try {
            return projectDataService.getCustomerList();
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public List<Status> getStatusList() throws BizException {
        try {
            return projectDataService.getStatusList();
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public Integer createProject(ProjectCreateFilter filter) throws BizException {
        try {
            return projectDataService.createProject(filter);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public Integer updateProject(ProjectCreateFilter filter) throws BizException {
        try {
            return projectDataService.updateProject(filter);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public long selectProject(String projectName) throws BizException {
        try {
            return projectDataService.selectProject(projectName);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public Integer updateDelProject(String id) throws BizException {
        try {
            return projectDataService.updateDelProject(Long.parseLong(id));
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public Integer startProject(String id) throws BizException {
        try {
            return projectDataService.startProject(Long.parseLong(id));
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public Integer stopProject(String id) throws BizException {
        try {

            return projectDataService.stopProject(Long.parseLong(id));
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public Integer updateProjectStatus(ProjectStatusFilter filter) throws BizException {
        try {
            return projectDataService.updateProjectStatus(filter);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public List<VisualizationBO> getVisualizationBOListByProjectId(Long projectId) throws BizException {
        try {
            return projectDataService.getVisualizationBOListByProjectId(projectId);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    @Transactional
    public Long copyProject(Long projectId,String projectName, String projectDescribe, String typeId, String managerId, String customerId, String startTime, String endTime) throws BizException {

        Long newProjectId = null;
        try {

            Project project = projectDataService.getProjectInf(projectId);

            ProjectCreateFilter filter = new ProjectCreateFilter(projectName,projectDescribe,typeId,managerId,customerId,startTime,endTime,project.getProjectType());

            String status = project.getStatus();
            if(status.equals("3")
                    ||status.equals("4")
                    ||status.equals("5")
                    ||status.equals("9")){
                status = "2";
            }
            filter.setStatus(status);
            projectDataService.createProject(filter);

            newProjectId = Long.parseLong(filter.getId());

            List<ProjectJobTypeBO> projectJobTypeBOList = projectJobTypeService.getProjectJobTypeListByProjectId(projectId);

            List<String> typeNos = new ArrayList<>();

            for (ProjectJobTypeBO projectJobTypeBO:projectJobTypeBOList) {
                typeNos.add(projectJobTypeBO.getTypeNo());
            }
            projectJobTypeService.addProjectJobType(typeNos,newProjectId);
            List<WorkFlowDetail> workFlowDetailList = workFlowDataService.getWorkFlowDetailByProjectId(projectId);

            List<WorkFlowParam> workFlowParamList = workFlowDataService.getWorkFlowParamByProJectId(projectId);
            Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
            for (WorkFlowParam workFlowParam:workFlowParamList) {
                workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
            }

            Map<Long,Long> oldAndNewDetailMap = new HashMap<>();
            Map<Long,WorkFlowParam> newParamMap = new HashMap<>();
            Map<Long,WorkFlowDetail> newDetailMap = new HashMap<>();
            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {

                WorkFlowDetail workFlowDetailTemp = new WorkFlowDetail();
                workFlowDetailTemp.setDataSourceType(workFlowDetail.getDataSourceType());
                workFlowDetailTemp.setProjectId(Long.parseLong(filter.getId()));
                workFlowDetailTemp.setQuartzTime(workFlowDetail.getQuartzTime());
                workFlowDetailTemp.setTypeNo(workFlowDetail.getTypeNo());
                workFlowDetailTemp.setFlowId(workFlowDetail.getFlowId());

                workFlowDataService.addWorkFlowDetail(workFlowDetailTemp);

                newDetailMap.put(workFlowDetailTemp.getFlowDetailId(),workFlowDetailTemp);

                WorkFlowParam workFlowParam = workFlowParamMap.get(workFlowDetail.getFlowDetailId());

                JSONObject jsonObject = JSONObject.fromObject(workFlowParam.getJsonParam());

                if(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT.equals(workFlowParam.getTypeNo())){
                    jsonObject.put("count","0");
                    jsonObject.put("projectid",""+newProjectId);
                }else if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(workFlowParam.getTypeNo())
                        ||Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(workFlowParam.getTypeNo())){
                    jsonObject.put("count",0);
                }

                workFlowParam.setJsonParam(jsonObject.toString());

                WorkFlowParam workFlowParamTemp = new WorkFlowParam();
                workFlowParamTemp.setFlowId(workFlowParam.getFlowId());
                workFlowParamTemp.setTypeNo(workFlowParam.getTypeNo());
                workFlowParamTemp.setJsonParam(workFlowParam.getJsonParam());
                workFlowParamTemp.setFlowDetailId(workFlowDetailTemp.getFlowDetailId());
                workFlowParamTemp.setParamType(workFlowParam.getParamType());
                workFlowParamTemp.setProjectId(Long.parseLong(filter.getId()));
                workFlowDataService.addWorkFlowParam(workFlowParamTemp);

                newParamMap.put(workFlowParamTemp.getFlowDetailId(),workFlowParamTemp);
                oldAndNewDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetailTemp.getFlowDetailId());
            }

            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {

                Long newDetailId = oldAndNewDetailMap.get(workFlowDetail.getFlowDetailId());

                //如果typeNo是统计，则更新detailId
                if(Constants.WORK_FLOW_TYPE_NO_STATISTICAL.equals(workFlowDetail.getTypeNo())){
                    WorkFlowParam workFlowParam = newParamMap.get(newDetailId);
                    JSONObject jsonObject = JSONObject.fromObject(workFlowParam.getJsonParam());

                    JSONArray jsonArray = jsonObject.getJSONObject("jsonParam").getJSONArray("dataType");
                    for (Object object:jsonArray) {
                        JSONObject obj = (JSONObject) object;

                        JSONArray detailIdArray = obj.getJSONArray("detailId");

                        JSONArray newDetailIdArray = new JSONArray();
                        for (Object objec:detailIdArray) {
                            String detail = (String) objec;

                            Long detailIdInt = Long.parseLong(detail);
                            Long newDetailIdInt = oldAndNewDetailMap.get(detailIdInt);

                            newDetailIdArray.add(Long.toString(newDetailIdInt));
                        }

                        obj.put("detailId",newDetailIdArray);
                    }

                    jsonObject.getJSONObject("jsonParam").put("dataType",jsonArray);

                    workFlowParam.setJsonParam(jsonObject.toString());
                    workFlowDataService.updateWorkFlowParam(workFlowParam);

                }

                WorkFlowDetail newWorkFlowDetail = newDetailMap.get(newDetailId);

                String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();

                String newPrevFlowDetailIds = "";
                if(!"0".equals(prevFlowDetailIds.trim())){
                    String [] preFlowDetailIdArray = prevFlowDetailIds.split(",");
                    for (String preFlowDetailId:preFlowDetailIdArray) {
                        Long preFlowDetailIdInt = Long.parseLong(preFlowDetailId);
                        Long newPreDetailId = oldAndNewDetailMap.get(preFlowDetailIdInt);
                        newPrevFlowDetailIds += newPreDetailId+",";
                    }

                    if(newPrevFlowDetailIds.length() > 0){
                        newPrevFlowDetailIds = newPrevFlowDetailIds.substring(0,newPrevFlowDetailIds.length()-1);
                    }

                }else {
                    newPrevFlowDetailIds = "0";
                }
                newWorkFlowDetail.setPrevFlowDetailIds(newPrevFlowDetailIds);


                String newNextFlowDetailIds = "";
                String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();

                if(!Validate.isEmpty(nextFlowDetailIds)){
                    String [] nextFlowDetailIdArray = nextFlowDetailIds.split(",");
                    for (String nextFlowDetailId:nextFlowDetailIdArray) {
                        Long nextFlowDetailIdInt = Long.parseLong(nextFlowDetailId);
                        Long newNextDetailId = oldAndNewDetailMap.get(nextFlowDetailIdInt);
                        newNextFlowDetailIds += newNextDetailId + ",";
                    }

                    if(newNextFlowDetailIds.length() > 0){
                        newNextFlowDetailIds = newNextFlowDetailIds.substring(0,newNextFlowDetailIds.length()-1);
                    }
                }

                newWorkFlowDetail.setNextFlowDetailIds(newNextFlowDetailIds);

                workFlowDataService.updateWorkFlowDetail(newWorkFlowDetail);

            }

        } catch (DAOException e) {
            throw new BizException(e);
        }

        return newProjectId;
    }

    @Override
    public List<String> getTypeNoByProjectId(Long projectId) throws BizException {
        try{
            return projectDataService.getTypeNoByProjectId(projectId);
        }catch (DAOException e){
            throw new BizException(e);
        }
    }

    @Override
    public void deleteProjectByProjectId(Long projectId) throws BizException {
        try{
            projectDataService.deleteProjectByProjectId(projectId);
        }catch (DAOException e){
            throw new BizException(e);
        }
    }
}
