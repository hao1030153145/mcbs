package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by hello on 2017/4/26.
 */
@ApiModel("主题分析设置返回 对象")
public class SubjectSetPO extends WorkFlowParamBasePO{

    @ApiModelProperty("主题域 id")
    private long subjectAresId;
    @ApiModelProperty("主题域 名字")
    private String subjectAresName;
    @ApiModelProperty("选择的 主题id（多个 英文逗号隔开）")
    private String subjectIds;
    @ApiModelProperty("选择的 主题 名字 （多个 英文逗号隔开）")
    private String subjectNames;
    @ApiModelProperty("结果策略类型（1为精英团，2为top具体值，3为top百分比，4为阈值）")
    private int resultsStrategyType;
    @ApiModelProperty("结果策略名字：如 Top 2或者 Top 3%")
    private String resultsStrategyTypeName;
    @ApiModelProperty("结果策略值")
    private int resultsStrategyTypeValue;

    public String getResultsStrategyTypeName() {
        return resultsStrategyTypeName;
    }

    public void setResultsStrategyTypeName(String resultsStrategyTypeName) {
        this.resultsStrategyTypeName = resultsStrategyTypeName;
    }

    public int getResultsStrategyTypeValue() {
        return resultsStrategyTypeValue;
    }

    public void setResultsStrategyTypeValue(int resultsStrategyTypeValue) {
        this.resultsStrategyTypeValue = resultsStrategyTypeValue;
    }

    public long getSubjectAresId() {
        return subjectAresId;
    }

    public void setSubjectAresId(long subjectAresId) {
        this.subjectAresId = subjectAresId;
    }

    public String getSubjectAresName() {
        return subjectAresName;
    }

    public void setSubjectAresName(String subjectAresName) {
        this.subjectAresName = subjectAresName;
    }

    public String getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(String subjectIds) {
        this.subjectIds = subjectIds;
    }

    public String getSubjectNames() {
        return subjectNames;
    }

    public void setSubjectNames(String subjectNames) {
        this.subjectNames = subjectNames;
    }

    public int getResultsStrategyType() {
        return resultsStrategyType;
    }

    public void setResultsStrategyType(int resultsStrategyType) {
        this.resultsStrategyType = resultsStrategyType;
    }
}
