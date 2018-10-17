package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.integration.DataService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.ContentTypeBO;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.DataSourceTypeRelation;
import com.transing.dpmbs.web.po.ContentTypePO;

import java.util.List;

/**
 * 用户数据操作接口
 *
 * @author lanceyan
 * @version 1.0
 * @see
 */
public interface ContentTypeDataService extends DataService {

    /**
     * 根据 dataSourceTypeId 查询 ContentTypeList
     * @param dataSourceTypeId
     * @return
     * @throws DataServiceException
     */
    List<ContentTypePO> getContentTypeList(long dataSourceTypeId)throws DataServiceException;

    List<ContentTypePO> getContentTypeList()throws DataServiceException;

    int addContentType(ContentTypeBO contentTypeBO)throws DataServiceException;

    int deleteContentTypeByDatasourceTypeId(int dataSourceTypeId)throws DataServiceException;

}