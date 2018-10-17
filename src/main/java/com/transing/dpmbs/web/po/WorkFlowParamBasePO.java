package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by hello on 2017/4/25.
 */
@ApiModel("工作流 基础 对象")
public class WorkFlowParamBasePO {

    @ApiModelProperty("参数id")
    private Long paramId;
    @ApiModelProperty("创建时间")
    private String createdDate;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }
}