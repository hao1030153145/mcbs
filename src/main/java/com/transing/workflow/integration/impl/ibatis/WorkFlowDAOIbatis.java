package com.transing.workflow.integration.impl.ibatis;
import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.bo.ParamBO;
import com.transing.dpmbs.integration.bo.WorkFlowListBO;
import com.transing.dpmbs.web.filter.WorkFlowListFilter;
import com.transing.dpmbs.web.po.WorkFlowListPO;
import com.transing.workflow.integration.WorkFlowDataService;
import com.transing.workflow.integration.bo.*;
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
@Repository("workFlowDataService")
public class WorkFlowDAOIbatis extends BaseDaoiBATIS implements WorkFlowDataService
{

    public void updateWorkFlowDetailToInitByWorkFlowDetailId(long workFlowDetailId)
    {
        Map param=new HashMap();
        param.put("workFlowDetailId",""+workFlowDetailId);
        param.put("jobStatus",0);
        param.put("errorMsg","");
        sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailByMap",param);
    }

    //更新工作流状态为运行中
    public void updateWorkFlowDetailToRunningByWorkFlowDetailId(long workFlowDetailId)
    {
        Map param=new HashMap();
        param.put("workFlowDetailId",""+workFlowDetailId);
        param.put("jobStatus",1);
        param.put("errorMsg","");
        sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailByMap",param);
    }


    //更新工作流状态为已完成
    public void updateWorkFlowDetailToComplatedByWorkFlowDetailId(long workFlowDetailId)
    {
        Map param=new HashMap();
        param.put("workFlowDetailId",""+workFlowDetailId);
        param.put("jobStatus",2);
        param.put("errorMsg","");
        sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailByMap",param);
    }

    //更新工作流状态为异常
    public void updateWorkFlowDetailToExceptionByWorkFlowDetailId(long workFlowDetailId,String errorMsg)
    {
        Map param=new HashMap();
        param.put("workFlowDetailId",""+workFlowDetailId);
        param.put("errorMsg",errorMsg);
        param.put("jobStatus",9);
        sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailByMap",param);
    }


    //更新工作流状态为暂停
    public void updateWorkFlowDetailToStopByWorkFlowDetailId(long workFlowDetailId)
    {
        Map param=new HashMap();
        param.put("workFlowDetailId",""+workFlowDetailId);
        param.put("errorMsg","");
        param.put("jobStatus",4);
        sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailByMap",param);
    }

    @Override
        public int updateWorkFlowInfoToStopByFlowId(long workFlowInfoId) {
        return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoToStopByFlowId",workFlowInfoId);
    }


    /**
     * 查询出所有需要处理的工作流信息
     *
     * @param
     * @return
     */
    public  List<WorkFlowInfo> getAllNeedProcessWorkFlowInfo()
    {
        return sqlSessionTemplate.selectList("workFlowMapper.getAllNeedProcessWorkFlowInfo");
    }

