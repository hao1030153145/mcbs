package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/4/28.
 */
@ApiModel("数据抓取 的jsonParam 返回 展示 po")
public class DataCrawlJsonParamPO extends WorkFlowParamBasePO {

    @ApiModelProperty("抓取方式")
    private String crawlWay;

    @ApiModelProperty("抓取方式名称")
    private String crawlWayName;

    @ApiModelProperty("抓取类型")
    private String crawlType;

    @ApiModelProperty("流程id")
    private int workFlowTemplateId;

    @ApiModelProperty("数据源id")
    private String datasourceId;

    @ApiModelProperty("数据源 类型id")
    private String datasourceTypeId;

    @ApiModelProperty("数据源 名称")
    private String datasourceName;

    @ApiModelProperty("任务 名称")
    private String taskName;

    @ApiModelProperty("数据源 类型名称")
    private String datasourceTypeName;

    @ApiModelProperty("关键字 名称")
    private String inputParams;

    @ApiModelProperty("状态名字")
    private String statusName;

    @ApiModelProperty("状态")
    private int status;

    @ApiModelProperty("抓取类型 (1为单次抓取，2为定时抓取)")
    private String crawlFreqType;

    @ApiModelProperty("抓取频次表达式")
    private String quartzTime;

    @ApiModelProperty("存储表名")
    private String storageTypeTable;

    @ApiModelProperty("输入参数list")
    private JSONArray inputParamArray;

    @ApiModelProperty("定时任务持续时间（秒）")
    private Long sustainTime;

    public Long getSustainTime() {
        return sustainTime;
    }

    public void setSustainTime(Long sustainTime) {
        this.sustainTime = sustainTime;
    }

    public String getCrawlWay() {
        return crawlWay;
    }

    public String getCrawlWayName() {
        return crawlWayName;
    }

    public void setCrawlWayName(String crawlWayName) {
        this.crawlWayName = crawlWayName;
    }

    public void setCrawlWay(String crawlWay) {
        this.crawlWay = crawlWay;
    }

    public String getCrawlType() {
        return crawlType;
    }

    public void setCrawlType(String crawlType) {
        this.crawlType = crawlType;
    }

    public int getWorkFlowTemplateId() {
        return workFlowTemplateId;
    }

    public void setWorkFlowTemplateId(int workFlowTemplateId) {
        this.workFlowTemplateId = workFlowTemplateId;
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

    public String getCrawlFreqType() {
        return crawlFreqType;
    }

    public void setCrawlFreqType(String crawlFreqType) {
        this.crawlFreqType = crawlFreqType;
    }

    public String getQuartzTime() {
        return quartzTime;
    }

    public void setQuartzTime(String quartzTime) {
        this.quartzTime = quartzTime;
    }

    public String getStorageTypeTable() {
        return storageTypeTable;
    }

    public void setStorageTypeTable(String storageTypeTable) {
        this.storageTypeTable = storageTypeTable;
    }

    public JSONArray getInputParamArray() {
        return inputParamArray;
    }

    public void setInputParamArray(JSONArray inputParamArray) {
        this.inputParamArray = inputParamArray;
    }
}
