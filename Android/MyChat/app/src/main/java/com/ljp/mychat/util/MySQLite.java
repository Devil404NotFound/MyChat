package com.ljp.mychat.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ljp.mychat.MychatSQLiteHelper;
import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.MyMessage;
import com.ljp.mychat.entity.MessageList;
import com.ljp.mychat.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLite {
    private static final String tag = "MySQLite";
    private MychatSQLiteHelper mHelper;
    public MySQLite(Context context){
        mHelper = new MychatSQLiteHelper(context);
    }

    /**
     * 获取信息列表的列表
     * @param userId
     * @return
     */
    public List<MessageList> getMessageLists(Integer userId){
        List<MessageList> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("message_list", null, "user_id=?", new String[]{"" + userId}, null, null, null);
        MessageList messageList;
        while(cursor.moveToNext()){
            messageList = new MessageList();
            messageList.setId(cursor.getInt(0));
            messageList.setUserId(userId);
            messageList.setMsg(getMessageById(cursor.getInt(2)));
            messageList.setFriendship(getFriendshipById(cursor.getInt(3)));
            list.add(messageList);
        }
        return list;
    }
    /**
     * 通过id查找Message
     * @param id
     * @return
     */
    private MyMessage getMessageById(Integer id){
        MyMessage msg = new MyMessage();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("message", null, "id=?", new String[]{"" + id}, null, null, null);
        if(cursor.moveToNext()){
            msg.setId(cursor.getInt(0));
            msg.setFromId(cursor.getInt(1));
            msg.setToId(cursor.getInt(2));
            msg.setMsgType(cursor.getInt(3));
            msg.setMsg(cursor.getString(4));
            msg.setMsgPath(cursor.getString(5));
            msg.setSendDate(new Date(cursor.getLong(6)));
        }
        return msg;
    }

    /**
     * 通过id查询Friendship
     * @param id
     * @return
     */
    private Friendship getFriendshipById(Integer id){
        Friendship friendship = new Friendship();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("friendship", null, "id=?", new String[]{"" + id}, null, null, null);
        if(cursor.moveToNext()){
            friendship.setId(id);
            friendship.setUserId(cursor.getInt(1));
            friendship.setFriend(getUser(cursor.getInt(2)));
            friendship.setRemark(cursor.getString(3));
            friendship.setEnabled(cursor.getInt(4));
        }
        return friendship;
    }

    public Friendship getFriendshipByUserIdAndFriendId(Integer userId, Integer friendId){
        Friendship friendship = new Friendship();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("friendship", null, "user_id=? and friend_id=?", new String[]{"" + userId, "" + friendId}, null, null, null);
        if(cursor.moveToNext()){
            friendship.setId(cursor.getInt(0));
            friendship.setUserId(cursor.getInt(1));
            friendship.setFriend(getUser(cursor.getInt(2)));
            friendship.setRemark(cursor.getString(3));
            friendship.setEnabled(cursor.getInt(4));
        }
        return friendship;
    }

    /**
     * 查询好友消息
     * @param friendId
     * @return
     */
    public List<MyMessage> getMessageList(Integer friendId){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<MyMessage> myMessageList = new ArrayList<>();
        Cursor cursor = db.query("message", null, "from_id=? or to_id=?", new String[]{"" + friendId, "" + friendId}, null, null,null);
        MyMessage myMessage;
        while(cursor.moveToNext()){
            myMessage = new MyMessage();
            myMessage.setId(cursor.getInt(0));
            myMessage.setFromId(cursor.getInt(1));
            myMessage.setToId(cursor.getInt(2));
            myMessage.setMsgType(cursor.getInt(3));
            myMessage.setMsg(cursor.getString(4));
            myMessage.setMsgPath(cursor.getString(5));
//            message.setSendDate(new Date(cursor.getLong(6)));
            myMessageList.add(myMessage);
        }
        return myMessageList;
    }
    /**
     * 从本地数据库获取用户信息
     * @param id
     * @return
     */
    public User getUser(Integer id){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        User user = new User();
        Cursor userCursor = db.query("user", null, "id=?", new String[]{""+id}, null, null, null);
        if(userCursor.moveToNext()){
            user.setId(userCursor.getInt(0));
            user.setMychatId(userCursor.getString(1));
            user.setUserName(userCursor.getString(2));
            user.setName(userCursor.getString(4));
            user.setGender(userCursor.getInt(5));
            user.setPhone(userCursor.getString(6));
            user.setEnabled(userCursor.getInt(7));
            user.setUserFacePath(userCursor.getString(8));
            user.setDescription(userCursor.getString(9));
            //创建时间不获取
            user.setLastOnlineTime(new Date(userCursor.getLong(11)));
            Log.i(tag, "查询本地好友信息成功");
        }
        return user;
    }

    /**
     * 从本地数据库获取好友列表
     * @return
     */
    public List<Friendship> getFriends(){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("friendship", null, null, null, null, null, null);
        List<Friendship> friendshipList = new ArrayList<>();
        while(cursor.moveToNext()){
            Friendship friendship = new Friendship();
            friendship.setId(cursor.getInt(0));
            friendship.setUserId(cursor.getInt(1));
            friendship.setFriend(getUser(cursor.getInt(2)));
            friendship.setRemark(cursor.getString(3));
            friendship.setEnabled(cursor.getInt(4));
//            System.out.println(friendship.toString());
            friendshipList.add(friendship);
        }
        db.close();
        Log.i(tag, "查询本地好友列表成功");
        return friendshipList;
    }

    /**
     * 保存自己的信息
     * @param user
     */
    public void saveUser(User user){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        synUser(user, db);
    }
    /**
     * 同步好友列表
     * @param list
     */
    public void syncFriends(List<Friendship> list){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values;
        Friendship friendship;
        int size = list.size();
        for(int i = 0; i < size; i++){
            friendship = list.get(i);
            //好友关系表
            values = new ContentValues();
            values.put("id", friendship.getId());
            values.put("user_id", friendship.getUserId());
            values.put("enabled", friendship.getEnabled());
            values.put("remark", friendship.getRemark());
            values.put("friend_id", friendship.getFriend().getId());
            //判断本地数据库是否有该记录
            boolean flag = db.query("friendship", null, "id=" + friendship.getId(), null, null, null, null).moveToNext();
            if(flag){
                db.update("friendship", values, "id=?", new String[]{"" + friendship.getId()});
            }else{
                db.insert("friendship", null, values);
            }
            Log.i(tag, "成功刷新好友列表");
            synUser(friendship.getFriend(), db);
        }
        db.close();
    }

    private void synUser(User friend, SQLiteDatabase db) {
        //好友信息表
        ContentValues values = new ContentValues();
            values.put("id", friend.getId());
            values.put("mychat_id", friend.getMychatId());
            values.put("user_name", friend.getUserName());
            values.put("name", friend.getName());
            values.put("gender", friend.getGender());
            values.put("phone", friend.getPhone());
            values.put("enabled", friend.getEnabled());
            values.put("user_face_path", friend.getUserFacePath());
            values.put("description", friend.getDescription());
//            userValues.put("create_time", friend.getCreateTime().getTime());
//            userValues.put("last_online_time", friend.getLastOnlineTime().getTime());
            boolean flag = db.query("user", null, "id=" + friend.getId(), null, null, null, null).moveToNext();
            if(flag){
                db.update("user", values, "id=?", new String[]{"" + friend.getId()});
            }else{
                db.insert("user", null, values);
            }
            Log.i(tag, "成功刷新好友信息");
    }

    /**
     * 保存用户信息（包括好友信息）
     * @param myMessage
     */
    public int saveMsg(MyMessage myMessage){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", myMessage.getId());
        values.put("from_id", myMessage.getFromId());
        values.put("to_id", myMessage.getToId());
        values.put("msg_type", myMessage.getMsgType());
        values.put("msg", myMessage.getMsg());
        values.put("msg_path", myMessage.getMsgPath());
//        values.put("send_time", message.getSendDate().getTime());
        long i = db.insert("message", null, values);
        return getLastMsgId(db);
    }

    /**
     * 获取最新插入的id
     * @param db
     * @return
     */
    private int getLastMsgId(SQLiteDatabase db){
        String sql = "select last_insert_rowid() from message";
        Cursor cursor = db.rawQuery(sql, null);
        int last = -1;
        if(cursor.moveToFirst()){
            last = cursor.getInt(0);
        }
        return last;
    }
    public void insertMessageList(MessageList messageList){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", messageList.getUserId());
        values.put("friendship_id", messageList.getFriendship().getId());
        values.put("message_id", messageList.getMsg().getId());
        db.insert("message_list", null, values);
    }
    public void updateMessageList(MessageList messageList){
        SQLiteDatabase db = mHelper.getWritableDatabase();ContentValues values = new ContentValues();
        values.put("user_id", messageList.getUserId());
        values.put("friendship_id", messageList.getFriendship().getId());
        values.put("message_id", messageList.getMsg().getId());
        db.update("message_list", values, "user_id=? and friendship_id=?", new String[]{""+messageList.getUserId(), ""+messageList.getFriendship().getId()});
    }
}
