package com.wuji.myCalendarServer.controller;

import com.wuji.myCalendarServer.bean.User;
import com.wuji.myCalendarServer.dto.ResponseResult;
import com.wuji.myCalendarServer.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
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
public class LoginController extends BaseController{

    @Autowired
    private LoginService loginService;

    Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseResult login(HttpServletRequest request, @RequestBody User user, BindingResult bindingResult) {
        logger.info("用户：" + user.getUserName() + " 登陆中");
        ResultDelegate delegate = new ResultDelegate() {
            @Override
            public Object getResultObject() throws Exception {
                return loginService.login(user);
            }
        };
        return getResponseResult(request, delegate, bindingResult);
    }
}
