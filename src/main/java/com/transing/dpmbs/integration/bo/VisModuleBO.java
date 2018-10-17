package com.transing.dpmbs.integration.bo;

import java.util.Date;

public class VisModuleBO {
    private Integer id;

    private Integer projectId;

    private Integer visId;

    private Date createTime;

    private Date lastmodifyTime;

    private String jsonParam;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getVisId() {
        return visId;
    }

    public void setVisId(Integer visId) {
        this.visId = visId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastmodifyTime() {
        return lastmodifyTime;
    }

    public void setLastmodifyTime(Date lastmodifyTime) {
        this.lastmodifyTime = lastmodifyTime;
    }

    public String getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
    }
}