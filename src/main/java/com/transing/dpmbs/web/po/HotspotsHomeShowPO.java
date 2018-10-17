package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */
@ApiModel("话题分析 返回对象")
public class HotspotsHomeShowPO extends HotspotsPO {
    @ApiModelProperty("进度")
    private Integer jobProgress;
    @ApiModelProperty("状态 0,待启动，1启动中，2已完成,4停止，9异常")
    private Integer jobStatus;
    @ApiModelProperty("错误描述信息")
    private String errorMsg;

    public Integer getJobProgress() {
        return jobProgress;
    }

    public void setJobProgress(Integer jobProgress) {
        this.jobProgress = jobProgress;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}