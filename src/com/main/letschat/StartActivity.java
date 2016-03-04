
package com.main.letschat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.util.exit.SysApplication;

/**
 * 类功能描述：欢迎界面</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class StartActivity extends Activity {

    /**
     *  登录按钮
     */
    private Button mLoginBtn; 

    /**
     * 退出按钮
     */
    private Button mExitBtn;  

    /**
     * 注册按钮
     */
    private Button mRegistBtn; 


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.welcome_activity);
        SysApplication.getInstance().addActivity(this);

        mExitBtn = (Button)findViewById(R.id.main_exit_btn);
        mRegistBtn = (Button)findViewById(R.id.main_regist_btn);
        mLoginBtn = (Button)findViewById(R.id.main_login_btn);

        mLoginBtn.setOnClickListener(mOnLoginBtnClickListener);
        mRegistBtn.setOnClickListener(mOnRegistBtnClickListener);
        mExitBtn.setOnClickListener(mOnExitBtnClickListener);
    }
    
    /**
     * 点击按钮登录
     */
    private OnClickListener mOnLoginBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };
    
    /**
     * 点击按钮注册
     */
    private OnClickListener mOnRegistBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    };
    
    /**
     * 点击按钮完全退出程序
     */
    private OnClickListener mOnExitBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            SysApplication.getInstance().exit();
        }
    } ;
}
