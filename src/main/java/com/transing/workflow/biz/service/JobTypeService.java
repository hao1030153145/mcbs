package com.transing.workflow.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BizService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.workflow.integration.bo.*;

import java.util.List;

/**
 * @author Sunny
 * @version 1.0
 */
public interface JobTypeService extends BizService {

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
     * 简单描述：得到该projectId的输出数据源
     * <p/>
     *
     * @param
     * @
     */
    List<OutDataSourceBo> getOutDataSource(long projectId);

    List<OutDataSourceBo> getOutDataSourceRejectAnlysis(long projectId);

    /**
     * 简单描述：得到该projectId的输出数据源
     * <p/>
     *
     * @param
     * @
     */
    List<OutDataSourceDetailBo> getOutDataSourceDetail(long resultTypeIdInt);

    /**
     * 简单描述：得到该projectId的输出数据源
     * <p/>
     *
     * @param
     * @
     */
    List<OutDataSourceDemoParamter> getOutDataSourceDemoParamterList(long resultTypeIdInt);

    /**
     * 根据 reusltTypeId查询 JobTypeResult对象
     * @param reusltTypeId
     * @return
     * @throws DataServiceException
     */
    JobTypeResultBO getJobTypeResultByResultTypeId(long reusltTypeId)throws BizException;

    /**
     * 根据typeNo 查询 JobTypeResultList
     * @param typeNo
     * @return
     * @throws DataServiceException
     */
    List<JobTypeResultBO> getJobTypeResultListByTypeNo(String typeNo)throws BizException;

    /**
     * 根据 projectResultTypeBO 信息删除 projectResultTypeBO
     * @param projectResultTypeBO
     * @return
     * @throws BizException
     */
    int deleteProjectResultByProjectResult(ProjectResultTypeBO projectResultTypeBO)throws BizException;

    /**
     * 根据 reusltTypeId 查询 JobTypeResultFieldList
     * @return
     * @throws BizException
     */
    List<JobTypeResultField> getResultFieldListByResultTypeId(long reusltTypeId)throws BizException;

}