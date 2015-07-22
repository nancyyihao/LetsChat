package com.main.letschat;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

/**欢迎界面，就是程序启动是的第一张图
 * @author jumper
 *
 */
public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent (WelcomeActivity.this,StartActivity.class);			
				startActivity(intent);			
				WelcomeActivity.this.finish();			
			}
		}, 2000) ;
	}
}
