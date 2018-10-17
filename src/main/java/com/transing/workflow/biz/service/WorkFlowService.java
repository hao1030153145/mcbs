package com.transing.workflow.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BizService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.ParamBO;
import com.transing.dpmbs.integration.bo.WorkFlowListBO;
import com.transing.dpmbs.web.filter.WorkFlowListFilter;
import com.transing.workflow.integration.bo.*;

import java.util.List;
import java.util.Map;

/**
 * @author Sunny
 * @version 1.0
 */
public interface WorkFlowService extends BizService
{

    //更新工作流状态为暂停
    int updateWorkFlowInfoToStopByFlowId(long workFlowInfoId);

    /**
     * 查询出所有需要处理的工作流信息
     *
     * @param
     * @return
     */
    List<WorkFlowInfo> getAllNeedProcessWorkFlowInfo();

    /**
     * 查询出所有正在处理的工作流信息
     *
     * @param
     * @return
     */
    List<WorkFlowInfo> getRunningWorkFlowInfo();


    /**
     * 根据workFlowId返回WorkFlowInfo信息
     *
     * @param workFlowId
     * @return
     */
    WorkFlowInfo getWorkFlowInfoByWorkFlowId(long workFlowId);

    /**
     * 根据workFlowDetailId返回WorkFlowDetail信息
     *
     * @param workFlowDetailId
     * @return
     */
    WorkFlowDetail getWorkFlowDetailByWorkFlowDetailId(long workFlowDetailId);

    /**
     * 根据 flowId 更新状态为等待执行
     * @param flowId
     * @return
     */
    int updateWorkFlowInfoToInitByFlowId(long flowId);

    /**
     * 更新工作流状态为运行中
     * @param workFlowDetailId
     */
    void updateWorkFlowDetailToRunningByWorkFlowDetailId(long workFlowDetailId);



    /**
     * 更新工作流状态为已完成
     * @param workFlowDetailId
     */
    void updateWorkFlowDetailToComplatedByWorkFlowDetailId(long workFlowDetailId);



    /**
     * 更新工作流状态为异常
     * @param workFlowDetailId
     */
    void updateWorkFlowDetailToExceptionByWorkFlowDetailId(long workFlowDetailId, String errorMsg);

    /**
     * 更新工作流状态为暂停
     * @param workFlowDetailId
     */
    void updateWorkFlowDetailToStopByWorkFlowDetailId(long workFlowDetailId);

    /**
     * 更新工作流状态为初始
     * @param workFlowDetailId
     */
    void updateWorkFlowDetailToInitByWorkFlowDetailId(long workFlowDetailId);



    /**
     * 根据workFlowId返回WorkFlowDetail 列表信息
     * @param workFlowId
     * @return
     */
    List<WorkFlowDetail> getWorkFlowDetailListByWorkFlowId(long workFlowId);

    /**
     * 添加工作流 流程数据信息 同时添加 detail表和param表
     * @param projectId
     * @param typeNo
     * @param jsonParam
     * @param paramType  请调用 WorkFlowParam类的两个静态参数 1为公共，0为私有的参数。
     * @return
     * @throws BizException
     */
    long addWorkDetail(String prevFlowDetailIds, long projectId,String typeNo,String jsonParam,int paramType)throws BizException;

    /**
     * 添加工作流 流程数据信息 同时添加 detail表和param表
     * @param projectId
     * @param typeNo
     * @param jsonParam
     * @param paramType  请调用 WorkFlowParam类的两个静态参数 1为公共，0为私有的参数。
     * @return
     * @throws BizException
     */
    long addWorkDetail(String prevFlowDetailIds,String nextFlowDetailIds, long projectId,String typeNo,String jsonParam,int paramType,String quartzTime,Long dataSourceTypeId)throws BizException;

