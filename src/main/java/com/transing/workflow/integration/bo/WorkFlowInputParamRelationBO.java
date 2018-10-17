package com.transing.workflow.integration.bo;

import java.util.Date;

/**
 * Created by byron on 2018/2/2 0002.
 */
public class WorkFlowInputParamRelationBO {
    private Integer id;
    private String typeNo;
    private Integer inputParamId;
    private String relationType;
    private Date createdTime;
    private Date lastmodifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public Integer getInputParamId() {
        return inputParamId;
    }

    public void setInputParamId(Integer inputParamId) {
        this.inputParamId = inputParamId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getLastmodifyTime() {
        return lastmodifyTime;
    }

    public void setLastmodifyTime(Date lastmodifyTime) {
        this.lastmodifyTime = lastmodifyTime;
    }
}
