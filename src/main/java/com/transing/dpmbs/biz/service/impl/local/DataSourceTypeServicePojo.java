package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.integration.ContentTypeDataService;
import com.transing.dpmbs.integration.DataSourceTypeDataService;
import com.transing.dpmbs.integration.bo.ContentTypeBO;
import com.transing.dpmbs.integration.bo.DataSourceType;
import com.transing.dpmbs.integration.bo.DatasourceBO;
import com.transing.dpmbs.integration.bo.DatasourceTypeBO;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.filter.DatasourceTypeFilter;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.constant.Constants;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author lanceyan
 * @version 1.0
 */
@Service("dataSourceTypeService")
public class DataSourceTypeServicePojo extends BaseService implements DataSourceTypeService {

    protected String loggerName = this.getClass().getSimpleName();

    public static final int RESPONSE_CODE_SUCCESS = 0;//返回成功

    public static final String  GET_DATASOURCEANDTYPELIST_API = "/common/getDataSourceAndDataSourceTypeList.json";

    public static final String GET_STORAGETYPEFIELDLISTBYTYPEID_API = "/common/getStorageTypeFieldList.json";

    public static final String GETSTORAGETYPEFIELD_HAS_RULE_BYDATASOURCETYPEID_API = "/common/getStorageTypeFieldByDatasourceTypeId.json";

    public static final String GET_FIELDTYPELIST_API = "/common/getFieldTypeList.json";

    public static final String GET_DATASOURCETYPEANDTABLENAME_API = "/common/getDataSourceTypeAndTableName.json";

    public static final  String GET_STORAGETYPELIST_API = "/common/getStorageTypeList.json";

    @Resource
    private DataSourceTypeDataService dataSourceTypeDataService;

    @Resource
    private ContentTypeDataService contentTypeDataService;

    @Override
    public List<DatasourcePO>  getDataSourceTypeList(String sourceType) throws BizException {

        List<DatasourcePO> datasourcePOList = new ArrayList<>();
        String crawlServerByEnv = WebUtil.getBaseServerByEnv();

        if(!Validate.isEmpty(sourceType)){
            if(sourceType.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
                sourceType = "2";
            }else if(sourceType.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                sourceType = "1";
            }

            Object data = callRemoteService(crawlServerByEnv+GET_DATASOURCEANDTYPELIST_API+"?sourceType="+sourceType,"get",null);
            if(null != data) {
                JSONArray jsonArray = (JSONArray) data;
                datasourcePOList = com.alibaba.fastjson.JSON.parseArray(jsonArray.toString(), DatasourcePO.class);

            }

        }else {
            Object data = callRemoteService(crawlServerByEnv+GET_DATASOURCEANDTYPELIST_API+"?sourceType="+1,"get",null);
            if(null != data) {
                JSONArray jsonArray = (JSONArray) data;
                datasourcePOList = com.alibaba.fastjson.JSON.parseArray(jsonArray.toString(), DatasourcePO.class);

                Object data2 = callRemoteService(crawlServerByEnv+GET_DATASOURCEANDTYPELIST_API+"?sourceType="+2,"get",null);
                if(null != data2) {
                    JSONArray jsonArray2 = (JSONArray) data2;
                    List<DatasourcePO> datasourcePOList2 = com.alibaba.fastjson.JSON.parseArray(jsonArray2.toString(), DatasourcePO.class);

                    datasourcePOList.addAll(datasourcePOList2);

                }

            }
        }

        if(!Validate.isEmpty(datasourcePOList)){
            for (int i = datasourcePOList.size()-1;i>=0;i--) {

                DatasourcePO datasourcePO = datasourcePOList.get(i);

                List<DatasourceTypePO> datasourceTypePOList = datasourcePO.getDatasourceTypes();
                for (int j = datasourceTypePOList.size()-1; j >=0 ; j--) {
                    DatasourceTypePO datasourceTypePO = datasourceTypePOList.get(j);

                    List<ContentTypePO> contentTypeList = contentTypeDataService.getContentTypeList(datasourceTypePO.getTypeId());
                    if(Validate.isEmpty(contentTypeList)){
                        datasourceTypePOList.remove(j);
                    }

                }

                if(Validate.isEmpty(datasourceTypePOList)){
                    datasourcePOList.remove(i);
                }else {
                    datasourcePO.setDatasourceTypes(datasourceTypePOList);
                }

            }
        }

        return datasourcePOList;
    }

