package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/28.
 */
@ApiModel("语义分析对象 返回 展示 po")
public class SeanticAnalysisShow extends WorkFlowParamBasePO {
    @ApiModelProperty("数据源 类型名称")
    private String dataSourceTypeName;

    @ApiModelProperty("分析对象名称")
    private String analysisObjectName;

    @ApiModelProperty("句级")
    private String sentence;

    @ApiModelProperty("段级")
    private String section;

    @ApiModelProperty("文级")
    private String article;

    public String getDataSourceTypeName() {
        return dataSourceTypeName;
    }

    public void setDataSourceTypeName(String dataSourceTypeName) {
        this.dataSourceTypeName = dataSourceTypeName;
    }

    public String getAnalysisObjectName() {
        return analysisObjectName;
    }

    public void setAnalysisObjectName(String analysisObjectName) {
        this.analysisObjectName = analysisObjectName;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }
}
