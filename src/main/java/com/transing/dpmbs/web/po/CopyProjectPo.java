package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * 复制任务集合
 */
@ApiModel(value = "复制任务据模型")
public class CopyProjectPo {
    private Long projectId;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
