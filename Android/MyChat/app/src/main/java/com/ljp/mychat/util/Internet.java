package com.ljp.mychat.util;
import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Internet{
    private static String sessionId = null;
    public final static String PATH = "http://192.168.43.141:8080/";
    public Internet(String requestPath, Handler handler){
        URL url = null;
        try {
            url = new URL(PATH + requestPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        runThread(url, handler, "GET", null);
    }
    public Internet(String requestPath, Handler handler, String method, String json){
        URL url = null;
        try {
            url = new URL(PATH + requestPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        runThread(url, handler, method, json);
    }

    /**
     * msg.what = 1 ===== 成功连接网络
     * msg.what = -1 ==== 连接超时
     * @param url
     * @param handler
     */
    private void runThread(URL url, final Handler handler, String method, String json){
        final URL finalUrl = url;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    HttpURLConnection conn = (HttpURLConnection) finalUrl.openConnection();
                    conn.setRequestMethod(method);
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Cookie", sessionId);
                    if(json != null){
                        conn.addRequestProperty("Accept", "*/*");
                        conn.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        OutputStream out = conn.getOutputStream();
                        out.write(json.getBytes());
                        out.flush();
                        out.close();
                    }
                    int code = conn.getResponseCode();
                    if(code == 200){
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while((len = is.read(buffer)) != -1){
                            bos.write(buffer, 0, len);
                        }
                        is.close();
                        conn.disconnect();
                        String res = new String(bos.toByteArray());
                        System.out.println(res);
                        if(handler != null){
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = res;
                            handler.sendMessage(msg);
                        }
                    }else{
                        System.out.println(url.getPath() + ":" + code);
                    }
                } catch (IOException e) {
                    if(handler != null){
                        Message msg = new Message();
                        msg.what = -1;
                        msg.obj = "无法连接服务器";
                        handler.sendMessage(msg);
                    }
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public static void setSessionId(String session) {
        sessionId = session;
    }
    public static String getSessionId(){
        return sessionId;
    }
}
