package com.ljp.mychat.controller;

import com.ljp.mychat.entity.RespMsg;
import com.ljp.mychat.entity.User;
import com.ljp.mychat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController()
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/user/{id}")
    public RespMsg findUserById(@PathVariable(value = "id") Integer id){
        User user = userService.getUserById(id);
        if(user == null){
            return new RespMsg(500, "数据库返回null");
        }else{
            return new RespMsg(200, "查询成功", user);
        }
    }
    /**
     * @param mychatId
     * 通过mychatId查询用户
     * @return
     */
    @GetMapping("userinfo/{mychatId}")
    public RespMsg findUser(@PathVariable(value = "mychatId")String mychatId){
        User user = userService.getUserByMychatId(mychatId);
        if(user == null){
            return new RespMsg(500, "数据库返回null");
        }else{
            return new RespMsg(200, "查询成功", user);
        }
    }

    /**
     * 修改用户
     * @param user
     * @return
     */
    @PutMapping("/user")
    public RespMsg updateUser(User user){
        boolean success = userService.updateUser(user);
        if(success){
            return new RespMsg(200, "修改成功");
        }else{
            return new RespMsg(1001,"修改失败");
        }
    }
}
