package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BizService;
import com.transing.dpmbs.integration.bo.ParamBO;

import java.util.List;

/**
 *
 * @author lanceyan
 * @version 1.0
 */
public interface ParamService extends BizService {

    /**
     * 根据typeLit 查询 key value 的list
     * @param typeList
     * @return
     * @throws BizException
     */
    public List<ParamBO> getKeyValueListByType(List<String> typeList)throws BizException;

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