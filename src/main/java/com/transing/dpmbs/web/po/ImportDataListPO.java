package com.transing.dpmbs.web.po;

import com.transing.dpmbs.integration.bo.ImportData;
import com.transing.dpmbs.integration.bo.ImportDataDetail;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "导入列表对象")
public class ImportDataListPO {
    @ApiModelProperty(value = "导入列表集合", required = true)
    private List<ImportDataDetail> importDataDetailList;

    public List<ImportDataDetail> getImportDataDetailList() {
        return importDataDetailList;
    }

    public void setImportDataDetailList(List<ImportDataDetail> importDataDetailList) {
        this.importDataDetailList = importDataDetailList;
    }
}
