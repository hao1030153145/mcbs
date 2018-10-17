package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/26.
 */
@ApiModel(value = "选择的词库 对象")
public class WordLibraryPO {

    @ApiModelProperty("词库类型  （0为通用，1为项目）")
    private int type;
    @ApiModelProperty("词库名字")
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
