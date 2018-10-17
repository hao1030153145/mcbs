package com.transing.dpmbs.integration.bo;

public class ProjectOne {
    private long projectId;
    private String projectName;
    private String projectDescribe;
    private String managerId;
    private String customerId;
    private String startTime;
    private String endTime;
    private String typeId;
    private String managerName;
    private String customerName;
    private String typeName;
    private String status;
    private String projectType;

    public static final String PROJECTTYPE_VIS = "vis";
    public static final String PROJECTTYPE_PAGE = "page";

    public ProjectOne(long id, String projectName, String projectDescribe, String managerId, String customerId, String startTime, String endTime, String typeId,String status,String projectType) {
        this.projectId = id;
        this.projectName = projectName;
        this.projectDescribe = projectDescribe;
        this.managerId = managerId;
        this.customerId = customerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.typeId = typeId;
        this.status = status;
        this.projectType = projectType;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
