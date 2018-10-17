package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2017/5/5.
 */
@ApiModel("话题分析 设置 主题域以及主题 返回 对象")
public class SubjectIdPO {
    @ApiModelProperty("主题域id")
    private String aresId;
    @ApiModelProperty("主题 id 多个英文逗号分割")
    private String subjectIds;

    public String getAresId() {
        return aresId;
    }

    public void setAresId(String aresId) {
        this.aresId = aresId;
    }

    public String getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(String subjectIds) {
        this.subjectIds = subjectIds;
    }
}
