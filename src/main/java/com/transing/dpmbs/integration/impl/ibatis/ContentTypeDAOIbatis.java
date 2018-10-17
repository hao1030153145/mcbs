package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.ContentTypeDataService;
import com.transing.dpmbs.integration.DataSourceTypeDataService;
import com.transing.dpmbs.integration.bo.ContentTypeBO;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.DataSourceTypeRelation;
import com.transing.dpmbs.web.po.ContentTypePO;
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
@Repository("contentTypeDataService")
public class ContentTypeDAOIbatis extends BaseDaoiBATIS implements ContentTypeDataService {

    @Override
    public List<ContentTypePO> getContentTypeList(long dataSourceTypeId) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("contentTypeMapper.getContentTypeListByTypeId",dataSourceTypeId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<ContentTypePO> getContentTypeList() throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("contentTypeMapper.getContentTypeList");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int addContentType(ContentTypeBO contentTypeBO) throws DataServiceException {
        try{
            return sqlSessionTemplate.insert("contentTypeMapper.addContentType",contentTypeBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int deleteContentTypeByDatasourceTypeId(int dataSourceTypeId) throws DataServiceException {
        try{
            return sqlSessionTemplate.delete("contentTypeMapper.deleteContentTypeByDatasourceTypeId",dataSourceTypeId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }
}