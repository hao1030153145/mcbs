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

/**
 * 项目单元测试类
 */
public class WordSegControllerTest extends AbstractSpringBaseControllerTest {


    @Test
    @Rollback(value = false)
    public void getSemanticAnalysisObjectListFromShowData() throws Exception {
        String requestURI = "/wordSegmentation/saveSegSet.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","108").param("typeNo","wordSegmentation").param("jsonParam","{\"paramId\":null,\"actionType\":0,\"wordLibraryList\":[{\"type\":0,\"name\":\"通用词库\",\"$$hashKey\":\"object:4\"}]}")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testWordSegController() throws Exception {
        String requestURI = "/subjectSet/getSubjectSemanticResultList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","882").param("paramId","4397").param("type","article").param("size","10")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testWordSegController2() throws Exception {
        String requestURI = "/wordSegmentation/getSegSemanticResultList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","899").param("paramId","4478").param("type","sentence").param("size","15").param("lastIndexId","1533284").param("flag","pre")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
}
