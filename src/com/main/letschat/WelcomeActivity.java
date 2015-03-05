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

/**欢迎界面
 * @author jumper
 *
 */
public class WelcomeActivity extends Activity {
	
	private Button login_btn,exit_btn,regist_btn ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		SysApplication.getInstance().addActivity(this); 
			
		exit_btn = (Button) findViewById(R.id.main_exit_btn) ;
		regist_btn = (Button) findViewById(R.id.main_regist_btn) ;
		login_btn = (Button) findViewById(R.id.main_login_btn) ;
		
		login_btn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(WelcomeActivity.this,Login.class);
				startActivity(intent);  
				//finish();	
			}
		}) ;
		
		regist_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(WelcomeActivity.this,RegistActivity.class);
				startActivity(intent);  
			}
		}) ;
		
		exit_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				SysApplication.getInstance().exit() ;	
			}
		}) ;
	}
	
}
