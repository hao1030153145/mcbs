package com.transing.dpmbs.integration.impl.ibatis;

/**
 * Created by byron on 2018/3/15 0015.
 */

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.StorageDataService;
import com.transing.dpmbs.integration.bo.StorageBO;
import com.transing.dpmbs.web.filter.StorageFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
@Scope("prototype")
@Repository("storageDataService")
public class StorageDAOIbatis extends BaseDaoiBATIS implements StorageDataService {
    @Override
    public List<StorageBO> getStorageBOByFilter(StorageFilter storageFilter) {
        try{
            return sqlSessionTemplate.selectList("storageMapper.getStorageBOByFilter",storageFilter);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public Long getCountByStorageName(String storageName) {
        try{
            return sqlSessionTemplate.selectOne("storageMapper.getCountByStorageName",storageName);
        }catch (DAOException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void addStorage(StorageBO storageBO) {
        try{
            sqlSessionTemplate.insert("storageMapper.addStorage",storageBO);
        }catch (DAOException e){
            throw new DAOException(e);
        }
    }

    @Override
    public StorageBO getStorageById(Long id) throws BizException {
        try{
            return sqlSessionTemplate.selectOne("storageMapper.getStorageById",id);
        }catch (DAOException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void updateStorage(StorageBO storageBO) throws BizException {
        try{
            sqlSessionTemplate.update("storageMapper.updateStorage",storageBO);
        }catch (DAOException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void delStorageById(Long storageId) throws BizException {
        try{
            sqlSessionTemplate.delete("storageMapper.delStorageById",storageId);
        }catch (DAOException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<StorageBO> getAllStorage() throws BizException {
        return sqlSessionTemplate.selectList("storageMapper.getAllStorage");
    }
}
