package com.transing.workflow.biz.service.impl.local;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.biz.service.mq.producer.BaseKafkaProducer;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.logicframework.integration.sao.hdfs.BaseSaoHDFS;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.biz.service.VisWorkFlowService;
import com.transing.dpmbs.constant.RedisKey;
import com.transing.dpmbs.integration.ProjectDataService;
import com.transing.dpmbs.integration.bo.*;
import com.transing.dpmbs.util.Base64Util;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.QuartzManager;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.filter.ProjectStatusFilter;
import com.transing.dpmbs.web.filter.WorkFlowListFilter;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.biz.service.WorkFlowTemplateService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.JobTypeDataService;
import com.transing.workflow.integration.WorkFlowDataService;
import com.transing.workflow.integration.bo.*;
import com.transing.workflow.util.VisWorkFlowStart;
import com.transing.workflow.util.WorkFlowStart;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * @author Sunny
 * @version 1.0
 */
@Service("workFlowService")
public class WorkFlowServicePojo extends BaseService implements WorkFlowService
{
    @Resource
    private WorkFlowDataService workFlowDataService;

    @Resource
    private JobTypeDataService jobTypeDataService;

    @Resource
    private ProjectJobTypeService projectJobTypeService;

    @Resource
    private ProjectDataService projectDataService;

    @Resource
    private WorkFlowTemplateService workFlowTemplateService;
    @Resource
    private DataSourceTypeService dataSourceTypeService;
    @Resource
    private VisWorkFlowService visWorkFlowService;
    @Resource
    private JobTypeService jobTypeService;
    @Resource
    private BaseSaoHDFS baseSaoHDFS;

    public static final String GET_STORAGETYPEFIELDLISTBYTYPEID_API = "/common/getStorageTypeFieldList.json";

    public static final String GET_FIELDTYPELIST_API = "/common/getFieldTypeList.json";

    public static final String GET_DATASOURCETYPEANDTABLENAME_API = "/common/getDataSourceTypeAndTableName.json";

    public static final String HTTP_PROTOCOL = "http://"; //http访问地址前缀

    public static final String IMG_PATH = "project/vis/workFlow/project/";

    private String upload;

    protected String loggerName = this.getClass().getSimpleName();
    @Resource
    private BaseDaoRedis redisClient;

    public  void  updateWorkFlowDetailToInitByWorkFlowDetailId(long workFlowDetailId)
    {
        workFlowDataService.updateWorkFlowDetailToInitByWorkFlowDetailId(workFlowDetailId);
    }


    @Override
    public void updateWorkFlowDetailToRunningByWorkFlowDetailId(long workFlowDetailId)
    {
        workFlowDataService.updateWorkFlowDetailToRunningByWorkFlowDetailId(workFlowDetailId);
    }

    @Override
    public void updateWorkFlowDetailToComplatedByWorkFlowDetailId(long workFlowDetailId)
    {
        workFlowDataService.updateWorkFlowDetailToComplatedByWorkFlowDetailId(workFlowDetailId);
    }


