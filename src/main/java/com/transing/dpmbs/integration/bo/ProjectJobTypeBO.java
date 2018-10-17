package com.transing.dpmbs.integration.bo;

import com.jeeframework.logicframework.integration.bo.AbstractBO;

import java.util.Date;

public class ProjectJobTypeBO extends AbstractBO {

    private Integer id;

    private Long projectId;

    private String preTypeNo;

    private String nextTypeNo;

    private String typeNo;

    private Integer sortNo;

    private Date createTime;

    private Date lastmodifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPreTypeNo() {
        return preTypeNo;
    }

    public void setPreTypeNo(String preTypeNo) {
        this.preTypeNo = preTypeNo;
    }

    public String getNextTypeNo() {
        return nextTypeNo;
    }

    public void setNextTypeNo(String nextTypeNo) {
        this.nextTypeNo = nextTypeNo;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastmodifyTime() {
        return lastmodifyTime;
    }

    public void setLastmodifyTime(Date lastmodifyTime) {
        this.lastmodifyTime = lastmodifyTime;
    }
}