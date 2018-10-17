package com.transing.dpmbs.web.po;

import java.util.List;
import java.util.Set;

public class StatisticsDataType {
    private String storageTypeTableId;

    private String storageTypeTableName;

    private Set<StatisticsDatasourceType> datasourceType ;

    public String getStorageTypeTableId() {
        return storageTypeTableId;
    }

    public void setStorageTypeTableId(String storageTypeTableId) {
        this.storageTypeTableId = storageTypeTableId;
    }

    public String getStorageTypeTableName() {
        return storageTypeTableName;
    }

    public void setStorageTypeTableName(String storageTypeTableName) {
        this.storageTypeTableName = storageTypeTableName;
    }

    public Set<StatisticsDatasourceType> getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Set<StatisticsDatasourceType> datasourceType) {
        this.datasourceType = datasourceType;
    }
}
