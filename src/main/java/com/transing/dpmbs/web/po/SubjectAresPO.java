package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/4/26.
 */
@ApiModel(value = "主题域 对象")
public class SubjectAresPO {

    @ApiModelProperty("主题域 id")
    private long id;
    @ApiModelProperty("主题域 名字")
    private String name;
    @ApiModelProperty("主题域 上级id")
    private long pid;
    @ApiModelProperty("是否是叶子节点")
    private int isLastChildren;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getIsLastChildren() {
        return isLastChildren;
    }

    public void setIsLastChildren(int isLastChildren) {
        this.isLastChildren = isLastChildren;
    }
}
