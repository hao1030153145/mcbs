package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.ParamDataService;
import com.transing.dpmbs.integration.bo.ParamBO;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问对象
 *
 * @author summer
 * @version 1.0
 */
@Scope("prototype")
@Repository("paramDataService")
public class ParamDAOIbatis extends BaseDaoiBATIS implements ParamDataService {

    @Override
    public List<ParamBO> getKeyValueListByType(List<String> typeList) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("param.getKeyValueListByType",typeList);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<ParamBO> getParamBoListByType(String type) throws BizException {
        try{
            return sqlSessionTemplate.selectList("param.getParamBoListByType",type);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<ParamBO> getParamBOList() throws BizException {
        try{
            return sqlSessionTemplate.selectList("param.getParamBoList");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }
}