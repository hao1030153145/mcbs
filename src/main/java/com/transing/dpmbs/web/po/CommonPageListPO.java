package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/5/2.
 */
@ApiModel("返回公共list对象")
public class CommonPageListPO {
    @ApiModelProperty("list 数据")
    private List<?> dataList;

    @ApiModelProperty(value = "count总数", required = true)
    private long count;

    public List<?> getDataList() {
        return dataList;
    }

    public void setDataList(List<?> dataList) {
        this.dataList = dataList;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
