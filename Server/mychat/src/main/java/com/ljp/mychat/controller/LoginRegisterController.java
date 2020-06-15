package com.ljp.mychat.controller;

import com.ljp.mychat.entity.RespMsg;
import com.ljp.mychat.entity.User;
import com.ljp.mychat.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;

@RestController
public class LoginRegisterController {

    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public RespMsg register(User user, HttpServletRequest request){
        if(user == null){
            return new RespMsg(412, "服务器接收不到User数据");
        }else{
            boolean flag = userService.addUser(user);
            String userFacePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/userface/" + user.getId();
            user.setUserFacePath(userFacePath);
            userService.updateUser(user);
            if(flag){
                //复制一个默认头像
                Resource resource = new ClassPathResource("resources/userface/default.jpg");
                File file = null;
                try {
                    file = resource.getFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String fileRealPath = file.getPath();
                try {
                    InputStream inputStream = new FileInputStream(fileRealPath);
                    fileRealPath = fileRealPath.replace("default", "" + user.getId());
                    OutputStream outputStream = new FileOutputStream(fileRealPath);
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while((len = inputStream.read(buffer)) != -1){
                        outputStream.write(buffer, 0, len);
                    }
                    inputStream.close();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return new RespMsg(200, "注册成功",user);
            }else{
                return new RespMsg(500, "注册失败");
            }
        }
    }

    /**
     * 登录
     * @param userName
     * @param password
     * @param session
     * @return
     */
    @PostMapping("/login")
    public RespMsg login(@Param(value = "userName") String userName,@Param(value = "password") String password, HttpSession session){
        User user = new User();
        user.setMychatId(userName);
        user.setPhone(userName);
        user.setPassword(password);
        user = userService.login(user);
        if(user != null){
            session.setAttribute("user", user.getId());
            return new RespMsg(200, "登录成功", user);
        }else{
           return new RespMsg(401, "用户名或密码错误");
        }
    }
}
