package com.ljp.mychat.entity;

public class MyUser extends User {
    private static User myUser;

    private MyUser(){

    }
    public static User getMyUser(){
        return myUser;
    }
    public static void setMyUser(User user){
        myUser = user;
    }

}
