package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.DataService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.ParamBO;

import java.util.List;


public interface ParamDataService extends DataService {

    /**
     * 根据typeList 查询 key value 的List
     * @param typeList
     * @return
     * @throws DataServiceException
     */
    public List<ParamBO> getKeyValueListByType(List<String> typeList) throws DataServiceException;
    /**
     * 根据type查询
     */
    List<ParamBO> getParamBoListByType(String type)throws BizException;

    /**
     * 查询所有type
     * @param
     * @return
     * @throws BizException
     */
    List<ParamBO> getParamBOList()throws BizException;




}