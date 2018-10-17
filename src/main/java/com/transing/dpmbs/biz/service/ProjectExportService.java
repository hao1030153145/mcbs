package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.transing.dpmbs.integration.bo.ProjectExportBO;
import com.transing.dpmbs.web.filter.ProjectExportFilter;
import com.transing.dpmbs.web.po.ProjectExportPo;

import java.util.List;
import java.util.Map;

/**
 * Created by byron on 2017/11/21 0021.
 */
public interface ProjectExportService {
    /**
     * 查询导出任务
     * @param projectExportFilter
     * @return
     * @throws BizException
     */
    List<ProjectExportPo> getProjectExportListByProjectExportFilter(ProjectExportFilter projectExportFilter)throws BizException;

    /**
     * 添加导出任务
     * @param projectExportBO
     * @throws BizException
     */
    void addProjectExportBO(ProjectExportBO projectExportBO) throws BizException;

    /**
     * 根据projectId查询导出项目
     * @param id
     * @return
     * @throws BizException
     */
    ProjectExportBO getProjectExportById(long id)throws BizException;

    /**
     * 通过id更新project_export的status
     * @param map
     * @throws BizException
     */
    void updateStatusById(Map<String,String> map)throws BizException;

    /**
     * 根据id 更新 project_export的progress
     * @param id
     * @throws BizException
     */
    void  updateProgressById(Long id,int progress)throws BizException;

    /**
     * 删除
     * @param id
     * @throws BizException
     */
    void deleteExportProjectById(int id) throws BizException;

    /**
     *  查询总数
     * @param id
     * @return
     * @throws BizException
     */
    int getProjectExportCount(int id)throws BizException;
}
