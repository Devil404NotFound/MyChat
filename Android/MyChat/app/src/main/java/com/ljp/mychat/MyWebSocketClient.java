package com.ljp.mychat;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MyWebSocketClient extends WebSocketClient {
    private String tag = "MyWebSocket";
    private static MyWebSocketClient client;
    public MyWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
        client = this;
    }
    public static MyWebSocketClient getClient(URI serverUri){
        return client;
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i(tag, "调用onOpen()方法");
    }
    @Override
    public void onMessage(String message) {
        Log.i(tag, "调用onMessage()方法");
        System.out.println("收到服务器信息："+ message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i(tag, "调用onClose()方法");
    }

    @Override
    public void onError(Exception ex) {
        Log.i(tag, "调用onError()方法");
    }
}
