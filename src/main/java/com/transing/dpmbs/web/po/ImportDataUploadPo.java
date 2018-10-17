package com.transing.dpmbs.web.po;

import com.transing.dpmbs.integration.bo.DataSourceTypeRelation;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 导入数据上传文件集合
 */
@ApiModel(value = "导入数据上传文件数据模型")
public class ImportDataUploadPo {
    @ApiModelProperty(value = "源字段列表集合", required = true)
    List<String> origainFieldList;
    @ApiModelProperty(value = "现字段列表集合", required = true)
    List<String> fieldNameList;
    @ApiModelProperty(value = "描述列表集合", required = true)
    List<String> fieldAnnotationList;
    @ApiModelProperty(value = "上传文件临时", required = true)
    String url;

    public List<String> getOrigainFieldList() {
        return origainFieldList;
    }

    public void setOrigainFieldList(List<String> origainFieldList) {
        this.origainFieldList = origainFieldList;
    }

    public List<String> getFieldNameList() {
        return fieldNameList;
    }

    public void setFieldNameList(List<String> fieldNameList) {
        this.fieldNameList = fieldNameList;
    }

    public List<String> getFieldAnnotationList() {
        return fieldAnnotationList;
    }

    public void setFieldAnnotationList(List<String> fieldAnnotationList) {
        this.fieldAnnotationList = fieldAnnotationList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
