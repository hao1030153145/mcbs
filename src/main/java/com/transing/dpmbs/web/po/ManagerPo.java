package com.transing.dpmbs.web.po;

import com.transing.dpmbs.integration.bo.Manager;
import com.transing.dpmbs.integration.bo.Project;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 项目经理集合
 */
@ApiModel(value = "用户数据模型")
public class ManagerPo {
    @ApiModelProperty(value = "项目列表集合", required = true)
    private List<Manager> list;

    public List<Manager> getList() {
        return list;
    }

    public void setList(List<Manager> list) {
        this.list = list;
    }
}