    @Override
    public List<WorkFlowInfo> getRunningWorkFlowInfo() {
        return sqlSessionTemplate.selectList("workFlowMapper.getRunningWorkFlowInfo");
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByStatus(Integer jobStatus) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailByStatus",jobStatus);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowInfo getWorkFlowInfoByWorkFlowId(long workFlowId)
    {
        return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowInfoByWorkFlowId",workFlowId);
    }

    @Override
    public WorkFlowDetail getWorkFlowDetailByWorkFlowDetailId(long workFlowDetailId)
    {
        return  sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowDetailByWorkFlowDetailId",workFlowDetailId);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailListByWorkFlowId(long workFlowId)
    {
        return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailListByWorkFlowId",workFlowId);
    }

    @Override
    public int addWorkFlowDetail(WorkFlowDetail workFlowDetail) throws DataServiceException {
        try {
            return sqlSessionTemplate.insert("workFlowMapper.addWorkFlowDetail",workFlowDetail);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int addWorkFlowParam(WorkFlowParam workFlowParam) throws DataServiceException {
        try {
            return sqlSessionTemplate.insert("workFlowMapper.addWorkFlowParam",workFlowParam);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowParam> getWorkFlowParamListByParam(String typeNo,long projectId) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("typeNo",typeNo);
            param.put("projectId",projectId);
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowParamListByParam",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowInfo getWorkFlowInfoByParam(long projectId,String typeNo) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("projectId",projectId);
            param.put("typeNo",typeNo);
            return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowInfoByParam",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int addWorkFlowInfo(WorkFlowInfo workFlowInfo) throws DataServiceException {
        try {
            return sqlSessionTemplate.insert("workFlowMapper.addWorkFlowInfo",workFlowInfo);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfo(WorkFlowInfo workFlowInfo) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfo",workFlowInfo);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowParam(WorkFlowParam workFlowParam) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowParam",workFlowParam);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowParam getWorkFlowParamByParamId(long paramId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowParamByParamId",paramId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int deleteWorkFlowDetailByDetailId(long flowDetailId) throws DataServiceException {
        try {
            return sqlSessionTemplate.delete("workFlowMapper.deleteWorkFlowDetailByDetailId",flowDetailId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int deleteWorkFlowParamByParamId(long paramId) throws DataServiceException {
        try {
            return sqlSessionTemplate.delete("workFlowMapper.deleteWorkFlowParamByParamId",paramId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowParam> getWorkFlowParamListByTypeNoList(List<String> typeNoList, long projectId) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("list",typeNoList);
            param.put("projectId",projectId);
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowParamListByTypeNoList",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailListByTypeNoList(List<String> typeNoList, long projectId) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("list",typeNoList);
            param.put("projectId",projectId);
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailListByTypeNoList",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailListByTypeNoListAndWorkFlowId(List<String> typeNoList, long workFlowId) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("list",typeNoList);
            param.put("workFlowId",workFlowId);
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailListByTypeNoListAndWorkFlowId",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowParam getWorkFlowParamByDetailId(long flowDetailId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowParamByDetailId",flowDetailId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailToStart(long flowDetailId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailToStart",flowDetailId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailToFinish(long flowDetailId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailToFinish",flowDetailId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfoToStart(long flowId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoToStart",flowId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfoToInitByFlowId(long flowId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoToInitByFlowId",flowId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfoComNum(long flowId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoComNum",flowId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfoToFinishIfComNum(long flowId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoToFinishIfComNum",flowId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfoStatusExceptionByFlowId(long flowId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoStatusExceptionByFlowId",flowId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailExceptionByMap(String errorMsg, long workFlowDetailId) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("errorMsg",errorMsg);
            param.put("workFlowDetailId",workFlowDetailId);
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailByMap",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfoToRunningByProjectId(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoToRunningByProjectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfoToStopByProjectId(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoToStopByProjectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailToStopByProjectId(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailToStopByProjectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailToStartByProjectId(long projectId,List<Long> detailIdList) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("projectId",projectId);
            param.put("list",detailIdList);
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailToStartByProjectId",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailToStartByWorkFlowId(long workFlowId, List<Long> detailIdList) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("workFlowId",workFlowId);
            param.put("list",detailIdList);
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailToStartByWorkFlowId",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailQuartzTime(WorkFlowDetail workFlowDetail) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailQuartzTime",workFlowDetail);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowInfo> getWorkFlowInfoByProjectId(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowInfoByProjectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowInfo getWorkFlowInfoByProjectIdANdTypeNo(long projectId, String typeNo) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("projectId",projectId);
            param.put("typeNo",typeNo);
            return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowInfoByProjectIdANdTypeNo",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailProgress(long flowDetailId, int jobProgress) throws DataServiceException {
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("flowDetailId",flowDetailId);
            map.put("jobProgress",jobProgress);

            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailProgress",map);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowInfoToRunningIfFinishByProjectId(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowInfoToRunningIfFinishByProjectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetailToRunningIfFinishByProjectId(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailToRunningIfFinishByProjectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public int updateWorkFlowDetail(WorkFlowDetail workFlowDetail) throws DataServiceException {
        try {
            return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetail",workFlowDetail);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowDetail> getFirstDetailByProjectId(long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("workFlowMapper.getFirstDetailByProjectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowDetail> getExecDetailListByTypeNo(String typeNo,long projectId) throws DataServiceException {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("typeNo",typeNo);
            param.put("projectId",projectId);
            return sqlSessionTemplate.selectList("workFlowMapper.getExecDetailListByTypeNo",param);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowParam> getWorkFlowParamByFlowParam(WorkFlowParam workFlowParam) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowParamByFlowParam",workFlowParam);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowParam> getWorkFlowParamByProJectId(Long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowParamByProJectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByProjectId(Long projectId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailByProjectId",projectId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowNodeBO> getWorkFlowNodeByTemplateId(int templateId) throws DataServiceException {
        try {
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowNodeByTemplateId",templateId);
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowNodeBO getWorkFlowNodeByFlowId(Long flowId) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowNodeByFlowId",flowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowParam getWorkFlowParamByFlowId(Long flowId,Long projectId) throws DataServiceException {
        Map<String,Object> map = new HashMap<>();
        map.put("flowId",flowId);
        map.put("projectId",projectId);
        try{
            return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowParamByFlowId",map);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowTemplateNodeParamBo> getWorkFlowTemplateNodeParamBoListByMap(Map map) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowTemplateNodeParamBoListByMap",map);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public int addWorkFlowNodeParam(WorkFlowNodeParamBo workFlowNodeParamBo) throws DataServiceException {
        try{
            return sqlSessionTemplate.insert("workFlowMapper.addWorkFlowNodeParam",workFlowNodeParamBo);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<JobTypeCategoryBO> getJobTypeCategoryBOList() throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getJobTypeCategoryBOList");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<JobTypeInfo> getJobTypeInfo() throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getJobTypeInfo");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowInputParamBo> getWorkFlowInputParamBoList() throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowInputParamBoList");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<StyleBO> getStyleBOList() throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getStyleBOList");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowTemplateNodeParamBo> getTemplateNodeParamByTemplateFlowId(long templateFlowId) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getTemplateNodeParamByTemplateFlowId",templateFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowNodeParamBo> getWorkFlowNodeParamByFlowDetailId(long workFlowDetailId) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowNodeParamByFlowDetailId",workFlowDetailId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }


    @Override
    public List<WorkFlowNodeParamBo> getWorkFlowNodeParamByProjectId(long projectId) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowNodeParamByProjectId",projectId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void updateWorkFlowNodeParam(WorkFlowNodeParamBo workFlowNodeParamBo) throws DataServiceException {
        try{
            sqlSessionTemplate.update("workFlowMapper.updateWorkFlowNodeParam",workFlowNodeParamBo);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowNodeParamBo> getWrokFlowNodeParamListByMap(Long projectId, String typeNo) throws DataServiceException {
        Map<String,Object> map = new HashMap<>();
        map.put("projectId",projectId);
        map.put("typeNo",typeNo);
        try{
           return sqlSessionTemplate.selectList("workFlowMapper.getWrokFlowNodeParamListByMap",map);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void deleteWorkFlowNodeParamByFlowDetailId(Long flowDetailId) throws DataServiceException {
        try{
            sqlSessionTemplate.selectList("workFlowMapper.deleteWorkFlowNodeParamByFlowDetailId",flowDetailId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void deleteWorkFlowOutputFiledByFlowDetailId(Long flowDetailId) throws DataServiceException {
        try{
            sqlSessionTemplate.selectList("workFlowMapper.deleteWorkFlowOutputFiledByFlowDetailId",flowDetailId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowInputParamRelationBO> getWorkFlowInputParamRelationBO() throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowInputParamRelationBO");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowListBO> getWorkFlowListPOByFilter(WorkFlowListFilter workFlowListFilter) throws BizException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowListPOByFilter",workFlowListFilter);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowListBO> getWorkFlowListByIncludeStatus(List<String> conditions,Long projectId) throws BizException {
        Map<String,Object> map = new HashMap<>();
        map.put("projectId",projectId);
        map.put("condition",conditions);
        try {
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowListByIncludeStatus",map);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void addWorkFlowListBO(WorkFlowListBO workFlowListBO) throws BizException {
        try{
            sqlSessionTemplate.insert("workFlowMapper.addWorkFlowListBO",workFlowListBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByWorkFlowId(Long workFlowId) throws BizException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailByWorkFlowId",workFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByWorkFlowIdAndProjectId(Long workFlowId, Long projectId) throws DataServiceException {
        try{
            Map<String,Long> map = new HashMap<>();
            map.put("workFlowId",workFlowId);
            map.put("projectId",projectId);
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailByWorkFlowIdAndProjectId",map);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowNodeParamBo> getWorkFlowNodeParamByWorkFlowId(Long workFlowId) throws BizException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowNodeParamByWorkFlowId",workFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public Long getWorkFlowListBOByWorkFlowName(Long projectId,String workFlowName) throws BizException {
        try{
            Map<String,Object> map = new HashMap();
            map.put("projectId",projectId);
            map.put("workFlowName",workFlowName);
            return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowListBOByWorkFlowName",map);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void delWorkFlowDetailByWorkFlowId(Long workFlowId) throws BizException {
        try{
            sqlSessionTemplate.delete("workFlowMapper.delWorkFlowDetailByWorkFlowId",workFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void delWorkFlowNodeParamByWorkFlowId(Long workFlowId) throws BizException {
        try{
            sqlSessionTemplate.delete("workFlowMapper.delWorkFlowNodeParamByWorkFlowId",workFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void delWorkFlowListByWorkFlowId(Long workFlowId) throws BizException {
        try{
            sqlSessionTemplate.delete("workFlowMapper.delWorkFlowListByWorkFlowId",workFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void deleteWorkFlowOutputFiledByFlowDetailIdList(List<WorkFlowDetail> workFlowDetails) throws BizException {
        try{
            sqlSessionTemplate.delete("workFlowMapper.deleteWorkFlowOutputFiledByFlowDetailIdList",workFlowDetails);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<ParamBO> getDownloadCoding() throws BizException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getDownloadCoding");
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void updateWorkFlowListStatus(WorkFlowListBO workFlowListBO) throws BizException {
        try{
            sqlSessionTemplate.update("workFlowMapper.updateWorkFlowListStatus",workFlowListBO);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public WorkFlowListBO getWorkFlowListBOByWorkFlowId(Long workFlowId) throws BizException {
        try{
           return sqlSessionTemplate.selectOne("workFlowMapper.getWorkFlowListBOByWorkFlowId",workFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<WorkFlowDetail> getFirstDetailByWorkFlowId(Long workFlowId) throws BizException {
        try{
            return sqlSessionTemplate.selectList("workFlowMapper.getFirstDetailByWorkFlowId",workFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void updateWorkFlowDetailToStopByWorkFlowId(Long workFlowId) throws BizException {
        try{
            sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailToStopByWorkFlowId",workFlowId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public long workFlowListCount(Long projectId,Integer status) throws BizException {
        try{
            Map<String,Object> map = new HashMap();
            map.put("projectId",projectId);
            map.put("status",status);
            return sqlSessionTemplate.selectOne("workFlowMapper.workFlowListCount",map);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }

    }
    @Override
    public void updateWorkFlowDetailByWorkFlowId(Long workFlowId) throws BizException {
        sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailByWorkFlowId",workFlowId);
    }

    @Override
    public int updateWorkFlowDetailToPreRunFinish(long flowDetailId) {
       return sqlSessionTemplate.update("workFlowMapper.updateWorkFlowDetailToPreRunFinish",flowDetailId);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailListByWorkFlowIdAndStatus(Long workFlowId) throws BizException {
        return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailListByWorkFlowIdAndStatus",workFlowId);
    }

    @Override
    public List<WorkFlowDetail> getWorkFlowDetailByWorkFlowIdAndProjectIds(Long workFlowId, Long projectId) {
        try{
            Map<String,Long> map = new HashMap<>();
            map.put("workFlowId",workFlowId);
            map.put("projectId",projectId);
            return sqlSessionTemplate.selectList("workFlowMapper.getWorkFlowDetailByWorkFlowIdAndProjectIds",map);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }
}