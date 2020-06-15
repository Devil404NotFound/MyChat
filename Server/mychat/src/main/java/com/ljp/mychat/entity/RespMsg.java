package com.ljp.mychat.entity;

/**
 * 返回前端信息的一个类
 */
public class RespMsg {
    int code;
    String msg;
    Object data;

    public RespMsg(){

    }
    public RespMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespMsg(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
