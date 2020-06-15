package com.ljp.mychat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ljp.mychat.MainActivity;
import com.ljp.mychat.MyWebSocketClient;
import com.ljp.mychat.MychatSQLiteHelper;
import com.ljp.mychat.R;
import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.MessageList;
import com.ljp.mychat.entity.MyMessage;
import com.ljp.mychat.entity.MyUser;
import com.ljp.mychat.entity.RequMsg;
import com.ljp.mychat.fragment.FragmentSettings;
import com.ljp.mychat.fragment.FriendshipFragment;
import com.ljp.mychat.fragment.MsgFragment;
import com.ljp.mychat.service.JSocketClientService;
import com.ljp.mychat.util.Internet;
import com.ljp.mychat.util.MyGson;
import com.ljp.mychat.util.MySQLite;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {
    private final static String tag = "HomeActivity";

    private ViewPager vp_content;
    private RadioGroup rg_home;
    private MyWebSocketClient client;
    private JSocketClientService mService;
    private JSocketClientService.JSocketClientBinder mBinder;
    private MessageReceiver mMessageReceiver;

    MySQLite mySQLite;
    private List<Fragment> fragmentList;
    private MsgFragment msgFragment;
    private FriendshipFragment friendshipFragment;
    private FragmentSettings fragmentSettings;
    //消息列表用到的List
    private Map<Integer, MessageList> friendMsgMap;
    private List<MessageList> msgLists;
    private List<Friendship> friendshipList;//好友列表


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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new Internet("friendships/" + MyUser.getMyUser().getId(), handler);
        initView();//初始化界面
        startJSocketClientService();//启动服务
        bindJSocketClientService();//绑定服务
        doRegisterMessageReceiver();//注册广播监听（用于接收onMessage()方法发送的广播）
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    /*----------------------friendship数据库测试-----------------------*/
    private MychatSQLiteHelper mychatSQLiteHelper = new MychatSQLiteHelper(this);
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                String json = (String) msg.obj;
                RequMsg requMsg = MyGson.getRequMsg(json);
                if(requMsg.getCode() == 200){
                    friendshipList = MyGson.getFriendshipList(requMsg.getData());
                    if(friendshipList == null){
                        Toast.makeText(HomeActivity.this, requMsg.getMsg(), Toast.LENGTH_SHORT).show();
                    }else{
                        MySQLite mySQLite = new MySQLite(HomeActivity.this);
                        mySQLite.syncFriends(friendshipList);//同步好友列表
//                        initView();//刷新一遍
                        fragmentList.remove(1);
                        fragmentList.add(1, new FriendshipFragment(friendshipList));
                    }
                }else if(requMsg.getCode() == 403){//如果Session过期
                    SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove("sessionId");
                    editor.commit();//记得提交
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    HomeActivity.this.finish();
                }else if(requMsg.getCode() == 401){//已经修改过密码
                    Toast.makeText(HomeActivity.this, "身份验证已过期，请重新登录！", Toast.LENGTH_SHORT).show();

                }
                    break;
                case -1:
                    String errorMsg = (String) msg.obj;
                    Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    /*-------------------------------------------------------*/

    /**
     * 初始化
     */
    private void initView(){
        mySQLite = new MySQLite(this);
        initMsgList();//初始化消息列表
        friendshipList = mySQLite.getFriends();//初始化好友列表

        //添加Fragment
        fragmentList = new ArrayList<>();
        msgFragment = new MsgFragment(msgLists);
        friendshipFragment = new FriendshipFragment(friendshipList);
        fragmentSettings = new FragmentSettings();
        fragmentList.add(msgFragment);
        fragmentList.add(friendshipFragment);
        fragmentList.add(fragmentSettings);
        //绑定View控件
        vp_content = findViewById(R.id.vp_content);
        rg_home = findViewById(R.id.rg_home);
        //给RadioGroup设置监听器
        rg_home.setOnCheckedChangeListener(onCheckedChangeListener);
        //给ViewPager设置监听器
        vp_content.addOnPageChangeListener(onPageChangeListener);
        //给ViewPager设置Adapter
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        vp_content.setAdapter(myFragmentPagerAdapter);
    }
    private void initMsgList(){
        //消息列表
        friendMsgMap = new HashMap<>();
        msgLists = mySQLite.getMessageLists(MyUser.getMyUser().getId());
        int size = msgLists.size();
        for (int i = 0; i < size; i++) {
            MessageList messageList = msgLists.get(i);
            friendMsgMap.put(messageList.getFriendship().getFriend().getId(), messageList);
        }
    }
    private void synMsgList(MyMessage myMessage){
        //获取
        Integer frinedId = myMessage.getFromId() == MyUser.getMyUser().getId() ?
                myMessage.getToId() : myMessage.getFromId();

        int msgId = mySQLite.saveMsg(myMessage);
        if(friendMsgMap.containsKey(frinedId)){//更新
            MessageList messageList = friendMsgMap.get(frinedId);
            messageList.setMsg(myMessage);
            mySQLite.updateMessageList(messageList);
        }else{
            myMessage.setId(msgId);
            MessageList messageList = new MessageList();
            messageList.setMsg(myMessage);
            Friendship friendship = mySQLite.getFriendshipByUserIdAndFriendId(MyUser.getMyUser().getId(), frinedId);
            messageList.setUserId(friendship.getUserId());
            messageList.setFriendship(friendship);
            mySQLite.insertMessageList(messageList);
            friendMsgMap.put(frinedId, messageList);
        }
        initView();///测试
    }
    /**
     * 注册点击按钮切换的监听器
     */
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int size = group.getChildCount();
            for (int i = 0; i < size; i++) {
                if(group.getChildAt(i).getId() == checkedId){
                    vp_content.setCurrentItem(i);
                }
            }
        }
    };

    /**
     * 监听滑动效果
     */
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) rg_home.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 重写 onCreateOptionsMenu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //加载 布局实现
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            /*case R.id.item_create_group:
                //新建群聊代码
                break;*/
            case R.id.item_add_friend:
                //添加好友
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 页面的适配器
     */
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;
        public MyFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
    /**
     * 内部类，一个消息接收者
     */
    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage myMessage = intent.getParcelableExtra("message");
//            listMessage.add(message);
//            reflushListView();
              synMsgList(myMessage);
              initView();
        }
    }
}
