package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/28.
 */
@ApiModel("数据抓取 返回 展示 po")
public class DataCrawlPO extends WorkFlowParamBasePO {

    @ApiModelProperty("数据源 名称")
    private String datasourceName;

    @ApiModelProperty("任务 名称")
    private String taskName;

    @ApiModelProperty("数据源 类型名称")
    private String datasourceTypeName;

    @ApiModelProperty("关键字 名称")
    private String inputParams;

    @ApiModelProperty("抓取频次表达式")
    private String crawlFreq;

    @ApiModelProperty("状态名字")
    private String statusName;

    @ApiModelProperty("抓取方式")
    private String crawlWay;

    @ApiModelProperty("抓取方式名字")
    private String crawlWayName;

    @ApiModelProperty("抓取类型")
    private String crawlType;

    @ApiModelProperty("流程id")
    private int workFlowTemplateId;

    @ApiModelProperty("状态")
    private int status;

    @ApiModelProperty("条数")
    private int count;

    @ApiModelProperty("jsonParam")
    private DataCrawlJsonParamPO jsonParam;

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    private String batchNo;

    public int getWorkFlowTemplateId() {
        return workFlowTemplateId;
    }

    public void setWorkFlowTemplateId(int workFlowTemplateId) {
        this.workFlowTemplateId = workFlowTemplateId;
    }

    public String getCrawlWay() {
        return crawlWay;
    }

    public void setCrawlWay(String crawlWay) {
        this.crawlWay = crawlWay;
    }

    public String getCrawlWayName() {
        return crawlWayName;
    }

    public void setCrawlWayName(String crawlWayName) {
        this.crawlWayName = crawlWayName;
    }

    public String getCrawlType() {
        return crawlType;
    }

    public void setCrawlType(String crawlType) {
        this.crawlType = crawlType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDatasourceTypeName() {
        return datasourceTypeName;
    }

    public void setDatasourceTypeName(String datasourceTypeName) {
        this.datasourceTypeName = datasourceTypeName;
    }

    public String getInputParams() {
        return inputParams;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
    }

    public String getCrawlFreq() {
        return crawlFreq;
    }

    public void setCrawlFreq(String crawlFreq) {
        this.crawlFreq = crawlFreq;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataCrawlJsonParamPO getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(DataCrawlJsonParamPO jsonParam) {
        this.jsonParam = jsonParam;
    }
}