    @Override
    public List<DatasourcePO> getAllDataSourceTypeList() throws BizException {
        List<DatasourcePO> datasourcePOList = new ArrayList<>();

        List<DatasourceBO> datasourceBOList = dataSourceTypeDataService.getDatasourceList();
        DatasourceTypeFilter filter = new DatasourceTypeFilter();
        List<DatasourceTypeBO> datasourceTypeBOList = dataSourceTypeDataService.getDatasourceTypeList(filter);
        Map<Integer,List<DatasourceTypePO>> datasourceTypePOMap = new HashMap<>();
        if(!Validate.isEmpty(datasourceTypeBOList)){
            for (DatasourceTypeBO datasourceTypeBO:datasourceTypeBOList) {

                int datasourceId = datasourceTypeBO.getDatasourceId();
                List<DatasourceTypePO> datasourceTypePOList = datasourceTypePOMap.get(datasourceId);
                if(null == datasourceTypePOList){
                    datasourceTypePOList = new ArrayList<>();
                    datasourceTypePOMap.put(datasourceId,datasourceTypePOList);
                }

                DatasourceTypePO datasourceTypePO = new DatasourceTypePO();

                datasourceTypePO.setTypeId((long)datasourceTypeBO.getDatasourceTypeId());
                datasourceTypePO.setTypeName(datasourceTypeBO.getDatasourceTypeName());
                datasourceTypePO.setStorageTypeTable(datasourceTypeBO.getStorageTypeTable());

                datasourceTypePOList.add(datasourceTypePO);
            }
        }

        if(!Validate.isEmpty(datasourceBOList)){
            for (DatasourceBO datasourceBO:datasourceBOList) {

                int datasourceId = datasourceBO.getDatasourceId();
                List<DatasourceTypePO> datasourceTypePOList = datasourceTypePOMap.get(datasourceId);
                if(Validate.isEmpty(datasourceTypePOList)){
                    continue;
                }

                String datasourceName = datasourceBO.getDatasourceName();

                DatasourcePO datasourcePO = new DatasourcePO();
                datasourcePO.setDatasourceId(datasourceId);
                datasourcePO.setDatasourceName(datasourceName);
                datasourcePO.setDatasourceTypes(datasourceTypePOList);
                datasourcePOList.add(datasourcePO);

            }
        }

        return datasourcePOList;
    }

    @Override
    public List<DatasourcePO> getNotAddDatasourceTypeList() throws BizException {

        List<DatasourcePO> datasourcePOList = new ArrayList<>();

        String crawlServerByEnv = WebUtil.getBaseServerByEnv();

        Object data = callRemoteService(crawlServerByEnv+GET_DATASOURCEANDTYPELIST_API+"?sourceType=1","get",null);
        if(null != data){
            JSONArray jsonArray = (JSONArray) data;
            datasourcePOList = com.alibaba.fastjson.JSON.parseArray(jsonArray.toString(),DatasourcePO.class);

            Object data2 = callRemoteService(crawlServerByEnv+GET_DATASOURCEANDTYPELIST_API+"?sourceType=2","get",null);
            if(null != data2){
                JSONArray jsonArray2 = (JSONArray) data2;
                List<DatasourcePO> datasourcePOList2 = com.alibaba.fastjson.JSON.parseArray(jsonArray2.toString(),DatasourcePO.class);
                datasourcePOList.addAll(datasourcePOList2);
            }

        }

        List<DatasourceTypeBO> datasourceTypeBOList = dataSourceTypeDataService.getDatasourceTypeList(new DatasourceTypeFilter());
        List<Long> addDatasourceTypeId = new ArrayList<>();
        if(!Validate.isEmpty(datasourceTypeBOList)){
            for (DatasourceTypeBO datasourceTypeBO:datasourceTypeBOList) {
                addDatasourceTypeId.add((long) datasourceTypeBO.getDatasourceTypeId());
            }
        }

        if(!Validate.isEmpty(datasourcePOList)){
            for (int i = datasourcePOList.size()-1; i >= 0; i--) {
                DatasourcePO datasourcePO = datasourcePOList.get(i);
                List<DatasourceTypePO> datasourceTypePOList = datasourcePO.getDatasourceTypes();
                if(!Validate.isEmpty(datasourceTypePOList)){
                    for (int j = datasourceTypePOList.size()-1; j >= 0 ; j--) {
                        DatasourceTypePO datasourceTypePO = datasourceTypePOList.get(j);
                        if(addDatasourceTypeId.contains(datasourceTypePO.getTypeId())){
                            datasourceTypePOList.remove(j);
                        }
                    }
                }
                if(Validate.isEmpty(datasourceTypePOList)){
                    datasourcePOList.remove(i);
                }

            }
        }

        return datasourcePOList;
    }

