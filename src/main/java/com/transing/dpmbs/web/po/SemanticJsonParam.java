package com.transing.dpmbs.web.po;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */
@ApiModel("编辑语义分析对象 参数")
public class SemanticJsonParam {
    @ApiModelProperty("数据源类型id")
    private long dataSourceTypeId;

    @ApiModelProperty("数据源类型 名称")
    private String dataSourceTypeName;

    @ApiModelProperty("存储表名")
    private String storageTypeTable;

    @ApiModelProperty("分析对象 这个对象")
    private List<AnalysisObject> analysisObject ;

    @ApiModelProperty("分析层级 这个对象")
    private List<AnalysisHierarchy> analysisHierarchy ;

    public String getStorageTypeTable() {
        return storageTypeTable;
    }

    public void setStorageTypeTable(String storageTypeTable) {
        this.storageTypeTable = storageTypeTable;
    }

    public void setDataSourceTypeId(long dataSourceTypeId){
        this.dataSourceTypeId = dataSourceTypeId;
    }
    public long getDataSourceTypeId(){
        return this.dataSourceTypeId;
    }
    public void setDataSourceTypeName(String dataSourceTypeName){
        this.dataSourceTypeName = dataSourceTypeName;
    }
    public String getDataSourceTypeName(){
        return this.dataSourceTypeName;
    }
    public void setAnalysisObject(List<AnalysisObject> analysisObject){
        this.analysisObject = analysisObject;
    }
    public List<AnalysisObject> getAnalysisObject(){
        return this.analysisObject;
    }
    public void setAnalysisHierarchy(List<AnalysisHierarchy> analysisHierarchy){
        this.analysisHierarchy = analysisHierarchy;
    }
    public List<AnalysisHierarchy> getAnalysisHierarchy(){
        return this.analysisHierarchy;
    }

}
