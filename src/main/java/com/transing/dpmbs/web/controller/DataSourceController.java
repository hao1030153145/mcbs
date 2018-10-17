package com.transing.dpmbs.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.SystemCode;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.DataSourceTypeService;
import com.transing.dpmbs.constant.Constants;


import com.transing.dpmbs.integration.bo.User;
import com.transing.dpmbs.util.CallRemoteServiceUtil;
import com.transing.dpmbs.util.WebUtil;

import com.transing.dpmbs.web.filter.DatasourceTypeFilter;
import com.transing.dpmbs.web.po.*;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包: com.transing.dpmbs.web.controller
 * 源文件:DataSourceController.java
 *
 * @author Allen  Copyright 2016 成都创行, Inc. All rights reserved.2017年05月02日
 */
@Controller("dataSourceController")
@RequestMapping(path = "/dataSource")
@Api(value = "数据源类型",description = "数据源类型以及类型字段的相关接口",position = 3)
public class DataSourceController
{
    @Resource
    private DataSourceTypeService dataSourceTypeService;

    @RequestMapping(value = "/getDateSourceTypeList.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 启用的数据源类型",position = 0)
    public List<DatasourcePO> getAllDataSourceTypeList(@RequestParam(value = "sourceType",required = false) @ApiParam(value = "sourceType(dataCrawl为pc抓取dataMcrawl为移动)",required = false) String sourceType,
                                                       HttpServletRequest request,HttpServletResponse response){

        List<DatasourcePO> datasourcePOList = dataSourceTypeService.getDataSourceTypeList(sourceType);
        return datasourcePOList;
    }

    @RequestMapping(value = "/getAllDateSourceTypeList.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 获取所有的数据源类型",position = 0)
    public List<DatasourcePO> getAllDateSourceTypeList(HttpServletRequest request,HttpServletResponse response){
        List<DatasourcePO> datasourcePOList = dataSourceTypeService.getAllDataSourceTypeList();
        return datasourcePOList;
    }

