package com.transing.dpmbs.integration.bo;

public class VisChartBOWithBLOBs extends VisChartBO {
    private String defaultDataArray;

    private String defaultTableArray;

    private String inputFieldArray;

    public String getDefaultDataArray() {
        return defaultDataArray;
    }

    public void setDefaultDataArray(String defaultDataArray) {
        this.defaultDataArray = defaultDataArray;
    }

    public String getDefaultTableArray() {
        return defaultTableArray;
    }

    public void setDefaultTableArray(String defaultTableArray) {
        this.defaultTableArray = defaultTableArray;
    }

    public String getInputFieldArray() {
        return inputFieldArray;
    }

    public void setInputFieldArray(String inputFieldArray) {
        this.inputFieldArray = inputFieldArray;
    }
}