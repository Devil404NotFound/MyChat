package com.ljp.mychat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljp.mychat.R;
import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.MessageList;

import java.util.List;

public class MessageListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MessageList> messageLists;

    public MessageListAdapter(Context mContext, List<MessageList> messageLists) {
        this.mContext = mContext;
        this.messageLists = messageLists;
    }

    @Override
    public int getCount() {
        return messageLists.size();//这个很重要，没有重写的话没有显示效果
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_message, null);
        MessageList messageList = messageLists.get(position);
        TextView tv_friend_name = view.findViewById(R.id.tv_msg_friend_name);
        ImageView iv_userFace = view.findViewById(R.id.iv_msg_friend_face);
        TextView tv_msg_time = view.findViewById(R.id.tv_msg_time);
        TextView tv_msg_content = view.findViewById(R.id.tv_msg_content);
//        iv_userFace.setScaleType(ImageView.ScaleType.FIT_CENTER);
        String friendName = messageList.getFriendship().getFriend().getUserName();
        if(messageList.getFriendship().getRemark() != null){
            friendName += "(" + messageList.getFriendship().getRemark() + ")";
        }
        tv_friend_name.setText(friendName);
        tv_msg_content.setText(messageList.getMsg().getMsg());

        return view;
    }
}
