package com.transing.workflow.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BizService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.web.po.WorkFlowTemplateDetailPO;
import com.transing.dpmbs.web.po.WorkFlowTemplatePO;
import com.transing.workflow.integration.bo.WorkFlowNodeBO;
import com.transing.workflow.integration.bo.WorkFlowTemplateBO;

import java.util.List;
import java.util.Map;

/**
 * @author Sunny
 * @version 1.0
 */
public interface WorkFlowTemplateService extends BizService {

    WorkFlowTemplateBO getWorkFlowTemplateListById(int id) throws BizException;

    List<WorkFlowTemplateBO> getWorkFlowTemplateListByParam(Integer status, String name, Integer page, Integer size) throws BizException;

    List<WorkFlowTemplateBO> getVisWorkFlowTemplateListByParam(Integer status, String name, Integer page, Integer size, String createTime, String endTime) throws BizException;

    Integer getVisWorkFlowTemplateCountByParam(Integer status, String name, String createTime, String endTime) throws BizException;

    Integer getWorkFlowTemplateCountByParam(Integer status, String name) throws BizException;

    Integer getStatusByDetailId(Integer detailId) throws BizException;

    String getTypeNoByDetailId(Integer flowDetailId) throws BizException;

    int addWorkFlowTemplate(WorkFlowTemplateBO workFlowTemplateBO) throws BizException;

    List<String> getVisTemplateNameList() throws  BizException;

    List<WorkFlowNodeBO> getWorkFlowNodeListByTemplateId(int templateId) throws BizException;

    int addWorkFlowNode(WorkFlowNodeBO workFlowNodeBO) throws BizException;

    int saveWorkFlowTemplateDeial(WorkFlowTemplateDetailPO workFlowTemplateDetailPO) throws BizException;

    int deleteWorkFlowTemplateDetail(int workFlowTemplateId) throws BizException;

    int updateWorkFlowNode(WorkFlowNodeBO workFlowNodeBO) throws BizException;

    int updateWorkTemplate(WorkFlowTemplateBO workFlowTemplateBO) throws BizException;

    int getVisWorkTemplateIsExistingByName(String name) throws BizException;

    int logicDeleteVisWorkFlowTemplateByIds(List<Integer> list) throws BizException;

    List<WorkFlowTemplatePO> getWorkFlowTemplateListByCondition(Map<String, Object> param) throws BizException;

    int getWorkFlowTemplateCountByCondition(Map<String, Object> param) throws BizException;

    /**
     * 保存可视化工作流模板节点配置信息
     *
     * @param body
     * @return
     * @throws BizException
     */
    List<Map<String, Object>> addVisWorkFlowTemplateNodeParam(String body) throws DataServiceException;
}