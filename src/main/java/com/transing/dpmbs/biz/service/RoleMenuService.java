package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BizService;
import com.transing.dpmbs.integration.bo.Menu;
import com.transing.dpmbs.web.po.ContentTypePO;

import java.util.List;

/**
 * @author lanceyan
 * @version 1.0
 */
public interface RoleMenuService extends BizService {

    /**
     * 通过userId查询 菜单list
     * @param userId
     * @return
     * @throws BizException
     */
    List<Menu> getMenuListByUserId(Integer userId)throws BizException;
}