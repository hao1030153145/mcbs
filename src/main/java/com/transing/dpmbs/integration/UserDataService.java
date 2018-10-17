package com.transing.dpmbs.integration;


import com.jeeframework.logicframework.integration.DataServiceException;
import com.transing.dpmbs.integration.bo.User;

public interface UserDataService {
    /**
     * 简单描述：根据用户名、密码返回用户对象
     * <p>
     *
     * @param user
     * @throws DataServiceException
     */
     User getUsersByPasswd(User user) throws DataServiceException;

     User getBossUserById(long uid) throws DataServiceException;


}
