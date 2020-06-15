package com.ljp.mychat.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ljp.mychat.MychatSQLiteHelper;
import com.ljp.mychat.R;
import com.ljp.mychat.activity.ChatRoomActivity;
import com.ljp.mychat.adapter.FriendListAdapter;
import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.User;
import com.ljp.mychat.util.MySQLite;

import java.util.ArrayList;
import java.util.List;

public class FriendshipFragment extends Fragment {
    private final String tag = "FriendshipFragment";

    private List<Friendship> friendshipList;
    private FriendListAdapter friendListAdapter;

    private ListView lv_friend_list;

    /**
     * 构造器，必须传一个List<Friendship>
     * @param friendshipList 好友列表
     */
    public FriendshipFragment(List<Friendship> friendshipList){
        this.friendshipList = friendshipList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i(tag, "onStart()");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(tag, "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(tag, "onDestroy()");
        super.onDestroy();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(tag, "调用onCreateView()");
        View view = inflater.inflate(R.layout.fragment_friendship, container, false);
        lv_friend_list = view.findViewById(R.id.lv_friend_list);
        friendListAdapter = new FriendListAdapter(this.getContext(), friendshipList);
        lv_friend_list.setAdapter(friendListAdapter);
        lv_friend_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friendship friendship = friendshipList.get(position);
                Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                intent.putExtra("friend", friendship);
                startActivity(intent);
            }
        });
        return view;
    }
}
