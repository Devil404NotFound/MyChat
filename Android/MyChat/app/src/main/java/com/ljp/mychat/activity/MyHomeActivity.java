package com.ljp.mychat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljp.mychat.R;
import com.ljp.mychat.adapter.FriendListAdapter;
import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.RequMsg;
import com.ljp.mychat.util.Internet;
import com.ljp.mychat.util.MyGson;

import java.util.List;

/**
 * 好友列表
 */
public class MyHomeActivity extends AppCompatActivity {
    private String tag = "FriendListActivity";
    private Gson gson = MyGson.getGson();
    private ListView lv_friendlist;
    private List<Friendship> friendshipList;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String jsonRequMsg = (String) msg.obj;
            RequMsg requMsg = gson.fromJson(jsonRequMsg, RequMsg.class);
            friendshipList = gson.fromJson(gson.toJson(requMsg.getData()), new TypeToken<List<Friendship>>(){}.getType());
            lv_friendlist.setAdapter(new FriendListAdapter(getApplicationContext(), friendshipList));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_home);
        findViewById();
        Internet internet = new Internet("http://192.168.43.141:8080/friendships/1", mHandler);
    }
    private void findViewById(){
        lv_friendlist = findViewById(R.id.lv_my_friend_list);
    }
}
