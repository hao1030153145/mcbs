package com.transing.dpmbs.util;

import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.HttpException;

import java.io.IOException;
import java.util.Map;

public class CallRemoteServiceUtil {
    public static final int RESPONSE_CODE_SUCCESS = 0;//返回成功
    /**
     * 远程服务调用方法
     *
     * @param serviceURL 服务访问URL
     * @param method     访问方法  get /   post
     * @param postData   请求参数
     * @return
     */
    public static Object callRemoteService(String loggerName,String serviceURL, String method, Map<String, String> postData) {
        HttpClientHelper httpClientHelper = new HttpClientHelper();

        String getTermListStr = "{}";
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
            LoggerUtil.infoTrace("==============url"+serviceURL+"================getTermListStr:"+getTermListStr+"===================");
        } catch (HttpException e) {
            LoggerUtil.errorTrace(loggerName, e);
        } catch (IOException e) {
            LoggerUtil.errorTrace(loggerName, e);
        }

        try {
            net.sf.json.JSONObject getTermListJsonObject = net.sf.json.JSONObject.fromObject(getTermListStr);
            int code = getTermListJsonObject.getInt("code");

            if (code == RESPONSE_CODE_SUCCESS) {

                Object termListArrayObject = getTermListJsonObject.get("data");
                return termListArrayObject;
            }


        } catch (JSONException e) {
            LoggerUtil.errorTrace("url = "+serviceURL+"   param: "+JSONObject.fromObject(postData));
            LoggerUtil.errorTrace(loggerName, "访问远程接口出错，跳出执行。", e);
            return null;
        }
        return null;
    }
}
