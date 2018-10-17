package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.biz.service.VisWorkFlowService;
import com.transing.dpmbs.integration.VisWorkFlowDataService;
import com.transing.dpmbs.integration.bo.TemplateOutputFiledBO;
import com.transing.dpmbs.integration.bo.VisWorkFlowBO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2018/1/5 0005.
 */

@Service("visWorkFlowService")
public class VisWorkFlowServicePojo extends BaseService implements VisWorkFlowService {

    @Resource
    private VisWorkFlowDataService visWorkFlowDataService;


    @Override
    public List<VisWorkFlowBO> getVisWorkFlowList(Integer flowDetailId) {
        return visWorkFlowDataService.getVisWorkFlow(flowDetailId);
    }

    @Override
    public int addWorkFlowOutputFiled(VisWorkFlowBO visWorkFlowBO) throws DataServiceException {
        return visWorkFlowDataService.addWorkFlowOutputFiled(visWorkFlowBO);
    }

    @Override
    public List<TemplateOutputFiledBO> getWorkFlowTemplateOutputFiledList(Integer flowId) {
        return visWorkFlowDataService.getWorkFlowTemplateOutputFiledList(flowId);
    }

    @Override
    public void deleteWorkFlowOutputFiledByDetailId(Long flowDetalId) throws DataServiceException {
        visWorkFlowDataService.deleteWorkFlowOutputFiledByDetailId(flowDetalId);
    }

    @Override
    public int addWorkFlowTemplateOutputFiled(TemplateOutputFiledBO templateOutputFiledBO) throws DataServiceException {
         return visWorkFlowDataService.addWorkFlowTemplateOutputFiled(templateOutputFiledBO);
    }

    @Override
    public void delWorkFlowTemplateOutputFiled(Long flowId) throws DataServiceException {
        visWorkFlowDataService.delWorkFlowTemplateOutputFiled(flowId);
    }
}
