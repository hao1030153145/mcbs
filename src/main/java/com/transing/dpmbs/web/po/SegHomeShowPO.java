package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/28.
 */
@ApiModel("分词 项目主页 返回 展示 po")
public class SegHomeShowPO extends WorkFlowParamBasePO {

    @ApiModelProperty("所选词库")
    private String wordLibrarys;
    @ApiModelProperty("操作类型 ")
    private String actionTypeName;
    @ApiModelProperty("进度")
    private Integer jobProgress;
    @ApiModelProperty("状态 0,待启动，1启动中，2已完成,4停止，9异常")
    private Integer jobStatus;
    @ApiModelProperty("错误描述信息")
    private String errorMsg;

    public String getActionTypeName() {
        return actionTypeName;
    }

    public void setActionTypeName(String actionTypeName) {
        this.actionTypeName = actionTypeName;
    }

    public String getWordLibrarys() {
        return wordLibrarys;
    }

    public void setWordLibrarys(String wordLibrarys) {
        this.wordLibrarys = wordLibrarys;
    }

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
