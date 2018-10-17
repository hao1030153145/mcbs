package com.transing.dpmbs.integration.bo;


public class DataSourceTypeRelation {
    private Long id;
    private String dataSourceTypeId;
    private String fieldName;
    private String fieldAnnotation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(String dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldAnnotation() {
        return fieldAnnotation;
    }

    public void setFieldAnnotation(String fieldAnnotation) {
        this.fieldAnnotation = fieldAnnotation;
    }
}