    @Override
    public void updateWorkFlowDetailToStopByWorkFlowDetailId(long workFlowDetailId)
    {
        workFlowDataService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetailId);
    }

    @Override
    public int updateWorkFlowInfoToStopByFlowId(long workFlowInfoId) {
        return workFlowDataService.updateWorkFlowInfoToStopByFlowId(workFlowInfoId);
    }

    /**
     * 查询出所有需要处理的工作流信息
     *
     * @param
     * @return
     */
    public List<WorkFlowInfo> getAllNeedProcessWorkFlowInfo()
    {
        return workFlowDataService.getAllNeedProcessWorkFlowInfo();
    }

    @Override
    public List<WorkFlowInfo> getRunningWorkFlowInfo() {
        return workFlowDataService.getRunningWorkFlowInfo();
    }

    @Override
    public WorkFlowInfo getWorkFlowInfoByWorkFlowId(long workFlowId)
    {
        return workFlowDataService.getWorkFlowInfoByWorkFlowId(workFlowId);
    }

    @Override
    public WorkFlowDetail getWorkFlowDetailByWorkFlowDetailId(long workFlowDetailId)
    {
        return workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(workFlowDetailId);
    }

    @Override
    public int updateWorkFlowInfoToInitByFlowId(long flowId) {
        return workFlowDataService.updateWorkFlowInfoToInitByFlowId(flowId);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailListByWorkFlowId(long workFlowId)
    {
        return workFlowDataService.getWorkFlowDetailListByWorkFlowId(workFlowId);
    }

    @Override
    @Transactional
    public long addWorkDetail(String prevFlowDetailIds,long projectId, String typeNo, String jsonParam,int paramType) throws BizException {

        WorkFlowDetail workFlowDetail = new WorkFlowDetail();
        workFlowDetail.setProjectId(projectId);
        workFlowDetail.setTypeNo(typeNo);
        workFlowDetail.setFlowId(0L);
        workFlowDetail.setPrevFlowDetailIds(prevFlowDetailIds);

        int i = workFlowDataService.addWorkFlowDetail(workFlowDetail);
        if(i > 0){
            long flowDetailId = workFlowDetail.getFlowDetailId();
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setFlowId(0L);
            workFlowParam.setTypeNo(typeNo);
            workFlowParam.setJsonParam(jsonParam);
            workFlowParam.setFlowDetailId(flowDetailId);
            workFlowParam.setParamType(paramType);
            workFlowParam.setProjectId(projectId);

            int j = workFlowDataService.addWorkFlowParam(workFlowParam);
            if(j > 0){

                String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                if(!Validate.isEmpty(prevFlowDetailIdArray)){
                    for (String prevFlowDetailId:prevFlowDetailIdArray) {
                        WorkFlowDetail workFlowDetail2 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(prevFlowDetailId));
                        String nextFlowDetailIds = workFlowDetail2.getNextFlowDetailIds();

                        if(!Validate.isEmpty(nextFlowDetailIds)){
                            nextFlowDetailIds += ","+flowDetailId;
                        }else {
                            nextFlowDetailIds = Long.toString(flowDetailId);
                        }

                        workFlowDetail2.setNextFlowDetailIds(nextFlowDetailIds);

                        workFlowDataService.updateWorkFlowDetail(workFlowDetail2);

                    }
                }

                return workFlowParam.getParamId();
            }
        }

        return 0;
    }

    @Override
    public long addWorkDetail(String prevFlowDetailIds, String nextFlowDetailIds, long projectId, String typeNo, String jsonParam, int paramType,String quartzTime,Long dataSourceTypeId) throws BizException {

        WorkFlowDetail workFlowDetail = new WorkFlowDetail();
        workFlowDetail.setProjectId(projectId);
        workFlowDetail.setTypeNo(typeNo);
        workFlowDetail.setFlowId(0L);
        if(null != quartzTime && !"".equals(quartzTime)){
            workFlowDetail.setQuartzTime(quartzTime);
        }
        workFlowDetail.setPrevFlowDetailIds(prevFlowDetailIds);
        if(null != dataSourceTypeId && dataSourceTypeId > 0){
            workFlowDetail.setDataSourceType(Long.toString(dataSourceTypeId));

        }
        workFlowDetail.setResultParam("");
        workFlowDetail.setNextFlowDetailIds(nextFlowDetailIds);

        int i = workFlowDataService.addWorkFlowDetail(workFlowDetail);
        if(i > 0){
            long flowDetailId = workFlowDetail.getFlowDetailId();
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setTypeNo(typeNo);
            workFlowParam.setJsonParam(jsonParam);
            workFlowParam.setFlowDetailId(flowDetailId);
            workFlowParam.setParamType(paramType);
            workFlowParam.setProjectId(projectId);
            workFlowParam.setFlowId(workFlowDetail.getFlowId());

            int j = workFlowDataService.addWorkFlowParam(workFlowParam);
            if(j > 0){

                String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                if(!Validate.isEmpty(prevFlowDetailIdArray)){
                    for (String prevFlowDetailId:prevFlowDetailIdArray) {
                        WorkFlowDetail workFlowDetail2 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(prevFlowDetailId));
                        if(null != workFlowDetail2){
                            String preNextFlowDetailIds = workFlowDetail2.getNextFlowDetailIds();

                            if(!Validate.isEmpty(preNextFlowDetailIds)){
                                preNextFlowDetailIds += ","+flowDetailId;
                            }else {
                                preNextFlowDetailIds = Long.toString(flowDetailId);
                            }

                            workFlowDetail2.setNextFlowDetailIds(preNextFlowDetailIds);

                            workFlowDataService.updateWorkFlowDetail(workFlowDetail2);
                        }

                    }
                }

                //更新下一个节点
                if(!Validate.isEmpty(nextFlowDetailIds)){
                    String [] nextFlowDetialIdArray = nextFlowDetailIds.split(",");
                    for (String nextDetailIdStr:nextFlowDetialIdArray) {
                        long nextDetialId = Long.parseLong(nextDetailIdStr);
                        WorkFlowDetail nextWorkFlowDetail = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(nextDetialId);
                        if(null != nextWorkFlowDetail){
                            String prevFlowDetailIds1 = nextWorkFlowDetail.getPrevFlowDetailIds();

                            if(!Validate.isEmpty(prevFlowDetailIds1)){
                                prevFlowDetailIds1 += ","+flowDetailId;
                            }else {
                                prevFlowDetailIds1 = Long.toString(flowDetailId);
                            }

                            nextWorkFlowDetail.setPrevFlowDetailIds(prevFlowDetailIds1);

                            workFlowDataService.updateWorkFlowDetail(nextWorkFlowDetail);
                        }

                    }
                }

                if(null != dataSourceTypeId && dataSourceTypeId > 0){
                    ProjectResultTypeBO projectResultTypeBO = new ProjectResultTypeBO();
                    projectResultTypeBO.setFlowDetailId(flowDetailId);
                    projectResultTypeBO.setFlowId(0L);
                    projectResultTypeBO.setResultTypeId(dataSourceTypeId);
                    projectResultTypeBO.setProjectId(projectId);
                    jobTypeDataService.addProjectResult(projectResultTypeBO);
                }

                return workFlowParam.getParamId();
            }
        }

        return 0;
    }

    @Override
    @Transactional
    public long addWorkDetail(String prevFlowDetailIds,long projectId, String typeNo, String jsonParam,int paramType,String quartzTime) throws BizException {

        WorkFlowDetail workFlowDetail = new WorkFlowDetail();
        workFlowDetail.setProjectId(projectId);
        workFlowDetail.setTypeNo(typeNo);
        workFlowDetail.setFlowId(0L);
        workFlowDetail.setQuartzTime(quartzTime);
        workFlowDetail.setPrevFlowDetailIds(prevFlowDetailIds);

        int i = workFlowDataService.addWorkFlowDetail(workFlowDetail);
        if(i > 0){
            long flowDetailId = workFlowDetail.getFlowDetailId();
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setFlowId(0L);
            workFlowParam.setTypeNo(typeNo);
            workFlowParam.setJsonParam(jsonParam);
            workFlowParam.setFlowDetailId(flowDetailId);
            workFlowParam.setParamType(paramType);
            workFlowParam.setProjectId(projectId);

            int j = workFlowDataService.addWorkFlowParam(workFlowParam);
            if(j > 0){

                String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                if(!Validate.isEmpty(prevFlowDetailIdArray)){
                    for (String prevFlowDetailId:prevFlowDetailIdArray) {
                        WorkFlowDetail workFlowDetail2 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(prevFlowDetailId));
                        String nextFlowDetailIds = workFlowDetail2.getNextFlowDetailIds();

                        if(!Validate.isEmpty(nextFlowDetailIds)){
                            nextFlowDetailIds += ","+flowDetailId;
                        }else {
                            nextFlowDetailIds = Long.toString(flowDetailId);
                        }

                        workFlowDetail2.setNextFlowDetailIds(nextFlowDetailIds);

                        workFlowDataService.updateWorkFlowDetail(workFlowDetail2);

                    }
                }

                return workFlowParam.getParamId();
            }
        }

        return 0;
    }

    @Override
    public long addWorkDetail(String prevFlowDetailIds,long projectId, String typeNo, String jsonParam, int paramType, String quartzTime, long dataSourceTypeId) throws BizException {

        WorkFlowDetail workFlowDetail = new WorkFlowDetail();
        workFlowDetail.setProjectId(projectId);
        workFlowDetail.setTypeNo(typeNo);
        workFlowDetail.setFlowId(0L);
        if(null != quartzTime && !"".equals(quartzTime)){
            workFlowDetail.setQuartzTime(quartzTime);
        }
        workFlowDetail.setPrevFlowDetailIds(prevFlowDetailIds);
        workFlowDetail.setDataSourceType(Long.toString(dataSourceTypeId));
        workFlowDetail.setResultParam("");

        int i = workFlowDataService.addWorkFlowDetail(workFlowDetail);
        if(i > 0){
            long flowDetailId = workFlowDetail.getFlowDetailId();
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setTypeNo(typeNo);
            workFlowParam.setJsonParam(jsonParam);
            workFlowParam.setFlowDetailId(flowDetailId);
            workFlowParam.setParamType(paramType);
            workFlowParam.setProjectId(projectId);
            workFlowParam.setFlowId(workFlowDetail.getFlowId());

            int j = workFlowDataService.addWorkFlowParam(workFlowParam);
            if(j > 0){

                String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                if(!Validate.isEmpty(prevFlowDetailIdArray)){
                    for (String prevFlowDetailId:prevFlowDetailIdArray) {
                        WorkFlowDetail workFlowDetail2 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(prevFlowDetailId));
                        if(null != workFlowDetail2){
                            String nextFlowDetailIds = workFlowDetail2.getNextFlowDetailIds();

                            if(!Validate.isEmpty(nextFlowDetailIds)){
                                nextFlowDetailIds += ","+flowDetailId;
                            }else {
                                nextFlowDetailIds = Long.toString(flowDetailId);
                            }

                            workFlowDetail2.setNextFlowDetailIds(nextFlowDetailIds);

                            workFlowDataService.updateWorkFlowDetail(workFlowDetail2);
                        }

                    }
                }

                ProjectResultTypeBO projectResultTypeBO = new ProjectResultTypeBO();
                projectResultTypeBO.setFlowDetailId(flowDetailId);
                projectResultTypeBO.setFlowId(0L);
                projectResultTypeBO.setResultTypeId(dataSourceTypeId);
                projectResultTypeBO.setProjectId(projectId);
                jobTypeDataService.addProjectResult(projectResultTypeBO);

                return workFlowParam.getParamId();
            }
        }

        return 0;
    }

    @Override
    @Transactional
    public long addWorkDetail(long projectId, String typeNo, String jsonParam, int paramType, String quartzTime, long dataSourceTypeId, int workTemplateId) throws BizException {

        long paramId = 0;

        List<WorkFlowNodeBO> workFlowNodeBOList = workFlowTemplateService.getWorkFlowNodeListByTemplateId(workTemplateId);
        long flowDetailId = 0L;
        for (WorkFlowNodeBO workFlowNodeBO:workFlowNodeBOList) {

            if(!workFlowNodeBO.getPreFlowIdIds().equals("0")){
                jsonParam = workFlowNodeBO.getNodeParam();

                dataSourceTypeId = net.sf.json.JSONObject.fromObject(jsonParam).optLong("datasourceTypeId",0);
            }

            WorkFlowDetail workFlowDetail = new WorkFlowDetail();

            workFlowDetail.setProjectId(projectId);
            workFlowDetail.setTypeNo(workFlowNodeBO.getTypeNo());
            workFlowDetail.setFlowId(workFlowNodeBO.getFlowId());
            if(null != quartzTime && !"".equals(quartzTime)){
                workFlowDetail.setQuartzTime(quartzTime);
            }

            workFlowDetail.setWorkFlowTemplateId(workFlowNodeBO.getTemplateId());
            workFlowDetail.setPrevFlowDetailIds(String.valueOf(flowDetailId));
            workFlowDetail.setDataSourceType(Long.toString(dataSourceTypeId));
            workFlowDetail.setResultParam("");

            workFlowDataService.addWorkFlowDetail(workFlowDetail);

            if(flowDetailId > 0){
                WorkFlowDetail workFlowDetailTemp = new WorkFlowDetail();
                workFlowDetailTemp.setFlowDetailId(flowDetailId);
                workFlowDetailTemp.setNextFlowDetailIds(String.valueOf(workFlowDetail.getFlowDetailId()));
                workFlowDataService.updateWorkFlowDetail(workFlowDetailTemp);
            }

            flowDetailId = workFlowDetail.getFlowDetailId();

            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setTypeNo(workFlowNodeBO.getTypeNo());
            workFlowParam.setFlowId(workFlowNodeBO.getFlowId());

            workFlowParam.setJsonParam(jsonParam);
            workFlowParam.setFlowDetailId(workFlowDetail.getFlowDetailId());
            workFlowParam.setParamType(paramType);
            workFlowParam.setProjectId(projectId);

            int k = workFlowDataService.addWorkFlowParam(workFlowParam);
            if(k > 0){

                ProjectResultTypeBO projectResultTypeBO = new ProjectResultTypeBO();
                projectResultTypeBO.setFlowDetailId(flowDetailId);
                projectResultTypeBO.setFlowId(0L);
                projectResultTypeBO.setResultTypeId(dataSourceTypeId);
                projectResultTypeBO.setProjectId(projectId);
                jobTypeDataService.addProjectResult(projectResultTypeBO);

                paramId = workFlowParam.getParamId();
            }

        }

        return paramId;
    }

    @Override
    public long addWorkDetail(String prevFlowDetailIds,long projectId, String typeNo, String jsonParam, int paramType, String quartzTime, List<Long> dataSourceTypes) throws BizException {
        WorkFlowDetail workFlowDetail = new WorkFlowDetail();
        workFlowDetail.setProjectId(projectId);
        workFlowDetail.setTypeNo(typeNo);
        workFlowDetail.setFlowId(0L);
        if(null != quartzTime && !"".equals(quartzTime)){
            workFlowDetail.setQuartzTime(quartzTime);
        }

        workFlowDetail.setPrevFlowDetailIds(prevFlowDetailIds);
        workFlowDetail.setResultParam("");

        int i = workFlowDataService.addWorkFlowDetail(workFlowDetail);
        if(i > 0){
            long flowDetailId = workFlowDetail.getFlowDetailId();
            WorkFlowParam workFlowParam = new WorkFlowParam();
            workFlowParam.setFlowId(workFlowDetail.getFlowId());
            workFlowParam.setTypeNo(typeNo);
            workFlowParam.setJsonParam(jsonParam);
            workFlowParam.setFlowDetailId(flowDetailId);
            workFlowParam.setParamType(paramType);
            workFlowParam.setProjectId(projectId);

            int j = workFlowDataService.addWorkFlowParam(workFlowParam);
            if(j > 0){

                String[] prevFlowDetailIdArray = prevFlowDetailIds.split(",");
                if(!Validate.isEmpty(prevFlowDetailIdArray)){
                    for (String prevFlowDetailId:prevFlowDetailIdArray) {
                        WorkFlowDetail workFlowDetail2 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(prevFlowDetailId));
                        if(null != workFlowDetail2){
                            String nextFlowDetailIds = workFlowDetail2.getNextFlowDetailIds();

                            if(!Validate.isEmpty(nextFlowDetailIds)){
                                nextFlowDetailIds += ","+flowDetailId;
                            }else {
                                nextFlowDetailIds = Long.toString(flowDetailId);
                            }

                            workFlowDetail2.setNextFlowDetailIds(nextFlowDetailIds);

                            workFlowDataService.updateWorkFlowDetail(workFlowDetail2);
                        }

                    }
                }

                if(Validate.isEmpty(dataSourceTypes)){
                    for (long datasourceTypeId:dataSourceTypes) {
                        ProjectResultTypeBO projectResultTypeBO = new ProjectResultTypeBO();
                        projectResultTypeBO.setFlowDetailId(flowDetailId);
                        projectResultTypeBO.setFlowId(workFlowDetail.getFlowId());
                        projectResultTypeBO.setResultTypeId(datasourceTypeId);
                        projectResultTypeBO.setProjectId(projectId);
                        jobTypeDataService.addProjectResult(projectResultTypeBO);
                    }
                }

                return workFlowParam.getParamId();
            }
        }

        return 0;
    }

    @Override
    public List<WorkFlowParam> getWorkFlowParamListByParam(String typeNo,long projectId) throws BizException {
        return workFlowDataService.getWorkFlowParamListByParam(typeNo,projectId);
    }

    @Override
    @Transactional
    public boolean addWorkInfo(long projectId,String typeNos) throws BizException {
        if(!Validate.isEmpty(typeNos)){
            String [] typeNoArray = typeNos.split(",");
            List<String> typeNoList = Lists.newArrayList(Arrays.asList(typeNoArray));
            //如果包含语义分析则 添加 分词，主题，话题 节点。
            if(typeNoList.contains(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT)){
                typeNoList.add(Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION);
                typeNoList.add(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING);
                typeNoList.add(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION);
            }
            boolean isSucc = projectJobTypeService.addProjectJobType(typeNoList,projectId);

            /*if(isSucc){
                for (String typeNo:typeNoList) {
                    WorkFlowInfo workFlowInfo = new WorkFlowInfo();
                    workFlowInfo.setTypeNo(typeNo);
                    workFlowInfo.setComplateJobNum(0);
                    workFlowInfo.setProjectId(projectId);
                    workFlowInfo.setCreatedBy("");
                    workFlowInfo.setTotalJobNum(0);
                    workFlowInfo.setUpdatedBy("");
                    workFlowInfo.setStatus(WorkFlowInfo.WORK_FLOW_STATUS_INIT);
                    workFlowDataService.addWorkFlowInfo(workFlowInfo);
                }

                return true;

            }*/
        }

        return false;
    }

    @Override
    public boolean updateWorkFlowParam(WorkFlowParam workFlowParam) throws BizException {
        int i = workFlowDataService.updateWorkFlowParam(workFlowParam);
        if(i > 0)
            return true;
        return false;
    }

    @Override
    @Transactional
    public boolean updateWorkFlowParam(WorkFlowParam workFlowParam, List<Long> dataSourceTypes) throws BizException {
        int i = workFlowDataService.updateWorkFlowParam(workFlowParam);
        if(i > 0){

            if(!Validate.isEmpty(dataSourceTypes)){
                WorkFlowParam oldWorkFlowParam = workFlowDataService.getWorkFlowParamByParamId(workFlowParam.getParamId());

                ProjectResultTypeBO projectResultTypeDelBO = new ProjectResultTypeBO();
                projectResultTypeDelBO.setProjectId(oldWorkFlowParam.getProjectId());
                projectResultTypeDelBO.setFlowDetailId(oldWorkFlowParam.getFlowDetailId());
                projectResultTypeDelBO.setFlowId(oldWorkFlowParam.getFlowId());

                jobTypeDataService.deleteProjectResultByProjectResult(projectResultTypeDelBO);

                List<JobTypeResultBO> jobTypeResultList = jobTypeDataService.getJobTypeResultListByParam(oldWorkFlowParam.getTypeNo(),dataSourceTypes);

                if(null != jobTypeResultList && jobTypeResultList.size() > 0){
                    for (JobTypeResultBO jobTypeResultBO:jobTypeResultList) {
                        ProjectResultTypeBO projectResultTypeBO = new ProjectResultTypeBO();
                        projectResultTypeBO.setFlowDetailId(oldWorkFlowParam.getFlowDetailId());
                        projectResultTypeBO.setFlowId(oldWorkFlowParam.getFlowId());
                        projectResultTypeBO.setResultTypeId(jobTypeResultBO.getReusltTypeId());
                        projectResultTypeBO.setProjectId(oldWorkFlowParam.getProjectId());
                        jobTypeDataService.addProjectResult(projectResultTypeBO);
                    }
                }

            }
            return true;

        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteWorkFlowParam(long paramId) throws BizException {
        WorkFlowParam workFlowParam = workFlowDataService.getWorkFlowParamByParamId(paramId);
        if(null != workFlowParam){

            long detailId = workFlowParam.getFlowDetailId();

            WorkFlowDetail workFlowDetail = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(detailId);

            String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
            if("0".equals(prevFlowDetailIds)){
                String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
                if(!Validate.isEmpty(nextFlowDetailIds)){
                    WorkFlowDetail nextWorkFlowDetai = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(nextFlowDetailIds));
                    String prevFlowDetailIds1 = nextWorkFlowDetai.getPrevFlowDetailIds();
                    String [] preDetailArray = prevFlowDetailIds1.split(",");
                    List<String> preDetailList = new ArrayList<>(Arrays.asList(preDetailArray));
                    preDetailList.remove(Long.toString(detailId));
                    String preStr = "";
                    if(preDetailList.size() > 0){
                        preStr = StringUtils.join(preDetailList,",");
                        nextWorkFlowDetai.setPrevFlowDetailIds(preStr);
                        workFlowDataService.updateWorkFlowDetail(nextWorkFlowDetai);
                    }else {
                        nextWorkFlowDetai.setPrevFlowDetailIds(preStr);
                        workFlowDataService.updateWorkFlowDetail(nextWorkFlowDetai);
                        WorkFlowParam param = workFlowDataService.getWorkFlowParamByDetailId(nextWorkFlowDetai.getFlowDetailId());
                        deleteWorkFlowParam(param.getParamId());
                    }
                }
            }else if("".equals(prevFlowDetailIds)){
                String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
                if(!Validate.isEmpty(nextFlowDetailIds)){
                    WorkFlowDetail nextWorkFlowDetai = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(nextFlowDetailIds));
                    String prevFlowDetailIds1 = nextWorkFlowDetai.getPrevFlowDetailIds();
                    String [] preDetailArray = prevFlowDetailIds1.split(",");
                    List<String> preDetailList = new ArrayList<>(Arrays.asList(preDetailArray));
                    preDetailList.remove(Long.toString(detailId));
                    String preStr = "";
                    if(preDetailList.size() > 0){
                        preStr = StringUtils.join(preDetailList,",");
                    }
                    nextWorkFlowDetai.setPrevFlowDetailIds(preStr);
                    workFlowDataService.updateWorkFlowDetail(nextWorkFlowDetai);

                    if("".equals(preStr)){
                        WorkFlowParam param = workFlowDataService.getWorkFlowParamByDetailId(nextWorkFlowDetai.getFlowDetailId());
                        deleteWorkFlowParam(param.getParamId());
                    }

                }
            }else {

                WorkFlowDetail preDetail = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(prevFlowDetailIds));

                String nextFlowDetailIds1 = workFlowDetail.getNextFlowDetailIds();
                if(!Validate.isEmpty(nextFlowDetailIds1)){
                    preDetail.setNextFlowDetailIds(nextFlowDetailIds1);
                    workFlowDataService.updateWorkFlowDetail(preDetail);

                    WorkFlowDetail nextDetail = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(nextFlowDetailIds1));
                    nextDetail.setPrevFlowDetailIds(""+preDetail.getFlowDetailId());
                    workFlowDataService.updateWorkFlowDetail(nextDetail);
                }else {
                    preDetail.setNextFlowDetailIds("");
                    workFlowDataService.updateWorkFlowDetail(preDetail);
                }

            }

            workFlowDataService.deleteWorkFlowDetailByDetailId(detailId);
            workFlowDataService.deleteWorkFlowParamByParamId(paramId);

            ProjectResultTypeBO projectResultTypeBO = new ProjectResultTypeBO();
            projectResultTypeBO.setProjectId(workFlowParam.getProjectId());
            projectResultTypeBO.setFlowId(workFlowParam.getFlowId());
            projectResultTypeBO.setFlowDetailId(workFlowParam.getFlowDetailId());
            jobTypeDataService.deleteProjectResultByProjectResult(projectResultTypeBO);

        }

        return true;
    }

    @Override
    public List<WorkFlowParam> getWorkFlowParamListByTypeNoList(List<String> typeNoList, long projectId) throws BizException {
        return workFlowDataService.getWorkFlowParamListByTypeNoList(typeNoList,projectId);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailListByTypeNoList(List<String> typeNoList, long projectId) throws BizException {
        return workFlowDataService.getWorkFlowDetailListByTypeNoList(typeNoList,projectId);
    }

    @Override
    public WorkFlowParam getWorkFlowParamByDetailId(long flowDetailId) throws BizException {
        return workFlowDataService.getWorkFlowParamByDetailId(flowDetailId);
    }

    @Override
    public int updateWorkFlowDetailToStart(long flowDetailId) throws BizException {
        return workFlowDataService.updateWorkFlowDetailToStart(flowDetailId);
    }

    @Override
    public int updateWorkFlowDetailToFinish(long flowDetailId) throws BizException {
        return workFlowDataService.updateWorkFlowDetailToFinish(flowDetailId);
    }

    @Override
    public int updateWorkFlowInfoToStart(long flowId) throws BizException {
        return workFlowDataService.updateWorkFlowInfoToStart(flowId);
    }

    @Override
    public int updateWorkFlowInfoComNum(long flowId) throws BizException {
        return workFlowDataService.updateWorkFlowInfoComNum(flowId);
    }

    @Override
    public int updateWorkFlowInfoToFinishIfComNum(long flowId) throws BizException {
        return workFlowDataService.updateWorkFlowInfoToFinishIfComNum(flowId);
    }

    @Override
    @Transactional
    public boolean updateWorkFlowToRunningByProjectId(long projectId) throws BizException {

        //获取有效的工作流节点信息
        List<JobTypeInfo> jobTypeInfoList = jobTypeDataService.getAllValidJobTypeInfo();
        Map<String,JobTypeInfo> jobTypeInfoMap = new HashMap<>();
        for (JobTypeInfo jobTypeInfo:jobTypeInfoList) {
            jobTypeInfoMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
        }

        Project project = projectDataService.getProjectInf(projectId);

        String projectType = project.getProjectType();

        //把两个list添加进redis缓存
        List<WorkFlowDetail> workFlowDetailList = workFlowDataService.getWorkFlowDetailByProjectId(projectId);

        List<WorkFlowParam> workFlowParamList = new ArrayList<>();

        workFlowParamList = workFlowDataService.getWorkFlowParamByProJectId(projectId);

        String workFlowDetailListStr = JSON.toJSONString(workFlowDetailList);
        String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
        redisClient.set(RedisKey.startProjectDetailList_suffix.name()+projectId,workFlowDetailListStr);
        redisClient.set(RedisKey.startProjectParamList_suffix.name()+projectId,workFlowParamListStr);

        Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
        for (WorkFlowParam workFlowParam:workFlowParamList) {
            workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
        }

        //找出流程节点和状态节点
        List<Long> detailIdList = new ArrayList<>();
        for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
            long detailId = workFlowDetail.getFlowDetailId();

            JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(workFlowDetail.getTypeNo());

            if(null != jobTypeInfo){

                //如果是话题 则 需要判断 是否是流程节点 还是 状态 节点
                if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(jobTypeInfo.getTypeNo())){
                    //判断如果是话题 需要判断是否是 流程节点
                    WorkFlowParam workFlowParam = workFlowParamMap.get(detailId);
                    HotspotsPO hotspotsPO = JSON.parseObject(workFlowParam.getJsonParam(),HotspotsPO.class);
                    //如果话题list不为空 则表示 是流程节点
                    if(!Validate.isEmpty(hotspotsPO.getTopicList())){
                        jobTypeInfo.setJobClassify(JobTypeInfo.JOB_CLASSIFY_PROCESS);
                    }
                }

                int jobClassify = jobTypeInfo.getJobClassify();
                if(JobTypeInfo.JOB_CLASSIFY_PROCESS == jobClassify){
                    detailIdList.add(detailId);
                }else if(JobTypeInfo.JOB_CLASSIFY_STATUS == jobClassify){
                    int jobStatus = workFlowDetail.getJobStatus();

                    //如果状态节点报错了
                    if(9 == jobStatus){
                        //状态节点 目前 也是 通过 zk来传递的则也通过 恢复zk的错误任务就好。
                        detailIdList.add(detailId);
                    }

                }
            }
        }

        new Thread(new WorkFlowStart(projectId)).start();//开始执行工作流启动

        workFlowDataService.updateWorkFlowDetailToStartByProjectId(projectId,detailIdList);//启动 工作流 详细

        return true;
    }

    @Override
    @Transactional
    public boolean startWorkFlowByParamId(long paramId, long projectId,String batchNo) throws BizException {

        WorkFlowParam workFlowParam = workFlowDataService.getWorkFlowParamByParamId(paramId);
        if(null != workFlowParam){
            WorkFlowDetail workFlowDetail = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(workFlowParam.getFlowDetailId());
            if(null != workFlowDetail){
                String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                if("0".equals(prevFlowDetailIds)){

                    List<WorkFlowDetail> firstDetailList = new ArrayList<>();
                    firstDetailList.add(workFlowDetail);
                    new Thread(new WorkFlowStart(projectId,firstDetailList,batchNo)).start();//开始执行工作流启动

                    //TODO 更新detail的状态

                    updateWorkFlowDetailToStart(workFlowDetail.getFlowDetailId());
                    updateNextDetailToStart(workFlowDetail);

                    projectDataService.startProject(workFlowDetail.getProjectId());

                    return true;
                }else {
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public boolean stopWorkFlowByParamId(long paramId, long projectId) throws BizException {
        WorkFlowParam workFlowParam = workFlowDataService.getWorkFlowParamByParamId(paramId);
        if(null != workFlowParam){
            WorkFlowDetail workFlowDetail = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(workFlowParam.getFlowDetailId());
            if(null != workFlowDetail){
                workFlowDataService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetail.getFlowDetailId());
                String prevFlowDetailIds = workFlowDetail.getPrevFlowDetailIds();
                if("0".equals(prevFlowDetailIds)) {

                    String typeNo = workFlowDetail.getTypeNo();
                    JobTypeInfo jobTypeInfo = jobTypeDataService.getValidJobTypeByTypeNo(typeNo);

                    if (typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)) {
                        String crawlServer = WebUtil.getCrawlServerByEnv();
                        Map<String,String> postData = new HashMap<>();
                        postData.put("detailId",Long.toString(workFlowDetail.getFlowDetailId()));
                        Object obj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServer+"/crawlTask/stopCrawl.json","post",postData);
                        if(null != obj){
                            return true;
                        }
                    }else if (typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)) {
                        String mCrawlServer = WebUtil.getMCrawlServerByEnv();
                        Map<String,String> postData = new HashMap<>();
                        postData.put("detailId",Long.toString(workFlowDetail.getFlowDetailId()));
                        Object obj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),mCrawlServer+"/crawlTask/stopCrawl.json","post",postData);
                        if(null != obj){
                            return true;
                        }
                    }

                }

            }
        }

        return false;
    }

    private boolean updateNextDetailToStart(WorkFlowDetail workFlowDetail){

        String nextFlowDetailIds = workFlowDetail.getNextFlowDetailIds();
        if(!Validate.isEmpty(nextFlowDetailIds)){
            String [] nextDetailArray = nextFlowDetailIds.split(",");
            for (String detailIdStr:nextDetailArray) {
                long detail = Long.parseLong(detailIdStr);
                WorkFlowDetail workFlowDetail2 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(detail);

                workFlowDataService.updateWorkFlowDetailToStart(detail);//把找到的更新为开始
                //递归调用 开始他的下一个节点
                updateNextDetailToStart(workFlowDetail2);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updateWorkFlowToStopByProjectId(long projectId) throws BizException {

        workFlowDataService.updateWorkFlowDetailToStopByProjectId(projectId);
        boolean flag=true;
        List<WorkFlowDetail> workFlowDetailList = workFlowDataService.getFirstDetailByProjectId(projectId);
        if(!Validate.isEmpty(workFlowDetailList)){

            String crawlServer = WebUtil.getCrawlServerByEnv();

            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {

                try {
                    QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                }catch (Exception e){
                    e.printStackTrace();
                }

                String typeNo = workFlowDetail.getTypeNo();
                if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                    workFlowDataService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetail.getFlowDetailId());

                    redisClient.del(RedisKey.totalTaskNum_suffix.name()+workFlowDetail.getFlowDetailId());
                    redisClient.del(RedisKey.finishNum_suffix.name()+workFlowDetail.getFlowDetailId());
                    redisClient.del(RedisKey.resultNum_suffix.name()+workFlowDetail.getFlowDetailId());

                    Map<String,String> postData = new HashMap<>();
                    postData.put("detailId",Long.toString(workFlowDetail.getFlowDetailId()));
                    Object obj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServer+"/crawlTask/stopCrawl.json","post",postData);
                    if(null != obj){
                        flag= true;
                    }else{
                       continue;
                    }
                }else if (typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)) {
                    workFlowDataService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetail.getFlowDetailId());

                    redisClient.del(RedisKey.totalTaskNum_suffix.name()+workFlowDetail.getFlowDetailId());
                    redisClient.del(RedisKey.finishNum_suffix.name()+workFlowDetail.getFlowDetailId());
                    redisClient.del(RedisKey.resultNum_suffix.name()+workFlowDetail.getFlowDetailId());

                    String mCrawlServer = WebUtil.getMCrawlServerByEnv();
                    Map<String,String> postData = new HashMap<>();
                    postData.put("detailId",Long.toString(workFlowDetail.getFlowDetailId()));
                    Object obj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),mCrawlServer+"/crawlTask/stopCrawl.json","post",postData);
                    if(null != obj){
                        flag= true;
                    }else{
                        continue;
                    }
                }
            }
        }

        return flag;
    }

    @Override
    public int updateWorkFlowDetailQuartzTime(WorkFlowDetail workFlowDetail) throws BizException {
        return workFlowDataService.updateWorkFlowDetailQuartzTime(workFlowDetail);
    }

    @Override
    public WorkFlowParam getWorkFlowParamByParamId(long paramId) throws BizException {
        return workFlowDataService.getWorkFlowParamByParamId(paramId);
    }

    @Override
    public List<WorkFlowInfo> getWorkFlowInfoByProjectId(long projectId) throws BizException {
        return workFlowDataService.getWorkFlowInfoByProjectId(projectId);
    }

    @Override
    public WorkFlowInfo getWorkFlowInfoByProjectIdANdTypeNo(long projectId, String typeNo) throws BizException {
        return workFlowDataService.getWorkFlowInfoByProjectIdANdTypeNo(projectId,typeNo);
    }

    @Override
    public int updateWorkFlowDetailProgress(long flowDetailId, int jobProgress) throws BizException {
        return workFlowDataService.updateWorkFlowDetailProgress(flowDetailId,jobProgress);
    }

    @Override
    public int updateWorkFlowInfoStatusExceptionByFlowId(long flowId) throws BizException {
        return workFlowDataService.updateWorkFlowInfoStatusExceptionByFlowId(flowId);
    }

    @Override
    public int updateWorkFlowDetailExceptionByMap(String errorMsg, long workFlowDetailId) throws BizException {
        workFlowDataService.updateWorkFlowDetailToExceptionByWorkFlowDetailId(workFlowDetailId,errorMsg);
        return 1;
    }

    @Override
    public boolean updateWorkFlowToRunningIfFinishByProjectId(long projectId) throws BizException {

        workFlowDataService.updateWorkFlowDetailToRunningIfFinishByProjectId(projectId);//将该项目的所有已完成的节点更新为启动中

        new Thread(new WorkFlowStart(projectId)).start();//开始执行工作流启动

        return true;
    }

    @Override
    public int updateWorkFlowDetail(WorkFlowDetail workFlowDetail) throws BizException {
        return workFlowDataService.updateWorkFlowDetail(workFlowDetail);
    }

    @Override
    public List<WorkFlowDetail> getFirstDetailByProjectId(long projectId) throws BizException {
        return workFlowDataService.getFirstDetailByProjectId(projectId);
    }

    @Override
    public List<WorkFlowDetail> getExecDetailListByTypeNo(String typeNo,long projectId) throws BizException {
        return workFlowDataService.getExecDetailListByTypeNo(typeNo,projectId);
    }

    @Override
    public List<WorkFlowParam> getWorkFlowParamByFlowParam(WorkFlowParam workFlowParam) throws BizException {
        return workFlowDataService.getWorkFlowParamByFlowParam(workFlowParam);
    }

    public void updateWorkFlowDetailToExceptionByWorkFlowDetailId(long workFlowDetailId, String errorMsg)
    {
        workFlowDataService.updateWorkFlowDetailToExceptionByWorkFlowDetailId(workFlowDetailId,errorMsg);
    }

    @Override
    public List<WorkFlowParam> getWorkFlowParamByProJectId(Long projectId) throws DataServiceException {
        return workFlowDataService.getWorkFlowParamByProJectId(projectId);
    }
    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByProjectId(Long projectId) throws DataServiceException {
        return workFlowDataService.getWorkFlowDetailByProjectId(projectId);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByStatus(Integer jobStatus) throws BizException {
        return workFlowDataService.getWorkFlowDetailByStatus(jobStatus);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByWorkFlowId(Long workFlowId) throws DataServiceException {
        return workFlowDataService.getWorkFlowDetailByWorkFlowId(workFlowId);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByWorkFlowIdAndProjectId(Long workFlowId, Long projectId) throws DataServiceException {
        return workFlowDataService.getWorkFlowDetailByWorkFlowIdAndProjectId(workFlowId,projectId);
    }

    @Override
    public List<WorkFlowNodeBO> getWorkFlowNodeByTemplateId(int templateId) throws DataServiceException {
        return workFlowDataService.getWorkFlowNodeByTemplateId(templateId);
    }

    @Override
    public WorkFlowNodeBO getWorkFlowNodeByFlowId(Long flowId) throws DataServiceException {
        return workFlowDataService.getWorkFlowNodeByFlowId(flowId);
    }

    @Override
    public WorkFlowParam getWorkFlowParamByFlowId(Long flowId,Long projectId) throws DataServiceException {
        return workFlowDataService.getWorkFlowParamByFlowId(flowId,projectId);
    }

    @Override
    @Transactional
    public void addProjectDetailIdByTemplateId(int workFlowTemplateId,Long workFlowId,Long projectId) throws DataServiceException {
        List<WorkFlowNodeBO> workFlowNodeBOList = workFlowDataService.getWorkFlowNodeByTemplateId(workFlowTemplateId);

        Map<Long,Long> map = new HashMap<>();
        Map<Long,Long> map2 = new HashMap<>();
        Map<Long,String> mapNextDetails = new HashMap<>();//下节点可能为空
        Map<Long,String> mapPreDetails = new HashMap<>();
        List<WorkFlowDetail> workFlowDetailList = new ArrayList<>();
        for(WorkFlowNodeBO w : workFlowNodeBOList){
            //根据模板id和模板的节点id查询该节点的详细参数配置
            Map<String,Object> filterMap = new HashMap<>();
            filterMap.put("workFlowTemplateId",workFlowTemplateId);
            filterMap.put("workFlowNodeId",w.getFlowId());
            List<WorkFlowTemplateNodeParamBo>  wftnpbList = workFlowDataService.getWorkFlowTemplateNodeParamBoListByMap(filterMap);
            WorkFlowDetail workFlowDetail = new WorkFlowDetail();
            for(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo : wftnpbList){
                if(workFlowTemplateNodeParamBo.getInputParamCnName().equals("数据源类型")){
                    workFlowDetail.setDataSourceType(workFlowTemplateNodeParamBo.getInputParamValue());
                    continue;
                }
                if(workFlowTemplateNodeParamBo.getInputParamCnName().equals("抓取频次")){
                    workFlowDetail.setQuartzTime(workFlowTemplateNodeParamBo.getInputParamValue());
                 continue;
                }
            }
            //1、首先将模板的流程节点id所对应的记录添加至工作流流程节点表。
            BeanUtils.copyProperties(w,workFlowDetail,new String[]{"preFlowIdIds","nextFlowIdIds","updatedDate","createdDate"});//复制两张表相同字段的值
            workFlowDetail.setWorkFlowTemplateId(w.getTemplateId());
            workFlowDetail.setWorkFlowId(workFlowId);
            workFlowDetail.setProjectId(projectId);
            workFlowDataService.addWorkFlowDetail(workFlowDetail);
            workFlowDetailList.add(workFlowDetail);
            //map(模板detailId,流程detailId)
            map.put(w.getFlowId(),workFlowDetail.getFlowDetailId());
            //map2(流程detailId,模板detailId)
            map2.put(workFlowDetail.getFlowDetailId(),w.getFlowId());
            mapNextDetails.put(w.getFlowId(),w.getNextFlowIdIds());
            mapPreDetails.put(w.getFlowId(),w.getPreFlowIdIds());

            for(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo : wftnpbList){
                WorkFlowNodeParamBo workFlowNodeParamBo = new WorkFlowNodeParamBo();
                BeanUtils.copyProperties(workFlowTemplateNodeParamBo,workFlowNodeParamBo,new String[]{"paramId","updatedDate","createdDate"});
                workFlowNodeParamBo.setWorkFlowId(workFlowId);
                workFlowNodeParamBo.setProjectId(projectId);
                workFlowNodeParamBo.setFlowDetailId(workFlowDetail.getFlowDetailId());
                workFlowNodeParamBo.setParamType("0");
                //新增
                workFlowDataService.addWorkFlowNodeParam(workFlowNodeParamBo);
            }
        }
        //开始更新
        for(WorkFlowDetail workFlowDetail : workFlowDetailList){
            //创建一个用于更新的workFlowDetailUpdate
            WorkFlowDetail workFlowDetailUpdate = new WorkFlowDetail();
            workFlowDetailUpdate.setFlowDetailId(workFlowDetail.getFlowDetailId());

            Long temDetail = map2.get(workFlowDetail.getFlowDetailId());//获取模板flowId
            //设置下节点
            String projectNextDetail = "";
            //需要判断下一个节点是否为空
            if(!Validate.isEmpty(mapNextDetails.get(temDetail))){
                String strNextDetail [] = String.valueOf(mapNextDetails.get(temDetail)).split(",");
                for(int i=0;i<strNextDetail.length;i++){
                    Long projectDetail = map.get(Long.parseLong(strNextDetail[i]));
                    if(projectDetail==null){
                        projectNextDetail = "";
                    }else{
                        projectNextDetail += projectDetail+",";
                    }
                }
                if(!projectNextDetail.equals("")){
                    projectNextDetail = projectNextDetail.substring(0,projectNextDetail.length()-1);
                }
            }
            workFlowDetailUpdate.setNextFlowDetailIds(projectNextDetail);
            //设置上节点
            String strPreDetail [] = mapPreDetails.get(temDetail).split(",");
            String projectPreDetail = "";
            for(int i=0;i<strPreDetail.length;i++){
                Long projectDetail=0L;
                if(!strPreDetail[i].equals("0")){
                    projectDetail = map.get(Long.parseLong(strPreDetail[i]));
                }
                projectPreDetail += projectDetail+",";
            }
            projectPreDetail = projectPreDetail.substring(0,projectPreDetail.length()-1);
            workFlowDetailUpdate.setPrevFlowDetailIds(projectPreDetail);
            //更新记录
            workFlowDataService.updateWorkFlowDetail(workFlowDetailUpdate);
        }
        //开始为工作流所有节点添加输出字段
        List<WorkFlowDetail> list = workFlowDataService.getWorkFlowDetailByWorkFlowId(workFlowId);
        List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowDataService.getWorkFlowNodeParamByProjectId(workFlowId);
        Map<String,Object> polymerizationMap = new HashMap<>();//聚合的map
        for(WorkFlowNodeParamBo workFlowNodeParamBo : workFlowNodeParamBoList){
            polymerizationMap.put(workFlowNodeParamBo.getInputParamCnName(),workFlowNodeParamBo.getInputParamValue());
        }
        for(WorkFlowDetail workFlowDetail : list){
            String typeNo = workFlowDetail.getTypeNo();
            Object o;
            switch (typeNo){
                case Constants.WORK_FLOW_TYPE_NO_DATAIMPORT:
                    List<String> outFieldList = new ArrayList<>();
                    o = polymerizationMap.get("字段映射");
                    if(o!=null){
                        JSONArray origainRelationJson = JSONObject.parseArray(o.toString());
                        for(Object object :origainRelationJson){
                            JSONObject origainRelationJsonObject = JSONObject.parseObject(object.toString());
                            if(!Validate.isEmpty(origainRelationJsonObject.getString("key"))){
                                outFieldList.add(origainRelationJsonObject.getString("key"));
                            }
                         }
                        //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                        if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                            visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                        }
                        addWorkFlowOutputFiled(workFlowDetail,outFieldList,typeNo);
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATACRAWL:
                    //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                    if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                        visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                    }
                    addWorkFlowOutputFiled(workFlowDetail,null,typeNo);
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL:
                    //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                    if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                        visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                    }
                    addWorkFlowOutputFiled(workFlowDetail,null,typeNo);
                    break;
                case Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT:
                    o = polymerizationMap.get("分析层级");
                    if(o!=null){
                        List<JobTypeResultField> jobTypeResultFieldList =
                                getJobTypeResultFieldListByResultTypeId(o.toString());
                        if(!Validate.isEmpty(jobTypeResultFieldList)){
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList){
                                String filedType = "";
                                VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowBO.setFiledType(filedType);
                                visWorkFlowBO.setStorageTypeTable(o.toString());
                                visWorkFlowBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION:
                    o = polymerizationMap.get("分词对象");
                    if(o!=null){
                        List<JobTypeResultField> jobTypeResultFieldList2 =
                                getJobTypeResultFieldListByResultTypeId(o.toString());
                        if(!Validate.isEmpty(jobTypeResultFieldList2)){
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList2){
                                if(jobTypeResultField.getColName().equals("主题json")||
                                        jobTypeResultField.getColName().equals("话题json")){
                                    continue;
                                }
                                String filedType = "";
                                VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowBO.setFiledType(filedType);
                                visWorkFlowBO.setStorageTypeTable(o.toString());
                                visWorkFlowBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING:
                    o = polymerizationMap.get("主题对象");
                    if(o!=null){
                        List<JobTypeResultField> jobTypeResultFieldList3 =
                                getJobTypeResultFieldListByResultTypeId(o.toString());
                        if(!Validate.isEmpty(jobTypeResultFieldList3)){
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList3){
                                if(jobTypeResultField.getColName().equals("分词结果")||
                                        jobTypeResultField.getColName().equals("关键词结果")||
                                        jobTypeResultField.getColName().equals("话题json")){
                                    continue;
                                }
                                String filedType = "";
                                VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowBO.setFiledType(filedType);
                                visWorkFlowBO.setStorageTypeTable(o.toString());
                                visWorkFlowBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION:
                    o = polymerizationMap.get("话题对象");
                    if(o!=null){
                        List<JobTypeResultField> jobTypeResultFieldList4 =
                                getJobTypeResultFieldListByResultTypeId(o.toString());
                        if(!Validate.isEmpty(jobTypeResultFieldList4)){
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList4){
                                if(jobTypeResultField.getColName().equals("分词结果")||
                                        jobTypeResultField.getColName().equals("关键词结果")||
                                        jobTypeResultField.getColName().equals("主题json")){
                                    continue;
                                }
                                String filedType = "";
                                VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowBO.setFiledType(filedType);
                                visWorkFlowBO.setStorageTypeTable(o.toString());
                                visWorkFlowBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT:
                    break;
                default:
            }
        }
    }

    @Override
    public List<JobTypeCategoryBO> getJobTypeCategoryBOList() throws DataServiceException {
        return workFlowDataService.getJobTypeCategoryBOList();
    }

    @Override
    public List<JobTypeInfo> getJobTypeInfo() throws DataServiceException {
        return workFlowDataService.getJobTypeInfo();
    }

    @Override
    public List<WorkFlowInputParamBo> getWorkFlowInputParamBoList() throws DataServiceException {
        List<WorkFlowInputParamBo> workFlowInputParamBoList = null;
        String workFlowInputParamBoStr = redisClient.get(RedisKey.workFlowInputParamBoList_suffix.name());
        if(!Validate.isEmpty(workFlowInputParamBoStr) && !"null".equals(workFlowInputParamBoStr)){
            workFlowInputParamBoList = JSON.parseArray(workFlowInputParamBoStr,WorkFlowInputParamBo.class);
        }else{
            workFlowInputParamBoList =workFlowDataService.getWorkFlowInputParamBoList();
            workFlowInputParamBoStr = JSON.toJSONString(workFlowInputParamBoList);
            redisClient.set(RedisKey.workFlowInputParamBoList_suffix.name(),workFlowInputParamBoStr);
            redisClient.expire(RedisKey.workFlowInputParamBoList_suffix.name(),7200);
        }
        return workFlowInputParamBoList;
    }

    @Override
    public List<StyleBO> getStyleBOList() throws DataServiceException {
        return workFlowDataService.getStyleBOList();
    }

    @Override
    public List<WorkFlowTemplateNodeParamBo> getTemplateNodeParamByTemplateFlowId(long TemplateFlowId) throws DataServiceException {
        return workFlowDataService.getTemplateNodeParamByTemplateFlowId(TemplateFlowId);
    }

    @Override
    public List<WorkFlowNodeParamBo> getWorkFlowNodeParamByFlowDetailId(long workFlowDetailId) throws DataServiceException {
        return workFlowDataService.getWorkFlowNodeParamByFlowDetailId(workFlowDetailId);
    }

    @Override
    public Map<String, List<Map<String, Object>>> getWorkFlowNodeList() throws DataServiceException {
        Map<String,List<Map<String,Object>>> resultMap = new HashMap<>();
        List<Map<String,Object>> categoryList = new ArrayList<>();
        String path = WebUtil.getDpmbsUploadByEnv();
        //将jobTypeCategoryBOList存入缓存
        String jobTypeCategoryStr = redisClient.get(RedisKey.jobTypeCategoryBOList_suffix.name());
        List<JobTypeCategoryBO> jobTypeCategoryBOList;
        if(!Validate.isEmpty(jobTypeCategoryStr)){
            jobTypeCategoryBOList = JSONArray.parseArray(jobTypeCategoryStr,JobTypeCategoryBO.class);
        }else{
            //查询job_type_category表
            jobTypeCategoryBOList = workFlowDataService.getJobTypeCategoryBOList();
            if(Validate.isEmpty(jobTypeCategoryBOList)){
                throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
            }
            redisClient.set(RedisKey.jobTypeCategoryBOList_suffix.name(),JSON.toJSONString(jobTypeCategoryBOList));
            redisClient.expire(RedisKey.jobTypeCategoryBOList_suffix.name(),7200);//设置过期时间为2小时
        }
        //将styleBOList存入缓存
        String styleBOListStr = redisClient.get(RedisKey.styleBOList_suffix.name());
        List<StyleBO> styleBOList;
        if(!Validate.isEmpty(styleBOListStr)){
            styleBOList = JSONArray.parseArray(styleBOListStr,StyleBO.class);
        }else{
            //查询style表
            styleBOList = workFlowDataService.getStyleBOList();
            if(Validate.isEmpty(styleBOList)){
                throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
            }
            redisClient.set(RedisKey.styleBOList_suffix.name(),JSON.toJSONString(styleBOList));
            redisClient.expire(RedisKey.styleBOList_suffix.name(),7200);//设置过期时间为2小时
        }
        //将jobTypeInfoList存入缓存
        String jobTypeInfoListStr = redisClient.get(RedisKey.jobTypeInfoList_suffix.name());
        List<JobTypeInfo> jobTypeInfoList;
        if(!Validate.isEmpty(jobTypeInfoListStr)){
            jobTypeInfoList = JSONArray.parseArray(jobTypeInfoListStr,JobTypeInfo.class);
        }else {
            //查询job_type_info
            jobTypeInfoList = workFlowDataService.getJobTypeInfo();
            if(Validate.isEmpty(jobTypeInfoList)){
                throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
            }
            redisClient.set(RedisKey.jobTypeInfoList_suffix.name(),JSON.toJSONString(jobTypeInfoList));
            redisClient.expire(RedisKey.jobTypeInfoList_suffix.name(),7200);//设置过期时间为2小时
        }
        //聚合数据
        for(JobTypeCategoryBO jobTypeCategoryBO : jobTypeCategoryBOList){
            Map<String ,Object> map = new HashMap<>();
            map.put("id",jobTypeCategoryBO.getId());
            map.put("name",jobTypeCategoryBO.getName());
            categoryList.add(map);
        }
        resultMap.put("categoryList",categoryList);
        List<Map<String,Object>> nodeList = new ArrayList<>();

        //查询work_flow_input_param
        List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowDataService.getWorkFlowInputParamBoList();
        if(Validate.isEmpty(workFlowInputParamBoList)){
            throw new WebException(MySystemCode.ACTION_EXCEPTION_MESSAGE);
        }
        //聚合数据
        Map<Integer,StyleBO> styleBOMap = new HashMap<>();
        for(StyleBO styleBO : styleBOList){
            styleBOMap.put(styleBO.getId(),styleBO);
        }

        for(JobTypeInfo jobTypeInfo : jobTypeInfoList){
            if(jobTypeInfo.getTypeNo().equals("statisticalAnalysis")){
                continue;
            }
            Map<String ,Object> map = new HashMap<>();
            map.put("jobTypeCategoryId",jobTypeInfo.getJobTypeCategoryId());
            map.put("typeNo",jobTypeInfo.getTypeNo());
            map.put("typeName",jobTypeInfo.getTypeName());
            map.put("processType",jobTypeInfo.getProgressUrl());
            map.put("typeClassify",jobTypeInfo.getTypeClassify());
            map.put("jobClassify",jobTypeInfo.getJobClassify());
            map.put("inputNum",jobTypeInfo.getInputNum());
            map.put("queryUrl",jobTypeInfo.getQueryUrl());
            if(jobTypeInfo.getImgUrl()!=null){
                String[] imgArray = jobTypeInfo.getImgUrl().split(",");
                String imgStr = "";
                for(int i = 0;i<imgArray.length;i++){
                    imgStr += path+imgArray[i]+",";
                }
                imgStr = imgStr.substring(0,imgStr.length()-1);
                map.put("imgUrl",imgStr);
            }
            map.put("tip",jobTypeInfo.getTip());
            List<Map<String,Object>> inputParamList = new ArrayList<>();
            for(WorkFlowInputParamBo workFlowInputParamBo : workFlowInputParamBoList){
                if(workFlowInputParamBo.getTypeNo().equals(jobTypeInfo.getTypeNo())){
                    Map<String,Object> inputParamMap = new HashMap<>();
                    inputParamMap.put("inputParamId",workFlowInputParamBo.getId());
                    inputParamMap.put("paramEnName",workFlowInputParamBo.getParamEnName());
                    inputParamMap.put("paramCnName",workFlowInputParamBo.getParamCnName());
                    inputParamMap.put("restrictions",workFlowInputParamBo.getRestrictions());
                    inputParamMap.put("required",workFlowInputParamBo.getIsRequired());
                    inputParamMap.put("requestUrl",workFlowInputParamBo.getRequestUrl());
                    inputParamMap.put("filedMapping",JSONObject.parse(workFlowInputParamBo.getFiledMapping()));
                    inputParamMap.put("nextParamId",workFlowInputParamBo.getNextParamId());
                    inputParamMap.put("preParamId",workFlowInputParamBo.getPreParamId());
                    Integer styleId = workFlowInputParamBo.getStyleId();
                    inputParamMap.put("styleCode",styleBOMap.get(styleId)==null ? null:styleBOMap.get(styleId).getStyleCode());
                    inputParamMap.put("value","");
                    inputParamList.add(inputParamMap);
                }
            }
            map.put("paramArray",inputParamList);
            nodeList.add(map);
        }
        resultMap.put("nodeList",nodeList);
        return resultMap;
    }

    @Override
    @Transactional
    public List<Map<String,Object>> addVisWorkFlowNodeParam(String body) throws DataServiceException {
        String analysisLevel= "";
        JSONObject jsonObject = JSONObject.parseObject(body);
        String projectId = jsonObject.getString("projectId");
        String workFlowId = jsonObject.getString("workFlowId");
        String templateId = jsonObject.getString("templateId");
        JSONArray nodeList = jsonObject.getJSONArray("nodeList");
        String preRun=jsonObject.getString("preRun");
        //存放nodeId与flowDetailId的映射关系map
        Map<String,Long> nodeIdFlowDetailMap = new HashMap<>();
        //存放flowDetailId与workFlowDetail的映射关系map
        Map<Long,WorkFlowDetail> flowDetailIdWorkFlowDetailMap = new HashMap<>();
        //聚合传入的nodeList
        Map<Long,Object> nodeMap = new HashMap<>();
        for(Object node : nodeList){
            JSONObject j = JSONObject.parseObject(node.toString());
            JSONObject nodeJsonObject = j.getJSONObject("data");
            if(nodeJsonObject.containsKey("flowDetailId") && nodeJsonObject.get("flowDetailId")!=null){
                long flowDetailId = nodeJsonObject.getLong("flowDetailId");
                nodeMap.put(flowDetailId,node);
            }
        }
        //查询所有的输入参数（input_param）。
        List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowDataService.getWorkFlowInputParamBoList();
        //聚合所有的输入参数
        Map<Integer,WorkFlowInputParamBo> workFlowInputParamBoMap = new HashMap<>();
        for(WorkFlowInputParamBo workFlowInputParamBo : workFlowInputParamBoList){
            workFlowInputParamBoMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo);
        }

        //被删除节点的下级节点集合
        List<Long> deletedNextDetailList = new ArrayList<>();
        //这一步主要是判断是否有被删除的节点，如果有则需要将数据库的记录删除掉
        List<WorkFlowDetail> workFlowDetailList = workFlowDataService.getWorkFlowDetailByWorkFlowId(Long.parseLong(workFlowId));
        for(WorkFlowDetail workFlowDetail : workFlowDetailList){
            long flowDetailId = workFlowDetail.getFlowDetailId();
            Object object = nodeMap.get(flowDetailId);
            if(object==null){
                //首先尝试移除掉被删除的节点（这样做是为了如果连续删除多个节点，则只保留最后一个删除节点的下级节点）
                deletedNextDetailList.remove(flowDetailId);
                //获取即将被删除节点的下级节点（可能有多个），并将它们添加至beforehand
                String nextFlowDetailIdStr = workFlowDetail.getNextFlowDetailIds();
                if(!Validate.isEmpty(nextFlowDetailIdStr)){
                    for(String nextFlowDetailId : nextFlowDetailIdStr.split(",")){
                        deletedNextDetailList.add(Long.parseLong(nextFlowDetailId));
                    }
                }
                workFlowDataService.deleteWorkFlowDetailByDetailId(flowDetailId);
                workFlowDataService.deleteWorkFlowNodeParamByFlowDetailId(flowDetailId);
                workFlowDataService.deleteWorkFlowOutputFiledByFlowDetailId(flowDetailId);
            }
        }
        //先把nodeList中的data和nodeId进行关联。
        Map<String,JSONObject> dataNodeIdMap = new HashMap<>();
        //存放首节点的list
        List<String> firstNodeIdList = new ArrayList<>();
        JSONArray exchangeNodeList = new JSONArray();
        for(Object node : nodeList){
            JSONObject j = JSONObject.parseObject(node.toString());
            String nodeId = j.getString("nodeId");
            dataNodeIdMap.put(nodeId,j);
            //如果当前节点的上节点为空,则认为当前节点为首节点，或单独节点
            if(Validate.isEmpty(j.getJSONArray("froms"))){
                firstNodeIdList.add(nodeId);
            }
        }
        //遍历每一个首节点。
        for(String nodeId : firstNodeIdList){
            //利用递归将后面的所有节点都放在exchangeNodeList中
            exchangeNodeList = getNextNodeDataByNodeId(nodeId,dataNodeIdMap,exchangeNodeList);
        }
        //1 开始修改或添加节点信息 work_flow_detail
        for(Object node : exchangeNodeList){
            WorkFlowDetail workFlowDetail = new WorkFlowDetail();
            JSONObject j = JSONObject.parseObject(node.toString());
            JSONObject jsonObject1 = j.getJSONObject("data");
            Long flowDetailId = jsonObject1.getLong("flowDetailId");
            String nodeId = j.getString("nodeId");
            String typeNo = jsonObject1.getString("typeNo");
            Boolean isSave = jsonObject1.getBoolean("isSave");
            String nodeInfo = j.getString("nodeInfo");
            JSONArray jsonArray = jsonObject1.getJSONArray("paramArray");
            String datasourceType = null;
            String quartzTime = null;
            //这里主要是将输入参数里的数据源类型的值取出来，赋值给work_flow_detail表的datasourceType字段
            if(!Validate.isEmpty(jsonArray)) {
                for (Object o : jsonArray) {
                    JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                    String value = inputParamJson.getString("value");
                    WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamJson.getInteger("inputParamId"));
                    if(workFlowInputParamBo.getTypeNo().equals(typeNo) &&
                            (workFlowInputParamBo.getParamEnName().equals("datasourceTypeId")||
                                    workFlowInputParamBo.getParamEnName().equals("typeName"))){
                        datasourceType = value;
                        continue;
                    }
                    if(workFlowInputParamBo.getTypeNo().equals(typeNo)&&
                            (workFlowInputParamBo.getParamEnName().equals("crawlFreq")||
                            workFlowInputParamBo.getParamEnName().equals("startFreqTypeName"))){
                        quartzTime = value;
                        if(quartzTime.equals("* * * * * *")){
                            quartzTime = "";
                        }else{
                            if(quartzTime.endsWith("*")){
                                quartzTime = quartzTime.substring(0,quartzTime.length()-1);
                                quartzTime += "?";
                            }
                        }
                    }
                }
            }
            //如果节点存在，则表示更新，不存在则表示添加
            if(flowDetailId != null){
                workFlowDetail.setTypeNo(typeNo);
                workFlowDetail.setFlowDetailId(flowDetailId);
                workFlowDetail.setNodeInfo(nodeInfo);
                workFlowDetail.setDataSourceType(datasourceType);
                workFlowDetail.setQuartzTime(quartzTime);
                workFlowDetail.setSave(isSave);
                workFlowDataService.updateWorkFlowDetail(workFlowDetail);

            }else{
                //如果模板id不为空，这需要将模板的id添加至work_flow_detail
                if(!Validate.isEmpty(templateId)){
                    workFlowDetail.setWorkFlowTemplateId(Integer.parseInt(templateId));
                }
                workFlowDetail.setFlowId(0L);
                workFlowDetail.setProjectId(Long.parseLong(projectId));
                workFlowDetail.setWorkFlowId(Long.parseLong(workFlowId));
                workFlowDetail.setNodeInfo(nodeInfo);
                workFlowDetail.setTypeNo(typeNo);
                workFlowDetail.setSave(isSave);
                workFlowDetail.setDataSourceType(datasourceType);
                workFlowDetail.setQuartzTime(quartzTime);
                workFlowDataService.addWorkFlowDetail(workFlowDetail);
                flowDetailId = workFlowDetail.getFlowDetailId();

            }
            //将flowDetailId和workFlowDetail关联
            flowDetailIdWorkFlowDetailMap.put(flowDetailId,workFlowDetail);
            //将nodeId和flowDetailId进行关联
            nodeIdFlowDetailMap.put(nodeId,workFlowDetail.getFlowDetailId());
            String crawlType = "";
            //1.1 开始添加节点的参数配置
            for (Object o : jsonArray) {
                WorkFlowNodeParamBo workFlowNodeParamBo = new WorkFlowNodeParamBo();
                JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                int inputParamId = inputParamJson.getInteger("inputParamId");
                //查询出当前参数的inputParamId所对应的记录，用于在添加work_flow_node_param时，需要一些冗余字段
                WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamId);
                //先判断该控件的英文名，并根据不同控件将其值进行调整。
                switch (workFlowInputParamBo.getParamEnName()){
                    case "url":
                        String value = inputParamJson.getString("value");
                        String fileBase = System.getProperty("upload.dir");
                        String fileParamtertm = "/project/temp/";
                        String fileParamter = "/project/importfile/";
                        if(!Validate.isEmpty(value) && value.indexOf(fileParamter) < 0){
                            String url = value;
                            //把上传文件移动到服务器目录
                            String originalUrl = fileBase + fileParamtertm + url;
                            File file = new File(originalUrl);
                            if (file.exists()) {
                                //上传文件到hdfs。
                                baseSaoHDFS.uploadFile(originalUrl,fileBase + fileParamter + url);
                                file.delete();
                                workFlowNodeParamBo.setInputParamValue(fileBase + fileParamter + url);
                            }
                        }else{
                            workFlowNodeParamBo.setInputParamValue(value);
                        }
                        break;
                    case "storageTypeTable":
                        if(!Validate.isEmpty(workFlowDetail.getDataSourceType())){
                            StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(workFlowDetail.getDataSourceType()));
                            workFlowNodeParamBo.setInputParamValue(storageTypePO.getStorageTypeTable());
                        }
                        break;
                    case "wordSegmentationObject":
                        if(inputParamJson.getString("value")!=null &&
                                (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                        inputParamJson.getString("value").equals("topicAnalysisDefinition")||
                                        inputParamJson.getString("value").equals("themeAnalysisSetting")||
                                        inputParamJson.getString("value").equals(analysisLevel))){
                            workFlowNodeParamBo.setInputParamValue(analysisLevel);
                        }else{
                            workFlowNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                        }
                        break;
                    case "analysisLevel":
                        analysisLevel = inputParamJson.getString("value");
                        workFlowNodeParamBo.setInputParamValue(analysisLevel);
                        break;
                    case "themeAnalysisSettingObject":
                        if(inputParamJson.getString("value")!=null &&
                                (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                        inputParamJson.getString("value").equals("wordSegmentation")||
                                        inputParamJson.getString("value").equals("topicAnalysisDefinition")||
                                        inputParamJson.getString("value").equals(analysisLevel))){
                            workFlowNodeParamBo.setInputParamValue(analysisLevel);
                        }else{
                            workFlowNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                        }
                        break;
                    case "topicAnalysisDefinitionObject":
                        if(inputParamJson.getString("value")!=null &&
                                (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                        inputParamJson.getString("value").equals("wordSegmentation")||
                                        inputParamJson.getString("value").equals("themeAnalysisSetting")||
                                        inputParamJson.getString("value").equals(analysisLevel))){
                            workFlowNodeParamBo.setInputParamValue(analysisLevel);
                        }else{
                            workFlowNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                        }
                        break;
                    case "crawlFreq":
                        workFlowNodeParamBo.setInputParamValue(quartzTime);
                        break;
                    case "startFreqTypeName":
                        workFlowNodeParamBo.setInputParamValue(quartzTime);
                        break;
                    default:
                        if(workFlowInputParamBo.getParamEnName().equals("crawlType")){
                            crawlType = inputParamJson.getString("value");
                        }
                        workFlowNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                }
                //如果有paramId 则表示是更新。如果没有则表示添加
                if (inputParamJson.getInteger("paramId") != null) {
                    workFlowNodeParamBo.setParamId(inputParamJson.getLong("paramId"));
                    //这里这样子做是因为bdi项目会有非常多的数据源。不便将config存入数据库，而选择存入缓存
                    if(("6".equals(inputParamJson.getString("inputParamId")) || "2".equals(inputParamJson.getString("inputParamId"))) &&
                            !Validate.isEmpty(inputParamJson.getString("config"))){
                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                            redisClient.set(RedisKey.dataSourceConfig.name(),inputParamJson.getString("config"));
                        }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                            redisClient.set(RedisKey.mDataSourceConfig.name(),inputParamJson.getString("config"));
                        }
                    }else{
                        workFlowNodeParamBo.setConfig(inputParamJson.getString("config"));
                    }
                    workFlowDataService.updateWorkFlowNodeParam(workFlowNodeParamBo);
                } else {
                    workFlowNodeParamBo.setFlowDetailId(flowDetailId);
                    workFlowNodeParamBo.setProjectId(Long.parseLong(projectId));
                    workFlowNodeParamBo.setTypeNo(typeNo);
                    workFlowNodeParamBo.setWorkFlowId(Long.parseLong(workFlowId));
                    workFlowNodeParamBo.setInputParamId(inputParamJson.getInteger("inputParamId"));
                    workFlowNodeParamBo.setInputParamCnName(workFlowInputParamBo.getParamCnName());
                    workFlowNodeParamBo.setInputParamType(workFlowInputParamBo.getParamType());
                    //这里这样子做是因为bdi项目会有非常多的数据源。不便将config存入数据库，而选择存入缓存
                    if(("6".equals(inputParamJson.getString("inputParamId")) || "2".equals(inputParamJson.getString("inputParamId"))) &&
                            !Validate.isEmpty(inputParamJson.getString("config"))){
                        if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                            redisClient.set(RedisKey.dataSourceConfig.name(),inputParamJson.getString("config"));
                        }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                            redisClient.set(RedisKey.mDataSourceConfig.name(),inputParamJson.getString("config"));
                        }
                    }else{
                        workFlowNodeParamBo.setConfig(inputParamJson.getString("config"));
                    }
                    workFlowDataService.addWorkFlowNodeParam(workFlowNodeParamBo);
                }
            }

            //1.2 继续添加输出字段
            //首先判断其节点类型
            switch (typeNo){
                case Constants.WORK_FLOW_TYPE_NO_DATAIMPORT:
                    List<String> list = new ArrayList<>();
                    //取出导入节点的字段映射。然后查询base系统，进行聚合
                    for (Object o : jsonArray) {
                        JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                        WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamJson.getInteger("inputParamId"));
                        if(workFlowInputParamBo.getParamEnName().equals("origainRelation")){
                            String origainRelation = inputParamJson.getString("value");
                            if(!Validate.isEmpty(origainRelation)){
                                JSONArray origainRelationJson = JSONObject.parseArray(origainRelation);
                                for(Object object :origainRelationJson){
                                    JSONObject origainRelationJsonObject = JSONObject.parseObject(object.toString());
                                    if(!Validate.isEmpty(origainRelationJsonObject.getString("key"))){
                                        list.add(origainRelationJsonObject.getString("key"));
                                    }
                                }
                                //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                                if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                    visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                }
                                addWorkFlowOutputFiled(workFlowDetail,list,typeNo);
                                break;
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATACRAWL:
                    //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                    if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                        visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                    }
                    addWorkFlowOutputFiled(workFlowDetail,null,typeNo);
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL:
                    //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                    if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                        visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                    }
                    addWorkFlowOutputFiled(workFlowDetail,null,typeNo);
                    break;
                case Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT:
                    if(!Validate.isEmpty(analysisLevel)){
                        List<JobTypeResultField> jobTypeResultFieldList =
                                getJobTypeResultFieldListByResultTypeId(analysisLevel);
                        if(!Validate.isEmpty(jobTypeResultFieldList)){
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList){
                                String filedType = "";
                                VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowBO.setFiledType(filedType);
                                visWorkFlowBO.setStorageTypeTable(analysisLevel);
                                visWorkFlowBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION:
                    if(!Validate.isEmpty(analysisLevel)){
                        List<JobTypeResultField> jobTypeResultFieldList2 =
                                getJobTypeResultFieldListByResultTypeId(analysisLevel);
                        if(!Validate.isEmpty(jobTypeResultFieldList2)){
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList2){
                                if(jobTypeResultField.getColName().equals("主题json")||
                                        jobTypeResultField.getColName().equals("话题json")){
                                    continue;
                                }
                                String filedType = "";
                                VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowBO.setFiledType(filedType);
                                visWorkFlowBO.setStorageTypeTable(analysisLevel);
                                visWorkFlowBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING:
                    if(!Validate.isEmpty(analysisLevel)){
                        List<JobTypeResultField> jobTypeResultFieldList3 =
                                getJobTypeResultFieldListByResultTypeId(analysisLevel);
                        if(!Validate.isEmpty(jobTypeResultFieldList3)){
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList3){
                                if(jobTypeResultField.getColName().equals("分词结果")||
                                        jobTypeResultField.getColName().equals("关键词结果")||
                                        jobTypeResultField.getColName().equals("话题json")){
                                    continue;
                                }
                                String filedType = "";
                                VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowBO.setFiledType(filedType);
                                visWorkFlowBO.setStorageTypeTable(analysisLevel);
                                visWorkFlowBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION:
                    if(!Validate.isEmpty(analysisLevel)){
                        List<JobTypeResultField> jobTypeResultFieldList4 =
                                getJobTypeResultFieldListByResultTypeId(analysisLevel);
                        if(!Validate.isEmpty(jobTypeResultFieldList4)){
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList4){
                                if(jobTypeResultField.getColName().equals("分词结果")||
                                        jobTypeResultField.getColName().equals("关键词结果")||
                                        jobTypeResultField.getColName().equals("主题json")){
                                    continue;
                                }
                                String filedType = "";
                                VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                switch (jobTypeResultField.getFieldType()){
                                    case 1:
                                        filedType = "number";
                                        break;
                                    case 2:
                                        filedType = "text";
                                        break;
                                    case 3:
                                        filedType = "datetime";
                                        break;
                                }
                                visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowBO.setFiledType(filedType);
                                visWorkFlowBO.setStorageTypeTable(analysisLevel);

                                visWorkFlowBO.setIsCustomed(0);
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                    break;
                case Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT:
                    break;
            }
        }

        //该map主要用于在校验是用
        Map<Long,WorkFlowDetail> detailMap = new HashMap<>();
        //2 再次进行遍历 进行next_folw_detail和pre_flow_detail的更新
        for(Object node : nodeList){
            WorkFlowDetail workFlowDetail = new WorkFlowDetail();
            JSONObject j = JSONObject.parseObject(node.toString());
            String nodeId = j.getString("nodeId");
            //获取上节点的nodeId
            JSONArray froms = j.getJSONArray("froms");
            String preFlowDetailIds = "";
            if(!Validate.isEmpty(froms)){
                for(int i= 0;i<froms.size();i++){
                    Long preFlowDetailLong =  nodeIdFlowDetailMap.get(froms.get(i));
                    preFlowDetailIds += preFlowDetailLong+",";
                }
                preFlowDetailIds = preFlowDetailIds.substring(0,preFlowDetailIds.length()-1);
                workFlowDetail.setPrevFlowDetailIds(preFlowDetailIds);
            }else{
                workFlowDetail.setPrevFlowDetailIds("0");
            }
            //获取下节点的nodeId
            JSONArray tos = j.getJSONArray("tos");
            String nextFlowDetailIds = "";
            if(!Validate.isEmpty(tos)){
                for(int i= 0;i<tos.size();i++){
                    if(tos.get(i) instanceof JSONArray){
                        JSONArray subTos = ((JSONArray) tos.get(i));
                        for(int k = 0;k<subTos.size();k++){
                            Long nextFlowDetailLong =  nodeIdFlowDetailMap.get(subTos.get(k));
                            nextFlowDetailIds += nextFlowDetailLong+",";
                        }
                    }else{
                        Long nextFlowDetailLong =  nodeIdFlowDetailMap.get(tos.get(i));
                        nextFlowDetailIds += nextFlowDetailLong+",";
                    }
                }
                nextFlowDetailIds = nextFlowDetailIds.substring(0,nextFlowDetailIds.length()-1);
                workFlowDetail.setNextFlowDetailIds(nextFlowDetailIds);
            }else{
                workFlowDetail.setNextFlowDetailIds(nextFlowDetailIds);
            }
            workFlowDetail.setFlowDetailId(nodeIdFlowDetailMap.get(nodeId));
            workFlowDataService.updateWorkFlowDetail(workFlowDetail);
            detailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
        }


        //3 这一步主要判断该工作流里是否有条件节点，如果有则需要将条件节点的paramValue值附上deitailId
        for(Map.Entry<String,Long> entry : nodeIdFlowDetailMap.entrySet()){
            Long detailId = entry.getValue();//取出每一个nodeId所对应的detailId
            WorkFlowDetail workFlowDetail = flowDetailIdWorkFlowDetailMap.get(detailId);//查询出每个detailId对应的workFlowDetail节点信息
            if(workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_CONDITION)){//判断该节点是否为条件节点
                List<WorkFlowNodeParamBo> list = workFlowDataService.getWorkFlowNodeParamByFlowDetailId(detailId);//如果是的话则查询出该节点所有的输入参数值
                if(!Validate.isEmpty(list)){
                    loop : for(WorkFlowNodeParamBo workFlowNodeParamBo : list){//遍历该节点的所有参数值
                        int inputParamId = workFlowNodeParamBo.getInputParamId();
                        if(inputParamId == 42){//找到条件设置的输入参数，并取出其值
                            String inputParamValue = workFlowNodeParamBo.getInputParamValue();
                            JSONArray inputParamValueArray = JSONArray.parseArray(inputParamValue);//转为jsonArray
                            if(!Validate.isEmpty(inputParamValueArray)){//判空
                                for(Object o : inputParamValueArray){
                                    List<Long> nextDetailIdList = new ArrayList<>();
                                    JSONObject conditionJson =(JSONObject) o;//注意这里使用强转，不会产生新的对象
                                    JSONArray nodeIdArray = conditionJson.getJSONArray("nodeId");//取出其所有下节点的nodeId
                                    if(!Validate.isEmpty(nodeIdArray)){//判空
                                        for(Object nodeId : nodeIdArray){
                                            Long nextDetailId = nodeIdFlowDetailMap.get(nodeId);//取出每个下节点所对应的detailId，并放入list
                                            nextDetailIdList.add(nextDetailId);
                                        }
                                        conditionJson.put("nextDetailId",nextDetailIdList);//在新增一个jsonObject,同时inputParamValueArray也被改变
                                    }
                                }
                                workFlowNodeParamBo.setInputParamValue(inputParamValueArray.toJSONString());
                                workFlowDataService.updateWorkFlowNodeParam(workFlowNodeParamBo);//在将其重新更新
                                break loop;
                            }else{
                                break loop;
                            }
                        }
                    }
                }
            }
        }

        //4 该步骤主要解决为输出节点和数据筛选节点配置输出字段，因为这两个节点必须要上一个节点的信息，所以只能在更新了上下节点关系后才能添加输出字段
        List<String> TypeNoList = new ArrayList<>();
        TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT);
        TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATAFILTER);
        TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_CONDITION);
        List<WorkFlowDetail> workFlowDetailList1 = workFlowDataService.getWorkFlowDetailListByTypeNoListAndWorkFlowId(TypeNoList,Long.parseLong(workFlowId));
        if(!Validate.isEmpty(workFlowDetailList1)){
            for(WorkFlowDetail workFlowDetail : workFlowDetailList1){
                if(workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT)){
                    //查到上一个节点的信息，并判断上一个节点是什么类型的节点。
                    String preFlowDetailId = workFlowDetail.getPrevFlowDetailIds();
                    if("0".equals(preFlowDetailId)){
                        WorkFlowDetail workFlowDetail1 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(preFlowDetailId));
                        //获得上节点的输出字段。
                        List<VisWorkFlowBO> visWorkFlowList = visWorkFlowService.getVisWorkFlowList(Integer.parseInt(preFlowDetailId));
                        //如果上节点是抓取或者是导入，则需要将输出节点选择的字段和上节点的输出字段做比较。
                        if(workFlowDetail1.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)||
                                workFlowDetail1.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                            String [] fieldArray = null;
                            //更具该该输出节点的detailId拿到该输出节点的所有配置，然后找到一个叫‘选择字段’的配置。并取出其值
                            List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowDataService.getWorkFlowNodeParamByFlowDetailId(workFlowDetail.getFlowDetailId());
                            for(WorkFlowNodeParamBo workFlowNodeParamBo :workFlowNodeParamBoList){
                                if(workFlowNodeParamBo.getInputParamCnName().equals("选择字段")){
                                    String fields = workFlowNodeParamBo.getInputParamValue();
                                    if (!Validate.isEmpty(fields)) {
                                        fieldArray = fields.split(",");
                                        break;
                                    }
                                }
                            }
                            if(fieldArray!=null){
                                Map<String,VisWorkFlowBO> visWorkFlowBOMap = new HashMap<>();
                                for(VisWorkFlowBO visWorkFlowBO : visWorkFlowList){
                                    visWorkFlowBOMap.put(visWorkFlowBO.getFiledEnName(),visWorkFlowBO);
                                }
                                //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                                if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                    visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                }
                                for(int i = 0;i<fieldArray.length;i++){
                                    if(visWorkFlowBOMap.get(fieldArray[i]) != null){//如果有匹配的字段 就重新添加到输出节点的输出字段
                                        VisWorkFlowBO visWorkFlowBO = visWorkFlowBOMap.get(fieldArray[i]);
                                        visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                        visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                                    }
                                }
                            }
                        }else{
                            //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(VisWorkFlowBO visWorkFlowBO : visWorkFlowList){
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                            }
                        }
                    }
                }else if(workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAFILTER)||
                        workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_CONDITION)){
                    //查到上一个节点的信息。
                    String preFlowDetailId = workFlowDetail.getPrevFlowDetailIds();
                    if(!preFlowDetailId.equals("0")){
                        //根据业务。数据筛选节点和条件节点的上节点有且只有一个。
                        List<VisWorkFlowBO> list = visWorkFlowService.getVisWorkFlowList(Integer.parseInt(preFlowDetailId));
                        //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                        if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                            visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                        }
                        for(VisWorkFlowBO visWorkFlowBO : list){
                            visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());//将每个上节点的输出字段重新赋值给当前数据筛选节点
                            visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);//重新添加。。
                        }
                    }
                }
            }
        }

        //5 校验项目
        //如果有被删除的节点，则直接将他的下节点更新为无效。并将该工作流更新为 配置中
         int count = 0;
         if(!Validate.isEmpty(deletedNextDetailList)){
            for(Long detailId : deletedNextDetailList){
                //这里需要确认被删除的节点的下级节点是否已经连接了上级节点。如果连接了上级节点，则不更新状态
                WorkFlowDetail workFlowDetail = detailMap.get(detailId);
                if(workFlowDetail != null && "0".equals(workFlowDetail.getPrevFlowDetailIds())){
                    workFlowDetail.setFlowDetailId(detailId);
                    workFlowDetail.setJobStatus(5);
                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                }else{
                    //因为可能涉及到有多个被删除的节点，这里使用计数的方式，如果被删除节点的下级节点重新连接了上级节点，则+1。
                    count++;
                }
            }
            //最后判断所有的被删除节点的下级节点是否都连接了上级节点，如果全都链接了。则继续使用校验的方法。
             //如果没有全部链接，则更新工作流状态
            if(count == deletedNextDetailList.size()){
                verifyProjectFlowDetail(workFlowId,detailMap);
            }else{
                //并将当前工作流设置为配置中
                WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                workFlowListBO.setStatus(5);
                workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
            }
         }else{
             verifyProjectFlowDetail(workFlowId,detailMap);
         }

        //6 保存图片,如果有节点才保存。没有节点则不保存图片，并且将该工作流设置为配置中
        if(!Validate.isEmpty(nodeList)){
            String imgStr = jsonObject.getString("file");
            imgStr = imgStr.split(",")[1];
            upload = System.getProperty("upload.dir");

            if(Validate.isEmpty(upload)){
                upload = "/data/upload/dev_dpmbs/";
            }
            if(!upload.endsWith("/")){
                upload+="/";
            }

            String imgPath = upload+IMG_PATH+workFlowId+".png";
            LoggerUtil.infoTrace(loggerName,"图片路径为:"+imgPath);

            Base64Util.GenerateImage(imgStr,imgPath);
            WorkFlowListBO workFlowListBO = new WorkFlowListBO();
            workFlowListBO.setImg("/"+IMG_PATH+workFlowId+".png");
            workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
            workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
        }else{
            WorkFlowListBO workFlowListBO = new WorkFlowListBO();
            workFlowListBO.setStatus(5);
            workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
            workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
        }



        //用于将detailId和paramId返回给前端
        List<Map<String,Object>> reslutList = new ArrayList<>();
        for(Map.Entry<String,Long> entry : nodeIdFlowDetailMap.entrySet()){
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("nodeId",entry.getKey());
            resultMap.put("detailId",entry.getValue());
            List<WorkFlowNodeParamBo> list = workFlowDataService.getWorkFlowNodeParamByFlowDetailId(entry.getValue());
            List<Map<String,Object>> reslutParamList = new ArrayList<>();
            for(WorkFlowNodeParamBo workFlowNodeParamBo : list){
                Map<String,Object> map = new HashMap<>();
                map.put("paramId",workFlowNodeParamBo.getParamId());
                map.put("inputParamId",workFlowNodeParamBo.getInputParamId());
                reslutParamList.add(map);
            }
            resultMap.put("paramArray",reslutParamList);
            reslutList.add(resultMap);
        }
        return reslutList;
    }

    /**
     * 根据数据源类型查询出存储字段并添加至work_flow_output_filed表中
     * @param workFlowDetail
     * @param list
     * @throws WebException
     */
    public void addWorkFlowOutputFiled(WorkFlowDetail workFlowDetail,List<String> list,String typeNo)throws WebException{
        if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)||
                typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)){
            if(!Validate.isEmpty(workFlowDetail.getDataSourceType())){
                List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationHasRuleList(workFlowDetail.getDataSourceType(),typeNo);
                StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(workFlowDetail.getDataSourceType()));
                for(StorageTypeFieldPO storageTypeFieldPO :storageTypeFieldPOList){
                    //创建输出表bo
                    VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                    visWorkFlowBO.setFiledCnName(storageTypeFieldPO.getFieldCnName());
                    visWorkFlowBO.setFiledEnName(storageTypeFieldPO.getFieldEnName());
                    visWorkFlowBO.setFiledId(storageTypeFieldPO.getId());
                    visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                    visWorkFlowBO.setFiledType(storageTypeFieldPO.getFieldType());
                    visWorkFlowBO.setIsCustomed(0);
                    visWorkFlowBO.setStorageTypeTable(storageTypePO.getStorageTypeTable());
                    visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                }
            }
        }else if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)){
            List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(workFlowDetail.getDataSourceType());
            StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(workFlowDetail.getDataSourceType()));
            Map<String,StorageTypeFieldPO> map = new HashMap<>();
            for(StorageTypeFieldPO storageTypeFieldPO : storageTypeFieldPOList){
                map.put(storageTypeFieldPO.getFieldEnName(),storageTypeFieldPO);
            }
            for(String str : list){
                StorageTypeFieldPO storageTypeFieldPO = map.get(str);
                if(storageTypeFieldPO!=null){
                    //创建输出表bo
                    VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                    visWorkFlowBO.setFiledCnName(storageTypeFieldPO.getFieldCnName());
                    visWorkFlowBO.setFiledEnName(storageTypeFieldPO.getFieldEnName());
                    visWorkFlowBO.setFiledId(storageTypeFieldPO.getId());
                    visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                    visWorkFlowBO.setFiledType(storageTypeFieldPO.getFieldType());
                    visWorkFlowBO.setIsCustomed(0);
                    visWorkFlowBO.setStorageTypeTable(storageTypePO.getStorageTypeTable());
                    visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                }
            }
        }
    }


    /**
     * 根据ResultTypeId查询job_type_result_field得到不同分析层级的所有字段
     * @param analysisLevel
     * @return
     */
    public List<JobTypeResultField> getJobTypeResultFieldListByResultTypeId(String analysisLevel){
        long jobResultTypeId = 0;
        switch (analysisLevel){
            case "sentence":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_SENTENCE;
                break;
            case "section":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_SECTION;
                break;
            case "article":
                jobResultTypeId = Constants.JOB_RESULT_TYPE_ARTICLE;
                break;
        }
        List<JobTypeResultField> jobTypeResultFieldList = jobTypeService.getResultFieldListByResultTypeId(jobResultTypeId);
        return jobTypeResultFieldList;
    }

    @Override
    public List<WorkFlowNodeParamBo> getWrokFlowNodeParamListByMap(Long projectId, String typeNo) throws DataServiceException {
        return workFlowDataService.getWrokFlowNodeParamListByMap(projectId,typeNo);
    }

    @Override
    public List<WorkFlowNodeParamBo> getWorkFlowNodeParamByProjectId(long projectId) throws BizException {
        return workFlowDataService.getWorkFlowNodeParamByProjectId(projectId);
    }

    @Override
    public List<WorkFlowNodeParamBo> getWorkFlowNodeParamByWorkFlowId(long workFlowId) throws BizException {
        return workFlowDataService.getWorkFlowNodeParamByWorkFlowId(workFlowId);
    }

    /**
     * 校验项目
     * @param  detailMap
     * @param  workFlowId
     * @throws BizException
     */
    public void verifyProjectFlowDetail(String workFlowId,Map<Long,WorkFlowDetail> detailMap) throws BizException{
        //尝试校验各个节点的依赖。并修改其项目的状态
        //先根据workFlowId查询该工作流所有的输入参数值
        List<WorkFlowNodeParamBo> workFlowNodeParamBos = workFlowDataService.getWorkFlowNodeParamByWorkFlowId(Long.parseLong(workFlowId));
        //查询所有不能为空的参数
        List<WorkFlowInputParamRelationBO> workFlowInputParamRelationBOS = workFlowDataService.getWorkFlowInputParamRelationBO();
        //聚合
        Map<Integer,WorkFlowInputParamRelationBO> workFlowInputParamRelationBOMap = new HashMap<>();
        for(WorkFlowInputParamRelationBO workFlowInputParamRelationBO : workFlowInputParamRelationBOS){
            workFlowInputParamRelationBOMap.put(workFlowInputParamRelationBO.getInputParamId(),workFlowInputParamRelationBO);
        }

        boolean flag = true;
        loopOut:for(WorkFlowNodeParamBo workFlowNodeParamBo : workFlowNodeParamBos){//遍历所有的输入参数
            JSONArray jsonArray = null;
            int inputParamId = workFlowNodeParamBo.getInputParamId();
            WorkFlowInputParamRelationBO workFlowInputParamRelationBO = workFlowInputParamRelationBOMap.get(inputParamId);
            WorkFlowDetail workFlowDetail = detailMap.get(workFlowNodeParamBo.getFlowDetailId());
            if(workFlowDetail==null){
                continue;
            }
            if(workFlowInputParamRelationBO!=null){
                if(Validate.isEmpty(workFlowNodeParamBo.getInputParamValue())){//如果某一个输入参数的值为空，则表示该工作流配置未完成。需将工作流更新为配置中
                    workFlowDetail.setJobStatus(5);
                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                    WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                    workFlowListBO.setStatus(5);
                    workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                    workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
                    flag = false;
                    break;
                }else if(workFlowInputParamRelationBO.getRelationType().equals("rely")){
                    //目前设计为上节点只能有一个
                    String preDetailId = workFlowDetail.getPrevFlowDetailIds().split(",")[0];
                    if(!Validate.isEmpty(preDetailId)){
                        List<VisWorkFlowBO> visWorkFlowBOS = visWorkFlowService.getVisWorkFlowList(Integer.parseInt(preDetailId));
                        List<String> fieldEnNameList = new ArrayList<>();
                        for(VisWorkFlowBO visWorkFlowBO : visWorkFlowBOS){
                            fieldEnNameList.add(visWorkFlowBO.getFiledEnName());
                        }
                        switch (workFlowInputParamRelationBO.getInputParamId()) {
                            case 18:
                                //jsonArray = JSONObject.parseObject(workFlowNodeParamBo.getConfig()).getJSONArray("list");
                                jsonArray = JSONObject.parseArray(workFlowNodeParamBo.getInputParamValue());
                                for (Object o1 : jsonArray) {
                                    JSONObject jsonObject1 = JSONObject.parseObject(o1.toString());
                                    String fieldEnName = jsonObject1.getString("contentType");
                                    //String fieldEnName = jsonObject1.getString("fieldEnName");
                                    if (!fieldEnNameList.contains(fieldEnName)) {
                                        //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                        workFlowDetail.setJobStatus(5);
                                        workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                        //并将当前工作流设置为配置中
                                        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                                        workFlowListBO.setStatus(5);
                                        workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                                        workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
                                        flag = false;
                                        break loopOut;
                                    }
                                }
                                if(flag){
                                    workFlowDetail.setJobStatus(0);
                                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                }
                                break;
                            case 40:
                                jsonArray = JSONObject.parseArray(workFlowNodeParamBo.getInputParamValue());
                                for (Object o1 : jsonArray) {
                                    JSONObject jsonObject1 = JSONObject.parseObject(o1.toString());
                                    String filed = jsonObject1.getString("filed");
                                    if (!fieldEnNameList.contains(filed)) {
                                        //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                        workFlowDetail.setJobStatus(5);
                                        workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                        //并将当前工作流设置为配置中
                                        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                                        workFlowListBO.setStatus(5);
                                        workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                                        workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
                                        flag = false;
                                        break loopOut;
                                    }
                                }
                                if(flag){
                                    workFlowDetail.setJobStatus(0);
                                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                }
                                break;
                            case 42:
                                jsonArray = JSONObject.parseArray(workFlowNodeParamBo.getInputParamValue());
                                for (Object o1 : jsonArray) {
                                    JSONObject jsonObject1 = JSONObject.parseObject(o1.toString());
                                    JSONArray paramArray = jsonObject1.getJSONArray("paramArray");
                                    if(!Validate.isEmpty(paramArray)){
                                        for(Object param : paramArray){
                                            JSONObject paramJson = JSONObject.parseObject(param.toString());
                                            String filed = paramJson.getString("filed");
                                            if (!fieldEnNameList.contains(filed)) {
                                                //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                                workFlowDetail.setJobStatus(5);
                                                workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                                //并将当前工作流设置为配置中
                                                WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                                                workFlowListBO.setStatus(5);
                                                workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                                                workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
                                                flag = false;
                                                break loopOut;
                                            }
                                        }
                                    }
                                }
                                if(flag){
                                    workFlowDetail.setJobStatus(0);
                                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                }
                                break;
                            case 37:
                                //如果是输出节点的字段
                                String[] strArray = workFlowNodeParamBo.getInputParamValue().split(",");
                                WorkFlowDetail workFlowDetail1 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(preDetailId));
                                if(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT.equals(workFlowDetail1.getTypeNo())){
                                   List<StorageTypeFieldPO> datasourceTypeId1 =dataSourceTypeService.getDataSourceTypeRelationList
                                           (workFlowDetail1.getDataSourceType());
                                    List<String> fieldEnNameList1 = new ArrayList<>();
                                    for (StorageTypeFieldPO storageTypeFieldPO:datasourceTypeId1
                                         ) {
                                        fieldEnNameList1.add(storageTypeFieldPO.getFieldEnName());
                                    }
                                    for (String o1 : strArray) {
                                        if (!fieldEnNameList1.contains(o1)) {
                                            //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                            workFlowDetail.setJobStatus(5);
                                            workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                            //并将当前工作流设置为配置中
                                            WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                                            workFlowListBO.setStatus(5);
                                            workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                                            workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
                                            flag = false;
                                            break loopOut;
                                        }
                                    }
                                }else {
                                    for (String o1 : strArray) {
                                        if (!fieldEnNameList.contains(o1)) {
                                            //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                            workFlowDetail.setJobStatus(5);
                                            workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                            //并将当前工作流设置为配置中
                                            WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                                            workFlowListBO.setStatus(5);
                                            workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                                            workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
                                            flag = false;
                                            break loopOut;
                                        }
                                    }
                                }
                                if(flag){
                                    workFlowDetail.setJobStatus(0);
                                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                }
                                break;
                            case 44:
                                jsonArray = JSONObject.parseArray(workFlowNodeParamBo.getInputParamValue());
                                for (Object o1 : jsonArray) {
                                    JSONObject jsonObject1 = JSONObject.parseObject(o1.toString());
                                    String filed = jsonObject1.getString("fieldEnName");
                                    if (!fieldEnNameList.contains(filed)) {
                                        //如果上节点的输出不包含当前节点的输入。则将当前节点的状态设置为无效节点
                                        workFlowDetail.setJobStatus(5);
                                        workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                        //并将当前工作流设置为配置中
                                        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                                        workFlowListBO.setStatus(5);
                                        workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                                        workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
                                        flag = false;
                                        break loopOut;
                                    }
                                }
                                if(flag){
                                    workFlowDetail.setJobStatus(0);
                                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                }
                                break;
                            default:
                                String storageTable = workFlowNodeParamBo.getInputParamValue();
                                if (!visWorkFlowBOS.get(0).getStorageTypeTable().equals(storageTable)) {
                                    WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                                    workFlowListBO.setStatus(5);
                                    workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                                    workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
                                    flag = false;
                                    break loopOut;
                                }
                                if(flag){
                                    workFlowDetail.setJobStatus(0);
                                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                                }
                        }
                    }
                }else if(workFlowNodeParamBo.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL) || workFlowNodeParamBo.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)){
                    workFlowDetail.setJobStatus(0);
                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                }
            }
        }
        if(flag){
            //如果所有节点的配置都没有问题则将该工作流设置为未启动状态
            WorkFlowListBO workFlowListBO = new WorkFlowListBO();
            workFlowListBO.setStatus(3);
            workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
            workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
        }
    }

    @Override
    public List<WorkFlowListBO> getWorkFlowListPOByFilter(WorkFlowListFilter workFlowListFilter) throws BizException {
        return workFlowDataService.getWorkFlowListPOByFilter(workFlowListFilter);
    }

    @Override
    public List<WorkFlowListBO> getWorkFlowListByIncludeStatus(List<String> conditions,Long projectId) throws BizException {
        return workFlowDataService.getWorkFlowListByIncludeStatus(conditions,projectId);
    }

    @Override
    public void addWorkFlowListBO(WorkFlowListBO workFlowListBO) throws BizException {
        workFlowDataService.addWorkFlowListBO(workFlowListBO);
    }

    @Override
    public Long getWorkFlowListBOByWorkFlowName(Long projectId,String workFlowName) throws BizException {
        return workFlowDataService.getWorkFlowListBOByWorkFlowName(projectId,workFlowName);
    }

    @Override
    @Transactional
    public void delWorkFlowByWorkFlowId(Long workFlowId) throws BizException {
        List<WorkFlowDetail> workFlowDetailList = workFlowDataService.getWorkFlowDetailByWorkFlowId(workFlowId);
        if(!Validate.isEmpty(workFlowDetailList)){
            workFlowDataService.delWorkFlowDetailByWorkFlowId(workFlowId);
            workFlowDataService.delWorkFlowNodeParamByWorkFlowId(workFlowId);
            workFlowDataService.delWorkFlowListByWorkFlowId(workFlowId);
            workFlowDataService.deleteWorkFlowOutputFiledByFlowDetailIdList(workFlowDetailList);
        }else{
            workFlowDataService.delWorkFlowListByWorkFlowId(workFlowId);
        }

    }

    @Override
    public List<ParamBO> getDownloadCoding() throws BizException {
        return workFlowDataService.getDownloadCoding();
    }

    @Override
    public WorkFlowListBO getWorkFlowListBOByWorkFlowId(Long workFlowId) throws BizException {
        return workFlowDataService.getWorkFlowListBOByWorkFlowId(workFlowId);
    }

    @Override
    @Transactional
    public void startVisWorkFlow(Long workFlowId,String preRun,String crawalPage) throws BizException {
        LoggerUtil.infoTrace("==================工作流开始执行啦==============================");
        List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();
        Map<String,JobTypeInfo> jobTypeInfoMap = new HashMap<>();
        for (JobTypeInfo jobTypeInfo:jobTypeInfoList) {
            jobTypeInfoMap.put(jobTypeInfo.getTypeNo(),jobTypeInfo);
        }
        //获取所有的输入参数配置
        List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowDataService.getWorkFlowInputParamBoList();
        Map<Integer,String> inputParamMap = new HashMap<>();
        for (WorkFlowInputParamBo workFlowInputParamBo:workFlowInputParamBoList) {
            inputParamMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo.getParamEnName());
        }
        //获取该工作流下所有的节点信息
        List<WorkFlowDetail> workFlowDetailList = workFlowDataService.getWorkFlowDetailByWorkFlowId(workFlowId);
        if(Validate.isEmpty(workFlowDetailList)){
            throw new WebException("数据为空");
        }
        Map<Long,WorkFlowDetail> workFlowDetailMap = new HashMap<>();
        for(WorkFlowDetail workFlowDetail : workFlowDetailList){
            workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
        }
        //获取该工作流下所有的节点的输入参数配置的值
        List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowDataService.getWorkFlowNodeParamByWorkFlowId(workFlowId);
        if(Validate.isEmpty(workFlowNodeParamBoList)){
            throw new WebException("数据为空");
        }
        List<WorkFlowParam> workFlowParamList = new ArrayList<>();
        Map<Long, net.sf.json.JSONObject> detailJsonMap = new HashMap<>();
        //遍历workFlowNodeParamBoList
        for(WorkFlowNodeParamBo  workFlowNodeParamBo :workFlowNodeParamBoList){
            int inputParamId = workFlowNodeParamBo.getInputParamId();
            long flowDetailId = workFlowNodeParamBo.getFlowDetailId();
            WorkFlowDetail workFlowDetail=workFlowDetailMap.get(flowDetailId);
            if(workFlowDetail.getJobStatus()==5){
                    continue;
            }
            net.sf.json.JSONObject jsonObject = detailJsonMap.get(flowDetailId);
            if(null == jsonObject){
                jsonObject = new net.sf.json.JSONObject();
                detailJsonMap.put(flowDetailId,jsonObject);
            }

            String paramEnName = inputParamMap.get(inputParamId);
            String paramValue = workFlowNodeParamBo.getInputParamValue();
            if(Validate.isEmpty(paramValue)){
                paramValue = "";
            }
            //判断是否是试运行
            if(preRun==null){
                jsonObject.put(paramEnName,paramValue);
            }else{
                //判断数据源类型的输入参数
                if(inputParamId==9&& !Validate.isEmpty(paramValue)){
                    LoggerUtil.debugTrace("paramValue"+paramValue);
                    net.sf.json.JSONArray jsonArray= net.sf.json.JSONArray.fromObject(paramValue);
                    net.sf.json.JSONArray newJsonArray=new net.sf.json.JSONArray();
                    for (Object jsonArrayObj:jsonArray){
                        net.sf.json.JSONObject jsonObject1= net.sf.json.JSONObject.fromObject(jsonArrayObj);
                        String paramEnNames=jsonObject1.getString("paramEnName");
                        //判断参数是否有页码,如果有页码就把页面上输入的参数设置进去
                        if(paramEnNames.equals("page")){
                            String paramValues=jsonObject1.getString("paramValue");
                            net.sf.json.JSONObject jsonPage= net.sf.json.JSONObject.fromObject(paramValues);
                            jsonPage.put("end",crawalPage);
                            jsonObject1.put("paramValue",jsonPage);
                            newJsonArray.add(jsonObject1.toString());
                        }else{
                            newJsonArray.add(jsonArrayObj);
                        }

                    }
                    jsonObject.put(paramEnName,newJsonArray.toString());
                }else{
                    jsonObject.put(paramEnName,paramValue);
                }
            }


        }

        for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
            if(workFlowDetail.getJobStatus()==5){
                continue;
            }
            WorkFlowParam workFlowParam = new WorkFlowParam();

            net.sf.json.JSONObject jsonParamObject = new net.sf.json.JSONObject();

            net.sf.json.JSONObject jsonObject = detailJsonMap.get(workFlowDetail.getFlowDetailId());
            if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(workFlowDetail.getTypeNo())
                    ||Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(workFlowDetail.getTypeNo())){

                String crawlType = jsonObject.getString("crawlType");

                if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                    crawlType = "1";
                    workFlowDetail.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
                    workFlowParam.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATACRAWL);
                }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                    crawlType = "2";
                    workFlowDetail.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);
                    workFlowParam.setTypeNo(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL);
                }

                Object datasourceIdObj = jsonObject.get("datasourceId");
                Object datasourceTypeId = jsonObject.get("datasourceTypeId");

                DatasourcePO datasourcePO = dataSourceTypeService.getDatasourceById(Long.parseLong(datasourceIdObj.toString()),crawlType);
                DatasourceTypePO datasourceTypePO = dataSourceTypeService.getDataSourceTypeById(Long.parseLong(datasourceTypeId.toString()));

                jsonObject.put("datasourceName",datasourcePO.getDatasourceName());
                jsonObject.put("datasourceTypeName",datasourceTypePO.getTypeName());

                net.sf.json.JSONObject jsonParam = new net.sf.json.JSONObject();

                jsonObject.put("taskName","抓取"+datasourceTypePO.getTypeName());

                jsonParam.put("jsonParam",jsonObject);

                jsonParamObject = jsonParam;
            }else if(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT.equals(workFlowDetail.getTypeNo())){
                jsonParamObject = jsonObject;
            }else if(Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT.equals(workFlowDetail.getTypeNo())){

                net.sf.json.JSONObject jsonParam = new net.sf.json.JSONObject();
                jsonParam.put("jsonParam",jsonObject);
                jsonParamObject = jsonParam;
            }else if(Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION.equals(workFlowDetail.getTypeNo())){
                jsonParamObject = jsonObject;
            }else if(Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING.equals(workFlowDetail.getTypeNo())){
                jsonParamObject.putAll(jsonObject);
                jsonParamObject.putAll(jsonObject.getJSONObject("resultsStrategyType"));
            }else if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(workFlowDetail.getTypeNo())){
                jsonParamObject = jsonObject;
            }else if(Constants.WORK_FLOW_TYPE_NO_DATAFILTER.equals(workFlowDetail.getTypeNo())){
                jsonParamObject = jsonObject;
            }else if(Constants.WORK_FLOW_TYPE_NO_PUShOSS.equals(workFlowDetail.getTypeNo())){
                jsonParamObject = jsonObject;
            }else if(Constants.WORK_FLOW_TYPE_NO_CONDITION.equals(workFlowDetail.getTypeNo())){
                jsonParamObject = jsonObject;
            }

            workFlowParam.setFlowId(workFlowDetail.getFlowId());
            workFlowParam.setParamType(WorkFlowParam.PARAM_TYPE_PRIVATE);
            workFlowParam.setFlowDetailId(workFlowDetail.getFlowDetailId());
            workFlowParam.setTypeNo(workFlowDetail.getTypeNo());
            workFlowParam.setProjectId(workFlowDetail.getProjectId());
            workFlowParam.setJsonParam(jsonParamObject.toString());
            workFlowParam.setWorkFlowId(workFlowId);
            workFlowParamList.add(workFlowParam);
        }

        String workFlowDetailListStr = JSON.toJSONString(workFlowDetailList);
        String workFlowParamListStr = JSON.toJSONString(workFlowParamList);
        redisClient.set(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId,workFlowDetailListStr);
        redisClient.expire(RedisKey.startWorkFlowDetailList_suffix.name()+workFlowId,4*3600);
        redisClient.set(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId,workFlowParamListStr);
        redisClient.expire(RedisKey.startWorkFlowParamList_suffix.name()+workFlowId,4*3600);
        Map<Long,WorkFlowParam> workFlowParamMap = new HashMap<>();
        for (WorkFlowParam workFlowParam:workFlowParamList) {
            workFlowParamMap.put(workFlowParam.getFlowDetailId(),workFlowParam);
        }

        //找出流程节点和状态节点
        List<Long> detailIdList = new ArrayList<>();
        for (WorkFlowDetail workFlowDetail:workFlowDetailList) {
            long detailId = workFlowDetail.getFlowDetailId();

            JobTypeInfo jobTypeInfo = jobTypeInfoMap.get(workFlowDetail.getTypeNo());

            if(null != jobTypeInfo){

                //如果是话题 则 需要判断 是否是流程节点 还是 状态 节点
                if(Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION.equals(jobTypeInfo.getTypeNo())){
                    //如果是 可视化 项目 则 话题 是 状态节点
                    jobTypeInfo.setJobClassify(JobTypeInfo.JOB_CLASSIFY_STATUS);
                }

                int jobClassify = jobTypeInfo.getJobClassify();
                if(JobTypeInfo.JOB_CLASSIFY_PROCESS == jobClassify){
                    detailIdList.add(detailId);
                }else if(JobTypeInfo.JOB_CLASSIFY_STATUS == jobClassify){
                    int jobStatus = workFlowDetail.getJobStatus();

                    //如果状态节点报错了
                    if(9 == jobStatus){
                        //状态节点 目前 也是 通过 zk来传递的则也通过 恢复zk的错误任务就好。
                        detailIdList.add(detailId);
                    }

                }
            }

        }

        new Thread(new VisWorkFlowStart(workFlowId,workFlowParamMap,preRun)).start();//开始执行工作流启动
        //将所有的流程节点更新为进行中
        workFlowDataService.updateWorkFlowDetailToStartByWorkFlowId(workFlowId,detailIdList);
        //判断该工作流的运行状态是不是试运行
        if(preRun==null){
            //更新工作流状态为进行中
            WorkFlowListBO workFlowListBO = new WorkFlowListBO();
            workFlowListBO.setStatus(1);
            workFlowListBO.setWorkFlowId(workFlowId);
            workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
            //根据工作流id查询出项目id。
            WorkFlowListBO workFlowListBO1 = workFlowDataService.getWorkFlowListBOByWorkFlowId(workFlowId);
            ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
            //将工作流项目状态设置为进行中
            projectStatusFilter.setStatus(3);
            projectStatusFilter.setId(workFlowListBO1.getProjectId());
            projectDataService.updateProjectStatus(projectStatusFilter);
        }else{
            //更新工作流状态为进行中
            WorkFlowListBO workFlowListBO = new WorkFlowListBO();
            workFlowListBO.setStatus(6);
            workFlowListBO.setProgress("-");
            workFlowListBO.setWorkFlowId(workFlowId);
            workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
        }
    }

    @Override
    public List<WorkFlowDetail> getFirstDetailByWorkFlowId(Long workFlowId) throws BizException {
        return workFlowDataService.getFirstDetailByWorkFlowId(workFlowId);
    }

    @Override
    public void updateWorkFlowListStatus(WorkFlowListBO workFlowListBO) throws BizException {
        workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
    }

    @Override
    public boolean stopWorkFlowListByWorkFlowId(Long workFlowId) throws BizException {
        //首先将工作流状态更新为停止
        WorkFlowListBO workFlowListBO = new WorkFlowListBO();
        workFlowListBO.setWorkFlowId(workFlowId);
        workFlowListBO.setStatus(4);
        workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
        //停止所有未启动的节点
        workFlowDataService.updateWorkFlowDetailToStopByWorkFlowId(workFlowId);
        //停止所有首节点
        List<WorkFlowDetail> workFlowDetailList = workFlowDataService.getFirstDetailByWorkFlowId(workFlowId);
        if(!Validate.isEmpty(workFlowDetailList)){
            String crawlServer = WebUtil.getCrawlServerByEnv();

            for (WorkFlowDetail workFlowDetail:workFlowDetailList) {

                try {
                    QuartzManager.removeJob(workFlowDetail.getTypeNo() + workFlowDetail.getFlowDetailId());
                }catch (Exception e){
                    e.printStackTrace();
                }

                String typeNo = workFlowDetail.getTypeNo();
                if(typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                    workFlowDataService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetail.getFlowDetailId());

                    redisClient.del(RedisKey.totalTaskNum_suffix.name()+workFlowDetail.getFlowDetailId());
                    redisClient.del(RedisKey.finishNum_suffix.name()+workFlowDetail.getFlowDetailId());
                    redisClient.del(RedisKey.resultNum_suffix.name()+workFlowDetail.getFlowDetailId());

                    Map<String,String> postData = new HashMap<>();
                    postData.put("detailId",Long.toString(workFlowDetail.getFlowDetailId()));
                    Object obj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServer+"/crawlTask/stopCrawl.json","post",postData);
                    if(null != obj){
                        return true;
                    }
                }else if (typeNo.equals(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL)) {
                    workFlowDataService.updateWorkFlowDetailToStopByWorkFlowDetailId(workFlowDetail.getFlowDetailId());

                    redisClient.del(RedisKey.totalTaskNum_suffix.name()+workFlowDetail.getFlowDetailId());
                    redisClient.del(RedisKey.finishNum_suffix.name()+workFlowDetail.getFlowDetailId());
                    redisClient.del(RedisKey.resultNum_suffix.name()+workFlowDetail.getFlowDetailId());

                    String mCrawlServer = WebUtil.getMCrawlServerByEnv();
                    Map<String,String> postData = new HashMap<>();
                    postData.put("detailId",Long.toString(workFlowDetail.getFlowDetailId()));
                    Object obj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),mCrawlServer+"/crawlTask/stopCrawl.json","post",postData);
                    if(null != obj){
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public long workFlowListCount(Long projectId,Integer status) throws BizException{
        return workFlowDataService.workFlowListCount(projectId,status);
    }
    @Override
    public void updateWorkFlowDetailByWorkFlowId(Long workFlowId) throws BizException {
        workFlowDataService.updateWorkFlowDetailByWorkFlowId(workFlowId);
    }
    //递归查询将所有的下级节点的参数以及信息加载至exchangeNodeList中
    public JSONArray getNextNodeDataByNodeId(String nodeId,Map<String,JSONObject> dataNodeIdMap,JSONArray exchangeNodeList){
        JSONObject jsonObject1 = dataNodeIdMap.get(nodeId);
        exchangeNodeList.add(jsonObject1);
        JSONArray jsonArray = jsonObject1.getJSONArray("tos");
        if(Validate.isEmpty(jsonArray)){
            return exchangeNodeList;
        }else{
            for(Object o : jsonArray){
                if(o instanceof String){
                    exchangeNodeList = getNextNodeDataByNodeId(o.toString(),dataNodeIdMap,exchangeNodeList);
                }else{
                    for(Object o1 : (JSONArray)o){
                        exchangeNodeList = getNextNodeDataByNodeId(o1.toString(),dataNodeIdMap,exchangeNodeList);
                    }
                }
            }
        }
        return exchangeNodeList;
    }
    public Map<String,Object> addVisWorkFlowNodeParamAndRun(String body) throws DataServiceException {
        JSONObject jsonObject = JSONObject.parseObject(body);
        //保存可视化节点信息
        addVisWorkFlowNodeParam(body);
        String preRun=jsonObject.getString("preRun");
        String workFlowId = jsonObject.getString("workFlowId");
        String crawalPage=jsonObject.getString("crawalPage");
        //查询该工作流下的所有节点
        List<WorkFlowDetail>  workFlowDetailList =getWorkFlowDetailByWorkFlowId(Long.parseLong(workFlowId));
        //查询该工作流下的无效节点
        List<WorkFlowDetail> workFlowDetailList1=getWorkFlowDetailListByWorkFlowIdAndStatus(Long.parseLong(workFlowId));
        //判断无效节点的数量和总节点的数量是否相等
        Map<String,Object> map=new HashMap<>();
        if(workFlowDetailList.size()==workFlowDetailList1.size()){
            map.put("message","试运行失败,请检查节点信息");
            return  map;
        }
        //试运行该工作流
        startVisWorkFlow(Long.parseLong(workFlowId),preRun,crawalPage);
        map.put("message","试运行成功");
        return  map;
    }

    @Override
    public List<Map<String, Object>> saveOnceWorkFlowProjectNodeInfo(String body) {
            String analysisLevel= "";
            JSONObject jsonObject = JSONObject.parseObject(body);
            String projectId = jsonObject.getString("projectId");
            String workFlowId = jsonObject.getString("workFlowId");
            JSONArray nodeList = jsonObject.getJSONArray("nodeList");
            //存放nodeId与flowDetailId的映射关系map
            Map<String,Long> nodeIdFlowDetailMap = new HashMap<>();
            //存放flowDetailId与workFlowDetail的映射关系map
            Map<Long,WorkFlowDetail> flowDetailIdWorkFlowDetailMap = new HashMap<>();
            //聚合传入的nodeList
            Map<Long,Object> nodeMap = new HashMap<>();
            for(Object node : nodeList){
                JSONObject j = JSONObject.parseObject(node.toString());
                JSONObject nodeJsonObject = j.getJSONObject("data");
                if(nodeJsonObject.containsKey("flowDetailId") && nodeJsonObject.get("flowDetailId")!=null){
                    long flowDetailId = nodeJsonObject.getLong("flowDetailId");
                    nodeMap.put(flowDetailId,node);
                }
            }
            //查询所有的输入参数（input_param）。
            List<WorkFlowInputParamBo> workFlowInputParamBoList = workFlowDataService.getWorkFlowInputParamBoList();
            //聚合所有的输入参数
            Map<Integer,WorkFlowInputParamBo> workFlowInputParamBoMap = new HashMap<>();
            for(WorkFlowInputParamBo workFlowInputParamBo : workFlowInputParamBoList){
                workFlowInputParamBoMap.put(workFlowInputParamBo.getId(),workFlowInputParamBo);
            }
            //先把nodeList中的data和nodeId进行关联。
            Map<String,JSONObject> dataNodeIdMap = new HashMap<>();
            //存放首节点的list
            List<String> firstNodeIdList = new ArrayList<>();
            JSONArray exchangeNodeList = new JSONArray();
            for(Object node : nodeList){
                JSONObject j = JSONObject.parseObject(node.toString());
                String nodeId = j.getString("nodeId");
                dataNodeIdMap.put(nodeId,j);
                //如果当前节点的上节点为空,则认为当前节点为首节点，或单独节点
                if(Validate.isEmpty(j.getJSONArray("froms"))){
                    firstNodeIdList.add(nodeId);
                }
            }
            //遍历每一个首节点。
            for(String nodeId : firstNodeIdList){
                //利用递归将后面的所有节点都放在exchangeNodeList中
                exchangeNodeList = getNextNodeDataByNodeId(nodeId,dataNodeIdMap,exchangeNodeList);
            }
            //1 开始修改或添加节点信息 work_flow_detail
            for(Object node : exchangeNodeList){
                WorkFlowDetail workFlowDetail = new WorkFlowDetail();
                JSONObject j = JSONObject.parseObject(node.toString());
                JSONObject jsonObject1 = j.getJSONObject("data");
                Long flowDetailId = jsonObject1.getLong("flowDetailId");
                String nodeId = j.getString("nodeId");
                String typeNo = jsonObject1.getString("typeNo");
                Boolean isSave = jsonObject1.getBoolean("isSave");
                String nodeInfo = j.getString("nodeInfo");
                JSONArray jsonArray = jsonObject1.getJSONArray("paramArray");
                String datasourceType = null;
                String quartzTime = null;
                //这里主要是将输入参数里的数据源类型的值取出来，赋值给work_flow_detail表的datasourceType字段
                if(!Validate.isEmpty(jsonArray)) {
                    for (Object o : jsonArray) {
                        JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                        String value = inputParamJson.getString("value");
                        WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamJson.getInteger("inputParamId"));
                        if(workFlowInputParamBo.getTypeNo().equals(typeNo) &&
                                (workFlowInputParamBo.getParamEnName().equals("datasourceTypeId")||
                                        workFlowInputParamBo.getParamEnName().equals("typeName"))){
                            datasourceType = value;
                            continue;
                        }
                        if(workFlowInputParamBo.getTypeNo().equals(typeNo)&&
                                (workFlowInputParamBo.getParamEnName().equals("crawlFreq")||
                                        workFlowInputParamBo.getParamEnName().equals("startFreqTypeName"))){
                            quartzTime = value;
                            if(quartzTime.equals("* * * * * *")){
                                quartzTime = "";
                            }else{
                                if(quartzTime.endsWith("*")){
                                    quartzTime = quartzTime.substring(0,quartzTime.length()-1);
                                    quartzTime += "?";
                                }
                            }
                        }
                    }
                }
                //如果节点存在，则表示更新，不存在则表示添加
                if(flowDetailId != null){
                    workFlowDetail.setTypeNo(typeNo);
                    workFlowDetail.setFlowDetailId(flowDetailId);
                    workFlowDetail.setNodeInfo(nodeInfo);
                    workFlowDetail.setDataSourceType(datasourceType);
                    workFlowDetail.setQuartzTime(quartzTime);
                    workFlowDetail.setSave(isSave);
                    workFlowDataService.updateWorkFlowDetail(workFlowDetail);

                }else{
                    workFlowDetail.setFlowId(0L);
                    workFlowDetail.setProjectId(Long.parseLong(projectId));
                    workFlowDetail.setWorkFlowId(Long.parseLong(workFlowId));
                    workFlowDetail.setNodeInfo(nodeInfo);
                    workFlowDetail.setTypeNo(typeNo);
                    workFlowDetail.setSave(isSave);
                    workFlowDetail.setDataSourceType(datasourceType);
                    workFlowDetail.setQuartzTime(quartzTime);
                    workFlowDataService.addWorkFlowDetail(workFlowDetail);
                    flowDetailId = workFlowDetail.getFlowDetailId();

                }
                //将flowDetailId和workFlowDetail关联
                flowDetailIdWorkFlowDetailMap.put(flowDetailId,workFlowDetail);
                //将nodeId和flowDetailId进行关联
                nodeIdFlowDetailMap.put(nodeId,workFlowDetail.getFlowDetailId());
                String crawlType = "";
                //1.1 开始添加节点的参数配置
                for (Object o : jsonArray) {
                    WorkFlowNodeParamBo workFlowNodeParamBo = new WorkFlowNodeParamBo();
                    JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                    int inputParamId = inputParamJson.getInteger("inputParamId");
                    //查询出当前参数的inputParamId所对应的记录，用于在添加work_flow_node_param时，需要一些冗余字段
                    WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamId);
                    //先判断该控件的英文名，并根据不同控件将其值进行调整。
                    switch (workFlowInputParamBo.getParamEnName()){
                        case "url":
                            String value = inputParamJson.getString("value");
                            String fileBase = System.getProperty("upload.dir");
                            String fileParamtertm = "/project/temp/";
                            String fileParamter = "/project/importfile/";
                            if(!Validate.isEmpty(value) && value.indexOf(fileParamter) < 0){
                                String url = value;
                                //把上传文件移动到服务器目录
                                String originalUrl = fileBase + fileParamtertm + url;
                                File file = new File(originalUrl);
                                if (file.exists()) {
                                    //上传文件到hdfs。
                                    baseSaoHDFS.uploadFile(originalUrl,fileBase + fileParamter + url);
                                    file.delete();
                                    workFlowNodeParamBo.setInputParamValue(fileBase + fileParamter + url);
                                }
                            }else{
                                workFlowNodeParamBo.setInputParamValue(value);
                            }
                            break;
                        case "storageTypeTable":
                            if(!Validate.isEmpty(workFlowDetail.getDataSourceType())){
                                StorageTypePO storageTypePO = dataSourceTypeService.getStorageTypeByDatasourceTypeId(Long.parseLong(workFlowDetail.getDataSourceType()));
                                workFlowNodeParamBo.setInputParamValue(storageTypePO.getStorageTypeTable());
                            }
                            break;
                        case "wordSegmentationObject":
                            if(inputParamJson.getString("value")!=null &&
                                    (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                            inputParamJson.getString("value").equals("topicAnalysisDefinition")||
                                            inputParamJson.getString("value").equals("themeAnalysisSetting")||
                                            inputParamJson.getString("value").equals(analysisLevel))){
                                workFlowNodeParamBo.setInputParamValue(analysisLevel);
                            }else{
                                workFlowNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                            }
                            break;
                        case "analysisLevel":
                            analysisLevel = inputParamJson.getString("value");
                            workFlowNodeParamBo.setInputParamValue(analysisLevel);
                            break;
                        case "themeAnalysisSettingObject":
                            if(inputParamJson.getString("value")!=null &&
                                    (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                            inputParamJson.getString("value").equals("wordSegmentation")||
                                            inputParamJson.getString("value").equals("topicAnalysisDefinition")||
                                            inputParamJson.getString("value").equals(analysisLevel))){
                                workFlowNodeParamBo.setInputParamValue(analysisLevel);
                            }else{
                                workFlowNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                            }
                            break;
                        case "topicAnalysisDefinitionObject":
                            if(inputParamJson.getString("value")!=null &&
                                    (inputParamJson.getString("value").equals("semanticAnalysisObject")||
                                            inputParamJson.getString("value").equals("wordSegmentation")||
                                            inputParamJson.getString("value").equals("themeAnalysisSetting")||
                                            inputParamJson.getString("value").equals(analysisLevel))){
                                workFlowNodeParamBo.setInputParamValue(analysisLevel);
                            }else{
                                workFlowNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                            }
                            break;
                        case "crawlFreq":
                            workFlowNodeParamBo.setInputParamValue(quartzTime);
                            break;
                        case "startFreqTypeName":
                            workFlowNodeParamBo.setInputParamValue(quartzTime);
                            break;
                        default:
                            if(workFlowInputParamBo.getParamEnName().equals("crawlType")){
                                crawlType = inputParamJson.getString("value");
                            }
                            workFlowNodeParamBo.setInputParamValue(inputParamJson.getString("value"));
                    }
                    //如果有paramId 则表示是更新。如果没有则表示添加
                    if (inputParamJson.getInteger("paramId") != null) {
                        workFlowNodeParamBo.setParamId(inputParamJson.getLong("paramId"));
                        //这里这样子做是因为bdi项目会有非常多的数据源。不便将config存入数据库，而选择存入缓存
                        if(("6".equals(inputParamJson.getString("inputParamId")) || "2".equals(inputParamJson.getString("inputParamId"))) &&
                                !Validate.isEmpty(inputParamJson.getString("config"))){
                            if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                                redisClient.set(RedisKey.dataSourceConfig.name(),inputParamJson.getString("config"));
                            }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                                redisClient.set(RedisKey.mDataSourceConfig.name(),inputParamJson.getString("config"));
                            }
                        }else{
                            workFlowNodeParamBo.setConfig(inputParamJson.getString("config"));
                        }
                        workFlowDataService.updateWorkFlowNodeParam(workFlowNodeParamBo);
                    } else {
                        workFlowNodeParamBo.setFlowDetailId(flowDetailId);
                        workFlowNodeParamBo.setProjectId(Long.parseLong(projectId));
                        workFlowNodeParamBo.setTypeNo(typeNo);
                        workFlowNodeParamBo.setWorkFlowId(Long.parseLong(workFlowId));
                        workFlowNodeParamBo.setInputParamId(inputParamJson.getInteger("inputParamId"));
                        workFlowNodeParamBo.setInputParamCnName(workFlowInputParamBo.getParamCnName());
                        workFlowNodeParamBo.setInputParamType(workFlowInputParamBo.getParamType());
                        //这里这样子做是因为bdi项目会有非常多的数据源。不便将config存入数据库，而选择存入缓存
                        if(("6".equals(inputParamJson.getString("inputParamId")) || "2".equals(inputParamJson.getString("inputParamId"))) &&
                                !Validate.isEmpty(inputParamJson.getString("config"))){
                            if(Constants.WORK_FLOW_TYPE_NO_DATACRAWL.equals(crawlType)){
                                redisClient.set(RedisKey.dataSourceConfig.name(),inputParamJson.getString("config"));
                            }else if(Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL.equals(crawlType)){
                                redisClient.set(RedisKey.mDataSourceConfig.name(),inputParamJson.getString("config"));
                            }
                        }else{
                            workFlowNodeParamBo.setConfig(inputParamJson.getString("config"));
                        }
                        workFlowDataService.addWorkFlowNodeParam(workFlowNodeParamBo);
                    }
                }

                //1.2 继续添加输出字段
                //首先判断其节点类型
                switch (typeNo){
                    case Constants.WORK_FLOW_TYPE_NO_DATAIMPORT:
                        List<String> list = new ArrayList<>();
                        //取出导入节点的字段映射。然后查询base系统，进行聚合
                        for (Object o : jsonArray) {
                            JSONObject inputParamJson = JSONObject.parseObject(o.toString());
                            WorkFlowInputParamBo workFlowInputParamBo = workFlowInputParamBoMap.get(inputParamJson.getInteger("inputParamId"));
                            if(workFlowInputParamBo.getParamEnName().equals("origainRelation")){
                                String origainRelation = inputParamJson.getString("value");
                                if(!Validate.isEmpty(origainRelation)){
                                    JSONArray origainRelationJson = JSONObject.parseArray(origainRelation);
                                    for(Object object :origainRelationJson){
                                        JSONObject origainRelationJsonObject = JSONObject.parseObject(object.toString());
                                        if(!Validate.isEmpty(origainRelationJsonObject.getString("key"))){
                                            list.add(origainRelationJsonObject.getString("key"));
                                        }
                                    }
                                    //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                                    if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                        visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                    }
                                    addWorkFlowOutputFiled(workFlowDetail,list,typeNo);
                                    break;
                                }
                            }
                        }
                        break;
                    case Constants.WORK_FLOW_TYPE_NO_DATACRAWL:
                        //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                        if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                            visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                        }
                        addWorkFlowOutputFiled(workFlowDetail,null,typeNo);
                        break;
                    case Constants.WORK_FLOW_TYPE_NO_DATA_M_CRAWL:
                        //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                        if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                            visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                        }
                        addWorkFlowOutputFiled(workFlowDetail,null,typeNo);
                        break;
                    case Constants.WORK_FLOW_TYPE_NO_SEMANTICANALYSISOBJECT:
                        if(!Validate.isEmpty(analysisLevel)){
                            List<JobTypeResultField> jobTypeResultFieldList =
                                    getJobTypeResultFieldListByResultTypeId(analysisLevel);
                            if(!Validate.isEmpty(jobTypeResultFieldList)){
                                if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                    visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                }
                                for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList){
                                    String filedType = "";
                                    VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                    switch (jobTypeResultField.getFieldType()){
                                        case 1:
                                            filedType = "number";
                                            break;
                                        case 2:
                                            filedType = "text";
                                            break;
                                        case 3:
                                            filedType = "datetime";
                                            break;
                                    }
                                    visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                    visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                    visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                    visWorkFlowBO.setFiledType(filedType);
                                    visWorkFlowBO.setStorageTypeTable(analysisLevel);
                                    visWorkFlowBO.setIsCustomed(0);
                                    visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                                }
                            }
                        }
                        break;
                    case Constants.WORK_FLOW_TYPE_NO_WORDSEGMENTATION:
                        if(!Validate.isEmpty(analysisLevel)){
                            List<JobTypeResultField> jobTypeResultFieldList2 =
                                    getJobTypeResultFieldListByResultTypeId(analysisLevel);
                            if(!Validate.isEmpty(jobTypeResultFieldList2)){
                                if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                    visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                }
                                for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList2){
                                    if(jobTypeResultField.getColName().equals("主题json")||
                                            jobTypeResultField.getColName().equals("话题json")){
                                        continue;
                                    }
                                    String filedType = "";
                                    VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                    switch (jobTypeResultField.getFieldType()){
                                        case 1:
                                            filedType = "number";
                                            break;
                                        case 2:
                                            filedType = "text";
                                            break;
                                        case 3:
                                            filedType = "datetime";
                                            break;
                                    }
                                    visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                    visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                    visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                    visWorkFlowBO.setFiledType(filedType);
                                    visWorkFlowBO.setStorageTypeTable(analysisLevel);
                                    visWorkFlowBO.setIsCustomed(0);
                                    visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                                }
                            }
                        }
                        break;
                    case Constants.WORK_FLOW_TYPE_NO_THEMEANALYSISSETTING:
                        if(!Validate.isEmpty(analysisLevel)){
                            List<JobTypeResultField> jobTypeResultFieldList3 =
                                    getJobTypeResultFieldListByResultTypeId(analysisLevel);
                            if(!Validate.isEmpty(jobTypeResultFieldList3)){
                                if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                    visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                }
                                for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList3){
                                    if(jobTypeResultField.getColName().equals("分词结果")||
                                            jobTypeResultField.getColName().equals("关键词结果")||
                                            jobTypeResultField.getColName().equals("话题json")){
                                        continue;
                                    }
                                    String filedType = "";
                                    VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                    switch (jobTypeResultField.getFieldType()){
                                        case 1:
                                            filedType = "number";
                                            break;
                                        case 2:
                                            filedType = "text";
                                            break;
                                        case 3:
                                            filedType = "datetime";
                                            break;
                                    }
                                    visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                    visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                    visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                    visWorkFlowBO.setFiledType(filedType);
                                    visWorkFlowBO.setStorageTypeTable(analysisLevel);
                                    visWorkFlowBO.setIsCustomed(0);
                                    visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                                }
                            }
                        }
                        break;
                    case Constants.WORK_FLOW_TYPE_NO_TOPICANALYSISDEFINITION:
                        if(!Validate.isEmpty(analysisLevel)){
                            List<JobTypeResultField> jobTypeResultFieldList4 =
                                    getJobTypeResultFieldListByResultTypeId(analysisLevel);
                            if(!Validate.isEmpty(jobTypeResultFieldList4)){
                                if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                    visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                }
                                for(JobTypeResultField jobTypeResultField : jobTypeResultFieldList4){
                                    if(jobTypeResultField.getColName().equals("分词结果")||
                                            jobTypeResultField.getColName().equals("关键词结果")||
                                            jobTypeResultField.getColName().equals("主题json")){
                                        continue;
                                    }
                                    String filedType = "";
                                    VisWorkFlowBO visWorkFlowBO = new VisWorkFlowBO();
                                    switch (jobTypeResultField.getFieldType()){
                                        case 1:
                                            filedType = "number";
                                            break;
                                        case 2:
                                            filedType = "text";
                                            break;
                                        case 3:
                                            filedType = "datetime";
                                            break;
                                    }
                                    visWorkFlowBO.setFiledCnName(jobTypeResultField.getColName());
                                    visWorkFlowBO.setFiledEnName(jobTypeResultField.getFieldName());
                                    visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                    visWorkFlowBO.setFiledType(filedType);
                                    visWorkFlowBO.setStorageTypeTable(analysisLevel);

                                    visWorkFlowBO.setIsCustomed(0);
                                    visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                                }
                            }
                        }
                        break;
                    case Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT:
                        break;
                }
            }

            //该map主要用于在校验是用
            Map<Long,WorkFlowDetail> detailMap = new HashMap<>();
            //2 再次进行遍历 进行next_folw_detail和pre_flow_detail的更新
            for(Object node : nodeList){
                WorkFlowDetail workFlowDetail = new WorkFlowDetail();
                JSONObject j = JSONObject.parseObject(node.toString());
                String nodeId = j.getString("nodeId");
                //获取上节点的nodeId
                JSONArray froms = j.getJSONArray("froms");
                String preFlowDetailIds = "";
                if(!Validate.isEmpty(froms)){
                    for(int i= 0;i<froms.size();i++){
                        Long preFlowDetailLong =  nodeIdFlowDetailMap.get(froms.get(i));
                        preFlowDetailIds += preFlowDetailLong+",";
                    }
                    preFlowDetailIds = preFlowDetailIds.substring(0,preFlowDetailIds.length()-1);
                    workFlowDetail.setPrevFlowDetailIds(preFlowDetailIds);
                }else{
                    workFlowDetail.setPrevFlowDetailIds("0");
                }
                //获取下节点的nodeId
                JSONArray tos = j.getJSONArray("tos");
                String nextFlowDetailIds = "";
                if(!Validate.isEmpty(tos)){
                    for(int i= 0;i<tos.size();i++){
                        if(tos.get(i) instanceof JSONArray){
                            JSONArray subTos = ((JSONArray) tos.get(i));
                            for(int k = 0;k<subTos.size();k++){
                                Long nextFlowDetailLong =  nodeIdFlowDetailMap.get(subTos.get(k));
                                nextFlowDetailIds += nextFlowDetailLong+",";
                            }
                        }else{
                            Long nextFlowDetailLong =  nodeIdFlowDetailMap.get(tos.get(i));
                            nextFlowDetailIds += nextFlowDetailLong+",";
                        }
                    }
                    nextFlowDetailIds = nextFlowDetailIds.substring(0,nextFlowDetailIds.length()-1);
                    workFlowDetail.setNextFlowDetailIds(nextFlowDetailIds);
                }else{
                    workFlowDetail.setNextFlowDetailIds(nextFlowDetailIds);
                }
                workFlowDetail.setFlowDetailId(nodeIdFlowDetailMap.get(nodeId));
                workFlowDataService.updateWorkFlowDetail(workFlowDetail);
                detailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
            }


            //3 这一步主要判断该工作流里是否有条件节点，如果有则需要将条件节点的paramValue值附上deitailId
            for(Map.Entry<String,Long> entry : nodeIdFlowDetailMap.entrySet()){
                Long detailId = entry.getValue();//取出每一个nodeId所对应的detailId
                WorkFlowDetail workFlowDetail = flowDetailIdWorkFlowDetailMap.get(detailId);//查询出每个detailId对应的workFlowDetail节点信息
                if(workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_CONDITION)){//判断该节点是否为条件节点
                    List<WorkFlowNodeParamBo> list = workFlowDataService.getWorkFlowNodeParamByFlowDetailId(detailId);//如果是的话则查询出该节点所有的输入参数值
                    if(!Validate.isEmpty(list)){
                        loop : for(WorkFlowNodeParamBo workFlowNodeParamBo : list){//遍历该节点的所有参数值
                            int inputParamId = workFlowNodeParamBo.getInputParamId();
                            if(inputParamId == 42){//找到条件设置的输入参数，并取出其值
                                String inputParamValue = workFlowNodeParamBo.getInputParamValue();
                                JSONArray inputParamValueArray = JSONArray.parseArray(inputParamValue);//转为jsonArray
                                if(!Validate.isEmpty(inputParamValueArray)){//判空
                                    for(Object o : inputParamValueArray){
                                        List<Long> nextDetailIdList = new ArrayList<>();
                                        JSONObject conditionJson =(JSONObject) o;//注意这里使用强转，不会产生新的对象
                                        JSONArray nodeIdArray = conditionJson.getJSONArray("nodeId");//取出其所有下节点的nodeId
                                        if(!Validate.isEmpty(nodeIdArray)){//判空
                                            for(Object nodeId : nodeIdArray){
                                                Long nextDetailId = nodeIdFlowDetailMap.get(nodeId);//取出每个下节点所对应的detailId，并放入list
                                                nextDetailIdList.add(nextDetailId);
                                            }
                                            conditionJson.put("nextDetailId",nextDetailIdList);//在新增一个jsonObject,同时inputParamValueArray也被改变
                                        }
                                    }
                                    workFlowNodeParamBo.setInputParamValue(inputParamValueArray.toJSONString());
                                    workFlowDataService.updateWorkFlowNodeParam(workFlowNodeParamBo);//在将其重新更新
                                    break loop;
                                }else{
                                    break loop;
                                }
                            }
                        }
                    }
                }
            }

            //4 该步骤主要解决为输出节点和数据筛选节点配置输出字段，因为这两个节点必须要上一个节点的信息，所以只能在更新了上下节点关系后才能添加输出字段
            List<String> TypeNoList = new ArrayList<>();
            TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT);
            TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_DATAFILTER);
            TypeNoList.add(Constants.WORK_FLOW_TYPE_NO_CONDITION);
            List<WorkFlowDetail> workFlowDetailList1 = workFlowDataService.getWorkFlowDetailListByTypeNoListAndWorkFlowId(TypeNoList,Long.parseLong(workFlowId));
            if(!Validate.isEmpty(workFlowDetailList1)){
                for(WorkFlowDetail workFlowDetail : workFlowDetailList1){
                    if(workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAOUTPUT)){
                        //查到上一个节点的信息，并判断上一个节点是什么类型的节点。
                        String preFlowDetailId = workFlowDetail.getPrevFlowDetailIds();
                        if("0".equals(preFlowDetailId)){
                            WorkFlowDetail workFlowDetail1 = workFlowDataService.getWorkFlowDetailByWorkFlowDetailId(Long.parseLong(preFlowDetailId));
                            //获得上节点的输出字段。
                            List<VisWorkFlowBO> visWorkFlowList = visWorkFlowService.getVisWorkFlowList(Integer.parseInt(preFlowDetailId));
                            //如果上节点是抓取或者是导入，则需要将输出节点选择的字段和上节点的输出字段做比较。
                            if(workFlowDetail1.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAIMPORT)||
                                    workFlowDetail1.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATACRAWL)){
                                String [] fieldArray = null;
                                //更具该该输出节点的detailId拿到该输出节点的所有配置，然后找到一个叫‘选择字段’的配置。并取出其值
                                List<WorkFlowNodeParamBo> workFlowNodeParamBoList = workFlowDataService.getWorkFlowNodeParamByFlowDetailId(workFlowDetail.getFlowDetailId());
                                for(WorkFlowNodeParamBo workFlowNodeParamBo :workFlowNodeParamBoList){
                                    if(workFlowNodeParamBo.getInputParamCnName().equals("选择字段")){
                                        String fields = workFlowNodeParamBo.getInputParamValue();
                                        if (!Validate.isEmpty(fields)) {
                                            fieldArray = fields.split(",");
                                            break;
                                        }
                                    }
                                }
                                if(fieldArray!=null){
                                    Map<String,VisWorkFlowBO> visWorkFlowBOMap = new HashMap<>();
                                    for(VisWorkFlowBO visWorkFlowBO : visWorkFlowList){
                                        visWorkFlowBOMap.put(visWorkFlowBO.getFiledEnName(),visWorkFlowBO);
                                    }
                                    //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                                    if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                        visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                    }
                                    for(int i = 0;i<fieldArray.length;i++){
                                        if(visWorkFlowBOMap.get(fieldArray[i]) != null){//如果有匹配的字段 就重新添加到输出节点的输出字段
                                            VisWorkFlowBO visWorkFlowBO = visWorkFlowBOMap.get(fieldArray[i]);
                                            visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                            visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                                        }
                                    }
                                }
                            }else{
                                //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                                if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                    visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                                }
                                for(VisWorkFlowBO visWorkFlowBO : visWorkFlowList){
                                    visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());
                                    visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);
                                }
                            }
                        }
                    }else if(workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_DATAFILTER)||
                            workFlowDetail.getTypeNo().equals(Constants.WORK_FLOW_TYPE_NO_CONDITION)){
                        //查到上一个节点的信息。
                        String preFlowDetailId = workFlowDetail.getPrevFlowDetailIds();
                        if(!preFlowDetailId.equals("0")){
                            //根据业务。数据筛选节点和条件节点的上节点有且只有一个。
                            List<VisWorkFlowBO> list = visWorkFlowService.getVisWorkFlowList(Integer.parseInt(preFlowDetailId));
                            //在添加前，需判断该流程节点是否已经存在输出字段，若不存在则添加。若存在则需先删除该节点的输出字段再添加
                            if(!Validate.isEmpty(visWorkFlowService.getVisWorkFlowList(workFlowDetail.getFlowDetailId().intValue()))){
                                visWorkFlowService.deleteWorkFlowOutputFiledByDetailId(workFlowDetail.getFlowDetailId());
                            }
                            for(VisWorkFlowBO visWorkFlowBO : list){
                                visWorkFlowBO.setFlowDetailId(workFlowDetail.getFlowDetailId().intValue());//将每个上节点的输出字段重新赋值给当前数据筛选节点
                                visWorkFlowService.addWorkFlowOutputFiled(visWorkFlowBO);//重新添加。。
                            }
                        }
                    }
                }
            }

            //5 校验项目
            //如果有被删除的节点，则直接将他的下节点更新为无效。并将该工作流更新为 配置中
