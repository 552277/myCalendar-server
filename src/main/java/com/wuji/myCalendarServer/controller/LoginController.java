package com.wuji.myCalendarServer.controller;

import com.wuji.myCalendarServer.bean.User;
import com.wuji.myCalendarServer.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhongweichang
 * @email 15090552277@163.com
 * @date 2018/7/18 下午7:19
 */
@RestController
@RequestMapping(value = "/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public boolean login(HttpServletRequest request, @RequestBody User user) {
        logger.info("用户：" + user.getUserName() + " 登陆中");
        return loginService.login(user);
    }
}
