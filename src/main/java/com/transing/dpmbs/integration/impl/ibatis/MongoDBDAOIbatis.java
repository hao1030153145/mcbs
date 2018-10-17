package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.transing.dpmbs.constant.MongoDBDbNames;
import com.transing.dpmbs.integration.MongoDBDataService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户数据访问对象
 *
 * @author lanceyan
 * @version 1.0
 */
@Repository("mongoDBDataService")
public class MongoDBDAOIbatis extends BaseDaoiBATIS implements MongoDBDataService {

    private MongoClient mongoClient = null;

    @Resource
    private Properties configProperties;

    private MongoDBDAOIbatis()
    {

    }

    public void init(){
        if (this.mongoClient == null)
        {
            MongoClientOptions.Builder build = new MongoClientOptions.Builder();
            build.connectionsPerHost(300);
            build.threadsAllowedToBlockForConnectionMultiplier(50);

            build.maxWaitTime(65000);
            build.connectTimeout(60000);

            MongoClientOptions myOptions = build.build();
            try
            {
                String mongodbUrl = configProperties.getProperty("mongodb.url");
                String mongodbPort = configProperties.getProperty("mongodb.port");

                String mongodbUserName = configProperties.getProperty("mongodb.userName");
                String mongodbPassword = configProperties.getProperty("mongodb.password");
                String mongodbSource = configProperties.getProperty("mongodb.source");
                ServerAddress serverAddress = new ServerAddress(mongodbUrl,Integer.parseInt(mongodbPort));
                MongoCredential credentials = MongoCredential.createScramSha1Credential(mongodbUserName, mongodbSource, mongodbPassword.toCharArray());

                /*ServerAddress serverAddress = new ServerAddress("118.190.117.195",Integer.parseInt("27017"));
                MongoCredential credentials = MongoCredential.createScramSha1Credential("dpmidc", "dpm", "dpmadmin@123456".toCharArray());*/

                List<ServerAddress> seeds = new ArrayList<ServerAddress>();

                seeds.add(serverAddress);

                List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();

                credentialsList.add(credentials);

                this.mongoClient = new MongoClient(seeds, credentialsList);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static final MongoDBDAOIbatis mongoDBDaoImpl = new MongoDBDAOIbatis();

    public static MongoDBDAOIbatis getMongoDBDaoImplInstance()
    {
        return mongoDBDaoImpl;
    }

    public String inSert(String dbName, String collectionName, Map<String, Object> paramMap)
    {

        init();

        String objId = "";
        MongoDatabase db = null;
        MongoCollection<Document> dbCollection = null;
        if ((paramMap != null) && (!paramMap.isEmpty()))
        {
            db = this.mongoClient.getDatabase(dbName);
            dbCollection = db.getCollection(collectionName);
            Document insertObj = new Document(paramMap);
            try
            {
                dbCollection.insertOne(insertObj);
                objId = insertObj.getObjectId("_id").toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                {
                    dbCollection = null;
                    db = null;
                }
            }
        }
        return objId;
    }

    public boolean delete(String dbName, String collectionName, Bson filter)
    {

        init();

        MongoDatabase db = null;
        MongoCollection<Document> dbCollection = null;
        if (filter != null) {
            try
            {
                db = this.mongoClient.getDatabase(dbName);
                dbCollection = db.getCollection(collectionName);

                DeleteResult result = dbCollection.deleteOne(filter);

                return result.getDeletedCount() >= 0L;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                {
                    dbCollection = null;
                    db = null;
                }
            }
        }
        return false;
    }

    public List<String> findListJsonStr(String dbName, String collectionName, Bson filter, Bson sort)
    {

        init();

        List<String> resultList = new ArrayList();
        MongoDatabase db = null;
        MongoCollection<Document> dbCollection = null;
        MongoCursor<Document> cursor = null;
        try
        {
            db = this.mongoClient.getDatabase(dbName);
            dbCollection = db.getCollection(collectionName);
            if (filter != null)
            {
                if (sort != null) {
                    cursor = dbCollection.find(filter).sort(sort).iterator();
                } else {
                    cursor = dbCollection.find(filter).iterator();
                }
            }
            else {
                cursor = dbCollection.find().iterator();
            }
            while (cursor.hasNext()) {
                resultList.add(((Document)cursor.next()).toJson());
            }
            return resultList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
            {
                dbCollection = null;
                db = null;
            }
        }
        return resultList;
    }

    public List<Document> findListDocument(String dbName, String collectionName, Bson filter, Bson sort)
    {

        init();


        List<Document> resultList = new ArrayList();
        MongoDatabase db = null;
        MongoCollection<Document> dbCollection = null;
        MongoCursor<Document> cursor = null;
        try
        {
            db = this.mongoClient.getDatabase(dbName);
            dbCollection = db.getCollection(collectionName);
            if (filter != null)
            {
                if (sort != null) {
                    resultList = (List)dbCollection.find(filter).sort(sort).into(new ArrayList());
                } else {
                    resultList = (List)dbCollection.find(filter).into(new ArrayList());
                }
            }
            else {
                resultList = (List)dbCollection.find().into(new ArrayList());
            }
            return resultList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
            {
                dbCollection = null;
                db = null;
            }
        }
        return resultList;
    }

    public Map<String, Object> findPageDocument(int pageNum, int pageSize, String dbName, String collectionName, Bson filter, Bson sort)
    {

        init();


        Map<String, Object> returnMap = new HashMap();
        List<Document> resultList = new ArrayList();
        MongoDatabase db = null;
        MongoCollection<Document> dbCollection = null;
        MongoCursor<Document> cursor = null;
        try
        {
            db = this.mongoClient.getDatabase(dbName);
            dbCollection = db.getCollection(collectionName);
            if (filter != null)
            {
                if (sort != null)
                {

                    int total = Long.valueOf(dbCollection.count(filter)).intValue();

                    returnMap.put("total", Integer.valueOf(total));

                    int pageTotal = total / pageSize;
                    if (total % pageSize > 0) {
                        pageTotal++;
                    }
                    if (pageNum > pageTotal) {
                        pageNum = pageTotal;
                    }
                    if (pageNum <= 0) {
                        pageNum = 1;
                    }
                    if (pageNum < pageTotal) {
                        returnMap.put("isHasNextPage", Boolean.valueOf(true));
                    } else if (pageNum >= pageTotal) {
                        returnMap.put("isHasNextPage", Boolean.valueOf(false));
                    }
                    resultList = (List)dbCollection.find(filter).limit(pageSize).skip((pageNum - 1) * pageSize).sort(sort).into(new ArrayList());

                    returnMap.put("pageData", resultList);
                }
                else
                {
                    int total = Long.valueOf(dbCollection.count(filter)).intValue();

                    returnMap.put("total", Integer.valueOf(total));

                    int pageTotal = total / pageSize;
                    if (total % pageSize > 0) {
                        pageTotal++;
                    }
                    if (pageNum > pageTotal) {
                        pageNum = pageTotal;
                    }
                    if (pageNum <= 0) {
                        pageNum = 1;
                    }
                    if (pageNum < pageTotal) {
                        returnMap.put("isHasNextPage", Boolean.valueOf(true));
                    } else if (pageNum >= pageTotal) {
                        returnMap.put("isHasNextPage", Boolean.valueOf(false));
                    }
                    resultList = (List)dbCollection.find(filter).limit(pageSize).skip((pageNum - 1) * pageSize).into(new ArrayList());

                    returnMap.put("pageData", resultList);
                }
            }
            else if (sort != null)
            {
                int total = Long.valueOf(dbCollection.count(filter)).intValue();

                returnMap.put("total", Integer.valueOf(total));

                int pageTotal = total / pageSize;
                if (total % pageSize > 0) {
                    pageTotal++;
                }
                if (pageNum > pageTotal) {
                    pageNum = pageTotal;
                }
                if (pageNum <= 0) {
                    pageNum = 1;
                }
                if (pageNum < pageTotal) {
                    returnMap.put("isHasNextPage", Boolean.valueOf(true));
                } else if (pageNum >= pageTotal) {
                    returnMap.put("isHasNextPage", Boolean.valueOf(false));
                }
                resultList = (List)dbCollection.find().limit(pageSize).skip((pageNum - 1) * pageSize).sort(sort).into(new ArrayList());

                returnMap.put("pageData", resultList);
            }
            else
            {
                int total = Long.valueOf(dbCollection.count()).intValue();

                returnMap.put("total", Integer.valueOf(total));

                int pageTotal = total / pageSize;
                if (total % pageSize > 0) {
                    pageTotal++;
                }
                if (pageNum > pageTotal) {
                    pageNum = pageTotal;
                }
                if (pageNum <= 0) {
                    pageNum = 1;
                }
                if (pageNum < pageTotal) {
                    returnMap.put("isHasNextPage", Boolean.valueOf(true));
                } else if (pageNum >= pageTotal) {
                    returnMap.put("isHasNextPage", Boolean.valueOf(false));
                }
                resultList = (List)dbCollection.find().limit(pageSize).skip((pageNum - 1) * pageSize).into(new ArrayList());

                returnMap.put("pageData", resultList);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
            {
                dbCollection = null;
                db = null;
            }
        }
        return returnMap;
    }

    public boolean update(String dbName, String collectionName, Bson filter, Map<String, Object> newParam)
    {
        init();


        MongoDatabase db = null;
        MongoCollection<Document> dbCollection = null;
        String resultString = null;
        if ((newParam == null) || (newParam.isEmpty())) {
            return true;
        }
        try
        {
            db = this.mongoClient.getDatabase(dbName);
            dbCollection = db.getCollection(collectionName);
            dbCollection.updateOne(filter, new Document("$set", new Document(newParam)));

            return resultString == null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (db != null)
            {
                dbCollection = null;
                db = null;
            }
        }
        return false;
    }

    public long count(String dbName, String collectionName, Bson filter)
    {
        init();


        MongoDatabase db = null;
        MongoCollection<Document> dbCollection = null;
        try
        {
            db = this.mongoClient.getDatabase(dbName);
            dbCollection = db.getCollection(collectionName);
            long l;
            if (filter != null) {
                return dbCollection.count(filter);
            }
            return dbCollection.count();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (db != null)
            {
                dbCollection = null;
                db = null;
            }
        }
        return 0L;
    }

    public List<Document> aggregate(String dbName, String collectionName, List<? extends Bson> pipeline)
    {

        init();


        List<Document> resultList = new ArrayList();
        MongoDatabase db = null;
        MongoCollection<Document> dbCollection = null;
        String resultString = null;
        if (pipeline != null) {
            try
            {
                db = this.mongoClient.getDatabase(dbName);
                dbCollection = db.getCollection(collectionName);
                resultList = (List)dbCollection.aggregate(pipeline).into(new ArrayList());
                return resultList;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (db != null)
                {
                    dbCollection = null;
                    db = null;
                }
            }
        }
        return resultList;
    }


    public static void main(String[] args) {
        MongoDBDAOIbatis mongoDBDAOIbatis = new MongoDBDAOIbatis();
        /*Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("title","1");
        paramMap.put("url","2");
        paramMap.put("source","3");
        paramMap.put("projectId","1");
        mongoDBDAOIbatis.inSert("test","statistics_data",paramMap);


        Map<String,Object> paramMap1 = new HashMap<>();
        paramMap1.put("title2222","1");
        paramMap1.put("url2222","2");
        paramMap1.put("source2222","3");
        paramMap1.put("projectId","2");
        mongoDBDAOIbatis.inSert("test","statistics_data",paramMap1);

        Map<String,Object> paramMap2 = new HashMap<>();
        paramMap2.put("title333","1");
        paramMap2.put("url333","2");
        paramMap2.put("source333","3");
        paramMap2.put("projectId","3");
        mongoDBDAOIbatis.inSert("test","statistics_data",paramMap2);*/

        Map<String,Object> filter = new HashMap<>();
        filter.put("projectId",50L);
        filter.put("detailId",302L);
        Map<String, Object> map = mongoDBDAOIbatis.findPageDocument(1,15, MongoDBDbNames.DB_DPM,MongoDBDbNames.COLLECTION_STATICAL_NAME,new Document(filter),null);

        System.out.println(map);


    }


}