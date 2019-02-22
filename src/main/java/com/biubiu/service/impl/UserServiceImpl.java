package com.biubiu.service.impl;

import com.biubiu.annotation.Qualifier;
import com.biubiu.annotation.Service;
import com.biubiu.dao.UserDao;
import com.biubiu.service.UserService;

/**
 * @author 张海彪
 * @create 2019-02-22 18:23
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Qualifier("userDaoImpl")
    private UserDao userDao;

    @Override
    public void insert() {
        userDao.insert();
    }
}
