package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONArray;

import java.util.Date;

@ApiModel(value = "抓取 选择 流程 配置 对象")
public class CrawlWorkFlowTemplatePO {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("数据源类型id")
    private Long datasourceTypeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDatasourceTypeId() {
        return datasourceTypeId;
    }

    public void setDatasourceTypeId(Long datasourceTypeId) {
        this.datasourceTypeId = datasourceTypeId;
    }
}