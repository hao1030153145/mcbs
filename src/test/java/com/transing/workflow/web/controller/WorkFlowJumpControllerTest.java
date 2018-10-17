package com.transing.workflow.web.controller;

import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Administrator on 2017/12/21 0021.
 */
public class WorkFlowJumpControllerTest extends AbstractSpringBaseControllerTest {

    @Test
    public void testToDataModular() throws Exception{

            String requestURI = "/project/projectListPage.html?type=vis";
            MvcResult mvcResult = this.mockMvc.perform(
                    MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no")).andDo(print()).andExpect(status().isOk()).andReturn();
            assertTrue(mvcResult.getModelAndView().getViewName().equals("projectManager/listPage/projectListPage"));
        }


}
