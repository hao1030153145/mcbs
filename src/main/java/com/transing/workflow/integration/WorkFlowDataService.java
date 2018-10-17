package com.transing.workflow.integration;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.DataService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.ParamBO;
import com.transing.dpmbs.integration.bo.WorkFlowListBO;
import com.transing.dpmbs.web.filter.WorkFlowListFilter;
import com.transing.dpmbs.web.po.WorkFlowListPO;
import com.transing.workflow.integration.bo.*;

import java.util.List;
import java.util.Map;

/**
 * WorkFlow数据操作接口
 *
 * @author Sunny
 * @version 1.0
 * @see
 */
public interface WorkFlowDataService extends DataService {

    void updateWorkFlowDetailToInitByWorkFlowDetailId(long workFlowDetailId);

    //更新工作流状态为运行中
    void updateWorkFlowDetailToRunningByWorkFlowDetailId(long workFlowDetailId);

    //更新工作流状态为已完成
    void updateWorkFlowDetailToComplatedByWorkFlowDetailId(long workFlowDetailId);

    //更新工作流状态为异常
    void updateWorkFlowDetailToExceptionByWorkFlowDetailId(long workFlowDetailId, String errorMsg);

    //更新工作流状态为暂停
    void updateWorkFlowDetailToStopByWorkFlowDetailId(long workFlowDetailId);

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
     * 根据状态查询detailList
     *
     */
    List<WorkFlowDetail> getWorkFlowDetailByStatus(Integer jobStatus)throws DataServiceException;

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
     * 根据workFlowId返回WorkFlowDetail 列表信息
     *
     * @param workFlowId
     * @return
     */
    List<WorkFlowDetail> getWorkFlowDetailListByWorkFlowId(long workFlowId);

    /**
     * 添加 工作流节点 数据 信息
     *
     * @param workFlowDetail
     * @return
     */
    int addWorkFlowDetail(WorkFlowDetail workFlowDetail) throws DataServiceException;

    /**
     * 添加参数信息
     *
     * @param workFlowParam
     * @return
     * @throws DataServiceException
     */
    int addWorkFlowParam(WorkFlowParam workFlowParam) throws DataServiceException;

    /**
     * 根据 typeNo 查询 参数信息
     *
     * @param typeNo
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowParam> getWorkFlowParamListByParam(String typeNo, long projectId) throws DataServiceException;

    /**
     * 根据项目id 和 typeNo 查询 工作节点信息
     *
     * @param projectId
     * @param typeNo
     * @return
     * @throws DataServiceException
     */
    WorkFlowInfo getWorkFlowInfoByParam(long projectId, String typeNo) throws DataServiceException;

    /**
     * 添加workFLowInfo信息
     *
     * @param workFlowInfo
     * @return
     * @throws DataServiceException
     */
    int addWorkFlowInfo(WorkFlowInfo workFlowInfo) throws DataServiceException;

    /**
     * 更新 workFlowInfo信息。
     *
     * @param workFlowInfo
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfo(WorkFlowInfo workFlowInfo) throws DataServiceException;

    /**
     * 更新 workFlowParam 信息
     *
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowParam(WorkFlowParam workFlowParam) throws DataServiceException;

    /**
     * 根据paramId 查询 workFlowParam信息
     *
     * @param paramId
     * @return
     * @throws DataServiceException
     */
    WorkFlowParam getWorkFlowParamByParamId(long paramId) throws DataServiceException;

    /**
     * 根据flowDetailId删除workDetail信息
     *
     * @param flowDetailId
     * @return
     * @throws DataServiceException
     */
    int deleteWorkFlowDetailByDetailId(long flowDetailId) throws DataServiceException;

    /**
     * 根据paramId删除 workFlowParam信息
     *
     * @param paramId
     * @return
     * @throws DataServiceException
     */
    int deleteWorkFlowParamByParamId(long paramId) throws DataServiceException;

