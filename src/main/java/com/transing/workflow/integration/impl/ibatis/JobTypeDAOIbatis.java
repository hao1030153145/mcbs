package com.transing.workflow.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.workflow.integration.JobTypeDataService;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.OutDataSourceBo;
import com.transing.workflow.integration.bo.OutDataSourceDemoParamter;
import com.transing.workflow.integration.bo.OutDataSourceDetailBo;
import com.transing.workflow.integration.bo.ProjectResultTypeBO;
import com.transing.workflow.integration.bo.*;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JobInfo数据访问对象
 *
 * @author Sunny
 * @version 1.0
 */
@Scope("prototype")
@Repository("jobDataService")
public class JobTypeDAOIbatis extends BaseDaoiBATIS implements JobTypeDataService {

    @Override
    public JobTypeInfo getValidJobTypeByTypeNo(String typeNo) {
        return sqlSessionTemplate.selectOne("jobTypeMapper.getValidJobTypeByTypeNo", typeNo);
    }

    @Override
    public List<JobTypeInfo> getAllValidJobTypeInfo() {
        return sqlSessionTemplate.selectList("jobTypeMapper.getAllValidJobTypeInfo");
    }

    @Override
    public List<Long> getOutDataSourceId(long projectId) {
        try {
            return sqlSessionTemplate.selectList("jobTypeMapper.getOutDataSourceId", projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<OutDataSourceBo> getOutDataSource(List<Long> idList) {
        try {
            return sqlSessionTemplate.selectList("jobTypeMapper.getOutDataSource", idList);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<OutDataSourceBo> getOutDataSourceRejectAnlysis(
            List<Long> idList)
    {
        try {
            return sqlSessionTemplate.selectList("jobTypeMapper.getOutDataSourceRejectAnlysis", idList);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<OutDataSourceDetailBo> getOutDataSourceDetail(long resultTypeId) {
        try {
            return sqlSessionTemplate.selectList("jobTypeMapper.getOutDataSourceDetail", resultTypeId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<OutDataSourceDemoParamter> getOutDataSourceDemoParamterList(long resultTypeId) {
        try {
            return sqlSessionTemplate.selectList("jobTypeMapper.getOutDataSourceDemoParamterList", resultTypeId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int addProjectResult(ProjectResultTypeBO projectResultTypeBO) throws DataServiceException {
        try {
            return sqlSessionTemplate.insert("jobTypeMapper.addProjectResult", projectResultTypeBO);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int deleteProjectResultByProjectResult(ProjectResultTypeBO projectResultTypeBO) throws DataServiceException {
        try {
            return sqlSessionTemplate.delete("jobTypeMapper.deleteProjectResultByProjectResult", projectResultTypeBO);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int deleteProjectResultByProjectId(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.delete("jobTypeMapper.deleteProjectResultByProjectId", projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public JobTypeResultBO getJobTypeResultByParam(String typeNo,long dataSourceTypeId) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("typeNo",typeNo);
            param.put("dataSourceTypeId",dataSourceTypeId);
            return sqlSessionTemplate.selectOne("jobTypeMapper.getJobTypeResultByParam", param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<JobTypeResultBO> getJobTypeResultListByTypeNo(String typeNo) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("jobTypeMapper.getJobTypeResultListByTypeNo", typeNo);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<JobTypeResultBO> getJobTypeResultListByParam(String typeNo, List<Long> dataSourceTypes) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("typeNo",typeNo);
            param.put("list",dataSourceTypes);
            return sqlSessionTemplate.selectList("jobTypeMapper.getJobTypeResultListByParam", param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public JobTypeResultBO getJobTypeResultByResultTypeId(long reusltTypeId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("jobTypeMapper.getJobTypeResultByResultTypeId", reusltTypeId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<JobTypeResultField> getResultFieldListByResultTypeId(long reusltTypeId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("jobTypeMapper.getResultFieldListByResultTypeId", reusltTypeId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }
}