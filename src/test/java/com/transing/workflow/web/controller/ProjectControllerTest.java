package com.transing.workflow.web.controller;

import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 项目单元测试类
 */
public class ProjectControllerTest extends AbstractSpringBaseControllerTest {

    @Test
    public void doProjectCreatePageHtml() throws Exception {
        String requestURI = "/project/projectCreatePage.html";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("from","0")).andDo(print()).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getModelAndView().getViewName().equals("projectManager/createPage/projectCreatePage"));
    }

    @Test
    public void doprojectListPageeHtml() throws Exception {
        String requestURI = "/project/projectListPage.html";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI)).andDo(print()).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getModelAndView().getViewName().equals("projectManager/listPage/projectListPage"));
    }

    @Test
    public void doProjectHomePage() throws Exception {
        String requestURI = "/project/projectHomePage.html";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("projectId","18")).andDo(print()).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getModelAndView().getViewName().equals("projectManager/homePage/projectHomePage"));
    }


    /**
     * 这个是测试测试的测试---haolen
     * @throws Exception
     */
    @Test
    public  void testTopicProject() throws Exception{
        String requestURI = "/project/getTopicList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testLogin","no").param("manager","").param("sortStatus","id desc").param("page","1").param("size","10").param("projectType","page")
        ).andDo(print()).andExpect(status().isOk()).andReturn();
        String respone = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(respone));
        assertTrue(JSONObject.fromObject(respone).getInt("code")==0);
    }

    @Test
    public void testProjectList() throws Exception {
        String requestURI = "/project/getProjectList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("manager","").param("sortStatus","id desc").param("page","1").param("size","15").param("projectType","page").param("projectName","")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }



    @Test
    public void testGetProjectInf() throws Exception {
        String requestURI = "/project/getProjectInf.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","5")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void testProjectManager() throws Exception {
        String requestURI = "/project/getProjectManagerList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("managerId","2")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void testCustomerList() throws Exception {
        String requestURI = "/project/getCustomerList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void testStatusList() throws Exception {
        String requestURI = "/project/getStatusList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testUpdateDelProject() throws Exception {
        String requestURI = "/project/updateDelProject.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","1")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testStartProject() throws Exception {
        String requestURI = "/project/startProject.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","473")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testStopProject() throws Exception {
        String requestURI = "/project/stopProject.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","209")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void copyProject() throws Exception {
        String requestURI = "/project/copyProject.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","399")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testCreateProject() throws Exception {
        List<Map> mapList=new ArrayList<>();
        for(int i=0;i<3;i++){
            Map<String,String> map=new HashMap<>();
            map.put("title","快醒醒");
            map.put("time","2015-4-5"+"i");
            mapList.add(map);
        }
        String json=com.alibaba.fastjson.JSONObject.toJSONString(mapList);
        System.out.println(json);
//        String requestURI = "/project/createProject.json";
//        MvcResult mvcResult = this.mockMvc.perform(
//                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no")
//                        .param("from","1")
//                        .param("projectId","28")
//                        .param("projectName","12dddddddy")
//                        .param("projectDescribe","not")
//                        .param("typeId","3")
//                        .param("managerId","1")
//                        .param("customerId","1")
//                        .param("startTime","2017-11-23")
//                        .param("endTime","2017-01-10")
//                        ).andDo(print()).andExpect(status().isOk()).andReturn();
//        String response = mvcResult.getResponse().getContentAsString();
//        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
//        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

}
