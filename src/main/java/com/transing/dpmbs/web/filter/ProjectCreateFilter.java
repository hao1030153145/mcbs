package com.transing.dpmbs.web.filter;

public class ProjectCreateFilter {
    private String id;
    private String projectName;
    private String projectDescribe;
    private String typeId;
    private String managerId;
    private String customerId;
    private String startTime;
    private String endTime;
    private String projectType;
    private String status;

    public ProjectCreateFilter(String projectName, String projectDescribe, String typeId, String managerId, String customerId, String startTime, String endTime,String projectType) {
        this.projectName = projectName;
        this.projectDescribe = projectDescribe;
        this.typeId = typeId;
        this.managerId = managerId;
        this.customerId = customerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.projectType = projectType;
    }

    public ProjectCreateFilter(String id, String projectName, String projectDescribe, String typeId, String managerId, String customerId, String startTime, String endTime,String projectType) {
        this.id = id;
        this.projectName = projectName;
        this.projectDescribe = projectDescribe;
        this.typeId = typeId;
        this.managerId = managerId;
        this.customerId = customerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.projectType = projectType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescribe() {
        return projectDescribe;
    }

    public void setProjectDescribe(String projectDescribe) {
        this.projectDescribe = projectDescribe;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
