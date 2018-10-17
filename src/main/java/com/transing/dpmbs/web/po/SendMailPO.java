package com.transing.dpmbs.web.po;

public class SendMailPO extends  CommonResultCodePO {
    private Long flowDetailId;
    private Long paramId;

    public Long getFlowDetailId() {
        return flowDetailId;
    }

    public void setFlowDetailId(Long flowDetailId) {
        this.flowDetailId = flowDetailId;
    }

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }
}
