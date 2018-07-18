package com.wuji.myCalendarServer.service;

import com.netease.cloud.ClientException;
import com.wuji.myCalendarServer.bean.User;
import com.wuji.myCalendarServer.mapper.UserMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private UserMapper userMapper;

    public boolean login(User user) {
        if(Strings.isEmpty(user.getEmail()) || Strings.isEmpty(user.getPassword())) {
            throw new ClientException("邮箱和密码不能为空！");
        }
        User existUser = userMapper.getUserByEmail(user.getEmail());
        if(existUser == null) {
            throw new ClientException("用户不存在!");
        }
        if(!user.getPassword().equals(existUser.getPassword())) {
            throw new ClientException("密码有误，请重新输入！");
        }
        return true;
    }
}
