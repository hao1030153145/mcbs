package com.transing.dpmbs.web.po;

import com.transing.dpmbs.integration.bo.Customer;
import com.transing.dpmbs.integration.bo.Manager;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 用户集合
 */
@ApiModel(value = "用户集数据模型")
public class CustomerPo {
    @ApiModelProperty(value = "项目列表集合", required = true)
    private List<Customer> list;

    public List<Customer> getList() {
        return list;
    }

    public void setList(List<Customer> list) {
        this.list = list;
    }
}
