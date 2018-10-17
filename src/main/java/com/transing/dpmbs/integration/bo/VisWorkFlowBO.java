package com.transing.dpmbs.integration.bo;

/**
 * 工作流输出字段bo
 * Created by Administrator on 2018/1/5 0005.
 */
public class VisWorkFlowBO {

    private int id;
    private int flowDetailId;
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

    public void setFlowDetailId(int flowDetailId) {
        this.flowDetailId = flowDetailId;
    }

    public int getFlowDetailId() {
        return flowDetailId;
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
