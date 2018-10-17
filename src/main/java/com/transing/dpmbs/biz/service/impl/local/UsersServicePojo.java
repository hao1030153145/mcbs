package com.transing.dpmbs.biz.service.impl.local;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.jeeframework.logicframework.biz.service.BaseService;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.transing.dpmbs.biz.service.UserService;
import com.transing.dpmbs.integration.UserDataService;
import com.transing.dpmbs.integration.bo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("usersService")
public class UsersServicePojo extends BaseService implements UserService {
    @Resource
    private UserDataService userDataService;


    @Override
    public User getUser(User user) throws BizException {
        try {
            return userDataService.getUsersByPasswd(user);
        } catch (DAOException e) {
            LoggerUtil.errorTrace("getUsersByPasswd", "根据用户名密码查询用户出错", e);
            throw new BizException("根据用户名密码查询用户数据库出错" + e, e);
        }
    }

    @Override
    public User getUserByid(long uid) throws BizException {
        try {
            return userDataService.getBossUserById(uid);
        } catch (DAOException e) {
            throw new BizException(e);
        }
    }
}
