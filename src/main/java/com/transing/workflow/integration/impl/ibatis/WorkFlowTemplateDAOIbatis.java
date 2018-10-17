package com.transing.workflow.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.web.po.WorkFlowTemplatePO;
import com.transing.workflow.integration.WorkFlowTemplateDataService;
import com.transing.workflow.integration.bo.WorkFlowNodeBO;
import com.transing.workflow.integration.bo.WorkFlowTemplateBO;
import com.transing.workflow.integration.bo.WorkFlowTemplateNodeParamBo;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WorkFlow数据访问对象
 *
 * @author Sunny
 * @version 1.0
 */
@Scope("prototype")
@Repository("workFlowTemplateDataService")
public class WorkFlowTemplateDAOIbatis extends BaseDaoiBATIS implements WorkFlowTemplateDataService
{

    @Override
    public WorkFlowTemplateBO getWorkFlowTemplateListById(int id) throws DataServiceException {
        return sqlSessionTemplate.selectOne("workFlowTemplateMapper.getWorkFlowTemplateListById",id);
    }

    @Override
    public List<WorkFlowTemplateBO> getWorkFlowTemplateListByParam(Integer status,String name,Integer page,Integer size) throws DataServiceException {
        Map<String,Object> param = new HashMap<>();

        if(null != status){
            param.put("status",status);
        }
        if(null != page){
            param.put("page",page);
        }
        if(null != size){
            param.put("size",size);
        }

        if(!Validate.isEmpty(name)){
            param.put("name",name);
        }


        return sqlSessionTemplate.selectList("workFlowTemplateMapper.getWorkFlowTemplateListByParam",param);
    }

    @Override
    public List<WorkFlowTemplateBO> getVisWorkFlowTemplateListByParam(Integer status, String name, Integer page, Integer size, String createTime, String endTime) throws DataServiceException {

        Map<String,Object> param = new HashMap<>();

        if (null != status){
            param.put("status",status);
        }

        if (!Validate.isEmpty(name)){
            param.put("name",name);
        }

        if (!Validate.isEmpty(createTime)){
            param.put("createTime",createTime);
        }

        if (!Validate.isEmpty(endTime)){
            param.put("endTime",endTime);
        }

        if (null != size){
            param.put("size",size);
        }

        if (null != page){
            param.put("page",page);
        }

        return sqlSessionTemplate.selectList("workFlowTemplateMapper.getVisWorkFlowTemplateListByParam",param);

    }

    /**
     * 根据id逻辑删除模板
     * @param list
     * @return
     * @throws DataServiceException
     */
    @Override
    public int logicDeleteVisWorkFlowTemplateByIds(List<Integer> list) throws DataServiceException {
        return sqlSessionTemplate.update("workFlowTemplateMapper.logicDeleteVisWorkFlowTemplateByIds",list);
    }

    /**
     *根据查询条件获得未被逻辑删除的模板数据
     * @param param
     * @return
     * @throws DataServiceException
     */
    @Override
    public List<WorkFlowTemplatePO> getWorkFlowTemplateListByCondition(Map<String, Object> param) throws DataServiceException {
        return sqlSessionTemplate.selectList("workFlowTemplateMapper.getWorkFlowTemplateListByCondition",param);
    }

    /**
     * 根据查询条件获得模板的数目
     * @param param
     * @return
     * @throws DataServiceException
     */
    @Override
    public int getWorkFlowTemplateCountByCondition(Map<String, Object> param) throws DataServiceException {
        return sqlSessionTemplate.selectOne("workFlowTemplateMapper.getWorkFlowTemplateCountByCondition",param);
    }

    @Override
    public Integer getStatusByDetailId(Integer detailId) throws DataServiceException {
        return sqlSessionTemplate.selectOne("workFlowMapper.getStatusByDetailId",detailId);
    }

    @Override
    public String getTypeNoByDetailId(Integer flowDetailId) throws DataServiceException {
        return sqlSessionTemplate.selectOne("workFlowMapper.getTypeNoByDetailId",flowDetailId);
    }

    @Override
    public Integer getWorkFlowTemplateCountByParam(Integer status,String name) throws DataServiceException {
        Map<String,Object> param = new HashMap<>();

        if(null != status){
            param.put("status",status);
        }

        if(!Validate.isEmpty(name)){
            param.put("name",name);
        }

        return sqlSessionTemplate.selectOne("workFlowTemplateMapper.getWorkFlowTemplateCountByParam",param);
    }

