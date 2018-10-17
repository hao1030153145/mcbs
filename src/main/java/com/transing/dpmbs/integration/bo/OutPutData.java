package com.transing.dpmbs.integration.bo;

public class OutPutData {

    public final static String API_TYPE_JSON = "json";
    public final static String API_TYPE_XML = "xml";

    private String name;
    private String typeName;
    private String type;
    private String apiType;
    private String mapRelationValue;
    private String mapRelationValueName;
    private String url;
    private String storageTypeTable;

    public OutPutData(){

    }

    public OutPutData(String name, String typeName, String type, String apiType, String mapRelationValue, String mapRelationValueName,String storageTypeTable) {
        this.name = name;
        this.typeName = typeName;
        this.type = type;
        this.apiType = apiType;
        this.mapRelationValue = mapRelationValue;
        this.mapRelationValueName = mapRelationValueName;
        this.storageTypeTable = storageTypeTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getMapRelationValue() {
        return mapRelationValue;
    }

    public void setMapRelationValue(String mapRelationValue) {
        this.mapRelationValue = mapRelationValue;
    }

    public String getMapRelationValueName() {
        return mapRelationValueName;
    }

    public void setMapRelationValueName(String mapRelationValueName) {
        this.mapRelationValueName = mapRelationValueName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStorageTypeTable() {
        return storageTypeTable;
    }

    public void setStorageTypeTable(String storageTypeTable) {
        this.storageTypeTable = storageTypeTable;
    }
}
