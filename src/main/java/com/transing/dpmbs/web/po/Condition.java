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
public class Condition {

    private String typeOf;
    private String condition;
    private String conditionName;
    private List<ConditionExp> conditionExpList;

    public String getTypeOf() {
        return typeOf;
    }

    public void setTypeOf(String typeOf) {
        this.typeOf = typeOf;
    }

    public void setCondition(String condition) {
         this.condition = condition;
     }
     public String getCondition() {
         return condition;
     }

    public void setConditionName(String conditionName) {
         this.conditionName = conditionName;
     }
     public String getConditionName() {
         return conditionName;
     }

    public void setConditionExpList(List<ConditionExp> conditionExpList) {
         this.conditionExpList = conditionExpList;
     }
     public List<ConditionExp> getConditionExpList() {
         return conditionExpList;
     }

}