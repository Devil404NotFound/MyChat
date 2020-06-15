package com.ljp.mychat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ljp.mychat.R;
import com.ljp.mychat.entity.MyMessage;
import com.ljp.mychat.entity.MyUser;

import java.util.List;

public class ChatRoomAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyMessage> list;
    public ChatRoomAdapter(Context context, List<MyMessage> list) {
        mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
        View view;
        MyMessage myMessage = list.get(position);
        if(MyUser.getMyUser().getId().equals(myMessage.getFromId())){
            view = View.inflate(mContext, R.layout.item_chat_send_text, null);
        }else{
            view = View.inflate(mContext, R.layout.item_chat_receive_text, null);
        }
        TextView tv_content = view.findViewById(R.id.tv_content);
        tv_content.setText(myMessage.getMsg());
        return view;
    }
}
