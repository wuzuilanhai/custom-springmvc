package com.biubiu.controller;

import com.biubiu.annotation.Controller;
import com.biubiu.annotation.Qualifier;
import com.biubiu.annotation.RequestMapping;
import com.biubiu.service.UserService;

/**
 * @author 张海彪
 * @create 2019-02-22 18:21
 */
@Controller("userController")
@RequestMapping("/rest")
public class UserController {

    @Qualifier("userServiceImpl")
    private UserService userService;

    @RequestMapping("/user")
    public void user() {
        userService.insert();
    }

    @RequestMapping("/user1")
    public void user1() {
        userService.insert();
    }

}
