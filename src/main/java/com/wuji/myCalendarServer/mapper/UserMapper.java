package com.wuji.myCalendarServer.mapper;

import com.wuji.myCalendarServer.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User getUserByEmail(@Param("email") String email);

}
