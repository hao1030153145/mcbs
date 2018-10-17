package com.transing.dpmbs.util.chart.impl;

import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.util.chart.ChartProcess;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by Administrator on 2017/8/18.
 */
public class PieChartProcess extends ChartProcess {

    @Override
    public Map<String, Object> conversionChart() {
        List<String> keyList = new ArrayList<>();
        String serviceField = "";
        String dataAxisField = "";
        if(null != fieldArray && fieldArray.size() > 0){
            for (int i = 0; i < fieldArray.size(); i++) {
                JSONObject jsonObject = fieldArray.getJSONObject(i);

                serviceField = jsonObject.getString("series");
                dataAxisField = jsonObject.getString("dataAxis");

                keyList.add(serviceField);
                keyList.add(dataAxisField);

            }
        }
        Map<String, Object> returnMap = new HashMap<>();

        List<List<String>> tableArray = new ArrayList<>();
        tableArray.add(keyList);
        Map<String,Integer> serviceMap = new HashMap<>();
        if(!Validate.isEmpty(mapList)){

            Set<String> dataAxisSet = new HashSet<>();
            for (Map<String, Object> map:mapList) {

                String serviceValue = map.get(serviceField).toString();
                Integer dataAxisValue = Integer.parseInt(map.get(dataAxisField).toString());

                Integer serviceInt = serviceMap.get(serviceValue);
                if(null == serviceInt){
                    dataAxisSet.add(serviceValue);//添加数据轴
                    serviceMap.put(serviceValue,dataAxisValue);
                }else{
                    serviceMap.put(serviceValue,serviceInt+dataAxisValue);
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

            Object legendObj = defaultJsonObject.get("legend");
            if(null != legendObj && !"".equals(legendObj)){
                JSONObject legendJsonObject = defaultJsonObject.getJSONObject("legend");
                JSONArray legendDataJsonArray = new JSONArray();
                legendDataJsonArray.addAll(dataAxisSet);
                legendJsonObject.put("data",legendDataJsonArray);
                defaultJsonObject.put("legend",legendJsonObject);
            }

            /*JSONObject toolboxJsonObject = new JSONObject();
            toolboxJsonObject.put("show",true);
            JSONObject featureJsonObject = new JSONObject();
            JSONObject markJsonObject = new JSONObject();
            markJsonObject.put("show",true);

            JSONObject magicTypeJsonObject = new JSONObject();
            magicTypeJsonObject.put("show",false);
            JSONArray typeJsonObjecy = new JSONArray();
            typeJsonObjecy.add("line");
            typeJsonObjecy.add("bar");
            magicTypeJsonObject.put("type",typeJsonObjecy);

            JSONObject saveAsImageJsonObject = new JSONObject();
            saveAsImageJsonObject.put("title","保存");
            saveAsImageJsonObject.put("show",true);

            featureJsonObject.put("mark",markJsonObject);
            featureJsonObject.put("magicType",magicTypeJsonObject);
            toolboxJsonObject.put("feature",featureJsonObject);*/

//            jsonObject.put("toolbox",toolboxJsonObject);

            JSONArray seriesJsonArray = defaultJsonObject.getJSONArray("series");
            JSONObject seriesJsonObject = seriesJsonArray.getJSONObject(0);

            JSONArray seriesDataJsonArray = new JSONArray();

            for (Map.Entry<String, Integer> entry:serviceMap.entrySet()) {
                String key = entry.getKey();
                int entryValue = entry.getValue();

                seriesJsonObject.put("name",key);
                seriesJsonObject.put("value",entryValue);

                seriesDataJsonArray.add(seriesJsonObject);

            }

            seriesJsonObject.put("data",seriesDataJsonArray);

            seriesJsonArray.set(0,seriesJsonObject);

            defaultJsonObject.put("series",seriesJsonArray);

            returnMap.put(PieChartProcess.KEY_DATAARRAY,defaultJsonObject);

            returnMap.put(PieChartProcess.KEY_TABLEARRAY,tableArray);

        }

        return returnMap;
    }
}