package com.transing.workflow.integration.bo;

import java.util.Date;

public class WorkFlowInfo {

    public static final int WORK_FLOW_STATUS_INIT = 0;
    public static final int WORK_FLOW_STATUS_RUN = 3;
    public static final int WORK_FLOW_STATUS_COMPLATE = 2;

    private Long flowId;

    private String typeNo;

    private Long projectId;

    private Integer totalJobNum;

    private Integer complateJobNum;

    private Integer status;

    private String createdBy;

    private Date createdTime;

    private String updatedBy;

    private Date updatedTime;

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getTotalJobNum() {
        return totalJobNum;
    }

    public void setTotalJobNum(Integer totalJobNum) {
        this.totalJobNum = totalJobNum;
    }

    public Integer getComplateJobNum() {
        return complateJobNum;
    }

    public void setComplateJobNum(Integer complateJobNum) {
        this.complateJobNum = complateJobNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}