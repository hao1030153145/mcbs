package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/4/28.
 */
@ApiModel("分词，主题分析，话题分析，项目主页 返回 展示 po")
public class SegSubjectHotpostHomtShowPO {
    @ApiModelProperty("分词返回 list以及 status")
    private List<SegHomeShowPO> segHomeShowPOList;
    @ApiModelProperty("主题分析 返回List 以及 status")
    private List<SubjectHomeShowPO> subjectHomeShowPOList;
    @ApiModelProperty("话题分析返回List 以及 status")
    private List<HotspotsHomeShowPO> hotspotsHomeShowPOList;

    public List<SegHomeShowPO> getSegHomeShowPOList() {
        return segHomeShowPOList;
    }

    public void setSegHomeShowPOList(List<SegHomeShowPO> segHomeShowPOList) {
        this.segHomeShowPOList = segHomeShowPOList;
    }

    public List<SubjectHomeShowPO> getSubjectHomeShowPOList() {
        return subjectHomeShowPOList;
    }

    public void setSubjectHomeShowPOList(List<SubjectHomeShowPO> subjectHomeShowPOList) {
        this.subjectHomeShowPOList = subjectHomeShowPOList;
    }

    public List<HotspotsHomeShowPO> getHotspotsHomeShowPOList() {
        return hotspotsHomeShowPOList;
    }

    public void setHotspotsHomeShowPOList(List<HotspotsHomeShowPO> hotspotsHomeShowPOList) {
        this.hotspotsHomeShowPOList = hotspotsHomeShowPOList;
    }
}
