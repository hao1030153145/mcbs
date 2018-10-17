package com.transing.dpmbs.integration.bo;

import java.util.Date;

public class VisJsonParamBO {
    private Integer id;

    private String type;

    private String position;

    private Date createTime;

    private Date lastmodifyTime;

    private String paramSettings;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getParamSettings() {
        return paramSettings;
    }

    public void setParamSettings(String paramSettings) {
        this.paramSettings = paramSettings;
    }
}