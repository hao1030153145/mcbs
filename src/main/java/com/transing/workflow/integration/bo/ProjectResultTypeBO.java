package com.transing.workflow.integration.bo;

import com.jeeframework.logicframework.integration.bo.AbstractBO;

/**
 * Created by hello on 2017/5/7.
 */
public class ProjectResultTypeBO extends AbstractBO {
    private long id;
    private long projectId;
    private long flowId;
    private long flowDetailId;
    private long resultTypeId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getFlowId() {
        return flowId;
    }

    public void setFlowId(long flowId) {
        this.flowId = flowId;
    }

    public long getFlowDetailId() {
        return flowDetailId;
    }

    public void setFlowDetailId(long flowDetailId) {
        this.flowDetailId = flowDetailId;
    }

    public long getResultTypeId() {
        return resultTypeId;
    }

    public void setResultTypeId(long resultTypeId) {
        this.resultTypeId = resultTypeId;
    }
}