    /**
     * 根据 typeNo List 和project 查询多个参数
     *
     * @param typeNoList
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowParam> getWorkFlowParamListByTypeNoList(List<String> typeNoList, long projectId) throws DataServiceException;

    /**
     * 根据typeNoList 和 projectId 查询 detail新
     *
     * @param typeNoList
     * @param workFlowId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getWorkFlowDetailListByTypeNoList(List<String> typeNoList, long workFlowId) throws DataServiceException;

    /**
     * 根据typeNoList 和 workFlowId 查询 detail新 dpm1.5.1
     *
     * @param typeNoList
     * @param workFlowId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getWorkFlowDetailListByTypeNoListAndWorkFlowId(List<String> typeNoList, long workFlowId) throws DataServiceException;

    /**
     * 根据flowDetailId 查询 参数信息
     *
     * @param flowDetailId
     * @return
     * @throws DataServiceException
     */
    WorkFlowParam getWorkFlowParamByDetailId(long flowDetailId) throws DataServiceException;

    /**
     * 根据flowDetailId 更新 状态为启动
     *
     * @param flowDetailId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailToStart(long flowDetailId) throws DataServiceException;

    /**
     * 根据 flowDetailId 更新 状态为 已完成
     *
     * @param flowDetailId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailToFinish(long flowDetailId) throws DataServiceException;

    /**
     * 根据 flowId 更新状态为启动
     *
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfoToStart(long flowId) throws DataServiceException;

    /**
     * 根据 flowId 更新状态为等待执行
     *
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfoToInitByFlowId(long flowId) throws DataServiceException;

    /**
     * 根据 flowId更新 完成job 数量
     *
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfoComNum(long flowId) throws DataServiceException;

    /**
     * 根据 flowId 并且判断完成数量是否与总任务数一致 更新 状态 为已完成
     *
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfoToFinishIfComNum(long flowId) throws DataServiceException;

    /**
     * 根据 flowId 更新 状态 为出错
     *
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfoStatusExceptionByFlowId(long flowId) throws DataServiceException;

    /**
     * 根据 workFlowDetailId 更新状态 为出错。
     *
     * @param errorMsg
     * @param workFlowDetailId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailExceptionByMap(String errorMsg, long workFlowDetailId) throws DataServiceException;

    /**
     * 根据projectId 更新 workFlowInfo 状态为启动，等待执行
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfoToRunningByProjectId(long projectId) throws DataServiceException;

    /**
     * 根据projectId 更新状态为停止
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfoToStopByProjectId(long projectId) throws DataServiceException;

    /**
     * 根据projectId 更新状态 为停止
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailToStopByProjectId(long projectId) throws DataServiceException;

    /**
     * 根据projectId 更新状态为 待启动
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailToStartByProjectId(long projectId, List<Long> detailIdList) throws DataServiceException;

    /**
     * 根据workFlowId 更新状态为 进行中 dpm1.5.1
     *
     * @param workFlowId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailToStartByWorkFlowId(long workFlowId, List<Long> detailIdList) throws DataServiceException;

    /**
     * 根据 workFlowDetail 对象 更新 quartzTime
     *
     * @param workFlowDetail
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailQuartzTime(WorkFlowDetail workFlowDetail) throws DataServiceException;

    /**
     * 根据projectId查询 info信息
     *
     * @param projectId
     * @throws DataServiceException
     */
    List<WorkFlowInfo> getWorkFlowInfoByProjectId(long projectId) throws DataServiceException;

    /**
     * 根据projectId查询 info信息
     *
     * @param projectId
     * @throws DataServiceException
     */
    WorkFlowInfo getWorkFlowInfoByProjectIdANdTypeNo(long projectId, String typeNo) throws DataServiceException;

    /**
     * 更新 job_progress
     *
     * @param flowDetailId
     * @param jobProgress
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailProgress(long flowDetailId, int jobProgress) throws DataServiceException;

    /**
     * 更新workinfo信息从完成状态 为 启动状态。
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowInfoToRunningIfFinishByProjectId(long projectId) throws DataServiceException;

    /**
     * 更新workDetail信息从完成状态 为 初始状态。
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetailToRunningIfFinishByProjectId(long projectId) throws DataServiceException;

    /**
     * 更新 workFlowDetail对象
     *
     * @param workFlowDetail
     * @return
     * @throws DataServiceException
     */
    int updateWorkFlowDetail(WorkFlowDetail workFlowDetail) throws DataServiceException;


    //---------------------V1.2.0----------------------------

