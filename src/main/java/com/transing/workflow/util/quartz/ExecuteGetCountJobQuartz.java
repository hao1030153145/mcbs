package com.transing.workflow.util.quartz;

import com.jeeframework.core.context.support.SpringContextHolder;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.proxy.controller.ProxyController;
import com.transing.workflow.web.controller.WorkFLowController;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExecuteGetCountJobQuartz implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String projectId=jobDataMap.getString("projectId");
        String batchNo=jobDataMap.getString("batchNo");
        Map datamap=new HashMap();
        datamap.put("projectID",projectId);
        datamap.put("batchNo",batchNo);
        Object datas=CallRemoteServiceUtil.callRemoteService(this.getClass().getName(),WebUtil.getCorpusServerByEnv() + "/live/queryAllData.json", "post", datamap);
        JSONObject jsonObjectData=JSONObject.fromObject(datas);
        LoggerUtil.debugTrace("jsonObjectData:="+jsonObjectData);
        JSONArray dataList = jsonObjectData.getJSONArray("dataList");
        String text="";
        for(Object data:dataList) {
            JSONObject jsonObject=JSONObject.fromObject(data);
            LoggerUtil.debugTrace("jsonObject======"+jsonObject);
            String value=jsonObject.getString("content");
            if(value!=null){
                text=text+value+"\n";
            }
        }
        LoggerUtil.debugTrace("===text:="+text);
        Map<String, String> headMap = new HashMap<>();
        headMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/57.0.2987.133 Safari/537.36");
        headMap.put("Upgrade-Insecure-Requests", "1");
        headMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headMap.put("Accept-Encoding", "gzip, deflate, sdch");
        headMap.put("Accept-Language", "zh-CN,zh;q=0.8");
        headMap.put("Cache-Control", "no-cache");
        Map postData = new HashMap<>();
        postData.put("texts", text);
        HttpClientHelper httpClientHelper = new HttpClientHelper();
        try {
            if(!text.equals("")&&text!=null){
            httpClientHelper.setSoTimeout(3000000);
            HttpResponse resp1 = httpClientHelper.doPost("http://10.66.183.223:8899/moods", postData,
                    "utf-8", "utf-8", headMap, null);
            LoggerUtil.debugTrace("url:="+"http://118.190.35.44:8899/moods"+"    postData"+postData.toString()+"   headMap"+headMap.toString());
            HttpResponse resp2 = httpClientHelper.doPost("http://10.66.183.223:8899/keys", postData,
                    "utf-8", "utf-8", headMap, null);

            String content = resp1.getContent();
            JSONObject jsonObject=JSONObject.fromObject(content);
            LoggerUtil.debugTrace("content:="+jsonObject);
            content=jsonObject.getString("moods");

            String content2 = resp2.getContent();
            JSONObject jsonObject1=JSONObject.fromObject(content2);
            LoggerUtil.debugTrace("content2:="+jsonObject1);
            content2=jsonObject1.getString("keys");
            LoggerUtil.debugTrace("content2:="+content2+"          content+"+content);

//            Object obj=CallRemoteServiceUtil.callRemoteService(this.getClass().getName(),WebUtil.getCorpusServerByEnv() + "/live/queryAllData.json", "post", map);
            Map map1=new HashMap();
            map1.put("projectID",projectId);
            map1.put("batchNo",batchNo);
            map1.put("type","tonality");
            map1.put("results",content);
            Object obj2=CallRemoteServiceUtil.callRemoteService(this.getClass().getName(),WebUtil.getCorpusServerByEnv() + "/live/addCommentAnalysis.json", "post", map1);
            Map map2=new HashMap();
            map2.put("projectID",projectId);
            map2.put("batchNo",batchNo);
            map2.put("type","cloud");
            map2.put("results",content2);
            Object obj1=CallRemoteServiceUtil.callRemoteService(this.getClass().getName(),WebUtil.getCorpusServerByEnv() + "/live/addCommentAnalysis.json", "post", map2);
            LoggerUtil.debugTrace("===obj1"+obj1+"      obj2"+obj2);

            }else{
                LoggerUtil.debugTrace("没有评论");
            }
        } catch (org.apache.http.HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClientHelper.getHttpClient().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
