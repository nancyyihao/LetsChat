
package com.main.letschat;

import com.util.connect.Connect;
import com.util.exit.SysApplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 类功能描述：登录Controller</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class LoginActivity extends Activity {

    private String mUser; // 用户名

    private String mPassword; // 密码

    private LinearLayout loginStatus; // 登录状态layout

    private RelativeLayout loginForm; // 登录的整个layout，点击登录后就把它的状态设为GONE,达到切换的效果

    private EditText textUser;

    private EditText textPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_activity);

        // 每新生成一个activity，就加入到SysApplication中，要完全退出程序，就把它们全部kill掉
        SysApplication.getInstance().addActivity(this);

        loginStatus = (LinearLayout)findViewById(R.id.login_status);
        loginForm = (RelativeLayout)findViewById(R.id.login_form);

        // 给登录按钮添加监听事件
        findViewById(R.id.login_login_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mUser = ((EditText)findViewById(R.id.login_user_edit)).getText().toString();
                mPassword = ((EditText)findViewById(R.id.login_passwd_edit)).getText().toString();

                // 因为连接服务器和登录比较耗时间，所以用多线程，异步处理，防止UI阻塞
                new Thread(doLogin).start();
            }
        });

        // 给忘记密码按钮添加监听事件
        findViewById(R.id.forget_passwd).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "该功能尚未开发，请等待!", Toast.LENGTH_SHORT).show();
            }
        });

        // 给左上角的返回键添加监听事件
        findViewById(R.id.login_reback_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    loginForm.setVisibility(View.GONE); // 正在登陆
                    loginStatus.setVisibility(View.VISIBLE);
                    break;
                case 2: // 登录失败
                    loginForm.setVisibility(View.VISIBLE);
                    loginStatus.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "登陆失败，请重试！", Toast.LENGTH_LONG).show();
                    break;
                case 3: // 登录成功
                    Intent intent = new Intent(LoginActivity.this, FriendlistActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    Runnable doLogin = new Runnable() {

        @Override
        public void run() {

            handler.sendEmptyMessage(1);
            if (Connect.getInstance().connectServer()) { // 能连上服务器

                if (Connect.getInstance().Login(mUser, mPassword)) // 登录
                    handler.sendEmptyMessage(3);
                else
                    handler.sendEmptyMessage(2);

            } else {

                handler.sendEmptyMessage(2);
            }
        }
    };

}
