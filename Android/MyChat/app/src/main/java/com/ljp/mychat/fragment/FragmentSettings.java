package com.ljp.mychat.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ljp.mychat.MainActivity;
import com.ljp.mychat.R;
import com.ljp.mychat.activity.HomeActivity;
import com.ljp.mychat.entity.MyUser;
import com.ljp.mychat.entity.RequMsg;
import com.ljp.mychat.entity.User;
import com.ljp.mychat.util.Internet;
import com.ljp.mychat.util.MyGson;
import com.ljp.mychat.util.MySQLite;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.w3c.dom.Text;

public class FragmentSettings extends Fragment {
    private final String tag = "FriendshipFragment";
    private User user;
    Button btn_unlogin;
    TextView tv_setting_mychat_id;
    TextView tv_setting_nickname;
    TextView tv_setting_gender;
    TextView tv_setting_qianming;
    RelativeLayout rl_change_nickname;
    RelativeLayout rl_change_gender;
    RelativeLayout rl_change_qianming;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String json = (String) msg.obj;
                    RequMsg requMsg = MyGson.getRequMsg(json);
                    if(requMsg.getCode() == 200){
                        //成功
                        MyUser.getMyUser().setUserName(user.getUserName());
                        Toast.makeText(getActivity(), requMsg.getMsg(), Toast.LENGTH_SHORT).show();

                    }else{
                        //失败
                        Toast.makeText(getActivity(), requMsg.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case -1:
                    String errorMsg = (String) msg.obj;
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        //绑定控件
        btn_unlogin = view.findViewById(R.id.btn_unlogin);
        tv_setting_mychat_id = view.findViewById(R.id.tv_setting_mychat_id);
        tv_setting_nickname = view.findViewById(R.id.tv_setting_nickname);
        tv_setting_gender = view.findViewById(R.id.tv_setting_gender);
        tv_setting_qianming = view.findViewById(R.id.tv_setting_qianming);
        rl_change_nickname = view.findViewById(R.id.rl_change_nickname);
        rl_change_gender = view.findViewById(R.id.rl_change_gender);
        rl_change_qianming = view.findViewById(R.id.rl_change_qianming);
        //设置用户信息
        User myUser = MyUser.getMyUser();
        tv_setting_nickname.setText(myUser.getUserName());
        tv_setting_mychat_id.setText(myUser.getMychatId());
        String gender = myUser.getGender() == 1 ? "男" : "女";
        tv_setting_gender.setText(gender);
        tv_setting_qianming.setText(myUser.getDescription());
        //设置昵称，性别，签名
        rl_change_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                builder.setTitle("修改昵称")
                        .setPlaceholder("在此输入您的昵称")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                CharSequence text = builder.getEditText().getText();
                                if (text != null && text.length() > 0) {
                                    user = new User();
                                    user.setId(MyUser.getMyUser().getId());
                                    user.setUserName(text.toString());
                                    new Internet("user", mHandler, "PUT", MyGson.getGson().toJson(user));
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "请填入昵称", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            }
        });


        //给退出登录按钮绑定监听器
        btn_unlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这个没效果
                /*new QMUIDialog.MessageDialogBuilder(getContext()).setTitle("确认退出登录")
                        .setMessage("确认退出登录？")
                        .addAction("确认", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                //确认退出
                                SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("password");
                                editor.remove("sessionId");
                                editor.commit();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();//结束登录
                            }
                        })
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {

                            }
                        }).create().show();*/
                new AlertDialog.Builder(getContext()).setTitle("确认退出")
                        .setMessage("确认退出登录？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //确认退出
                                SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("password");
                                editor.remove("sessionId");
                                editor.commit();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();//结束登录
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            //不处理
                    }
                }).create().show();
            }
        });
        return view;
    }
}