    /**
     * 添加工作流 流程数据信息 同时添加 detail表和param表
     * @param projectId
     * @param typeNo
     * @param jsonParam
     * @param paramType  请调用 WorkFlowParam类的两个静态参数 1为公共，0为私有的参数。
     * @return
     * @throws BizException
     */
    long addWorkDetail(String prevFlowDetailIds,long projectId, String typeNo, String jsonParam,int paramType,String quartzTime)throws BizException;

    /**
     * 添加工作流 流程数据信息 同时添加 detail表和param表 以及projectResultType表
     * @param projectId
     * @param typeNo
     * @param jsonParam
     * @param paramType
     * @param quartzTime 没有可以传null
     * @param dataSourceTypeId
     * @return
     * @throws BizException
     */
    long addWorkDetail(String prevFlowDetailIds,long projectId, String typeNo, String jsonParam,int paramType,String quartzTime,long dataSourceTypeId)throws BizException;

    /**
     * 添加工作流 流程数据信息 同时添加 detail表和param表 以及projectResultType表
     * @param projectId
     * @param typeNo
     * @param jsonParam
     * @param paramType
     * @param quartzTime 没有可以传null
     * @param dataSourceTypeId
     * @return
     * @throws BizException
     */
    long addWorkDetail(long projectId, String typeNo, String jsonParam,int paramType,String quartzTime,long dataSourceTypeId,int workTemplateId)throws BizException;

    /**
     * 添加工作流 流程数据信息 同时添加 detail表和param表 以及projectResultType表
     * @param projectId
     * @param typeNo
     * @param jsonParam
     * @param paramType
     * @param quartzTime
     * @param dataSourceTypes
     * @return
     * @throws BizException
     */
    long addWorkDetail(String prevFlowDetailIds,long projectId, String typeNo, String jsonParam, int paramType, String quartzTime, List<Long> dataSourceTypes) throws BizException;

    /**
     * 根据typeNo 和 projectId 查询 参数信息
     * @param typeNo
     * @param projectId
     * @return
     * @throws BizException
     */
    List<WorkFlowParam> getWorkFlowParamListByParam(String typeNo,long projectId)throws BizException;

    /**
     * 通过projectId 添加好 工作流节点信息
     * @param projectId
     * @return
     * @throws BizException
     */
    boolean addWorkInfo(long projectId,String typeNos)throws BizException;

    /**
     * 更新workFlowParam信息
     * @param workFlowParam
     * @return
     * @throws BizException
     */
    boolean updateWorkFlowParam(WorkFlowParam workFlowParam)throws BizException;

    /**
     * 更新workFlowParam信息
     * @param workFlowParam
     * @return
     * @throws BizException
     */
    boolean updateWorkFlowParam(WorkFlowParam workFlowParam,List<Long> dataSourceTypes)throws BizException;

    /**
     * 根据 paramId 同时 删除detail表和param表
     * @param paramId
     * @return
     * @throws BizException
     */
    boolean deleteWorkFlowParam(long paramId)throws BizException;

    /**
     * 根据 typeNo和projectId 查询 参数信息
     * @param typeNoList
     * @param projectId
     * @return
     * @throws BizException
     */
    List<WorkFlowParam> getWorkFlowParamListByTypeNoList(List<String> typeNoList, long projectId)throws BizException;

    /**
     * 根据 typeNoList 和projectId 查询 detail信息
     * @param typeNoList
     * @param projectId
     * @return
     * @throws BizException
     */
    List<WorkFlowDetail> getWorkFlowDetailListByTypeNoList(List<String> typeNoList, long projectId)throws BizException;

    /**
     * 根据flowDetailId 查询 参数信息
     * @param flowDetailId
     * @return
     * @throws BizException
     */
    WorkFlowParam getWorkFlowParamByDetailId(long flowDetailId)throws BizException;

    /**
     * 根据flowDetailId 更新 状态为启动
     * @param flowDetailId
     * @return
     * @throws BizException
     */
    int updateWorkFlowDetailToStart(long flowDetailId)throws BizException;

