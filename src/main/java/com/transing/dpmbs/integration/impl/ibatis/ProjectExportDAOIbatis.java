package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.ProjectExportDataService;
import com.transing.dpmbs.integration.bo.ProjectExportBO;
import com.transing.dpmbs.web.filter.ProjectExportFilter;
import com.transing.dpmbs.web.po.ProjectExportPo;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by byron on 2017/11/21 0021.
 */
@Scope("prototype")
@Repository("projectExportDataService")
public class ProjectExportDAOIbatis extends BaseDaoiBATIS implements ProjectExportDataService{
    @Override
    public List<ProjectExportPo> getProjectExportListByProjectExportFilter(ProjectExportFilter projectExportFilter) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("projectExport.getProjectExportListByProjectExportFilter",projectExportFilter);
        }catch (DataAccessException e){
            throw new DAOException("根据条件查询项目列表失败", e);
        }
    }

    @Override
    public void addProjectExportBO(ProjectExportBO projectExportBO) throws BizException {
        try{
            sqlSessionTemplate.insert("projectExport.addProjectExportBO",projectExportBO);
        }catch (DataAccessException e){
            throw new DAOException("添加失败",e);
        }
    }

    @Override
    public ProjectExportBO getProjectExportById(long id) throws BizException {
        try{
            return sqlSessionTemplate.selectOne("projectExport.getProjectExportById",id);
        }catch (DataAccessException e){
            throw new DAOException("查询失败",e);
        }
    }

    @Override
    public void updateStatusById(Map<String,String> map) throws BizException {
        try{
            sqlSessionTemplate.update("projectExport.updateStatusById",map);
        }catch (DataAccessException e){
            throw new DAOException("更新失败",e);
        }
    }

    @Override
    public void updateProgressById(Long id,int progress) throws BizException {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("progress",progress);
        try{
            sqlSessionTemplate.update("projectExport.updateProgressById",map);
        }catch (DataAccessException e){
            throw new DAOException("更新失败",e);
        }
    }

    @Override
    public void deleteExportProjectById(int id) throws BizException {
        try{
            sqlSessionTemplate.delete("projectExport.deleteExportProjectById",id);
        }catch (DataAccessException e){
            throw new DAOException("删除失败",e);
        }
    }

    @Override
    public int getProjectExportCount(int id) throws BizException {
        try {
           return sqlSessionTemplate.selectOne("projectExport.getProjectExportCount", id);
        } catch (DataAccessException e) {
            throw new DAOException("查询数量失败", e);
        }
    }
}
