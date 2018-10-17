package com.transing.dpmbs.integration.bo;

import com.transing.dpmbs.web.po.WorkFlowParamBasePO;

public class ImportData {
    private String projectid;
    private String name;
    private String typeName;
    private String typeId;
    private String count;
    private String url;
    private String origainRelation;
    private String isStart;
    private String storageTypeTable;

    public ImportData() {
    }

    public ImportData(String projectid, String name, String typeName, String typeId, String count, String url, String origainRelation, String isStart,String storageTypeTable) {
        this.projectid = projectid;
        this.name = name;
        this.typeName = typeName;
        this.typeId = typeId;
        this.count = count;
        this.url = url;
        this.origainRelation = origainRelation;
        this.isStart = isStart;
        this.storageTypeTable = storageTypeTable;
    }

    public String getStorageTypeTable() {
        return storageTypeTable;
    }

    public void setStorageTypeTable(String storageTypeTable) {
        this.storageTypeTable = storageTypeTable;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrigainRelation() {
        return origainRelation;
    }

    public void setOrigainRelation(String origainRelation) {
        this.origainRelation = origainRelation;
    }

    public String getIsStart() {
        return isStart;
    }

    public void setIsStart(String isStart) {
        this.isStart = isStart;
    }
}
