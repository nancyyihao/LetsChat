
package com.main.letschat;

import com.util.connect.Connect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 类功能描述：退出登录</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class LogoutActivity extends Activity {

    /**
     * 用本Activity模拟对话框
     */
    private LinearLayout mLayout;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_dialog);

        mLayout = (LinearLayout)findViewById(R.id.logout_layout);
        mLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.comfirm_logout), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * (点击其他地方也退出) 
     * @param v
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        finish();
        return true;
    }

    /**
     * (否按钮对应的事件) 
     * @param v
     */
    public void logoutbutton1(View v) { 
        this.finish();
    }

    /**
     * ("是"按钮对应的事件) 
     * @param v
     */
    public void logoutbutton0(View v) { 
        
        this.finish();
        Connect.getInstance().Logout();
        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
