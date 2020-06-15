package com.ljp.mychat.entity;

public class MessageList {
    private Integer id;
    private Integer userId;
    private Friendship friendship;
    private MyMessage msg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Friendship getFriendship() {
        return friendship;
    }

    public void setFriendship(Friendship friendship) {
        this.friendship = friendship;
    }

    public MyMessage getMsg() {
        return msg;
    }

    public void setMsg(MyMessage msg) {
        this.msg = msg;
    }
}
