package com.transing.workflow.web.controller;

import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import com.jeeframework.util.json.JSONUtils;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Administrator on 2018/1/4 0004.
 */
public class FlowWorkTemplateControllerTest extends AbstractSpringBaseControllerTest {


    /**
     * 这个是测试批量删除模板
     * @throws Exception
     */
   @Test
   public void TestDeleteTemplate() throws Exception{
        String requestURL = "/workFlowTemplate/deleteWorkFlowTemplate.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURL).param("testInLogin","no").param("templateId","10")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(JSONUtils.isJSONValid(response));
        String string = "123";
        System.out.print(string);
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);

    }

    @Test
    public void TestGetTemplateList() throws Exception{
        String requestURL = "/workFlowTemplate/getWorkFlowTemplateListNoPage.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURL).param("testInLogin","no")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(JSONUtils.isJSONValid(response));
        /*assertTrue(JSONObject.fromObject(response).getInt("code") == 0);*/

    }


    /**
     * 这个是测试可视化工作流模板列表分页
     * @throws Exception
     */
    @Test
    public void TestGetVisTemplateListByParam() throws Exception{
        String requestURL = "/workFlowTemplate/getVisWorkFlowTemplateList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURL).param("testInLogin","no").param("page","1").param("size","10")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);

    }

    /**
     * 这个是测试非可视化工作流模板列表分页
     * @throws Exception
     */
    @Test
    public void TestGetTemplateListByParam() throws Exception{
        String requestURL = "/workFlowTemplate/getWorkFlowTemplateList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURL).param("testInLogin","no")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);

    }

    /**
     * 这个是测试跳转到可视化创建页面
     * @throws Exception
     */
   /* @Test
    public void TestGetCreateTemplateHtml() throws Exception{
        String requestURL = "/toCreateVisWorkFlowProject.html";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURL).param("testLogin","no")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getModelAndView().getViewName().equals(""));
    }*/


    /**
     * 这个是测试跳转页面到可视化工作台
     * @throws Exception
     */
    /*@Test
    public void TestGetWorkFlowHtml() throws Exception{
        String requestURL = "/toWorkFlow.html";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURL).param("testLogin","no").param("projectId","").param("templateId","")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getModelAndView().getViewName().equals(""));
    }*/





}
