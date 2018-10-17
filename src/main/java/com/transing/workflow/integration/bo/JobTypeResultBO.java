package com.transing.workflow.integration.bo;

import com.jeeframework.logicframework.integration.bo.AbstractBO;

/**
 * Created by Administrator on 2017/5/8.
 */
public class JobTypeResultBO extends AbstractBO {
    private int reusltTypeId;
    private long dataSourceType;
    private String resultTypeName;

    public int getReusltTypeId() {
        return reusltTypeId;
    }

    public void setReusltTypeId(int reusltTypeId) {
        this.reusltTypeId = reusltTypeId;
    }

    public long getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(long dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getResultTypeName() {
        return resultTypeName;
    }

    public void setResultTypeName(String resultTypeName) {
        this.resultTypeName = resultTypeName;
    }

}
