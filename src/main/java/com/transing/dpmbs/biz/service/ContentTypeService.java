package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BizService;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.DataSourceTypeRelation;
import com.transing.dpmbs.web.po.ContentTypePO;

import java.util.List;

/**
 * @author lanceyan
 * @version 1.0
 */
public interface ContentTypeService extends BizService {

    /**
     * 根据 dataSourceTypeId 查询 ContentTypeList
     * @param dataSourceTypeId
     * @return
     * @throws BizException
     */
    List<ContentTypePO> getContentTypeList(long dataSourceTypeId)throws BizException;
}