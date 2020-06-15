package com.ljp.mychat.service;

import com.ljp.mychat.entity.Friendship;

import java.util.List;

public interface FriendshipService {
    boolean addFriendship(Friendship friendship);//添加好友
    boolean updateFriendship(Friendship friendship);//修改好友状态（删除、拉黑等）

    boolean deleteFriendship(Friendship friendship);//删除数据，不对外开放

    List<Friendship> getUserList(Integer user_id);//获取好友列表
    Friendship getFriendshipByUserIdAndFriendId(Friendship friendship);//根据userId和FriendId查询好友对象
}
