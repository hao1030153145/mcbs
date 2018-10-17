package com.transing.dpmbs.biz.service;

import com.jeeframework.logicframework.biz.exception.BizException;
import com.transing.dpmbs.integration.bo.User;


public interface UserService {
    public User getUser(User user) throws BizException;

    public User getUserByid(long uid) throws BizException;
}
