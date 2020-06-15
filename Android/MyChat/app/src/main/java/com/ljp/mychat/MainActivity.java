package com.ljp.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ljp.mychat.activity.HomeActivity;
import com.ljp.mychat.entity.MyUser;
import com.ljp.mychat.entity.RequMsg;
import com.ljp.mychat.entity.User;
import com.ljp.mychat.util.Internet;
import com.ljp.mychat.util.MyGson;
import com.ljp.mychat.util.MySQLite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
//    Integer PERMISSION_GRANTED = 200;
    private final String tag = "MainActivity";
    private EditText et_user_name;
    private EditText et_password;
    private Button btn_login;
    private Button btn_open_register;

    private SharedPreferences sp;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //成功
                    String json = (String) msg.obj;
                    RequMsg requMsg = MyGson.getRequMsg(json);
                    if(requMsg.getCode() == 200){
                        User myUser = MyGson.getUser(requMsg.getData());
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("id", myUser.getId());
                        editor.putString("userName", myUser.getMychatId());
                        editor.putString("password", et_password.getText().toString());
                        editor.putString("sessionId", Internet.getSessionId());
                        editor.commit();
                        new MySQLite(MainActivity.this).saveUser(myUser);
                        MyUser.setMyUser(myUser);
                        Toast.makeText(MainActivity.this, requMsg.getMsg(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();//结束登录
                    }else if(requMsg.getCode() == 401){
                        Toast.makeText(MainActivity.this, requMsg.getMsg(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, requMsg.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case -1:
                    //联网失败
                    String errorMsg = (String) msg.obj;
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_user_name = findViewById(R.id.et_user_name);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_open_register =findViewById(R.id.btn_open_register);

        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        //实现自动登录
        String sessionId = sp.getString("sessionId", "");
        Integer id = sp.getInt("id", -1);
        String name = sp.getString("userName", "");
        String password = sp.getString("password", "");
        et_user_name.setText(name);
        et_password.setText(password);
        if(!"".equals(sessionId)){
            Internet.setSessionId(sessionId);
            User user = new MySQLite(this).getUser(id);
            MyUser.setMyUser(user);
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            MainActivity.this.finish();//结束登录
        }else if(!"".equals(name) && !"".equals(password)){//如果Session失效
            login(name, password);
        }else{
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = et_user_name.getText().toString();
                    String password = et_password.getText().toString();
                    if("".equals(name)){
                        Toast.makeText(v.getContext(), "MychatID/手机号不能为空", Toast.LENGTH_SHORT).show();
                    }else if("".equals(password)){
                        Toast.makeText(v.getContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    }else{
                        login(name, password);
                    }
                }
            });
        }


        /*测试拦截器
        * 结果：服务器的拦截器不会拦截WebSocket*/
        /*URI uri = URI.create("ws://192.168.43.141:8080/webSocket/1");
        MyWebSocketClient client = new MyWebSocketClient(uri);
        try {
            client.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 发送登录请求
     * @param userName
     * @param password
     */
    public void login(String userName, String password){
        URL url = null;
        try {
            url = new URL(Internet.PATH + "/login");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URL finalUrl = url;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    HttpURLConnection conn = (HttpURLConnection) finalUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(5000);
                    conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    conn.connect();
                    OutputStream out = conn.getOutputStream();
                    String content = "userName=" + userName + "&password=" + password;
                    out.write(content.getBytes());
                    out.flush();
                    out.close();
                    int code = conn.getResponseCode();
                    if(code == 200){
                        String cookie = conn.getHeaderField("set-cookie");
                        String sessionId = cookie.substring(0, cookie.indexOf(';'));
                        Internet.setSessionId(sessionId);
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

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = res;
                        mHandler.sendMessage(msg);
                    }else{
                        System.out.println(code);
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = -1;
                    msg.obj = "无法连接服务器";
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    protected void onStop() {
        Log.i(tag, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(tag, "onDestroy()");
        super.onDestroy();
    }









    /**
     * 动态申请权限Android6.0才用
     */
    /*private void applyPerssionInternet(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_GRANTED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_GRANTED){
            Toast.makeText(this, "成功获取网络访问权限", Toast.LENGTH_SHORT).show();
        }
    }*/
}
