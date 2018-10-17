/**
 * 1.0   lanceyan  2008-5-20  Create
 */

package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 注册用户页面对象
 */
@ApiModel(value = "项目list返回的PO")
public class SubjectListPO {
    @ApiModelProperty(value = "id", required = true)
    private long id;

    @ApiModelProperty(value = "pid", required = true)
    private long pid;

    @ApiModelProperty(value = "名字", required = true)
    private String name;

    @ApiModelProperty(value = "主题PO", required = true)
    private SubjectPO projectPO;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SubjectPO getProjectPO() {
        return projectPO;
    }

    public void setProjectPO(SubjectPO projectPO) {
        this.projectPO = projectPO;
    }
}


