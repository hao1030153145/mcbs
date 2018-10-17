package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.integration.DataService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.web.filter.DatasourceTypeFilter;
import com.transing.dpmbs.web.filter.GetUsersFilter;

import java.util.List;

/**
 * 用户数据操作接口
 *
 * @author lanceyan
 * @version 1.0
 * @see
 */
public interface DataSourceTypeDataService extends DataService {

    List<DataSourceTypeRelation> getDataSourceTypeRelationList(String id)throws DataServiceException;

    DataSourceType getDataSourceTypeById(Long id) throws DataServiceException;

    int addDatasource(DatasourceBO datasourceBO)throws DataServiceException;

    int addDatasourceType(DatasourceTypeBO datasourceTypeBO)throws DataServiceException;

    List<DatasourceBO> getDatasourceList()throws DataServiceException;

    DatasourceBO getDatasourceById(int datasourceId)throws DataServiceException;

    List<DatasourceTypeBO> getDatasourceTypeList(DatasourceTypeFilter filter)throws DataServiceException;

    int updateDatasourceTypeStatus(Integer status ,String updateBy,Integer datasourceTypeId)throws DataServiceException;

    int updateDatasourceTypeUp(Integer datasourceTypeId,String updateBy)throws DataServiceException;

    int getDatasourceTypeCount(DatasourceTypeFilter filter)throws DataServiceException;

}