package com.transing.dpmbs.web.filter;

/**
 * Created by byron on 2017/11/21 0021.
 */

import java.util.Date;

/**
 * 定义导出任务查询条件类
 */
public class ProjectExportFilter {
    private String fileName;//文件名
    private String exportDataType;//导出数据类型
    private String createTimeStart;//创建时间(开始)
    private String createTimeEnd;//创建时间(结束)
    private String projectType;//项目类型
    private int projectId;//项目id
    private Long page;
    private String status;
    private Long size;


    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExportDataType() {
        return exportDataType;
    }

    public void setExportDataType(String exportDataType) {
        this.exportDataType = exportDataType;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