    @Override
    public List<StorageTypeFieldPO> getDataSourceTypeRelationList(String id) throws BizException {

        List<StorageTypeFieldPO> storageTypeFieldPOList = new ArrayList<>();

        String crawlServerByEnv = WebUtil.getBaseServerByEnv();

        Object data = callRemoteService(crawlServerByEnv+GET_STORAGETYPEFIELDLISTBYTYPEID_API+"?datasourceTypeId="+id,"get",null);
        Object data2 = callRemoteService(crawlServerByEnv+GET_FIELDTYPELIST_API,"get",null);
        if(null != data && null != data2){
            JSONArray jsonArray = (JSONArray) data;
            JSONArray jsonArray2 = (JSONArray) data2;

            Map<String,String> fieldTypeMap = new HashMap<>();

            for (int i = 0; i < jsonArray2.size(); i++) {
                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                fieldTypeMap.put(jsonObject.getString("id"),jsonObject.getString("fieldType"));
            }

            storageTypeFieldPOList = com.alibaba.fastjson.JSON.parseArray(jsonArray.toString(),StorageTypeFieldPO.class);
            if(!Validate.isEmpty(storageTypeFieldPOList)){
                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                    String fieldType = fieldTypeMap.get(storageTypeFieldPO.getFieldType());
                    if(!Validate.isEmpty(fieldType)){
                        storageTypeFieldPO.setFieldType(fieldType);
                    }
                }
            }
        }

