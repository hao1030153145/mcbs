package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.integration.DataService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.DatasourceTypeBO;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.web.po.ContentTypePO;
import com.transing.dpmbs.web.po.DatasourceTypePO;

import java.util.List;

/**
 * 用户数据操作接口
 *
 * @author lanceyan
 * @version 1.0
 * @see
 */
public interface ProjectJobTypeDataService extends DataService {

    /**
     * 通过项目id 查询 出该项目的工作流 信息
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    List<ProjectJobTypeBO> getProjectJobTypeListByProjectId(long projectId)throws DataServiceException;

    /**
     * 添加 projectJobTypeBO 对象
     * @return
     * @throws DataServiceException
     */
    long addProjectJobType(ProjectJobTypeBO projectJobTypeBO)throws DataServiceException;

    /**
     * 通过projectId 删除项目工作流 信息
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    int deleteByProjectId(long projectId)throws DataServiceException;

    /**
     * 更新 projectJobTypeBO 对象
     * @param projectJobTypeBO
     * @return
     * @throws DataServiceException
     */
    int updateProjectJobType(ProjectJobTypeBO projectJobTypeBO)throws DataServiceException;

    /**
     * 通过 ProjectJobTypeBO 对象 查询 ProjectJobTypeBO对象
     * @param projectJobTypeBO
     * @return
     * @throws DataServiceException
     */
    ProjectJobTypeBO getProjectJobTypeListByProjectJobType(ProjectJobTypeBO projectJobTypeBO)throws DataServiceException;
}