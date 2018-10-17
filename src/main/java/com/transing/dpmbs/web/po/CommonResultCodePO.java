package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Map;


@ApiModel(value = "公共返回 是否成功 对象")
public class CommonResultCodePO {

    @ApiModelProperty(value = "code :0为成功 ",required = true)
    private int code;
    @ApiModelProperty(value = "提示信息 ")
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}