        return storageTypeFieldPOList;
    }

    @Override
    public List<StorageTypeFieldPO> getDataSourceTypeRelationHasRuleList(String id, String typeNo) throws BizException {
        List<StorageTypeFieldPO> storageTypeFieldPOList = new ArrayList<>();

        String crawlServerByEnv = WebUtil.getBaseServerByEnv();

        String queryHasRuleServer = "";

        if(!Validate.isEmpty(typeNo) && typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
            queryHasRuleServer = WebUtil.getCrawlBsServerByEnv();
        }else if(!Validate.isEmpty(typeNo) && typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
            queryHasRuleServer = WebUtil.getMCrawlServerByEnv();
        }else if(!Validate.isEmpty(typeNo) && typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)){
            queryHasRuleServer = WebUtil.getCrawlBsServerByEnv();
        }

        if(Validate.isEmpty(queryHasRuleServer)){
            return null;
        }

        Object data3 = callRemoteService(queryHasRuleServer+GETSTORAGETYPEFIELD_HAS_RULE_BYDATASOURCETYPEID_API+"?datasourceTypeId="+id,"get",null);
        Object data = callRemoteService(crawlServerByEnv+GET_STORAGETYPEFIELDLISTBYTYPEID_API+"?datasourceTypeId="+id,"get",null);
        Object data2 = callRemoteService(crawlServerByEnv+GET_FIELDTYPELIST_API,"get",null);
        if(null != data && null != data2 && null != data3){
            JSONArray jsonArray = (JSONArray) data;
            JSONArray jsonArray2 = (JSONArray) data2;
            JSONArray jsonArray3 = (JSONArray) data3;

            Map<String,String> fieldTypeMap = new HashMap<>();
            for (int i = 0; i < jsonArray2.size(); i++) {
                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                fieldTypeMap.put(jsonObject.getString("id"),jsonObject.getString("fieldType"));
            }

            Set<Integer> storageFieldIdSet = new HashSet<>();
            if(!Validate.isEmpty(jsonArray3)){
                for (Object object:jsonArray3) {
                    JSONObject jsonObject = (JSONObject) object;
                    storageFieldIdSet.add(jsonObject.optInt("storageFieldId",0));
                }
            }

            storageTypeFieldPOList = com.alibaba.fastjson.JSON.parseArray(jsonArray.toString(),StorageTypeFieldPO.class);
            if(!Validate.isEmpty(storageTypeFieldPOList)){
                for (int i = storageTypeFieldPOList.size()-1; i >=0 ; i--) {
                    StorageTypeFieldPO storageTypeFieldPO = storageTypeFieldPOList.get(i);
                    if(!storageFieldIdSet.contains(storageTypeFieldPO.getId())){
                        storageTypeFieldPOList.remove(i);
                    }
                    String fieldType = fieldTypeMap.get(storageTypeFieldPO.getFieldType());
                    if(!Validate.isEmpty(fieldType)){
                        storageTypeFieldPO.setFieldType(fieldType);
                    }
                }
            }
        }

        return storageTypeFieldPOList;
    }

    @Override
    public StorageTypePO getStorageTypeList(String storageTypeTable) throws BizException {
        List<StorageTypePO> storageTypePOList = new ArrayList<>();
        String crawlServerByEnv = WebUtil.getBaseServerByEnv();
        Object data = callRemoteService(crawlServerByEnv+GET_STORAGETYPELIST_API,"get",null);
        if(null != data){
            JSONArray jsonArray = (JSONArray) data;
            storageTypePOList = com.alibaba.fastjson.JSON.parseArray(jsonArray.toString(),StorageTypePO.class);
        }
        if(storageTypePOList!=null&&storageTypePOList.size()!=0){
            for(StorageTypePO storageTypePO : storageTypePOList){
                if(storageTypePO.getStorageTypeTable().equals(storageTypeTable)){
                    return storageTypePO;
                }
            }
        }
        return null;
    }

    @Override
    public StorageTypePO getStorageTypeByDatasourceTypeId(long datasourceTypeId) throws BizException {
        String crawlServerByEnv = WebUtil.getBaseServerByEnv();
        Object data = callRemoteService(crawlServerByEnv+GET_DATASOURCETYPEANDTABLENAME_API+"?datasourceTypeId="+datasourceTypeId,"get",null);
        StorageTypePO storageTypePO = new StorageTypePO();
        if(null != data){
            JSONObject jsonObject = (JSONObject) data;

            storageTypePO.setStorageTypeName(jsonObject.optString("storageTypeName",""));
            storageTypePO.setStorageTypeTable(jsonObject.optString("storageTypeTable",""));
            storageTypePO.setId(jsonObject.optLong("storageTypeId",0));

        }

        return storageTypePO;
    }

    @Override
    public DatasourceTypePO getDataSourceTypeById(Long id) throws BizException
    {

        DatasourceTypePO datasourceTypePO = new DatasourceTypePO();

        String crawlServerByEnv = WebUtil.getBaseServerByEnv();

        Object data = callRemoteService(crawlServerByEnv+GET_DATASOURCETYPEANDTABLENAME_API+"?datasourceTypeId="+id,"get",null);
        if(null != data){
            JSONObject jsonObject = (JSONObject) data;

            datasourceTypePO = com.alibaba.fastjson.JSON.parseObject(jsonObject.toString(),DatasourceTypePO.class);

        }

        return datasourceTypePO;
    }

    @Override
    public DatasourcePO getDatasourceById(long datasourceId ,String sourceType) throws BizException {
        String crawlServerByEnv = WebUtil.getBaseServerByEnv();

        Object data = callRemoteService(crawlServerByEnv+GET_DATASOURCEANDTYPELIST_API+"?datasourceId="+datasourceId +"&sourceType="+sourceType,"get",null);
        if(null != data){
            JSONArray jsonArray = (JSONArray) data;
            if(jsonArray.size() > 0){
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                DatasourcePO datasourcePO = com.alibaba.fastjson.JSON.parseObject(jsonObject.toString(),DatasourcePO.class);
                return datasourcePO;
            }
        }

        return null;
    }

    @Override
    public List<DatasourceContentPO> getDatasourceTypeContent(DatasourceTypeFilter filter) throws BizException {
        List<DatasourceContentPO> datasourceContentPOList = new ArrayList<>();
        List<DatasourceBO> datasourceBOList = dataSourceTypeDataService.getDatasourceList();
        Map<Integer,String> datasourceBOMap = new HashMap<>();
        if(!Validate.isEmpty(datasourceBOList)){
            for (DatasourceBO datasourceBO:datasourceBOList) {
                datasourceBOMap.put(datasourceBO.getDatasourceId(),datasourceBO.getDatasourceName());
            }
        }
        List<ContentTypePO> contentTypePOList = contentTypeDataService.getContentTypeList();
        Map<Integer,List<ContentTypePO>> contentTypePOMap = new HashMap<>();
        if(!Validate.isEmpty(contentTypePOList)){
            for (ContentTypePO contentTypePO:contentTypePOList) {
                int dataSourceTypeId = contentTypePO.getDataSourceTypeId();
                List<ContentTypePO> contentTypeList = contentTypePOMap.get(dataSourceTypeId);
                if(null == contentTypeList){
                    contentTypeList = new ArrayList<>();
                    contentTypePOMap.put(dataSourceTypeId,contentTypeList);
                }
                contentTypeList.add(contentTypePO);
            }
        }

        List<DatasourceTypeBO> datasourceTypeBOList =dataSourceTypeDataService.getDatasourceTypeList(filter);
        if(!Validate.isEmpty(datasourceTypeBOList)){
            for (DatasourceTypeBO datasourceTypeBO:datasourceTypeBOList) {

                DatasourceContentPO datasourceContentPO = new DatasourceContentPO();
                DatasourceContentJsonParamPO jsonParam = new DatasourceContentJsonParamPO();

                int datasourceTypeId = datasourceTypeBO.getDatasourceTypeId();
                int datasourceId = datasourceTypeBO.getDatasourceId();
                String datasourceTypeName = datasourceTypeBO.getDatasourceTypeName();
                String datasourceName = datasourceBOMap.get(datasourceId);

                jsonParam.setId(datasourceTypeId);
                jsonParam.setDatasourceId(datasourceId);
                jsonParam.setDatasourceName(datasourceName);
                jsonParam.setDatasourceTypeId(datasourceTypeId);
                jsonParam.setDatasourceTypeName(datasourceTypeName);

                datasourceContentPO.setId(datasourceTypeId);
                datasourceContentPO.setConfObject("语义分析对象");
                datasourceContentPO.setDatasourceId(datasourceId);
                datasourceContentPO.setDatasourceName(datasourceName);
                datasourceContentPO.setDatasourceTypeId(datasourceTypeId);
                datasourceContentPO.setDatasourceTypeName(datasourceTypeName);
                datasourceContentPO.setUpdatedTime(datasourceTypeBO.getUpdatedTime());
                datasourceContentPO.setUpdatedBy(datasourceTypeBO.getUpdatedBy());

                List<ContentTypePO> contentTypeList = contentTypePOMap.get(datasourceTypeId);
                List<StorageTypeFieldPO> storageTypeFieldPOList = getDataSourceTypeRelationList(Integer.toString(datasourceTypeId));
                Map<String,String> fieldMap = new HashMap<>();
                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                    fieldMap.put(storageTypeFieldPO.getFieldEnName(),Integer.toString(storageTypeFieldPO.getId()));
                }

                String confObject = "";
                List<DataSourceContentTypePO> contentTypePOList1 = new ArrayList<>();
                for (int i = 0; i < contentTypeList.size(); i++) {
                    ContentTypePO contentTypePO = contentTypeList.get(i);

                    int isDefault = contentTypePO.getIsDefault();
                    if(isDefault == 1){

                        if(i==0){
                            if(i == contentTypeList.size()-1){
                                confObject += "("+contentTypePO.getContentTypeName()+")+";
                            }else {
                                confObject += "("+contentTypePO.getContentTypeName()+"+";
                            }
                        }else {
                            if(i == contentTypeList.size()-1){
                                confObject += contentTypePO.getContentTypeName()+")+";
                            }else {
                                confObject += contentTypePO.getContentTypeName()+"+";
                            }
                        }

                    }else{
                        if(confObject.indexOf("(") >= 0 && confObject.indexOf(")") < 0){
                            confObject = confObject.substring(0,confObject.length()-1);
                            confObject += ")"+"+"+contentTypePO.getContentTypeName()+"+";
                        }else{
                            confObject += contentTypePO.getContentTypeName()+"+";
                        }
                    }

                    DataSourceContentTypePO dataSourceContentTypePO = new DataSourceContentTypePO();
                    String fieldId = fieldMap.get(contentTypePO.getContentType());
                    dataSourceContentTypePO.setFieldId(fieldId);
                    dataSourceContentTypePO.setIsDefault(contentTypePO.getIsDefault());
                    contentTypePOList1.add(dataSourceContentTypePO);

                }

                if(!Validate.isEmpty(confObject)){
                    confObject = confObject.substring(0,confObject.length()-1);
                }
                datasourceContentPO.setConfField(confObject);
                jsonParam.setContentTypes(contentTypePOList1);
                jsonParam.setConfObject("语义分析对象");
                datasourceContentPO.setJsonParam(jsonParam);

                datasourceContentPO.setStatus(datasourceTypeBO.getStatus());

                String statusName = "";
                switch (datasourceTypeBO.getStatus()){
                    case 0:statusName = "失效";break;
                    case 1:statusName = "有效";break;
                }
                datasourceContentPO.setStatusName(statusName);

                datasourceContentPOList.add(datasourceContentPO);

            }
        }


        return datasourceContentPOList;
    }

    @Override
    @Transactional
    public int saveDatasourceTypeConf(DatasourceContentJsonParamPO jsonParam,String updateBy) throws BizException {
        int id = jsonParam.getId();
        int datasourceId = jsonParam.getDatasourceId();
        String datasourceName = jsonParam.getDatasourceName();
        int datasourceTypeId = jsonParam.getDatasourceTypeId();
        String datasourceTypeName = jsonParam.getDatasourceTypeName();
        if(id == 0){

            DatasourceBO datasourceBOTmp = dataSourceTypeDataService.getDatasourceById(datasourceId);
            int i = 1;
            if(datasourceBOTmp == null){
                DatasourceBO datasourceBO = new DatasourceBO();
                datasourceBO.setDatasourceId(datasourceId);
                datasourceBO.setDatasourceName(datasourceName);
                i = dataSourceTypeDataService.addDatasource(datasourceBO);
            }

            if(i > 0){
                DatasourceTypeBO datasourceTypeBO = new DatasourceTypeBO();
                datasourceTypeBO.setDatasourceId(datasourceId);
                datasourceTypeBO.setDatasourceTypeId(datasourceTypeId);
                datasourceTypeBO.setDatasourceTypeName(datasourceTypeName);
                datasourceTypeBO.setStatus(0);

                DatasourceTypePO datasourceTypePO = new DatasourceTypePO();

                String crawlServerByEnv = WebUtil.getBaseServerByEnv();

                Object data = callRemoteService(crawlServerByEnv+GET_DATASOURCETYPEANDTABLENAME_API+"?datasourceTypeId="+datasourceTypeId,"get",null);
                if(null != data){
                    JSONObject jsonObject = (JSONObject) data;

                    datasourceTypePO = com.alibaba.fastjson.JSON.parseObject(jsonObject.toString(),DatasourceTypePO.class);

                }

                datasourceTypeBO.setStorageTypeTable(datasourceTypePO.getStorageTypeTable());
                datasourceTypeBO.setUpdatedBy(updateBy);

                int j = dataSourceTypeDataService.addDatasourceType(datasourceTypeBO);
                if(j>0){
                    List<StorageTypeFieldPO> storageTypeFieldPOList = getDataSourceTypeRelationList(Integer.toString(datasourceTypeId));
                    Map<String,StorageTypeFieldPO> stringStorageTypeFieldPOMap = new HashMap<>();
                    if(!Validate.isEmpty(storageTypeFieldPOList)){
                        for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                            stringStorageTypeFieldPOMap.put(Integer.toString(storageTypeFieldPO.getId()),storageTypeFieldPO);
                        }
                    }
                    List<DataSourceContentTypePO> contentTypePOList = jsonParam.getContentTypes();
                    if(!Validate.isEmpty(contentTypePOList)){
                        for (DataSourceContentTypePO dataSourceContentTypePO:contentTypePOList) {
                            StorageTypeFieldPO storageTypeFieldPO = stringStorageTypeFieldPOMap.get(dataSourceContentTypePO.getFieldId());

                            ContentTypeBO contentTypeBO = new ContentTypeBO();
                            contentTypeBO.setDatasourceTypeId(datasourceTypeId);
                            contentTypeBO.setIsDefault(dataSourceContentTypePO.getIsDefault());
                            contentTypeBO.setContentType(storageTypeFieldPO.getFieldEnName());
                            contentTypeBO.setContentTypeName(storageTypeFieldPO.getFieldCnName());

                            contentTypeDataService.addContentType(contentTypeBO);
                        }
                    }

                }

            }
        }else {
            contentTypeDataService.deleteContentTypeByDatasourceTypeId(datasourceTypeId);
            dataSourceTypeDataService.updateDatasourceTypeUp(datasourceTypeId,updateBy);
            List<StorageTypeFieldPO> storageTypeFieldPOList = getDataSourceTypeRelationList(Integer.toString(datasourceTypeId));
            Map<String,StorageTypeFieldPO> stringStorageTypeFieldPOMap = new HashMap<>();
            if(!Validate.isEmpty(storageTypeFieldPOList)){
                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                    stringStorageTypeFieldPOMap.put(Integer.toString(storageTypeFieldPO.getId()),storageTypeFieldPO);
                }
            }
            List<DataSourceContentTypePO> contentTypePOList = jsonParam.getContentTypes();
            if(!Validate.isEmpty(contentTypePOList)){
                for (DataSourceContentTypePO dataSourceContentTypePO:contentTypePOList) {
                    StorageTypeFieldPO storageTypeFieldPO = stringStorageTypeFieldPOMap.get(dataSourceContentTypePO.getFieldId());

                    ContentTypeBO contentTypeBO = new ContentTypeBO();
                    contentTypeBO.setDatasourceTypeId(datasourceTypeId);
                    contentTypeBO.setIsDefault(dataSourceContentTypePO.getIsDefault());
                    contentTypeBO.setContentType(storageTypeFieldPO.getFieldEnName());
                    contentTypeBO.setContentTypeName(storageTypeFieldPO.getFieldCnName());

                    contentTypeDataService.addContentType(contentTypeBO);
                }
            }
        }

        return 1;
    }

    @Override
    public int updateDatasourceTypeStatus(Integer status,String updateBy, Integer datasourceTypeId) throws BizException {
        return dataSourceTypeDataService.updateDatasourceTypeStatus(status, updateBy, datasourceTypeId);
    }

    @Override
    public int getDatasourceTypeCount(DatasourceTypeFilter filter) throws BizException {
        return dataSourceTypeDataService.getDatasourceTypeCount(filter);
    }

    /**
     * 远程服务调用方法
     *
     * @param serviceURL 服务访问URL
     * @param method     访问方法  get /   post
     * @param postData   请求参数
     * @return
     */
    protected Object callRemoteService(String serviceURL, String method, Map<String, String> postData) {
        String getTermListStr = doHttpRequest(serviceURL, method, postData);

        try {
            JSONObject getTermListJsonObject = JSONObject.fromObject(getTermListStr);
            int code = getTermListJsonObject.getInt("code");

            if (code == RESPONSE_CODE_SUCCESS) {

                Object termListArrayObject = getTermListJsonObject.get("data");
                return termListArrayObject;
            }else {
                throw new BizException("访问网络出错啦，message = " + getTermListJsonObject.getString("message"));
            }

        } catch (JSONException e) {
            LoggerUtil.errorTrace(loggerName, "访问远程接口出错，跳出执行。返回内容为：" + getTermListStr, e);
            throw e;
        }
    }

    protected String doHttpRequest(String serviceURL, String method, Map<String, String> postData) {
        HttpClientHelper httpClientHelper = new HttpClientHelper();
        httpClientHelper.setConnectionTimeout(60000);
        int retryTimes = 0;
        String getTermListStr = "{}";
        while (retryTimes < 3) {
            try {
                HttpResponse getTermListResponse = null;

                if (method.equalsIgnoreCase("get")) {
                    getTermListResponse = httpClientHelper.doGet(serviceURL,
                            "utf-8", "utf-8", null,
                            null);
                } else {
                    getTermListResponse = httpClientHelper.doPost(serviceURL, postData, "utf-8", "utf-8", null, null);
                }
                getTermListStr = getTermListResponse.getContent();
                break;
            } catch (HttpException e) {
                LoggerUtil.errorTrace(loggerName, e);
                break;
            } catch (IOException e) {
                retryTimes++;
                LoggerUtil.errorTrace(loggerName, e);
                LoggerUtil.debugTrace(loggerName, "出现IO错误，重试 " + retryTimes + " 次");

            }
        }
        return getTermListStr;
    }


}