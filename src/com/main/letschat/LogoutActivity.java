package com.main.letschat;

import com.util.connect.Connect;
import com.util.exit.SysApplication;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Build;


/**
 * 类功能描述：退出登录</br>
 *
 * @author 王明献
 * @version 1.0
 * </p>
 * 修改时间：</br>
 * 修改备注：</br>
 */
public class LogoutActivity extends Activity {
	
	private LinearLayout layout;    //用本Activity模拟对话框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logout_dialog);
		
		layout=(LinearLayout)findViewById(R.id.logout_layout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Toast.makeText(getApplicationContext(), "确定退出登录？", 
						Toast.LENGTH_SHORT).show();	
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	                                       
		finish();
		return true;
	}
	
	public void logoutbutton1(View v) {    //否按钮对应的事件
    	this.finish();    	
      }  
	public void logoutbutton0(View v) {     //"是"按钮对应的事件 
    	this.finish();
    	Connect.getInstance().Logout() ;
    	Intent intent = new Intent();
	    intent.setClass(this, StartActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
      }  

}
