package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 包: com.transing.dpmbs.web.po
 * 源文件:DataSourcePO.java
 *
 * @author Allen  Copyright 2016 成都创行, Inc. All rights reserved.2017年05月02日
 */
@ApiModel(value = "数据源类型的模型")
public class DatasourceTypePO {
    @ApiModelProperty
    private Long typeId;
    @ApiModelProperty
    private String typeName;
    @ApiModelProperty
    private String storageTypeTable;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStorageTypeTable() {
        return storageTypeTable;
    }

    public void setStorageTypeTable(String storageTypeTable) {
        this.storageTypeTable = storageTypeTable;
    }
}



