package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.UserDataService;
import com.transing.dpmbs.integration.VisualizationDataService;
import com.transing.dpmbs.integration.bo.*;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Scope("prototype")
@Repository("visualizationDataService")
public class VisualizationDAOibatis extends BaseDaoiBATIS implements VisualizationDataService {


    @Override
    public List<VisTemplateBO> getVisTemplateList() throws DataServiceException {
        return sqlSessionTemplate.selectList("VisualizationMapper.getVisTemplateList");
    }

    @Override
    public List<VisualizationBO> getVisualizationList(long projectId) throws DataServiceException {
        return sqlSessionTemplate.selectList("VisualizationMapper.getVisualizationList",projectId);
    }

    @Override
    public int addVisualization(VisualizationBO visualizationBO) throws DataServiceException {
        return sqlSessionTemplate.insert("VisualizationMapper.addVisualization",visualizationBO);
    }

    @Override
    public int updateVisualization(VisualizationBO visualizationBO) throws DataServiceException {
        return sqlSessionTemplate.update("VisualizationMapper.updateVisualization",visualizationBO);
    }

    @Override
    public List<CategoryBO> getVisCategoryList() throws DataServiceException {
        return sqlSessionTemplate.selectList("VisualizationMapper.getVisCategoryList");
    }

    @Override
    public List<VisChartBOWithBLOBs> getVisChartList() throws DataServiceException {
        return sqlSessionTemplate.selectList("VisualizationMapper.getVisChartList");
    }

    @Override
    public VisChartBOWithBLOBs getVisChartById(int visChartId) throws DataServiceException {
        return sqlSessionTemplate.selectOne("VisualizationMapper.getVisChartById",visChartId);
    }

    @Override
    public List<VisJsonParamBO> getVisSettingJsonParamList() throws DataServiceException {
        return sqlSessionTemplate.selectList("VisualizationMapper.getVisSettingJsonParamList");
    }

    @Override
    public List<VisJsonParamBO> getVisSettingJsonParamListByType(String type) throws DataServiceException {
        return sqlSessionTemplate.selectList("VisualizationMapper.getVisSettingJsonParamListByType",type);
    }

    @Override
    public VisualizationBO getVisualizationById(int visId) throws DataServiceException {
        return sqlSessionTemplate.selectOne("VisualizationMapper.getVisualizationById",visId);
    }

    @Override
    public int deleteVisById(int visId) throws DataServiceException {
        return sqlSessionTemplate.delete("VisualizationMapper.deleteVisById",visId);
    }

    @Override
    public int addVisModule(VisModuleBO visModuleBO) throws DataServiceException {
        return sqlSessionTemplate.insert("VisualizationMapper.addVisModule",visModuleBO);
    }

    @Override
    public int deleteVisModuleByVisId(int visId) throws DataServiceException {
        return sqlSessionTemplate.delete("VisualizationMapper.deleteVisModuleByVisId",visId);
    }

    @Override
    public List<VisModuleBO> getVisModuleList(int visId) throws DataServiceException {
        return sqlSessionTemplate.selectList("VisualizationMapper.getVisModuleList",visId);
    }
}