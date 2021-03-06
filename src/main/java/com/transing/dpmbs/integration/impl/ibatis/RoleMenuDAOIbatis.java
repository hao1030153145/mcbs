package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.ContentTypeDataService;
import com.transing.dpmbs.integration.RoleMenuDataService;
import com.transing.dpmbs.integration.bo.Menu;
import com.transing.dpmbs.integration.bo.Role;
import com.transing.dpmbs.web.po.ContentTypePO;
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
@Repository("roleMenuDataService")
public class RoleMenuDAOIbatis extends BaseDaoiBATIS implements RoleMenuDataService {

    @Override
    public List<Menu> getMenuListByListId(List<Integer> menuIdList) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("userRoleMenuMapper.getMenuListByListId",menuIdList);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<Integer> getRoleIdListByUserId(Integer userId) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("userRoleMenuMapper.getRoleIdListByUserId",userId);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }

    @Override
    public List<Role> getRoleListByIdList(List<Integer> idList) throws DataServiceException {
        try{
            return sqlSessionTemplate.selectList("userRoleMenuMapper.getRoleListByIdList",idList);
        }catch (DataAccessException e){
            throw new DAOException(e);
        }
    }
}