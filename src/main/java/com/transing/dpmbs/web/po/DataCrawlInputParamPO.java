package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/28.
 */
@ApiModel("数据抓取 的jsonParam 里的inputParam 返回 展示 po")
public class DataCrawlInputParamPO {

    @ApiModelProperty("参数id")
    private String id;

    @ApiModelProperty("参数中文关键字")
    private String paramCnName;

    @ApiModelProperty("参数 英文 关键字")
    private String paramEnName;

    @ApiModelProperty("样式编码")
    private String styleCode;

    @ApiModelProperty("关键字 输入的值")
    private Object paramValue;

    @ApiModelProperty("是否必填")
    private String isRequired;

    @ApiModelProperty("提示信息")
    private String prompt;

    @ApiModelProperty("约束条件")
    private String restrictions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParamCnName() {
        return paramCnName;
    }

    public void setParamCnName(String paramCnName) {
        this.paramCnName = paramCnName;
    }

    public String getParamEnName() {
        return paramEnName;
    }

    public void setParamEnName(String paramEnName) {
        this.paramEnName = paramEnName;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public void setStyleCode(String styleCode) {
        this.styleCode = styleCode;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }

    public String getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }
}
