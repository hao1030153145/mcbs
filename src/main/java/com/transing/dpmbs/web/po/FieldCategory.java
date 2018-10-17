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
public class FieldCategory {

    private String fieldCategory;
    private String fieldCategoryName;
    private List<Field> fieldList;
    public void setFieldCategory(String fieldCategory) {
         this.fieldCategory = fieldCategory;
     }
     public String getFieldCategory() {
         return fieldCategory;
     }

    public void setFieldCategoryName(String fieldCategoryName) {
         this.fieldCategoryName = fieldCategoryName;
     }
     public String getFieldCategoryName() {
         return fieldCategoryName;
     }

    public void setFieldList(List<Field> fieldList) {
         this.fieldList = fieldList;
     }
     public List<Field> getFieldList() {
         return fieldList;
     }

}