    /**
     * 根据 flowDetailId 更新 状态为 已完成
     * @param flowDetailId
     * @return
     * @throws BizException
     */
    int updateWorkFlowDetailToFinish(long flowDetailId)throws BizException;

    /**
     * 根据 flowId 更新状态为启动
     * @param flowId
     * @return
     * @throws BizException
     */
    int updateWorkFlowInfoToStart(long flowId)throws BizException;

    /**
     * 根据 flowId更新 完成job 数量
     * @param flowId
     * @return
     * @throws BizException
     */
    int updateWorkFlowInfoComNum(long flowId)throws BizException;

    /**
     * 根据 flowId 并且判断完成数量是否与总任务数一致 更新 状态 为已完成
     * @param flowId
     * @return
     * @throws BizException
     */
    int updateWorkFlowInfoToFinishIfComNum(long flowId)throws BizException;

    /**
     * 根据projectId 启动 工作流
     * @param projectId
     * @return
     * @throws BizException
     */
    boolean updateWorkFlowToRunningByProjectId(long projectId)throws BizException;

    /**
     * 通过paramId 启动 工作流
     * @param paramId
     * @param projectId
     * @return
     * @throws BizException
     */
    boolean startWorkFlowByParamId(long paramId,long projectId,String batchNo)throws BizException;

    /**
     * 通过paramId 启动 工作流
     * @param paramId
     * @param projectId
     * @return
     * @throws BizException
     */
    boolean stopWorkFlowByParamId(long paramId,long projectId)throws BizException;

    /**
     * 根据project 停止 工作流
     * @param projectId
     * @return
     * @throws BizException
     */
    boolean updateWorkFlowToStopByProjectId(long projectId)throws BizException;

    /**
     * 根据 workFlowDetail 对象 更新 quartzTime
     * @param workFlowDetail
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailQuartzTime(WorkFlowDetail workFlowDetail)throws BizException;

    /**
     * 根据paramId 查询 workFlowParam信息
     * @param paramId
     * @return
     * @throws DataServiceException
     */
    WorkFlowParam getWorkFlowParamByParamId(long paramId)throws BizException;

    /**
     * 根据projectId查询 info信息
     * @param projectId
     * @throws DataServiceException
     */
    List<WorkFlowInfo> getWorkFlowInfoByProjectId(long projectId)throws BizException;

    /**
     * 根据projectId查询 info信息
     * @param projectId
     * @throws DataServiceException
     */
    WorkFlowInfo getWorkFlowInfoByProjectIdANdTypeNo(long projectId,String typeNo)throws BizException;

    /**
     * 更新 job_progress
     * @param flowDetailId
     * @param jobProgress
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailProgress(long flowDetailId,int jobProgress)throws BizException;

    /**
     * 根据 flowId 更新 状态 为出错
     * @param flowId
     * @return
     * @throws BizException
     */
    int updateWorkFlowInfoStatusExceptionByFlowId(long flowId)throws BizException;

    /**
     * 根据 workFlowDetailId 更新状态 为出错。
     * @param errorMsg
     * @param workFlowDetailId
     * @return
     * @throws BizException
     */
    int updateWorkFlowDetailExceptionByMap(String errorMsg,long workFlowDetailId)throws BizException;

    /**
     * 根据projectId 启动 工作流
     * 如果项目是 完成状态
     * @param projectId
     * @return
     * @throws BizException
     */
    boolean updateWorkFlowToRunningIfFinishByProjectId(long projectId)throws BizException;

