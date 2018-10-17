package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 获取统计分析列表集合
 */
@ApiModel(value = "获取统计分析列表数据模型")
public class StatisticsAnalysisPo {
    private String errorMsg;
    private int jobStatus;
    private int jobProgress;
    @ApiModelProperty(value = "任务id", required = true)
    private long paramId;
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "数据结果", required = true)
    private String dataType;
    @ApiModelProperty(value = "维度", required = true)
    private String dimension;
    @ApiModelProperty(value = "条件", required = true)
    private String condition;
    private StatisticsAnalysisJsonParam jsonParam;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(int jobStatus) {
        this.jobStatus = jobStatus;
    }

    public int getJobProgress() {
        return jobProgress;
    }

    public void setJobProgress(int jobProgress) {
        this.jobProgress = jobProgress;
    }

    public long getParamId() {
        return paramId;
    }

    public void setParamId(long paramId) {
        this.paramId = paramId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public StatisticsAnalysisJsonParam getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(StatisticsAnalysisJsonParam jsonParam) {
        this.jsonParam = jsonParam;
    }
}
