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

public class ExportDataControllerTest extends AbstractSpringBaseControllerTest {
    @Test
    @Rollback(value = false)
    public void testDeleteImport() throws Exception {
        String requestURI = "/export/getOutDataSource.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","27")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testAddoutput() throws Exception {
        String requestURI = "/export/saveOutput.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","26").param("name","qwe").param("typeName","微博").param("type","1").param("apiType","xml").param("typeNo","dataOutput").param("paramId","2")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testGetOutputList() throws Exception {
        String requestURI = "/export/getOutputList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","26").param("typeNo","dataOutput")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testGetOutDataSourceDetail() throws Exception {
        String requestURI = "/export/getOutDataSourceDetail.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("resultTypeId","6").param("projectId","6")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void getDataApi() throws Exception {
        String requestURI = "/export/getDataApi.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("paramId", "559").param("page", "1")
                        .param("size", "1000")).andDo(print()).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getModelAndView().getViewName().equals("login/login"));
    }


}
