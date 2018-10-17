package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.transing.dpmbs.biz.service.StorageService;
import com.transing.dpmbs.integration.StorageDataService;
import com.transing.dpmbs.integration.bo.StorageBO;
import com.transing.dpmbs.web.filter.StorageFilter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by byron on 2018/3/15 0015.
 */
@Service("storageService")
public class StorageServicePojo implements StorageService{
    @Resource
    private StorageDataService storageDataService;
    @Override
    public List<StorageBO> getStorageBOByFilter(StorageFilter storageFilter) throws BizException{
        return storageDataService.getStorageBOByFilter(storageFilter);
    }

    @Override
    public Long getCountByStorageName(String storageName) throws BizException {
        return storageDataService.getCountByStorageName(storageName);
    }

    @Override
    public void addStorage(StorageBO storageBO) throws BizException {
        storageDataService.addStorage(storageBO);
    }

    @Override
    public StorageBO getStorageById(Long id) throws BizException {
        return storageDataService.getStorageById(id);
    }

    @Override
    public void updateStorage(StorageBO storageBO) throws BizException {
        storageDataService.updateStorage(storageBO);
    }

    @Override
    public void delStorageById(Long storageId) throws BizException {
        storageDataService.delStorageById(storageId);
    }

    @Override
    public List<StorageBO> getAllStorage() throws BizException {
        return storageDataService.getAllStorage();
    }
}
