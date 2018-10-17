package com.transing.dpmbs.web.po;

import com.transing.dpmbs.integration.bo.ImportDataDetail;
import com.transing.dpmbs.integration.bo.OutPutDataDetail;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "导出列表对象")
public class OutputDataListPO {
    @ApiModelProperty(value = "导出列表集合", required = true)
    private List<OutPutDataDetail> outPutDataDetailList;

    public List<OutPutDataDetail> getOutPutDataDetailList() {
        return outPutDataDetailList;
    }

    public void setOutPutDataDetailList(List<OutPutDataDetail> outPutDataDetailList) {
        this.outPutDataDetailList = outPutDataDetailList;
    }
}
