package com.transing.dpmbs.web.po;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */
public class StatisticsAnalysisDataTypePO {
    private List<String> datasourceType;
    private List<String> detailId;
    private String storageTypeTableId;

    public List<String> getDetailId() {
        return detailId;
    }

    public void setDetailId(List<String> detailId) {
        this.detailId = detailId;
    }

    public List<String> getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(List<String> datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getStorageTypeTableId() {
        return storageTypeTableId;
    }

    public void setStorageTypeTableId(String storageTypeTableId) {
        this.storageTypeTableId = storageTypeTableId;
    }
}
