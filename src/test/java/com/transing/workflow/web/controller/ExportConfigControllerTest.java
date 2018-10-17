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
 * 包: com.transing.workflow.web.controller
 * 源文件:ExportConfigControllerTest.java
 *
 * @author Allen  Copyright 2016 成都创行, Inc. All rights reserved.2017年05月31日
 */
public class ExportConfigControllerTest extends
        AbstractSpringBaseControllerTest
{
    @Test
    @Rollback(value = false)
    public void getListTest() throws Exception
    {
        String requestURI = "/exportConfig/getExportFileConfigList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(requestURI).param("testInLogin", "no").param("projectId", "39")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void getDataTypeRelationList() throws Exception
    {
        String requestURI = "/exportConfig/getDataTypeRelationList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(requestURI).param("testInLogin", "no").param("dataSourceTypeId", "6").param("exportDataTypeId", "1").param("projectId", "108")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void delConfigInfoTest() throws Exception{
        String requestURL="/exportConfig/deleteExportFileConfig.json";
        MvcResult mvcResult=this.mockMvc.perform(MockMvcRequestBuilders.post(requestURL).param("testInLogin","no").param("paramId","129")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response=mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
    }
}
