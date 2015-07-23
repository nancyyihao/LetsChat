
package com.main.letschat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


/**
 * 类功能描述：欢迎界面，就是程序启动是的第一张图</br>
 *
 * @author 王明献
 * @version 1.0
 * </p>
 * 修改时间：</br>
 * 修改备注：</br>
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, StartActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 2000); // 延迟2000ms,就是2秒后跳转到StartActivity
    }
}
