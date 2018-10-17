package com.transing.dpmbs.util.chart.impl;

import com.alibaba.fastjson.JSONPObject;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.util.chart.ChartProcess;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by Administrator on 2017/8/18.
 */
public class LineChartProcess extends ChartProcess {

    @Override
    public Map<String, Object> conversionChart() {
        List<String> keyList = new ArrayList<>();
        String serviceField = "";
        String xAxisField = "";
        String yAxisField = "";
        if(null != fieldArray && fieldArray.size() > 0){
            for (int i = 0; i < fieldArray.size(); i++) {
                JSONObject jsonObject = fieldArray.getJSONObject(i);

                serviceField = jsonObject.getString("series");
                xAxisField = jsonObject.getString("xAxis");
                yAxisField = jsonObject.getString("yAxis");

                keyList.add(serviceField);
                keyList.add(xAxisField);
                keyList.add(yAxisField);

            }
        }
        Map<String, Object> returnMap = new HashMap<>();

        List<List<String>> tableArray = new ArrayList<>();
        tableArray.add(keyList);
        Map<String,List<Map<String, Object>>> serviceMap = new HashMap<>();
        if(!Validate.isEmpty(mapList)){
            List<String> xAxisList = new ArrayList<>();
            List<String> yAxisList = new ArrayList<>();
            List<String> serviceSet = new ArrayList<>();
            for (Map<String, Object> map:mapList) {

                if(null == map.get(xAxisField) || "".equals(map.get(xAxisField))){
                    continue;
                }

                if(null == map.get(yAxisField) || "".equals(map.get(yAxisField))){
                    continue;
                }

                if(null == map.get(serviceField) || "".equals(map.get(serviceField))){
                    continue;
                }

                String serviceValue = map.get(serviceField).toString();
                List<Map<String, Object>> serviceList = serviceMap.get(serviceValue);
                if(null == serviceList){
                    if(!serviceSet.contains(serviceValue)){
                        serviceSet.add(serviceValue);
                    }
                    serviceList = new ArrayList<>();
                    serviceMap.put(serviceValue,serviceList);
                }

                serviceList.add(map);

                //取出y轴数据作为list
                String yAxisValue = map.get(yAxisField).toString();
                if(!yAxisList.contains(yAxisValue)){
                    yAxisList.add(yAxisValue);
                }

                //添加x轴的数据
                String xAxisValue = map.get(xAxisField).toString();
                if(!xAxisList.contains(xAxisValue)){
                    xAxisList.add(xAxisValue);
                }

                List<String> stringList = new ArrayList<>();
                for (String key:keyList) {
                    String value = map.get(key).toString();
                    stringList.add(value);

                }
                tableArray.add(stringList);
            }
            /*JSONObject titleJsonObject = new JSONObject();
            titleJsonObject.put("text","统计分析");
            titleJsonObject.put("subtext","数据");
            jsonObject.put("title",titleJsonObject);

            JSONObject tooltipJsonObject = new JSONObject();
            tooltipJsonObject.put("trigger","axis");
            jsonObject.put("tooltip",tooltipJsonObject);*/

            Object legendJsonObj = defaultJsonObject.get("legend");
            if(null != legendJsonObj){

                JSONObject legendJsonObject = (JSONObject) legendJsonObj;

                JSONArray legendDataJsonArray = new JSONArray();
                legendDataJsonArray.addAll(serviceSet);
                legendJsonObject.put("data",legendDataJsonArray);
                defaultJsonObject.put("legend",legendJsonObject);
            }

            defaultJsonObject.put("calculable",true);

            JSONArray xAxisJsonArray = new JSONArray();
            JSONObject xAxisJsonObject = new JSONObject();
            xAxisJsonObject.put("type","category");
            JSONArray dataJsonArray = new JSONArray();
            dataJsonArray.addAll(xAxisList);
            xAxisJsonObject.put("data",dataJsonArray);
            xAxisJsonObject.put("boundaryGap",false);

            xAxisJsonArray.add(xAxisJsonObject);
            defaultJsonObject.put("xAxis",xAxisJsonArray);

            JSONArray yAxisJsonArray = new JSONArray();
            JSONObject yAxisJsonObject = new JSONObject();
            yAxisJsonObject.put("type","value");
            /*yAxisJsonObject.put("name","");
            JSONArray ydataJsonArray = new JSONArray();
            ydataJsonArray.addAll(yAxisList);
            yAxisJsonObject.put("data",ydataJsonArray);
            */
            yAxisJsonArray.add(yAxisJsonObject);
            defaultJsonObject.put("yAxis",yAxisJsonArray);

            JSONArray seriesJsonArray = new JSONArray();
            JSONObject seriesJsonObject = new JSONObject();

            for (Map.Entry<String, List<Map<String, Object>>> entry:serviceMap.entrySet()) {
                String key = entry.getKey();
                List<Map<String, Object>> valueList = entry.getValue();
                JSONArray seriesDataJsonArray = new JSONArray();

                if(!Validate.isEmpty(valueList) && !Validate.isEmpty(xAxisList)){

                    for (String xAxis:xAxisList) {

                        boolean isFind = false;
                        for (Map<String, Object> map:valueList) {

                            String xAxisValue = map.get(xAxisField).toString();
                            if(xAxis.equals(xAxisValue)){
                                String data = map.get(yAxisField).toString();
                                seriesDataJsonArray.add(data);
                                isFind =true;
                                break;
                            }
                        }
                        if(!isFind){
                            seriesDataJsonArray.add("");
                        }

                    }


                }

                seriesJsonObject.put("name",key);
                seriesJsonObject.put("type","line");
                seriesJsonObject.put("data",seriesDataJsonArray);

                seriesJsonArray.add(seriesJsonObject);

            }
            defaultJsonObject.put("series",seriesJsonArray);

            returnMap.put(LineChartProcess.KEY_DATAARRAY,defaultJsonObject);

            returnMap.put(LineChartProcess.KEY_TABLEARRAY,tableArray);

        }

        return returnMap;
    }
}