package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.ProjectDataService;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.web.filter.ProjectCreateFilter;
import com.transing.dpmbs.web.filter.ProjectFilter;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Scope("prototype")
@Repository("projectDataService")
public class ProjectDAOibatis extends BaseDaoiBATIS implements ProjectDataService {

    @Override
    public List<Project> getProjectList(ProjectFilter projectFilter) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("projectMapper.getProjectList", projectFilter);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Project getProjectInf(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("projectMapper.getProjectInf", projectId);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public long getProjectCount(ProjectFilter projectFilter) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("projectMapper.getProjectListCount", projectFilter);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public List<Manager> getProjectManagerList() throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("projectMapper.getProjectManager");
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public List<Customer> getCustomerList() throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("projectMapper.getCustomerList");
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public List<Status> getStatusList() throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("projectMapper.getStatusList");
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Manager getProjectManager(String id) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("projectMapper.getManager",id);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Customer getCustomer(String id) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("projectMapper.getCustomer",id);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Status getStatus(String id) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("projectMapper.getType",id);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Integer createProject(ProjectCreateFilter filter) throws DataServiceException {
        try {
            return sqlSessionTemplate.insert("projectMapper.createProject", filter);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Integer updateProject(ProjectCreateFilter filter) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("projectMapper.updateProject", filter);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public long selectProject(String projectName) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("projectMapper.selectProject", projectName);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Integer updateDelProject(long id) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("projectMapper.updateDelProject", id);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Integer startProject(long id) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("projectMapper.startProject", id);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Integer stopProject(long id) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("projectMapper.stopProject", id);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public Integer updateProjectStatus(ProjectStatusFilter filter) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("projectMapper.updateProjectStatus", filter);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public List<VisualizationBO> getVisualizationBOListByProjectId(Long projectId) throws BizException {
        try {
            return sqlSessionTemplate.selectList("projectMapper.getVisualizationBOListByProjectId", projectId);
        } catch (DataAccessException e) {
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public List<String> getTypeNoByProjectId(Long projectId) throws BizException {
        try{
            return sqlSessionTemplate.selectList("projectMapper.getTypeNoByProjectId",projectId);
        }catch (DataAccessException e){
            throw new DAOException("根据条件查询项目列表失败",e);
        }
    }

    @Override
    public void deleteProjectByProjectId(Long projectId) throws BizException {
        try{
             sqlSessionTemplate.delete("projectMapper.deleteProjectByProjectId",projectId);
        }catch (DataAccessException e){
            throw new DAOException("根据条件查询项目列表失败",e);
        }
    }
}
