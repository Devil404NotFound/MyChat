package com.ljp.mychat.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 好友列表
 */
public class Friendship implements Parcelable {
    private Integer id;
    private Integer userId;
    private String remark;
    private Integer enabled;
    private User friend;
    public Friendship(){

    }
    protected Friendship(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        remark = in.readString();
        if (in.readByte() == 0) {
            enabled = null;
        } else {
            enabled = in.readInt();
        }
        //重点：从序列化的in中获取User对象
        if(in.readByte() == 1){
            friend = in.readParcelable(getClass().getClassLoader());
        }
    }

    public static final Creator<Friendship> CREATOR = new Creator<Friendship>() {
        @Override
        public Friendship createFromParcel(Parcel in) {
            return new Friendship(in);
        }

        @Override
        public Friendship[] newArray(int size) {
            return new Friendship[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(remark);
        if (enabled == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(enabled);
        }
        //将User对象转换为Parcel的序列化数据
        if(friend == null){
            dest.writeByte((byte)0);
        }else{
            dest.writeByte((byte)1);
            dest.writeParcelable(friend, flags);
        }
    }

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + id +
                ", userId=" + userId +
                ", remark='" + remark + '\'' +
                ", enabled=" + enabled +
                ", friend=" + friend +
                '}';
    }

}
