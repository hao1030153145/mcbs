package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.integration.DataService;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

/**
 * 用户数据操作接口
 *
 * @author lanceyan
 * @version 1.0
 * @see
 */
public interface MongoDBDataService extends DataService {

    public  String inSert(String dbName, String collectionName, Map<String, Object> paramMap);

    public  boolean delete(String dbName, String collectionName, Bson filter);

    public  List<String> findListJsonStr(String dbName, String collectionName, Bson filter, Bson sort);

    public  List<Document> findListDocument(String dbName, String collectionName, Bson filter, Bson sort);

    public  boolean update(String dbName, String collectionName, Bson filter, Map<String, Object> newParam);

    public  List<Document> aggregate(String dbName, String collectionName, List<? extends Bson> pipeline);

    public  Map<String, Object> findPageDocument(int pageNum, int pageSize, String dbName, String collectionName, Bson filter, Bson sort);


}