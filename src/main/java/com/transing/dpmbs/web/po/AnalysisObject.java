package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/25.
 */
@ApiModel(value = "语义分析 分析对象")
public class AnalysisObject {

    @ApiModelProperty("分割 对象（字段） （标题为1，全文为2）")
    private String contentType;

    @ApiModelProperty("按照怎么样分割 （1为全文，2为字，3为段落，4为句）")
    private int subType;

    @ApiModelProperty("分割 具体的值 （0为全文，具体值就是多少字句）")
    private int value;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSubType(int subType){
        this.subType = subType;
    }
    public int getSubType(){
        return this.subType;
    }
    public void setValue(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }

}
