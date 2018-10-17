package com.transing.workflow.web.controller;

import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Administrator on 2018/1/8 0008.
 */
public class VisWorkFlowControllerTest extends AbstractSpringBaseControllerTest {


    @Test
    public void testLogin() throws Exception {
        String requestURI = "/visWorkFlow/getWorkFlowNodeData.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","471").param("flowDetailId","1233").param("page","1").param("flag","pre")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
    @Test
    public void testSaveOrUpdateTemplate() throws Exception {
        String requestURI = "/visWorkFlow/saveOrUpdateVisTemplate.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("templateName","sdfs")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);


    }

}
