package com.transing.dpmbs.web.po;

import com.transing.dpmbs.integration.bo.Manager;
import com.transing.dpmbs.integration.bo.Status;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 类型集合
 */
@ApiModel(value = "用户数据模型")
public class StatusPo {
    @ApiModelProperty(value = "项目列表集合", required = true)
    private List<Status> list;

    public List<Status> getList() {
        return list;
    }

    public void setList(List<Status> list) {
        this.list = list;
    }
}
