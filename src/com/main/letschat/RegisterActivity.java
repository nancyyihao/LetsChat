
package com.main.letschat;

import com.util.connect.Connect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 类功能描述：注册界面</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class RegisterActivity extends Activity {

    /**
     * 用来获取用户名
     */
    private EditText mUserEdit;  

    /**
     * 用来获取用户密码
     */
    private EditText mPswdEdit;  

    /**
     * 用户名
     */
    private String mUserName;

    /**
     * 密码
     */
    private String mPassWord;

    /**
     * 注册按钮
     */
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_activity);

        mButton = (Button)findViewById(R.id.regist_btn);

        mButton.setOnClickListener(mClickListener);
    }
    
    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            mUserEdit = (EditText)findViewById(R.id.regist_user_edit);
            mPswdEdit = (EditText)findViewById(R.id.regist_passwd_edit);

            mUserName = mUserEdit.getText().toString();
            mPassWord = mUserEdit.getText().toString();

            // 多线程启动耗时（如登录，注册等）操作，避免UI阻塞
            new Thread(doRegist).start();

        }
    } ;
    
    /**
     * 处理消息
     */
    @SuppressLint("HandlerLeak") 
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // 注册失败
                    Toast toast = Toast.makeText(RegisterActivity.this, "注册失败，请稍候再试!",
                            Toast.LENGTH_LONG);

                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                case 2: // 注册成功，跳转到登陆界面
                    Toast toast2 = Toast.makeText(RegisterActivity.this, "注册成功，请登录!",
                            Toast.LENGTH_LONG);

                    toast2.setGravity(Gravity.CENTER, 0, 0);
                    toast2.show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 进行注册
     */
    Runnable doRegist = new Runnable() {

        @Override
        public void run() {

            if (Connect.getInstance().connectServer()) {

                if (Connect.getInstance().regist(mUserName, mPassWord).equals("SUCCESS")) {
                    Log.e("regist", "成功！");
                    handler.sendEmptyMessage(2);
                } else {
                    handler.sendEmptyMessage(1);
                }
            } else {
                handler.sendEmptyMessage(1);
            }
        }
    };

    /**
     * (点击注册界面左上方返回键会触发这个事件) 
     * @param v
     */
    public void regist_back(View v) { 

        finish();
        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
        startActivity(intent);
    }
}
