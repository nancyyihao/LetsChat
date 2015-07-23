
package com.main.letschat;

import com.util.connect.Connect;
import com.util.exit.SysApplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    /**
     * 用户名
     */
    private String mUser; 

    /**
     * 密码
     */
    private String mPassword; 

    /**
     * 登录状态layout
     */
    private LinearLayout mLoginStatus;  

    /**
     * 登录的整个layout，点击登录后就把它的状态设为GONE,达到切换的效果
     */
    private RelativeLayout mLoginForm; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_activity);

        // 每新生成一个activity，就加入到SysApplication中，要完全退出程序，就把它们全部kill掉
        SysApplication.getInstance().addActivity(this);

        mLoginStatus = (LinearLayout)findViewById(R.id.login_status);
        mLoginForm = (RelativeLayout)findViewById(R.id.login_form);
     
        findViewById(R.id.login_login_btn).setOnClickListener(mOnLoginBtnClickListener);
        findViewById(R.id.forget_passwd).setOnClickListener(mOnForgetBtnClickListener);
        findViewById(R.id.login_reback_btn).setOnClickListener(mOnBackBtnClickListener);
    }

    /**
     * 点击登录按钮开始登陆
     */
    private OnClickListener mOnLoginBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            mUser = ((EditText) findViewById(R.id.login_user_edit)).getText().toString();
            mPassword = ((EditText) findViewById(R.id.login_passwd_edit)).getText().toString();

            // 因为连接服务器和登录比较耗时间，所以用多线程，异步处理，防止UI阻塞
            new Thread(doLogin).start();
        }
    } ;
    
    /**
     * 点击按钮跳转到密码找回界面
     */
    private OnClickListener mOnForgetBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Toast.makeText(getApplicationContext(), getResources().
                    getString(R.string.unsupport), Toast.LENGTH_SHORT).show();
        }
    } ;
    
    /**
     * 点击按钮返回
     */
    private OnClickListener mOnBackBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    } ;
    
    /**
     * 处理登录是否成功的消息
     */
    @SuppressLint("HandlerLeak") 
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    mLoginForm.setVisibility(View.GONE); // 正在登陆
                    mLoginStatus.setVisibility(View.VISIBLE);
                    break;
                    
                case 2: // 登录失败
                    mLoginForm.setVisibility(View.VISIBLE);
                    mLoginStatus.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, getResources().
                            getString(R.string.login_fail), Toast.LENGTH_LONG).show();
                    break;
                    
                case 3: // 登录成功
                    Intent intent = new Intent(LoginActivity.this, 
                            FriendlistActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                    
                default:
                    break;
            }
        }
    };

    /**
     * 连接服务器和登录
     */
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
