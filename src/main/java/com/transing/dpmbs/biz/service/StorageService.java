package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.transing.dpmbs.integration.bo.StorageBO;
import com.transing.dpmbs.web.filter.StorageFilter;

import java.util.List;

/**
 * Created by byron on 2018/3/15 0015.
 */
public interface StorageService {
    /**
     * 根据filter查询所有储存 dpm1.5.1
     * @param storageFilter
     * @return
     */
    List<StorageBO> getStorageBOByFilter(StorageFilter storageFilter) throws BizException;

    /**
     * 根据存储名称查询数量 dpm1.5.1
      * @return
     */
    Long getCountByStorageName(String storageName) throws BizException;

    /**
     * 添加存储 dpm1.5.1
     * @param storageBO
     * @throws BizException
     */
    void addStorage(StorageBO storageBO)throws BizException;

    /**
     * 根据id查询storage
     * @param id
     * @return
     * @throws BizException
     */
    StorageBO getStorageById(Long id)throws BizException;

    /**
     * 更新存储 dpm1.5.1
     * @param storageBO
     * @throws BizException
     */
    void updateStorage(StorageBO storageBO)throws BizException;

    /**
     * 删除存储 dpm1.5.1
     * @param storageId
     * @throws BizException
     */
    void delStorageById(Long storageId) throws BizException;

    /**
     * 查询所有的存储 用于推送节点 dpm1.5.1
     * @return
     * @throws BizException
     */
    List<StorageBO> getAllStorage() throws BizException;

}
