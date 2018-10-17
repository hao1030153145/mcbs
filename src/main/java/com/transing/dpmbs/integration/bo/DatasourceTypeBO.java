package com.transing.dpmbs.integration.bo;

public class DatasourceTypeBO {
    private Integer datasourceId;
    private Integer datasourceTypeId;
    private Integer storageTypeId;
    private String storageTypeTable;
    private String datasourceTypeName;
    private Integer status;
    private String updatedTime;
    private String updatedBy;

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getStorageTypeTable() {
        return storageTypeTable;
    }

    public void setStorageTypeTable(String storageTypeTable) {
        this.storageTypeTable = storageTypeTable;
    }

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public Integer getDatasourceTypeId() {
        return datasourceTypeId;
    }

    public void setDatasourceTypeId(Integer datasourceTypeId) {
        this.datasourceTypeId = datasourceTypeId;
    }

    public Integer getStorageTypeId() {
        return storageTypeId;
    }

    public void setStorageTypeId(Integer storageTypeId) {
        this.storageTypeId = storageTypeId;
    }

    public String getDatasourceTypeName() {
        return datasourceTypeName;
    }

    public void setDatasourceTypeName(String datasourceTypeName) {
        this.datasourceTypeName = datasourceTypeName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
