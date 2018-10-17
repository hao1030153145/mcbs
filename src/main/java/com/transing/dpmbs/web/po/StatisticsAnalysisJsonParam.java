package com.transing.dpmbs.web.po;

import net.sf.json.JSONObject;

import java.util.List;

public class StatisticsAnalysisJsonParam {

    private Long paramId;

    private String name;

    private List<StatisticsAnalysisDataTypePO> dataType ;

    private String analysisHierarchy;

    private List<StatisticsFieldAndFilter> fieldAndFilter ;

    private List<StatisticsObject> statisticsObject ;

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StatisticsAnalysisDataTypePO> getDataType() {
        return dataType;
    }

    public void setDataType(List<StatisticsAnalysisDataTypePO> dataType) {
        this.dataType = dataType;
    }

    public String getAnalysisHierarchy() {
        return analysisHierarchy;
    }

    public void setAnalysisHierarchy(String analysisHierarchy) {
        this.analysisHierarchy = analysisHierarchy;
    }

    public List<StatisticsFieldAndFilter> getFieldAndFilter() {
        return fieldAndFilter;
    }

    public void setFieldAndFilter(List<StatisticsFieldAndFilter> fieldAndFilter) {
        this.fieldAndFilter = fieldAndFilter;
    }

    public List<StatisticsObject> getStatisticsObject() {
        return statisticsObject;
    }

    public void setStatisticsObject(List<StatisticsObject> statisticsObject) {
        this.statisticsObject = statisticsObject;
    }
}
