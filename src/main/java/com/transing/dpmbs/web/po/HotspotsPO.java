package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */
@ApiModel("话题分析 返回对象")
public class HotspotsPO extends WorkFlowParamBasePO {

    public static String ANALYSISOBJECT_TYPE_ALL = "all";
    public static String ANALYSISOBJECT_TYPE_FILTER = "filter";

    @ApiModelProperty("结果名")
    private String resultName;
    @ApiModelProperty("话题层级")
    private int hotspotsLevel;
    @ApiModelProperty("打分方式 （1为标准，2为近期加权）")
    private int scoringWay;
    @ApiModelProperty("打分方式 名称")
    private String scoringWayName;
    @ApiModelProperty("启动频次 （1为不循环，2为每日，3为每周，4为每月）")
    private int startFreqType;
    @ApiModelProperty("启动频次 名称")
    private String startFreqTypeName;
    @ApiModelProperty("定义 名称")
    private String definitionName;
    @ApiModelProperty("每次处理多少小时以内的语料（24为24小时，36为36小时，72为72小时）")
    private int handleHour;
    @ApiModelProperty("启动频次 具体的值")
    private String startFreqValue;
    @ApiModelProperty("选择的主题域以及主题 list")
    private List<SubjectIdPO> subjectAresList;
    @ApiModelProperty("主题域 各个主题之间的关系（1为或，2为与）辅助主题域之间的关系是或")
    private int relationship;
    @ApiModelProperty("结果策略类型（1为不限）")
    private int resultsStrategyType;
    @ApiModelProperty("话题list")
    private List<TopicPO> topicList;
    @ApiModelProperty("话题对象(all为全部语料，filter为主题筛选)")
    private String analysisObject;

    public String getAnalysisObject() {
        return analysisObject;
    }

    public void setAnalysisObject(String analysisObject) {
        this.analysisObject = analysisObject;
    }

    public List<TopicPO> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<TopicPO> topicList) {
        this.topicList = topicList;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public int getHotspotsLevel() {
        return hotspotsLevel;
    }

    public void setHotspotsLevel(int hotspotsLevel) {
        this.hotspotsLevel = hotspotsLevel;
    }

    public int getScoringWay() {
        return scoringWay;
    }

    public void setScoringWay(int scoringWay) {
        this.scoringWay = scoringWay;
    }

    public String getScoringWayName() {
        return scoringWayName;
    }

    public void setScoringWayName(String scoringWayName) {
        this.scoringWayName = scoringWayName;
    }

    public int getStartFreqType() {
        return startFreqType;
    }

    public void setStartFreqType(int startFreqType) {
        this.startFreqType = startFreqType;
    }

    public String getStartFreqTypeName() {
        return startFreqTypeName;
    }

    public void setStartFreqTypeName(String startFreqTypeName) {
        this.startFreqTypeName = startFreqTypeName;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public int getHandleHour() {
        return handleHour;
    }

    public void setHandleHour(int handleHour) {
        this.handleHour = handleHour;
    }

    public String getStartFreqValue() {
        return startFreqValue;
    }

    public void setStartFreqValue(String startFreqValue) {
        this.startFreqValue = startFreqValue;
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }

    public List<SubjectIdPO> getSubjectAresList() {
        return subjectAresList;
    }

    public void setSubjectAresList(List<SubjectIdPO> subjectAresList) {
        this.subjectAresList = subjectAresList;
    }

    public int getResultsStrategyType() {
        return resultsStrategyType;
    }

    public void setResultsStrategyType(int resultsStrategyType) {
        this.resultsStrategyType = resultsStrategyType;
    }
}