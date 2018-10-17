package com.transing.dpmbs.integration.bo;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/20 0020.
 */
public class ProjectExportBO {
    private Integer id;
    private Integer projectId;
    private String jsonParam;
    private Integer status;
    private Integer progress;
    private String resultJsonParam;
    private String errorMessage;
    private Date createTime;
    private Date lastmodifyTime;

    public String getResultJsonParam() {
        return resultJsonParam;
    }

    public void setResultJsonParam(String resultJsonParam) {
        this.resultJsonParam = resultJsonParam;
    }

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

    public String getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
}
