package com.ljp.mychat.mapper;

import com.ljp.mychat.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserMapper {
    User login(User user);   // 登录
    boolean addUser(User user);//添加用户
    boolean deleteUserById(Integer id);//删除用户
    boolean updateUser(User user);//更新用户
    User getUserById(Integer id);//通过id获取用户
    User getUserByMychatId(String mychatId);//通过mychatId获取用户
}
