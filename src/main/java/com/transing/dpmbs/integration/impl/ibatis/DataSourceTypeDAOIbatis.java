package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.DataSourceTypeDataService;
import com.transing.dpmbs.integration.UserDataService;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.web.filter.DatasourceTypeFilter;
import com.transing.dpmbs.web.filter.GetUsersFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问对象
 *
 * @author summer
 * @version 1.0
 */
@Scope("prototype")
@Repository("dataSourceTypeDataService")
public class DataSourceTypeDAOIbatis extends BaseDaoiBATIS implements DataSourceTypeDataService {

    @Override
    public List<DataSourceTypeRelation> getDataSourceTypeRelationList(String id) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("dataSourceTypeMapper.getDataSourceTypeRelationList",id);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public DataSourceType getDataSourceTypeById(Long id)
            throws DataServiceException
    {
        try{
            return sqlSessionTemplate.selectOne("dataSourceTypeMapper.getDataSourceTypeById",id);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int addDatasource(DatasourceBO datasourceBO) throws DataServiceException {
        try{
            return sqlSessionTemplate.insert("dataSourceTypeMapper.addDatasource",datasourceBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int addDatasourceType(DatasourceTypeBO datasourceTypeBO) throws DataServiceException {
        try{
            return sqlSessionTemplate.insert("dataSourceTypeMapper.addDatasourceType",datasourceTypeBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<DatasourceBO> getDatasourceList() throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("dataSourceTypeMapper.getDatasourceList");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public DatasourceBO getDatasourceById(int datasourceId) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectOne("dataSourceTypeMapper.getDatasourceById",datasourceId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<DatasourceTypeBO> getDatasourceTypeList(DatasourceTypeFilter filter) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("dataSourceTypeMapper.getDatasourceTypeList",filter);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int updateDatasourceTypeStatus(Integer status, String updateBy,Integer datasourceTypeId) throws DataServiceException {
        try{
            Map<String,Object> param = new HashMap<>();
            param.put("status",status);
            param.put("updateBy",updateBy);
            param.put("datasourceTypeId",datasourceTypeId);
            return sqlSessionTemplate.update("dataSourceTypeMapper.updateDatasourceTypeStatus",param);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int updateDatasourceTypeUp(Integer datasourceTypeId, String updateBy) throws DataServiceException {
        try{
            Map<String,Object> param = new HashMap<>();
            param.put("updateBy",updateBy);
            param.put("datasourceTypeId",datasourceTypeId);
            return sqlSessionTemplate.update("dataSourceTypeMapper.updateDatasourceTypeUp",param);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int getDatasourceTypeCount(DatasourceTypeFilter filter) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectOne("dataSourceTypeMapper.getDatasourceTypeCount",filter);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }
}