package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BizService;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.DataSourceTypeRelation;
import com.transing.dpmbs.integration.bo.User;
import com.transing.dpmbs.web.filter.DatasourceTypeFilter;
import com.transing.dpmbs.web.filter.GetUsersFilter;
import com.transing.dpmbs.web.po.*;

import java.util.List;
import java.util.Map;

/**
 * @author lanceyan
 * @version 1.0
 */
public interface DataSourceTypeService extends BizService {

    /**
     * 查询 数据源类型 list
     * @return
     * @throws BizException
     */

    List<DatasourcePO> getDataSourceTypeList(String sourceType)throws BizException;

    List<DatasourcePO> getAllDataSourceTypeList()throws BizException;

    List<DatasourcePO> getNotAddDatasourceTypeList()throws BizException;

    List<StorageTypeFieldPO> getDataSourceTypeRelationList(String id)throws BizException;

    /**
     * 根据datasourceTypeId和typeNo查询 有规则的存储字段
     * @param id
     * @param typeNo
     * @return
     * @throws BizException
     */
    List<StorageTypeFieldPO> getDataSourceTypeRelationHasRuleList(String id,String typeNo)throws BizException;
    //根据数据源类型表名查询
    StorageTypePO getStorageTypeList(String storageTypeTable)throws BizException;

    StorageTypePO getStorageTypeByDatasourceTypeId(long datasourceTypeId)throws BizException;

    DatasourceTypePO getDataSourceTypeById(Long id) throws BizException;

    DatasourcePO getDatasourceById(long datasourceId,String sourceType)throws BizException;

    List<DatasourceContentPO> getDatasourceTypeContent(DatasourceTypeFilter filter)throws BizException;

    int saveDatasourceTypeConf(DatasourceContentJsonParamPO jsonParam,String updateBy)throws BizException;

    int updateDatasourceTypeStatus(Integer status ,String updateBy,Integer datasourceTypeId)throws BizException;

    int getDatasourceTypeCount(DatasourceTypeFilter filter)throws BizException;
}