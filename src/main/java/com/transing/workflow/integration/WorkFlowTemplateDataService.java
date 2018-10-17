package com.transing.workflow.integration;

import com.jeeframework.logicframework.integration.DataService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.web.po.WorkFlowTemplatePO;
import com.transing.workflow.integration.bo.WorkFlowNodeBO;
import com.transing.workflow.integration.bo.WorkFlowTemplateBO;
import com.transing.workflow.integration.bo.WorkFlowTemplateNodeParamBo;

import java.util.List;
import java.util.Map;

/**
 * WorkFlow数据操作接口
 *
 * @author Sunny
 * @version 1.0
 * @see
 */
public interface WorkFlowTemplateDataService extends DataService {
    WorkFlowTemplateBO getWorkFlowTemplateListById(int id) throws DataServiceException;

    List<WorkFlowTemplateBO> getWorkFlowTemplateListByParam(Integer status, String name, Integer page, Integer size) throws DataServiceException;

    List<WorkFlowTemplateBO> getVisWorkFlowTemplateListByParam(Integer status, String name, Integer page, Integer size, String createTime, String endTime) throws DataServiceException;

    Integer getWorkFlowTemplateCountByParam(Integer status, String name) throws DataServiceException;

    Integer getStatusByDetailId(Integer detailId) throws DataServiceException;

    String getTypeNoByDetailId(Integer flowDetailId) throws DataServiceException;

    Integer getVisWorkFlowTemplateCountByParam(Integer status, String name, String createTime, String endTime) throws DataServiceException;

    int addWorkFlowTemplate(WorkFlowTemplateBO workFlowTemplateBO) throws DataServiceException;

    List<String> getVisTemplateNameList() throws DataServiceException;

    List<WorkFlowNodeBO> getWorkFlowNodeListByTemplateId(int templateId) throws DataServiceException;

    int addWorkFlowNode(WorkFlowNodeBO workFlowNodeBO) throws DataServiceException;

    int updateWorkFlowNode(WorkFlowNodeBO workFlowNodeBO) throws DataServiceException;

    int updateWorkTemplate(WorkFlowTemplateBO workFlowTemplateBO) throws DataServiceException;

    int getVisWorkTemplateIsExistingByName(String name) throws DataServiceException;

    int deleteWorkFlowNodeByTemplateId(int templateId) throws DataServiceException;

    int deleteWorkTemplateById(int id) throws DataServiceException;

    int logicDeleteVisWorkFlowTemplateByIds(List<Integer> list) throws DataServiceException;

    List<WorkFlowTemplatePO> getWorkFlowTemplateListByCondition(Map<String, Object> param) throws DataServiceException;

    int getWorkFlowTemplateCountByCondition(Map<String, Object> param) throws DataServiceException;

    /**
     * 删除work_flow_node表的记录通过flowId
     *
     * @param flowId
     * @throws DataServiceException
     */
    void deleteWorkFlowNodeByFlowId(Long flowId) throws DataServiceException;

    /**
     * 删除work_flow_template_node_param表的记录通过flowId
     *
     * @param flowId
     * @throws DataServiceException
     */
    void deleteWorkFlowTemplateNodeParamByFlowId(Long flowId) throws DataServiceException;

    /**
     * 更新work_flow_template_node_param表。
     *
     * @param workFlowTemplateNodeParamBo
     * @throws DataServiceException
     */
    void updateWorkFlowTemplateNodeParam(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo) throws DataServiceException;

    /**
     * 添加work_flow_template_node_param表
     *
     * @param workFlowTemplateNodeParamBo
     * @throws DataServiceException
     */
    void addWorkFlowTemplateNodeParam(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo) throws DataServiceException;

    /**
     * 根据flowId查询work_flow_template_node_param 记录
     *
     * @param flowId
     * @return
     * @throws DataServiceException
     */
    List<WorkFlowTemplateNodeParamBo> getWorkFlowTemplateNodeParamByFlowId(Long flowId) throws DataServiceException;

    List<WorkFlowNodeBO> getWorkFlowNodeListByTypeNoList(List<String> typeNoList, Long templateId) throws DataServiceException;

    WorkFlowNodeBO getWorkFlowDetailByFlowId(long preFlowIdIds) throws DataServiceException;
}
