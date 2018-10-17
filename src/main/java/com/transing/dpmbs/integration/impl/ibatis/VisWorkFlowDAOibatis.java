package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.VisWorkFlowDataService;
import com.transing.dpmbs.integration.bo.TemplateOutputFiledBO;
import com.transing.dpmbs.integration.bo.VisWorkFlowBO;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2018/1/5 0005.
 */
@Repository("visWorkFlowDataService")
public class VisWorkFlowDAOibatis extends BaseDaoiBATIS implements VisWorkFlowDataService {


    @Override
    public List<VisWorkFlowBO> getVisWorkFlow(Integer flowDetailId) {
        return sqlSessionTemplate.selectList("visWorkFlowMapper.getVisWorkFlowListByIdFlowDetailId",flowDetailId);
    }

    @Override
    public int addWorkFlowOutputFiled(VisWorkFlowBO visWorkFlowBO) throws DataServiceException {
        try{
            return sqlSessionTemplate.insert("visWorkFlowMapper.addWorkFlowOutputFiled",visWorkFlowBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void deleteWorkFlowOutputFiledByDetailId(Long flowDetalId) throws DataServiceException {
        sqlSessionTemplate.delete("visWorkFlowMapper.deleteWorkFlowOutputFiledByDetailId",flowDetalId);
    }

    @Override
    public List<TemplateOutputFiledBO> getWorkFlowTemplateOutputFiledList(Integer flowId) {
        return sqlSessionTemplate.selectList("visWorkFlowMapper.getWorkFlowTemplateOutputFiledList",flowId);
    }

    @Override
    public int addWorkFlowTemplateOutputFiled(TemplateOutputFiledBO templateOutputFiledBO) throws DataServiceException {
        try{
            return sqlSessionTemplate.insert("visWorkFlowMapper.addWorkFlowTemplateOutputFiled",templateOutputFiledBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void delWorkFlowTemplateOutputFiled(Long flowId) throws DataServiceException {
        try{
            sqlSessionTemplate.delete("visWorkFlowMapper.delWorkFlowTemplateOutputFiled",flowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }
}
