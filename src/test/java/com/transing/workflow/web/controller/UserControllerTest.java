package com.transing.workflow.web.controller;

import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import com.transing.dpmbs.web.form.UserQueryRequest;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户登录注册单元测试类
 *
 * @author lance
 * @version 1.0 2017-02-14 11:29
 */
public class UserControllerTest extends AbstractSpringBaseControllerTest {
    @Test
    public void testLogin() throws Exception {
        String requestURI = "/login.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("userName", "admin").param("passwd", "admin321").param("remember","1")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void doGetLoginHtml() throws Exception {
        String requestURI = "/login.html";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI)).andDo(print()).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getModelAndView().getViewName().equals("login/login"));
    }

    @Test
    public void doGetLogoutHtml() throws Exception {
        String requestURI = "/logout.html";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no")).andDo(print()).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getModelAndView().getViewName().equals("login/login"));
    }
}