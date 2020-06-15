package com.ljp.mychat.entity;

import jdk.nashorn.internal.ir.annotations.Ignore;

import java.util.Date;

public class User {
    private Integer id;
    private String mychatId;
    private String userName;
    private String password;
    private String name;
    private Integer gender;//性别，0未设置，1男，2女
    private String phone;
    private int enabled;//是否可用，0：不可用 1：普通用户 2：管理员
    private String userFacePath;//头像路径
    private String description;//描述
    private Date createTime;//创建时间
    private Date lastOnlineTime;//最近上线时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMychatId() {
        return mychatId;
    }

    public void setMychatId(String mychatId) {
        this.mychatId = mychatId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getUserFacePath() {
        return userFacePath;
    }

    public void setUserFacePath(String userFacePath) {
        this.userFacePath = userFacePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", mychatId='" + mychatId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", enabled=" + enabled +
                ", userFacePath='" + userFacePath + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", lastOnlineTime=" + lastOnlineTime +
                '}';
    }
}
