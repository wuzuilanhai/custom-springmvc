package com.biubiu.dao.impl;

import com.biubiu.annotation.Repository;
import com.biubiu.dao.UserDao;

/**
 * @author 张海彪
 * @create 2019-02-22 18:25
 */
@Repository("userDaoImpl")
public class UserDaoImpl implements UserDao {

    @Override
    public void insert() {
        System.out.println("execute UserDaoImpl.insert()...");
    }

}
