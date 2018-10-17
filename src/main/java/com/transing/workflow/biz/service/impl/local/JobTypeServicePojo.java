package com.transing.workflow.biz.service.impl.local;

import com.alibaba.fastjson.JSON;
import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.util.string.StringUtils;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.constant.RedisKey;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.po.StorageTypeFieldPO;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.integration.JobTypeDataService;
import com.transing.workflow.integration.bo.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sunny
 * @version 1.0
 */
@Service("jobTypeService")
public class JobTypeServicePojo extends BaseService implements JobTypeService
{
    @Resource
    private JobTypeDataService jobTypeDataService;
    @Resource
    private BaseDaoRedis redisClient;

    @Override
    public JobTypeInfo getValidJobTypeByTypeNo(String jobNo)
    {
        return jobTypeDataService.getValidJobTypeByTypeNo(jobNo);
    }

    @Override
    public List<JobTypeInfo> getAllValidJobTypeInfo()
    {
        List<JobTypeInfo> jobTypeInfoList = null;
        String allValidTypeJobInfo = redisClient.get(RedisKey.allValidJobTypeInfoList.name());
        if(!Validate.isEmpty(allValidTypeJobInfo) && !"null".equals(allValidTypeJobInfo)){
            jobTypeInfoList = (List<JobTypeInfo>) JSON.parseArray(allValidTypeJobInfo,JobTypeInfo.class);
        }else {
            jobTypeInfoList = jobTypeDataService.getAllValidJobTypeInfo();

            allValidTypeJobInfo = JSON.toJSONString(jobTypeInfoList);
            redisClient.set(RedisKey.allValidJobTypeInfoList.name(),allValidTypeJobInfo);
            redisClient.expire(RedisKey.allValidJobTypeInfoList.name(),4*3600);
        }

        return jobTypeInfoList;
    }

    @Override
    public List<OutDataSourceBo> getOutDataSource(long projectId) {
        List<Long> idList =jobTypeDataService.getOutDataSourceId(projectId);
        List<Long> dpmIdList = new ArrayList<>();
        if(!Validate.isEmpty(idList)){
            for (int i = idList.size()-1;i>=0;i--) {
                long id = idList.get(i);
                if(id < 0){
                    dpmIdList.add(idList.remove(i));
                }
            }
        }
        List<OutDataSourceBo> dataSourceBoList = new ArrayList<>();
        if(null != idList && idList.size() > 0){
            String datasourceTypeIdArray = "";
            for (long id:idList) {
                datasourceTypeIdArray += id+",";
            }
            if(!Validate.isEmpty(datasourceTypeIdArray)){
                datasourceTypeIdArray = datasourceTypeIdArray.substring(0,datasourceTypeIdArray.length() - 1 );
            }
            String crawlServerByEnv = WebUtil.getBaseServerByEnv();

            Object result = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServerByEnv+"/common/getMultipleDataSourceTypeList.json?datasourceTypeIdArray="+datasourceTypeIdArray,"get",null);
            if(null != result ){
                JSONArray jsonArray = (JSONArray) result;
                for (int i = 0; i < jsonArray.size(); i++) {
                    OutDataSourceBo outDataSourceBo = new OutDataSourceBo();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String datasourceTypeName = jsonObject.getString("datasourceTypeName");
                    long datasourceTypeId = jsonObject.getLong("id");
                    outDataSourceBo.setResultTypeName(datasourceTypeName);
                    outDataSourceBo.setReusltTypeId(datasourceTypeId);

                    dataSourceBoList.add(outDataSourceBo);
                }
            }
        }

        if(!Validate.isEmpty(dpmIdList)){
            List<OutDataSourceBo> outDataSourceBoList = jobTypeDataService.getOutDataSource(dpmIdList);
            if(!Validate.isEmpty(outDataSourceBoList)){
                dataSourceBoList.addAll(outDataSourceBoList);
            }
        }

