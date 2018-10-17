package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.transing.dpmbs.biz.service.ProjectExportService;
import com.transing.dpmbs.integration.ProjectExportDataService;
import com.transing.dpmbs.integration.bo.ProjectExportBO;
import com.transing.dpmbs.web.filter.ProjectExportFilter;
import com.transing.dpmbs.web.po.ProjectExportPo;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by byron on 2017/11/21 0021.
 */
@Scope("prototype")
@Repository("projectExportService")
public class ProjectExportServicePojo implements ProjectExportService {
    @Resource
    private ProjectExportDataService projectExportDataService;
    @Override
    public List<ProjectExportPo> getProjectExportListByProjectExportFilter(ProjectExportFilter projectExportFilter) throws BizException {
        return projectExportDataService.getProjectExportListByProjectExportFilter(projectExportFilter);
    }

    @Override
    public void addProjectExportBO(ProjectExportBO projectExportBO) throws BizException {
        projectExportDataService.addProjectExportBO(projectExportBO);
    }

    @Override
    public ProjectExportBO getProjectExportById(long id) throws BizException {
        return projectExportDataService.getProjectExportById(id);
    }

    @Override
    public void updateStatusById(Map<String,String> map) throws BizException {
        projectExportDataService.updateStatusById(map);
    }

    @Override
    public void updateProgressById(Long id,int progress) throws BizException {
        projectExportDataService.updateProgressById(id,progress);
    }

    @Override
    public void deleteExportProjectById(int id) throws BizException {
        projectExportDataService.deleteExportProjectById(id);
    }

    @Override
    public int getProjectExportCount(int id) throws BizException {
         return projectExportDataService.getProjectExportCount(id);
    }
}
