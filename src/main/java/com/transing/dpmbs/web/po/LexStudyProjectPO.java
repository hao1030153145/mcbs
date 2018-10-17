package com.transing.dpmbs.web.po;

import com.jeeframework.logicframework.integration.bo.AbstractBO;

import java.util.Date;

public class LexStudyProjectPO {
    private Integer id;

    private Integer pid;

    private String projectName;

    private Integer typeSpId;

    private String termName;

    private String dateCondition;

    private Integer dataTotal;

    private Integer returnTotal;

    private Integer userId;

    private String userName;

    private Date finishTime;

    private Date startTime;

    private Date createTime;

    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getTypeSpId() {
        return typeSpId;
    }

    public void setTypeSpId(Integer typeSpId) {
        this.typeSpId = typeSpId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getDateCondition() {
        return dateCondition;
    }

    public void setDateCondition(String dateCondition) {
        this.dateCondition = dateCondition;
    }

    public Integer getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(Integer dataTotal) {
        this.dataTotal = dataTotal;
    }

    public Integer getReturnTotal() {
        return returnTotal;
    }

    public void setReturnTotal(Integer returnTotal) {
        this.returnTotal = returnTotal;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}