package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/15.
 */
public class WorkFlowTemplateListPO {
    @ApiModelProperty(value = "抓取流程列表集合", required = true)
    private List<WorkFlowTemplatePO> workFlowTemplateList;
    @ApiModelProperty(value = "抓取流程个数", required = true)
    private long count;

    public List<WorkFlowTemplatePO> getWorkFlowTemplateList() {
        return workFlowTemplateList;
    }

    public void setWorkFlowTemplateList(List<WorkFlowTemplatePO> workFlowTemplateList) {
        this.workFlowTemplateList = workFlowTemplateList;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
