package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/5/10.
 */
@ApiModel(value = "数据源类型下拉选项")
public class DataSourceContentTypePO {
    @ApiModelProperty("是否默认 1为默认 0为不是默认")
    private int isDefault;

    @ApiModelProperty("是否默认 1为默认 0为不是默认")
    private String fieldId;

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
}
