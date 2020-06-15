package com.ljp.mychat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljp.mychat.R;
import com.ljp.mychat.entity.Friendship;

import java.util.List;

public class FriendListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Friendship> friendshipList;

    public FriendListAdapter(Context mContext, List<Friendship> friendshipList) {
        this.mContext = mContext;
        this.friendshipList = friendshipList;
    }

    @Override
    public int getCount() {
        return friendshipList.size();
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
        View view = View.inflate(mContext, R.layout.item_friend, null);
        Friendship friendship = friendshipList.get(position);
        TextView tv_friend_name = view.findViewById(R.id.tv_friend_name);
        ImageView iv_userFace = view.findViewById(R.id.iv_friend_face);
//        iv_userFace.setScaleType(ImageView.ScaleType.FIT_CENTER);
        String friendName = friendship.getFriend().getUserName();
        if(friendship.getRemark() != null){
           friendName += "(" + friendship.getRemark() + ")";
        }
        tv_friend_name.setText(friendName);
        return view;
    }
}
