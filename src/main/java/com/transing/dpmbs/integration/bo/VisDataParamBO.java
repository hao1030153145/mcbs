package com.transing.dpmbs.integration.bo;

public class VisDataParamBO {
    private Integer id;

    private Integer visModule;

    private Integer dataId;

    private Integer workParamId;

    private Integer limitNum;

    private String sortCol;

    private String paramJson;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVisModule() {
        return visModule;
    }

    public void setVisModule(Integer visModule) {
        this.visModule = visModule;
    }

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public Integer getWorkParamId() {
        return workParamId;
    }

    public void setWorkParamId(Integer workParamId) {
        this.workParamId = workParamId;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public String getSortCol() {
        return sortCol;
    }

    public void setSortCol(String sortCol) {
        this.sortCol = sortCol;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}