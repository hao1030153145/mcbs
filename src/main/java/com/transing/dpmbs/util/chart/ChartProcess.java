package com.transing.dpmbs.util.chart;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/18.
 */
public abstract class ChartProcess {

    public static String KEY_DATAARRAY = "dataArray";
    public static String KEY_TABLEARRAY = "tableArray";

    protected List<Map<String,Object>> mapList = null;
    protected JSONArray fieldArray = null;
    protected JSONObject defaultJsonObject = null;

    public ChartProcess(){

    }

    public ChartProcess(List<Map<String,Object>> mapList, JSONArray fieldArray,JSONObject defaultJsonObject){
        this.fieldArray = fieldArray;
        this.mapList = mapList;
        this.defaultJsonObject = defaultJsonObject;
    }

    public void init(List<Map<String,Object>> mapList, JSONArray fieldArray,JSONObject defaultJsonObject){
        this.fieldArray = fieldArray;
        this.mapList = mapList;
        this.defaultJsonObject = defaultJsonObject;
    }

    public abstract  Map<String, Object> conversionChart();

}