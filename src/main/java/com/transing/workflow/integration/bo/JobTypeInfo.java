package com.transing.workflow.integration.bo;

import java.util.Date;

public class JobTypeInfo {

    public static final int JOB_TYPE_CRAWL = 1;
    public static final int JOB_TYPE_ANALYSIS = 2;

    public static final int JOB_CLASSIFY_PROCESS = 1;
    public static final int JOB_CLASSIFY_STATUS = 2;

    public static final int TYPE_CLASSIFY_NORMAL = 1;
    public static final int TYPE_CLASSIFY_LOOP = 2;

    private String typeNo;

    private String typeName;

    private String typeDesc;

    private String paramConfigUrl;

    private String executeUrl;

    private String progressUrl;

    private String resultUrl;

    private Integer typeClassify;

    private Integer jobType;

    private Integer typeStatus;

    private Integer orderNumber;

    private String createdBy;

    private Date createdTime;

    private String updatedBy;

    private Date updatedTime;

    private Integer jobClassify;

    private Integer inputNum;

    private Integer jobTypeCategoryId;

    private String queryUrl;

    private String imgUrl;

    private String tip;

    public Integer getJobClassify() {
        return jobClassify;
    }

    public void setJobClassify(Integer jobClassify) {
        this.jobClassify = jobClassify;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getParamConfigUrl() {
        return paramConfigUrl;
    }

    public void setParamConfigUrl(String paramConfigUrl) {
        this.paramConfigUrl = paramConfigUrl;
    }

    public String getExecuteUrl() {
        return executeUrl;
    }

    public void setExecuteUrl(String executeUrl) {
        this.executeUrl = executeUrl;
    }

    public String getProgressUrl() {
        return progressUrl;
    }

    public void setProgressUrl(String progressUrl) {
        this.progressUrl = progressUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public Integer getTypeClassify() {
        return typeClassify;
    }

    public void setTypeClassify(Integer typeClassify) {
        this.typeClassify = typeClassify;
    }

    public Integer getTypeStatus() {
        return typeStatus;
    }

    public void setTypeStatus(Integer typeStatus) {
        this.typeStatus = typeStatus;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
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

    public Integer getInputNum() {
        return inputNum;
    }

    public void setInputNum(Integer inputNum) {
        this.inputNum = inputNum;
    }

    public Integer getJobTypeCategoryId() {
        return jobTypeCategoryId;
    }

    public void setJobTypeCategoryId(Integer jobTypeCategoryId) {
        this.jobTypeCategoryId = jobTypeCategoryId;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}