        return dataSourceBoList;
    }

    @Override
    public List<OutDataSourceBo> getOutDataSourceRejectAnlysis(long projectId)
    {
        List<Long> idList =jobTypeDataService.getOutDataSourceId(projectId);
        if(null != idList && idList.size() > 0){
            for (int i = idList.size()-1;i>=0;i--) {
                long id = idList.get(i);
                if(id < 0){
                    idList.remove(i);
                }
            }
        }

        List<OutDataSourceBo> dataSourceBoList = new ArrayList<>();
        if(null != idList && idList.size() > 0){
            String datasourceTypeIdArray = "";
            for (long id:idList) {
                datasourceTypeIdArray += id+",";
            }
            if(!Validate.isEmpty(datasourceTypeIdArray)){
                datasourceTypeIdArray = datasourceTypeIdArray.substring(0,datasourceTypeIdArray.length() - 1 );
            }
            String crawlServerByEnv = WebUtil.getBaseServerByEnv();

            Object result = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServerByEnv+"/common/getMultipleDataSourceTypeList.json?datasourceTypeIdArray="+datasourceTypeIdArray,"get",null);
            if(null != result ){
                JSONArray jsonArray = (JSONArray) result;
                for (int i = 0; i < jsonArray.size(); i++) {
                    OutDataSourceBo outDataSourceBo = new OutDataSourceBo();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String datasourceTypeName = jsonObject.getString("datasourceTypeName");
                    long datasourceTypeId = jsonObject.getLong("id");
                    outDataSourceBo.setResultTypeName(datasourceTypeName);
                    outDataSourceBo.setReusltTypeId(datasourceTypeId);

                    dataSourceBoList.add(outDataSourceBo);
                }
            }
        }

        return dataSourceBoList;
    }

    @Override
    public List<OutDataSourceDetailBo> getOutDataSourceDetail(long resultTypeIdInt) {
        List<OutDataSourceDetailBo> outDataSourceDetailBoList = new ArrayList<>();

        if(resultTypeIdInt > 0){
            List<StorageTypeFieldPO> storageTypeFieldPOList = new ArrayList<>();

            String crawlServerByEnv = WebUtil.getBaseServerByEnv();

            Object data = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServerByEnv+"/common/getStorageTypeFieldList.json?datasourceTypeId="+resultTypeIdInt,"get",null);
            if(null != data){
                JSONArray jsonArray = (JSONArray) data;

                storageTypeFieldPOList = com.alibaba.fastjson.JSON.parseArray(jsonArray.toString(),StorageTypeFieldPO.class);
            }

            if(!Validate.isEmpty(storageTypeFieldPOList)){
                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                    OutDataSourceDetailBo outDataSourceDetailBo = new OutDataSourceDetailBo();
                    outDataSourceDetailBo.setFieldId(Integer.toString(storageTypeFieldPO.getId()));
                    outDataSourceDetailBo.setFieldName(storageTypeFieldPO.getFieldEnName());
                    outDataSourceDetailBoList.add(outDataSourceDetailBo);
                }
            }

        }else {
            outDataSourceDetailBoList = jobTypeDataService.getOutDataSourceDetail(resultTypeIdInt);
        }
        return outDataSourceDetailBoList;
    }

    @Override
    public List<OutDataSourceDemoParamter> getOutDataSourceDemoParamterList(long resultTypeIdInt) {

        List<OutDataSourceDemoParamter> outDataSourceDemoParamterList = new ArrayList<>();

        if(resultTypeIdInt > 0){
            List<StorageTypeFieldPO> storageTypeFieldPOList = new ArrayList<>();

            String crawlServerByEnv = WebUtil.getBaseServerByEnv();

            Object data = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServerByEnv+"/common/getStorageTypeFieldList.json?datasourceTypeId="+resultTypeIdInt,"get",null);
            if(null != data){
                JSONArray jsonArray = (JSONArray) data;

                storageTypeFieldPOList = com.alibaba.fastjson.JSON.parseArray(jsonArray.toString(),StorageTypeFieldPO.class);
            }

            if(!Validate.isEmpty(storageTypeFieldPOList)){
                for (StorageTypeFieldPO storageTypeFieldPO:storageTypeFieldPOList) {
                    OutDataSourceDemoParamter outDataSourceDemoParamter = new OutDataSourceDemoParamter();

                    outDataSourceDemoParamter.setFieldName(storageTypeFieldPO.getFieldEnName());
                    outDataSourceDemoParamter.setFieldDesc(storageTypeFieldPO.getFieldCnName());

                    outDataSourceDemoParamterList.add(outDataSourceDemoParamter);
                }
            }

        }else {
            outDataSourceDemoParamterList = jobTypeDataService.getOutDataSourceDemoParamterList(resultTypeIdInt);
        }

        return outDataSourceDemoParamterList;
    }

    @Override
    public JobTypeResultBO getJobTypeResultByResultTypeId(long reusltTypeId) throws BizException {
        JobTypeResultBO jobTypeResultBO = new JobTypeResultBO();
        if(reusltTypeId < 0){
            jobTypeDataService.getJobTypeResultByResultTypeId(reusltTypeId);
        }else {
            String crawlServerByEnv = WebUtil.getBaseServerByEnv();

            Object data = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServerByEnv+"/common/getDataSourceTypeAndTableName.json?datasourceTypeId="+reusltTypeId,"get",null);
            if(null != data){
                JSONObject jsonObject = (JSONObject) data;
                long typeId = jsonObject.getLong("typeId");
                String typeName = jsonObject.getString("typeName");
                String storageTypeTable = jsonObject.getString("storageTypeTable");

                jobTypeResultBO.setResultTypeName(typeName);
                jobTypeResultBO.setDataSourceType(typeId);
                jobTypeResultBO.setReusltTypeId((int) typeId);
            }
        }
        return jobTypeResultBO;
    }

    @Override
    public List<JobTypeResultBO> getJobTypeResultListByTypeNo(String typeNo) throws BizException {
        return jobTypeDataService.getJobTypeResultListByTypeNo(typeNo);
    }

    @Override
    public int deleteProjectResultByProjectResult(ProjectResultTypeBO projectResultTypeBO) throws BizException {
        return jobTypeDataService.deleteProjectResultByProjectResult(projectResultTypeBO);
    }

    @Override
    public List<JobTypeResultField> getResultFieldListByResultTypeId(long reusltTypeId) throws BizException {
        return jobTypeDataService.getResultFieldListByResultTypeId(reusltTypeId);
    }

}