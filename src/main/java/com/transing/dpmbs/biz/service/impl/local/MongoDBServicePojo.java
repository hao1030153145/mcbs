package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.transing.dpmbs.biz.service.MongoDBService;
import com.transing.dpmbs.biz.service.UserService;
import com.transing.dpmbs.integration.MongoDBDataService;
import com.transing.dpmbs.integration.UserDataService;
import com.transing.dpmbs.integration.bo.User;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service("mongoDBService")
public class MongoDBServicePojo extends BaseService implements MongoDBService {
    @Resource
    private MongoDBDataService mongoDBDataService;

    @Override
    public Map<String, Object> findPageDocument(int pageNum, int pageSize, String dbName, String collectionName, Bson filter, Bson sort) throws BizException {
        return mongoDBDataService.findPageDocument(pageNum,pageSize,dbName,collectionName,filter,sort);
    }
}
