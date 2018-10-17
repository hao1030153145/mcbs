package com.transing.dpmbs.integration.bo;

import java.util.Date;

public class VisChartBO {
    private Integer id;

    private Integer visId;

    private Integer categoryId;

    private String path;

    private String processClass;

    private Integer visJsonParamId;

    private String position;

    private String img;

    private String type;

    private String typeName;

    private String name;

    private Date createTime;

    private Date lastmodifyTime;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVisId() {
        return visId;
    }

    public void setVisId(Integer visId) {
        this.visId = visId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProcessClass() {
        return processClass;
    }

    public void setProcessClass(String processClass) {
        this.processClass = processClass;
    }

    public Integer getVisJsonParamId() {
        return visJsonParamId;
    }

    public void setVisJsonParamId(Integer visJsonParamId) {
        this.visJsonParamId = visJsonParamId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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