package com.transing.workflow.integration.bo;

import java.util.Date;

/**
 * Created by byron on 2018/1/8 0008.
 */
public class WorkFlowInputParamBo {
    private Integer id;
    private String typeNo;
    private String paramCnName;
    private String paramEnName;
    private String prompt;
    private Integer styleId;
    private String restrictions;
    private Integer isRequired;
    private String controlProp;
    private Date createTime;
    private Date lastmodifyTime;
    private String requestUrl;
    private String filedMapping;
    private String paramType;
    private String isShow;
    private String nextParamId;
    private String preParamId;
    private Integer orderNum;

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

    public String getParamCnName() {
        return paramCnName;
    }

    public void setParamCnName(String paramCnName) {
        this.paramCnName = paramCnName;
    }

    public String getParamEnName() {
        return paramEnName;
    }

    public void setParamEnName(String paramEnName) {
        this.paramEnName = paramEnName;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Integer getStyleId() {
        return styleId;
    }

    public void setStyleId(Integer styleId) {
        this.styleId = styleId;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public Integer getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }

    public String getControlProp() {
        return controlProp;
    }

    public void setControlProp(String controlProp) {
        this.controlProp = controlProp;
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

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getFiledMapping() {
        return filedMapping;
    }

    public void setFiledMapping(String filedMapping) {
        this.filedMapping = filedMapping;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getNextParamId() {
        return nextParamId;
    }

    public void setNextParamId(String nextParamId) {
        this.nextParamId = nextParamId;
    }

    public String getPreParamId() {
        return preParamId;
    }

    public void setPreParamId(String preParamId) {
        this.preParamId = preParamId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}

