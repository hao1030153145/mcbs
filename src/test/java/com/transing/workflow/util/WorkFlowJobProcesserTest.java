package com.transing.workflow.util;

import com.alibaba.fastjson.JSON;
import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.integration.bo.ImportDataDetail;
import com.transing.dpmbs.util.WebUtil;
import com.transing.dpmbs.web.po.SemanticAnalysisObjectPO;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import org.apache.http.HttpException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

/**
 * 描述
 *
 * @author lance
 * @version 1.0 2017-02-26 21:01
 */
public class WorkFlowJobProcesserTest extends AbstractSpringBaseControllerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @Rollback(false)
    public void executeFirstJob() throws Exception {

        JobTypeInfo jobTypeInfo = new JobTypeInfo();
        jobTypeInfo.setExecuteUrl("/workFlow/executeWorkFlow.json");
        jobTypeInfo.setProgressUrl("/workFlow/getExecuteStatus.json");
        jobTypeInfo.setJobType(2);
        jobTypeInfo.setResultUrl("/workFlow/getWorkFlowResultParams.json");
        jobTypeInfo.setTypeClassify(2);
        jobTypeInfo.setJobType(2);
        jobTypeInfo.setTypeNo("dataImport");

        WorkFlowDetail workFlowDetail = new WorkFlowDetail();

        WorkFlowExecuteMethod workFlowExecuteMethod = new WorkFlowExecuteMethod();

//        workFlowExecuteMethod.executeFirstJob(this.getClass().getSimpleName(),jobTypeInfo,workFlowDetail);

    }

    public static void main(String[] args) {

        String jsonParamStr = "";

        SemanticAnalysisObjectPO semanticAnalysisObjectPO = JSON.parseObject(jsonParamStr,SemanticAnalysisObjectPO.class);
    }

}