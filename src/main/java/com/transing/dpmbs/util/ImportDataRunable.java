package com.transing.dpmbs.util;

import com.alibaba.fastjson.JSONObject;
import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import net.sf.json.JSONException;
import org.apache.http.HttpException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/18.
 */
public class ImportDataRunable implements Runnable {

    public static final String HTTP_PROTOCOL = "http://"; //http访问地址前缀
    public static final String ADD_DATA_URI = "/addDataInSearcher.json"; //保存

    protected String loggerName = this.getClass().getSimpleName();

    public static final int RESPONSE_CODE_SUCCESS = 0;//返回成功

    public File csvFile;

    public ImportDataRunable(File csvFile) {
        this.csvFile = csvFile;
    }

    @Override
    public void run() {

        List<Map> listPage = new ArrayList<>();
        try {
            CSVFileUtil csvFileUtil = new CSVFileUtil(csvFile.getAbsolutePath(),"gb2312");
            String firstSource = csvFileUtil.readLine();
            List<String> titleList = CSVFileUtil.fromCSVLinetoArray(firstSource);

            String source = csvFileUtil.readLine();
            while (!Validate.isEmpty(source)) {
                Map<String,Object> map = new HashMap<>();

                List<String> list = CSVFileUtil.fromCSVLinetoArray(source);
                for (int i = 0; i < titleList.size(); i++) {
                    String title = titleList.get(i);
                    String value = list.get(i);

                    map.put(title,value);
                }

                listPage.add(map);

                if(listPage.size() >= 100){
                    addData(listPage);
                    listPage.clear();
                }

                source = csvFileUtil.readLine();
            }

            if(listPage.size() > 0){
                addData(listPage);
                listPage.clear();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean addData(List<Map> listPage){
        String jsonArray = JSONObject.toJSONString(listPage);
        // 5.调用/addDataInSearcher.json接口
        Map<String, String> getTermListByTermNamePostData2 = new HashMap<String, String>();
        getTermListByTermNamePostData2.put("dataType", "news");
        getTermListByTermNamePostData2.put("dataJSON", jsonArray);
        callRemoteService(HTTP_PROTOCOL + "118.190.83.99:8080" + ADD_DATA_URI, "post", getTermListByTermNamePostData2);
        LoggerUtil.infoTrace(loggerName,"上传数据到"+"http://118.190.83.99:8080"+"数据量："+listPage.size());

        return true;
    }


    /**
     * 远程服务调用方法
     *
     * @param serviceURL 服务访问URL
     * @param method     访问方法  get /   post
     * @param postData   请求参数
     * @return
     */
    protected Object callRemoteService(String serviceURL, String method, Map<String, String> postData) {
        String getTermListStr = doHttpRequest(serviceURL, method, postData);

        try {
            net.sf.json.JSONObject getTermListJsonObject = net.sf.json.JSONObject.fromObject(getTermListStr);
            int code = getTermListJsonObject.getInt("code");

            if (code == RESPONSE_CODE_SUCCESS) {

                Object termListArrayObject = getTermListJsonObject.get("data");
                return termListArrayObject;
            }else {
                throw new BizException("访问网络出错啦，message = " + getTermListJsonObject.getString("message"));
            }

        } catch (JSONException e) {
            LoggerUtil.errorTrace(loggerName, "访问远程接口出错，跳出执行。返回内容为：" + getTermListStr, e);
            throw e;
        }
    }

    protected String doHttpRequest(String serviceURL, String method, Map<String, String> postData) {
        HttpClientHelper httpClientHelper = new HttpClientHelper();
        httpClientHelper.setConnectionTimeout(60000);
        int retryTimes = 0;
        String getTermListStr = "{}";
        while (retryTimes < 3) {
            try {
                HttpResponse getTermListResponse = null;

                if (method.equalsIgnoreCase("get")) {
                    getTermListResponse = httpClientHelper.doGet(serviceURL,
                            "utf-8", "utf-8", null,
                            null);
                } else {
                    getTermListResponse = httpClientHelper.doPost(serviceURL, postData, "utf-8", "utf-8", null, null);
                }
                getTermListStr = getTermListResponse.getContent();
                break;
            } catch (HttpException e) {
                LoggerUtil.errorTrace(loggerName, e);
                break;
            } catch (IOException e) {
                retryTimes++;
                LoggerUtil.errorTrace(loggerName, e);
                LoggerUtil.debugTrace(loggerName, "出现IO错误，重试 " + retryTimes + " 次");

            }
        }
        return getTermListStr;
    }
}
