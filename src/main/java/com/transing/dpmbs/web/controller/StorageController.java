package com.transing.dpmbs.web.controller;

import com.jeeframework.util.validate.Validate;
import com.jeeframework.webframework.exception.WebException;
import com.transing.dpmbs.biz.service.StorageService;
import com.transing.dpmbs.integration.bo.StorageBO;
import com.transing.dpmbs.web.exception.MySystemCode;
import com.transing.dpmbs.web.filter.StorageFilter;
import com.transing.dpmbs.web.po.CommonPageListPO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/15.
 */
@Controller("storageController")
@Api(value = "存储管理", description = "存储管理相关接口", position = 2)
@RequestMapping("/storage")
public class StorageController {
    @Resource
    private StorageService storageService;

    @RequestMapping(value = "/toStorageList.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "跳转至存储列表页面", position = 0)
    public ModelAndView toStorageList(){
        return new ModelAndView("newProject/storage/storageList");
    }

    @RequestMapping(value = "/getStorageList.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "查询存储列表", position = 0)
    public CommonPageListPO getStorageList(@RequestParam(value = "storageName",required = false) @ApiParam("存储名称") String storageName,
                                       @RequestParam(value = "storageType",required = false) @ApiParam("存储类型") String storageType,
                                       @RequestParam(value = "page",required = false,defaultValue = "1") @ApiParam("页码") Integer page,
                                       @RequestParam(value = "pageSize",required = false,defaultValue = "10") @ApiParam("条数") Integer pageSize,
                                       @RequestParam(value = "sort",required = false) @ApiParam("排序字段及排序方式") String sort){
        CommonPageListPO commonPageListPO = new CommonPageListPO();
        StorageFilter storageFilter = new StorageFilter();
        storageFilter.setStorageName(storageName);
        storageFilter.setPage((page-1)*10);
        storageFilter.setPageSize(pageSize);
        storageFilter.setStorageType(storageType);
        storageFilter.setSort(sort);
        List<StorageBO> storageBOList = storageService.getStorageBOByFilter(storageFilter);
        commonPageListPO.setDataList(storageBOList);
        commonPageListPO.setCount(storageBOList.size());
        return commonPageListPO;
    }

    @RequestMapping(value = "/toCreateStorage.html", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "跳转至存储创建页面", position = 0)
    public ModelAndView toCreateStorage(@RequestParam(value = "storageId",required = false) @ApiParam("存储id") String storageId,
                                        HttpServletRequest request, HttpServletResponse response){
        if(!Validate.isEmpty(storageId)){
            Map result = new HashMap();
            result.put("storageId",storageId);
            request.setAttribute("resultMap",result);
        }
        return new ModelAndView("newProject/storage/createStorage");
    }

    @RequestMapping(value = "/addStorage.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "创建（编辑）存储", position = 0)
    public Map<String,Object> addStorage(@RequestParam(value = "id",required = false) @ApiParam("存储id") String id,
                           @RequestParam(value = "storageName") @ApiParam("存储名称") String storageName,
                           @RequestParam(value = "storageType") @ApiParam("存储类型") String storageType,
                           @RequestParam(value = "keyId") @ApiParam("密钥id") String keyId,
                           @RequestParam(value = "password") @ApiParam("密匙") String password,
                           @RequestParam(value = "storageAddress") @ApiParam("存储位置") String storageAddress,
                           @RequestParam(value = "path") @ApiParam("访问路径") String path){
        Map<String,Object> resultMap = new HashMap<>();
        StorageBO storageBO = new StorageBO();
        //首先检验其名称是否存在
        if(storageService.getCountByStorageName(storageName)>0 && id==null){
            throw new WebException(MySystemCode.BIZ_CREATE_PROJECT);
        }
        storageBO.setStorageName(storageName);
        storageBO.setStorageAddress(storageAddress);
        storageBO.setStorageType(storageType);
        storageBO.setKeyId(keyId);
        storageBO.setPassword(password);
        storageBO.setPath(path);
        if(id != null){//如果id不为空则表示更新
            storageBO.setId(Long.parseLong(id));
            storageService.updateStorage(storageBO);
            resultMap.put("message", "更新成功");
            return resultMap;
        }else{
            storageService.addStorage(storageBO);
            resultMap.put("message", "保存成功");
            return resultMap;
        }
    }

    @RequestMapping(value = "/getStorage.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询存储回显", position = 0)
    public StorageBO getStorage(@RequestParam(value = "storageId") @ApiParam("存储id") Long storageId){
        StorageBO storageBO = storageService.getStorageById(storageId);
        return storageBO;
    }

    @RequestMapping(value = "/delStorage.json", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "删除存储", position = 0)
    public void delStorage(@RequestParam(value = "storageId") @ApiParam("存储id") Long storageId){
        storageService.delStorageById(storageId);
    }

    @RequestMapping(value = "/getAllStorage.json", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询所有的存储", position = 0)
    public List<StorageBO> getAllStorage(){
        List<StorageBO> list = storageService.getAllStorage();
        return list;
    }
}
