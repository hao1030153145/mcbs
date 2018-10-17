package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/6/26.
 */
@ApiModel(value = "数据源类型的模型")
public class   DatasourcePO {
    @ApiModelProperty("数据源id")
    private long datasourceId;
    @ApiModelProperty("数据源名称")
    private String datasourceName;
    @ApiModelProperty("数据源类型 list对象")
    private List<DatasourceTypePO> datasourceTypes;

    public long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public List<DatasourceTypePO> getDatasourceTypes() {
        return datasourceTypes;
    }

    public void setDatasourceTypes(List<DatasourceTypePO> datasourceTypes) {
        this.datasourceTypes = datasourceTypes;
    }
}