    /**
     * 更新 workFlowDetail对象
     * @param workFlowDetail
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetail(WorkFlowDetail workFlowDetail)throws BizException;

    //----------------------V1.2.0-----------------------------------

    /**
     * 通过 projectId 查询 工作流需要 执行的第一个节点
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getFirstDetailByProjectId(long projectId)throws BizException;

    /**
     * 通过 typeNo查询 当前typeNo 所有 需要执行的 detailList
     * @param typeNo
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getExecDetailListByTypeNo(String typeNo,long projectId)throws BizException;

    /**
     * 通过 workFlowParam 参数查询 workFlowParamList
     * @param workFlowParam
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowParam> getWorkFlowParamByFlowParam(WorkFlowParam workFlowParam)throws BizException;

    /**
     * 通过projectId 查询workFlowParamList
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowParam> getWorkFlowParamByProJectId(Long projectId)throws DataServiceException;

    /**
     * 通过workFlowId 查询workFlowDetailList dpm1.5.1
     * @param workFlowId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getWorkFlowDetailByWorkFlowId(Long workFlowId) throws DataServiceException;

    /**
     * 通过workFlowId和projectId 查询workFlowDetailList dpm1.5.1
     * @param workFlowId projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getWorkFlowDetailByWorkFlowIdAndProjectId(Long workFlowId,Long projectId) throws DataServiceException;

    /**
     * 通过projectId 查询workFlowDetailList
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getWorkFlowDetailByProjectId(Long projectId) throws DataServiceException;

    /**
     * 根据状态查询detailList
     *
     */

    List<WorkFlowDetail> getWorkFlowDetailByStatus(Integer jobStatus)throws BizException;

    /**
     * 根据templateId查询workFlowNodeList
     * @param templateId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeBO> getWorkFlowNodeByTemplateId(int templateId) throws DataServiceException;

    /**
     * 通过flowId查询WorkFlowNodeBO
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    WorkFlowNodeBO getWorkFlowNodeByFlowId(Long flowId)throws DataServiceException;

    /**
     * 根据flowId查询workFlowParam
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    WorkFlowParam getWorkFlowParamByFlowId(Long flowId,Long projectId) throws DataServiceException;


    /**
     * 根据workFlowTemplateId和projectId来查询添加项目detail
     * @param workFlowTemplateId
     * @return
     * @throws DataServiceException
     */
    void addProjectDetailIdByTemplateId(int workFlowTemplateId,Long workFlowId,Long projectId)throws DataServiceException;

    /**
     * 查询job_type_category表
     * @return
     * @throws DataServiceException
     */
    List<JobTypeCategoryBO> getJobTypeCategoryBOList()throws DataServiceException;

    /**
     * 查询所有的JobTypeInfo
     * @return
     * @throws DataServiceException
     */
    List<JobTypeInfo> getJobTypeInfo()throws DataServiceException;

    /**
     * 查询所有节点参数配置
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowInputParamBo> getWorkFlowInputParamBoList()throws DataServiceException;

    /**
     * 查询所有样式
     * @return
     * @throws DataServiceException
     */
    List<StyleBO> getStyleBOList()throws DataServiceException;

    /**
     * 根据TemplateFlowId查询work_flow_template_node_param表
     * @param TemplateFlowId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowTemplateNodeParamBo> getTemplateNodeParamByTemplateFlowId(long TemplateFlowId)throws DataServiceException;

    /**
     * 根据workFlowDetailId查询work_flow_node_param表
     * @param workFlowDetailId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeParamBo> getWorkFlowNodeParamByFlowDetailId(long workFlowDetailId) throws DataServiceException;



    /**
     * 保存可视化工作流节点参数配置
     * @param body
     * @throws DataServiceException
     */
    List<Map<String,Object>> addVisWorkFlowNodeParam(String body) throws DataServiceException;

    Map<String,List<Map<String,Object>>> getWorkFlowNodeList()throws DataServiceException;

    /**
     * 组合查询work_flow_node_param表记录
     * @param projectId
     * @param typeNo
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeParamBo> getWrokFlowNodeParamListByMap(Long projectId,String typeNo) throws DataServiceException;

    /**
     * 根据projectId查询work_flow_node_param表
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeParamBo> getWorkFlowNodeParamByProjectId(long projectId) throws BizException;

    /**
     * 根据workFlowId查询work_flow_node_param表 dpm1.5.1
     * @param workFlowId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeParamBo> getWorkFlowNodeParamByWorkFlowId(long workFlowId) throws BizException;

    /**
     * 根据filter查询WorkFlowListPO dpm1.5.1
     * @param workFlowListFilter
     * @return
     * @throws BizException
     */
    List<WorkFlowListBO> getWorkFlowListPOByFilter(WorkFlowListFilter workFlowListFilter)throws BizException;

