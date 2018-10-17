package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.biz.service.ContentTypeService;
import com.transing.dpmbs.biz.service.RoleMenuService;
import com.transing.dpmbs.integration.ContentTypeDataService;
import com.transing.dpmbs.integration.RoleMenuDataService;
import com.transing.dpmbs.integration.bo.Menu;
import com.transing.dpmbs.integration.bo.Role;
import com.transing.dpmbs.web.po.ContentTypePO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lanceyan
 * @version 1.0
 */
@Service("roleMenuService")
public class RoleMenuServicePojo extends BaseService implements RoleMenuService {
    @Resource
    private RoleMenuDataService roleMenuDataService;

    @Override
    public List<Menu> getMenuListByUserId(Integer userId) throws BizException {
        List<Menu> menuList = new ArrayList<>();
        List<Integer> roleIdList = roleMenuDataService.getRoleIdListByUserId(userId);
        if(!Validate.isEmpty(roleIdList)){
            List<Role> roleList = roleMenuDataService.getRoleListByIdList(roleIdList);
            if(!Validate.isEmpty(roleList)){
                for (Role role:roleList) {
                    String menuIds = role.getMenuIds();
                    String[] menuIdArray = menuIds.split(",");
                    List<Integer> menuIdList = new ArrayList<>();
                    for (String menmuIdStr:menuIdArray) {
                        if(!menmuIdStr.matches("\\d+"))
                            continue;
                        menuIdList.add(Integer.parseInt(menmuIdStr));
                    }

                    List<Menu> list = roleMenuDataService.getMenuListByListId(menuIdList);
                    menuList.addAll(list);

                }
            }
        }
        return menuList;
    }
}