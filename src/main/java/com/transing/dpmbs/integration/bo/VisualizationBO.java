package com.transing.dpmbs.integration.bo;

import com.jeeframework.logicframework.integration.bo.AbstractBO;

import java.util.Date;

public class VisualizationBO extends AbstractBO {
    private Integer id;

    private String name;

    private Integer projectId;

    private String createTime;

    private String lastmodifyTime;

    private String image;

    private String backSetting;

    public String getBackSetting() {
        return backSetting;
    }

    public void setBackSetting(String backSetting) {
        this.backSetting = backSetting;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastmodifyTime() {
        return lastmodifyTime;
    }

    public void setLastmodifyTime(String lastmodifyTime) {
        this.lastmodifyTime = lastmodifyTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}