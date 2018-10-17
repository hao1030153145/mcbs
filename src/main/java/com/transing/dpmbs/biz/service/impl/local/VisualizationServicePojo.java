package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.transing.dpmbs.biz.service.UserService;
import com.transing.dpmbs.biz.service.VisualizationService;
import com.transing.dpmbs.integration.UserDataService;
import com.transing.dpmbs.integration.VisualizationDataService;
import com.transing.dpmbs.integration.bo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("visualizationService")
public class VisualizationServicePojo extends BaseService implements VisualizationService {
    @Resource
    private VisualizationDataService visualizationDataService;

    @Override
    public List<VisualizationBO> getVisualizationList(long projectId) throws BizException {
        try {
            return visualizationDataService.getVisualizationList(projectId);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public List<VisTemplateBO> getVisTemplateList() throws BizException {
        return visualizationDataService.getVisTemplateList();
    }

    @Override
    public int deleteVisById(int visId) throws BizException {
        return visualizationDataService.deleteVisById(visId);
    }

    @Override
    public int addVisModule(VisModuleBO visModuleBO) throws BizException {
        return visualizationDataService.addVisModule(visModuleBO);
    }

    @Override
    public int deleteVisModuleByVisId(int visId) throws BizException {
        return visualizationDataService.deleteVisModuleByVisId(visId);
    }

    @Override
    public List<VisModuleBO> getVisModuleList(int visId) throws BizException {
        return visualizationDataService.getVisModuleList(visId);
    }

    @Override
    public List<VisJsonParamBO> getVisSettingJsonParamListByType(String type) throws BizException {
        return visualizationDataService.getVisSettingJsonParamListByType(type);
    }

    @Override
    public int addVisualization(VisualizationBO visualizationBO) throws BizException {
        try {
            return visualizationDataService.addVisualization(visualizationBO);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public int updateVisualization(VisualizationBO visualizationBO) throws BizException {
        try {
            return visualizationDataService.updateVisualization(visualizationBO);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public List<CategoryBO> getVisCategoryList() throws BizException {
        try {
            return visualizationDataService.getVisCategoryList();
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public List<VisChartBOWithBLOBs> getVisChartList() throws BizException {
        try {
            return visualizationDataService.getVisChartList();
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public VisChartBOWithBLOBs getVisChartById(int visChartId) throws BizException {
        try {
            return visualizationDataService.getVisChartById(visChartId);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public List<VisJsonParamBO> getVisSettingJsonParamList() throws BizException {
        try {
            return visualizationDataService.getVisSettingJsonParamList();
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }

    @Override
    public VisualizationBO getVisualizationById(int visId) throws BizException {
        return visualizationDataService.getVisualizationById(visId);
    }
}
