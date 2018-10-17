package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 新建任务集合
 */
@ApiModel(value = "新建任务据模型")
public class CreateProjectPo {
    @ApiModelProperty(value = "跳转url",required = true)
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
