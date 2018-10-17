package com.transing.dpmbs.integration.bo;


/**
 * Created by Administrator on 2017/5/10.
 */
public class ContentTypeBO {
    private Long id;
    private Integer datasourceTypeId;
    private String contentType;
    private String contentTypeName;
    private Integer isDefault;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getDatasourceTypeId() {
        return datasourceTypeId;
    }

    public void setDatasourceTypeId(Integer datasourceTypeId) {
        this.datasourceTypeId = datasourceTypeId;
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
