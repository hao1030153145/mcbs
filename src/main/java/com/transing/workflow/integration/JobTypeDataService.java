package com.transing.workflow.integration;

import com.jeeframework.logicframework.integration.DataService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.OutDataSourceBo;
import com.transing.workflow.integration.bo.OutDataSourceDemoParamter;
import com.transing.workflow.integration.bo.OutDataSourceDetailBo;
import com.transing.workflow.integration.bo.ProjectResultTypeBO;
import com.transing.workflow.integration.bo.*;
import java.util.List;

/**
 *  JobInfo数据操作接口
 *
 * @author Sunny
 * @version 1.0
 * @see
 */
public interface JobTypeDataService extends DataService {

    /**
     * 简单描述：根据jobNo返回有效的job信息
     * <p/>
     *
     * @param jobNo
     * @
     */
    JobTypeInfo getValidJobTypeByTypeNo(String jobNo);


    /**
     * 简单描述：得到所有有效的Job信息
     * <p/>
     *
     * @param
     * @
     */
    List<JobTypeInfo> getAllValidJobTypeInfo();

    /**
     * 简单描述：得到该projectId的ResultTypeId
     * <p/>
     *
     * @param
     * @
     */
    List<Long> getOutDataSourceId(long projectId);

    /**
     * 简单描述：得到该ResultTypeId的输出数据源
     * <p/>
     *
     * @param
     * @
     */
    List<OutDataSourceBo> getOutDataSource(List<Long> idList);

    List<OutDataSourceBo> getOutDataSourceRejectAnlysis(List<Long> idList);

    List<OutDataSourceDetailBo> getOutDataSourceDetail(long resultTypeId);

    List<OutDataSourceDemoParamter> getOutDataSourceDemoParamterList(long resultTypeId);
    /**
     * 添加 项目 在各个节点返回 数据类型
     * @return
     * @throws DataServiceException
     */
    int addProjectResult(ProjectResultTypeBO projectResultTypeBO)throws DataServiceException;

    /**
     * 根据 projectResultTypeBO 信息删除 projectResultTypeBO
     * @param projectResultTypeBO
     * @return
     * @throws DataServiceException
     */
    int deleteProjectResultByProjectResult(ProjectResultTypeBO projectResultTypeBO)throws DataServiceException;

    /**
     * 根据projectId 删除 ProjectResultType
     * @param projectId
     * @return
     * @throws DataServiceException
     */
    int deleteProjectResultByProjectId(long projectId)throws DataServiceException;

    /**
     * 根据dataSourceTypeId  和 typeNo 查询JobTypeResultBO
     * @param dataSourceTypeId
     * @return
     * @throws DataServiceException
     */
    JobTypeResultBO getJobTypeResultByParam(String typeNo,long dataSourceTypeId)throws DataServiceException;

    /**
     * 根据typeNo 查询 JobTypeResultList
     * @param typeNo
     * @return
     * @throws DataServiceException
     */
    List<JobTypeResultBO> getJobTypeResultListByTypeNo(String typeNo)throws DataServiceException;

    /**
     * 根据dataSourceTypes  和 typeNo 查询JobTypeResultBO
     * @param typeNo
     * @param dataSourceTypes
     * @return
     * @throws DataServiceException
     */
    List<JobTypeResultBO> getJobTypeResultListByParam(String typeNo,List<Long> dataSourceTypes)throws DataServiceException;

    /**
     * 根据 reusltTypeId查询 JobTypeResult对象
     * @param reusltTypeId
     * @return
     * @throws DataServiceException
     */
    JobTypeResultBO getJobTypeResultByResultTypeId(long reusltTypeId)throws DataServiceException;

    /**
     * 根据 reusltTypeId 查询 JobTypeResultFieldList
     * @return
     * @throws DataServiceException
     */
    List<JobTypeResultField> getResultFieldListByResultTypeId(long reusltTypeId)throws DataServiceException;

}