package com.transing.workflow.integration.bo;

import java.util.Date;

public class WorkFlowDetail {

    public static int JOB_STATUS_RUNING = 1;

    public static int JOB_STATUS_COMPLETE = 2;

    public static int JOB_STATUS_EXCEPTION = 9;

    private Integer workFlowTemplateId;

    private Long flowDetailId;

    private Long flowId;

    private Long projectId;
    private Long workFlowId;

    private String typeNo;

    private String prevFlowDetailIds;

    private String nextFlowDetailIds;

    private String resultParam;

    private String dataSourceType;

    private Date jobBeginTime;

    private Date jobEndTime;

    private Integer jobProgress;

    private Integer jobStatus;

    private String quartzTime;

    private String errorMsg;

    private String createdBy;

    private Date createdTime;

    private String updatedBy;

    private Date updatedTime;

    private String nodeInfo;

    private Boolean isSave;

    public Boolean getSave() {
        return isSave;
    }

    public void setSave(Boolean save) {
        isSave = save;
    }

    public Integer getWorkFlowTemplateId() {
        return workFlowTemplateId;
    }

    public void setWorkFlowTemplateId(Integer workFlowTemplateId) {
        this.workFlowTemplateId = workFlowTemplateId;
    }

    public String getQuartzTime() {
        return quartzTime;
    }

    public void setQuartzTime(String quartzTime) {
        this.quartzTime = quartzTime;
    }

    public Long getFlowDetailId() {
        return flowDetailId;
    }

    public void setFlowDetailId(Long flowDetailId) {
        this.flowDetailId = flowDetailId;
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

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Date getJobBeginTime() {
        return jobBeginTime;
    }

    public void setJobBeginTime(Date jobBeginTime) {
        this.jobBeginTime = jobBeginTime;
    }

    public Date getJobEndTime() {
        return jobEndTime;
    }

    public void setJobEndTime(Date jobEndTime) {
        this.jobEndTime = jobEndTime;
    }

    public Integer getJobProgress() {
        return jobProgress;
    }

    public void setJobProgress(Integer jobProgress) {
        this.jobProgress = jobProgress;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
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

    public String getPrevFlowDetailIds() {
        return prevFlowDetailIds;
    }

    public void setPrevFlowDetailIds(String prevFlowDetailIds) {
        this.prevFlowDetailIds = prevFlowDetailIds;
    }

    public String getNextFlowDetailIds() {
        return nextFlowDetailIds;
    }

    public void setNextFlowDetailIds(String nextFlowDetailIds) {
        this.nextFlowDetailIds = nextFlowDetailIds;
    }

    public String getResultParam() {
        return resultParam;
    }

    public void setResultParam(String resultParam) {
        this.resultParam = resultParam;
    }

    public String getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(String nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public Long getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(Long workFlowId) {
        this.workFlowId = workFlowId;
    }
}