    @RequestMapping(value = "/datasourceConfListPage.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "数据源配置列表", position = 0)
    public ModelAndView projectListPageHtml(HttpServletRequest req, HttpServletResponse res) {
        return new ModelAndView("datasourceManager/index");
    }

    @RequestMapping(value = "/getStatusList.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取类型列表", notes = "", position = 0)
    public List<DatasoureTypeStatus> getStatusList(HttpServletRequest req, HttpServletResponse res) {
        List<DatasoureTypeStatus> result = new ArrayList<>();
        DatasoureTypeStatus status = new DatasoureTypeStatus();
        status.setValue(0);
        status.setName("失效");
        result.add(status);

        DatasoureTypeStatus status1 = new DatasoureTypeStatus();
        status1.setValue(1);
        status1.setName("有效");
        result.add(status1);

        return result;
    }

    @RequestMapping(value = "/getNotAddDatasourceTypeList.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 获取未添加的数据源类型",position = 0)
    public List<DatasourcePO> getNotAddDatasourceTypeList(HttpServletRequest request,HttpServletResponse response){
        List<DatasourcePO> datasourcePOList = dataSourceTypeService.getNotAddDatasourceTypeList();
        return datasourcePOList;
    }

    @RequestMapping(value = "/getDatasourceTypeListAndContentType.json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询 获取所有的数据源类型",position = 0)
    public CommonPageListPO getDatasourceTypeListAndContentType(@RequestParam(value = "datasourceId",required = false) @ApiParam(value = "数据源ID",required = false) String datasourceId,
                                                                  @RequestParam(value = "datasourceTypeId",required = false) @ApiParam(value = "数据源类型ID",required = false) String datasourceTypeId,
                                                                  @RequestParam(value = "status",required = false) @ApiParam(value = "状态（1为有效，0为无效）",required = false) String status,
                                                                  @RequestParam(value = "sortStatus", required = false) @ApiParam(value = "排序字段") String sortStatus,
                                                                  @RequestParam(value = "page", required = false) @ApiParam(value = "页数") String page,
                                                                  @RequestParam(value = "size", required = false) @ApiParam(value = "请求个数") String size,
            HttpServletRequest request,HttpServletResponse response){

        int pageInt = null!=page?Integer.parseInt(page):1;
        int sizeInt = null != size? Integer.parseInt(size):10;

        DatasourceTypeFilter datasourceTypeFilter = new DatasourceTypeFilter();
        if(!Validate.isEmpty(datasourceId) && datasourceId.matches("\\d+")){
            datasourceTypeFilter.setDatasourceId(Integer.parseInt(datasourceId));
        }

        if(!Validate.isEmpty(datasourceTypeId) && datasourceTypeId.matches("\\d+")){
            datasourceTypeFilter.setDatasourceTypeId(Integer.parseInt(datasourceTypeId));
        }
        if(!Validate.isEmpty(status) && status.matches("\\d+")){
            datasourceTypeFilter.setStatus(Integer.parseInt(status));
        }

        if(!Validate.isEmpty(sortStatus)){
            String sortStatu = sortStatus.split("\\s+")[0];
            if(sortStatu.equals("id")){
                sortStatu = "datasource_type_id";
            }

            datasourceTypeFilter.setSortStatus(sortStatu);
            datasourceTypeFilter.setDirect(sortStatus.split("\\s+")[1]);
        }
        datasourceTypeFilter.setPage((pageInt-1)*sizeInt);
        datasourceTypeFilter.setSize(sizeInt);

        List<DatasourceContentPO> datasourceContentPOList = dataSourceTypeService.getDatasourceTypeContent(datasourceTypeFilter);
        int count = dataSourceTypeService.getDatasourceTypeCount(datasourceTypeFilter);

        CommonPageListPO commonPageListPO = new CommonPageListPO();
        commonPageListPO.setDataList(datasourceContentPOList);
        commonPageListPO.setCount(count);

        return commonPageListPO;
    }

    @RequestMapping(value = "/saveDatasourceTypeConf.json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "保存 数据源类型配置",position = 0)
    public CommonPO saveDatasourceTypeConf(@RequestParam(value = "jsonParam",required = true) @ApiParam(value = "jsonParam",required = true) String jsonParam,
                                           HttpServletRequest request, HttpServletResponse response){

        DatasourceContentJsonParamPO datasourceContentJsonParamPO = JSON.parseObject(jsonParam,DatasourceContentJsonParamPO.class);

        User user = (User) request.getSession().getAttribute(Constants.WITH_SESSION_USER);
        String username = user.getAccount();

        int i = dataSourceTypeService.saveDatasourceTypeConf(datasourceContentJsonParamPO,username);

        CommonPO commonPO = new CommonPO();
        commonPO.setCode(0);
        return commonPO;
    }

    @RequestMapping(value = "/enabledDatasourceAndContent.json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "启用数据源类型",position = 0)
    public CommonPO enabledDatasourceAndContent(@RequestParam(value = "id",required = true) @ApiParam(value = "id",required = true) String id,
                                           HttpServletRequest request, HttpServletResponse response){

        if(Validate.isEmpty(id) && !id.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int idInt = Integer.parseInt(id);
        User user = (User) request.getSession().getAttribute(Constants.WITH_SESSION_USER);
        String username = user.getAccount();
        dataSourceTypeService.updateDatasourceTypeStatus(1,username,idInt);

        CommonPO commonPO = new CommonPO();
        commonPO.setCode(0);
        return commonPO;
    }

    @RequestMapping(value = "/failDatasourceAndContent.json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "失效数据源类型",position = 0)
    public CommonPO failDatasourceAndContent(@RequestParam(value = "id",required = true) @ApiParam(value = "id",required = true) String id,
                                           HttpServletRequest request, HttpServletResponse response){

        if(Validate.isEmpty(id) && !id.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }

        int idInt = Integer.parseInt(id);

        User user = (User) request.getSession().getAttribute(Constants.WITH_SESSION_USER);
        String username = user.getAccount();

        dataSourceTypeService.updateDatasourceTypeStatus(0,username,idInt);

        CommonPO commonPO = new CommonPO();
        commonPO.setCode(0);
        return commonPO;
    }


    @RequestMapping(value = "/getDataSourceTypeRelation.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询 获取指定数据源的所有字段",position = 0)
    public List<StorageTypeFieldPO> getDataSourceColumnsList(@RequestParam(value = "datasourceTypeId",required = true) @ApiParam(value = "数据源类型ID",required = true) String dataSourceTypeId,HttpServletRequest request){
        if (Validate.isEmpty(dataSourceTypeId)||!dataSourceTypeId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }
        List<StorageTypeFieldPO> storageTypeFieldPOList = dataSourceTypeService.getDataSourceTypeRelationList(dataSourceTypeId);
        return storageTypeFieldPOList;
    }

    @RequestMapping(value = "/updateDatasource.json",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "外部接口 更新数据源",position = 0)
    public CommonPO updateDatasource(@RequestParam(value = "datasourceId",required = true) @ApiParam(value = "数据源ID",required = true) String datasourceId,
                                     @RequestParam(value = "status",required = true) @ApiParam(value = "状态（1为发布，0为下架）",required = true) String status,
                                                             HttpServletRequest request){
        if (Validate.isEmpty(datasourceId)||!datasourceId.matches("\\d+")){
            throw new WebException(SystemCode.SYS_REQUEST_EXCEPTION);
        }


        CommonPO commonPO = new CommonPO();

        return commonPO;

    }

    @RequestMapping(value = "/getDateSourceListByCrawlType.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据抓取类型查询数据源（用于可视化工作流）",position = 0)
    public List<DatasourcePO> getDateSourceTypeListByCrawlType(@RequestParam(value = "key",required = false) @ApiParam(value = "crawlType(dataCrawl为pc抓取dataMcrawl为移动)",required = false) String key){
        List<DatasourcePO> datasourcePOList = dataSourceTypeService.getDataSourceTypeList(key);
        return datasourcePOList;
    }

    @RequestMapping(value="/getDataSourceTypeByDataSourceId.json",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value="根据数据源id查询数据源类型列表（用于可视化工作流）",position = 0)
    public List<Map<String,Object>> getDataSourceTypeByDataSourceId(@RequestParam(value = "datasourceId") @ApiParam(value = "数据源ID") String datasourceId,
                                                     @RequestParam(value="key",required = false) @ApiParam(value="抓取类型",required = false) String key){

        Map<String,Object> reslutMap = new HashMap<>();
        List<Map<String,Object>> reslutList= new ArrayList<>();
        Long datasourceIdLong = Long.parseLong(datasourceId);
        List<DatasourcePO> datasourcePOList = dataSourceTypeService.getDataSourceTypeList(key);
        String crawlServer = WebUtil.getBaseServerByEnv();

        for(DatasourcePO datasourcePO : datasourcePOList){
            if(datasourceIdLong == datasourcePO.getDatasourceId()){
                List<DatasourceTypePO> datasourceTypePOList = datasourcePO.getDatasourceTypes();
                if(!Validate.isEmpty(datasourceTypePOList)){
                    for(DatasourceTypePO datasourceTypePO : datasourceTypePOList){
                        Map<String,Object> map = new HashMap<>();
                        map.put("datasourceTypeId",datasourceTypePO.getTypeId());
                        map.put("datasourceTypeName",datasourceTypePO.getTypeName());
                        Object resultObj = CallRemoteServiceUtil.callRemoteService(this.getClass().getSimpleName(),crawlServer+"/common/getCrawlInputParamsByDatasourceType.json?datasourceTypeId="+datasourceTypePO.getTypeId(),"get",null);
                        JSONArray jsonArray = new JSONArray();
                        if(null != resultObj) {
                            jsonArray = (JSONArray) resultObj;
                            for(Object object : jsonArray){
                                net.sf.json.JSONObject jsonObject1 = (net.sf.json.JSONObject) object;
                                if(jsonObject1.getString("paramEnName").equals("page")){
                                    JSONObject paramValue = new JSONObject();
                                    paramValue.put("start",1);
                                    paramValue.put("end",1);
                                    jsonObject1.put("paramValue",paramValue);
                                }
                            }
                        }
                        map.put("inpuutParam",jsonArray);
                        reslutList.add(map);
                    }
                }
                break;
            }
        }
        return reslutList;
    }
}
