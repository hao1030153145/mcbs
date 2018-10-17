package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.TemplateOutputFiledBO;
import com.transing.dpmbs.integration.bo.VisWorkFlowBO;

import java.util.List;

/**
 * Created by Administrator on 2018/1/5 0005.
 */
public interface VisWorkFlowDataService {



    List<VisWorkFlowBO> getVisWorkFlow(Integer flowDetailId);

    /**
     * 添加输出字段到work_flow_output_filed表
     * @param visWorkFlowBO
     * @return
     * @throws DataServiceException
     */
    int addWorkFlowOutputFiled(VisWorkFlowBO visWorkFlowBO) throws DataServiceException;

    /**
     * 删除输出字段
     * @param flowDetalId
     * @throws DataServiceException
     */
    void deleteWorkFlowOutputFiledByDetailId(Long flowDetalId) throws DataServiceException;

    /**
     * 查询模板输出字段
     * @param flowId
     * @return
     */
    List<TemplateOutputFiledBO> getWorkFlowTemplateOutputFiledList(Integer flowId);

    /**
     * 添加输出字段到work_flow_template_output_filed表 模板输出字段表 dpm1.5.1
     * @param templateOutputFiledBO
     * @return
     * @throws DataServiceException
     */
    int addWorkFlowTemplateOutputFiled(TemplateOutputFiledBO templateOutputFiledBO) throws DataServiceException;

    /**
     * 删除输出字段 模板输出字段表 dpm1.5.1
     * @param flowId
     * @throws DataServiceException
     */
    void delWorkFlowTemplateOutputFiled(Long flowId) throws DataServiceException;
}
