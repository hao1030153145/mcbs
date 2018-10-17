package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.web.filter.ProjectCreateFilter;
import com.transing.dpmbs.web.filter.ProjectFilter;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;

import java.util.List;
import java.util.Map;

public interface ProjectDataService {
    List<Project> getProjectList(ProjectFilter projectFilter) throws DataServiceException;

    Project getProjectInf(long projectId) throws DataServiceException;

    long getProjectCount(ProjectFilter projectFilter) throws DataServiceException;

    List<Manager> getProjectManagerList() throws DataServiceException;

    List<Customer> getCustomerList() throws DataServiceException;

    List<Status> getStatusList() throws DataServiceException;

    Manager getProjectManager(String id) throws DataServiceException;

    Customer getCustomer(String id) throws DataServiceException;

    Status getStatus(String id) throws DataServiceException;

    Integer createProject(ProjectCreateFilter filter) throws DataServiceException;

    Integer updateProject(ProjectCreateFilter filter) throws DataServiceException;

    long selectProject(String projectName) throws DataServiceException;

    Integer updateDelProject(long id) throws DataServiceException;

    Integer startProject(long id) throws DataServiceException;

    Integer stopProject(long id) throws DataServiceException;

    Integer updateProjectStatus(ProjectStatusFilter filter) throws DataServiceException;

    List<String> getTypeNoByProjectId(Long projectId)throws BizException;

    List<VisualizationBO> getVisualizationBOListByProjectId(Long projectId)throws BizException;

    void deleteProjectByProjectId(Long projectId) throws BizException;
}
