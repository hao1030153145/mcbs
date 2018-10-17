/**
  * Copyright 2017 bejson.com 
  */
package com.transing.dpmbs.web.po;
import java.util.List;

/**
 * Auto-generated: 2017-08-22 14:55:9
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class FieldFilterPO {

    private List<FieldCategory> fieldCategoryList;
    private List<Condition> conditionList;
    private List<FieldType> fieldTypeList;
    private List<Field> fieldList;
    private List<StatisticsType> statisticsType;
    private List<DimensionActionPO> dimensionActionList;

    public void setFieldCategoryList(List<FieldCategory> fieldCategoryList) {
         this.fieldCategoryList = fieldCategoryList;
     }
     public List<FieldCategory> getFieldCategoryList() {
         return fieldCategoryList;
     }

    public void setConditionList(List<Condition> conditionList) {
         this.conditionList = conditionList;
     }
     public List<Condition> getConditionList() {
         return conditionList;
     }

    public void setFieldTypeList(List<FieldType> fieldTypeList) {
         this.fieldTypeList = fieldTypeList;
     }
     public List<FieldType> getFieldTypeList() {
         return fieldTypeList;
     }

    public void setFieldList(List<Field> fieldList) {
         this.fieldList = fieldList;
     }
     public List<Field> getFieldList() {
         return fieldList;
     }

    public void setStatisticsType(List<StatisticsType> statisticsType) {
         this.statisticsType = statisticsType;
     }
    public List<StatisticsType> getStatisticsType() {
         return statisticsType;
     }

    public List<DimensionActionPO> getDimensionActionList() {
        return dimensionActionList;
    }

    public void setDimensionActionList(List<DimensionActionPO> dimensionActionList) {
        this.dimensionActionList = dimensionActionList;
    }
}