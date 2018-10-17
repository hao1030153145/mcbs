package com.transing.workflow.integration.bo;

import java.util.Date;

/**
 * Created by byron on 2018/1/8 0008.
 */
public class WorkFlowNodeParamBo {
    private Long paramId;
    private Long flowDetailId;
    private Long projectId;
    private Long workFlowId;
    private String typeNo;
    private String paramType;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private Integer inputParamId;
    private String inputParamCnName;
    private String inputParamType;
    private String inputParamValue;
    private String config;

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public Long getFlowDetailId() {
        return flowDetailId;
    }

    public void setFlowDetailId(Long flowDetailId) {
        this.flowDetailId = flowDetailId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(Long workFlowId) {
        this.workFlowId = workFlowId;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getInputParamId() {
        return inputParamId;
    }

    public void setInputParamId(Integer inputParamId) {
        this.inputParamId = inputParamId;
    }

    public String getInputParamCnName() {
        return inputParamCnName;
    }

    public void setInputParamCnName(String inputParamCnName) {
        this.inputParamCnName = inputParamCnName;
    }

    public String getInputParamType() {
        return inputParamType;
    }

    public void setInputParamType(String inputParamType) {
        this.inputParamType = inputParamType;
    }

    public String getInputParamValue() {
        return inputParamValue;
    }

    public void setInputParamValue(String inputParamValue) {
        this.inputParamValue = inputParamValue;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
