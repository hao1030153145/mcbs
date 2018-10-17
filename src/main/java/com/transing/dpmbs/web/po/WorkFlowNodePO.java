package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONObject;

@ApiModel(value = "抓取流程配置 节点 对象")
public class WorkFlowNodePO {

    @ApiModelProperty(value = "id", required = true)
    private Long flowId;

    @ApiModelProperty(value = "类型", required = true)
    private String typeNo;

    @ApiModelProperty(value = "节点名称", required = true)
    private String name;

    @ApiModelProperty(value = "节点参数", required = true)
    private JSONObject nodeParam;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject getNodeParam() {
        return nodeParam;
    }

    public void setNodeParam(JSONObject nodeParam) {
        this.nodeParam = nodeParam;
    }
}