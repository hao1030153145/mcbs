package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/6/26.
 */
@ApiModel(value = "数据源类型的模型")
public class DatasourceContentPO {
    @ApiModelProperty("数据源id")
    private int datasourceId;

    @ApiModelProperty("id")
    private int id;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("数据源类型 id")
    private int datasourceTypeId;

    @ApiModelProperty("数据源类型 名称")
    private String datasourceTypeName;

    @ApiModelProperty("配置对象")
    private String confObject;

    @ApiModelProperty("配置内容")
    private String confField;

    @ApiModelProperty("状态")
    private String statusName;

    private Integer status;

    @ApiModelProperty("状态")
    private String updatedTime;

    @ApiModelProperty("操作者")
    private String updatedBy;

    @ApiModelProperty("前端修改的PO")
    private DatasourceContentJsonParamPO jsonParam;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public DatasourceContentJsonParamPO getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(DatasourceContentJsonParamPO jsonParam) {
        this.jsonParam = jsonParam;
    }

    public int getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId) {
        this.datasourceId = datasourceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public int getDatasourceTypeId() {
        return datasourceTypeId;
    }

    public void setDatasourceTypeId(int datasourceTypeId) {
        this.datasourceTypeId = datasourceTypeId;
    }

    public String getDatasourceTypeName() {
        return datasourceTypeName;
    }

    public void setDatasourceTypeName(String datasourceTypeName) {
        this.datasourceTypeName = datasourceTypeName;
    }

    public String getConfObject() {
        return confObject;
    }

    public void setConfObject(String confObject) {
        this.confObject = confObject;
    }

    public String getConfField() {
        return confField;
    }

    public void setConfField(String confField) {
        this.confField = confField;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
