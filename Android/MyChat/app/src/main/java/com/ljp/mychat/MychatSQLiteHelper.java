package com.ljp.mychat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MychatSQLiteHelper extends SQLiteOpenHelper {
    private final String tag = "MychatSQLiteHelper";


    public MychatSQLiteHelper(@Nullable Context context) {
        super(context, "mychat", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //新建用户表
        db.execSQL("create table user(" +
                "id integer primary key autoincrement, " +
                "mychat_id varchar(20) not null unique," +
                "user_name varchar(20)," +
                "password varchar(255)," +
                "name varchar(20)," +
                "gender tinyint default 0," +
                "phone varchar(20)," +
                "enabled tinyint default 1," +
                "user_face_path varchar(50)," +
                "description varchar(100)," +
                "create_time int," +
                "last_online_time int)");
        //新建好友关系表
        db.execSQL("create table friendship(" +
                "id integer primary key," +
                "user_id integer not null," +
                "friend_id integer not null," +
                "remark varchar(20)," +
                "enabled integer default 1)");
        //新建消息表
        db.execSQL("create table message(" +
                "id integer primary key autoincrement," +
                "from_id integer not null," +
                "to_id integer not null," +
                "msg_type tinyint not null," +
                "msg text," +
                "msg_path varchar(255)," +
                "send_time integer(15))");
        //新建消息列表
        db.execSQL("create table message_list(" +
                "id integer primary key autoincrement," +
                "user_id integer not null," +
                "friendship_id integer not null," +
                "message_id integer)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(tag, "数据库从"+ oldVersion + "更新到" + newVersion + "版本");
    }
}
