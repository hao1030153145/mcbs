package com.transing.dpmbs.web.po;

import com.transing.workflow.integration.bo.OutDataSourceBo;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "输出类型数据模型")
public class OutDataSourcePO {
    @ApiModelProperty(value = "", required = true)
    List<OutDataSourceBo> outDataSourceList;

    public List<OutDataSourceBo> getOutDataSourceList() {
        return outDataSourceList;
    }

    public void setOutDataSourceList(List<OutDataSourceBo> outDataSourceList) {
        this.outDataSourceList = outDataSourceList;
    }
}
