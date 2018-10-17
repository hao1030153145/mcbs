package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/6/26.
 */
@ApiModel(value = "数据源类型和语义配置 前端修改 的 PO")
public class DatasourceContentJsonParamPO {
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

    @ApiModelProperty("选择的字段")
    private List<DataSourceContentTypePO> contentTypes;

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

    public List<DataSourceContentTypePO> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<DataSourceContentTypePO> contentTypes) {
        this.contentTypes = contentTypes;
    }
}
