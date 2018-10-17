package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.transing.dpmbs.biz.service.ContentTypeService;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.integration.ContentTypeDataService;
import com.transing.dpmbs.integration.DataSourceTypeDataService;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.DataSourceTypeRelation;
import com.transing.dpmbs.web.po.ContentTypePO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lanceyan
 * @version 1.0
 */
@Service("contentTypeService")
public class ContentTypeServicePojo extends BaseService implements ContentTypeService {
    @Resource
    private ContentTypeDataService contentTypeDataService;

    @Override
    public List<ContentTypePO> getContentTypeList(long dataSourceTypeId) throws BizException {
        return contentTypeDataService.getContentTypeList(dataSourceTypeId);
    }
}