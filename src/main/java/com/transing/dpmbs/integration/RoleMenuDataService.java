package com.transing.dpmbs.integration;

import com.jeeframework.logicframework.integration.DataService;
import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.Menu;
import com.transing.dpmbs.integration.bo.Role;
import com.transing.dpmbs.web.po.ContentTypePO;

import java.util.List;

/**
 * 用户数据操作接口
 *
 * @author lanceyan
 * @version 1.0
 * @see
 */
public interface RoleMenuDataService extends DataService {

    /**
     * 通过菜单idList查询 菜单list
     * @param menuIdList
     * @return
     * @throws DataServiceException
     */
    List<Menu> getMenuListByListId(List<Integer> menuIdList)throws DataServiceException;

    /**
     * 通过userId查询 RoleIdList
     */
    List<Integer> getRoleIdListByUserId(Integer userId)throws DataServiceException;

    /**
     * 通过角色idlist查询角色list
     * @param idList
     * @return
     * @throws DataServiceException
     */
    List<Role> getRoleListByIdList(List<Integer> idList)throws DataServiceException;
}