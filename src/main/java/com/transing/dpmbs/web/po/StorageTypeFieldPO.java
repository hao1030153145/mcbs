package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/6/26.
 */
@ApiModel(value = "存储 字段 对象")
public class StorageTypeFieldPO {
    @ApiModelProperty(value = "主键id", required = true)
    private Integer id;
    @ApiModelProperty(value = "字段中文名", required = true)
    private String fieldCnName;
    @ApiModelProperty(value = "字段英文名", required = true)
    private String fieldEnName;
    @ApiModelProperty(value = "字段描述", required = true)
    private String fieldDesc;
    @ApiModelProperty(value = "字段类型", required = true)
    private String fieldType;
    @ApiModelProperty(value = "字段长度", required = true)
    private Integer fieldLength;
    @ApiModelProperty(value = "小数点长度", required = true)
    private Integer decimalLength;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldCnName() {
        return fieldCnName;
    }

    public void setFieldCnName(String fieldCnName) {
        this.fieldCnName = fieldCnName;
    }

    public String getFieldEnName() {
        return fieldEnName;
    }

    public void setFieldEnName(String fieldEnName) {
        this.fieldEnName = fieldEnName;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    public Integer getDecimalLength() {
        return decimalLength;
    }

    public void setDecimalLength(Integer decimalLength) {
        this.decimalLength = decimalLength;
    }
}
