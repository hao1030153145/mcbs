package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BizService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.DatasourceTypeBO;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.web.po.ContentTypePO;
import com.transing.dpmbs.web.po.DatasourceTypePO;
import com.transing.dpmbs.web.po.StatisticsAnalysisPo;

import java.util.List;

/**
 * @author lanceyan
 * @version 1.0
 */
public interface ProjectJobTypeService extends BizService {


    /**
     * 通过项目id 查询 出该项目的工作流 信息
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<ProjectJobTypeBO> getProjectJobTypeListByProjectId(long projectId)throws BizException;

    /**
     * 通过 typeNos 和projectId 添加 项目工作流信息
     * @param typeNos
     * @param projectId
     * @return
     * @throws BizException
     */
    boolean addProjectJobType(List<String> typeNos,long projectId)throws BizException;

    /**
     * 通过 ProjectJobTypeBO 对象 查询 ProjectJobTypeBO对象
     * @param projectJobTypeBO
     * @return
     * @throws DataServiceException
     */
    ProjectJobTypeBO getProjectJobTypeListByProjectJobType(ProjectJobTypeBO projectJobTypeBO)throws BizException;

}