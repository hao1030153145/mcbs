package com.transing.task.rdb.impl;

import com.jeeframework.jeetask.task.Task;

/**
 * Created by Administrator on 2017/9/7.
 */
public class DpmTask extends Task {
    private Long projectId;
    private Long flowId;
    private Long flowDetailId;
    private String typeNo;
    private String errorMessage;
    private String jobStatus="0";
    private String jobProgress="0";

    private String jsonParam;
    private int paramType;
    private String resultJsonParam;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Long getFlowDetailId() {
        return flowDetailId;
    }

    public void setFlowDetailId(Long flowDetailId) {
        this.flowDetailId = flowDetailId;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobProgress() {
        return jobProgress;
    }

    public void setJobProgress(String jobProgress) {
        this.jobProgress = jobProgress;
    }

    public String getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
    }

    public int getParamType() {
        return paramType;
    }

    public void setParamType(int paramType) {
        this.paramType = paramType;
    }

    public String getResultJsonParam() {
        return resultJsonParam;
    }

    public void setResultJsonParam(String resultJsonParam) {
        this.resultJsonParam = resultJsonParam;
    }
}
