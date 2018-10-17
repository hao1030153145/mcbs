package com.transing.proxy.controller;

import com.jeeframework.logicframework.integration.dao.redis.BaseDaoRedis;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.util.encrypt.MD5Util;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.QQMailUtil;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.DataImportListFromShowDataPo;
import com.transing.proxy.po.TypeNoWorkFlowTemplateIdMap;
import com.transing.workflow.constant.Constants;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by byron on 2018/4/11 0011.
 */

@Controller("proxyController")
@RequestMapping("/liveData")
@Api(value = "看看直播", description = "看看直播代理接口", position = 2)
public class ProxyController {
    private static  final  String  ADDRESSER="2565513662@qq.com";//发件人账号
    private static  final   String COMMAND="lbswlqnkyvnidjig";//smtp口令
    private static  final  String ADDRESSEE="531956949@qq.com";//收件人账号
    public static final String ADDWORKFLOWTASK = "/addWorkFlowTask.json";



    @Resource
    private BaseDaoRedis redis;


    @RequestMapping(value = "/addKankanLiveComments.json", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> addKankanLiveComments(@RequestBody String body){

        JSONObject bodyJson = JSONObject.fromObject(body);
        String projectId = bodyJson.getString("projectId");
        JSONArray data = bodyJson.getJSONArray("data");
        String token = bodyJson.getString("token");
        String batchNo=bodyJson.getString("batchNo");
        Integer wordId=0;
        Map<String,Object> resultMap = new HashMap<>();

        String string = MD5Util.encrypt(projectId+Constants.TOKEN);
        if(!Validate.isEmpty(token)){
            if(!string.equals(token)){
                throw new WebException(MySystemCode.TOKEN_ERROR);
            }
        }else{
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        if (projectId == null || "".equals(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (data == null || "".equals(data)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(batchNo==null || "".equals(batchNo)){
            throw  new WebException((SystemCode.SYS_REQUEST_EXCEPTION));
        }
        String host = WebUtil.getLocalHostServerByEnv();

        JSONArray jsonArray = JSONArray.fromObject(data);

        List<Map<String,String>> errorList = new ArrayList<>();
        List<Map<String,String>> successList=new ArrayList<>();
        StringBuffer context=new StringBuffer();
        int error = 0;
        if(jsonArray!=null){
            resultMap.put("total",jsonArray.size());

            for(Object o : jsonArray){
                JSONObject jsonObject = (JSONObject) o;
                String typeNo = jsonObject.getString("typeNo");
                Integer workFlowId = TypeNoWorkFlowTemplateIdMap.getWorkFlowId(typeNo);
                String url = jsonObject.getString("url");
                if(workFlowId != null){
                    wordId=workFlowId;
                    Map<String,String> map = new HashMap<>();
                    map.put("projectId",projectId);
                    map.put("token",token);
                    map.put("batchNo",batchNo);
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("url",url);
                    map.put("value",jsonObject1.toString());
                    map.put("workFlowId",workFlowId+"");
                    map.put("token",token);
                    if(TypeNoWorkFlowTemplateIdMap.tencent_live.getName().equals(typeNo)){
                        map.put("crawlFreq","300,86400");
                    }else if(TypeNoWorkFlowTemplateIdMap.sina_weibo.getName().equals(typeNo)){
                        map.put("crawlFreq","300,86400");
                    }else if(TypeNoWorkFlowTemplateIdMap.sina_live.getName().equals(typeNo)){
                        map.put("crawlFreq","300,86400");
                    }else if(TypeNoWorkFlowTemplateIdMap.wangyi_live.getName().equals(typeNo)){
                        map.put("crawlFreq","300,86400");
                    }else if(TypeNoWorkFlowTemplateIdMap.yidian_live.getName().equals(typeNo)){
                        map.put("crawlFreq","300,86400");
                    }else if(TypeNoWorkFlowTemplateIdMap.youku_live.getName().equals(typeNo)){
                        map.put("crawlFreq","300,86400");
                    }
                    LoggerUtil.debugTrace("---=====batchNo========"+batchNo);
                    Object object = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),host+ADDWORKFLOWTASK,"post",map);
                    if(object == null){
                        error++;
                        Map<String,String> errorMap = new HashMap<>();
                        errorMap.put("url",url);
                        errorMap.put("typeNo",typeNo);
                        errorMap.put("msg","系统异常");
                        errorList.add(errorMap);
                        context.append("错误地址:"+url+",错误原因:系统异常             ");
                    }else {
                        Map<String,String> successMap = new HashMap<>();
                        JSONObject bodyJson2 = JSONObject.fromObject(object);
                        String paramId=bodyJson2.getString("paramId");
                        String detailId=bodyJson2.getString("flowDetailId");
                        successMap.put("paramId",paramId);
                        successMap.put("detailId",detailId);
                        successList.add(successMap);
                        context.append("成功detailId:"+detailId+",paramId:"+paramId+"          ");
                    }
                }else{
                    error++;
                    Map<String,String> errorMap = new HashMap<>();
                    errorMap.put("url",url);
                    errorMap.put("msg","该站点不能识别");
                    errorList.add(errorMap);
                }
            }
        }
        resultMap.put("error",error);
        resultMap.put("errorList",errorList);
        //邮箱标题
        String title=projectId+"新增"+wordId;
        //邮箱内容
        String contests="一共"+jsonArray.size()+"条,错误"+error+"条"+context.toString()+"。";
        //获取上次的时间
        String times=redis.get("sendMailLastTime");
        //判断上次的时间是否为空
        if(times==null){
            //调用QQMailUtil发送邮件
            QQMailUtil.sendMail(title,ADDRESSER,COMMAND,contests,ADDRESSEE);
            //把这次发邮箱的时间记录
            redis.set("sendMailLastTime",""+System.currentTimeMillis());
        }else{
            Long outTime=Long.parseLong(times);
            Long nowTime=System.currentTimeMillis();
            //判断这次发邮箱和上次发邮箱的时间差是多少
            if (((nowTime-outTime)/1000)>60){
                //调用QQMailUtil发送邮件
                QQMailUtil.sendMail(title,ADDRESSER,COMMAND,contests,ADDRESSEE);
                redis.set("sendMailLastTime",""+System.currentTimeMillis());
            }
        }

        return resultMap;
    }
    @RequestMapping(value = "/getKanKanData.json", method = RequestMethod.POST)
    @ResponseBody
    public String getKanKanData(@RequestBody String body) {
        JSONObject bodyJson = JSONObject.fromObject(body);
        String projectId = bodyJson.getString("projectId");
        String token = bodyJson.getString("token");
        String batchNo=bodyJson.getString("batchNo");
        if (Validate.isEmpty(projectId) || !projectId.matches("\\d+")) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if (Validate.isEmpty(batchNo)) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        String string = MD5Util.encrypt(projectId+Constants.TOKEN);
        if(!Validate.isEmpty(token)){
            if(!string.equals(token)){
                throw new WebException(MySystemCode.TOKEN_ERROR);
            }
        }else{
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        Map<String, String> dataMap = new HashMap<>();
        String data=null;
        dataMap.put("projectID", projectId);
        dataMap.put("batchNo", batchNo);
        Object obj = CallRemoteServiceUtil.callRemoteService(this.getClass().getName(), WebUtil.getCorpusServerByEnv() + "/live/queryCommentTotalData.json", "post", dataMap);
        if (obj != null) {
            JSONObject jsonObject = JSONObject.fromObject(obj);
//            JSONObject object1=jsonObject.getJSONObject("partakeShowData");
//            JSONArray obj1=object1.getJSONArray("webCNname");
//            for (Object title:obj1
//                    ) {
//                webCNname.add(title.toString());
//            }
//            JSONArray obj2=object1.getJSONArray("partakeList");
//            for (Object data:obj2
//                    ) {
//                partakeList.add(data.toString());
//            }
//            JSONObject object3=jsonObject.getJSONObject("allData");
//            JSONObject object3s=object3.getJSONObject("dataList");
//            JSONArray interactList1=object3s.getJSONArray("interactList");
//            for (Object data:interactList1
//                    ) {
//                interactList.add(data.toString());
//            }
//            map1.put("webCNname",webCNname);
//            map1.put("partakeList",partakeList);
//            //词云
//            JSONObject cloudData=jsonObject.getJSONObject("cloudData");
//            //调性
//            JSONObject  tonalityData=jsonObject.getJSONObject("cloudData");
//            JSONObject  nteractListShowData=jsonObject.getJSONObject("nteractListShowData");
//            JSONObject  partakeOfTwentyFour=jsonObject.getJSONObject("partakeOfTwentyFour");
//            Map dataList=new HashMap();
//            dataList.put("partakeList",partakeList);
//            dataList.put("interactList",interactList);
//            map3.put("webCNname",webCNname);
//            map3.put("dataList",dataList);
//            map.put("partakeShowData",map1);
//            map.put("allData",map3);
//            map.put("cloudData",cloudData);
//            map.put("tonalityData",tonalityData);
//            map.put("nteractListShowData",nteractListShowData);
//            map.put("partakeOfTwentyFour",partakeOfTwentyFour);
              data=jsonObject.toString();

        }
        return data;

    }
    @RequestMapping(value = "/getDataImportListFromShowData.json", method = RequestMethod.POST)
    @ResponseBody
    public  Map<String,Object> getDataImportListFromShowData(@RequestBody String body) {
        JSONObject bodyJson = JSONObject.fromObject(body);
        String projectId = bodyJson.getString("projectId");
        String token = bodyJson.getString("token");
        String batchNo=bodyJson.getString("batchNo");
        if(Validate.isEmpty(projectId) || !projectId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(Validate.isEmpty(batchNo)){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        String string = MD5Util.encrypt(projectId+Constants.TOKEN);
        if(!Validate.isEmpty(token)){
            if(!string.equals(token)){
                throw new WebException(MySystemCode.TOKEN_ERROR);
            }
        }else{
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        if(batchNo==null || "".equals(batchNo)){
            throw  new WebException((SystemCode.SYS_REQUEST_EXCEPTION));
        }
        Map<String,String> dataMap=new HashMap<>();
        Map<String,Object> allDataMap=new HashMap<>();
        dataMap.put("projectID",projectId);
        dataMap.put("batchNo",batchNo);
        Object obj=CallRemoteServiceUtil.callRemoteService(this.getClass().getName(),WebUtil.getCorpusServerByEnv() + "/live/queryAllData.json", "post", dataMap);
        if (obj != null) {
            JSONObject jsonObject = JSONObject.fromObject(obj);
            JSONArray titleList = jsonObject.getJSONArray("titleList");
            //存放英文标题
            List<String> titleENlist = new ArrayList<>();
            for (Object title : titleList) {
                titleENlist.add(title.toString());
            }
            JSONArray dataList = jsonObject.getJSONArray("dataList");
            //存放所有的数据
            List< Map<Object,String>> alldata=new ArrayList<>();
            for (Object data:dataList ) {
                JSONObject data1=JSONObject.fromObject(data);
                //存放一条数据
                Map<Object,String> onelist=new HashMap();
                for (Object title1:titleENlist) {
                    String value =data1.getString(title1.toString());
//                    if((title1.equals("datetime")||title1.equals("crawltime")) && value!=null ){
//                        net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(value);
//                        Date date = (Date) net.sf.json.JSONObject.toBean(jsonObject1, Date.class);
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        value = format.format(date);
//                    }
                    onelist.put(title1,value);
                }
                alldata.add(onelist);
            }
            allDataMap.put("dataList",alldata);
        }
        return allDataMap;

    }
    public static void main(String[] args) {
        String string = MD5Util.encrypt("699transingAdmin");
        System.out.println(string);
    }
}
