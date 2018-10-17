package com.transing.dpmbs.integration.bo;

import com.jeeframework.logicframework.integration.bo.AbstractBO;

/**
 * 用户对象
 *
 * @author summer
 * @version 1.0
 * @see AbstractBO
 */
public class DataSourceType extends UserBase {

    private Long id;
    private String name;
    private String outTable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutTable() {
        return outTable;
    }

    public void setOutTable(String outTable) {
        this.outTable = outTable;
    }
}