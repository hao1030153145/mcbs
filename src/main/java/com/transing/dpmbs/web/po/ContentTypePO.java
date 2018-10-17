package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/5/10.
 */
@ApiModel(value = "数据源类型下拉选项")
public class ContentTypePO {
    @ApiModelProperty("主键id")
    private long id;
    @ApiModelProperty("数据源类型idid")
    private int dataSourceTypeId;
    @ApiModelProperty(" 选项 值（数据源类型字段）")
    private String contentType;
    @ApiModelProperty("选项名称 （数据源类型 字段 诠释）")
    private String contentTypeName;
    @ApiModelProperty("是否默认 1为默认 0为不是默认")
    private int isDefault;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(int dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentTypeName() {
        return contentTypeName;
    }

    public void setContentTypeName(String contentTypeName) {
        this.contentTypeName = contentTypeName;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
