package com.transing.dpmbs.integration.bo;


public class ImportDataDetail {
    private String paramId;
    private String projectid;
    private String name;
    private String typeName;
    private String typeId;
    private String count;
    private String url;
    private String origainRelation;
    private String isStart;
    private String updatedDate;

    /**
     * 更新进度显示
     * by allen
     * v1.1.1
     */
    private String jobStatus;
    private String jobProgress;
    private String errorMsg;

    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrigainRelation() {
        return origainRelation;
    }

    public void setOrigainRelation(String origainRelation) {
        this.origainRelation = origainRelation;
    }

    public String getIsStart() {
        return isStart;
    }

    public void setIsStart(String isStart) {
        this.isStart = isStart;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getJobStatus()
    {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus)
    {
        this.jobStatus = jobStatus;
    }

    public String getJobProgress()
    {
        return jobProgress;
    }

    public void setJobProgress(String jobProgress)
    {
        this.jobProgress = jobProgress;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }
}
