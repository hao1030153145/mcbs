package com.transing.dpmbs.web.po;

import com.transing.workflow.integration.bo.OutDataSourceBo;
import com.transing.workflow.integration.bo.OutDataSourceDemoParamter;
import com.transing.workflow.integration.bo.OutDataSourceDetailBo;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "输出类型详细数据模型")
public class OutDataSourceDetailPO {
    @ApiModelProperty(value = "", required = true)
    List<OutDataSourceDetailBo> outDataSourceDetailBoList;
    @ApiModelProperty(value = "", required = true)
    List<OutDataSourceDemoParamter> outDataSourceDemoParamterList;
    @ApiModelProperty(value = "", required = true)
    String url;

    public List<OutDataSourceDetailBo> getOutDataSourceDetailBoList() {
        return outDataSourceDetailBoList;
    }

    public void setOutDataSourceDetailBoList(List<OutDataSourceDetailBo> outDataSourceDetailBoList) {
        this.outDataSourceDetailBoList = outDataSourceDetailBoList;
    }

    public List<OutDataSourceDemoParamter> getOutDataSourceDemoParamterList() {
        return outDataSourceDemoParamterList;
    }

    public void setOutDataSourceDemoParamterList(List<OutDataSourceDemoParamter> outDataSourceDemoParamterList) {
        this.outDataSourceDemoParamterList = outDataSourceDemoParamterList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
