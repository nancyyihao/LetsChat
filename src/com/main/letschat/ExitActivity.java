
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

    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_dialog);

        mLayout = (LinearLayout)findViewById(R.id.exit_layout);
        mLayout.setOnClickListener(mOnClickListener);
    }
    
    /**
     * 点击显示"空空如也"
     */
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Toast.makeText(getApplicationContext(), getResources()
                    .getString(R.string.nothing), Toast.LENGTH_SHORT).show();
        }
    } ;

    /**
     * (用activity模拟对话框,点其他地方就认为放弃，结束activity) 
     * @param v
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    /**
     * (不退出就结束当前activity，返回之前的activity) 
     * @param v
     */
    public void exitbutton1(View v) { 

        this.finish();
    }

    /**
     * (完全退出程序) 
     * @param v
     */
    public void exitbutton0(View v) { 

        this.finish();
        SysApplication.getInstance().exit();
    }

}
