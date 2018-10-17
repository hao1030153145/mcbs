package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.*;

import java.util.List;

public interface VisualizationDataService {

     List<VisualizationBO> getVisualizationList(long projectId) throws DataServiceException;

     int addVisualization(VisualizationBO visualizationBO) throws DataServiceException;

     int updateVisualization(VisualizationBO visualizationBO) throws DataServiceException;

     List<CategoryBO> getVisCategoryList()throws DataServiceException;

     List<VisChartBOWithBLOBs> getVisChartList()throws DataServiceException;

     VisChartBOWithBLOBs getVisChartById(int visChartId)throws DataServiceException;

     List<VisJsonParamBO> getVisSettingJsonParamList()throws DataServiceException;

     List<VisJsonParamBO> getVisSettingJsonParamListByType(String type)throws DataServiceException;

     List<VisTemplateBO> getVisTemplateList()throws BizException;

    VisualizationBO getVisualizationById(int visId)throws DataServiceException;

    int deleteVisById(int visId)throws DataServiceException;

    int addVisModule(VisModuleBO visModuleBO)throws DataServiceException;

    int deleteVisModuleByVisId(int visId)throws DataServiceException;

    List<VisModuleBO> getVisModuleList(int visId)throws DataServiceException;

}
