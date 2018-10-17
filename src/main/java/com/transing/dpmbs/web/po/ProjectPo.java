package com.transing.dpmbs.web.po;

import com.transing.dpmbs.integration.bo.Project;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * 任务列表集合
 */
@ApiModel(value = "用户数据模型")
public class ProjectPo {
    @ApiModelProperty(value = "项目列表集合", required = true)
    private List<Map<String,Object>> projectList;
    @ApiModelProperty(value = "项目个数", required = true)
    private long count;

    public List<Map<String,Object>> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Map<String,Object>>projectList) {
        this.projectList = projectList;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
