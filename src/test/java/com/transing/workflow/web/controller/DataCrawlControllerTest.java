package com.transing.workflow.web.controller;

import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 项目单元测试类
 */
public class DataCrawlControllerTest extends AbstractSpringBaseControllerTest {


    @Test
    @Rollback(value = false)
    public void save() throws Exception {
        String requestURI = "/dataCrawl/saveCrawlObject.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","606").param("typeNo","dataCrawl").param("jsonParam","{\"taskName\":\"测试\",\"datasourceId\":\"22\",\"datasourceTypeId\":\"8\",\"storageTypeTable\":\"news\",\"inputParamArray\":[{\"controlProp\":\"\",\"createTime\":{\"date\":9,\"day\":1,\"hours\":18,\"minutes\":57,\"month\":9,\"seconds\":10,\"time\":1507546630000,\"timezoneOffset\":-480,\"year\":117},\"datasourceId\":22,\"datasourceTypeId\":8,\"id\":13,\"isRequired\":\"0\",\"paramCnName\":\"关键词\",\"paramEnName\":\"keyword\",\"prompt\":\"请输入关键词\",\"restrictions\":\"\",\"styleId\":\"1\",\"styleName\":\"文本框\",\"styleCode\":\"input\",\"$$hashKey\":\"object:65\",\"paramValue\":\"宝马\"},{\"controlProp\":\"\",\"createTime\":{\"date\":9,\"day\":1,\"hours\":18,\"minutes\":57,\"month\":9,\"seconds\":10,\"time\":1507546630000,\"timezoneOffset\":-480,\"year\":117},\"datasourceId\":22,\"datasourceTypeId\":8,\"id\":14,\"isRequired\":\"0\",\"paramCnName\":\"测试\",\"paramEnName\":\"test\",\"prompt\":\"请输入test\",\"restrictions\":\"\",\"styleId\":\"1\",\"styleName\":\"文本框\",\"styleCode\":\"input\",\"$$hashKey\":\"object:66\",\"paramValue\":\"test\"}],\"crawlFreqType\":\"1\",\"quartzTime\":\"* * * * * *\",\"crawlType\":\"dataCrawl\",\"crawlWay\":\"data\",\"workFlowTemplateId\":\"10\"}")
                        ).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void delCrawlObject() throws Exception
    {
        String requestURI = "/dataCrawl/delCrawlObject.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(requestURI).param("testInLogin", "no").param("paramId", "501")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }


    @Test
    @Rollback(value = false)
    public void getWorkFlowTemplateList() throws Exception
    {
        String requestURI = "/dataCrawl/getWorkFlowTemplateList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(requestURI).param("testInLogin", "no").param("paramId", "496")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void getDataCrawlList() throws Exception
    {
        String requestURI = "/dataCrawl/getDataCrawlList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(requestURI).param("testInLogin", "no").param("projectId", "120").param("typeNo","dataCrawl")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void startCrawlObject() throws Exception
    {
        String requestURI = "/dataCrawl/startCrawlObject.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(requestURI).param("testInLogin", "no").param("projectId", "104").param("paramId","972")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void get() throws Exception
    {
        /*String requestURI = "/dataCrawl/getUploadFile.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(requestURI).param("testInLogin", "no").param("url", "1503034999147keyword.xlsx").param("projectId", "104")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();*/

        HttpClientHelper httpClientHelper = new HttpClientHelper();
        Map<String, String> getTermListByTermNamePostData = new HashMap<String, String>();
        getTermListByTermNamePostData.put("url", "1503042059035200news.xlsx");
        HttpResponse httpResponse = httpClientHelper.doPostAndRetBytes("http://dpmbs2dev.dookoo.net/dataCrawl/getUploadFile.json",getTermListByTermNamePostData, "utf-8", "utf-8",null, null);

        byte[] v =  httpResponse.getContentBytes();
        File file = new File("D:\\data\\project\\datacrawl\\tmp\\test.xlsx");
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        fos = new FileOutputStream(file);
        bos = new BufferedOutputStream(fos);
        bos.write(v);

        bos.flush();
        bos.close();

        /*assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);*/

    }

}
