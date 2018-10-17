package com.transing.dpmbs.web.filter;

public class DatasourceTypeFilter {
    private Integer datasourceId;
    private Integer datasourceTypeId;
    private Integer storageTypeId;
    private String datasourceTypeName;
    private Integer status;
    private String sortStatus;
    private String direct;
    private long page;
    private long size;

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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

    public String getSortStatus() {
        return sortStatus;
    }

    public void setSortStatus(String sortStatus) {
        this.sortStatus = sortStatus;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }
}
