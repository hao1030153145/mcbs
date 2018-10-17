package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.transing.dpmbs.integration.bo.User;
import org.bson.conversions.Bson;

import java.util.Map;


public interface MongoDBService {

    public Map<String, Object> findPageDocument(int pageNum, int pageSize, String dbName, String collectionName, Bson filter, Bson sort)throws BizException;

}
