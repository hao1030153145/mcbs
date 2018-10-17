package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.transing.dpmbs.biz.service.ContentTypeService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.integration.ContentTypeDataService;
import com.transing.dpmbs.integration.ProjectJobTypeDataService;
import com.transing.dpmbs.integration.bo.DatasourceTypeBO;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.web.po.ContentTypePO;
import com.transing.dpmbs.web.po.DatasourceTypePO;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.integration.bo.JobTypeInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lanceyan
 * @version 1.0
 */
@Service("projectJobTypeService")
public class ProjectJobTypeServicePojo extends BaseService implements ProjectJobTypeService {
    @Resource
    private ProjectJobTypeDataService projectJobTypeDataService;

    @Resource
    private JobTypeService jobTypeService;

    @Override
    public List<ProjectJobTypeBO> getProjectJobTypeListByProjectId(long projectId) throws BizException {
        return projectJobTypeDataService.getProjectJobTypeListByProjectId(projectId);
    }

    @Override
    @Transactional
    public boolean addProjectJobType(List<String> typeNos, long projectId) throws BizException {

        //因为前端做不到 排好序 在传上来 所以写了这个影响效率的 垃圾代码
        List<JobTypeInfo> jobTypeInfoList = jobTypeService.getAllValidJobTypeInfo();

        int lastProjectJobTypeId = 0;
        String lastTypeNo = "";

        for (JobTypeInfo jobTypeInfo:jobTypeInfoList) {
            String typeNo = jobTypeInfo.getTypeNo();
            int orderNumber = jobTypeInfo.getOrderNumber();

            if(typeNos.contains(typeNo)){

                //如果最后添加的 项目工作流信息 则 更新 该数据的前一个 typeNo信息
                if(lastProjectJobTypeId > 0){

                    ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                    projectJobTypeBO.setId(lastProjectJobTypeId);
                    projectJobTypeBO.setNextTypeNo(typeNo);
                    projectJobTypeDataService.updateProjectJobType(projectJobTypeBO);
                }

                //正常就添加当前 typeNo 项目工作流信息
                ProjectJobTypeBO projectJobTypeBO = new ProjectJobTypeBO();
                projectJobTypeBO.setProjectId(projectId);
                projectJobTypeBO.setTypeNo(typeNo);
                projectJobTypeBO.setSortNo(orderNumber);
                projectJobTypeBO.setPreTypeNo(lastTypeNo);
                projectJobTypeBO.setNextTypeNo("");//先暂时设置下一个typeNo为空

                projectJobTypeDataService.addProjectJobType(projectJobTypeBO);

                lastTypeNo = typeNo;
                lastProjectJobTypeId =  projectJobTypeBO.getId();

            }
        }

        return true;
    }

    @Override
    public ProjectJobTypeBO getProjectJobTypeListByProjectJobType(ProjectJobTypeBO projectJobTypeBO) throws BizException {
        return projectJobTypeDataService.getProjectJobTypeListByProjectJobType(projectJobTypeBO);
    }
}