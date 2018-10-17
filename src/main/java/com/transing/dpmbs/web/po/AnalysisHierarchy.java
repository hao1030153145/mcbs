package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/25.
 */
@ApiModel(value = "语义分析 分析层级 对象")
public class AnalysisHierarchy {

    public static final int ANALYSIS_HIERARCHY_TYPE_SEGMENT = 1;  //分析层级对象 分词
    public static final int ANALYSIS_HIERARCHY_TYPE_SUBJECT = 2;  //分析层级对象 主题
    public static final int ANALYSIS_HIERARCHY_TYPE_TOPIC = 3;  //分析层级对象 话题

    public static final int ANALYSIS_HIERARCHY_SENTENCE = 1;  //分析层级 句子
    public static final int ANALYSIS_HIERARCHY_SECTION = 2;  //分析层级 段落
    public static final int ANALYSIS_HIERARCHY_ARTICLE = 3;  //分析层级 文章


    public static final int ANALYSIS_HIERARCHY_CALCULATION_MAX = 1;  //1为取大值，
    public static final int ANALYSIS_HIERARCHY_CALCULATION_ADD = 2;  //2为简单相加，
    public static final int ANALYSIS_HIERARCHY_CALCULATION_ADD_WEIGHT = 3;  //3为次数加权

    @ApiModelProperty("分析层级的 分析对象 的 类型 （1是分词，2是主题，3是话题）")
    private int type;

    @ApiModelProperty("分析层级的 分析层级 （1为句子，2为段落，3为文章）")
    private String hierarchy;

    @ApiModelProperty("计算方式:1为取大值，2为简单相加，3为次数加权")
    private String calculation;

    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public void setHierarchy(String hierarchy){
        this.hierarchy = hierarchy;
    }
    public String getHierarchy(){
        return this.hierarchy;
    }

    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }
}
