package com.ljp.mychat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.ljp.mychat.MyWebSocketClient;
import com.ljp.mychat.entity.MyMessage;
import com.ljp.mychat.entity.MyUser;
import com.ljp.mychat.util.MyGson;
import com.ljp.mychat.util.MySQLite;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class JSocketClientService extends Service {
    private final String tag = "JSocketClientService";
    private final String socketPath = "ws://192.168.43.141:8080/webSocket/";

    private MySQLite mySQLite;
    private MyWebSocketClient client;
    private JSocketClientBinder mBinder = new JSocketClientBinder();
    private Gson gson = MyGson.getGson();

    /**
     *使用Binder在Activity与Service之间通信
     */
    public class JSocketClientBinder extends Binder{
        public JSocketClientService getService(){
            return JSocketClientService.this;
        }
    }
    public MyWebSocketClient getClient(){
        return client;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initSocketClient();//初始化WebSocket;
        startAutoConnection();//开启心跳重连
        mySQLite = new MySQLite(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopAutoConnection();//取消心跳重连
        disConnect();
        Log.i(tag, "onDestroy()");
        super.onDestroy();
    }

    public void send(MyMessage myMessage){
        if(client != null && client.isOpen()){
            client.send(gson.toJson(myMessage));
            mySendBroadcast(myMessage);
        }
    }
    /**
     * 初始化SocketClient
     */
    private void initSocketClient(){
        URI uri = URI.create(socketPath + MyUser.getMyUser().getId());
        client = new MyWebSocketClient(uri){
            @Override
            public void onMessage(String message) {
                System.out.println("收到服务器消息息："+ message);
                MyMessage myMessageObj = gson.fromJson(message, MyMessage.class);
                mySendBroadcast(myMessageObj);
            }
        };
        //连接服务器
        try {
            client.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播
     * @param myMessageObj
     */
    private void mySendBroadcast(MyMessage myMessageObj){
        //发送广播
        Intent intent = new Intent();
        intent.setAction("com.ljp.mychat.message");
        intent.putExtra("message", myMessageObj);
        sendBroadcast(intent);
//        mySQLite.saveMsg(myMessageObj);//保存消息到本地====这个交给HomeActivity去做了
    }
    private void disConnect(){
        if(client != null){
            try {
                client.closeBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client = null;
        }
    }

    /*------------------开启心跳重连任务------------------------*/
    private Timer timer;
    private final Integer PERIOD = 10000;

    /**
     * 开启心跳重连
     */
    private void startAutoConnection(){
        timer = new Timer();
        TimerTask timerTask =new TimerTask() {
            @Override
            public void run() {
                Log.i(tag, "正在检测Socket是否断开连接......");
                if(client == null){
                    initSocketClient();
                }else if(client.isClosed()){
                    try {
                        client.reconnectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.schedule(timerTask, PERIOD, PERIOD);
    }

    /**
     * 取消心跳重连
     */
    private void stopAutoConnection(){
        Log.i(tag, "取消心跳重连");
        timer.cancel();
    }

}
