package com.ljp.mychat.service.impl;


import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.mapper.FriendshipMapper;
import com.ljp.mychat.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    @Autowired
    private FriendshipMapper friendshipMapper;
    @Override
    public boolean addFriendship(Friendship friendship) {
        return friendshipMapper.addFriendship(friendship);
    }

    @Override
    public boolean updateFriendship(Friendship friendship) {
        return friendshipMapper.updateFriendship(friendship);
    }

    @Override
    public boolean deleteFriendship(Friendship friendship) {
        return friendshipMapper.deleteFriendship(friendship);
    }

    @Override
    public List<Friendship> getUserList(Integer user_id) {
        return friendshipMapper.getUserList(user_id);
    }

    @Override
    public Friendship getFriendshipByUserIdAndFriendId(Friendship friendship) {
        return friendshipMapper.getFriendshipByUserIdAndFriendId(friendship);
    }
}
