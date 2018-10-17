/**
 * 1.0   lanceyan  2008-5-20  Create
 */

package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 注册用户页面对象
 */
@ApiModel(value = "项目list返回的ProjectPO")
public class SubjectPO extends LexStudyProjectPO {
    @ApiModelProperty(value = "id", required = true)
    private List<LexContextPO> contextList;
    @ApiModelProperty(value = "pid", required = true)
    private List<LexSourcePO> sourceList;

    public List<LexContextPO> getContextList() {
        return contextList;
    }

    public void setContextList(List<LexContextPO> contextList) {
        this.contextList = contextList;
    }

    public List<LexSourcePO> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<LexSourcePO> sourceList) {
        this.sourceList = sourceList;
    }
}


