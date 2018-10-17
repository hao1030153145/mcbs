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
 * Created by byron on 2017/11/21 0021.
 */
public class projectExportControllerText extends AbstractSpringBaseControllerTest {

    @Test
    public void testProjectExportList() throws Exception {
        String requestURI = "/projectExport/getProjectExportList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no")
                        .param("page","2").param("size","10")
//                        .param("fileName","").param("exportDataType","原始数据")
//                        .param("createTime","").param("projectType","")
                        .param("projectId","795")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
    @Test
    public void testGetConditionList() throws Exception {
        String requestURI = "/projectExport/getConditionList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
    @Test
    @Rollback(false)
    public void testSaveProjectExport() throws Exception {
        String requestURI = "/projectExport/saveProjectExport.json";
        String str = "{\n" +
                "    \"projectId\": \"843\",\n" +
                "    \"exportType\": \"excel\",\n" +
                "    \"fileName\": \"/天涯\",\n" +
                "    \"exportChoice\": \"originalData\",\n" +
                "    \"exportChoiceCnName\": \"原始数据\",\n" +
                "    \"detailIdArray\": [\n" +
                "        3106\n" +
                "    ],\n" +
                "    \"taskNameArray\": [\n" +
                "        \"抓取 搜狐\"\n" +
                "    ],\n" +
                "    \"dataWay\": \"result\",\n" +
                "    \"nodeTask\": \"\",\n" +
                "    \"nodeTaskDetailId\": \"\",\n" +
                "    \"fieldAndFilter\": [\n" +
                "        \n" +
                "    ],\n" +
                "    \"dataSourceTypeArray\": [\n" +
                "        {\n" +
                "            \"list\": [\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"Title\",\n" +
                "                    \"fieldCnName\": \"标题一\",\n" +
                "                    \"id\": 1,\n" +
                "                    \"fieldType\": \"text\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"url\",\n" +
                "                    \"fieldCnName\": \"文章链接二\",\n" +
                "                    \"id\": 2,\n" +
                "                    \"fieldType\": \"text\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"Column\",\n" +
                "                    \"fieldCnName\": \"栏目三\",\n" +
                "                    \"id\": 3,\n" +
                "                    \"fieldType\": \"text\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"Author\",\n" +
                "                    \"fieldCnName\": \"作者四\",\n" +
                "                    \"id\": 4,\n" +
                "                    \"fieldType\": \"text\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"Datetime\",\n" +
                "                    \"fieldCnName\": \"发布时间五\",\n" +
                "                    \"id\": 5,\n" +
                "                    \"fieldType\": \"datetime\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"Content\",\n" +
                "                    \"fieldCnName\": \"内容七\",\n" +
                "                    \"id\": 10,\n" +
                "                    \"fieldType\": \"text\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"Summary\",\n" +
                "                    \"fieldCnName\": \"摘要六\",\n" +
                "                    \"id\": 9,\n" +
                "                    \"fieldType\": \"text\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"keyword\",\n" +
                "                    \"fieldCnName\": \"关键词十三\",\n" +
                "                    \"id\": 31,\n" +
                "                    \"fieldType\": \"text\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"CrawlTime\",\n" +
                "                    \"fieldCnName\": \"采集时间十四\",\n" +
                "                    \"id\": 33,\n" +
                "                    \"fieldType\": \"datetime\",\n" +
                "                    \"select\": true\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldEnName\": \"crawlsite\",\n" +
                "                    \"fieldCnName\": \"网站\",\n" +
                "                    \"id\": 79,\n" +
                "                    \"fieldType\": \"text\",\n" +
                "                    \"select\": true\n" +
                "                }\n" +
                "            ],\n" +
                "            \"value\": \"新闻\",\n" +
                "            \"key\": \"news\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"level\": {\n" +
                "        \"type\": \"1,2,3\",\n" +
                "        \"hierarchy\": \"1,2,3\"\n" +
                "    }\n" +
                "}";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("body",str)).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
    @Test
    public void getFieldList() throws Exception {
        String requestURI = "/projectExport/getDataTypeList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
    @Test
    public void getTaskNameList() throws Exception {
        String requestURI = "/projectExport/getTaskNameList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("projectId","338")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void getNodeNameList() throws Exception {
        String requestURI = "/projectExport/getNodeNameList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("detailId","2421")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void getExportFileTypeList() throws Exception {
        String requestURI = "/projectExport/getExportFileTypeList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("projectId","695")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void getFieldList2()throws Exception {
        String requestURI = "/projectExport/getFieldList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                (MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("detailId","2888"))).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
    @Test
    @Rollback(false)
    public void deleteExportTask()throws Exception {
        String requestURI = "/projectExport/deleteExportTask.json";
        MvcResult mvcResult = this.mockMvc.perform(
                (MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("id","30,20"))).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
    @Test
    @Rollback(false)
    public void startProject()throws Exception {
        String requestURI = "/projectExport/startProjectExport.json";
        MvcResult mvcResult = this.mockMvc.perform(
                (MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectExportTaskId","170"))).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
//        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
    @Test
    public void download()throws Exception {
        String requestURI = "/projectExport/downloadTask.json";
        MvcResult mvcResult = this.mockMvc.perform(
                (MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("id","8"))).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));

    }


}