//            int count = 0;
              verifyProjectFlowDetail(workFlowId,detailMap);
            //6 保存图片,如果有节点才保存。没有节点则不保存图片，并且将该工作流设置为配置中
            if(!Validate.isEmpty(nodeList)){
//                String imgStr = jsonObject.getString("file");
//                imgStr = imgStr.split(",")[1];
//                upload = System.getProperty("upload.dir");
//
//                if(Validate.isEmpty(upload)){
//                    upload = "/data/upload/dev_dpmbs/";
//                }
//                if(!upload.endsWith("/")){
//                    upload+="/";
//                }
//
//                String imgPath = upload+IMG_PATH+workFlowId+".png";
//                LoggerUtil.infoTrace(loggerName,"图片路径为:"+imgPath);
//
//                Base64Util.GenerateImage(imgStr,imgPath);
//                WorkFlowListBO workFlowListBO = new WorkFlowListBO();
//                workFlowListBO.setImg("/"+IMG_PATH+workFlowId+".png");
//                workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
//                workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
            }else{
                WorkFlowListBO workFlowListBO = new WorkFlowListBO();
                workFlowListBO.setStatus(5);
                workFlowListBO.setWorkFlowId(Long.parseLong(workFlowId));
                workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
            }
            //用于将detailId和paramId返回给前端
            List<Map<String,Object>> reslutList = new ArrayList<>();
            for(Map.Entry<String,Long> entry : nodeIdFlowDetailMap.entrySet()){
                Map<String,Object> resultMap = new HashMap<>();
                resultMap.put("nodeId",entry.getKey());
                resultMap.put("detailId",entry.getValue());
                List<WorkFlowNodeParamBo> list = workFlowDataService.getWorkFlowNodeParamByFlowDetailId(entry.getValue());
                List<Map<String,Object>> reslutParamList = new ArrayList<>();
                for(WorkFlowNodeParamBo workFlowNodeParamBo : list){
                    Map<String,Object> map = new HashMap<>();
                    map.put("paramId",workFlowNodeParamBo.getParamId());
                    map.put("inputParamId",workFlowNodeParamBo.getInputParamId());
                    reslutParamList.add(map);
                }
                resultMap.put("paramArray",reslutParamList);
                reslutList.add(resultMap);
            }
            return reslutList;

    }

    @Override
    public Map<String, Object> getNodeProgress(String projectId, String workFlowId,String progress) {
        Long projectIdInt=Long.parseLong(projectId);
        Long workFlowIdInt=Long.parseLong(workFlowId);
        Map<Long,WorkFlowDetail> workFlowDetailMap=new HashMap<>();
        WorkFlowListBO workFlowListBO=new WorkFlowListBO();
        if(progress==null||"".equals(progress)){
            WorkFlowListBO workFlowListBO1=workFlowDataService.getWorkFlowListBOByWorkFlowId(workFlowIdInt);
            progress=workFlowListBO1.getProgress();
            if(progress.equals("-")){
                progress="";
            }
        }else {
            workFlowListBO.setProgress(progress);
            workFlowListBO.setWorkFlowId(workFlowIdInt);
            workFlowDataService.updateWorkFlowListStatus(workFlowListBO);
        }

        //更根据project,workflowId查询表
        List<WorkFlowDetail> workFlowDetailList=workFlowDataService.getWorkFlowDetailByWorkFlowIdAndProjectIds(workFlowIdInt,projectIdInt);
        //聚合存入到map
        for (WorkFlowDetail workFlowDetail : workFlowDetailList) {
            workFlowDetailMap.put(workFlowDetail.getFlowDetailId(),workFlowDetail);
        }
        int count=0;
        Map<String,Object> data=new HashMap<>();
        List<String> finishNodes=new ArrayList<>();
        if(workFlowDetailList!=null){
            List<String> list=new ArrayList<>();
            for (WorkFlowDetail workFlowDetail : workFlowDetailList) {
                //判断该节点是否完成
                if(workFlowDetail.getJobStatus()==6){
                    count+=1;
                    String nodeInfo=workFlowDetail.getNodeInfo();
                    JSONObject jsonObject=JSONObject.parseObject(nodeInfo);
                    //获取下一节点的name
                    String name=jsonObject.getString("name");
                    finishNodes.add(name);
                }
                //判断该节点完成并且下个节点是运行状态
                if(workFlowDetail.getJobStatus()==6){
                    //获取下一节点的detailId
                    String next=workFlowDetail.getNextFlowDetailIds();
                    //当此节点为条件节点时,就需要分割
                    if(next!=null&&!("").equals(next)){
                    String [] nextFlowDetail=next.split(",");
                    for(int i=0;i<nextFlowDetail.length;i++){
                        WorkFlowDetail details=workFlowDetailMap.get(Long.parseLong(nextFlowDetail[i]));
                        //下一节点是运行中的状态
                        if(details.getJobStatus()==1){
                           String nodeInfo=details.getNodeInfo();
                           JSONObject jsonObject=JSONObject.parseObject(nodeInfo);
                           //获取下一节点的name
                           String name=jsonObject.getString("name");
                            list.add(name);
                            data.put("nodeName",list);
                        }
                    }
                    }
                    //如果节点是首节点并且是运行状态则返回该节点的name
                }else if(workFlowDetail.getPrevFlowDetailIds().equals("0")&&workFlowDetail.getJobStatus()==1){
                    String nodeInfo=workFlowDetail.getNodeInfo();
                    JSONObject jsonObject=JSONObject.parseObject(nodeInfo);
                    //获取下一节点的name
                    String name=jsonObject.getString("name");
                    list.add(name);
                    data.put("nodeName",list);
                }

            }
        }
        data.put("allCount",workFlowDetailList.size());
        data.put("finishCount",count);
        data.put("finishNodes",finishNodes);
        data.put("progress",progress);

        return data;
    }

    @Override
    public int updateWorkFlowDetailToPreRunFinish(long flowDetailId) {
        return workFlowDataService.updateWorkFlowDetailToPreRunFinish(flowDetailId);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailListByWorkFlowIdAndStatus(Long workFlowId) {
        return workFlowDataService.getWorkFlowDetailListByWorkFlowIdAndStatus(workFlowId);
    }
}