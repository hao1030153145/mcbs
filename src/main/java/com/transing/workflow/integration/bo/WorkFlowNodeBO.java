package com.transing.workflow.integration.bo;

import java.util.Date;

public class WorkFlowNodeBO {

    private Integer templateId;

    private Long flowId;

    private String preFlowIdIds;

    private String nextFlowIdIds;

    private String typeNo;

    private String name;

    private String createdBy;

    private Date createdTime;

    private String updatedBy;

    private Date updatedTime;

    private String nodeParam;

    private String nodeInfo;

    private Boolean isSave;

    private  Integer jobStatus;

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Boolean getSave() {
        return isSave;
    }

    public void setSave(Boolean save) {
        isSave = save;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getPreFlowIdIds() {
        return preFlowIdIds;
    }

    public void setPreFlowIdIds(String preFlowIdIds) {
        this.preFlowIdIds = preFlowIdIds;
    }

    public String getNextFlowIdIds() {
        return nextFlowIdIds;
    }

    public void setNextFlowIdIds(String nextFlowIdIds) {
        this.nextFlowIdIds = nextFlowIdIds;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getNodeParam() {
        return nodeParam;
    }

    public void setNodeParam(String nodeParam) {
        this.nodeParam = nodeParam;
    }

    public String getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(String nodeInfo) {
        this.nodeInfo = nodeInfo;
    }
}