package com.transing.dpmbs.integration.bo;

import java.util.Date;

/**
 * Created by byron on 2018/3/14 0014.
 */
public class WorkFlowListBO {

    private Long workFlowId;
    private Long projectId;
    private Integer workFlowTemplateId;
    private String workFlowName;
    private String createTime;
    private String finishTime;
    private Integer status;
    private String img;
    private String progress;

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Long getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(Long workFlowId) {
        this.workFlowId = workFlowId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getWorkFlowTemplateId() {
        return workFlowTemplateId;
    }

    public void setWorkFlowTemplateId(Integer workFlowTemplateId) {
        this.workFlowTemplateId = workFlowTemplateId;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
