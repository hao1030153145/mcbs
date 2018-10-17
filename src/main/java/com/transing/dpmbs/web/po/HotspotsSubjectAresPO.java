package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/4/26.
 */
@ApiModel(value = "话题分析 返回主题域 对象")
public class HotspotsSubjectAresPO {

    @ApiModelProperty("主题域 list")
    private List<SubjectAresPO> ares;
    @ApiModelProperty("主题 list")
    private List<SubjectAresPO> subject;

    public List<SubjectAresPO> getAres() {
        return ares;
    }

    public void setAres(List<SubjectAresPO> ares) {
        this.ares = ares;
    }

    public List<SubjectAresPO> getSubject() {
        return subject;
    }

    public void setSubject(List<SubjectAresPO> subject) {
        this.subject = subject;
    }
}
