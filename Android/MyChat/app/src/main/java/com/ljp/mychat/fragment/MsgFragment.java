package com.ljp.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ljp.mychat.R;
import com.ljp.mychat.activity.ChatRoomActivity;
import com.ljp.mychat.adapter.FriendListAdapter;
import com.ljp.mychat.adapter.MessageListAdapter;
import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.MessageList;

import java.util.List;

public class MsgFragment extends Fragment {
    private final static String tag = "MsgFragment";
    private MessageListAdapter messageListAdapter;
    private ListView lv_msg;
    private List<MessageList> msgLists;

    public MsgFragment(List<MessageList> msgLists){
        this.msgLists = msgLists;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        //更新消息界面
        messageListAdapter = new MessageListAdapter(this.getContext(), msgLists);
        lv_msg.setAdapter(messageListAdapter);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg, container, false);
        lv_msg = view.findViewById(R.id.lv_msg);
        messageListAdapter = new MessageListAdapter(this.getContext(), msgLists);
        lv_msg.setAdapter(messageListAdapter);
        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageList messageList = msgLists.get(position);
                Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                intent.putExtra("friend", messageList.getFriendship());
                startActivity(intent);
            }
        });
        return view;
    }
}
