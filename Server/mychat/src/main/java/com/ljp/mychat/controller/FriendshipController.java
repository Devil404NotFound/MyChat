package com.ljp.mychat.controller;

import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.RespMsg;
import com.ljp.mychat.entity.User;
import com.ljp.mychat.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FriendshipController {
    @Autowired
    private FriendshipService friendshipService;

    /**
     * 添加好友接口
     * @param friendship
     * @return
     */
    @PostMapping("/friendship")
    public RespMsg addFriends(Friendship friendship){
        if(friendship == null){
            return new RespMsg(412, "服务器接收不到Friendship数据");
        }else{
            friendshipService.addFriendship(friendship);
            return new RespMsg(200, "添加好友成功", friendship);
        }
    }

    /**
     * 修改好友状态接口
     * @param friendship
     * @return
     */
    @PutMapping("/friendship")
    public RespMsg updateFriendship(Friendship friendship){
        if(friendship.getUserId() == null || friendship.getFriend() == null){
            return new RespMsg(412, "服务器接收不到Friendship数据");
        }else{
            friendshipService.updateFriendship(friendship);
            return new RespMsg(200, "操作成功", friendship);
        }
    }
    @GetMapping("/friendships/{userId}")
    public RespMsg getUserFriends(@PathVariable(value = "userId") Integer userId){
        List<Friendship> list = friendshipService.getUserList(userId);
        return new RespMsg(200, "获取好友列表成功", list);
    }
    @GetMapping("/friendship/{ids}")
    public RespMsg getFriendshipByUserIdAndFriendId(@PathVariable(value = "ids") String ids){
        String[] strId = ids.split("and");
        Integer userId = Integer.valueOf(strId[0]);
        Integer friendId = Integer.valueOf(strId[1]);
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        User friend = new User();
        friend.setId(friendId);
        friendship.setFriend(friend);
        friendship = friendshipService.getFriendshipByUserIdAndFriendId(friendship);
        if(friendship == null){
            return new RespMsg(500, "不是好友关系");
        }else{
            return new RespMsg(200, "查询成功", friendship);
        }
    }
}