    /**
     * 根据状态查询工作流（可包含多个状态）dpm1.5.1
     * @param conditions
     * @return
     * @throws BizException
     */
    List<WorkFlowListBO> getWorkFlowListByIncludeStatus(List<String> conditions,Long projectId)throws BizException;

    /**
     * 添加可视化工作流 dpm1.5.1
     * @param workFlowListBO
     * @throws BizException
     */
    void addWorkFlowListBO(WorkFlowListBO workFlowListBO) throws BizException;

    /**
     * 根据名称查询可视化工作流数量 用于校验 dpm1.5.1
     * @param workFlowName projectId
     * @return
     * @throws BizException
     */
    Long getWorkFlowListBOByWorkFlowName(Long projectId,String workFlowName) throws BizException;

    /**
     * 根据工作流id删除工作流相关记录 dpm1.5.1
     * @param workFlowId
     * @throws BizException
     */
    void delWorkFlowByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 推送节点查询下载编码 dpm1.5.1
     * @return
     * @throws BizException
     */
    List<ParamBO> getDownloadCoding()throws BizException;

    /**
     * 根据workFlowId查询workFlowList表 工作流信息 dpm1.5.1
     * @param workFlowId
     * @return
     * @throws BizException
     */
    WorkFlowListBO getWorkFlowListBOByWorkFlowId(Long workFlowId)throws BizException;

    /**
     * 启动可视化工作流 dpm1.5.1
     * @param workFlowId
     * @throws BizException
     */
    void startVisWorkFlow(Long workFlowId,String preRun,String crawalPage)throws BizException;

    /**
     * 根据workFlowId获取该工作流下所有的首节点 dpm1.5.1
     * @param workFlowId
     * @return
     * @throws BizException
     */
    List<WorkFlowDetail> getFirstDetailByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 更新工作流状态 dpm 1.5.1
     * @param workFlowListBO
     * @throws BizException
     */
    void updateWorkFlowListStatus(WorkFlowListBO workFlowListBO)throws  BizException;

    /**
     * 停止工作流 dpm1.5.1
     * @param workFlowId
     * @throws BizException
     */
    boolean stopWorkFlowListByWorkFlowId(Long workFlowId) throws BizException;

    long workFlowListCount(Long projectId,Integer status) throws  BizException;
    /**
     * 根据workFlowId改变节点的状态
     * @param workFlowId
     */
    void updateWorkFlowDetailByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 保存可视化工作流项目节点配置信息并试运行
     * @param body
     * @return
     */
    Map<String,Object> addVisWorkFlowNodeParamAndRun(String body) throws  BizException;

    /**
     * 保存可视化工作流中的单个节点的数据
     * @param body
     * @return
     * @throws BizException
     */
    List<Map<String,Object>> saveOnceWorkFlowProjectNodeInfo(String body) throws  BizException;

    /**
     * 查看试运行中,正在运行的节点信息
     * @param projectId
     * @param workFlowId
     * @return
     * @throws BizException
     */
    Map<String,Object> getNodeProgress(String projectId, String workFlowId,String progress) throws  BizException;

    /**
     * 试运行时,根据flowDetailid改变状态为试运行完成状态
     * @param flowDetailId
     * @return
     * @throws BizException
     */
    int updateWorkFlowDetailToPreRunFinish(long flowDetailId) throws  BizException;

    /**
     * 判断该工作流下有没有无效节点
     * @param workFlowId
     * @return
     * @throws BizException
     */
    List<WorkFlowDetail> getWorkFlowDetailListByWorkFlowIdAndStatus(Long workFlowId) throws  BizException;
}