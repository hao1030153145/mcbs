package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/6/26.
 */
@ApiModel(value = "抓取输入参数")
public class CrawlInputParamPO {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("数据源id")
    private String datasourceId;
    @ApiModelProperty("数据源类型id")
    private String datasourceTypeId;
    @ApiModelProperty("参数中文名")
    private String paramCnName;
    @ApiModelProperty("参数英文名")
    private String paramEnName;
    @ApiModelProperty("提示文本")
    private String prompt;
    @ApiModelProperty("样式名称")
    private String styleName;
    @ApiModelProperty("样式编码")
    private String styleCode;
    @ApiModelProperty("约束条件")
    private String restrictions;
    @ApiModelProperty("是否必填")
    private String isRequired;
    @ApiModelProperty("输入参数")
    private Object paramValue;
    @ApiModelProperty("属性值")
    private JSONObject controlProp;

    public JSONObject getControlProp() {
        return controlProp;
    }

    public void setControlProp(JSONObject controlProp) {
        this.controlProp = controlProp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceTypeId() {
        return datasourceTypeId;
    }

    public void setDatasourceTypeId(String datasourceTypeId) {
        this.datasourceTypeId = datasourceTypeId;
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

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public void setStyleCode(String styleCode) {
        this.styleCode = styleCode;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }
}
