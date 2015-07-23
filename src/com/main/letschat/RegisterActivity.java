
package com.main.letschat;

import com.util.connect.Connect;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.os.Build;

/**
 * 类功能描述：注册界面</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class RegisterActivity extends Activity {

    private EditText userEdit; // 用来获取用户名

    private EditText pswdEdit; // 用来获取用户密码

    private String username;

    private String password;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_activity);

        button = (Button)findViewById(R.id.regist_btn);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                userEdit = (EditText)findViewById(R.id.regist_user_edit);
                pswdEdit = (EditText)findViewById(R.id.regist_passwd_edit);

                username = userEdit.getText().toString();
                password = userEdit.getText().toString();

                // 多线程启动耗时（如登录，注册等）操作，避免UI阻塞
                new Thread(doRegist).start();

            }
        });
    }

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

    Runnable doRegist = new Runnable() {

        @Override
        public void run() {

            if (Connect.getInstance().connectServer()) {

                if (Connect.getInstance().regist(username, password).equals("SUCCESS")) {
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

    public void regist_back(View v) { // 点击注册界面左上方返回键会触发这个事件

        finish();
        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
        startActivity(intent);
    }
}
