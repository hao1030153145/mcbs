package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.ContentTypeDataService;
import com.transing.dpmbs.integration.ProjectJobTypeDataService;
import com.transing.dpmbs.integration.bo.DatasourceTypeBO;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.web.po.ContentTypePO;
import com.transing.dpmbs.web.po.DatasourceTypePO;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问对象
 *
 * @author summer
 * @version 1.0
 */
@Scope("prototype")
@Repository("projectJobTypeDataService")
public class ProjectJobTypeDAOIbatis extends BaseDaoiBATIS implements ProjectJobTypeDataService {

    @Override
    public List<ProjectJobTypeBO> getProjectJobTypeListByProjectId(long projectId) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("projectJobTypeMapper.getProjectJobTypeListByProjectId",projectId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public long addProjectJobType(ProjectJobTypeBO projectJobTypeBO) throws DataServiceException {
        try{
            return sqlSessionTemplate.insert("projectJobTypeMapper.addProjectJobType",projectJobTypeBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int deleteByProjectId(long projectId) throws DataServiceException {
        try{
            return sqlSessionTemplate.delete("projectJobTypeMapper.deleteByProjectId",projectId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int updateProjectJobType(ProjectJobTypeBO projectJobTypeBO) throws DataServiceException {
        try{
            return sqlSessionTemplate.update("projectJobTypeMapper.updateProjectJobType",projectJobTypeBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public ProjectJobTypeBO getProjectJobTypeListByProjectJobType(ProjectJobTypeBO projectJobTypeBO) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectOne("projectJobTypeMapper.getProjectJobTypeListByProjectJobType",projectJobTypeBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }
}