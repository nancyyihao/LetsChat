
package com.main.letschat;

import java.io.DataOutputStream;

import com.util.exit.SysApplication;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

/**
 * 类功能描述：欢迎界面</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class StartActivity extends Activity {

    private Button login_btn; // 登录按钮

    private Button exit_btn; // 退出按钮

    private Button regist_btn; // 注册按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.welcome_activity);
        SysApplication.getInstance().addActivity(this);

        exit_btn = (Button)findViewById(R.id.main_exit_btn);
        regist_btn = (Button)findViewById(R.id.main_regist_btn);
        login_btn = (Button)findViewById(R.id.main_login_btn);

        login_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        regist_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        exit_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 完全退出程序
                SysApplication.getInstance().exit();
            }
        });
    }

}
