package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONArray;

import java.util.Date;
import java.util.List;

@ApiModel(value = "抓取流程配置 详情页面 对象")
public class WorkFlowTemplateDetailPO {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("抓取配置名称")
    private String templateName;

    @ApiModelProperty("抓取配置 详情 list")
    private List<WorkFlowNodePO> workFlowInfoList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<WorkFlowNodePO> getWorkFlowInfoList() {
        return workFlowInfoList;
    }

    public void setWorkFlowInfoList(List<WorkFlowNodePO> workFlowInfoList) {
        this.workFlowInfoList = workFlowInfoList;
    }
}