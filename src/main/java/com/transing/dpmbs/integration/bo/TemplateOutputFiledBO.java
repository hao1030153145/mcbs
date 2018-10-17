package com.transing.dpmbs.integration.bo;

/**
 * 模板输出字段bo
 * Created by byron on 2018/3/28 0028.
 */
public class TemplateOutputFiledBO {
    private int id;
    private int flowId;
    private int filedId;
    private String filedEnName;
    private int isCustomed;
    private String filedType;
    private String filedCnName;
    private String storageTypeTable;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;

    }

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public void setFiledId(int filedId) {
        this.filedId = filedId;
    }

    public int getFiledId() {
        return filedId;
    }

    public void setFiledEnName(String filedEnName) {
        this.filedEnName = filedEnName;
    }

    public String getFiledEnName() {
        return filedEnName;
    }

    public void setIsCustomed(int isCustomed) {
        this.isCustomed = isCustomed;
    }

    public int getIsCustomed() {
        return isCustomed;
    }

    public void setFiledType(String filedType) {
        this.filedType = filedType;
    }

    public String getFiledType() {
        return filedType;
    }

    public void setFiledCnName(String filedCnName) {
        this.filedCnName = filedCnName;
    }

    public String getFiledCnName() {
        return filedCnName;
    }

    public void setStorageTypeTable(String storageTypeTable) {
        this.storageTypeTable = storageTypeTable;
    }

    public String getStorageTypeTable() {
        return storageTypeTable;
    }
}
