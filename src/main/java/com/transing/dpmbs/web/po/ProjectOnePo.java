package com.transing.dpmbs.web.po;

import com.transing.dpmbs.integration.bo.Project;
import com.transing.dpmbs.integration.bo.ProjectOne;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 查询单个任务集合
 */
@ApiModel(value = "查询单个任务数据模型")
public class ProjectOnePo {
    @ApiModelProperty(value = "查询任务集合", required = true)
    private ProjectOne projectOne;

    public ProjectOne getProjectOne() {
        return projectOne;
    }

    public void setProjectOne(ProjectOne projectOne) {
        this.projectOne = projectOne;
    }
}
