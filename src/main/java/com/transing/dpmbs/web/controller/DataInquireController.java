package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.ContentTypeService;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.biz.service.MongoDBService;
import com.transing.dpmbs.biz.service.ProjectJobTypeService;
import com.transing.dpmbs.constant.MongoDBDbNames;
import com.transing.dpmbs.integration.bo.ImportData;
import com.transing.dpmbs.integration.bo.ImportDataDetail;
import com.transing.dpmbs.integration.bo.ProjectJobTypeBO;
import com.transing.dpmbs.integration.bo.RelationOrigin;
import com.transing.dpmbs.util.*;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.po.*;
import com.transing.workflow.biz.service.JobTypeService;
import com.transing.workflow.biz.service.WorkFlowService;
import com.transing.workflow.constant.Constants;
import com.transing.workflow.integration.bo.JobTypeInfo;
import com.transing.workflow.integration.bo.WorkFlowDetail;
import com.transing.workflow.integration.bo.WorkFlowParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("dataInquireController")
@Api(value = "查询数据", description = "导入数据相关的访问接口", position = 3)
public class DataInquireController {
    @Resource
    private WorkFlowService workFlowService;

    @Resource
    private MongoDBService mongoDBService;

    @RequestMapping(value = "/dataInquire.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查看", notes = "", position = 0)
    public DataImportListFromShowDataPo getStatisticalAnalysisFromShowData(@RequestParam(value = "paramId", required = true) @ApiParam(value = "paramId", required = true) String paramId,
                                                                      @RequestParam(value = "projectId", required = true) @ApiParam(value = "项目 id", required = true) String projectId,
                                                                      @RequestParam(value = "page", required = false) @ApiParam(value = "页数", required = true) String page,
                                                                      @RequestParam(value = "size", required = false) @ApiParam(value = "查询条数", required = true) String size,
                                                                      @RequestParam(value = "lastIndexId",required = false)@ApiParam(value = "最后一个indexId",required = true) String lastIndexId,
                                                                      HttpServletRequest req, HttpServletResponse res) {
        DataImportListFromShowDataPo dataImportListFromShowDataPo = new DataImportListFromShowDataPo();

        int pageInt = null != page && !"".equals(page) ? Integer.parseInt(page) : 1;
        int sizeInt = null != size && !"".equals(size) ? Integer.parseInt(size) : 10;

        long paramIdInt = Long.parseLong(paramId);
        WorkFlowParam workFlowParam = workFlowService.getWorkFlowParamByParamId(paramIdInt);
        if (null == workFlowParam) {
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        Map<String,Object> param = new HashMap<>();
        param.put("projectId",workFlowParam.getProjectId());
        param.put("detailId",workFlowParam.getFlowDetailId());

        Map<String, Object> map = mongoDBService.findPageDocument(pageInt,sizeInt, MongoDBDbNames.DB_DPM,MongoDBDbNames.COLLECTION_STATICAL_NAME,new Document(param),null);
        List<Document> resultList = (List<Document>) map.get("pageData");
        List<String> titleList = new ArrayList<>();
        List<List> dataList = new ArrayList<>();
        if(!Validate.isEmpty(resultList)){
            Document doc = resultList.get(0);
            Set<String> keyList = doc.keySet();
            titleList.addAll(keyList);
            for (Document document:resultList) {
                List<String> data = new ArrayList<>();
                for (String title:titleList) {
                    Object o = document.get(title);
                    data.add(o!=null ? o.toString():"");
                }
                dataList.add(data);
            }
        }

        dataImportListFromShowDataPo.setCount(map.get("total").toString());
        dataImportListFromShowDataPo.setDataList(dataList);
        dataImportListFromShowDataPo.setTitleList(titleList);
        return dataImportListFromShowDataPo;
    }

}
