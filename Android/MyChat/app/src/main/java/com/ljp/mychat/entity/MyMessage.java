package com.ljp.mychat.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MyMessage implements Parcelable {
    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String msg;
    private Integer msgType;
    private String msgPath;
    private Date sendDate;

    public MyMessage(){

    }
    protected MyMessage(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            fromId = null;
        } else {
            fromId = in.readInt();
        }
        if (in.readByte() == 0) {
            toId = null;
        } else {
            toId = in.readInt();
        }
        msg = in.readString();
        if (in.readByte() == 0) {
            msgType = null;
        } else {
            msgType = in.readInt();
        }
        msgPath = in.readString();
    }

    public static final Creator<MyMessage> CREATOR = new Creator<MyMessage>() {
        @Override
        public MyMessage createFromParcel(Parcel in) {
            return new MyMessage(in);
        }

        @Override
        public MyMessage[] newArray(int size) {
            return new MyMessage[size];
        }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (fromId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(fromId);
        }
        if (toId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(toId);
        }
        dest.writeString(msg);
        if (msgType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(msgType);
        }
        dest.writeString(msgPath);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getMsgPath() {
        return msgPath;
    }

    public void setMsgPath(String msgPath) {
        this.msgPath = msgPath;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
