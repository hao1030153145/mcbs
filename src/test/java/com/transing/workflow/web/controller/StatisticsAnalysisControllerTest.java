package com.transing.workflow.web.controller;

import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatisticsAnalysisControllerTest extends AbstractSpringBaseControllerTest {
    @Test
    @Rollback(value = false)
    public void saveStatisticsAnalysis() throws Exception {
        String requestURI = "/statisticsAnalysis/saveStatisticsAnalysis.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("jsonParam", "{\"limitNum\":0,\"paramId\":926,\"name\":\"测序\",\"dataType\":[{\"datasourceType\":[\"11\"],\"detailId\":[],\"storageTypeTableId\":\"news\",\"$$hashKey\":\"object:8\",\"isShow\":false}],\"analysisHierarchy\":\"sentence\",\"fieldAndFilter\":[{\"fieldCategory\":\"source\",\"field\":\"summary\",\"fieldZh\":\"summary,text,摘要\",\"fieldName\":\"摘要\",\"fieldType\":\"dimension\",\"conditionType\":null,\"conditionExp\":null,\"conditionValue\":null,\"conditionValue2\":null,\"$$hashKey\":\"object:15\"},{\"fieldCategory\":\"source\",\"field\":\"author\",\"fieldZh\":\"author,text,作者\",\"fieldName\":\"作者\",\"fieldType\":\"dimension\",\"conditionType\":null,\"conditionExp\":null,\"conditionValue\":null,\"conditionValue2\":null,\"$$hashKey\":\"object:16\"}],\"statisticsObject\":[{\"field\":\"text\",\"fieldZh\":\"text,text,内容\",\"type\":\"count\",\"$$hashKey\":\"object:39\"}],\"sortOrder\":null}").param("projectId","180")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }


    @Test
    public void getFieldFilterList() throws Exception{
        String requestURI = "/statisticsAnalysis/getFieldFilterList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("projectId","180").param("dataType","[{\"datasourceType\":[\"1\",\"12\",\"14\"],\"$$hashKey\":\"object:6\",\"storageTypeTableId\":\"weibolist\",\"isShow\":true}]")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }


    @Test
    @Rollback(value = false)
    public void getDataTypeList() throws Exception{
        String requestURI = "/statisticsAnalysis/getDataTypeList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("projectId","204")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
}