    @Override
    public Integer getVisWorkFlowTemplateCountByParam(Integer status, String name,String createTime,String endTime) throws DataServiceException {

        Map<String,Object> param = new HashMap<>();
        if (null != status){
            param.put("status",status);
        }
        if (!Validate.isEmpty(name)){
            param.put("name",name);
        }
        if (!Validate.isEmpty(createTime)){
            param.put("createTime",createTime);
        }
        if (!Validate.isEmpty(endTime)){
            param.put("endTime",endTime);
        }

        return  sqlSessionTemplate.selectOne("workFlowTemplateMapper.getVisWorkFlowTemplateCountByParam",param);
    }

    @Override
    public int addWorkFlowTemplate(WorkFlowTemplateBO workFlowTemplateBO) throws DataServiceException {
        return sqlSessionTemplate.insert("workFlowTemplateMapper.addWorkFlowTemplate",workFlowTemplateBO);
    }

    @Override
    public List<String> getVisTemplateNameList() throws DataServiceException {
        return sqlSessionTemplate.selectList("workFlowTemplateMapper.getVisTemplateNameList");
    }

    @Override
    public List<WorkFlowNodeBO> getWorkFlowNodeListByTemplateId(int templateId) throws DataServiceException {
        return sqlSessionTemplate.selectList("workFlowTemplateMapper.getWorkFlowNodeListByTemplateId",templateId);
    }

    @Override
    public int addWorkFlowNode(WorkFlowNodeBO workFlowNodeBO) throws DataServiceException {
        return sqlSessionTemplate.insert("workFlowTemplateMapper.addWorkFlowNode",workFlowNodeBO);
    }

    @Override
    public int updateWorkFlowNode(WorkFlowNodeBO workFlowNodeBO) throws DataServiceException {
        return sqlSessionTemplate.update("workFlowTemplateMapper.updateWorkFlowNode",workFlowNodeBO);
    }

    @Override
    public int updateWorkTemplate(WorkFlowTemplateBO workFlowTemplateBO) throws DataServiceException {
        return sqlSessionTemplate.update("workFlowTemplateMapper.updateWorkTemplate",workFlowTemplateBO);
    }

    @Override
    public int getVisWorkTemplateIsExistingByName(String name) throws DataServiceException {
        return sqlSessionTemplate.selectOne("workFlowTemplateMapper.getVisWorkFlowTemplateIsExistingByName",name);
    }

    @Override
    public int deleteWorkFlowNodeByTemplateId(int templateId) throws DataServiceException {
        return sqlSessionTemplate.delete("workFlowTemplateMapper.deleteWorkFlowNodeByTemplateId",templateId);
    }

    @Override
    public int deleteWorkTemplateById(int id) throws DataServiceException {
        return sqlSessionTemplate.delete("workFlowTemplateMapper.deleteWorkTemplateById",id);
    }

    @Override
    public void deleteWorkFlowNodeByFlowId(Long flowId) throws DataServiceException {
        sqlSessionTemplate.delete("workFlowTemplateMapper.deleteWorkFlowNodeByFlowId",flowId);
    }

    @Override
    public void deleteWorkFlowTemplateNodeParamByFlowId(Long flowId) throws DataServiceException {
        sqlSessionTemplate.delete("workFlowTemplateMapper.deleteWorkFlowTemplateNodeParamByFlowId",flowId);
    }

    @Override
    public void updateWorkFlowTemplateNodeParam(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo) throws DataServiceException {
        sqlSessionTemplate.update("workFlowTemplateMapper.updateWorkFlowTemplateNodeParam",workFlowTemplateNodeParamBo);
    }

    @Override
    public void addWorkFlowTemplateNodeParam(WorkFlowTemplateNodeParamBo workFlowTemplateNodeParamBo) throws DataServiceException {
        sqlSessionTemplate.insert("workFlowTemplateMapper.addWorkFlowTemplateNodeParam",workFlowTemplateNodeParamBo);
    }

    @Override
    public List<WorkFlowTemplateNodeParamBo> getWorkFlowTemplateNodeParamByFlowId(Long flowId) throws DataServiceException {
        return sqlSessionTemplate.selectList("workFlowTemplateMapper.getWorkFlowTemplateNodeParamByFlowId"
                ,flowId);
    }

    @Override
    public List<WorkFlowNodeBO> getWorkFlowNodeListByTypeNoList(List<String> typeNoList, Long templateId) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("list",typeNoList);
            param.put("templateId",templateId);
            return sqlSessionTemplate.selectList("workFlowTemplateMapper.getWorkFlowNodeListByTypeNoList",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowNodeBO getWorkFlowDetailByFlowId(long preFlowIdIds) throws DataServiceException {
        return sqlSessionTemplate.selectOne("workFlowTemplateMapper.getWorkFlowDetailByFlowId",preFlowIdIds);
    }
}