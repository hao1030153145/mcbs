package com.transing.dpmbs.web.filter;

/**
 * Created by byron on 2018/3/14 0014.
 * 工作流过滤条件对象
 */
public class WorkFlowListFilter {

    private Long projectId;
    private String workFlowName;
    private String sort;
    private Integer status;
    private Integer page;
    private Integer size;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long pojectId) {
        this.projectId = pojectId;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
