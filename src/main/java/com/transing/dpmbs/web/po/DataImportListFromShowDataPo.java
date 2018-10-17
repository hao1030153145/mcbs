package com.transing.dpmbs.web.po;

import java.util.List;

/**
 * 查看导入结果集合
 */
public class DataImportListFromShowDataPo {
    private List<List> dataList ;

    private List<String> titleList ;

    private String count;

    public List<List> getDataList() {
        return dataList;
    }

    public void setDataList(List<List> dataList) {
        this.dataList = dataList;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
