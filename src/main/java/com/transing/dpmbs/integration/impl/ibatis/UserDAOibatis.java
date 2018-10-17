package com.transing.dpmbs.integration.impl.ibatis;

import com.jeeframework.logicframework.integration.DataServiceException;
import com.jeeframework.logicframework.integration.dao.DAOException;
import com.jeeframework.logicframework.integration.dao.ibatis.BaseDaoiBATIS;
import com.transing.dpmbs.integration.UserDataService;
import com.transing.dpmbs.integration.bo.User;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Scope("prototype")
@Repository("usersDataService")
public class UserDAOibatis extends BaseDaoiBATIS implements UserDataService {
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public User getUsersByPasswd(User userParam) throws DataServiceException {
        try {
            User user = sqlSessionTemplate.selectOne("userMapper.getUsersByPasswd", userParam);
            return user;
        } catch (DataAccessException e) {
            throw new DAOException("根据密码查询用户信息失败", e);
        }
    }

    @Override
    public User getBossUserById(long uid) throws DataServiceException {
        try {
            User user = sqlSessionTemplate.selectOne("userMapper.getUsersById", uid);
            return user;
        } catch (DataAccessException e) {
            throw new DAOException(e);
        }
    }
}