    /**
     * 通过 projectId 查询 工作流需要 执行的首节点 可能为多个
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getFirstDetailByProjectId(long projectId) throws DataServiceException;

    /**
     * 通过 typeNo查询 当前typeNo 所有 需要执行的 detailList
     *
     * @param typeNo
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getExecDetailListByTypeNo(String typeNo, long projectId) throws DataServiceException;

    /**
     * 通过 workFlowParam 参数查询 workFlowParamList
     *
     * @param workFlowParam
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowParam> getWorkFlowParamByFlowParam(WorkFlowParam workFlowParam) throws DataServiceException;

    /**
     * 通过projectId 查询workFlowParamList
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowParam> getWorkFlowParamByProJectId(Long projectId) throws DataServiceException;

    /**
     * 通过projectId 查询workFlowDetailList
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getWorkFlowDetailByProjectId(Long projectId) throws DataServiceException;

    /**
     * 根据templateId查询workFlowNodeList
     *
     * @param templateId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeBO> getWorkFlowNodeByTemplateId(int templateId) throws DataServiceException;

    /**
     * 通过flowId查询WorkFlowNodeBO
     *
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    WorkFlowNodeBO getWorkFlowNodeByFlowId(Long flowId) throws DataServiceException;

    /**
     * 根据flowId查询workFlowParam
     *
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    WorkFlowParam getWorkFlowParamByFlowId(Long flowId, Long projectId) throws DataServiceException;

    /**
     * 根据workFlowNodeId 查询模板节点的参数配置
     *
     * @param map
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowTemplateNodeParamBo> getWorkFlowTemplateNodeParamBoListByMap(Map map) throws DataServiceException;

    /**
     * 添加workFlowNodeParamBo
     *
     * @param workFlowNodeParamBo
     * @return
     * @throws DataServiceException
     */
    int addWorkFlowNodeParam(WorkFlowNodeParamBo workFlowNodeParamBo) throws DataServiceException;

    /**
     * 查询job_type_category表
     *
     * @return
     * @throws DataServiceException
     */
    List<JobTypeCategoryBO> getJobTypeCategoryBOList() throws DataServiceException;

    /**
     * 查询所有的JobTypeInfo
     *
     * @return
     * @throws DataServiceException
     */
    List<JobTypeInfo> getJobTypeInfo() throws DataServiceException;

    /**
     * 查询所有节点参数配置
     *
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowInputParamBo> getWorkFlowInputParamBoList() throws DataServiceException;

    /**
     * 查询所有样式
     *
     * @return
     * @throws DataServiceException
     */
    List<StyleBO> getStyleBOList() throws DataServiceException;

    /**
     * 根据TemplateFlowId查询work_flow_template_node_param表
     *
     * @param templateFlowId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowTemplateNodeParamBo> getTemplateNodeParamByTemplateFlowId(long templateFlowId) throws DataServiceException;

    /**
     * 根据workFlowDetailId查询work_flow_node_param表
     *
     * @param workFlowDetailId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeParamBo> getWorkFlowNodeParamByFlowDetailId(long workFlowDetailId) throws DataServiceException;


    /**
     * 根据projectId查询work_flow_node_param表
     *
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeParamBo> getWorkFlowNodeParamByProjectId(long projectId) throws DataServiceException;


    /**
     * 根据workFlowNodeParamBo更新work_flow_node_param表记录
     *
     * @param workFlowNodeParamBo
     * @throws DataServiceException
     */
    void updateWorkFlowNodeParam(WorkFlowNodeParamBo workFlowNodeParamBo) throws DataServiceException;

    /**
     * 组合查询work_flow_node_param表记录
     *
     * @param projectId
     * @param typeNo
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowNodeParamBo> getWrokFlowNodeParamListByMap(Long projectId, String typeNo) throws DataServiceException;

    /**
     * 删除work_flow_node_param数据
     *
     * @param flowDetailId
     * @throws DataServiceException
     */
    void deleteWorkFlowNodeParamByFlowDetailId(Long flowDetailId) throws DataServiceException;

    /**
     * 删除work_flow_output_filed数据
     *
     * @param flowDetailId
     * @throws DataServiceException
     */
    void deleteWorkFlowOutputFiledByFlowDetailId(Long flowDetailId) throws DataServiceException;

