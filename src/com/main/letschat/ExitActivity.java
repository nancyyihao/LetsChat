
package com.main.letschat;

import com.util.exit.SysApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 类功能描述：退出</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class ExitActivity extends Activity {

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_dialog);

        layout = (LinearLayout)findViewById(R.id.exit_layout);
        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "HAHA", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 用activity模拟对话框,点其他地方就认为放弃，结束activity
        finish();
        return true;
    }

    public void exitbutton1(View v) { // 不退出

        this.finish();
    }

    public void exitbutton0(View v) { // 完全退出程序

        this.finish();
        SysApplication.getInstance().exit();
    }

}
