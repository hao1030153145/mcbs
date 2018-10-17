package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.transing.dpmbs.integration.bo.*;

import java.util.List;


public interface VisualizationService {

    List<VisualizationBO> getVisualizationList(long projectId) throws BizException;

    int addVisualization(VisualizationBO visualizationBO) throws BizException;

    int updateVisualization(VisualizationBO visualizationBO) throws BizException;

    List<CategoryBO> getVisCategoryList()throws BizException;

    List<VisChartBOWithBLOBs> getVisChartList()throws BizException;

    VisChartBOWithBLOBs getVisChartById(int visChartId)throws BizException;

    List<VisJsonParamBO> getVisSettingJsonParamList()throws BizException;

    VisualizationBO getVisualizationById(int visId)throws BizException;

    List<VisTemplateBO> getVisTemplateList()throws BizException;

    int deleteVisById(int visId)throws BizException;

    int addVisModule(VisModuleBO visModuleBO)throws BizException;

    int deleteVisModuleByVisId(int visId)throws BizException;

    List<VisModuleBO> getVisModuleList(int visId)throws BizException;

    List<VisJsonParamBO> getVisSettingJsonParamListByType(String type)throws BizException;
}