    /**
     * 查询所有依赖数据
     *
     * @return
     */
    List<WorkFlowInputParamRelationBO> getWorkFlowInputParamRelationBO() throws DataServiceException;

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
    List<WorkFlowListBO> getWorkFlowListByIncludeStatus(List<String> conditions,Long ProjectId)throws BizException;

    /**
     * 添加可视化工作流 dpm1.5.1
     * @param workFlowListBO
     * @throws BizException
     */
    void addWorkFlowListBO(WorkFlowListBO workFlowListBO) throws BizException;

    /**
     * 根据可视化工作流id查询所有节点 dpm1.5.1
     * @param workFlowId
     * @return
     * @throws BizException
     */
    List<WorkFlowDetail> getWorkFlowDetailByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 通过workFlowId和projectId 查询workFlowDetailList dpm1.5.1
     * @param workFlowId
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowDetail> getWorkFlowDetailByWorkFlowIdAndProjectId(Long workFlowId,Long projectId) throws DataServiceException;

    /**
     * 根据可视化工作流id查询所有节点的参数配置 dpm1.5.1
     * @param workFlowId
     * @return
     * @throws BizException
     */
    List<WorkFlowNodeParamBo> getWorkFlowNodeParamByWorkFlowId(Long workFlowId) throws  BizException;

    /**
     * 根据名称查询可视化工作流数量 用于校验 dpm1.5.1
     * @param workFlowName
     * @return
     * @throws BizException
     */
    Long getWorkFlowListBOByWorkFlowName(Long projectId,String workFlowName) throws BizException;

    /**
     * 根据工作流id删除workFlowDetail dpm1.5.1
     * @param workFlowId
     * @throws BizException
     */
    void delWorkFlowDetailByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 根据工作流id删除workFlowNodeParam dpm1.5.1
     * @param workFlowId
     * @throws BizException
     */
    void delWorkFlowNodeParamByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 根据工作流id删除workFlowList记录 dpm1.5.1
     * @param workFlowId
     * @throws BizException
     */
    void delWorkFlowListByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 根据workFlowDetails批量删除work_flow_output_filed记录 dpm1.5.1
     * @param workFlowDetails
     * @throws BizException
     */
    void deleteWorkFlowOutputFiledByFlowDetailIdList(List<WorkFlowDetail> workFlowDetails) throws BizException;

    /**
     * 推送节点查询下载编码 dpm1.5.1
     * @return
     * @throws BizException
     */
    List<ParamBO> getDownloadCoding()throws BizException;

    /**
     * 更新工作流状态 dpm 1.5.1
     * @param workFlowListBO
     * @throws BizException
     */
    void updateWorkFlowListStatus(WorkFlowListBO workFlowListBO)throws  BizException;

    /**
     * 根据workFlowId查询workFlowList表 工作流信息 dpm1.5.1
     * @param workFlowId
     * @return
     * @throws BizException
     */
    WorkFlowListBO getWorkFlowListBOByWorkFlowId(Long workFlowId)throws BizException;

    /**
     * 根据workFlowId获取该工作流下所有的首节点 dpm1.5.1
     * @param workFlowId
     * @return
     * @throws BizException
     */
    List<WorkFlowDetail> getFirstDetailByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 根据工作流id停止该工作流下所有的节点 dpm1.5.1
     * @param workFlowId
     * @throws BizException
     */
    void updateWorkFlowDetailToStopByWorkFlowId(Long workFlowId) throws BizException;

    long workFlowListCount(Long projectId,Integer status) throws  BizException;
    /**
     *根据workFlowId改变节点的状态
     * @param workFlowId
     * @throws BizException
     */
    void updateWorkFlowDetailByWorkFlowId(Long workFlowId) throws BizException;

    /**
     * 根据detailId改变节点的状态为完成状态
     * @param flowDetailId
     * @return
     */
    int updateWorkFlowDetailToPreRunFinish(long flowDetailId);

    List<WorkFlowDetail> getWorkFlowDetailListByWorkFlowIdAndStatus(Long workFlowId) throws BizException;

    List<WorkFlowDetail> getWorkFlowDetailByWorkFlowIdAndProjectIds(Long workFlowIdInt, Long projectIdInt);
}