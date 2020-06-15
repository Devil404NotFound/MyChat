package com.ljp.mychat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ljp.mychat.MyWebSocketClient;
import com.ljp.mychat.R;
import com.ljp.mychat.adapter.ChatRoomAdapter;
import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.MyMessage;
import com.ljp.mychat.entity.MyUser;
import com.ljp.mychat.service.JSocketClientService;
import com.ljp.mychat.util.MyGson;
import com.ljp.mychat.util.MySQLite;

import java.util.List;

public class ChatRoomActivity  extends AppCompatActivity {
    private final String tag = "ChatRoomActivity";
    
    private Button btn_send;
    private EditText et_content;
    private ListView listView;
    private TextView tv_friend_title_name;
    private Friendship friend;
    private MyWebSocketClient client;
    private JSocketClientService mService;
    private JSocketClientService.JSocketClientBinder mBinder;
    private MessageReceiver mMessageReceiver;
    private List<MyMessage> listMyMessage;
    private Gson gson = MyGson.getGson();

    /**
     *定义一个服务连接的匿名内部类
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(tag, "活动与服务已连接");
            mBinder = (JSocketClientService.JSocketClientBinder) service;
            mService = mBinder.getService();
            client = mService.getClient();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(tag, "活动与服务已断开");
        }
    };

    /**
     * 启动服务
     */
    public void startJSocketClientService(){
        Intent startIntent = new Intent(this, JSocketClientService.class);
        startService(startIntent);
    }
    /**
     *绑定服务
     */
    public void bindJSocketClientService(){
        Intent bindIntent =new Intent(this, JSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }
    /**
     * 注册广播接收器
     */
    private void doRegisterMessageReceiver(){
        mMessageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter("com.ljp.mychat.message");
        registerReceiver(mMessageReceiver, intentFilter);
    }

    /**
     * 内部类，一个消息接收者
     */
    private class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage myMessage = intent.getParcelableExtra("message");
            //过滤其他服务在发送消息时发的广播
            if(myMessage.getToId()==MyUser.getMyUser().getId()){
                listMyMessage.add(myMessage);
                reflushListView();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initChatRoom();
//        startJSocketClientService();//启动服务
        bindJSocketClientService();//绑定服务
        doRegisterMessageReceiver();//注册广播监听（用于接收onMessage()方法发送的广播）
    }

    @Override
    protected void onStart() {
        Log.i(tag, "调用onStart()");
        reflushListView();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(tag, "调用onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(tag, "调用onDestroy()");
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }



    /**
     * 初始化
     */
    private void initChatRoom(){
        this.btn_send = findViewById(R.id.btn_send);
        this.et_content = findViewById(R.id.et_content);
        this.listView = findViewById(R.id.chatmsg_listView);
        this.tv_friend_title_name = findViewById(R.id.tv_friend_title_name);
        friend = getIntent().getParcelableExtra("friend");//获取好友关系以及好友信息
        //设置标题栏好友名字
        String name = friend.getFriend().getUserName();
        if(friend.getRemark() != null){
            name += "(" + friend.getRemark() + ")";
        }
        tv_friend_title_name.setText(name);

        //获取本地消息记录
        listMyMessage = new MySQLite(this).getMessageList(friend.getFriend().getId());
        if(listMyMessage.size() != 0){
            reflushListView();
        }
    }

    /**
     * 更新界面（ListView）
     */
    private void reflushListView(){
        listView.setAdapter(new ChatRoomAdapter(this, listMyMessage));
//        listView.smoothScrollToPosition(listMessage.size() - 1);//没效果
        listView.setSelection(listView.getAdapter().getCount() - 1);//跳转到最后一行
    }

    /**
     * 发送消息
     * @param view
     */
    public void send(View view){
        String content = et_content.getText().toString();
        MyMessage myMessage = new MyMessage();
        myMessage.setFromId(MyUser.getMyUser().getId());
        myMessage.setMsgType(1);
        myMessage.setToId(friend.getFriend().getId());
        myMessage.setMsg(content);
        et_content.setText("");
        listMyMessage.add(myMessage);
        reflushListView();
        if(client != null && client.isOpen()){
            mService.send(myMessage);
        }else{
            Toast.makeText(this, "服务器已断开，请检查网络或重新启动App",Toast.LENGTH_SHORT).show();
        }
    }
}
