package com.ljp.mychat.service;

import com.ljp.mychat.entity.User;

import java.util.List;

public interface UserService {
    User login(User user);   // 登录
    boolean addUser(User user);//添加用户
    boolean deleteUserById(Integer id);//删除用户
    boolean updateUser(User user);//更新用户
    User getUserById(Integer id);//通过id获取用户
    User getUserByMychatId(String mychatId);//通过mychatId获取用户
}
