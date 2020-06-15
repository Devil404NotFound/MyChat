package com.ljp.mychat.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljp.mychat.entity.Message;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/webSocket/{sid}")
@Component
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();
    private static ObjectMapper mapper = new ObjectMapper();//JSON转换工具

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
//                System.out.println("发送数据：" + message);
                session.getBasicRemote().sendText(message);
            }
        }
    }
    //给指定用户发送信息
    public void sendInfo(String userName, String message){
        Session session = sessionPools.get(userName);
        try {
            sendMessage(session, message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String myChatId){
        sessionPools.put(myChatId, session);
        addOnlineCount();
        System.out.println(myChatId + "加入webSocket！当前人数为" + onlineNum);
        /*try {
            sendMessage(session, "欢迎" + myChatId + "加入连接！");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "sid") String userName){
        sessionPools.remove(userName);
        subOnlineCount();
        System.out.println(userName + "断开webSocket连接！当前人数为" + onlineNum);
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(String reqMessage) throws IOException{
        System.out.println("客户端：" + reqMessage + ",已收到");
        /*for (Session session: sessionPools.values()) {
            try {
                sendMessage(session, message);
            } catch(Exception e){
                e.printStackTrace();
                continue;
            }
        }*/
        Message message = mapper.readValue(reqMessage, Message.class);
        sendInfo(message.getToId(), mapper.writeValueAsString(message));
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误,session:" + session.getId());
        throwable.printStackTrace();
    }

    private static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    private static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

}
