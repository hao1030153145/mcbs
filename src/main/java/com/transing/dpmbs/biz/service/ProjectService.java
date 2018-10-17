package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.web.filter.ProjectCreateFilter;
import com.transing.dpmbs.web.filter.ProjectFilter;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;

import java.util.List;
import java.util.Map;

public interface ProjectService {
    List<Project> getProjectList(ProjectFilter projectFilter) throws BizException;

    ProjectOne getProjectInf(long projectId) throws BizException;

    long getProjectCount(ProjectFilter projectFilter) throws BizException;

    List<Manager> getProjectManager() throws BizException;

    List<Customer> getCustomerList() throws BizException;

    List<Status> getStatusList() throws BizException;

    Integer createProject(ProjectCreateFilter filter) throws BizException;

    Integer updateProject(ProjectCreateFilter filter) throws BizException;

    long selectProject(String projectName) throws BizException;

    Integer updateDelProject(String id) throws BizException;

    Integer startProject(String id) throws BizException;

    Integer stopProject(String id) throws BizException;

    Integer updateProjectStatus(ProjectStatusFilter filter) throws BizException;

    List<String> getTypeNoByProjectId(Long projectId)throws BizException;

    List<VisualizationBO> getVisualizationBOListByProjectId(Long projectId)throws BizException;

    void deleteProjectByProjectId(Long projectId) throws BizException;

    /**
     * 复制项目
     * @param projectId
     * @return
     * @throws BizException
     */
    Long copyProject(Long projectId,String projectName, String projectDescribe, String typeId, String managerId, String customerId, String startTime, String endTime)throws BizException;

}
