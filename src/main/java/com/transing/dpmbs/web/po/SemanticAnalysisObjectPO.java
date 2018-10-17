package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/25.
 */
@ApiModel("语义分析对象 返回 PO")
public class SemanticAnalysisObjectPO extends SeanticAnalysisShow{

    @ApiModelProperty("编辑时 需要的参数")
    private SemanticJsonParam jsonParam;

    @ApiModelProperty("状态")
    private int jobStatus;
    @ApiModelProperty("进度")
    private int jobProgress;

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

    public SemanticJsonParam getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(SemanticJsonParam jsonParam) {
        this.jsonParam = jsonParam;
    }
}
