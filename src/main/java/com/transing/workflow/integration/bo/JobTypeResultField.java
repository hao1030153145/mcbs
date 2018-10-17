package com.transing.workflow.integration.bo;

import com.jeeframework.logicframework.integration.bo.AbstractBO;

/**
 * Created by Administrator on 2017/5/31.
 */
public class JobTypeResultField extends AbstractBO {

    private int fieldId;
    private int resultTypeId;
    private String fieldName;
    private String colName;
    private String colDesc;
    private int fieldType;//1为int，2为string，3date

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getResultTypeId() {
        return resultTypeId;
    }

    public void setResultTypeId(int resultTypeId) {
        this.resultTypeId = resultTypeId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColDesc() {
        return colDesc;
    }

    public void setColDesc(String colDesc) {
        this.colDesc = colDesc;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }
}
